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
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
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
import app.zingo.employeemanagements.Model.Employee;
import app.zingo.employeemanagements.Model.ExpenseAdminData;
import app.zingo.employeemanagements.Model.TaskAdminData;
import app.zingo.employeemanagements.Model.Tasks;
import app.zingo.employeemanagements.R;
import app.zingo.employeemanagements.UI.Common.ImageFullScreenActivity;
import app.zingo.employeemanagements.UI.NewAdminDesigns.UpdateExpenseScreen;
import app.zingo.employeemanagements.UI.NewAdminDesigns.UpdateTaskScreen;
import app.zingo.employeemanagements.Utils.PreferenceHandler;
import app.zingo.employeemanagements.Utils.ThreadExecuter;
import app.zingo.employeemanagements.Utils.Util;
import app.zingo.employeemanagements.WebApi.EmployeeApi;
import app.zingo.employeemanagements.WebApi.TasksAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.text.TextUtils.isEmpty;

public class ExpenseListDataAdapter extends RecyclerView.Adapter<ExpenseListDataAdapter.ViewHolder>{

    private Context context;
    private ArrayList<ExpenseAdminData> list;


    public ExpenseListDataAdapter(Context context, ArrayList<ExpenseAdminData> list) {

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
        final ExpenseAdminData dto = list.get(position);

        if(dto!=null){


            String status = dto.getExpenses().getStatus();


            holder.mTaskName.setText(dto.getExpenses().getExpenseTitle());
            holder.mExpAmt.setText("Amount: ₹ "+dto.getExpenses().getAmount());
            // holder.mTaskDesc.setText("Description: \n"+dto.getTaskDescription());

            String froms = dto.getExpenses().getDate();

            if(dto.getExpenses().getImageUrl()!=null&&!dto.getExpenses().getImageUrl().isEmpty()){
                holder.mAttach.setVisibility(View.VISIBLE);
            }else{
                holder.mAttach.setVisibility(View.GONE);
            }

            holder.mAttach.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(dto.getExpenses().getImageUrl()!=null&&!dto.getExpenses().getImageUrl().isEmpty()){

                        Intent img = new Intent(context,ImageFullScreenActivity.class);
                        img.putExtra("Image",dto.getExpenses().getImageUrl());
                        ((Activity)context).startActivity(img);


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

            String lngi = dto.getExpenses().getLongititude();
            String lati = dto.getExpenses().getLatitude();

            if(lngi!=null&&lati!=null){

                double lngiValue  = Double.parseDouble(lngi);
                double latiValue  = Double.parseDouble(lati);

                if(lngiValue!=0&&latiValue!=0){
                    // getAddress(lngiValue,latiValue,holder.mLocation);
                }
            }



            holder.mToAllocate.setText(""+dto.getEmployee().getEmployeeName());
           // getManagers(dto.getExpenses().getManagerId(),holder.mCreatedBy,"Manager");

            String claimed = "Claimed: ₹ "+dto.getExpenses().getClaimedAmount();

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



                            final MyRegulerText mEmpName = (MyRegulerText) views.findViewById(R.id.employee_name);
                            final MyRegulerText mPhone = (MyRegulerText) views.findViewById(R.id.call_employee);
                            final MyRegulerText mEmail = (MyRegulerText) views.findViewById(R.id.email_employee);

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
                                    emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, ""+dto.getExpenses().getExpenseTitle());
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

                        Intent updateSc = new Intent(context,UpdateExpenseScreen.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("Expenses",dto.getExpenses());
                        bundle.putInt("Position",position);
                        updateSc.putExtras(bundle);
                        ((Activity)context).startActivity(updateSc);

                    }else{


                    }

                }
            });


        }






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


                           /* if (progressDialog!=null)
                                progressDialog.dismiss();*/
                            ArrayList<Employee> list = response.body();


                            if (list !=null && list.size()!=0) {

                                final Employee employees = list.get(0);
                                if(employees!=null){
                                    try{

                                        if(type!=null&&!type.isEmpty()&&type.equalsIgnoreCase("Manager")){
                                            textView.setText(""+employees.getEmployeeName());
                                        }else if(type!=null&&!type.isEmpty()&&type.equalsIgnoreCase("Employee")){
                                            textView.setText(""+employees.getEmployeeName());
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

        MyRegulerText mToAllocate,mTaskName,mDuration,mCreatedBy,mExpAmt;
        View mStatus;
        TextView mStatusText;

        /*  public TextView mTaskName,mTaskDesc,mDuration,mDeadLine,mStatus,mCreatedBy,mLocation,mToAllocate;*/

        //   public LinearLayout mNotificationMain,mContact,mtaskUpdate;
        public LinearLayout mContact,mtaskUpdate,mAttach;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(true);

            mTaskName = (MyRegulerText)itemView.findViewById(R.id.title_task);
            mExpAmt = (MyRegulerText)itemView.findViewById(R.id.exp_amount);
            // mTaskDesc = (TextView)itemView.findViewById(R.id.title_description);
            mDuration = (MyRegulerText)itemView.findViewById(R.id.time_task);
            // mDeadLine = (TextView)itemView.findViewById(R.id.dead_line_task);
            mStatus = (View)itemView.findViewById(R.id.status);
            mStatusText = (TextView) itemView.findViewById(R.id.status_text);
            mCreatedBy = (MyRegulerText)itemView.findViewById(R.id.created_by);
            //  mLocation = (TextView)itemView.findViewById(R.id.task_location);
            mToAllocate = (MyRegulerText)itemView.findViewById(R.id.to_allocated);

            // mNotificationMain = (LinearLayout) itemView.findViewById(R.id.attendanceItem);
            mContact = (LinearLayout) itemView.findViewById(R.id.contact_employee);
            mtaskUpdate = (LinearLayout) itemView.findViewById(R.id.task_update);
            mAttach = (LinearLayout) itemView.findViewById(R.id.attach_image);


        }
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
