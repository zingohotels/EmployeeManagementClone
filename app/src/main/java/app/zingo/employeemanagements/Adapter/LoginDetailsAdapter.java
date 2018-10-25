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
                    .inflate(R.layout.adapter_login_details, parent, false);
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

                String date[] = loginDetails.getLoginDate().split("T");
                String dateValue="";
                Date dates = null;
                try {
                    dates = new SimpleDateFormat("yyyy-MM-dd").parse(date[0]);
                    dateValue = new SimpleDateFormat("MMM dd,yyyy").format(dates);
                    holder.mDate.setText(dateValue);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String login = loginDetails.getLoginTime();
                String logout = loginDetails.getLogOutTime();



                if(login!=null&&!login.isEmpty()){
                    String time ="";
                    if(logout!=null&&!logout.isEmpty()){

                        time = login+" to "+logout;
                        dateCal(dateValue,login,logout,holder.mDuration);
                        holder.mDuration.setTextColor(Color.GREEN);

                    }else{

                        time = login+"-";
                        dateCal(dateValue,login,new SimpleDateFormat("MMM dd,yyyy hh:mm a").format(new Date()),holder.mDuration);
                        holder.mDuration.setTextColor(Color.RED);
                    }
                    holder.mTime.setText(time);

                }else{

                    holder.mTime.setText("");
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

        TextView mDate,mTime,mDuration;




        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(true);

            mDate = (TextView)itemView.findViewById(R.id.login_date);
            mTime = (TextView)itemView.findViewById(R.id.login_logout);
            mDuration = (TextView)itemView.findViewById(R.id.working_hours);



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

