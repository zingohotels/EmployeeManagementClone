package app.zingo.employeemanagements.ui.NewAdminDesigns;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airpay.airpaysdk_simplifiedotp.AirpayActivity;
import com.airpay.airpaysdk_simplifiedotp.ResponseMessage;
import com.airpay.airpaysdk_simplifiedotp.Transaction;
import com.razorpay.Checkout;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.zip.CRC32;

import app.zingo.employeemanagements.Custom.MyRegulerText;
import app.zingo.employeemanagements.model.Employee;
import app.zingo.employeemanagements.model.Organization;
import app.zingo.employeemanagements.model.OrganizationPayments;
import app.zingo.employeemanagements.model.PlanIntentData;
import app.zingo.employeemanagements.utils.Constants;
import app.zingo.employeemanagements.utils.PreferenceHandler;
import app.zingo.employeemanagements.utils.ThreadExecuter;
import app.zingo.employeemanagements.utils.Util;
import app.zingo.employeemanagements.WebApi.EmployeeApi;
import app.zingo.employeemanagements.WebApi.OrganizationApi;
import app.zingo.employeemanagements.WebApi.OrganizationPaymentAPI;
import app.zingo.employeemanagements.base.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlanSubscribtionScreen extends AppCompatActivity {


    MyRegulerText mPlanName,mStratDate,mEndDate,mSubTotal,mTaxes;
    ImageView mAdd,mRemove;
    TextView mEmployeeCount,mPay;

    PlanIntentData planIntentData;
    Organization organization;

    //data
    int employeeCount=0;
    double amount=0,gst=0,grand=0;
    String  paymentId = "";
    double passAmount = 0;

    //airpay
    ResponseMessage resp;
    ArrayList<Transaction> transactionList;
    private String ErrorMessage = "invalid";
    public boolean ischaracter;
    public boolean boolIsError_new = true;


    private int k = 0;
    public final int PERMISSION_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{

            setContentView(R.layout.activity_plan_subscribtion_screen);

            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Plan Subscribtion");

            mPlanName = findViewById(R.id.plan_name_sub);
            mEmployeeCount = findViewById(R.id.employee_count_plan);
            mPay = findViewById(R.id.pay_plan);
            mStratDate = findViewById(R.id.start_date_plan);
            mEndDate = findViewById(R.id.end_date_plan);
            mSubTotal = findViewById(R.id.sub_toal_amount);
            mTaxes = findViewById(R.id.tax_amount);

            mAdd = findViewById(R.id.adult_add);
            mRemove = findViewById(R.id.adult_remove);

            Bundle bundle = getIntent().getExtras();

            if(bundle!=null){

                planIntentData = ( PlanIntentData )bundle.getSerializable("PlanIntent");
                organization = ( Organization ) bundle.getSerializable("Organization");



                if(planIntentData!=null){

                   mStratDate.setText(""+planIntentData.getViewStartDate());
                   mEndDate.setText(""+planIntentData.getViewEndDate());
                   mPlanName.setText(""+planIntentData.getPlanName());

                    amount = planIntentData.getAmount();

                    if(organization!=null){

                        //getEmployees(organization.getOrganizationId());
                        employeeCount = 1;
                        mEmployeeCount.setText(""+employeeCount);

                        double value = amount * (employeeCount);
                        double gstV = value * 18;
                        gst = gstV/100;

                        passAmount = gst+value;
                        mPay.setText("Pay ₹ "+new DecimalFormat("#.##").format(passAmount));
                        mSubTotal.setText("₹ "+new DecimalFormat("#.##").format(value));
                        mTaxes.setText("₹ "+new DecimalFormat("#.##").format(gst));

                    }else{
                        //getEmployees(PreferenceHandler.getInstance());
                        Toast.makeText( PlanSubscribtionScreen.this, "Something went wrong.Please try again some time later", Toast.LENGTH_SHORT).show();
                    }

                    //passAmount = planIntentData.getAmount();

                }else{

                    Toast.makeText( PlanSubscribtionScreen.this, "Something went wrong.Please try again some time later", Toast.LENGTH_SHORT).show();
                }


            }else{
                Toast.makeText( PlanSubscribtionScreen.this, "Something went wrong.Please try again some time later", Toast.LENGTH_SHORT).show();
            }

            mAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int count = Integer.parseInt(mEmployeeCount.getText().toString());

                    mEmployeeCount.setText(""+(count+1));

                    double value = amount * (count+1);

                    double gstV = value * 18;
                    gst = gstV/100;

                    passAmount = gst+value;
                    mPay.setText("Pay ₹ "+new DecimalFormat("#.##").format(passAmount));
                    mSubTotal.setText("₹ "+new DecimalFormat("#.##").format(value));
                    mTaxes.setText("₹ "+new DecimalFormat("#.##").format(gst));



                }
            });


            mRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int count = Integer.parseInt(mEmployeeCount.getText().toString());

                    if(count<=1){

                        Toast.makeText( PlanSubscribtionScreen.this, "Atleast one should be there", Toast.LENGTH_SHORT).show();

                    }else if(count>1){

                        if(count<=employeeCount){

                            Toast.makeText( PlanSubscribtionScreen.this, "You need to delete some employees from your organization ", Toast.LENGTH_SHORT).show();
                        }else{
                            mEmployeeCount.setText(""+(count-1));
                            double value = amount * (count-1);
                            double gstV = value * 18;
                            gst = gstV/100;

                            passAmount = gst+value;
                            mPay.setText("Pay ₹ "+new DecimalFormat("#.##").format(passAmount));
                            mSubTotal.setText("₹ "+new DecimalFormat("#.##").format(value));
                            mTaxes.setText("₹ "+new DecimalFormat("#.##").format(gst));
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
                            airpay();
                            //updateOrg(organization);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }else{
                        Toast.makeText( PlanSubscribtionScreen.this, "Something went wrong.Please try again some time", Toast.LENGTH_SHORT).show();
                    }

                }
            });

        }catch(Exception e){

            e.printStackTrace();
        }

    }

    private void getEmployees(final int id){


        final ProgressDialog progressDialog = new ProgressDialog( PlanSubscribtionScreen.this);
        progressDialog.setTitle("Loading Details..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new ThreadExecuter ().execute( new Runnable() {
            @Override
            public void run() {
                EmployeeApi apiService = Util.getClient().create( EmployeeApi.class);
                Call<ArrayList< Employee >> call = apiService.getEmployeesByOrgId(id);

                call.enqueue(new Callback<ArrayList< Employee >>() {
                    @Override
                    public void onResponse( Call<ArrayList< Employee >> call, Response<ArrayList< Employee >> response) {
                        // Log error here since request failed
                        if (progressDialog != null&&progressDialog.isShowing())
                            progressDialog.dismiss();
                        int statusCode = response.code();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {



                            ArrayList< Employee > list = response.body();


                            if (list != null && list.size() != 0) {

                                employeeCount = list.size();
                                mEmployeeCount.setText(""+employeeCount);

                                double value = amount * (employeeCount);
                                double gstV = value * 18;
                                gst = gstV/100;

                                passAmount = gst+value;
                                mPay.setText("Pay ₹ "+new DecimalFormat("#.##").format(passAmount));
                                mSubTotal.setText("₹ "+new DecimalFormat("#.##").format(value));
                                mTaxes.setText("₹ "+new DecimalFormat("#.##").format(gst));

                            }else{

                            }
                        }else {


                            Toast.makeText( PlanSubscribtionScreen.this, "Failed due to : " + response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure( Call<ArrayList< Employee >> call, Throwable t) {
                        // Log error here since request failed
                        if (progressDialog != null&&progressDialog.isShowing())
                            progressDialog.dismiss();

                        Log.e("TAG", t.toString());
                    }
                });
            }


        });
    }

    public void updateOrg(final Organization organization) {

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Updating Details..");
        dialog.setCancelable(false);
        dialog.show();


        OrganizationApi apiService = Util.getClient().create( OrganizationApi.class);

        Call< Organization > call = apiService.updateOrganization(organization.getOrganizationId(),organization);

        call.enqueue(new Callback< Organization >() {
            @Override
            public void onResponse( Call< Organization > call, Response< Organization > response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {
                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }


                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201|| statusCode == 204) {


                        OrganizationPayments op = new OrganizationPayments ();
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
                        Toast.makeText( PlanSubscribtionScreen.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
            public void onFailure( Call< Organization > call, Throwable t) {

                if(dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }

                Toast.makeText( PlanSubscribtionScreen.this , "Failed due to Bad Internet Connection" , Toast.LENGTH_SHORT ).show( );
                Log.e("TAG", t.toString());
            }
        });



    }

    public void addOrgaPay( final Organization organization, final OrganizationPayments organizationPayments) {


        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Saving Details..");
        dialog.setCancelable(false);
        dialog.show();

        OrganizationPaymentAPI apiService = Util.getClient().create( OrganizationPaymentAPI.class);

        Call< OrganizationPayments > call = apiService.addOrganizationPayments(organizationPayments);

        call.enqueue(new Callback< OrganizationPayments >() {
            @Override
            public void onResponse( Call< OrganizationPayments > call, Response< OrganizationPayments > response) {
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


                            Intent i = new Intent( PlanSubscribtionScreen.this, AdminNewMainScreen.class);
                            // set the new task and clear flags
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            PlanSubscribtionScreen.this.finish();
                        }




                    }else {
                        Toast.makeText( PlanSubscribtionScreen.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
            public void onFailure( Call< OrganizationPayments > call, Throwable t) {

                if(dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
                Toast.makeText( PlanSubscribtionScreen.this , "Failed due to Bad Internet Connection" , Toast.LENGTH_SHORT ).show( );
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


    //airpay Payment Gatewy
    public void callback(ArrayList<Transaction> data, boolean flag) {
        if (data != null) {
        }
    }

    public void airpay(){


        Intent myIntent = new Intent(this, AirpayActivity.class);

        Bundle b = new Bundle();

        // Please enter Merchant configuration value


        // Live Merchant Details - Merchant Id -
        b.putString("USERNAME", Constants.AIR_UN);
        b.putString("PASSWORD", Constants.AIR_PWD);
        b.putString("SECRET", Constants.AIR_SEC);
        b.putString("MERCHANT_ID", Constants.AIR_MI);


        b.putString("EMAIL", PreferenceHandler.getInstance( PlanSubscribtionScreen.this).getUserEmail());

        // This is for dynamic phone no value code - Uncomment it
        b.putString("PHONE", "" + PreferenceHandler.getInstance( PlanSubscribtionScreen.this).getPhoneNumber());
					/*//  Please enter phone no value
					b.putString("PHONE", "");*/
        b.putString("FIRSTNAME", PreferenceHandler.getInstance( PlanSubscribtionScreen.this).getUserFullName());
        b.putString("LASTNAME", "");
        // b.putString("ADDRESS", address.getEditableText().toString().trim());
        //b.putString("CITY", city.getEditableText().toString().trim());
        //b.putString("STATE", state.getEditableText().toString().trim());
        //b.putString("COUNTRY", country.getEditableText().toString().trim());
        //b.putString("PIN_CODE", pincode.getEditableText().toString().trim());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date random = new Date();
        String bookNumber = dateFormat.format(random);
        b.putString("ORDER_ID", "ZPSA"+bookNumber);





        String perValue = String.valueOf(passAmount);
        b.putString("AMOUNT", perValue.trim());

        b.putString("CURRENCY", "356");
        b.putString("ISOCURRENCY", "INR");
        b.putString("CHMOD", "");
        b.putString("CUSTOMVAR", "");
        b.putString("TXNSUBTYPE", "");
        b.putString("WALLET", "0");

        // Live Success URL Merchant Id -
        b.putString("SUCCESS_URL", "www.zingohotels.com");


        b.putParcelable("RESPONSEMESSAGE", (Parcelable) resp);

        myIntent.putExtras(b);
        startActivityForResult(myIntent, 120);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            Bundle bundle = data.getExtras();
            transactionList = new ArrayList<Transaction>();
            transactionList = (ArrayList<Transaction>) bundle.getSerializable("DATA");
            if (transactionList != null) {
                Toast.makeText(this, transactionList.get(0).getSTATUS() + "\n" + transactionList.get(0).getSTATUSMSG(), Toast.LENGTH_LONG).show();

                if (transactionList.get(0).getSTATUS() != null) {
                    Log.e("STATUS -> ", "=" + transactionList.get(0).getSTATUS());

                }
                if (transactionList.get(0).getMERCHANTKEY() != null) {
                    Log.e("MERCHANT KEY -> ", "=" + transactionList.get(0).getMERCHANTKEY());

                }
                if (transactionList.get(0).getMERCHANTPOSTTYPE() != null) {
                    Log.e("MERCHANT POST TYPE ", "=" + transactionList.get(0).getMERCHANTPOSTTYPE());
                }
                if (transactionList.get(0).getSTATUSMSG() != null) {
                    Log.e("STATUS MSG -> ", "=" + transactionList.get(0).getSTATUSMSG()); //  success or fail
                }
                if (transactionList.get(0).getTRANSACTIONAMT() != null) {
                    Log.e("TRANSACTION AMT -> ", "=" + transactionList.get(0).getTRANSACTIONAMT());

                }
                if (transactionList.get(0).getTXN_MODE() != null) {
                    Log.e("TXN MODE -> ", "=" + transactionList.get(0).getTXN_MODE());
                }
                if (transactionList.get(0).getMERCHANTTRANSACTIONID() != null) {
                    Log.e("MERCHANT_TXN_ID -> ", "=" + transactionList.get(0).getMERCHANTTRANSACTIONID()); // order id

                }
                if (transactionList.get(0).getSECUREHASH() != null) {
                    Log.e("SECURE HASH -> ", "=" + transactionList.get(0).getSECUREHASH());
                }
                if (transactionList.get(0).getCUSTOMVAR() != null) {
                    Log.e("CUSTOMVAR -> ", "=" + transactionList.get(0).getCUSTOMVAR());
                }
                if (transactionList.get(0).getTRANSACTIONID() != null) {
                    Log.e("TXN ID -> ", "=" + transactionList.get(0).getTRANSACTIONID());
                }
                if (transactionList.get(0).getTRANSACTIONSTATUS() != null) {
                    Log.e("TXN STATUS -> ", "=" + transactionList.get(0).getTRANSACTIONSTATUS());
                }
                if (transactionList.get(0).getTXN_DATE_TIME() != null) {
                    Log.e("TXN_DATETIME -> ", "=" + transactionList.get(0).getTXN_DATE_TIME());
                }
                if (transactionList.get(0).getTXN_CURRENCY_CODE() != null) {
                    Log.e("TXN_CURRENCY_CODE -> ", "=" + transactionList.get(0).getTXN_CURRENCY_CODE());
                }
                if (transactionList.get(0).getTRANSACTIONVARIANT() != null) {
                    Log.e("TRANSACTIONVARIANT -> ", "=" + transactionList.get(0).getTRANSACTIONVARIANT());
                }
                if (transactionList.get(0).getCHMOD() != null) {
                    Log.e("CHMOD -> ", "=" + transactionList.get(0).getCHMOD());
                }
                if (transactionList.get(0).getBANKNAME() != null) {
                    Log.e("BANKNAME -> ", "=" + transactionList.get(0).getBANKNAME());
                }
                if (transactionList.get(0).getCARDISSUER() != null) {
                    Log.e("CARDISSUER -> ", "=" + transactionList.get(0).getCARDISSUER());
                }
                if (transactionList.get(0).getFULLNAME() != null) {
                    Log.e("FULLNAME -> ", "=" + transactionList.get(0).getFULLNAME());
                }
                if (transactionList.get(0).getEMAIL() != null) {
                    Log.e("EMAIL -> ", "=" + transactionList.get(0).getEMAIL());
                }
                if (transactionList.get(0).getCONTACTNO() != null) {
                    Log.e("CONTACTNO -> ", "=" + transactionList.get(0).getCONTACTNO());
                }
                if (transactionList.get(0).getMERCHANT_NAME() != null) {
                    Log.e("MERCHANT_NAME -> ", "=" + transactionList.get(0).getMERCHANT_NAME());
                }
                if (transactionList.get(0).getSETTLEMENT_DATE() != null) {
                    Log.e("SETTLEMENT_DATE -> ", "=" + transactionList.get(0).getSETTLEMENT_DATE());
                }
                if (transactionList.get(0).getSURCHARGE() != null) {
                    Log.e("SURCHARGE -> ", "=" + transactionList.get(0).getSURCHARGE());
                }
                if (transactionList.get(0).getBILLEDAMOUNT() != null) {
                    Log.e("BILLEDAMOUNT -> ", "=" + transactionList.get(0).getBILLEDAMOUNT());
                }
                if (transactionList.get(0).getISRISK() != null) {
                    Log.e("ISRISK -> ", "=" + transactionList.get(0).getISRISK());
                }

                String transid = transactionList.get(0).getMERCHANTTRANSACTIONID();
                String apTransactionID = transactionList.get(0).getTRANSACTIONID();
                String amount = transactionList.get(0).getTRANSACTIONAMT();
                String transtatus = transactionList.get(0).getTRANSACTIONSTATUS();
                String message = transactionList.get(0).getSTATUSMSG();

                String merchantid = Constants.AIR_MI; // Please enter Merchant Id
                String username = Constants.AIR_UN;        // Please enter Username
                String sParam = transid + ":" + apTransactionID + ":" + amount + ":" + transtatus + ":" + message + ":" + merchantid + ":" + username;
                CRC32 crc = new CRC32();
                crc.update(sParam.getBytes());
                String sCRC = "" + crc.getValue();
                Log.e("Verified Hash ==", "Verified Hash= " + sCRC);

                if (sCRC.equalsIgnoreCase(transactionList.get(0).getSECUREHASH())) {
                    Log.e("Airpay Secure ->", " Secure hash mismatched");
                } else {
                    Log.e("Airpay Secure ->", " Secure hash matched");
                }


                //Log.e("Remaining Params-->>","Remaining Params-->>"+transactionList.get(0).getMyMap());

                // This code is to get remaining extra value pair.
                for (String key : transactionList.get(0).getMyMap().keySet()) {
                    Log.e("EXTRA-->>", "KEY: " + key + " VALUE: " + transactionList.get(0).getMyMap().get(key));
                    String extra_param= transactionList.get(0).getMyMap().get("PRI_ACC_NO_START"); // To replace key value as you want
                    Log.e("Extra Param -->","="+extra_param);
                    transactionList.get(0).getMyMap().get(key);
                }

                if(transactionList.get(0).getSTATUS().equalsIgnoreCase("200")||transactionList.get(0).getSTATUS().contains("success")){


                    paymentId = apTransactionID;

                    updateOrg(organization);
                    Toast.makeText(this, "Payment Successful: " + paymentId, Toast.LENGTH_SHORT).show();

                }

            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e("Error Message --- >>>", "Error Message --- >>> " + e.getMessage());
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
