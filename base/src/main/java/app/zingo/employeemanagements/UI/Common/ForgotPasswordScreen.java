package app.zingo.employeemanagements.UI.Common;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import app.zingo.employeemanagements.Model.Employee;
import app.zingo.employeemanagements.base.R;
import app.zingo.employeemanagements.UI.LandingScreen;
import app.zingo.employeemanagements.Utils.PreferenceHandler;
import app.zingo.employeemanagements.Utils.ThreadExecuter;
import app.zingo.employeemanagements.Utils.Util;
import app.zingo.employeemanagements.WebApi.EmployeeApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordScreen extends AppCompatActivity {

    TextInputEditText mNewPassword,mConfirm;
    AppCompatButton mSave;


    Employee profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{

            setContentView(R.layout.activity_forgot_password_screen);

            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Change Password");


            mNewPassword = findViewById(R.id.newpwd);
            mConfirm = findViewById(R.id.confirmpwd);
            mSave = findViewById(R.id.savePassword);

            Bundle bundle = getIntent().getExtras();

            if(bundle!=null){
                profile = (Employee)bundle.getSerializable("Employee");
            }else{

                Toast.makeText(this, "Please Try again Sometime", Toast.LENGTH_SHORT).show();
            }

            mSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(profile!=null){
                        validate();
                    }else{
                        Toast.makeText(ForgotPasswordScreen.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public void getProfile(final int id){

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setTitle("Please Wait..");
        dialog.show();

        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {

                final EmployeeApi subCategoryAPI = Util.getClient().create(EmployeeApi.class);
                Call<ArrayList<Employee>> getProf = subCategoryAPI.getProfileById(id);
                //Call<ArrayList<Blogs>> getBlog = blogApi.getBlogs();

                getProf.enqueue(new Callback<ArrayList<Employee>>() {

                    @Override
                    public void onResponse(Call<ArrayList<Employee>> call, Response<ArrayList<Employee>> response) {

                        if(dialog != null)
                        {
                            dialog.dismiss();
                        }
                        if (response.code() == 200)
                        {
                            System.out.println("Inside api");



                            if(response.body()!=null){
                                profile = response.body().get(0);
                            }

                        }else{
                            Toast.makeText(ForgotPasswordScreen.this, "Failed due to status code"+response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Employee>> call, Throwable t) {

                        if(dialog != null)
                        {
                            dialog.dismiss();
                        }
                        Toast.makeText(ForgotPasswordScreen.this, "Something went wrong due to "+t.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

            }

        });
    }

    public void validate(){


        String newPwd = mNewPassword.getText().toString();
        String cnfrmPwd = mConfirm.getText().toString();

        if(newPwd.isEmpty()){

            Toast.makeText(this, "New Password is required", Toast.LENGTH_SHORT).show();

        }else if(cnfrmPwd.isEmpty()){

            Toast.makeText(this, "Confirm password is required", Toast.LENGTH_SHORT).show();

        }else if(!newPwd.isEmpty()&&!cnfrmPwd.isEmpty()&&!newPwd.equalsIgnoreCase(cnfrmPwd)){

            Toast.makeText(this, "Password does not match", Toast.LENGTH_SHORT).show();

        }else{
            Employee employee = profile;
            profile.setPassword(cnfrmPwd);
            profile.setAppOpen(false);
            updateProfile(employee);
        }
    }


    public void updateProfile(final Employee employee){

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setTitle("Updaitng Password");
        dialog.show();

        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {

                final EmployeeApi subCategoryAPI = Util.getClient().create(EmployeeApi.class);
                Call<Employee> getProf = subCategoryAPI.updateEmployee(employee.getEmployeeId(),employee);
                //Call<ArrayList<Blogs>> getBlog = blogApi.getBlogs();

                getProf.enqueue(new Callback<Employee>() {

                    @Override
                    public void onResponse(Call<Employee> call, Response<Employee> response) {

                        if(dialog != null)
                        {
                            dialog.dismiss();
                        }
                        if (response.code() == 200||response.code()==201||response.code()==204)
                        {
                            PreferenceHandler.getInstance(ForgotPasswordScreen.this).clear();

                            Intent log = new Intent(ForgotPasswordScreen.this, LandingScreen.class);
                            log.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            log.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            Toast.makeText(ForgotPasswordScreen.this,"Logout",Toast.LENGTH_SHORT).show();
                            startActivity(log);
                            finish();

                        }else{
                            Toast.makeText(ForgotPasswordScreen.this, "Failed due to status code"+response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Employee> call, Throwable t) {

                        if(dialog != null)
                        {
                            dialog.dismiss();
                        }
                        Toast.makeText(ForgotPasswordScreen.this, "Something went wrong due to "+t.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

            }

        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id)
        {
            case android.R.id.home:

                ForgotPasswordScreen.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}

