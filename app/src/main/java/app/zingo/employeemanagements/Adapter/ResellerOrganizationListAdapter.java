package app.zingo.employeemanagements.Adapter;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import app.zingo.employeemanagements.Custom.MyRegulerText;
import app.zingo.employeemanagements.Model.Employee;
import app.zingo.employeemanagements.Model.Organization;
import app.zingo.employeemanagements.Model.Tasks;
import app.zingo.employeemanagements.R;
import app.zingo.employeemanagements.Utils.PreferenceHandler;
import app.zingo.employeemanagements.Utils.ThreadExecuter;
import app.zingo.employeemanagements.Utils.Util;
import app.zingo.employeemanagements.WebApi.EmployeeApi;
import app.zingo.employeemanagements.WebApi.TasksAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResellerOrganizationListAdapter extends RecyclerView.Adapter<ResellerOrganizationListAdapter.ViewHolder>{

    private Context context;
    private ArrayList<Organization> list;


    public ResellerOrganizationListAdapter(Context context, ArrayList<Organization> list) {

        this.context = context;
        this.list = list;


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_organization_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Organization dto = list.get(position);

        if(dto!=null){


            String status = dto.getAppType();


            holder.mName.setText(dto.getOrganizationName());
            holder.mAddress.setText(""+dto.getAddress()+"\n"+dto.getCity()+"\n"+dto.getState());

            String froms = dto.getLicenseStartDate();
            String tos = dto.getLicenseEndDate();

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

            holder.mAppType.setText(dto.getAppType());







        }






    }




    @Override
    public int getItemCount() {
        return list.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder /*implements View.OnClickListener*/ {

        public TextView mName,mAddress,mDuration,mAppType;



        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(true);

            mName = (TextView)itemView.findViewById(R.id.organization_name_reseller);
            mAddress = (TextView)itemView.findViewById(R.id.organization_address_reseller);
            mDuration = (TextView)itemView.findViewById(R.id.organization_time_reseller);

            mAppType = (TextView)itemView.findViewById(R.id.status);




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
