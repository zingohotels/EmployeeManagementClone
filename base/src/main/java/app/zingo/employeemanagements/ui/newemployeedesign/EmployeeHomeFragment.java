package app.zingo.employeemanagements.ui.newemployeedesign;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

import app.zingo.employeemanagements.model.Employee;
import app.zingo.employeemanagements.ui.Common.ChangePasswordScreen;
import app.zingo.employeemanagements.ui.Common.CustomerMapViewScreen;
import app.zingo.employeemanagements.ui.Common.InvokeService;
import app.zingo.employeemanagements.ui.Common.NotificationShowActivity;
import app.zingo.employeemanagements.ui.Company.OrganizationDetailScree;
import app.zingo.employeemanagements.ui.Employee.EmployeeListScreen;
import app.zingo.employeemanagements.ui.Employee.LeaveManagementHost;
import app.zingo.employeemanagements.ui.NewAdminDesigns.DailyTargetsForEmployeeActivity;
import app.zingo.employeemanagements.ui.NewAdminDesigns.EmployeeDashBoardAdminView;
import app.zingo.employeemanagements.ui.NewAdminDesigns.EmployeeUpdateListScreen;
import app.zingo.employeemanagements.ui.NewAdminDesigns.TeamMembersList;
import app.zingo.employeemanagements.utils.PreferenceHandler;
import app.zingo.employeemanagements.utils.ThreadExecuter;
import app.zingo.employeemanagements.utils.Util;
import app.zingo.employeemanagements.WebApi.EmployeeApi;
import app.zingo.employeemanagements.base.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass. fragment_employee_home
 */
public class EmployeeHomeFragment extends Fragment {

    private final String TAG = "Employer Dashboard";
    View layout;
    private LinearLayout attendance,leaveApplications,tasks,expenses,meeting,team,
            logout,deptOrg,chngPwd,salary,client,ShareApp,orders,weekOff;
    private Employee employeed;

    public EmployeeHomeFragment() {
        // Required empty public constructor
    }

    public static EmployeeHomeFragment getInstance() {
        return new EmployeeHomeFragment ();
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
                    Intent invo = new Intent(getActivity(), InvokeService.class);
                    startActivity(invo);
                }
            });

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
        attendance = this.layout.findViewById(R.id.attendance);
        leaveApplications = this.layout.findViewById(R.id.leaveApplications);
        chngPwd = this.layout.findViewById(R.id.change_password);
        salary = this.layout.findViewById(R.id.salary);
        client = this.layout.findViewById(R.id.customer_creation);

        tasks = this.layout.findViewById(R.id.task_layout);
        expenses = this.layout.findViewById(R.id.expenses_mgmt);
        weekOff = this.layout.findViewById(R.id.week_off_mgmt);
        meeting = this.layout.findViewById(R.id.meeting);

        team = this.layout.findViewById(R.id.team);
        deptOrg = this.layout.findViewById(R.id.department);
        logout = this.layout.findViewById(R.id.logout);
        ShareApp = this.layout.findViewById(R.id.share_app);
        orders = this.layout.findViewById(R.id.orders);


        ShareApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String upToNCharacters = PreferenceHandler.getInstance(getActivity()).getCompanyName().substring(0, Math.min(PreferenceHandler.getInstance(getActivity()).getCompanyName().length(), 4));

                String body = "<html><head>" +
                        "</head>" +
                        "<body>" +
                        "<h2>Hello,</h2>" +
                        "<p><br>You are invited to join the Mysolite Employee App Platform. </p></br></br>"+
                        "<br><p>Here is a Procedure to Join the Platform using the Below Procedures. Make sure you store them safely. </p>"+
                        "</br><p><br>Our Organization Code- "+upToNCharacters+PreferenceHandler.getInstance(getActivity()).getCompanyId()+
                        "</br></p><br><b>Step 1:  </b>"+"Download the app by clicking here <a href=\"https://play.google.com/store/apps/details?id=app.zingo.employeemanagements\">https://play.google.com/store/apps/details?id=app.zingo.employeemanagements</a>"+
                        "</br><br><b>Step 2: </b>"+"Click on Get Started and \"Join us as an Employee\""+
                        "</br><br><b>Step 3: </b>"+"Verify your Mobile number and then Enter the Organization Code - "+upToNCharacters+PreferenceHandler.getInstance(getActivity()).getCompanyId()+
                        "</br><br><b>Step 4:</b>"+"Enter your basic details and the complete the Sign up process"+
                        "</br><p>From now on, Please login to your account using your organization email id and your password on a daily basis for attendance system,leave management,Expense management, sales visit etc., via mobile app. </p>"+
                        "</br><p>If you have any questions then contact the Admin/HR of the company.</p>"+
                        "</br><p><b>Cheers,</b><br><br>"+PreferenceHandler.getInstance(getActivity()).getUserFullName()+"</p></body></html>";

               /* String htmlString = "<h1>Hello World!</h1>";
                Spanned spanned = HtmlCompat.fromHtml(htmlString, HtmlCompat.FROM_HTML_MODE_COMPACT);

                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("text/html");
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Employee Management App Invitation");
                emailIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(new StringBuilder()
                        .append(spanned)
                        .toString()));
                //emailIntent.putExtra(Intent.EXTRA_HTML_TEXT, body);
                startActivity(Intent.createChooser(emailIntent, "Send email.."));*/

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setType("text/plain");
                emailIntent.setData(Uri.parse("mailto:" ));


                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Employee Management App Invitation");


                emailIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(new StringBuilder()
                        .append(body)
                        .toString()));
                //emailIntent.putExtra(Intent.EXTRA_HTML_TEXT, body);
                startActivity(Intent.createChooser(emailIntent, "Send email.."));
            }
        });

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

            weekOff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        openMenuViews(weekOff);
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


         orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    openMenuViews(orders);
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

    public void openMenuViews(View view) {

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
          //  bundle.putInt("ProfileId",employeed.getEmployeeId());
            attnd.putExtras(bundle);
            getActivity().startActivity(attnd);
        } else if (view.getId() == R.id.leaveApplications) {
            Intent leave = new Intent(getActivity(), LeaveManagementHost.class);
            Bundle bundle = new Bundle();
            bundle.putInt("EmployeeId",employeed.getEmployeeId());
            bundle.putSerializable("Employee",employeed);
            leave.putExtras(bundle);
            getActivity().startActivity(leave);
        }else if (view.getId() == R.id.week_off_mgmt) {
            Intent leave = new Intent(getActivity(), WeekOffApply.class);
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
        }else if (view.getId() == R.id.orders) {
           /* Intent orders = new Intent(getActivity(), DailyOrdersForEmployeeActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("Profile",employeed);
            bundle.putInt("ProfileId",employeed.getEmployeeId());
            orders.putExtras(bundle);
            startActivity(orders);*/

            Intent order = new Intent(getActivity(), StockOrderCategoryListScreen.class);
            startActivity(order);

           /* Intent task = new Intent(getActivity(), StockOrderCategoryListScreen.class);
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
            Intent chnage = new Intent(getActivity(), CustomerMapViewScreen.class);
            startActivity(chnage);

          //  Toast.makeText(getActivity(), "Coming Soon", Toast.LENGTH_SHORT).show();
        }else if (view.getId() == R.id.logout) {

            Intent employees = new Intent(getContext(), NotificationShowActivity.class);
            Bundle bun = new Bundle();
            bun.putSerializable("Employee",employeed);
            bun.putString("Type","Employee");
            employees.putExtras(bun);
            getContext().startActivity(employees);

        }
    }

    private void getEmployees(){




        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {
                EmployeeApi apiService = Util.getClient().create( EmployeeApi.class);
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
                                employeed = list.get(0);

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

}
