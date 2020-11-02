package app.zingo.employeemanagements.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;

import app.zingo.employeemanagements.Custom.MyTextView;
import app.zingo.employeemanagements.model.WorkingDay;
import app.zingo.employeemanagements.ui.Company.WorkingDaysScreen;
import app.zingo.employeemanagements.base.R;

public class ShiftAdapter extends RecyclerView.Adapter< ShiftAdapter.ViewHolder> {

    private Context context;
    private ArrayList< WorkingDay > timings;

    public ShiftAdapter(Context context, ArrayList< WorkingDay > timings)
    {
        this.context = context;
        this.timings = timings;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_shift_timing,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final WorkingDay timing = timings.get(position);

        if(timing!=null){

            holder.mText.setText("Shift "+(position+1));

            holder.mNavLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent timeIntent = new Intent(context, WorkingDaysScreen.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("Timing",timing);
                    timeIntent.putExtras(bundle);
                    ((Activity)context).startActivity(timeIntent);

                }
            });

        }


    }

    @Override
    public int getItemCount() {
        return timings.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        MyTextView mText;
        LinearLayout mNavLay;

        public ViewHolder(View itemView) {
            super(itemView);

            mText = itemView.findViewById(R.id.tv1_timing);
            mNavLay = itemView.findViewById(R.id.navigation_lay);

        }
    }


}

