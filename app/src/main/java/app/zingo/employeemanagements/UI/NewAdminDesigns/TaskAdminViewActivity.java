package app.zingo.employeemanagements.UI.NewAdminDesigns;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.List;

import app.zingo.employeemanagements.R;
import app.zingo.employeemanagements.UI.Admin.EmployeeTaskMapScreen;

public class TaskAdminViewActivity extends AppCompatActivity {

    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{

            setContentView(R.layout.activity_task_admin_view);
            setupToolbar();
            setupViewPager((ViewPager) findViewById(R.id.viewPager));

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList();
        private final List<String> mFragmentTitleList = new ArrayList();

        public ViewPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        public Fragment getItem(int i) {
            return (Fragment) this.mFragmentList.get(i);
        }

        public int getCount() {
            return this.mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String str) {
            this.mFragmentList.add(fragment);
            this.mFragmentTitleList.add(str);
        }

        public CharSequence getPageTitle(int i) {
            return (CharSequence) this.mFragmentTitleList.get(i);
        }
    }


    private void setupViewPager(ViewPager viewPager) {
        viewPager.setOffscreenPageLimit(1);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());


        viewPagerAdapter.addFragment(TaskListFragment.getInstance(), "Tasks");

        viewPager.setAdapter(viewPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

    }
    public void setupToolbar() {
        this.mToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(this.mToolbar);
        Assert.assertNotNull(getSupportActionBar());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Employee View");
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_task_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id)
        {
            case android.R.id.home:

                TaskAdminViewActivity.this.finish();
                break;

            case R.id.action_task_view:

                TaskAdminViewActivity.this.finish();
                break;




        }
        return super.onOptionsItemSelected(item);
    }
}
