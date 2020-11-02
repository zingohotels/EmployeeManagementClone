package app.zingo.employeemanagements.ui.NewAdminDesigns;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MenuItem;

import java.util.ArrayList;

import app.zingo.employeemanagements.adapter.TaskAdminListAdapter;
import app.zingo.employeemanagements.adapter.TaskListAdapter;
import app.zingo.employeemanagements.model.TaskAdminData;
import app.zingo.employeemanagements.model.Tasks;
import app.zingo.employeemanagements.base.R;

public class PendingTasks extends AppCompatActivity {

    RecyclerView mTaskList;

    ArrayList< TaskAdminData > pendingTasks;
    ArrayList<Tasks> pendingTasksNormal;
    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{

            setContentView(R.layout.activity_pending_tasks);


            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
           // title = "Tasks";
            //setTitle("Pending Tasks");

            mTaskList = findViewById(R.id.task_list_dash);

            Bundle bundle = getIntent().getExtras();

            if(bundle!=null){

                pendingTasks = (ArrayList< TaskAdminData >)bundle.getSerializable("PendingTasks");
                pendingTasksNormal = (ArrayList<Tasks>)bundle.getSerializable("PendingTasksPendingTasksNormal");
                title =bundle.getString("Title");
            }
            if(title!=null&&!title.isEmpty()){
                setTitle(title);
            }else{
                setTitle("Tasks");
            }
            if(pendingTasks!=null&&pendingTasks.size()!=0){

                TaskAdminListAdapter mAdapter = new TaskAdminListAdapter ( PendingTasks.this,pendingTasks);
                mTaskList.setAdapter(mAdapter);


            }else if(pendingTasksNormal!=null&&pendingTasksNormal.size()!=0){

                TaskListAdapter mAdapter = new TaskListAdapter ( PendingTasks.this,pendingTasksNormal);
                mTaskList.setAdapter(mAdapter);


            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id)
        {
            case android.R.id.home:

                PendingTasks.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
