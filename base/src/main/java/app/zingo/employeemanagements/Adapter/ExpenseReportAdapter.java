package app.zingo.employeemanagements.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import app.zingo.employeemanagements.Custom.MyRegulerText;
import app.zingo.employeemanagements.Model.Employee;
import app.zingo.employeemanagements.Model.Expenses;
import app.zingo.employeemanagements.Model.Tasks;
import app.zingo.employeemanagements.UI.Common.ImageFullScreenActivity;
import app.zingo.employeemanagements.UI.NewAdminDesigns.UpdateExpenseScreen;
import app.zingo.employeemanagements.Utils.PreferenceHandler;
import app.zingo.employeemanagements.base.R;
import app.zingo.employeemanagements.Utils.ThreadExecuter;
import app.zingo.employeemanagements.Utils.Util;
import app.zingo.employeemanagements.WebApi.EmployeeApi;
import app.zingo.employeemanagements.WebApi.ExpensesApi;
import app.zingo.employeemanagements.WebApi.TasksAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExpenseReportAdapter  extends RecyclerView.Adapter<ExpenseReportAdapter.ViewHolder>{

    private Context context;
    private ArrayList<Expenses> list;

    public ExpenseReportAdapter(Context context, ArrayList<Expenses> list) {

        this.context = context;
        this.list = list;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.expense_admin_adapter, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Expenses dto = list.get(position);


        if(dto!=null){




            String status = dto.getStatus();


            holder.mTaskName.setText(dto.getExpenseTitle());
            holder.mExpAmt.setText("Amount: ₹ "+dto.getAmount());
            // holder.mTaskDesc.setText("Description: \n"+dto.getTaskDescription());

            String froms = dto.getDate();

            if(dto.getImageUrl()!=null&&!dto.getImageUrl().isEmpty()){
                holder.mAttach.setVisibility(View.VISIBLE);
            }else{
                holder.mAttach.setVisibility(View.GONE);
            }

            holder.mAttach.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(dto.getImageUrl()!=null&&!dto.getImageUrl().isEmpty()){

                        Intent img = new Intent(context, ImageFullScreenActivity.class);
                        img.putExtra("Image",dto.getImageUrl());
                        context.startActivity(img);


                    }else{

                    }

                }
            });

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


            holder.mDuration.setText(froms+"");
            // holder.mDeadLine.setText(dto.getDeadLine());
            //holder.mStatus.setText(dto.getStatus());

            String lngi = dto.getLongititude();
            String lati = dto.getLatitude();

            if(lngi!=null&&lati!=null){

                double lngiValue  = Double.parseDouble(lngi);
                double latiValue  = Double.parseDouble(lati);

                if(lngiValue!=0&&latiValue!=0){
                    // getAddress(lngiValue,latiValue,holder.mLocation);
                }
            }


            getEmployee(dto.getEmployeeId(),holder.mToAllocate);

            //holder.mToAllocate.setText(""+dto.getEmployee().getEmployeeName());
            // getManagers(dto.getExpenses().getManagerId(),holder.mCreatedBy,"Manager");

            String claimed = "Claimed: ₹ "+dto.getClaimedAmount();

            SpannableString ss1=  new SpannableString(claimed);
            ss1.setSpan(new RelativeSizeSpan(1f), 11,claimed.length()-1, 0); // set size
            ss1.setSpan(new ForegroundColorSpan(Color.RED), 11, claimed.length()-1, 0);// set color


            holder.mCreatedBy.setText(ss1);
            // holder.mCreatedBy.setText(dto.getStatus());

            if(status.equalsIgnoreCase("Pending")){
                holder.mStatus.setBackgroundColor(Color.parseColor("#FFFF00"));
                holder.mStatusText.setBackground(context.getResources().getDrawable(R.drawable.oval_yellow));
                holder.mStatusText.setText("P");
            }else if(status.equalsIgnoreCase("Approved")){
                holder.mStatus.setBackgroundColor(Color.parseColor("#00FF00"));
                holder.mStatusText.setBackground(context.getResources().getDrawable(R.drawable.oval_green));
                holder.mStatusText.setText("A");
            }else if(status.equalsIgnoreCase("Rejected")){
                holder.mStatus.setBackgroundColor(Color.parseColor("#FF0000"));
                holder.mStatusText.setText("R");
                holder.mStatusText.setBackground(context.getResources().getDrawable(R.drawable.oval_red));
            }else{
                holder.mStatus.setBackgroundColor(Color.parseColor("#FF0000"));
                holder.mStatusText.setText("C");
                holder.mStatusText.setBackground(context.getResources().getDrawable(R.drawable.oval_red));
            }

            if(PreferenceHandler.getInstance(context).getUserRoleUniqueID()==2||PreferenceHandler.getInstance(context).getUserRoleUniqueID()==9){
                holder.mContact.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


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

                            final Employee employees = dto.getEmployee();

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
                                    emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, ""+dto.getExpenseTitle());
                                    emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");

                                    /* Send it off to the Activity-Chooser */
                                    context.startActivity(Intent.createChooser(emailIntent, "Send mail..."));

                                }
                            });

                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                });


            }else{
                holder.mContact.setVisibility(View.GONE);
            }


            holder.mtaskUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(PreferenceHandler.getInstance(context).getUserRoleUniqueID()==2||PreferenceHandler.getInstance(context).getUserRoleUniqueID()==9){

                        Intent updateSc = new Intent(context, UpdateExpenseScreen.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("Expenses",dto);
                        bundle.putInt("Position",position);
                        updateSc.putExtras(bundle);
                        context.startActivity(updateSc);

                    }else{

                        if(dto.getStatus()!=null&&!dto.getStatus().equalsIgnoreCase("Pending")){

                        }else{

                            Intent updateSc = new Intent(context,UpdateExpenseScreen.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("Expenses",dto);
                            bundle.putInt("Position",position);
                            updateSc.putExtras(bundle);
                            context.startActivity(updateSc);
                        }


                    }

                }
            });


        }





    }




    @Override
    public int getItemCount() {
        return list.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder /*implements View.OnClickListener*/ {


      /*  TextView mExpenseTitle,mExpAmt,mExpDate,mExpEmName,mstatus;
        ImageView mExpImg;
        LinearLayout mExpUpdate;

        public View mView;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(true);

            mExpenseTitle = itemView.findViewById(R.id.expense_title);
            mExpAmt = itemView.findViewById(R.id.expense_amount_report);
            mExpDate = itemView.findViewById(R.id.expense_date);
            mExpEmName = itemView.findViewById(R.id.employee_name);
            mstatus = itemView.findViewById(R.id.status_expense);
            mExpImg = itemView.findViewById(R.id.expense_image);
            mExpUpdate = itemView.findViewById(R.id.expense_update);





        }*/

        MyRegulerText mToAllocate,mTaskName,mDuration,mCreatedBy,mExpAmt;
        View mStatus;
        TextView mStatusText;

        /*  public TextView mTaskName,mTaskDesc,mDuration,mDeadLine,mStatus,mCreatedBy,mLocation,mToAllocate;*/

        //   public LinearLayout mNotificationMain,mContact,mtaskUpdate;
        public LinearLayout mContact,mtaskUpdate,mAttach;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(true);

            mTaskName = itemView.findViewById(R.id.title_task);
            mExpAmt = itemView.findViewById(R.id.exp_amount);
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
            mAttach = itemView.findViewById(R.id.attach_image);


        }
    }

    public void updateExpenses(final Expenses tasks, final AlertDialog dialogs) {



        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage("Saving Details..");
        dialog.setCancelable(false);
        dialog.show();

        ExpensesApi apiService = Util.getClient().create(ExpensesApi.class);

        Call<Expenses> call = apiService.updateExpenses(tasks.getExpenseId(),tasks);

        call.enqueue(new Callback<Expenses>() {
            @Override
            public void onResponse(Call<Expenses> call, Response<Expenses> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {
                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201|| statusCode == 204) {


                        Toast.makeText(context, "Update Expenses succesfully", Toast.LENGTH_SHORT).show();

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
            public void onFailure(Call<Expenses> call, Throwable t) {

                if(dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
                Toast.makeText(context, "Failed Due to "+t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());
            }
        });
    }

    private void getEmployee(final int id, final MyRegulerText textView){
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

                                        textView.setText("Created By "+employees.getEmployeeName());

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
