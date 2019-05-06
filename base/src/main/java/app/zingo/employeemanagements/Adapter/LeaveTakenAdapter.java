package app.zingo.employeemanagements.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import app.zingo.employeemanagements.Model.Employee;
import app.zingo.employeemanagements.Model.Leaves;
import app.zingo.employeemanagements.base.R;
import app.zingo.employeemanagements.UI.Admin.EmployeesDashBoard;

/**
 * Created by ZingoHotels Tech on 17-10-2018.
 */

public class LeaveTakenAdapter  extends RecyclerView.Adapter<LeaveTakenAdapter.ViewHolder>{

    private Context context;
    private ArrayList<Leaves> list;

    public LeaveTakenAdapter(Context context, ArrayList<Leaves> list) {

        this.context = context;
        this.list = list;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_leave_taken, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Leaves dto = list.get(position);


        if(dto!=null){

            Random rnd = new Random();
            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));


            holder.mView.setBackgroundColor(color);
            holder.mLeaveCount.setTextColor(color);
            holder.mLeaveCount.setText("Leave "+(position+1));

            String froms = dto.getFromDate();
            String tos = dto.getToDate();

            if(froms.contains("T")){

                String dojs[] = froms.split("T");

                try {
                    Date afromDate = new SimpleDateFormat("yyyy-MM-dd").parse(dojs[0]);
                    froms = new SimpleDateFormat("MMM dd,yyyy").format(afromDate);
                    holder.mFrom.setText(""+froms);

                } catch (ParseException e) {
                    e.printStackTrace();
                }


            }

            if(tos.contains("T")){

                String dojs[] = tos.split("T");

                try {
                    Date afromDate = new SimpleDateFormat("yyyy-MM-dd").parse(dojs[0]);
                    tos = new SimpleDateFormat("MMM dd,yyyy").format(afromDate);
                    holder.mTo.setText(""+tos);
                } catch (ParseException e) {
                    e.printStackTrace();
                }


            }


            holder.mLeaveType.setText(""+dto.getLeaveType());
            holder.mLeaveComment.setText(""+dto.getLeaveComment());

        }





    }




    @Override
    public int getItemCount() {
        return list.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder /*implements View.OnClickListener*/ {

        TextInputEditText mLeaveType,mFrom,mTo;
        EditText mLeaveComment;
        TextView mLeaveCount;

        public View mView;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(true);

            mLeaveType = itemView.findViewById(R.id.leave_type);
            mFrom = itemView.findViewById(R.id.from_date);
            mLeaveCount = itemView.findViewById(R.id.leave_count);
            mTo = itemView.findViewById(R.id.to_date);
            mLeaveComment = itemView.findViewById(R.id.leave_comment);

            mView = itemView.findViewById(R.id.view_color);


        }
    }
}
