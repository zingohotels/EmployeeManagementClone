package app.zingo.employeemanagements.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;

import app.zingo.employeemanagements.Custom.MyRegulerText;
import app.zingo.employeemanagements.Custom.MyTextView;
import app.zingo.employeemanagements.base.R;
import app.zingo.employeemanagements.model.OrganizationBreakTimes;

public class BreakAdapter  extends RecyclerView.Adapter< BreakAdapter.ViewHolder> {

    private Context context;
    private ArrayList< OrganizationBreakTimes > organizations;

    public BreakAdapter(Context context, ArrayList< OrganizationBreakTimes > organizations)
    {
        this.context = context;
        this.organizations = organizations;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate( R.layout.adapter_holiday_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final OrganizationBreakTimes organization = organizations.get(position);

        if(organization!=null){

            holder.mHoliday.setText(""+organization.getBreakName().toUpperCase());



            holder.mHolidayDate.setText(""+organization.getBreakStartTime()+" to "+organization.getBreakEndTime());

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

