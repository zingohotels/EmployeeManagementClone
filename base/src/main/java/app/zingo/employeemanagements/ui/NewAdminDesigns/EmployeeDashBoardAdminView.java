package app.zingo.employeemanagements.ui.NewAdminDesigns;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import app.zingo.employeemanagements.base.R;
import app.zingo.employeemanagements.adapter.MeetingDetailAdapter;
import app.zingo.employeemanagements.adapter.TaskListAdapter;
import app.zingo.employeemanagements.Custom.MyRegulerText;
import app.zingo.employeemanagements.model.Employee;
import app.zingo.employeemanagements.model.EmployeeImages;
import app.zingo.employeemanagements.model.Expenses;
import app.zingo.employeemanagements.model.LiveTracking;
import app.zingo.employeemanagements.model.LoginDetails;
import app.zingo.employeemanagements.model.Meetings;
import app.zingo.employeemanagements.model.Tasks;
import app.zingo.employeemanagements.ui.landing.InternalServerErrorScreen;
import app.zingo.employeemanagements.ui.newemployeedesign.MeetingDetailList;
import app.zingo.employeemanagements.utils.BaseActivity;
import app.zingo.employeemanagements.utils.Common;
import app.zingo.employeemanagements.utils.PreferenceHandler;
import app.zingo.employeemanagements.utils.ProgressBarUtil;
import app.zingo.employeemanagements.utils.Util;
import app.zingo.employeemanagements.WebApi.ExpensesApi;
import app.zingo.employeemanagements.WebApi.LiveTrackingAPI;
import app.zingo.employeemanagements.WebApi.LoginDetailsAPI;
import app.zingo.employeemanagements.WebApi.MeetingsAPI;
import app.zingo.employeemanagements.WebApi.TasksAPI;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmployeeDashBoardAdminView extends BaseActivity {
    Employee employee;
    //  MyRegulerText mWorkedDays,mCompletedtasks,mTotalMeetings,mTotalExpenses;
    TextView mWorkedDays,mCompletedtasks,mTotalMeetings,mEmployeeName,mDate,mStatus,mLoginId;
    MyRegulerText mLoginTime,mLoginText,mLoginAddress,mLogoutTime,mLogouText,mLoginTimeText,mLogoutTimeText,
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
    ArrayList<Tasks> OnGoingTask;

    ArrayList< Meetings > employeeMeetings;
    ArrayList< Meetings > pendingMeetings ;
    ArrayList< Meetings > completedMeetings ;
    ArrayList< Meetings > closedMeetings ;

    ArrayList< Meetings > dayemployeeMeetings;
    ArrayList< Meetings > daypendingMeetings ;
    ArrayList< Meetings > daycompletedMeetings ;
    ArrayList< Meetings > dayclosedMeetings ;

    int total=0,pending=0,complete=0,closed=0;
    int daytotal=0,daypending=0,daycomplete=0,dayclosed=0;

    int totalMeeting=0,pendingMeeting=0,completeMeetings=0,closedMeeting=0;
    int daytotalMeetings=0,daypendingMeeting=0,daycompleteMeetings=0,dayclosedMeeting=0;

    SimpleDateFormat dateFormat;
    private ProgressBarUtil progressBarUtil;
    long meetDiff=0,workingDiff = 0,diffHrs = 0,taskTimeDiff = 0;
    TasksAPI mTaskService = Common.getTaskAPI ();
    LoginDetailsAPI mLoginDetailsService = Common.getLoginDetailsAPI ();
    MeetingsAPI mMeetingsService = Common.getMeetingsAPI ();
    LiveTrackingAPI mLiveLocationsService = Common.getLiveTrackingAPI ();
    ExpensesApi mExpensesService = Common.getExpensesAPI ();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_dash_board_admin_view);
        initViews();
    }

    @SuppressLint ("SimpleDateFormat")
    private void initViews ( ) {
        try{
            Objects.requireNonNull ( getSupportActionBar ( ) ).setHomeButtonEnabled(true);
            Objects.requireNonNull ( getSupportActionBar ( ) ).setDisplayHomeAsUpEnabled(true);
            progressBarUtil = new ProgressBarUtil ( this );
            mWorkedDays = findViewById( R.id.worked_days_count);
            mCompletedtasks = findViewById(R.id.completed_task_count_text);
            mTotalMeetings = findViewById(R.id.meeting_count_text);
            mEmployeeName = findViewById(R.id.name);
            mProfilePic = findViewById(R.id.employee_pic);
            //mDay = (CustomSpinner) findViewById(R.id.spinner);
            // mTotalExpenses = (MyRegulerText)findViewById(R.id.expense_count_text);
            mTitle = findViewById( R.id.atten_title);
            mLoginTime = findViewById(R.id.login_time);
            mLoginText = findViewById(R.id.logged_text);
            mLoginTimeText = findViewById(R.id.login_time_text);
            mLogoutTimeText = findViewById(R.id.logout_time_text);
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
            mTaskList.setLayoutManager(new LinearLayoutManager (this));
            mNoRecord = findViewById(R.id.noRecordFound);

            mMeetingList = findViewById(R.id.targetList_meeting);
            mMeetingList.setLayoutManager(new LinearLayoutManager(this));
            mNoMeeting = findViewById(R.id.noRecordFound_meetings);
            setTitle("Dash Board");

            Bundle bundle = getIntent().getExtras();
            if(bundle!=null){
                employee = (Employee)bundle.getSerializable("Profile");
            }

            if(PreferenceHandler.getInstance( EmployeeDashBoardAdminView.this).getUserRoleUniqueID()==2||PreferenceHandler.getInstance( EmployeeDashBoardAdminView.this).getUserRoleUniqueID()==9){

            }else{
                mStatus.setEnabled(false);
            }

            mStatus.setOnClickListener( v -> {
                try {
                    AlertDialog.Builder builder = new AlertDialog.Builder( EmployeeDashBoardAdminView.this);
                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View views = inflater.inflate(R.layout.alert_update_login, null);
                    builder.setView(views);
                    final Spinner mTask = views.findViewById(R.id.task_status_update);
                    final Button mSave = views.findViewById(R.id.save);
                    final AlertDialog dialogs = builder.create();
                    dialogs.show();
                    dialogs.setCanceledOnTouchOutside(true);
                    if (mStatus.getText().toString().equalsIgnoreCase("Absent")) {
                        mTask.setSelection(0);
                        //mTask.setEnabled(false);
                    } else {
                        mTask.setSelection(1);
                    }
                    mSave.setOnClickListener( v1 -> {
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
                                LoginDetails loginDetails = new LoginDetails ();
                                loginDetails.setEmployeeId(employee.getEmployeeId());
                                loginDetails.setLatitude("" + PreferenceHandler.getInstance(getApplicationContext()).getOrganizationLati());
                                loginDetails.setLongitude("" + PreferenceHandler.getInstance(getApplicationContext()).getOrganizationLongi());
                                LatLng master = new LatLng(Double.parseDouble(PreferenceHandler.getInstance(getApplicationContext()).getOrganizationLati()), Double.parseDouble(PreferenceHandler.getInstance(getApplicationContext()).getOrganizationLongi()));
                                String address = getAddress( master);
                                loginDetails.setLocation(String.valueOf (address));
                                //loginDetails.setStatus (status);
                                //loginDetails.setTotalMeeting (status);
                                loginDetails.setLoginTime(""+sdt.format(new Date()) );
                                loginDetails.setLoginDate(""+ sdf.format(new Date()));
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
                    } );

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } );

            String[] data = {"Filter By","Today", "Yesterday", "Date Picker"};
            ArrayAdapter adapter = new ArrayAdapter<>( EmployeeDashBoardAdminView.this, R.layout.spinner_item_selected, data);
            adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            mDate.setText(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
            mPrevious.setOnClickListener( view -> {
                try{
                    final Date date = dateFormat.parse(mDate.getText().toString());
                    final Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    calendar.add(Calendar.DAY_OF_YEAR, -1);
                    Date date2 = calendar.getTime();
                    mDate.setText(dateFormat.format(date2));
                    //Today Summary
                    LoginDetails ld  = new LoginDetails ();
                    ld.setEmployeeId(employee.getEmployeeId());
                    ld.setLoginDate(new SimpleDateFormat("MM/dd/yyyy").format(date2));
                    String logDate = new SimpleDateFormat("MMM dd,yyyy").format(date2);
                    getLoginDetailsDate(ld,logDate);

                    getTasks(employee.getEmployeeId(),new SimpleDateFormat("yyyy-MM-dd").format(date2));

                    Meetings md  = new Meetings ();
                    md.setEmployeeId(employee.getEmployeeId());
                    md.setMeetingDate(new SimpleDateFormat("MM/dd/yyyy").format(date2));
                    String mdDate = new SimpleDateFormat("MMM dd,yyyy").format(date2);
                    getMeetingsDetails(md,mdDate);

                    LiveTracking lv = new LiveTracking ();
                    lv.setEmployeeId(employee.getEmployeeId());
                    lv.setTrackingDate(new SimpleDateFormat("MM/dd/yyyy").format(date2));
                    getLiveLocation(lv);

                    getExpense(employee.getEmployeeId(),new SimpleDateFormat("yyyy-MM-dd").format(date2));
                    //getLiveLocation(profile.getEmployeeId());

                }catch (Exception e){
                    e.printStackTrace();
                }
            } );

            mNext.setOnClickListener( view -> {
                //dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                try{
                    final Date date = dateFormat.parse(mDate.getText().toString());
                    String newDate = dateFormat.format(new Date());
                    final Date newDates = dateFormat.parse(newDate);

                    if(newDates.getTime()<=date.getTime()){
                        Toast.makeText( EmployeeDashBoardAdminView.this, "Data will not available for future date", Toast.LENGTH_SHORT).show();

                    }else{
                        final Calendar calendar = Calendar.getInstance();
                        calendar.setTime(date);
                        calendar.add(Calendar.DAY_OF_YEAR, 1);
                        Date date2 = calendar.getTime();
                        mDate.setText(dateFormat.format(date2));
                        //Today Summary
                        LoginDetails ld  = new LoginDetails ();
                        ld.setEmployeeId(employee.getEmployeeId());
                        ld.setLoginDate(new SimpleDateFormat("MM/dd/yyyy").format(date2));
                        String logDate = new SimpleDateFormat("MMM dd,yyyy").format(date2);
                        getLoginDetailsDate(ld,logDate);

                        getTasks(employee.getEmployeeId(),new SimpleDateFormat("yyyy-MM-dd").format(date2));

                        Meetings md  = new Meetings ();
                        md.setEmployeeId(employee.getEmployeeId());
                        md.setMeetingDate(new SimpleDateFormat("MM/dd/yyyy").format(date2));
                        String mdDate = new SimpleDateFormat("MMM dd,yyyy").format(date2);
                        getMeetingsDetails(md,mdDate);

                        LiveTracking lv = new LiveTracking ();
                        lv.setEmployeeId(employee.getEmployeeId());
                        lv.setTrackingDate(new SimpleDateFormat("MM/dd/yyyy").format(date2));
                        getLiveLocation(lv);

                        getExpense(employee.getEmployeeId(),new SimpleDateFormat("yyyy-MM-dd").format(date2));

                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            } );

            mDate.setOnClickListener( view -> openDatePicker(mDate) );

            if(employee!=null){
                mEmployeeName.setText(""+employee.getEmployeeName());
                ArrayList< EmployeeImages > images = employee.getEmployeeImages();
                if(images!=null&&images.size()!=0){
                    EmployeeImages employeeImages = images.get(0);
                    if(employeeImages!=null){
                        String base=employeeImages.getImage();
                        if(base != null && !base.isEmpty()){
                            Picasso.with ( EmployeeDashBoardAdminView.this).load(base).placeholder(R.drawable.profile_image).error(R.drawable.profile_image).into(mProfilePic);
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

                    final Calendar calendar = Calendar.getInstance();
                    Date date2 = calendar.getTime();
                    //Today Summary
                    LoginDetails ld  = new LoginDetails ();
                    ld.setEmployeeId(employee.getEmployeeId());
                    ld.setLoginDate(new SimpleDateFormat("MM/dd/yyyy").format(date));
                    String logDate = new SimpleDateFormat("MMM dd,yyyy").format(date);
                    getLoginDetailsDate(ld,logDate);

                    getTasks(employee.getEmployeeId(),new SimpleDateFormat("yyyy-MM-dd").format(date2));

                    Meetings md  = new Meetings ();
                    md.setEmployeeId(employee.getEmployeeId());
                    md.setMeetingDate(new SimpleDateFormat("MM/dd/yyyy").format(date));
                    String mdDate = new SimpleDateFormat("MMM dd,yyyy").format(date);
                    getMeetingsDetails(md,mdDate);
                    //getLiveLocation(profile.getEmployeeId());
                    LiveTracking lv = new LiveTracking ();
                    lv.setEmployeeId(employee.getEmployeeId());
                    lv.setTrackingDate(new SimpleDateFormat("MM/dd/yyyy").format(date));
                    getLiveLocation(lv);

                    getExpense(employee.getEmployeeId(),new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

                }catch (Exception e){
                    e.printStackTrace();
                    Intent error = new Intent( EmployeeDashBoardAdminView.this, InternalServerErrorScreen.class);
                    startActivity(error);
                }
            }

            mLoginAddress.setOnClickListener( v -> {
                if(checkIn&&checkLogIn!=null){
                    Intent map = new Intent( EmployeeDashBoardAdminView.this, CheckInMapScreen.class);
                    Bundle bundle14 = new Bundle();
                    checkLogIn.setTitle(""+employee.getEmployeeName()+" - Check-In");
                    checkLogIn.setStatus(""+employee.getEmployeeName()+" - Check-In");
                    bundle14.putSerializable("Location",checkLogIn);
                    map.putExtras( bundle14 );
                    startActivity(map);
                }
            } );

            mLogoutAddress.setOnClickListener( v -> {
                if(checkout&&checkLogOut!=null){
                    Intent map = new Intent( EmployeeDashBoardAdminView.this,CheckInMapScreen.class);
                    Bundle bundle1 = new Bundle();
                    checkLogOut.setTitle(""+employee.getEmployeeName()+" - Check-Out");
                    checkLogOut.setStatus(""+employee.getEmployeeName()+" - Check-In");
                    bundle1.putSerializable("Location",checkLogOut);
                    map.putExtras( bundle1 );
                    startActivity(map);
                }
            } );

            mTaskREad.setOnClickListener( v -> {
                Intent intent = new Intent( EmployeeDashBoardAdminView.this, DailyTargetsForEmployeeActivity.class);
                Bundle bundle12 = new Bundle();
                bundle12.putSerializable("Profile",employee);
                bundle12.putInt("ProfileId",employee.getEmployeeId());
                intent.putExtras( bundle12 );
                startActivity(intent);
            } );

            mMeetingRead.setOnClickListener( v -> {
                Intent attnd = new Intent( EmployeeDashBoardAdminView.this, MeetingDetailList.class);
                Bundle bundle13 = new Bundle();
                bundle13.putSerializable("Profile",employee);
                bundle13.putInt("ProfileId",employee.getEmployeeId());
                attnd.putExtras( bundle13 );
                startActivity(attnd);
            } );

            mCompletedtasks.setOnClickListener( v -> {
                if(dayemployeeTasks!=null&&dayemployeeTasks.size()!=0){
                    /*Intent pending = new Intent(EmployeeDashBoardAdminView.this,PendingTasks.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("PendingTasks",dayemployeeTasks);
                    bundle.putString("Title","Task List");
                    pending.putExtras(bundle);
                    startActivity(pending);*/
                }
            } );

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /*APIS*/
    private void getTasks(final int employeeId,final String comDate) {
                mTaskService.getTasksByEmployeeIdRx (employeeId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<Tasks>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ArrayList<Tasks> result) {
                        if(result!=null) {
                            setTaskData ( result , comDate );
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println ("Error "+e.getMessage());
                        setTaskRecord ();
                        mAvgtaskTime.setText ( emptyTime ( diffHrs) );
                    }

                    @Override
                    public void onComplete() {
                        // Updates UI with data
                    }
                });
    }

    private void getExpense(final int employeeId, final String comDate){
                mExpensesService.getExpenseByEmployeeIdAndOrganizationIdRx (PreferenceHandler.getInstance ( this ).getCompanyId (),employeeId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<Expenses>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ArrayList<Expenses> result) {
                        setExpenseData(result, comDate);
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println ("Error "+e.getMessage());
                        setExpenseRecord();
                    }

                    @Override
                    public void onComplete() {
                        // Updates UI with data
                    }
                });

    }

    private void getLiveLocation(final LiveTracking lv) {
                mLiveLocationsService.getLiveTrackingByEmployeeIdAndDateRx (lv)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<LiveTracking>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ArrayList<LiveTracking> result) {
                        if(result!=null&&result.size ()!=0){
                            setLiveLocationStatus(setLiveLocationData(result));
                        }else{
                            setLiveLocationRecord ();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println ("Error "+e.getMessage());
                        setLiveLocationRecord();
                    }

                    @Override
                    public void onComplete() {
                        // Updates UI with data
                    }
                });
    }

    private void getLoginDetailsDate( final LoginDetails loginDetails, final String comDate){
                progressBarUtil.showProgress ( "Please Wait ..." );
                mLoginDetailsService.getLoginByEmployeeIdAndDateRx (loginDetails)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<LoginDetails>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ArrayList<LoginDetails> result) {
                        if(result!=null&&result.size ()!=0){
                            progressBarUtil.hideProgress ();
                            setLoginData(result,comDate);
                        }else {
                            setLoginRecord ("ABSENT");
                            mIdleTime.setText ( emptyTime ( 0 ) );
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        progressBarUtil.hideProgress ();
                        System.out.println ("Error "+e.getMessage());
                        setLoginRecord ( "" );
                    }

                    @Override
                    public void onComplete() {
                        progressBarUtil.hideProgress ();
                        // Updates UI with data
                    }
                });
    }

    private void getMeetingsDetails( final Meetings loginDetails, final String comDate){
            mMeetingsService.getMeetingsByEmployeeIdAndDateRx (loginDetails)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<Meetings>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ArrayList<Meetings> result) {
                        if(result!=null){
                            setMeetingsData (result,comDate);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println ("Error "+e.getMessage());
                        setMeetingdRecord ();
                    }

                    @Override
                    public void onComplete() {
                        // Updates UI with data
                    }
                });
    }

    /*SetRecords*/

    private void setMeetingdRecord ( ) {
        mNoMeeting.setVisibility(View.VISIBLE);
        mMeetingList.setVisibility(View.GONE);
        mMeetingRead.setVisibility(View.GONE);
    }

    private void setMeetingsData ( ArrayList < Meetings > list , String comDate ) {
        meetDiff = 0;
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
            for ( Meetings meetings:list) {
                employeeMeetings.add ( meetings );
                dayemployeeMeetings.add ( meetings );
                if(meetings.getStatus()!=null&&meetings.getStatus().equalsIgnoreCase("Completed")){
                    completedMeetings.add(meetings);
                    daycompletedMeetings.add(meetings);
                }else if(meetings.getStatus()!=null&&meetings.getStatus().equalsIgnoreCase("In Meeting")){
                    pendingMeetings.add(meetings);
                    daypendingMeetings.add(meetings);
                }else if(meetings.getStatus()!=null&&meetings.getStatus().equalsIgnoreCase("Closed")){
                    closedMeetings.add(meetings);
                    dayclosedMeetings.add(meetings);
                }
            }

            /*System.out.println ( "Suree MeetingTime :"+ workingTime ( meetingTimeDifference(list,comDate,diffHrs),"Meetings" ) );
            System.out.println ( "Suree Average MeetingTime:"+averageMeetingTime (list, meetingTimeDifference(list,comDate,diffHrs),"Meetings" ) );*/
            mMetingAdrz.setText(workingTime (meetDiff= meetingTimeDifference(list,comDate,diffHrs),"Meetings" ));
            mMeetingTime.setText("Total Meeting Time");
            mAvgMeetingTime.setText(averageMeetingTime (list, meetingTimeDifference(list,comDate,diffHrs),"Meetings" ));

            if(dayemployeeMeetings!=null&&dayemployeeMeetings.size()!=0){
                mNoMeeting.setVisibility(View.GONE);
                mMeetingList.setVisibility(View.VISIBLE);
                mMeetingList.removeAllViews();

                if(dayemployeeMeetings.size()>2){
                    ArrayList< Meetings > todaymeetings = new ArrayList<>();
                    todaymeetings.add(dayemployeeMeetings.get(0));
                    todaymeetings.add(dayemployeeMeetings.get(1));
                    MeetingDetailAdapter adapter = new MeetingDetailAdapter( EmployeeDashBoardAdminView.this,todaymeetings);
                    mMeetingList.setAdapter(adapter);
                    mMeetingRead.setVisibility(View.VISIBLE);
                }else{
                    mMeetingRead.setVisibility(View.GONE);
                    MeetingDetailAdapter adapter = new MeetingDetailAdapter( EmployeeDashBoardAdminView.this,dayemployeeMeetings);
                    mMeetingList.setAdapter(adapter);
                }
                /*totalTargets.setText(""+daytotal);
                 openTargets.setText(""+daypending);
                 closedTargets.setText(""+daycomplete);
                 movedTargets.setText(""+dayclosed);*/
            }else{
                setMeetingdRecord ();
            }

        }else{
            mMeetingTime.setText("No Meetings");
            mMetingAdrz.setText(emptyTime(meetDiff));
            mAvgMeetingTime.setText(emptyTime(meetDiff));
            mTotalMeetings.setText("0");
            setMeetingdRecord ();
        }
    }

    private void setTaskRecord ( ) {
        mTaskList.removeAllViews();
        mCompletedtasks.setText("0");
        mNoRecord.setVisibility(View.VISIBLE);
        mTaskList.setVisibility(View.GONE);
        mTaskREad.setVisibility(View.GONE);
    }

    @SuppressLint ("SimpleDateFormat")
    private void setTaskData ( ArrayList < Tasks > list , String comDate ) {
        diffHrs = 0;
        employeeTasks = new ArrayList<>();
        pendingTasks = new ArrayList<>();
        completedTasks = new ArrayList<>();
        closedTasks = new ArrayList<>();
        dayemployeeTasks = new ArrayList<>();
        daypendingTasks = new ArrayList<>();
        daycompletedTasks = new ArrayList<>();
        dayclosedTasks = new ArrayList<>();
        OnGoingTask = new ArrayList <> (  );

        daytotal=0;
        daypending=0;
        daycomplete=0;
        dayclosed=0;

        Date date = new Date();
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(comDate);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(list!=null&&list.size()!=0){
            for (Tasks task:list) {
                if(task.getStatus().equalsIgnoreCase("On-Going")&&(task.getStartDate()!=null&&task.getEndDate()!=null)){
                    OnGoingTask.add(task);
                }else if(task.getStatus().equalsIgnoreCase("Completed")&&(task.getStartDate()!=null&&task.getEndDate()!=null)){
                    completedTasks.add(task);
                }

                Date fromDate = null;
                Date toDate = null;

                if(task.getStartDate()!=null&&!task.getStartDate().isEmpty()){
                    if(task.getStartDate().contains("T")){
                        String dojs[] = task.getStartDate().split("T");

                        try {
                            fromDate = new SimpleDateFormat("yyyy-MM-dd").parse(dojs[0]);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }

                if(task.getEndDate()!=null&&!task.getEndDate().isEmpty()){
                    if(task.getEndDate().contains("T")){
                        String dojs[] = task.getEndDate().split("T");
                        try {
                            toDate = new SimpleDateFormat("yyyy-MM-dd").parse(dojs[0]);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }

                if(fromDate!=null&&toDate!=null){
                    if(date.getTime() >= fromDate.getTime() && date.getTime() <= toDate.getTime()){
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
                    ArrayList<Tasks> todayTasks = new ArrayList<>();
                    todayTasks.add(dayemployeeTasks.get(0));
                    todayTasks.add(dayemployeeTasks.get(1));
                    mAdapter = new TaskListAdapter( EmployeeDashBoardAdminView.this,todayTasks);
                    mTaskList.setAdapter(mAdapter);
                    mTaskREad.setVisibility(View.VISIBLE);
                }else{
                    mTaskREad.setVisibility(View.GONE);
                    mAdapter = new TaskListAdapter( EmployeeDashBoardAdminView.this,dayemployeeTasks);
                    mTaskList.setAdapter(mAdapter);
                }
            }else{
                setTaskRecord ();
            }
        }

        if(dayemployeeTasks.size ()!=0){
            setTaskStatus(averageTaskTime (dayemployeeTasks, taskTimeDifference(dayemployeeTasks,comDate,diffHrs),"Task" ),"Task");
        }else{
            mAvgtaskTime.setText ( emptyTime (diffHrs) );
        }
    }

    private void setTaskStatus ( SpannableString taskWorkingTime , String status ) {
        if(status.equalsIgnoreCase ( "Task" )){
            mAvgtaskTime.setText ( emptyTime (diffHrs));
        }else{
            mAvgtaskTime.setText ( emptyTime (diffHrs) );
        }
    }

    private void setLiveLocationRecord ( ) {
        mKmTravelled.setText("0 km/0 mile");
    }

    private void setLiveLocationStatus ( double distance ) {
        mKmTravelled.setText(kilometersAndMiles(metersToKms ( distance ),metersToMiles ( distance )));
    }

    private void setExpenseData ( ArrayList < Expenses > list , String dateValue ) {
        ArrayList< Expenses > todayTasks = new ArrayList<>();
        Date date = new Date();

        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(dateValue);

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (list !=null && list.size()!=0) {
            double amt = 0;

            for ( Expenses task:list) {
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
                    }
                }

                if(afromDate!=null){
                    if(date.getTime() == afromDate.getTime() ){
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
                setExpenseRecord();
            }

        }else{
            setExpenseRecord();
            // Toast.makeText(DailyTargetsForEmployeeActivity.this, "No Tasks given for this employee ", Toast.LENGTH_SHORT).show();
        }
    }

    private void setExpenseRecord ( ) {
        mExpAmt.setText("₹ 0");
        mExpeText.setText("No Expenses");
    }

    private void getMeetings ( ArrayList < LoginDetails > list ) {
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
    }

    private void setLoginData ( ArrayList<LoginDetails> list, String comDate) {
        String loginTime = list.get(0).getLoginTime();
        String logoutTime = list.get(list.size()-1).getLogOutTime();
        getMeetings(list);
        mLoginId.setText(list.get(0).getLoginDetailsId()+"");

        if(loginTime!=null&&!loginTime.isEmpty()){
            try{
                Date dateLog = new SimpleDateFormat("MMM dd,yyyy hh:mm a").parse(loginTime);
                mLoginTime.setText(""+new SimpleDateFormat("hh:mm a").format(dateLog));
                mLoginText.setText("Check-In Location");
                mLoginTimeText.setText("Check-In\n Time");
                LatLng latLng = new LatLng(Double.parseDouble(list.get(0).getLatitude()),Double.parseDouble(list.get(0).getLongitude()));
                String address = getAddress( latLng);
                mLoginAddress.setText(""+address);
                checkIn = true;
                checkLogIn = list.get(0);
            }catch (Exception e){
                e.printStackTrace();
            }

        }else{
            mLoginTime.setText("");
            mLoginAddress.setText("");
        }

        if(logoutTime!=null&&!logoutTime.isEmpty()){
            try{
                Date dateLog = new SimpleDateFormat("MMM dd,yyyy hh:mm a").parse(logoutTime);
                mLogoutTime.setText(""+new SimpleDateFormat("hh:mm a").format(dateLog));
                mLogouText.setText("Check-Out Location");
                mLogoutTimeText.setText("Check-Out\n Time");
                if(list.get ( list.size ()-1 ).getLogoutLatitude ()!=null&&list.get ( list.size ()-1 ).getLogoutLongitude ()!=null){
                    LatLng latLng = new LatLng(Double.parseDouble(list.get(list.size()-1).getLogoutLatitude ()),Double.parseDouble(list.get(list.size()-1).getLogoutLongitude ()));
                    String address = getAddress(latLng);
                    mLogoutAddress.setText(""+address);
                    checkLogOut = list.get(list.size()-1);
                    checkout = true;
                    setLoginStatus(workingTime(workingDiff = workingTimeDifference(list, loginTime,comDate,diffHrs),"Logout") , "Logout" );
                }else{
                    mLogoutAddress.setText("");
                    checkLogOut = list.get(list.size()-1);
                    checkout = true;
                    setLoginStatus(workingTime(workingDiff = workingTimeDifference(list, loginTime,comDate,diffHrs),"Logout") , "Logout" );
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            mLogoutAddress.setText("");
            setLoginStatus(workingTime(workingDiff = workingTimeDifference(list, loginTime,comDate,diffHrs),"Working"),"Working");
        }
    }

    private void setLoginStatus ( SpannableString workingTime , String loginStatus ) {
        if(loginStatus.equalsIgnoreCase ( "Working" )){
            mLogouText.setText ( workingTime );
            mIdleTime.setText(workingTime);
            mLogoutTimeText.setText ( "Working" );
            mLogoutTime.setText ( "Working" );
        }else if(loginStatus.equalsIgnoreCase ( "Logout" )){
            mIdleTime.setText(workingTime);
        }else {
            mIdleTime.setText ( emptyTime ( 0 ) );
        }
    }

    private void setLoginRecord ( String absent ) {
        if(!absent.isEmpty ()&&absent.equalsIgnoreCase ( "Absent" )){
            mStatus.setText("Absent");
            mStatus.setBackgroundColor(Color.parseColor("#FF0000"));
            mStatus.setVisibility(View.VISIBLE);
            mLoginId.setText(0+"");
            mLoginTime.setText("Absent");
            mLoginTime.setTextColor(Color.parseColor("#FF0000"));
            mLogoutTime.setText("Absent");
            mLogoutTime.setTextColor(Color.parseColor("#FF0000"));
            mLogouText.setText("");
            mLogoutTimeText.setText ("");
            mLogoutAddress.setText("");
            mLoginText.setText("");
            mLoginTimeText.setText ("");
            mLoginAddress.setText("");
        }else{
            mLoginTime.setText("Absent");
            mLoginTime.setTextColor(Color.parseColor("#FF0000"));
            mLogoutTime.setText("Absent");
            mLogoutTime.setTextColor(Color.parseColor("#FF0000"));
            mLogouText.setText("");
            mLogoutTimeText.setText ("");
            mLogoutAddress.setText("");
            mLoginText.setText("");
            mLoginTimeText.setText ("");
            mLoginAddress.setText("");
        }
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
                                    LoginDetails ld  = new LoginDetails ();
                                    ld.setEmployeeId(employee.getEmployeeId());
                                    ld.setLoginDate(new SimpleDateFormat("MM/dd/yyyy").format(date));
                                    String logDate = new SimpleDateFormat("MMM dd,yyyy").format(date);
                                    getLoginDetailsDate(ld,logDate);

                                    getTasks(employee.getEmployeeId(),new SimpleDateFormat("yyyy-MM-dd").format(fdate));

                                    Meetings md  = new Meetings ();
                                    md.setEmployeeId(employee.getEmployeeId());
                                    md.setMeetingDate(new SimpleDateFormat("MM/dd/yyyy").format(date));
                                    String mdDate = new SimpleDateFormat("MMM dd,yyyy").format(date);
                                    getMeetingsDetails(md,mdDate);

                                    LiveTracking lv = new LiveTracking ();
                                    lv.setEmployeeId(employee.getEmployeeId());
                                    lv.setTrackingDate(new SimpleDateFormat("MM/dd/yyyy").format(date));
                                    getLiveLocation(lv);
                                    //getLiveLocation(profile.getEmployeeId());

                                    getExpense(employee.getEmployeeId(),new SimpleDateFormat("yyyy-MM-dd").format(fdate));

                                }catch (Exception e){
                                    e.printStackTrace();
                                    Intent error = new Intent( EmployeeDashBoardAdminView.this, InternalServerErrorScreen.class);
                                    startActivity(error);
                                }

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                        catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }, mYear, mMonth, mDay);

        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if ( id == android.R.id.home ) {
            EmployeeDashBoardAdminView.this.finish ( );
        }
        return super.onOptionsItemSelected(item);
    }

    public void idealWorkingTime ( long workingDiff, long meetDiff , int dayclosed ){
        long idleTime = workingDiff - (meetDiff);
        if(workingDiff==0||dayclosed==0){
            String as= String.format("%02d", 00) +" hr "+String.format("%02d", 00)+" mins";
            SpannableString ss1a=  new SpannableString(as);
            ss1a.setSpan(new RelativeSizeSpan(1f), 0,2, 0); // set size
            ss1a.setSpan(new ForegroundColorSpan(Color.RED), 0, 2, 0);// set color
            ss1a.setSpan(new RelativeSizeSpan(1f), 6,8, 0); // set size
            ss1a.setSpan(new ForegroundColorSpan(Color.RED), 6, 8, 0);// set color
            mAvgtaskTime.setText(ss1a);                                                                                                     //AVERAGETASK

            if(idleTime<0){
                mIdleTime.setText(emptyTime ( workingDiff ));
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
            long avgTaskdiff = workingDiff/dayclosed;
            int avgminutes = (int) ((avgTaskdiff / (1000*60)) % 60);
            int avghours   = (int) ((avgTaskdiff / (1000*60*60)) % 24);
            String as= String.format("%02d", avghours) +" hr "+String.format("%02d", avgminutes)+" mins";
            SpannableString ss1a=  new SpannableString(as);
            ss1a.setSpan(new RelativeSizeSpan(1f), 0,2, 0); // set size
            ss1a.setSpan(new ForegroundColorSpan(Color.RED), 0, 2, 0);// set color                                              //AVERAGETASK
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
            }else if(avgTaskdiff!=0){
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
    }

    public void getLoginDetailsById(final int id,final String status,final TextView statusText){
        mLoginDetailsService.getLoginByIdRx (id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<LoginDetails>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(LoginDetails result) {
                        if(result!=null) {
                            setUpdateDataByLogindetails(result,status,statusText);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println ("Error "+e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        // Updates UI with data
                    }
                });
    }

    private void setUpdateDataByLogindetails ( LoginDetails result , String status , TextView statusText ) {
        try {
            if(status.equalsIgnoreCase ( "Absent" )){
                @SuppressLint ("SimpleDateFormat") SimpleDateFormat sdt = new SimpleDateFormat("MMM dd,yyyy hh:mm a");
                result.setLogoutLatitude ("" + PreferenceHandler.getInstance(getApplicationContext()).getOrganizationLati());
                result.setLogoutLongitude ("" + PreferenceHandler.getInstance(getApplicationContext()).getOrganizationLongi());
                LatLng master = new LatLng(Double.parseDouble(PreferenceHandler.getInstance(getApplicationContext()).getOrganizationLati()), Double.parseDouble(PreferenceHandler.getInstance(getApplicationContext()).getOrganizationLongi()));
                String address = getAddress(master);
                result.setLogoutLocation (String.valueOf (address));
                //result.setStatus (status);
                // result.setTotalMeeting ( status );
                result.setLogOutTime(""+sdt.format(new Date()) );
                updateLogin ( result , statusText );
            }

        } catch ( Exception ex ) {
            ex.printStackTrace ( );
        }
    }

    public void updateLogin( final LoginDetails loginDetails, final TextView statusText) {
        LoginDetailsAPI apiService = Util.getClient().create( LoginDetailsAPI.class);
        Call< LoginDetails > call = apiService.updateLoginById(loginDetails.getLoginDetailsId(),loginDetails);
        call.enqueue(new Callback< LoginDetails >() {
            @Override
            public void onResponse( Call< LoginDetails > call, Response< LoginDetails > response) {
                try {
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

                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure( Call< LoginDetails > call, Throwable t) {
                Toast.makeText( getApplicationContext( ) , "Failed Due to Bad Internet Connection" , Toast.LENGTH_SHORT ).show( );
                Log.e("TAG", t.toString());
            }
        });
    }

    public void addLogin( final LoginDetails loginDetails, final TextView loginId, final TextView status) {
        mLoginDetailsService.addLoginRx (loginDetails)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber< LoginDetails> () {
                    @Override
                    public void onSubscribe ( Subscription s ) {
                    }
                    @Override
                    public void onNext(LoginDetails result) {
                        //In Flowable onNext method not calling
                        /*if(result.getStatus ()!=null){
                            System.out.println ("Suree Next"  );
                            status.setText(result.getStatus ());
                            loginId.setText(""+result.getLoginDetailsId());
                            status.setBackgroundColor(Color.parseColor("#00FF00"));
                        }*/
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println ("Errors "+e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        // Updates UI with data
                        System.out.println ("Suree onComplete"  );
                    }
                });
    }
}
