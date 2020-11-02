package app.zingo.employeemanagements.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import app.zingo.employeemanagements.Custom.MyRegulerText;
import app.zingo.employeemanagements.model.Employee;
import app.zingo.employeemanagements.model.Leaves;
import app.zingo.employeemanagements.ui.NewAdminDesigns.UpdateLeaveScreen;
import app.zingo.employeemanagements.ui.newemployeedesign.UpdateWeekOff;
import app.zingo.employeemanagements.utils.PreferenceHandler;
import app.zingo.employeemanagements.utils.ThreadExecuter;
import app.zingo.employeemanagements.utils.Util;
import app.zingo.employeemanagements.WebApi.EmployeeApi;
import app.zingo.employeemanagements.base.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LeaveDashBoardAdapter extends RecyclerView.Adapter< LeaveDashBoardAdapter.ViewHolder>{

    private Context context;
    private ArrayList<Leaves> list;
    private String type;

    public LeaveDashBoardAdapter(Context context, ArrayList<Leaves> list,String type) {

        this.context = context;
        this.list = list;
        this.type = type;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_leave_dashboard, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Leaves dto = list.get(position);


        if(dto!=null){

            getEmployee(dto.getEmployeeId(),holder.mEmpName,"name");

            holder.mLeaveUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    if(type==null){


                        Intent intent = new Intent(context, UpdateLeaveScreen.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt("LeaveId",list.get(position).getLeaveId());
                        bundle.putSerializable("Leaves",list.get(position));
                        intent.putExtras(bundle);
                        context.startActivity(intent);


                    }else if(type!=null&&type.equalsIgnoreCase("WeekOff")){


                        Intent intent = new Intent(context, UpdateWeekOff.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt("LeaveId",list.get(position).getLeaveId());
                        bundle.putSerializable("Leaves",list.get(position));
                        intent.putExtras(bundle);
                        context.startActivity(intent);


                    }



                }
            });

            if(PreferenceHandler.getInstance(context).getUserRoleUniqueID()==2|| PreferenceHandler.getInstance(context).getUserRoleUniqueID()==9){
                holder.mEmpContact.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        getEmployee(dto.getEmployeeId(),holder.mEmpName,"contact");



                    }
                });


            }else{
                holder.mEmpContact.setVisibility(View.GONE);
            }


            String froms = dto.getFromDate();
            String tos = dto.getToDate();

            if(froms.contains("T")){

                String dojs[] = froms.split("T");

                try {
                    Date afromDate = new SimpleDateFormat("yyyy-MM-dd").parse(dojs[0]);
                    froms = new SimpleDateFormat("MMM dd,yyyy").format(afromDate);


                } catch (ParseException e) {
                    e.printStackTrace();
                }


            }

            if(tos.contains("T")){

                String dojs[] = tos.split("T");

                try {
                    Date afromDate = new SimpleDateFormat("yyyy-MM-dd").parse(dojs[0]);
                    tos = new SimpleDateFormat("MMM dd,yyyy").format(afromDate);

                } catch (ParseException e) {
                    e.printStackTrace();
                }


            }


            holder.mLeaveDuration.setText(froms+" to "+tos);
            holder.mLeaveDesc.setText(""+dto.getLeaveComment());

            if(dto.getStatus()!=null&&!dto.getStatus().isEmpty()){

                if(dto.getStatus().equalsIgnoreCase("Approved")){

                    holder.mStatusView.setBackgroundColor(Color.parseColor("#00FF00"));
                    holder.mLeaveStatus.setBackground(context.getResources().getDrawable(R.drawable.oval_green));
                    holder.mLeaveStatus.setText("A");


                }else if(dto.getStatus().equalsIgnoreCase("Rejected")){

                    holder.mStatusView.setBackgroundColor(Color.parseColor("#FF0000"));
                    holder.mLeaveStatus.setBackground(context.getResources().getDrawable(R.drawable.oval_red));
                    holder.mLeaveStatus.setText("R");


                }else if(dto.getStatus().equalsIgnoreCase("Pending")){

                    holder.mStatusView.setBackgroundColor(Color.parseColor("#FFFF00"));
                    holder.mLeaveStatus.setBackground(context.getResources().getDrawable(R.drawable.oval_yellow));
                    holder.mLeaveStatus.setText("P");

                }
            }else{
                holder.mStatusView.setBackgroundColor(Color.parseColor("#FFFF00"));
                holder.mLeaveStatus.setBackground(context.getResources().getDrawable(R.drawable.oval_yellow));
                holder.mLeaveStatus.setText("P");
            }

            if(dto.getLeaveType()!=null&&!dto.getLeaveType().isEmpty()){

                if(dto.getLeaveType().equalsIgnoreCase("Paid")){

                    holder.mLeaveType.setText("Type: "+dto.getLeaveType());

                }else if(dto.getLeaveType().equalsIgnoreCase("Un Paid")){

                    holder.mLeaveType.setText("Type: "+dto.getLeaveType());

                }else{
                    holder.mLeaveType.setText(" Type: Pending");
                }
            }else{

                holder.mLeaveType.setText("Type: Pending");
            }

        }


    }




    @Override
    public int getItemCount() {
        return list.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder{

        public MyRegulerText mEmpName,mLeaveDuration,mLeaveDesc,mLeaveType;
        public TextView mLeaveStatus;
        public LinearLayout mLeaveUpdate,mEmpContact;
        public View mStatusView;

        public ViewHolder(final View itemView) {
            super(itemView);
            itemView.setClickable(true);

            mEmpName = itemView.findViewById(R.id.from_empl);
            mLeaveDuration = itemView.findViewById(R.id.time_leave);
            mLeaveDesc = itemView.findViewById(R.id.leave_desc);
            mLeaveType = itemView.findViewById(R.id.leave_type);
            mLeaveStatus = itemView.findViewById(R.id.status_text);
            mLeaveUpdate = itemView.findViewById(R.id.leave_update);
            mEmpContact = itemView.findViewById(R.id.contact_employee);
            mStatusView = itemView.findViewById(R.id.status);

        }
    }

    private void getEmployee( final int id, final MyRegulerText textView, final String type){



        new ThreadExecuter ().execute( new Runnable() {
            @Override
            public void run() {
                EmployeeApi apiService = Util.getClient().create( EmployeeApi.class);
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

                                        if(type.equalsIgnoreCase("name")){
                                            textView.setText(""+employees.getEmployeeName());
                                        }else if(type.equalsIgnoreCase("contact")){

                                            try{


                                                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
                                                LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                                View views = inflater.inflate(R.layout.alert_contact_employee, null);

                                                builder.setView(views);



                                                final MyRegulerText mEmpName = views.findViewById(R.id.employee_name);
                                                final MyRegulerText mPhone = views.findViewById(R.id.call_employee);
                                                final MyRegulerText mEmail = views.findViewById(R.id.email_employee);

                                                final androidx.appcompat.app.AlertDialog dialogs = builder.create();
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

                                                        final Intent emailIntent = new Intent(Intent.ACTION_SEND);

                                                        /* Fill it with Data */
                                                        emailIntent.setType("plain/text");
                                                        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{""+employees.getPrimaryEmailAddress()});
                                                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Leave Acknowldgement/Details");
                                                        emailIntent.putExtra(Intent.EXTRA_TEXT, "");

                                                        /* Send it off to the Activity-Chooser */
                                                        context.startActivity(Intent.createChooser(emailIntent, "Send mail..."));

                                                    }
                                                });









                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }

                                        }


                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }


                            }else{

                            }

                        }else {



                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Employee>> call, Throwable t) {


                        Log.e("TAG", t.toString());
                    }
                });
            }


        });
    }

}
