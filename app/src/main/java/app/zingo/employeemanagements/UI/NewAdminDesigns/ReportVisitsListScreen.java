package app.zingo.employeemanagements.UI.NewAdminDesigns;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import java.util.ArrayList;

import app.zingo.employeemanagements.Adapter.EmployeeMeetingAdapter;
import app.zingo.employeemanagements.Adapter.ExpenseReportAdapter;
import app.zingo.employeemanagements.Model.Expenses;
import app.zingo.employeemanagements.Model.Meetings;
import app.zingo.employeemanagements.R;

public class ReportVisitsListScreen extends AppCompatActivity {

    View layout;
    private EmployeeMeetingAdapter mAdapter;
    RecyclerView mTaskList;

    Toolbar mToolbar;

    ArrayList<Meetings> employeeMeetings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        try{

            setContentView(R.layout.activity_report_visits_list_screen);

            setupToolbar();
            Bundle bundle = getIntent().getExtras();
            if (bundle!=null) {
                employeeMeetings = (ArrayList<Meetings>)bundle.getSerializable("EmployeeMeetings");
            }

            mTaskList = (RecyclerView) findViewById(R.id.visit_list_report);
            mTaskList.setLayoutManager(new LinearLayoutManager(this));


            if(employeeMeetings!=null&&employeeMeetings.size()!=0){

                mAdapter = new EmployeeMeetingAdapter(ReportVisitsListScreen.this,employeeMeetings);
                mTaskList.setAdapter(mAdapter);

            }else{
                Toast.makeText(ReportVisitsListScreen.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }


        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void setupToolbar() {
        this.mToolbar = (Toolbar) findViewById(R.id.app_bar);
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
