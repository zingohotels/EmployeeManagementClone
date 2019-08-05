package app.zingo.employeemanagements.UI.Common;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import java.util.ArrayList;
import java.util.Calendar;

import app.zingo.employeemanagements.Adapter.BreakAdapter;
import app.zingo.employeemanagements.Custom.MyEditText;
import app.zingo.employeemanagements.Model.OrganizationBreakTimes;
import app.zingo.employeemanagements.Utils.PreferenceHandler;
import app.zingo.employeemanagements.Utils.ThreadExecuter;
import app.zingo.employeemanagements.Utils.Util;
import app.zingo.employeemanagements.WebApi.OrganizationBreakTimesAPI;
import app.zingo.employeemanagements.base.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BreakTimeListScreen extends AppCompatActivity {

    LinearLayout mNoBreak;
    RecyclerView mBreakList;
    FloatingActionButton mAddBreak;
    ImageView mLoader;

    int orgId;

    String startT="",endT="";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{

            setContentView(R.layout.activity_break_time_list_screen);

            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Break Timings");

            mNoBreak= findViewById(R.id.noBranchFound);
            mBreakList = findViewById(R.id.break_list_data);
            mAddBreak = findViewById(R.id.add_break_float);
            mLoader = findViewById(R.id.spin_loader);

            Glide.with(this)
                    .load(R.drawable.spin)
                    .into(new GlideDrawableImageViewTarget(mLoader));


            // getBranches(PreferenceHandler.getInstance(BreakTimeListScreen.this).getCompanyId());

            Bundle bun = getIntent().getExtras();

            if(bun!=null){

                orgId = bun.getInt("OrganizationId",0);
            }

            if(orgId!=0){

                getBreaks(orgId);
            }else{
                getBreaks(PreferenceHandler.getInstance(BreakTimeListScreen.this).getCompanyId());
            }

            mAddBreak.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(orgId!=0){

                        breakAlert(orgId);
                    }else{
                        breakAlert(PreferenceHandler.getInstance(BreakTimeListScreen.this).getCompanyId());
                    }

                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void getBreaks(final int id) {



        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {

                final OrganizationBreakTimesAPI orgApi = Util.getClient().create(OrganizationBreakTimesAPI.class);
                Call<ArrayList<OrganizationBreakTimes>> getProf = orgApi.getBreaksByOrgId(id);
                //Call<ArrayList<Blogs>> getBlog = blogApi.getBlogs();

                getProf.enqueue(new Callback<ArrayList<OrganizationBreakTimes>>() {

                    @Override
                    public void onResponse(Call<ArrayList<OrganizationBreakTimes>> call, Response<ArrayList<OrganizationBreakTimes>> response) {



                        if (response.code() == 200||response.code() == 201||response.code() == 204)
                        {
                            mLoader.setVisibility(View.GONE);

                            ArrayList<OrganizationBreakTimes> holidayLists = response.body();

                            if(holidayLists!=null&&holidayLists.size()!=0){

                                mLoader.setVisibility(View.GONE);
                                mNoBreak.setVisibility(View.GONE);

                                BreakAdapter adapter = new BreakAdapter(BreakTimeListScreen.this,holidayLists);
                                mBreakList.setAdapter(adapter);


                            }


                        }else{

                            mLoader.setVisibility(View.GONE);

                            Toast.makeText(BreakTimeListScreen.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<OrganizationBreakTimes>> call, Throwable t) {

                        mLoader.setVisibility(View.GONE);


                        Toast.makeText(BreakTimeListScreen.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                    }
                });

            }

        });
    }

    private void breakAlert(final int id){

        AlertDialog.Builder builder = new AlertDialog.Builder(BreakTimeListScreen.this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View views = inflater.inflate(R.layout.break_time_alert, null);

        builder.setView(views);
        final Button mSave = views.findViewById(R.id.save);
        final MyEditText start = views.findViewById(R.id.break_start_time);
        final MyEditText end = views.findViewById(R.id.break_end_time);
        final TextInputEditText mName = views.findViewById(R.id.break_name);


        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);


        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openTimePicker(start,"Start");

            }
        });

        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openTimePicker(end,"End");

            }
        });




        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String name = mName.getText().toString();
                String stratTime = start.getText().toString();
                String endTime = end.getText().toString();

                if(name.isEmpty()){

                    Toast.makeText(BreakTimeListScreen.this, "Please enter Break Name", Toast.LENGTH_SHORT).show();

                }else if (stratTime.isEmpty()||startT.isEmpty()){

                    Toast.makeText(BreakTimeListScreen.this, "Please enter Start Time", Toast.LENGTH_SHORT).show();
                }else if (endTime.isEmpty()||endT.isEmpty()){

                    Toast.makeText(BreakTimeListScreen.this, "Please enter End Time", Toast.LENGTH_SHORT).show();
                }else{

                    OrganizationBreakTimes breakTimes = new OrganizationBreakTimes();
                    breakTimes.setBreakName(name);
                    breakTimes.setBreakStartTime(stratTime);
                    breakTimes.setBreakEndTime(endTime);

                    if(orgId!=0){

                        breakTimes.setOrganizationId(orgId);
                    }else{
                        breakTimes.setOrganizationId(PreferenceHandler.getInstance(BreakTimeListScreen.this).getCompanyId());
                    }

                    addBreaks(breakTimes,dialog);
                }


            }
        });

    }

    public void addBreaks(final OrganizationBreakTimes breakTimes, final AlertDialog dialogs) {


        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Saving Details..");
        dialog.setCancelable(false);
        dialog.show();

        OrganizationBreakTimesAPI apiService = Util.getClient().create(OrganizationBreakTimesAPI.class);

        Call<OrganizationBreakTimes> call = apiService.addBreaks(breakTimes);

        call.enqueue(new Callback<OrganizationBreakTimes>() {
            @Override
            public void onResponse(Call<OrganizationBreakTimes> call, Response<OrganizationBreakTimes> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {
                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201 || statusCode == 204) {

                        OrganizationBreakTimes s = response.body();

                        if(s!=null){

                            Toast.makeText(BreakTimeListScreen.this, "Saved Successfully ", Toast.LENGTH_SHORT).show();

                            dialogs.dismiss();

                            if(orgId!=0){

                                getBreaks(orgId);
                            }else{
                                getBreaks(PreferenceHandler.getInstance(BreakTimeListScreen.this).getCompanyId());
                            }


                        }




                    }else {
                        Toast.makeText(BreakTimeListScreen.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
            public void onFailure(Call<OrganizationBreakTimes> call, Throwable t) {

                if(dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
                Toast.makeText( BreakTimeListScreen.this , "Failed due to Bad Internet Connection" , Toast.LENGTH_SHORT ).show( );
                Log.e("TAG", t.toString());
            }
        });



    }


    public void openTimePicker(final MyEditText tv, final String type){

        final Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(BreakTimeListScreen.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                //tv.setText( selectedHour + ":" + selectedMinute);

                try {

                    boolean isPM =(hourOfDay >= 12);

                    String cin = ""+String.format("%02d:%02d %s", (hourOfDay == 12 || hourOfDay == 0) ? 12 : hourOfDay % 12, minute, isPM ? "PM" : "AM");


                    if(type!=null&&type.equalsIgnoreCase("Start")){

                        startT = ""+String.format("%02d:%02d", hourOfDay, minute);

                    }else if(type!=null&&type.equalsIgnoreCase("End")){

                        endT = ""+String.format("%02d:%02d", hourOfDay, minute);

                    }
                    tv.setText( cin);
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        }, hour, minute, false);//Yes 24 hour time

        mTimePicker.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id)
        {
            case android.R.id.home:

                BreakTimeListScreen.this.finish();
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}
