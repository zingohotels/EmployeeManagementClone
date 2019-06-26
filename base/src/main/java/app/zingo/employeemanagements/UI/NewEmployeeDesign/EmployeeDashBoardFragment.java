package app.zingo.employeemanagements.UI.NewEmployeeDesign;


import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
import app.zingo.employeemanagements.Model.Customer;
import app.zingo.employeemanagements.Model.Employee;
import app.zingo.employeemanagements.Model.Leaves;
import app.zingo.employeemanagements.Model.LoginDetails;
import app.zingo.employeemanagements.Model.MeetingDetailsNotificationManagers;
import app.zingo.employeemanagements.Model.Meetings;
import app.zingo.employeemanagements.Model.Tasks;
import app.zingo.employeemanagements.UI.NewAdminDesigns.PendingTasks;
import app.zingo.employeemanagements.Utils.Constants;
import app.zingo.employeemanagements.Utils.PreferenceHandler;
import app.zingo.employeemanagements.Utils.ThreadExecuter;
import app.zingo.employeemanagements.Utils.TrackGPS;
import app.zingo.employeemanagements.Utils.Util;
import app.zingo.employeemanagements.WebApi.EmployeeApi;
import app.zingo.employeemanagements.WebApi.LeaveAPI;
import app.zingo.employeemanagements.WebApi.LoginDetailsAPI;
import app.zingo.employeemanagements.WebApi.MeetingNotificationAPI;
import app.zingo.employeemanagements.WebApi.MeetingsAPI;
import app.zingo.employeemanagements.WebApi.TasksAPI;
import app.zingo.employeemanagements.WebApi.UploadApi;
import app.zingo.employeemanagements.base.R;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static android.text.TextUtils.isEmpty;

/**
 * A simple {@link Fragment} subclass.
 */
public class EmployeeDashBoardFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    final String TAG = "Employer Dash";
    View layout;
    RecyclerView mTaskList,mMeetingList;
    TextView mLoggedTime,mMeeting;
    LinearLayout mTodayTaskLayout,mPendingLayout;
    private TaskListAdapter mAdapter;
    MyRegulerText mWorkedDays,mLeaveDays,mOnTask,mPending;

    Context mContext;



    int onTasks=0,pendingTask=0;



    ArrayList<Tasks> employeeTasks;
    ArrayList<Tasks> pendingTasks ;
    ArrayList<Tasks> completedTasks ;
    ArrayList<Tasks> closedTasks ;
    ArrayList<Tasks> onTask ;

    int monthDate = -1;

    //
    ImageView mImageView;
    File file;
    Dialog dialogs;
    LinearLayout mContent;
    View view;
    signature mSignature;
    Button  mClear, mGetSigns, mCancel;
    Bitmap bitmap;

    // Creating Separate Directory for saving Generated Images
    String DIRECTORY = Environment.getExternalStorageDirectory().getPath() + "/Krony Apps/";
    String pic_name = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
    String StoredPath = DIRECTORY + pic_name + ".png";
    String StoredPathSelfie = DIRECTORY + pic_name+"selfie" + ".png";

    static final int REQUEST_IMAGE_CAPTURE = 1;



    private GoogleMap mMap;

    private GoogleApiClient mLocationClient;
    Location currentLocation;

    //Location
    TrackGPS gps;
    double latitude, longitude;

    String  planType = "";


    //Google Place API
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 60000;  /* 60 secs */
    private long FASTEST_INTERVAL = 30000; /* 30 secs */





    Meetings loginDetails;
    MeetingDetailsNotificationManagers md;
    boolean methodAdd = false;


    boolean firstTime = true;
    boolean firstChck = true;

    ArrayList<Customer> customerArrayList;
    Spinner customerSpinner;
    LinearLayout ClientNameLayout;
    int clientId = 0;



    public EmployeeDashBoardFragment() {
        // Required empty public constructor
    }

    public static EmployeeDashBoardFragment getInstance() {
        return new EmployeeDashBoardFragment();
    }

    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        // GAUtil.trackScreen(getActivity(), "Employer Dashboard");
    }


    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        super.onCreateView(layoutInflater, viewGroup, savedInstanceState);
        try{
            this.layout = layoutInflater.inflate(R.layout.fragment_employee_dash_board, viewGroup, false);

            mContext = getContext();
            gps = new TrackGPS(getActivity());

            mTaskList = layout.findViewById(R.id.task_list_dash);
            mLoggedTime = layout.findViewById(R.id.log_out_time);
            mMeeting = layout.findViewById(R.id.meeting_info);
            mTodayTaskLayout = layout.findViewById(R.id.today_task_list);
            mTodayTaskLayout.setVisibility(View.GONE);
            mWorkedDays = layout.findViewById(R.id.worked_days_count);
            mLeaveDays = layout.findViewById(R.id.leave_days_count);
            mOnTask = layout.findViewById(R.id.on_task_count_text);
            mPendingLayout = layout.findViewById(R.id.pending_task_layout);
            mPending = layout.findViewById(R.id.pending_task_text);
            mMeetingList = layout.findViewById(R.id.meetingList);
            mMeetingList.setLayoutManager(new LinearLayoutManager(getActivity()));



            String meetingStatus = PreferenceHandler.getInstance(getActivity()).getMeetingLoginStatus();

            if (meetingStatus != null && meetingStatus.equalsIgnoreCase("Login")) {
                mMeeting.setVisibility(View.VISIBLE);
                mMeeting.setText("On-Going Meeting");
                getMeetingss(PreferenceHandler.getInstance(getActivity()).getMeetingId());
            }


            getEmployees();

            if (mLocationClient == null) {

                mLocationClient = new GoogleApiClient.Builder(this.getContext())
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();
            }


            mPendingLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(pendingTasks!=null&&pendingTasks.size()!=0){
                        Intent pending = new Intent(getActivity(), PendingTasks.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("PendingTasksNormal",pendingTasks);
                        pending.putExtras(bundle);
                        startActivity(pending);
                    }

                }
            });


            return this.layout;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public void onResume() {
        super.onResume();
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
    }

    private void getEmployees(){


        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {
                EmployeeApi apiService = Util.getClient().create(EmployeeApi.class);
                Call<ArrayList<Employee>> call = apiService.getProfileById(PreferenceHandler.getInstance(getActivity()).getUserId());

                call.enqueue(new Callback<ArrayList<Employee>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Employee>> call, Response<ArrayList<Employee>> response) {
                        int statusCode = response.code();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {


                           /* if (progressDialog != null&&progressDialog.isShowing())
                                progressDialog.dismiss();*/
                            ArrayList<Employee> list = response.body();


                            if (list !=null && list.size()!=0) {

                               Employee employees = list.get(0);


                                        getTasks(employees.getEmployeeId());
                                        getApprovedLeaveDetails(employees.getEmployeeId());
                                        //getRejectedLeaveDetails(employees.getEmployeeId());



                                        LoginDetails loginDetails = new LoginDetails();
                                        loginDetails.setEmployeeId(employees.getEmployeeId());
                                        loginDetails.setLoginDate(new SimpleDateFormat("MM/dd/yyyy").format(new Date()));
                                        getLoginDetails(loginDetails);


                                //}

                            }else{

                            }

                        }else {


                            Toast.makeText(getActivity(), "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
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







    private void getTasks(final int employeeId){




        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {
                TasksAPI apiService = Util.getClient().create(TasksAPI.class);
                Call<ArrayList<Tasks>> call = apiService.getTasks();

                call.enqueue(new Callback<ArrayList<Tasks>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Tasks>> call, Response<ArrayList<Tasks>> response) {
                        int statusCode = response.code();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {



                            ArrayList<Tasks> list = response.body();
                            ArrayList<Tasks> todayTasks = new ArrayList<>();
                            employeeTasks = new ArrayList<>();
                            pendingTasks = new ArrayList<>();
                            completedTasks = new ArrayList<>();
                            closedTasks = new ArrayList<>();
                            onTask = new ArrayList<>();

                            Date date = new Date();
                            Date adate = new Date();
                            Date edate = new Date();



                            try {
                                date = new SimpleDateFormat("yyyy-MM-dd").parse(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));


                            } catch (Exception e) {
                                e.printStackTrace();
                            }



                            if (list !=null && list.size()!=0) {



                                for (Tasks task:list) {

                                    if(task.getEmployeeId()==employeeId){

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
                                             /*   String parse = new SimpleDateFormat("MMM yyyy").format(afromDate);
                                                fromDate = new SimpleDateFormat("MMM yyyy").parse(parse);*/

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
                                              /*  String parse = new SimpleDateFormat("MMM yyyy").format(atoDate);
                                                toDate = new SimpleDateFormat("MMM yyyy").parse(parse);*/

                                            }

                                        }

                                        if(afromDate!=null&&atoDate!=null){



                                            if(date.getTime() >= afromDate.getTime() && date.getTime() <= atoDate.getTime()){

                                                todayTasks.add(task);

                                                if(task.getStatus().equalsIgnoreCase("Completed")){
                                                    completedTasks.add(task);
                                                    //complete = complete+1;
                                                }else if(task.getStatus().equalsIgnoreCase("Closed")){
                                                    closedTasks.add(task);
                                                    // closed = closed+1;
                                                }else if(task.getStatus().equalsIgnoreCase("On-Going")){
                                                    onTask.add(task);
                                                    onTasks = onTasks+1;
                                                }

                                            }else if(task.getStatus().equalsIgnoreCase("Pending")){
                                                todayTasks.add(task);

                                                pendingTasks.add(task);
                                                pendingTask = pendingTask+1;
                                            }
                                        }

                                        employeeTasks.add(task);
                                        // total = total+1;



                                    }

                                }

                                if(employeeTasks!=null&&employeeTasks.size()!=0){

                                    if(todayTasks!=null&&todayTasks.size()!=0) {
                                        mTodayTaskLayout.setVisibility(View.VISIBLE);
                                        mAdapter = new TaskListAdapter(getContext(), todayTasks);
                                        mTaskList.setAdapter(mAdapter);
                                    }

                                    mOnTask.setText(""+onTasks);
                                    mPending.setText(""+pendingTask);
                                }else{

                                }



                            }else{

                                // Toast.makeText(DailyTargetsForEmployeeActivity.this, "No Tasks given for this employee ", Toast.LENGTH_SHORT).show();

                            }

                        }else {



                            //Toast.makeText(DailyTargetsForEmployeeActivity.this, "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Tasks>> call, Throwable t) {
                        // Log error here since request failed
                       /* if (progressDialog != null&&progressDialog.isShowing())
                            progressDialog.dismiss();*/
                        Log.e("TAG", t.toString());
                    }
                });
            }


        });
    }

    private void getLoginDetails(final LoginDetails loginDetails){




        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {
                LoginDetailsAPI apiService = Util.getClient().create(LoginDetailsAPI.class);
                Call<ArrayList<LoginDetails>> call = apiService.getLoginByEmployeeId(loginDetails.getEmployeeId());

                call.enqueue(new Callback<ArrayList<LoginDetails>>() {
                    @Override
                    public void onResponse(Call<ArrayList<LoginDetails>> call, Response<ArrayList<LoginDetails>> response) {
                        int statusCode = response.code();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {



                            ArrayList<LoginDetails> list = response.body();

                            if (list !=null && list.size()!=0) {

                                LoginDetails loginDetails = list.get(list.size()-1);

                                monthDate = Calendar.getInstance().get(Calendar.MONTH)+1;
                                int year = Calendar.getInstance().get(Calendar.YEAR);


                                Date adate = null;
                                Date edate = null;

                                String dateValues = new SimpleDateFormat("yyyy-MM-dd").format(new Date());


                                try {

                                    adate = new SimpleDateFormat("yyyy-MM-dd").parse(year+"-"+monthDate+"-01");
                                    edate = new SimpleDateFormat("yyyy-MM-dd").parse(dateValues);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                                if(loginDetails!=null){

                                    String logout = loginDetails.getLogOutTime();
                                    String login = loginDetails.getLoginTime();
                                    String dates = loginDetails.getLoginDate();
                                    String date= null;

                                    if(dates!=null&&!dates.isEmpty()){

                                        if(dates.contains("T")){

                                            String logins[] = dates.split("T");
                                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                            SimpleDateFormat sdfs = new SimpleDateFormat("MMM dd,yyyy");

                                            Date dt = null;
                                            try {
                                                dt = sdf.parse(logins[0]);
                                                date = sdfs.format(dt);
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    }

                                    if(logout!=null&&!logout.isEmpty()&&(login!=null&&!login.isEmpty())){

                                        if(date!=null&&!date.isEmpty()){

                                            mLoggedTime.setText("Last Logout : "+logout);

                                        }else{

                                            mLoggedTime.setText("Last Logout : "+logout);
                                        }


                                        PreferenceHandler.getInstance(getActivity()).setLoginStatus("Logout");

                                    }else if(login!=null&&!login.isEmpty()&&(logout==null||logout.isEmpty())){

                                        if(date!=null&&!date.isEmpty()){

                                            mLoggedTime.setText("Last Logged in : "+login);

                                        }else{

                                            mLoggedTime.setText("Last Logged in : "+login);
                                        }

                                        PreferenceHandler.getInstance(getActivity()).setLoginStatus("Login");
                                        PreferenceHandler.getInstance(getActivity()).setLoginId(loginDetails.getLoginDetailsId());
                                    }

                                }

                                Collections.sort(list, LoginDetails.compareLogin);


                                ArrayList<String> dateList = new ArrayList<>();

                                for(int i=0;i<list.size();i++){

                                    if(list.get(i).getLoginDate().contains("T")){

                                        String date[] = list.get(i).getLoginDate().split("T");
                                        Date dates = null;
                                        try {
                                            dates = new SimpleDateFormat("yyyy-MM-dd").parse(date[0]);
                                            String dateValue = new SimpleDateFormat("MMM dd,yyyy").format(dates);

                                            if(adate!=null&&edate!=null){

                                                if(dates.getTime()>=adate.getTime()&&dates.getTime()<=edate.getTime()){

                                                    dateList.add(date[0]);
                                                }

                                            }




                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }

                                    }

                                }

                                if(dateList!=null&&dateList.size()!=0){

                                    Set<String> s = new LinkedHashSet<String>(dateList);

                                    mWorkedDays.setText(""+s.size());
                                }



                            }else{

                                mLoggedTime.setText("You have not Check-In today.Please put your check-in in Attendance Screen");

                               /* final android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(getActivity());
                                LayoutInflater inflater = getLayoutInflater();
                                View views = inflater.inflate(R.layout.check_in_pop,null);
                                Button agree = (Button) views.findViewById(R.id.dialog_ok);


                                dialogBuilder.setView(views);
                                final android.app.AlertDialog dialog = dialogBuilder.create();
                                dialog.setCancelable(true);
                                dialog.show();

                                agree.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                        Intent intent = new Intent(getActivity(), EmployeeNewMainScreen.class);
                                        intent.putExtra("viewpager_position", 2);
                                        intent.putExtra("Condition", false);
                                        startActivity(intent);
                                    }
                                });*/

                            }

                        }else {



                            //Toast.makeText(DailyTargetsForEmployeeActivity.this, "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<LoginDetails>> call, Throwable t) {
                        // Log error here since request failed
                       /* if (progressDialog != null&&progressDialog.isShowing())
                            progressDialog.dismiss();*/
                        Log.e("TAG", t.toString());
                    }
                });
            }


        });
    }

    private void getApprovedLeaveDetails(final int employeeId){

        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {
                LeaveAPI apiService = Util.getClient().create(LeaveAPI.class);
                Call<ArrayList<Leaves>> call = apiService.getLeavesByStatusAndEmployeeId("Approved",employeeId);

                call.enqueue(new Callback<ArrayList<Leaves>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Leaves>> call, Response<ArrayList<Leaves>> response) {
                        int statusCode = response.code();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {


                            int daysInMonth = 0;



                            try{

                                ArrayList<Leaves> list = response.body();
                                ArrayList<Leaves> approvedLeave = new ArrayList<>();


                                Date date = new Date();
                                Date adate = new Date();
                                Date edate = new Date();



                                try {
                                    date = new SimpleDateFormat("yyyy-MM-dd").parse(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

                                    System.out.println("Day countr "+daysInMonth);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }






                                if (list !=null && list.size()!=0) {


                                    /*for (Leaves leaves:list) {

                                        String froms = leaves.getFromDate();
                                        String tos = leaves.getToDate();
                                        Date fromDate = null;
                                        Date toDate = null;
                                        Date afromDate = null;
                                        Date atoDate = null;

                                        if(froms!=null&&!froms.isEmpty()){

                                            if(froms.contains("T")){

                                                String dojs[] = froms.split("T");

                                                afromDate = new SimpleDateFormat("yyyy-MM-dd").parse(dojs[0]);
                                             *//*   String parse = new SimpleDateFormat("MMM yyyy").format(afromDate);
                                                fromDate = new SimpleDateFormat("MMM yyyy").parse(parse);*//*

                                            }

                                        }

                                        if(tos!=null&&!tos.isEmpty()){

                                            if(tos.contains("T")){

                                                String dojs[] = tos.split("T");

                                                atoDate = new SimpleDateFormat("yyyy-MM-dd").parse(dojs[0]);
                                              *//*  String parse = new SimpleDateFormat("MMM yyyy").format(atoDate);
                                                toDate = new SimpleDateFormat("MMM yyyy").parse(parse);*//*

                                            }

                                        }

                                        if(afromDate!=null&&atoDate!=null){



                                            if(date.getTime() >= afromDate.getTime() && date.getTime() <= atoDate.getTime()){

                                                approvedLeave.add(leaves);

                                                checkValue = true;


                                            }
                                        }


                                        if(approvedLeave.size()!=0){
                                            mLeaveDays.setText(""+approvedLeave.size());
                                        }



                                    }
*/
                                    mLeaveDays.setText(""+list.size());

                                }else{



                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }


                        }else {


                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Leaves>> call, Throwable t) {
                        // Log error here since request failed

                        Log.e("TAG", t.toString());
                    }
                });
            }


        });
    }
   /* private void getRejectedLeaveDetails(final int employeeId){




        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {
                LeaveAPI apiService = Util.getClient().create(LeaveAPI.class);
                Call<ArrayList<Leaves>> call = apiService.getLeavesByStatusAndEmployeeId("Rejected",employeeId);

                call.enqueue(new Callback<ArrayList<Leaves>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Leaves>> call, Response<ArrayList<Leaves>> response) {
                        int statusCode = response.code();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {


                            int daysInMonth = 0;



                            try{

                                ArrayList<Leaves> list = response.body();
                                ArrayList<Leaves> rejectedLeave = new ArrayList<>();


                                Date date = new Date();
                                Date adate = new Date();
                                Date edate = new Date();



                                try {
                                    date = new SimpleDateFormat("yyyy-MM-dd").parse(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

                                    System.out.println("Day countr "+daysInMonth);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }






                                if (list !=null && list.size()!=0) {


                                    for (Leaves leaves:list) {

                                        String froms = leaves.getFromDate();
                                        String tos = leaves.getToDate();
                                        Date fromDate = null;
                                        Date toDate = null;
                                        Date afromDate = null;
                                        Date atoDate = null;

                                        if(froms!=null&&!froms.isEmpty()){

                                            if(froms.contains("T")){

                                                String dojs[] = froms.split("T");

                                                afromDate = new SimpleDateFormat("yyyy-MM-dd").parse(dojs[0]);
                                             *//*   String parse = new SimpleDateFormat("MMM yyyy").format(afromDate);
                                                fromDate = new SimpleDateFormat("MMM yyyy").parse(parse);*//*

                                            }

                                        }

                                        if(tos!=null&&!tos.isEmpty()){

                                            if(tos.contains("T")){

                                                String dojs[] = tos.split("T");

                                                atoDate = new SimpleDateFormat("yyyy-MM-dd").parse(dojs[0]);
                                              *//*  String parse = new SimpleDateFormat("MMM yyyy").format(atoDate);
                                                toDate = new SimpleDateFormat("MMM yyyy").parse(parse);*//*

                                            }

                                        }

                                        if(afromDate!=null&&atoDate!=null){



                                            if(date.getTime() >= afromDate.getTime() && date.getTime() <= atoDate.getTime()){

                                                rejectedLeave.add(leaves);

                                                checkValue = true;

                                            }
                                        }


                                        if(rejectedLeave.size()!=0){
                                            absentEmployee = absentEmployee+1;
                                            mEmployeeAbsent.setText(""+absentEmployee);
                                        }



                                    }


                                }else{



                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }


                        }else {


                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Leaves>> call, Throwable t) {
                        // Log error here since request failed

                        Log.e("TAG", t.toString());
                    }
                });
            }


        });
    }*/

    public static <T> ArrayList<T> removeDuplicates(ArrayList<T> list)
    {

        // Create a new ArrayList
        ArrayList<T> newList = new ArrayList<T>();

        // Traverse through the first list
        for (T element : list) {

            // If this element is not present in newList
            // then add it
            if (!newList.contains(element)) {

                newList.add(element);
            }
        }

        // return the new list
        return newList;
    }

    public void getMeetingss(final int id){

        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {

                final MeetingsAPI subCategoryAPI = Util.getClient().create(MeetingsAPI.class);
                Call<Meetings> getProf = subCategoryAPI.getMeetingById(id);
                //Call<ArrayList<Blogs>> getBlog = blogApi.getBlogs();

                getProf.enqueue(new Callback<Meetings>() {

                    @Override
                    public void onResponse(Call<Meetings> call, Response<Meetings> response) {

                        if (response.code() == 200||response.code() == 201||response.code() == 204)
                        {
                            System.out.println("Inside api");

                            final Meetings dto = response.body();

                            if(dto!=null){

                                try{

                                    ArrayList<Meetings> list = new ArrayList<>();
                                    list.add(dto);




                                    if (list !=null && list.size()!=0) {


                                        MeetingDetailAdapter adapter = new MeetingDetailAdapter(getActivity(),list);
                                        mMeetingList.setAdapter(adapter);



                                    }


                                }catch (Exception e){
                                    e.printStackTrace();
                                }

                            }




                        }else{


                            //meet
                        }
                    }

                    @Override
                    public void onFailure(Call<Meetings> call, Throwable t) {

                    }
                });

            }

        });
    }

    public class MeetingDetailAdapter extends RecyclerView.Adapter<MeetingDetailAdapter.ViewHolder>{

        private Context context;
        private ArrayList<Meetings> list;

        GoogleMap mMap;
        Marker marker;
        public MeetingDetailAdapter(Context context, ArrayList<Meetings> list) {

            this.context = context;
            this.list = list;


        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.adapter_meeting_report_list, parent, false);
            ViewHolder viewHolder = new ViewHolder(v);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final Meetings dto = list.get(position);

            if(dto!=null){


                String status = dto.getStatus();


                holder.mTaskName.setText(dto.getMeetingDetails());
                holder.mTaskDesc.setText("Description: \n"+dto.getMeetingAgenda());

                String froms = dto.getStartTime();
                String tos = dto.getEndTime();

                if(froms!=null&&tos!=null){
                    holder.mDuration.setText(froms+" to "+tos);
                }else if(froms!=null&&tos==null){
                    holder.mDuration.setText(froms+"");
                }else{
                    holder.mDuration.setVisibility(View.GONE);
                }


                holder.mDeadLine.setVisibility(View.GONE);
                //holder.mDeadLine.setText(dto.getDeadLine());
                holder.mStatus.setText(dto.getStatus());

                String lngi = dto.getEndLongitude();
                String lati = dto.getEndLatitude();
                String lngis = dto.getEndLongitude();
                String latis = dto.getEndLatitude();

                if(lngi!=null&&lati!=null&&!lngi.isEmpty()&&!lati.isEmpty()){

                    try{

                        double lngiValue  = Double.parseDouble(lngi);
                        double latiValue  = Double.parseDouble(lati);

                        if(lngiValue!=0&&latiValue!=0){
                            getAddress(lngiValue,latiValue,holder.mLocation);
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                        holder.mLocation.setText("Not Available");
                    }


                }else if(lngis!=null&&latis!=null&&!lngis.isEmpty()&&!latis.isEmpty()){

                    try{

                        double lngiValue  = Double.parseDouble(lngis);
                        double latiValue  = Double.parseDouble(latis);

                        if(lngiValue!=0&&latiValue!=0){
                            getAddress(lngiValue,latiValue,holder.mLocation);
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                        holder.mLocation.setText("Not Available");
                    }


                }

                getManagers(dto.getEmployeeId(),holder.mCreatedBy);
                // holder.mCreatedBy.setText(dto.getStatus());

                if(status.equalsIgnoreCase("Completed")){
                    holder.mStatus.setBackgroundColor(Color.parseColor("#00FF00"));
                }else if(status.equalsIgnoreCase("In-Meeing")){
                    holder.mStatus.setBackgroundColor(Color.parseColor("#D81B60"));
                }

                if(PreferenceHandler.getInstance(context).getUserRoleUniqueID()==2){
                    holder.mContact.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            getEmployees(dto.getEmployeeId(),dto);

                        }
                    });


                }else{
                    holder.mContact.setVisibility(View.GONE);
                  //  holder.mEdit.setVisibility(View.GONE);
                }

                holder.mEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        try{


                            getMeetings(dto.getMeetingsId());






                        }catch (Exception e){
                            e.printStackTrace();
                        }


                    }
                });


                holder.mMap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        try{

                            final Dialog dialog = new Dialog(context);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            /////make map clear
                            //dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

                            dialog.setContentView(R.layout.activity_maps);////your custom content

                            MapView mMapView = dialog.findViewById(R.id.organization_map);
                            MapsInitializer.initialize(context);

                            mMapView.onCreate(dialog.onSaveInstanceState());
                            mMapView.onResume();


                            mMapView.getMapAsync(new OnMapReadyCallback() {
                                @Override
                                public void onMapReady(final GoogleMap googleMap) {

                                    try{

                                        //LatLng posisiabsen = new LatLng(Double.parseDouble(dto.getEndLatitude()), Double.parseDouble(dto.getEndLongitude())); ////your lat lng
                                        googleMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(dto.getEndLatitude()), Double.parseDouble(dto.getEndLongitude()))).title("Map View"));

                                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(Double.parseDouble(dto.getEndLatitude()), Double.parseDouble(dto.getEndLongitude()))));
                                        googleMap.getUiSettings().setZoomControlsEnabled(true);
                                        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);

                                    }catch (Exception e){
                                        e.printStackTrace();
                                        LatLng posisiabsen = new LatLng(Double.parseDouble(PreferenceHandler.getInstance(context).getOrganizationLati()), Double.parseDouble(PreferenceHandler.getInstance(context).getOrganizationLongi())); ////your lat lng
                                        googleMap.addMarker(new MarkerOptions().position(posisiabsen).title("Map View"));
                                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(posisiabsen));
                                        googleMap.getUiSettings().setZoomControlsEnabled(true);
                                        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
                                    }

                                }
                            });


                            dialog.show();
                            dialog.setCancelable(true);
                            dialog.setCanceledOnTouchOutside(true);


                        }catch (Exception e){
                            e.printStackTrace();
                        }


                    }
                });

            }






        }

        private void getEmployees(final int id, final Meetings dto){




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
                                        try{

                                            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
                                            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                            View views = inflater.inflate(R.layout.alert_contact_employee, null);

                                            builder.setView(views);



                                            final MyRegulerText mEmpName = views.findViewById(R.id.employee_name);
                                            final MyRegulerText mPhone = views.findViewById(R.id.call_employee);
                                            final MyRegulerText mEmail = views.findViewById(R.id.email_employee);

                                            final android.support.v7.app.AlertDialog dialogs = builder.create();
                                            dialogs.show();
                                            dialogs.setCanceledOnTouchOutside(true);


                                            mEmpName.setText("Contact "+employees.getEmployeeName());
                                            mPhone.setText("Call "+employees.getPhoneNumber());
                                            mEmail.setText("Email "+employees.getPrimaryEmailAddress());


                                            mPhone.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {

                                                    Intent intent = new Intent(Intent.ACTION_DIAL);
                                                    intent.setData(Uri.parse("tel:"+employees.getPhoneNumber()));
                                                    context.startActivity(intent);
                                                }
                                            });

                                            mEmail.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {

                                                    final Intent emailIntent = new Intent(Intent.ACTION_SEND);

                                                    /* Fill it with Data */
                                                    emailIntent.setType("plain/text");
                                                    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{""+employees.getPrimaryEmailAddress()});
                                                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, ""+dto.getMeetingDetails());
                                                    emailIntent.putExtra(Intent.EXTRA_TEXT, "");

                                                    /* Send it off to the Activity-Chooser */
                                                    context.startActivity(Intent.createChooser(emailIntent, "Send mail..."));

                                                }
                                            });









                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
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
        private void getManagers(final int id, final TextView textView){




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
                                        try{

                                            textView.setText("Created By "+employees.getEmployeeName());


                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
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


        @Override
        public int getItemCount() {
            return list.size();
        }


        class ViewHolder extends RecyclerView.ViewHolder /*implements View.OnClickListener*/ {

            public TextView mTaskName,mTaskDesc,mDuration,mDeadLine,mStatus,mCreatedBy,mLocation;

            public LinearLayout mNotificationMain,mContact,mtaskUpdate;

            public  ImageView mEdit,mMap;

            public ViewHolder(View itemView) {
                super(itemView);
                itemView.setClickable(true);

                mTaskName = itemView.findViewById(R.id.title_task);
                mTaskDesc = itemView.findViewById(R.id.title_description);
                mDuration = itemView.findViewById(R.id.time_task);
                mDeadLine = itemView.findViewById(R.id.dead_line_task);
                mStatus = itemView.findViewById(R.id.status);
                mCreatedBy = itemView.findViewById(R.id.created_by);
                mLocation = itemView.findViewById(R.id.task_location);
                mEdit = itemView.findViewById(R.id.update);
                mMap = itemView.findViewById(R.id.map);

                mNotificationMain = itemView.findViewById(R.id.attendanceItem);
                mContact = itemView.findViewById(R.id.contact_employee);
                mtaskUpdate = itemView.findViewById(R.id.task_update);


            }
        }



        public void getAddress(final double longitude,final double latitude,final TextView textView )
        {

            try
            {
                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(context, Locale.ENGLISH);


                addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName();



                System.out.println("address = "+address);

                String currentLocation;

                if(!isEmpty(address))
                {
                    currentLocation=address;
                    textView.setText(currentLocation);

                }
                else
                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();


            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }

    public void getMeetings(final int id){

        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {

                final MeetingsAPI subCategoryAPI = Util.getClient().create(MeetingsAPI.class);
                Call<Meetings> getProf = subCategoryAPI.getMeetingById(id);
                //Call<ArrayList<Blogs>> getBlog = blogApi.getBlogs();

                getProf.enqueue(new Callback<Meetings>() {

                    @Override
                    public void onResponse(Call<Meetings> call, Response<Meetings> response) {

                        if (response.code() == 200||response.code() == 201||response.code() == 204)
                        {
                            System.out.println("Inside api");

                            final Meetings dto = response.body();

                            if(dto!=null){

                                try{

                                    if(locationCheck()){
                                        String message = "Login";




                                        message = "Do you want to Check-Out?";
                                        String option = "Meeting-Out";



                                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
                                        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                        View views = inflater.inflate(R.layout.activity_meeting_add_with_sign_screen, null);

                                        builder.setView(views);
                                     /*   final Button mSave = (Button) views.findViewById(R.id.save);
                                        mSave.setText(option);
                                        final EditText mRemarks = (EditText) views.findViewById(R.id.meeting_remarks);
                                        final TextInputEditText mClient = (TextInputEditText) views.findViewById(R.id.client_name);
                                        final TextInputEditText mContact = (TextInputEditText) views.findViewById(R.id.client_contact);
                                        final TextInputEditText mPurpose = (TextInputEditText) views.findViewById(R.id.purpose_meeting);*/

                                        final Button  mSave = views.findViewById(R.id.save);
                                        mSave.setText(option);
                                        final  EditText mDetails = views.findViewById(R.id.meeting_remarks);
                                        final  LinearLayout mSpinnerLay = views.findViewById(R.id.spinner_lay);
                                        final TextInputEditText mClientName = views.findViewById(R.id.client_name);
                                        final TextInputEditText mClientMobile = views.findViewById(R.id.client_contact_number);
                                        final TextInputEditText  mClientMail = views.findViewById(R.id.client_contact_email);
                                        final TextInputEditText mPurpose = views.findViewById(R.id.purpose_meeting);
                                        final CheckBox mGetSign = views.findViewById(R.id.get_sign_check);
                                        final CheckBox mTakeImage = views.findViewById(R.id.get_image_check);
                                        final ImageView mImageView = views.findViewById(R.id.selfie_pic);
                                        customerSpinner = views.findViewById(R.id.customer_spinner_adpter);
                                        ClientNameLayout =  views.findViewById(R.id.client_name_layout);
                                        mSpinnerLay.setVisibility(View.GONE);
                                        ClientNameLayout.setVisibility(View.VISIBLE);



                                        mDetails.setText(""+dto.getMeetingDetails());
                                        methodAdd = true;
                                        if(dto.getMeetingPersonDetails().contains("%")){

                                            String person[] = dto.getMeetingPersonDetails().split("%");

                                            if(person.length==1){
                                                mClientName.setText(""+dto.getMeetingPersonDetails());
                                            }else if(person.length==2){
                                                mClientName.setText(""+person[0]);
                                                mClientMail.setText(""+person[1]);
                                            }else if(person.length==3){
                                                mClientName.setText(""+person[0]);
                                                mClientMail.setText(""+person[1]);
                                                mClientMobile.setText(""+person[2]);
                                            }

                                        }else{
                                            mClientName.setText(""+dto.getMeetingPersonDetails());
                                        }

                                        mPurpose.setText(""+dto.getMeetingAgenda());

                                        if(dto.getEndPlaceID()!=null&&!dto.getEndPlaceID().isEmpty()){
                                            Picasso.with(getActivity()).load(dto.getEndPlaceID()).placeholder(R.drawable.profile_image).error(R.drawable.no_image).into(mImageView);
                                        }

                                        final android.app.AlertDialog dialog = builder.create();
                                        dialog.show();
                                        dialog.setCanceledOnTouchOutside(true);

                                        /*customerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                            @Override
                                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                                if(customerArrayList!=null && customerArrayList.size()!=0){


                                                    if(customerArrayList.get(position).getCustomerName()!=null && customerArrayList.get(position).getCustomerName().equalsIgnoreCase("Others"))
                                                    {
                                                        mClientMobile.setText("");
                                                        mClientName.setText("");
                                                        mClientMail.setText("");
                                                        ClientNameLayout.setVisibility(View.VISIBLE);

                                                    }
                                                    else {
                                                        mClientMobile.setText(""+customerArrayList.get(position).getCustomerMobile());
                                                        mClientName.setText(""+customerArrayList.get(position).getCustomerName());
                                                        mClientMail.setText(""+customerArrayList.get(position).getCustomerEmail());
                                                        clientId = customerArrayList.get(position).getCustomerId();
                                                        ClientNameLayout.setVisibility(View.GONE);

                                                    }
                                                }
                                            }

                                            @Override
                                            public void onNothingSelected(AdapterView<?> parent) {

                                            }
                                        });
*/
                                        /*if(dto.getCustomerId()!=0){

                                            getCustomersWithId(PreferenceHandler.getInstance(getActivity()).getCompanyId(),dto.getCustomerId());
                                        }else{
                                            getCustomersWithId(PreferenceHandler.getInstance(getActivity()).getCompanyId(),0);
                                        }*/

                                        mSave.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {

                                                String client = mClientName.getText().toString();
                                                String purpose = mPurpose.getText().toString();
                                                String detail = mDetails.getText().toString();
                                                String mobile = mClientMobile.getText().toString();
                                                String email = mClientMail.getText().toString();
                                                // String customer = customerSpinner.getSelectedItem().toString();

                                                if(client==null||client.isEmpty()){

                                                    Toast.makeText(getActivity(), "Please mention client name", Toast.LENGTH_SHORT).show();

                                                }else if(purpose==null||purpose.isEmpty()){

                                                    Toast.makeText(getActivity(), "Please mention purpose of meeting", Toast.LENGTH_SHORT).show();

                                                }else if(detail==null||detail.isEmpty()){

                                                    Toast.makeText(getActivity(), "Please mention remarks about meeting", Toast.LENGTH_SHORT).show();

                                                }else{

                                                    //gps = new TrackGPS(getActivity());

                                                    if(locationCheck()){

                                                        if(currentLocation!=null) {

                                                            latitude = currentLocation.getLatitude();
                                                            longitude = currentLocation.getLongitude();

                                                            LatLng masters = new LatLng(latitude, longitude);
                                                            String addresss = null;
                                                            try {
                                                                addresss = getAddress(masters);
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }

                                                           /* latLong.setText(addresss);
                                                            centreMapOnLocationWithLatLng(masters, "" + PreferenceHandler.getInstance(getActivity()).getUserFullName());
*/

                                                            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                                                            SimpleDateFormat sdt = new SimpleDateFormat("MMM dd,yyyy hh:mm a");

                                                            LatLng master = new LatLng(latitude,longitude);
                                                            String address = null;
                                                            try {
                                                                address = getAddress(master);
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }

                                                            loginDetails = dto;
                                                            loginDetails.setEmployeeId(PreferenceHandler.getInstance(getActivity()).getUserId());
                                                          /*  loginDetails.setStartLatitude(""+latitude);
                                                            loginDetails.setStartLongitude(""+longitude);
                                                            loginDetails.setStartLocation(""+address);
                                                            loginDetails.setStartTime(""+sdt.format(new Date()));*/
                                                            loginDetails.setEndLatitude(""+latitude);
                                                            loginDetails.setEndLongitude(""+longitude);
                                                            loginDetails.setEndLocation(""+address);
                                                            loginDetails.setEndTime(""+sdt.format(new Date()));
                                                            loginDetails.setMeetingDate(""+sdf.format(new Date()));
                                                            loginDetails.setMeetingAgenda(purpose);
                                                            loginDetails.setMeetingDetails(detail);
                                                            loginDetails.setStatus("Completed");

                                                            /*if(customer!=null&&!customer.equalsIgnoreCase("Others")){

                                                                if(customerArrayList!=null&&customerArrayList.size()!=0)
                                                                    loginDetails.setCustomerId(clientId);

                                                            }*/

                                                            String contact = "";

                                                            if(email!=null&&!email.isEmpty()){
                                                                contact = contact+"%"+email;
                                                            }

                                                            if(mobile!=null&&!mobile.isEmpty()){
                                                                contact = contact+"%"+mobile;
                                                            }

                                                            if(contact!=null&&!contact.isEmpty()){
                                                                loginDetails.setMeetingPersonDetails(client+""+contact);
                                                            }else{
                                                                loginDetails.setMeetingPersonDetails(client);
                                                            }

                                                            try {

                                                                md = new MeetingDetailsNotificationManagers();
                                                                md.setTitle("Meeting Details from "+ PreferenceHandler.getInstance(getActivity()).getUserFullName());
                                                                md.setMessage("Meeting with "+client+" for "+purpose);
                                                                md.setLocation(address);
                                                                md.setLongitude(""+longitude);
                                                                md.setLatitude(""+latitude);
                                                                md.setMeetingDate(""+sdt.format(new Date()));
                                                                md.setStatus("Completed");
                                                                md.setEmployeeId(PreferenceHandler.getInstance(getActivity()).getUserId());
                                                                md.setManagerId(PreferenceHandler.getInstance(getActivity()).getManagerId());
                                                                md.setMeetingPerson(client);
                                                                md.setMeetingsId(loginDetails.getMeetingsId());
                                                                md.setMeetingsDetails(purpose);
                                                                md.setMeetingComments(detail);

                                                                if (mGetSign.isChecked()&&!mTakeImage.isChecked()){
                                                                    // Method to create Directory, if the Directory doesn't exists
                                                                    file = new File(DIRECTORY);
                                                                    if (!file.exists()) {
                                                                        file.mkdir();
                                                                    }

                                                                    // Dialog Function
                                                                    dialogs = new Dialog(getActivity());
                                                                    // Removing the features of Normal Dialogs
                                                                    dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                                    dialogs.setContentView(R.layout.dialog_signature);
                                                                    dialogs.setCancelable(true);

                                                                    dialog_action(loginDetails,md,"null",dialog);

                                                                }else if (!mGetSign.isChecked()&&mTakeImage.isChecked()){

                                                                    file = new File(DIRECTORY);
                                                                    if (!file.exists()) {
                                                                        file.mkdir();
                                                                    }

                                                                       /* // Dialog Function
                                                                        dialog = new Dialog(MeetingAddWithSignScreen.this);
                                                                        // Removing the features of Normal Dialogs
                                                                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                                        dialog.setContentView(R.layout.dialog_signature);
                                                                        dialog.setCancelable(true);*/

                                                                    dispatchTakePictureIntent();
                                                                    dialog.dismiss();

                                                                    //dialog_action(loginDetails,md,"Selfie");

                                                                }else if (mGetSign.isChecked()&&mTakeImage.isChecked()){

                                                                    file = new File(DIRECTORY);
                                                                    if (!file.exists()) {
                                                                        file.mkdir();
                                                                    }

                                                                    // Dialog Function
                                                                    dialogs = new Dialog(getActivity());
                                                                    // Removing the features of Normal Dialogs
                                                                    dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                                    dialogs.setContentView(R.layout.dialog_signature);
                                                                    dialogs.setCancelable(true);

                                                                    dialog_action(loginDetails,md,"Selfie",dialog);

                                                                }else{
                                                                    updateMeeting(loginDetails,md);
                                                                }

                                                                dialog.dismiss();


                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }else if(latitude!=0&&longitude!=0){



                                                          /*  latitude = currentLocation.getLatitude();
                                                            longitude = currentLocation.getLongitude();*/

                                                            LatLng masters = new LatLng(latitude, longitude);
                                                            String addresss = null;
                                                            try {
                                                                addresss = getAddress(masters);
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }

                                                           /* latLong.setText(addresss);
                                                            centreMapOnLocationWithLatLng(masters, "" + PreferenceHandler.getInstance(getActivity()).getUserFullName());
*/

                                                            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                                                            SimpleDateFormat sdt = new SimpleDateFormat("MMM dd,yyyy hh:mm a");

                                                            LatLng master = new LatLng(latitude,longitude);
                                                            String address = null;
                                                            try {
                                                                address = getAddress(master);
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }

                                                            loginDetails = dto;
                                                            loginDetails.setEmployeeId(PreferenceHandler.getInstance(getActivity()).getUserId());
                                                          /*  loginDetails.setStartLatitude(""+latitude);
                                                            loginDetails.setStartLongitude(""+longitude);
                                                            loginDetails.setStartLocation(""+address);
                                                            loginDetails.setStartTime(""+sdt.format(new Date()));*/
                                                            loginDetails.setEndLatitude(""+latitude);
                                                            loginDetails.setEndLongitude(""+longitude);
                                                            loginDetails.setEndLocation(""+address);
                                                            loginDetails.setEndTime(""+sdt.format(new Date()));
                                                            loginDetails.setMeetingDate(""+sdf.format(new Date()));
                                                            loginDetails.setMeetingAgenda(purpose);
                                                            loginDetails.setMeetingDetails(detail);
                                                            loginDetails.setStatus("Completed");

                                                            String contact = "";

                                                            if(email!=null&&!email.isEmpty()){
                                                                contact = contact+"%"+email;
                                                            }

                                                            if(mobile!=null&&!mobile.isEmpty()){
                                                                contact = contact+"%"+mobile;
                                                            }

                                                            if(contact!=null&&!contact.isEmpty()){
                                                                loginDetails.setMeetingPersonDetails(client+""+contact);
                                                            }else{
                                                                loginDetails.setMeetingPersonDetails(client);
                                                            }

                                                            try {

                                                                md = new MeetingDetailsNotificationManagers();
                                                                md.setTitle("Meeting Details from "+ PreferenceHandler.getInstance(getActivity()).getUserFullName());
                                                                md.setMessage("Meeting with "+client+" for "+purpose);
                                                                md.setLocation(address);
                                                                md.setLongitude(""+longitude);
                                                                md.setLatitude(""+latitude);
                                                                md.setMeetingDate(""+sdt.format(new Date()));
                                                                md.setStatus("Completed");
                                                                md.setEmployeeId(PreferenceHandler.getInstance(getActivity()).getUserId());
                                                                md.setManagerId(PreferenceHandler.getInstance(getActivity()).getManagerId());
                                                                md.setMeetingPerson(client);
                                                                md.setMeetingsId(loginDetails.getMeetingsId());
                                                                md.setMeetingsDetails(purpose);
                                                                md.setMeetingComments(detail);

                                                                if (mGetSign.isChecked()&&!mTakeImage.isChecked()){
                                                                    // Method to create Directory, if the Directory doesn't exists
                                                                    file = new File(DIRECTORY);
                                                                    if (!file.exists()) {
                                                                        file.mkdir();
                                                                    }

                                                                    // Dialog Function
                                                                    dialogs = new Dialog(getActivity());
                                                                    // Removing the features of Normal Dialogs
                                                                    dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                                    dialogs.setContentView(R.layout.dialog_signature);
                                                                    dialogs.setCancelable(true);

                                                                    dialog_action(loginDetails,md,"null",dialog);

                                                                }else if (mGetSign.isChecked()&&mTakeImage.isChecked()){

                                                                    file = new File(DIRECTORY);
                                                                    if (!file.exists()) {
                                                                        file.mkdir();
                                                                    }

                                                                    // Dialog Function
                                                                    dialogs = new Dialog(getActivity());
                                                                    // Removing the features of Normal Dialogs
                                                                    dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                                    dialogs.setContentView(R.layout.dialog_signature);
                                                                    dialogs.setCancelable(true);

                                                                    dialog_action(loginDetails,md,"Selfie",dialog);

                                                                }else if (!mGetSign.isChecked()&&mTakeImage.isChecked()){

                                                                    file = new File(DIRECTORY);
                                                                    if (!file.exists()) {
                                                                        file.mkdir();
                                                                    }

                                                                       /* // Dialog Function
                                                                        dialog = new Dialog(MeetingAddWithSignScreen.this);
                                                                        // Removing the features of Normal Dialogs
                                                                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                                        dialog.setContentView(R.layout.dialog_signature);
                                                                        dialog.setCancelable(true);*/

                                                                    dispatchTakePictureIntent();
                                                                    dialog.dismiss();

                                                                    //dialog_action(loginDetails,md,"Selfie");

                                                                }else{
                                                                    updateMeeting(loginDetails,md);
                                                                }

                                                                dialog.dismiss();


                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    }
                                                   /* if(gps!=null&&gps.canGetLocation())
                                                    {
                                                        System.out.println("Long and lat Rev"+gps.getLatitude()+" = "+gps.getLongitude());
                                                        latitude = gps.getLatitude();
                                                        longitude = gps.getLongitude();




                                                    }
                                                    else
                                                    {

                                                    }*/

                                                }
                                            }
                                        });

                                    }else{

                                    }




                                }catch (Exception e){
                                    e.printStackTrace();
                                }

                            }




                        }else{


                            //meet
                        }
                    }

                    @Override
                    public void onFailure(Call<Meetings> call, Throwable t) {

                    }
                });

            }

        });
    }

    public void updateMeeting(final Meetings loginDetails, final MeetingDetailsNotificationManagers md) {



        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Saving Details..");
        dialog.setCancelable(false);
        dialog.show();

        MeetingsAPI apiService = Util.getClient().create(MeetingsAPI.class);

        Call<Meetings> call = apiService.updateMeetingById(loginDetails.getMeetingsId(),loginDetails);

        call.enqueue(new Callback<Meetings>() {
            @Override
            public void onResponse(Call<Meetings> call, Response<Meetings> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {
                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201||response.code()==204) {


                        if(dialogs!=null){

                            dialogs.dismiss();
                        }

                        saveMeetingNotification(md);

                        Toast.makeText(getActivity(), "You Checked out", Toast.LENGTH_SHORT).show();

                        PreferenceHandler.getInstance(getActivity()).setMeetingId(0);
                        // getMeetingDetails();


                        PreferenceHandler.getInstance(getActivity()).setMeetingLoginStatus("Logout");



                    }else {
                        Toast.makeText(getActivity(), "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
            public void onFailure(Call<Meetings> call, Throwable t) {

                if(dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
                Toast.makeText(getActivity(), "Failed Due to "+t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());
            }
        });



    }

    public void saveMeetingNotification(final MeetingDetailsNotificationManagers md) {



        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Saving Details..");
        dialog.setCancelable(false);
        dialog.show();

        MeetingNotificationAPI apiService = Util.getClient().create(MeetingNotificationAPI.class);

        Call<MeetingDetailsNotificationManagers> call = apiService.saveMeetingNotification(md);

        call.enqueue(new Callback<MeetingDetailsNotificationManagers>() {
            @Override
            public void onResponse(Call<MeetingDetailsNotificationManagers> call, Response<MeetingDetailsNotificationManagers> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {
                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201) {

                        MeetingDetailsNotificationManagers s = response.body();

                        if(s!=null){

                            MeetingDetailsNotificationManagers md = new MeetingDetailsNotificationManagers();
                            md.setTitle(s.getTitle());
                            md.setMessage(s.getMessage());
                            md.setLocation(s.getLocation());
                            md.setLongitude(""+s.getLongitude());
                            md.setLatitude(""+s.getLatitude());
                            md.setMeetingDate(""+s.getMeetingDate());
                            md.setStatus(s.getStatus());
                            md.setEmployeeId(s.getManagerId());
                            md.setManagerId(s.getEmployeeId());
                            md.setMeetingPerson(s.getMeetingPerson());
                            md.setMeetingsDetails(s.getMeetingsDetails());
                            md.setMeetingComments(s.getMeetingComments());
                            md.setMeetingsId(s.getMeetingsId());
                            md.setSenderId(Constants.SENDER_ID);
                            md.setServerId(Constants.SERVER_ID);

                            sendMeetingNotification(md);

                        }




                    }else {
                        Toast.makeText(getActivity(), "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
            public void onFailure(Call<MeetingDetailsNotificationManagers> call, Throwable t) {

                if(dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
                Toast.makeText(getActivity(), "Failed Due to "+t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());
            }
        });



    }

    public void sendMeetingNotification(final MeetingDetailsNotificationManagers md) {



        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Sending Details..");
        dialog.setCancelable(false);
        dialog.show();

        MeetingNotificationAPI apiService = Util.getClient().create(MeetingNotificationAPI.class);

        Call<ArrayList<String>> call = apiService.sendMeetingNotification(md);

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



                        getActivity().recreate();


                    }else {
                        Toast.makeText(getActivity(), "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getActivity(), "Failed Due to "+t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());
            }
        });



    }


    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
        this.mMap.setMaxZoomPreference(24.0f);

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i("salam", " Connected");

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        currentLocation = LocationServices.FusedLocationApi.getLastLocation(mLocationClient);


        if (currentLocation != null) {
            //  latLong.setText("Latitude : " + currentLocation.getLatitude() + " , Longitude : " + currentLocation.getLongitude());

            latitude = currentLocation.getLatitude();
            longitude = currentLocation.getLongitude();

            LatLng master = new LatLng(latitude,longitude);
            String address = null;
            try {
                address = getAddress(master);
            } catch (Exception e) {
                e.printStackTrace();
            }





        }

        startLocationUpdates();


    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    protected void startLocationUpdates() {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getActivity(), "Enable Permissions", Toast.LENGTH_LONG).show();
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mLocationClient, mLocationRequest, this);


    }

    @Override
    public void onLocationChanged(Location location) {


        if(location!=null){

            latitude = location.getLatitude();
            longitude = location.getLongitude();

            LatLng master = new LatLng(latitude,longitude);
            String address = null;
            try {
                address = getAddress(master);
            } catch (Exception e) {
                e.printStackTrace();
            }



        }



    }



    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    public boolean locationCheck(){

        final boolean status = false;
        LocationManager lm = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {
            // notify user
            android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(getActivity());
            dialog.setMessage("Location is not enable");
            dialog.setPositiveButton("Open Settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    getActivity().startActivity(myIntent);
                    //get gps
                }
            });
            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub


                }
            });
            dialog.show();
            return false;
        }else{
            return true;
        }
    }

    private String getAddress(LatLng latLng) {
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        String result = null;
        try {
            List<Address> addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
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

    // Function for Digital Signature
    public void dialog_action(final Meetings loginDetails, final MeetingDetailsNotificationManagers md, final String type, final android.app.AlertDialog alertDialog) {

        mContent = dialogs.findViewById(R.id.linearLayout);
        mSignature = new signature(getActivity(), null);
        mSignature.setBackgroundColor(Color.WHITE);
        // Dynamically generating Layout through java code
        mContent.addView(mSignature, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mClear = dialogs.findViewById(R.id.clear);
        mGetSigns = dialogs.findViewById(R.id.getsign);
        mGetSigns.setEnabled(false);
        mCancel = dialogs.findViewById(R.id.cancel);
        view = mContent;

        mClear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.v("log_tag", "Panel Cleared");
                mSignature.clear();
                mGetSigns.setEnabled(false);
            }
        });

        mGetSigns.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                Log.v("log_tag", "Panel Saved");
                view.setDrawingCacheEnabled(true);
                mSignature.save(view, StoredPath,loginDetails,md,type,alertDialog);
                if(dialogs!=null){

                    dialogs.dismiss();
                }

                Toast.makeText(getActivity(), "Successfully Saved", Toast.LENGTH_SHORT).show();
                // Calling the same class
                //recreate();

            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.v("log_tag", "Panel Canceled");
                if(dialogs!=null){

                    dialogs.dismiss();
                }

                // Calling the same class
                getActivity().recreate();
            }
        });
        dialogs.show();
    }

    public class signature extends View {

        private static final float STROKE_WIDTH = 5f;
        private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        private Paint paint = new Paint();
        private Path path = new Path();

        private float lastTouchX;
        private float lastTouchY;
        private final RectF dirtyRect = new RectF();

        public signature(Context context, AttributeSet attrs) {
            super(context, attrs);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);
        }

        public void save(View v, String StoredPath, final Meetings loginDetails, final MeetingDetailsNotificationManagers md, final String type, final android.app.AlertDialog alertDialog) {
            Log.v("log_tag", "Width: " + v.getWidth());
            Log.v("log_tag", "Height: " + v.getHeight());
            if (bitmap == null) {
                bitmap = Bitmap.createBitmap(mContent.getWidth(), mContent.getHeight(), Bitmap.Config.RGB_565);
            }
            Canvas canvas = new Canvas(bitmap);
            try {
                // Output the file
                FileOutputStream mFileOutStream = new FileOutputStream(StoredPath);
                v.draw(canvas);

                // Convert the output file to Image such as .png
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, mFileOutStream);
                mFileOutStream.flush();
                mFileOutStream.close();

                File file = new File(StoredPath);

                if(file.length() <= 1*1024*1024)
                {
                    FileOutputStream out = null;
                    String[] filearray = StoredPath.split("/");
                    final String filename = getFilename(filearray[filearray.length-1]);

                    out = new FileOutputStream(filename);
                    Bitmap myBitmap = BitmapFactory.decodeFile(StoredPath);

//          write the compressed bitmap at the field_icon specified by filename.
                    myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

                    uploadImage(filename,loginDetails,md,type);



                }
                else
                {
                    compressImage(StoredPath,loginDetails,md,type);
                }

            } catch (Exception e) {
                Log.v("log_tag", e.toString());
            }

        }



        public void clear() {
            path.reset();
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawPath(path, paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float eventX = event.getX();
            float eventY = event.getY();
            mGetSigns.setEnabled(true);

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(eventX, eventY);
                    lastTouchX = eventX;
                    lastTouchY = eventY;
                    return true;

                case MotionEvent.ACTION_MOVE:

                case MotionEvent.ACTION_UP:

                    resetDirtyRect(eventX, eventY);
                    int historySize = event.getHistorySize();
                    for (int i = 0; i < historySize; i++) {
                        float historicalX = event.getHistoricalX(i);
                        float historicalY = event.getHistoricalY(i);
                        expandDirtyRect(historicalX, historicalY);
                        path.lineTo(historicalX, historicalY);
                    }
                    path.lineTo(eventX, eventY);
                    break;

                default:
                    debug("Ignored touch event: " + event.toString());
                    return false;
            }

            invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                    (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

            lastTouchX = eventX;
            lastTouchY = eventY;

            return true;
        }

        private void debug(String string) {

            Log.v("log_tag", string);

        }

        private void expandDirtyRect(float historicalX, float historicalY) {
            if (historicalX < dirtyRect.left) {
                dirtyRect.left = historicalX;
            } else if (historicalX > dirtyRect.right) {
                dirtyRect.right = historicalX;
            }

            if (historicalY < dirtyRect.top) {
                dirtyRect.top = historicalY;
            } else if (historicalY > dirtyRect.bottom) {
                dirtyRect.bottom = historicalY;
            }
        }

        private void resetDirtyRect(float eventX, float eventY) {
            dirtyRect.left = Math.min(lastTouchX, eventX);
            dirtyRect.right = Math.max(lastTouchX, eventX);
            dirtyRect.top = Math.min(lastTouchY, eventY);
            dirtyRect.bottom = Math.max(lastTouchY, eventY);
        }
    }

    public String getFilename(String filePath) {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "MyFolder/Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        System.out.println("getFilePath = "+filePath);
        String uriSting;
        if(filePath.contains(".jpg"))
        {
            uriSting = (file.getAbsolutePath() + "/" + filePath);
        }
        else
        {
            uriSting = (file.getAbsolutePath() + "/" + filePath+".jpg" );
        }
        return uriSting;

    }

    private void uploadImage(final String filePath, final Meetings loginDetails, final MeetingDetailsNotificationManagers md, final String type)
    {
        //String filePath = getRealPathFromURIPath(uri, ImageUploadActivity.this);

        final File file = new File(filePath);
        int size = 1*1024*1024;

        if(file != null)
        {
            if(file.length() > size)
            {
                System.out.println(file.length());
                compressImage(filePath,loginDetails,md,type);
            }
            else
            {
                final ProgressDialog dialog = new ProgressDialog(getActivity());
                dialog.setCancelable(false);
                dialog.setTitle("Uploading Image..");
                dialog.show();
                Log.d("Image Upload", "Filename " + file.getName());

                RequestBody mFile = RequestBody.create(MediaType.parse("image"), file);
                MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), mFile);
                RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());
                UploadApi uploadImage = Util.getClient().create(UploadApi.class);

                Call<String> fileUpload = uploadImage.uploadProfileImages(fileToUpload, filename);
                fileUpload.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if(dialog != null && dialog.isShowing())
                        {
                            dialog.dismiss();
                        }





                        try {

                            if(type!=null&&type.equalsIgnoreCase("Selfie")){
                                if(Util.IMAGE_URL==null){
                                    loginDetails.setEndPlaceID(Constants.IMAGE_URL+ response.body());
                                }else{
                                    loginDetails.setEndPlaceID(Util.IMAGE_URL+ response.body());
                                }

                                dispatchTakePictureIntent();

                            }else if(type!=null&&type.equalsIgnoreCase("Done")){

                                if(Util.IMAGE_URL==null){
                                    loginDetails.setStartPlaceID(Constants.IMAGE_URL+ response.body());
                                }else{
                                    loginDetails.setStartPlaceID(Util.IMAGE_URL+ response.body());
                                }

                                if(methodAdd){
                                    updateMeeting(loginDetails,md);
                                }


                            }else{

                                if(Util.IMAGE_URL==null){
                                    loginDetails.setEndPlaceID(Constants.IMAGE_URL+ response.body());
                                }else{
                                    loginDetails.setEndPlaceID(Util.IMAGE_URL+ response.body());
                                }
                                if(methodAdd){
                                    updateMeeting(loginDetails,md);
                                }else{
                                    //addMeeting(loginDetails,md);
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        if(filePath.contains("MyFolder/Images"))
                        {
                            file.delete();
                        }
                    }
                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.d("UpdateCate", "Error " + t.getMessage());
                    }
                });
            }
        }
    }


    public String compressImage(String filePath, final Meetings loginDetails, final MeetingDetailsNotificationManagers md, final String type) {

        //String filePath = getRealPathFromURI(imageUri);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = actualHeight/2;//2033.0f;
        float maxWidth = actualWidth/2;//1011.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String[] filearray = filePath.split("/");
        final String filename = getFilename(filearray[filearray.length-1]);
        try {
            out = new FileOutputStream(filename);


//          write the compressed bitmap at the field_icon specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

            uploadImage(filename,loginDetails,md,type);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filename;

    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            saveSelfie(imageBitmap,StoredPathSelfie);

        }
    }

    public void saveSelfie(Bitmap bitmap, String StoredPath) {

        if (bitmap == null) {
            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
        }

        try {
            // Output the file
            FileOutputStream mFileOutStream = new FileOutputStream(StoredPath);


            // Convert the output file to Image such as .png
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, mFileOutStream);
            mFileOutStream.flush();
            mFileOutStream.close();

            File file = new File(StoredPath);

            if(file.length() <= 1*1024*1024)
            {
                FileOutputStream out = null;
                String[] filearray = StoredPath.split("/");
                final String filename = getFilename(filearray[filearray.length-1]);

                out = new FileOutputStream(filename);
                Bitmap myBitmap = BitmapFactory.decodeFile(StoredPath);

//          write the compressed bitmap at the field_icon specified by filename.
                myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

                uploadImage(filename,loginDetails,md,"Done");

                mImageView.setVisibility(View.VISIBLE);
                mImageView.setImageBitmap(bitmap);

            }
            else
            {
                compressImage(StoredPath,loginDetails,md,"Done");
            }

        } catch (Exception e) {
            Log.v("log_tag", e.toString());
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        /*if (mLocationClient == null) {
            mLocationClient = new GoogleApiClient.Builder(this.getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }else{
            mLocationClient.connect();
        }*/

        if (mLocationClient != null) {
            mLocationClient.connect();
        }
    }

}

