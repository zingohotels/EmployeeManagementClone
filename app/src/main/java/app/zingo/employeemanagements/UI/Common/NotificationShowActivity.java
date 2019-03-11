package app.zingo.employeemanagements.UI.Common;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;

import app.zingo.employeemanagements.Database.DBHelper;
import app.zingo.employeemanagements.Model.NotificationSettingsData;
import app.zingo.employeemanagements.R;
import app.zingo.employeemanagements.UI.NewAdminDesigns.EmployeeEditScreen;

public class NotificationShowActivity extends AppCompatActivity {

    Switch mAttendance,mTask,mLeave,mMeetings,mExpenses;
    AppCompatButton mSave;

    private DBHelper mydb ;

    ArrayList<NotificationSettingsData> notificationSettingsDatas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{

            setContentView(R.layout.activity_notification_show);

            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Notification Settings");


            mAttendance = (Switch)findViewById(R.id.attendance_noti);
            mTask = (Switch)findViewById(R.id.task_noti);
            mLeave = (Switch)findViewById(R.id.leave_noti);
            mMeetings = (Switch)findViewById(R.id.meeting_noti);
            mExpenses = (Switch)findViewById(R.id.expense_noti);
            mSave = (AppCompatButton)findViewById(R.id.save_settings);

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
}
