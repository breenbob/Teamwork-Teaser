package conorbreen.com.teamworkteaser.services;

import org.greenrobot.eventbus.EventBus;

import conorbreen.com.teamworkteaser.models.PostProject;
import conorbreen.com.teamworkteaser.models.Project;
import conorbreen.com.teamworkteaser.models.enums.ProjectStatus;
import conorbreen.com.teamworkteaser.models.events.DataRefreshFinishEvent;
import conorbreen.com.teamworkteaser.retrofit.RestClient;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by Conor Breen on 23/01/2018.
 */

/**
 * Our Rx powered retrofit client REST API wrapper.
 * Api calls use observer/subscribe paradigm to notify Realm data layer upon successful download.
 */
public class TeamworkApiService {
    private static TeamworkApiService instance = null;

    public static TeamworkApiService getInstance() {
        if (instance == null) {
            instance = new TeamworkApiService();
        }

        return instance;
    }

    public void getAllProjects() {
        // Use Retrofit Observables to feed data to our data repository
        RestClient.getInstance().getApiService().getAllProjects(ProjectStatus.All)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                // If we wanted to work with unmanaged objects using copyFromRealm, we could
                // close our realm once all operations are complete using the doAfterTerminate,
                // but doing so would mean we lose lose real-time updates from our RealmResults/RealmRecyclerViewAdapter:
               // .doAfterTerminate(() -> realmService.close())
                .subscribe(response -> {
                    Timber.d("%d projects received.", response.getProjects().size());
                    TeamworkRealmService.getInstance().saveProjects(response.getProjects());
                }, throwable -> {
                    Timber.e(throwable, "GetAllProjects Api call failed with error");
                    EventBus.getDefault().post(new DataRefreshFinishEvent(false));
                });
    }

    public Observable<Response<ResponseBody>> starProject(int projectId) {
        return RestClient.getInstance().getApiService().starProject(projectId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Response<ResponseBody>> unstarProject(int projectId) {
        return RestClient.getInstance().getApiService().unstarProject(projectId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Response<ResponseBody>> createProject(Project newProject) {
        return RestClient.getInstance().getApiService().createProject(new PostProject(newProject))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Response<ResponseBody>> updateProject(Project existingProject) {
        return RestClient.getInstance().getApiService().updateProject(existingProject.getId(), new PostProject(existingProject))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
