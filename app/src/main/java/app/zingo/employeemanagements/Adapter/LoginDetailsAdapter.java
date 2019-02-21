package app.zingo.employeemanagements.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import app.zingo.employeemanagements.Model.LoginDetails;
import app.zingo.employeemanagements.R;

/**
 * Created by ZingoHotels Tech on 29-09-2018.
 */

public class LoginDetailsAdapter extends RecyclerView.Adapter<LoginDetailsAdapter.ViewHolder>{

    private Context context;
    private ArrayList<LoginDetails> list;
    boolean visible;

    public LoginDetailsAdapter(Context context, ArrayList<LoginDetails> list) {

        this.context = context;
        this.list = list;


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        try{
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.adapter_dash_employee_admin, parent, false);
            ViewHolder viewHolder = new ViewHolder(v);
            return viewHolder;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        try{

            final LoginDetails loginDetails = list.get(position);

            if(loginDetails!=null){

                String loginTime = loginDetails.getLoginTime();
                String logoutTime = loginDetails.getLogOutTime();
                String loginDate = loginDetails.getLoginDate();
                String dateValue = "";

                if(loginDate.contains("T")){

                    String dateValues[] = loginDate.split("T");
                    dateValue = dateValues[0];

                }



                if(loginTime!=null&&!loginTime.isEmpty()){
                    holder.mLoginTime.setText(""+loginTime);
                }else{
                    holder.mLoginTime.setText("");
                }

                if(logoutTime!=null&&!logoutTime.isEmpty()){
                    holder.mLogoutTime.setText(""+logoutTime);
                }else{
                    holder.mLogoutTime.setText("Working");
                }


                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy hh:mm a");
                SimpleDateFormat sdfs = new SimpleDateFormat("MMM dd,yyyy");

                Date date=null;
                try {
                    date = new SimpleDateFormat("yyyy-MM-dd").parse(dateValue);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Date fd=null,td=null;
                String comDate = new SimpleDateFormat("MMM dd,yyyy").format(date);


                if(loginTime==null||loginTime.isEmpty()){

                    loginTime = comDate +" 00:00 am";
                }

                if(logoutTime==null||logoutTime.isEmpty()){

                    logoutTime = comDate  +" "+new SimpleDateFormat("hh:mm a").format(new Date()) ;
                }

                try {
                    fd = sdf.parse(""+loginTime);
                    td = sdf.parse(""+logoutTime);

                    long diffHrs = td.getTime() - fd.getTime();

                    int minutes = (int) ((diffHrs / (1000*60)) % 60);
                    int hours   = (int) ((diffHrs / (1000*60*60)) % 24);
                    int days   = (int) ((diffHrs / (1000*60*60*24)));



                    holder.mDuration.setText(String.format("%02d", days)+":"+String.format("%02d", hours) +":"+String.format("%02d", minutes));

                } catch (ParseException e) {
                    e.printStackTrace();

                }


            }




        }catch (Exception e){
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder  {

        TextView mLoginTime,mLogoutTime,mDuration;




        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(true);

            mLoginTime = (TextView)itemView.findViewById(R.id.report_login);
            mLogoutTime = (TextView)itemView.findViewById(R.id.report_logout);
            mDuration = (TextView)itemView.findViewById(R.id.report_hours);



        }
    }

    public void dateCal(String date,String login,String logout,TextView textView){

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy hh:mm a");
        DecimalFormat df = new DecimalFormat("00");

        Date fd=null,td=null;

        try {
            fd = sdf.parse(""+login);
            td = sdf.parse(""+logout);

            long diff = td.getTime() - fd.getTime();
            long diffDays = diff / (24 * 60 * 60 * 1000);
            long Hours = diff / (60 * 60 * 1000) % 24;
            long Minutes = diff / (60 * 1000) % 60;
            long Seconds = diff / 1000 % 60;

            textView.setText(df.format(Hours)+":"+df.format(Minutes));
        } catch (ParseException e) {
            e.printStackTrace();
        }



    }
}

