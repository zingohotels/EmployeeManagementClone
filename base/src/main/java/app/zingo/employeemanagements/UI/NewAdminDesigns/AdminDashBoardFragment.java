package app.zingo.employeemanagements.UI.NewAdminDesigns;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import app.zingo.employeemanagements.Adapter.LiveTrackingAdapter;
import app.zingo.employeemanagements.Adapter.MeetingDetailAdapter;
import app.zingo.employeemanagements.Adapter.TaskAdminListAdapter;
import app.zingo.employeemanagements.Custom.MyRegulerText;
import app.zingo.employeemanagements.Model.Departments;
import app.zingo.employeemanagements.Model.Employee;
import app.zingo.employeemanagements.Model.Leaves;
import app.zingo.employeemanagements.Model.LiveTracking;
import app.zingo.employeemanagements.Model.LoginDetails;
import app.zingo.employeemanagements.Model.Meetings;
import app.zingo.employeemanagements.Model.TaskAdminData;
import app.zingo.employeemanagements.Model.Tasks;
import app.zingo.employeemanagements.UI.Employee.CreateEmployeeScreen;
import app.zingo.employeemanagements.UI.Employee.EmployeeListScreen;
import app.zingo.employeemanagements.UI.Landing.InternalServerErrorScreen;
import app.zingo.employeemanagements.Utils.PreferenceHandler;
import app.zingo.employeemanagements.Utils.ThreadExecuter;
import app.zingo.employeemanagements.Utils.Util;
import app.zingo.employeemanagements.WebApi.DepartmentApi;
import app.zingo.employeemanagements.WebApi.EmployeeApi;
import app.zingo.employeemanagements.WebApi.LeaveAPI;
import app.zingo.employeemanagements.WebApi.LiveTrackingAPI;
import app.zingo.employeemanagements.WebApi.LoginDetailsAPI;
import app.zingo.employeemanagements.WebApi.MeetingsAPI;
import app.zingo.employeemanagements.WebApi.TasksAPI;
import app.zingo.employeemanagements.base.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;
import uk.co.deanwild.materialshowcaseview.ShowcaseTooltip;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdminDashBoardFragment extends Fragment {

    final String TAG = "Employer Dash";
    View layout;
    RecyclerView mTaskList,mLiveList,mMeetingList;
    LinearLayout mPendingLayout,mOnTaskLay,mDepartmentLay,mEmployeeLay,mPresenEmploLay,mAbEmLayy,mLeaveLay,mNoMeeting;//mTaskLayout
    private TaskAdminListAdapter mAdapter;
    private LiveTrackingAdapter mLiveAdapter;
    MyRegulerText mDeptCount,mEmployeeCount,mOnTask,mPending,mEmployeePresent,mEmployeeAbsent,
                    mLeaveEmployee,mTaskREad,mLiveRead,mMeetingRead;//mUnmarkedEmployee

    static Context mContext;
    LinearLayout mNoRecord,mLiveLay;


    int employee=0,onTasks=0,pendingTask=0,presentEmployee=0,
                absentEmployee=0,leaveEmployee=0;

    boolean checkValue = false;

    ArrayList<TaskAdminData> employeeTasks;
    ArrayList<TaskAdminData> pendingTasks ;
    ArrayList<TaskAdminData> completedTasks ;
    ArrayList<TaskAdminData> closedTasks ;
    ArrayList<TaskAdminData> onTask ;
    ArrayList<TaskAdminData> todayTasks;
    ArrayList<LiveTracking> todayLIve;

    ArrayList<Employee> presentEmployees;
    ArrayList<Employee> absentEmployees;
    ArrayList<Employee> allEmployees;
    ArrayList<Employee> leaveEmployees;

    ArrayList<Integer> preEmpId;
    ArrayList<Integer> leaEmpId;
    ArrayList<Integer> absEmpId;
    ArrayList<Integer> all;
    ArrayList<Meetings> dayemployeeMeetings;

    Handler h;
    Runnable runnable;
    int delay = 2*1000;

    private  String SHOWCASE_ID_ADMIN ;


    public AdminDashBoardFragment() {
    }

    public static AdminDashBoardFragment getInstance() {
        return new AdminDashBoardFragment();
    }

    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);

    }


    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup,
                             Bundle savedInstanceState) {

        super.onCreateView(layoutInflater, viewGroup, savedInstanceState);
        try{
            this.layout = layoutInflater.inflate(R.layout.fragment_admin_dash_board, viewGroup, false);

            mContext = getContext();

            SHOWCASE_ID_ADMIN = "ToolsAdmins"+PreferenceHandler.getInstance(getActivity()).getUserId();

            mTaskList = layout.findViewById(R.id.task_list_dash);
            mTaskList.setLayoutManager(new LinearLayoutManager(getActivity()));

            mLiveList = layout.findViewById(R.id.location_list_track);
            mLiveList.setLayoutManager(new LinearLayoutManager(getActivity()));
           // mTaskLayout = (LinearLayout) layout.findViewById(R.id.today_task_list);
            mPendingLayout = layout.findViewById(R.id.pending_task_layout);
            mOnTaskLay = layout.findViewById(R.id.on_task_count_layout);
            mDepartmentLay = layout.findViewById(R.id.dept_count_layout);
            mEmployeeLay = layout.findViewById(R.id.employee_count_layout);
            mPresenEmploLay = layout.findViewById(R.id.present_count_layout);
            mAbEmLayy = layout.findViewById(R.id.absent_count_layout);
            mLeaveLay = layout.findViewById(R.id.leave_count_layout);
            mDeptCount = layout.findViewById(R.id.dept_count_text);
            mEmployeeCount = layout.findViewById(R.id.employee_count_text);
            mOnTask = layout.findViewById(R.id.on_task_count_text);
            mPending = layout.findViewById(R.id.pending_task_text);
            mEmployeePresent = layout.findViewById(R.id.today_employee_present);
            mEmployeeAbsent = layout.findViewById(R.id.absent_employee);
            mLeaveEmployee = layout.findViewById(R.id.leave_employees);
            mTaskREad = layout.findViewById(R.id.read_more);
            mLiveRead = layout.findViewById(R.id.read_more_live);
            mNoMeeting = layout.findViewById(R.id.noRecordFound_meetings);
            mMeetingList = layout.findViewById(R.id.targetList_meeting);
            mMeetingRead = layout.findViewById(R.id.read_meeting);
           // mUnmarkedEmployee = (MyRegulerText)layout.findViewById(R.id.unmarked_employees);

            mNoRecord = layout.findViewById(R.id.noRecordFound);
            mLiveLay = layout.findViewById(R.id.noLocationFound);


            leaEmpId = new ArrayList<>();
            preEmpId = new ArrayList<>();
            absEmpId = new ArrayList<>();
            all = new ArrayList<>();


            if(PreferenceHandler.getInstance(getActivity()).getCompanyId()!=0){

                getEmployees();
                getDepartment();

            }else{
                Intent error = new Intent(getActivity(),InternalServerErrorScreen.class);
                startActivity(error);
            }

            mTaskREad.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(todayTasks!=null&&todayTasks.size()!=0){

                        Intent intent = new Intent(getActivity(), AdminNewMainScreen.class);
                        if(PreferenceHandler.getInstance(getActivity()).getUserRoleUniqueID()==9){
                            intent.putExtra("viewpager_position", 3);
                        }else{
                            intent.putExtra("viewpager_position", 2);
                        }

                        startActivity(intent);

                    }


                }
            });

            mLiveRead.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(todayLIve!=null&&todayLIve.size()!=0){

                        Intent live = new Intent(getActivity(), EmployeeListScreen.class);
                        live.putExtra("Type","Live");
                        getContext().startActivity(live);

                    }


                }
            });

         //   showalertbox();

            if(checkValue){
                all.addAll(leaEmpId);
                all.addAll(preEmpId);
                all.addAll(absEmpId);

                all = removeDuplicates(all);

                allEmployees.removeAll(presentEmployees);
                allEmployees.removeAll(leaveEmployees);
                allEmployees = removeDuplicates(allEmployees);
                absentEmployees = allEmployees;

               mEmployeeAbsent.setText(""+(all.size()-1));
            }else{
                h = new Handler();
                //1 second=1000 milisecond, 15*1000=15seconds


                h.postDelayed( runnable = new Runnable() {
                    public void run() {
                        //do something
                        if(checkValue){
                            all.addAll(leaEmpId);
                            all.addAll(preEmpId);
                            all.addAll(absEmpId);

                            allEmployees.removeAll(presentEmployees);
                            allEmployees.removeAll(leaveEmployees);
                            allEmployees = removeDuplicates(allEmployees);
                            absentEmployees = allEmployees;

                            all = removeDuplicates(all);
                            mEmployeeAbsent.setText(""+(employee-all.size()));
                        }else if(allEmployees!=null&&allEmployees.size()!=0){

                            try{

                                all.addAll(leaEmpId);
                                all.addAll(preEmpId);
                                all.addAll(absEmpId);



                                allEmployees.removeAll(presentEmployees);
                                allEmployees.removeAll(leaveEmployees);
                                allEmployees = removeDuplicates(allEmployees);
                                absentEmployees = allEmployees;

                                all = removeDuplicates(all);
                                mEmployeeAbsent.setText(""+(employee-all.size()));

                            }catch (Exception e){
                                e.printStackTrace();
                            }

                        }
                        h.postDelayed(runnable, delay);
                    }
                }, delay);
            }

            mPendingLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(pendingTasks!=null&&pendingTasks.size()!=0){
                        Intent pending = new Intent(getActivity(),PendingTasks.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("PendingTasks",pendingTasks);
                        bundle.putString("Title","Pending Tasks");
                        pending.putExtras(bundle);
                        startActivity(pending);
                    }

                }
            });

            mOnTaskLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(onTask!=null&&onTask.size()!=0){
                        Intent pending = new Intent(getActivity(),PendingTasks.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("PendingTasks",onTask);
                        bundle.putString("Title","On Tasks");
                        pending.putExtras(bundle);
                        startActivity(pending);
                    }

                }
            });

            mDepartmentLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //presentShowcaseView();
                    if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.LOLLIPOP) {
                        presentShowcaseSequence();
                    }



                }
            });

            mEmployeeLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent employee = new Intent(getContext(), EmployeeUpdateListScreen.class);
                    getContext().startActivity(employee);

                }
            });

            mPresenEmploLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(presentEmployees!=null&&presentEmployees.size()!=0){

                        Intent pending = new Intent(getActivity(),PresentEmployeeListScreen.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("Employees",presentEmployees);
                        pending.putExtras(bundle);
                        startActivity(pending);

                    }else {
                        Toast.makeText(getActivity(), "No Employees", Toast.LENGTH_SHORT).show();
                    }


                }
            });

            mAbEmLayy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {




                    if(absentEmployees!=null&&absentEmployees.size()!=0){

                        Intent pending = new Intent(getActivity(),PresentEmployeeListScreen.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("Employees",absentEmployees);
                        pending.putExtras(bundle);
                        startActivity(pending);

                    }else {
                        Toast.makeText(getActivity(), "No Employees", Toast.LENGTH_SHORT).show();
                    }


                }
            });

            mLeaveLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(leaveEmployees!=null&&leaveEmployees.size()!=0){

                        Intent pending = new Intent(getActivity(),PresentEmployeeListScreen.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("Employees",leaveEmployees);
                        pending.putExtras(bundle);
                        startActivity(pending);

                    }else {
                        Toast.makeText(getActivity(), "No Employees", Toast.LENGTH_SHORT).show();
                    }


                }
            });

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

    private void getEmployees(){


        /*final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Loading Employees");
        progressDialog.setCancelable(false);
        progressDialog.show();*/

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

                           /* if (progressDialog!=null)
                                progressDialog.dismiss();*/
                            ArrayList<Employee> list = response.body();

                            employeeTasks = new ArrayList<>();
                            todayTasks = new ArrayList<>();
                            todayLIve = new ArrayList<>();
                            pendingTasks = new ArrayList<>();
                            completedTasks = new ArrayList<>();
                            closedTasks = new ArrayList<>();
                            onTask = new ArrayList<>();
                            presentEmployees = new ArrayList<>();
                            absentEmployees = new ArrayList<>();
                            allEmployees = new ArrayList<>();
                            leaveEmployees = new ArrayList<>();

                            if (list !=null && list.size()!=0) {

                                ArrayList<Employee> employees = new ArrayList<>();
                                dayemployeeMeetings = new ArrayList<>();

                                for (Employee emp:list) {

                                    if(emp.getUserRoleId()!=2){

                                        allEmployees.add(emp);
                                        employees.add(emp);

                                        final Calendar calendar = Calendar.getInstance();
                                        Date date2 = calendar.getTime();
                                        getTasks(emp,new SimpleDateFormat("yyyy-MM-dd").format(date2));
                                        getApprovedLeaveDetails(emp.getEmployeeId(),emp);
                                        getRejectedLeaveDetails(emp.getEmployeeId());

                                        LoginDetails loginDetails = new LoginDetails();
                                        loginDetails.setEmployeeId(emp.getEmployeeId());
                                        loginDetails.setLoginDate(new SimpleDateFormat("MM/dd/yyyy").format(new Date()));
                                        getLoginDetails(loginDetails,emp);

                                        LiveTracking lv = new LiveTracking();
                                        lv.setEmployeeId(emp.getEmployeeId());
                                        lv.setTrackingDate(new SimpleDateFormat("MM/dd/yyyy").format(new Date()));
                                        getLiveLocation(lv);

                                        Meetings md  = new Meetings();
                                        md.setEmployeeId(emp.getEmployeeId());
                                        md.setMeetingDate(new SimpleDateFormat("MM/dd/yyyy").format(new Date()));
                                        String mdDate = new SimpleDateFormat("MMM dd,yyyy").format(new Date());
                                        getMeetingsDetails(md,mdDate);

                                    }

                                }


                                if(employees!=null&&employees.size()!=0){

                                    employee = employees.size();

                                    mEmployeeCount.setText(""+employees.size());
                                    mOnTask.setText(""+onTasks);
                                    mPending.setText(""+pendingTask);


                                }else{


                                }


                                //}

                            }else{

                            }

                        }else {


                            Toast.makeText(getActivity(), "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Employee>> call, Throwable t) {
                        // Log error here since request failed
                      /*  if (progressDialog!=null)
                            progressDialog.dismiss();*/

                        Log.e("TAG", t.toString());
                    }
                });
            }


        });
    }


    private void getDepartment(){


        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Loading Department");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {
                DepartmentApi apiService = Util.getClient().create(DepartmentApi.class);
                Call<ArrayList<Departments>> call = apiService.getDepartmentByOrganization(PreferenceHandler.getInstance(getActivity()).getCompanyId());

                call.enqueue(new Callback<ArrayList<Departments>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Departments>> call, Response<ArrayList<Departments>> response) {
                        int statusCode = response.code();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {


                           if (progressDialog!=null&&progressDialog.isShowing())
                                progressDialog.dismiss();
                            ArrayList<Departments> list = response.body();
                            ArrayList<Departments> deptList = new ArrayList<>();


                            if (list !=null && list.size()!=0) {

                                //}
                                for (Departments dept:list) {

                                    if(!dept.getDepartmentName().equalsIgnoreCase("Founders")){
                                        deptList.add(dept);
                                    }

                                }

                                if(deptList.size()==0){
                                    departmentAlert();
                                }else{
                                        mDeptCount.setText(""+deptList.size());
                                }

                            }else{
                                departmentAlert();
                            }

                        }else {


                            Toast.makeText(getActivity(), "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Departments>> call, Throwable t) {
                        // Log error here since request failed
                        if (progressDialog!=null&&progressDialog.isShowing())
                            progressDialog.dismiss();

                        Log.e("TAG", t.toString());
                    }
                });
            }


        });
    }

    private void departmentAlert(){

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View views = inflater.inflate(R.layout.alert_department_create, null);

        builder.setView(views);
         builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    // Consumed
// Not consumed
                    return keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP;
                }
            });

        final Button mSave = views.findViewById(R.id.save);
        final EditText desc = views.findViewById(R.id.department_description);
        final TextInputEditText mName = views.findViewById(R.id.department_name);


        final android.support.v7.app.AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);

        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String name = mName.getText().toString();
                String descrp = desc.getText().toString();

                if(name.isEmpty()){

                    Toast.makeText(getContext(), "Please enter Department Name", Toast.LENGTH_SHORT).show();

                }else if (descrp.isEmpty()){

                    Toast.makeText(getContext(), "Please enter Department Description", Toast.LENGTH_SHORT).show();
                }else{

                    Departments departments = new Departments();
                    departments.setDepartmentName(name);
                    departments.setDepartmentDescription(descrp);
                    departments.setOrganizationId(PreferenceHandler.getInstance(getContext()).getCompanyId());

                    try {
                        addDepartments(departments,dialog);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


            }
        });

    }


    public void addDepartments(final Departments departments,final AlertDialog dialogs) {


        final ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setMessage("Saving Details..");
        dialog.setCancelable(false);
        dialog.show();

        DepartmentApi apiService = Util.getClient().create(DepartmentApi.class);

        Call<Departments> call = apiService.addDepartments(departments);

        call.enqueue(new Callback<Departments>() {
            @Override
            public void onResponse(Call<Departments> call, Response<Departments> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {
                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201) {

                        Departments s = response.body();

                        if(s!=null){

                            Toast.makeText(getContext(), "Department Creted Successfully ", Toast.LENGTH_SHORT).show();

                            dialogs.dismiss();
                            showalertbox();



                        }




                    }else {
                        Toast.makeText(getActivity(), "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception ex)
                {

                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }
                    ex.printStackTrace();
                }
//                callGetStartEnd();
            }

            @Override
            public void onFailure(Call<Departments> call, Throwable t) {

                if(dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
                Toast.makeText( getActivity( ) , "Failed due to Bad Internet Connection" , Toast.LENGTH_SHORT ).show( );
                Log.e("TAG", t.toString());
            }
        });



    }

    private void getTasks(final Employee employee,final String dateValue){

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
                                             /*   String parse = new SimpleDateFormat("MMM yyyy").format(afromDate);
                                                fromDate = new SimpleDateFormat("MMM yyyy").parse(parse);*/

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
                                              /*  String parse = new SimpleDateFormat("MMM yyyy").format(atoDate);
                                                toDate = new SimpleDateFormat("MMM yyyy").parse(parse);*/

                                            }

                                        }

                                        if(afromDate!=null&&atoDate!=null){



                                            if(date.getTime() >= afromDate.getTime() && date.getTime() <= atoDate.getTime()){

                                                TaskAdminData taskAdminDatas = new TaskAdminData();
                                                taskAdminDatas.setEmployee(employee);
                                                taskAdminDatas.setTasks(task);

                                                todayTasks.add(taskAdminDatas);

                                            }
                                        }

                                        TaskAdminData taskAdminDatas = new TaskAdminData();
                                        taskAdminDatas.setEmployee(employee);
                                        taskAdminDatas.setTasks(task);
                                        employeeTasks.add(taskAdminDatas);
                                        // total = total+1;

                                        if(task.getStatus().equalsIgnoreCase("Completed")){
                                            completedTasks.add(taskAdminDatas);
                                            //complete = complete+1;
                                        }else if(task.getStatus().equalsIgnoreCase("Pending")){
                                            pendingTasks.add(taskAdminDatas);
                                            pendingTask = pendingTask+1;
                                        }else if(task.getStatus().equalsIgnoreCase("Closed")){
                                            closedTasks.add(taskAdminDatas);
                                            // closed = closed+1;
                                        }else if(task.getStatus().equalsIgnoreCase("On-Going")){
                                            onTask.add(taskAdminDatas);
                                            onTasks = onTasks+1;
                                        }

                                    }


                                }

                                if(employeeTasks!=null&&employeeTasks.size()!=0){

                                    if(todayTasks!=null&&todayTasks.size()!=0){
                                        //mTaskLayout.setVisibility(View.VISIBLE);

                                        mNoRecord.setVisibility(View.GONE);
                                        mTaskList.setVisibility(View.VISIBLE);
                                        mTaskList.removeAllViews();

                                        if(todayTasks.size()<=2){
                                            mTaskList.removeAllViews();

                                            mAdapter = new TaskAdminListAdapter(getContext(),todayTasks);
                                            mTaskList.setAdapter(mAdapter);
                                            mTaskREad.setVisibility(View.GONE);
                                        }else if(todayTasks.size()>2){

                                            mTaskList.removeAllViews();
                                            ArrayList<TaskAdminData> twoArray = new ArrayList<>();
                                            twoArray.add(todayTasks.get(0));
                                            twoArray.add(todayTasks.get(1));
                                            mAdapter = new TaskAdminListAdapter(getContext(),twoArray);
                                            mTaskList.setAdapter(mAdapter);
                                            mTaskREad.setVisibility(View.VISIBLE);
                                        }


                                    }



                                    mOnTask.setText(""+onTasks);
                                    mPending.setText(""+pendingTask);


                                }else{

                                }



                            }else{

                               // Toast.makeText(DailyTargetsForEmployeeActivity.this, "No Tasks given for this employee ", Toast.LENGTH_SHORT).show();

                            }

                        }else {



                            //Toast.makeText(DailyTargetsForEmployeeActivity.this, "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Tasks>> call, Throwable t) {
                        // Log error here since request failed
                       /* if (progressDialog!=null)
                            progressDialog.dismiss();*/
                        Log.e("TAG", t.toString());
                    }
                });
            }


        });
    }

    private void getMeetingsDetails(final Meetings loginDetails, final String comDate){

        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {
                MeetingsAPI apiService = Util.getClient().create(MeetingsAPI.class);
                Call<ArrayList<Meetings>> call = apiService.getMeetingsByEmployeeIdAndDate(loginDetails);

                call.enqueue(new Callback<ArrayList<Meetings>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Meetings>> call, Response<ArrayList<Meetings>> response) {
                        int statusCode = response.code();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {



                            ArrayList<Meetings> list = response.body();

                            /*employeeMeetings = new ArrayList<>();
                            pendingMeetings = new ArrayList<>();
                            completedMeetings = new ArrayList<>();
                            closedMeetings = new ArrayList<>();

*/


                           /* daypendingMeetings = new ArrayList<>();
                            daycompletedMeetings = new ArrayList<>();
                            dayclosedMeetings = new ArrayList<>();*/

                            /*daytotalMeetings=0;
                            daypendingMeeting=0;
                            daycompleteMeetings=0;
                            dayclosedMeeting=0;*/


                            if (list !=null && list.size()!=0) {

                                long diffHrs = 0;
                                //mTotalMeetings.setText(""+list.size());

                                for (Meetings lg:list) {

                                    // employeeMeetings.add(lg);
                                    dayemployeeMeetings.add(lg);


                                    if(lg.getStartTime()!=null&&lg.getEndTime()!=null){

                                        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy hh:mm a");
                                        SimpleDateFormat sdfs = new SimpleDateFormat("MMM dd,yyyy");

                                        Date fd=null,td=null;

                                        String logoutT = lg.getEndTime();
                                        String loginT = lg.getStartTime();

                                        if(loginT==null||loginT.isEmpty()){

                                            loginT = comDate +" 00:00 am";
                                        }

                                        if(logoutT==null||logoutT.isEmpty()){

                                            logoutT = comDate  +" "+new SimpleDateFormat("hh:mm a").format(new Date()) ;
                                        }

                                        try {
                                            fd = sdf.parse(""+loginT);
                                            td = sdf.parse(""+logoutT);

                                            long diff = td.getTime() - fd.getTime();
                                            diffHrs = diffHrs+diff;

                                        } catch (ParseException e) {
                                            e.printStackTrace();

                                        }

                                    }

                                    /*if(lg.getStatus()!=null&&lg.getStatus().equalsIgnoreCase("Completed")){

                                       // completedMeetings.add(lg);
                                        daycompletedMeetings.add(lg);
                                    }else if(lg.getStatus()!=null&&lg.getStatus().equalsIgnoreCase("In Meeting")){

                                        pendingMeetings.add(lg);
                                        daypendingMeetings.add(lg);
                                    }else if(lg.getStatus()!=null&&lg.getStatus().equalsIgnoreCase("Closed")){

                                        closedMeetings.add(lg);
                                        dayclosedMeetings.add(lg);
                                    }*/


                                }






                               /* int minutes = (int) ((diffHrs / (1000*60)) % 60);
                                int hours   = (int) ((diffHrs / (1000*60*60)) % 24);
                                meetDiff = diffHrs;



                                DecimalFormat df = new DecimalFormat("00");

                                String s= String.format("%02d", hours) +" hr "+String.format("%02d", minutes)+" mins";
                                SpannableString ss1=  new SpannableString(s);
                                ss1.setSpan(new RelativeSizeSpan(1f), 0,2, 0); // set size
                                ss1.setSpan(new ForegroundColorSpan(Color.RED), 0, 2, 0);// set color
                                ss1.setSpan(new RelativeSizeSpan(1f), 6,8, 0); // set size
                                ss1.setSpan(new ForegroundColorSpan(Color.RED), 6, 8, 0);// set color
                                mMetingAdrz.setText(ss1);
                                mMeetingTime.setText("Total Meeting Time");

                                long avgMeetingdiff = diffHrs/list.size();

                                int avgminutes = (int) ((avgMeetingdiff / (1000*60)) % 60);
                                int avghours   = (int) ((avgMeetingdiff / (1000*60*60)) % 24);

                                String as= String.format("%02d", avghours) +" hr "+String.format("%02d", avgminutes)+" mins";
                                SpannableString ss1a=  new SpannableString(as);
                                ss1a.setSpan(new RelativeSizeSpan(1f), 0,2, 0); // set size
                                ss1a.setSpan(new ForegroundColorSpan(Color.RED), 0, 2, 0);// set color
                                ss1a.setSpan(new RelativeSizeSpan(1f), 6,8, 0); // set size
                                ss1a.setSpan(new ForegroundColorSpan(Color.RED), 6, 8, 0);// set color
                                mAvgMeetingTime.setText(ss1a);*/


                            }System.out.println("Suree"+ dayemployeeMeetings.size());

                            if(dayemployeeMeetings!=null&&dayemployeeMeetings.size()!=0){

                                mNoMeeting.setVisibility(View.GONE);
                                mMeetingList.setVisibility(View.VISIBLE);
                                mMeetingList.removeAllViews();

                                if(dayemployeeMeetings.size()>2){
                                    ArrayList<Meetings> twoArray = new ArrayList<>();
                                    twoArray.add(dayemployeeMeetings.get(0));
                                    twoArray.add(dayemployeeMeetings.get(1));
                                    MeetingDetailAdapter adapter = new MeetingDetailAdapter(getActivity(),twoArray);
                                    mMeetingList.setAdapter(adapter);
                                    mMeetingRead.setVisibility(View.VISIBLE);
                                }else{
                                    mMeetingRead.setVisibility(View.GONE);
                                    MeetingDetailAdapter adapter = new MeetingDetailAdapter(getActivity(),dayemployeeMeetings);
                                    mMeetingList.setAdapter(adapter);
                                }


                                    /*totalTargets.setText(""+daytotal);
                                    openTargets.setText(""+daypending);
                                    closedTargets.setText(""+daycomplete);
                                    movedTargets.setText(""+dayclosed);*/
                            }else{

                                mNoMeeting.setVisibility(View.VISIBLE);
                                mMeetingList.setVisibility(View.GONE);
                                mMeetingRead.setVisibility(View.GONE);
                            }

                        }else {
                            System.out.println("Suree"+ dayemployeeMeetings.size());

                            if(dayemployeeMeetings!=null&&dayemployeeMeetings.size()!=0){

                                mNoMeeting.setVisibility(View.GONE);
                                mMeetingList.setVisibility(View.VISIBLE);
                                mMeetingList.removeAllViews();
                                System.out.println("Suree"+ dayemployeeMeetings.size());

                                if(dayemployeeMeetings.size()>2){
                                    ArrayList<Meetings> twoArray = new ArrayList<>();
                                    twoArray.add(dayemployeeMeetings.get(0));
                                    twoArray.add(dayemployeeMeetings.get(1));
                                    MeetingDetailAdapter adapter = new MeetingDetailAdapter(getActivity(),twoArray);
                                    mMeetingList.setAdapter(adapter);
                                    mMeetingRead.setVisibility(View.VISIBLE);
                                }else{
                                    mMeetingRead.setVisibility(View.GONE);
                                    MeetingDetailAdapter adapter = new MeetingDetailAdapter(getActivity(),dayemployeeMeetings);
                                    mMeetingList.setAdapter(adapter);
                                }


                                    /*totalTargets.setText(""+daytotal);
                                    openTargets.setText(""+daypending);
                                    closedTargets.setText(""+daycomplete);
                                    movedTargets.setText(""+dayclosed);*/
                            }else{

                                mNoMeeting.setVisibility(View.VISIBLE);
                                mMeetingList.setVisibility(View.GONE);
                                mMeetingRead.setVisibility(View.GONE);
                            }

                            /*String as= String.format("%02d", 00) +" hr "+String.format("%02d", 00)+" mins";
                            SpannableString ss1a=  new SpannableString(as);
                            ss1a.setSpan(new RelativeSizeSpan(1f), 0,2, 0); // set size
                            ss1a.setSpan(new ForegroundColorSpan(Color.RED), 0, 2, 0);// set color
                            ss1a.setSpan(new RelativeSizeSpan(1f), 6,8, 0); // set size
                            ss1a.setSpan(new ForegroundColorSpan(Color.RED), 6, 8, 0);// set color
                            mAvgMeetingTime.setText(ss1a);*/
                            //Toast.makeText(DailyTargetsForEmployeeActivity.this, "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Meetings>> call, Throwable t) {
                        // Log error here since request failed
                       /* if (progressDialog!=null)
                            progressDialog.dismiss();*/
                        System.out.println("Suree"+ dayemployeeMeetings.size());
                        Log.e("TAG", t.toString());
                        if(dayemployeeMeetings!=null&&dayemployeeMeetings.size()!=0){

                            mNoMeeting.setVisibility(View.GONE);
                            mMeetingList.setVisibility(View.VISIBLE);
                            mMeetingList.removeAllViews();

                            if(dayemployeeMeetings.size()>2){
                                ArrayList<Meetings> twoArray = new ArrayList<>();
                                twoArray.add(dayemployeeMeetings.get(0));
                                twoArray.add(dayemployeeMeetings.get(1));
                                MeetingDetailAdapter adapter = new MeetingDetailAdapter(getActivity(),twoArray);
                                mMeetingList.setAdapter(adapter);
                                mMeetingRead.setVisibility(View.VISIBLE);
                            }else{
                                mMeetingRead.setVisibility(View.GONE);
                                MeetingDetailAdapter adapter = new MeetingDetailAdapter(getActivity(),dayemployeeMeetings);
                                mMeetingList.setAdapter(adapter);
                            }


                                    /*totalTargets.setText(""+daytotal);
                                    openTargets.setText(""+daypending);
                                    closedTargets.setText(""+daycomplete);
                                    movedTargets.setText(""+dayclosed);*/
                        }else{

                            mNoMeeting.setVisibility(View.VISIBLE);
                            mMeetingList.setVisibility(View.GONE);
                            mMeetingRead.setVisibility(View.GONE);
                        }
                    }
                });
            }


        });
    }


    private void getLiveLocation(final LiveTracking lv){
        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {
                LiveTrackingAPI apiService = Util.getClient().create(LiveTrackingAPI.class);
                Call<ArrayList<LiveTracking>> call = apiService.getLiveTrackingByEmployeeIdAndDate(lv);

                call.enqueue(new Callback<ArrayList<LiveTracking>>() {
                    @Override
                    public void onResponse(Call<ArrayList<LiveTracking>> call, Response<ArrayList<LiveTracking>> response) {
                        int statusCode = response.code();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {



                            ArrayList<LiveTracking> list = response.body();


                            if (list !=null && list.size()!=0) {

                                todayLIve.add(list.get(list.size()-1));

                                    if(todayLIve!=null&&todayLIve.size()!=0){
                                        //mTaskLayout.setVisibility(View.VISIBLE);

                                        mLiveLay.setVisibility(View.GONE);
                                        mLiveList.setVisibility(View.VISIBLE);
                                        mLiveList.removeAllViews();

                                        if(todayLIve.size()<=2){
                                            mLiveList.removeAllViews();

                                            mLiveAdapter = new LiveTrackingAdapter(getContext(),todayLIve);
                                            mLiveList.setAdapter(mLiveAdapter);
                                            mLiveRead.setVisibility(View.GONE);

                                        }else if(todayLIve.size()>2){

                                            mLiveList.removeAllViews();
                                            ArrayList<LiveTracking> twoArray = new ArrayList<>();
                                            twoArray.add(todayLIve.get(0));
                                            twoArray.add(todayLIve.get(1));
                                            mLiveAdapter = new LiveTrackingAdapter(getContext(),twoArray);
                                            mLiveList.setAdapter(mLiveAdapter);
                                            mLiveRead.setVisibility(View.VISIBLE);
                                        }


                                    }


                            }else{

                               // Toast.makeText(DailyTargetsForEmployeeActivity.this, "No Tasks given for this employee ", Toast.LENGTH_SHORT).show();

                            }

                        }else {



                            //Toast.makeText(DailyTargetsForEmployeeActivity.this, "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<LiveTracking>> call, Throwable t) {
                        // Log error here since request failed
                       /* if (progressDialog!=null)
                            progressDialog.dismiss();*/
                        Log.e("TAG", t.toString());
                    }
                });
            }


        });
    }

    private void getLoginDetails(final LoginDetails loginDetails,final Employee employee){


        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {
                LoginDetailsAPI apiService = Util.getClient().create(LoginDetailsAPI.class);
                Call<ArrayList<LoginDetails>> call = apiService.getLoginByEmployeeIdAndDate(loginDetails);

                call.enqueue(new Callback<ArrayList<LoginDetails>>() {
                    @Override
                    public void onResponse(Call<ArrayList<LoginDetails>> call, Response<ArrayList<LoginDetails>> response) {
                        int statusCode = response.code();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {



                            ArrayList<LoginDetails> list = response.body();

                            if (list !=null && list.size()!=0) {




                                if(list.get(list.size()-1).getLogOutTime()==null||list.get(list.size()-1).getLogOutTime().isEmpty()){

                                    preEmpId.add(loginDetails.getEmployeeId());
                                    presentEmployee = presentEmployee+1;
                                    mEmployeePresent.setText(""+presentEmployee);
                                    presentEmployees.add(employee);
                                    checkValue = true;
                                }




                            }else{

                                // Toast.makeText(DailyTargetsForEmployeeActivity.this, "No Tasks given for this employee ", Toast.LENGTH_SHORT).show();

                            }

                        }else {



                            //Toast.makeText(DailyTargetsForEmployeeActivity.this, "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<LoginDetails>> call, Throwable t) {
                        // Log error here since request failed
                       /* if (progressDialog!=null)
                            progressDialog.dismiss();*/
                        Log.e("TAG", t.toString());
                    }
                });
            }


        });
    }

    private void getApprovedLeaveDetails(final int employeeId,final Employee employee){

        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {
                LeaveAPI apiService = Util.getClient().create(LeaveAPI.class);
                Call<ArrayList<Leaves>> call = apiService.getLeavesByStatusAndEmployeeId("Approved",employeeId);

                call.enqueue(new Callback<ArrayList<Leaves>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Leaves>> call, Response<ArrayList<Leaves>> response) {
                        int statusCode = response.code();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {


                            int daysInMonth = 0;



                            try{

                                ArrayList<Leaves> list = response.body();
                                ArrayList<Leaves> approvedLeave = new ArrayList<>();


                                Date date = new Date();
                                Date adate = new Date();
                                Date edate = new Date();



                                    try {
                                        date = new SimpleDateFormat("yyyy-MM-dd").parse(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

                                        System.out.println("Day countr "+daysInMonth);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }






                                if (list !=null && list.size()!=0) {


                                    for (Leaves leaves:list) {

                                        String froms = leaves.getFromDate();
                                        String tos = leaves.getToDate();
                                        Date fromDate = null;
                                        Date toDate = null;
                                        Date afromDate = null;
                                        Date atoDate = null;

                                        if(froms!=null&&!froms.isEmpty()){

                                            if(froms.contains("T")){

                                                String dojs[] = froms.split("T");

                                                afromDate = new SimpleDateFormat("yyyy-MM-dd").parse(dojs[0]);
                                             /*   String parse = new SimpleDateFormat("MMM yyyy").format(afromDate);
                                                fromDate = new SimpleDateFormat("MMM yyyy").parse(parse);*/

                                            }

                                        }

                                        if(tos!=null&&!tos.isEmpty()){

                                            if(tos.contains("T")){

                                                String dojs[] = tos.split("T");

                                                atoDate = new SimpleDateFormat("yyyy-MM-dd").parse(dojs[0]);
                                              /*  String parse = new SimpleDateFormat("MMM yyyy").format(atoDate);
                                                toDate = new SimpleDateFormat("MMM yyyy").parse(parse);*/

                                            }

                                        }

                                        if(afromDate!=null&&atoDate!=null){



                                            if(date.getTime() >= afromDate.getTime() && date.getTime() <= atoDate.getTime()){

                                                approvedLeave.add(leaves);
                                                leaveEmployees.add(employee);
                                                leaEmpId.add(employeeId);
                                                checkValue = true;


                                            }
                                        }


                                        if(approvedLeave.size()!=0){
                                            leaveEmployee = leaveEmployee+1;
                                            mLeaveEmployee.setText(""+leaveEmployee);
                                        }



                                    }


                                }else{



                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }


                        }else {


                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Leaves>> call, Throwable t) {
                        // Log error here since request failed

                        Log.e("TAG", t.toString());
                    }
                });
            }


        });
    }

    private void getRejectedLeaveDetails(final int employeeId){




        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {
                LeaveAPI apiService = Util.getClient().create(LeaveAPI.class);
                Call<ArrayList<Leaves>> call = apiService.getLeavesByStatusAndEmployeeId("Rejected",employeeId);

                call.enqueue(new Callback<ArrayList<Leaves>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Leaves>> call, Response<ArrayList<Leaves>> response) {
                        int statusCode = response.code();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {


                            int daysInMonth = 0;



                            try{

                                ArrayList<Leaves> list = response.body();
                                ArrayList<Leaves> rejectedLeave = new ArrayList<>();


                                Date date = new Date();
                                Date adate = new Date();
                                Date edate = new Date();



                                try {
                                    date = new SimpleDateFormat("yyyy-MM-dd").parse(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

                                    System.out.println("Day countr "+daysInMonth);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }






                                if (list !=null && list.size()!=0) {


                                    for (Leaves leaves:list) {

                                        String froms = leaves.getFromDate();
                                        String tos = leaves.getToDate();
                                        Date fromDate = null;
                                        Date toDate = null;
                                        Date afromDate = null;
                                        Date atoDate = null;

                                        if(froms!=null&&!froms.isEmpty()){

                                            if(froms.contains("T")){

                                                String dojs[] = froms.split("T");

                                                afromDate = new SimpleDateFormat("yyyy-MM-dd").parse(dojs[0]);
                                             /*   String parse = new SimpleDateFormat("MMM yyyy").format(afromDate);
                                                fromDate = new SimpleDateFormat("MMM yyyy").parse(parse);*/

                                            }

                                        }

                                        if(tos!=null&&!tos.isEmpty()){

                                            if(tos.contains("T")){

                                                String dojs[] = tos.split("T");

                                                atoDate = new SimpleDateFormat("yyyy-MM-dd").parse(dojs[0]);
                                              /*  String parse = new SimpleDateFormat("MMM yyyy").format(atoDate);
                                                toDate = new SimpleDateFormat("MMM yyyy").parse(parse);*/

                                            }

                                        }

                                        if(afromDate!=null&&atoDate!=null){



                                            if(date.getTime() >= afromDate.getTime() && date.getTime() <= atoDate.getTime()){

                                                rejectedLeave.add(leaves);
                                                absEmpId.add(employeeId);
                                                checkValue = true;

                                            }
                                        }


                                        if(rejectedLeave.size()!=0){
                                            absentEmployee = absentEmployee+1;
                                            //mEmployeeAbsent.setText(""+absentEmployee);
                                        }



                                    }


                                }else{



                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }


                        }else {


                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Leaves>> call, Throwable t) {
                        // Log error here since request failed

                        Log.e("TAG", t.toString());
                    }
                });
            }


        });
    }

    public static <T> ArrayList<T> removeDuplicates(ArrayList<T> list)
    {

        // Create a new ArrayList
        ArrayList<T> newList = new ArrayList<T>();

        // Traverse through the first list
        for (T element : list) {

            // If this element is not present in newList
            // then add it
            if (!newList.contains(element)) {

                newList.add(element);
            }
        }

        // return the new list
        return newList;
    }

    private void showalertbox() {



        final android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(getActivity());
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.depart_add_alert_layout,null);
        TextView dontCancelBtn = view.findViewById(R.id.no_btn);
        TextView cancelBtn = view.findViewById(R.id.exit_app_btn);
        TextView ask = view.findViewById(R.id.ask);

        dialogBuilder.setView(view);
        final android.app.AlertDialog dialog = dialogBuilder.create();
        dialog.setCancelable(true);
        dialog.show();
        dontCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    if(dialog != null)
                    {
                        dialog.dismiss();
                    }

                    Intent employeeList = new Intent(getContext(), DepartmentLilstScreen.class);
                    getActivity().startActivity(employeeList);


                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try
                {
                    if(dialog != null)
                    {
                        dialog.dismiss();
                    }

                    Intent employee =new Intent(getActivity(),CreateEmployeeScreen.class);
                    startActivity(employee);


                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                int updatedPositions = data.getIntExtra("Position",UpdateTaskScreen.ADAPTER_POSITION);

                if(updatedPositions!=-1){

                    mAdapter.notifyItemChanged(updatedPositions);
                }

            }
        }
    }

    void presentShowcaseView() {

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(getActivity(), SHOWCASE_ID_ADMIN);


        ShowcaseTooltip toolTip1 = ShowcaseTooltip.build(getActivity())
                .corner(30)
                .textColor(Color.parseColor("#007686"))
                .text("Click here to view department list of your organizations<br><br>Tap anywhere to continue");


        sequence.addSequenceItem(

                new MaterialShowcaseView.Builder(getActivity())
                        .setTarget(mDepartmentLay)
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
                .text("Click here to view entire employee list of your organizations and you can add/edit employee also<br><br>Tap anywhere to continue");

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(getActivity())
                        .setTarget(mEmployeeLay)
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
                .text("Click here to view all On-Going Task from your employees<br><br>Tap anywhere to continue");

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(getActivity())
                        .setTarget(mOnTaskLay)
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
                .text("Click here to view all Pending Task from your employees<br><br>Tap anywhere to continue");

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(getActivity())
                        .setTarget(mPendingLayout)
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
                .text("Click here to view all present employees now on your organization<br><br>Tap anywhere to continue");

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(getActivity())
                        .setTarget(mPresenEmploLay)
                        .setToolTip(toolTip5)
                        .setTooltipMargin(20)
                        .setShapePadding(30)
                        .setDismissOnTouch(true)
                        .setMaskColour(getResources().getColor(R.color.tooltip_mask))
                        .build()
        );

        ShowcaseTooltip toolTip6 = ShowcaseTooltip.build(getActivity())
                .corner(30)
                .textColor(Color.parseColor("#007686"))
                .text("Click here to view all absent employees today<br><br>Tap anywhere to continue");

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(getActivity())
                        .setTarget(mAbEmLayy)
                        .setToolTip(toolTip6)
                        .setTooltipMargin(20)
                        .setShapePadding(30)
                        .setDismissOnTouch(true)
                        .setMaskColour(getResources().getColor(R.color.tooltip_mask))
                        .build()
        );


        ShowcaseTooltip toolTip7 = ShowcaseTooltip.build(getActivity())
                .corner(30)
                .textColor(Color.parseColor("#007686"))
                .text("Click here to view list of employees who has taken leave today.<br><br>Tap anywhere to continue");

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(getActivity())
                        .setTarget(mLeaveLay)
                        .setToolTip(toolTip7)
                        .setTooltipMargin(20)
                        .setShapePadding(30)
                        .setDismissOnTouch(true)
                        .setMaskColour(getResources().getColor(R.color.tooltip_mask))
                        .build()
        );




        sequence.start();

        if(sequence.hasFired()){

            Intent organization = new Intent(getActivity(), DepartmentLilstScreen.class);
            getContext().startActivity(organization);

        }




    }

    private void presentShowcaseSequence() {

        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500); // half second between each showcase view

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(getActivity(), SHOWCASE_ID_ADMIN);

        sequence.setOnItemShownListener(new MaterialShowcaseSequence.OnSequenceItemShownListener() {
            @Override
            public void onShow(MaterialShowcaseView itemView, int position) {

            }
        });

        sequence.setConfig(config);

        /*sequence.addSequenceItem(

                new MaterialShowcaseView.Builder(getActivity())
                        .setTarget(mDepartmentLay)
                        .setSkipText("SKIP")
                        .withRectangleShape()
                        .setTooltipMargin(30)
                        .setShapePadding(50)
                        .setDismissOnTouch(true)
                        .setMaskColour(getResources().getColor(R.color.tooltip_mask))
                        .build()
        );*/


                sequence.addSequenceItem(mDepartmentLay, "Click here to view department list of your organizations", "GOT IT");

        ShowcaseTooltip toolTip2 = ShowcaseTooltip.build(getActivity())
                .corner(30)
                .textColor(Color.parseColor("#007686"))
                .text("Click here to view entire employee list of your organizations and you can add/edit employee also<br><br>Tap anywhere to continue");



        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(getActivity())
                        .setTarget(mEmployeeLay)
                        .setToolTip(toolTip2)
                        .setSkipText("SKIP")
                        .setDismissText("GOT IT")
                        .withRectangleShape(true)
                        .build()
        );

        ShowcaseTooltip toolTip3 = ShowcaseTooltip.build(getActivity())
                .corner(30)
                .textColor(Color.parseColor("#007686"))
                .text("Click here to view all On-Going Task from your employees<br><br>Tap anywhere to continue");

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(getActivity())
                        .setTarget(mOnTaskLay)
                        .setToolTip(toolTip3)
                        .setSkipText("SKIP")
                        .setDismissText("GOT IT")
                        .withRectangleShape(true)
                        .build()
        );

        ShowcaseTooltip toolTip4 = ShowcaseTooltip.build(getActivity())
                .corner(30)
                .textColor(Color.parseColor("#007686"))
                .text("Click here to view all Pending Task from your employees<br><br>Tap anywhere to continue");

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(getActivity())
                        .setTarget(mPendingLayout)
                        .setToolTip(toolTip4)
                        .setSkipText("SKIP")
                        .setDismissText("GOT IT")
                        .withRectangleShape(true)
                        .build()
        );

        ShowcaseTooltip toolTip5 = ShowcaseTooltip.build(getActivity())
                .corner(30)
                .textColor(Color.parseColor("#007686"))
                .text("Click here to view all present employees now on your organization<br><br>Tap anywhere to continue");

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(getActivity())
                        .setTarget(mPresenEmploLay)
                        .setToolTip(toolTip5)
                        .setSkipText("SKIP")
                        .setDismissText("GOT IT")
                        .withRectangleShape(true)
                        .build()
        );

        ShowcaseTooltip toolTip6 = ShowcaseTooltip.build(getActivity())
                .corner(30)
                .textColor(Color.parseColor("#007686"))
                .text("Click here to view all absent employees today<br><br>Tap anywhere to continue");

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(getActivity())
                        .setTarget(mAbEmLayy)
                        .setToolTip(toolTip6)
                        .setSkipText("SKIP")
                        .setDismissText("GOT IT")
                        .withRectangleShape(true)
                        .build()
        );


        ShowcaseTooltip toolTip7 = ShowcaseTooltip.build(getActivity())
                .corner(30)
                .textColor(Color.parseColor("#007686"))
                .text("Click here to view list of employees who has taken leave today.<br><br>Tap anywhere to continue");

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(getActivity())
                        .setTarget(mLeaveLay)
                        .setToolTip(toolTip7)
                        .setSkipText("SKIP")
                        .setDismissText("GOT IT")
                        .withRectangleShape(true)
                        .build()
        );



       /* sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setSkipText("SKIP")
                        .setTarget(mProfileImage)
                        .setDismissText("GOT IT")
                        .setContentText("Click here to Upload profile picture")
                        .withRectangleShape()
                        .build()
        );
*/



        sequence.start();



    }

}
