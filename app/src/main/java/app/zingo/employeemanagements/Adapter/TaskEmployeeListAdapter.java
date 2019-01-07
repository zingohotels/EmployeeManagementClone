package app.zingo.employeemanagements.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import app.zingo.employeemanagements.Model.Employee;
import app.zingo.employeemanagements.R;
import app.zingo.employeemanagements.UI.Admin.CreatePaySlip;
import app.zingo.employeemanagements.UI.Admin.EmployeeLiveMappingScreen;
import app.zingo.employeemanagements.UI.Admin.EmployeesDashBoard;
import app.zingo.employeemanagements.UI.Admin.TaskManagementHost;
import app.zingo.employeemanagements.UI.Employee.EmployeeMeetingHost;
import app.zingo.employeemanagements.UI.NewAdminDesigns.AdminNewMainScreen;
import app.zingo.employeemanagements.UI.NewAdminDesigns.DailyTargetsForEmployeeActivity;

/**
 * Created by ZingoHotels Tech on 07-01-2019.
 */

public class TaskEmployeeListAdapter  extends RecyclerView.Adapter<TaskEmployeeListAdapter.ViewHolder>{

    private Context context;
    private ArrayList<Employee> list;


    public TaskEmployeeListAdapter(Context context, ArrayList<Employee> list) {

        this.context = context;
        this.list = list;


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_task_employees, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Employee dto = list.get(position);

        if(dto!=null){

            holder.mProfileName.setText(dto.getEmployeeName());

            if(dto.getEmployeeImages()!=null&&dto.getEmployeeImages().size()!=0){
                String base=dto.getEmployeeImages().get(0).getImage();
                if(base != null && !base.isEmpty()){
                    Picasso.with(context).load(base).placeholder(R.drawable.profile_image).
                            error(R.drawable.profile_image).into(holder.mProfileImage);


                }
            }

            holder.mProfileMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    Intent intent = new Intent(context, DailyTargetsForEmployeeActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("Profile",list.get(position));
                    bundle.putInt("ProfileId",list.get(position).getEmployeeId());
                    intent.putExtras(bundle);
                    context.startActivity(intent);




                }
            });
        }






    }




    @Override
    public int getItemCount() {
        return list.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder /*implements View.OnClickListener*/ {

        public TextView mProfileName;
        ImageView mProfileImage;
        public LinearLayout mProfileMain;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(true);

            mProfileName = (TextView)itemView.findViewById(R.id.name);
            mProfileImage = (ImageView)itemView.findViewById(R.id.profilePicture);
            mProfileMain = (LinearLayout) itemView.findViewById(R.id.attendanceItem);


        }
    }
}
