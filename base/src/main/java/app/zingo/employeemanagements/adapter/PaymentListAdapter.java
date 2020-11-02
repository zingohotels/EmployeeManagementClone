package app.zingo.employeemanagements.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import app.zingo.employeemanagements.Custom.MyTextView;
import app.zingo.employeemanagements.model.OrganizationPayments;
import app.zingo.employeemanagements.model.Tasks;
import app.zingo.employeemanagements.utils.Util;
import app.zingo.employeemanagements.WebApi.TasksAPI;
import app.zingo.employeemanagements.base.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentListAdapter extends RecyclerView.Adapter< PaymentListAdapter.ViewHolder>{

    private Context context;
    private ArrayList< OrganizationPayments > list;


    public PaymentListAdapter(Context context, ArrayList< OrganizationPayments > list) {

        this.context = context;
        this.list = list;


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_organization_payment_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final OrganizationPayments dto = list.get(position);

        if(dto!=null){





            holder.mName.setText(dto.getTitle());
            holder.mAddress.setText(""+dto.getPaymentBy());

            String froms = dto.getPaymentDate();
            String tos = dto.getZingyPaymentDate();

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
            holder.mDuration.setText(froms+"");
            holder.mAmount.setText("Rs "+dto.getAmount());

            double amount = dto.getAmount();
            double commission = dto.getResellerCommissionPercentage();

            double value = amount * commission;
            double valuePer = value/100;
            holder.mCommissionPer.setText("Rs."+new DecimalFormat("#.##").format(valuePer));

            holder.mAppType.setText(dto.getZingyPaymentStatus());







        }






    }




    @Override
    public int getItemCount() {
        return list.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder /*implements View.OnClickListener*/ {

        public MyTextView mName,mAddress,mDuration,mAppType,mAmount,mCommissionPer;



        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(true);

            mName = itemView.findViewById(R.id.payment_name_reseller);
            mAddress = itemView.findViewById(R.id.organization_address_reseller);
            mDuration = itemView.findViewById(R.id.organization_time_reseller);
            mAmount = itemView.findViewById(R.id.organization_amoubt_reseller);
            mCommissionPer = itemView.findViewById(R.id.commision_price);

            mAppType = itemView.findViewById(R.id.status);




        }
    }

    public void updateTasks(final Tasks tasks, final AlertDialog dialogs) {



        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage("Saving Details..");
        dialog.setCancelable(false);
        dialog.show();

        TasksAPI apiService = Util.getClient().create( TasksAPI.class);

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
                //Toast.makeText(context, "Failed due to Bad Internet Connection", Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());
            }
        });



    }
}
