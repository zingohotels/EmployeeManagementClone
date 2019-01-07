package app.zingo.employeemanagements.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import app.zingo.employeemanagements.Model.LoginDetailsNotificationManagers;
import app.zingo.employeemanagements.Model.Tasks;
import app.zingo.employeemanagements.R;

/**
 * Created by ZingoHotels Tech on 07-01-2019.
 */

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.ViewHolder>{

    private Context context;
    private ArrayList<Tasks> list;


    public TaskListAdapter(Context context, ArrayList<Tasks> list) {

        this.context = context;
        this.list = list;


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_task_list_employee, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Tasks dto = list.get(position);

        if(dto!=null){


            String status = dto.getStatus();


            holder.mTaskName.setText(dto.getTaskName());
            holder.mTaskDesc.setText("Description: \n"+dto.getTaskDescription());
            holder.mDuration.setText(dto.getStartDate()+" to "+dto.getEndDate());
            holder.mDeadLine.setText(dto.getDeadLine());
            holder.mStatus.setText(dto.getStatus());

            if(status.equalsIgnoreCase("Pending")){
                holder.mStatus.setBackgroundColor(Color.parseColor("#FF0000"));
            }else if(status.equalsIgnoreCase("Completed")){
                holder.mStatus.setBackgroundColor(Color.parseColor("#00FF00"));
            }if(status.equalsIgnoreCase("Closed")){
                holder.mStatus.setBackgroundColor(Color.parseColor("#FFFF00"));
            }
        }






    }




    @Override
    public int getItemCount() {
        return list.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder /*implements View.OnClickListener*/ {

        public TextView mTaskName,mTaskDesc,mDuration,mDeadLine,mStatus;

        public LinearLayout mNotificationMain;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(true);

            mTaskName = (TextView)itemView.findViewById(R.id.title_task);
            mTaskDesc = (TextView)itemView.findViewById(R.id.title_description);
            mDuration = (TextView)itemView.findViewById(R.id.time_task);
            mDeadLine = (TextView)itemView.findViewById(R.id.dead_line_task);
            mStatus = (TextView)itemView.findViewById(R.id.status);

            mNotificationMain = (LinearLayout) itemView.findViewById(R.id.attendanceItem);


        }
    }
}
