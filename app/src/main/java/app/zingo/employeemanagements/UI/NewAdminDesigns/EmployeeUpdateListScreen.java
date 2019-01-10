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
import app.zingo.employeemanagements.Adapter.EmployeeUpdateAdapter;
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

public class EmployeeUpdateListScreen extends AppCompatActivity {

    RecyclerView mProfileList;
    FloatingActionButton mAddProfiles;

    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
            setContentView(R.layout.activity_employee_update_list_screen);

            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Employee Details");

            mProfileList = (RecyclerView)findViewById(R.id.profile_list);
            mAddProfiles = (FloatingActionButton) findViewById(R.id.add_profile);


            mAddProfiles.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent employee =new Intent(EmployeeUpdateListScreen.this,CreateEmployeeScreen.class);
                    startActivity(employee);
                }
            });


        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        getProfiles();
    }

    private void getProfiles(){


        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading Employees");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {
                EmployeeApi apiService = Util.getClient().create(EmployeeApi.class);
                Call<ArrayList<Employee>> call = apiService.getEmployeesByOrgId(PreferenceHandler.getInstance(EmployeeUpdateListScreen.this).getCompanyId());

                call.enqueue(new Callback<ArrayList<Employee>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Employee>> call, Response<ArrayList<Employee>> response) {
                        int statusCode = response.code();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {


                            if (progressDialog!=null)
                                progressDialog.dismiss();
                            ArrayList<Employee> list = response.body();


                            if (list !=null && list.size()!=0) {

                                ArrayList<Employee> employees = new ArrayList<>();
                                for(int i=0;i<list.size();i++){

                                    if(list.get(i).getEmployeeId()!=PreferenceHandler.getInstance(EmployeeUpdateListScreen.this).getUserId()){

                                        employees.add(list.get(i));

                                    }
                                }

                                if(employees!=null&&employees.size()!=0){
                                    Collections.sort(employees,Employee.compareEmployee);
                                    EmployeeUpdateAdapter adapter = new EmployeeUpdateAdapter(EmployeeUpdateListScreen.this, employees,type);
                                    mProfileList.setAdapter(adapter);

                                    if(PreferenceHandler.getInstance(EmployeeUpdateListScreen.this).getEmployeeLimit()>=employees.size()){
                                        mAddProfiles.setVisibility(View.GONE);
                                    }
                                }else{
                                    Toast.makeText(EmployeeUpdateListScreen.this,"No Employees added",Toast.LENGTH_LONG).show();
                                    Intent employee =new Intent(EmployeeUpdateListScreen.this,CreateEmployeeScreen.class);
                                    startActivity(employee);
                                }


                                //}

                            }else{
                                Toast.makeText(EmployeeUpdateListScreen.this,"No Employees added",Toast.LENGTH_LONG).show();
                                Intent employee =new Intent(EmployeeUpdateListScreen.this,CreateEmployeeScreen.class);
                                startActivity(employee);
                            }

                        }else {


                            Toast.makeText(EmployeeUpdateListScreen.this, "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Employee>> call, Throwable t) {
                        // Log error here since request failed
                        if (progressDialog!=null)
                            progressDialog.dismiss();
                        Log.e("TAG", t.toString());
                    }
                });
            }


        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id)
        {
            case android.R.id.home:

                EmployeeUpdateListScreen.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
