package app.zingo.employeemanagements.UI.Employee;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import app.zingo.employeemanagements.Adapter.LeaveTakenAdapter;
import app.zingo.employeemanagements.Adapter.LoginDetailsAdapter;
import app.zingo.employeemanagements.Model.Leaves;
import app.zingo.employeemanagements.Model.LoginDetails;
import app.zingo.employeemanagements.base.R;
import app.zingo.employeemanagements.UI.Admin.EmployeesDashBoard;
import app.zingo.employeemanagements.Utils.ThreadExecuter;
import app.zingo.employeemanagements.Utils.Util;
import app.zingo.employeemanagements.WebApi.LeaveAPI;
import app.zingo.employeemanagements.WebApi.LoginDetailsAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LeaveTakenDays extends AppCompatActivity {

    RecyclerView mLeaveList;

    int employeeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
            setContentView(R.layout.activity_leave_taken_days);

            mLeaveList = findViewById(R.id.leave_list);

            Bundle bundle = getIntent().getExtras();

            if(bundle!=null){

                employeeId = bundle.getInt("EmployeeId");
            }



            if(employeeId!=0){
                getLeaveDetails(employeeId);
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }


    private void getLeaveDetails(final int employeeId){


        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading Details..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {
                LeaveAPI apiService = Util.getClient().create(LeaveAPI.class);
                Call<ArrayList<Leaves>> call = apiService.getLeavesByEmployeeId(employeeId);

                call.enqueue(new Callback<ArrayList<Leaves>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Leaves>> call, Response<ArrayList<Leaves>> response) {
                        int statusCode = response.code();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {


                            if (progressDialog != null&&progressDialog.isShowing())
                                progressDialog.dismiss();
                            ArrayList<Leaves> list = response.body();



                            if (list !=null && list.size()!=0) {

                                LeaveTakenAdapter adapter = new LeaveTakenAdapter(LeaveTakenDays.this,list);
                                mLeaveList.setAdapter(adapter);

                            }else{

                                Toast.makeText(LeaveTakenDays.this, "No leaves ", Toast.LENGTH_SHORT).show();

                            }

                        }else {

                            if (progressDialog != null&&progressDialog.isShowing())
                                progressDialog.dismiss();

                            Toast.makeText(LeaveTakenDays.this, "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Leaves>> call, Throwable t) {
                        // Log error here since request failed
                        if (progressDialog != null&&progressDialog.isShowing())
                            progressDialog.dismiss();
                        Log.e("TAG", t.toString());
                    }
                });
            }


        });
    }
}
