package app.zingo.employeemanagements.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import app.zingo.employeemanagements.Model.Employee;
import app.zingo.employeemanagements.Model.Expenses;
import app.zingo.employeemanagements.Model.LoginDetails;
import app.zingo.employeemanagements.Model.Meetings;
import app.zingo.employeemanagements.Model.Tasks;
import app.zingo.employeemanagements.base.R;
import app.zingo.employeemanagements.UI.Admin.CreatePaySlip;
import app.zingo.employeemanagements.UI.Admin.EmployeeLiveMappingScreen;
import app.zingo.employeemanagements.UI.Admin.EmployeesDashBoard;
import app.zingo.employeemanagements.UI.Admin.TaskManagementHost;
import app.zingo.employeemanagements.UI.Employee.EmployeeMeetingHost;
import app.zingo.employeemanagements.UI.NewAdminDesigns.EmployeeExpenseList;
import app.zingo.employeemanagements.UI.NewAdminDesigns.LeaveEmployeeListScreen;
import app.zingo.employeemanagements.Utils.PreferenceHandler;
import app.zingo.employeemanagements.Utils.ThreadExecuter;
import app.zingo.employeemanagements.Utils.Util;
import app.zingo.employeemanagements.WebApi.ExpensesApi;
import app.zingo.employeemanagements.WebApi.LoginDetailsAPI;
import app.zingo.employeemanagements.WebApi.MeetingsAPI;
import app.zingo.employeemanagements.WebApi.TasksAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportDetailEmployeeAdapter extends RecyclerView.Adapter<ReportDetailEmployeeAdapter.ViewHolder>{

    private Context context;
    private ArrayList<Employee> list;


    public ReportDetailEmployeeAdapter(Context context, ArrayList<Employee> list) {

        this.context = context;
        this.list = list;


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_report_details, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Employee dto = list.get(position);

        if(dto!=null){

            holder.mName.setText(""+dto.getEmployeeName());

            LoginDetails ld  = new LoginDetails();
            ld.setEmployeeId(dto.getEmployeeId());
            ld.setLoginDate(new SimpleDateFormat("MM/dd/yyyy").format(new Date()));
            getLoginDetails(ld,holder.mLogin,holder.mLogout,holder.mHours);

            Meetings md  = new Meetings();
            md.setEmployeeId(dto.getEmployeeId());
            md.setMeetingDate(new SimpleDateFormat("MM/dd/yyyy").format(new Date()));
            getMeetingsDetails(md,holder.mVisits);

            getTasks(dto.getEmployeeId(),"Completed",holder.mTasks);
            getExpense(dto.getEmployeeId(),holder.mExpenses, holder.mExpAmt);
        }

    }




    @Override
    public int getItemCount() {
        return list.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder /*implements View.OnClickListener*/ {

        public TextView mName,mLogin,mLogout,mHours,mVisits,mTasks,mExpenses,mExpAmt;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(true);

            mName = itemView.findViewById(R.id.report_name);
            mLogin = itemView.findViewById(R.id.report_login);
            mLogout = itemView.findViewById(R.id.report_logout);
            mHours = itemView.findViewById(R.id.report_hours);
            mVisits = itemView.findViewById(R.id.report_visit);
            mTasks = itemView.findViewById(R.id.report_task);
            mExpenses = itemView.findViewById(R.id.report_expense);
            mExpAmt = itemView.findViewById(R.id.report_expense_amount);



        }
    }

    private void getLoginDetails(final LoginDetails loginDetails,final TextView login,final TextView logout,final TextView workingHrs){




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

                                String loginTime = list.get(0).getLoginTime();
                                String logoutTime = list.get(list.size()-1).getLogOutTime();

                                if(loginTime!=null&&!loginTime.isEmpty()){
                                    login.setText(""+loginTime);
                                }else{
                                    login.setText("");
                                }

                                if(logoutTime!=null&&!logoutTime.isEmpty()){
                                    logout.setText(""+logoutTime);
                                }else{
                                    logout.setText("Working");
                                }

                                long diffHrs = 0;

                                for (LoginDetails lg:list) {

                                    if((loginTime==null||loginTime.isEmpty())&&(lg.getLogOutTime()==null||lg.getLogOutTime().isEmpty())){

                                    }else{
                                        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy hh:mm a");
                                        SimpleDateFormat sdfs = new SimpleDateFormat("MMM dd,yyyy");

                                        Date fd=null,td=null;

                                        String logoutT = lg.getLogOutTime();
                                        String loginT = lg.getLoginTime();

                                        if(loginT==null||loginT.isEmpty()){

                                            loginT = sdfs.format(new Date()) +" 00:00 am";
                                        }

                                        if(logoutT==null||logoutT.isEmpty()){

                                            logoutT = sdf.format(new Date()) ;
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

                                }


                                long Hours = diffHrs / (60 * 60 * 1000) ;
                                long Minutes = diffHrs / (60 * 1000);

                                int minutes = (int) ((diffHrs / (1000*60)) % 60);
                                int hours   = (int) ((diffHrs / (1000*60*60)) % 24);
                                int days   = (int) ((diffHrs / (1000*60*60*24)));


                                DecimalFormat df = new DecimalFormat("00");

                                workingHrs.setText(String.format("%02d", days)+String.format("%02d", hours) +":"+String.format("%02d", minutes));

                            }else{

                                login.setText("ABSENT");
                                login.setTextColor(Color.parseColor("#FF0000"));
                                logout.setTextColor(Color.parseColor("#FF0000"));
                                logout.setText("ABSENT");

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
    private void getMeetingsDetails(final Meetings loginDetails, final TextView visit){




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

                            if (list !=null && list.size()!=0) {

                                visit.setText(""+list.size());

                            }else{

                                // Toast.makeText(DailyTargetsForEmployeeActivity.this, "No Tasks given for this employee ", Toast.LENGTH_SHORT).show();

                            }

                        }else {



                            //Toast.makeText(DailyTargetsForEmployeeActivity.this, "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Meetings>> call, Throwable t) {
                        // Log error here since request failed
                       /* if (progressDialog!=null)
                            progressDialog.dismiss();*/
                        Log.e("TAG", t.toString());
                    }
                });
            }


        });
    }
    private void getTasks(final int employeeId,final String status,final  TextView task){




        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {
                TasksAPI apiService = Util.getClient().create(TasksAPI.class);
                Call<ArrayList<Tasks>> call = apiService.getTasksByEmployeeIdStatus(employeeId,status);

                call.enqueue(new Callback<ArrayList<Tasks>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Tasks>> call, Response<ArrayList<Tasks>> response) {
                        int statusCode = response.code();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {



                            ArrayList<Tasks> list = response.body();
                            ArrayList<Tasks> todayTasks = new ArrayList<>();


                            Date date = new Date();
                            Date adate = new Date();
                            Date edate = new Date();



                            try {
                                date = new SimpleDateFormat("yyyy-MM-dd").parse(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));


                            } catch (Exception e) {
                                e.printStackTrace();
                            }



                            if (list !=null && list.size()!=0) {



                                for (Tasks task:list) {



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

                                            todayTasks.add(task);


                                        }
                                    }






                                }

                                if(todayTasks!=null&&todayTasks.size()!=0){

                                    task.setText(""+todayTasks.size());



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
    private void getExpense(final int employeeId,final TextView exp,final  TextView expeAmt){




        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {
                ExpensesApi apiService = Util.getClient().create(ExpensesApi.class);
                Call<ArrayList<Expenses>> call = apiService.getExpenseByEmployeeIdAndOrganizationId(PreferenceHandler.getInstance(context).getCompanyId(),employeeId);

                call.enqueue(new Callback<ArrayList<Expenses>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Expenses>> call, Response<ArrayList<Expenses>> response) {
                        int statusCode = response.code();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {



                            ArrayList<Expenses> list = response.body();
                            ArrayList<Expenses> todayTasks = new ArrayList<>();


                            Date date = new Date();
                            Date adate = new Date();
                            Date edate = new Date();



                            try {
                                date = new SimpleDateFormat("yyyy-MM-dd").parse(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));


                            } catch (Exception e) {
                                e.printStackTrace();
                            }



                            if (list !=null && list.size()!=0) {


                                double amt = 0;

                                for (Expenses task:list) {



                                    String froms = task.getDate();


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



                                    if(afromDate!=null){



                                        if(date.getTime() == afromDate.getTime() ){

                                            todayTasks.add(task);
                                            amt = amt+task.getAmount();


                                        }
                                    }






                                }

                                if(todayTasks!=null&&todayTasks.size()!=0){

                                    exp.setText(""+todayTasks.size());
                                    expeAmt.setText("Rs "+new DecimalFormat("#.##").format(amt));



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
                    public void onFailure(Call<ArrayList<Expenses>> call, Throwable t) {
                        // Log error here since request failed
                       /* if (progressDialog!=null)
                            progressDialog.dismiss();*/
                        Log.e("TAG", t.toString());
                    }
                });
            }


        });
    }
}
