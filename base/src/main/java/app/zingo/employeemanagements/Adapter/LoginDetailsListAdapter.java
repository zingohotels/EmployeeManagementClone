package app.zingo.employeemanagements.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

import app.zingo.employeemanagements.Model.LoginDetails;
import app.zingo.employeemanagements.base.R;

/**
 * Created by ZingoHotels Tech on 27-10-2018.
 */

public class LoginDetailsListAdapter  extends RecyclerView.Adapter<LoginDetailsListAdapter.ViewHolder>{

    private Context context;
    private ArrayList<LoginDetails> list;

    public LoginDetailsListAdapter(Context context, ArrayList<LoginDetails> list) {

        this.context = context;
        this.list = list;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        try {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.adapter_login_details_list, parent, false);
            ViewHolder viewHolder = new ViewHolder(v);
            return viewHolder;

        }catch (Exception e){
            e.printStackTrace();

            return null;
        }

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final LoginDetails dto = list.get(position);

        if(dto!=null){

            holder.mMeetingCount.setText("Login "+(position+1));
            Random rnd = new Random();
            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));


            holder.mView.setBackgroundColor(color);
            holder.mMeetingCount.setTextColor(color);

        }

        String startTime = dto.getLoginTime();
        String endTime = dto.getLogOutTime();
        String startLocation = dto.getLocation();
        String endLocation = dto.getLocation();


        String checkDetails="",outDetails="";

        if(startTime!=null){
            checkDetails = checkDetails+"Login  Time:\n"+startTime;
        }

        if(startLocation!=null){

            checkDetails = checkDetails+"\n\nLogin Location:\n"+startLocation;
        }

        if(endTime!=null){
            outDetails = outDetails+"Logout Time:\n"+endTime;
        }

        if(endLocation!=null){

            outDetails = outDetails+"\n\nLogout Location:\n"+endLocation;
        }


        if(checkDetails!=null&&!checkDetails.isEmpty()){

            holder.mCheckInDetails.setText(checkDetails);
        }

        if(outDetails!=null&&!outDetails.isEmpty()){

            holder.mCheckOutDetails.setText(outDetails);
        }


    }




    @Override
    public int getItemCount() {
        return list.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder /*implements View.OnClickListener*/ {

        EditText mCheckInDetails,mCheckOutDetails;
        TextView mMeetingCount;
        public View mView;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(true);

            mMeetingCount = itemView.findViewById(R.id.meeting_count);
            mCheckInDetails = itemView.findViewById(R.id.check_in_details);
            mCheckOutDetails = itemView.findViewById(R.id.check_out_details);
            mView = itemView.findViewById(R.id.view_color);



        }
    }
}
