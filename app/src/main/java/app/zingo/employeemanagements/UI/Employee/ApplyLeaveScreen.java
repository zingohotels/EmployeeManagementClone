package app.zingo.employeemanagements.UI.Employee;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import app.zingo.employeemanagements.Model.Employee;
import app.zingo.employeemanagements.Model.LeaveNotificationManagers;
import app.zingo.employeemanagements.Model.Leaves;
import app.zingo.employeemanagements.Model.MeetingDetailsNotificationManagers;
import app.zingo.employeemanagements.R;
import app.zingo.employeemanagements.Utils.Constants;
import app.zingo.employeemanagements.Utils.PreferenceHandler;
import app.zingo.employeemanagements.Utils.Util;
import app.zingo.employeemanagements.WebApi.EmployeeApi;
import app.zingo.employeemanagements.WebApi.LeaveAPI;
import app.zingo.employeemanagements.WebApi.LeaveNotificationAPI;
import app.zingo.employeemanagements.WebApi.MeetingNotificationAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApplyLeaveScreen extends AppCompatActivity {

    TextInputEditText mLeaveType,mFrom,mTo;
    EditText mLeaveComment;
    AppCompatButton mApply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{

            setContentView(R.layout.activity_apply_leave_screen);

            mLeaveType = (TextInputEditText)findViewById(R.id.leave_type);
            mLeaveType.setVisibility(View.GONE);
            mFrom = (TextInputEditText)findViewById(R.id.from_date);
            mTo = (TextInputEditText)findViewById(R.id.to_date);
            mLeaveComment = (EditText)findViewById(R.id.leave_comment);
            mApply = (AppCompatButton) findViewById(R.id.apply_leave);


            mFrom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    openDatePicker(mFrom);
                }
            });

            mTo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    openDatePicker(mTo);
                }
            });

            mApply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    validate();
                }
            });



        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void openDatePicker(final TextInputEditText tv) {
        // Get Current Date

        final Calendar c = Calendar.getInstance();
        int mYear  = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay   = c.get(Calendar.DAY_OF_MONTH);
        //launch datepicker modal
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        try {
                            Log.d("Date", "DATE SELECTED "+dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                            Calendar newDate = Calendar.getInstance();
                            newDate.set(year,monthOfYear,dayOfMonth);


                            String date1 = (monthOfYear + 1)  + "/" + (dayOfMonth) + "/" + year;

                            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy");



                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
                            try {
                                Date fdate = simpleDateFormat.parse(date1);

                                String from1 = sdf.format(fdate);

                                tv.setText(from1);


                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                        }
                        catch (Exception ex)
                        {
                            ex.printStackTrace();
                        }


                    }
                }, mYear, mMonth, mDay);


        datePickerDialog.show();

    }

    public void validate(){

        //String leaveType = mLeaveType.getText().toString();
        String from = mFrom.getText().toString();
        String to = mTo.getText().toString();
        String leaveComment = mLeaveComment.getText().toString();


        /*if(leaveType.isEmpty()){

            Toast.makeText(this, "Leave type is required", Toast.LENGTH_SHORT).show();

        }else */if(from.isEmpty()){

            Toast.makeText(this, "From date is required", Toast.LENGTH_SHORT).show();

        }else if(to.isEmpty()){

            Toast.makeText(this, "To date is required", Toast.LENGTH_SHORT).show();

        }else if(leaveComment.isEmpty()){

            Toast.makeText(this, "Leave Comment is required", Toast.LENGTH_SHORT).show();

        }else{

            try{
                Leaves leaves = new Leaves();
                //leaves.setLeaveType(leaveType);
                leaves.setLeaveComment(leaveComment);

                SimpleDateFormat df = new SimpleDateFormat("MMM dd,yyyy");
                SimpleDateFormat dfs = new SimpleDateFormat("MM/dd/yyyy");

                Date fromDate = df.parse(from);
                Date toDate = df.parse(to);
                leaves.setFromDate(dfs.format(fromDate));
                leaves.setToDate(dfs.format(toDate));
                leaves.setStatus("Pending");
                leaves.setLeaveType("UnConfirmed");
                int diffs = (int)dateCal(from,to);
                leaves.setNoOfDays(diffs);
                leaves.setApprovedDate(dfs.format(fromDate));
                leaves.setEmployeeId(PreferenceHandler.getInstance(ApplyLeaveScreen.this).getUserId());
                try {
                    addLeave(leaves);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }catch (Exception e){
                e.printStackTrace();
            }


        }
    }

    public long dateCal(String start,String end){

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy");
        System.out.println("Loigin "+start);
        System.out.println("Logout "+end);


        Date fd=null,td=null;



        try {
            fd = sdf.parse(""+start);
            td = sdf.parse(""+end);

            long diff = td.getTime() - fd.getTime();
            long Hours = diff / (60 * 60 * 1000) % 24;
            long Minutes = diff / (60 * 1000) % 60;
            long diffDays = diff / (24 * 60 * 60 * 1000);
            System.out.println("Diff "+diff);
            System.out.println("Hours "+Hours);
            System.out.println("Minutes "+Minutes);
          /*  long diffDays = diff / (24 * 60 * 60 * 1000);
            long Hours = diff / (60 * 60 * 1000) % 24;
            long Minutes = diff / (60 * 1000) % 60;
            long Seconds = diff / 1000 % 60;*/

            return  diffDays;
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }


    }

    public void addLeave(final Leaves leaves) throws Exception{



        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Saving Details..");
        dialog.setCancelable(false);
        dialog.show();

        LeaveAPI apiService = Util.getClient().create(LeaveAPI.class);

        Call<Leaves> call = apiService.addLeave(leaves);

        call.enqueue(new Callback<Leaves>() {
            @Override
            public void onResponse(Call<Leaves> call, Response<Leaves> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {
                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201) {

                        Leaves s = response.body();

                        if(s!=null){


                            //ApplyLeaveScreen.this.finish();
                            LeaveNotificationManagers lm = new LeaveNotificationManagers();
                            lm.setTitle("Apply For Leave");
                            lm.setMessage("Leave from "+s.getFromDate()+" to "+s.getToDate());
                            lm.setReason(s.getLeaveId()+","+s.getLeaveComment());
                            lm.setEmployeeId(s.getEmployeeId());
                            lm.setManagerId(PreferenceHandler.getInstance(ApplyLeaveScreen.this).getManagerId());
                            lm.setEmployeeName(PreferenceHandler.getInstance(ApplyLeaveScreen.this).getUserFullName());
                            lm.setFromDate(leaves.getFromDate());
                            lm.setToDate(leaves.getToDate());
                            lm.setLeaveId(s.getLeaveId()+"");
                            saveLeave(lm);


                        }




                    }else {
                        Toast.makeText(ApplyLeaveScreen.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception ex)
                {

                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }
                    ex.printStackTrace();
                }
//                callGetStartEnd();
            }

            @Override
            public void onFailure(Call<Leaves> call, Throwable t) {

                if(dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
                Toast.makeText(ApplyLeaveScreen.this, "Failed Due to "+t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());
            }
        });



    }

    public void saveLeave(final LeaveNotificationManagers leaves) throws Exception{



        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Saving Details..");
        dialog.setCancelable(false);
        dialog.show();

        LeaveNotificationAPI apiService = Util.getClient().create(LeaveNotificationAPI.class);

        Call<LeaveNotificationManagers> call = apiService.saveLeave(leaves);

        call.enqueue(new Callback<LeaveNotificationManagers>() {
            @Override
            public void onResponse(Call<LeaveNotificationManagers> call, Response<LeaveNotificationManagers> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {
                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201) {

                        LeaveNotificationManagers s = response.body();

                        if(s!=null){

                            leaves.setSenderId(Constants.SENDER_ID);
                            leaves.setServerId(Constants.SERVER_ID);

                           sendLeaveNotification(leaves);
                            //ApplyLeaveScreen.this.finish();


                        }




                    }else {
                        Toast.makeText(ApplyLeaveScreen.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception ex)
                {

                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }
                    ex.printStackTrace();
                }
//                callGetStartEnd();
            }

            @Override
            public void onFailure(Call<LeaveNotificationManagers> call, Throwable t) {

                if(dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
                Toast.makeText(ApplyLeaveScreen.this, "Failed Due to "+t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());
            }
        });



    }

    public void sendLeaveNotification(final LeaveNotificationManagers lm) throws Exception{



        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Sending Details..");
        dialog.setCancelable(false);
        dialog.show();

        LeaveNotificationAPI apiService = Util.getClient().create(LeaveNotificationAPI.class);

        Call<ArrayList<String>> call = apiService.sendLeaveNotification(lm);

        call.enqueue(new Callback<ArrayList<String>>() {
            @Override
            public void onResponse(Call<ArrayList<String>> call, Response<ArrayList<String>> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {
                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201) {


                    ApplyLeaveScreen.this.finish();


                    }else {
                        Toast.makeText(ApplyLeaveScreen.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception ex)
                {

                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }
                    ex.printStackTrace();
                }
//                callGetStartEnd();
            }

            @Override
            public void onFailure(Call<ArrayList<String>> call, Throwable t) {

                if(dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
                Toast.makeText(ApplyLeaveScreen.this, "Failed Due to "+t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());
            }
        });



    }
}
