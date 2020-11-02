package app.zingo.employeemanagements.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

import app.zingo.employeemanagements.model.Employee;
import app.zingo.employeemanagements.ui.Admin.EmployeesDashBoard;
import app.zingo.employeemanagements.utils.PreferenceHandler;
import app.zingo.employeemanagements.utils.ThreadExecuter;
import app.zingo.employeemanagements.utils.Util;
import app.zingo.employeemanagements.WebApi.EmployeeApi;
import app.zingo.employeemanagements.base.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TeamMembersAdapter extends RecyclerView.Adapter< TeamMembersAdapter.ViewHolder> {

    private Context context;
    private ArrayList< Employee > departments;

    public TeamMembersAdapter(Context context, ArrayList< Employee > departments)
    {
        this.context = context;
        this.departments = departments;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_team_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final Employee employee = departments.get(position);

        if(employee!=null){

            holder.mEmName.setText(""+employee.getEmployeeName());


            holder.mShowList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(holder.mTeamEmLis.getVisibility()==View.VISIBLE){
                        holder.mTeamEmLis.setVisibility(View.GONE);
                    }else if(holder.mTeamEmLis.getVisibility()==View.GONE){
                        holder.mTeamEmLis.setVisibility(View.VISIBLE);
                        holder.mTeamEmLis.removeAllViews();
                        getProfiles(employee.getEmployeeId(),holder.mTeamEmLis);
                    }

                }
            });

            holder.mEmName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(context, EmployeesDashBoard.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("Profile",employee);
                    bundle.putInt("ProfileId",employee.getEmployeeId());
                    intent.putExtras(bundle);
                    context.startActivity(intent);


                }
            });


        }


    }

    @Override
    public int getItemCount() {
        return departments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView mEmName;
        CardView mLayout;
        RecyclerView mTeamEmLis;
        ImageView mShowList;

        public ViewHolder(View itemView) {
            super(itemView);

            mEmName = itemView.findViewById(R.id.employee_name);
            mLayout = itemView.findViewById(R.id.employee);
            mTeamEmLis = itemView.findViewById(R.id.team_emp_list);
            mShowList = itemView.findViewById(R.id.shw_emp_list);

        }
    }

    private void getProfiles(final int empId,final RecyclerView mEmList){


        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Loading Team members");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new ThreadExecuter ().execute( new Runnable() {
            @Override
            public void run() {
                EmployeeApi apiService = Util.getClient().create( EmployeeApi.class);
                Call<ArrayList< Employee >> call = apiService.getEmployeesByOrgId(PreferenceHandler.getInstance(context).getCompanyId());

                call.enqueue(new Callback<ArrayList< Employee >>() {
                    @Override
                    public void onResponse( Call<ArrayList< Employee >> call, Response<ArrayList< Employee >> response) {
                        int statusCode = response.code();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {


                            if (progressDialog != null&&progressDialog.isShowing())
                                progressDialog.dismiss();
                            ArrayList< Employee > list = response.body();
                            ArrayList< Employee > teamlist = new ArrayList<>();


                            if (list !=null && list.size()!=0) {

                                for ( Employee emp:list) {

                                    if(emp.getManagerId()==empId){
                                        teamlist.add(emp);
                                    }

                                }

                                if(teamlist!=null&&teamlist.size()!=0){

                                    Collections.sort(teamlist, Employee.compareEmployee);
                                    TeamMembersAdapter adapter = new TeamMembersAdapter (context, teamlist);
                                    mEmList.setAdapter(adapter);

                                }else{
                                    Toast.makeText(context, "You don't have any Team", Toast.LENGTH_SHORT).show();
                                }



                                //}

                            }else{
                               // Toast.makeText(TeamMembersList.this,"No Employees added",Toast.LENGTH_LONG).show();

                            }

                        }else {


                            //Toast.makeText(TeamMembersList.this, "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure( Call<ArrayList< Employee >> call, Throwable t) {
                        // Log error here since request failed
                        if (progressDialog != null&&progressDialog.isShowing())
                            progressDialog.dismiss();
                        Log.e("TAG", t.toString());
                    }
                });
            }


        });
    }
}

