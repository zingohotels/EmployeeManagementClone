package app.zingo.employeemanagements.UI;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import app.zingo.employeemanagements.Custom.MyEditText;
import app.zingo.employeemanagements.Custom.MyRegulerText;
import app.zingo.employeemanagements.FireBase.SharedPrefManager;
import app.zingo.employeemanagements.Model.Departments;
import app.zingo.employeemanagements.Model.Employee;
import app.zingo.employeemanagements.Model.Organization;
import app.zingo.employeemanagements.R;
import app.zingo.employeemanagements.UI.Admin.DashBoardAdmin;
import app.zingo.employeemanagements.UI.Employee.DashBoardEmployee;
import app.zingo.employeemanagements.UI.NewAdminDesigns.AdminNewMainScreen;
import app.zingo.employeemanagements.Utils.Constants;
import app.zingo.employeemanagements.Utils.PreferenceHandler;
import app.zingo.employeemanagements.Utils.ThreadExecuter;
import app.zingo.employeemanagements.Utils.Util;
import app.zingo.employeemanagements.WebApi.DepartmentApi;
import app.zingo.employeemanagements.WebApi.EmployeeApi;
import app.zingo.employeemanagements.WebApi.OrganizationApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LandingScreen extends AppCompatActivity {

    TextView mSignIn,mSupport;
    MyEditText mEmail,mPassword;
    MyRegulerText mSignInButton,mGetStarted,mContactUs;
    CheckBox mShowPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{

            setContentView(R.layout.activity_landing_screen);

            mSupport = (TextView)findViewById(R.id.landing_support);
            mEmail = (MyEditText)findViewById(R.id.landing_email);
            mPassword = (MyEditText)findViewById(R.id.landing_password);
            mSignInButton = (MyRegulerText)findViewById(R.id.buttonsignin);
            mGetStarted = (MyRegulerText)findViewById(R.id.button_get_started);
            mContactUs = (MyRegulerText)findViewById(R.id.button_contact_us);
            mShowPwd = (CheckBox) findViewById(R.id.show_hide_password);

            String token = SharedPrefManager.getInstance(LandingScreen.this).getDeviceToken();

            mContactUs.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent contact = new Intent(LandingScreen.this,ContactUsScreen.class);
                    startActivity(contact);
                }
            });

            mSupport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent support = new Intent(LandingScreen.this,SupportScreen.class);
                    startActivity(support);
                }
            });

            mGetStarted.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent started = new Intent(LandingScreen.this,SignUpOptioins.class);
                    startActivity(started);
                }
            });

            mSignInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    validateField();

                }
            });

            mShowPwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton button,
                                             boolean isChecked) {

                    // If it is checked then show password else hide password
                    if (isChecked) {

                        mShowPwd.setText("Hide Password");// change checkbox text

                        mPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                        mPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());// show password
                    } else {
                        mShowPwd.setText("Show Password");// change checkbox text

                        mPassword.setInputType(InputType.TYPE_CLASS_TEXT
                                | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        mPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());// hide password

                    }

                }
            });



        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void validateField(){

        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();

        if(email==null||email.isEmpty()){
            Toast.makeText(this, "Email is required", Toast.LENGTH_SHORT).show();
        }else if(password==null||password.isEmpty()){
            Toast.makeText(this, "Password is required", Toast.LENGTH_SHORT).show();
        }else{

            Employee employee = new Employee();
            employee.setPassword(password);

            employee.setEmail(email);
            loginEmployee(employee);

        }
    }

    private void loginEmployee( final Employee p){

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait..");
        progressDialog.setCancelable(false);
        progressDialog.show();


        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {


                EmployeeApi apiService = Util.getClient().create(EmployeeApi.class);


                Call<ArrayList<Employee>> call = apiService.getEmployeeforLogin(p);

                call.enqueue(new Callback<ArrayList<Employee>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Employee>> call, Response<ArrayList<Employee>> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                        int statusCode = response.code();
                        if (progressDialog != null)
                            progressDialog.dismiss();
                        if (statusCode == 200 || statusCode == 201) {

                            ArrayList<Employee> dto1 = response.body();//-------------------should not be list------------
                            if (dto1!=null && dto1.size()!=0) {
                                Employee dto = dto1.get(0);


                                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(LandingScreen.this);
                                SharedPreferences.Editor spe = sp.edit();
                                spe.putInt(Constants.USER_ID, dto.getEmployeeId());
                                PreferenceHandler.getInstance(LandingScreen.this).setUserId(dto.getEmployeeId());
                                PreferenceHandler.getInstance(LandingScreen.this).setManagerId(dto.getManagerId());
                                PreferenceHandler.getInstance(LandingScreen.this).setUserRoleUniqueID(dto.getUserRoleId());
                                PreferenceHandler.getInstance(LandingScreen.this).setUserName(dto.getEmployeeName());
                                PreferenceHandler.getInstance(LandingScreen.this).setUserEmail(dto.getPrimaryEmailAddress());
                                PreferenceHandler.getInstance(LandingScreen.this).setUserFullName(dto.getEmployeeName());
                                spe.putString("FullName", dto.getEmployeeName());
                                spe.putString("Password", dto.getPassword());
                                spe.putString("Email", dto.getPrimaryEmailAddress());
                                spe.putString("PhoneNumber", dto.getPhoneNumber());
                                spe.apply();

                                if(dto.getStatus().contains("Active")){

                                    getDepartment(dto.getDepartmentId(),dto);


                                }else if(dto.getStatus().equalsIgnoreCase("Disabled")){
                                    Toast.makeText(LandingScreen.this, "Your Account is Disabled", Toast.LENGTH_SHORT).show();
                                }else{

                                }



                            }else{
                                if (progressDialog != null)
                                    progressDialog.dismiss();
                                Toast.makeText(LandingScreen.this, "Login credentials are wrong..", Toast.LENGTH_SHORT).show();

                            }
                        }else {
                            if (progressDialog!=null)
                                progressDialog.dismiss();
                            Toast.makeText(LandingScreen.this, "Login failed due to status code:"+statusCode, Toast.LENGTH_SHORT).show();
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

    public void getDepartment(final int id,final Employee dto){

        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {

                final DepartmentApi subCategoryAPI = Util.getClient().create(DepartmentApi.class);
                Call<Departments> getProf = subCategoryAPI.getDepartmentById(id);
                //Call<ArrayList<Blogs>> getBlog = blogApi.getBlogs();

                getProf.enqueue(new Callback<Departments>() {

                    @Override
                    public void onResponse(Call<Departments> call, Response<Departments> response) {

                        if (response.code() == 200||response.code() == 201||response.code() == 204)
                        {
                            System.out.println("Inside api");

                            getCompany(response.body().getOrganizationId(),dto);


                        }else{

                            Intent i = new Intent(LandingScreen.this, AdminNewMainScreen.class);
                            i.putExtra("Profile",dto);
                            startActivity(i);
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<Departments> call, Throwable t) {

                    }
                });

            }

        });
    }

    public void getCompany(final int id,final Employee dto){

        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {

                final OrganizationApi subCategoryAPI = Util.getClient().create(OrganizationApi.class);
                Call<ArrayList<Organization>> getProf = subCategoryAPI.getOrganizationById(id);
                //Call<ArrayList<Blogs>> getBlog = blogApi.getBlogs();

                getProf.enqueue(new Callback<ArrayList<Organization>>() {

                    @Override
                    public void onResponse(Call<ArrayList<Organization>> call, Response<ArrayList<Organization>> response) {

                        if (response.code() == 200||response.code() == 201||response.code() == 204&&response.body().size()!=0)
                        {
                            Organization organization = response.body().get(0);
                            System.out.println("Inside api");
                            PreferenceHandler.getInstance(LandingScreen.this).setCompanyId(organization.getOrganizationId());
                            PreferenceHandler.getInstance(LandingScreen.this).setCompanyName(organization.getOrganizationName());
                            PreferenceHandler.getInstance(LandingScreen.this).setAppType(organization.getAppType());

                            PreferenceHandler.getInstance(LandingScreen.this).setAppType(organization.getAppType());
                            PreferenceHandler.getInstance(LandingScreen.this).setLicenseStartDate(organization.getLicenseStartDate());
                            PreferenceHandler.getInstance(LandingScreen.this).setLicenseEndDate(organization.getLicenseEndDate());
                            PreferenceHandler.getInstance(LandingScreen.this).setSignupDate(organization.getSignupDate());
                            PreferenceHandler.getInstance(LandingScreen.this).setPlanType(organization.getPlanType());
                            PreferenceHandler.getInstance(LandingScreen.this).setEmployeeLimit(organization.getEmployeeLimit());
                            PreferenceHandler.getInstance(LandingScreen.this).setPlanId(organization.getPlanId());

                            String licenseStartDate = organization.getLicenseStartDate();
                            String licenseEndDate = organization.getLicenseEndDate();
                            SimpleDateFormat smdf = new SimpleDateFormat("MM/dd/yyyy");

                            if(organization.getAppType()!=null&&organization.getAppType().equalsIgnoreCase("Trial")){

                                try{

                                    if((smdf.parse(licenseEndDate).getTime()<smdf.parse(smdf.format(new Date())).getTime())){


                                        if(PreferenceHandler.getInstance(LandingScreen.this).getUserRoleUniqueID()==2){

                                            popupUpgrade("Your free trial version expired.Upgrade the app to continue the services","Any quieries about Plans contact our Customer Support team");

                                        }else{

                                            popupUpgrade("Your free trial version expired.Upgrade the app to continue the services","Ask your management to Upgrade the app version.");

                                        }

                                    }else{
                                        if(PreferenceHandler.getInstance(LandingScreen.this).getUserRoleUniqueID()==2){
                                            Intent i = new Intent(LandingScreen.this, AdminNewMainScreen.class);
                                            //Intent i = new Intent(LandingScreen.this, DashBoardEmployee.class);
                                            i.putExtra("Profile",dto);

                                            startActivity(i);
                                            finish();
                                        }else{
                                            //Intent i = new Intent(LandingScreen.this, DashBoardAdmin.class);
                                            Intent i = new Intent(LandingScreen.this, DashBoardEmployee.class);
                                            i.putExtra("Profile",dto);
                                            i.putExtra("Organization",organization);
                                            startActivity(i);
                                            finish();
                                        }
                                    }

                                }catch (Exception e){
                                    e.printStackTrace();
                                }

                            }else if(organization.getAppType()!=null&&organization.getAppType().equalsIgnoreCase("Paid")){

                                if(PreferenceHandler.getInstance(LandingScreen.this).getUserRoleUniqueID()==2){
                                    Intent i = new Intent(LandingScreen.this, AdminNewMainScreen.class);
                                    //Intent i = new Intent(LandingScreen.this, DashBoardEmployee.class);
                                    i.putExtra("Profile",dto);

                                    startActivity(i);
                                    finish();
                                }else{
                                    //Intent i = new Intent(LandingScreen.this, DashBoardAdmin.class);
                                    Intent i = new Intent(LandingScreen.this, DashBoardEmployee.class);
                                    i.putExtra("Profile",dto);
                                    i.putExtra("Organization",organization);
                                    startActivity(i);
                                    finish();
                                }

                            }





                        }else{

                            if(PreferenceHandler.getInstance(LandingScreen.this).getUserRoleUniqueID()==2){
                                Intent i = new Intent(LandingScreen.this, AdminNewMainScreen.class);
                                //Intent i = new Intent(LandingScreen.this, DashBoardEmployee.class);
                                i.putExtra("Profile",dto);
                                startActivity(i);
                                finish();
                            }else{
                                //Intent i = new Intent(LandingScreen.this, DashBoardAdmin.class);
                                Intent i = new Intent(LandingScreen.this, DashBoardEmployee.class);
                                i.putExtra("Profile",dto);
                                startActivity(i);
                                finish();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Organization>> call, Throwable t) {

                    }
                });

            }

        });
    }

    public void popupUpgrade(final String text,final String days){

        try{

            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(LandingScreen.this);
            LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View views = inflater.inflate(R.layout.app_upgrade_pop, null);

            builder.setView(views);

            final Button mPaid = (Button) views.findViewById(R.id.paid_version_upgrade);
            final MyRegulerText mCompanyName = (MyRegulerText) views.findViewById(R.id.company_name_upgrade);
            final MyRegulerText mText = (MyRegulerText) views.findViewById(R.id.alert_message_upgrade);
            final MyRegulerText mDay = (MyRegulerText) views.findViewById(R.id.day_count_upgrade);

            final android.support.v7.app.AlertDialog dialogs = builder.create();
            dialogs.show();
            dialogs.setCanceledOnTouchOutside(true);

            mCompanyName.setText("Dear "+PreferenceHandler.getInstance(LandingScreen.this).getCompanyName());
            mText.setText(""+text);
            mDay.setText(""+days);


            if(PreferenceHandler.getInstance(LandingScreen.this).getUserRoleUniqueID()==2){



            }else{

                mPaid.setVisibility(View.GONE);

            }
            mPaid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    dialogs.dismiss();

                }
            });









        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
