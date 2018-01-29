package conorbreen.com.teamworkteaser.ui.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;


import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import conorbreen.com.teamworkteaser.R;
import conorbreen.com.teamworkteaser.models.enums.ProjectStatus;
import conorbreen.com.teamworkteaser.models.events.DataRefreshStartEvent;
import conorbreen.com.teamworkteaser.services.TeamworkApiService;
import conorbreen.com.teamworkteaser.services.TeamworkRealmService;
import conorbreen.com.teamworkteaser.ui.adapters.FragmentTabPagerAdapter;
import conorbreen.com.teamworkteaser.ui.fragments.ProjectListFragment;
import timber.log.Timber;

/**
 * Created by Conor Breen on 23/01/2018.
 */

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    // region View Bindings
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tabLayoutProjectList)
    TabLayout tabLayoutProjectList;
    @BindView(R.id.viewPagerProjectList)
    ViewPager viewPagerProjectList;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    // endregion

    //region String bindings
    @BindString(R.string.tab_active)
    String tab_active;
    @BindString(R.string.tab_archived)
    String tab_archived;
    @BindString(R.string.tab_completed)
    String tab_completed;
    @BindString(R.string.tab_current)
    String tab_current;
    @BindString(R.string.tab_late)
    String tab_late;
    //endregion

    @BindString(R.string.main_activity_title)
    String title;

    // Allows our Vector Asset Studio drawables to be used in DrawableContainers
    // i.e. in our Starred CheckBox button state selector
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setTitle(title);
        setSupportActionBar(toolbar);

        setupDrawerLayout();
        setupViewPagerTabLayout();

        refreshData(true);
    }

    private void setupDrawerLayout() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setupViewPagerTabLayout() {
        FragmentTabPagerAdapter tabPagerAdapter = new FragmentTabPagerAdapter(getSupportFragmentManager());

        tabPagerAdapter.addFragment(ProjectListFragment.newInstance(ProjectStatus.Active), tab_active);
        tabPagerAdapter.addFragment(ProjectListFragment.newInstance(ProjectStatus.Current), tab_current);
        tabPagerAdapter.addFragment(ProjectListFragment.newInstance(ProjectStatus.Late), tab_late);
        tabPagerAdapter.addFragment(ProjectListFragment.newInstance(ProjectStatus.Completed), tab_completed);
        tabPagerAdapter.addFragment(ProjectListFragment.newInstance(ProjectStatus.Archived), tab_archived);

        viewPagerProjectList.setAdapter(tabPagerAdapter);
        viewPagerProjectList.setOffscreenPageLimit(tabPagerAdapter.getCount()-1);
        tabLayoutProjectList.setupWithViewPager(viewPagerProjectList);
    }

    public void refreshData(boolean triggerStartEvent) {
        // If being called from child fragments allows them to prevent event from being posted to alert SwipeRefreshLayouts
        // so they can change to refreshing state
        if (triggerStartEvent) {
            EventBus.getDefault().post(new DataRefreshStartEvent());
        }

        TeamworkApiService.getInstance().getAllProjects();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            TeamworkRealmService.getInstance().close();
        } catch (IOException ex) {
            Timber.e(ex);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
}
