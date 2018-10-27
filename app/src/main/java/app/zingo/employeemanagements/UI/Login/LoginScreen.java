package app.zingo.employeemanagements.UI.Login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import app.zingo.employeemanagements.FireBase.SharedPrefManager;
import app.zingo.employeemanagements.Model.Departments;
import app.zingo.employeemanagements.Model.Employee;
import app.zingo.employeemanagements.Model.EmployeeImages;
import app.zingo.employeemanagements.Model.Organization;
import app.zingo.employeemanagements.R;
import app.zingo.employeemanagements.UI.Admin.DashBoardAdmin;
import app.zingo.employeemanagements.UI.Employee.DashBoardEmployee;
import app.zingo.employeemanagements.UI.Landing.SplashScreen;
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

public class LoginScreen extends AppCompatActivity {
     TextInputEditText mEmail,mPassword;
     AppCompatButton mLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{

            setContentView(R.layout.activity_login_screen);

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN |
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

            mEmail = (TextInputEditText)findViewById(R.id.email);
            mPassword = (TextInputEditText)findViewById(R.id.password);

            mLogin = (AppCompatButton)findViewById(R.id.loginAccount);

            String token = SharedPrefManager.getInstance(LoginScreen.this).getDeviceToken();

            System.out.println("Splash Token  = "+token);


            mLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    validate();
                }
            });


        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void validate(){

        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();

        if(email==null||email.isEmpty()){

            Toast.makeText(this, "Please enter Email", Toast.LENGTH_SHORT).show();

        }else if(password==null||password.isEmpty()){

            Toast.makeText(this, "Please enter Password", Toast.LENGTH_SHORT).show();

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


                                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(LoginScreen.this);
                                SharedPreferences.Editor spe = sp.edit();
                                spe.putInt(Constants.USER_ID, dto.getEmployeeId());
                                PreferenceHandler.getInstance(LoginScreen.this).setUserId(dto.getEmployeeId());
                                PreferenceHandler.getInstance(LoginScreen.this).setManagerId(dto.getManagerId());
                                PreferenceHandler.getInstance(LoginScreen.this).setUserRoleUniqueID(dto.getUserRoleId());
                                PreferenceHandler.getInstance(LoginScreen.this).setUserName(dto.getEmployeeName());
                                PreferenceHandler.getInstance(LoginScreen.this).setUserEmail(dto.getPrimaryEmailAddress());
                                PreferenceHandler.getInstance(LoginScreen.this).setUserFullName(dto.getEmployeeName());
                                spe.putString("FullName", dto.getEmployeeName());
                                spe.putString("Password", dto.getPassword());
                                spe.putString("Email", dto.getPrimaryEmailAddress());
                                spe.putString("PhoneNumber", dto.getPhoneNumber());
                                spe.apply();

                                if(dto.getStatus().contains("Active")){

                                    getDepartment(dto.getDepartmentId(),dto);


                                }else if(dto.getStatus().equalsIgnoreCase("Disabled")){
                                    Toast.makeText(LoginScreen.this, "Your Account is Disabled", Toast.LENGTH_SHORT).show();
                                }else{

                                }



                            }else{
                                if (progressDialog != null)
                                    progressDialog.dismiss();
                                Toast.makeText(LoginScreen.this, "Login credentials are wrong..", Toast.LENGTH_SHORT).show();

                            }
                        }else {
                            if (progressDialog!=null)
                                progressDialog.dismiss();
                            Toast.makeText(LoginScreen.this, "Login failed due to status code:"+statusCode, Toast.LENGTH_SHORT).show();
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

                            Intent i = new Intent(LoginScreen.this, DashBoardAdmin.class);
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
                Call<Organization> getProf = subCategoryAPI.getOrganizationById(id);
                //Call<ArrayList<Blogs>> getBlog = blogApi.getBlogs();

                getProf.enqueue(new Callback<Organization>() {

                    @Override
                    public void onResponse(Call<Organization> call, Response<Organization> response) {

                        if (response.code() == 200||response.code() == 201||response.code() == 204)
                        {
                            System.out.println("Inside api");
                            PreferenceHandler.getInstance(LoginScreen.this).setCompanyId(response.body().getOrganizationId());
                            PreferenceHandler.getInstance(LoginScreen.this).setCompanyName(response.body().getOrganizationName());

                            if(PreferenceHandler.getInstance(LoginScreen.this).getUserRoleUniqueID()==2){
                                Intent i = new Intent(LoginScreen.this, DashBoardAdmin.class);
                                //Intent i = new Intent(LoginScreen.this, DashBoardEmployee.class);
                                i.putExtra("Profile",dto);
                                startActivity(i);
                                finish();
                            }else{
                                //Intent i = new Intent(LoginScreen.this, DashBoardAdmin.class);
                                Intent i = new Intent(LoginScreen.this, DashBoardEmployee.class);
                                i.putExtra("Profile",dto);
                                startActivity(i);
                                finish();
                            }



                        }else{

                            if(PreferenceHandler.getInstance(LoginScreen.this).getUserRoleUniqueID()==2){
                                Intent i = new Intent(LoginScreen.this, DashBoardAdmin.class);
                                //Intent i = new Intent(LoginScreen.this, DashBoardEmployee.class);
                                i.putExtra("Profile",dto);
                                startActivity(i);
                                finish();
                            }else{
                                //Intent i = new Intent(LoginScreen.this, DashBoardAdmin.class);
                                Intent i = new Intent(LoginScreen.this, DashBoardEmployee.class);
                                i.putExtra("Profile",dto);
                                startActivity(i);
                                finish();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Organization> call, Throwable t) {

                    }
                });

            }

        });
    }
}
