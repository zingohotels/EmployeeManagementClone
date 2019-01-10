package app.zingo.employeemanagements.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import app.zingo.employeemanagements.Model.Employee;
import app.zingo.employeemanagements.R;
import app.zingo.employeemanagements.UI.Admin.CreatePaySlip;
import app.zingo.employeemanagements.UI.Admin.EmployeeLiveMappingScreen;
import app.zingo.employeemanagements.UI.Admin.EmployeesDashBoard;
import app.zingo.employeemanagements.UI.Admin.TaskManagementHost;
import app.zingo.employeemanagements.UI.Employee.EmployeeMeetingHost;
import app.zingo.employeemanagements.UI.NewAdminDesigns.EmployeeEditScreen;

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

                    Intent intent = new Intent(context, EmployeeEditScreen.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("EmployeeId",list.get(position).getEmployeeId());
                    bundle.putSerializable("Employee",list.get(position));
                    intent.putExtras(bundle);
                    context.startActivity(intent);


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
