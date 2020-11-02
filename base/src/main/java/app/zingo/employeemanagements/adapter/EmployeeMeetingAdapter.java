package app.zingo.employeemanagements.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputEditText;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

import app.zingo.employeemanagements.model.Employee;
import app.zingo.employeemanagements.model.Meetings;
import app.zingo.employeemanagements.ui.Common.ImageFullScreenActivity;
import app.zingo.employeemanagements.utils.ThreadExecuter;
import app.zingo.employeemanagements.utils.Util;
import app.zingo.employeemanagements.WebApi.EmployeeApi;
import app.zingo.employeemanagements.base.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ZingoHotels Tech on 17-10-2018.
 */

public class EmployeeMeetingAdapter extends RecyclerView.Adapter<EmployeeMeetingAdapter.ViewHolder>{

    private Context context;
    private ArrayList<Meetings> list;

    public EmployeeMeetingAdapter(Context context, ArrayList<Meetings> list) {

        this.context = context;
        this.list = list;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        try {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.adapter_meeting_list, parent, false);
            ViewHolder viewHolder = new ViewHolder(v);
            return viewHolder;

        }catch (Exception e){
            e.printStackTrace();

            return null;
        }

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Meetings dto = list.get(position);

        if(dto!=null){

            getEmployees(dto.getEmployeeId(),holder.mEmpName);
            holder.mMeetingCount.setText("Meeting "+(position+1));
            Random rnd = new Random();
            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));


            holder.mView.setBackgroundColor(color);
            holder.mMeetingCount.setTextColor(color);

            if(dto.getMeetingPersonDetails().contains("%")){

                String[] person = dto.getMeetingPersonDetails ( ).split ( "%" );
                holder.mContact.setText(person[1]);
                holder.mClient.setText(person[0]);
            }else{
                holder.mClient.setText(dto.getMeetingPersonDetails());
            }

            holder.mPurpose.setText(dto.getMeetingAgenda());
            holder.mRemarks.setText(dto.getMeetingDetails());

            ArrayList<String> imageList = new ArrayList <> (  );

            String image_start = dto.getStartPlaceID ();
            String image_end = dto.getEndPlaceID ();

            if(image_start!=null&&!image_start.isEmpty ()){

                imageList.add ( image_start );

            }


            if(image_end!=null&&!image_end.isEmpty ()){

                imageList.add ( image_end );

            }

            if(imageList.size ()!=0){
                holder.mMeetingImages.setVisibility ( View.VISIBLE);
            }else{
                holder.mMeetingImages.setVisibility ( View.GONE);

            }

            holder.mMeetingImages.setOnClickListener ( v -> {

                Bundle bundle = new Bundle (  );
                bundle.putStringArrayList ( "imageList",imageList );
                Intent imageFull = new Intent ( context, ImageFullScreenActivity.class );
                imageFull.putExtras ( bundle);
                (( Activity )context).startActivity ( imageFull );

            } );

            String startTime = dto.getStartTime();
            String endTime = dto.getEndTime();
            String startLocation = dto.getStartLocation();
            String endLocation = dto.getEndLocation();

            String checkDetails="",outDetails="";

            if(startTime!=null){
                checkDetails = checkDetails+"Meeting Start Time:\n"+startTime;
            }

            if(startLocation!=null){

                checkDetails = checkDetails+"\n\nMeeting Location:\n"+startLocation;
            }

            if(endTime!=null){
                outDetails = outDetails+"Meeting End Time:\n"+endTime;
            }

            if(endLocation!=null){

                outDetails = outDetails+"\n\nMeeting Location:\n"+endLocation;
            }


            if(!checkDetails.isEmpty()){

                holder.mCheckInDetails.setText(checkDetails);
            }

            if(!outDetails.isEmpty()){

                holder.mCheckOutDetails.setText(outDetails);
            }
        }




    }




    @Override
    public int getItemCount() {
        return list.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder /*implements View.OnClickListener*/ {

        EditText mRemarks,mCheckInDetails,mCheckOutDetails;
        TextInputEditText mClient,mContact,mPurpose;
        TextView mMeetingCount,mEmpName;
        ImageView mMeetingImages;
        public View mView;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(true);

            mRemarks = itemView.findViewById(R.id.meeting_remarks);
            mMeetingCount = itemView.findViewById(R.id.meeting_count);
            mEmpName = itemView.findViewById(R.id.empl_name);
            mCheckInDetails = itemView.findViewById(R.id.check_in_details);
            mCheckOutDetails = itemView.findViewById(R.id.check_out_details);
            mClient = itemView.findViewById(R.id.client_name);
            mContact = itemView.findViewById(R.id.client_contact);
            mPurpose = itemView.findViewById(R.id.purpose_meeting);
            mView = itemView.findViewById(R.id.view_color);
            mMeetingImages = itemView.findViewById(R.id.meeting_images);



        }
    }

    private void getEmployees(final int id,final TextView empName){


        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {
                EmployeeApi apiService = Util.getClient().create(EmployeeApi.class);
                Call<ArrayList<Employee>> call = apiService.getProfileById(id);

                call.enqueue(new Callback<ArrayList<Employee>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Employee>> call, Response<ArrayList<Employee>> response) {
                        int statusCode = response.code();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {


                           /* if (progressDialog != null&&progressDialog.isShowing())
                                progressDialog.dismiss();*/
                            ArrayList<Employee> list = response.body();


                            if (list !=null && list.size()!=0) {

                                final Employee employees = list.get(0);
                                if(employees!=null){

                                    empName.setText(""+employees.getEmployeeName());
                                }





                                //}

                            }else{

                            }

                        }else {



                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Employee>> call, Throwable t) {
                        // Log error here since request failed
                      /*  if (progressDialog != null&&progressDialog.isShowing())
                            progressDialog.dismiss();*/

                        Log.e("TAG", t.toString());
                    }
                });
            }


        });
    }
}
