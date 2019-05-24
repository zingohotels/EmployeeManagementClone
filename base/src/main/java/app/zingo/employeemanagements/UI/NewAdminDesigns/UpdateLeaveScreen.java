package app.zingo.employeemanagements.UI.NewAdminDesigns;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import app.zingo.employeemanagements.Adapter.LeaveTakenEmployeeAdapter;
import app.zingo.employeemanagements.Model.LeaveNotificationManagers;
import app.zingo.employeemanagements.Model.Leaves;
import app.zingo.employeemanagements.base.R;
import app.zingo.employeemanagements.UI.Employee.ApplyLeaveScreen;
import app.zingo.employeemanagements.UI.Landing.SplashScreen;
import app.zingo.employeemanagements.Utils.PreferenceHandler;
import app.zingo.employeemanagements.Utils.ThreadExecuter;
import app.zingo.employeemanagements.Utils.Util;
import app.zingo.employeemanagements.WebApi.LeaveAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateLeaveScreen extends AppCompatActivity {

    TextInputEditText mFrom,mTo;
    Spinner mLeaveType,mLeaveStatus;
    EditText mLeaveComment;
    AppCompatButton mApply;

    Leaves leavess;
    String[] leaveTypes,leaveStauses;
    int leaveId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{

            setContentView(R.layout.activity_update_leave_screen);

            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Leave Details");

            mLeaveType = findViewById(R.id.leave_type_spinner);
            mLeaveStatus = findViewById(R.id.leave_status_spinner);

            mFrom = findViewById(R.id.from_date);
            mTo = findViewById(R.id.to_date);
            mLeaveComment = findViewById(R.id.leave_comment);
            mApply = findViewById(R.id.apply_leave);

             leaveTypes = getResources().getStringArray(R.array.leave_type);
             leaveStauses = getResources().getStringArray(R.array.leave_status);


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

            Bundle bundle = getIntent().getExtras();

            if(bundle!=null){
                leavess = (Leaves)bundle.getSerializable("Leaves");
                leaveId = bundle.getInt("LeaveId");
            }

            if(leavess!=null){
                setData(leavess);
            }else if(leaveId!=0){
                getLeaveDetails(leaveId);
            }



        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void setData(Leaves dto){

        String froms = dto.getFromDate();
        String tos = dto.getToDate();

        if(froms.contains("T")){

            String dojs[] = froms.split("T");

            try {
                Date afromDate = new SimpleDateFormat("yyyy-MM-dd").parse(dojs[0]);
                froms = new SimpleDateFormat("MMM dd,yyyy").format(afromDate);
                mFrom.setText(""+froms);

            } catch (ParseException e) {
                e.printStackTrace();
            }


        }

        if(tos.contains("T")){

            String dojs[] = tos.split("T");

            try {
                Date afromDate = new SimpleDateFormat("yyyy-MM-dd").parse(dojs[0]);
                tos = new SimpleDateFormat("MMM dd,yyyy").format(afromDate);
                mTo.setText(""+tos);
            } catch (ParseException e) {
                e.printStackTrace();
            }


        }


        if(dto.getLeaveType().equalsIgnoreCase("Paid")){
            mLeaveType.setSelection(0);
        }else{
            mLeaveType.setSelection(1);
        }

        if(dto.getStatus().equalsIgnoreCase("Pending")){
            mLeaveStatus.setSelection(0);
        }else  if(dto.getStatus().equalsIgnoreCase("Approved")){
            mLeaveStatus.setSelection(1);
        }else{
            mLeaveStatus.setSelection(2);
        }



        mLeaveComment.setText(""+dto.getLeaveComment());

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
        String leaveType= mLeaveType.getSelectedItem().toString();
        String leaveStatus= mLeaveStatus.getSelectedItem().toString();


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
                Leaves leaves = leavess;
                //leaves.setLeaveType(leaveType);
                leaves.setLeaveComment(leaveComment);

                SimpleDateFormat df = new SimpleDateFormat("MMM dd,yyyy");
                SimpleDateFormat dfs = new SimpleDateFormat("MM/dd/yyyy");

                Date fromDate = df.parse(from);
                Date toDate = df.parse(to);
                leaves.setFromDate(dfs.format(fromDate));
                leaves.setToDate(dfs.format(toDate));
                leaves.setStatus(leaveStatus);
                leaves.setLeaveType(leaveType);
                int diffs = (int)dateCal(from,to);
                leaves.setNoOfDays(diffs);
                leaves.setApprovedDate(dfs.format(new Date()));

                try {
                    updateLeaves(leaves);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }catch (Exception e){
                e.printStackTrace();
            }


        }
    }
    public void updateLeaves(final Leaves leaves) {



        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Saving Details..");
        dialog.setCancelable(false);
        dialog.show();

        LeaveAPI apiService = Util.getClient().create(LeaveAPI.class);

        Call<Leaves> call = apiService.updateLeaves(leaves.getLeaveId(),leaves);

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
                    if (statusCode == 200 || statusCode == 201|| statusCode == 204) {


                        Toast.makeText(UpdateLeaveScreen.this, "Update leave succesfully", Toast.LENGTH_SHORT).show();



                    }else {
                        Toast.makeText(UpdateLeaveScreen.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(UpdateLeaveScreen.this, "Failed Due to "+t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());
            }
        });



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

    private void getLeaveDetails(final int leaveId){


        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading Details..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {
                LeaveAPI apiService = Util.getClient().create(LeaveAPI.class);
                Call<Leaves> call = apiService.getLeaveById(leaveId);

                call.enqueue(new Callback<Leaves>() {
                    @Override
                    public void onResponse(Call<Leaves> call, Response<Leaves> response) {
                        int statusCode = response.code();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {


                            if (progressDialog!=null)
                                progressDialog.dismiss();
                            leavess = response.body();

                            if(leavess!=null){

                                setData(leavess);

                            }else{
                                Toast.makeText(UpdateLeaveScreen.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }

                        }else {

                            if (progressDialog!=null)
                                progressDialog.dismiss();

                            Toast.makeText(UpdateLeaveScreen.this, "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Leaves> call, Throwable t) {
                        // Log error here since request failed
                        if (progressDialog!=null)
                            progressDialog.dismiss();
                        Log.e("TAG", t.toString());
                    }
                });
            }


        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id){

            case android.R.id.home:

                if(leaveId!=0){

                    Intent splash = new Intent(UpdateLeaveScreen.this,AdminHomeFragment.class);
                    startActivity(splash);
                    UpdateLeaveScreen.this.finish();

                }else{

                    UpdateLeaveScreen.this.finish();
                }

        }

        return super.onOptionsItemSelected(item);

    }
}
