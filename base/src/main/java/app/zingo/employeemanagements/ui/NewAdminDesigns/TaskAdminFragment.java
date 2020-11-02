package app.zingo.employeemanagements.ui.NewAdminDesigns;


import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import app.zingo.employeemanagements.adapter.TaskAdminListAdapter;
import app.zingo.employeemanagements.model.Employee;
import app.zingo.employeemanagements.model.TaskAdminData;
import app.zingo.employeemanagements.model.Tasks;
import app.zingo.employeemanagements.utils.PreferenceHandler;
import app.zingo.employeemanagements.utils.ThreadExecuter;
import app.zingo.employeemanagements.utils.Util;
import app.zingo.employeemanagements.WebApi.EmployeeApi;
import app.zingo.employeemanagements.WebApi.TasksAPI;
import app.zingo.employeemanagements.base.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseTooltip;

/**
 * A simple {@link Fragment} subclass. //fragment_task_admin
 */
public class TaskAdminFragment extends Fragment {

    final String TAG = "Employer Task Employee";
    View layout;
    FloatingActionButton refresh;
    TextView mDate;
    ImageView mPrevious,mNext;
    //View layout;
    private TaskAdminListAdapter mAdapter;
    RecyclerView mTaskList;
    static Context mContext;

    SimpleDateFormat dateFormat;

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

    ArrayList<TaskAdminData> employeeTasks;
    ArrayList<TaskAdminData> pendingTasks ;
    ArrayList<TaskAdminData> completedTasks ;
    ArrayList<TaskAdminData> closedTasks ;

    ArrayList<TaskAdminData> dayemployeeTasks;
    ArrayList<TaskAdminData> daypendingTasks ;
    ArrayList<TaskAdminData> daycompletedTasks ;
    ArrayList<TaskAdminData> dayclosedTasks ;

    int total=0,pending=0,complete=0,closed=0;
    int daytotal=0,daypending=0,daycomplete=0,dayclosed=0;
    private   String SHOWCASE_ID_ADMIN;


    public TaskAdminFragment() {
        // Required empty public constructor
    }

    public static TaskAdminFragment getInstance() {
        return new TaskAdminFragment();
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
            this.layout = layoutInflater.inflate(R.layout.fragment_task_admin, viewGroup, false);

            this.presentDate = this.layout.findViewById(R.id.presentDate);
            SHOWCASE_ID_ADMIN = "ToolsAdminst"+ PreferenceHandler.getInstance(getActivity()).getUserId();
            // this.presentDate.setText(DateUtil.getReadableDate(this.mCalendarDay));
            mTaskList = this.layout.findViewById(R.id.targetList);
            mTaskList.setLayoutManager(new LinearLayoutManager(getActivity()));

            // this.prevDay = (ImageView) findViewById(R.id.previousDay);
            // this.nextDay = (ImageView) findViewById(R.id.nextDay);
            this.refresh = this.layout.findViewById(R.id.refresh);
            totalTargets = this.layout.findViewById(R.id.totalTargets);
            openTargets = this.layout.findViewById(R.id.openTargets);
            closedTargets = this.layout.findViewById(R.id.closedTargets);
            movedTargets = this.layout.findViewById(R.id.movedTargets);

            mPendingTask = this.layout.findViewById(R.id.openTargetsLayout);
            mCompletedTask = this.layout.findViewById(R.id.closedTargetsLayout);
            mClosedTask = this.layout.findViewById(R.id.movedTargetsLayout);
            mTotalTask = this.layout.findViewById(R.id.totalTargetsLayout);
            mNoRecord = this.layout.findViewById(R.id.noRecordFound);

            mDate = this.layout.findViewById(R.id.presentDate);


            mPrevious = this.layout.findViewById(R.id.previousDay);
            mNext = this.layout.findViewById(R.id.nextDay);

            mContext = getContext();

            dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            mDate.setText(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));

            /*this.prevDay.setOnClickListener(new C13241());
            this.nextDay.setOnClickListener(new C13252());*/


            refresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent viewEmpl  = new Intent(getActivity(), TaskAdminViewActivity.class);
                    startActivity(viewEmpl);
                }
            });

            mPendingTask.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.LOLLIPOP) {
                        presentShowcaseView();
                    }

                    //presentShowcaseView();

                    mTaskList.removeAllViews();

                    if(daypendingTasks!=null&&daypendingTasks.size()!=0){

                        mAdapter = new TaskAdminListAdapter(getActivity(),daypendingTasks);
                        mTaskList.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                    }else{
                        mAdapter = new TaskAdminListAdapter(getActivity(),new ArrayList<TaskAdminData>());
                        mTaskList.setAdapter(mAdapter);
                        Toast.makeText(getActivity(), "No Pending Tasks are there", Toast.LENGTH_SHORT).show();
                    }


                }
            });

            mCompletedTask.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.LOLLIPOP) {
                        presentShowcaseView();
                    }

                    mTaskList.removeAllViews();

                    if(daycompletedTasks!=null&&daycompletedTasks.size()!=0){
                        mAdapter = new TaskAdminListAdapter(getActivity(),daycompletedTasks);
                        mTaskList.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                    }else{
                        mAdapter = new TaskAdminListAdapter(getActivity(),new ArrayList<TaskAdminData>());
                        mTaskList.setAdapter(mAdapter);
                        Toast.makeText(getActivity(), "No Completed Tasks are there", Toast.LENGTH_SHORT).show();
                    }


                }
            });

            mClosedTask.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.LOLLIPOP) {
                        presentShowcaseView();
                    }

                    mTaskList.removeAllViews();

                    if(dayclosedTasks!=null&&dayclosedTasks.size()!=0){
                        mAdapter = new TaskAdminListAdapter(getActivity(),dayclosedTasks);
                        mTaskList.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                    }else{
                        mAdapter = new TaskAdminListAdapter(getActivity(),new ArrayList<TaskAdminData>());
                        mTaskList.setAdapter(mAdapter);
                        Toast.makeText(getActivity(), "No Closed Tasks are there", Toast.LENGTH_SHORT).show();
                    }


                }
            });

            mTotalTask.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.LOLLIPOP) {
                        presentShowcaseView();
                    }

                    mTaskList.removeAllViews();

                    if(dayemployeeTasks!=null&&dayemployeeTasks.size()!=0){
                        mAdapter = new TaskAdminListAdapter(getActivity(),dayemployeeTasks);
                        mTaskList.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                    }else{
                        mAdapter = new TaskAdminListAdapter(getActivity(),new ArrayList<TaskAdminData>());
                        mTaskList.setAdapter(mAdapter);
                        Toast.makeText(getActivity(), "No Tasks are there", Toast.LENGTH_SHORT).show();
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

                        taskFilter(new SimpleDateFormat("yyyy-MM-dd").format(date2));


                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            });

            mDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.LOLLIPOP) {
                        presentShowcaseView();
                    }
                    openDatePicker(mDate);
                }
            });

            getEmployees();

            return this.layout;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
    public void openDatePicker(final TextView tv) {
        // Get Current Date

        final Calendar c = Calendar.getInstance();
        int  mYear  = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay   = c.get(Calendar.DAY_OF_MONTH);
        //launch datepicker modal
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
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
    public void onResume() {
        super.onResume();
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
    }

    private void getEmployees(){




        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {
                EmployeeApi apiService = Util.getClient().create(EmployeeApi.class);
                Call<ArrayList<Employee>> call = apiService.getEmployeesByOrgId(PreferenceHandler.getInstance(getActivity()).getCompanyId());

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


                                for (Employee employee:list) {
                                    final Calendar calendar = Calendar.getInstance();
                                    Date date2 = calendar.getTime();

                                    getTasks(employee,new SimpleDateFormat("yyyy-MM-dd").format(date2));
                                }
                                //}

                            }else{

                                Toast.makeText(getActivity(), "No employees added", Toast.LENGTH_SHORT).show();

                            }

                        }else {


                            Toast.makeText(getActivity(), "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Employee>> call, Throwable t) {
                        // Log error here since request failed
                      /*  if (progressDialog != null&&progressDialog.isShowing())
                            progressDialog.dismiss();*/
                        Toast.makeText(getActivity(), "No employees added", Toast.LENGTH_SHORT).show();
                        Log.e("TAG", t.toString());
                    }
                });
            }


        });
    }

    private void getTasks(final Employee employee, final String dateValue){


        final int employeeId = employee.getEmployeeId();


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



                            ArrayList<Tasks> list = response.body();

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


                                    if(task.getCategory()==null){



                                        TaskAdminData taskAdminData = new TaskAdminData();
                                        taskAdminData.setEmployee(employee);
                                        taskAdminData.setTasks(task);

                                        employeeTasks.add(taskAdminData);
                                        total = total+1;

                                        if(task.getStatus().equalsIgnoreCase("Completed")){
                                            completedTasks.add(taskAdminData);
                                            complete = complete+1;
                                        }else if(task.getStatus().equalsIgnoreCase("Pending")){
                                            pendingTasks.add(taskAdminData);
                                            pending = pending+1;
                                        }else if(task.getStatus().equalsIgnoreCase("Closed")){
                                            closedTasks.add(taskAdminData);
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
                                                TaskAdminData taskAdminDatas = new TaskAdminData();
                                                taskAdminDatas.setEmployee(employee);
                                                taskAdminDatas.setTasks(task);

                                                dayemployeeTasks.add(taskAdminDatas);
                                                daytotal = daytotal+1;

                                                if(task.getStatus().equalsIgnoreCase("Completed")){
                                                    daycompletedTasks.add(taskAdminData);
                                                    daycomplete = daycomplete+1;
                                                }else if(task.getStatus().equalsIgnoreCase("Pending")){
                                                    daypendingTasks.add(taskAdminData);
                                                    daypending = daypending+1;
                                                }else if(task.getStatus().equalsIgnoreCase("Closed")){
                                                    dayclosedTasks.add(taskAdminData);
                                                    dayclosed = dayclosed+1;
                                                }

                                            }
                                        }

                                    }


                                }

                                if(dayemployeeTasks!=null&&dayemployeeTasks.size()!=0){

                                    mNoRecord.setVisibility(View.GONE);
                                    mTaskList.setVisibility(View.VISIBLE);
                                    mAdapter = new TaskAdminListAdapter(getActivity(),dayemployeeTasks);
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
                    public void onFailure(Call<ArrayList<Tasks>> call, Throwable t) {
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



                for (TaskAdminData task:employeeTasks) {


                    String froms = task.getTasks().getStartDate();
                    String tos = task.getTasks().getEndDate();

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

                            if(task.getTasks().getStatus().equalsIgnoreCase("Completed")){
                                daycompletedTasks.add(task);
                                daycomplete = daycomplete+1;
                            }else if(task.getTasks().getStatus().equalsIgnoreCase("Pending")){
                                daypendingTasks.add(task);
                                daypending = daypending+1;
                            }else if(task.getTasks().getStatus().equalsIgnoreCase("Closed")){
                                dayclosedTasks.add(task);
                                dayclosed = dayclosed+1;
                            }

                        }
                    }



                }

                if(dayemployeeTasks!=null&&dayemployeeTasks.size()!=0){

                    mNoRecord.setVisibility(View.GONE);
                    mTaskList.setVisibility(View.VISIBLE);

                    mAdapter = new TaskAdminListAdapter(getActivity(),dayemployeeTasks);
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

    void presentShowcaseView() {

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(getActivity(), SHOWCASE_ID_ADMIN);


        ShowcaseTooltip toolTip1 = ShowcaseTooltip.build(getActivity())
                .corner(30)
                .textColor(Color.parseColor("#007686"))
                .text("Click here to view all task lists of your employees<br><br>Tap anywhere to continue");


        sequence.addSequenceItem(

                new MaterialShowcaseView.Builder(getActivity())
                        .setTarget(mTotalTask)
                        .setToolTip(toolTip1)
                        .withRectangleShape()
                        .setTooltipMargin(30)
                        .setShapePadding(50)
                        .setDismissOnTouch(true)
                        .setMaskColour(getResources().getColor(R.color.tooltip_mask))
                        .build()
        );


        ShowcaseTooltip toolTip2 = ShowcaseTooltip.build(getActivity())
                .corner(30)
                .textColor(Color.parseColor("#007686"))
                .text("Click here to view pending task list of your employees<br><br>Tap anywhere to continue");

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(getActivity())
                        .setTarget(mPendingTask)
                        .setToolTip(toolTip2)
                        .setTooltipMargin(20)
                        .setShapePadding(30)
                        .setDismissOnTouch(true)
                        .setMaskColour(getResources().getColor(R.color.tooltip_mask))
                        .build()
        );

        ShowcaseTooltip toolTip3 = ShowcaseTooltip.build(getActivity())
                .corner(30)
                .textColor(Color.parseColor("#007686"))
                .text("Click here to view completed task lists of your employees<br><br>Tap anywhere to continue");

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(getActivity())
                        .setTarget(mCompletedTask)
                        .setToolTip(toolTip3)
                        .setTooltipMargin(20)
                        .setShapePadding(30)
                        .setDismissOnTouch(true)
                        .setMaskColour(getResources().getColor(R.color.tooltip_mask))
                        .build()
        );

        ShowcaseTooltip toolTip4 = ShowcaseTooltip.build(getActivity())
                .corner(30)
                .textColor(Color.parseColor("#007686"))
                .text("Click here to view all closed task lists of your employees<br><br>Tap anywhere to continue");

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(getActivity())
                        .setTarget(mClosedTask)
                        .setToolTip(toolTip4)
                        .setTooltipMargin(20)
                        .setShapePadding(30)
                        .setDismissOnTouch(true)
                        .setMaskColour(getResources().getColor(R.color.tooltip_mask))
                        .build()
        );

        ShowcaseTooltip toolTip5 = ShowcaseTooltip.build(getActivity())
                .corner(30)
                .textColor(Color.parseColor("#007686"))
                .text("Click here to change date and see below details based on selected date<br><br>Tap anywhere to continue");

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(getActivity())
                        .setTarget(mDate)
                        .setToolTip(toolTip5)
                        .setTooltipMargin(20)
                        .setShapePadding(30)
                        .setDismissOnTouch(true)
                        .setMaskColour(getResources().getColor(R.color.tooltip_mask))
                        .build()
        );




        sequence.start();






    }


}
