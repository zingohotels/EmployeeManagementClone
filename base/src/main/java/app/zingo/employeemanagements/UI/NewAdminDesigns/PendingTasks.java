package app.zingo.employeemanagements.UI.NewAdminDesigns;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;

import app.zingo.employeemanagements.Adapter.TaskAdminListAdapter;
import app.zingo.employeemanagements.Adapter.TaskListAdapter;
import app.zingo.employeemanagements.Model.TaskAdminData;
import app.zingo.employeemanagements.Model.Tasks;
import app.zingo.employeemanagements.base.R;
import app.zingo.employeemanagements.UI.EmployeeSignUp;

public class PendingTasks extends AppCompatActivity {

    RecyclerView mTaskList;

    ArrayList<TaskAdminData> pendingTasks;
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

                pendingTasks = (ArrayList<TaskAdminData>)bundle.getSerializable("PendingTasks");
                title =bundle.getString("Title");
            }
            if(title!=null&&!title.isEmpty()){
                setTitle(title);
            }else{
                setTitle("Tasks");
            }
            if(pendingTasks!=null&&pendingTasks.size()!=0){

                TaskAdminListAdapter mAdapter = new TaskAdminListAdapter(PendingTasks.this,pendingTasks);
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
