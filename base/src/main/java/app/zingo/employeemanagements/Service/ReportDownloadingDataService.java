package app.zingo.employeemanagements.Service;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.SplittableRandom;
import java.util.Timer;
import java.util.TimerTask;

import app.zingo.employeemanagements.Model.Employee;
import app.zingo.employeemanagements.Model.Expenses;
import app.zingo.employeemanagements.Model.LiveTracking;
import app.zingo.employeemanagements.Model.LoginDetails;
import app.zingo.employeemanagements.Model.Meetings;
import app.zingo.employeemanagements.Model.ObservableReportData;
import app.zingo.employeemanagements.Model.ReportDataEmployee;
import app.zingo.employeemanagements.Model.ReportDataModel;
import app.zingo.employeemanagements.Model.Tasks;
import app.zingo.employeemanagements.UI.Common.ReportManagementScreen;
import app.zingo.employeemanagements.UI.NewEmployeeDesign.BreakPurpose;
import app.zingo.employeemanagements.Utils.PreferenceHandler;
import app.zingo.employeemanagements.Utils.ThreadExecuter;
import app.zingo.employeemanagements.Utils.TrackGPS;
import app.zingo.employeemanagements.Utils.Util;
import app.zingo.employeemanagements.WebApi.EmployeeApi;
import app.zingo.employeemanagements.WebApi.LiveTrackingAPI;
import app.zingo.employeemanagements.WebApi.MultpleAPI;
import app.zingo.employeemanagements.base.BuildConfig;
import app.zingo.employeemanagements.base.R;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Alignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.functions.FuncN;
import rx.schedulers.Schedulers;

public class ReportDownloadingDataService extends Service {

    private static final String TAG_FOREGROUND_SERVICE = "FOREGROUND_SERVICE";

    public static final String ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE";

    public static final String ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE";


    ArrayList<ReportDataEmployee> reportDataEmployeeArrayList;

    ArrayList<ObservableReportData> data = new ArrayList<>();

    int totalDataCount = 0;

    


    public ReportDownloadingDataService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG_FOREGROUND_SERVICE, "My foreground service onCreate().");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startMyOwnForeground(){
        String NOTIFICATION_CHANNEL_ID = "app.zingo.employeemanagements";
        String channelName = "Report Data Downloading";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Report Data Downloading")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null)
        {
            String action = intent.getAction();
            String startDate = "";
            String endDate = "";

            Bundle bundle = intent.getExtras();
            if(bundle!=null){

                startDate = bundle.getString("StartDate");
                endDate = bundle.getString("EndDate");
            }


            switch (action)
            {
                case ACTION_START_FOREGROUND_SERVICE:

                    if(startDate!=null&&!startDate.isEmpty()&&endDate!=null&&!endDate.isEmpty()){

                        startForegroundService(startDate,endDate);
                    }

                    //Toast.makeText(getApplicationContext(), "Foreground service is started.", Toast.LENGTH_LONG).show();
                    break;
                case ACTION_STOP_FOREGROUND_SERVICE:
                    stopForegroundService();
                    //Toast.makeText(getApplicationContext(), "Foreground service is stopped.", Toast.LENGTH_LONG).show();
                    break;

            }
        }
        return START_STICKY;

    }

    /* Used to build and start foreground service. */
    private void startForegroundService(final String startDate,final String endDate)
    {
        Log.d(TAG_FOREGROUND_SERVICE, "Start foreground service.");



        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            String NOTIFICATION_CHANNEL_ID = "app.zingo.employeemanagements";
            String channelName = "Report Data Downloading";
            NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(chan);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
            Notification notification = notificationBuilder.setOngoing(true)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Report Data Downloading")
                    .setPriority(NotificationManager.IMPORTANCE_MIN)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .build();
            startForeground(2, notification);
           //call function for employees

            getEmployees(startDate,endDate);


        }else{

            // Create notification default intent.
            Intent intent = new Intent();
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

            //Above Android 8 create channel Id for notification
            String CHANNEL_ID = ""+ 1;// The id of the channel.
            CharSequence name = "Zingo" ;// The user-visible name of the channel.
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel mChannel=null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            }

            // Create notification builder.
            Notification.Builder builder = null;

            builder = new Notification.Builder(this)
                    .setContentTitle("Report Data Downloading")
                    .setPriority(Notification.PRIORITY_MAX)
                    .setWhen(System.currentTimeMillis())
                    .setFullScreenIntent(pendingIntent,false)
                    .setSmallIcon(R.mipmap.ic_launcher);

            builder.setDefaults(Notification.DEFAULT_VIBRATE);
            builder.setLights(Color.YELLOW, 1000, 300);

            Notification notification = builder.build();

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                assert notificationManager != null;
                notificationManager.createNotificationChannel(mChannel);
            }

            // notificationManager.notify(m, builder.build());

            // Start foreground service.
           //call function for employees
            getEmployees(startDate,endDate);

            startForeground(1, notification);
        }




    }

    private void stopForegroundService()
    {
        Log.d(TAG_FOREGROUND_SERVICE, "Stop foreground service.");

        // Stop foreground service and remove the notification.
        stopForeground(true);

        // Stop the foreground service.
        stopSelf();
    }


    private void getEmployees(final String startDateValue, final String endDateValue){




        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {
                EmployeeApi apiService = Util.getClient().create(EmployeeApi.class);
                Call<ArrayList<Employee>> call = apiService.getEmployeesByOrgId(PreferenceHandler.getInstance(ReportDownloadingDataService.this).getCompanyId());

                call.enqueue(new Callback<ArrayList<Employee>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Employee>> call, Response<ArrayList<Employee>> response) {
                        int statusCode = response.code();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {



                            ArrayList<Employee> list = response.body();
                            reportDataEmployeeArrayList = new ArrayList<>();



                            if (list !=null && list.size()!=0) {


                                ArrayList<Employee> employees = new ArrayList<>();

                                for(int i=0;i<list.size();i++){

                                    if(list.get(i).getUserRoleId()!=2){

                                        employees.add(list.get(i));


                                    }
                                }


                                if(employees!=null&&employees.size()!=0){


                                    List<Date> dates = getDates(startDateValue, endDateValue);

                                    totalDataCount = employees.size()*dates.size();
                                    for(Date date:dates)
                                    {

                                  /*  adapter = new ReportManagementScreen.ReportDetailEmployeeAdapter(ReportManagementScreen.this,employees,dateValue);
                                    mEmpList.setAdapter(adapter);*/

                                    for (Employee e:employees) {

                                            ReportDataEmployee reportDataEmployee = new ReportDataEmployee();
                                            reportDataEmployee.setName(e.getEmployeeName());
                                            getData(reportDataEmployee,e,new SimpleDateFormat("yyyy-MM-dd").format(date));

                                        }

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


                        Log.e("TAG", t.toString());
                    }
                });
            }


        });
    }

    private static List<Date> getDates(String dateString1, String dateString2)
    {
        ArrayList<Date> dates = new ArrayList<Date>();
        DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");

        Date date1 = null;
        Date date2 = null;

        try {
            date1 = df1 .parse(dateString1);
            date2 = df1 .parse(dateString2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);


        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        while(!cal1.after(cal2))
        {
            dates.add(cal1.getTime());
            cal1.add(Calendar.DATE, 1);
        }
        return dates;
    }

    public void getData(final ReportDataEmployee reportDataEmployee,final Employee dto,final String dateValue){


        Date date=null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(dateValue);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        LoginDetails ld  = new LoginDetails();
        ld.setEmployeeId(dto.getEmployeeId());
        ld.setLoginDate(new SimpleDateFormat("MM/dd/yyyy").format(date));
        String logDate = new SimpleDateFormat("MMM dd,yyyy").format(date);
        //getLoginDetails(ld,holder.mLogin,holder.mLogout,holder.mHours,logDate);


        LiveTracking lv = new LiveTracking();
        lv.setEmployeeId(dto.getEmployeeId());
        lv.setTrackingDate(new SimpleDateFormat("MM/dd/yyyy").format(date));
        //getLiveLocation(lv,holder.mKm);

        Meetings md  = new Meetings();
        md.setEmployeeId(dto.getEmployeeId());
        md.setMeetingDate(new SimpleDateFormat("MM/dd/yyyy").format(date));
        //getMeetingsDetails(md,holder.mVisits);

        //getTasks(dto.getEmployeeId(),"Completed",holder.mTasks,dateValue);
        //getExpense(dto.getEmployeeId(),holder.mExpenses, holder.mExpAmt,dateValue);

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://zingolocals.azurewebsites.net/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();


        MultpleAPI backendApi = Util.getClient().create(MultpleAPI.class);

        List<Observable<?>> requests = new ArrayList<>();

      /*  Observable<ArrayList<LoginDetails>> obs1= backendApi.getLoginByEmployeeIdAndDate(ld);
        Observable<ArrayList<LiveTracking>> obs2=backendApi.getLiveTrackingByEmployeeIdAndDate(lv);
        Observable<ArrayList<Meetings>> obs3= backendApi.getMeetingsByEmployeeIdAndDate(md);1008974
        Observable<ArrayList<Tasks>> obs4=backendApi.getTasksByEmployeeId(dto.getEmployeeId());1008985
        Observable<ArrayList<Expenses>> obs5= backendApi.getExpenseByEmployeeIdAndOrganizationId(PreferenceHandler.getInstance(ReportBulkdataScreen.this).getCompanyId(),dto.getEmployeeId());
*/
         requests.add(backendApi.getLoginByEmployeeIdAndDate(ld));
        requests.add(backendApi.getLiveTrackingByEmployeeIdAndDate(lv));
         requests.add(backendApi.getMeetingsByEmployeeIdAndDate(md));
        requests.add(backendApi.getTasksByEmployeeId(dto.getEmployeeId()));
        requests.add(backendApi.getExpenseByEmployeeIdAndOrganizationId(PreferenceHandler.getInstance(ReportDownloadingDataService.this).getCompanyId(),dto.getEmployeeId()));



        Observable<ObservableReportData> combinedObservable = Observable.zip(requests, new FuncN<ObservableReportData>() {
            @Override
            public ObservableReportData call(Object... args) {

                ObservableReportData result = new ObservableReportData();

                if(args!=null&&args.length==5){

                    result.setLoginDetailsArrayList((ArrayList<LoginDetails>)args[0]);
                    result.setLiveTrackingArrayList((ArrayList<LiveTracking>)args[1]);
                    result.setMeetingsArrayList((ArrayList<Meetings>)args[2]);
                    result.setTasksArrayList((ArrayList<Tasks>)args[3]);
                    result.setExpensesArrayList((ArrayList<Expenses>)args[4]);
                    result.setEmpName(reportDataEmployee.getName());

                    data.add(result);
                    checkData(data,dateValue);
                }

                return result;
            }
        });






        combinedObservable.observeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<ObservableReportData>() {
                    @Override
                    public void onCompleted() {

                        System.out.println(" ");
                    }

                    @Override
                    public void onError(Throwable throwable) {
                    }

                    @Override
                    public void onNext(ObservableReportData wrapperObj) {
                        System.out.println("Data "+wrapperObj.getLoginDetailsArrayList().size()+" ");
                    }
                });




    }

    public void checkData(ArrayList<ObservableReportData> datapassing,final String dateValue){

        if(datapassing!=null&&datapassing.size()!=0&&datapassing.size()==totalDataCount){

            //stopForegroundService();

            reportDataEmployeeArrayList = new ArrayList<>();


            for (ObservableReportData data:datapassing) {

                ReportDataEmployee reportDataEmployee = new ReportDataEmployee();

                ArrayList<LoginDetails> loginDetailsArrayList = data.getLoginDetailsArrayList();

                if (loginDetailsArrayList !=null && loginDetailsArrayList.size()!=0) {



                    String loginTime = loginDetailsArrayList.get(0).getLoginTime();
                    String logoutTime = loginDetailsArrayList.get(loginDetailsArrayList.size()-1).getLogOutTime();

                    if(loginTime!=null&&!loginTime.isEmpty()){
                        reportDataEmployee.setLoginTime(""+loginTime);
                    }else{
                        reportDataEmployee.setLoginTime("");
                    }

                    if(logoutTime!=null&&!logoutTime.isEmpty()){
                        reportDataEmployee.setLogoutTime(""+logoutTime);
                    }else{
                        reportDataEmployee.setLogoutTime("Working");
                    }

                    long diffHrs = 0;

                    for (LoginDetails lg:loginDetailsArrayList) {

                        Date date = null;
                        try {
                            date = new SimpleDateFormat("yyyy-MM-dd").parse(dateValue);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        String comDate = new SimpleDateFormat("MMM dd,yyyy").format(date);

                        if((loginTime==null||loginTime.isEmpty())&&(lg.getLogOutTime()==null||lg.getLogOutTime().isEmpty())){

                        }else{
                            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy hh:mm a");
                            SimpleDateFormat sdfs = new SimpleDateFormat("MMM dd,yyyy");

                            Date fd=null,td=null;

                            String logoutT = lg.getLogOutTime();
                            String loginT = lg.getLoginTime();

                            if(loginT==null||loginT.isEmpty()){

                                loginT = comDate +" 00:00 am";
                            }else if(loginT.contains(" a.m.")){


                                loginT = loginT.replace(" a.m."," AM");

                            }else if(logoutT.contains(" p.m.")){

                                loginT = loginT.replace(" p.m."," PM");
                            }

                            if(logoutT==null||logoutT.isEmpty()){

                                logoutT = comDate  +" "+new SimpleDateFormat("hh:mm a").format(new Date()) ;
                            }else if(logoutT.contains(" a.m.")){


                                logoutT = logoutT.replace(" a.m."," AM");

                            }else if(logoutT.contains(" p.m.")){

                                logoutT = logoutT.replace(" p.m."," PM");
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

                    int minutes = (int) ((diffHrs / (1000*60)) % 60);
                    int hours   = (int) ((diffHrs / (1000*60*60)) % 24);
                    int days   = (int) ((diffHrs / (1000*60*60*24)));


                    DecimalFormat df = new DecimalFormat("00");

                    reportDataEmployee.setHours(String.format("%02d", days)+":"+String.format("%02d", hours) +":"+String.format("%02d", minutes));

                }else{

                    reportDataEmployee.setLoginTime("ABSENT");

                    reportDataEmployee.setLogoutTime("ABSENT");
                    reportDataEmployee.setHours("ABSENT");


                    // Toast.makeText(DailyTargetsForEmployeeActivity.this, "No Tasks given for this employee ", Toast.LENGTH_SHORT).show();

                }

                ArrayList<Meetings> meetingsArrayList = data.getMeetingsArrayList();

                if (meetingsArrayList !=null && meetingsArrayList.size()!=0) {

                    reportDataEmployee.setVisits(""+meetingsArrayList.size());


                }else{
                    reportDataEmployee.setVisits("0"+meetingsArrayList.size());

                    // Toast.makeText(DailyTargetsForEmployeeActivity.this, "No Tasks given for this employee ", Toast.LENGTH_SHORT).show();

                }


                ArrayList<Tasks> tasksArrayList = data.getTasksArrayList();
                ArrayList<Tasks> todayTasks = new ArrayList<>();


                Date date = new Date();
                Date adate = new Date();
                Date edate = new Date();



                try {
                    date = new SimpleDateFormat("yyyy-MM-dd").parse(dateValue);


                } catch (Exception e) {
                    e.printStackTrace();
                }



                if (tasksArrayList !=null && tasksArrayList.size()!=0) {



                    for (Tasks task:tasksArrayList) {



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





                            }
                        }






                    }

                    if(todayTasks!=null&&todayTasks.size()!=0){

                        reportDataEmployee.setTasks(""+todayTasks.size());





                    }else{
                        reportDataEmployee.setTasks("0");
                    }



                }else{

                    reportDataEmployee.setTasks("0");
                    // Toast.makeText(DailyTargetsForEmployeeActivity.this, "No Tasks given for this employee ", Toast.LENGTH_SHORT).show();

                }





                ArrayList<Expenses> expensesArrayList = data.getExpensesArrayList();
                ArrayList<Expenses> todayExpenses = new ArrayList<>();


                Date dateExpense = new Date();
                Date adateExpense = new Date();
                Date edateExpense = new Date();



                try {
                    dateExpense = new SimpleDateFormat("yyyy-MM-dd").parse(dateValue);


                } catch (Exception e) {
                    e.printStackTrace();
                }



                if (expensesArrayList !=null && expensesArrayList.size()!=0) {


                    double amt = 0;

                    for (Expenses expenses:expensesArrayList) {



                        String froms = expenses.getDate();


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



                            if(dateExpense.getTime() == afromDate.getTime() ){



                                todayExpenses.add(expenses);
                                amt = amt+expenses.getAmount();


                            }
                        }






                    }

                    if(todayTasks!=null&&todayTasks.size()!=0){



                        reportDataEmployee.setExpenses(""+todayTasks.size());
                        reportDataEmployee.setExpensesAmt(""+new DecimalFormat("#.##").format(amt));




                    }else{
                        reportDataEmployee.setExpenses("0");
                        reportDataEmployee.setExpensesAmt("0");


                    }



                }else{

                    reportDataEmployee.setExpenses("0");
                    reportDataEmployee.setExpensesAmt("0");
                    // Toast.makeText(DailyTargetsForEmployeeActivity.this, "No Tasks given for this employee ", Toast.LENGTH_SHORT).show();

                }






                ArrayList<LiveTracking> liveTrackingArrayList = data.getLiveTrackingArrayList();
                float distance = 0;


                if (liveTrackingArrayList !=null && liveTrackingArrayList.size()!=0) {

                    Collections.sort(liveTrackingArrayList,LiveTracking.compareLiveTrack);




                    double lati = 0,lngi=0;
                    double latis = 0,lngis=0;

                    Location locationA = new Location("point A");
                    Location locationB = new Location("point B");

                    for(int i=1;i<liveTrackingArrayList.size();i++){


                        if(liveTrackingArrayList.get(i).getLongitude()!=null||liveTrackingArrayList.get(i).getLatitude()!=null){

                            double lat = Double.parseDouble(liveTrackingArrayList.get(i).getLatitude());
                            double lng = Double.parseDouble(liveTrackingArrayList.get(i).getLongitude());



                            if(lat==0&&lng==0){

                            }else{

                                if(liveTrackingArrayList.size()==1){

                                    reportDataEmployee.setKms("0");

                                }else{


                                    lati = Double.parseDouble(liveTrackingArrayList.get(i-1).getLatitude());
                                    lngi = Double.parseDouble(liveTrackingArrayList.get(i-1).getLongitude());


                                    if(lati==0&&lngi==0){

                                        lati = Double.parseDouble(liveTrackingArrayList.get(i-1).getLatitude());
                                        lngi = Double.parseDouble(liveTrackingArrayList.get(i-1).getLongitude());


                                    }else{

                                        latis = lati;
                                        lngis = lngi;

                                        locationA.setLatitude(latis);
                                        locationA.setLongitude(lngis);


                                        locationB.setLatitude(Double.parseDouble(liveTrackingArrayList.get(i).getLatitude()));
                                        locationB.setLongitude(Double.parseDouble(liveTrackingArrayList.get(i).getLongitude()));

                                        distance = distance+locationA.distanceTo(locationB);
                                        double kms = distance/1000.0;
                                        double miles = distance*0.000621371192;
                                        reportDataEmployee.setKms(new DecimalFormat("#.##").format(kms)+" Km/"+new DecimalFormat("#.##").format(miles)+" miles");




                                    }


                                }



                            }



                        }


                    }





                }else{

                    reportDataEmployee.setKms("0");

                }

                reportDataEmployee.setName(data.getEmpName());

                reportDataEmployeeArrayList.add(reportDataEmployee);

                if(reportDataEmployeeArrayList.size()==datapassing.size()){

                    if(generateReport(reportDataEmployeeArrayList)){
                        reportDataEmployeeArrayList = new ArrayList<>();
                    }

                }


            }





        }


    }

    private boolean generateReport(ArrayList<ReportDataEmployee> list) {
        boolean success = false;
        WritableWorkbook workbook = null;

        stopForegroundService();

        try {
            File sd = Environment.getExternalStorageDirectory();
            String csvFile = "TeamActivity"+new SimpleDateFormat("ddMMyy").format(new Date())+".xls";

            File directory = new File(sd.getAbsolutePath()+"/TeamActivity");
            //create directory if not exist
            if (!directory.exists() && !directory.isDirectory()) {
                directory.mkdirs();
            }
            File file = new File(directory, csvFile);
            String sheetName = "TeamActivity_"+new SimpleDateFormat("ddMMyy").format(new Date());//name of sheet

            WorkbookSettings wbSettings = new WorkbookSettings();
            wbSettings.setLocale(new Locale("en", "EN"));

            workbook = Workbook.createWorkbook(file, wbSettings);
            //Excel sheet name. 0 represents first sheet
            WritableSheet sheet = workbook.createSheet(sheetName, 0);


            sheet.addCell(new Label(5,0,PreferenceHandler.getInstance(ReportDownloadingDataService.this).getCompanyName()));

            sheet.mergeCells(5,0,10,0);

            sheet.mergeCells(5,1,10,1);
            sheet.addCell(new Label(5,2,"Chart As On "+new SimpleDateFormat("dd/MM/yyyy").format(new Date())));
            sheet.mergeCells(5,2,8,2);


            sheet.addCell(new Label(3,3,"Generated On "+new SimpleDateFormat("dd/MM/yyyy, hh:mm aa").format(new Date())));
            sheet.mergeCells(3,3,6,3);

            sheet.addCell(new Label(7,3,"User : "+PreferenceHandler.getInstance(ReportDownloadingDataService.this).getUserFullName()));
            sheet.mergeCells(7,3,10,3);

            sheet.setColumnView(0, 20);
            sheet.setColumnView(1, 20);
            sheet.setColumnView(2, 20);
            sheet.setColumnView(3, 25);
            sheet.setColumnView(4, 10);
            sheet.setColumnView(5, 15);
            sheet.setColumnView(6, 20);
            sheet.setColumnView(7, 15);
            sheet.setColumnView(8, 15);
            sheet.setColumnView(9, 15);
            sheet.setColumnView(10, 15);
            sheet.setColumnView(11, 8);
            sheet.setColumnView(12, 15);
            sheet.setColumnView(13, 15);
            sheet.setColumnView(14, 15);
            sheet.setColumnView(15, 15);
            sheet.setColumnView(16, 15);
            sheet.setColumnView(17, 15);
            sheet.setColumnView(18, 15);
            sheet.setColumnView(19, 15);
            sheet.setColumnView(20, 15);
            sheet.setColumnView(21, 15);
            sheet.setColumnView(22, 15);
            sheet.setColumnView(23, 15);
            sheet.setColumnView(24, 15);

            WritableCellFormat cellFormats = new WritableCellFormat();
            cellFormats.setAlignment(Alignment.CENTRE);

            WritableCellFormat cellFormatL = new WritableCellFormat();
            cellFormats.setAlignment(Alignment.LEFT);





            sheet.addCell(new Label(0, 6, "Name",cellFormats));
            sheet.addCell(new Label(1, 6, "Login",cellFormats));
            sheet.addCell(new Label(2, 6, "Logout",cellFormats));
            sheet.addCell(new Label(3, 6, "Hours",cellFormats));
            sheet.addCell(new Label(4, 6, "Visits",cellFormats));
            sheet.addCell(new Label(5, 6, "Tasks",cellFormats));
            sheet.addCell(new Label(6, 6, "Expense",cellFormats));
            sheet.addCell(new Label(7, 6, "Expense Amount",cellFormats));
            sheet.addCell(new Label(8, 6, "Kms",cellFormats));


            if(list != null)
            {

                for (int i=0;i<list.size();i++)
                {
                    ReportDataEmployee rd = list.get(i);

                    if(rd !=null )
                    {
                        /*CellView cell=sheet.getColumnView(i);
                        cell.setAutosize(true);
                        sheet.setColumnView(i, cell);*/
                        sheet.addCell(new Label(0, i+7, rd.getName(),cellFormats));
                        sheet.addCell(new Label(1, i+7, rd.getLoginTime(),cellFormats));
                        sheet.addCell(new Label(2, i+7, rd.getLogoutTime(),cellFormats));
                        sheet.addCell(new Label(3, i+7, rd.getHours(),cellFormats));
                        sheet.addCell(new Label(4, i+7, rd.getVisits(),cellFormats));
                        sheet.addCell(new Label(5, i+7, rd.getTasks(),cellFormats));
                        sheet.addCell(new Label(6, i+7, rd.getExpenses(),cellFormats));
                        sheet.addCell(new Label(7, i+7, rd.getExpensesAmt(),cellFormats));
                        sheet.addCell(new Label(8, i+7, rd.getKms(),cellFormats));



                    }
                }


            }

            workbook.write();
            System.out.println("Your file is stored in "+file.toString());

            return true;
        } catch (WriteException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        finally {
            if(workbook != null)
            {
                try {
                    workbook.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (WriteException e) {
                    e.printStackTrace();
                }
            }
        }


    }
}