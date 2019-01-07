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
import app.zingo.employeemanagements.Model.LoginDetailsNotificationManagers;
import app.zingo.employeemanagements.R;
import app.zingo.employeemanagements.UI.Admin.EmployeesDashBoard;

/**
 * Created by ZingoHotels Tech on 07-01-2019.
 */

public class LoginDetailsNotificationAdapter  extends RecyclerView.Adapter<LoginDetailsNotificationAdapter.ViewHolder>{

    private Context context;
    private ArrayList<LoginDetailsNotificationManagers> list;


    public LoginDetailsNotificationAdapter(Context context, ArrayList<LoginDetailsNotificationManagers> list) {

        this.context = context;
        this.list = list;


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_login_notifications, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final LoginDetailsNotificationManagers dto = list.get(position);

        if(dto!=null){

            String title = dto.getTitle();
            String status = dto.getStatus();

            if(title.contains("Login Details from ")){
                title = title.replace("Login Details from ","");
            }

            if(status.equalsIgnoreCase("In meeting")||status.equalsIgnoreCase("Login")){
                status = "Login";
            }
            holder.mTitle.setText(title+" - "+status);
            holder.mTime.setText(dto.getLoginDate());
            holder.mAddress.setText(dto.getLocation());
        }






    }




    @Override
    public int getItemCount() {
        return list.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder /*implements View.OnClickListener*/ {

        public TextView mTitle,mTime,mAddress;

        public LinearLayout mNotificationMain;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(true);

            mTitle = (TextView)itemView.findViewById(R.id.title_login_notifications);
            mTime = (TextView)itemView.findViewById(R.id.time_login);
            mAddress = (TextView)itemView.findViewById(R.id.login_address);
            mNotificationMain = (LinearLayout) itemView.findViewById(R.id.attendanceItem);


        }
    }
}
