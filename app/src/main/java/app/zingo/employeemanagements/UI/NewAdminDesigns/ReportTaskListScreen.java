package app.zingo.employeemanagements.UI.NewAdminDesigns;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import junit.framework.Assert;

import java.util.ArrayList;

import app.zingo.employeemanagements.Adapter.TaskListAdapter;
import app.zingo.employeemanagements.Model.Tasks;
import app.zingo.employeemanagements.R;
import app.zingo.employeemanagements.UI.Admin.CreateTaskScreen;
import app.zingo.employeemanagements.UI.Admin.EmployeeTaskMapScreen;

public class ReportTaskListScreen extends AppCompatActivity {

    View layout;
    private TaskListAdapter mAdapter;
    RecyclerView mTaskList;

    Toolbar mToolbar;
    TextView movedTargets;
    TextView closedTargets;
    TextView totalTargets;
    TextView openTargets;
    TextView presentDate;

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

            setContentView(R.layout.activity_report_task_list_screen);

            setupToolbar();
            Bundle bundle = getIntent().getExtras();
            if (bundle!=null) {
                employeeTasks = (ArrayList<Tasks>)bundle.getSerializable("EmployeeTask");
            }

            //            this.mCalendarDay = CalendarDay.from(new Date());
            this.presentDate = (TextView) findViewById(R.id.presentDate);
            // this.presentDate.setText(DateUtil.getReadableDate(this.mCalendarDay));
            mTaskList = (RecyclerView) findViewById(R.id.targetList);
            mTaskList.setLayoutManager(new LinearLayoutManager(this));

            // this.prevDay = (ImageView) findViewById(R.id.previousDay);
            // this.nextDay = (ImageView) findViewById(R.id.nextDay);

            totalTargets = (TextView) findViewById(R.id.totalTargets);
            openTargets = (TextView) findViewById(R.id.openTargets);
            closedTargets = (TextView) findViewById(R.id.closedTargets);
            movedTargets = (TextView) findViewById(R.id.movedTargets);

            mPendingTask = (LinearLayout) findViewById(R.id.openTargetsLayout);
            mCompletedTask = (LinearLayout) findViewById(R.id.closedTargetsLayout);
            mClosedTask = (LinearLayout) findViewById(R.id.movedTargetsLayout);
            mTotalTask = (LinearLayout) findViewById(R.id.totalTargetsLayout);


            mPendingTask.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mTaskList.removeAllViews();

                    if(pendingTasks!=null&&pendingTasks.size()!=0){
                        mAdapter = new TaskListAdapter(ReportTaskListScreen.this,pendingTasks);
                        mTaskList.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                    }else{
                        mAdapter = new TaskListAdapter(ReportTaskListScreen.this,new ArrayList<Tasks>());
                        mTaskList.setAdapter(mAdapter);
                        Toast.makeText(ReportTaskListScreen.this, "No Pending Tasks given for this employee", Toast.LENGTH_SHORT).show();
                    }


                }
            });

            mCompletedTask.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mTaskList.removeAllViews();

                    if(completedTasks!=null&&completedTasks.size()!=0){
                        mAdapter = new TaskListAdapter(ReportTaskListScreen.this,completedTasks);
                        mTaskList.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                    }else{
                        mAdapter = new TaskListAdapter(ReportTaskListScreen.this,new ArrayList<Tasks>());
                        mTaskList.setAdapter(mAdapter);
                        Toast.makeText(ReportTaskListScreen.this, "No Completed Tasks given for this employee", Toast.LENGTH_SHORT).show();
                    }


                }
            });

            mClosedTask.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mTaskList.removeAllViews();

                    if(closedTasks!=null&&closedTasks.size()!=0){
                        mAdapter = new TaskListAdapter(ReportTaskListScreen.this,closedTasks);
                        mTaskList.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                    }else{
                        mAdapter = new TaskListAdapter(ReportTaskListScreen.this,new ArrayList<Tasks>());
                        mTaskList.setAdapter(mAdapter);
                        Toast.makeText(ReportTaskListScreen.this, "No Closed Tasks given for this employee", Toast.LENGTH_SHORT).show();
                    }


                }
            });

            mTotalTask.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mTaskList.removeAllViews();

                    if(employeeTasks!=null&&employeeTasks.size()!=0){
                        mAdapter = new TaskListAdapter(ReportTaskListScreen.this,employeeTasks);
                        mTaskList.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                    }else{
                        mAdapter = new TaskListAdapter(ReportTaskListScreen.this,new ArrayList<Tasks>());
                        mTaskList.setAdapter(mAdapter);
                        Toast.makeText(ReportTaskListScreen.this, "No Tasks given for this employee", Toast.LENGTH_SHORT).show();
                    }


                }
            });

            if(employeeTasks!=null&&employeeTasks.size()!=0){

                pendingTasks = new ArrayList<>();
                completedTasks = new ArrayList<>();
                closedTasks = new ArrayList<>();


                for (Tasks task:employeeTasks) {

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

                    mAdapter = new TaskListAdapter(ReportTaskListScreen.this,employeeTasks);
                    mTaskList.setAdapter(mAdapter);

                    totalTargets.setText(""+total);
                    openTargets.setText(""+pending);
                    closedTargets.setText(""+complete);
                    movedTargets.setText(""+closed);

                }

            }else{
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id)
        {
            case android.R.id.home:

                ReportTaskListScreen.this.finish();

        }
        return super.onOptionsItemSelected(item);
    }
}
