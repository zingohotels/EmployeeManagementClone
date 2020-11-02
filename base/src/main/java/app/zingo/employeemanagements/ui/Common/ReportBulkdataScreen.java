package app.zingo.employeemanagements.ui.Common;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import app.zingo.employeemanagements.model.Employee;
import app.zingo.employeemanagements.model.LiveTracking;
import app.zingo.employeemanagements.model.LoginDetails;
import app.zingo.employeemanagements.model.Meetings;
import app.zingo.employeemanagements.model.ObservableReportData;
import app.zingo.employeemanagements.model.ReportDataEmployee;
import app.zingo.employeemanagements.Service.ReportDownloadingDataService;
import app.zingo.employeemanagements.utils.PreferenceHandler;
import app.zingo.employeemanagements.utils.ThreadExecuter;
import app.zingo.employeemanagements.utils.Util;
import app.zingo.employeemanagements.WebApi.EmployeeApi;
import app.zingo.employeemanagements.WebApi.MultpleAPI;
import app.zingo.employeemanagements.base.R;
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

public class ReportBulkdataScreen extends AppCompatActivity {
    ArrayList< ReportDataEmployee > reportDataEmployees;
    ArrayList<ObservableReportData> data = new ArrayList<>();
    TextView mFromDate,mToDate;
    LinearLayout mCurrent,mLast,mPast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            setContentView(R.layout.activity_report_bulkdata_screen);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Report Management");
            mCurrent = (LinearLayout)findViewById(R.id.currentMonth_layout);
            mLast = (LinearLayout)findViewById(R.id.lastMonth_layout);
            mPast = (LinearLayout)findViewById(R.id.pastMonth_layout);
            //getEmployees(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            mCurrent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Calendar c = Calendar.getInstance();
                    int year = c.get(Calendar.YEAR);
                    int month = c.get(Calendar.MONTH);
                    String monthValue = "";

                    if((month+1)<10){
                        monthValue = "0"+(month+1);
                    }else{
                        monthValue = ""+(month+1);
                    }
                    String startDate = year+"-"+monthValue+"-01";
                    String endDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                    //Toast.makeText(ReportBulkdataScreen.this, "Month "+month+" Year"+year, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent( ReportBulkdataScreen.this, ReportDownloadingDataService.class);
                    Bundle mBundle = new Bundle();
                    mBundle.putString("StartDate", startDate);
                    mBundle.putString("EndDate", endDate);
                    intent.setAction( ReportDownloadingDataService.ACTION_START_FOREGROUND_SERVICE);
                    intent.putExtras(mBundle);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(intent);
                    } else {
                        startService(intent);
                    }
                }
            });

            mLast.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    Calendar c = Calendar.getInstance();
                    c.add(Calendar.MONTH, -1);
                    int year = c.get(Calendar.YEAR);
                    int month = c.get(Calendar.MONTH);


                    String monthValue = "";


                    int res = c.getActualMaximum(Calendar.DATE);

                    if((month+1)<10){
                        monthValue = "0"+(month+1);
                    }else{
                        monthValue = ""+(month+1);
                    }
                    String startDate = year+"-"+monthValue+"-01";
                    String endDate = year+"-"+monthValue+"-"+res;

                    //Toast.makeText(ReportBulkdataScreen.this, "year "+month+" day"+res, Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent( ReportBulkdataScreen.this, ReportDownloadingDataService.class);
                    Bundle mBundle = new Bundle();
                    mBundle.putString("StartDate", startDate);
                    mBundle.putString("EndDate", endDate);
                    intent.setAction( ReportDownloadingDataService.ACTION_START_FOREGROUND_SERVICE);
                    intent.putExtras(mBundle);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(intent);
                    } else {
                        startService(intent);
                    }
                }
            });

            mPast.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        showAlert();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void getEmployees(final String dateValue){
        final ProgressDialog progressDialog = new ProgressDialog( ReportBulkdataScreen.this);
        progressDialog.setTitle("Loading Employees");
        progressDialog.setCancelable(false);
        progressDialog.show();
        new ThreadExecuter ().execute( new Runnable() {
            @Override
            public void run() {
                EmployeeApi apiService = Util.getClient().create( EmployeeApi.class);
                Call<ArrayList< Employee >> call = apiService.getEmployeesByOrgId(PreferenceHandler.getInstance( ReportBulkdataScreen.this).getCompanyId());
                call.enqueue(new Callback<ArrayList< Employee >>() {
                    @Override
                    public void onResponse( Call<ArrayList< Employee >> call, Response<ArrayList< Employee >> response) {
                        int statusCode = response.code();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {
                            if (progressDialog!=null)
                                progressDialog.dismiss();
                            ArrayList< Employee > list = response.body();
                            if (list !=null && list.size()!=0) {
                                ArrayList< Employee > employees = new ArrayList<>();
                                for(int i=0;i<list.size();i++){
                                    if(list.get(i).getUserRoleId()!=2){
                                        employees.add(list.get(i));
                                    }
                                }

                                if(employees!=null&&employees.size()!=0){
                                    reportDataEmployees = new ArrayList<>();
                                    for ( Employee e:employees) {
                                        ReportDataEmployee reportDataEmployee = new ReportDataEmployee ();
                                        reportDataEmployee.setName(e.getEmployeeName());
                                        getData(reportDataEmployee,e,dateValue);
                                    }
                                }
                                //}

                            }else{

                            }

                        }else {
                            Toast.makeText( ReportBulkdataScreen.this, "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
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

    public void getData( final ReportDataEmployee reportDataEmployee, final Employee dto, final String dateValue){
        Date date=null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(dateValue);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        LoginDetails ld  = new LoginDetails ();
        ld.setEmployeeId(dto.getEmployeeId());
        ld.setLoginDate(new SimpleDateFormat("MM/dd/yyyy").format(date));
        String logDate = new SimpleDateFormat("MMM dd,yyyy").format(date);
        //getLoginDetails(ld,holder.mLogin,holder.mLogout,holder.mHours,logDate);

        LiveTracking lv = new LiveTracking ();
        lv.setEmployeeId(dto.getEmployeeId());
        lv.setTrackingDate(new SimpleDateFormat("MM/dd/yyyy").format(date));
        //getLiveLocation(lv,holder.mKm);

        Meetings md  = new Meetings ();
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


        MultpleAPI backendApi = Util.getClient().create( MultpleAPI.class);

        List<Observable<?>> requests = new ArrayList<>();

      /*  Observable<ArrayList<LoginDetails>> obs1= backendApi.getLoginByEmployeeIdAndDate(ld);
        Observable<ArrayList<LiveTracking>> obs2=backendApi.getLiveTrackingByEmployeeIdAndDate(lv);
        Observable<ArrayList<Meetings>> obs3= backendApi.getMeetingsByEmployeeIdAndDate(md);1008974
        Observable<ArrayList<Tasks>> obs4=backendApi.getTasksByEmployeeId(dto.getEmployeeId());1008985
        Observable<ArrayList<Expenses>> obs5= backendApi.getExpenseByEmployeeIdAndOrganizationId(PreferenceHandler.getInstance(ReportBulkdataScreen.this).getCompanyId(),dto.getEmployeeId());
*/
        //  requests.add(backendApi.getLoginByEmployeeIdAndDate(ld));
        requests.add(backendApi.getLiveTrackingByEmployeeIdAndDate(lv));
        // requests.add(backendApi.getMeetingsByEmployeeIdAndDate(md));
        //requests.add(backendApi.getTasksByEmployeeId(dto.getEmployeeId()));
        //requests.add(backendApi.getExpenseByEmployeeIdAndOrganizationId(PreferenceHandler.getInstance(ReportBulkdataScreen.this).getCompanyId(),dto.getEmployeeId()));

        Observable<ObservableReportData> combinedObservable = Observable.zip(requests, new FuncN<ObservableReportData>() {
            @Override
            public ObservableReportData call(Object... args) {
                ObservableReportData result = new ObservableReportData();
                if(args!=null&&args.length==1){
                    //   result.setLoginDetailsArrayList((ArrayList<LoginDetails>)args[0]);
                    result.setLiveTrackingArrayList((ArrayList< LiveTracking >)args[0]);
                    //  result.setMeetingsArrayList((ArrayList<Meetings>)args[2]);
                    // result.setTasksArrayList((ArrayList<Tasks>)args[3]);
                    // result.setExpensesArrayList((ArrayList<Expenses>)args[4]);

                    data.add(result);
                }
                return result;
            }
        });

        System.out.println("Obser "+data.size());
        if(data.size()!=0){
            //  System.out.println("value "+data.get(0).getLoginDetailsArrayList().size());
        }

       /* combinedObservable.subscribe(item -> System.out.print(item), error -> error.printStackTrace(),
                () -> System.out.println());*/


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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            ReportBulkdataScreen.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAlert() throws Exception {
        AlertDialog.Builder builder = new AlertDialog.Builder( ReportBulkdataScreen.this);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.pop_custom_date,null);
        mFromDate = (TextView)view.findViewById(R.id.filter_book_from_date);
        mToDate = (TextView)view.findViewById(R.id.filter_book_to_date);
        Button mFilterBookings = (Button)view.findViewById(R.id.filter_search_reports);
        builder.setView(view);
        builder.setTitle("Select Date to Filter");

        final AlertDialog dialog = builder.create();
        dialog.show();

        //builder.show();

        mFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker(mFromDate);
            }
        });

        mToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker(mToDate);
            }
        });

        mFilterBookings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String fromdate = mFromDate.getText().toString();
                    String todate = mToDate.getText().toString();

                    if(fromdate == null || fromdate.isEmpty())
                    {
                        Toast.makeText( ReportBulkdataScreen.this,"Please select from date",Toast.LENGTH_LONG).show();
                    }
                    else if(todate == null || todate.isEmpty())
                    {
                        Toast.makeText( ReportBulkdataScreen.this,"Please select to date",Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        if(dialog != null && dialog.isShowing())
                        {
                            dialog.dismiss();
                        }
                        Intent intent = new Intent( ReportBulkdataScreen.this, ReportDownloadingDataService.class);
                        Bundle mBundle = new Bundle();
                        mBundle.putString("StartDate", fromdate);
                        mBundle.putString("EndDate", todate);
                        intent.setAction( ReportDownloadingDataService.ACTION_START_FOREGROUND_SERVICE);
                        intent.putExtras(mBundle);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            startForegroundService(intent);
                        } else {
                            startService(intent);
                        }
                    }
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        });
    }

    public void openDatePicker(final TextView tv) {
        // Get Current Date
        try{
            final Calendar c = Calendar.getInstance();
            int mYear  = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH);
            int mDay   = c.get(Calendar.DAY_OF_MONTH);
            //launch datepicker modal
            DatePickerDialog datePickerDialog = new DatePickerDialog( ReportBulkdataScreen.this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            Log.d("Date", "DATE SELECTED "+dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                            Calendar newDate = Calendar.getInstance();
                            newDate.set(year,monthOfYear,dayOfMonth);

                            String date1 = year+"-"+(monthOfYear + 1)  + "-" + dayOfMonth;

                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");


                            if (tv.equals(mFromDate)){

                                try {
                                    Date tdate = sdf.parse(date1);
                                    String from = sdf.format(tdate);
                                    String to = mToDate.getText().toString();

                                    if(to!=null&&!to.isEmpty()){

                                        Date toDate = sdf.parse(to);

                                        if(tdate.getTime()>=toDate.getTime()){

                                            Toast.makeText( ReportBulkdataScreen.this, "Please select lesser than To Date", Toast.LENGTH_SHORT).show();
                                        }else{
                                            tv.setText(from);
                                        }

                                    }else{
                                        tv.setText(from);
                                    }

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }



                            }else if (tv.equals(mToDate)) {

                                try {
                                    Date tdate = sdf.parse(date1);
                                    String to = sdf.format(tdate);

                                    String from = mFromDate.getText().toString();

                                    if(from!=null&&!from.isEmpty()){

                                        Date fromDate = sdf.parse(from);

                                        if(tdate.getTime()<=fromDate.getTime()){

                                            Toast.makeText( ReportBulkdataScreen.this, "Please select greater than From Date", Toast.LENGTH_SHORT).show();
                                            tv.setText("");
                                        }else{
                                            tv.setText(to);
                                        }

                                    }else{
                                        Toast.makeText( ReportBulkdataScreen.this, "Please Select From Date", Toast.LENGTH_SHORT).show();
                                    }


                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        /*else
                        {
                            dateOfBirth = date1;
                            dob_traveler.setText(dateOfBirth);
                        }*/
                        }
                    }, mYear, mMonth, mDay);

            datePickerDialog.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
