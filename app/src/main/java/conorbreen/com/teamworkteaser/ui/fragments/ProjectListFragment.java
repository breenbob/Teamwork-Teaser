package conorbreen.com.teamworkteaser.ui.fragments;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
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
import conorbreen.com.teamworkteaser.models.events.DataRefreshFinishEvent;
import conorbreen.com.teamworkteaser.services.TeamworkRealmService;
import conorbreen.com.teamworkteaser.ui.activities.MainActivity;
import conorbreen.com.teamworkteaser.ui.adapters.ProjectListRealmRecyclerViewAdapter;
import conorbreen.com.teamworkteaser.ui.UIConstants;
import conorbreen.com.teamworkteaser.utils.ColorUtils;
import io.reactivex.disposables.Disposable;
import io.realm.RealmChangeListener;
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
    @BindView(R.id.cvNoResults)
    CardView cvNoResults;
    //endregion

    private ProjectStatus projectStatus;
    private ProjectListRealmRecyclerViewAdapter realmAdapter;
    private TeamworkRealmService realmService;
    private Disposable disposable;
    private RealmResults<Project> projectListResults;
    private RealmChangeListener<RealmResults<Project>> changeListener = new RealmChangeListener<RealmResults<Project>>() {
        @Override
        public void onChange(RealmResults<Project> projects) {
            Timber.d("RealmResults change listener called for fragment with status %s", projectStatus.toString());

            if (projects.isLoaded() && projects.isValid()) {
                if (projects.isEmpty()) {
                    cvNoResults.setVisibility(View.VISIBLE);
                    recyclerProjectList.setVisibility(View.GONE);
                } else {
                    cvNoResults.setVisibility(View.GONE);
                    recyclerProjectList.setVisibility(View.VISIBLE);
                }
            }
        }
    };

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

        setupSwipeRefreshLayout();

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

    private void setupSwipeRefreshLayout() {
        // Change default swipe refresh indicator color from plain black to use array of much nicer material colors
        swipeRefreshProjectList.setColorSchemeColors(ColorUtils.getMaterialPalette());
        swipeRefreshProjectList.setOnRefreshListener(this::refreshData);
    }

    private void refreshData() {
        if (getActivity() != null) {
            swipeRefreshProjectList.setRefreshing(true);
            ((MainActivity) getActivity()).refreshData(false);
        }
    }

    private void setupRecyclerView(RealmResults<Project> projects) {
        this.projectListResults = projects;

         if (realmAdapter == null) {
            realmAdapter = new ProjectListRealmRecyclerViewAdapter(getContext(), projectListResults);
            recyclerProjectList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayout.VERTICAL, false));
            recyclerProjectList.setAdapter(realmAdapter);
            recyclerProjectList.setHasFixedSize(true);

            // Use support library ItemTouchHelper to offer Swipe to delete project from list functionality
            ProjectTouchHelperCallback touchHelperCallback = new ProjectTouchHelperCallback();
            ItemTouchHelper touchHelper = new ItemTouchHelper(touchHelperCallback);
            touchHelper.attachToRecyclerView(recyclerProjectList);
        }

        if (projects.isLoaded() && projects.isValid()) {
            if (projects.isEmpty()) {
                cvNoResults.setVisibility(View.VISIBLE);
                recyclerProjectList.setVisibility(View.GONE);
            } else {
                cvNoResults.setVisibility(View.GONE);
                recyclerProjectList.setVisibility(View.VISIBLE);
            }
        }

        // RealmResults doesn't need a change listener to update RecyclerView in real-time as the adapter
        // extends the RealmRecyclerViewAdapter base class, however I can still add an additional listener
        // to notify this fragment to show a "No results" message to the user.
        this.projectListResults.addChangeListener(changeListener);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataRefreshFinish(DataRefreshFinishEvent event) {
        Timber.d("Project data refresh succeeded: %s", String.valueOf(event.isSuccessful()));
        swipeRefreshProjectList.setRefreshing(false);

        if (!event.isSuccessful()) {
            // Use Length long when presenting the user with an Action to perform
            Snackbar.make(getView(), R.string.snackbar_data_refresh_error, Snackbar.LENGTH_LONG)
                    .setAction(R.string.snackbar_button_retry, onClick -> refreshData())
                    .show();
        }
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
        disposable.dispose();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        try {
            realmService.close();
            this.projectListResults.removeChangeListener(changeListener);
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
