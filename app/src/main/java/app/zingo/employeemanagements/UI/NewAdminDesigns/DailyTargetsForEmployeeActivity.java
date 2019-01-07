package app.zingo.employeemanagements.UI.NewAdminDesigns;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.List;

import app.zingo.employeemanagements.Adapter.TaskAdapter;
import app.zingo.employeemanagements.Adapter.TaskListAdapter;
import app.zingo.employeemanagements.Model.Employee;
import app.zingo.employeemanagements.Model.Tasks;
import app.zingo.employeemanagements.R;
import app.zingo.employeemanagements.UI.Admin.CreateTaskScreen;
import app.zingo.employeemanagements.UI.Admin.TaskListScreen;
import app.zingo.employeemanagements.Utils.ThreadExecuter;
import app.zingo.employeemanagements.Utils.Util;
import app.zingo.employeemanagements.WebApi.TasksAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DailyTargetsForEmployeeActivity extends AppCompatActivity {


    FloatingActionButton floatingActionButton;
    View layout;
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


    LinearLayout mTotalTask,mPendingTask,mCompletedTask,mClosedTask;

    ArrayList<Tasks> employeeTasks;
    ArrayList<Tasks> pendingTasks ;
    ArrayList<Tasks> completedTasks ;
    ArrayList<Tasks> closedTasks ;

    int total=0,pending=0,complete=0,closed=0;

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
            this.presentDate = (TextView) findViewById(R.id.presentDate);
           // this.presentDate.setText(DateUtil.getReadableDate(this.mCalendarDay));
            mTaskList = (RecyclerView) findViewById(R.id.targetList);
            mTaskList.setLayoutManager(new LinearLayoutManager(this));

           // this.prevDay = (ImageView) findViewById(R.id.previousDay);
           // this.nextDay = (ImageView) findViewById(R.id.nextDay);
            this.floatingActionButton = (FloatingActionButton) findViewById(R.id.addTargetOption);
            totalTargets = (TextView) findViewById(R.id.totalTargets);
            openTargets = (TextView) findViewById(R.id.openTargets);
            closedTargets = (TextView) findViewById(R.id.closedTargets);
            movedTargets = (TextView) findViewById(R.id.movedTargets);

            mPendingTask = (LinearLayout) findViewById(R.id.openTargetsLayout);
            mCompletedTask = (LinearLayout) findViewById(R.id.closedTargetsLayout);
            mClosedTask = (LinearLayout) findViewById(R.id.movedTargetsLayout);
            mTotalTask = (LinearLayout) findViewById(R.id.totalTargetsLayout);

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

            mPendingTask.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mTaskList.removeAllViews();

                    if(pendingTasks!=null&&pendingTasks.size()!=0){
                        mAdapter = new TaskListAdapter(DailyTargetsForEmployeeActivity.this,pendingTasks);
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

                    if(completedTasks!=null&&completedTasks.size()!=0){
                        mAdapter = new TaskListAdapter(DailyTargetsForEmployeeActivity.this,completedTasks);
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

                    if(closedTasks!=null&&closedTasks.size()!=0){
                        mAdapter = new TaskListAdapter(DailyTargetsForEmployeeActivity.this,closedTasks);
                        mTaskList.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                    }else{
                        mAdapter = new TaskListAdapter(DailyTargetsForEmployeeActivity.this,new ArrayList<Tasks>());
                        mTaskList.setAdapter(mAdapter);
                        Toast.makeText(DailyTargetsForEmployeeActivity.this, "No Closed Pending Tasks given for this employee", Toast.LENGTH_SHORT).show();
                    }


                }
            });

            mTotalTask.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mTaskList.removeAllViews();

                    if(employeeTasks!=null&&employeeTasks.size()!=0){
                        mAdapter = new TaskListAdapter(DailyTargetsForEmployeeActivity.this,employeeTasks);
                        mTaskList.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                    }else{
                        mAdapter = new TaskListAdapter(DailyTargetsForEmployeeActivity.this,new ArrayList<Tasks>());
                        mTaskList.setAdapter(mAdapter);
                        Toast.makeText(DailyTargetsForEmployeeActivity.this, "No Tasks given for this employee", Toast.LENGTH_SHORT).show();
                    }


                }
            });

            getTasks(mEmployeeId);


        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void setupToolbar() {
        this.mToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(this.mToolbar);
        Assert.assertNotNull(getSupportActionBar());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Tasks");
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 16908332) {
            return super.onOptionsItemSelected(menuItem);
        }
        finish();
        return true;
    }

    private void getTasks(final int employeeId){


        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading Details..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {
                TasksAPI apiService = Util.getClient().create(TasksAPI.class);
                Call<ArrayList<Tasks>> call = apiService.getTasks();

                call.enqueue(new Callback<ArrayList<Tasks>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Tasks>> call, Response<ArrayList<Tasks>> response) {
                        int statusCode = response.code();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {


                            if (progressDialog!=null)
                                progressDialog.dismiss();
                            ArrayList<Tasks> list = response.body();
                            employeeTasks = new ArrayList<>();
                            pendingTasks = new ArrayList<>();
                            completedTasks = new ArrayList<>();
                            closedTasks = new ArrayList<>();



                            if (list !=null && list.size()!=0) {


                                for (Tasks task:list) {

                                    if(task.getEmployeeId()==employeeId){

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

                                    }

                                }

                                if(employeeTasks!=null&&employeeTasks.size()!=0){
                                    mAdapter = new TaskListAdapter(DailyTargetsForEmployeeActivity.this,employeeTasks);
                                    mTaskList.setAdapter(mAdapter);

                                    totalTargets.setText(""+total);
                                    openTargets.setText(""+pending);
                                    closedTargets.setText(""+complete);
                                    movedTargets.setText(""+closed);
                                }else{
                                    Toast.makeText(DailyTargetsForEmployeeActivity.this, "No Tasks given for this employee", Toast.LENGTH_SHORT).show();
                                }



                            }else{

                                Toast.makeText(DailyTargetsForEmployeeActivity.this, "No Tasks given for this employee ", Toast.LENGTH_SHORT).show();

                            }

                        }else {

                            if (progressDialog!=null)
                                progressDialog.dismiss();

                            Toast.makeText(DailyTargetsForEmployeeActivity.this, "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Tasks>> call, Throwable t) {
                        // Log error here since request failed
                        if (progressDialog!=null)
                            progressDialog.dismiss();
                        Log.e("TAG", t.toString());
                    }
                });
            }


        });
    }
}
