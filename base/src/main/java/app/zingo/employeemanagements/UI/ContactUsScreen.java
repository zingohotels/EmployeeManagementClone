package app.zingo.employeemanagements.UI;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hbb20.CountryCodePicker;

import app.zingo.employeemanagements.Custom.MyRegulerText;
import app.zingo.employeemanagements.Model.EmailData;
import app.zingo.employeemanagements.base.R;
import app.zingo.employeemanagements.Utils.ThreadExecuter;
import app.zingo.employeemanagements.Utils.Util;
import app.zingo.employeemanagements.WebApi.SendEmailAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactUsScreen extends AppCompatActivity {

    LinearLayout mInfoLay,mCompanyLay,mInquiryLay;
    MyRegulerText mInfoNext,mCompanyNext,mCompanyPrev,mInquiryPrev,mInquirySubmit;
    TextInputEditText mName,mJob,mEmail,mPhoneNumber,mBusinessName,mEmployeeCount;
    CountryCodePicker mCCP,mCountry;
    TextInputEditText mComments;

    LinearLayout mWhatsapp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            setContentView(R.layout.activity_contact_us_screen);

            mInfoLay = findViewById(R.id.info_layout);
            mCompanyLay = findViewById(R.id.company_layout);
            mInquiryLay = findViewById(R.id.inqiury_layout);

            mInfoNext = findViewById(R.id.buttonInfoNext);
            mCompanyNext = findViewById(R.id.buttonCompanyNext);
            mCompanyPrev = findViewById(R.id.buttonCompanyPrevious);
            mInquirySubmit = findViewById(R.id.buttonInquiryNext);
            mInquiryPrev = findViewById(R.id.buttonInquiryPrevious);
            mWhatsapp = findViewById(R.id.whatsapp_open);

            mName = findViewById(R.id.user_name);
            mJob = findViewById(R.id.user_job);
            mEmail = findViewById(R.id.user_email);
            mPhoneNumber = findViewById(R.id.phone_number);
            mBusinessName = findViewById(R.id.company_name);
            mEmployeeCount = findViewById(R.id.company_employee);

            mCCP = findViewById(R.id.ccp);
            mCountry = findViewById(R.id.ccp_country);

            mComments = findViewById(R.id.location_text);

            mWhatsapp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String digits = "\\d+";

                    try {

                        Uri uri = Uri.parse("whatsapp://send?phone=+919986128021" );
                        Intent i = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(i);
                    }
                    catch (ActivityNotFoundException e){
                        e.printStackTrace();
                        Toast.makeText(ContactUsScreen.this, "WhatsApp not installed.", Toast.LENGTH_SHORT).show();
                    }

                }
            });


            mInfoNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String name = mName.getText().toString();
                    String job = mJob.getText().toString();
                    String email = mEmail.getText().toString();
                    String mobile = mPhoneNumber.getText().toString();
                    String mobileCountry = mCCP.getSelectedCountryCodeWithPlus();

                    if(name==null||name.isEmpty()){
                        Toast.makeText(ContactUsScreen.this, "Your name is required", Toast.LENGTH_SHORT).show();
                    }else if(job==null||job.isEmpty()){
                        Toast.makeText(ContactUsScreen.this, "Job Title is required", Toast.LENGTH_SHORT).show();
                    }else if(email==null||email.isEmpty()){
                        Toast.makeText(ContactUsScreen.this, "Email is required", Toast.LENGTH_SHORT).show();
                    }else if(mobile==null||mobile.isEmpty()){
                        Toast.makeText(ContactUsScreen.this, "Phone number is required", Toast.LENGTH_SHORT).show();
                    }else{
                        mInfoLay.setVisibility(View.GONE);
                        mCompanyLay.setVisibility(View.VISIBLE);
                        mInquiryLay.setVisibility(View.GONE);
                    }


                }
            });

            mCompanyNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String business = mPhoneNumber.getText().toString();
                    String employee = mPhoneNumber.getText().toString();
                    String country = mCountry.getSelectedCountryName();

                    if(business==null||business.isEmpty()){
                        Toast.makeText(ContactUsScreen.this, "Business name is required", Toast.LENGTH_SHORT).show();
                    }else if(employee==null||employee.isEmpty()){
                        Toast.makeText(ContactUsScreen.this, "Employee count is required", Toast.LENGTH_SHORT).show();
                    }else{
                        mInfoLay.setVisibility(View.GONE);
                        mCompanyLay.setVisibility(View.GONE);
                        mInquiryLay.setVisibility(View.VISIBLE);
                    }



                }
            });

            mCompanyPrev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mInfoLay.setVisibility(View.VISIBLE);
                    mCompanyLay.setVisibility(View.GONE);
                    mInquiryLay.setVisibility(View.GONE);
                }
            });

            mInquiryPrev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mInfoLay.setVisibility(View.GONE);
                    mCompanyLay.setVisibility(View.VISIBLE);
                    mInquiryLay.setVisibility(View.GONE);

                }
            });

            mInquirySubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    validateField();

                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public void validateField(){

        String name = mName.getText().toString();
       // String job = mJob.getText().toString();
        String email = mEmail.getText().toString();
        String mobile = mPhoneNumber.getText().toString();
        String mobileCountry = mCCP.getSelectedCountryCodeWithPlus();
        String business = mBusinessName.getText().toString();
        String employee = mEmployeeCount.getText().toString();
        String country = mCountry.getSelectedCountryName();
        String inquiry = mComments.getText().toString();

        if(name==null||name.isEmpty()){
            Toast.makeText(this, "Your name is required", Toast.LENGTH_SHORT).show();
        }/*else if(job==null||job.isEmpty()){
            Toast.makeText(this, "Job Title is required", Toast.LENGTH_SHORT).show();
        }*/else if(email==null||email.isEmpty()){
            Toast.makeText(this, "Email is required", Toast.LENGTH_SHORT).show();
        }else if(mobile==null||mobile.isEmpty()){
            Toast.makeText(this, "Phone number is required", Toast.LENGTH_SHORT).show();
        }else if(business==null||business.isEmpty()){
            Toast.makeText(this, "Business name is required", Toast.LENGTH_SHORT).show();
        }else if(employee==null||employee.isEmpty()){
            Toast.makeText(this, "Employee count is required", Toast.LENGTH_SHORT).show();
        }else if(inquiry==null||inquiry.isEmpty()){
            Toast.makeText(this, "Please ask your quires", Toast.LENGTH_SHORT).show();
        }else{

            String body = "<html><head><style>table " +
                    "{border-collapse: collapse;}" +
                    "table, td, th {" +
                    "border: 1px solid black;} table th{\n" +
                    "  color:#0000ff;\n" +
                    "}" +
                    "</style></head>" +
                    "<body>" +
                    "<h2>Dear Team Members,</h2>" +
                    "<p><br>Please find below Client Details for Employee management App Request</p></br></br>"+
                    "<br><b>Client Name: </b>"+name+
                  /*  "</br><br><b>Job Title: </b>"+job+*/
                    "</br><br><b>Email: </b>"+email+
                    "</br><br><b>Phone Number: </b>"+mobileCountry+""+mobile+
                    "</br><br><b>Business Name: </b>"+business+
                    "</br><br><b>No of Employees: </b>"+employee+
                    "</br><br><b>Country: </b>"+country+
                    "</br><br><b>Location: </b>"+inquiry+
                    "</br><p><b>Thanks</b></p><br><br></body></html>";

            EmailData emailData = new EmailData();
            emailData.setEmailAddress("abhinav@zingohotels.com");
            emailData.setBody(body);
            emailData.setSubject("Employee Management App Call Back Request");
            emailData.setUserName("nishar@zingohotels.com");
            emailData.setPassword("Razin@1993");
            emailData.setFromName("Nishar");
            emailData.setFromEmail("nishar@zingohotels.com");

            if(Util.isNetworkAvailable(ContactUsScreen.this)){
                sendEmailAutomatic(emailData);
            }else{
                Toast.makeText(this, "Please check your Internet Connection", Toast.LENGTH_LONG).show();
            }


        }
    }

    private void sendEmailAutomatic(final EmailData emailData) {

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Sending Email..");
        dialog.setCancelable(false);
        dialog.show();


        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {


                SendEmailAPI travellerApi = Util.getClient().create(SendEmailAPI.class);
                Call<String> response = travellerApi.sendEmail(emailData);

                response.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {


                        try{

                            if(dialog != null && dialog.isShowing())
                            {
                                dialog.dismiss();
                            }
                            System.out.println(response.code());
                            if(response.code() == 200||response.code() == 201)
                            {
                                if(response.body() != null)
                                {


                                    if(response.body().equalsIgnoreCase("Email Sent Successfully")){
                                        showAlertBox();
                                    }else{
                                        Toast.makeText(ContactUsScreen.this, "Something went wrong. So please contact through Call", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }else{
                                Toast.makeText(ContactUsScreen.this, "Something went wrong due to "+response.code()+". So please contact through Call", Toast.LENGTH_LONG).show();
                            }
                        }catch (Exception e){
                            if(dialog != null && dialog.isShowing())
                            {
                                dialog.dismiss();
                            }
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        if(dialog != null && dialog.isShowing())
                        {
                            dialog.dismiss();
                        }
                    }
                });
            }
        });
    }

    private void showAlertBox() {

        AlertDialog.Builder builder = new AlertDialog.Builder(ContactUsScreen.this);
        builder.setMessage("Thank you for showing interest in our app\nOur Sales team will get in touch with you within 2 working days.");
        builder.setCancelable(false);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                ContactUsScreen.this.finish();
            }
        });

        AlertDialog thankyoudialog = builder.create();
        thankyoudialog.show();
    }
}
