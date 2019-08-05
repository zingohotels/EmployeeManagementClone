package app.zingo.employeemanagements.UI.Plan;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import app.zingo.employeemanagements.Custom.MyRegulerText;
import app.zingo.employeemanagements.Model.Organization;
import app.zingo.employeemanagements.Model.OrganizationPayments;
import app.zingo.employeemanagements.Model.PlanIntentData;
import app.zingo.employeemanagements.UI.NewAdminDesigns.PlanSubscribtionScreen;
import app.zingo.employeemanagements.Utils.PreferenceHandler;
import app.zingo.employeemanagements.Utils.ThreadExecuter;
import app.zingo.employeemanagements.Utils.Util;
import app.zingo.employeemanagements.WebApi.OrganizationApi;
import app.zingo.employeemanagements.WebApi.OrganizationPaymentAPI;
import app.zingo.employeemanagements.base.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BasicPlanSubscription extends AppCompatActivity implements PaymentResultListener {

    MyRegulerText mThree,mSix,mYear;

    Organization organization;

    String appType="";
    String startDate = "",endDate="";
    String planType="";

    int planId=2,rateId=0,emplCount=0;
    String planName="Basic",paymentId="";
    int price=0;

    PlanIntentData planData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{

            setContentView(R.layout.activity_basic_plan_subscription);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Basic Plan");

            mThree = findViewById(R.id.three);
            mSix = findViewById(R.id.six);
            mYear = findViewById(R.id.year);


            getCompany(PreferenceHandler.getInstance(BasicPlanSubscription.this).getCompanyId());

            mThree.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(organization!=null){

                        if(appType!=null&&!appType.isEmpty()){

                            planData = new PlanIntentData();

                            if(appType.equalsIgnoreCase("Trial")){

                                Calendar c = Calendar.getInstance();
                                c.setTime(new Date());

                                c.add(Calendar.DATE, 90);


                                // convert calendar to date
                                Date additionalDate = c.getTime();

                                Bundle bundlePlan = new Bundle();
                                planData.setPassStartDate(""+new SimpleDateFormat("MM/dd/yyyy").format(new Date()));
                                planData.setViewStartDate(""+new SimpleDateFormat("MMM dd,yyyy").format(new Date()));
                                planData.setPlanType("Basic,3");
                                planData.setPlanId(2);
                                planData.setPassEndDate(""+new SimpleDateFormat("MM/dd/yyyy").format(additionalDate));
                                planData.setViewEndDate(""+new SimpleDateFormat("MMM dd,yyyy").format(additionalDate));
                                planData.setPlanName("Basic");
                                planData.setAmount(80 * 3);
                                bundlePlan.putSerializable("PlanIntent",planData);
                                bundlePlan.putSerializable("Organization",organization);


                                Intent plan  = new Intent(BasicPlanSubscription.this, PlanSubscribtionScreen.class);
                                plan.putExtras(bundlePlan);
                                startActivity(plan);

                                startDate = new SimpleDateFormat("MM/dd/yyyy").format(new Date());
                                organization.setLicenseStartDate(startDate);
                                organization.setAppType("Paid");
                                organization.setPlanType("Basic,3");
                                organization.setPlanId(2);
                                organization.setLicenseEndDate(new SimpleDateFormat("MM/dd/yyyy").format(additionalDate));
                                //getEmployees(organization.getOrganizationId(),organization,3);




                            }
                        }
                    }

                }
            });

            mYear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(organization!=null){

                        if(appType!=null&&!appType.isEmpty()){

                            planData = new PlanIntentData();

                            if(appType.equalsIgnoreCase("Trial")){

                                Calendar c = Calendar.getInstance();
                                c.setTime(new Date());

                                c.add(Calendar.DATE, 365);


                                // convert calendar to date
                                Date additionalDate = c.getTime();

                                Bundle bundlePlan = new Bundle();
                                planData.setPassStartDate(""+new SimpleDateFormat("MM/dd/yyyy").format(new Date()));
                                planData.setViewStartDate(""+new SimpleDateFormat("MMM dd,yyyy").format(new Date()));
                                planData.setPlanType("Basic,5");
                                planData.setPlanId(2);
                                planData.setPassEndDate(""+new SimpleDateFormat("MM/dd/yyyy").format(additionalDate));
                                planData.setViewEndDate(""+new SimpleDateFormat("MMM dd,yyyy").format(additionalDate));
                                planData.setPlanName("Basic");
                                planData.setAmount(2 * 365);
                                bundlePlan.putSerializable("PlanIntent",planData);
                                bundlePlan.putSerializable("Organization",organization);


                                Intent plan  = new Intent(BasicPlanSubscription.this, PlanSubscribtionScreen.class);
                                plan.putExtras(bundlePlan);
                                startActivity(plan);

                               /* startDate = new SimpleDateFormat("MM/dd/yyyy").format(new Date());
                                organization.setLicenseStartDate(startDate);
                                organization.setAppType("Paid");
                                organization.setPlanType("Basic,5");
                                Calendar c = Calendar.getInstance();
                                c.setTime(new Date());

                                c.add(Calendar.DATE, 365);
                                organization.setPlanId(2);

                                // convert calendar to date
                                Date additionalDate = c.getTime();
                                organization.setLicenseEndDate(new SimpleDateFormat("MM/dd/yyyy").format(additionalDate));*/
                                //getEmployees(organization.getOrganizationId(),organization,12);

                            }
                        }
                    }

                }
            });

            mSix.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(organization!=null){

                        if(appType!=null&&!appType.isEmpty()){

                            planData = new PlanIntentData();

                            if(appType.equalsIgnoreCase("Trial")){

                                Calendar c = Calendar.getInstance();
                                c.setTime(new Date());

                                c.add(Calendar.DATE, 180);


                                // convert calendar to date
                                Date additionalDate = c.getTime();

                                Bundle bundlePlan = new Bundle();
                                planData.setPassStartDate(""+new SimpleDateFormat("MM/dd/yyyy").format(new Date()));
                                planData.setViewStartDate(""+new SimpleDateFormat("MMM dd,yyyy").format(new Date()));
                                planData.setPlanType("Basic,4");
                                planData.setPlanId(2);
                                planData.setPassEndDate(""+new SimpleDateFormat("MM/dd/yyyy").format(additionalDate));
                                planData.setViewEndDate(""+new SimpleDateFormat("MMM dd,yyyy").format(additionalDate));
                                planData.setPlanName("Basic");
                                planData.setAmount( 68 * 6);
                                bundlePlan.putSerializable("PlanIntent",planData);
                                bundlePlan.putSerializable("Organization",organization);


                                Intent plan  = new Intent(BasicPlanSubscription.this, PlanSubscribtionScreen.class);
                                plan.putExtras(bundlePlan);
                                startActivity(plan);

                               /* startDate = new SimpleDateFormat("MM/dd/yyyy").format(new Date());
                                organization.setLicenseStartDate(startDate);
                                organization.setAppType("Paid");
                                organization.setPlanType("Basic,4");
                                Calendar c = Calendar.getInstance();
                                c.setTime(new Date());

                                c.add(Calendar.DATE, 180);
                                organization.setPlanId(2);

                                // convert calendar to date
                                Date additionalDate = c.getTime();
                                organization.setLicenseEndDate(new SimpleDateFormat("MM/dd/yyyy").format(additionalDate));
                                getEmployees(organization.getOrganizationId(),organization,6);*/

                            }
                        }
                    }

                }
            });



        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void getCompany(final int id) {

        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {

                final OrganizationApi subCategoryAPI = Util.getClient().create(OrganizationApi.class);
                Call<ArrayList<Organization>> getProf = subCategoryAPI.getOrganizationById(id);
                //Call<ArrayList<Blogs>> getBlog = blogApi.getBlogs();

                getProf.enqueue(new Callback<ArrayList<Organization>>() {

                    @Override
                    public void onResponse(Call<ArrayList<Organization>> call, Response<ArrayList<Organization>> response) {

                        if (response.code() == 200||response.code() == 201||response.code() == 204)
                        {
                            organization = response.body().get(0);

                            if(organization!=null){

                                appType = organization.getAppType();
                                planType = organization.getPlanType();
                                startDate = organization.getLicenseStartDate();
                                endDate = organization.getLicenseEndDate();


                            }else{
                                Toast.makeText(BasicPlanSubscription.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }


                        }else{

                            Toast.makeText(BasicPlanSubscription.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Organization>> call, Throwable t) {

                        Toast.makeText(BasicPlanSubscription.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                    }
                });

            }

        });
    }

    public void popupOne(final Organization org, final String planName, final double amount){

        try{

            AlertDialog.Builder builder = new AlertDialog.Builder(BasicPlanSubscription.this);
            LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View views = inflater.inflate(R.layout.pop_payment_details, null);

            builder.setView(views);

            final Button mProceed = views.findViewById(R.id.paid_version);
            final TextView mPlanName = views.findViewById(R.id.plan_name);
            final TextView mValid = views.findViewById(R.id.plan_validity);
            final TextView mEmL = views.findViewById(R.id.plan_empl_limit);
            final TextView mAmt = views.findViewById(R.id.plan_amount);
            final AlertDialog dialogs = builder.create();
            dialogs.show();
            dialogs.setCanceledOnTouchOutside(false);

            mPlanName.setText(""+planName);
            mValid.setText(org.getLicenseStartDate()+" to "+org.getLicenseEndDate());
            mEmL.setText(""+org.getEmployeeLimit());
            mAmt.setText("Rs "+amount);


            mProceed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    startPayment();
                    dialogs.dismiss();


                }
            });









        }catch (Exception e){
            e.printStackTrace();
        }

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
            options.put("amount",price * 100);
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

    public void updateOrg(final Organization organization) {




        OrganizationApi apiService = Util.getClient().create(OrganizationApi.class);

        Call<Organization> call = apiService.updateOrganization(organization.getOrganizationId(),organization);

        call.enqueue(new Callback<Organization>() {
            @Override
            public void onResponse(Call<Organization> call, Response<Organization> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {


                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201|| statusCode == 204) {


                        OrganizationPayments op = new OrganizationPayments();
                        op.setTitle("Plan Subscription for Creating organization");
                        op.setDescription("Plan Name "+organization.getPlanId()+" License End date "+organization.getLicenseEndDate());
                        op.setPaymentDate(new SimpleDateFormat("MM/dd/yyyy").format(new Date()));
                        op.setOrganizationId(organization.getOrganizationId());
                        op.setPaymentBy(organization.getOrganizationName()+"");
                        op.setAmount(price * 100);
                        op.setTransactionId(""+paymentId);
                        op.setTransactionMethod("");
                        op.setZingyPaymentStatus("Pending");
                        op.setZingyPaymentDate(new SimpleDateFormat("MM/dd/yyyy").format(new Date()));
                        op.setResellerCommissionPercentage(10);

                        addOrgaPay(organization,op);




                    }else {
                        Toast.makeText(BasicPlanSubscription.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception ex)
                {


                    ex.printStackTrace();
                }
//                callGetStartEnd();
            }

            @Override
            public void onFailure(Call<Organization> call, Throwable t) {


                //Toast.makeText(BasicPlanSubscription.this, "Failed due to Bad Internet Connection", Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());
            }
        });



    }

    public void addOrgaPay(final Organization organization, final OrganizationPayments organizationPayments) {


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


                            BasicPlanSubscription.this.finish();
                        }




                    }else {
                        Toast.makeText(BasicPlanSubscription.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
                //Toast.makeText(BasicPlanSubscription.this, "Failed due to Bad Internet Connection", Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());
            }
        });



    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id)
        {
            case android.R.id.home:

                BasicPlanSubscription.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
