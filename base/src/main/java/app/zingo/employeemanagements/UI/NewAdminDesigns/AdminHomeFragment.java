package app.zingo.employeemanagements.UI.NewAdminDesigns;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.pdf.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import app.zingo.employeemanagements.UI.Common.ReportBulkdataScreen;
import app.zingo.employeemanagements.UI.FAQ.FAQFragment;
import app.zingo.employeemanagements.UI.Plan.PlanDesignActivity;
import app.zingo.employeemanagements.base.BuildConfig;
import app.zingo.employeemanagements.Model.Employee;
import app.zingo.employeemanagements.base.R;
import app.zingo.employeemanagements.Service.LocationForegroundService;
import app.zingo.employeemanagements.UI.Admin.DashBoardAdmin;
import app.zingo.employeemanagements.UI.Common.AllEmployeeLiveLocation;
import app.zingo.employeemanagements.UI.Common.ChangePasswordScreen;
import app.zingo.employeemanagements.UI.Common.CustomerCreation;
import app.zingo.employeemanagements.UI.Common.CustomerList;
import app.zingo.employeemanagements.UI.Common.NotificationShowActivity;
import app.zingo.employeemanagements.UI.Common.ReportManagementScreen;
import app.zingo.employeemanagements.UI.Company.OrganizationDetailScree;
import app.zingo.employeemanagements.UI.Company.WorkingDaysScreen;
import app.zingo.employeemanagements.UI.Employee.EmployeeListScreen;
import app.zingo.employeemanagements.UI.LandingScreen;
import app.zingo.employeemanagements.UI.NewEmployeeDesign.ExpenseManageHost;
import app.zingo.employeemanagements.UI.PlanMainHostScreen;
import app.zingo.employeemanagements.Utils.PreferenceHandler;
import app.zingo.employeemanagements.Utils.ThreadExecuter;
import app.zingo.employeemanagements.Utils.Util;
import app.zingo.employeemanagements.WebApi.EmployeeApi;
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
public class AdminHomeFragment extends Fragment {

    final String TAG = "Employer Dashboard";
    View layout;
    LinearLayout attendance,leaveApplications,employees;
    TextView qr_OrgId;
    LinearLayout departments,liveTracking,tasks,expenses,team,client;
    LinearLayout salary,logout,deptOrg,chngPwd,plans,reports,holiday,settings,faq;

    Employee employee;

    private   String SHOWCASE_ID_ADMIN ;


    public AdminHomeFragment() {
        // Required empty public constructor
    }

    public static AdminHomeFragment getInstance() {
        return new AdminHomeFragment();
    }

    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
      // GAUtil.trackScreen(getActivity(), "Employer Dashboard");
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreateView(layoutInflater, viewGroup, savedInstanceState);

        try{
            this.layout = layoutInflater.inflate(R.layout.fragment_admin_home, viewGroup, false);
            getEmployees();
            setupListeners();
            viewGroup = this.layout.findViewById(R.id.renewWarning);
            viewGroup.setVisibility(View.GONE);

            qr_OrgId = this.layout.findViewById(R.id.organization_id);

            SHOWCASE_ID_ADMIN = "ToolsAdminshome"+PreferenceHandler.getInstance(getActivity()).getUserId();

            if(PreferenceHandler.getInstance(getActivity()).getCompanyName().length()>=4){
                String upToNCharacters = PreferenceHandler.getInstance(getActivity()).getCompanyName().substring(0, Math.min(PreferenceHandler.getInstance(getActivity()).getCompanyName().length(), 4));
                qr_OrgId.setText("Organization Code: "+upToNCharacters+""+PreferenceHandler.getInstance(getActivity()).getCompanyId());
            }else{
                qr_OrgId.setText("Organization Code: "+PreferenceHandler.getInstance(getActivity()).getCompanyName()+""+PreferenceHandler.getInstance(getActivity()).getCompanyId());
            }



            /*if (layoutInflater != null) {

                *//*long daysDiff = DateUtil.daysDiff(new Date(), new Date(layoutInflater.getSubscriptionEndDate()));
                if (!StringUtils.equals(layoutInflater.getSubscriptionStatus(), "Active") || StringUtils.equalsIgnoreCase(AppConstants.PAY_MODE_PG, layoutInflater.getSubscriptionMode()) != null) {
                    viewGroup.setVisibility(8);
                } else if (daysDiff < 0 || daysDiff >= 4) {
                    viewGroup.setVisibility(8);
                } else {
                    viewGroup.setVisibility(0);
                }*//*
            } else {
                viewGroup.setVisibility(8);
            }*/
            return this.layout;
        }catch (Exception e){
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

    public void setupListeners() {
        attendance = this.layout.findViewById(R.id.attendance);
        leaveApplications = this.layout.findViewById(R.id.leaveApplications);
        employees = this.layout.findViewById(R.id.employees);
        holiday = this.layout.findViewById(R.id.holiday);
        settings = this.layout.findViewById(R.id.settings);
        faq = this.layout.findViewById(R.id.faq_home);

        departments = this.layout.findViewById(R.id.department);
        liveTracking = this.layout.findViewById(R.id.live_tracking);
        tasks = this.layout.findViewById(R.id.task_layout);
        client = this.layout.findViewById(R.id.customer_creation);

        expenses = this.layout.findViewById(R.id.expenses_mgmt);
        team = this.layout.findViewById(R.id.team);
        plans = this.layout.findViewById(R.id.plan_detail);


        salary = this.layout.findViewById(R.id.salary);
        deptOrg = this.layout.findViewById(R.id.department_org);
        logout = this.layout.findViewById(R.id.logout);
        chngPwd = this.layout.findViewById(R.id.change_password);
        reports = this.layout.findViewById(R.id.report_mgmt);

        if(PreferenceHandler.getInstance(getActivity()).getResellerUserId()!=0){
            plans.setVisibility(View.GONE);
        }

        //App new version available
        View updatedText = this.layout.findViewById(R.id.updateText);
/*        if (this.mAppUser == null || this.mAppUser.getAppVersion() == 0 || AMApp.version <= this.mAppUser.getAppVersion()) {
            updatedText.setVisibility(8);
            return;
        }
        updatedText.setVisibility(0);
        findViewById.setOnClickListener(this);*/

        attendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openMenuViews(attendance);
            }
        });

        faq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openMenuViews(faq);
            }
        });

        leaveApplications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMenuViews(leaveApplications);
            }
        });

        employees.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMenuViews(employees);
            }
        });

        expenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMenuViews(expenses);
            }
        });

        departments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openMenuViews(departments);
            }
        });

        liveTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMenuViews(liveTracking);
            }
        });
        team.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMenuViews(team);
            }
        });

        tasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMenuViews(tasks);
            }
        });
        deptOrg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMenuViews(deptOrg);
            }
        });
        salary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMenuViews(salary);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMenuViews(logout);
            }
        });
        chngPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMenuViews(chngPwd);
            }
        });
        plans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMenuViews(plans);
            }
        });
        reports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMenuViews(reports);
            }
        });
        holiday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMenuViews(holiday);
            }
        });
        client.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMenuViews(client);
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMenuViews(settings);
            }
        });

    }

    public void openMenuViews(View view) {

        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500); // half second between each showcase view

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(getActivity(), SHOWCASE_ID_ADMIN);

        sequence.setOnItemShownListener(new MaterialShowcaseSequence.OnSequenceItemShownListener() {
            @Override
            public void onShow(MaterialShowcaseView itemView, int position) {

            }
        });

        sequence.setConfig(config);

       /* .setSkipText("SKIP")
                .setDismissText("GOT IT")
                .withRectangleShape(true)
                .build()*/

        ShowcaseTooltip toolTip1 = ShowcaseTooltip.build(getActivity())
                .corner(30)
                .textColor(Color.parseColor("#007686"))
                .text("Click here to view attendance report of your employees<br><br>Tap anywhere to continue");


        sequence.addSequenceItem(attendance, "Click here to view attendance report of your employees", "GOT IT");





        ShowcaseTooltip toolTip2 = ShowcaseTooltip.build(getActivity())
                .corner(30)
                .textColor(Color.parseColor("#007686"))
                .text("Click here to view last updated location of your employees and entire route map of your employees also<br><br>Tap anywhere to continue");

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(getActivity())
                        .setTarget(liveTracking)
                        .setToolTip(toolTip2)
                        .setSkipText("SKIP")
                        .setDismissText("GOT IT")
                        .withRectangleShape(true)
                        .build()
        );

        ShowcaseTooltip toolTip3 = ShowcaseTooltip.build(getActivity())
                .corner(30)
                .textColor(Color.parseColor("#007686"))
                .text("Click here to view all task of your employees based on date and status.<br><br>Tap anywhere to continue");

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(getActivity())
                        .setTarget(tasks)
                        .setToolTip(toolTip3)
                        .setSkipText("SKIP")
                        .setDismissText("GOT IT")
                        .withRectangleShape(true)
                        .build()
        );

        ShowcaseTooltip toolTip4 = ShowcaseTooltip.build(getActivity())
                .corner(30)
                .textColor(Color.parseColor("#007686"))
                .text("Click here to view all expenses of your employees based on monthly report<br><br>Tap anywhere to continue");

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(getActivity())
                        .setTarget(expenses)
                        .setToolTip(toolTip4)
                        .setSkipText("SKIP")
                        .setDismissText("GOT IT")
                        .withRectangleShape(true)
                        .build()
        );

        ShowcaseTooltip toolTip5 = ShowcaseTooltip.build(getActivity())
                .corner(30)
                .textColor(Color.parseColor("#007686"))
                .text("Click here to view employees leave dashboard based on monthly dashboard and you can approve/reject also.<br><br>Tap anywhere to continue");

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(getActivity())
                        .setTarget(leaveApplications)
                        .setToolTip(toolTip5)
                        .setSkipText("SKIP")
                        .setDismissText("GOT IT")
                        .withRectangleShape(true)
                        .build()
        );

        ShowcaseTooltip toolTip6 = ShowcaseTooltip.build(getActivity())
                .corner(30)
                .textColor(Color.parseColor("#007686"))
                .text("Click here to generate salary slip for your employees and send it also.<br><br>Tap anywhere to continue");

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(getActivity())
                        .setTarget(salary)
                        .setToolTip(toolTip6)
                        .setSkipText("SKIP")
                        .setDismissText("GOT IT")
                        .withRectangleShape(true)
                        .build()
        );


        ShowcaseTooltip toolTip7 = ShowcaseTooltip.build(getActivity())
                .corner(30)
                .textColor(Color.parseColor("#007686"))
                .text("Click here to view the team members of you.<br><br>Tap anywhere to continue");

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(getActivity())
                        .setTarget(team)
                        .setToolTip(toolTip7)
                        .setSkipText("SKIP")
                        .setDismissText("GOT IT")
                        .withRectangleShape(true)
                        .build()
        );

        ShowcaseTooltip toolTip8 = ShowcaseTooltip.build(getActivity())
                .corner(30)
                .textColor(Color.parseColor("#007686"))
                .text("Click here to view report of your employees based on date.<br><br>Tap anywhere to continue");

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(getActivity())
                        .setTarget(reports)
                        .setToolTip(toolTip8)
                        .setSkipText("SKIP")
                        .setDismissText("GOT IT")
                        .withRectangleShape(true)
                        .build()
        );

        ShowcaseTooltip toolTip9 = ShowcaseTooltip.build(getActivity())
                .corner(30)
                .textColor(Color.parseColor("#007686"))
                .text("Click here to view department and create department.You can also view employees based on departments<br><br>Tap anywhere to continue");

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(getActivity())
                        .setTarget(departments)
                        .setToolTip(toolTip9)
                        .setSkipText("SKIP")
                        .setDismissText("GOT IT")
                        .withRectangleShape(true)
                        .build()
        );

        ShowcaseTooltip toolTip10 = ShowcaseTooltip.build(getActivity())
                .corner(30)
                .textColor(Color.parseColor("#007686"))
                .text("Click here to view the employee list and add/edit employees<br><br>Tap anywhere to continue");

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(getActivity())
                        .setTarget(employees)
                        .setToolTip(toolTip10)
                        .setSkipText("SKIP")
                        .setDismissText("GOT IT")
                        .withRectangleShape(true)
                        .build()
        );


        ShowcaseTooltip toolTip11 = ShowcaseTooltip.build(getActivity())
                .corner(30)
                .textColor(Color.parseColor("#007686"))
                .text("Click here to create and view customer<br><br>Tap anywhere to continue");

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(getActivity())
                        .setTarget(client)
                        .setToolTip(toolTip11)
                        .setSkipText("SKIP")
                        .setDismissText("GOT IT")
                        .withRectangleShape(true)
                        .build()
        );


        ShowcaseTooltip toolTip12 = ShowcaseTooltip.build(getActivity())
                .corner(30)
                .textColor(Color.parseColor("#007686"))
                .text("Click here to change your password.<br><br>Tap anywhere to continue");

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(getActivity())
                        .setTarget(chngPwd)
                        .setToolTip(toolTip12)
                        .setSkipText("SKIP")
                        .setDismissText("GOT IT")
                        .withRectangleShape(true)
                        .build()
        );

        ShowcaseTooltip toolTip13 = ShowcaseTooltip.build(getActivity())
                .corner(30)
                .textColor(Color.parseColor("#007686"))
                .text("Click here to view/update/create your organizations,holidays,office timings.<br><br>Tap anywhere to continue");

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(getActivity())
                        .setTarget(departments)
                        .setToolTip(toolTip13)
                        .setSkipText("SKIP")
                        .setDismissText("GOT IT")
                        .withRectangleShape(true)
                        .build()
        );


        ShowcaseTooltip toolTip14 = ShowcaseTooltip.build(getActivity())
                .corner(30)
                .textColor(Color.parseColor("#007686"))
                .text("Click here to view plan features and subscribe plan<br><br>Tap anywhere to continue");

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(getActivity())
                        .setTarget(plans)
                        .setToolTip(toolTip14)
                        .setSkipText("SKIP")
                        .setDismissText("GOT IT")
                        .withRectangleShape(true)
                        .build()
        );


        ShowcaseTooltip toolTip15 = ShowcaseTooltip.build(getActivity())
                .corner(30)
                .textColor(Color.parseColor("#007686"))
                .text("Click here to do logout.<br><br>Tap anywhere to continue");

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(getActivity())
                        .setTarget(settings)
                        .setToolTip(toolTip15)
                        .setSkipText("SKIP")
                        .setDismissText("GOT IT")
                        .withRectangleShape(true)
                        .build()
        );



        sequence.start();

        if(sequence.hasFired()){

            Intent intent;
            if (view.getId() == R.id.employees) {
                Intent employee = new Intent(getContext(), EmployeeUpdateListScreen.class);
                getContext().startActivity(employee);

            }else if (view.getId() == R.id.settings) {
                Intent employees = new Intent(getContext(), NotificationShowActivity.class);
                Bundle bun = new Bundle();
                bun.putSerializable("Employee",employee);
                employees.putExtras(bun);
                getContext().startActivity(employees);

            } else if (view.getId() == R.id.plan_detail) {
                Intent employee = new Intent(getContext(), PlanDesignActivity.class);
                getContext().startActivity(employee);

            } else if (view.getId() == R.id.team) {
                Intent employee = new Intent(getContext(), TeamMembersList.class);
                getContext().startActivity(employee);

            } else if (view.getId() == R.id.holiday) {
                Intent employee = new Intent(getContext(), WorkingDaysScreen.class);
                getContext().startActivity(employee);

            } else if (view.getId() == R.id.faq_home) {
                Intent employee = new Intent(getContext(), FAQFragment.class);
                getContext().startActivity(employee);

            } else if (view.getId() == R.id.attendance) {
                intent = new Intent(getContext(), EmployeeListScreen.class);
                intent.putExtra("viewId", view.getId());
                intent.putExtra("Type","attendance");
                getContext().startActivity(intent);
            }else if (view.getId() == R.id.report_mgmt) {
                intent = new Intent(getContext(), ReportManagementScreen.class);
                intent.putExtra("viewId", view.getId());
                intent.putExtra("Type","Report");
                getContext().startActivity(intent);
            } else if (view.getId() == R.id.leaveApplications) {
                intent = new Intent(getContext(), EmployeeListScreen.class);
                intent.putExtra("Type","Leave");
                intent.putExtra("viewId", view.getId());
                getContext().startActivity(intent);
            }else if (view.getId() == R.id.expenses_mgmt) {
                Intent live = new Intent(getActivity(), ExpensesListAdmin.class);
                live.putExtra("Type","Expense");
                getContext().startActivity(live);

            }else if (view.getId() == R.id.department) {
                Intent organization = new Intent(getActivity(), OrganizationProfileScreen.class);
                getContext().startActivity(organization);
            }else if (view.getId() == R.id.department_org) {
                Intent organization = new Intent(getActivity(), DepartmentLilstScreen.class);
                getContext().startActivity(organization);
            }else if (view.getId() == R.id.live_tracking) {
                Intent live = new Intent(getActivity(), AllEmployeeLiveLocation.class);
                live.putExtra("Type","Live");
                getContext().startActivity(live);

            }else if (view.getId() == R.id.task_layout) {
                Intent task = new Intent(getActivity(), EmployeeListScreen.class);
                task.putExtra("Type","Task");
                getContext().startActivity(task);
            }else if (view.getId() == R.id.salary) {
                Intent salary = new Intent(getActivity(), EmployeeListScreen.class);
                salary.putExtra("Type","Salary");
                startActivity(salary);
            }else if (view.getId() == R.id.change_password) {
                Intent chnage = new Intent(getActivity(), ChangePasswordScreen.class);
                startActivity(chnage);
            }else if (view.getId() == R.id.customer_creation) {
                Intent chnage = new Intent(getActivity(), CustomerList.class);
                startActivity(chnage);

                //Toast.makeText(getActivity(), "Coming Soon", Toast.LENGTH_SHORT).show();
            }else if (view.getId() == R.id.logout) {

                if(employee!=null){

                    Employee profile = employee;

                    profile.setAppOpen(false);
                    String app_version = PreferenceHandler.getInstance(getActivity()).getAppVersion();
                    profile.setLastUpdated(""+ BuildConfig.VERSION_NAME);
                    profile.setLastseen(new SimpleDateFormat("MM/dd/yyyy").format(new Date()));
                    updateProfile(profile);

                }else{

                    getProfile(PreferenceHandler.getInstance(getActivity()).getUserId());

                }

            }

        }







    }



    private void getEmployees(){




        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {
                EmployeeApi apiService = Util.getClient().create(EmployeeApi.class);
                Call<ArrayList<Employee>> call = apiService.getProfileById(PreferenceHandler.getInstance(getActivity()).getUserId());

                call.enqueue(new Callback<ArrayList<Employee>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Employee>> call, Response<ArrayList<Employee>> response) {
                        int statusCode = response.code();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {


                           /* if (progressDialog != null&&progressDialog.isShowing())
                                progressDialog.dismiss();*/
                            ArrayList<Employee> list = response.body();


                            if (list !=null && list.size()!=0) {

                                employee = list.get(0);


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
                      /*  if (progressDialog != null&&progressDialog.isShowing())
                            progressDialog.dismiss();*/

                        Log.e("TAG", t.toString());
                    }
                });
            }


        });
    }

    public void getProfile(final int id ){

        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setCancelable(false);
        dialog.setTitle("Saving Details...");
        dialog.show();

        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {

                final EmployeeApi subCategoryAPI = Util.getClient().create(EmployeeApi.class);
                Call<ArrayList<Employee>> getProf = subCategoryAPI.getProfileById(id);
                //Call<ArrayList<Blogs>> getBlog = blogApi.getBlogs();

                getProf.enqueue(new Callback<ArrayList<Employee>>() {

                    @Override
                    public void onResponse(Call<ArrayList<Employee>> call, Response<ArrayList<Employee>> response) {

                        if(dialog != null && dialog.isShowing())
                        {
                            dialog.dismiss();
                        }

                        if (response.code() == 200)
                        {
                            System.out.println("Inside api");

                            Employee profile = response.body().get(0);
                            String app_version = PreferenceHandler.getInstance(getActivity()).getAppVersion();
                            profile.setLastUpdated(""+ BuildConfig.VERSION_NAME);
                            profile.setLastseen(new SimpleDateFormat("MM/dd/yyyy").format(new Date()));
                            profile.setAppOpen(false);
                            updateProfile(profile);

                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Employee>> call, Throwable t) {

                        if(dialog != null && dialog.isShowing())
                        {
                            dialog.dismiss();
                        }

                    }
                });

            }

        });
    }

    public void updateProfile(final Employee employee){

        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setCancelable(false);
        dialog.setTitle("Saving Details...");
        dialog.show();

        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {

                final EmployeeApi subCategoryAPI = Util.getClient().create(EmployeeApi.class);
                Call<Employee> getProf = subCategoryAPI.updateEmployee(employee.getEmployeeId(),employee);
                //Call<ArrayList<Blogs>> getBlog = blogApi.getBlogs();

                getProf.enqueue(new Callback<Employee>() {

                    @Override
                    public void onResponse(Call<Employee> call, Response<Employee> response) {

                        if(dialog != null && dialog.isShowing())
                        {
                            dialog.dismiss();
                        }

                        if(PreferenceHandler.getInstance(getActivity()).getUserRoleUniqueID()==9){

                            Intent intent = new Intent(getActivity(), LocationForegroundService.class);
                            intent.setAction(LocationForegroundService.ACTION_STOP_FOREGROUND_SERVICE);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                getActivity().startForegroundService(intent);
                            } else {
                                getActivity().startService(intent);
                            }
                        }

                        if (response.code() == 200||response.code()==201||response.code()==204)
                        {


                            PreferenceHandler.getInstance(getActivity()).clear();


                            Intent log = new Intent(getActivity(), LandingScreen.class);
                            log.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            log.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            Toast.makeText(getActivity(),"Logout",Toast.LENGTH_SHORT).show();
                            startActivity(log);
                            getActivity().finish();

                        }else{
                            PreferenceHandler.getInstance(getActivity()).clear();

                            Intent log = new Intent(getActivity(), LandingScreen.class);
                            log.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            log.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            Toast.makeText(getActivity(),"Logout",Toast.LENGTH_SHORT).show();
                            startActivity(log);
                            getActivity().finish();
                            // Toast.makeText(ChangePasswordScreen.this, "Failed due to status code"+response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Employee> call, Throwable t) {

                        if(dialog != null && dialog.isShowing())
                        {
                            dialog.dismiss();
                        }

                        if(PreferenceHandler.getInstance(getActivity()).getUserRoleUniqueID()==9){

                            Intent intent = new Intent(getActivity(), LocationForegroundService.class);
                            intent.setAction(LocationForegroundService.ACTION_STOP_FOREGROUND_SERVICE);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                getActivity().startForegroundService(intent);
                            } else {
                                getActivity().startService(intent);
                            }
                        }


                        PreferenceHandler.getInstance(getActivity()).clear();

                        Intent log = new Intent(getActivity(), LandingScreen.class);
                        log.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        log.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        Toast.makeText(getActivity(),"Logout",Toast.LENGTH_SHORT).show();
                        startActivity(log);
                        getActivity().finish();
                        //  Toast.makeText(ChangePasswordScreen.this, "Something went wrong due to "+t.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

            }

        });
    }

    void presentShowcaseView() {

      }

}
