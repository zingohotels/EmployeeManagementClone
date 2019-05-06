package app.zingo.employeemanagements.UI.Common;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import java.util.ArrayList;

import app.zingo.employeemanagements.Adapter.LeaveDashBoardAdapter;
import app.zingo.employeemanagements.Adapter.TaskAdminListAdapter;
import app.zingo.employeemanagements.Model.Leaves;
import app.zingo.employeemanagements.Model.TaskAdminData;
import app.zingo.employeemanagements.base.R;
import app.zingo.employeemanagements.UI.NewAdminDesigns.PendingTasks;

public class LeaveListScreen extends AppCompatActivity {

    RecyclerView mLeaveList;

    ArrayList<Leaves> mLeaves;
    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{

            setContentView(R.layout.activity_leave_list_screen);

            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            // title = "Tasks";
            //setTitle("Pending Tasks");

            mLeaveList = findViewById(R.id.leave_list_admin);

            Bundle bundle = getIntent().getExtras();

            if(bundle!=null){

                mLeaves = (ArrayList<Leaves>)bundle.getSerializable("Leaves");
                title =bundle.getString("Title");
            }
            if(title!=null&&!title.isEmpty()){
                setTitle(title);
            }else{
                setTitle("Leaves");
            }
            if(mLeaves!=null&&mLeaves.size()!=0){

                LeaveDashBoardAdapter mAdapter = new LeaveDashBoardAdapter(LeaveListScreen.this,mLeaves);
                mLeaveList.setAdapter(mAdapter);


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

                LeaveListScreen.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
