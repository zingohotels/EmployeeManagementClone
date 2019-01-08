package app.zingo.employeemanagements.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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

            String froms = dto.getStartDate();
            String tos = dto.getEndDate();

            Date afromDate = null;
            Date atoDate = null;

            if(froms!=null&&!froms.isEmpty()){

                if(froms.contains("T")){

                    String dojs[] = froms.split("T");

                    try {
                        afromDate = new SimpleDateFormat("yyyy-MM-dd").parse(dojs[0]);
                        froms = new SimpleDateFormat("dd MMM yyyy").format(afromDate);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }


                }

            }

            if(tos!=null&&!tos.isEmpty()){

                if(tos.contains("T")){

                    String dojs[] = tos.split("T");

                    try {
                        atoDate = new SimpleDateFormat("yyyy-MM-dd").parse(dojs[0]);
                        tos = new SimpleDateFormat("dd MMM yyyy").format(atoDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                                              /*  String parse = new SimpleDateFormat("MMM yyyy").format(atoDate);
                                                toDate = new SimpleDateFormat("MMM yyyy").parse(parse);*/

                }

            }
            holder.mDuration.setText(froms+" to "+tos);
            holder.mDeadLine.setText(dto.getDeadLine());
            holder.mStatus.setText(dto.getStatus());

            if(status.equalsIgnoreCase("Pending")){
                holder.mStatus.setBackgroundColor(Color.parseColor("#FF0000"));
            }else if(status.equalsIgnoreCase("Completed")){
                holder.mStatus.setBackgroundColor(Color.parseColor("#00FF00"));
            }else if(status.equalsIgnoreCase("Closed")){
                holder.mStatus.setBackgroundColor(Color.parseColor("#FFFF00"));
            }else if(status.equalsIgnoreCase("On-Going")){
                holder.mStatus.setBackgroundColor(Color.parseColor("#D81B60"));
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
