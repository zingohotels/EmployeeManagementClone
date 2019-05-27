package app.zingo.employeemanagements.UI.NewAdminDesigns;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import app.zingo.employeemanagements.Adapter.ExpenseListDataAdapter;
import app.zingo.employeemanagements.Model.Employee;
import app.zingo.employeemanagements.Model.ExpenseAdminData;
import app.zingo.employeemanagements.Model.Expenses;
import app.zingo.employeemanagements.UI.Employee.EmployeeListScreen;
import app.zingo.employeemanagements.Utils.PreferenceHandler;
import app.zingo.employeemanagements.Utils.ThreadExecuter;
import app.zingo.employeemanagements.Utils.Util;
import app.zingo.employeemanagements.WebApi.EmployeeApi;
import app.zingo.employeemanagements.WebApi.ExpensesApi;
import app.zingo.employeemanagements.base.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExpensesListAdmin extends AppCompatActivity {

    FloatingActionButton refresh;
    TextView mDate;
    ImageView mPrevious,mNext;
    //View layout;
    private ExpenseListDataAdapter mAdapter;
    RecyclerView mTaskList;
    static Context mContext;

    SimpleDateFormat dateFormat;

    List<Expenses> mTargetList = new ArrayList();
    Toolbar mToolbar;
    TextView movedTargets;
    TextView closedTargets;
    TextView totalTargets;
    TextView openTargets;
    TextView presentDate;
    ImageView prevDay;
    ImageView nextDay;


    LinearLayout mTotalTask,mPendingTask,mCompletedTask,mClosedTask,mNoRecord;

    ArrayList<ExpenseAdminData> employeeTasks;
    ArrayList<ExpenseAdminData> pendingTasks ;
    ArrayList<ExpenseAdminData> completedTasks ;
    ArrayList<ExpenseAdminData> closedTasks ;

    ArrayList<ExpenseAdminData> dayemployeeTasks;
    ArrayList<ExpenseAdminData> daypendingTasks ;
    ArrayList<ExpenseAdminData> daycompletedTasks ;
    ArrayList<ExpenseAdminData> dayclosedTasks ;

    int total=0,pending=0,complete=0,closed=0;
    int daytotal=0,daypending=0,daycomplete=0,dayclosed=0;

    ArrayList<Employee> emplList;
    String restartdate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{

            setContentView(R.layout.activity_expenses_list_admin);
            setupToolbar();

            presentDate = findViewById(R.id.presentDate);
            // this.presentDate.setText(DateUtil.getReadableDate(this.mCalendarDay));
            mTaskList = findViewById(R.id.targetList);
            mTaskList.setLayoutManager(new LinearLayoutManager(ExpensesListAdmin.this));


            refresh = findViewById(R.id.refresh);
            totalTargets = findViewById(R.id.totalTargets);
            openTargets = findViewById(R.id.openTargets);
            closedTargets = findViewById(R.id.closedTargets);
            movedTargets = findViewById(R.id.movedTargets);

            mPendingTask = findViewById(R.id.openTargetsLayout);
            mCompletedTask = findViewById(R.id.closedTargetsLayout);
            mClosedTask = findViewById(R.id.movedTargetsLayout);
            mTotalTask = findViewById(R.id.totalTargetsLayout);
            mNoRecord = findViewById(R.id.noRecordFound);

            mDate = findViewById(R.id.presentDate);


            mPrevious = findViewById(R.id.previousDay);
            mNext = findViewById(R.id.nextDay);



            dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            mDate.setText(""+new SimpleDateFormat("dd-MM-yyyy").format(new Date()));

            /*this.prevDay.setOnClickListener(new C13241());
            this.nextDay.setOnClickListener(new C13252());*/

            emplList = new ArrayList<>();
            refresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent live = new Intent(ExpensesListAdmin.this, EmployeeListScreen.class);
                    live.putExtra("Type","Expense");
                    startActivity(live);
                }
            });

            mPendingTask.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mTaskList.removeAllViews();

                    if(daypendingTasks!=null&&daypendingTasks.size()!=0){

                        mAdapter = new ExpenseListDataAdapter(ExpensesListAdmin.this,daypendingTasks);
                        mTaskList.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                    }else{
                        mAdapter = new ExpenseListDataAdapter(ExpensesListAdmin.this,new ArrayList<ExpenseAdminData>());
                        mTaskList.setAdapter(mAdapter);
                        Toast.makeText(ExpensesListAdmin.this, "No Pending Expenses are there", Toast.LENGTH_SHORT).show();
                    }


                }
            });

            mCompletedTask.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mTaskList.removeAllViews();

                    if(daycompletedTasks!=null&&daycompletedTasks.size()!=0){
                        mAdapter = new ExpenseListDataAdapter(ExpensesListAdmin.this,daycompletedTasks);
                        mTaskList.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                    }else{
                        mAdapter = new ExpenseListDataAdapter(ExpensesListAdmin.this,new ArrayList<ExpenseAdminData>());
                        mTaskList.setAdapter(mAdapter);
                        Toast.makeText(ExpensesListAdmin.this, "No Appreoved Expenses are there", Toast.LENGTH_SHORT).show();
                    }


                }
            });

            mClosedTask.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mTaskList.removeAllViews();

                    if(dayclosedTasks!=null&&dayclosedTasks.size()!=0){
                        mAdapter = new ExpenseListDataAdapter(ExpensesListAdmin.this,dayclosedTasks);
                        mTaskList.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                    }else{
                        mAdapter = new ExpenseListDataAdapter(ExpensesListAdmin.this,new ArrayList<ExpenseAdminData>());
                        mTaskList.setAdapter(mAdapter);
                        Toast.makeText(ExpensesListAdmin.this, "No Rejected Expenses are there", Toast.LENGTH_SHORT).show();
                    }


                }
            });

            mTotalTask.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mTaskList.removeAllViews();

                    if(dayemployeeTasks!=null&&dayemployeeTasks.size()!=0){
                        mAdapter = new ExpenseListDataAdapter(ExpensesListAdmin.this,dayemployeeTasks);
                        mTaskList.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                    }else{
                        mAdapter = new ExpenseListDataAdapter(ExpensesListAdmin.this,new ArrayList<ExpenseAdminData>());
                        mTaskList.setAdapter(mAdapter);
                        Toast.makeText(ExpensesListAdmin.this, "No Expenses are there", Toast.LENGTH_SHORT).show();
                    }


                }
            });
            mPrevious.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try{
                        final Date date = dateFormat.parse(mDate.getText().toString());
                        final Calendar calendar = Calendar.getInstance();
                        calendar.setTime(date);
                        calendar.add(Calendar.DAY_OF_YEAR, -1);

                        Date date2 = calendar.getTime();

                        mDate.setText(dateFormat.format(date2));

                        restartdate = new SimpleDateFormat("yyyy-MM-dd").format(date2);

                        taskFilter(new SimpleDateFormat("yyyy-MM-dd").format(date2));


                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });

            mNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    try{
                        final Date date = dateFormat.parse(mDate.getText().toString());
                        final Calendar calendar = Calendar.getInstance();
                        calendar.setTime(date);
                        calendar.add(Calendar.DAY_OF_YEAR, 1);

                        Date date2 = calendar.getTime();

                        mDate.setText(dateFormat.format(date2));

                        restartdate = new SimpleDateFormat("yyyy-MM-dd").format(date2);

                        taskFilter(new SimpleDateFormat("yyyy-MM-dd").format(date2));


                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            });

            mDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openDatePicker(mDate);
                }
            });

            getEmployees();

        }catch (Exception e){
            e.printStackTrace();
        }

    }
    public void setupToolbar() {
        mToolbar = findViewById(R.id.app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Expenses");
    }

    private void getEmployees(){




        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {
                EmployeeApi apiService = Util.getClient().create(EmployeeApi.class);
                Call<ArrayList<Employee>> call = apiService.getEmployeesByOrgId(PreferenceHandler.getInstance(ExpensesListAdmin.this).getCompanyId());

                call.enqueue(new Callback<ArrayList<Employee>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Employee>> call, Response<ArrayList<Employee>> response) {
                        int statusCode = response.code();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {


                           /* if (progressDialog != null&&progressDialog.isShowing())
                                progressDialog.dismiss();*/
                            ArrayList<Employee> list = response.body();


                            if (list !=null && list.size()!=0) {

                                ArrayList<Employee> employeesList= list;

                                Collections.sort(list, Employee.compareEmployee);

                                employeeTasks = new ArrayList<>();
                                pendingTasks = new ArrayList<>();
                                completedTasks = new ArrayList<>();
                                closedTasks = new ArrayList<>();

                                dayemployeeTasks = new ArrayList<>();
                                daypendingTasks = new ArrayList<>();
                                daycompletedTasks = new ArrayList<>();
                                dayclosedTasks = new ArrayList<>();

                                emplList = list;

                                for (Employee employee:list) {
                                    final Calendar calendar = Calendar.getInstance();
                                    Date date2 = calendar.getTime();

                                    restartdate = new SimpleDateFormat("yyyy-MM-dd").format(date2);

                                    getExpenses(employee,new SimpleDateFormat("yyyy-MM-dd").format(date2));
                                }
                                //}

                            }else{

                                Toast.makeText(ExpensesListAdmin.this, "No employees added", Toast.LENGTH_SHORT).show();

                            }

                        }else {


                            Toast.makeText(ExpensesListAdmin.this, "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Employee>> call, Throwable t) {
                        // Log error here since request failed
                      /*  if (progressDialog != null&&progressDialog.isShowing())
                            progressDialog.dismiss();*/
                        Toast.makeText(ExpensesListAdmin.this, "No employees added", Toast.LENGTH_SHORT).show();
                        Log.e("TAG", t.toString());
                    }
                });
            }


        });
    }

    private void getExpenses(final Employee employee, final String dateValue){


        final int employeeId = employee.getEmployeeId();


        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {
                ExpensesApi apiService = Util.getClient().create(ExpensesApi.class);
                Call<ArrayList<Expenses>> call = apiService.getExpenseByEmployeeIdAndOrganizationId(PreferenceHandler.getInstance(ExpensesListAdmin.this).getCompanyId(),employeeId);

                call.enqueue(new Callback<ArrayList<Expenses>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Expenses>> call, Response<ArrayList<Expenses>> response) {
                        int statusCode = response.code();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {



                            ArrayList<Expenses> list = response.body();

                            Date date = new Date();
                            Date adate = new Date();
                            Date edate = new Date();

                            try {
                                date = new SimpleDateFormat("yyyy-MM-dd").parse(dateValue);


                            } catch (Exception e) {
                                e.printStackTrace();
                            }



                            if (list !=null && list.size()!=0) {



                                for (Expenses task:list) {


                                    ExpenseAdminData taskAdminData = new ExpenseAdminData();
                                    taskAdminData.setEmployee(employee);
                                    taskAdminData.setExpenses(task);

                                    employeeTasks.add(taskAdminData);
                                    total = total+1;

                                    if(task.getStatus().equalsIgnoreCase("Approved")){
                                        completedTasks.add(taskAdminData);
                                        complete = complete+1;
                                    }else if(task.getStatus().equalsIgnoreCase("Pending")){
                                        pendingTasks.add(taskAdminData);
                                        pending = pending+1;
                                    }else if(task.getStatus().equalsIgnoreCase("Rejected")){
                                        closedTasks.add(taskAdminData);
                                        closed = closed+1;
                                    }




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


                                        }

                                    }



                                    if(afromDate!=null){

                                        if(date.getTime() == afromDate.getTime() ){
                                            ExpenseAdminData taskAdminDatas = new ExpenseAdminData();
                                            taskAdminDatas.setEmployee(employee);
                                            taskAdminDatas.setExpenses(task);

                                            dayemployeeTasks.add(taskAdminDatas);
                                            daytotal = daytotal+1;

                                            if(task.getStatus().equalsIgnoreCase("Approved")){
                                                daycompletedTasks.add(taskAdminData);
                                                daycomplete = daycomplete+1;
                                            }else if(task.getStatus().equalsIgnoreCase("Pending")){
                                                daypendingTasks.add(taskAdminData);
                                                daypending = daypending+1;
                                            }else if(task.getStatus().equalsIgnoreCase("Rejected")){
                                                dayclosedTasks.add(taskAdminData);
                                                dayclosed = dayclosed+1;
                                            }

                                        }
                                    }



                                }

                                if(dayemployeeTasks!=null&&dayemployeeTasks.size()!=0){

                                    mNoRecord.setVisibility(View.GONE);
                                    mTaskList.setVisibility(View.VISIBLE);
                                    mAdapter = new ExpenseListDataAdapter(ExpensesListAdmin.this,dayemployeeTasks);
                                    mTaskList.setAdapter(mAdapter);

                                    totalTargets.setText(""+daytotal);
                                    openTargets.setText(""+daypending);
                                    closedTargets.setText(""+daycomplete);
                                    movedTargets.setText(""+dayclosed);
                                }else{

                                }



                            }else{

                                //Toast.makeText(DailyTargetsForEmployeeActivity.this, "No Tasks given for this employee ", Toast.LENGTH_SHORT).show();

                            }

                        }else {



                            // Toast.makeText(DailyTargetsForEmployeeActivity.this, "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Expenses>> call, Throwable t) {
                        // Log error here since request failed

                        Log.e("TAG", t.toString());
                    }
                });
            }


        });
    }


    public void taskFilter(final String dateValue){

        if(employeeTasks!=null&&employeeTasks.size()!=0){

            mNoRecord.setVisibility(View.VISIBLE);
            mTaskList.setVisibility(View.GONE);
            mTaskList.removeAllViews();

            dayemployeeTasks = new ArrayList<>();
            daypendingTasks = new ArrayList<>();
            daycompletedTasks = new ArrayList<>();
            dayclosedTasks = new ArrayList<>();

            daytotal=0;
            daypending=0;
            daycomplete=0;
            dayclosed=0;

            totalTargets.setText(""+daytotal);
            openTargets.setText(""+daypending);
            closedTargets.setText(""+daycomplete);
            movedTargets.setText(""+dayclosed);

            Date date = new Date();
            Date adate = new Date();
            Date edate = new Date();

            try {
                date = new SimpleDateFormat("yyyy-MM-dd").parse(dateValue);


            } catch (Exception e) {
                e.printStackTrace();
            }



            if (employeeTasks !=null && employeeTasks.size()!=0) {



                for (ExpenseAdminData task:employeeTasks) {


                    String froms = task.getExpenses().getDate();


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


                    if(afromDate!=null){

                        if(date.getTime() == afromDate.getTime()){


                            dayemployeeTasks.add(task);
                            daytotal = daytotal+1;

                            if(task.getExpenses().getStatus().equalsIgnoreCase("Approved")){
                                daycompletedTasks.add(task);
                                daycomplete = daycomplete+1;
                            }else if(task.getExpenses().getStatus().equalsIgnoreCase("Pending")){
                                daypendingTasks.add(task);
                                daypending = daypending+1;
                            }else if(task.getExpenses().getStatus().equalsIgnoreCase("Rejected")){
                                dayclosedTasks.add(task);
                                dayclosed = dayclosed+1;
                            }

                        }
                    }



                }

                if(dayemployeeTasks!=null&&dayemployeeTasks.size()!=0){

                    mNoRecord.setVisibility(View.GONE);
                    mTaskList.setVisibility(View.VISIBLE);

                    mAdapter = new ExpenseListDataAdapter(ExpensesListAdmin.this,dayemployeeTasks);
                    mTaskList.setAdapter(mAdapter);

                    totalTargets.setText(""+daytotal);
                    openTargets.setText(""+daypending);
                    closedTargets.setText(""+daycomplete);
                    movedTargets.setText(""+dayclosed);
                }else{

                }



            }else{

                //Toast.makeText(DailyTargetsForEmployeeActivity.this, "No Tasks given for this employee ", Toast.LENGTH_SHORT).show();

            }



        }
    }

    public void openDatePicker(final TextView tv) {
        // Get Current Date

        final Calendar c = Calendar.getInstance();
        int  mYear  = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay   = c.get(Calendar.DAY_OF_MONTH);
        //launch datepicker modal
        DatePickerDialog datePickerDialog = new DatePickerDialog(ExpensesListAdmin.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        try {
                            Log.d("Date", "DATE SELECTED "+dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                            Calendar newDate = Calendar.getInstance();
                            newDate.set(year,monthOfYear,dayOfMonth);


                            String date1 = (dayOfMonth)  + "-" + (monthOfYear + 1)+ "-" + year;



                            if (tv.equals(mDate)){


                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
                                try {
                                    Date fdate = simpleDateFormat.parse(date1);

                                    String startDate = simpleDateFormat.format(fdate);
                                    tv.setText(""+startDate);

                                    restartdate = new SimpleDateFormat("yyyy-MM-dd").format(fdate);

                                    taskFilter(new SimpleDateFormat("yyyy-MM-dd").format(fdate));



                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                //


                            }

                        }
                        catch (Exception ex)
                        {
                            ex.printStackTrace();
                        }


                    }
                }, mYear, mMonth, mDay);


        datePickerDialog.show();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id)
        {
            case android.R.id.home:

                ExpensesListAdmin.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if(emplList!=null&&emplList.size()!=0&&restartdate!=null&&!restartdate.isEmpty()){

            for (Employee e:emplList) {
                getExpenses(e, restartdate);
            }
        }
    }
}
