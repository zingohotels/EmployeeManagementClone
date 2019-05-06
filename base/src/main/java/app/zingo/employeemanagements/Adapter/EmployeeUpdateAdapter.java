package app.zingo.employeemanagements.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import app.zingo.employeemanagements.Model.Employee;
import app.zingo.employeemanagements.base.R;
import app.zingo.employeemanagements.UI.Admin.CreatePaySlip;
import app.zingo.employeemanagements.UI.Admin.EmployeeLiveMappingScreen;
import app.zingo.employeemanagements.UI.Admin.EmployeesDashBoard;
import app.zingo.employeemanagements.UI.Admin.TaskManagementHost;
import app.zingo.employeemanagements.UI.Employee.EmployeeMeetingHost;
import app.zingo.employeemanagements.UI.NewAdminDesigns.EmployeeEditScreen;
import app.zingo.employeemanagements.UI.NewAdminDesigns.EmployeeUpdateListScreen;
import app.zingo.employeemanagements.Utils.PreferenceHandler;
import app.zingo.employeemanagements.Utils.ThreadExecuter;
import app.zingo.employeemanagements.Utils.Util;
import app.zingo.employeemanagements.WebApi.EmployeeApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ZingoHotels Tech on 10-01-2019.
 */

public class EmployeeUpdateAdapter  extends RecyclerView.Adapter<EmployeeUpdateAdapter.ViewHolder>{

    private Context context;
    private ArrayList<Employee> list;
    String type;

    public EmployeeUpdateAdapter(Context context, ArrayList<Employee> list,String type) {

        this.context = context;
        this.list = list;
        this.type = type;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_profile_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Employee dto = list.get(position);

        holder.mProfileName.setText(dto.getEmployeeName());
        holder.mProfileEmail.setText(dto.getPrimaryEmailAddress());


        holder.mProfileMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Do you want to do ?");
                builder.setCancelable(true);
                builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Intent intent = new Intent(context, EmployeeEditScreen.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt("EmployeeId",list.get(position).getEmployeeId());
                        bundle.putInt("OrganizationId",PreferenceHandler.getInstance(context).getBranchId());
                        bundle.putSerializable("Employee",list.get(position));
                        intent.putExtras(bundle);
                        context.startActivity(intent);





                    }
                });
                builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Do you want to delete?");
                        builder.setCancelable(false);
                        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                deleteprofile(dto.getEmployeeId());




                            }
                        });
                        builder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                holder.mProfileMain.setBackgroundColor(Color.parseColor("#ffffff"));

                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();


                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });


    }


    private void deleteprofile(final int id){
        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {
                EmployeeApi apiService = Util.getClient().create(EmployeeApi.class);
                Call<Employee> call = apiService.deletEmployee(id);

                call.enqueue(new Callback<Employee>() {
                    @Override
                    public void onResponse(Call<Employee> call, Response<Employee> response) {
                        int statusCode = response.code();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {

                            Employee dto = response.body();
                            //getPettyCash();
                            Intent refresh = new Intent(context,context.getClass());
                            context.startActivity(refresh);
                            ((Activity)context).finish();

                        }else {

                            Toast.makeText(context, "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Employee> call, Throwable t) {
                        Log.e("TAG", t.toString());
                    }
                });
            }


        });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder /*implements View.OnClickListener*/ {

        public TextView mProfileName,mProfileEmail;
        public LinearLayout mProfileMain;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(true);

            mProfileName = itemView.findViewById(R.id.profile_name_adapter);
            mProfileEmail = itemView.findViewById(R.id.profile_email_adapter);
            mProfileMain = itemView.findViewById(R.id.profileLayout);


        }
    }
}
