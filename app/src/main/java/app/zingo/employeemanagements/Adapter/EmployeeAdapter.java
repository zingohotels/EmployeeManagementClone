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
import app.zingo.employeemanagements.R;
import app.zingo.employeemanagements.UI.Admin.CreatePaySlip;
import app.zingo.employeemanagements.UI.Admin.EmployeeLiveMappingScreen;
import app.zingo.employeemanagements.UI.Admin.EmployeesDashBoard;
import app.zingo.employeemanagements.UI.Admin.TaskManagementHost;
import app.zingo.employeemanagements.UI.Common.ReportManagementScreen;
import app.zingo.employeemanagements.UI.Employee.EmployeeMeetingHost;
import app.zingo.employeemanagements.UI.Employee.LeaveManagementHost;
import app.zingo.employeemanagements.UI.NewAdminDesigns.EmployeeExpenseList;
import app.zingo.employeemanagements.UI.NewAdminDesigns.LeaveEmployeeListScreen;
import app.zingo.employeemanagements.Utils.ThreadExecuter;
import app.zingo.employeemanagements.Utils.Util;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ZingoHotels Tech on 28-09-2018.
 */

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.ViewHolder>{

    private Context context;
    private ArrayList<Employee> list;
    String type;

    public EmployeeAdapter(Context context, ArrayList<Employee> list,String type) {

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

                if(type!=null&&type.equalsIgnoreCase("Meetings")){

                    Intent intent = new Intent(context, EmployeeMeetingHost.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("EmployeeId",list.get(position).getEmployeeId());
                    intent.putExtras(bundle);
                    context.startActivity(intent);

                }else if(type!=null&&type.equalsIgnoreCase("attendance")){

                    Intent intent = new Intent(context, EmployeesDashBoard.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("Profile",list.get(position));
                    bundle.putInt("ProfileId",list.get(position).getEmployeeId());
                    intent.putExtras(bundle);
                    context.startActivity(intent);

                }else if(type!=null&&type.equalsIgnoreCase("Expense")){

                    Intent intent = new Intent(context, EmployeeExpenseList.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("Profile",list.get(position));
                    bundle.putInt("EmployeeId",list.get(position).getEmployeeId());
                    intent.putExtras(bundle);
                    context.startActivity(intent);

                }else  if(type!=null&&type.equalsIgnoreCase("Salary")){

                    Intent intent = new Intent(context, CreatePaySlip.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("EmployeeId",list.get(position).getEmployeeId());
                    bundle.putSerializable("Employee",list.get(position));
                    intent.putExtras(bundle);
                    context.startActivity(intent);

                }else  if(type!=null&&type.equalsIgnoreCase("Live")){

                    Intent intent = new Intent(context, EmployeeLiveMappingScreen.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("EmployeeId",list.get(position).getEmployeeId());
                    bundle.putSerializable("Employee",list.get(position));
                    intent.putExtras(bundle);
                    context.startActivity(intent);

                }else  if(type!=null&&type.equalsIgnoreCase("Task")){

                    Intent intent = new Intent(context, TaskManagementHost.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("EmployeeId",list.get(position).getEmployeeId());
                    bundle.putInt("DepartmentId",list.get(position).getDepartmentId());
                    bundle.putSerializable("Employee",list.get(position));
                    intent.putExtras(bundle);
                    context.startActivity(intent);

                }else  if(type!=null&&type.equalsIgnoreCase("Leave")){

                    Intent intent = new Intent(context, LeaveEmployeeListScreen.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("EmployeeId",list.get(position).getEmployeeId());
                    bundle.putSerializable("Employee",list.get(position));
                    intent.putExtras(bundle);
                    context.startActivity(intent);

                }else  if(type!=null&&type.equalsIgnoreCase("Report")){

                    Intent intent = new Intent(context, ReportManagementScreen.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("EmployeeId",list.get(position).getEmployeeId());
                    bundle.putSerializable("Employee",list.get(position));
                    intent.putExtras(bundle);
                    context.startActivity(intent);

                }else{
                    Intent intent = new Intent(context, EmployeesDashBoard.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("Profile",list.get(position));
                    bundle.putInt("ProfileId",list.get(position).getEmployeeId());
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }



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

            mProfileName = (TextView)itemView.findViewById(R.id.profile_name_adapter);
            mProfileEmail = (TextView)itemView.findViewById(R.id.profile_email_adapter);
            mProfileMain = (LinearLayout) itemView.findViewById(R.id.profileLayout);


        }
    }
}
