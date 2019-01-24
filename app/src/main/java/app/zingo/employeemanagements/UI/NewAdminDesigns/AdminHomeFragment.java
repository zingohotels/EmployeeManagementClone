package app.zingo.employeemanagements.UI.NewAdminDesigns;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.itextpdf.text.pdf.StringUtils;

import app.zingo.employeemanagements.R;
import app.zingo.employeemanagements.UI.Admin.DashBoardAdmin;
import app.zingo.employeemanagements.UI.Common.ChangePasswordScreen;
import app.zingo.employeemanagements.UI.Company.OrganizationDetailScree;
import app.zingo.employeemanagements.UI.Employee.EmployeeListScreen;
import app.zingo.employeemanagements.UI.LandingScreen;
import app.zingo.employeemanagements.UI.NewEmployeeDesign.ExpenseManageHost;
import app.zingo.employeemanagements.UI.PlanMainHostScreen;
import app.zingo.employeemanagements.Utils.PreferenceHandler;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdminHomeFragment extends Fragment {

    final String TAG = "Employer Dashboard";
    View layout;
    LinearLayout attendance,leaveApplications,employees;
    LinearLayout departments,liveTracking,tasks,expenses;
    LinearLayout salary,logout,deptOrg,chngPwd,plans,reports;


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
        attendance = (LinearLayout) this.layout.findViewById(R.id.attendance);
        leaveApplications = (LinearLayout) this.layout.findViewById(R.id.leaveApplications);
        employees = (LinearLayout) this.layout.findViewById(R.id.employees);

        departments = (LinearLayout) this.layout.findViewById(R.id.department);
        liveTracking = (LinearLayout) this.layout.findViewById(R.id.live_tracking);
        tasks = (LinearLayout) this.layout.findViewById(R.id.task_layout);

        expenses = (LinearLayout) this.layout.findViewById(R.id.expenses_mgmt);
        plans = (LinearLayout) this.layout.findViewById(R.id.plan_detail);


        salary = (LinearLayout) this.layout.findViewById(R.id.salary);
        deptOrg = (LinearLayout) this.layout.findViewById(R.id.department_org);
        logout = (LinearLayout) this.layout.findViewById(R.id.logout);
        chngPwd = (LinearLayout) this.layout.findViewById(R.id.change_password);
        reports = (LinearLayout) this.layout.findViewById(R.id.report_mgmt);

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

    }

    public void openMenuViews(View view) {

        Intent intent;
        if (view.getId() == R.id.employees) {
            Intent employee = new Intent(getContext(), EmployeeUpdateListScreen.class);
            getContext().startActivity(employee);

        } else if (view.getId() == R.id.plan_detail) {
            Intent employee = new Intent(getContext(), PlanMainHostScreen.class);
            getContext().startActivity(employee);

        } else if (view.getId() == R.id.attendance) {
            intent = new Intent(getContext(), EmployeeListScreen.class);
            intent.putExtra("viewId", view.getId());
            intent.putExtra("Type","attendance");
            getContext().startActivity(intent);
        }else if (view.getId() == R.id.report_mgmt) {
            intent = new Intent(getContext(), EmployeeListScreen.class);
            intent.putExtra("viewId", view.getId());
            intent.putExtra("Type","Report");
            getContext().startActivity(intent);
        } else if (view.getId() == R.id.leaveApplications) {
            intent = new Intent(getContext(), EmployeeListScreen.class);
            intent.putExtra("Type","Leave");
            intent.putExtra("viewId", view.getId());
            getContext().startActivity(intent);
        }else if (view.getId() == R.id.expenses_mgmt) {
            Intent live = new Intent(getActivity(), EmployeeListScreen.class);
            live.putExtra("Type","Expense");
            getContext().startActivity(live);

        }else if (view.getId() == R.id.department) {
            Intent organization = new Intent(getActivity(), OrganizationDetailScree.class);
            getContext().startActivity(organization);
        }else if (view.getId() == R.id.department_org) {
            Intent organization = new Intent(getActivity(), DepartmentLilstScreen.class);
            getContext().startActivity(organization);
        }else if (view.getId() == R.id.live_tracking) {
            Intent live = new Intent(getActivity(), EmployeeListScreen.class);
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


}
