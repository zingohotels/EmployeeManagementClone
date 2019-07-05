package app.zingo.employeemanagements.UI.Employee;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import app.zingo.employeemanagements.Adapter.DepartmentSpinnerAdapter;
import app.zingo.employeemanagements.Adapter.ManagerSpinnerAdapter;
import app.zingo.employeemanagements.Adapter.ShiftAdapter;
import app.zingo.employeemanagements.Adapter.ShiftSpinnerAdapter;
import app.zingo.employeemanagements.Custom.MyEditText;
import app.zingo.employeemanagements.Model.Departments;
import app.zingo.employeemanagements.Model.Designations;
import app.zingo.employeemanagements.Model.Employee;
import app.zingo.employeemanagements.Model.WorkingDay;
import app.zingo.employeemanagements.UI.NewAdminDesigns.ShiftScreenList;
import app.zingo.employeemanagements.Utils.PreferenceHandler;
import app.zingo.employeemanagements.Utils.ThreadExecuter;
import app.zingo.employeemanagements.Utils.Util;
import app.zingo.employeemanagements.WebApi.DepartmentApi;
import app.zingo.employeemanagements.WebApi.EmployeeApi;
import app.zingo.employeemanagements.WebApi.OrganizationTimingsAPI;
import app.zingo.employeemanagements.base.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateEmployeeScreen extends AppCompatActivity {

    TextInputEditText mName,mPrimaryEmail,mSecondaryEmail,
            mMobile,mPassword,mConfirm,mDesignation,mSalary;
    EditText mAddress;
    MyEditText mDob,mDoj;
    Spinner mDepartment,mtoReport,mShift;
    RadioButton mMale,mFemale,mOthers;
    CheckBox mLocationCondition,mCheckTime;
    AppCompatButton mCreate;
    Switch mAdmin;

    ArrayList<Departments> departmentData;
    ArrayList<Employee> employees;
    ArrayList<WorkingDay> workingDays;

    private String current = "";
    private String ddmmyyyy = "DDMMYYYY";
    private Calendar cal = Calendar.getInstance();

    int orgId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{

            setContentView(R.layout.activity_create_employee_screen);

            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Create Employee");

            Bundle bun = getIntent().getExtras();

            if(bun!=null){

                orgId = bun.getInt("BranchId",0);
            }



            mAdmin = findViewById(R.id.admin_switch);
            mName = findViewById(R.id.name);
            mDob = findViewById(R.id.dob);
            mDoj = findViewById(R.id.doj);
            mDesignation = findViewById(R.id.designation);
            mSalary = findViewById(R.id.salary);
            mPrimaryEmail = findViewById(R.id.email);
            mSecondaryEmail = findViewById(R.id.semail);
            mMobile = findViewById(R.id.mobile);
            mPassword = findViewById(R.id.password);
            mConfirm = findViewById(R.id.confirmpwd);
            mDepartment = findViewById(R.id.android_material_design_spinner);
            mtoReport = findViewById(R.id.managers_list);
            mShift = findViewById(R.id.shift_list);

            mAddress = findViewById(R.id.address);
            mLocationCondition = findViewById(R.id.location_condition);
            mCheckTime = findViewById(R.id.time_condition);
            mMale = findViewById(R.id.founder_male);
            mFemale = findViewById(R.id.founder_female);
            mOthers = findViewById(R.id.founder_other);

            mCreate = findViewById(R.id.createFounder);

            mAdmin.setTextOff("No");
            mAdmin.setTextOn("Yes");



            mDob.addTextChangedListener(new TextWatcher() {



                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    if (!s.toString().equals(current)) {
                        String clean = s.toString().replaceAll("[^\\d.]|\\.", "");
                        String cleanC = current.replaceAll("[^\\d.]|\\.", "");

                        int cl = clean.length();
                        int sel = cl;
                        for (int i = 2; i <= cl && i < 6; i += 2) {
                            sel++;
                        }
                        //Fix for pressing delete next to a forward slash
                        if (clean.equals(cleanC)) sel--;

                        if (clean.length() < 8){
                            clean = clean + ddmmyyyy.substring(clean.length());
                        }else{
                            //This part makes sure that when we finish entering numbers
                            //the date is correct, fixing it otherwise
                            int day  = Integer.parseInt(clean.substring(0,2));
                            int mon  = Integer.parseInt(clean.substring(2,4));
                            int year = Integer.parseInt(clean.substring(4,8));

                            String currentYear = new SimpleDateFormat("yyyy").format(new Date());

                            int years = Integer.parseInt(currentYear);

                            mon = mon < 1 ? 1 : mon > 12 ? 12 : mon;
                            cal.set(Calendar.MONTH, mon-1);
                            year = (year<1900)?1900:(year>years)?years:year;
                            cal.set(Calendar.YEAR, year);
                            // ^ first set year for the line below to work correctly
                            //with leap years - otherwise, date e.g. 29/02/2012
                            //would be automatically corrected to 28/02/2012

                            day = (day > cal.getActualMaximum(Calendar.DATE))? cal.getActualMaximum(Calendar.DATE):day;
                            clean = String.format("%02d%02d%02d",day, mon, year);
                        }

                        clean = String.format("%s/%s/%s", clean.substring(0, 2),
                                clean.substring(2, 4),
                                clean.substring(4, 8));

                        sel = sel < 0 ? 0 : sel;
                        current = clean;
                        mDob.setText(current);
                        mDob.setSelection(sel < current.length() ? sel : current.length());
                    }

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });


            mDoj.addTextChangedListener(new TextWatcher() {



                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    if (!s.toString().equals(current)) {
                        String clean = s.toString().replaceAll("[^\\d.]|\\.", "");
                        String cleanC = current.replaceAll("[^\\d.]|\\.", "");

                        int cl = clean.length();
                        int sel = cl;
                        for (int i = 2; i <= cl && i < 6; i += 2) {
                            sel++;
                        }
                        //Fix for pressing delete next to a forward slash
                        if (clean.equals(cleanC)) sel--;

                        if (clean.length() < 8){
                            clean = clean + ddmmyyyy.substring(clean.length());
                        }else{
                            //This part makes sure that when we finish entering numbers
                            //the date is correct, fixing it otherwise
                            int day  = Integer.parseInt(clean.substring(0,2));
                            int mon  = Integer.parseInt(clean.substring(2,4));
                            int year = Integer.parseInt(clean.substring(4,8));

                            String currentYear = new SimpleDateFormat("yyyy").format(new Date());

                            int years = Integer.parseInt(currentYear);

                            mon = mon < 1 ? 1 : mon > 12 ? 12 : mon;
                            cal.set(Calendar.MONTH, mon-1);
                            year = (year<1900)?1900:(year>years)?years:year;
                            cal.set(Calendar.YEAR, year);
                            // ^ first set year for the line below to work correctly
                            //with leap years - otherwise, date e.g. 29/02/2012
                            //would be automatically corrected to 28/02/2012

                            day = (day > cal.getActualMaximum(Calendar.DATE))? cal.getActualMaximum(Calendar.DATE):day;
                            clean = String.format("%02d%02d%02d",day, mon, year);
                        }

                        clean = String.format("%s/%s/%s", clean.substring(0, 2),
                                clean.substring(2, 4),
                                clean.substring(4, 8));

                        sel = sel < 0 ? 0 : sel;
                        current = clean;
                        mDoj.setText(current);
                        mDoj.setSelection(sel < current.length() ? sel : current.length());
                    }

                }

                @Override
                public void afterTextChanged(Editable s) {

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

            if(mLocationCondition.isChecked()){

                mLocationCondition.setText("Check-in Location (If Checked Employee has to Check-in from Office Only)");
            }else{
                mLocationCondition.setText("Check-in Location");
            }

            if(mCheckTime.isChecked()){
                mCheckTime.setText("Check-in Time (If Checked Employee has to Check-in within Office Check-in Hours only)");
            }else{
                mCheckTime.setText("Check-in Time");
            }

            mLocationCondition.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if(isChecked){
                        mLocationCondition.setText("Check-in Location (If Checked Employee has to Check-in from Office Only)");
                    }else{
                        mLocationCondition.setText("Check-in Location");
                    }
                }
            });

            mCheckTime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if(isChecked){
                        mCheckTime.setText("Check-in Time (If Checked Employee has to Check-in within Office Check-in Hours only)");
                    }else{
                        mCheckTime.setText("Check-in Time");
                    }
                }
            });

            if(orgId!=0){

                getDepartment(orgId);
                getmanagerProfile(orgId);
                getShiftTimings(orgId);
            }else{
                getDepartment(PreferenceHandler.getInstance(CreateEmployeeScreen.this).getCompanyId());
                getmanagerProfile(PreferenceHandler.getInstance(CreateEmployeeScreen.this).getCompanyId());
                getShiftTimings(PreferenceHandler.getInstance(CreateEmployeeScreen.this).getCompanyId());
            }



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

    public void validate() {

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

        }else if(!password.isEmpty()&&!confirm.isEmpty()&&!password.equals(confirm)){

            Toast.makeText(this, "Confirm password should be same as Password", Toast.LENGTH_SHORT).show();
        }else if(!mMale.isChecked()&&!mFemale.isChecked()&&!mOthers.isChecked()){

            Toast.makeText(this, "Please Select Gender", Toast.LENGTH_SHORT).show();

        }else{



            Employee employee = new Employee();
            employee.setEmployeeName(name);
            if(address!=null&&!address.isEmpty()){
                employee.setAddress(address);
            }

            if(mLocationCondition.isChecked()){
                employee.setLocationOn(false);
            }else{
                employee.setLocationOn(true);
            }

            if(mCheckTime.isChecked()){
                employee.setDataOn(false);
            }else{
                employee.setDataOn(true);
            }


            if(mMale.isChecked()){
                employee.setGender("Male");
            }else if(mFemale.isChecked()){

                employee.setGender("Female");
            }else if(mOthers.isChecked()){

                employee.setGender("Others");
            }

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
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
            if(secondary!=null&&!secondary.isEmpty()){
                employee.setAlternateEmailAddress(secondary);
            }

            employee.setPhoneNumber(mobile);
            employee.setPassword(password);
            employee.setDepartmentId(departmentData.get(mDepartment.getSelectedItemPosition()).getDepartmentId());

            if(employees!=null&&employees.size()!=0){
                employee.setManagerId(employees.get(mtoReport.getSelectedItemPosition()).getEmployeeId());
            }else{
                employee.setManagerId(0);
            }

            if(workingDays!=null&&workingDays.size()!=0){
                employee.setDeviceModel(""+workingDays.get(mShift.getSelectedItemPosition()).getOrganizationTimingId());
            }else{
                employee.setDeviceModel(""+0);
            }



            employee.setStatus("Active");

            if(mAdmin.isChecked()){
                employee.setUserRoleId(9);
            }else{
                employee.setUserRoleId(1);
            }


            Designations designations = new Designations();
            designations.setDesignationTitle(designation);
            designations.setDescription(designation);

            checkUserByEmailId(employee,designations);

        }

    }

    public void addEmployee(final Employee employee) {



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

    private void checkUserByEmailId(final Employee userProfile, final Designations designations){


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

    private void checkUserByPhone(final Employee userProfile, final Designations designations){

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

                                    userProfile.setDesignation(designations);

                                    addEmployee(userProfile);
                                    //addDesignations(designations,userProfile);
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

    private void getmanagerProfile(final int id){


        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading...");
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
                        int statusCode = response.code();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {


                            if (progressDialog != null&&progressDialog.isShowing())
                                progressDialog.dismiss();
                            ArrayList<Employee> list = response.body();


                            if (list !=null && list.size()!=0) {

                                employees = list;
                                /*for(int i=0;i<list.size();i++){

                                    if(list.get(i).getUserRoleId()==9||list.get(i).getUserRoleId()==2){

                                        employees.add(list.get(i));

                                    }
                                }*/

                                if(employees!=null&&employees.size()!=0){
                                    Collections.sort(employees, Employee.compareEmployee);
                                    ManagerSpinnerAdapter arrayAdapter = new ManagerSpinnerAdapter(CreateEmployeeScreen.this, employees);
                                    mtoReport.setAdapter(arrayAdapter);


                                }else{
                                    Toast.makeText(CreateEmployeeScreen.this,"No Employees added",Toast.LENGTH_LONG).show();

                                }


                                //}

                            }else{
                                Toast.makeText(CreateEmployeeScreen.this,"No Employees added",Toast.LENGTH_LONG).show();

                            }

                        }else {


                            Toast.makeText(CreateEmployeeScreen.this, "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Employee>> call, Throwable t) {
                        // Log error here since request failed
                        if (progressDialog != null&&progressDialog.isShowing())
                            progressDialog.dismiss();
                        Log.e("TAG", t.toString());
                    }
                });
            }


        });
    }

    public void getShiftTimings(final int id) {



        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {

                final OrganizationTimingsAPI orgApi = Util.getClient().create(OrganizationTimingsAPI.class);
                Call<ArrayList<WorkingDay>> getProf = orgApi.getOrganizationTimingByOrgId(id);
                //Call<ArrayList<Blogs>> getBlog = blogApi.getBlogs();

                getProf.enqueue(new Callback<ArrayList<WorkingDay>>() {

                    @Override
                    public void onResponse(Call<ArrayList<WorkingDay>> call, Response<ArrayList<WorkingDay>> response) {



                        if (response.code() == 200||response.code() == 201||response.code() == 204)
                        {

                            workingDays = response.body();

                            if(workingDays!=null&&workingDays.size()!=0){

                                ShiftSpinnerAdapter arrayAdapter = new ShiftSpinnerAdapter(CreateEmployeeScreen.this, workingDays);
                                mShift.setAdapter(arrayAdapter);

                            }


                        }else{



                            Toast.makeText(CreateEmployeeScreen.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<WorkingDay>> call, Throwable t) {




                        Toast.makeText(CreateEmployeeScreen.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                    }
                });

            }

        });
    }
}
