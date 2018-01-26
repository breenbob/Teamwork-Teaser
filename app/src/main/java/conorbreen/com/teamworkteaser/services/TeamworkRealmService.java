package conorbreen.com.teamworkteaser.services;

import android.support.annotation.UiThread;

import org.greenrobot.eventbus.EventBus;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

import conorbreen.com.teamworkteaser.models.enums.ProjectStatus;
import conorbreen.com.teamworkteaser.models.events.ProjectListEvent;
import conorbreen.com.teamworkteaser.models.Project;
import conorbreen.com.teamworkteaser.utils.EnumUtils;
import io.reactivex.Flowable;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;
import timber.log.Timber;

/**
 * Created by Conor Breen on 23/01/2018.
 */

/**
 * Our data repository service for all interaction with our realm!
 * This uses event bus to notify the main UI thread of save success/failure.
 */
public class TeamworkRealmService implements Closeable {
    private Realm realm;

    public TeamworkRealmService()
    {
        this.realm = Realm.getDefaultInstance();
    }

    public void saveProjects(final List<Project> projects) {
        if (projects.isEmpty())
            return;

        realm.executeTransactionAsync(r -> {
                    for (Project project : projects) {
                        r.copyToRealmOrUpdate(project);
                    }
                },
                /* Using EventBus to notify main UI thread of save completion -
                   this isn't really necessary from a realm perspective as using RealmResults with a RealmRecyclerViewAdapter will
                   update the UI in real time anyway using the built-in change listener, but is useful to notify the UI anyway,
                   for example to setRefreshing(false) on a SwipeRefreshLayout... and also to demonstrate the use of EventBus!
                   You could probably put the data in the event itself, but then I wouldn't get to demonstrate as much realm usage! */
                () -> EventBus.getDefault().post(new ProjectListEvent(true)),
                throwable -> {
                    Timber.e(throwable, "Project data could not be saved");
                    EventBus.getDefault().post(new ProjectListEvent(false));
                });
    }

    public Flowable<RealmResults<Project>> getProjectsByStatus(ProjectStatus status) {
        String value = EnumUtils.getSerializedName(status);
        return realm.where(Project.class)
                .equalTo("status", value, Case.INSENSITIVE)
                .findAllAsync()
                .asFlowable();
    }

    @Override
    @UiThread
    public void close() throws IOException {
        realm.close();
    }
}
