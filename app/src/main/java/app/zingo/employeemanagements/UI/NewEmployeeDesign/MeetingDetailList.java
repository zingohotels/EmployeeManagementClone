package app.zingo.employeemanagements.UI.NewEmployeeDesign;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import junit.framework.Assert;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import app.zingo.employeemanagements.Adapter.MeetingDetailAdapter;
import app.zingo.employeemanagements.Adapter.TaskListAdapter;
import app.zingo.employeemanagements.Model.Employee;
import app.zingo.employeemanagements.Model.Meetings;
import app.zingo.employeemanagements.Model.Tasks;
import app.zingo.employeemanagements.R;
import app.zingo.employeemanagements.UI.Admin.CreateTaskScreen;
import app.zingo.employeemanagements.UI.Admin.EmployeeTaskMapScreen;
import app.zingo.employeemanagements.UI.NewAdminDesigns.DailyTargetsForEmployeeActivity;
import app.zingo.employeemanagements.Utils.PreferenceHandler;
import app.zingo.employeemanagements.Utils.ThreadExecuter;
import app.zingo.employeemanagements.Utils.Util;
import app.zingo.employeemanagements.WebApi.MeetingsAPI;
import app.zingo.employeemanagements.WebApi.TasksAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MeetingDetailList extends AppCompatActivity {

    FloatingActionButton floatingActionButton,refresh;
    View layout;
    private MeetingDetailAdapter mAdapter;
    RecyclerView mMeetingList;

    //CalendarDay mCalendarDay;
    private Employee mEmployee;
    private int mEmployeeId;
    List<Meetings> mMeetingLists = new ArrayList();
    Toolbar mToolbar;

    ArrayList<Meetings> employeeMeetings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        try{

            setContentView(R.layout.activity_meeting_detail_list);
            setupToolbar();

            mMeetingList = (RecyclerView) findViewById(R.id.meetingList);
            mMeetingList.setLayoutManager(new LinearLayoutManager(this));


            this.floatingActionButton = (FloatingActionButton) findViewById(R.id.addMeetingtOption);
            this.refresh = (FloatingActionButton) findViewById(R.id.refresh);

            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent createTask = new Intent(MeetingDetailList.this, MeetingAddWithSignScreen.class);
                    createTask.putExtra("EmployeeId", mEmployeeId);
                    startActivity(createTask);

                }
            });

            refresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent createTask = new Intent(MeetingDetailList.this, MeetingDetailList.class);

                    startActivity(createTask);
                    MeetingDetailList.this.finish();

                }
            });

            Bundle bundle = getIntent().getExtras();

            if(bundle!=null){

                mEmployeeId = bundle.getInt("ProfileId");


            }

            if(mEmployeeId!=0){
                getMeetings(mEmployeeId);
            }else{

                getMeetings(PreferenceHandler.getInstance(MeetingDetailList.this).getUserId());

            }






        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void setupToolbar() {
        this.mToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(this.mToolbar);
        Assert.assertNotNull(getSupportActionBar());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Meetings");
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_calendar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id)
        {
            case android.R.id.home:

                MeetingDetailList.this.finish();
                break;

            case R.id.action_calendar:

                openDatePicker();
                break;


        }
        return super.onOptionsItemSelected(item);
    }


    private void getMeetings(final int employeeId){


        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading Details..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {
                MeetingsAPI apiService = Util.getClient().create(MeetingsAPI.class);
                Call<ArrayList<Meetings>> call = apiService.getMeetingsByEmployeeId(employeeId);

                call.enqueue(new Callback<ArrayList<Meetings>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Meetings>> call, Response<ArrayList<Meetings>> response) {
                        int statusCode = response.code();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {


                            if (progressDialog!=null)
                                progressDialog.dismiss();
                            ArrayList<Meetings> list = response.body();
                            employeeMeetings = response.body();




                            if (list !=null && list.size()!=0) {


                                mAdapter = new MeetingDetailAdapter(MeetingDetailList.this,list);
                                mMeetingList.setAdapter(mAdapter);



                            }else{

                                Toast.makeText(MeetingDetailList.this, "No Meetings given for this employee ", Toast.LENGTH_SHORT).show();

                            }

                        }else {

                            if (progressDialog!=null)
                                progressDialog.dismiss();

                            Toast.makeText(MeetingDetailList.this, "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Meetings>> call, Throwable t) {
                        // Log error here since request failed
                        if (progressDialog!=null)
                            progressDialog.dismiss();
                        Log.e("TAG", t.toString());
                    }
                });
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
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        try {
                            Log.d("Date", "DATE SELECTED "+dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                            Calendar newDate = Calendar.getInstance();
                            newDate.set(year,monthOfYear,dayOfMonth);


                            String date1 = (monthOfYear + 1)  + "/" + (dayOfMonth) + "/" + year;
                            String date2 = year  + "-" +(monthOfYear + 1)+ "-" +  (dayOfMonth);

                            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy");



                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
                            try {
                                Date fdate = simpleDateFormat.parse(date1);
                                Meetings md  = new Meetings();

                                if(mEmployeeId!=0){
                                    md.setEmployeeId(mEmployeeId);
                                }else{
                                    md.setEmployeeId(PreferenceHandler.getInstance(MeetingDetailList.this).getUserId());
                                }

                                md.setMeetingDate(date1);
                                getMeetingsDetails(md);


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

        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();

    }

    private void getMeetingsDetails(final Meetings loginDetails){




        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {
                MeetingsAPI apiService = Util.getClient().create(MeetingsAPI.class);
                Call<ArrayList<Meetings>> call = apiService.getMeetingsByEmployeeIdAndDate(loginDetails);

                call.enqueue(new Callback<ArrayList<Meetings>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Meetings>> call, Response<ArrayList<Meetings>> response) {
                        int statusCode = response.code();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {



                            ArrayList<Meetings> list = response.body();

                            if (list !=null && list.size()!=0) {

                                mMeetingList.removeAllViews();
                                mAdapter = new MeetingDetailAdapter(MeetingDetailList.this,list);
                                mMeetingList.setAdapter(mAdapter);

                            }else{


                                // Toast.makeText(DailyTargetsForEmployeeActivity.this, "No Tasks given for this employee ", Toast.LENGTH_SHORT).show();

                            }

                        }else {



                            //Toast.makeText(DailyTargetsForEmployeeActivity.this, "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Meetings>> call, Throwable t) {
                        // Log error here since request failed
                       /* if (progressDialog!=null)
                            progressDialog.dismiss();*/
                        Log.e("TAG", t.toString());
                    }
                });
            }


        });
    }
}
