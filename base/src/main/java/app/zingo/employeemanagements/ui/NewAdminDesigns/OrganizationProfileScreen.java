package app.zingo.employeemanagements.ui.NewAdminDesigns;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MenuItem;

import java.util.ArrayList;

import app.zingo.employeemanagements.adapter.NavigationAdapter;
import app.zingo.employeemanagements.model.Navigation_Model;
import app.zingo.employeemanagements.utils.PreferenceHandler;
import app.zingo.employeemanagements.base.R;

public class OrganizationProfileScreen extends AppCompatActivity {

    String[] tv1 = {"About Organization","Branches","Holidays","Office Timing","Break Timing"};//

    Integer[] imagehistory = {R.drawable.office_about, R.drawable.branches,
            R.drawable.holiday_list, R.drawable.office_timings, R.drawable.coffee_break};//
    Integer[] image1 ={R.drawable.ic_chevron_right_black_24dp, R.drawable.ic_chevron_right_black_24dp, R.drawable.ic_chevron_right_black_24dp,
            R.drawable.ic_chevron_right_black_24dp, R.drawable.ic_chevron_right_black_24dp};//

    private RecyclerView recyclerView;
    private NavigationAdapter navigation_adapter;
    private ArrayList<Navigation_Model> navigation_models;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{

            setContentView(R.layout.activity_organization_profile_screen);

            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Organization Profile");

            recyclerView = findViewById(R.id.organization_profile_list);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager( OrganizationProfileScreen.this);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());

            navigation_models = new ArrayList<>();

            for(int i=0;i < imagehistory.length;i++) {
                Navigation_Model ab = new Navigation_Model(tv1[i],imagehistory[i],image1[i]);
                navigation_models.add(ab);
            }
            navigation_adapter = new NavigationAdapter ( OrganizationProfileScreen.this,navigation_models);
            recyclerView.setAdapter(navigation_adapter);

        }catch(Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id)
        {
            case android.R.id.home:

                OrganizationProfileScreen.this.finish();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        PreferenceHandler.getInstance( OrganizationProfileScreen.this).setBranchId(0);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        PreferenceHandler.getInstance( OrganizationProfileScreen.this).setBranchId(0);
    }
}
