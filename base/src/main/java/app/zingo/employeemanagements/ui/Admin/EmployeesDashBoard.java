package app.zingo.employeemanagements.ui.Admin;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import app.zingo.employeemanagements.model.Employee;
import app.zingo.employeemanagements.model.EmployeeImages;
import app.zingo.employeemanagements.model.LoginDetails;
import app.zingo.employeemanagements.model.Meetings;
import app.zingo.employeemanagements.model.Tasks;
import app.zingo.employeemanagements.ui.Common.EmployeeMeetingMap;
import app.zingo.employeemanagements.ui.landing.InternalServerErrorScreen;
import app.zingo.employeemanagements.utils.BaseActivity;
import app.zingo.employeemanagements.utils.Common;
import app.zingo.employeemanagements.WebApi.LoginDetailsAPI;
import app.zingo.employeemanagements.WebApi.MeetingsAPI;
import app.zingo.employeemanagements.WebApi.TasksAPI;
import app.zingo.employeemanagements.base.R;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class EmployeesDashBoard extends BaseActivity {
    CollapsingToolbarLayout collapsingToolbarLayout;
    CircleImageView collapsingToolbarImageView;
    TextView mName,mWorkedDays,mWorkedHours,mTotalMeeting,mMeetingHours,mAvgMeeting,mIdle,mTotaltask,mTotalKm,mAvgTask,mTaskTime;
    RecyclerView mLoginDetails;
    Employee profile;
    int profileId;
    long loginHour=0,meetingHour=0;
    EmployeeImages employeeImages;
    private MeetingsAPI mMeetingsService = Common.getMeetingsAPI ();
    private LoginDetailsAPI mLoginService = Common.getLoginDetailsAPI ();
    private TasksAPI mTasksService = Common.getTaskAPI ();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            setContentView(R.layout.activity_employees_dash_board);
            Toolbar toolbar = findViewById(R.id.collapsing_toolbar);
            setSupportActionBar(toolbar);
            ActionBar actionBar = getSupportActionBar();
            if(actionBar!=null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }

            collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar_layout);
            collapsingToolbarImageView = findViewById(R.id.collapsing_toolbar_image_view);
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
                ArrayList< EmployeeImages > images = profile.getEmployeeImages();
                if(images!=null&&images.size()!=0){
                    employeeImages = images.get(0);
                    if(employeeImages!=null){
                        String base=employeeImages.getImage();
                        if(base != null && !base.isEmpty()){
                            Picasso.with( EmployeesDashBoard.this).load(base).placeholder(R.drawable.profile_image).error(R.drawable.profile_image).into(collapsingToolbarImageView);
                        }
                    }
                }

                try{
                    getLoginDetails (profile.getEmployeeId());
                    getTasks (profile.getEmployeeId());
                }catch (Exception e){
                    e.printStackTrace();
                    Intent error = new Intent( EmployeesDashBoard.this, InternalServerErrorScreen.class);
                    startActivity(error);
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void getMeetingDetails ( final int employeeId){
        mMeetingsService.getMeetingsByEmployeeIdRx (employeeId)
                .subscribeOn( Schedulers.io())
                .observeOn( AndroidSchedulers.mainThread())
                .subscribe(new Observer <ArrayList<Meetings>> () {
                    @Override
                    public void onSubscribe( Disposable d) {

                    }

                    @Override
                    public void onNext(ArrayList<Meetings> result) {
                        if(result!=null){
                            setMeetingsData (result,employeeId);
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

    private void getLoginDetails ( final int employeeId){
        mLoginService.getLoginByEmployeeIdRx (employeeId)
                .subscribeOn( Schedulers.io())
                .observeOn( AndroidSchedulers.mainThread())
                .subscribe(new Observer <ArrayList<LoginDetails>> () {
                    @Override
                    public void onSubscribe( Disposable d) {

                    }

                    @Override
                    public void onNext(ArrayList<LoginDetails> result) {
                        if(result!=null&&result.size ()!=0){
                            setLoginData (result,employeeId);
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

    private void getTasks ( final int employeeId){
        mTasksService.getTasksByEmployeeIdRx (employeeId)
                .subscribeOn( Schedulers.io())
                .observeOn( AndroidSchedulers.mainThread())
                .subscribe(new Observer <ArrayList<Tasks>> () {
                    @Override
                    public void onSubscribe( Disposable d) {

                    }

                    @Override
                    public void onNext(ArrayList<Tasks> result) {
                        if(result!=null){
                            setTasksData (result,employeeId);
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

    private void setMeetingsData ( ArrayList < Meetings > list , int employeeId ) {
        // long hours=0;
        long diffHrs = 0;
        if (list !=null && list.size()!=0) {
            Collections.sort ( list , Meetings.compareMeetings );
            mTotalMeeting.setText ( "" + list.size ( ) );
            for ( int i = 0 ; i < list.size ( ) ; i++ ) {
                if ( list.get ( i ).getMeetingDate ( ).contains ( "T" ) ) {
                    String loginTime = list.get ( i ).getStartTime ( );
                    String logoutTime = list.get ( i ).getEndTime ( );
                    String date[] = list.get ( i ).getMeetingDate ( ).split ( "T" );
                    Date dates = null;
                    try {
                        dates = new SimpleDateFormat ( "yyyy-MM-dd" ).parse ( date[ 0 ] );
                        String dateValue = new SimpleDateFormat ( "MMM dd,yyyy" ).format ( dates );
                        SimpleDateFormat sdf = new SimpleDateFormat ( "MMM dd,yyyy hh:mm a" );
                        SimpleDateFormat sdfs = new SimpleDateFormat ( "MMM dd,yyyy" );
                        try {
                            dates = new SimpleDateFormat ( "yyyy-MM-dd" ).parse ( dateValue );
                        } catch ( ParseException e ) {
                            e.printStackTrace ( );
                        }
                        Date fd = null, td = null;
                        String comDate = new SimpleDateFormat ( "MMM dd,yyyy" ).format ( dates );
                        if ( loginTime == null || loginTime.isEmpty ( ) ) {
                            loginTime = comDate + " 00:00 am";
                        }
                        if ( logoutTime == null || logoutTime.isEmpty ( ) ) {
                            logoutTime = comDate + " " + new SimpleDateFormat ( "hh:mm a" ).format ( dates );
                        }
                        try {
                            fd = sdf.parse ( "" + loginTime );
                            td = sdf.parse ( "" + logoutTime );
                            long diff = td.getTime ( ) - fd.getTime ( );
                            diffHrs = diffHrs + diff;

                        } catch ( ParseException e ) {
                            e.printStackTrace ( );
                        }

                    } catch ( ParseException e ) {
                        e.printStackTrace ( );
                    }
                }
            }
            meetingHour = diffHrs;
            long avg = diffHrs / list.size ( );
            long hourss = loginHour - meetingHour;
            int minutes = ( int ) ( ( diffHrs / ( 1000 * 60 ) ) % 60 );
            int hours = ( int ) ( ( diffHrs / ( 1000 * 60 * 60 ) ) % 24 );
            int days = ( int ) ( ( diffHrs / ( 1000 * 60 * 60 * 24 ) ) );
            DecimalFormat df = new DecimalFormat ( "00" );
            mMeetingHours.setText ( String.format ( "%02d" , days ) + "d" + String.format ( "%02d" , hours ) + "hr" + String.format ( "%02d" , minutes ) + "mins" );
            minutes = ( int ) ( ( avg / ( 1000 * 60 ) ) % 60 );
            hours = ( int ) ( ( avg / ( 1000 * 60 * 60 ) ) % 24 );
            days = ( int ) ( ( avg / ( 1000 * 60 * 60 * 24 ) ) );
            mAvgMeeting.setText ( String.format ( "%02d" , days ) + "d" + String.format ( "%02d" , hours ) + "hr" + String.format ( "%02d" , minutes ) + "mins" );
            minutes = ( int ) ( ( hourss / ( 1000 * 60 ) ) % 60 );
            hours = ( int ) ( ( hourss / ( 1000 * 60 * 60 ) ) % 24 );
            days = ( int ) ( ( hourss / ( 1000 * 60 * 60 * 24 ) ) );
            mIdle.setText ( String.format ( "%02d" , days ) + "d" + String.format ( "%02d" , hours ) + "hr" + String.format ( "%02d" , minutes ) + "mins" );
        }

    }

    private void setLoginData ( ArrayList < LoginDetails > list , int employeeId ) {
        long diffHrs=0;
        Collections.sort(list, LoginDetails.compareLogin);
        LoginDetailsAdapter adapter = new LoginDetailsAdapter( EmployeesDashBoard.this,list);
        mLoginDetails.setAdapter(adapter);
        ArrayList<String> dateList = new ArrayList<>();
        String comDate = null;
        for(LoginDetails loginDetails:list){
            String loginTime = loginDetails.getLoginTime();
            String logoutTime = loginDetails.getLogOutTime();
            diffHrs=workingTimeDifference ( list,null,null,diffHrs );
            diffHrs++;
            System.out.println ( "Suree Count :"+diffHrs );
            /*
            if(loginDetails.getLoginDate().contains("T")){
                String loginTime = loginDetails.getLoginTime();
                String logoutTime = loginDetails.getLogOutTime();
                String date[] = loginDetails.getLoginDate().split("T");
                Date dates = null;
                try {
                    dates = new SimpleDateFormat("yyyy-MM-dd").parse(date[0]);
                    String dateValue = new SimpleDateFormat("MMM dd,yyyy").format(dates);
                    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy hh:mm a");
                    try {
                        dates = new SimpleDateFormat("yyyy-MM-dd").parse(dateValue);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Date fd=null,td=null;
                     comDate = new SimpleDateFormat("MMM dd,yyyy").format(dates);

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
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                dateList.add(date[0]);
            }*/

        }
        System.out.println ( "Suree :"+diffHrs );
        System.out.println ( "Suree Login Time : "+(workingTime(workingTimeDifference ( list,null,comDate,diffHrs ),"Logout")));
        mWorkedHours.setText(""+workingTime(workingTimeDifference ( list,null,comDate,diffHrs ),"Logout")/*workingTime(workingTimeDifference ( list,null,comDate,0 ),"Logout")*/);

        if(dateList!=null&&dateList.size()!=0){
            Set<String> s = new LinkedHashSet<String>(dateList);
            mWorkedDays.setText(""+s.size());
        }

        loginHour = diffHrs;
        int minutes = (int) ((diffHrs / (1000*60)) % 60);
        int hours   = (int) ((diffHrs / (1000*60*60)) % 24);
        int days   = (int) ((diffHrs / (1000*60*60*24)));

        DecimalFormat df = new DecimalFormat("00");
      //  mWorkedHours.setText(String.format("%02d", days)+"d"+String.format("%02d", hours) +"hr"+String.format("%02d", minutes)+"mins");
        getMeetingDetails (employeeId);
    }

    private void setTasksData ( ArrayList < Tasks > list , int employeeId ) {
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
            mAvgTask.setText(String.format("%02d", days)+"d"+String.format("%02d", hours) +"hr"+String.format("%02d", minutes)+"mins");
        }
    }


    public long dateCal(String date,String login,String logout){
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy hh:mm a");
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

            return  diff;
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if ( id == android.R.id.home ) {
            EmployeesDashBoard.this.finish ( );
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
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_dash_employee_admin, parent, false);
                return new ViewHolder(v);
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

                    if(loginTime!=null&&!loginTime.isEmpty()){ holder.mLoginTime.setText(""+loginTime); }
                    else{ holder.mLoginTime.setText(""); }
                    if(logoutTime!=null&&!logoutTime.isEmpty()){ holder.mLogoutTime.setText(""+logoutTime); }else
                        { holder.mLogoutTime.setText("Working"); }

                    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy hh:mm a");

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

       /* public void dateCal(String date,String login,String logout,TextView textView){

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
        }*/
    }
}
