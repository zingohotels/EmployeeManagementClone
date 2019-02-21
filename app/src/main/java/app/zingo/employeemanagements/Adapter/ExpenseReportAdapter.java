package app.zingo.employeemanagements.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import app.zingo.employeemanagements.Model.Employee;
import app.zingo.employeemanagements.Model.Expenses;
import app.zingo.employeemanagements.Model.Tasks;
import app.zingo.employeemanagements.R;
import app.zingo.employeemanagements.Utils.ThreadExecuter;
import app.zingo.employeemanagements.Utils.Util;
import app.zingo.employeemanagements.WebApi.EmployeeApi;
import app.zingo.employeemanagements.WebApi.ExpensesApi;
import app.zingo.employeemanagements.WebApi.TasksAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExpenseReportAdapter  extends RecyclerView.Adapter<ExpenseReportAdapter.ViewHolder>{

    private Context context;
    private ArrayList<Expenses> list;

    public ExpenseReportAdapter(Context context, ArrayList<Expenses> list) {

        this.context = context;
        this.list = list;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_expense_report_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Expenses dto = list.get(position);


        if(dto!=null){




            holder.mExpenseTitle.setText(""+dto.getExpenseTitle());
            //holder.mExpenseComment.setText(""+dto.getDescription());
            getEmployee(dto.getEmployeeId(),holder.mExpEmName);
            holder.mExpAmt.setText("Rs."+dto.getAmount());

            holder.mstatus.setText(""+dto.getStatus());

            String froms = dto.getDate();


            if(froms.contains("T")){

                String dojs[] = froms.split("T");

                try {
                    Date afromDate = new SimpleDateFormat("yyyy-MM-dd").parse(dojs[0]);
                    froms = new SimpleDateFormat("MMM dd,yyyy").format(afromDate);
                    holder.mExpDate.setText(""+froms);

                } catch (ParseException e) {
                    e.printStackTrace();
                }


            }

            String image = dto.getImageUrl();

            if(image!=null&&!image.isEmpty()){

                Picasso.with(context).load(image).placeholder(R.drawable.profile_image).
                        error(R.drawable.profile_image).into(holder.mExpImg);

            }

            holder.mExpUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    try{

                        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
                        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View views = inflater.inflate(R.layout.expense_update_dialog, null);

                        builder.setView(views);
                        String[] taskStatus = context.getResources().getStringArray(R.array.task_status);

                        final Spinner mTask = (Spinner) views.findViewById(R.id.task_status_update);
                        final Button mSave = (Button) views.findViewById(R.id.save);
                        final EditText desc = (EditText) views.findViewById(R.id.task_comments);

                        final android.support.v7.app.AlertDialog dialogs = builder.create();
                        dialogs.show();
                        dialogs.setCanceledOnTouchOutside(true);

                        if(dto.getStatus().equalsIgnoreCase("Pending")){

                            mTask.setSelection(0);
                        }else if(dto.getStatus().equalsIgnoreCase("Approved")){
                            mTask.setSelection(1);

                        }else if(dto.getStatus().equalsIgnoreCase("Rejected")){
                            mTask.setSelection(2);

                        }

                        desc.setText(""+dto.getDescription());



                        mSave.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Expenses tasks = dto;
                                tasks.setStatus(mTask.getSelectedItem().toString());
                                tasks.setDescription(desc.getText().toString());
                                try {
                                    updateExpenses(dto,dialogs);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        });











                    }catch (Exception e){
                        e.printStackTrace();
                    }


                }
            });



        }





    }




    @Override
    public int getItemCount() {
        return list.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder /*implements View.OnClickListener*/ {


        TextView mExpenseTitle,mExpAmt,mExpDate,mExpEmName,mstatus;
        ImageView mExpImg;
        LinearLayout mExpUpdate;

        public View mView;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(true);

            mExpenseTitle = (TextView) itemView.findViewById(R.id.expense_title);
            mExpAmt = (TextView) itemView.findViewById(R.id.expense_amount_report);
            mExpDate = (TextView) itemView.findViewById(R.id.expense_date);
            mExpEmName = (TextView) itemView.findViewById(R.id.employee_name);
            mstatus = (TextView) itemView.findViewById(R.id.status_expense);
            mExpImg = (ImageView) itemView.findViewById(R.id.expense_image);
            mExpUpdate = (LinearLayout) itemView.findViewById(R.id.expense_update);






        }
    }

    public void updateExpenses(final Expenses tasks, final AlertDialog dialogs) throws Exception{



        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage("Saving Details..");
        dialog.setCancelable(false);
        dialog.show();

        ExpensesApi apiService = Util.getClient().create(ExpensesApi.class);

        Call<Expenses> call = apiService.updateExpenses(tasks.getExpenseId(),tasks);

        call.enqueue(new Callback<Expenses>() {
            @Override
            public void onResponse(Call<Expenses> call, Response<Expenses> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {
                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201|| statusCode == 204) {


                        Toast.makeText(context, "Update Expenses succesfully", Toast.LENGTH_SHORT).show();

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
            public void onFailure(Call<Expenses> call, Throwable t) {

                if(dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
                Toast.makeText(context, "Failed Due to "+t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());
            }
        });



    }

    private void getEmployee(final int id, final TextView textView){




        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {
                EmployeeApi apiService = Util.getClient().create(EmployeeApi.class);
                Call<ArrayList<Employee>> call = apiService.getProfileById(id);

                call.enqueue(new Callback<ArrayList<Employee>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Employee>> call, Response<ArrayList<Employee>> response) {
                        int statusCode = response.code();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {


                           /* if (progressDialog!=null)
                                progressDialog.dismiss();*/
                            ArrayList<Employee> list = response.body();


                            if (list !=null && list.size()!=0) {

                                final Employee employees = list.get(0);
                                if(employees!=null){
                                    try{

                                        textView.setText("Created By "+employees.getEmployeeName());


                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }





                                //}

                            }else{

                            }

                        }else {



                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Employee>> call, Throwable t) {
                        // Log error here since request failed
                      /*  if (progressDialog!=null)
                            progressDialog.dismiss();*/

                        Log.e("TAG", t.toString());
                    }
                });
            }


        });
    }
}
