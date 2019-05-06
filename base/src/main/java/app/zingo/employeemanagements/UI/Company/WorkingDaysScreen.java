package app.zingo.employeemanagements.UI.Company;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import app.zingo.employeemanagements.Adapter.DepartmentAdapter;
import app.zingo.employeemanagements.Custom.MyTextView;
import app.zingo.employeemanagements.Database.DBHelper;
import app.zingo.employeemanagements.Model.Departments;
import app.zingo.employeemanagements.Model.Organization;
import app.zingo.employeemanagements.Model.WorkingDay;
import app.zingo.employeemanagements.base.R;
import app.zingo.employeemanagements.UI.Admin.CreateTaskScreen;
import app.zingo.employeemanagements.UI.Common.NotificationShowActivity;
import app.zingo.employeemanagements.UI.NewAdminDesigns.DepartmentLilstScreen;
import app.zingo.employeemanagements.Utils.PreferenceHandler;
import app.zingo.employeemanagements.Utils.ThreadExecuter;
import app.zingo.employeemanagements.Utils.Util;
import app.zingo.employeemanagements.WebApi.OrganizationApi;
import app.zingo.employeemanagements.WebApi.OrganizationTimingsAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WorkingDaysScreen extends AppCompatActivity {

    private static final String TAG = WorkingDaysScreen.class.getSimpleName();
    Switch mSun,mMon,mTue,mWed,mThu,mFri,mSat;
    LinearLayout mSunLay,mMonLay,mTueLay,mWedLay,mThuLay,mFriLay,mSatLay;
    MyTextView mSunStart,mMonStart,mTueStart,mWedStart,mThuStart,mFriStart,mSatStart;
    MyTextView mSunEnd,mMonEnd,mTueEnd,mWedEnd,mThuEnd,mFriEnd,mSatEnd;


    AppCompatButton mSave;

    boolean isSun=false,isMon=false,isTue=false,isWed=false,isThu=false,isFri=false,isSat=false;

    WorkingDay updateWorkingDay;
    DBHelper mydb;

    int orgId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
            setContentView(R.layout.activity_working_days_screen);

            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Days");

            Bundle bun = getIntent().getExtras();

            if(bun!=null){

                orgId = bun.getInt("OrganizationId",0);
            }

            mSun = findViewById(R.id.sunday);
            mMon = findViewById(R.id.monday);
            mTue = findViewById(R.id.tuesday);
            mWed = findViewById(R.id.wednesday);
            mThu = findViewById(R.id.thursday);
            mFri = findViewById(R.id.friday);
            mSat = findViewById(R.id.saturday);

            mSunLay = findViewById(R.id.timing_sunday);
            mMonLay = findViewById(R.id.timing_monday);
            mTueLay = findViewById(R.id.timing_tuesday);
            mWedLay = findViewById(R.id.timing_wed);
            mThuLay = findViewById(R.id.timing_thur);
            mFriLay = findViewById(R.id.timing_fri);
            mSatLay = findViewById(R.id.timing_satday);

            mSunStart = findViewById(R.id.start_sun);
            mMonStart = findViewById(R.id.start_mon);
            mTueStart = findViewById(R.id.start_tues);
            mWedStart = findViewById(R.id.start_wed);
            mThuStart = findViewById(R.id.start_thu);
            mFriStart = findViewById(R.id.start_fri);
            mSatStart = findViewById(R.id.start_sat);

            mSunEnd = findViewById(R.id.end_sun);
            mMonEnd = findViewById(R.id.end_mon);
            mTueEnd = findViewById(R.id.end_tues);
            mWedEnd = findViewById(R.id.end_wed);
            mThuEnd = findViewById(R.id.end_thu);
            mFriEnd = findViewById(R.id.end_fri);
            mSatEnd = findViewById(R.id.end_sat);

            mSave = findViewById(R.id.save);

            mydb = new DBHelper(WorkingDaysScreen.this);




            mSun.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                    if(isChecked){

                        mSunLay.setVisibility(View.VISIBLE);
                        isSun =true;
                    }else{
                        mSunLay.setVisibility(View.GONE);
                        isSun =false;
                    }
                }
            });

            mMon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                    if(isChecked){

                        mMonLay.setVisibility(View.VISIBLE);
                        isMon =true;
                    }else{
                        mMonLay.setVisibility(View.GONE);
                        isMon =false;
                    }
                }
            });

            mTue.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                    if(isChecked){

                        mTueLay.setVisibility(View.VISIBLE);
                        isTue =true;
                    }else{
                        mTueLay.setVisibility(View.GONE);
                        isTue =false;
                    }
                }
            });

            mWed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                    if(isChecked){

                        mWedLay.setVisibility(View.VISIBLE);
                        isWed =true;
                    }else{
                        mWedLay.setVisibility(View.GONE);
                        isWed =false;
                    }
                }
            });

            mThu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                    if(isChecked){

                        mThuLay.setVisibility(View.VISIBLE);
                        isThu =true;
                    }else{
                        mThuLay.setVisibility(View.GONE);
                        isThu =false;
                    }
                }
            });

            mFri.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                    if(isChecked){

                        mFriLay.setVisibility(View.VISIBLE);
                        isFri =true;
                    }else{
                        mFriLay.setVisibility(View.GONE);
                        isFri =false;
                    }
                }
            });

            mSat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                    if(isChecked){

                        mSatLay.setVisibility(View.VISIBLE);
                        isSat = true;
                    }else{
                        mSatLay.setVisibility(View.GONE);
                        isSat = false;
                    }
                }
            });

            mSunStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    openTimePicker(mSunStart);
                }
            });

            mSunEnd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    openTimePicker(mSunEnd);
                }
            });

            mMonStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    openTimePicker(mMonStart);
                }
            });

            mMonEnd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    openTimePicker(mMonEnd);
                }
            });

            mTueStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    openTimePicker(mTueStart);
                }
            });

            mTueEnd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    openTimePicker(mTueEnd);
                }
            });

            mWedStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    openTimePicker(mWedStart);
                }
            });

            mWedEnd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    openTimePicker(mWedEnd);
                }
            });

            mThuStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    openTimePicker(mThuStart);
                }
            });

            mThuEnd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    openTimePicker(mThuEnd);
                }
            });

            mFriStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    openTimePicker(mFriStart);
                }
            });

            mFriEnd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    openTimePicker(mFriEnd);
                }
            });

            mSatStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    openTimePicker(mSatStart);
                }
            });

            mSatEnd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    openTimePicker(mSatEnd);
                }
            });

            if(orgId!=0){

                getOrganizationTimings(orgId);
            }else{
                getOrganizationTimings(PreferenceHandler.getInstance(WorkingDaysScreen.this).getCompanyId());
            }




            mSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String sunS = mSunStart.getText().toString();
                    String monS = mMonStart.getText().toString();
                    String tueS = mTueStart.getText().toString();
                    String wedS = mWedStart.getText().toString();
                    String thuS = mThuStart.getText().toString();
                    String friS = mFriStart.getText().toString();
                    String satS = mSatStart.getText().toString();

                    String sunE = mSunEnd.getText().toString();
                    String monE = mMonEnd.getText().toString();
                    String tueE = mTueEnd.getText().toString();
                    String wedE = mWedEnd.getText().toString();
                    String thuE = mThuEnd.getText().toString();
                    String friE = mFriEnd.getText().toString();
                    String satE = mSatEnd.getText().toString();


                    if(!validate(mSun,sunS,sunE)){

                        Toast.makeText(WorkingDaysScreen.this, "Field should not be empty", Toast.LENGTH_SHORT).show();

                    }else  if(!validate(mMon,monS,monE)){

                        Toast.makeText(WorkingDaysScreen.this, "Field should not be empty", Toast.LENGTH_SHORT).show();

                    }else  if(!validate(mTue,tueS,tueE)){

                        Toast.makeText(WorkingDaysScreen.this, "Field should not be empty", Toast.LENGTH_SHORT).show();

                    }else  if(!validate(mWed,wedS,wedE)){

                        Toast.makeText(WorkingDaysScreen.this, "Field should not be empty", Toast.LENGTH_SHORT).show();

                    }else  if(!validate(mThu,thuS,thuE)){

                        Toast.makeText(WorkingDaysScreen.this, "Field should not be empty", Toast.LENGTH_SHORT).show();

                    }else  if(!validate(mFri,friS,friE)){

                        Toast.makeText(WorkingDaysScreen.this, "Field should not be empty", Toast.LENGTH_SHORT).show();

                    }else  if(!validate(mSat,satS,satE)){

                        Toast.makeText(WorkingDaysScreen.this, "Field should not be empty", Toast.LENGTH_SHORT).show();

                    }else{

                        WorkingDay wd = new WorkingDay();

                        if(updateWorkingDay!=null){

                            wd = updateWorkingDay;
                        }else{

                            wd = new WorkingDay();
                        }


                        wd.setSuday(isSun);
                        wd.setMonday(isMon);
                        wd.setiSTuesday(isTue);
                        wd.setWednesday(isWed);
                        wd.setThursday(isThu);
                        wd.setFriday(isFri);
                        wd.setSaturday(isSat);
                        wd.setSundayCheckInTime(sunS);
                        wd.setSundayCheckOutTime(sunE);
                        wd.setMondayCheckInTime(monS);
                        wd.setMondayCheckOutTime(monE);
                        wd.setTuesdayCheckInTime(tueS);
                        wd.setTuesdayCheckOutTime(tueE);
                        wd.setWednesdayCheckInTime(wedS);
                        wd.setWednesdayCheckOutTime(wedE);
                        wd.setThursdayCheckInTime(thuS);
                        wd.setThursdayCheckOutTime(thuE);
                        wd.setFridayCheckInTime(friS);
                        wd.setFridayCheckOutTime(friE);
                        wd.setSaturdayCheckInTime(satS);
                        wd.setSaturdayCheckOutTime(satE);

                        if(orgId!=0){

                            wd.setOrganizationId(orgId);
                        }else{
                            wd.setOrganizationId(PreferenceHandler.getInstance(WorkingDaysScreen.this).getCompanyId());
                        }



                        if(updateWorkingDay!=null){

                            updateOrganizationTimings(wd);
                        }else{

                            addOrganizationTimings(wd);
                        }



                    }


                }
            });


        }catch (Exception e){
            e.printStackTrace();
        }

    }



    public void openTimePicker(final MyTextView tv){

        final Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(WorkingDaysScreen.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                //tv.setText( selectedHour + ":" + selectedMinute);

                try {



                    boolean isPM =(hourOfDay >= 12);

                    String cin = ""+String.format("%02d:%02d %s", (hourOfDay == 12 || hourOfDay == 0) ? 12 : hourOfDay % 12, minute, isPM ? "PM" : "AM");
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


    public boolean validate(final Switch day,final String start,final String end){

        if(day.isChecked()){
            if(start==null||start.isEmpty()){
                return false;
            }else return end != null && !end.isEmpty();
        }else{
            return true;
        }
    }


    public void getOrganizationTimings(final int id) {

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Get Details..");
        dialog.setCancelable(false);
        dialog.show();

        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {

                final OrganizationTimingsAPI orgtimeapi = Util.getClient().create(OrganizationTimingsAPI.class);
                Call<ArrayList<WorkingDay>> getProf = orgtimeapi.getOrganizationTimingByOrgId(id);
                //Call<ArrayList<Blogs>> getBlog = blogApi.getBlogs();

                getProf.enqueue(new Callback<ArrayList<WorkingDay>>() {

                    @Override
                    public void onResponse(Call<ArrayList<WorkingDay>> call, Response<ArrayList<WorkingDay>> response) {

                        if(dialog!=null&&dialog.isShowing()){

                            dialog.dismiss();
                        }

                        if (response.code() == 200||response.code() == 201||response.code() == 204)
                        {

                            ArrayList<WorkingDay> workingDayArrayList = response.body();

                            if(workingDayArrayList!=null&&workingDayArrayList.size()!=0){

                                WorkingDay displayData = workingDayArrayList.get(workingDayArrayList.size()-1);

                                updateWorkingDay = displayData;


                                if(displayData!=null){

                                    setData(displayData);

                                }
                            }


                        }else{

                            Toast.makeText(WorkingDaysScreen.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<WorkingDay>> call, Throwable t) {

                        if(dialog!=null&&dialog.isShowing()){

                            dialog.dismiss();
                        }


                        Toast.makeText(WorkingDaysScreen.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                    }
                });

            }

        });
    }


    public void addOrganizationTimings(final WorkingDay workingDay) {

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Saving Details..");
        dialog.setCancelable(false);
        dialog.show();

        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {

                final OrganizationTimingsAPI orgtimeapi = Util.getClient().create(OrganizationTimingsAPI.class);
                Call<WorkingDay> getProf = orgtimeapi.addOrganizationTimings(workingDay);
                //Call<ArrayList<Blogs>> getBlog = blogApi.getBlogs();

                getProf.enqueue(new Callback<WorkingDay>() {

                    @Override
                    public void onResponse(Call<WorkingDay> call, Response<WorkingDay> response) {

                        if(dialog!=null&&dialog.isShowing()){

                            dialog.dismiss();
                        }

                        if (response.code() == 200||response.code() == 201||response.code() == 204)
                        {

                            WorkingDay workingDayArrayList = response.body();

                            if(workingDayArrayList!=null){


                                Toast.makeText(WorkingDaysScreen.this, "Timing saved success fully", Toast.LENGTH_SHORT).show();


                            }


                        }else{

                            Toast.makeText(WorkingDaysScreen.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onFailure(Call<WorkingDay> call, Throwable t) {

                        if(dialog!=null&&dialog.isShowing()){

                            dialog.dismiss();
                        }


                        Toast.makeText(WorkingDaysScreen.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                    }
                });

            }

        });
    }


    public void updateOrganizationTimings(final WorkingDay workingDay) {

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Saving Details..");
        dialog.setCancelable(false);
        dialog.show();

        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {

                final OrganizationTimingsAPI orgtimeapi = Util.getClient().create(OrganizationTimingsAPI.class);
                Call<WorkingDay> getProf = orgtimeapi.updateOrganizationTimings(workingDay.getOrganizationTimingId(),workingDay);
                //Call<ArrayList<Blogs>> getBlog = blogApi.getBlogs();

                getProf.enqueue(new Callback<WorkingDay>() {

                    @Override
                    public void onResponse(Call<WorkingDay> call, Response<WorkingDay> response) {

                        if(dialog!=null&&dialog.isShowing()){

                            dialog.dismiss();
                        }

                        if (response.code() == 200||response.code() == 201||response.code() == 204)
                        {

                            Toast.makeText(WorkingDaysScreen.this, "Organization timing saved success fully", Toast.LENGTH_SHORT).show();

                        }else{

                            Toast.makeText(WorkingDaysScreen.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onFailure(Call<WorkingDay> call, Throwable t) {

                        if(dialog!=null&&dialog.isShowing()){

                            dialog.dismiss();
                        }


                        Toast.makeText(WorkingDaysScreen.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                    }
                });

            }

        });
    }


    public void setData(WorkingDay workingDay){


        mSun.setChecked(workingDay.isSuday());
        mMon.setChecked(workingDay.isMonday());
        mTue.setChecked(workingDay.isiSTuesday());
        mWed.setChecked(workingDay.isWednesday());
        mThu.setChecked(workingDay.isThursday());
        mFri.setChecked(workingDay.isFriday());
        mSat.setChecked(workingDay.isSaturday());

        mSunStart.setText(workingDay.getSundayCheckInTime());
        mMonStart.setText(workingDay.getMondayCheckInTime());
        mTueStart.setText(workingDay.getTuesdayCheckInTime());
        mWedStart.setText(workingDay.getWednesdayCheckInTime());
        mThuStart.setText(workingDay.getThursdayCheckInTime());
        mFriStart.setText(workingDay.getFridayCheckInTime());
        mSatStart.setText(workingDay.getSaturdayCheckInTime());

        mSunEnd.setText(workingDay.getSundayCheckOutTime());
        mMonEnd.setText(workingDay.getMondayCheckOutTime());
        mTueEnd.setText(workingDay.getTuesdayCheckOutTime());
        mWedEnd.setText(workingDay.getWednesdayCheckOutTime());
        mThuEnd.setText(workingDay.getThursdayCheckOutTime());
        mFriEnd.setText(workingDay.getFridayCheckOutTime());
        mSatEnd.setText(workingDay.getSaturdayCheckOutTime());


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id)
        {
            case android.R.id.home:

                WorkingDaysScreen.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
