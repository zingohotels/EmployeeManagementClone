package app.zingo.employeemanagements.UI.Employee;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


import app.zingo.employeemanagements.Adapter.DepartmentSpinnerAdapter;
import app.zingo.employeemanagements.Model.Departments;
import app.zingo.employeemanagements.Model.Designations;
import app.zingo.employeemanagements.Model.Employee;
import app.zingo.employeemanagements.R;
import app.zingo.employeemanagements.UI.Company.CreateFounderScreen;
import app.zingo.employeemanagements.UI.Login.LoginScreen;
import app.zingo.employeemanagements.Utils.PreferenceHandler;
import app.zingo.employeemanagements.Utils.ThreadExecuter;
import app.zingo.employeemanagements.Utils.Util;
import app.zingo.employeemanagements.WebApi.DepartmentApi;
import app.zingo.employeemanagements.WebApi.DesignationsAPI;
import app.zingo.employeemanagements.WebApi.EmployeeApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateEmployeeScreen extends AppCompatActivity {

    TextInputEditText mName,mDob,mDoj,mPrimaryEmail,mSecondaryEmail,
            mMobile,mPassword,mConfirm,mDesignation,mSalary;
    EditText mAddress;
    Spinner mDepartment;
    RadioButton mMale,mFemale,mOthers;
    AppCompatButton mCreate;

    ArrayList<Departments> departmentData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{

            setContentView(R.layout.activity_create_employee_screen);

            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Create Employee");

            mName = (TextInputEditText)findViewById(R.id.name);
            mDob = (TextInputEditText)findViewById(R.id.dob);
            mDoj = (TextInputEditText)findViewById(R.id.doj);
            mDesignation = (TextInputEditText)findViewById(R.id.designation);
            mSalary = (TextInputEditText)findViewById(R.id.salary);
            mPrimaryEmail = (TextInputEditText)findViewById(R.id.email);
            mSecondaryEmail = (TextInputEditText)findViewById(R.id.semail);
            mMobile = (TextInputEditText)findViewById(R.id.mobile);
            mPassword = (TextInputEditText)findViewById(R.id.password);
            mConfirm = (TextInputEditText)findViewById(R.id.confirmpwd);
            mDepartment = (Spinner) findViewById(R.id.android_material_design_spinner);

            mAddress = (EditText)findViewById(R.id.address);

            mMale = (RadioButton)findViewById(R.id.founder_male);
            mFemale = (RadioButton)findViewById(R.id.founder_female);
            mOthers = (RadioButton)findViewById(R.id.founder_other);

            mCreate = (AppCompatButton)findViewById(R.id.createFounder);

            mDob.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    openDatePicker(mDob);
                }
            });

            mDoj.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    openDatePicker(mDoj);
                }
            });

            mCreate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try{
                        validate();
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            });

            getDepartment(PreferenceHandler.getInstance(CreateEmployeeScreen.this).getCompanyId());

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void openDatePicker(final TextInputEditText tv) {
        // Get Current Date

        final Calendar c = Calendar.getInstance();
        int mYear  = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay   = c.get(Calendar.DAY_OF_MONTH);
        //launch datepicker modal
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        try {
                            Log.d("Date", "DATE SELECTED "+dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                            Calendar newDate = Calendar.getInstance();
                            newDate.set(year,monthOfYear,dayOfMonth);


                            String date1 = (monthOfYear + 1)  + "/" + (dayOfMonth) + "/" + year;

                            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy");



                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
                            try {
                                Date fdate = simpleDateFormat.parse(date1);

                                String from1 = sdf.format(fdate);

                                tv.setText(from1);


                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                        }
                        catch (Exception ex)
                        {
                            ex.printStackTrace();
                        }


                    }
                }, mYear, mMonth, mDay);


        datePickerDialog.show();

    }

    public void validate() throws Exception{

        String name = mName.getText().toString();
        String dob = mDob.getText().toString();
        String doj = mDoj.getText().toString();
        String designation = mDesignation.getText().toString();
        String salary = mSalary.getText().toString();
        String primary = mPrimaryEmail.getText().toString();
        String secondary = mSecondaryEmail.getText().toString();
        String mobile = mMobile.getText().toString();
        String password = mPassword.getText().toString();
        String confirm = mConfirm.getText().toString();
        String address = mAddress.getText().toString();

        if(name.isEmpty()){

            Toast.makeText(this, "Name is required", Toast.LENGTH_SHORT).show();

        }else if(dob.isEmpty()){

            Toast.makeText(this, "DOB is required", Toast.LENGTH_SHORT).show();

        }else if(doj.isEmpty()){

            Toast.makeText(this, "Founded date is required", Toast.LENGTH_SHORT).show();

        }else if(primary.isEmpty()){

            Toast.makeText(this, "Primary Email is required", Toast.LENGTH_SHORT).show();

        }else if(secondary.isEmpty()){

            Toast.makeText(this, "Secondary Email is required", Toast.LENGTH_SHORT).show();

        }else if(mobile.isEmpty()){

            Toast.makeText(this, "Mobile is required", Toast.LENGTH_SHORT).show();

        }else if(designation.isEmpty()){

            Toast.makeText(this, "Designation is required", Toast.LENGTH_SHORT).show();

        }else if(salary.isEmpty()){

            Toast.makeText(this, "Salary is required", Toast.LENGTH_SHORT).show();

        }else if(password.isEmpty()){

            Toast.makeText(this, "Password is required", Toast.LENGTH_SHORT).show();

        }else if(confirm.isEmpty()){

            Toast.makeText(this, "Confirm Password is required", Toast.LENGTH_SHORT).show();

        }else if(address.isEmpty()) {

            Toast.makeText(this, "Address is required", Toast.LENGTH_SHORT).show();

        }else if(!password.isEmpty()&&!confirm.isEmpty()&&!password.equals(confirm)){

            Toast.makeText(this, "Confirm password should be same as Password", Toast.LENGTH_SHORT).show();
        }else if(!mMale.isChecked()&&!mFemale.isChecked()&&!mOthers.isChecked()){

            Toast.makeText(this, "Please Select Gender", Toast.LENGTH_SHORT).show();

        }else{



            Employee employee = new Employee();
            employee.setEmployeeName(name);
            employee.setAddress(address);
            if(mMale.isChecked()){
                employee.setGender("Male");
            }else if(mFemale.isChecked()){

                employee.setGender("Female");
            }else if(mOthers.isChecked()){

                employee.setGender("Others");
            }

            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
            try {
                Date fdate = sdf.parse(dob);

                String from1 = simpleDateFormat.format(fdate);
                employee.setDateOfBirth(from1);

                fdate = sdf.parse(doj);

                from1 = simpleDateFormat.format(fdate);
                employee.setDateOfJoining(from1);



            } catch (ParseException e) {
                e.printStackTrace();
            }

            employee.setPrimaryEmailAddress(primary);
            employee.setSalary(Double.parseDouble(salary));
            employee.setAlternateEmailAddress(secondary);
            employee.setPhoneNumber(mobile);
            employee.setPassword(password);
            employee.setDepartmentId(departmentData.get(mDepartment.getSelectedItemPosition()).getDepartmentId());


            employee.setStatus("Active");
            employee.setUserRoleId(1);

            Designations designations = new Designations();
            designations.setDesignationTitle(designation);
            designations.setDescription(designation);

            checkUserByEmailId(employee,designations);

        }

    }

    public void addEmployee(final Employee employee) throws Exception{



        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Saving Details..");
        dialog.setCancelable(false);
        dialog.show();

        EmployeeApi apiService = Util.getClient().create(EmployeeApi.class);

        Call<Employee> call = apiService.addEmployee(employee);

        call.enqueue(new Callback<Employee>() {
            @Override
            public void onResponse(Call<Employee> call, Response<Employee> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {
                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201) {

                        Employee s = response.body();

                        if(s!=null){

                            Toast.makeText(CreateEmployeeScreen.this, "Employee Added Success fully", Toast.LENGTH_SHORT).show();


                            CreateEmployeeScreen.this.finish();



                        }




                    }else {
                        Toast.makeText(CreateEmployeeScreen.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
            public void onFailure(Call<Employee> call, Throwable t) {

                if(dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
                Toast.makeText(CreateEmployeeScreen.this, "Failed Due to "+t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());
            }
        });



    }

    private void checkUserByEmailId(final Employee userProfile,final Designations designations){


        userProfile.setEmail(userProfile.getPrimaryEmailAddress());


        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setTitle("Please wait..");
        dialog.show();


        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {


                EmployeeApi apiService =
                        Util.getClient().create(EmployeeApi.class);

                Call<ArrayList<Employee>> call = apiService.getUserByEmail(userProfile);

                call.enqueue(new Callback<ArrayList<Employee>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Employee>> call, Response<ArrayList<Employee>> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                        int statusCode = response.code();

                        if(dialog != null)
                        {
                            dialog.dismiss();
                        }

                        if(statusCode == 200 || statusCode == 204)
                        {

                            ArrayList<Employee> responseProfile = response.body();
                            if(responseProfile != null && responseProfile.size()!=0 )
                            {

                                mPrimaryEmail.setError("Email Exists");
                                Toast.makeText(CreateEmployeeScreen.this, "Email already Exists", Toast.LENGTH_SHORT).show();


                            }
                            else
                            {
                                checkUserByPhone(userProfile,designations);
                            }
                        }
                        else
                        {

                            Toast.makeText(CreateEmployeeScreen.this,response.message(),Toast.LENGTH_SHORT).show();
                        }
//                callGetStartEnd();
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Employee>> call, Throwable t) {
                        // Log error here since request failed

                        if(dialog != null)
                        {
                            dialog.dismiss();
                        }

                        Log.e("TAG", t.toString());
                    }
                });
            }
        });
    }

    private void checkUserByPhone(final Employee userProfile,final Designations designations){

        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {


                EmployeeApi apiService =
                        Util.getClient().create(EmployeeApi.class);

                Call<ArrayList<Employee>> call = apiService.getUserByPhone(userProfile.getPhoneNumber());

                call.enqueue(new Callback<ArrayList<Employee>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Employee>> call, Response<ArrayList<Employee>> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                        int statusCode = response.code();

                        if(statusCode == 200 || statusCode == 204)
                        {
                            ArrayList<Employee> responseProfile = response.body();
                            if(responseProfile != null && responseProfile.size()!=0 )
                            {
                                mMobile.setError("Number Already Exists");
                                Toast.makeText(CreateEmployeeScreen.this, "Mobile already Exists", Toast.LENGTH_SHORT).show();

                            }
                            else
                            {

                                try {

                                    addDesignations(designations,userProfile);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        else
                        {

                            Toast.makeText(CreateEmployeeScreen.this,response.message(),Toast.LENGTH_SHORT).show();
                        }
//                callGetStartEnd();
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Employee>> call, Throwable t) {
                        // Log error here since request failed

                        Log.e("TAG", t.toString());
                    }
                });
            }
        });
    }


    private void getDepartment(final int id){

        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {


                DepartmentApi apiService =
                        Util.getClient().create(DepartmentApi.class);

                Call<ArrayList<Departments>> call = apiService.getDepartmentByOrganization(id);

                call.enqueue(new Callback<ArrayList<Departments>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Departments>> call, Response<ArrayList<Departments>> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                        int statusCode = response.code();

                        if(statusCode == 200 || statusCode == 204)
                        {
                            ArrayList<Departments> departmentsList = response.body();
                            if(departmentsList != null && departmentsList.size()!=0 )
                            {

                                departmentData = new ArrayList<>();

                                for(int i=0;i<departmentsList.size();i++){

                                    if(!departmentsList.get(i).getDepartmentName().equalsIgnoreCase("Founders")){

                                        departmentData.add(departmentsList.get(i));
                                    }
                                }

                                if(departmentData!=null&&departmentData.size()!=0){

                                    DepartmentSpinnerAdapter arrayAdapter = new DepartmentSpinnerAdapter(CreateEmployeeScreen.this, departmentData);
                                    mDepartment.setAdapter(arrayAdapter);

                                }



                            }
                            else
                            {


                            }
                        }
                        else
                        {

                            Toast.makeText(CreateEmployeeScreen.this,response.message(),Toast.LENGTH_SHORT).show();
                        }
//                callGetStartEnd();
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Departments>> call, Throwable t) {
                        // Log error here since request failed

                        Log.e("TAG", t.toString());
                    }
                });
            }
        });
    }

    public void addDesignations(final Designations designations,final Employee employee) throws Exception{



        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Saving Details..");
        dialog.setCancelable(false);
        dialog.show();

        DesignationsAPI apiService = Util.getClient().create(DesignationsAPI.class);

        Call<Designations> call = apiService.addDesignations(designations);

        call.enqueue(new Callback<Designations>() {
            @Override
            public void onResponse(Call<Designations> call, Response<Designations> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {
                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201) {

                        Designations s = response.body();

                        if(s!=null){

                            employee.setDesignationId(s.getDesignationId());
                            employee.setManagerId(PreferenceHandler.getInstance(CreateEmployeeScreen.this).getUserId());
                            addEmployee(employee);


                        }




                    }else {
                        Toast.makeText(CreateEmployeeScreen.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
            public void onFailure(Call<Designations> call, Throwable t) {

                if(dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
                Toast.makeText(CreateEmployeeScreen.this, "Failed Due to "+t.getMessage(), Toast.LENGTH_SHORT).show();
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

                CreateEmployeeScreen.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
