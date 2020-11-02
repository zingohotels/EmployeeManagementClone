package app.zingo.employeemanagements.ui.Common;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import app.zingo.employeemanagements.Database.DBHelper;
import app.zingo.employeemanagements.model.Employee;
import app.zingo.employeemanagements.model.NotificationSettingsData;
import app.zingo.employeemanagements.Service.LocationForegroundService;
import app.zingo.employeemanagements.ui.LandingScreen;
import app.zingo.employeemanagements.ui.newemployeedesign.EmployeeNewMainScreen;
import app.zingo.employeemanagements.utils.PreferenceHandler;
import app.zingo.employeemanagements.utils.ThreadExecuter;
import app.zingo.employeemanagements.utils.Util;
import app.zingo.employeemanagements.WebApi.EmployeeApi;
import app.zingo.employeemanagements.base.BuildConfig;
import app.zingo.employeemanagements.base.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationShowActivity extends AppCompatActivity {

    Switch mAttendance,mAttendanceLate,mTask,mLeave,mMeetings,mExpenses;
    AppCompatButton mSave;
    LinearLayout logout;

    private DBHelper mydb ;

    ArrayList<NotificationSettingsData> notificationSettingsDatas;

    Employee employee;
    String type;
    String notificationData = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{

            setContentView(R.layout.activity_notification_show);

            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Notification Settings");

            Bundle bun = getIntent().getExtras();

            if(bun!=null){

                employee = (Employee)bun.getSerializable("Employee");
                type = bun.getString("Type");
            }


            mAttendance = findViewById(R.id.attendance_noti);
            mAttendanceLate = findViewById(R.id.late_attendance_noti);
            mTask = findViewById(R.id.task_noti);
            mLeave = findViewById(R.id.leave_noti);
            mMeetings = findViewById(R.id.meeting_noti);
            mExpenses = findViewById(R.id.expense_noti);
            logout = findViewById(R.id.logout);
            mSave = findViewById(R.id.save_settings);

            mydb = new DBHelper(this);



            if(employee==null){
                getProfileById(PreferenceHandler.getInstance( NotificationShowActivity.this).getUserId());
            }else{

                String notificationValues = employee.getAllowanceType();

                if(notificationValues!=null&&!notificationValues.isEmpty()){

                    if(notificationValues.contains("Attendance")){

                        mAttendance.setChecked(true);

                    }

                    if(notificationValues.contains("Late")){
                        mAttendanceLate.setChecked(true);

                    }

                    if(notificationValues.contains("Expenses")){
                        mExpenses.setChecked(true);

                    }

                    if(notificationValues.contains("Meetings")){
                        mMeetings.setChecked(true);

                    }

                    if(notificationValues.contains("Leave")){

                        mLeave.setChecked(true);
                    }

                    if(notificationValues.contains("Task")){
                        mTask.setChecked(true);

                    }
                }
            }

            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    StatusBarNotification[] barNotifications = new StatusBarNotification[0];
                    if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        barNotifications = notificationManager.getActiveNotifications();
                        for(StatusBarNotification notification: barNotifications) {
                            System.out.println("Notification Id "+notification.getId());
                        }
                    }


                    if(type!=null&&type.equalsIgnoreCase("Employee")){



                        String loginStatus = PreferenceHandler.getInstance( NotificationShowActivity.this).getLoginStatus();
                        String meetingStatus = PreferenceHandler.getInstance( NotificationShowActivity.this).getMeetingLoginStatus();

                        if (loginStatus != null && !loginStatus.isEmpty()) {

                            if (loginStatus.equalsIgnoreCase("Login")) {

                                if (meetingStatus != null && meetingStatus.equalsIgnoreCase("Login")) {

                                    Toast.makeText( NotificationShowActivity.this, "You are in some meeting .So Please checkout", Toast.LENGTH_SHORT).show();

                                } else {



                                    final android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder( NotificationShowActivity.this);
                                    LayoutInflater inflater = getLayoutInflater();
                                    View views = inflater.inflate(R.layout.absent_condition,null);
                                    Button agree = views.findViewById(R.id.dialog_ok);


                                    dialogBuilder.setView(views);
                                    final android.app.AlertDialog dialog = dialogBuilder.create();
                                    dialog.setCancelable(true);
                                    dialog.show();

                                    agree.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialog.dismiss();
                                            Intent intent = new Intent( NotificationShowActivity.this, EmployeeNewMainScreen.class);
                                            intent.putExtra("viewpager_position", 2);
                                            startActivity(intent);
                                        }
                                    });
                                }


                            }else{
                                if(employee!=null){

                                    Employee profile = employee;

                                    profile.setAppOpen(false);
                                    String app_version = PreferenceHandler.getInstance( NotificationShowActivity.this).getAppVersion();
                                    profile.setLastUpdated(""+ BuildConfig.VERSION_NAME);
                                    profile.setLastseen(new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new Date()));
                                    updateProfile(profile);

                                }else{

                                    getProfile(PreferenceHandler.getInstance( NotificationShowActivity.this).getUserId());

                                }
                            }

                        }


                    }else{

                        if(employee!=null){

                            Employee profile = employee;

                            profile.setAppOpen(false);
                            String app_version = PreferenceHandler.getInstance( NotificationShowActivity.this).getAppVersion();
                            profile.setLastUpdated(""+ BuildConfig.VERSION_NAME);
                            profile.setLastseen(new SimpleDateFormat("MM/dd/yyyy").format(new Date()));
                            updateProfile(profile);

                        }else{

                            getProfile(PreferenceHandler.getInstance( NotificationShowActivity.this).getUserId());

                        }


                    }



                }
            });

            mSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {



                        if(mAttendance.isChecked()){

                           notificationData = notificationData+",Attendance";


                        }

                        if(mTask.isChecked()){

                            notificationData = notificationData+",Task";
                        }

                        if(mLeave.isChecked()){

                            notificationData = notificationData+",Leave";
                        }

                        if(mMeetings.isChecked()){

                            notificationData = notificationData+",Meetings";
                        }

                        if(mExpenses.isChecked()){

                            notificationData = notificationData+",Expenses";
                        }

                        if(mAttendanceLate.isChecked()){

                            notificationData = notificationData+",Late";
                        }


                    if(employee!=null){

                        Employee profile = employee;
                        profile.setAllowanceType(notificationData);
                        updateProfileNotification(profile);

                    }



                }
            });


        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id)
        {
            case android.R.id.home:

                NotificationShowActivity.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void getProfile(final int id ){

        final ProgressDialog dialog = new ProgressDialog( NotificationShowActivity.this);
        dialog.setCancelable(false);
        dialog.setTitle("Saving Details...");
        dialog.show();

        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {

                final EmployeeApi subCategoryAPI = Util.getClient().create( EmployeeApi.class);
                Call<ArrayList<Employee>> getProf = subCategoryAPI.getProfileById(id);
                //Call<ArrayList<Blogs>> getBlog = blogApi.getBlogs();

                getProf.enqueue(new Callback<ArrayList<Employee>>() {

                    @Override
                    public void onResponse(Call<ArrayList<Employee>> call, Response<ArrayList<Employee>> response) {

                        if(dialog != null && dialog.isShowing())
                        {
                            dialog.dismiss();
                        }

                        if (response.code() == 200)
                        {
                            System.out.println("Inside api");

                            Employee profile = response.body().get(0);
                            String app_version = PreferenceHandler.getInstance( NotificationShowActivity.this).getAppVersion();
                            profile.setLastUpdated(""+ BuildConfig.VERSION_NAME);
                            profile.setLastseen(new SimpleDateFormat("MM/dd/yyyy").format(new Date()));
                            profile.setAppOpen(false);
                            updateProfile(profile);

                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Employee>> call, Throwable t) {

                        if(dialog != null && dialog.isShowing())
                        {
                            dialog.dismiss();
                        }

                    }
                });

            }

        });
    }

    public void getProfileById(final int id ){

        final ProgressDialog dialog = new ProgressDialog( NotificationShowActivity.this);
        dialog.setCancelable(false);
        dialog.setTitle("Loading Details...");
        dialog.show();

        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {

                final EmployeeApi subCategoryAPI = Util.getClient().create( EmployeeApi.class);
                Call<ArrayList<Employee>> getProf = subCategoryAPI.getProfileById(id);
                //Call<ArrayList<Blogs>> getBlog = blogApi.getBlogs();

                getProf.enqueue(new Callback<ArrayList<Employee>>() {

                    @Override
                    public void onResponse(Call<ArrayList<Employee>> call, Response<ArrayList<Employee>> response) {

                        if(dialog != null && dialog.isShowing())
                        {
                            dialog.dismiss();
                        }

                        if (response.code() == 200)
                        {
                            System.out.println("Inside api");

                            employee = response.body().get(0);

                            if(employee!=null){
                                String notificationValues = employee.getAllowanceType();

                                if(notificationValues!=null&&!notificationValues.isEmpty()){

                                    if(notificationValues.contains("Attendance")){

                                        mAttendance.setChecked(true);

                                    }

                                    if(notificationValues.contains("Late")){
                                        mAttendanceLate.setChecked(true);

                                    }

                                    if(notificationValues.contains("Expenses")){
                                        mExpenses.setChecked(true);

                                    }

                                    if(notificationValues.contains("Meetings")){
                                        mMeetings.setChecked(true);

                                    }

                                    if(notificationValues.contains("Leave")){

                                        mLeave.setChecked(true);
                                    }

                                    if(notificationValues.contains("Task")){
                                        mTask.setChecked(true);

                                    }
                                }
                            }


                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Employee>> call, Throwable t) {

                        if(dialog != null && dialog.isShowing())
                        {
                            dialog.dismiss();
                        }

                    }
                });

            }

        });
    }



    public void updateProfile(final Employee employee){

        final ProgressDialog dialog = new ProgressDialog( NotificationShowActivity.this);
        dialog.setCancelable(false);
        dialog.setTitle("Saving Details...");
        dialog.show();

        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {

                final EmployeeApi subCategoryAPI = Util.getClient().create( EmployeeApi.class);
                Call<Employee> getProf = subCategoryAPI.updateEmployee(employee.getEmployeeId(),employee);
                //Call<ArrayList<Blogs>> getBlog = blogApi.getBlogs();

                getProf.enqueue(new Callback<Employee>() {

                    @Override
                    public void onResponse(Call<Employee> call, Response<Employee> response) {

                        if(dialog != null && dialog.isShowing())
                        {
                            dialog.dismiss();
                        }

                        if(PreferenceHandler.getInstance( NotificationShowActivity.this).getUserRoleUniqueID()==9){

                            Intent intent = new Intent( NotificationShowActivity.this, LocationForegroundService.class);
                            intent.setAction(LocationForegroundService.ACTION_STOP_FOREGROUND_SERVICE);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                NotificationShowActivity.this.startForegroundService(intent);
                            } else {
                                NotificationShowActivity.this.startService(intent);
                            }
                        }

                        if (response.code() == 200||response.code()==201||response.code()==204)
                        {


                            PreferenceHandler.getInstance( NotificationShowActivity.this).clear();


                            Intent log = new Intent( NotificationShowActivity.this, LandingScreen.class);
                            log.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            log.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            Toast.makeText( NotificationShowActivity.this,"Logout",Toast.LENGTH_SHORT).show();
                            startActivity(log);
                            NotificationShowActivity.this.finish();

                        }else{
                            PreferenceHandler.getInstance( NotificationShowActivity.this).clear();

                            Intent log = new Intent( NotificationShowActivity.this, LandingScreen.class);
                            log.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            log.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            Toast.makeText( NotificationShowActivity.this,"Logout",Toast.LENGTH_SHORT).show();
                            startActivity(log);
                            NotificationShowActivity.this.finish();
                            // Toast.makeText(ChangePasswordScreen.this, "Failed due to status code"+response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Employee> call, Throwable t) {

                        if(dialog != null && dialog.isShowing())
                        {
                            dialog.dismiss();
                        }

                        if(PreferenceHandler.getInstance( NotificationShowActivity.this).getUserRoleUniqueID()==9){

                            Intent intent = new Intent( NotificationShowActivity.this, LocationForegroundService.class);
                            intent.setAction(LocationForegroundService.ACTION_STOP_FOREGROUND_SERVICE);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                NotificationShowActivity.this.startForegroundService(intent);
                            } else {
                                NotificationShowActivity.this.startService(intent);
                            }
                        }


                        PreferenceHandler.getInstance( NotificationShowActivity.this).clear();

                        Intent log = new Intent( NotificationShowActivity.this, LandingScreen.class);
                        log.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        log.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        Toast.makeText( NotificationShowActivity.this,"Logout",Toast.LENGTH_SHORT).show();
                        startActivity(log);
                        NotificationShowActivity.this.finish();
                        //  Toast.makeText(ChangePasswordScreen.this, "Something went wrong due to "+"Bad Internet Connection", Toast.LENGTH_SHORT).show();

                    }
                });

            }

        });
    }

    public void updateProfileNotification(final Employee employee){

        final ProgressDialog dialog = new ProgressDialog( NotificationShowActivity.this);
        dialog.setCancelable(false);
        dialog.setTitle("Saving Details...");
        dialog.show();

        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {

                final EmployeeApi subCategoryAPI = Util.getClient().create( EmployeeApi.class);
                Call<Employee> getProf = subCategoryAPI.updateEmployee(employee.getEmployeeId(),employee);
                //Call<ArrayList<Blogs>> getBlog = blogApi.getBlogs();

                getProf.enqueue(new Callback<Employee>() {

                    @Override
                    public void onResponse(Call<Employee> call, Response<Employee> response) {

                        if(dialog != null && dialog.isShowing())
                        {
                            dialog.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(Call<Employee> call, Throwable t) {

                        if(dialog != null && dialog.isShowing())
                        {
                            dialog.dismiss();
                        }


                    }
                });

            }

        });
    }
}
