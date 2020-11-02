package app.zingo.employeemanagements.ui.Common;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import app.zingo.employeemanagements.Custom.MyRegulerText;
import app.zingo.employeemanagements.model.EmailData;
import app.zingo.employeemanagements.model.Employee;
import app.zingo.employeemanagements.model.Expenses;
import app.zingo.employeemanagements.model.LiveTracking;
import app.zingo.employeemanagements.model.LoginDetails;
import app.zingo.employeemanagements.model.LoginDetailsNotificationManagers;
import app.zingo.employeemanagements.model.Meetings;
import app.zingo.employeemanagements.model.ReportDataEmployee;
import app.zingo.employeemanagements.model.ReportDataModel;
import app.zingo.employeemanagements.model.Tasks;
import app.zingo.employeemanagements.ui.NewAdminDesigns.ReportExpenseList;
import app.zingo.employeemanagements.ui.NewAdminDesigns.ReportTaskListScreen;
import app.zingo.employeemanagements.ui.NewAdminDesigns.ReportVisitsListScreen;
import app.zingo.employeemanagements.utils.BaseActivity;
import app.zingo.employeemanagements.utils.Common;
import app.zingo.employeemanagements.utils.PreferenceHandler;
import app.zingo.employeemanagements.utils.ThreadExecuter;
import app.zingo.employeemanagements.utils.Util;
import app.zingo.employeemanagements.WebApi.EmployeeApi;
import app.zingo.employeemanagements.WebApi.ExpensesApi;
import app.zingo.employeemanagements.WebApi.LiveTrackingAPI;
import app.zingo.employeemanagements.WebApi.LoginDetailsAPI;
import app.zingo.employeemanagements.WebApi.LoginNotificationAPI;
import app.zingo.employeemanagements.WebApi.MeetingsAPI;
import app.zingo.employeemanagements.WebApi.TasksAPI;
import app.zingo.employeemanagements.base.R;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
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

import static app.zingo.employeemanagements.utils.BaseActivity.metersToKms;
import static app.zingo.employeemanagements.utils.BaseActivity.metersToMiles;

public class ReportManagementScreen extends BaseActivity {
    MyRegulerText mTotalEmployee,mPresentEmployee,mTotalTask,mCompletedTask,mMeetings,mExpenses;
    LinearLayout mTaskShow,mExpenseList,mShowVisit;
    RecyclerView mEmpList;
    Button mReport;
    ArrayList<Tasks> employeeTasks;
    ArrayList< Expenses > employeeExpense;
    ArrayList< Meetings > employeeMeetings;
    ArrayList<Tasks> completedTasks ;
    int completeTask = 0,presentEmployee=0;

    ArrayList<Integer> preEmpId;
    ReportDataModel reportDataModel;
    ArrayList< ReportDataEmployee > reportDataEmployees;
    ReportDetailEmployeeAdapter adapter;
    ReportDetailEmployeeAdapter.ViewHolder viewHolder;
    String kms ="";
    int totalEmp,preEmp=0,totaltask =0,compTask=0,visit=0,expense=0;
    ArrayList< Employee > employees;
    LiveTrackingAPI mLiveLocationsService = Common.getLiveTrackingAPI ();
    MeetingsAPI mMeetingsService = Common.getMeetingsAPI ();
    private LoginDetailsAPI mLoginDetailsService = Common.getLoginDetailsAPI ();
    private TasksAPI mTaskService = Common.getTaskAPI ();
    private ExpensesApi mExpensesService = Common.getExpensesAPI ();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
            setContentView(R.layout.activity_report_management_screen);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Report Management");

            mTotalEmployee = findViewById(R.id.total_employee_count);
            mPresentEmployee = findViewById(R.id.present_employee_count_text);
            mTotalTask = findViewById(R.id.total_tasks);
            mCompletedTask = findViewById(R.id.completed_task_count_text);
            mMeetings = findViewById(R.id.meeting_count);
            mExpenses = findViewById(R.id.expense_count_text);
            mTaskShow = findViewById(R.id.task_list_show);
            mExpenseList = findViewById(R.id.report_expense_list);
            mShowVisit = findViewById(R.id.show_visits);

            mEmpList = findViewById(R.id.report_list);
            mReport = findViewById(R.id.generate_report_btn);

            preEmpId = new ArrayList<>();
            employees = new ArrayList<>();

            /*getDashBoard();
            getExpenses(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));*/
            getEmployees(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));


            mReport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(reportDataModel!=null){
                        reportDataModel.setPreEmply(mPresentEmployee.getText().toString());
                        reportDataModel.setTotalEmp(mTotalEmployee.getText().toString());
                        reportDataModel.setComplTask(mCompletedTask.getText().toString());
                        reportDataModel.setTotalTas(mTotalTask.getText().toString());
                        reportDataModel.setVisit(mMeetings.getText().toString());
                        reportDataModel.setExpenses(mExpenses.getText().toString());

                        reportDataEmployees = new ArrayList<>();

                        for (int i = 0; i < adapter.getItemCount(); i++) {
                            viewHolder = (ReportDetailEmployeeAdapter.ViewHolder) mEmpList.findViewHolderForAdapterPosition(i);

                            TextView name = viewHolder.mName;
                            TextView login = viewHolder.mLogin;
                            TextView logout = viewHolder.mLogout;
                            TextView hours = viewHolder.mHours;
                            TextView bhours = viewHolder.breakText;
                            TextView visits = viewHolder.mVisits;
                            TextView tasks = viewHolder.mTasks;
                            TextView expense = viewHolder.mExpenses;
                            TextView expenAmount = viewHolder.mExpAmt;
                            TextView kmss = viewHolder.mKm;

                            ReportDataEmployee rd = new ReportDataEmployee ();

                            rd.setName(name.getText().toString());
                            rd.setLoginTime(login.getText().toString());
                            rd.setLogoutTime(logout.getText().toString());
                            rd.setHours(hours.getText().toString());
                            rd.setBreakHours(bhours.getText().toString());
                            rd.setVisits(visits.getText().toString());
                            rd.setTasks(tasks.getText().toString());
                            rd.setExpenses(expense.getText().toString());
                            rd.setExpensesAmt(expenAmount.getText().toString());
                            rd.setKms(kmss.getText().toString());

                            reportDataEmployees.add(rd);
                        }

                        reportDataModel.setReportDataEmployees(reportDataEmployees);
                        if(reportDataModel!=null){
                            boolean isfilecreated = generateReport(reportDataModel);
                            //saveExcelFile();
                            if(isfilecreated) {
                                try{
                                    File root = Environment.getExternalStorageDirectory();
                                    String csvFile = "TeamActivity_"+new SimpleDateFormat("MMMyy").format(new Date())+".xls"; //File Name
                                    File directory = new File(root.getAbsolutePath()+"/TeamActivity");
                                    Intent emailIntent = new Intent(Intent.ACTION_SEND);
                                    emailIntent.setType("text/plain");
                                    File file = new File(directory, csvFile);
                                    if (!file.exists() || !file.canRead()) {
                                        return;
                                    }
                                    Uri uri = null;
                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                        uri = FileProvider.getUriForFile( ReportManagementScreen.this, "app.zingo.employeemanagements.fileprovider", file);
                                    }else{
                                        uri = Uri.fromFile(file);
                                    }

                                    //Uri uri = Uri.fromFile(file);
                                    if(uri!=null){
                                        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
                                    }else{
                                        Toast.makeText( ReportManagementScreen.this, "File cannot access", Toast.LENGTH_SHORT).show();
                                    }
           /* Uri uri = Uri.fromFile(file);
            emailIntent.putExtra(Intent.EXTRA_STREAM, uri);*/
                                    startActivity(Intent.createChooser(emailIntent, "Pick an Email provider"));
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }

                      EmailData emailData = new EmailData ();
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
                                /*"                padding-left: 34px;\n" +
                                "                padding-right: 34px;\n" +*/
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
                                "\t\t\t\t  <h2 style=\"color: white\">Mysolite App</h2>\t \t\t\t\n" +
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

                        /* if(Util.isNetworkAvailable(ReportManagementScreen.this)){
                            sendEmailAutomatic(emailData);
                        }else{
                            Toast.makeText(ReportManagementScreen.this, "Please check your Internet Connection", Toast.LENGTH_LONG).show();
                        }*/
                    }
                }
            });


            mTaskShow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(employeeTasks!=null&&employeeTasks.size()!=0){
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("EmployeeTask",employeeTasks);
                        Intent tsak = new Intent( ReportManagementScreen.this, ReportTaskListScreen.class);
                        tsak.putExtras(bundle);
                        startActivity(tsak);

                    }else{
                        Toast.makeText( ReportManagementScreen.this, "No Task available on selected Date", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            mExpenseList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(employeeExpense!=null&&employeeExpense.size()!=0){
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("EmployeeExpense",employeeExpense);
                        Intent tsak = new Intent( ReportManagementScreen.this, ReportExpenseList.class);
                        tsak.putExtras(bundle);
                        startActivity(tsak);

                    }else{
                        Toast.makeText( ReportManagementScreen.this, "No Expenses available on selected Date", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            mShowVisit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(employeeMeetings!=null&&employeeMeetings.size()!=0){
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("EmployeeMeetings",employeeMeetings);
                        Intent tsak = new Intent( ReportManagementScreen.this, ReportVisitsListScreen.class);
                        tsak.putExtras(bundle);
                        startActivity(tsak);

                    }else{
                        Toast.makeText( ReportManagementScreen.this, "No Meetings available on selected Date", Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void getEmployees(final String dateValue){
        final ProgressDialog progressDialog = new ProgressDialog( ReportManagementScreen.this);
        progressDialog.setTitle("Loading Employees");
        progressDialog.setCancelable(false);
        progressDialog.show();
        new ThreadExecuter ().execute( new Runnable() {
            @Override
            public void run() {
                EmployeeApi apiService = Util.getClient().create( EmployeeApi.class);
                Call<ArrayList< Employee >> call = apiService.getEmployeesByOrgId(PreferenceHandler.getInstance( ReportManagementScreen.this).getCompanyId());
                call.enqueue(new Callback<ArrayList< Employee >>() {
                    @Override
                    public void onResponse( Call<ArrayList< Employee >> call, Response<ArrayList< Employee >> response) {
                        int statusCode = response.code();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {
                            if (progressDialog!=null)
                                progressDialog.dismiss();
                            ArrayList< Employee > list = response.body();
                            reportDataModel = new ReportDataModel ();
                            employeeTasks = new ArrayList<>();
                            employeeExpense = new ArrayList<>();
                            employeeMeetings = new ArrayList<>();
                           // pendingTasks = new ArrayList<>();
                            completedTasks = new ArrayList<>();
                            /*closedTasks = new ArrayList<>();
                            onTask = new ArrayList<>();*/
                            totalEmp =0;
                            preEmp=0;
                            totaltask=0;
                            compTask=0;
                            visit=0;
                            expense=0;

                            if (list !=null && list.size()!=0) {
                                ArrayList< Employee > employees = new ArrayList<>();
                                for(int i=0;i<list.size();i++){
                                    if(list.get(i).getUserRoleId()!=2){
                                        employees.add(list.get(i));
                                       /* getTasks(list.get(i).getEmployeeId());
                                        LoginDetails loginDetails = new LoginDetails();
                                        loginDetails.setEmployeeId(list.get(i).getEmployeeId());
                                        Date date=null;
                                        try {
                                             date = new SimpleDateFormat("yyyy-MM-dd").parse(dateValue);
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                        loginDetails.setLoginDate(new SimpleDateFormat("MM/dd/yyyy").format(date));
                                        getLoginDetails(loginDetails);*/

                                    }
                                }

                                if(employees!=null&&employees.size()!=0){
                                    reportDataEmployees = new ArrayList<>();
                                    mTotalEmployee.setText("/"+employees.size());
                                    totalEmp = employees.size();
                                    mEmpList.removeAllViews();
                                    adapter = new ReportDetailEmployeeAdapter( ReportManagementScreen.this,employees,dateValue);
                                    mEmpList.setAdapter(adapter);
                                }

                            }else{

                            }

                        }else {
                            Toast.makeText( ReportManagementScreen.this, "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure( Call<ArrayList< Employee >> call, Throwable t) {
                        // Log error here since request failed
                        if (progressDialog!=null)
                            progressDialog.dismiss();
                        Log.e("TAG", t.toString());
                    }
                });
            }
        });
    }

    public class ReportDetailEmployeeAdapter extends RecyclerView.Adapter<ReportDetailEmployeeAdapter.ViewHolder>{
        private Context context;
        private ArrayList< Employee > list;
        private String dateValue;

        public ReportDetailEmployeeAdapter( @NonNull Context context, @NonNull ArrayList< Employee > list, String dateValue) {
            this.context = context;
            this.list = list;
            this.dateValue = dateValue;
        }

        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_report_details, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
            final Employee dto = list.get(position);

            if(dto!=null){

                ReportDataEmployee rd = new ReportDataEmployee ();

                Date date=null;
                try {
                    date = new SimpleDateFormat("yyyy-MM-dd").parse(dateValue);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                holder.mName.setText(""+dto.getEmployeeName());

                LoginDetails ld  = new LoginDetails ();
                ld.setEmployeeId(dto.getEmployeeId());
                ld.setLoginDate(new SimpleDateFormat("MM/dd/yyyy").format(date));
                String logDate = new SimpleDateFormat("MMM dd,yyyy").format(date);
                getLoginDetails(ld,holder.mLogin,holder.mLogout,holder.mHours,logDate);

                getLoginNotifications(dto.getEmployeeId(),new SimpleDateFormat("MMM dd,yyyy").format(new Date()),holder.breakText,logDate);


                LiveTracking lv = new LiveTracking ();
                lv.setEmployeeId(dto.getEmployeeId());
                lv.setTrackingDate(new SimpleDateFormat("MM/dd/yyyy").format(date));
                getLiveLocation(lv,holder.mKm);

                Meetings md  = new Meetings ();
                md.setEmployeeId(dto.getEmployeeId());
                md.setMeetingDate(new SimpleDateFormat("MM/dd/yyyy").format(date));
                getMeetingsDetails(md,holder.mVisits);

                getTasks(dto.getEmployeeId(),"Completed",holder.mTasks,dateValue);
                getExpense(dto.getEmployeeId(),holder.mExpenses, holder.mExpAmt,dateValue);
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder /*implements View.OnClickListener*/ {

            public TextView mName,mLogin,mLogout,mHours,mVisits,mTasks,mExpenses,mExpAmt,mKm,breakText;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                itemView.setClickable(true);
                mName = itemView.findViewById(R.id.report_name);
                mLogin = itemView.findViewById(R.id.report_login);
                mLogout = itemView.findViewById(R.id.report_logout);
                mHours = itemView.findViewById(R.id.report_hours);
                breakText = itemView.findViewById(R.id.report_hours_break);
                mVisits = itemView.findViewById(R.id.report_visit);
                mTasks = itemView.findViewById(R.id.report_task);
                mExpenses = itemView.findViewById(R.id.report_expense);
                mExpAmt = itemView.findViewById(R.id.report_expense_amount);
                mKm = itemView.findViewById(R.id.report_km);
            }
        }

        private void getLoginDetails( final LoginDetails loginDetails, final TextView login, final TextView logout, final TextView workingHrs, final String comDate){
            mLoginDetailsService.getLoginByEmployeeIdAndDateRx (loginDetails)
                    .subscribeOn( Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer <ArrayList<LoginDetails>> () {
                        @Override
                        public void onSubscribe( @NotNull Disposable d) {

                        }

                        @Override
                        public void onNext( @NotNull ArrayList<LoginDetails> result) {
                            if( result.size ( ) != 0 ){
                                setLoginData(result,login,logout,workingHrs,comDate);
                            }else {
                                setLoginRecord (login,logout);
                            }
                        }

                        @Override
                        public void onError( @NotNull Throwable e) {
                            System.out.println ("Error "+e.getMessage());
                            setLoginRecord (login,logout);
                        }

                        @Override
                        public void onComplete() {
                            // Updates UI with data
                        }
                    });
        }

        private void getMeetingsDetails( final Meetings loginDetails,  final TextView visits){
            mMeetingsService.getMeetingsByEmployeeIdAndDateRx (loginDetails)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<ArrayList<Meetings>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(ArrayList<Meetings> result) {
                            if(result!=null&& result.size ( )!=0){
                                setMeetingsData (result,visits);
                            }else {
                                mMeetings.setText(""+visit);
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

        private void getLoginNotifications(final int id,final String dateValue,final  TextView breaktext, final String comDate){
            LoginNotificationAPI apiService = Util.getClient().create( LoginNotificationAPI.class);
            Call<ArrayList< LoginDetailsNotificationManagers >> call = apiService.getNotificationByEmployeeId(id);
            call.enqueue(new Callback<ArrayList< LoginDetailsNotificationManagers >>() {
                @SuppressLint ("SimpleDateFormat")
                @Override
                public void onResponse( Call<ArrayList< LoginDetailsNotificationManagers >> call, Response<ArrayList< LoginDetailsNotificationManagers >> response) {
                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {
                        ArrayList< LoginDetailsNotificationManagers > list = response.body();
                        ArrayList< LoginDetailsNotificationManagers > dateLogins = new ArrayList<>();
                        if (list !=null && list.size()!=0) {
                            Collections.sort(list, LoginDetailsNotificationManagers.compareLoginNM);
                            Collections.reverse(list);
                            Date date = new Date();
                            Date adate = new Date();

                            String currentDateStart = dateValue+" 12:00 AM";
                            String currentDateEnd = dateValue+" 11:59 PM";
                            try {
                                date = new SimpleDateFormat("MMM dd,yyyy hh:mm a").parse(currentDateStart);
                                adate = new SimpleDateFormat("MMM dd,yyyy hh:mm a").parse(currentDateEnd);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            String loginT="";
                            String logoutT="";
                            long diffHrs = 0;

                            for ( LoginDetailsNotificationManagers ln:list) {
                                String froms = ln.getLoginDate();
                                Date loginDate = null;
                                try {
                                    loginDate = new SimpleDateFormat("MMM dd,yyyy hh:mm a").parse(froms);
                                    String status = ln.getStatus();

                                    if(date!=null&&adate!=null&&(status!=null&&(status.equalsIgnoreCase("Lunch Break")||status.equalsIgnoreCase("Tea Break")))){
                                        if(date.getTime()<=loginDate.getTime()&&adate.getTime()>=loginDate.getTime()){
                                            dateLogins.add(ln);
                                            if(ln.getMessage()!=null&&(ln.getMessage().contains("Break taken at"))){
                                                loginT = ln.getLoginDate();
                                            }else if(ln.getMessage()!=null&&(ln.getMessage().contains("Break ended at"))){
                                                logoutT = ln.getLoginDate();
                                            }

                                            if((loginT==null||loginT.isEmpty())){

                                            }else{
                                                if(ln.getMessage()!=null&&(ln.getMessage().contains("Break taken at "))){
                                                    loginT = ln.getLoginDate();
                                                }else if(ln.getMessage()!=null&&(ln.getMessage().contains("Break ended at"))){
                                                    logoutT = ln.getLoginDate();
                                                }
                                                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy hh:mm a");
                                                Date fd=null,td=null;
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
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }

                            int minutes = (int) ((diffHrs / (1000*60)) % 60);
                            int hours   = (int) ((diffHrs / (1000*60*60)) % 24);
                            int days   = (int) ((diffHrs / (1000*60*60*24)));
                            breaktext.setText(String.format("%02d", days)+":"+String.format("%02d", hours) +":"+String.format("%02d", minutes));
                        }else{
                            breaktext.setText("0");
                        }

                    }else {
                        breaktext.setText("0");
                    }
                }

                @Override
                public void onFailure( Call<ArrayList< LoginDetailsNotificationManagers >> call, Throwable t) {
                    breaktext.setText("0");
                    Log.e("TAG", t.toString());
                }
            });
        }

        private void getTasks(final int employeeId,final String status,final  TextView task, final String comDate) {
            mTaskService.getTasksByEmployeeIdRx (employeeId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<ArrayList<Tasks>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(ArrayList<Tasks> result) {
                            if(result.size ()!=0) {
                                setTaskData ( result , comDate ,status, task);
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

        private void getExpense(final int employeeId,final TextView exp, final  TextView expeAmt, final String comDate){
            mExpensesService.getExpenseByEmployeeIdAndOrganizationIdRx (PreferenceHandler.getInstance (ReportManagementScreen.this ).getCompanyId (),employeeId)
                    .subscribeOn(Schedulers.io())
                    .observeOn( AndroidSchedulers.mainThread())
                    .subscribe(new Observer<ArrayList<Expenses>>() {
                        @Override
                        public void onSubscribe( Disposable d) {

                        }

                        @Override
                        public void onNext(ArrayList<Expenses> result) {
                            if(result.size ()!=0){
                                setExpenseData(result,exp,expeAmt, comDate);
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

        private void getLiveLocation(final LiveTracking lv, TextView mKm) {
            mLiveLocationsService.getLiveTrackingByEmployeeIdAndDateRx (lv)
                    .subscribeOn( Schedulers.io())
                    .observeOn( AndroidSchedulers.mainThread())
                    .subscribe(new Observer <ArrayList<LiveTracking>> () {
                        @Override
                        public void onSubscribe( Disposable d) {

                        }

                        @Override
                        public void onNext(ArrayList<LiveTracking> result) {
                            if(result!=null&&result.size ()!=0){
                                setLiveLocationStatus(setLiveLocationData(result), mKm);
                            }else{
                                setLiveLocationRecord (mKm);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            System.out.println ("Error "+e.getMessage());
                            setLiveLocationRecord(mKm);
                        }

                        @Override
                        public void onComplete() {
                            // Updates UI with data
                        }
                    });
        }
    }

    private void setExpenseData ( ArrayList < Expenses > list , TextView exp , TextView expeAmt , String comDate ) {
        ArrayList< Expenses > todayTasks = new ArrayList<>();
        Date date = new Date();
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(comDate);
        } catch (Exception e) {
            e.printStackTrace();
        }

        double amt = 0;
        for ( Expenses task:list) {
            String froms = task.getDate();
            Date afromDate = null;
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
    }

    private void setTaskData ( ArrayList < Tasks > list , String comDate , String status, TextView mTask ) {
        ArrayList<Tasks> todayTasks = new ArrayList<>();

        Date date = new Date();
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(comDate);
        } catch (Exception e) {
            e.printStackTrace();
        }


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
            mTask.setText(""+todayTasks.size());
            totaltask = totaltask+1;
            mTotalTask.setText("/"+employeeTasks.size());

        }else{
            mTotalTask.setText("/"+employeeTasks.size());
        }
    }

    private void setLoginRecord ( TextView login , TextView logout ) {
        login.setText("ABSENT");
        login.setTextColor(Color.parseColor("#FF0000"));
        logout.setTextColor(Color.parseColor("#FF0000"));
        logout.setText("ABSENT");
        mPresentEmployee.setText(""+preEmp);
    }

    private void setLoginData ( ArrayList < LoginDetails > list , TextView login , TextView logout , TextView workingHrs , String comDate ) {
        preEmp = preEmp+1;
        mPresentEmployee.setText(""+preEmp);
        String loginTime = list.get(0).getLoginTime();
        String logoutTime = list.get(list.size()-1).getLogOutTime();

        if(loginTime!=null&&!loginTime.isEmpty()){
            login.setText(""+loginTime);
        }else{
            login.setText("");
        }

        if(logoutTime!=null&&!logoutTime.isEmpty()){
            logout.setText(""+logoutTime);
        }else{
            logout.setText("Working");
        }

        long diffHrs = 0;

        for ( LoginDetails lg:list) {

            if((loginTime==null||loginTime.isEmpty())&&(lg.getLogOutTime()==null||lg.getLogOutTime().isEmpty())){

            }else{
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy hh:mm a");
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

        int minutes = (int) ((diffHrs / (1000*60)) % 60);
        int hours   = (int) ((diffHrs / (1000*60*60)) % 24);
        int days   = (int) ((diffHrs / (1000*60*60*24)));
        workingHrs.setText(String.format("%02d", days)+":"+String.format("%02d", hours) +":"+String.format("%02d", minutes));
    }

    private void setMeetingsData ( ArrayList < Meetings > result , TextView visits ) {
        visits.setText(""+result.size());
        visit = visit + result.size();
        employeeMeetings.addAll(result);
        mMeetings.setText(""+visit);
    }

    private void setLiveLocationRecord ( TextView mKm) {
        mKm.setText("0 km/0 miles");
    }

    private void setLiveLocationStatus ( double distance , TextView mKm ) {
        mKm.setText(new DecimalFormat("#.##").format(metersToKms ( distance ))+" Km/"+new DecimalFormat("#.##").format(metersToMiles ( distance ))+" miles");
        reportDataModel.setKmt(""+new DecimalFormat("#.##").format(metersToKms ( distance ))+" Km/"+new DecimalFormat("#.##").format(metersToMiles ( distance ))+" miles");
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_custom_report, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            ReportManagementScreen.this.finish();
        } else if (id == R.id.action_calendar) {
            openDatePicker();
        }else if (id == R.id.action_calendar_past) {
            Intent custom = new Intent( ReportManagementScreen.this, ReportBulkdataScreen.class);
            startActivity(custom);
        }
        return super.onOptionsItemSelected(item);
    }

    public void openDatePicker() {
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

                            ArrayList<Date> dates = new ArrayList<Date>();
                            SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");

                           SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
                            try {
                                Date fdate = simpleDateFormat.parse(date1);
                                /*LiveTracking lv = new LiveTracking();
                                new SimpleDateFormat("yyyy-MM-dd").format(new Date())
                                lv.setTrackingDate(date1);
                                getLiveLocation(lv);*/
                                getEmployees(date2);

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
}
