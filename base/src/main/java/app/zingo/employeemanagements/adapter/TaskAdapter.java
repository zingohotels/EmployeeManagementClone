package app.zingo.employeemanagements.adapter;

import android.content.Context;
import android.graphics.Color;
import com.google.android.material.textfield.TextInputEditText;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import app.zingo.employeemanagements.model.Tasks;
import app.zingo.employeemanagements.base.R;

/**
 * Created by ZingoHotels Tech on 05-01-2019.
 */

public class TaskAdapter   extends RecyclerView.Adapter< TaskAdapter.ViewHolder>{

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

            String froms = dto.getStartDate();
            String tos = dto.getEndDate();

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

           /* holder.mFrom.setText(""+dto.getStartDate());
            holder.mTo.setText(""+dto.getEndDate());*/
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

            mTaskName = itemView.findViewById(R.id.task_name);
            mFrom = itemView.findViewById(R.id.from_date);
            mTo = itemView.findViewById(R.id.to_date);
            mDead = itemView.findViewById(R.id.dead_line);
            mdesc = itemView.findViewById(R.id.task_description);

            mView = itemView.findViewById(R.id.view_color);



        }
    }
}
