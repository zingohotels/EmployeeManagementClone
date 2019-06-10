package app.zingo.employeemanagements.UI.NewEmployeeDesign;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import app.zingo.employeemanagements.Adapter.TaskListAdapter;
import app.zingo.employeemanagements.Model.Employee;
import app.zingo.employeemanagements.Model.Tasks;
import app.zingo.employeemanagements.UI.Admin.CreateTaskScreen;
import app.zingo.employeemanagements.Utils.PreferenceHandler;
import app.zingo.employeemanagements.Utils.ThreadExecuter;
import app.zingo.employeemanagements.Utils.Util;
import app.zingo.employeemanagements.WebApi.TasksAPI;
import app.zingo.employeemanagements.base.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class EmployeeTaskFragment extends Fragment {

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

    public EmployeeTaskFragment() {
        // Required empty public constructor
    }

    public static EmployeeTaskFragment getInstance() {
        return new EmployeeTaskFragment();
    }

    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        // GAUtil.trackScreen(getActivity(), "Employer Dashboard");
    }


    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        super.onCreateView(layoutInflater, viewGroup, savedInstanceState);
        try{
            this.layout = layoutInflater.inflate(R.layout.fragment_employee_task, viewGroup, false);

                mEmployeeId = PreferenceHandler.getInstance(getContext()).getUserId();


//            this.mCalendarDay = CalendarDay.from(new Date());
            this.presentDate = layout.findViewById(R.id.presentDate);
            // this.presentDate.setText(DateUtil.getReadableDate(this.mCalendarDay));
            mTaskList = layout.findViewById(R.id.targetList);
            mTaskList.setLayoutManager(new LinearLayoutManager(getContext()));

            // this.prevDay = (ImageView) findViewById(R.id.previousDay);
            // this.nextDay = (ImageView) findViewById(R.id.nextDay);
            this.floatingActionButton = layout.findViewById(R.id.addTargetOption);
            totalTargets = layout.findViewById(R.id.totalTargets);
            openTargets = layout.findViewById(R.id.openTargets);
            closedTargets = layout.findViewById(R.id.closedTargets);
            movedTargets = layout.findViewById(R.id.movedTargets);

            mPendingTask = layout.findViewById(R.id.openTargetsLayout);
            mCompletedTask = layout.findViewById(R.id.closedTargetsLayout);
            mClosedTask = layout.findViewById(R.id.movedTargetsLayout);
            mTotalTask = layout.findViewById(R.id.totalTargetsLayout);

            /*this.prevDay.setOnClickListener(new C13241());
            this.nextDay.setOnClickListener(new C13252());*/
            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent createTask = new Intent(getContext(), CreateTaskScreen.class);
                    createTask.putExtra("EmployeeId", mEmployeeId);
                    createTask.putExtra("Type", "Employee");
                    startActivity(createTask);

                }
            });

            mPendingTask.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mTaskList.removeAllViews();

                    if(pendingTasks!=null&&pendingTasks.size()!=0){
                        mAdapter = new TaskListAdapter(getContext(),pendingTasks);
                        mTaskList.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                    }else{
                        mAdapter = new TaskListAdapter(getContext(),new ArrayList<Tasks>());
                        mTaskList.setAdapter(mAdapter);
                        Toast.makeText(getContext(), "No Pending Tasks given for this employee", Toast.LENGTH_SHORT).show();
                    }


                }
            });

            mCompletedTask.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mTaskList.removeAllViews();

                    if(completedTasks!=null&&completedTasks.size()!=0){
                        mAdapter = new TaskListAdapter(getContext(),completedTasks);
                        mTaskList.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                    }else{
                        mAdapter = new TaskListAdapter(getContext(),new ArrayList<Tasks>());
                        mTaskList.setAdapter(mAdapter);
                        Toast.makeText(getContext(), "No Completed Tasks given for this employee", Toast.LENGTH_SHORT).show();
                    }


                }
            });

            mClosedTask.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mTaskList.removeAllViews();

                    if(closedTasks!=null&&closedTasks.size()!=0){
                        mAdapter = new TaskListAdapter(getContext(),closedTasks);
                        mTaskList.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                    }else{
                        mAdapter = new TaskListAdapter(getContext(),new ArrayList<Tasks>());
                        mTaskList.setAdapter(mAdapter);
                        Toast.makeText(getContext(), "No Closed Tasks given for this employee", Toast.LENGTH_SHORT).show();
                    }


                }
            });

            mTotalTask.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mTaskList.removeAllViews();

                    if(employeeTasks!=null&&employeeTasks.size()!=0){
                        mAdapter = new TaskListAdapter(getContext(),employeeTasks);
                        mTaskList.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                    }else{
                        mAdapter = new TaskListAdapter(getContext(),new ArrayList<Tasks>());
                        mTaskList.setAdapter(mAdapter);
                        Toast.makeText(getContext(), "No Tasks given for this employee", Toast.LENGTH_SHORT).show();
                    }


                }
            });

            getTasks(mEmployeeId);

            return this.layout;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public void onResume() {
        super.onResume();
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
    }

    private void getTasks(final int employeeId){


/*        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Loading Details..");
        progressDialog.setCancelable(false);
        progressDialog.show();*/

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


                           /* if (progressDialog != null&&progressDialog.isShowing())
                                progressDialog.dismiss();*/
                            ArrayList<Tasks> list = response.body();
                            employeeTasks = new ArrayList<>();
                            pendingTasks = new ArrayList<>();
                            completedTasks = new ArrayList<>();
                            closedTasks = new ArrayList<>();



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

                                }

                                if(employeeTasks!=null&&employeeTasks.size()!=0){

                                    mAdapter = new TaskListAdapter(getContext(),employeeTasks);
                                    mTaskList.setAdapter(mAdapter);

                                    totalTargets.setText(""+total);
                                    openTargets.setText(""+pending);
                                    closedTargets.setText(""+complete);
                                    movedTargets.setText(""+closed);
                                }else{
                                    Toast.makeText(getContext(), "No Tasks given for this employee", Toast.LENGTH_SHORT).show();
                                }



                            }else{

                                Toast.makeText(getContext(), "No Tasks given for this employee ", Toast.LENGTH_SHORT).show();

                            }

                        }else {

                           /* if (progressDialog != null&&progressDialog.isShowing())
                                progressDialog.dismiss();*/

                            Toast.makeText(getContext(), "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Tasks>> call, Throwable t) {
                        // Log error here since request failed
                       /* if (progressDialog != null&&progressDialog.isShowing())
                            progressDialog.dismiss();*/
                        Log.e("TAG", t.toString());
                    }
                });
            }


        });
    }

}
