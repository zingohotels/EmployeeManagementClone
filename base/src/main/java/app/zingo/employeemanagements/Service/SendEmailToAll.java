package app.zingo.employeemanagements.Service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import app.zingo.employeemanagements.Model.EmailData;
import app.zingo.employeemanagements.Model.Employee;
import app.zingo.employeemanagements.Model.LiveTracking;
import app.zingo.employeemanagements.Model.LoginDetails;
import app.zingo.employeemanagements.Model.Meetings;
import app.zingo.employeemanagements.Model.Organization;
import app.zingo.employeemanagements.Model.ReportDataEmployee;
import app.zingo.employeemanagements.Model.ReportDataModel;
import app.zingo.employeemanagements.Utils.ThreadExecuter;
import app.zingo.employeemanagements.Utils.Util;
import app.zingo.employeemanagements.WebApi.EmployeeApi;
import app.zingo.employeemanagements.WebApi.OrganizationApi;
import app.zingo.employeemanagements.WebApi.SendEmailAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendEmailToAll extends Service {

    ReportDataModel reportDataModel;
    ArrayList<ReportDataEmployee> reportDataEmployees;

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub

        //getEmployees(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        try {
            getCompany();
        } catch (Exception e) {
            e.printStackTrace();
        }
       // sendEmail();

        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        // TODO Auto-generated method stub
        Intent restartService = new Intent(getApplicationContext(),
                this.getClass());
        restartService.setPackage(getPackageName());
        PendingIntent restartServicePI = PendingIntent.getService(
                getApplicationContext(), 1, restartService,
                PendingIntent.FLAG_ONE_SHOT);

        //Restart the service once it has been killed android


        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 100, restartServicePI);

    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();

        System.out.println("Location 1");

        //start a separate thread and start listening to your network object
    }


    public void getCompany() {

        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {

                final OrganizationApi subCategoryAPI = Util.getClient().create(OrganizationApi.class);
                Call<ArrayList<Organization>> getProf = subCategoryAPI.getOrganization();
                //Call<ArrayList<Blogs>> getBlog = blogApi.getBlogs();

                getProf.enqueue(new Callback<ArrayList<Organization>>() {

                    @Override
                    public void onResponse(Call<ArrayList<Organization>> call, Response<ArrayList<Organization>> response) {

                        if (response.code() == 200||response.code() == 201||response.code() == 204)
                        {

                            ArrayList<Organization> list = response.body();

                            if(list!=null&&list.size()!=0){


                                for (Organization org:list) {

                                    getEmployees(new SimpleDateFormat("yyyy-MM-dd").format(new Date()),org.getOrganizationId());

                                }



                            }



                        }else{

                            //Toast.makeText(BasicPlanScreen.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Organization>> call, Throwable t) {

                       // Toast.makeText(BasicPlanScreen.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                    }
                });

            }

        });
    }

    private void getEmployees(final String dateValue,final int orgId){



        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {
                EmployeeApi apiService = Util.getClient().create(EmployeeApi.class);
                Call<ArrayList<Employee>> call = apiService.getEmployeesByOrgId(orgId);

                call.enqueue(new Callback<ArrayList<Employee>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Employee>> call, Response<ArrayList<Employee>> response) {
                        int statusCode = response.code();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {



                            ArrayList<Employee> list = response.body();
                            reportDataModel = new ReportDataModel();



                            if (list !=null && list.size()!=0) {

                                ArrayList<Employee> employees = new ArrayList<>();

                                for(int i=0;i<list.size();i++){


                                    if(list.get(i).getUserRoleId()==2){
                                        reportDataModel.setMailID(list.get(i).getPrimaryEmailAddress());
                                        break;
                                    }


                                }


                                if(list!=null&&list.size()!=0){

                                    reportDataEmployees = new ArrayList<>();

                                    for(int j=0;j<list.size();j++){

                                        Date date=null;
                                        try {
                                            date = new SimpleDateFormat("yyyy-MM-dd").parse(dateValue);
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }


                                        LoginDetails ld  = new LoginDetails();
                                        ld.setEmployeeId(list.get(j).getEmployeeId());
                                        ld.setLoginDate(new SimpleDateFormat("MM/dd/yyyy").format(date));
                                        String logDate = new SimpleDateFormat("MMM dd,yyyy").format(date);
                                        //getLoginDetails(ld,logDate);


                                        LiveTracking lv = new LiveTracking();
                                        lv.setEmployeeId(list.get(j).getEmployeeId());
                                        lv.setTrackingDate(new SimpleDateFormat("MM/dd/yyyy").format(date));
                                       // getLiveLocation(lv);

                                        Meetings md  = new Meetings();
                                        md.setEmployeeId(list.get(j).getEmployeeId());
                                        md.setMeetingDate(new SimpleDateFormat("MM/dd/yyyy").format(date));
                                       // getMeetingsDetails(md);

                                        //getTasks(list.get(j).getEmployeeId(),"Completed",dateValue);
                                        //getExpense(list.get(j).getEmployeeId(),dateValue);
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

                        Log.e("TAG", t.toString());
                    }
                });
            }


        });
    }

 /*   private void getLoginDetails(final LoginDetails loginDetails, final String comDate){




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



                            ArrayList<LoginDetails> list = response.body();

                            if (list !=null && list.size()!=0) {



                                String loginTime = list.get(0).getLoginTime();
                                String logoutTime = list.get(list.size()-1).getLogOutTime();





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

                                int minutes = (int) ((diffHrs / (1000*60)) % 60);
                                int hours   = (int) ((diffHrs / (1000*60*60)) % 24);
                                int days   = (int) ((diffHrs / (1000*60*60*24)));


                                DecimalFormat df = new DecimalFormat("00");



                            }else{



                                // Toast.makeText(DailyTargetsForEmployeeActivity.this, "No Tasks given for this employee ", Toast.LENGTH_SHORT).show();

                            }

                        }else {



                            //Toast.makeText(DailyTargetsForEmployeeActivity.this, "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<LoginDetails>> call, Throwable t) {
                        // Log error here since request failed
                       *//* if (progressDialog!=null)
                            progressDialog.dismiss();*//*
                        Log.e("TAG", t.toString());
                    }
                });
            }


        });
    }
    private void getMeetingsDetails(final Meetings loginDetails, final TextView visits){




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

                                visits.setText(""+list.size());
                                visit = visit + list.size();
                                employeeMeetings.addAll(list);
                                mMeetings.setText(""+visit);

                            }else{
                                mMeetings.setText(""+visit);

                                // Toast.makeText(DailyTargetsForEmployeeActivity.this, "No Tasks given for this employee ", Toast.LENGTH_SHORT).show();

                            }

                        }else {



                            //Toast.makeText(DailyTargetsForEmployeeActivity.this, "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Meetings>> call, Throwable t) {
                        // Log error here since request failed
                       *//* if (progressDialog!=null)
                            progressDialog.dismiss();*//*
                        Log.e("TAG", t.toString());
                    }
                });
            }


        });
    }
    private void getTasks(final int employeeId,final String status,final  TextView task,final String dateValue){




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



                            ArrayList<Tasks> list = response.body();
                            ArrayList<Tasks> todayTasks = new ArrayList<>();


                            Date date = new Date();
                            Date adate = new Date();
                            Date edate = new Date();



                            try {
                                date = new SimpleDateFormat("yyyy-MM-dd").parse(dateValue);


                            } catch (Exception e) {
                                e.printStackTrace();
                            }



                            if (list !=null && list.size()!=0) {



                                for (Tasks task:list) {



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
                                             *//*   String parse = new SimpleDateFormat("MMM yyyy").format(afromDate);
                                                fromDate = new SimpleDateFormat("MMM yyyy").parse(parse);*//*

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
                                              *//*  String parse = new SimpleDateFormat("MMM yyyy").format(atoDate);
                                                toDate = new SimpleDateFormat("MMM yyyy").parse(parse);*//*

                                        }

                                    }

                                    if(afromDate!=null&&atoDate!=null){



                                        if(date.getTime() >= afromDate.getTime() && date.getTime() <= atoDate.getTime()){

                                            todayTasks.add(task);
                                            employeeTasks.add(task);

                                            if (task.getStatus().equalsIgnoreCase(status)) {

                                                compTask = compTask+1;
                                                mCompletedTask.setText(""+compTask);
                                            }else{
                                                mCompletedTask.setText(""+compTask);
                                            }


                                        }
                                    }






                                }

                                if(todayTasks!=null&&todayTasks.size()!=0){

                                    task.setText(""+todayTasks.size());

                                    totaltask = totaltask+1;
                                    mTotalTask.setText("/"+employeeTasks.size());



                                }else{
                                    mTotalTask.setText("/"+employeeTasks.size());
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
                       *//* if (progressDialog!=null)
                            progressDialog.dismiss();*//*
                        Log.e("TAG", t.toString());
                    }
                });
            }


        });
    }
    private void getExpense(final int employeeId,final TextView exp,final  TextView expeAmt,final String dateValue){




        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {
                ExpensesApi apiService = Util.getClient().create(ExpensesApi.class);
                Call<ArrayList<Expenses>> call = apiService.getExpenseByEmployeeIdAndOrganizationId(PreferenceHandler.getInstance(context).getCompanyId(),employeeId);

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
                                             *//*   String parse = new SimpleDateFormat("MMM yyyy").format(afromDate);
                                                fromDate = new SimpleDateFormat("MMM yyyy").parse(parse);*//*

                                        }

                                    }



                                    if(afromDate!=null){



                                        if(date.getTime() == afromDate.getTime() ){

                                            employeeExpense.add(task);

                                            todayTasks.add(task);
                                            amt = amt+task.getAmount();


                                        }
                                    }






                                }

                                if(todayTasks!=null&&todayTasks.size()!=0){



                                    exp.setText(""+todayTasks.size());
                                    expeAmt.setText("Rs "+new DecimalFormat("#.##").format(amt));
                                    expense = expense+todayTasks.size();
                                    mExpenses.setText(""+expense);



                                }else{
                                    mExpenses.setText(""+expense);


                                }



                            }else{

                                // Toast.makeText(DailyTargetsForEmployeeActivity.this, "No Tasks given for this employee ", Toast.LENGTH_SHORT).show();

                            }

                        }else {



                            //Toast.makeText(DailyTargetsForEmployeeActivity.this, "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Expenses>> call, Throwable t) {
                        // Log error here since request failed
                       *//* if (progressDialog!=null)
                            progressDialog.dismiss();*//*
                        Log.e("TAG", t.toString());
                    }
                });
            }


        });
    }

    private void getLiveLocation(final LiveTracking lv,final TextView km){


        final ProgressDialog progressDialog = new ProgressDialog(ReportManagementScreen.this);
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
                            float distance = 0;


                            if (list !=null && list.size()!=0) {

                                Collections.sort(list,LiveTracking.compareLiveTrack);


                                double lati = 0,lngi=0;
                                double latis = 0,lngis=0;

                                for(int i=1;i<list.size();i++){

                                    if(list.get(i).getLongitude()!=null||list.get(i).getLatitude()!=null){

                                        double lat = Double.parseDouble(list.get(i).getLatitude());
                                        double lng = Double.parseDouble(list.get(i).getLongitude());



                                        if(lat==0&&lng==0){

                                        }else{

                                            if(list.size()==1){

                                            }else{
                                                Location locationA = new Location("point A");

                                                lati = Double.parseDouble(list.get(i-1).getLatitude());
                                                lngi = Double.parseDouble(list.get(i-1).getLongitude());


                                                if(lati==0&&lngi==0){

                                                    lati = Double.parseDouble(list.get(i-1).getLatitude());
                                                    lngi = Double.parseDouble(list.get(i-1).getLongitude());


                                                }else{

                                                    latis = lati;
                                                    lngis = lngi;

                                                    locationA.setLatitude(latis);
                                                    locationA.setLongitude(lngis);
                                                    Location locationB = new Location("point B");

                                                    locationB.setLatitude(Double.parseDouble(list.get(i).getLatitude()));
                                                    locationB.setLongitude(Double.parseDouble(list.get(i).getLongitude()));

                                                    distance = distance+locationA.distanceTo(locationB);
                                                    double kms = distance/1000.0;
                                                    double miles = distance*0.000621371192;
                                                    km.setText(new DecimalFormat("#.##").format(kms)+" Km/"+new DecimalFormat("#.##").format(miles)+" miles");
                                                    reportDataModel.setKmt(""+new DecimalFormat("#.##").format(kms)+" Km/"+new DecimalFormat("#.##").format(miles)+" miles");

                                                }


                                            }



                                        }



                                    }


                                }











                            }else{



                            }

                        }else {



                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<LiveTracking>> call, Throwable t) {
                        // Log error here since request failed
                        if (progressDialog!=null)
                            progressDialog.dismiss();
                        Log.e("TAG", t.toString());
                    }
                });
            }


        });
    }*/

    public void sendEmail(){

        EmailData emailData = new EmailData();

        String data = "";
        for(int j=0;j<reportDataModel.getReportDataEmployees().size();j++){
            data = data+"<tr><td style=\"text-align:center\">"+reportDataModel.getReportDataEmployees().get(j).getName()+"</td>"
                    +"<td style=\"text-align:center\">"+reportDataModel.getReportDataEmployees().get(j).getLoginTime()+"</td>"
                    +"<td style=\"text-align:center\">"+reportDataModel.getReportDataEmployees().get(j).getLogoutTime()+"</td>"
                    +"<td style=\"text-align:center\">"+reportDataModel.getReportDataEmployees().get(j).getHours()+"</td>"
                    +"<td style=\"text-align:center\">"+reportDataModel.getReportDataEmployees().get(j).getVisits()+"</td>"
                    +"<td style=\"text-align:center\">"+reportDataModel.getReportDataEmployees().get(j).getTasks()+"</td>"
                    +"<td style=\"text-align:center\">"+reportDataModel.getReportDataEmployees().get(j).getExpenses()+"</td>"
                    +"<td style=\"text-align:center\">"+reportDataModel.getReportDataEmployees().get(j).getExpensesAmt()    +"</td></tr>";
        }

        String body = "<!DOCTYPE html> \n" +
                "<html> \n" +
                "<head>\n" +
                "  <meta charset=\"utf-8\">\n" +
                "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                "  \n" +
                "  <link href='https://fonts.googleapis.com/css?family=Roboto' rel='stylesheet'>\n" +
                "  <link rel=\"stylesheet\" type=\"text/css\" href=\"css/main.css\" />\n" +
                "  <link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css\">\n" +
                "  <link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css\">\n" +
                "  <script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js\"></script>\n" +
                "  <script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js\"></script>\n" +
                "  <style text=\"text/css\">\n" +
                "body{\n" +
                "    font-family: 'Roboto';\n" +
                "\tfont-size:15px;\n" +
                "\tcolor:#454545;\n" +
                "}\n" +
                "\n" +
                "#form-check{\n" +
                "\ttext-align:center;\n" +
                "\tpadding:100px 410px 0px 410px;\n" +
                "}\n" +
                "\n" +
                ".bg-frm{\n" +
                "\tbackground-color: #EE596C;\n" +
                "\tpadding:10px 10px 10px 10px;\n" +
                "}\n" +
                "\n" +
                "\n" +
                "#form-check h5{\n" +
                "\tcolor:#fff;\n" +
                "\ttext-align:left;\n" +
                "\tpadding:0px 13px;\n" +
                "\tfont-size:15px;\n" +
                "}\n" +
                "\n" +
                "#form-check-one{\n" +
                "\tpadding:0px 410px 0px 410px;\n" +
                "}\n" +
                "\n" +
                ".box-check-one{\n" +
                "\twidth:100%;\n" +
                "\tborder:1px solid gray;\n" +
                "\tpadding:10px 10px;\n" +
                "}\n" +
                "\n" +
                "#form-check-one form{\n" +
                "\tpadding:10px 0px;\n" +
                "}\n" +
                " table, th, td { \n" +
                "                border: 2px solid green; \n" +
                "                text-align:center; \n" +
                "                border-collapse: collapse;\n" +
                "                padding: 10px;\n" +
                                "                padding-left: 34px;\n" +
                                "                padding-right: 34px;\n" +
                "              \n" +
                "            } \n" +
                "            h1 { \n" +
                "            color:green; \n" +
                "            } \n" +
                "  </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "<div id=\"form-check-one\">\n" +
                "\t<div class=\"bg-frm\">\n" +
                "\t\t<div class=\"row\">\n" +
                "\t\t\t<div class=\"col-lg-5 col-md-5 col-sm-5 col-xs-12\">\t\t\t\n" +
                "\t\t\t\t  <h2 style=\"color: white\">Zingy App</h2>\t \t\t\t\n" +
                "\t\t\t</div>\t\n" +
                "\t\t\t<div class=\"col-lg-7 col-md-7 col-sm-7 col-xs-12\">\n" +
                "\t\t\t\t<h4 style=\"color: white; text-align:right\">Team Activity Report</h4>\n" +
                "\t\t\t\t<h4 style=\"color: white; text-align:right\">"+new SimpleDateFormat("dd MMM,yyyyy").format(new Date())+"</h4>\n" +
                "\t\t\t</div>\t\t\n" +
                "\t\t</div>\n" +
                "\t\t<hr>\n" +
                "\t\t<div class=\"row\" style=\" position:center;>\n" +
                "\t\t\t<div class=\"col-lg-12 col-md-12 col-sm-12 col-xs-12\">\n" +
                "\t\t\t\t<table style=\"background-color: white; color:black; position:center;\">\n" +
                "\t\t\t\t\t<tr>\n" +
                "\t\t\t\t\t\t<th>ATTENDANCE</th>\n" +
                "\t\t\t\t\t\t<th>TASK</th>\n" +
                "\t\t\t\t\t\t<th>VISIT</th>\n" +
                "\t\t\t\t\t\t<th>EXPENSES</th>\n" +
                "\t\t\t\t\t</tr>\n" +
                "\t\t\t\t\t<tr>\n" +
                "\t\t\t\t\t\t<td>"+reportDataModel.getPreEmply()+""+reportDataModel.getTotalEmp()+"</td>\n" +
                "\t\t\t\t\t\t<td>"+reportDataModel.getComplTask()+""+reportDataModel.getTotalTas()+"</td>\n" +
                "\t\t\t\t\t\t<td>"+reportDataModel.getVisit()+"</td>\n" +
                "\t\t\t\t\t\t<td>"+reportDataModel.getExpenses()+"</td>\n" +
                "\t\t\t\t\t</tr>\n" +
                "\t\t\t\t</table>\n" +
                "\t\t\t</div>\n" +
                "\t\t</div>\t\t\n" +
                "\t\t<br /><div class=\"row\">\n" +
                "\t\t\t<div class=\"col-lg-12 col-md-12 col-sm-12 col-xs-12\">\n" +
                "\t\t\t\t<table style=\"background-color: white; color:black;\">\n" +
                "\t\t\t\t\t<tr>\n" +
                "\t\t\t\t\t\t<th>NAME</th>\n" +
                "\t\t\t\t\t\t<th>LOGIN</th>\n" +
                "\t\t\t\t\t\t<th>LOGOUT</th>\n" +
                "\t\t\t\t\t\t<th>HOURS</th>\n" +
                "\t\t\t\t\t\t<th>VISITS</th>\n" +
                "\t\t\t\t\t\t<th>TASKS</th>\n" +
                "\t\t\t\t\t\t<th>EXPENSES</th>\n" +
                "\t\t\t\t\t\t<th>EXP AMOUNT</th>\n" +
                "\t\t\t\t\t</tr>\n" +
                "\t\t\t\t\t<tr>\n" +
                "\t\t\t\t\t\t"+data+"\n" +
                "\t\t\t\t</table>\n" +
                "\t\t\t</div>\n" +
                "\t\t</div>\t\t\n" +
                "\t</div>\n" +
                "</div>\n" +
                "</body>\n" +
                "</html>\n" +
                "\n";
        emailData.setEmailAddress("mednizar.s@gmail.com");
        emailData.setBody(body);
        emailData.setSubject("Today Report");
        emailData.setUserName("nishar@zingohotels.com");
        emailData.setPassword("Razin@1993");
        emailData.setFromName("Zingo Bookings");
        emailData.setFromEmail("nishar@zingohotels.com");
        sendEmailAutomatic(emailData);


    }

    private void sendEmailAutomatic(final EmailData emailData) {




        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {


                SendEmailAPI travellerApi = Util.getClient().create(SendEmailAPI.class);
                Call<String> response = travellerApi.sendEmail(emailData);

                response.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {


                        try{


                            System.out.println(response.code());
                            if(response.code() == 200||response.code() == 201)
                            {
                                if(response.body() != null)
                                {


                                    if(response.body().equalsIgnoreCase("Email Sent Successfully")){
                                        //Toast.makeText(SendEmailToAll.this, "", Toast.LENGTH_SHORT).show();
                                    }else{
                                        //Toast.makeText(ContactUsScreen.this, "Something went wrong. So please contact through Call", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }else{
                                // Toast.makeText(ContactUsScreen.this, "Something went wrong due to "+response.code()+". So please contact through Call", Toast.LENGTH_LONG).show();
                            }
                        }catch (Exception e){

                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });
            }
        });
    }


    @Override
    public void onDestroy() {

        super.onDestroy();
    }
}