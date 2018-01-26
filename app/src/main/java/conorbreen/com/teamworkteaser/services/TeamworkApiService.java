package conorbreen.com.teamworkteaser.services;

import com.google.common.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;

import conorbreen.com.teamworkteaser.models.events.StarEvent;
import conorbreen.com.teamworkteaser.retrofit.RestClient;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by Conor Breen on 23/01/2018.
 */

/**
 * Our Rx powered retrofit client REST API wrapper.
 * Api calls use observer/subscribe paradigm to notify Realm data layer upon successful download.
 */
public class TeamworkApiService {

    private TeamworkRealmService realmService;

    public TeamworkApiService(TeamworkRealmService realmService)
    {
        this.realmService = realmService;
    }

    public void getAllProjects() {
        // Use Retrofit Observables to feed data to our data repository
        RestClient.getInstance().getApiService().getAllProjects()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                // If we wanted to work with unmanaged objects using copyFromRealm, we could
                // close our realm once all operations are complete using the doAfterTerminate,
                // but doing so would mean we lose lose real-time updates from our RealmResults/RealmRecyclerViewAdapter:
               // .doAfterTerminate(() -> realmService.close())
                .subscribe(response -> {
                    Timber.d("%d projects received.", response.getProjects().size());
                    realmService.saveProjects(response.getProjects());
                }, throwable -> {
                    Timber.d("GetAllProjects Api call failed with error: %s", throwable.toString());
                });
    }

    public void starProject(int projectId) {
        RestClient.getInstance().getApiService().starProject(projectId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    Timber.d(response.isSuccessful() ? "Project starred: %s" : "Project could not be starred: %s", response.message());
                    EventBus.getDefault().post(new StarEvent(true, true));
                }, throwable -> {
                    Timber.d("StarProject Api call failed with error: %s", throwable.toString());

                    // Let UI thread know so it can update realm / show a snackbar
                    EventBus.getDefault().post(new StarEvent(false, true));
                });
    }

    public void unstarProject(int projectId) {
        RestClient.getInstance().getApiService().unstarProject(projectId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    Timber.d(response.isSuccessful() ? "Project unstarred: %s" : "Project could not be unstarred: %s", response.message());
                    EventBus.getDefault().post(new StarEvent(true, false));
                }, throwable -> {
                    Timber.d("StarProject Api call failed with error: %s", throwable.toString());

                    // Let UI thread know so it can update realm / show a snackbar
                    EventBus.getDefault().post(new StarEvent(false, false));
                });
    }
}
