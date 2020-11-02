package app.zingo.employeemanagements.ui.Employee;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import app.zingo.employeemanagements.adapter.LoginDetailsAdapter;
import app.zingo.employeemanagements.model.LoginDetails;
import app.zingo.employeemanagements.utils.ThreadExecuter;
import app.zingo.employeemanagements.utils.Util;
import app.zingo.employeemanagements.WebApi.LoginDetailsAPI;
import app.zingo.employeemanagements.base.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WorkedDaysListScreen extends AppCompatActivity {

    RecyclerView mWorkedDayList;
    int employeeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{

            setContentView(R.layout.activity_worked_days_list_screen);

            mWorkedDayList = findViewById(R.id.worked_day_list);

            Bundle bundle = getIntent().getExtras();

            if(bundle!=null){

                employeeId = bundle.getInt("EmployeeId");
            }

            if(employeeId!=0){
                getLoginDetails(employeeId);

            }


        }catch(Exception e){
            e.printStackTrace();
        }

    }

    private void getLoginDetails(final int employeeId){


        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading Details..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new ThreadExecuter ().execute( new Runnable() {
            @Override
            public void run() {
                LoginDetailsAPI apiService = Util.getClient().create( LoginDetailsAPI.class);
                Call<ArrayList< LoginDetails >> call = apiService.getLoginByEmployeeId(employeeId);

                call.enqueue(new Callback<ArrayList< LoginDetails >>() {
                    @Override
                    public void onResponse( Call<ArrayList< LoginDetails >> call, Response<ArrayList< LoginDetails >> response) {
                        int statusCode = response.code();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {


                            if (progressDialog != null&&progressDialog.isShowing())
                                progressDialog.dismiss();
                            ArrayList< LoginDetails > list = response.body();
                            long hours=0;


                            if (list !=null && list.size()!=0) {

                                Collections.sort(list, LoginDetails.compareLogin);
                                LoginDetailsAdapter adapter = new LoginDetailsAdapter ( WorkedDaysListScreen.this,list);
                                mWorkedDayList.setAdapter(adapter);

                                ArrayList<String> dateList = new ArrayList<>();
                                for(int i=0;i<list.size();i++){

                                    if(list.get(i).getLoginDate().contains("T")){





                                        String date[] = list.get(i).getLoginDate().split("T");
                                        Date dates = null;
                                        try {
                                            dates = new SimpleDateFormat("yyyy-MM-dd").parse(date[0]);
                                            String dateValue = new SimpleDateFormat("MMM dd,yyyy").format(dates);


                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                        dateList.add(date[0]);
                                    }

                                }

                                if(dateList!=null&&dateList.size()!=0){

                                    Set<String> s = new LinkedHashSet<String>(dateList);


                                }


                            }else{

                            }

                        }else {

                            if (progressDialog != null&&progressDialog.isShowing())
                                progressDialog.dismiss();

                            Toast.makeText( WorkedDaysListScreen.this, "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure( Call<ArrayList< LoginDetails >> call, Throwable t) {
                        // Log error here since request failed
                        if (progressDialog!=null)
                            progressDialog.dismiss();
                        Log.e("TAG", t.toString());
                    }
                });
            }


        });
    }
}
