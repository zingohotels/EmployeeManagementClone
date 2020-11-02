package app.zingo.employeemanagements.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputEditText;
import androidx.recyclerview.widget.RecyclerView;
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

import app.zingo.employeemanagements.model.Leaves;
import app.zingo.employeemanagements.ui.NewAdminDesigns.UpdateLeaveScreen;
import app.zingo.employeemanagements.base.R;

/**
 * Created by ZingoHotels Tech on 10-01-2019.
 */

public class LeaveTakenEmployeeAdapter extends RecyclerView.Adapter< LeaveTakenEmployeeAdapter.ViewHolder>{

    private Context context;
    private ArrayList<Leaves> list;

    public LeaveTakenEmployeeAdapter(Context context, ArrayList<Leaves> list) {

        this.context = context;
        this.list = list;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_leave_employee, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Leaves dto = list.get(position);


        if(dto!=null){

            Random rnd = new Random();
            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

            holder.mUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    Intent intent = new Intent(context, UpdateLeaveScreen.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("LeaveId",list.get(position).getLeaveId());
                    bundle.putSerializable("Leaves",list.get(position));
                    intent.putExtras(bundle);
                    context.startActivity(intent);



                }
            });

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
            holder.mLeaveStatus.setText(""+dto.getStatus());
            holder.mLeaveComment.setText(""+dto.getLeaveComment());



        }





    }




    @Override
    public int getItemCount() {
        return list.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder /*implements View.OnClickListener*/ {

        TextInputEditText mLeaveType,mFrom,mTo,mLeaveStatus;
        EditText mLeaveComment;
        TextView mUpdate;
        TextView mLeaveCount;
        LinearLayout mRoot;

        public View mView;

        public ViewHolder(final View itemView) {
            super(itemView);
            itemView.setClickable(true);

            mRoot = itemView.findViewById(R.id.rootView);
            mLeaveType = itemView.findViewById(R.id.leave_type);
            mLeaveStatus = itemView.findViewById(R.id.leave_status);
            mFrom = itemView.findViewById(R.id.from_date);
            mLeaveCount = itemView.findViewById(R.id.leave_count);
            mUpdate = itemView.findViewById(R.id.leave_update);
            mTo = itemView.findViewById(R.id.to_date);
            mLeaveComment = itemView.findViewById(R.id.leave_comment);

            mView = itemView.findViewById(R.id.view_color);




        }
    }
}
