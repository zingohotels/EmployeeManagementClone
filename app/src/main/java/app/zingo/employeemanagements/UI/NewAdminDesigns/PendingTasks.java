package app.zingo.employeemanagements.UI.NewAdminDesigns;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;

import app.zingo.employeemanagements.Adapter.TaskListAdapter;
import app.zingo.employeemanagements.Model.Tasks;
import app.zingo.employeemanagements.R;
import app.zingo.employeemanagements.UI.EmployeeSignUp;

public class PendingTasks extends AppCompatActivity {

    RecyclerView mTaskList;

    ArrayList<Tasks> pendingTasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{

            setContentView(R.layout.activity_pending_tasks);


            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Pending Tasks");

            mTaskList = (RecyclerView) findViewById(R.id.task_list_dash);

            Bundle bundle = getIntent().getExtras();

            if(bundle!=null){

                pendingTasks = (ArrayList<Tasks>)bundle.getSerializable("PendingTasks");
            }
            if(pendingTasks!=null&&pendingTasks.size()!=0){



                TaskListAdapter mAdapter = new TaskListAdapter(PendingTasks.this,pendingTasks);
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
