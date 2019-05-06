package app.zingo.employeemanagements.Adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import app.zingo.employeemanagements.Custom.MyRegulerText;
import app.zingo.employeemanagements.Custom.MyTextView;
import app.zingo.employeemanagements.Model.Employee;
import app.zingo.employeemanagements.Model.HolidayList;
import app.zingo.employeemanagements.Model.Organization;
import app.zingo.employeemanagements.base.R;
import app.zingo.employeemanagements.UI.NewAdminDesigns.BranchOptionScreen;
import app.zingo.employeemanagements.Utils.PreferenceHandler;
import app.zingo.employeemanagements.Utils.ThreadExecuter;
import app.zingo.employeemanagements.Utils.Util;
import app.zingo.employeemanagements.WebApi.EmployeeApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HolidayListAdapter extends RecyclerView.Adapter<HolidayListAdapter.ViewHolder> {

    private Context context;
    private ArrayList<HolidayList> organizations;

    public HolidayListAdapter(Context context, ArrayList<HolidayList> organizations)
    {
        this.context = context;
        this.organizations = organizations;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_holiday_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final HolidayList organization = organizations.get(position);

        if(organization!=null){

            holder.mHoliday.setText(""+organization.getHolidayDescription().toUpperCase());

            String date = organization.getHolidayDate();

            if(date.contains("T")){

                String dojs[] = date.split("T");

                try {
                    Date afromDate = new SimpleDateFormat("yyyy-MM-dd").parse(dojs[0]);
                    date = new SimpleDateFormat("MMM dd,yyyy").format(afromDate);


                } catch (ParseException e) {
                    e.printStackTrace();
                }


            }

            holder.mHolidayDate.setText(""+date+" "+organization.getHolidayDay());

            holder.mBranch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                  /*  Intent branch = new Intent(context,BranchOptionScreen.class);
                    PreferenceHandler.getInstance(context).setBranchId(organization.getOrganizationId());
                    ((Activity)context).startActivity(branch);*/

                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return organizations.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        MyTextView mHoliday;
        MyRegulerText mHolidayDate;
        LinearLayout mBranch;

        public ViewHolder(View itemView) {
            super(itemView);

            mHoliday = itemView.findViewById(R.id.holiday_name_adapter);
            mHolidayDate = itemView.findViewById(R.id.holiday_date);
            mBranch = itemView.findViewById(R.id.branch_click_layout);


        }
    }


}

