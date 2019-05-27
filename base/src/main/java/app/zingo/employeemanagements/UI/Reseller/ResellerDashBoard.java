package app.zingo.employeemanagements.UI.Reseller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;

import app.zingo.employeemanagements.Adapter.ResellerOrganizationListAdapter;
import app.zingo.employeemanagements.Custom.MyRegulerText;
import app.zingo.employeemanagements.Model.Employee;
import app.zingo.employeemanagements.Model.Organization;
import app.zingo.employeemanagements.Model.OrganizationPayments;
import app.zingo.employeemanagements.UI.Landing.InternalServerErrorScreen;
import app.zingo.employeemanagements.Utils.PreferenceHandler;
import app.zingo.employeemanagements.Utils.ThreadExecuter;
import app.zingo.employeemanagements.Utils.Util;
import app.zingo.employeemanagements.WebApi.EmployeeApi;
import app.zingo.employeemanagements.WebApi.ResellerAPI;
import app.zingo.employeemanagements.base.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResellerDashBoard extends AppCompatActivity {

    MyRegulerText mOrganizationCount,mEmployeeCount,mTrial,mPaid,mTotal,mCommission;

    LinearLayout mUpcomingLay;
    RecyclerView mUpcominList;

    int emplCount=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{

            setContentView(R.layout.activity_reseller_dash_board);

            mOrganizationCount = findViewById(R.id.organization_count);
            mEmployeeCount = findViewById(R.id.employee_count);
            mTrial = findViewById(R.id.trial_version);
            mPaid = findViewById(R.id.paid_version);
            mTotal = findViewById(R.id.total_payment);
            mCommission = findViewById(R.id.commission);

            mUpcomingLay = findViewById(R.id.upcoming_transaction_lay);
            mUpcominList = findViewById(R.id.upcoming_transaction);

            int mResellerId = PreferenceHandler.getInstance(ResellerDashBoard.this).getResellerUserId();


            if(mResellerId!=0){
                getOrganizations(mResellerId);
                getOrganizationsPayment(mResellerId);
            }else{

                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void getOrganizations(final int mResellerId){


        final ProgressDialog progressDialog = new ProgressDialog(ResellerDashBoard.this);
        progressDialog.setTitle("Loading Details..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {
                ResellerAPI apiService = Util.getClient().create(ResellerAPI.class);
                Call<ArrayList<Organization>> call = apiService.getOrganizationBySellerId(mResellerId);

                call.enqueue(new Callback<ArrayList<Organization>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Organization>> call, Response<ArrayList<Organization>> response) {
                        int statusCode = response.code();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {


                            if (progressDialog!=null)
                                progressDialog.dismiss();
                            ArrayList<Organization> list = response.body();




                            if (list !=null && list.size()!=0) {

                                ResellerOrganizationListAdapter adapter = new ResellerOrganizationListAdapter(ResellerDashBoard.this,list);
                                mUpcominList.setAdapter(adapter);

                                mOrganizationCount.setText(""+list.size());
                                int trial = 0,paid = 0;

                                for (Organization org:list) {

                                    String apptype = org.getAppType();
                                    getEmployees(org.getOrganizationId());

                                    if(apptype!=null&& apptype.equalsIgnoreCase("Trial")){

                                        trial = trial+1;

                                    }else if(apptype!=null&& apptype.equalsIgnoreCase("Paid")){

                                        paid = paid+1;

                                    }

                                }


                                mTrial.setText(""+trial);
                                mPaid.setText(""+paid);




                            }else{
                               mUpcomingLay.setVisibility(View.GONE);

                                Toast.makeText(ResellerDashBoard.this, "No Organizations under you ", Toast.LENGTH_SHORT).show();

                            }

                        }else {

                            if (progressDialog!=null)
                                progressDialog.dismiss();

                            if(statusCode==500){
                                Intent error = new Intent(ResellerDashBoard.this, InternalServerErrorScreen.class);
                                startActivity(error);
                            }else{

                                Toast.makeText(ResellerDashBoard.this,  "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
                            }


                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Organization>> call, Throwable t) {
                        // Log error here since request failed
                       /* if (progressDialog!=null)
                            progressDialog.dismiss();*/
                        Log.e("TAG", t.toString());
                    }
                });
            }


        });
    }

    private void getEmployees(final int id){


        /*final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Loading Employees");
        progressDialog.setCancelable(false);
        progressDialog.show();*/

        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {
                EmployeeApi apiService = Util.getClient().create(EmployeeApi.class);
                Call<ArrayList<Employee>> call = apiService.getEmployeesByOrgId(id);

                call.enqueue(new Callback<ArrayList<Employee>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Employee>> call, Response<ArrayList<Employee>> response) {
                        int statusCode = response.code();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {


                           /* if (progressDialog!=null)
                                progressDialog.dismiss();*/
                            ArrayList<Employee> list = response.body();


                            if (list != null && list.size() != 0) {

                                emplCount = emplCount+list.size();

                                mEmployeeCount.setText("" + list.size());


                            }
                        }else {


                            Toast.makeText(ResellerDashBoard.this, "Failed due to : " + response.message(), Toast.LENGTH_SHORT).show();
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

    private void getOrganizationsPayment(final int mResellerId){


        final ProgressDialog progressDialog = new ProgressDialog(ResellerDashBoard.this);
        progressDialog.setTitle("Loading Details..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {
                ResellerAPI apiService = Util.getClient().create(ResellerAPI.class);
                Call<ArrayList<OrganizationPayments>> call = apiService.getOrganizationPaymentBySellerId(mResellerId);

                call.enqueue(new Callback<ArrayList<OrganizationPayments>>() {
                    @Override
                    public void onResponse(Call<ArrayList<OrganizationPayments>> call, Response<ArrayList<OrganizationPayments>> response) {
                        int statusCode = response.code();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {


                            if (progressDialog!=null)
                                progressDialog.dismiss();
                            ArrayList<OrganizationPayments> list = response.body();




                            if (list !=null && list.size()!=0) {

                                double amount = 0,per = 0;

                                for (OrganizationPayments org:list) {

                                    amount = amount+org.getAmount();

                                    double value = org.getResellerCommissionPercentage()*org.getAmount();
                                    double pers = value/100;
                                    per = per + pers;



                                }

                                mTotal.setText(new DecimalFormat("#.##").format(amount));
                                mCommission.setText(new DecimalFormat("#.##").format(per));




                            }else{

                                Toast.makeText(ResellerDashBoard.this, "No Payment done from your organizations ", Toast.LENGTH_SHORT).show();

                            }

                        }else {

                            if (progressDialog!=null)
                                progressDialog.dismiss();

                            if(statusCode==500){
                                Intent error = new Intent(ResellerDashBoard.this, InternalServerErrorScreen.class);
                                startActivity(error);
                            }else{
                                Toast.makeText(ResellerDashBoard.this,  "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
                            }


                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<OrganizationPayments>> call, Throwable t) {
                        // Log error here since request failed
                       /* if (progressDialog!=null)
                            progressDialog.dismiss();*/
                        Log.e("TAG", t.toString());
                    }
                });
            }


        });
    }
}
