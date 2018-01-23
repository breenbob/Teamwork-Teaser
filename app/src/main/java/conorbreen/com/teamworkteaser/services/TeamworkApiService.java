package conorbreen.com.teamworkteaser.services;

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
                    Timber.d("Data could not be downloaded: %s", throwable.toString());
                });
    }
}
