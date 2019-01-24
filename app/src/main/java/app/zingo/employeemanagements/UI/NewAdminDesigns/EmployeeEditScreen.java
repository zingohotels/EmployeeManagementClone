package app.zingo.employeemanagements.UI.NewAdminDesigns;

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
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

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
import app.zingo.employeemanagements.UI.Common.ChangePasswordScreen;
import app.zingo.employeemanagements.UI.Employee.CreateEmployeeScreen;
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

public class EmployeeEditScreen extends AppCompatActivity {

    TextInputEditText mName,mDob,mDoj,mPrimaryEmail,mSecondaryEmail,
            mMobile,mDesignation,mSalary;
    EditText mAddress;
    CheckBox mLocationCondition;
    Spinner mDepartment;
    RadioButton mMale,mFemale,mOthers;
    AppCompatButton mCreate;

    ArrayList<Departments> departmentData;

    Employee employees;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
            setContentView(R.layout.activity_employee_edit_screen);

            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Update Employee");

            mName = (TextInputEditText)findViewById(R.id.name);
            mDob = (TextInputEditText)findViewById(R.id.dob);
            mDoj = (TextInputEditText)findViewById(R.id.doj);
            mDesignation = (TextInputEditText)findViewById(R.id.designation);
            mSalary = (TextInputEditText)findViewById(R.id.salary);
            mPrimaryEmail = (TextInputEditText)findViewById(R.id.email);
            mSecondaryEmail = (TextInputEditText)findViewById(R.id.semail);
            mMobile = (TextInputEditText)findViewById(R.id.mobile);
            mLocationCondition = (CheckBox) findViewById(R.id.location_condition);

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

            Bundle bundle = getIntent().getExtras();

            if(bundle!=null){
                employees = (Employee)bundle.getSerializable("Employee");

                if(employees!=null){
                    setupData(employees);
                }
            }


        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void setupData(Employee employee){

        mName.setText(""+employee.getEmployeeName());

        String dob = employee.getDateOfBirth();
        String doj = employee.getDateOfJoining();

        if(dob.contains("T")){

            String dojs[] = dob.split("T");

            try {
                Date afromDate = new SimpleDateFormat("yyyy-MM-dd").parse(dojs[0]);
                dob = new SimpleDateFormat("MMM dd,yyyy").format(afromDate);
                mDob.setText(""+dob);

            } catch (ParseException e) {
                e.printStackTrace();
            }


        }

        if(doj.contains("T")){

            String dojs[] = doj.split("T");

            try {
                Date afromDate = new SimpleDateFormat("yyyy-MM-dd").parse(dojs[0]);
                doj = new SimpleDateFormat("MMM dd,yyyy").format(afromDate);
                mDoj.setText(""+doj);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        //mDesignation.setText();
        boolean location = employee.isLocationOn();

        if(location){
            mLocationCondition.setChecked(true);
        }
        mSalary.setText(""+employee.getSalary());
        mPrimaryEmail.setText(""+employee.getPrimaryEmailAddress());
        mSecondaryEmail.setText(""+employee.getAlternateEmailAddress());
        mMobile.setText(""+employee.getPhoneNumber());
        mAddress.setText(""+employee.getAddress());

        getDepartment(PreferenceHandler.getInstance(EmployeeEditScreen.this).getCompanyId(),employee.getDepartmentId());

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

        }else if(address.isEmpty()) {

            Toast.makeText(this, "Address is required", Toast.LENGTH_SHORT).show();

        }else if(!mMale.isChecked()&&!mFemale.isChecked()&&!mOthers.isChecked()){

            Toast.makeText(this, "Please Select Gender", Toast.LENGTH_SHORT).show();

        }else{



            Employee employee = employees;
            employee.setEmployeeName(name);
            employee.setAddress(address);

            if(mLocationCondition.isChecked()){
                employee.setLocationOn(true);
            }else{
                employee.setLocationOn(false);
            }
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

            employee.setDepartmentId(departmentData.get(mDepartment.getSelectedItemPosition()).getDepartmentId());


            employee.setStatus("Active");
            employee.setUserRoleId(1);

            Designations designations = new Designations();
            designations.setDesignationTitle(designation);
            designations.setDescription(designation);
            addDesignations(designations,employee);


        }

    }

    private void getDepartment(final int id,final int deptId){

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

                                int value = 0;

                                for(int i=0;i<departmentsList.size();i++){

                                    if(!departmentsList.get(i).getDepartmentName().equalsIgnoreCase("Founders")){

                                        departmentData.add(departmentsList.get(i));
                                        if(departmentsList.get(i).getDepartmentId()==deptId){
                                            value = departmentData.size()-1;
                                        }
                                    }
                                }

                                if(departmentData!=null&&departmentData.size()!=0){

                                    DepartmentSpinnerAdapter arrayAdapter = new DepartmentSpinnerAdapter(EmployeeEditScreen.this, departmentData);
                                    mDepartment.setAdapter(arrayAdapter);
                                    mDepartment.setSelection(value);

                                }



                            }
                            else
                            {


                            }
                        }
                        else
                        {

                            Toast.makeText(EmployeeEditScreen.this,response.message(),Toast.LENGTH_SHORT).show();
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
                            employee.setManagerId(PreferenceHandler.getInstance(EmployeeEditScreen.this).getUserId());
                            updateProfile(employee);


                        }




                    }else {
                        Toast.makeText(EmployeeEditScreen.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(EmployeeEditScreen.this, "Failed Due to "+t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());
            }
        });



    }

    public void updateProfile(final Employee employee){

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setTitle("Updaitng...");
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

                            EmployeeEditScreen.this.finish();

                        }else{
                            Toast.makeText(EmployeeEditScreen.this, "Failed due to status code"+response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Employee> call, Throwable t) {

                        if(dialog != null)
                        {
                            dialog.dismiss();
                        }
                        Toast.makeText(EmployeeEditScreen.this, "Something went wrong due to "+t.getMessage(), Toast.LENGTH_SHORT).show();

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

                EmployeeEditScreen.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
