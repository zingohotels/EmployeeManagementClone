package app.zingo.employeemanagements.ui.Admin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import app.zingo.employeemanagements.model.Employee;
import app.zingo.employeemanagements.model.EmployeeImages;
import app.zingo.employeemanagements.model.LiveTracking;
import app.zingo.employeemanagements.model.LoginDetails;
import app.zingo.employeemanagements.model.Meetings;
import app.zingo.employeemanagements.model.Tasks;
import app.zingo.employeemanagements.ui.Common.EmployeeMeetingMap;
import app.zingo.employeemanagements.ui.landing.InternalServerErrorScreen;
import app.zingo.employeemanagements.utils.PreferenceHandler;
import app.zingo.employeemanagements.utils.ThreadExecuter;
import app.zingo.employeemanagements.utils.Util;
import app.zingo.employeemanagements.WebApi.LiveTrackingAPI;
import app.zingo.employeemanagements.WebApi.LoginDetailsAPI;
import app.zingo.employeemanagements.WebApi.MeetingsAPI;
import app.zingo.employeemanagements.WebApi.TasksAPI;
import app.zingo.employeemanagements.base.R;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmployeesDashBoard extends AppCompatActivity {

    CollapsingToolbarLayout collapsingToolbarLayout;
    CircleImageView collapsingToolbarImageView;
    TextView mName,mWorkedDays,mWorkedHours,mTotalMeeting,mMeetingHours,mAvgMeeting,mIdle,mTotaltask,mTotalKm,mAvgTask,mTaskTime;
    RecyclerView mLoginDetails;

    Employee profile;
    int profileId;
    long loginHour=0,meetingHour=0;

    EmployeeImages employeeImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{

            setContentView(R.layout.activity_employees_dash_board);

            Toolbar toolbar = findViewById(R.id.collapsing_toolbar);

            setSupportActionBar(toolbar);

            ActionBar actionBar = getSupportActionBar();

            if(actionBar!=null)
            {
                // Display home menu item.
                actionBar.setDisplayHomeAsUpEnabled(true);
            }

            // Set collapsing tool bar title.
            collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar_layout);
            // Set collapsing tool bar image.
            collapsingToolbarImageView = findViewById(R.id.collapsing_toolbar_image_view);
            //collapsingToolbarImageView.setImageResource(R.drawable.img1);

            mWorkedDays = findViewById(R.id.worked_days);
            mWorkedHours = findViewById(R.id.worked_hours);
            mTotalMeeting = findViewById(R.id.travel_distance);
            mMeetingHours = findViewById(R.id.target_complete);
            mAvgMeeting = findViewById(R.id.avg_meeting);
            mIdle = findViewById(R.id.idle_time);
            mTotaltask = findViewById(R.id.total_tasks);
            mTaskTime = findViewById(R.id.task_time);
            mAvgTask = findViewById(R.id.avg_task);
            mTotalKm = findViewById(R.id.km_travelled);
            mLoginDetails = findViewById(R.id.login_details);

            Bundle bundle = getIntent().getExtras();

            if(bundle!=null){

                profile = ( Employee )bundle.getSerializable("Profile");
                profileId = bundle.getInt("ProfileId");
            }

            mTotalMeeting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent meeting = new Intent( EmployeesDashBoard.this, EmployeeMeetingMap.class);
                    meeting.putExtra("EmployeeId",profileId);
                    startActivity(meeting);
                }
            });
            if(profile!=null){
                collapsingToolbarLayout.setTitle(""+profile.getEmployeeName());
                /*Picasso.with(ProfileBlogList.this).load(profile.getProfilePhoto()).placeholder(R.drawable.profile_image).
                        error(R.drawable.profile_image).into(collapsingToolbarImageView);*/

                ArrayList< EmployeeImages > images = profile.getEmployeeImages();

                if(images!=null&&images.size()!=0){
                    employeeImages = images.get(0);

                    if(employeeImages!=null){


                        String base=employeeImages.getImage();
                        if(base != null && !base.isEmpty()){
                            Picasso.with( EmployeesDashBoard.this).load(base).placeholder(R.drawable.profile_image).
                                    error(R.drawable.profile_image).into(collapsingToolbarImageView);


                        }
                    }

                }

                try{
                    getLoginDetails(profile.getEmployeeId());
                    getTasks(profile.getEmployeeId());
                    //getLiveLocation(profile.getEmployeeId());
                }catch (Exception e){
                    e.printStackTrace();
                    Intent error = new Intent( EmployeesDashBoard.this, InternalServerErrorScreen.class);
                    startActivity(error);
                }



            }else{
                //getProfile(profileId);

            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void getLoginDetails(final int employeeId) {


        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading Details..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new ThreadExecuter ().execute( new Runnable() {
            @Override
            public void run() {
                LoginDetailsAPI apiService = Util.getClient().create( LoginDetailsAPI.class);
                Call<ArrayList< LoginDetails >> call = apiService.getLoginByEmployeeId(employeeId);

                call.enqueue(new Callback<ArrayList< LoginDetails >>() {
                    @Override
                    public void onResponse( Call<ArrayList< LoginDetails >> call, Response<ArrayList< LoginDetails >> response) {
                        int statusCode = response.code();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {


                            if (progressDialog != null&&progressDialog.isShowing())
                                progressDialog.dismiss();
                            ArrayList< LoginDetails > list = response.body();
                           // long hours=0,hourMins=0;
                            long diffHrs=0;


                            if (list !=null && list.size()!=0) {

                                Collections.sort(list, LoginDetails.compareLogin);
                                LoginDetailsAdapter adapter = new LoginDetailsAdapter( EmployeesDashBoard.this,list);
                                mLoginDetails.setAdapter(adapter);

                                ArrayList<String> dateList = new ArrayList<>();
                                for(int i=0;i<list.size();i++){

                                    if(list.get(i).getLoginDate().contains("T")){

                                        String loginTime = list.get(i).getLoginTime();
                                        String logoutTime = list.get(i).getLogOutTime();
                                        String date[] = list.get(i).getLoginDate().split("T");
                                        Date dates = null;
                                        try {
                                            dates = new SimpleDateFormat("yyyy-MM-dd").parse(date[0]);
                                            String dateValue = new SimpleDateFormat("MMM dd,yyyy").format(dates);

                                            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy hh:mm a");
                                            SimpleDateFormat sdfs = new SimpleDateFormat("MMM dd,yyyy");


                                            try {
                                                dates = new SimpleDateFormat("yyyy-MM-dd").parse(dateValue);
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }

                                            Date fd=null,td=null;
                                            String comDate = new SimpleDateFormat("MMM dd,yyyy").format(dates);


                                            if(loginTime==null||loginTime.isEmpty()){

                                                loginTime = comDate +" 00:00 am";
                                            }

                                            if(logoutTime==null||logoutTime.isEmpty()){

                                                logoutTime = comDate  +" "+new SimpleDateFormat("hh:mm a").format(new Date()) ;
                                            }

                                            try {
                                                fd = sdf.parse(""+loginTime);
                                                td = sdf.parse(""+logoutTime);

                                                long diff = td.getTime() - fd.getTime();

                                                diffHrs = diffHrs+diff;



                                            } catch (ParseException e) {
                                                e.printStackTrace();

                                            }

                                           /* hours = dateCal(dateValue,list.get(i).getLoginTime(),list.get(i).getLogOutTime());
                                            hourMins = hourMins+hours;*/
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                        dateList.add(date[0]);
                                    }

                                }

                                if(dateList!=null&&dateList.size()!=0){

                                    Set<String> s = new LinkedHashSet<String>(dateList);

                                    mWorkedDays.setText(""+s.size());
                                }

                                loginHour = diffHrs;

                                int minutes = (int) ((diffHrs / (1000*60)) % 60);
                                int hours   = (int) ((diffHrs / (1000*60*60)) % 24);
                                int days   = (int) ((diffHrs / (1000*60*60*24)));


                                DecimalFormat df = new DecimalFormat("00");


                                mWorkedHours.setText(String.format("%02d", days)+"d"+String.format("%02d", hours) +"hr"+String.format("%02d", minutes)+"mins");
                                getMeetingDetails(employeeId);

                            }else{

                            }

                        }else {

                            if (progressDialog != null&&progressDialog.isShowing())
                                progressDialog.dismiss();

                            Toast.makeText( EmployeesDashBoard.this, "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure( Call<ArrayList< LoginDetails >> call, Throwable t) {
                        // Log error here since request failed
                        if (progressDialog != null&&progressDialog.isShowing())
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

        new ThreadExecuter ().execute( new Runnable() {
            @Override
            public void run() {
                MeetingsAPI apiService = Util.getClient().create( MeetingsAPI.class);
                Call<ArrayList< Meetings >> call = apiService.getMeetingsByEmployeeId(employeeId);

                call.enqueue(new Callback<ArrayList< Meetings >>() {
                    @Override
                    public void onResponse( Call<ArrayList< Meetings >> call, Response<ArrayList< Meetings >> response) {
                        int statusCode = response.code();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {


                            if (progressDialog != null&&progressDialog.isShowing())
                                progressDialog.dismiss();
                            ArrayList< Meetings > list = response.body();
                           // long hours=0;
                            long diffHrs = 0;


                            if (list !=null && list.size()!=0) {

                                Collections.sort(list, Meetings.compareMeetings);
                                mTotalMeeting.setText(""+list.size());

                                for(int i=0;i<list.size();i++){

                                    if(list.get(i).getMeetingDate().contains("T")){

                                        String loginTime = list.get(i).getStartTime();
                                        String logoutTime = list.get(i).getEndTime();
                                        String date[] = list.get(i).getMeetingDate().split("T");
                                        Date dates = null;
                                        try {
                                            dates = new SimpleDateFormat("yyyy-MM-dd").parse(date[0]);
                                            String dateValue = new SimpleDateFormat("MMM dd,yyyy").format(dates);

                                            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy hh:mm a");
                                            SimpleDateFormat sdfs = new SimpleDateFormat("MMM dd,yyyy");


                                            try {
                                                dates = new SimpleDateFormat("yyyy-MM-dd").parse(dateValue);
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }

                                            Date fd=null,td=null;
                                            String comDate = new SimpleDateFormat("MMM dd,yyyy").format(dates);


                                            if(loginTime==null||loginTime.isEmpty()){

                                                loginTime = comDate +" 00:00 am";
                                            }

                                            if(logoutTime==null||logoutTime.isEmpty()){

                                                logoutTime = comDate  +" "+new SimpleDateFormat("hh:mm a").format(dates) ;
                                            }

                                            try {
                                                fd = sdf.parse(""+loginTime);
                                                td = sdf.parse(""+logoutTime);

                                                long diff = td.getTime() - fd.getTime();

                                                diffHrs = diffHrs+diff;



                                            } catch (ParseException e) {
                                                e.printStackTrace();

                                            }

                                           /* hours = dateCal(dateValue,list.get(i).getLoginTime(),list.get(i).getLogOutTime());
                                            hourMins = hourMins+hours;*/
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }


                                        /*String date[] = list.get(i).getMeetingDate().split("T");
                                        Date dates = null;
                                        try {
                                            dates = new SimpleDateFormat("yyyy-MM-dd").parse(date[0]);
                                            String dateValue = new SimpleDateFormat("MMM dd,yyyy").format(dates);

                                            String end = list.get(i).getEndTime();

                                            if(end==null||end.isEmpty()){

                                                end = new SimpleDateFormat("hh:mm a").format(new Date());
                                            }

                                            hours = dateCal(dateValue,list.get(i).getStartTime(),end)+hours;
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }*/

                                    }

                                }

                                meetingHour = diffHrs;

                                long avg = diffHrs/list.size();

                                long hourss = loginHour - meetingHour;

                                int minutes = (int) ((diffHrs / (1000*60)) % 60);
                                int hours   = (int) ((diffHrs / (1000*60*60)) % 24);
                                int days   = (int) ((diffHrs / (1000*60*60*24)));


                                DecimalFormat df = new DecimalFormat("00");


                                mMeetingHours.setText(String.format("%02d", days)+"d"+String.format("%02d", hours) +"hr"+String.format("%02d", minutes)+"mins");

                                 minutes = (int) ((avg / (1000*60)) % 60);
                                 hours   = (int) ((avg / (1000*60*60)) % 24);
                                 days   = (int) ((avg / (1000*60*60*24)));

                                mAvgMeeting.setText(String.format("%02d", days)+"d"+String.format("%02d", hours) +"hr"+String.format("%02d", minutes)+"mins");

                                minutes = (int) ((hourss / (1000*60)) % 60);
                                hours   = (int) ((hourss / (1000*60*60)) % 24);
                                days   = (int) ((hourss / (1000*60*60*24)));

                                mIdle.setText(String.format("%02d", days)+"d"+String.format("%02d", hours) +"hr"+String.format("%02d", minutes)+"mins");

                            }else{

                            }

                        }else {


                            Toast.makeText( EmployeesDashBoard.this, "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure( Call<ArrayList< Meetings >> call, Throwable t) {
                        // Log error here since request failed
                        if (progressDialog != null&&progressDialog.isShowing())
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

    private void getTasks(final int employeeId) {


        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading Tasks..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new ThreadExecuter ().execute( new Runnable() {
            @Override
            public void run() {
                TasksAPI apiService = Util.getClient().create( TasksAPI.class);
                Call<ArrayList<Tasks>> call = apiService.getTasksByEmployeeId(employeeId);

                call.enqueue(new Callback<ArrayList<Tasks>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Tasks>> call, Response<ArrayList<Tasks>> response) {
                        int statusCode = response.code();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {


                            if (progressDialog != null&&progressDialog.isShowing())
                                progressDialog.dismiss();
                            ArrayList<Tasks> list = response.body();
                            ArrayList<Tasks> employeeTasks = new ArrayList<>();
                            ArrayList<Tasks> onTask = new ArrayList<>();
                            ArrayList<Tasks> completedTask = new ArrayList<>();

                            long diffHrs = 0;


                            if(list!=null&&list.size()!=0){

                                mTotaltask.setText(String.valueOf(list.size()));

                                for (Tasks task:list) {

                                    if(task.getStatus().equalsIgnoreCase("On-Going")&&(task.getStartDate()!=null&&task.getEndDate()!=null)){
                                        onTask.add(task);

                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                        String login,logout;

                                        login = task.getStartDate();
                                        logout = task.getEndDate();

                                        if(login.contains("T")){
                                            String[] start = login.split("T");
                                            login = start[0];
                                        }

                                        if(logout.contains("T")){
                                            String[] start = logout.split("T");
                                            logout = start[0];
                                        }



                                        Date fd=null,td=null;

                                        if(login==null||login.isEmpty()){

                                            login = sdf.format(new Date());
                                        }

                                        if(logout==null||logout.isEmpty()){

                                            logout = sdf.format(new Date()) ;
                                        }

                                        try {
                                            fd = sdf.parse("" + login);
                                            td = sdf.parse("" + logout);

                                            long diff = td.getTime() - fd.getTime();

                                            diffHrs = diffHrs+diff;

                                        }catch (Exception w){
                                            w.printStackTrace();
                                        }



                                        }else if(task.getStatus().equalsIgnoreCase("Completed")&&(task.getStartDate()!=null&&task.getEndDate()!=null)){
                                        completedTask.add(task);
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                        String login,logout;

                                        login = task.getStartDate();
                                        logout = task.getEndDate();

                                        if(login.contains("T")){
                                            String[] start = login.split("T");
                                            login = start[0];
                                        }

                                        if(logout.contains("T")){
                                            String[] start = logout.split("T");
                                            logout = start[0];
                                        }



                                        Date fd=null,td=null;

                                        if(login==null||login.isEmpty()){

                                            login = sdf.format(new Date());
                                        }

                                        if(logout==null||logout.isEmpty()){

                                            logout = sdf.format(new Date()) ;
                                        }

                                        try {
                                            fd = sdf.parse("" + login);
                                            td = sdf.parse("" + logout);

                                            long diff = td.getTime() - fd.getTime();

                                            diffHrs = diffHrs+diff;


                                        }catch (Exception w){
                                            w.printStackTrace();
                                        }



                                    }

                                }



                                long avghours = diffHrs/list.size();
                                long hourst = avghours*list.size();

                                int minutes = (int) ((avghours / (1000*60)) % 60);
                                int hours   = (int) ((avghours / (1000*60*60)) % 24);
                                int days   = (int) ((avghours / (1000*60*60*24)));

                                long Hours = hourst / (60 * 60 * 1000) % 24;
                                long Minutes = hourst / (60 * 1000) % 60;

                                long avgHours = avghours / (60 * 60 * 1000) % 24;
                                long avgMinutes = avghours / (60 * 1000) % 60;

                                mTaskTime.setText(""+(completedTask.size()));
                                //mAvgTask.setText(""+avgHours+":"+avgMinutes);
                                mAvgTask.setText(String.format("%02d", days)+"d"+String.format("%02d", hours) +"hr"+String.format("%02d", minutes)+"mins");


                            }





                        }else {

                            if (progressDialog != null&&progressDialog.isShowing())
                                progressDialog.dismiss();

                            Toast.makeText( EmployeesDashBoard.this, "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Tasks>> call, Throwable t) {
                        // Log error here since request failed
                        if (progressDialog != null&&progressDialog.isShowing())
                            progressDialog.dismiss();
                        Log.e("TAG", t.toString());
                    }
                });
            }


        });
    }

    private void getLiveLocation(final int employeeId) {


        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading Details..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new ThreadExecuter ().execute( new Runnable() {
            @Override
            public void run() {
                LiveTrackingAPI apiService = Util.getClient().create( LiveTrackingAPI.class);
                Call<ArrayList< LiveTracking >> call = apiService.getLiveTrackingByEmployeeId(employeeId);

                call.enqueue(new Callback<ArrayList< LiveTracking >>() {
                    @Override
                    public void onResponse( Call<ArrayList< LiveTracking >> call, Response<ArrayList< LiveTracking >> response) {
                        int statusCode = response.code();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {


                            if (progressDialog != null&&progressDialog.isShowing())
                                progressDialog.dismiss();
                            ArrayList< LiveTracking > list = response.body();
                            long hours=0;


                            float distance = 0;

                            if (list !=null && list.size()!=0) {

                                for (int i=0;i<list.size();i++) {

                                    if(i==0){
                                        Location locationA = new Location("point A");

                                        locationA.setLatitude(Double.parseDouble(PreferenceHandler.getInstance( EmployeesDashBoard.this).getOrganizationLati()));
                                        locationA.setLongitude(Double.parseDouble(PreferenceHandler.getInstance( EmployeesDashBoard.this).getOrganizationLongi()));

                                        Location locationB = new Location("point B");

                                        locationB.setLatitude(Double.parseDouble(list.get(0).getLatitude()));
                                        locationB.setLongitude(Double.parseDouble(list.get(0).getLongitude()));

                                        distance = distance+locationA.distanceTo(locationB);
                                    }else if(i>0&&i<(list.size()-1)){
                                        Location locationA = new Location("point A");

                                        locationA.setLatitude(Double.parseDouble(list.get(i-1).getLatitude()));
                                        locationA.setLongitude(Double.parseDouble(list.get(i-1).getLongitude()));

                                        Location locationB = new Location("point B");

                                        locationB.setLatitude(Double.parseDouble(list.get(i).getLatitude()));
                                        locationB.setLongitude(Double.parseDouble(list.get(i).getLongitude()));

                                        distance = distance+locationA.distanceTo(locationB);
                                    }else if(i==(list.size()-1)){
                                        Location locationA = new Location("point A");

                                        locationA.setLatitude(Double.parseDouble(list.get(i-1).getLatitude()));
                                        locationA.setLongitude(Double.parseDouble(list.get(i-1).getLongitude()));

                                        Location locationB = new Location("point B");

                                        locationB.setLatitude(Double.parseDouble(list.get(i).getLatitude()));
                                        locationB.setLongitude(Double.parseDouble(list.get(i).getLongitude()));

                                        distance = distance+locationA.distanceTo(locationB);

                                    }

                                }


                                double distanceValue = distance/1000.0;
                                mTotalKm.setText(""+new DecimalFormat("#.##").format(distanceValue));



                            }else{

                            }

                        }else {


                            Toast.makeText( EmployeesDashBoard.this, "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure( Call<ArrayList< LiveTracking >> call, Throwable t) {
                        // Log error here since request failed
                        if (progressDialog != null&&progressDialog.isShowing())
                            progressDialog.dismiss();
                        Log.e("TAG", t.toString());
                    }
                });
            }


        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id)
        {
            case android.R.id.home:

                EmployeesDashBoard.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public class LoginDetailsAdapter extends RecyclerView.Adapter<LoginDetailsAdapter.ViewHolder>{

        private Context context;
        private ArrayList< LoginDetails > list;
        boolean visible;

        public LoginDetailsAdapter(Context context, ArrayList< LoginDetails > list) {

            this.context = context;
            this.list = list;


        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            try{
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.adapter_dash_employee_admin, parent, false);
                ViewHolder viewHolder = new ViewHolder(v);
                return viewHolder;
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }

        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            try{

                final LoginDetails loginDetails = list.get(position);

                if(loginDetails!=null){

                    String loginTime = loginDetails.getLoginTime();
                    String logoutTime = loginDetails.getLogOutTime();
                    String loginDate = loginDetails.getLoginDate();
                    String dateValue = "";

                    if(loginDate.contains("T")){

                        String dateValues[] = loginDate.split("T");
                        dateValue = dateValues[0];

                    }



                    if(loginTime!=null&&!loginTime.isEmpty()){
                        holder.mLoginTime.setText(""+loginTime);
                    }else{
                        holder.mLoginTime.setText("");
                    }

                    if(logoutTime!=null&&!logoutTime.isEmpty()){
                        holder.mLogoutTime.setText(""+logoutTime);
                    }else{
                        holder.mLogoutTime.setText("Working");
                    }


                    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy hh:mm a");
                    SimpleDateFormat sdfs = new SimpleDateFormat("MMM dd,yyyy");

                    Date date=null;
                    try {
                        date = new SimpleDateFormat("yyyy-MM-dd").parse(dateValue);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    Date fd=null,td=null;
                    String comDate = new SimpleDateFormat("MMM dd,yyyy").format(date);


                    if(loginTime==null||loginTime.isEmpty()){

                        loginTime = comDate +" 00:00 am";
                    }

                    if(logoutTime==null||logoutTime.isEmpty()){

                        logoutTime = comDate  +" "+new SimpleDateFormat("hh:mm a").format(new Date()) ;
                    }

                    try {
                        fd = sdf.parse(""+loginTime);
                        td = sdf.parse(""+logoutTime);

                        long diffHrs = td.getTime() - fd.getTime();

                        int minutes = (int) ((diffHrs / (1000*60)) % 60);
                        int hours   = (int) ((diffHrs / (1000*60*60)) % 24);
                        int days   = (int) ((diffHrs / (1000*60*60*24)));



                        holder.mDuration.setText(String.format("%02d", days)+":"+String.format("%02d", hours) +":"+String.format("%02d", minutes));

                    } catch (ParseException e) {
                        e.printStackTrace();

                    }


                }




            }catch (Exception e){
                e.printStackTrace();
            }


        }

        @Override
        public int getItemCount() {
            return list.size();
        }


        class ViewHolder extends RecyclerView.ViewHolder  {

            TextView mLoginTime,mLogoutTime,mDuration;




            public ViewHolder(View itemView) {
                super(itemView);
                itemView.setClickable(true);

                mLoginTime = itemView.findViewById(R.id.report_login);
                mLogoutTime = itemView.findViewById(R.id.report_logout);
                mDuration = itemView.findViewById(R.id.report_hours);



            }
        }

        public void dateCal(String date,String login,String logout,TextView textView){

            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy hh:mm a");
            DecimalFormat df = new DecimalFormat("00");

            Date fd=null,td=null;

            try {
                fd = sdf.parse(""+login);
                td = sdf.parse(""+logout);

                long diff = td.getTime() - fd.getTime();
                long diffDays = diff / (24 * 60 * 60 * 1000);
                long Hours = diff / (60 * 60 * 1000) % 24;
                long Minutes = diff / (60 * 1000) % 60;
                long Seconds = diff / 1000 % 60;

                textView.setText(df.format(Hours)+":"+df.format(Minutes));
            } catch (ParseException e) {
                e.printStackTrace();
            }



        }
    }


}
