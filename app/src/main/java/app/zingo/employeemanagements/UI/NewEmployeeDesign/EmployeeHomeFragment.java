package app.zingo.employeemanagements.UI.NewEmployeeDesign;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import app.zingo.employeemanagements.Model.Employee;
import app.zingo.employeemanagements.Model.LoginDetails;
import app.zingo.employeemanagements.R;
import app.zingo.employeemanagements.UI.Admin.EmployeesDashBoard;
import app.zingo.employeemanagements.UI.Admin.TaskManagementHost;
import app.zingo.employeemanagements.UI.Common.ChangePasswordScreen;
import app.zingo.employeemanagements.UI.Company.OrganizationDetailScree;
import app.zingo.employeemanagements.UI.Employee.EmployeeListScreen;
import app.zingo.employeemanagements.UI.Employee.LeaveManagementHost;
import app.zingo.employeemanagements.UI.LandingScreen;
import app.zingo.employeemanagements.UI.NewAdminDesigns.DepartmentLilstScreen;
import app.zingo.employeemanagements.UI.NewAdminDesigns.EmployeeUpdateListScreen;
import app.zingo.employeemanagements.UI.NewAdminDesigns.LeaveEmployeeListScreen;
import app.zingo.employeemanagements.Utils.PreferenceHandler;
import app.zingo.employeemanagements.Utils.ThreadExecuter;
import app.zingo.employeemanagements.Utils.Util;
import app.zingo.employeemanagements.WebApi.EmployeeApi;
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
    LinearLayout tasks,expenses,meeting;
    LinearLayout logout,deptOrg,chngPwd;

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
            viewGroup.setVisibility(View.GONE);
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

        getEmployees();
        attendance = (LinearLayout) this.layout.findViewById(R.id.attendance);
        leaveApplications = (LinearLayout) this.layout.findViewById(R.id.leaveApplications);
        chngPwd = (LinearLayout) this.layout.findViewById(R.id.change_password);



        tasks = (LinearLayout) this.layout.findViewById(R.id.task_layout);
        expenses = (LinearLayout) this.layout.findViewById(R.id.expenses_mgmt);
        meeting = (LinearLayout) this.layout.findViewById(R.id.meeting);


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

        attendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openMenuViews(attendance);
            }
        });

        leaveApplications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMenuViews(leaveApplications);
            }
        });

        expenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMenuViews(expenses);
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
        meeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMenuViews(meeting);
            }
        });

    }

    public void openMenuViews(View view) {

        Intent intent;
        if (view.getId() == R.id.employees) {
            Intent employee = new Intent(getContext(), EmployeeUpdateListScreen.class);
            getContext().startActivity(employee);

        } else if (view.getId() == R.id.attendance) {
            Intent attnd = new Intent(getActivity(), EmployeesDashBoard.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("Profile",employeed);
            bundle.putInt("ProfileId",employeed.getEmployeeId());
            attnd.putExtras(bundle);
            getActivity().startActivity(attnd);
        }else if (view.getId() == R.id.meeting) {
            Intent attnd = new Intent(getActivity(), MeetingAddWithSignScreen.class);
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
            Intent task = new Intent(getActivity(), TaskManagementHost.class);
            Bundle bundle = new Bundle();
            bundle.putInt("EmployeeId",employeed.getEmployeeId());
            bundle.putSerializable("Employee",employeed);
            task.putExtras(bundle);
            getActivity().startActivity(task);
        }else if (view.getId() == R.id.salary) {
            Intent salary = new Intent(getActivity(), EmployeeListScreen.class);
            salary.putExtra("Type","Salary");
            startActivity(salary);
        }else if (view.getId() == R.id.logout) {
            PreferenceHandler.getInstance(getActivity()).clear();

            Intent log = new Intent(getActivity(), LandingScreen.class);
            log.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            log.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            Toast.makeText(getActivity(),"Logout",Toast.LENGTH_SHORT).show();
            startActivity(log);
            getActivity().finish();
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
}
