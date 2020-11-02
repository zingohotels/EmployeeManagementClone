package app.zingo.employeemanagements.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

import app.zingo.employeemanagements.Custom.MyRegulerText;
import app.zingo.employeemanagements.Custom.MyTextView;
import app.zingo.employeemanagements.base.R;
import app.zingo.employeemanagements.model.Employee;
import app.zingo.employeemanagements.model.Organization;
import app.zingo.employeemanagements.ui.NewAdminDesigns.AdminNewMainScreen;
import app.zingo.employeemanagements.ui.NewAdminDesigns.BranchOptionScreen;
import app.zingo.employeemanagements.utils.PreferenceHandler;
import app.zingo.employeemanagements.utils.ThreadExecuter;
import app.zingo.employeemanagements.utils.Util;
import app.zingo.employeemanagements.WebApi.EmployeeApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BranchAdapter extends RecyclerView.Adapter< BranchAdapter.ViewHolder> {

    Context context;
    private ArrayList< Organization > organizations;

    public BranchAdapter(Context context, ArrayList< Organization > organizations)
    {
        this.context = context;
        this.organizations = organizations;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate( R.layout.branch_list_adapter,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final Organization organization = organizations.get(position);

        if(organization!=null){

            holder.mOrgName.setText(""+organization.getOrganizationName());
            holder.mAddress.setText(""+organization.getAddress());

            holder.mBranch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Do you want to do ?");
                    builder.setCancelable(true);
                    builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();

                            try{

                                Intent branch = new Intent(context,BranchOptionScreen.class);
                                PreferenceHandler.getInstance(context).setBranchId(organization.getOrganizationId());
                                ((Activity)context).startActivity(branch);



                            }catch (Exception e){
                                e.printStackTrace();
                                dialogInterface.dismiss();
                            }

                        }
                    });
                    builder.setNegativeButton("View", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();



                            PreferenceHandler.getInstance(context).setCompanyId(organization.getOrganizationId());
                            PreferenceHandler.getInstance(context).setHeadOrganizationId(organization.getHeadOrganizationId());
                            PreferenceHandler.getInstance(context).setCompanyName(organization.getOrganizationName());
                            PreferenceHandler.getInstance(context).setAppType(organization.getAppType());

                            PreferenceHandler.getInstance(context).setAppType(organization.getAppType());
                            PreferenceHandler.getInstance(context).setLicenseStartDate(organization.getLicenseStartDate());
                            PreferenceHandler.getInstance(context).setLicenseEndDate(organization.getLicenseEndDate());
                            PreferenceHandler.getInstance(context).setSignupDate(organization.getSignupDate());
                            PreferenceHandler.getInstance(context).setOrganizationLongi(organization.getLongitude());
                            PreferenceHandler.getInstance(context).setOrganizationLati(organization.getLatitude());
                            PreferenceHandler.getInstance(context).setPlanType(organization.getPlanType());
                            PreferenceHandler.getInstance(context).setEmployeeLimit(organization.getEmployeeLimit());
                            PreferenceHandler.getInstance(context).setPlanId(organization.getPlanId());
                            PreferenceHandler.getInstance(context).setResellerUserId(organization.getResellerProfileId());


                            try{

                                Intent main = new Intent(context, AdminNewMainScreen.class);  //your class
                                ((Activity)context).startActivity(main);


                            }catch (Exception e){
                                e.printStackTrace();
                            }


                        }
                    });


                    AlertDialog dialog = builder.create();
                    dialog.show();



                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return organizations.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

       MyTextView mOrgName;
       MyRegulerText mAddress;
       LinearLayout mBranch;

        public ViewHolder(View itemView) {
            super(itemView);

            mOrgName = itemView.findViewById( R.id.branch_name_adapter);
            mAddress = itemView.findViewById(R.id.branch_address);
            mBranch = itemView.findViewById(R.id.branch_click_layout);


        }
    }

    private void getEmpByDep(final int deptId,final RecyclerView mEmpList){


        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Loading Employees");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new ThreadExecuter ().execute( new Runnable() {
            @Override
            public void run() {
                EmployeeApi apiService = Util.getClient().create( EmployeeApi.class);
                Call<ArrayList<Employee>> call = apiService.getEmployeesByDepId(deptId);

                call.enqueue(new Callback<ArrayList<Employee>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Employee>> call, Response<ArrayList<Employee>> response) {
                        int statusCode = response.code();

                        if (progressDialog != null&&progressDialog.isShowing())
                            progressDialog.dismiss();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {



                            ArrayList<Employee> list = response.body();


                            if (list !=null && list.size()!=0) {

                                ArrayList<Employee> employees = new ArrayList<>();
                                for(int i=0;i<list.size();i++){

                                    if(list.get(i).getEmployeeId()!= PreferenceHandler.getInstance(context).getUserId()){

                                        employees.add(list.get(i));

                                    }
                                }

                                if(employees!=null&&employees.size()!=0){
                                    Collections.sort(employees, Employee.compareEmployee);

                                    EmployeeUpdateAdapter adapter = new EmployeeUpdateAdapter (context, employees,"Update");
                                    mEmpList.setAdapter(adapter);

                                }else{
                                    Toast.makeText(context,"No Employees added",Toast.LENGTH_LONG).show();
                                }


                                //}

                            }else{
                                Toast.makeText(context,"No Employees added",Toast.LENGTH_LONG).show();
                            }

                        }else {


                            Toast.makeText(context, "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Employee>> call, Throwable t) {
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

