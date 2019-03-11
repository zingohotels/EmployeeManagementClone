package app.zingo.employeemanagements.UI.NewAdminDesigns;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import app.zingo.employeemanagements.Custom.MyRegulerText;
import app.zingo.employeemanagements.Model.Employee;
import app.zingo.employeemanagements.Model.Organization;
import app.zingo.employeemanagements.Model.OrganizationPayments;
import app.zingo.employeemanagements.Model.PlanIntentData;
import app.zingo.employeemanagements.R;
import app.zingo.employeemanagements.UI.AdvancePlanScreen;
import app.zingo.employeemanagements.UI.BasicPlanScreen;
import app.zingo.employeemanagements.Utils.PreferenceHandler;
import app.zingo.employeemanagements.Utils.ThreadExecuter;
import app.zingo.employeemanagements.Utils.Util;
import app.zingo.employeemanagements.WebApi.EmployeeApi;
import app.zingo.employeemanagements.WebApi.OrganizationApi;
import app.zingo.employeemanagements.WebApi.OrganizationPaymentAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlanSubscribtionScreen extends AppCompatActivity implements PaymentResultListener {


    MyRegulerText mPlanName,mStratDate,mEndDate;
    ImageView mAdd,mRemove;
    TextView mEmployeeCount,mPay;


    PlanIntentData planIntentData;
    Organization organization;

    //data
    int employeeCount=0;
    double amount=0;
    String  paymentId = "";
    double passAmount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{

            setContentView(R.layout.activity_plan_subscribtion_screen);

            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Plan Subscribtion");

            mPlanName = (MyRegulerText)findViewById(R.id.plan_name_sub);
            mEmployeeCount = (TextView)findViewById(R.id.employee_count_plan);
            mPay = (TextView)findViewById(R.id.pay_plan);
            mStratDate = (MyRegulerText)findViewById(R.id.start_date_plan);
            mEndDate = (MyRegulerText)findViewById(R.id.end_date_plan);

            mAdd = (ImageView) findViewById(R.id.adult_add);
            mRemove = (ImageView)findViewById(R.id.adult_remove);

            Bundle bundle = getIntent().getExtras();

            if(bundle!=null){

                planIntentData = (PlanIntentData)bundle.getSerializable("PlanIntent");
                organization = (Organization) bundle.getSerializable("Organization");

                if(organization!=null){

                    getEmployees(organization.getOrganizationId());

                }else{
                    //getEmployees(PreferenceHandler.getInstance());
                    Toast.makeText(PlanSubscribtionScreen.this, "Something went wrong.Please try again some time later", Toast.LENGTH_SHORT).show();
                }

                if(planIntentData!=null){

                   mStratDate.setText(""+planIntentData.getViewStartDate());
                   mEndDate.setText(""+planIntentData.getViewEndDate());
                   mPlanName.setText(""+planIntentData.getPlanName());

                    amount = planIntentData.getAmount();
                    //passAmount = planIntentData.getAmount();

                }else{

                    Toast.makeText(PlanSubscribtionScreen.this, "Something went wrong.Please try again some time later", Toast.LENGTH_SHORT).show();
                }


            }else{
                Toast.makeText(PlanSubscribtionScreen.this, "Something went wrong.Please try again some time later", Toast.LENGTH_SHORT).show();
            }

            mAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int count = Integer.parseInt(mEmployeeCount.getText().toString());

                    mEmployeeCount.setText(""+(count+1));

                    double value = amount * (count+1);
                    passAmount = value;
                    mPay.setText("Pay ₹ "+new DecimalFormat("#.##").format(value));



                }
            });


            mRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int count = Integer.parseInt(mEmployeeCount.getText().toString());

                    if(count<=1){

                        Toast.makeText(PlanSubscribtionScreen.this, "Atleast one should be there", Toast.LENGTH_SHORT).show();

                    }else if(count>1){

                        if(count<=employeeCount){

                            Toast.makeText(PlanSubscribtionScreen.this, "You need to delete some employees from your organization ", Toast.LENGTH_SHORT).show();
                        }else{
                            mEmployeeCount.setText(""+(count-1));
                            double value = amount * (count-1);
                            passAmount = value;
                            mPay.setText("Pay ₹ "+new DecimalFormat("#.##").format(value));
                        }


                    }


                }
            });


            mPay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(organization!=null&&planIntentData!=null){

                                organization.setLicenseStartDate(planIntentData.getPassStartDate());
                                organization.setAppType("Paid");
                                organization.setPlanType(planIntentData.getPlanType());
                                organization.setPlanId(planIntentData.getPlanId());
                                organization.setLicenseEndDate(planIntentData.getPassEndDate());
                                organization.setEmployeeLimit(Integer.parseInt(mEmployeeCount.getText().toString()));
                        try {
                            startPayment();
                            //updateOrg(organization);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }else{
                        Toast.makeText(PlanSubscribtionScreen.this, "Something went wrong.Please try again some time", Toast.LENGTH_SHORT).show();
                    }

                }
            });

        }catch(Exception e){
            e.printStackTrace();
        }

    }

    private void getEmployees(final int id){


        final ProgressDialog progressDialog = new ProgressDialog(PlanSubscribtionScreen.this);
        progressDialog.setTitle("Loading Details..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {
                EmployeeApi apiService = Util.getClient().create(EmployeeApi.class);
                Call<ArrayList<Employee>> call = apiService.getEmployeesByOrgId(id);

                call.enqueue(new Callback<ArrayList<Employee>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Employee>> call, Response<ArrayList<Employee>> response) {
                        // Log error here since request failed
                        if (progressDialog!=null)
                            progressDialog.dismiss();
                        int statusCode = response.code();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {



                            ArrayList<Employee> list = response.body();


                            if (list != null && list.size() != 0) {

                                employeeCount = list.size();
                                mEmployeeCount.setText(""+employeeCount);

                                double value = amount * (employeeCount);
                                passAmount = value;
                                mPay.setText("Pay ₹ "+new DecimalFormat("#.##").format(value));

                            }else{

                            }
                        }else {


                            Toast.makeText(PlanSubscribtionScreen.this, "Failed due to : " + response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Employee>> call, Throwable t) {
                        // Log error here since request failed
                        if (progressDialog!=null)
                            progressDialog.dismiss();

                        Log.e("TAG", t.toString());
                    }
                });
            }


        });
    }

    public void updateOrg(final Organization organization) throws Exception{

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Updating Details..");
        dialog.setCancelable(false);
        dialog.show();


        OrganizationApi apiService = Util.getClient().create(OrganizationApi.class);

        Call<Organization> call = apiService.updateOrganization(organization.getOrganizationId(),organization);

        call.enqueue(new Callback<Organization>() {
            @Override
            public void onResponse(Call<Organization> call, Response<Organization> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {
                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }


                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201|| statusCode == 204) {


                        OrganizationPayments op = new OrganizationPayments();
                        op.setTitle("Plan Subscription for Creating organization");
                        op.setDescription("Plan Name "+organization.getPlanId()+" License End date "+organization.getLicenseEndDate());
                        op.setPaymentDate(new SimpleDateFormat("MM/dd/yyyy").format(new Date()));
                        op.setOrganizationId(organization.getOrganizationId());
                        op.setPaymentBy(organization.getOrganizationName()+"");
                        op.setAmount(passAmount);
                        op.setTransactionId(""+paymentId);
                        op.setTransactionMethod("");
                        op.setZingyPaymentStatus("Pending");
                        op.setZingyPaymentDate(new SimpleDateFormat("MM/dd/yyyy").format(new Date()));
                        op.setResellerCommissionPercentage(10);

                        addOrgaPay(organization,op);




                    }else {
                        Toast.makeText(PlanSubscribtionScreen.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
            public void onFailure(Call<Organization> call, Throwable t) {

                if(dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }

                Toast.makeText(PlanSubscribtionScreen.this, "Failed Due to "+t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());
            }
        });



    }

    public void addOrgaPay(final Organization organization,final OrganizationPayments organizationPayments) throws Exception{


        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Saving Details..");
        dialog.setCancelable(false);
        dialog.show();

        OrganizationPaymentAPI apiService = Util.getClient().create(OrganizationPaymentAPI.class);

        Call<OrganizationPayments> call = apiService.addOrganizationPayments(organizationPayments);

        call.enqueue(new Callback<OrganizationPayments>() {
            @Override
            public void onResponse(Call<OrganizationPayments> call, Response<OrganizationPayments> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {
                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201) {

                        OrganizationPayments s = response.body();

                        if(s!=null){


                            Intent i = new Intent(PlanSubscribtionScreen.this, AdminNewMainScreen.class);
                            // set the new task and clear flags
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            PlanSubscribtionScreen.this.finish();
                        }




                    }else {
                        Toast.makeText(PlanSubscribtionScreen.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
            public void onFailure(Call<OrganizationPayments> call, Throwable t) {

                if(dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
                Toast.makeText(PlanSubscribtionScreen.this, "Failed Due to "+t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());
            }
        });



    }

    public void startPayment() {

        final Activity activity = this;

        final Checkout co = new Checkout();

        try {
            JSONObject options = new JSONObject();

            options.put("name", "EMS" );
            options.put("description", "For  "+organization.getPlanType()+" Plan");
            //You can omit the image option to fetch the image from dashboard
            //options.put("image", R.drawable.app_logo);
            options.put("currency", "INR");
            options.put("amount",(int)passAmount * 100);
            //options.put("amount","100");

            JSONObject preFill = new JSONObject();
            preFill.put("email", "");
            preFill.put("contact","" );

            options.put("prefill", preFill);

            co.open(activity, options);
        } catch (Exception e) {
            Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT)
                    .show();
            e.printStackTrace();
        }
    }



    @SuppressWarnings("unused")
    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        try {

            paymentId = razorpayPaymentID;

            updateOrg(organization);
            Toast.makeText(this, "Payment Successful: " + razorpayPaymentID, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            // Log.e(TAG, "Exception in onPaymentSuccess", e);
        }
    }


    @SuppressWarnings("unused")
    @Override
    public void onPaymentError(int code, String response) {
        try {
            Toast.makeText(this, "Payment failed: " + code + " " + response, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            //Log.e(TAG, "Exception in onPaymentError", e);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){

            case android.R.id.home:
                PlanSubscribtionScreen.this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
