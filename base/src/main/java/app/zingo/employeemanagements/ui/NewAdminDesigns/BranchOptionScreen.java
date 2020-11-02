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
import app.zingo.employeemanagements.base.R;

public class BranchOptionScreen extends AppCompatActivity {

    String[] tv1 = {"Branch Info","Departments","Employees","Holidays","Office Timing"};

    Integer[] imagehistory = {R.drawable.office_about,
            R.drawable.branches, R.drawable.branch_employees, R.drawable.holiday_list, R.drawable.office_timings};
    Integer[] image1 ={R.drawable.ic_chevron_right_black_24dp, R.drawable.ic_chevron_right_black_24dp, R.drawable.ic_chevron_right_black_24dp, R.drawable.ic_chevron_right_black_24dp,
            R.drawable.ic_chevron_right_black_24dp};

    private RecyclerView recyclerView;
    private NavigationAdapter navigation_adapter;
    private ArrayList< Navigation_Model > navigation_models;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{

            setContentView(R.layout.activity_branch_option_screen);

            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Branch Details");

            recyclerView = findViewById(R.id.organization_profile_list);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager( BranchOptionScreen.this);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());

            navigation_models = new ArrayList<>();

            for(int i=0;i < imagehistory.length;i++) {
                Navigation_Model ab = new Navigation_Model (tv1[i],imagehistory[i],image1[i]);
                navigation_models.add(ab);
            }
            navigation_adapter = new NavigationAdapter ( BranchOptionScreen.this,navigation_models);
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

                BranchOptionScreen.this.finish();
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}
