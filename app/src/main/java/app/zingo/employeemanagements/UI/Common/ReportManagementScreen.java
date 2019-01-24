package app.zingo.employeemanagements.UI.Common;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import app.zingo.employeemanagements.R;
import app.zingo.employeemanagements.UI.Employee.EmployeeListScreen;

public class ReportManagementScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
            setContentView(R.layout.activity_report_management_screen);

            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Report Management");



        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id)
        {
            case android.R.id.home:

                ReportManagementScreen.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
