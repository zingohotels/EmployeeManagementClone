package app.zingo.employeemanagements.UI.NewAdminDesigns;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

import app.zingo.employeemanagements.Adapter.EmployeeAdapter;
import app.zingo.employeemanagements.Model.Employee;
import app.zingo.employeemanagements.R;
import app.zingo.employeemanagements.UI.Employee.CreateEmployeeScreen;
import app.zingo.employeemanagements.UI.Employee.EmployeeListScreen;
import app.zingo.employeemanagements.Utils.PreferenceHandler;
import app.zingo.employeemanagements.Utils.ThreadExecuter;
import app.zingo.employeemanagements.Utils.Util;
import app.zingo.employeemanagements.WebApi.EmployeeApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PresentEmployeeListScreen extends AppCompatActivity {

    RecyclerView mProfileList;

    String type;
    ArrayList<Employee> presentEmployeeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        try{

            setContentView(R.layout.activity_present_employee_list_screen);

            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Employee Details");

            mProfileList = (RecyclerView)findViewById(R.id.profile_list);

            Bundle bundle = getIntent().getExtras();

            if(bundle!=null){
                type = bundle.getString("Type");
                presentEmployeeList = (ArrayList<Employee>)bundle.getSerializable("Employees");
            }

            if(presentEmployeeList!=null&&presentEmployeeList.size()!=0){

                Collections.sort(presentEmployeeList,Employee.compareEmployee);
                EmployeeAdapter adapter = new EmployeeAdapter(PresentEmployeeListScreen.this, presentEmployeeList,type);
                mProfileList.setAdapter(adapter);

            }else{
                Toast.makeText(PresentEmployeeListScreen.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }




        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id)
        {
            case android.R.id.home:

                PresentEmployeeListScreen.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
