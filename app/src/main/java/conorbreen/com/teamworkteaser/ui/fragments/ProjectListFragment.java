package conorbreen.com.teamworkteaser.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import conorbreen.com.teamworkteaser.R;
import conorbreen.com.teamworkteaser.models.Project;
import conorbreen.com.teamworkteaser.models.enums.ProjectStatus;
import conorbreen.com.teamworkteaser.models.events.ProjectListEvent;
import conorbreen.com.teamworkteaser.services.TeamworkRealmService;
import conorbreen.com.teamworkteaser.ui.adapters.ProjectListRealmRecyclerViewAdapter;
import conorbreen.com.teamworkteaser.utils.UIConstants;
import io.reactivex.disposables.Disposable;
import io.realm.RealmResults;
import timber.log.Timber;

/**
 * Created by Conor Breen on 24/01/2018.
 */

public class ProjectListFragment extends Fragment {
    //region View Binding
    @BindView(R.id.swipeRefreshProjectList)
    SwipeRefreshLayout swipeRefreshProjectList;

    @BindView(R.id.recyclerProjectList)
    RecyclerView recyclerProjectList;
    //endregion

    private ProjectStatus projectStatus;
    private ProjectListRealmRecyclerViewAdapter realmAdapter;
    private TeamworkRealmService realmService;
    private Disposable disposable;
    private RealmResults<Project> projectListResults;

    public static ProjectListFragment newInstance(ProjectStatus status) {
        Bundle bundleStatus = new Bundle();
        bundleStatus.putInt(UIConstants.BundleKeys.ProjectStatusKey, status.ordinal());

        ProjectListFragment instance = new ProjectListFragment();
        instance.setArguments(bundleStatus);

        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_project_list, container, false);

        projectStatus = ProjectStatus.values()[getArguments().getInt(UIConstants.BundleKeys.ProjectStatusKey)];
        realmService = new TeamworkRealmService();

        ButterKnife.bind(this, v);

        realmService.getProjectsByStatus(projectStatus);
        disposable = realmService.getProjectsByStatus(projectStatus)
        .subscribe(
                projects -> {
                    Timber.d("RealmService results loaded: %s", projects.isLoaded());
                    setupRecyclerView(projects);
                },
                throwable -> Timber.e(throwable, "An error occurred when fetching the project list:")
        );

        return v;
    }

    private void setupRecyclerView(RealmResults<Project> projects) {
        this.projectListResults = projects;

        if (realmAdapter != null) {
            realmAdapter = new ProjectListRealmRecyclerViewAdapter(projectListResults);
            recyclerProjectList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayout.VERTICAL, false));
            recyclerProjectList.setAdapter(realmAdapter);
            recyclerProjectList.setHasFixedSize(true);
            //recyclerProjectList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

            // Use support library ItemTouchHelper to offer Swipe to delete project from list functionality
            ProjectTouchHelperCallback touchHelperCallback = new ProjectTouchHelperCallback();
            ItemTouchHelper touchHelper = new ItemTouchHelper(touchHelperCallback);
            touchHelper.attachToRecyclerView(recyclerProjectList);
        }
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
    public void onPause() {
        super.onPause();
        disposable.dispose();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        try {
            realmService.close();
        } catch (IOException ex) {
            Timber.e(ex);
        }
    }

    private class ProjectTouchHelperCallback extends ItemTouchHelper.SimpleCallback {

        ProjectTouchHelperCallback() {
            super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return true;
        }

        @Override
        public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
            // TODO: delete viewHolder.getItemId() from Realm and API
            Timber.d("Project with Id %d was swiped!", viewHolder.getItemId());
        }

        @Override
        public boolean isLongPressDragEnabled() {
            return true;
        }
    }
}
