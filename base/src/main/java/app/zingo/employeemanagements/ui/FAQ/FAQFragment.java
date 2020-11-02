package app.zingo.employeemanagements.ui.FAQ;

import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import app.zingo.employeemanagements.base.R;

public class FAQFragment extends AppCompatActivity {

    ViewPager faq_viewpager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{

            setContentView(R.layout.activity_faqfragment);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("FAQ Section");

            faq_viewpager = ( ViewPager ) findViewById(R.id.viewPager_faq);
            setupViewPager(faq_viewpager);


        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void setupViewPager( ViewPager viewPager) {
        viewPager.setOffscreenPageLimit(2);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        viewPagerAdapter.addFragment(CompanyFAQ.getInstance(), "Join as a Company & a Founder");
        viewPagerAdapter.addFragment(EmployeeFAQ.getInstance(), "Join as an Employee");

      //  viewPagerAdapter.addFragment(CompanyFAQ.getInstance(), "Join as a Reseller");

        viewPager.setAdapter(viewPagerAdapter);
        TabLayout tabLayout = findViewById(R.id.tabs_faq);
        tabLayout.setupWithViewPager(viewPager);



    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List< Fragment > mFragmentList = new ArrayList();
        private final List<String> mFragmentTitleList = new ArrayList();

        public ViewPagerAdapter( FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        public Fragment getItem( int i) {
            return this.mFragmentList.get(i);
        }

        public int getCount() {
            return this.mFragmentList.size();
        }

        public void addFragment( Fragment fragment, String str) {
            this.mFragmentList.add(fragment);
            this.mFragmentTitleList.add(str);
        }

        public CharSequence getPageTitle(int i) {
            return this.mFragmentTitleList.get(i);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id)
        {
            case android.R.id.home:
                FAQFragment.this.finish();
                break;

        }

        return super.onOptionsItemSelected(item);
    }
}
