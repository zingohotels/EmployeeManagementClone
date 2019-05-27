package app.zingo.employeemanagements.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import app.zingo.employeemanagements.Model.Employee;
import app.zingo.employeemanagements.Model.LoginDetails;
import app.zingo.employeemanagements.UI.NewAdminDesigns.DailyTargetsForEmployeeActivity;
import app.zingo.employeemanagements.Utils.ThreadExecuter;
import app.zingo.employeemanagements.Utils.Util;
import app.zingo.employeemanagements.WebApi.LoginDetailsAPI;
import app.zingo.employeemanagements.base.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ZingoHotels Tech on 07-01-2019.
 */

public class TaskEmployeeListAdapter  extends RecyclerView.Adapter<TaskEmployeeListAdapter.ViewHolder>{

    private Context context;
    private ArrayList<Employee> list;


    public TaskEmployeeListAdapter(Context context, ArrayList<Employee> list) {

        this.context = context;
        this.list = list;


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_task_employees, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Employee dto = list.get(position);

        if(dto!=null){

            holder.mProfileName.setText(dto.getEmployeeName());

            if(dto.getEmployeeImages()!=null&&dto.getEmployeeImages().size()!=0){
                String base=dto.getEmployeeImages().get(0).getImage();
                if(base != null && !base.isEmpty()){
                    Picasso.with(context).load(base).placeholder(R.drawable.profile_image).
                            error(R.drawable.profile_image).into(holder.mProfileImage);


                }
            }

            LoginDetails loginDetails = new LoginDetails();
            loginDetails.setEmployeeId(dto.getEmployeeId());
            loginDetails.setLoginDate(new SimpleDateFormat("MM/dd/yyyy").format(new Date()));
            getLoginDetails(loginDetails,holder.numTargets);

            holder.mProfileMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    Intent intent = new Intent(context, DailyTargetsForEmployeeActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("Profile",list.get(position));
                    bundle.putInt("ProfileId",list.get(position).getEmployeeId());
                    intent.putExtras(bundle);
                    context.startActivity(intent);




                }
            });
        }






    }




    @Override
    public int getItemCount() {
        return list.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder /*implements View.OnClickListener*/ {

        public TextView mProfileName,numTargets;
        ImageView mProfileImage;
        public LinearLayout mProfileMain;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(true);

            mProfileName = itemView.findViewById(R.id.name);
            numTargets = itemView.findViewById(R.id.numTargets);
            mProfileImage = itemView.findViewById(R.id.profilePicture);
            mProfileMain = itemView.findViewById(R.id.attendanceItem);


        }
    }

    private void getLoginDetails(final LoginDetails loginDetails, final TextView employees){




        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {
                LoginDetailsAPI apiService = Util.getClient().create(LoginDetailsAPI.class);
                Call<ArrayList<LoginDetails>> call = apiService.getLoginByEmployeeIdAndDate(loginDetails);

                call.enqueue(new Callback<ArrayList<LoginDetails>>() {
                    @Override
                    public void onResponse(Call<ArrayList<LoginDetails>> call, Response<ArrayList<LoginDetails>> response) {
                        int statusCode = response.code();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {



                            ArrayList<LoginDetails> list = response.body();

                            if (list !=null && list.size()!=0) {

                                employees.setText("P");

                            }else{

                                employees.setText("A");
                                final int sdk = android.os.Build.VERSION.SDK_INT;
                                if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                                    employees.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.oval_red) );
                                } else {
                                    employees.setBackground(ContextCompat.getDrawable(context, R.drawable.oval_red));
                                }

                                // Toast.makeText(DailyTargetsForEmployeeActivity.this, "No Tasks given for this employee ", Toast.LENGTH_SHORT).show();

                            }

                        }else {



                            //Toast.makeText(DailyTargetsForEmployeeActivity.this, "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<LoginDetails>> call, Throwable t) {
                        // Log error here since request failed
                       /* if (progressDialog != null&&progressDialog.isShowing())
                            progressDialog.dismiss();*/
                        Log.e("TAG", t.toString());
                    }
                });
            }


        });
    }
}
