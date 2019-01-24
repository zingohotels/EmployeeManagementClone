package app.zingo.employeemanagements.Adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.gcm.Task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import app.zingo.employeemanagements.Custom.MyRegulerText;
import app.zingo.employeemanagements.Custom.MyTextView;
import app.zingo.employeemanagements.Model.Employee;
import app.zingo.employeemanagements.Model.Leaves;
import app.zingo.employeemanagements.Model.LoginDetails;
import app.zingo.employeemanagements.Model.LoginDetailsNotificationManagers;
import app.zingo.employeemanagements.Model.Tasks;
import app.zingo.employeemanagements.R;
import app.zingo.employeemanagements.UI.NewAdminDesigns.AdminNewMainScreen;
import app.zingo.employeemanagements.UI.NewAdminDesigns.UpdateLeaveScreen;
import app.zingo.employeemanagements.Utils.PreferenceHandler;
import app.zingo.employeemanagements.Utils.ThreadExecuter;
import app.zingo.employeemanagements.Utils.Util;
import app.zingo.employeemanagements.WebApi.EmployeeApi;
import app.zingo.employeemanagements.WebApi.LeaveAPI;
import app.zingo.employeemanagements.WebApi.TasksAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ZingoHotels Tech on 07-01-2019.
 */

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.ViewHolder>{

    private Context context;
    private ArrayList<Tasks> list;


    public TaskListAdapter(Context context, ArrayList<Tasks> list) {

        this.context = context;
        this.list = list;


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_task_list_employee, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Tasks dto = list.get(position);

        if(dto!=null){


            String status = dto.getStatus();


            holder.mTaskName.setText(dto.getTaskName());
            holder.mTaskDesc.setText("Description: \n"+dto.getTaskDescription());

            String froms = dto.getStartDate();
            String tos = dto.getEndDate();

            Date afromDate = null;
            Date atoDate = null;

            if(froms!=null&&!froms.isEmpty()){

                if(froms.contains("T")){

                    String dojs[] = froms.split("T");

                    if(dojs[1].equalsIgnoreCase("00:00:00")){
                        try {
                            afromDate = new SimpleDateFormat("yyyy-MM-dd").parse(dojs[0]);
                            froms = new SimpleDateFormat("dd MMM yyyy").format(afromDate);

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }else{
                        try {
                            afromDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dojs[0]+" "+dojs[1]);
                            froms = new SimpleDateFormat("dd MMM yyyy HH:mm:ss").format(afromDate);

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }




                }

            }

            if(tos!=null&&!tos.isEmpty()){

                if(tos.contains("T")){

                    String dojs[] = tos.split("T");

                    if(dojs[1].equalsIgnoreCase("00:00:00")){
                        try {
                            atoDate = new SimpleDateFormat("yyyy-MM-dd").parse(dojs[0]);
                            tos = new SimpleDateFormat("dd MMM yyyy").format(atoDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }else{
                        try {
                            atoDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dojs[0]+" "+dojs[1]);
                            tos = new SimpleDateFormat("dd MMM yyyy HH:mm:ss").format(atoDate);

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }


                                              /*  String parse = new SimpleDateFormat("MMM yyyy").format(atoDate);
                                                toDate = new SimpleDateFormat("MMM yyyy").parse(parse);*/

                }

            }
            holder.mDuration.setText(froms+" to "+tos);
            holder.mDeadLine.setText(dto.getDeadLine());
            holder.mStatus.setText(dto.getStatus());

            getManagers(dto.getToReportEmployeeId(),holder.mCreatedBy);
           // holder.mCreatedBy.setText(dto.getStatus());

            if(status.equalsIgnoreCase("Pending")){
                holder.mStatus.setBackgroundColor(Color.parseColor("#FF0000"));
            }else if(status.equalsIgnoreCase("Completed")){
                holder.mStatus.setBackgroundColor(Color.parseColor("#00FF00"));
            }else if(status.equalsIgnoreCase("Closed")){
                holder.mStatus.setBackgroundColor(Color.parseColor("#FFFF00"));
            }else if(status.equalsIgnoreCase("On-Going")){
                holder.mStatus.setBackgroundColor(Color.parseColor("#D81B60"));
            }

            if(PreferenceHandler.getInstance(context).getUserRoleUniqueID()==2){
                holder.mContact.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {



                        getEmployees(dto.getEmployeeId(),dto);

                    }
                });


            }else{
                holder.mContact.setVisibility(View.GONE);
            }

            holder.mtaskUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    try{

                        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
                        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View views = inflater.inflate(R.layout.alert_task_update, null);

                        builder.setView(views);
                        String[] taskStatus = context.getResources().getStringArray(R.array.task_status);





                        final Spinner mTask = (Spinner) views.findViewById(R.id.task_status_update);
                        final Button mSave = (Button) views.findViewById(R.id.save);
                        final EditText desc = (EditText) views.findViewById(R.id.task_comments);

                        final android.support.v7.app.AlertDialog dialogs = builder.create();
                        dialogs.show();
                        dialogs.setCanceledOnTouchOutside(true);

                        if(dto.getStatus().equalsIgnoreCase("Pending")){

                            mTask.setSelection(0);
                        }else if(dto.getStatus().equalsIgnoreCase("On-Going")){
                            mTask.setSelection(1);

                        }else if(dto.getStatus().equalsIgnoreCase("Completed")){
                            mTask.setSelection(2);

                        }else if(dto.getStatus().equalsIgnoreCase("Closed")){
                            mTask.setSelection(3);

                        }



                        mSave.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Tasks tasks = dto;
                                tasks.setStatus(mTask.getSelectedItem().toString());
                                tasks.setRemarks(desc.getText().toString());
                                try {
                                    updateTasks(dto,dialogs);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        });











                    }catch (Exception e){
                        e.printStackTrace();
                    }


                }
            });

        }






    }

    private void getEmployees(final int id, final Tasks dto){




        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {
                EmployeeApi apiService = Util.getClient().create(EmployeeApi.class);
                Call<ArrayList<Employee>> call = apiService.getProfileById(id);

                call.enqueue(new Callback<ArrayList<Employee>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Employee>> call, Response<ArrayList<Employee>> response) {
                        int statusCode = response.code();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {


                           /* if (progressDialog!=null)
                                progressDialog.dismiss();*/
                            ArrayList<Employee> list = response.body();


                            if (list !=null && list.size()!=0) {

                                final Employee employees = list.get(0);
                                if(employees!=null){
                                    try{

                                        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
                                        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                        View views = inflater.inflate(R.layout.alert_contact_employee, null);

                                        builder.setView(views);



                                        final MyRegulerText mEmpName = (MyRegulerText) views.findViewById(R.id.employee_name);
                                        final MyRegulerText mPhone = (MyRegulerText) views.findViewById(R.id.call_employee);
                                        final MyRegulerText mEmail = (MyRegulerText) views.findViewById(R.id.email_employee);

                                        final android.support.v7.app.AlertDialog dialogs = builder.create();
                                        dialogs.show();
                                        dialogs.setCanceledOnTouchOutside(true);


                                       mEmpName.setText("Contact "+employees.getEmployeeName());
                                        mPhone.setText("Call "+employees.getPhoneNumber());
                                        mEmail.setText("Email "+employees.getPrimaryEmailAddress());


                                        mPhone.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                                intent.setData(Uri.parse("tel:"+employees.getPhoneNumber()));
                                                context.startActivity(intent);
                                            }
                                        });

                                        mEmail.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

                                                /* Fill it with Data */
                                                emailIntent.setType("plain/text");
                                                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{""+employees.getPrimaryEmailAddress()});
                                                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, ""+dto.getTaskName());
                                                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");

                                                /* Send it off to the Activity-Chooser */
                                                context.startActivity(Intent.createChooser(emailIntent, "Send mail..."));

                                            }
                                        });









                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }





                                //}

                            }else{

                            }

                        }else {



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
    private void getManagers(final int id, final TextView textView){




        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {
                EmployeeApi apiService = Util.getClient().create(EmployeeApi.class);
                Call<ArrayList<Employee>> call = apiService.getProfileById(id);

                call.enqueue(new Callback<ArrayList<Employee>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Employee>> call, Response<ArrayList<Employee>> response) {
                        int statusCode = response.code();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {


                           /* if (progressDialog!=null)
                                progressDialog.dismiss();*/
                            ArrayList<Employee> list = response.body();


                            if (list !=null && list.size()!=0) {

                                final Employee employees = list.get(0);
                                if(employees!=null){
                                    try{

                                        textView.setText("Created By"+employees.getEmployeeName());


                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }





                                //}

                            }else{

                            }

                        }else {



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


    @Override
    public int getItemCount() {
        return list.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder /*implements View.OnClickListener*/ {

        public TextView mTaskName,mTaskDesc,mDuration,mDeadLine,mStatus,mCreatedBy;

        public LinearLayout mNotificationMain,mContact,mtaskUpdate;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(true);

            mTaskName = (TextView)itemView.findViewById(R.id.title_task);
            mTaskDesc = (TextView)itemView.findViewById(R.id.title_description);
            mDuration = (TextView)itemView.findViewById(R.id.time_task);
            mDeadLine = (TextView)itemView.findViewById(R.id.dead_line_task);
            mStatus = (TextView)itemView.findViewById(R.id.status);
            mCreatedBy = (TextView)itemView.findViewById(R.id.created_by);

            mNotificationMain = (LinearLayout) itemView.findViewById(R.id.attendanceItem);
            mContact = (LinearLayout) itemView.findViewById(R.id.contact_employee);
            mtaskUpdate = (LinearLayout) itemView.findViewById(R.id.task_update);


        }
    }

    public void updateTasks(final Tasks tasks, final AlertDialog dialogs) throws Exception{



        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage("Saving Details..");
        dialog.setCancelable(false);
        dialog.show();

        TasksAPI apiService = Util.getClient().create(TasksAPI.class);

        Call<Tasks> call = apiService.updateTasks(tasks.getTaskId(),tasks);

        call.enqueue(new Callback<Tasks>() {
            @Override
            public void onResponse(Call<Tasks> call, Response<Tasks> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {
                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201|| statusCode == 204) {


                        Toast.makeText(context, "Update Task succesfully", Toast.LENGTH_SHORT).show();

                        dialogs.dismiss();

                    }else {
                        Toast.makeText(context, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
            public void onFailure(Call<Tasks> call, Throwable t) {

                if(dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
                Toast.makeText(context, "Failed Due to "+t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());
            }
        });



    }
}
