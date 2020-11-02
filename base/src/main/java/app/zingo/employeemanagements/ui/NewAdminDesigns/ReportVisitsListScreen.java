package app.zingo.employeemanagements.ui.NewAdminDesigns;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import app.zingo.employeemanagements.adapter.EmployeeMeetingAdapter;
import app.zingo.employeemanagements.model.Meetings;
import app.zingo.employeemanagements.base.R;

public class ReportVisitsListScreen extends AppCompatActivity {

    View layout;
    private EmployeeMeetingAdapter mAdapter;
    RecyclerView mTaskList;

    Toolbar mToolbar;

    ArrayList< Meetings > employeeMeetings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        try{

            setContentView(R.layout.activity_report_visits_list_screen);

            setupToolbar();
            Bundle bundle = getIntent().getExtras();
            if (bundle!=null) {
                employeeMeetings = (ArrayList< Meetings >)bundle.getSerializable("EmployeeMeetings");
            }

            mTaskList = findViewById(R.id.visit_list_report);
            mTaskList.setLayoutManager(new LinearLayoutManager(this));


            if(employeeMeetings!=null&&employeeMeetings.size()!=0){

                mAdapter = new EmployeeMeetingAdapter( ReportVisitsListScreen.this,employeeMeetings);
                mTaskList.setAdapter(mAdapter);

            }else{
                Toast.makeText( ReportVisitsListScreen.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }


        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void setupToolbar() {
        this.mToolbar = findViewById(R.id.app_bar);
        setSupportActionBar(this.mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Visits");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id)
        {
            case android.R.id.home:

                ReportVisitsListScreen.this.finish();

        }
        return super.onOptionsItemSelected(item);
    }
}
