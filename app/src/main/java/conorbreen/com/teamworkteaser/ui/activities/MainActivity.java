package conorbreen.com.teamworkteaser.ui.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;


import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import conorbreen.com.teamworkteaser.R;
import conorbreen.com.teamworkteaser.models.enums.ProjectStatus;
import conorbreen.com.teamworkteaser.services.TeamworkApiService;
import conorbreen.com.teamworkteaser.services.TeamworkRealmService;
import conorbreen.com.teamworkteaser.ui.adapters.FragmentTabPagerAdapter;
import conorbreen.com.teamworkteaser.ui.fragments.ProjectListFragment;
import timber.log.Timber;

/**
 * Created by Conor Breen on 23/01/2018.
 */

public class MainActivity extends AppCompatActivity {
    // region View Bindings
    @BindView(R.id.tabLayoutProjectList)
    TabLayout tabLayoutProjectList;
    @BindView(R.id.viewPagerProjectList)
    ViewPager viewPagerProjectList;
    // endregion

    private TeamworkRealmService realmService;
    private TeamworkApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setTitle("Teamwork.com Teaser");

        setupViewPager();

        realmService = new TeamworkRealmService();
        apiService = new TeamworkApiService(realmService);

        refreshData();
    }

    private void setupViewPager() {
        FragmentTabPagerAdapter tabPagerAdapter = new FragmentTabPagerAdapter(getSupportFragmentManager());

        tabPagerAdapter.addFragment(ProjectListFragment.newInstance(ProjectStatus.Active), "Active");
        tabPagerAdapter.addFragment(ProjectListFragment.newInstance(ProjectStatus.Archived), "Archived");

        viewPagerProjectList.setAdapter(tabPagerAdapter);
    }

    public void refreshData() {
        //TODO: SwipeRefreshLayout.setRefreshing(true)?
        apiService.getAllProjects();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            realmService.close();
        } catch (IOException ex) {
            Timber.e(ex);
        }
    }
}
