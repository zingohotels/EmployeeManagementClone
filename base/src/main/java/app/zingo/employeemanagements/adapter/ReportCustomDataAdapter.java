package app.zingo.employeemanagements.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import app.zingo.employeemanagements.model.ReportDataEmployee;
import app.zingo.employeemanagements.base.R;


public class ReportCustomDataAdapter extends RecyclerView.Adapter< ReportCustomDataAdapter.ViewHolder>{

    private Context context;
    private ArrayList< ReportDataEmployee > list;



    public ReportCustomDataAdapter(Context context, ArrayList< ReportDataEmployee > list) {

        this.context = context;
        this.list = list;



    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_custom_report, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final ReportDataEmployee dto = list.get(position);

        if(dto!=null){





            holder.mDate.setText(""+dto.getDate());
            holder.mName.setText(""+dto.getName());
            holder.mLogin.setText(""+dto.getLoginTime());
            holder.mLogout.setText(""+dto.getLogoutTime());
            holder.mHours.setText(""+dto.getHours());
            holder.mVisits.setText(""+dto.getVisits());
            holder.mTasks.setText(""+dto.getTasks());
            holder.mExpenses.setText(""+dto.getExpenses());
            holder.mExpAmt.setText(""+dto.getExpensesAmt());
            holder.mKm.setText(""+dto.getKms());


        }

    }




    @Override
    public int getItemCount() {
        return list.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder /*implements View.OnClickListener*/ {

        public TextView mName,mDate,mLogin,mLogout,mHours,mVisits,mTasks,mExpenses,mExpAmt,mKm;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(true);

            mDate = itemView.findViewById(R.id.report_date);
            mName = itemView.findViewById(R.id.report_name);
            mLogin = itemView.findViewById(R.id.report_login);
            mLogout = itemView.findViewById(R.id.report_logout);
            mHours = itemView.findViewById(R.id.report_hours);
            mVisits = itemView.findViewById(R.id.report_visit);
            mTasks = itemView.findViewById(R.id.report_task);
            mExpenses = itemView.findViewById(R.id.report_expense);
            mExpAmt = itemView.findViewById(R.id.report_expense_amount);
            mKm = itemView.findViewById(R.id.report_km);



        }
    }


}