package app.zingo.employeemanagements.UI.NewEmployeeDesign;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import app.zingo.employeemanagements.Model.Employee;
import app.zingo.employeemanagements.Model.EmployeeImages;
import app.zingo.employeemanagements.Model.LoginDetails;
import app.zingo.employeemanagements.Model.LoginDetailsNotificationManagers;
import app.zingo.employeemanagements.R;
import app.zingo.employeemanagements.Service.LocationForegroundService;
import app.zingo.employeemanagements.UI.Admin.EmployeesDashBoard;
import app.zingo.employeemanagements.UI.Admin.TaskManagementHost;
import app.zingo.employeemanagements.UI.Common.ChangePasswordScreen;
import app.zingo.employeemanagements.UI.Common.InvokeService;
import app.zingo.employeemanagements.UI.Company.OrganizationDetailScree;
import app.zingo.employeemanagements.UI.Employee.EmployeeListScreen;
import app.zingo.employeemanagements.UI.Employee.LeaveManagementHost;
import app.zingo.employeemanagements.UI.Landing.InternalServerErrorScreen;
import app.zingo.employeemanagements.UI.LandingScreen;
import app.zingo.employeemanagements.UI.NewAdminDesigns.AdminNewMainScreen;
import app.zingo.employeemanagements.UI.NewAdminDesigns.DailyTargetsForEmployeeActivity;
import app.zingo.employeemanagements.UI.NewAdminDesigns.DepartmentLilstScreen;
import app.zingo.employeemanagements.UI.NewAdminDesigns.EmployeeDashBoardAdminView;
import app.zingo.employeemanagements.UI.NewAdminDesigns.EmployeeUpdateListScreen;
import app.zingo.employeemanagements.UI.NewAdminDesigns.ExpenseDashBoardAdmin;
import app.zingo.employeemanagements.UI.NewAdminDesigns.LeaveEmployeeListScreen;
import app.zingo.employeemanagements.UI.NewAdminDesigns.TeamMembersList;
import app.zingo.employeemanagements.Utils.Constants;
import app.zingo.employeemanagements.Utils.PreferenceHandler;
import app.zingo.employeemanagements.Utils.ThreadExecuter;
import app.zingo.employeemanagements.Utils.Util;
import app.zingo.employeemanagements.WebApi.EmployeeApi;
import app.zingo.employeemanagements.WebApi.LoginDetailsAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass. fragment_employee_home
 */
public class EmployeeHomeFragment extends Fragment {

    final String TAG = "Employer Dashboard";
    View layout;
    LinearLayout attendance,leaveApplications;
    LinearLayout tasks,expenses,meeting,team;
    LinearLayout logout,deptOrg,chngPwd,salary,client;

    Employee employeed;


    public EmployeeHomeFragment() {
        // Required empty public constructor
    }

    public static EmployeeHomeFragment getInstance() {
        return new EmployeeHomeFragment();
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
            this.layout = layoutInflater.inflate(R.layout.fragment_employee_home, viewGroup, false);

            setupListeners();
            viewGroup = this.layout.findViewById(R.id.renewWarning);

            if(PreferenceHandler.getInstance(getActivity()).getUserId()==20){

            }else{
                viewGroup.setVisibility(View.GONE);
            }

            viewGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent invo = new Intent(getActivity(),InvokeService.class);
                    startActivity(invo);

                }
            });
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

    public void setupListeners() throws Exception {

        getEmployees();
        attendance = (LinearLayout) this.layout.findViewById(R.id.attendance);
        leaveApplications = (LinearLayout) this.layout.findViewById(R.id.leaveApplications);
        chngPwd = (LinearLayout) this.layout.findViewById(R.id.change_password);
        salary = (LinearLayout) this.layout.findViewById(R.id.salary);
        client = (LinearLayout) this.layout.findViewById(R.id.customer_creation);

        tasks = (LinearLayout) this.layout.findViewById(R.id.task_layout);
        expenses = (LinearLayout) this.layout.findViewById(R.id.expenses_mgmt);
        meeting = (LinearLayout) this.layout.findViewById(R.id.meeting);

        team = (LinearLayout) this.layout.findViewById(R.id.team);
        deptOrg = (LinearLayout) this.layout.findViewById(R.id.department);
        logout = (LinearLayout) this.layout.findViewById(R.id.logout);

        //App new version available
        View updatedText = this.layout.findViewById(R.id.updateText);
/*        if (this.mAppUser == null || this.mAppUser.getAppVersion() == 0 || AMApp.version <= this.mAppUser.getAppVersion()) {
            updatedText.setVisibility(8);
            return;
        }
        updatedText.setVisibility(0);
        findViewById.setOnClickListener(this);*/

        try{



        attendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    openMenuViews(attendance);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        leaveApplications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    openMenuViews(leaveApplications);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        expenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    openMenuViews(expenses);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        team.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    openMenuViews(team);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        salary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    openMenuViews(salary);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        tasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    openMenuViews(tasks);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        deptOrg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    openMenuViews(deptOrg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    openMenuViews(logout);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        chngPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    openMenuViews(chngPwd);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        meeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    openMenuViews(meeting);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        client.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    openMenuViews(client);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        }catch (Exception e){
            e.printStackTrace();
        }


    }

    public void openMenuViews(View view) throws Exception {

        Intent intent;
        if (view.getId() == R.id.employees) {
            Intent employee = new Intent(getContext(), EmployeeUpdateListScreen.class);
            getContext().startActivity(employee);

        }  else if (view.getId() == R.id.team) {
            Intent employee = new Intent(getContext(), TeamMembersList.class);
            getContext().startActivity(employee);

        }else if (view.getId() == R.id.salary) {
            Intent salary = new Intent(getActivity(), ViewPaySlipScreen.class);
            salary.putExtra("Type","Salary");
            startActivity(salary);
        } else if (view.getId() == R.id.attendance) {
            Intent attnd = new Intent(getActivity(), EmployeeDashBoardAdminView.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("Profile",employeed);
            bundle.putInt("ProfileId",employeed.getEmployeeId());
            attnd.putExtras(bundle);
            getActivity().startActivity(attnd);
        }else if (view.getId() == R.id.meeting) {
            Intent attnd = new Intent(getActivity(), MeetingDetailList.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("Profile",employeed);
            bundle.putInt("ProfileId",employeed.getEmployeeId());
            attnd.putExtras(bundle);
            getActivity().startActivity(attnd);
        } else if (view.getId() == R.id.leaveApplications) {
            Intent leave = new Intent(getActivity(), LeaveManagementHost.class);
            Bundle bundle = new Bundle();
            bundle.putInt("EmployeeId",employeed.getEmployeeId());
            bundle.putSerializable("Employee",employeed);
            leave.putExtras(bundle);
            getActivity().startActivity(leave);
        }else if (view.getId() == R.id.expenses_mgmt) {
            Intent leave = new Intent(getActivity(), ExpenseManageHost.class);
            Bundle bundle = new Bundle();
            bundle.putInt("EmployeeId",employeed.getEmployeeId());
            bundle.putSerializable("Employee",employeed);
            leave.putExtras(bundle);
            getActivity().startActivity(leave);


        }else if (view.getId() == R.id.department) {
            Intent organization = new Intent(getActivity(), OrganizationDetailScree.class);
            getContext().startActivity(organization);
        }else if (view.getId() == R.id.department_org) {
            Intent organization = new Intent(getActivity(), OrganizationDetailScree.class);
            getContext().startActivity(organization);
        }else if (view.getId() == R.id.live_tracking) {
            Intent live = new Intent(getActivity(), EmployeeListScreen.class);
            live.putExtra("Type","Live");
            getContext().startActivity(live);

        }else if (view.getId() == R.id.change_password) {
            Intent chnage = new Intent(getActivity(), ChangePasswordScreen.class);
            startActivity(chnage);
        }else if (view.getId() == R.id.task_layout) {

            Intent task = new Intent(getActivity(), DailyTargetsForEmployeeActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("Profile",employeed);
            bundle.putInt("ProfileId",employeed.getEmployeeId());
            task.putExtras(bundle);
            startActivity(task);

           /* Intent task = new Intent(getActivity(), TaskManagementHost.class);
            Bundle bundle = new Bundle();
            bundle.putInt("EmployeeId",employeed.getEmployeeId());
            bundle.putSerializable("Employee",employeed);
            task.putExtras(bundle);
            getActivity().startActivity(task);*/
        }else if (view.getId() == R.id.salary) {
            Intent salary = new Intent(getActivity(), EmployeeListScreen.class);
            salary.putExtra("Type","Salary");
            startActivity(salary);
        }else if (view.getId() == R.id.customer_creation) {
            /*Intent chnage = new Intent(getActivity(), CustomerCreation.class);
            startActivity(chnage);*/

            Toast.makeText(getActivity(), "Coming Soon", Toast.LENGTH_SHORT).show();
        }else if (view.getId() == R.id.logout) {



            String loginStatus = PreferenceHandler.getInstance(getActivity()).getLoginStatus();
            String meetingStatus = PreferenceHandler.getInstance(getActivity()).getMeetingLoginStatus();

            if (loginStatus != null && !loginStatus.isEmpty()) {

                if (loginStatus.equalsIgnoreCase("Login")) {

                    if (meetingStatus != null && meetingStatus.equalsIgnoreCase("Login")) {

                        Toast.makeText(getActivity(), "You are in some meeting .So Please checkout", Toast.LENGTH_SHORT).show();

                    } else {



                        final android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(getActivity());
                        LayoutInflater inflater = getLayoutInflater();
                        View views = inflater.inflate(R.layout.absent_condition,null);
                        Button agree = (Button) views.findViewById(R.id.dialog_ok);


                        dialogBuilder.setView(views);
                        final android.app.AlertDialog dialog = dialogBuilder.create();
                        dialog.setCancelable(true);
                        dialog.show();

                        agree.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                Intent intent = new Intent(getActivity(), EmployeeNewMainScreen.class);
                                intent.putExtra("viewpager_position", 2);
                                startActivity(intent);
                            }
                        });
                    }


                }else{
                    if(employeed!=null){

                        Employee profile = employeed;

                        profile.setAppOpen(false);
                        updateProfile(profile);

                    }else{

                        getProfile(PreferenceHandler.getInstance(getActivity()).getUserId());

                    }
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


                           /* if (progressDialog!=null)
                                progressDialog.dismiss();*/
                            ArrayList<Employee> list = response.body();


                            if (list !=null && list.size()!=0) {

                                employeed = list.get(0);


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

                        Intent intent = new Intent(getActivity(), LocationForegroundService.class);
                        intent.setAction(LocationForegroundService.ACTION_STOP_FOREGROUND_SERVICE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            getActivity().startForegroundService(intent);
                        } else {
                            getActivity().startService(intent);
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

                        Intent intent = new Intent(getActivity(), LocationForegroundService.class);
                        intent.setAction(LocationForegroundService.ACTION_STOP_FOREGROUND_SERVICE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            getActivity().startForegroundService(intent);
                        } else {
                            getActivity().startService(intent);
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


}
