package conorbreen.com.teamworkteaser.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;

import conorbreen.com.teamworkteaser.R;
import conorbreen.com.teamworkteaser.events.ProjectListEvent;
import conorbreen.com.teamworkteaser.models.Project;
import conorbreen.com.teamworkteaser.services.TeamworkApiService;
import conorbreen.com.teamworkteaser.services.TeamworkRealmService;
import io.realm.RealmResults;
import timber.log.Timber;

/**
 * Created by Conor Breen on 23/01/2018.
 */

public class MainActivity extends AppCompatActivity {
    private TeamworkRealmService realmService;
    private TeamworkApiService apiService;
    private RealmResults<Project> savedProjects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Teamwork.com Teaser");

        realmService = new TeamworkRealmService();
        apiService = new TeamworkApiService(realmService);

        refreshData();
    }

    public void refreshData() {
        //TODO: SwipeRefreshLayout.setRefreshing(true)?
        apiService.getAllProjects();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onProjectListEvent(ProjectListEvent event) {
        Timber.d("Project data refresh succeeded: %s", String.valueOf(event.isSuccessful()));
        //TODO: SwipeRefreshLayout.setRefreshing(false)?
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Keeping your realm open is a perfectly legitimate and often desirable thing to do
        // Its the only way to keep RealmResults in a managed/connected state, so change events are received etc.
        try {
            realmService.close();
        } catch (IOException ex) {
            Timber.e(ex);
        }
    }
}
