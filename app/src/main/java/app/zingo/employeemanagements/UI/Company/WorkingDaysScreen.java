package app.zingo.employeemanagements.UI.Company;

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

import app.zingo.employeemanagements.Custom.MyTextView;
import app.zingo.employeemanagements.Model.HolidayList;
import app.zingo.employeemanagements.Model.WorkingDay;
import app.zingo.employeemanagements.R;
import app.zingo.employeemanagements.UI.Admin.CreateTaskScreen;
import app.zingo.employeemanagements.Utils.PreferenceHandler;

public class WorkingDaysScreen extends AppCompatActivity {

    private static final String TAG = WorkingDaysScreen.class.getSimpleName();
    Switch mSun,mMon,mTue,mWed,mThu,mFri,mSat;
    LinearLayout mSunLay,mMonLay,mTueLay,mWedLay,mThuLay,mFriLay,mSatLay;
    MyTextView mSunStart,mMonStart,mTueStart,mWedStart,mThuStart,mFriStart,mSatStart;
    MyTextView mSunEnd,mMonEnd,mTueEnd,mWedEnd,mThuEnd,mFriEnd,mSatEnd;


    AppCompatButton mSave;

    boolean isSun=false,isMon=false,isTue=false,isWed=false,isThu=false,isFri=false,isSat=false;

    //Firebase database
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;

    private String userId;

    ArrayList<WorkingDay> workingDaysList;
    WorkingDay workingDay;

    private String current = "";
    private String ddmmyyyy = "hhmm a";
    private Calendar cal = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
            setContentView(R.layout.activity_working_days_screen);

            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Days");

            mSun = (Switch)findViewById(R.id.sunday);
            mMon = (Switch)findViewById(R.id.monday);
            mTue = (Switch)findViewById(R.id.tuesday);
            mWed = (Switch)findViewById(R.id.wednesday);
            mThu = (Switch)findViewById(R.id.thursday);
            mFri = (Switch)findViewById(R.id.friday);
            mSat = (Switch)findViewById(R.id.saturday);

            mSunLay = (LinearLayout) findViewById(R.id.timing_sunday);
            mMonLay = (LinearLayout)findViewById(R.id.timing_monday);
            mTueLay = (LinearLayout)findViewById(R.id.timing_tuesday);
            mWedLay = (LinearLayout)findViewById(R.id.timing_wed);
            mThuLay = (LinearLayout)findViewById(R.id.timing_thur);
            mFriLay = (LinearLayout)findViewById(R.id.timing_fri);
            mSatLay = (LinearLayout)findViewById(R.id.timing_satday);

            mSunStart = (MyTextView) findViewById(R.id.start_sun);
            mMonStart = (MyTextView)findViewById(R.id.start_mon);
            mTueStart = (MyTextView)findViewById(R.id.start_tues);
            mWedStart = (MyTextView)findViewById(R.id.start_wed);
            mThuStart = (MyTextView)findViewById(R.id.start_thu);
            mFriStart = (MyTextView)findViewById(R.id.start_fri);
            mSatStart = (MyTextView)findViewById(R.id.start_sat);

            mSunEnd = (MyTextView) findViewById(R.id.end_sun);
            mMonEnd = (MyTextView)findViewById(R.id.end_mon);
            mTueEnd = (MyTextView)findViewById(R.id.end_tues);
            mWedEnd = (MyTextView)findViewById(R.id.end_wed);
            mThuEnd = (MyTextView)findViewById(R.id.end_thu);
            mFriEnd = (MyTextView)findViewById(R.id.end_fri);
            mSatEnd = (MyTextView)findViewById(R.id.end_sat);

            mSave = (AppCompatButton)findViewById(R.id.save);

            mFirebaseInstance = FirebaseDatabase.getInstance();


            // store app title to 'app_title' node
            mFirebaseInstance.getReference("table_title").setValue("WorkingDay");


            mFirebaseDatabase = FirebaseDatabase.getInstance().getReference().child("working_day"+PreferenceHandler.getInstance(WorkingDaysScreen.this).getCompanyId());

            mFirebaseDatabase.orderByChild("orgId").equalTo(PreferenceHandler.getInstance(WorkingDaysScreen.this).getCompanyId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.e(TAG, "App title updated");

                    workingDaysList = new ArrayList<>();

                    if(dataSnapshot.exists()){

                        for (DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {
                            WorkingDay message = messageSnapshot.getValue(WorkingDay.class);
                            workingDaysList.add(message);
                            userId = messageSnapshot.getKey();
                        }

                        if(workingDaysList!=null&&workingDaysList.size()!=0){

                            workingDay = workingDaysList.get(workingDaysList.size()-1);

                            mSun.setChecked(workingDay.isSunday());
                            mMon.setChecked(workingDay.isIsmonday());
                            mTue.setChecked(workingDay.isTuesday());
                            mWed.setChecked(workingDay.isWednesday());
                            mThu.setChecked(workingDay.isThursday());
                            mFri.setChecked(workingDay.isFriday());
                            mSat.setChecked(workingDay.isSaturday());

                            mSunStart.setText(workingDay.getSunStartTime());
                            mMonStart.setText(workingDay.getMonStartTime());
                            mTueStart.setText(workingDay.getTueStartTime());
                            mWedStart.setText(workingDay.getWedStartTime());
                            mThuStart.setText(workingDay.getThuStartTime());
                            mFriStart.setText(workingDay.getFriStartTime());
                            mSatStart.setText(workingDay.getSatStartTime());

                            mSunEnd.setText(workingDay.getSunEndTime());
                            mMonEnd.setText(workingDay.getMonEndTime());
                            mTueEnd.setText(workingDay.getTueEndTime());
                            mWedEnd.setText(workingDay.getWedEndTime());
                            mThuEnd.setText(workingDay.getThuEndTime());
                            mFriEnd.setText(workingDay.getFriEndTime());
                            mSatEnd.setText(workingDay.getSatEndTime());


                        }


                        //Toast.makeText(WorkingDaysScreen.this, ""+userId, Toast.LENGTH_SHORT).show();


                    }else{

                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.e(TAG, "Failed to read app title value.", error.toException());
                }
            });




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
                        wd.setSunday(isSun);
                        wd.setIsmonday(isMon);
                        wd.setTuesday(isTue);
                        wd.setWednesday(isWed);
                        wd.setThursday(isThu);
                        wd.setFriday(isFri);
                        wd.setSaturday(isSat);
                        wd.setSunStartTime(sunS);
                        wd.setSunEndTime(sunE);
                        wd.setMonStartTime(monS);
                        wd.setMonEndTime(monE);
                        wd.setTueStartTime(tueS);
                        wd.setTueEndTime(tueE);
                        wd.setWedStartTime(wedS);
                        wd.setWedEndTime(wedE);
                        wd.setThuStartTime(thuS);
                        wd.setThuEndTime(thuE);
                        wd.setFriStartTime(friS);
                        wd.setFriEndTime(friE);
                        wd.setSatStartTime(satS);
                        wd.setSatEndTime(satE);
                        ArrayList<HolidayList> hds = new ArrayList<>();
                        HolidayList hd = new HolidayList();
                        hd.setmDate("28-08-1993");
                        hd.setDetail("Razin Birthday");
                        hds.add(hd);
                        wd.setHolidayLists(hds);

                        wd.setOrgId(PreferenceHandler.getInstance(WorkingDaysScreen.this).getCompanyId());


                        if (TextUtils.isEmpty(userId)) {
                            createWorkingDay(wd);
                        } else {
                            updateWorking(wd);
                        }

                    }


                }
            });


        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void createWorkingDay(final WorkingDay wds) {
        // TODO
        // In real apps this userId should be fetched
        // by implementing firebase auth

        if (TextUtils.isEmpty(userId)) {
            userId = mFirebaseDatabase.push().getKey();
        }
        //WorkingDay wd = new WorkingDay(suns,mons,tues,weds,thus,fris,sats,PreferenceHandler.getInstance(WorkingDaysScreen.this).getCompanyId());

        mFirebaseDatabase.child(userId).setValue(wds);

        addUserChangeListener();
    }

    private void addUserChangeListener() {
        // User data change listener
        mFirebaseDatabase.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                WorkingDay user = dataSnapshot.getValue(WorkingDay.class);

                // Check for null
                if (user == null) {
                    Log.e(TAG, "User data is null!");
                    return;
                }

                Log.e(TAG, "User data is changed!" + user.isSunday + ", " + user.isFriday);
                Toast.makeText(WorkingDaysScreen.this, "Added Successfully", Toast.LENGTH_SHORT).show();

               // Toast.makeText(WorkingDaysScreen.this, "added " + user.isSunday + ", " + user.isFriday, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(TAG, "Failed to read user", error.toException());
            }
        });
    }

    private void updateWorking(final WorkingDay wds) {

        mFirebaseDatabase.child(userId).child("isSunday").setValue(wds.isSunday());
        mFirebaseDatabase.child(userId).child("ismonday").setValue(wds.isIsmonday());
        mFirebaseDatabase.child(userId).child("isTuesday").setValue(wds.isTuesday());
        mFirebaseDatabase.child(userId).child("isWednesday").setValue(wds.isWednesday());
        mFirebaseDatabase.child(userId).child("isThursday").setValue(wds.isThursday());
        mFirebaseDatabase.child(userId).child("isFriday").setValue(wds.isFriday());
        mFirebaseDatabase.child(userId).child("isSaturday").setValue(wds.isSaturday());

        mFirebaseDatabase.child(userId).child("sunStartTime").setValue(wds.getSunStartTime());
        mFirebaseDatabase.child(userId).child("sunEndTime").setValue(wds.getSunEndTime());
        mFirebaseDatabase.child(userId).child("monStartTime").setValue(wds.getMonStartTime());
        mFirebaseDatabase.child(userId).child("monEndTime").setValue(wds.getMonEndTime());
        mFirebaseDatabase.child(userId).child("tueStartTime").setValue(wds.getTueStartTime());
        mFirebaseDatabase.child(userId).child("tueEndTime").setValue(wds.getTueEndTime());
        mFirebaseDatabase.child(userId).child("wedStartTime").setValue(wds.getWedStartTime());

        mFirebaseDatabase.child(userId).child("wedEndTime").setValue(wds.getWedEndTime());
        mFirebaseDatabase.child(userId).child("thuStartTime").setValue(wds.getThuStartTime());
        mFirebaseDatabase.child(userId).child("thuEndTime").setValue(wds.getThuEndTime());
        mFirebaseDatabase.child(userId).child("friStartTime").setValue(wds.getFriStartTime());
        mFirebaseDatabase.child(userId).child("friEndTime").setValue(wds.getFriEndTime());
        mFirebaseDatabase.child(userId).child("satStartTime").setValue(wds.getSatStartTime());
        mFirebaseDatabase.child(userId).child("satEndTime").setValue(wds.getSatEndTime());
        mFirebaseDatabase.child(userId).child("orgId").setValue(wds.getOrgId());
        mFirebaseDatabase.child(userId).child("holidayLists").setValue(wds.getHolidayLists());

        Toast.makeText(WorkingDaysScreen.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
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
            }else if(end==null||end.isEmpty()){
                return false;
            }else{
                return true;
            }
        }else{
            return true;
        }
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
