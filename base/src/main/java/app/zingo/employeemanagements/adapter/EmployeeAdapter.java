package app.zingo.employeemanagements.adapter;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.model.LatLng;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import app.zingo.employeemanagements.Custom.ICheckChangeListener;
import app.zingo.employeemanagements.model.Employee;
import app.zingo.employeemanagements.model.LoginDetails;
import app.zingo.employeemanagements.ui.Admin.CreatePaySlip;
import app.zingo.employeemanagements.ui.Admin.EmployeeLiveMappingScreen;
import app.zingo.employeemanagements.ui.Employee.ApplyLeaveScreen;
import app.zingo.employeemanagements.ui.Employee.EmployeeMeetingHost;
import app.zingo.employeemanagements.ui.NewAdminDesigns.DailyOrdersForEmployeeActivity;
import app.zingo.employeemanagements.ui.NewAdminDesigns.DailyTargetsForEmployeeActivity;
import app.zingo.employeemanagements.ui.NewAdminDesigns.EmployeeDashBoardAdminView;
import app.zingo.employeemanagements.ui.NewAdminDesigns.ExpenseDashBoardAdmin;
import app.zingo.employeemanagements.ui.NewAdminDesigns.LeaveDashBoardAdminScreen;
import app.zingo.employeemanagements.ui.newemployeedesign.CreateExpensesScreen;
import app.zingo.employeemanagements.ui.newemployeedesign.MeetingDetailList;
import app.zingo.employeemanagements.utils.ThreadExecuter;
import app.zingo.employeemanagements.utils.Util;
import app.zingo.employeemanagements.WebApi.LoginDetailsAPI;
import app.zingo.employeemanagements.base.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ZingoHotels Tech on 28-09-2018.
 */

public class EmployeeAdapter extends RecyclerView.Adapter< EmployeeAdapter.ViewHolder>{
    private Context context;
    private ArrayList<Employee> list;
    private AlertDialog.Builder builder;
    private String type;

    public EmployeeAdapter(Context context, ArrayList<Employee> list,String type) {
        this.context = context;
        this.list = list;
        this.type = type;
    }

    @Override
    public @NotNull ViewHolder onCreateViewHolder( @NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_profile_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Employee dto = list.get(position);
        holder.mProfileName.setText(dto.getEmployeeName());
        holder.mProfileEmail.setText(dto.getPrimaryEmailAddress());
        LoginDetails loginDetails = new LoginDetails();
        loginDetails.setEmployeeId(dto.getEmployeeId());
        loginDetails.setLoginDate(new SimpleDateFormat("MM/dd/yyyy").format(new Date()));
        getLoginDetails(loginDetails,holder.mStatus,holder.mLoginId);

        holder.mProfileMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(type!=null&&type.equalsIgnoreCase("Meetings")){
                    Intent intent = new Intent(context, EmployeeMeetingHost.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("EmployeeId",list.get(position).getEmployeeId());
                    intent.putExtras(bundle);
                    context.startActivity(intent);

                }else if(type!=null&&type.equalsIgnoreCase("attendance")){
                    Intent intent = new Intent(context, EmployeeDashBoardAdminView.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("Profile",list.get(position));
                    bundle.putInt("ProfileId",list.get(position).getEmployeeId());
                    intent.putExtras(bundle);
                    context.startActivity(intent);

                }else if(type!=null&&type.equalsIgnoreCase("meetingsList")){
                    Intent intent = new Intent(context, MeetingDetailList.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("Profile",list.get(position));
                    bundle.putInt("ProfileId",list.get(position).getEmployeeId());
                    intent.putExtras(bundle);
                    context.startActivity(intent);

                }else if(type!=null&&type.equalsIgnoreCase("Expense")){

                    builder = new AlertDialog.Builder(context);
                    builder.setTitle("Expense");
                    builder.setMessage("What do you want to do ?");
                    builder.setPositiveButton("Create Expense",
                            ( dialog , id ) -> {
                                dialog.cancel();
                                Intent dash = new Intent(context, CreateExpensesScreen.class);
                                dash.putExtra("EmployeeId", list.get(position).getEmployeeId());
                                dash.putExtra("ManagerId", list.get(position).getManagerId());
                                context.startActivity(dash);
                            } );

                    builder.setNeutralButton("Cancel",
                            ( dialog , id ) -> dialog.cancel() );

                    builder.setNegativeButton("View",
                            ( dialog , id ) -> {
                                dialog.cancel();
                                Intent intent = new Intent(context, ExpenseDashBoardAdmin.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("Profile",list.get(position));
                                bundle.putInt("EmployeeId",list.get(position).getEmployeeId());
                                intent.putExtras(bundle);
                                context.startActivity(intent);
                            } );
                    builder.create().show();

                }else  if(type!=null&&type.equalsIgnoreCase("Salary")){

                    Intent intent = new Intent(context, CreatePaySlip.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("EmployeeId",list.get(position).getEmployeeId());
                    bundle.putSerializable("Employee",list.get(position));
                    intent.putExtras(bundle);
                    context.startActivity(intent);

                }else  if(type!=null&&type.equalsIgnoreCase("Live")){

                    Intent intent = new Intent(context, EmployeeLiveMappingScreen.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("EmployeeId",list.get(position).getEmployeeId());
                    bundle.putSerializable("Employee",list.get(position));
                    intent.putExtras(bundle);
                    context.startActivity(intent);

                }else  if(type!=null&&type.equalsIgnoreCase("Task")){

                   /* Intent intent = new Intent(context, TaskManagementHost.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("EmployeeId",list.get(position).getEmployeeId());
                    bundle.putInt("DepartmentId",list.get(position).getDepartmentId());
                    bundle.putSerializable("Employee",list.get(position));
                    intent.putExtras(bundle);
                    context.startActivity(intent);*/

                    Intent intent = new Intent(context, DailyTargetsForEmployeeActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("Profile",list.get(position));
                    bundle.putInt("ProfileId",list.get(position).getEmployeeId());
                    intent.putExtras(bundle);
                    context.startActivity(intent);

                }else  if(type!=null&&type.equalsIgnoreCase("Orders")){

                   /* Intent intent = new Intent(context, TaskManagementHost.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("EmployeeId",list.get(position).getEmployeeId());
                    bundle.putInt("DepartmentId",list.get(position).getDepartmentId());
                    bundle.putSerializable("Employee",list.get(position));
                    intent.putExtras(bundle);
                    context.startActivity(intent);*/

                    Intent intent = new Intent(context, DailyOrdersForEmployeeActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("Profile",list.get(position));
                    bundle.putInt("ProfileId",list.get(position).getEmployeeId());
                    intent.putExtras(bundle);
                    context.startActivity(intent);

                }else  if(type!=null&&type.equalsIgnoreCase("Leave")){

                    builder = new AlertDialog.Builder(context);
                    builder.setTitle("Leave");
                    //builder.setIcon(R.drawable.ic_attachment);
                    builder.setMessage("What do you want to do ?");
                    builder.setPositiveButton("Create Leave",
                            ( dialog , id ) -> {
                                dialog.cancel();
                                Intent dash = new Intent(context, ApplyLeaveScreen.class);
                                dash.putExtra("EmployeeId", list.get(position).getEmployeeId());
                                dash.putExtra("ManagerId", list.get(position).getManagerId());
                                context.startActivity(dash);
                            } );

                    builder.setNeutralButton("Cancel",
                            ( dialog , id ) -> dialog.cancel() );

                    builder.setNegativeButton("View",
                            ( dialog , id ) -> {
                                dialog.cancel();
                                Intent intent = new Intent(context, LeaveDashBoardAdminScreen.class);
                                Bundle bundle = new Bundle();
                                bundle.putInt("EmployeeId",list.get(position).getEmployeeId());
                                bundle.putSerializable("Employee",list.get(position));
                                intent.putExtras(bundle);
                                context.startActivity(intent);
                            } );
                    builder.create().show();

                }else  if(type!=null&&type.equalsIgnoreCase("WeekOff")){

                    builder = new AlertDialog.Builder(context);
                    builder.setTitle("WeekOff");
                    //builder.setIcon(R.drawable.ic_attachment);
                    builder.setMessage("What do you want to do ?");
                    builder.setPositiveButton("Create WeekOff",
                            ( dialog , id ) -> {
                                dialog.cancel();
                                Intent dash = new Intent(context, ApplyLeaveScreen.class);
                                dash.putExtra("EmployeeId", list.get(position).getEmployeeId());
                                dash.putExtra("ManagerId", list.get(position).getManagerId());
                                context.startActivity(dash);
                            } );

                    builder.setNeutralButton("Cancel",
                            ( dialog , id ) -> dialog.cancel() );

                    builder.setNegativeButton("View",
                            ( dialog , id ) -> {
                                dialog.cancel();
                                Intent intent = new Intent(context, LeaveDashBoardAdminScreen.class);
                                Bundle bundle = new Bundle();
                                bundle.putInt("EmployeeId",list.get(position).getEmployeeId());
                                bundle.putSerializable("Employee",list.get(position));
                                intent.putExtras(bundle);
                                context.startActivity(intent);
                            } );
                    builder.create().show();

                }/*else  if(type!=null&&type.equalsIgnoreCase("Report")){
                    Intent intent = new Intent(context, ReportManagementScreen.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("EmployeeId",list.get(position).getEmployeeId());
                    bundle.putSerializable("Employee",list.get(position));
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }*/else{
                    Intent intent = new Intent(context, EmployeeDashBoardAdminView.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("Profile",list.get(position));
                    bundle.putInt("ProfileId",list.get(position).getEmployeeId());
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    class ViewHolder extends RecyclerView.ViewHolder /*implements View.OnClickListener*/ {

        public TextView mProfileName,mProfileEmail,mStatus,mLoginId;
        public LinearLayout mProfileMain;
        private ICheckChangeListener iCheckChangeListener;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(true);

            mStatus = itemView.findViewById(R.id.status);
            mProfileName = itemView.findViewById(R.id.profile_name_adapter);
            mProfileEmail = itemView.findViewById(R.id.profile_email_adapter);
            mLoginId = itemView.findViewById(R.id.hidden_login_id);
            mProfileMain = itemView.findViewById(R.id.profileLayout);
        }
    }

    private void getLoginDetails(final LoginDetails loginDetails, final TextView employees,final TextView hidden){
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
                                    if(list.get(0).getTotalMeeting()!=null&&!list.get(0).getTotalMeeting().isEmpty()){
                                        if(list.get(0).getTotalMeeting().equalsIgnoreCase("Absent")){
                                            employees.setText("Absent");
                                            employees.setBackgroundColor(Color.parseColor("#FF0000"));
                                            employees.setVisibility(View.VISIBLE);
                                        }else{
                                            employees.setText("Present");
                                            employees.setVisibility(View.VISIBLE);
                                        }
                                    }else{
                                        employees.setText("Present");
                                        employees.setVisibility(View.VISIBLE);
                                    }
                                    hidden.setText(list.get(0).getLoginDetailsId()+"");

                                }else{
                                    employees.setText("Absent");
                                    employees.setBackgroundColor(Color.parseColor("#FF0000"));
                                    employees.setVisibility(View.VISIBLE);
                                    hidden.setText(0+"");
                                }

                            }else {
                                employees.setText ( "Absent" );
                                employees.setBackgroundColor ( Color.parseColor ( "#FF0000" ) );
                                employees.setVisibility ( View.VISIBLE );
                                hidden.setText ( 0 + "" );
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<LoginDetails>> call, Throwable t) {
                        // Log error here since request failed
                        Log.e("TAG", t.toString());
                    }
                });
            }
        });
    }

    public void getLogiDnetailsById(final int id,final String status,final TextView statusText){
        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage("Saving Details..");
        dialog.setCancelable(false);
        dialog.show();
        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {
                final LoginDetailsAPI subCategoryAPI = Util.getClient().create(LoginDetailsAPI.class);
                Call<LoginDetails> getProf = subCategoryAPI.getLoginById(id);
                getProf.enqueue(new Callback<LoginDetails>() {
                    @Override
                    public void onResponse(Call<LoginDetails> call, Response<LoginDetails> response) {
                        if(dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }

                        if (response.code() == 200||response.code() == 201||response.code() == 204) {
                            System.out.println("Inside api");
                            final LoginDetails dto = response.body();
                            if(dto!=null){
                                try {
                                    LoginDetails loginDetails = dto;
                                    loginDetails.setTotalMeeting(status);
                                    updateLogin(loginDetails,statusText);
                                }
                                catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginDetails> call, Throwable t) {
                        if(dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                });
            }
        });
    }

    public void updateLogin(final LoginDetails loginDetails,final TextView statusText) {
        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage("Saving Details..");
        dialog.setCancelable(false);
        dialog.show();
        LoginDetailsAPI apiService = Util.getClient().create(LoginDetailsAPI.class);
        Call<LoginDetails> call = apiService.updateLoginById(loginDetails.getLoginDetailsId(),loginDetails);
        call.enqueue(new Callback<LoginDetails>() {
            @Override
            public void onResponse(Call<LoginDetails> call, Response<LoginDetails> response) {
                try {
                    if(dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201||response.code()==204) {
                        statusText.setText(loginDetails.getTotalMeeting()+"");
                        if(loginDetails.getTotalMeeting().equalsIgnoreCase("Present")){
                            statusText.setBackgroundColor(Color.parseColor("#00FF00"));
                        }else{
                            statusText.setBackgroundColor(Color.parseColor("#FF0000"));
                        }
                    }else {
                        Toast.makeText(context, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception ex) {
                    if(dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<LoginDetails> call, Throwable t) {
                if(dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }

                Toast.makeText( context , "Failed due to Bad Internet Connection" , Toast.LENGTH_SHORT ).show( );
                Log.e("TAG", t.toString());
            }
        });
    }

    public void addLogin(final LoginDetails loginDetails,final TextView loginId,final TextView status) {
        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage("Saving Details..");
        dialog.setCancelable(false);
        dialog.show();
        LoginDetailsAPI apiService = Util.getClient().create(LoginDetailsAPI.class);
        Call<LoginDetails> call = apiService.addLogin(loginDetails);
        call.enqueue(new Callback<LoginDetails>() {
            @Override
            public void onResponse(Call<LoginDetails> call, Response<LoginDetails> response) {
                try {
                    if(dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201) {
                        LoginDetails s = response.body();
                        if(s!=null){
                            status.setText("Present");
                            loginId.setText(""+s.getLoginDetailsId());
                            status.setBackgroundColor(Color.parseColor("#00FF00"));
                        }

                    }else {
                        Toast.makeText(context, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception ex) {
                    if(dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<LoginDetails> call, Throwable t) {
                if(dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                Toast.makeText( context , "Failed due to Bad Internet Connection" , Toast.LENGTH_SHORT ).show( );
                Log.e("TAG", t.toString());
            }
        });
    }
}