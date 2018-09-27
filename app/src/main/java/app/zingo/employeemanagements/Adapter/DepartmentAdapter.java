package app.zingo.employeemanagements.Adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import app.zingo.employeemanagements.Model.Departments;
import app.zingo.employeemanagements.R;

/**
 * Created by ZingoHotels Tech on 27-09-2018.
 */

public class DepartmentAdapter extends RecyclerView.Adapter<DepartmentAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Departments> departments;

    public DepartmentAdapter(Context context, ArrayList<Departments> departments)
    {
        this.context = context;
        this.departments = departments;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_department_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        Departments department = departments.get(position);

        if(department!=null){

            holder.mDepartmentName.setText(""+department.getDepartmentName());
            holder.mLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return departments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView mDepartmentName;
        CardView mLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            mDepartmentName = (TextView)itemView.findViewById(R.id.department_name);
            mLayout = (CardView) itemView.findViewById(R.id.department);

        }
    }
}

