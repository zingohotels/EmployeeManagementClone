package app.zingo.employeemanagements.UI.NewAdminDesigns;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
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
import java.util.Date;
import java.util.List;

import app.zingo.employeemanagements.Adapter.TaskListAdapter;
import app.zingo.employeemanagements.Model.Employee;
import app.zingo.employeemanagements.Model.Tasks;
import app.zingo.employeemanagements.UI.Admin.CreateTaskScreen;
import app.zingo.employeemanagements.UI.Admin.EmployeeTaskMapScreen;
import app.zingo.employeemanagements.Utils.ThreadExecuter;
import app.zingo.employeemanagements.Utils.Util;
import app.zingo.employeemanagements.WebApi.TasksAPI;
import app.zingo.employeemanagements.base.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DailyTargetsForEmployeeActivity extends AppCompatActivity {


    FloatingActionButton floatingActionButton,refresh;
    TextView mDate;
    ImageView mPrevious,mNext;
    //View layout;
    private TaskListAdapter mAdapter;
    RecyclerView mTaskList;

    //CalendarDay mCalendarDay;
    private Employee mEmployee;
    private int mEmployeeId;
    List<Tasks> mTargetList = new ArrayList();
    Toolbar mToolbar;
    TextView movedTargets;
    TextView closedTargets;
    TextView totalTargets;
    TextView openTargets;
    TextView presentDate;
    ImageView prevDay;
    ImageView nextDay;


    LinearLayout mTotalTask,mPendingTask,mCompletedTask,mClosedTask,mNoRecord;

    ArrayList<Tasks> employeeTasks;
    ArrayList<Tasks> pendingTasks ;
    ArrayList<Tasks> completedTasks ;
    ArrayList<Tasks> closedTasks ;

    ArrayList<Tasks> dayemployeeTasks;
    ArrayList<Tasks> daypendingTasks ;
    ArrayList<Tasks> daycompletedTasks ;
    ArrayList<Tasks> dayclosedTasks ;

    int total=0,pending=0,complete=0,closed=0;
    int daytotal=0,daypending=0,daycomplete=0,dayclosed=0;

    SimpleDateFormat dateFormat;
    String passDate = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{

            setContentView(R.layout.activity_daily_targets_for_employee);
            setupToolbar();
            Bundle bundle = getIntent().getExtras();
            if (bundle!=null) {
                mEmployeeId = bundle.getInt("ProfileId");
            }

//            this.mCalendarDay = CalendarDay.from(new Date());
            this.presentDate = findViewById(R.id.presentDate);
           // this.presentDate.setText(DateUtil.getReadableDate(this.mCalendarDay));
            mTaskList = findViewById(R.id.targetList);
            mTaskList.setLayoutManager(new LinearLayoutManager(this));

           // this.prevDay = (ImageView) findViewById(R.id.previousDay);
           // this.nextDay = (ImageView) findViewById(R.id.nextDay);
            this.floatingActionButton = findViewById(R.id.addTargetOption);
            this.refresh = findViewById(R.id.refresh);
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
            mDate.setText(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
            passDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

            /*this.prevDay.setOnClickListener(new C13241());
            this.nextDay.setOnClickListener(new C13252());*/
            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent createTask = new Intent(DailyTargetsForEmployeeActivity.this, CreateTaskScreen.class);
                    createTask.putExtra("EmployeeId", mEmployeeId);
                    startActivity(createTask);

                }
            });

            refresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent createTask = new Intent(DailyTargetsForEmployeeActivity.this, DailyTargetsForEmployeeActivity.class);
                    createTask.putExtra("ProfileId", mEmployeeId);
                    startActivity(createTask);
                    DailyTargetsForEmployeeActivity.this.finish();

                }
            });

            mPendingTask.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mTaskList.removeAllViews();

                    if(daypendingTasks!=null&&daypendingTasks.size()!=0){
                        mAdapter = new TaskListAdapter(DailyTargetsForEmployeeActivity.this,daypendingTasks);
                        mTaskList.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                    }else{
                        mAdapter = new TaskListAdapter(DailyTargetsForEmployeeActivity.this,new ArrayList<Tasks>());
                        mTaskList.setAdapter(mAdapter);
                        Toast.makeText(DailyTargetsForEmployeeActivity.this, "No Pending Tasks given for this employee", Toast.LENGTH_SHORT).show();
                    }


                }
            });

            mCompletedTask.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mTaskList.removeAllViews();

                    if(daycompletedTasks!=null&&daycompletedTasks.size()!=0){
                        mAdapter = new TaskListAdapter(DailyTargetsForEmployeeActivity.this,daycompletedTasks);
                        mTaskList.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                    }else{
                        mAdapter = new TaskListAdapter(DailyTargetsForEmployeeActivity.this,new ArrayList<Tasks>());
                        mTaskList.setAdapter(mAdapter);
                        Toast.makeText(DailyTargetsForEmployeeActivity.this, "No Completed Tasks given for this employee", Toast.LENGTH_SHORT).show();
                    }


                }
            });

            mClosedTask.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mTaskList.removeAllViews();

                    if(dayclosedTasks!=null&&dayclosedTasks.size()!=0){
                        mAdapter = new TaskListAdapter(DailyTargetsForEmployeeActivity.this,dayclosedTasks);
                        mTaskList.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                    }else{
                        mAdapter = new TaskListAdapter(DailyTargetsForEmployeeActivity.this,new ArrayList<Tasks>());
                        mTaskList.setAdapter(mAdapter);
                        Toast.makeText(DailyTargetsForEmployeeActivity.this, "No Closed Tasks given for this employee", Toast.LENGTH_SHORT).show();
                    }


                }
            });

            mTotalTask.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mTaskList.removeAllViews();

                    if(dayemployeeTasks!=null&&dayemployeeTasks.size()!=0){
                        mAdapter = new TaskListAdapter(DailyTargetsForEmployeeActivity.this,dayemployeeTasks);
                        mTaskList.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                    }else{
                        mAdapter = new TaskListAdapter(DailyTargetsForEmployeeActivity.this,new ArrayList<Tasks>());
                        mTaskList.setAdapter(mAdapter);
                        Toast.makeText(DailyTargetsForEmployeeActivity.this, "No Tasks given for this employee", Toast.LENGTH_SHORT).show();
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
                        passDate = new SimpleDateFormat("yyyy-MM-dd").format(date2);

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
                        passDate = new SimpleDateFormat("yyyy-MM-dd").format(date2);
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
            final Calendar calendar = Calendar.getInstance();
            Date date2 = calendar.getTime();

            getTasks(mEmployeeId,new SimpleDateFormat("yyyy-MM-dd").format(date2));


        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void openDatePicker(final TextView tv) {
        // Get Current Date

        final Calendar c = Calendar.getInstance();
        int  mYear  = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay   = c.get(Calendar.DAY_OF_MONTH);
        //launch datepicker modal
        DatePickerDialog datePickerDialog = new DatePickerDialog(DailyTargetsForEmployeeActivity.this,
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


                                    taskFilter(new SimpleDateFormat("yyyy-MM-dd").format(fdate));

                                    String startDate = simpleDateFormat.format(fdate);
                                    tv.setText(startDate);

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

    public void setupToolbar() {
        this.mToolbar = findViewById(R.id.app_bar);
        setSupportActionBar(this.mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Tasks");
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.task_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            DailyTargetsForEmployeeActivity.this.finish();

        } else if (id == R.id.action_map) {
            Intent map = new Intent(DailyTargetsForEmployeeActivity.this, EmployeeTaskMapScreen.class);
            map.putExtra("EmployeeId", mEmployeeId);
            map.putExtra("Date", passDate);
            startActivity(map);

        }
        return super.onOptionsItemSelected(item);
    }

  /*  public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 16908332) {
            return super.onOptionsItemSelected(menuItem);
        }
        finish();
        return true;
    }*/

    private void getTasks(final int employeeId,final String dateValue){


        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading Details..");
        progressDialog.setCancelable(false);
        progressDialog.show();

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


                            if (progressDialog != null&&progressDialog.isShowing())
                                progressDialog.dismiss();
                            ArrayList<Tasks> list = response.body();
                            employeeTasks = new ArrayList<>();
                            pendingTasks = new ArrayList<>();
                            completedTasks = new ArrayList<>();
                            closedTasks = new ArrayList<>();


                            dayemployeeTasks = new ArrayList<>();
                            daypendingTasks = new ArrayList<>();
                            daycompletedTasks = new ArrayList<>();
                            dayclosedTasks = new ArrayList<>();

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




                                    employeeTasks.add(task);
                                    total = total+1;

                                    if(task.getStatus().equalsIgnoreCase("Completed")){
                                        completedTasks.add(task);
                                        complete = complete+1;
                                    }else if(task.getStatus().equalsIgnoreCase("Pending")){
                                        pendingTasks.add(task);
                                        pending = pending+1;
                                    }else if(task.getStatus().equalsIgnoreCase("Closed")){
                                        closedTasks.add(task);
                                        closed = closed+1;
                                    }




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


                                            dayemployeeTasks.add(task);
                                            daytotal = daytotal+1;

                                            if(task.getStatus().equalsIgnoreCase("Completed")){
                                                daycompletedTasks.add(task);
                                                daycomplete = daycomplete+1;
                                            }else if(task.getStatus().equalsIgnoreCase("Pending")){
                                                daypendingTasks.add(task);
                                                daypending = daypending+1;
                                            }else if(task.getStatus().equalsIgnoreCase("Closed")){
                                                dayclosedTasks.add(task);
                                                dayclosed = dayclosed+1;
                                            }

                                        }
                                    }



                                }

                                if(dayemployeeTasks!=null&&dayemployeeTasks.size()!=0){

                                    mNoRecord.setVisibility(View.GONE);
                                    mTaskList.setVisibility(View.VISIBLE);
                                    mAdapter = new TaskListAdapter(DailyTargetsForEmployeeActivity.this,dayemployeeTasks);
                                    mTaskList.setAdapter(mAdapter);

                                    totalTargets.setText(""+daytotal);
                                    openTargets.setText(""+daypending);
                                    closedTargets.setText(""+daycomplete);
                                    movedTargets.setText(""+dayclosed);
                                }else{

                                    mNoRecord.setVisibility(View.VISIBLE);
                                    mTaskList.setVisibility(View.GONE);
                                }



                            }else{

                                mNoRecord.setVisibility(View.VISIBLE);
                                mTaskList.setVisibility(View.GONE);

                                Toast.makeText(DailyTargetsForEmployeeActivity.this, "No Tasks given for this employee ", Toast.LENGTH_SHORT).show();

                            }

                        }else {

                            if (progressDialog != null&&progressDialog.isShowing())
                                progressDialog.dismiss();

                            Toast.makeText(DailyTargetsForEmployeeActivity.this, "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Tasks>> call, Throwable t) {
                        // Log error here since request failed
                        if (progressDialog != null&&progressDialog.isShowing())
                            progressDialog.dismiss();
                        mNoRecord.setVisibility(View.VISIBLE);
                        mTaskList.setVisibility(View.GONE);
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



                for (Tasks task:employeeTasks) {


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


                            dayemployeeTasks.add(task);
                            daytotal = daytotal+1;

                            if(task.getStatus().equalsIgnoreCase("Completed")){
                                daycompletedTasks.add(task);
                                daycomplete = daycomplete+1;
                            }else if(task.getStatus().equalsIgnoreCase("Pending")){
                                daypendingTasks.add(task);
                                daypending = daypending+1;
                            }else if(task.getStatus().equalsIgnoreCase("Closed")){
                                dayclosedTasks.add(task);
                                dayclosed = dayclosed+1;
                            }

                        }
                    }



                }

                if(dayemployeeTasks!=null&&dayemployeeTasks.size()!=0){

                    mNoRecord.setVisibility(View.GONE);
                    mTaskList.setVisibility(View.VISIBLE);

                    mAdapter = new TaskListAdapter(DailyTargetsForEmployeeActivity.this,dayemployeeTasks);
                    mTaskList.setAdapter(mAdapter);

                    totalTargets.setText(""+daytotal);
                    openTargets.setText(""+daypending);
                    closedTargets.setText(""+daycomplete);
                    movedTargets.setText(""+dayclosed);
                }else{


                }



            }else{

                //Toast.makeText(DailyTargetsForEmployeeActivity.this, "No Tasks given for this employee ", Toast.LENGTH_SHORT).show();
                mNoRecord.setVisibility(View.VISIBLE);
                mTaskList.setVisibility(View.GONE);
                mTaskList.removeAllViews();

            }



        }else{
            mNoRecord.setVisibility(View.VISIBLE);
            mTaskList.setVisibility(View.GONE);
            mTaskList.removeAllViews();
        }
    }

}
