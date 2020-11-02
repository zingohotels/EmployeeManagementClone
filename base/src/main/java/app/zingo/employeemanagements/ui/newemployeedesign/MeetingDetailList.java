package app.zingo.employeemanagements.ui.newemployeedesign;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import app.zingo.employeemanagements.adapter.MeetingDetailAdapter;
import app.zingo.employeemanagements.model.Meetings;
import app.zingo.employeemanagements.utils.PreferenceHandler;
import app.zingo.employeemanagements.utils.ProgressBarUtil;
import app.zingo.employeemanagements.utils.Util;
import app.zingo.employeemanagements.WebApi.MeetingsAPI;
import app.zingo.employeemanagements.base.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MeetingDetailList extends AppCompatActivity {

    FloatingActionButton floatingActionButton,refresh;
    View layout;
    private MeetingDetailAdapter mAdapter;
    RecyclerView mMeetingList;

    //CalendarDay mCalendarDay;
    private int mEmployeeId;
    Toolbar mToolbar;

    ArrayList< Meetings > employeeMeetings;

    ProgressBarUtil progressBarUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        try{

            setContentView(R.layout.activity_meeting_detail_list);
            setupToolbar();
            progressBarUtil = new ProgressBarUtil ( MeetingDetailList.this );

            mMeetingList = findViewById(R.id.meetingList);
            mMeetingList.setLayoutManager(new LinearLayoutManager(this));


            this.floatingActionButton = findViewById(R.id.addMeetingtOption);
            this.refresh = findViewById(R.id.refresh);

            Bundle bundle = getIntent().getExtras();

            if(bundle!=null){

                mEmployeeId = bundle.getInt("ProfileId");


            }



            floatingActionButton.setOnClickListener( view -> {

                Intent createTask = new Intent( MeetingDetailList.this, MeetingAddWithSignScreen.class);
                createTask.putExtra("EmployeeId", mEmployeeId);
                startActivity(createTask);

            } );

            refresh.setOnClickListener( view -> {

                Intent createTask = new Intent( MeetingDetailList.this, MeetingDetailList.class);
                createTask.putExtra("ProfileId", mEmployeeId);
                startActivity(createTask);
                MeetingDetailList.this.finish();

            } );

            if(mEmployeeId!=0){
                getMeetings(mEmployeeId);
            }else{

                getMeetings(PreferenceHandler.getInstance( MeetingDetailList.this).getUserId());

            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void setupToolbar() {
        this.mToolbar = findViewById(R.id.app_bar);
        setSupportActionBar(this.mToolbar);
        Objects.requireNonNull ( getSupportActionBar ( ) ).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Meetings");
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_calendar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            MeetingDetailList.this.finish();

        } else if (id == R.id.action_calendar) {
            openDatePicker();

        }
        return super.onOptionsItemSelected(item);
    }


    private void getMeetings(final int employeeId){


        progressBarUtil.showProgress ( "Loading Meetings..." );


        MeetingsAPI apiService = Util.getClient().create(MeetingsAPI.class);
        Call<ArrayList< Meetings >> call = apiService.getMeetingsByEmployeeId(employeeId);

        call.enqueue(new Callback<ArrayList< Meetings >>() {
            @Override
            public void onResponse( @NonNull Call<ArrayList< Meetings >> call, @NonNull Response<ArrayList< Meetings >> response) {
                int statusCode = response.code();
                if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {


                    if(progressBarUtil!=null){
                        progressBarUtil.hideProgress ();
                    }

                    ArrayList< Meetings > list = response.body();
                    employeeMeetings = response.body();




                    if (list !=null && list.size()!=0) {


                        mAdapter = new MeetingDetailAdapter( MeetingDetailList.this,list);
                        mMeetingList.setAdapter(mAdapter);



                    }else{

                        Toast.makeText( MeetingDetailList.this, "No Meetings given for this employee ", Toast.LENGTH_SHORT).show();

                    }

                }else {

                    if(progressBarUtil!=null){
                        progressBarUtil.hideProgress ();
                    }

                    Toast.makeText( MeetingDetailList.this, "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure( @NonNull Call<ArrayList< Meetings >> call, @NonNull Throwable t) {
                // Log error here since request failed
                if(progressBarUtil!=null){
                    progressBarUtil.hideProgress ();
                }
                Log.e("TAG", t.toString());
            }
        });
    }

    public void openDatePicker() {
        // Get Current Date

        final Calendar c = Calendar.getInstance();
        int mYear  = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay   = c.get(Calendar.DAY_OF_MONTH);
        //launch datepicker modal
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                ( view , year , monthOfYear , dayOfMonth ) -> {
                    try {
                        Log.d("Date", "DATE SELECTED "+dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year,monthOfYear,dayOfMonth);


                        String date1 = (monthOfYear + 1)  + "/" + (dayOfMonth) + "/" + year;

                        try {
                            Meetings md  = new Meetings ();

                            if(mEmployeeId!=0){
                                md.setEmployeeId(mEmployeeId);
                            }else{
                                md.setEmployeeId(PreferenceHandler.getInstance( MeetingDetailList.this).getUserId());
                            }

                            md.setMeetingDate(date1);
                            getMeetingsDetails(md);


                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }


                } , mYear, mMonth, mDay);

        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();

    }

    private void getMeetingsDetails(final Meetings loginDetails){

        progressBarUtil.showProgress ( "Loading Meetings..." );

        MeetingsAPI apiService = Util.getClient().create(MeetingsAPI.class);
        Call<ArrayList< Meetings >> call = apiService.getMeetingsByEmployeeIdAndDate(loginDetails);

        call.enqueue(new Callback<ArrayList< Meetings >>() {
            @Override
            public void onResponse( @NonNull Call<ArrayList< Meetings >> call, @NonNull Response<ArrayList< Meetings >> response) {
                int statusCode = response.code();
                if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {

                    if(progressBarUtil!=null){
                        progressBarUtil.hideProgress ();
                    }

                    ArrayList< Meetings > list = response.body();

                    if (list !=null && list.size()!=0) {

                        mMeetingList.removeAllViews();
                        mAdapter = new MeetingDetailAdapter( MeetingDetailList.this,list);
                        mMeetingList.setAdapter(mAdapter);

                    }

                }else {

                    if(progressBarUtil!=null){
                        progressBarUtil.hideProgress ();
                    }

                    //Toast.makeText(DailyTargetsForEmployeeActivity.this, "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure( @NonNull Call<ArrayList< Meetings >> call, @NonNull Throwable t) {
                if(progressBarUtil!=null){
                    progressBarUtil.hideProgress ();
                }
                Log.e("TAG", t.toString());
            }
        });
    }
}
