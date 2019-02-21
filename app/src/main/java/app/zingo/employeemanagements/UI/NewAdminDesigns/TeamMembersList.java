package app.zingo.employeemanagements.UI.NewAdminDesigns;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

import app.zingo.employeemanagements.Adapter.EmployeeUpdateAdapter;
import app.zingo.employeemanagements.Adapter.TeamMembersAdapter;
import app.zingo.employeemanagements.Model.Employee;
import app.zingo.employeemanagements.R;
import app.zingo.employeemanagements.UI.Employee.CreateEmployeeScreen;
import app.zingo.employeemanagements.Utils.PreferenceHandler;
import app.zingo.employeemanagements.Utils.ThreadExecuter;
import app.zingo.employeemanagements.Utils.Util;
import app.zingo.employeemanagements.WebApi.EmployeeApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TeamMembersList extends AppCompatActivity {

    RecyclerView mTeamList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{

            setContentView(R.layout.activity_team_members_list);

            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Team Details");

            mTeamList = (RecyclerView)findViewById(R.id.team_list);

            getProfiles();


        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void getProfiles(){


        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading Team members");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {
                EmployeeApi apiService = Util.getClient().create(EmployeeApi.class);
                Call<ArrayList<Employee>> call = apiService.getEmployeesByOrgId(PreferenceHandler.getInstance(TeamMembersList.this).getCompanyId());

                call.enqueue(new Callback<ArrayList<Employee>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Employee>> call, Response<ArrayList<Employee>> response) {
                        int statusCode = response.code();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {


                            if (progressDialog!=null)
                                progressDialog.dismiss();
                            ArrayList<Employee> list = response.body();
                            ArrayList<Employee> teamlist = new ArrayList<>();


                            if (list !=null && list.size()!=0) {

                                for (Employee emp:list) {

                                    if(emp.getManagerId()==PreferenceHandler.getInstance(TeamMembersList.this).getUserId()){
                                        teamlist.add(emp);
                                    }

                                }

                                if(teamlist!=null&&teamlist.size()!=0){

                                    Collections.sort(teamlist,Employee.compareEmployee);
                                    TeamMembersAdapter adapter = new TeamMembersAdapter(TeamMembersList.this, teamlist);
                                    mTeamList.setAdapter(adapter);

                                }else{
                                    Toast.makeText(TeamMembersList.this, "You don't have any Team", Toast.LENGTH_SHORT).show();
                                }



                                //}

                            }else{
                                Toast.makeText(TeamMembersList.this,"No Employees added",Toast.LENGTH_LONG).show();

                            }

                        }else {


                            Toast.makeText(TeamMembersList.this, "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
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

                TeamMembersList.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
