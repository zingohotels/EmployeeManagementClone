package app.zingo.employeemanagements.UI.Common;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
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

import app.zingo.employeemanagements.base.BuildConfig;
import app.zingo.employeemanagements.Database.DBHelper;
import app.zingo.employeemanagements.Model.Employee;
import app.zingo.employeemanagements.Model.NotificationSettingsData;
import app.zingo.employeemanagements.Model.WorkingDay;
import app.zingo.employeemanagements.base.R;
import app.zingo.employeemanagements.Service.LocationForegroundService;
import app.zingo.employeemanagements.UI.LandingScreen;
import app.zingo.employeemanagements.UI.NewAdminDesigns.EmployeeEditScreen;
import app.zingo.employeemanagements.UI.NewEmployeeDesign.EmployeeNewMainScreen;
import app.zingo.employeemanagements.Utils.PreferenceHandler;
import app.zingo.employeemanagements.Utils.ThreadExecuter;
import app.zingo.employeemanagements.Utils.Util;
import app.zingo.employeemanagements.WebApi.EmployeeApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationShowActivity extends AppCompatActivity {

    Switch mAttendance,mTask,mLeave,mMeetings,mExpenses;
    AppCompatButton mSave;
    LinearLayout logout;

    private DBHelper mydb ;

    ArrayList<NotificationSettingsData> notificationSettingsDatas;

    Employee employee;
    String type;

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
            mTask = findViewById(R.id.task_noti);
            mLeave = findViewById(R.id.leave_noti);
            mMeetings = findViewById(R.id.meeting_noti);
            mExpenses = findViewById(R.id.expense_noti);
            logout = findViewById(R.id.logout);
            mSave = findViewById(R.id.save_settings);

            mydb = new DBHelper(this);

            if(mydb!=null){



                notificationSettingsDatas = mydb.getAllNotifications();

                if(notificationSettingsDatas!=null&&notificationSettingsDatas.size()!=0){

                    for (NotificationSettingsData ndfs:notificationSettingsDatas) {

                        if(ndfs.getName()!=null&&ndfs.getName().equalsIgnoreCase("Attendance")){
                            mAttendance.setChecked(true);
                        }else{
                            mAttendance.setChecked(false);
                        }

                        if(ndfs.getName()!=null&&ndfs.getName().equalsIgnoreCase("Task")){
                            mTask.setChecked(true);
                        }else{
                            mTask.setChecked(false);
                        }

                        if(ndfs.getName()!=null&&ndfs.getName().equalsIgnoreCase("Leave")){
                            mLeave.setChecked(true);
                        }else{
                            mLeave.setChecked(false);
                        }

                        if(ndfs.getName()!=null&&ndfs.getName().equalsIgnoreCase("Meetings")){
                            mMeetings.setChecked(true);
                        }else{
                            mMeetings.setChecked(false);
                        }

                        if(ndfs.getName()!=null&&ndfs.getName().equalsIgnoreCase("Expesnses")){
                            mExpenses.setChecked(true);
                        }else{
                            mExpenses.setChecked(false);
                        }

                    }
                }


            }


            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    if(type!=null&&type.equalsIgnoreCase("Employee")){



                        String loginStatus = PreferenceHandler.getInstance(NotificationShowActivity.this).getLoginStatus();
                        String meetingStatus = PreferenceHandler.getInstance(NotificationShowActivity.this).getMeetingLoginStatus();

                        if (loginStatus != null && !loginStatus.isEmpty()) {

                            if (loginStatus.equalsIgnoreCase("Login")) {

                                if (meetingStatus != null && meetingStatus.equalsIgnoreCase("Login")) {

                                    Toast.makeText(NotificationShowActivity.this, "You are in some meeting .So Please checkout", Toast.LENGTH_SHORT).show();

                                } else {



                                    final android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(NotificationShowActivity.this);
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
                                            Intent intent = new Intent(NotificationShowActivity.this, EmployeeNewMainScreen.class);
                                            intent.putExtra("viewpager_position", 2);
                                            startActivity(intent);
                                        }
                                    });
                                }


                            }else{
                                if(employee!=null){

                                    Employee profile = employee;

                                    profile.setAppOpen(false);
                                    String app_version = PreferenceHandler.getInstance(NotificationShowActivity.this).getAppVersion();
                                    profile.setLastUpdated(""+ BuildConfig.VERSION_NAME);
                                    profile.setLastseen(new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new Date()));
                                    updateProfile(profile);

                                }else{

                                    getProfile(PreferenceHandler.getInstance(NotificationShowActivity.this).getUserId());

                                }
                            }

                        }


                    }else{

                        if(employee!=null){

                            Employee profile = employee;

                            profile.setAppOpen(false);
                            String app_version = PreferenceHandler.getInstance(NotificationShowActivity.this).getAppVersion();
                            profile.setLastUpdated(""+ BuildConfig.VERSION_NAME);
                            profile.setLastseen(new SimpleDateFormat("MM/dd/yyyy").format(new Date()));
                            updateProfile(profile);

                        }else{

                            getProfile(PreferenceHandler.getInstance(NotificationShowActivity.this).getUserId());

                        }


                    }



                }
            });

            mSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if(notificationSettingsDatas==null&&notificationSettingsDatas.size()==0){

                        ArrayList<NotificationSettingsData> notificationSettingsData = new ArrayList<>();

                        if(mAttendance.isChecked()){

                            NotificationSettingsData ndf = new NotificationSettingsData();
                            ndf.setName("Attendance");
                            ndf.setStatus(1);
                            notificationSettingsData.add(ndf);


                        }else{
                            NotificationSettingsData ndf = new NotificationSettingsData();
                            ndf.setName("Attendance");
                            ndf.setStatus(0);
                            notificationSettingsData.add(ndf);
                        }

                        if(mTask.isChecked()){

                            NotificationSettingsData ndf = new NotificationSettingsData();
                            ndf.setName("Task");
                            ndf.setStatus(1);
                            notificationSettingsData.add(ndf);
                        }else{
                            NotificationSettingsData ndf = new NotificationSettingsData();
                            ndf.setName("Task");
                            ndf.setStatus(0);
                            notificationSettingsData.add(ndf);
                        }

                        if(mLeave.isChecked()){

                            NotificationSettingsData ndf = new NotificationSettingsData();
                            ndf.setName("Leave");
                            ndf.setStatus(1);
                            notificationSettingsData.add(ndf);
                        }else{
                            NotificationSettingsData ndf = new NotificationSettingsData();
                            ndf.setName("Leave");
                            ndf.setStatus(0);
                            notificationSettingsData.add(ndf);
                        }

                        if(mMeetings.isChecked()){

                            NotificationSettingsData ndf = new NotificationSettingsData();
                            ndf.setName("Meetings");
                            ndf.setStatus(1);
                            notificationSettingsData.add(ndf);
                        }else{
                            NotificationSettingsData ndf = new NotificationSettingsData();
                            ndf.setName("Meetings");
                            ndf.setStatus(0);
                            notificationSettingsData.add(ndf);
                        }

                        if(mExpenses.isChecked()){

                            NotificationSettingsData ndf = new NotificationSettingsData();
                            ndf.setName("Expesnses");
                            ndf.setStatus(1);
                            notificationSettingsData.add(ndf);
                        }else{
                            NotificationSettingsData ndf = new NotificationSettingsData();
                            ndf.setName("Expesnses");
                            ndf.setStatus(0);
                            notificationSettingsData.add(ndf);
                        }

                        if(notificationSettingsData!=null&&notificationSettingsData.size()!=0){

                            for (NotificationSettingsData ndfs :notificationSettingsData) {

                                if(mydb!=null&&ndfs!=null){

                                    mydb.addNotification(ndfs);

                                }

                            }

                        }else{
                            Toast.makeText(NotificationShowActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }


                    }else{




                        ArrayList<NotificationSettingsData> notificationSettingsData = new ArrayList<>();

                        if(mAttendance.isChecked()){

                            NotificationSettingsData ndf = new NotificationSettingsData();
                            ndf.setName("Attendance");
                            ndf.setStatus(1);
                            notificationSettingsData.add(ndf);


                        }else{
                            NotificationSettingsData ndf = new NotificationSettingsData();
                            ndf.setName("Attendance");
                            ndf.setStatus(0);
                            notificationSettingsData.add(ndf);
                        }

                        if(mTask.isChecked()){

                            NotificationSettingsData ndf = new NotificationSettingsData();
                            ndf.setName("Task");
                            ndf.setStatus(1);
                            notificationSettingsData.add(ndf);
                        }else{
                            NotificationSettingsData ndf = new NotificationSettingsData();
                            ndf.setName("Task");
                            ndf.setStatus(0);
                            notificationSettingsData.add(ndf);
                        }

                        if(mLeave.isChecked()){

                            NotificationSettingsData ndf = new NotificationSettingsData();
                            ndf.setName("Leave");
                            ndf.setStatus(1);
                            notificationSettingsData.add(ndf);
                        }else{
                            NotificationSettingsData ndf = new NotificationSettingsData();
                            ndf.setName("Leave");
                            ndf.setStatus(0);
                            notificationSettingsData.add(ndf);
                        }

                        if(mMeetings.isChecked()){

                            NotificationSettingsData ndf = new NotificationSettingsData();
                            ndf.setName("Meetings");
                            ndf.setStatus(1);
                            notificationSettingsData.add(ndf);
                        }else{
                            NotificationSettingsData ndf = new NotificationSettingsData();
                            ndf.setName("Meetings");
                            ndf.setStatus(0);
                            notificationSettingsData.add(ndf);
                        }

                        if(mExpenses.isChecked()){

                            NotificationSettingsData ndf = new NotificationSettingsData();
                            ndf.setName("Expesnses");
                            ndf.setStatus(1);
                            notificationSettingsData.add(ndf);
                        }else{
                            NotificationSettingsData ndf = new NotificationSettingsData();
                            ndf.setName("Expesnses");
                            ndf.setStatus(0);
                            notificationSettingsData.add(ndf);
                        }

                        if(notificationSettingsData!=null&&notificationSettingsData.size()!=0){

                            for (NotificationSettingsData ndfs :notificationSettingsData) {

                                if(mydb!=null&&ndfs!=null){

                                    mydb.updateNotification(ndfs);

                                }

                            }

                        }else{
                            Toast.makeText(NotificationShowActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }


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

        final ProgressDialog dialog = new ProgressDialog(NotificationShowActivity.this);
        dialog.setCancelable(false);
        dialog.setTitle("Saving Details...");
        dialog.show();

        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {

                final EmployeeApi subCategoryAPI = Util.getClient().create(EmployeeApi.class);
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
                            String app_version = PreferenceHandler.getInstance(NotificationShowActivity.this).getAppVersion();
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

    public void updateProfile(final Employee employee){

        final ProgressDialog dialog = new ProgressDialog(NotificationShowActivity.this);
        dialog.setCancelable(false);
        dialog.setTitle("Saving Details...");
        dialog.show();

        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {

                final EmployeeApi subCategoryAPI = Util.getClient().create(EmployeeApi.class);
                Call<Employee> getProf = subCategoryAPI.updateEmployee(employee.getEmployeeId(),employee);
                //Call<ArrayList<Blogs>> getBlog = blogApi.getBlogs();

                getProf.enqueue(new Callback<Employee>() {

                    @Override
                    public void onResponse(Call<Employee> call, Response<Employee> response) {

                        if(dialog != null && dialog.isShowing())
                        {
                            dialog.dismiss();
                        }

                        if(PreferenceHandler.getInstance(NotificationShowActivity.this).getUserRoleUniqueID()==9){

                            Intent intent = new Intent(NotificationShowActivity.this, LocationForegroundService.class);
                            intent.setAction(LocationForegroundService.ACTION_STOP_FOREGROUND_SERVICE);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                NotificationShowActivity.this.startForegroundService(intent);
                            } else {
                                NotificationShowActivity.this.startService(intent);
                            }
                        }

                        if (response.code() == 200||response.code()==201||response.code()==204)
                        {


                            PreferenceHandler.getInstance(NotificationShowActivity.this).clear();


                            Intent log = new Intent(NotificationShowActivity.this, LandingScreen.class);
                            log.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            log.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            Toast.makeText(NotificationShowActivity.this,"Logout",Toast.LENGTH_SHORT).show();
                            startActivity(log);
                            NotificationShowActivity.this.finish();

                        }else{
                            PreferenceHandler.getInstance(NotificationShowActivity.this).clear();

                            Intent log = new Intent(NotificationShowActivity.this, LandingScreen.class);
                            log.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            log.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            Toast.makeText(NotificationShowActivity.this,"Logout",Toast.LENGTH_SHORT).show();
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

                        if(PreferenceHandler.getInstance(NotificationShowActivity.this).getUserRoleUniqueID()==9){

                            Intent intent = new Intent(NotificationShowActivity.this, LocationForegroundService.class);
                            intent.setAction(LocationForegroundService.ACTION_STOP_FOREGROUND_SERVICE);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                NotificationShowActivity.this.startForegroundService(intent);
                            } else {
                                NotificationShowActivity.this.startService(intent);
                            }
                        }


                        PreferenceHandler.getInstance(NotificationShowActivity.this).clear();

                        Intent log = new Intent(NotificationShowActivity.this, LandingScreen.class);
                        log.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        log.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        Toast.makeText(NotificationShowActivity.this,"Logout",Toast.LENGTH_SHORT).show();
                        startActivity(log);
                        NotificationShowActivity.this.finish();
                        //  Toast.makeText(ChangePasswordScreen.this, "Something went wrong due to "+t.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

            }

        });
    }
}
