package app.zingo.employeemanagements.Adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
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



import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import app.zingo.employeemanagements.Custom.MyRegulerText;
import app.zingo.employeemanagements.Custom.MyTextView;
import app.zingo.employeemanagements.Model.Employee;
import app.zingo.employeemanagements.Model.Leaves;
import app.zingo.employeemanagements.Model.LoginDetails;
import app.zingo.employeemanagements.Model.LoginDetailsNotificationManagers;
import app.zingo.employeemanagements.Model.Tasks;
import app.zingo.employeemanagements.base.R;
import app.zingo.employeemanagements.UI.NewAdminDesigns.AdminNewMainScreen;
import app.zingo.employeemanagements.UI.NewAdminDesigns.UpdateLeaveScreen;
import app.zingo.employeemanagements.UI.NewAdminDesigns.UpdateTaskScreen;
import app.zingo.employeemanagements.Utils.PreferenceHandler;
import app.zingo.employeemanagements.Utils.ThreadExecuter;
import app.zingo.employeemanagements.Utils.Util;
import app.zingo.employeemanagements.WebApi.EmployeeApi;
import app.zingo.employeemanagements.WebApi.LeaveAPI;
import app.zingo.employeemanagements.WebApi.TasksAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.text.TextUtils.isEmpty;

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
                .inflate(R.layout.task_design_adapter, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Tasks dto = list.get(position);

        if(dto!=null){


            String status = dto.getStatus();


            holder.mTaskName.setText(dto.getTaskName());
           // holder.mTaskDesc.setText("Description: \n"+dto.getTaskDescription());

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
                            froms = new SimpleDateFormat("dd MMM yyyy HH:mm").format(afromDate);

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
                            tos = new SimpleDateFormat("dd MMM yyyy HH:mm").format(atoDate);

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }


                                              /*  String parse = new SimpleDateFormat("MMM yyyy").format(atoDate);
                                                toDate = new SimpleDateFormat("MMM yyyy").parse(parse);*/

                }

            }
            holder.mDuration.setText(froms+" - "+tos);
           // holder.mDeadLine.setText(dto.getDeadLine());
            //holder.mStatus.setText(dto.getStatus());

            String lngi = dto.getLongitude();
            String lati = dto.getLatitude();

            if(lngi!=null&&lati!=null){

                double lngiValue  = Double.parseDouble(lngi);
                double latiValue  = Double.parseDouble(lati);

                if(lngiValue!=0&&latiValue!=0){
                   // getAddress(lngiValue,latiValue,holder.mLocation);
                }
            }


            getManagers(dto.getEmployeeId(),holder.mToAllocate,"Employee");
            getManagers(dto.getToReportEmployeeId(),holder.mCreatedBy,"Manager");
           // holder.mCreatedBy.setText(dto.getStatus());

            if(status.equalsIgnoreCase("Pending")){
                holder.mStatus.setBackgroundColor(Color.parseColor("#FF0000"));
                holder.mStatusText.setBackground(context.getResources().getDrawable(R.drawable.oval_red));
                holder.mStatusText.setText("P");
            }else if(status.equalsIgnoreCase("Completed")){
                holder.mStatus.setBackgroundColor(Color.parseColor("#00FF00"));
                holder.mStatusText.setBackground(context.getResources().getDrawable(R.drawable.oval_green));
                holder.mStatusText.setText("Co");
            }else if(status.equalsIgnoreCase("Closed")){
                holder.mStatus.setBackgroundColor(Color.parseColor("#FFFF00"));
                holder.mStatusText.setText("Cl");
                holder.mStatusText.setBackground(context.getResources().getDrawable(R.drawable.oval_yellow));
            }else if(status.equalsIgnoreCase("On-Going")){
                holder.mStatus.setBackgroundColor(Color.parseColor("#D81B60"));
                holder.mStatusText.setText("On");
                holder.mStatusText.setBackground(context.getResources().getDrawable(R.drawable.oval_pink));
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


                    Intent updateSc = new Intent(context,UpdateTaskScreen.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("Task",dto);
                    updateSc.putExtras(bundle);
                    context.startActivity(updateSc);
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


                           /* if (progressDialog != null&&progressDialog.isShowing())
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



                                        final MyRegulerText mEmpName = views.findViewById(R.id.employee_name);
                                        final MyRegulerText mPhone = views.findViewById(R.id.call_employee);
                                        final MyRegulerText mEmail = views.findViewById(R.id.email_employee);

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
                      /*  if (progressDialog != null&&progressDialog.isShowing())
                            progressDialog.dismiss();*/

                        Log.e("TAG", t.toString());
                    }
                });
            }


        });
    }
    private void getManagers(final int id, final MyRegulerText textView,final String type){




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


                           /* if (progressDialog != null&&progressDialog.isShowing())
                                progressDialog.dismiss();*/
                            ArrayList<Employee> list = response.body();


                            if (list !=null && list.size()!=0) {

                                final Employee employees = list.get(0);
                                if(employees!=null){
                                    try{

                                        if(type!=null&&!type.isEmpty()&&type.equalsIgnoreCase("Manager")){
                                            textView.setText(""+employees.getEmployeeName());
                                        }else if(type!=null&&!type.isEmpty()&&type.equalsIgnoreCase("Employee")){
                                            textView.setText("To "+employees.getEmployeeName());
                                        }




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
                      /*  if (progressDialog != null&&progressDialog.isShowing())
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

        MyRegulerText mToAllocate,mTaskName,mDuration,mCreatedBy;
        View mStatus;
        TextView mStatusText;

      /*  public TextView mTaskName,mTaskDesc,mDuration,mDeadLine,mStatus,mCreatedBy,mLocation,mToAllocate;*/

     //   public LinearLayout mNotificationMain,mContact,mtaskUpdate;
        public LinearLayout mContact,mtaskUpdate;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(true);

            mTaskName = itemView.findViewById(R.id.title_task);
           // mTaskDesc = (TextView)itemView.findViewById(R.id.title_description);
            mDuration = itemView.findViewById(R.id.time_task);
           // mDeadLine = (TextView)itemView.findViewById(R.id.dead_line_task);
            mStatus = itemView.findViewById(R.id.status);
            mStatusText = itemView.findViewById(R.id.status_text);
            mCreatedBy = itemView.findViewById(R.id.created_by);
          //  mLocation = (TextView)itemView.findViewById(R.id.task_location);
            mToAllocate = itemView.findViewById(R.id.to_allocated);

           // mNotificationMain = (LinearLayout) itemView.findViewById(R.id.attendanceItem);
            mContact = itemView.findViewById(R.id.contact_employee);
            mtaskUpdate = itemView.findViewById(R.id.task_update);


        }
    }

    public void updateTasks(final Tasks tasks, final AlertDialog dialogs) {



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

    public void getAddress(final double longitude,final double latitude,final TextView textView )
    {

        try
        {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(context, Locale.ENGLISH);


            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();



            System.out.println("address = "+address);

            String currentLocation;

            if(!isEmpty(address))
            {
                currentLocation=address;
                textView.setText(currentLocation);

            }
            else
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();


        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
