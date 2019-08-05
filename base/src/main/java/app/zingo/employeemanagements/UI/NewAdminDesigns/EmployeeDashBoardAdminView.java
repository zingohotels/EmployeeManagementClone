package app.zingo.employeemanagements.UI.NewAdminDesigns;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import app.zingo.employeemanagements.Adapter.MeetingDetailAdapter;
import app.zingo.employeemanagements.Adapter.TaskListAdapter;
import app.zingo.employeemanagements.Custom.MyRegulerText;
import app.zingo.employeemanagements.Model.Employee;
import app.zingo.employeemanagements.Model.EmployeeImages;
import app.zingo.employeemanagements.Model.Expenses;
import app.zingo.employeemanagements.Model.LiveTracking;
import app.zingo.employeemanagements.Model.LoginDetails;
import app.zingo.employeemanagements.Model.Meetings;
import app.zingo.employeemanagements.Model.Tasks;
import app.zingo.employeemanagements.UI.Landing.InternalServerErrorScreen;
import app.zingo.employeemanagements.UI.NewEmployeeDesign.MeetingDetailList;
import app.zingo.employeemanagements.Utils.PreferenceHandler;
import app.zingo.employeemanagements.Utils.ThreadExecuter;
import app.zingo.employeemanagements.Utils.Util;
import app.zingo.employeemanagements.WebApi.ExpensesApi;
import app.zingo.employeemanagements.WebApi.LiveTrackingAPI;
import app.zingo.employeemanagements.WebApi.LoginDetailsAPI;
import app.zingo.employeemanagements.WebApi.MeetingsAPI;
import app.zingo.employeemanagements.WebApi.TasksAPI;
import app.zingo.employeemanagements.base.R;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmployeeDashBoardAdminView extends AppCompatActivity {

    Employee employee;
    //  MyRegulerText mWorkedDays,mCompletedtasks,mTotalMeetings,mTotalExpenses;
    TextView mWorkedDays,mCompletedtasks,mTotalMeetings,mEmployeeName,mDate,mStatus,mLoginId;
    MyRegulerText mLoginTime,mLoginText,mLoginAddress,mLogoutTime,mLogouText,
            mLogoutAddress,mMeetingTime,mMetingAdrz,mTaskREad,mMeetingRead,
            mTitle,mExpeText,mExpAmt,mKmTravelled,mAvgtaskTime,mAvgMeetingTime,mIdleTime;//,mLogoutTime,mWorkingHours
    CircleImageView mProfilePic;
    // CustomSpinner mDay;

    ImageView mPrevious,mNext;

    boolean checkIn=false,checkout=false;
    LoginDetails checkLogIn,checkLogOut;

    LinearLayout mNoRecord,mNoMeeting;
    private TaskListAdapter mAdapter;
    RecyclerView mTaskList,mMeetingList;

    ArrayList<Tasks> employeeTasks;
    ArrayList<Tasks> pendingTasks ;
    ArrayList<Tasks> completedTasks ;
    ArrayList<Tasks> closedTasks ;

    ArrayList<Tasks> dayemployeeTasks;
    ArrayList<Tasks> daypendingTasks ;
    ArrayList<Tasks> daycompletedTasks ;
    ArrayList<Tasks> dayclosedTasks ;

    ArrayList<Meetings> employeeMeetings;
    ArrayList<Meetings> pendingMeetings ;
    ArrayList<Meetings> completedMeetings ;
    ArrayList<Meetings> closedMeetings ;

    ArrayList<Meetings> dayemployeeMeetings;
    ArrayList<Meetings> daypendingMeetings ;
    ArrayList<Meetings> daycompletedMeetings ;
    ArrayList<Meetings> dayclosedMeetings ;

    int total=0,pending=0,complete=0,closed=0;
    int daytotal=0,daypending=0,daycomplete=0,dayclosed=0;

    int totalMeeting=0,pendingMeeting=0,completeMeetings=0,closedMeeting=0;
    int daytotalMeetings=0,daypendingMeeting=0,daycompleteMeetings=0,dayclosedMeeting=0;

    SimpleDateFormat dateFormat;
    String passDate = "";


    long meetDiff=0,workingDiff = 0;
    boolean taslTrue = false;
    boolean loginTrue = false;

    Handler h;
    Runnable runnable;
    int delay = 5*1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
            setContentView(R.layout.activity_employee_dash_board_admin_view);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            mWorkedDays = findViewById(R.id.worked_days_count);
            mCompletedtasks = findViewById(R.id.completed_task_count_text);
            mTotalMeetings = findViewById(R.id.meeting_count_text);
            mEmployeeName = findViewById(R.id.name);
            mProfilePic = findViewById(R.id.employee_pic);
            //mDay = (CustomSpinner) findViewById(R.id.spinner);
            // mTotalExpenses = (MyRegulerText)findViewById(R.id.expense_count_text);
            mTitle = findViewById(R.id.atten_title);
            mLoginTime = findViewById(R.id.login_time);
            mLoginText = findViewById(R.id.logged_text);
            mLoginAddress = findViewById(R.id.check_in_address);
            mLogoutTime = findViewById(R.id.logout_time);
            mLogouText = findViewById(R.id.log_out__text);
            mLogoutAddress = findViewById(R.id.check_out_address);
            mMeetingTime = findViewById(R.id.meeting_time);
            mMetingAdrz = findViewById(R.id.meeting__text);
            mTaskREad = findViewById(R.id.read_task);
            mMeetingRead = findViewById(R.id.read_meeting);
            mExpeText = findViewById(R.id.expenses_text);
            mExpAmt = findViewById(R.id.expense_amount);
            mKmTravelled = findViewById(R.id.km_text);
            mAvgMeetingTime = findViewById(R.id.avg_meeting_time);
            mAvgtaskTime = findViewById(R.id.avg_task_time);
            mIdleTime = findViewById(R.id.idle_time);
            mStatus = findViewById(R.id.status);
            mLoginId = findViewById(R.id.hidden_login_id);
           /* mLogoutTime = (MyRegulerText)findViewById(R.id.logout_time);
            mWorkingHours = (MyRegulerText)findViewById(R.id.working_hours);*/

            mDate = findViewById(R.id.presentDate);
            mPrevious = findViewById(R.id.previousDay);
            mNext = findViewById(R.id.nextDay);

            mTaskList = findViewById(R.id.targetList);
            mTaskList.setLayoutManager(new LinearLayoutManager(this));
            mNoRecord = findViewById(R.id.noRecordFound);

            mMeetingList = findViewById(R.id.targetList_meeting);
            mMeetingList.setLayoutManager(new LinearLayoutManager(this));
            mNoMeeting = findViewById(R.id.noRecordFound_meetings);

            setTitle("Dash Board");

            Bundle bundle = getIntent().getExtras();

            if(bundle!=null){

                employee = (Employee)bundle.getSerializable("Profile");
            }

            if(PreferenceHandler.getInstance(EmployeeDashBoardAdminView.this).getUserRoleUniqueID()==2||PreferenceHandler.getInstance(EmployeeDashBoardAdminView.this).getUserRoleUniqueID()==9){

            }else{
                mStatus.setEnabled(false);

            }


            mStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {

                        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(EmployeeDashBoardAdminView.this);
                        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View views = inflater.inflate(R.layout.alert_update_login, null);

                        builder.setView(views);


                        final Spinner mTask = views.findViewById(R.id.task_status_update);
                        final Button mSave = views.findViewById(R.id.save);

                        final android.support.v7.app.AlertDialog dialogs = builder.create();
                        dialogs.show();
                        dialogs.setCanceledOnTouchOutside(true);

                        if (mStatus.getText().toString().equalsIgnoreCase("Absent")) {

                            mTask.setSelection(0);
                            //mTask.setEnabled(false);
                        } else {
                            mTask.setSelection(1);
                        }


                        mSave.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                String status = mTask.getSelectedItem().toString();
                                if (status != null && status.equalsIgnoreCase("Absent")) {

                                    String value = mLoginId.getText().toString();

                                    int valueCheck = Integer.parseInt(value);

                                    if (valueCheck == 0) {
                                        dialogs.dismiss();

                                    } else {
                                        getLoginDetailsById(Integer.parseInt(value), status, mStatus);
                                        dialogs.dismiss();
                                    }


                                } else {

                                    String value = mLoginId.getText().toString();

                                    int valueCheck = Integer.parseInt(value);

                                    if (valueCheck == 0) {

                                        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                                        SimpleDateFormat sdt = new SimpleDateFormat("MMM dd,yyyy hh:mm a");

                                        LoginDetails loginDetails = new LoginDetails();
                                        loginDetails.setEmployeeId(employee.getEmployeeId());
                                        loginDetails.setLatitude("" + PreferenceHandler.getInstance(getApplicationContext()).getOrganizationLati());
                                        loginDetails.setLongitude("" + PreferenceHandler.getInstance(getApplicationContext()).getOrganizationLongi());

                                        LatLng master = new LatLng(Double.parseDouble(PreferenceHandler.getInstance(getApplicationContext()).getOrganizationLati()), Double.parseDouble(PreferenceHandler.getInstance(getApplicationContext()).getOrganizationLongi()));
                                        String address = getAddress(master);

                                        loginDetails.setLocation("" + address);
                                        loginDetails.setLoginTime("" + sdt.format(new Date()));
                                        loginDetails.setLoginDate("" + sdf.format(new Date()));
                                        loginDetails.setLogOutTime("");
                                        try {
                                            addLogin(loginDetails, mLoginId, mStatus);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                        dialogs.dismiss();

                                    } else {
                                        getLoginDetailsById(Integer.parseInt(value), status, mStatus);
                                        dialogs.dismiss();
                                    }
                                }
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                }
            });
            String[] data = {"Filter By","Today", "Yesterday", "Date Picker"};

            ArrayAdapter adapter = new ArrayAdapter<>(EmployeeDashBoardAdminView.this, R.layout.spinner_item_selected, data);
            adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

            dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            mDate.setText(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));

            mPrevious.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try{
                        final Date date = dateFormat.parse(mDate.getText().toString());
                        final Calendar calendar = Calendar.getInstance();
                        calendar.setTime(date);
                        calendar.add(Calendar.DAY_OF_YEAR, -1);

                        Date date2 = calendar.getTime();

                        mDate.setText(dateFormat.format(date2));



                        //Today Summary
                        LoginDetails ld  = new LoginDetails();
                        ld.setEmployeeId(employee.getEmployeeId());
                        ld.setLoginDate(new SimpleDateFormat("MM/dd/yyyy").format(date2));
                        String logDate = new SimpleDateFormat("MMM dd,yyyy").format(date2);
                        getLoginDetailsDate(ld,logDate);

                        Meetings md  = new Meetings();
                        md.setEmployeeId(employee.getEmployeeId());
                        md.setMeetingDate(new SimpleDateFormat("MM/dd/yyyy").format(date2));
                        String mdDate = new SimpleDateFormat("MMM dd,yyyy").format(date2);
                        getMeetingsDetails(md,mdDate);

                        LiveTracking lv = new LiveTracking();
                        lv.setEmployeeId(employee.getEmployeeId());
                        lv.setTrackingDate(new SimpleDateFormat("MM/dd/yyyy").format(date2));
                        getLiveLocation(lv);

                        getTasks(employee.getEmployeeId(),new SimpleDateFormat("yyyy-MM-dd").format(date2));
                        getExpense(employee.getEmployeeId(),new SimpleDateFormat("yyyy-MM-dd").format(date2));
                        //getLiveLocation(profile.getEmployeeId());
                        valueCheck();

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });

            mNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    try{
                        final Date date = dateFormat.parse(mDate.getText().toString());
                        String newDate = dateFormat.format(new Date());
                        final Date newDates = dateFormat.parse(newDate);

                        if(newDates.getTime()<=date.getTime()){

                            Toast.makeText(EmployeeDashBoardAdminView.this, "Data will not available for future date", Toast.LENGTH_SHORT).show();

                        }else{
                            final Calendar calendar = Calendar.getInstance();
                            calendar.setTime(date);
                            calendar.add(Calendar.DAY_OF_YEAR, 1);

                            Date date2 = calendar.getTime();

                            mDate.setText(dateFormat.format(date2));



                            //Today Summary
                            LoginDetails ld  = new LoginDetails();
                            ld.setEmployeeId(employee.getEmployeeId());
                            ld.setLoginDate(new SimpleDateFormat("MM/dd/yyyy").format(date2));
                            String logDate = new SimpleDateFormat("MMM dd,yyyy").format(date2);
                            getLoginDetailsDate(ld,logDate);

                            Meetings md  = new Meetings();
                            md.setEmployeeId(employee.getEmployeeId());
                            md.setMeetingDate(new SimpleDateFormat("MM/dd/yyyy").format(date2));
                            String mdDate = new SimpleDateFormat("MMM dd,yyyy").format(date2);
                            getMeetingsDetails(md,mdDate);

                            LiveTracking lv = new LiveTracking();
                            lv.setEmployeeId(employee.getEmployeeId());
                            lv.setTrackingDate(new SimpleDateFormat("MM/dd/yyyy").format(date2));
                            getLiveLocation(lv);

                            getTasks(employee.getEmployeeId(),new SimpleDateFormat("yyyy-MM-dd").format(date2));
                            getExpense(employee.getEmployeeId(),new SimpleDateFormat("yyyy-MM-dd").format(date2));

                            valueCheck();
                            //getLiveLocation(profile.getEmployeeId());
                        }




                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            });

            mDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openDatePicker(mDate);
                }
            });




            if(employee!=null){

                mEmployeeName.setText(""+employee.getEmployeeName());

                ArrayList<EmployeeImages> images = employee.getEmployeeImages();

                if(images!=null&&images.size()!=0){
                    EmployeeImages employeeImages = images.get(0);

                    if(employeeImages!=null){


                        String base=employeeImages.getImage();
                        if(base != null && !base.isEmpty()){
                            Picasso.with(EmployeeDashBoardAdminView.this).load(base).placeholder(R.drawable.profile_image).error(R.drawable.profile_image).into(mProfilePic);


                        }
                    }

                }

                String dateValue = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

                try{

                    Date date=null;
                    try {
                        date = new SimpleDateFormat("yyyy-MM-dd").parse(dateValue);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    //getLoginDetails(employee.getEmployeeId());
                    //getMeetingDetails(employee.getEmployeeId());

                    final Calendar calendar = Calendar.getInstance();
                    Date date2 = calendar.getTime();


                    //Today Summary
                    LoginDetails ld  = new LoginDetails();
                    ld.setEmployeeId(employee.getEmployeeId());
                    ld.setLoginDate(new SimpleDateFormat("MM/dd/yyyy").format(date));
                    String logDate = new SimpleDateFormat("MMM dd,yyyy").format(date);
                    getLoginDetailsDate(ld,logDate);

                    Meetings md  = new Meetings();
                    md.setEmployeeId(employee.getEmployeeId());
                    md.setMeetingDate(new SimpleDateFormat("MM/dd/yyyy").format(date));
                    String mdDate = new SimpleDateFormat("MMM dd,yyyy").format(date);
                    getMeetingsDetails(md,mdDate);
                    //getLiveLocation(profile.getEmployeeId());

                    LiveTracking lv = new LiveTracking();
                    lv.setEmployeeId(employee.getEmployeeId());
                    lv.setTrackingDate(new SimpleDateFormat("MM/dd/yyyy").format(date));
                    getLiveLocation(lv);

                    getTasks(employee.getEmployeeId(),new SimpleDateFormat("yyyy-MM-dd").format(date2));
                    getExpense(employee.getEmployeeId(),new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                    valueCheck();

                }catch (Exception e){
                    e.printStackTrace();
                    Intent error = new Intent(EmployeeDashBoardAdminView.this,InternalServerErrorScreen.class);
                    startActivity(error);
                }

            }

            mLoginAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(checkIn&&checkLogIn!=null){

                        Intent map = new Intent(EmployeeDashBoardAdminView.this,CheckInMapScreen.class);
                        Bundle bundle = new Bundle();
                        checkLogIn.setTitle(""+employee.getEmployeeName()+" - Check-In");
                        checkLogIn.setStatus(""+employee.getEmployeeName()+" - Check-In");
                        bundle.putSerializable("Location",checkLogIn);
                        map.putExtras(bundle);
                        startActivity(map);


                    }


                }
            });

            mLogoutAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(checkout&&checkLogOut!=null){

                        Intent map = new Intent(EmployeeDashBoardAdminView.this,CheckInMapScreen.class);
                        Bundle bundle = new Bundle();
                        checkLogOut.setTitle(""+employee.getEmployeeName()+" - Check-Out");
                        checkLogOut.setStatus(""+employee.getEmployeeName()+" - Check-In");
                        bundle.putSerializable("Location",checkLogOut);
                        map.putExtras(bundle);
                        startActivity(map);


                    }


                }
            });

            mTaskREad.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(EmployeeDashBoardAdminView.this, DailyTargetsForEmployeeActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("Profile",employee);
                    bundle.putInt("ProfileId",employee.getEmployeeId());
                    intent.putExtras(bundle);
                    startActivity(intent);

                }
            });

            mMeetingRead.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent attnd = new Intent(EmployeeDashBoardAdminView.this, MeetingDetailList.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("Profile",employee);
                    bundle.putInt("ProfileId",employee.getEmployeeId());
                    attnd.putExtras(bundle);
                    startActivity(attnd);

                }
            });

            mCompletedtasks.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if(dayemployeeTasks!=null&&dayemployeeTasks.size()!=0){

                        /*Intent pending = new Intent(EmployeeDashBoardAdminView.this,PendingTasks.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("PendingTasks",dayemployeeTasks);
                        bundle.putString("Title","Task List");
                        pending.putExtras(bundle);
                        startActivity(pending);*/
                    }

                }
            });



        }catch (Exception e){
            e.printStackTrace();
        }

        // title = "Tasks";
    }

    private void getLoginDetails(final int employeeId) {


        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading Details..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {
                LoginDetailsAPI apiService = Util.getClient().create(LoginDetailsAPI.class);
                Call<ArrayList<LoginDetails>> call = apiService.getLoginByEmployeeId(employeeId);

                call.enqueue(new Callback<ArrayList<LoginDetails>>() {
                    @Override
                    public void onResponse(Call<ArrayList<LoginDetails>> call, Response<ArrayList<LoginDetails>> response) {
                        int statusCode = response.code();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {


                            if (progressDialog!=null)
                                progressDialog.dismiss();
                            ArrayList<LoginDetails> list = response.body();


                            if (list !=null && list.size()!=0) {

                                Collections.sort(list,LoginDetails.compareLogin);


                                ArrayList<String> dateList = new ArrayList<>();
                                for(int i=0;i<list.size();i++){

                                    if(list.get(i).getLoginDate().contains("T")){


                                        String date[] = list.get(i).getLoginDate().split("T");

                                        dateList.add(date[0]);
                                    }

                                }

                                if(dateList!=null&&dateList.size()!=0){

                                    Set<String> s = new LinkedHashSet<String>(dateList);

                                    //mWorkedDays.setText(""+s.size());
                                }




                            }else{

                            }

                        }else {

                            if (progressDialog!=null)
                                progressDialog.dismiss();

                            Toast.makeText(EmployeeDashBoardAdminView.this, "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<LoginDetails>> call, Throwable t) {
                        // Log error here since request failed
                        if (progressDialog!=null)
                            progressDialog.dismiss();
                        Log.e("TAG", t.toString());
                    }
                });
            }


        });
    }

    private void getMeetingDetails(final int employeeId){


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



                            if (list !=null && list.size()!=0) {

                                Collections.sort(list,Meetings.compareMeetings);
                                //mTotalMeetings.setText(""+list.size());

                            }else{

                            }

                        }else {


                            Toast.makeText(EmployeeDashBoardAdminView.this, "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
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

    public long dateCal(String date,String login,String logout){

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy hh:mm a");
        System.out.println("Loigin "+login);
        System.out.println("Logout "+logout);


        Date fd=null,td=null;

        if(login==null||login.isEmpty()){

            login = date+" 00:00 am";
        }

        if(logout==null||logout.isEmpty()){

            logout = sdf.format(new Date()) ;
        }

        try {
            fd = sdf.parse(""+login);
            td = sdf.parse(""+logout);

            long diff = td.getTime() - fd.getTime();
            long Hours = diff / (60 * 60 * 1000) % 24;
            long Minutes = diff / (60 * 1000) % 60;
            System.out.println("Diff "+diff);
            System.out.println("Hours "+Hours);
            System.out.println("Minutes "+Minutes);
          /*  long diffDays = diff / (24 * 60 * 60 * 1000);
            long Hours = diff / (60 * 60 * 1000) % 24;
            long Minutes = diff / (60 * 1000) % 60;
            long Seconds = diff / 1000 % 60;*/

            return  diff;
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }


    }

    private void getTasks(final int employeeId,final String dateValue) {

        employeeTasks = new ArrayList<>();
        pendingTasks = new ArrayList<>();
        completedTasks = new ArrayList<>();
        closedTasks = new ArrayList<>();
        final ArrayList<Tasks> onTask = new ArrayList<>();


        dayemployeeTasks = new ArrayList<>();
        daypendingTasks = new ArrayList<>();
        daycompletedTasks = new ArrayList<>();
        dayclosedTasks = new ArrayList<>();

        daytotal=0;
        daypending=0;
        daycomplete=0;
        dayclosed=0;

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading Details..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {
                TasksAPI apiService = Util.getClient().create(TasksAPI.class);
                Call<ArrayList<Tasks>> call = apiService.getTasksByEmployeeId(employeeId);

                call.enqueue(new Callback<ArrayList<Tasks>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Tasks>> call, Response<ArrayList<Tasks>> response) {
                        int statusCode = response.code();



                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {

                            taslTrue = true;

                            if (progressDialog!=null)
                                progressDialog.dismiss();
                            ArrayList<Tasks> list = response.body();



                            Date date = new Date();
                            Date adate = new Date();
                            Date edate = new Date();

                            try {
                                date = new SimpleDateFormat("yyyy-MM-dd").parse(dateValue);


                            } catch (Exception e) {
                                e.printStackTrace();
                            }




                            if(list!=null&&list.size()!=0){

                                //mTotaltask.setText(String.valueOf(list.size()));

                                for (Tasks task:list) {

                                    if(task.getStatus().equalsIgnoreCase("On-Going")&&(task.getStartDate()!=null&&task.getEndDate()!=null)){

                                        onTask.add(task);

                                    }else if(task.getStatus().equalsIgnoreCase("Completed")&&(task.getStartDate()!=null&&task.getEndDate()!=null)){

                                        completedTasks.add(task);
                                    }


                                    String froms = task.getStartDate();
                                    String tos = task.getEndDate();

                                    Date afromDate = null;
                                    Date atoDate = null;

                                    if(froms!=null&&!froms.isEmpty()){

                                        if(froms.contains("T")){

                                            String dojs[] = froms.split("T");

                                            try {
                                                afromDate = new SimpleDateFormat("yyyy-MM-dd").parse(dojs[0]);
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }


                                        }

                                    }

                                    if(tos!=null&&!tos.isEmpty()){

                                        if(tos.contains("T")){

                                            String dojs[] = tos.split("T");

                                            try {
                                                atoDate = new SimpleDateFormat("yyyy-MM-dd").parse(dojs[0]);
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }

                                        }

                                    }

                                    if(afromDate!=null&&atoDate!=null){

                                        if(date.getTime() >= afromDate.getTime() && date.getTime() <= atoDate.getTime()){


                                            dayemployeeTasks.add(task);
                                            daytotal = daytotal+1;

                                            if(task.getStatus().equalsIgnoreCase("Completed")){
                                                daycompletedTasks.add(task);
                                                daycomplete = daycomplete+1;
                                            }else if(task.getStatus().equalsIgnoreCase("Pending")){
                                                daypendingTasks.add(task);
                                                daypending = daypending+1;
                                            }else if(task.getStatus().equalsIgnoreCase("On-Going")){
                                                dayclosedTasks.add(task);
                                                dayclosed = dayclosed+1;
                                            }

                                        }
                                    }
                                }


                                mCompletedtasks.setText(""+daytotal);

                                if(dayemployeeTasks!=null&&dayemployeeTasks.size()!=0){

                                    mNoRecord.setVisibility(View.GONE);
                                    mTaskList.setVisibility(View.VISIBLE);
                                    mTaskList.removeAllViews();

                                    if(dayemployeeTasks.size()>2){
                                        ArrayList<Tasks> twoArray = new ArrayList<>();
                                        twoArray.add(dayemployeeTasks.get(0));
                                        twoArray.add(dayemployeeTasks.get(1));
                                        mAdapter = new TaskListAdapter(EmployeeDashBoardAdminView.this,twoArray);
                                        mTaskList.setAdapter(mAdapter);
                                        mTaskREad.setVisibility(View.VISIBLE);
                                    }else{
                                        mTaskREad.setVisibility(View.GONE);
                                        mAdapter = new TaskListAdapter(EmployeeDashBoardAdminView.this,dayemployeeTasks);
                                        mTaskList.setAdapter(mAdapter);
                                    }


                                  /*  mAdapter = new TaskListAdapter(EmployeeDashBoardAdminView.this,dayemployeeTasks);
                                    mTaskList.setAdapter(mAdapter);*/

                                    /*totalTargets.setText(""+daytotal);
                                    openTargets.setText(""+daypending);
                                    closedTargets.setText(""+daycomplete);
                                    movedTargets.setText(""+dayclosed);*/
                                }else{
                                    mTaskList.removeAllViews();
                                    mCompletedtasks.setText("0");
                                    mNoRecord.setVisibility(View.VISIBLE);
                                    mTaskList.setVisibility(View.GONE);
                                    mTaskREad.setVisibility(View.GONE);
                                }

                            }


                        }else {

                            if (progressDialog!=null)
                                progressDialog.dismiss();

                            taslTrue = true;
                            mTaskList.removeAllViews();
                            mCompletedtasks.setText("0");
                            mNoRecord.setVisibility(View.VISIBLE);
                            mTaskList.setVisibility(View.GONE);
                            mTaskREad.setVisibility(View.GONE);
                            Toast.makeText(EmployeeDashBoardAdminView.this, "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Tasks>> call, Throwable t) {
                        // Log error here since request failed
                        if (progressDialog!=null)
                            progressDialog.dismiss();
                        taslTrue = true;
                        mTaskList.removeAllViews();
                        mCompletedtasks.setText("0");
                        mNoRecord.setVisibility(View.VISIBLE);
                        mTaskList.setVisibility(View.GONE);
                        mTaskREad.setVisibility(View.GONE);
                        Log.e("TAG", t.toString());
                    }
                });
            }


        });
    }

    private void getLiveLocation(final LiveTracking lv) {


        final ProgressDialog progressDialog = new ProgressDialog(EmployeeDashBoardAdminView.this);
        progressDialog.setTitle("Loading Details..");
        progressDialog.setCancelable(false);
        progressDialog.show();



        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {
                LiveTrackingAPI apiService = Util.getClient().create(LiveTrackingAPI.class);
                Call<ArrayList<LiveTracking>> call = apiService.getLiveTrackingByEmployeeIdAndDate(lv);

                call.enqueue(new Callback<ArrayList<LiveTracking>>() {
                    @Override
                    public void onResponse(Call<ArrayList<LiveTracking>> call, Response<ArrayList<LiveTracking>> response) {
                        int statusCode = response.code();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {


                            if (progressDialog!=null)
                                progressDialog.dismiss();
                            ArrayList<LiveTracking> list = response.body();
                            double distance = 0;


                            if (list !=null && list.size()!=0) {

                                Collections.sort(list,LiveTracking.compareLiveTrack);


                                double lati = 0,lngi=0;
                                double latis = 0,lngis=0;

                                String logoutT = "",loginT="";

                                String time = list.get(0).getTrackingTime();

                                Location locationA = new Location("point A");
                                Location locationB = new Location("point B");

                                int minutes =0;
                                boolean addValue = false;
                                Date fd=null,td=null;
                                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                                long diff=0;
                                int indexValue = 0;

                                lati = Double.parseDouble(list.get(0).getLatitude());
                                lngi = Double.parseDouble(list.get(0).getLongitude());

                                for(int i=1;i<list.size();i++){

                                    if(list.get(i).getLongitude()!=null||list.get(i).getLatitude()!=null){

                                        double lat = Double.parseDouble(list.get(i).getLatitude());
                                        double lng = Double.parseDouble(list.get(i).getLongitude());



                                        if(lat==0&&lng==0){

                                        }else{

                                            if(list.size()==1){

                                            }else{

                                                if(lati==0&&lngi==0){

                                                    lati = Double.parseDouble(list.get(i-1).getLatitude());
                                                    lngi = Double.parseDouble(list.get(i-1).getLongitude());


                                                }else{

                                                    latis = lati;
                                                    lngis = lngi;

                                                    double orgLati = Double.parseDouble(PreferenceHandler.getInstance(EmployeeDashBoardAdminView.this).getOrganizationLati());
                                                    double orgLngi = Double.parseDouble(PreferenceHandler.getInstance(EmployeeDashBoardAdminView.this).getOrganizationLongi());

                                                    locationA.setLatitude(latis);
                                                    locationA.setLongitude(lngis);


                                                    locationB.setLatitude(Double.parseDouble(list.get(i).getLatitude()));
                                                    locationB.setLongitude(Double.parseDouble(list.get(i).getLongitude()));


                                                    if(distance(orgLati,orgLngi,Double.parseDouble(list.get(i).getLatitude()),Double.parseDouble(list.get(i).getLongitude()))>0.500&&distance(latis,lngis,Double.parseDouble(list.get(i).getLatitude()),Double.parseDouble(list.get(i).getLongitude()))>0.500){

                                                        distance = distance+ distance(latis,lngis,Double.parseDouble(list.get(i).getLatitude()),Double.parseDouble(list.get(i).getLongitude()));

                                                        lati = Double.parseDouble(list.get(i).getLatitude());
                                                        lngi = Double.parseDouble(list.get(i).getLongitude());


                                                    }


                                                    double kms = distance;
                                                    double miles = distance*0.621371;

                                                    String kmValue = new DecimalFormat("#.##").format(kms);
                                                    String mileValue = new DecimalFormat("#.##").format(miles);

                                                   /* SpannableString ss1=  new SpannableString(kmValue);
                                                    ss1.setSpan(new RelativeSizeSpan(1f), 0,kmValue.length()-1, 0); // set size
                                                    ss1.setSpan(new ForegroundColorSpan(Color.RED), 0, kmValue.length()-1, 0);// set color
                                                    SpannableString ss12=  new SpannableString(mileValue);
                                                    ss12.setSpan(new RelativeSizeSpan(1f), 0,mileValue.length()-1, 0); // set size
                                                    ss12.setSpan(new ForegroundColorSpan(Color.RED), 0, mileValue.length()-1, 0);// set color
*/


                                                    mKmTravelled.setText(kmValue+" Km/"+mileValue+" miles");

                                                }


                                            }



                                        }



                                    }


                                }





                            }else{

                                mKmTravelled.setText("0 km/0 mile");

                            }

                        }else {

                            mKmTravelled.setText("0 km/0 mile");

                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<LiveTracking>> call, Throwable t) {
                        // Log error here since request failed
                        if (progressDialog!=null)
                            progressDialog.dismiss();
                        Log.e("TAG", t.toString());
                        mKmTravelled.setText("0 km/0 mile");
                    }
                });
            }


        });
    }

    private void getExpense(final int employeeId, final String dateValue){


        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {
                ExpensesApi apiService = Util.getClient().create(ExpensesApi.class);
                Call<ArrayList<Expenses>> call = apiService.getExpenseByEmployeeIdAndOrganizationId(PreferenceHandler.getInstance(EmployeeDashBoardAdminView.this).getCompanyId(),employeeId);

                call.enqueue(new Callback<ArrayList<Expenses>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Expenses>> call, Response<ArrayList<Expenses>> response) {
                        int statusCode = response.code();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {

                            ArrayList<Expenses> list = response.body();
                            ArrayList<Expenses> todayTasks = new ArrayList<>();


                            Date date = new Date();
                            Date adate = new Date();
                            Date edate = new Date();

                            try {
                                date = new SimpleDateFormat("yyyy-MM-dd").parse(dateValue);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            if (list !=null && list.size()!=0) {

                                double amt = 0;

                                for (Expenses task:list) {

                                    String froms = task.getDate();
                                    Date afromDate = null;
                                    Date atoDate = null;

                                    if(froms!=null&&!froms.isEmpty()){

                                        if(froms.contains("T")){

                                            String dojs[] = froms.split("T");

                                            try {
                                                afromDate = new SimpleDateFormat("yyyy-MM-dd").parse(dojs[0]);
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                             /*   String parse = new SimpleDateFormat("MMM yyyy").format(afromDate);
                                                fromDate = new SimpleDateFormat("MMM yyyy").parse(parse);*/

                                        }
                                    }

                                    if(afromDate!=null){

                                        if(date.getTime() == afromDate.getTime() ){

                                            // employeeExpense.add(task);
                                            todayTasks.add(task);
                                            amt = amt+task.getAmount();
                                        }
                                    }
                                }

                                if(todayTasks!=null&&todayTasks.size()!=0){

                                    mWorkedDays.setText(""+todayTasks.size());
                                    mExpAmt.setText("₹ "+new DecimalFormat("#.##").format(amt));
                                    mExpeText.setText("Total Expenses Amount");

                                }else{
                                    //mExpenses.setText(""+expense);
                                    mExpAmt.setText("₹ 0");

                                    mExpeText.setText("No Expenses");
                                }

                            }else{

                                mExpAmt.setText("₹ 0");

                                mExpeText.setText("No Expenses");

                                // Toast.makeText(DailyTargetsForEmployeeActivity.this, "No Tasks given for this employee ", Toast.LENGTH_SHORT).show();
                            }

                        }else {

                            mExpAmt.setText("₹ 0");

                            mExpeText.setText("No Expenses");

                            //Toast.makeText(DailyTargetsForEmployeeActivity.this, "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Expenses>> call, Throwable t) {
                        // Log error here since request failed
                       /* if (progressDialog!=null)
                            progressDialog.dismiss();*/
                        Log.e("TAG", t.toString());
                        mExpAmt.setText("₹ 0");

                        mExpeText.setText("No Expenses");
                    }
                });
            }
        });
    }

    private void getLoginDetailsDate(final LoginDetails loginDetails,final String comDate){

        workingDiff = 0;

        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {
                LoginDetailsAPI apiService = Util.getClient().create(LoginDetailsAPI.class);
                Call<ArrayList<LoginDetails>> call = apiService.getLoginByEmployeeIdAndDate(loginDetails);

                call.enqueue(new Callback<ArrayList<LoginDetails>>() {
                    @Override
                    public void onResponse(Call<ArrayList<LoginDetails>> call, Response<ArrayList<LoginDetails>> response) {
                        int statusCode = response.code();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {
                            loginTrue = true;
                            ArrayList<LoginDetails> list = response.body();

                            if (list !=null && list.size()!=0) {

                                String loginTime = list.get(0).getLoginTime();
                                String logoutTime = list.get(list.size()-1).getLogOutTime();

                                if(list.get(0).getTotalMeeting()!=null&&!list.get(0).getTotalMeeting().isEmpty()){

                                    if(list.get(0).getTotalMeeting().equalsIgnoreCase("Absent")){
                                        mStatus.setText("Absent");
                                        mStatus.setBackgroundColor(Color.parseColor("#FF0000"));
                                        mStatus.setVisibility(View.VISIBLE);
                                    }else{
                                        mStatus.setText("Present");
                                        mStatus.setBackgroundColor(Color.parseColor("#00FF00"));
                                        mStatus.setVisibility(View.VISIBLE);
                                    }
                                }else{
                                    mStatus.setText("Present");
                                    mStatus.setBackgroundColor(Color.parseColor("#00FF00"));
                                    mStatus.setVisibility(View.VISIBLE);
                                }
                                mLoginId.setText(list.get(0).getLoginDetailsId()+"");

                                if(loginTime!=null&&!loginTime.isEmpty()){

                                    try{

                                        Date dateLog = new SimpleDateFormat("MMM dd,yyyy hh:mm a").parse(loginTime);
                                        mLoginTime.setText(""+new SimpleDateFormat("hh:mm a").format(dateLog));
                                        mLoginText.setText("Check-In");
                                        LatLng latLng = new LatLng(Double.parseDouble(list.get(0).getLatitude()),Double.parseDouble(list.get(0).getLongitude()));
                                        String address = getAddress(latLng);
                                        mLoginAddress.setText(""+address);
                                        checkIn = true;
                                        checkLogIn = list.get(0);

                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }

                                }else{
                                    mLoginTime.setText("");
                                }

                                if(logoutTime!=null&&!logoutTime.isEmpty()){
                                    try{

                                        Date dateLog = new SimpleDateFormat("MMM dd,yyyy hh:mm a").parse(logoutTime);
                                        mLogoutTime.setText(""+new SimpleDateFormat("hh:mm a").format(dateLog));
                                        mLogouText.setText("Check-Out");
                                        LatLng latLng = new LatLng(Double.parseDouble(list.get(list.size()-1).getLatitude()),Double.parseDouble(list.get(list.size()-1).getLongitude()));
                                        String address = getAddress(latLng);
                                        mLogoutAddress.setText(""+address);
                                        checkLogOut = list.get(list.size()-1);
                                        checkout = true;

                                        long diffHrs = 0;

                                        for (LoginDetails lg:list) {

                                            if((loginTime==null||loginTime.isEmpty())&&(lg.getLogOutTime()==null||lg.getLogOutTime().isEmpty())){

                                            }else{
                                                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy hh:mm a");
                                                SimpleDateFormat sdfs = new SimpleDateFormat("MMM dd,yyyy");

                                                Date fd=null,td=null;

                                                String logoutT = lg.getLogOutTime();
                                                String loginT = lg.getLoginTime();

                                                if(loginT==null||loginT.isEmpty()){

                                                    loginT = comDate +" 00:00 am";
                                                }

                                                if(logoutT==null||logoutT.isEmpty()){

                                                    logoutT = comDate  +" "+new SimpleDateFormat("hh:mm a").format(new Date()) ;
                                                }

                                                try {
                                                    fd = sdf.parse(""+loginT);
                                                    td = sdf.parse(""+logoutT);

                                                    long diff = td.getTime() - fd.getTime();
                                                    diffHrs = diffHrs+diff;

                                                } catch (ParseException e) {
                                                    e.printStackTrace();

                                                }
                                            }
                                        }

                                        long Hours = diffHrs / (60 * 60 * 1000) ;
                                        long Minutes = diffHrs / (60 * 1000);

                                        workingDiff =diffHrs;

                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }else{
                                    mLogoutTime.setText("Working");

                                    long diffHrs = 0;

                                    for (LoginDetails lg:list) {

                                        if((loginTime==null||loginTime.isEmpty())&&(lg.getLogOutTime()==null||lg.getLogOutTime().isEmpty())){

                                        }else{
                                            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy hh:mm a");
                                            SimpleDateFormat sdfs = new SimpleDateFormat("MMM dd,yyyy");

                                            Date fd=null,td=null;

                                            String logoutT = lg.getLogOutTime();
                                            String loginT = lg.getLoginTime();

                                            if(loginT==null||loginT.isEmpty()){

                                                loginT = comDate +" 00:00 am";
                                            }

                                            if(logoutT==null||logoutT.isEmpty()){

                                                logoutT = comDate  +" "+new SimpleDateFormat("hh:mm a").format(new Date()) ;
                                            }

                                            try {
                                                fd = sdf.parse(""+loginT);
                                                td = sdf.parse(""+logoutT);

                                                long diff = td.getTime() - fd.getTime();
                                                diffHrs = diffHrs+diff;

                                            } catch (ParseException e) {
                                                e.printStackTrace();

                                            }
                                        }
                                    }

                                    long Hours = diffHrs / (60 * 60 * 1000) ;
                                    long Minutes = diffHrs / (60 * 1000);

                                    workingDiff =diffHrs;

                                    int minutes = (int) ((diffHrs / (1000*60)) % 60);
                                    int hours   = (int) ((diffHrs / (1000*60*60)) % 24);
                                    int days   = (int) ((diffHrs / (1000*60*60*24)));
                                    DecimalFormat df = new DecimalFormat("00");

                                    String s= String.format("%02d", hours) +" hr "+String.format("%02d", minutes)+" mins";
                                    SpannableString ss1=  new SpannableString(s);
                                    ss1.setSpan(new RelativeSizeSpan(1f), 0,2, 0); // set size
                                    ss1.setSpan(new ForegroundColorSpan(Color.RED), 0, 2, 0);// set color
                                    ss1.setSpan(new RelativeSizeSpan(1f), 6,8, 0); // set size
                                    ss1.setSpan(new ForegroundColorSpan(Color.RED), 6, 8, 0);// set color
                                    mLogouText.setText(ss1);
                                    // mLogoutAddress.setVisibility(View.GONE);
                                }
                                //mWorkingHours.setText(ss1);

                                //mWorkingHours.setText(String.format("%02d", days)+":"+String.format("%02d", hours) +":"+String.format("%02d", minutes));

                            }else{

                                mStatus.setText("Absent");
                                mStatus.setBackgroundColor(Color.parseColor("#FF0000"));
                                mStatus.setVisibility(View.VISIBLE);
                                mLoginId.setText(0+"");
                                mLoginTime.setText("ABSENT");
                                mLoginTime.setTextColor(Color.parseColor("#FF0000"));
                                mLogoutTime.setText("ABSENT");
                                mLogoutTime.setTextColor(Color.parseColor("#FF0000"));
                                mLogouText.setText("");
                                mLogoutAddress.setText("");
                                mLoginText.setText("");
                                mLoginAddress.setText("");
                              /*  mLogoutTime.setTextColor(Color.parseColor("#FF0000"));
                                mLogoutTime.setText("ABSENT");
                                mWorkingHours.setTextColor(Color.parseColor("#FF0000"));
                                mWorkingHours.setText("ABSENT");*/


                                // Toast.makeText(DailyTargetsForEmployeeActivity.this, "No Tasks given for this employee ", Toast.LENGTH_SHORT).show();
                            }

                        }else {


                            mLoginTime.setText("ABSENT");
                            mLoginTime.setTextColor(Color.parseColor("#FF0000"));
                            mLogoutTime.setText("ABSENT");
                            mLogoutTime.setTextColor(Color.parseColor("#FF0000"));
                            mLogouText.setText("");
                            mLogoutAddress.setText("");
                            mLoginText.setText("");
                            mLoginAddress.setText("");

                            loginTrue = true;
                            //Toast.makeText(DailyTargetsForEmployeeActivity.this, "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<LoginDetails>> call, Throwable t) {
                        // Log error here since request failed
                       /* if (progressDialog!=null)
                            progressDialog.dismiss();*/
                        mLoginTime.setText("ABSENT");
                        mLoginTime.setTextColor(Color.parseColor("#FF0000"));
                        mLogoutTime.setText("ABSENT");
                        mLogoutTime.setTextColor(Color.parseColor("#FF0000"));
                        mLogouText.setText("");
                        mLogoutAddress.setText("");
                        mLoginText.setText("");
                        mLoginAddress.setText("");
                        Log.e("TAG", t.toString());

                        loginTrue = true;
                    }
                });
            }
        });
    }

    private String getAddress(LatLng latLng) {
        Geocoder geocoder = new Geocoder(EmployeeDashBoardAdminView.this, Locale.getDefault());
        String result = null;
        try {
            List<Address> addressList = geocoder.getFromLocation(
                    latLng.latitude, latLng.longitude, 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    sb.append(address.getAddressLine(i)).append(",");
                }

                result = address.getAddressLine(0);



                return result;
            }
            return result;
        } catch (IOException e) {
            Log.e("MapLocation", "Unable connect to Geocoder", e);
            return result;
        }

    }

    private void getMeetingsDetails(final Meetings loginDetails,final String comDate){

        meetDiff = 0;

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

                            employeeMeetings = new ArrayList<>();
                            pendingMeetings = new ArrayList<>();
                            completedMeetings = new ArrayList<>();
                            closedMeetings = new ArrayList<>();

                            dayemployeeMeetings = new ArrayList<>();
                            daypendingMeetings = new ArrayList<>();
                            daycompletedMeetings = new ArrayList<>();
                            dayclosedMeetings = new ArrayList<>();

                            daytotalMeetings=0;
                            daypendingMeeting=0;
                            daycompleteMeetings=0;
                            dayclosedMeeting=0;


                            if (list !=null && list.size()!=0) {

                                long diffHrs = 0;
                                mTotalMeetings.setText(""+list.size());

                                for (Meetings lg:list) {

                                    employeeMeetings.add(lg);
                                    dayemployeeMeetings.add(lg);


                                    if(lg.getStartTime()!=null&&lg.getEndTime()!=null){

                                        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy hh:mm a");
                                        SimpleDateFormat sdfs = new SimpleDateFormat("MMM dd,yyyy");

                                        Date fd=null,td=null;

                                        String logoutT = lg.getEndTime();
                                        String loginT = lg.getStartTime();

                                        if(loginT==null||loginT.isEmpty()){

                                            loginT = comDate +" 00:00 am";
                                        }

                                        if(logoutT==null||logoutT.isEmpty()){

                                            logoutT = comDate  +" "+new SimpleDateFormat("hh:mm a").format(new Date()) ;
                                        }

                                        try {
                                            fd = sdf.parse(""+loginT);
                                            td = sdf.parse(""+logoutT);

                                            long diff = td.getTime() - fd.getTime();
                                            diffHrs = diffHrs+diff;

                                        } catch (ParseException e) {
                                            e.printStackTrace();

                                        }
                                    }

                                    if(lg.getStatus()!=null&&lg.getStatus().equalsIgnoreCase("Completed")){

                                        completedMeetings.add(lg);
                                        daycompletedMeetings.add(lg);
                                    }else if(lg.getStatus()!=null&&lg.getStatus().equalsIgnoreCase("In Meeting")){

                                        pendingMeetings.add(lg);
                                        daypendingMeetings.add(lg);
                                    }else if(lg.getStatus()!=null&&lg.getStatus().equalsIgnoreCase("Closed")){

                                        closedMeetings.add(lg);
                                        dayclosedMeetings.add(lg);
                                    }
                                }

                                if(dayemployeeMeetings!=null&&dayemployeeMeetings.size()!=0){
                                    mNoMeeting.setVisibility(View.GONE);
                                    mMeetingList.setVisibility(View.VISIBLE);
                                    mMeetingList.removeAllViews();

                                    if(dayemployeeMeetings.size()>2){
                                        ArrayList<Meetings> twoArray = new ArrayList<>();
                                        twoArray.add(dayemployeeMeetings.get(0));
                                        twoArray.add(dayemployeeMeetings.get(1));
                                        MeetingDetailAdapter adapter = new MeetingDetailAdapter(EmployeeDashBoardAdminView.this,twoArray);
                                        mMeetingList.setAdapter(adapter);
                                        mMeetingRead.setVisibility(View.VISIBLE);
                                    }else{
                                        mMeetingRead.setVisibility(View.GONE);
                                        MeetingDetailAdapter adapter = new MeetingDetailAdapter(EmployeeDashBoardAdminView.this,dayemployeeMeetings);
                                        mMeetingList.setAdapter(adapter);
                                    }
                                    /*totalTargets.setText(""+daytotal);
                                    openTargets.setText(""+daypending);
                                    closedTargets.setText(""+daycomplete);
                                    movedTargets.setText(""+dayclosed);*/
                                }else{

                                    mNoMeeting.setVisibility(View.VISIBLE);
                                    mMeetingList.setVisibility(View.GONE);
                                    mMeetingRead.setVisibility(View.GONE);
                                }

                                int minutes = (int) ((diffHrs / (1000*60)) % 60);
                                int hours   = (int) ((diffHrs / (1000*60*60)) % 24);
                                meetDiff = diffHrs;

                                DecimalFormat df = new DecimalFormat("00");

                                String s= String.format("%02d", hours) +" hr "+String.format("%02d", minutes)+" mins";
                                SpannableString ss1=  new SpannableString(s);
                                ss1.setSpan(new RelativeSizeSpan(1f), 0,2, 0); // set size
                                ss1.setSpan(new ForegroundColorSpan(Color.RED), 0, 2, 0);// set color
                                ss1.setSpan(new RelativeSizeSpan(1f), 6,8, 0); // set size
                                ss1.setSpan(new ForegroundColorSpan(Color.RED), 6, 8, 0);// set color
                                mMetingAdrz.setText(ss1);
                                mMeetingTime.setText("Total Meeting Time");
                                long avgMeetingdiff = diffHrs/list.size();
                                int avgminutes = (int) ((avgMeetingdiff / (1000*60)) % 60);
                                int avghours   = (int) ((avgMeetingdiff / (1000*60*60)) % 24);

                                String as= String.format("%02d", avghours) +" hr "+String.format("%02d", avgminutes)+" mins";
                                SpannableString ss1a=  new SpannableString(as);
                                ss1a.setSpan(new RelativeSizeSpan(1f), 0,2, 0); // set size
                                ss1a.setSpan(new ForegroundColorSpan(Color.RED), 0, 2, 0);// set color
                                ss1a.setSpan(new RelativeSizeSpan(1f), 6,8, 0); // set size
                                ss1a.setSpan(new ForegroundColorSpan(Color.RED), 6, 8, 0);// set color
                                mAvgMeetingTime.setText(ss1a);


                            }else{
                                mMeetingTime.setText("No Meetings");
                                mMetingAdrz.setText("");
                                mTotalMeetings.setText("0");
                                mNoMeeting.setVisibility(View.VISIBLE);
                                mMeetingList.setVisibility(View.GONE);
                                mMeetingRead.setVisibility(View.GONE);

                                String as= String.format("%02d", 00) +" hr "+String.format("%02d", 00)+" mins";
                                SpannableString ss1a=  new SpannableString(as);
                                ss1a.setSpan(new RelativeSizeSpan(1f), 0,2, 0); // set size
                                ss1a.setSpan(new ForegroundColorSpan(Color.RED), 0, 2, 0);// set color
                                ss1a.setSpan(new RelativeSizeSpan(1f), 6,8, 0); // set size
                                ss1a.setSpan(new ForegroundColorSpan(Color.RED), 6, 8, 0);// set color
                                mAvgMeetingTime.setText(ss1a);

                            }

                        }else {

                            mNoMeeting.setVisibility(View.VISIBLE);
                            mMeetingList.setVisibility(View.GONE);
                            mMeetingRead.setVisibility(View.GONE);

                            String as= String.format("%02d", 00) +" hr "+String.format("%02d", 00)+" mins";
                            SpannableString ss1a=  new SpannableString(as);
                            ss1a.setSpan(new RelativeSizeSpan(1f), 0,2, 0); // set size
                            ss1a.setSpan(new ForegroundColorSpan(Color.RED), 0, 2, 0);// set color
                            ss1a.setSpan(new RelativeSizeSpan(1f), 6,8, 0); // set size
                            ss1a.setSpan(new ForegroundColorSpan(Color.RED), 6, 8, 0);// set color
                            mAvgMeetingTime.setText(ss1a);
                            //Toast.makeText(DailyTargetsForEmployeeActivity.this, "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Meetings>> call, Throwable t) {
                        // Log error here since request failed
                       /* if (progressDialog!=null)
                            progressDialog.dismiss();*/
                        Log.e("TAG", t.toString());
                        mNoMeeting.setVisibility(View.VISIBLE);
                        mMeetingList.setVisibility(View.GONE);
                        mMeetingRead.setVisibility(View.GONE);

                        String as= String.format("%02d", 00) +" hr "+String.format("%02d", 00)+" mins";
                        SpannableString ss1a=  new SpannableString(as);
                        ss1a.setSpan(new RelativeSizeSpan(1f), 0,2, 0); // set size
                        ss1a.setSpan(new ForegroundColorSpan(Color.RED), 0, 2, 0);// set color
                        ss1a.setSpan(new RelativeSizeSpan(1f), 6,8, 0); // set size
                        ss1a.setSpan(new ForegroundColorSpan(Color.RED), 6, 8, 0);// set color
                        mAvgMeetingTime.setText(ss1a);
                    }
                });
            }
        });
    }

    public void openDatePicker(final TextView tv) {
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
                                String dateValue = new SimpleDateFormat("yyyy-MM-dd").format(fdate);
                                //mTitle.setText(new SimpleDateFormat("dd-MM-yyyy").format(fdate)+" Summary");//dd-MM-yyyy
                                tv.setText(new SimpleDateFormat("dd-MM-yyyy").format(fdate)+"");

                                try{

                                    Date date=null;
                                    try {
                                        date = new SimpleDateFormat("yyyy-MM-dd").parse(dateValue);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    //Today Summary
                                    LoginDetails ld  = new LoginDetails();
                                    ld.setEmployeeId(employee.getEmployeeId());
                                    ld.setLoginDate(new SimpleDateFormat("MM/dd/yyyy").format(date));
                                    String logDate = new SimpleDateFormat("MMM dd,yyyy").format(date);
                                    getLoginDetailsDate(ld,logDate);

                                    Meetings md  = new Meetings();
                                    md.setEmployeeId(employee.getEmployeeId());
                                    md.setMeetingDate(new SimpleDateFormat("MM/dd/yyyy").format(date));
                                    String mdDate = new SimpleDateFormat("MMM dd,yyyy").format(date);
                                    getMeetingsDetails(md,mdDate);

                                    LiveTracking lv = new LiveTracking();
                                    lv.setEmployeeId(employee.getEmployeeId());
                                    lv.setTrackingDate(new SimpleDateFormat("MM/dd/yyyy").format(date));
                                    getLiveLocation(lv);
                                    //getLiveLocation(profile.getEmployeeId());

                                    getTasks(employee.getEmployeeId(),new SimpleDateFormat("yyyy-MM-dd").format(fdate));
                                    getExpense(employee.getEmployeeId(),new SimpleDateFormat("yyyy-MM-dd").format(fdate));

                                    valueCheck();

                                }catch (Exception e){
                                    e.printStackTrace();
                                    Intent error = new Intent(EmployeeDashBoardAdminView.this,InternalServerErrorScreen.class);
                                    startActivity(error);
                                }

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

    public void openDatePickers(final TextView tv,final String type) {
        // Get Current Date

        final Calendar c = Calendar.getInstance();
        int mYear  = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay   = c.get(Calendar.DAY_OF_MONTH);
        //launch datepicker modal
        DatePickerDialog datePickerDialog = new DatePickerDialog(EmployeeDashBoardAdminView.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Log.d("Date", "DATE SELECTED "+dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year,monthOfYear,dayOfMonth);
                        String date1 =  year+ "-" +(monthOfYear + 1)+"-"+ dayOfMonth  ;
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                        if (type!=null&&type.equalsIgnoreCase("allFromDate")){

                            try {
                                Date tdate = sdf.parse(date1);
                                String from = sdf.format(tdate);

                                tv.setText(from);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                        }else if (type!=null&&type.equalsIgnoreCase("allToDate")) {

                            try {
                                Date tdate = sdf.parse(date1);
                                String to = sdf.format(tdate);
                                tv.setText(to);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id)
        {
            case android.R.id.home:

                EmployeeDashBoardAdminView.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void valueCheck(){
        if(taslTrue&&loginTrue){

            //workingDiff/dayclosed;
            if(workingDiff==0||dayclosed==0){

                String as= String.format("%02d", 00) +" hr "+String.format("%02d", 00)+" mins";
                SpannableString ss1a=  new SpannableString(as);
                ss1a.setSpan(new RelativeSizeSpan(1f), 0,2, 0); // set size
                ss1a.setSpan(new ForegroundColorSpan(Color.RED), 0, 2, 0);// set color
                ss1a.setSpan(new RelativeSizeSpan(1f), 6,8, 0); // set size
                ss1a.setSpan(new ForegroundColorSpan(Color.RED), 6, 8, 0);// set color
                mAvgtaskTime.setText(ss1a);

            }else{

                long idleTime = workingDiff - (meetDiff);

                long avgTaskdiff = workingDiff/dayclosed;

                int avgminutes = (int) ((avgTaskdiff / (1000*60)) % 60);
                int avghours   = (int) ((avgTaskdiff / (1000*60*60)) % 24);

                String as= String.format("%02d", avghours) +" hr "+String.format("%02d", avgminutes)+" mins";
                SpannableString ss1a=  new SpannableString(as);
                ss1a.setSpan(new RelativeSizeSpan(1f), 0,2, 0); // set size
                ss1a.setSpan(new ForegroundColorSpan(Color.RED), 0, 2, 0);// set color
                ss1a.setSpan(new RelativeSizeSpan(1f), 6,8, 0); // set size
                ss1a.setSpan(new ForegroundColorSpan(Color.RED), 6, 8, 0);// set color
                mAvgtaskTime.setText(ss1a);

                if(avgTaskdiff!=0||idleTime<0){
                    String asi= String.format("%02d", 00) +" hr "+String.format("%02d", 00)+" mins";
                    SpannableString ss1i=  new SpannableString(asi);
                    ss1i.setSpan(new RelativeSizeSpan(1f), 0,2, 0); // set size
                    ss1i.setSpan(new ForegroundColorSpan(Color.RED), 0, 2, 0);// set color
                    ss1i.setSpan(new RelativeSizeSpan(1f), 6,8, 0); // set size
                    ss1i.setSpan(new ForegroundColorSpan(Color.RED), 6, 8, 0);// set color
                    mIdleTime.setText(ss1i);
                }else{

                    int avgminutesi = (int) ((idleTime / (1000*60)) % 60);
                    int avghoursi   = (int) ((idleTime / (1000*60*60)) % 24);

                    String asi= String.format("%02d", avghoursi) +" hr "+String.format("%02d", avgminutesi)+" mins";
                    SpannableString ss1i=  new SpannableString(asi);
                    ss1i.setSpan(new RelativeSizeSpan(1f), 0,2, 0); // set size
                    ss1i.setSpan(new ForegroundColorSpan(Color.RED), 0, 2, 0);// set color
                    ss1i.setSpan(new RelativeSizeSpan(1f), 6,8, 0); // set size
                    ss1i.setSpan(new ForegroundColorSpan(Color.RED), 6, 8, 0);// set color
                    mIdleTime.setText(ss1i);
                }
            }

        }else{

            h = new Handler();
            //1 second=1000 milisecond, 15*1000=15seconds
            h.postDelayed( runnable = new Runnable() {
                public void run() {
                    //do something
                    if(taslTrue&&loginTrue){

                        //workingDiff/dayclosed;
                        if(workingDiff==0||dayclosed==0){

                            String as= String.format("%02d", 00) +" hr "+String.format("%02d", 00)+" mins";
                            SpannableString ss1a=  new SpannableString(as);
                            ss1a.setSpan(new RelativeSizeSpan(1f), 0,2, 0); // set size
                            ss1a.setSpan(new ForegroundColorSpan(Color.RED), 0, 2, 0);// set color
                            ss1a.setSpan(new RelativeSizeSpan(1f), 6,8, 0); // set size
                            ss1a.setSpan(new ForegroundColorSpan(Color.RED), 6, 8, 0);// set color
                            mAvgtaskTime.setText(ss1a);

                            long idleTime = workingDiff - (meetDiff);

                            if(idleTime<0){
                                String asi= String.format("%02d", 00) +" hr "+String.format("%02d", 00)+" mins";
                                SpannableString ss1i=  new SpannableString(asi);
                                ss1i.setSpan(new RelativeSizeSpan(1f), 0,2, 0); // set size
                                ss1i.setSpan(new ForegroundColorSpan(Color.RED), 0, 2, 0);// set color
                                ss1i.setSpan(new RelativeSizeSpan(1f), 6,8, 0); // set size
                                ss1i.setSpan(new ForegroundColorSpan(Color.RED), 6, 8, 0);// set color
                                mIdleTime.setText(ss1i);
                            }else{

                                int avgminutesi = (int) ((idleTime / (1000*60)) % 60);
                                int avghoursi   = (int) ((idleTime / (1000*60*60)) % 24);

                                String asi= String.format("%02d", avghoursi) +" hr "+String.format("%02d", avgminutesi)+" mins";
                                SpannableString ss1i=  new SpannableString(asi);
                                ss1i.setSpan(new RelativeSizeSpan(1f), 0,2, 0); // set size
                                ss1i.setSpan(new ForegroundColorSpan(Color.RED), 0, 2, 0);// set color
                                ss1i.setSpan(new RelativeSizeSpan(1f), 6,8, 0); // set size
                                ss1i.setSpan(new ForegroundColorSpan(Color.RED), 6, 8, 0);// set color
                                mIdleTime.setText(ss1i);
                            }

                        }else{

                            long idleTime = workingDiff - (meetDiff);

                            long avgTaskdiff = workingDiff/dayclosed;

                            int avgminutes = (int) ((avgTaskdiff / (1000*60)) % 60);
                            int avghours   = (int) ((avgTaskdiff / (1000*60*60)) % 24);

                            String as= String.format("%02d", avghours) +" hr "+String.format("%02d", avgminutes)+" mins";
                            SpannableString ss1a=  new SpannableString(as);
                            ss1a.setSpan(new RelativeSizeSpan(1f), 0,2, 0); // set size
                            ss1a.setSpan(new ForegroundColorSpan(Color.RED), 0, 2, 0);// set color
                            ss1a.setSpan(new RelativeSizeSpan(1f), 6,8, 0); // set size
                            ss1a.setSpan(new ForegroundColorSpan(Color.RED), 6, 8, 0);// set color
                            mAvgtaskTime.setText(ss1a);

                            if(avgTaskdiff!=0||idleTime<0){
                                String asi= String.format("%02d", 00) +" hr "+String.format("%02d", 00)+" mins";
                                SpannableString ss1i=  new SpannableString(asi);
                                ss1i.setSpan(new RelativeSizeSpan(1f), 0,2, 0); // set size
                                ss1i.setSpan(new ForegroundColorSpan(Color.RED), 0, 2, 0);// set color
                                ss1i.setSpan(new RelativeSizeSpan(1f), 6,8, 0); // set size
                                ss1i.setSpan(new ForegroundColorSpan(Color.RED), 6, 8, 0);// set color
                                mIdleTime.setText(ss1i);
                            }else{

                                int avgminutesi = (int) ((idleTime / (1000*60)) % 60);
                                int avghoursi   = (int) ((idleTime / (1000*60*60)) % 24);

                                String asi= String.format("%02d", avghoursi) +" hr "+String.format("%02d", avgminutesi)+" mins";
                                SpannableString ss1i=  new SpannableString(asi);
                                ss1i.setSpan(new RelativeSizeSpan(1f), 0,2, 0); // set size
                                ss1i.setSpan(new ForegroundColorSpan(Color.RED), 0, 2, 0);// set color
                                ss1i.setSpan(new RelativeSizeSpan(1f), 6,8, 0); // set size
                                ss1i.setSpan(new ForegroundColorSpan(Color.RED), 6, 8, 0);// set color
                                mIdleTime.setText(ss1i);
                            }
                        }

                        taslTrue = false;
                        loginTrue = false;

                    }
                    h.postDelayed(runnable, delay);
                }
            }, delay);

        }
    }


    public void getLoginDetailsById(final int id,final String status,final TextView statusText){

        final ProgressDialog dialog = new ProgressDialog(EmployeeDashBoardAdminView.this);
        dialog.setMessage("Saving Details..");
        dialog.setCancelable(false);
        dialog.show();


        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {

                final LoginDetailsAPI subCategoryAPI = Util.getClient().create(LoginDetailsAPI.class);
                Call<LoginDetails> getProf = subCategoryAPI.getLoginById(id);
                //Call<ArrayList<Blogs>> getBlog = blogApi.getBlogs();

                getProf.enqueue(new Callback<LoginDetails>() {

                    @Override
                    public void onResponse(Call<LoginDetails> call, Response<LoginDetails> response) {

                        if(dialog != null && dialog.isShowing())
                        {
                            dialog.dismiss();
                        }

                        if (response.code() == 200||response.code() == 201||response.code() == 204)
                        {
                            System.out.println("Inside api");

                            final LoginDetails dto = response.body();

                            if(dto!=null){

                                try {

                                    LoginDetails loginDetails = dto;
                                    loginDetails.setTotalMeeting(status);
                                    updateLogin(loginDetails,statusText);

                                }
                                catch (Exception ex)
                                {
                                    ex.printStackTrace();
                                }
                            }

                        }else{

                        }
                    }

                    @Override
                    public void onFailure(Call<LoginDetails> call, Throwable t) {

                        if(dialog != null && dialog.isShowing())
                        {
                            dialog.dismiss();
                        }
                    }
                });
            }
        });
    }

    public void updateLogin(final LoginDetails loginDetails,final TextView statusText) {

        final ProgressDialog dialog = new ProgressDialog(EmployeeDashBoardAdminView.this);
        dialog.setMessage("Saving Details..");
        dialog.setCancelable(false);
        dialog.show();

        LoginDetailsAPI apiService = Util.getClient().create(LoginDetailsAPI.class);
        Call<LoginDetails> call = apiService.updateLoginById(loginDetails.getLoginDetailsId(),loginDetails);
        call.enqueue(new Callback<LoginDetails>() {
            @Override
            public void onResponse(Call<LoginDetails> call, Response<LoginDetails> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {
                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201||response.code()==204) {

                        statusText.setText(loginDetails.getTotalMeeting()+"");

                        if(loginDetails.getTotalMeeting().equalsIgnoreCase("Present")){
                            statusText.setBackgroundColor(Color.parseColor("#00FF00"));
                        }else{
                            statusText.setBackgroundColor(Color.parseColor("#FF0000"));
                        }

                    }else {
                        Toast.makeText(getApplicationContext(), "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
            public void onFailure(Call<LoginDetails> call, Throwable t) {

                if(dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }

                Toast.makeText( getApplicationContext( ) , "Failed Due to Bad Internet Connection" , Toast.LENGTH_SHORT ).show( );
                Log.e("TAG", t.toString());
            }
        });
    }

    public void addLogin(final LoginDetails loginDetails,final TextView loginId,final TextView status) {
        final ProgressDialog dialog = new ProgressDialog(EmployeeDashBoardAdminView.this);
        dialog.setMessage("Saving Details..");
        dialog.setCancelable(false);
        dialog.show();

        LoginDetailsAPI apiService = Util.getClient().create(LoginDetailsAPI.class);
        Call<LoginDetails> call = apiService.addLogin(loginDetails);
        call.enqueue(new Callback<LoginDetails>() {
            @Override
            public void onResponse(Call<LoginDetails> call, Response<LoginDetails> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {
                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201) {

                        LoginDetails s = response.body();

                        if(s!=null){

                            status.setText("Present");
                            loginId.setText(""+s.getLoginDetailsId());
                            status.setBackgroundColor(Color.parseColor("#00FF00"));
                        }

                    }else {
                        Toast.makeText(getApplicationContext(), "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
            public void onFailure(Call<LoginDetails> call, Throwable t) {

                if(dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
                Toast.makeText( getApplicationContext( ) , "Failed Due to Bad Internet Connection" , Toast.LENGTH_SHORT ).show( );
                Log.e("TAG", t.toString());
            }
        });
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
}