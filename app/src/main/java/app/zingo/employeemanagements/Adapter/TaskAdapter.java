package app.zingo.employeemanagements.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

import app.zingo.employeemanagements.Model.Leaves;
import app.zingo.employeemanagements.Model.Tasks;
import app.zingo.employeemanagements.R;

/**
 * Created by ZingoHotels Tech on 05-01-2019.
 */

public class TaskAdapter   extends RecyclerView.Adapter<TaskAdapter.ViewHolder>{

    private Context context;
    private ArrayList<Tasks> list;

    public TaskAdapter(Context context, ArrayList<Tasks> list) {

        this.context = context;
        this.list = list;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_task_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Tasks dto = list.get(position);


        if(dto!=null){

            Random rnd = new Random();
            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

            holder.mView.setBackgroundColor(color);

            holder.mFrom.setText(""+dto.getStartDate());
            holder.mTo.setText(""+dto.getEndDate());
            holder.mDead.setText(""+dto.getDeadLine());
            holder.mTaskName.setText(""+dto.getTaskName());
            holder.mdesc.setText(""+dto.getTaskDescription());


        }





    }




    @Override
    public int getItemCount() {
        return list.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder /*implements View.OnClickListener*/ {

        TextInputEditText mTaskName,mFrom,mTo,mDead;
        EditText mdesc;

        public View mView;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(true);

            mTaskName = (TextInputEditText)itemView.findViewById(R.id.task_name);
            mFrom = (TextInputEditText)itemView.findViewById(R.id.from_date);
            mTo = (TextInputEditText)itemView.findViewById(R.id.to_date);
            mDead = (TextInputEditText)itemView.findViewById(R.id.dead_line);
            mdesc = (EditText)itemView.findViewById(R.id.task_description);

            mView = (View) itemView.findViewById(R.id.view_color);



        }
    }
}
