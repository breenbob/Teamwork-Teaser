package conorbreen.com.teamworkteaser.services;

import android.support.annotation.UiThread;

import org.greenrobot.eventbus.EventBus;

import java.io.Closeable;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import conorbreen.com.teamworkteaser.models.Category;
import conorbreen.com.teamworkteaser.models.enums.ProjectStatus;
import conorbreen.com.teamworkteaser.models.events.DataRefreshFinishEvent;
import conorbreen.com.teamworkteaser.models.Project;
import conorbreen.com.teamworkteaser.utils.EnumUtils;
import io.reactivex.Flowable;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.Sort;
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
    private static TeamworkRealmService instance = null;

    public TeamworkRealmService()
    {
        this.realm = Realm.getDefaultInstance();
    }

    public static TeamworkRealmService getInstance() {
        if (instance == null) {
            instance = new TeamworkRealmService();
        }

        return instance;
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
                () -> EventBus.getDefault().post(new DataRefreshFinishEvent(true)),
                throwable -> {
                    Timber.e(throwable, "Project data could not be saved");
                    EventBus.getDefault().post(new DataRefreshFinishEvent(false));
                });
    }

    public Flowable<RealmResults<Project>> getProjectsByStatus(ProjectStatus status) {
        switch(status)
        {
            case Active:
            case Archived:
            {
                // Only certain statuses that the API accepts exist as possible column values
                return getProjectsByActualStatus(status);
            }
            case Current:
            {
                return getCurrentProjects();
            }
            case Completed:
            {
                return getCompletedProjects();
            }
            case Late:
            {
                return getLateProjects();
            }
            case All:
                throw new UnsupportedOperationException("Status not supported in local queries.");
        }

        return null;
    }

    /**
     * Returns projects with a status that is actually represented in the database.
     * i.e. Active or Archived
     */
    private Flowable<RealmResults<Project>> getProjectsByActualStatus(ProjectStatus status) {
        String value = EnumUtils.getSerializedName(status);
        return realm.where(Project.class)
                .equalTo("status", value, Case.INSENSITIVE)
                .findAllAsync()
                .asFlowable();
    }

    /**
     * Project is current if status is active, end date has not yet been reached,
     * and subStatus is not completed.
     */
    private Flowable<RealmResults<Project>> getCurrentProjects() {
        String value = EnumUtils.getSerializedName(ProjectStatus.Active);
        return realm.where(Project.class)
                .greaterThan("endDate", new Date())
                .and()
                .equalTo("status", value, Case.INSENSITIVE)
                .and()
                .not()
                .equalTo("subStatus", "comleted")
                .findAllAsync()
                .asFlowable();
    }

    /**
     * Was tricky to tell from API documentation and using Teamwork Projects just what makes a project Completed.
     * Tested by marking a project as complete, then refetching the json - noticed a new field called subStatus with
     * value "completed"...
     */
    private Flowable<RealmResults<Project>> getCompletedProjects() {
        String value = EnumUtils.getSerializedName(ProjectStatus.Completed);
        return realm.where(Project.class)
                .equalTo("subStatus", value, Case.INSENSITIVE)
                .findAllAsync()
                .asFlowable();
    }

    /**
     * Project is probably late if End date has been surpassed but project is still active,
     * (from testing in the web portal I can tell the completed sub-status does not seem to come into play here)
     */
    private Flowable<RealmResults<Project>> getLateProjects() {
        String value = EnumUtils.getSerializedName(ProjectStatus.Active);
        return realm.where(Project.class)
                .lessThan("endDate", new Date())
                .and()
                .equalTo("status", value, Case.INSENSITIVE)
                .findAllAsync()
                .asFlowable();
    }

    public void updatedManagedObject(Realm.Transaction transaction, Realm.Transaction.OnSuccess onSuccess, Realm.Transaction.OnError onError) {
        realm.executeTransactionAsync(transaction, onSuccess, onError);
    }

    /**
     * Gets all categories currently stored in the database.
     */
    public Flowable<RealmResults<Category>> getAllCategories() {
        return realm.where(Category.class)
                .sort("name", Sort.ASCENDING)
                .findAllAsync()
                .asFlowable();
    }

    public  <E extends RealmObject> List<E> copyFromRealm(RealmResults<E> results)  {
        return realm.copyFromRealm(results);
    }

    public  <E extends RealmObject> E copyFromRealm(E result)  {
        if (result.isManaged()) {
            return realm.copyFromRealm(result);
        } else {
            return result;
        }
    }

    @Override
    @UiThread
    public void close() throws IOException {
        if (!realm.isClosed()) {
            realm.close();
        }
    }
}
