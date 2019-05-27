package app.zingo.employeemanagements.UI;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import app.zingo.employeemanagements.Custom.CustomSpinner;
import app.zingo.employeemanagements.Custom.MyEditText;
import app.zingo.employeemanagements.Custom.MyTextView;
import app.zingo.employeemanagements.Model.Departments;
import app.zingo.employeemanagements.Model.Designations;
import app.zingo.employeemanagements.Model.Employee;
import app.zingo.employeemanagements.Model.Organization;
import app.zingo.employeemanagements.UI.Landing.InternalServerErrorScreen;
import app.zingo.employeemanagements.Utils.PreferenceHandler;
import app.zingo.employeemanagements.Utils.ThreadExecuter;
import app.zingo.employeemanagements.Utils.Util;
import app.zingo.employeemanagements.WebApi.DepartmentApi;
import app.zingo.employeemanagements.WebApi.DesignationsAPI;
import app.zingo.employeemanagements.WebApi.EmployeeApi;
import app.zingo.employeemanagements.WebApi.OrganizationApi;
import app.zingo.employeemanagements.base.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmployeeSignUp extends AppCompatActivity {

    /*TextInputEditText mName,mDob,mDoj,mPrimaryEmail,mSecondaryEmail,
            mMobile,mPassword,mConfirm,mOrganizationCode,mDesignation;*/

    MyEditText mName,mPrimaryEmail,mSecondaryEmail,
            mMobile,mPassword,mConfirm,mOrganizationCode,mDesignation;//,mDob,mDoj
    MyEditText mAddress;
    CardView mOrganizationCodeLayout;
    NestedScrollView mEmployeeLayout;
    CustomSpinner mDepartment;//mEmailEnd
    RadioButton mMale,mFemale,mOthers;
    MyTextView mCreate;
    AppCompatButton mVerifyCode;

    ArrayList<Departments> departmentData;

    String[] organizationEmail;

    CheckBox mShowPwd;

    private String current = "";
    private String ddmmyyyy = "DDMMYYYY";
    private Calendar cal = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{

            setContentView(R.layout.activity_new_employee_signup);

            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Employee Signup");



            mName = findViewById(R.id.name);
          /*  mDob = (MyEditText)findViewById(R.id.dob);
            mDoj = (MyEditText)findViewById(R.id.doj);*/
            mDesignation = findViewById(R.id.designation);
            mOrganizationCode = findViewById(R.id.organization_code);
            mOrganizationCodeLayout = findViewById(R.id.card);
            mEmployeeLayout = findViewById(R.id.employee_detail_layout);

            mPrimaryEmail = findViewById(R.id.email);
            mSecondaryEmail = findViewById(R.id.semail);
            mMobile = findViewById(R.id.mobile);
            mPassword = findViewById(R.id.password);
            mConfirm = findViewById(R.id.confirmpwd);
            mDepartment = findViewById(R.id.android_material_design_spinner);
          //  mEmailEnd = (Spinner) findViewById(R.id.primary_email_end);

            mAddress = findViewById(R.id.address);

            mMale = findViewById(R.id.founder_male);
            mFemale = findViewById(R.id.founder_female);
            mOthers = findViewById(R.id.founder_other);

            mCreate = findViewById(R.id.createFounder);
            mVerifyCode = findViewById(R.id.verify_org_code);

            mShowPwd = findViewById(R.id.show_hide_password);

            mMobile.setText(""+ PreferenceHandler.getInstance(EmployeeSignUp.this).getPhoneNumber());
            mMobile.setEnabled(false);

            mDepartment.setSpinnerEventsListener(new CustomSpinner.OnSpinnerEventsListener() {
                public void onSpinnerOpened() {
                    mDepartment.setSelected(true);
                }
                public void onSpinnerClosed() {
                    mDepartment.setSelected(false);
                }
            });

            /*mDob.addTextChangedListener(new TextWatcher() {



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
            });*/

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

            mVerifyCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try{
                        validateCode();
                    }catch (Exception e){
                        e.printStackTrace();
                    }

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

                        mConfirm.setInputType(InputType.TYPE_CLASS_TEXT);
                        mConfirm.setTransformationMethod(HideReturnsTransformationMethod.getInstance());// show password
                    } else {
                        mShowPwd.setText("Show Password");// change checkbox text

                        mPassword.setInputType(InputType.TYPE_CLASS_TEXT
                                | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        mPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());// hide password

                        mConfirm.setInputType(InputType.TYPE_CLASS_TEXT
                                | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        mConfirm.setTransformationMethod(PasswordTransformationMethod.getInstance());// hide password

                    }

                }
            });

       //

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void validateCode(){

        String organizationCode = mOrganizationCode.getText().toString();

        if(organizationCode.isEmpty()){

            Toast.makeText(this, "Organization Code should not be empty", Toast.LENGTH_SHORT).show();
        }else{

            String code = organizationCode.replaceAll("[^0-9]", "");
            try {
                getCompany(Integer.parseInt(code),organizationCode);
            } catch (Exception e) {
                e.printStackTrace();
                Intent i = new Intent(EmployeeSignUp.this, InternalServerErrorScreen.class);

                startActivity(i);
            }

        }
    }

    public void openDatePicker(final MyEditText tv) {
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
        /*String dob = mDob.getText().toString();
        String doj = mDoj.getText().toString();*/
        String designation = mDesignation.getText().toString();
        String primary = mPrimaryEmail.getText().toString();
        String secondary = mSecondaryEmail.getText().toString();
        String mobile = mMobile.getText().toString();
        String password = mPassword.getText().toString();
        String confirm = mConfirm.getText().toString();
        String address = mAddress.getText().toString();

        if(name.isEmpty()){

            Toast.makeText(this, "Name is required", Toast.LENGTH_SHORT).show();

        }/*else if(dob.isEmpty()){

            Toast.makeText(this, "DOB is required", Toast.LENGTH_SHORT).show();

        }else if(doj.isEmpty()){

            Toast.makeText(this, "Founded date is required", Toast.LENGTH_SHORT).show();

        }*/else if(primary.isEmpty()){

            Toast.makeText(this, "Primary Email is required", Toast.LENGTH_SHORT).show();

        }else if(designation.isEmpty()){

            Toast.makeText(this, "Designation is required", Toast.LENGTH_SHORT).show();

        }else if(mobile.isEmpty()){

            Toast.makeText(this, "Mobile is required", Toast.LENGTH_SHORT).show();

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
                //Date fdate = sdf.parse(dob);

                String from1 = simpleDateFormat.format(new Date());
                employee.setDateOfBirth(from1);

               // fdate = sdf.parse(doj);

                from1 = simpleDateFormat.format(new Date());
                employee.setDateOfJoining(from1);



            } catch (Exception e) {
                e.printStackTrace();
            }

            employee.setPrimaryEmailAddress(primary);
            if(address!=null&&!address.isEmpty()){
                employee.setAlternateEmailAddress(secondary);
            }

            employee.setPhoneNumber(mobile);
            employee.setPassword(password);
            employee.setDepartmentId(departmentData.get(mDepartment.getSelectedItemPosition()).getDepartmentId());


            employee.setStatus("Deactive");
            employee.setUserRoleId(1);

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

                            Toast.makeText(EmployeeSignUp.this, "Employee Added Success fully", Toast.LENGTH_SHORT).show();


                            PreferenceHandler.getInstance(EmployeeSignUp.this).clear();
                            PreferenceHandler.getInstance(EmployeeSignUp.this).setUserId(s.getEmployeeId());

                            Intent i = new Intent(EmployeeSignUp.this, LandingScreen.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                            finish();



                        }




                    }else {
                        Toast.makeText(EmployeeSignUp.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(EmployeeSignUp.this, "Failed Due to "+t.getMessage(), Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(EmployeeSignUp.this, "Email already Exists", Toast.LENGTH_SHORT).show();


                            }
                            else
                            {
                                checkUserByPhone(userProfile,designations);
                            }
                        }
                        else
                        {

                            Toast.makeText(EmployeeSignUp.this,response.message(),Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(EmployeeSignUp.this, "Mobile already Exists", Toast.LENGTH_SHORT).show();

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

                            Toast.makeText(EmployeeSignUp.this,response.message(),Toast.LENGTH_SHORT).show();
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
    public void addDesignations(final Designations designations, final Employee employee) {



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
                            addEmployee(employee);


                        }




                    }else {
                        Toast.makeText(EmployeeSignUp.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(EmployeeSignUp.this, "Failed Due to "+t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());
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
                                ArrayList<String> depaName = new ArrayList<>();

                                for(int i=0;i<departmentsList.size();i++){

                                    if(!departmentsList.get(i).getDepartmentName().equalsIgnoreCase("Founders")){

                                        departmentData.add(departmentsList.get(i));
                                        depaName.add(departmentsList.get(i).getDepartmentName());
                                    }
                                }

                                if(departmentData!=null&&departmentData.size()!=0){

                                    ArrayAdapter adapter = new ArrayAdapter<>(EmployeeSignUp.this, R.layout.spinner_item_selected, depaName);
                                    adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

                                   // DepartmentSpinnerAdapter arrayAdapter = new DepartmentSpinnerAdapter(EmployeeSignUp.this, departmentData);
                                    mDepartment.setAdapter(adapter);

                                }



                            }
                            else
                            {


                            }
                        }
                        else
                        {

                            Toast.makeText(EmployeeSignUp.this,response.message(),Toast.LENGTH_SHORT).show();
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

    public void getCompany(final int id,final String code) {

        mVerifyCode.setText("Loading..");
        mVerifyCode.setEnabled(false);

        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {

                final OrganizationApi subCategoryAPI = Util.getClient().create(OrganizationApi.class);
                Call<ArrayList<Organization>> getProf = subCategoryAPI.getOrganizationById(id);
                //Call<ArrayList<Blogs>> getBlog = blogApi.getBlogs();

                getProf.enqueue(new Callback<ArrayList<Organization>>() {

                    @Override
                    public void onResponse(Call<ArrayList<Organization>> call, Response<ArrayList<Organization>> response) {

                        if ((response.code() == 200||response.code() == 201||response.code() == 204)&&response.body().size()!=0)
                        {

                            Organization organization = response.body().get(0);
                            System.out.println("Inside api");

                            if(organization!=null){
                                String orgName = organization.getOrganizationName();
                                String upToNCharacters="";

                                if(orgName!=null&&orgName.length()>=4){
                                    upToNCharacters = organization.getOrganizationName().substring(0, Math.min(organization.getOrganizationName().length(), 4));
                                }else if(orgName!=null&&orgName.length()<4){
                                    upToNCharacters = organization.getOrganizationName().substring(0, Math.min(organization.getOrganizationName().length(), orgName.length()));
                                }

                                //Toast.makeText(EmployeeSignUp.this, ""+upToNCharacters, Toast.LENGTH_SHORT).show();

                                if(code.equalsIgnoreCase(upToNCharacters+id)){

                                    mOrganizationCodeLayout.setVisibility(View.GONE);
                                    mEmployeeLayout.setVisibility(View.VISIBLE);

                                    /*String email = organization.getLocation();

                                    if(email!=null&&email.contains("@")){

                                        organizationEmail = email.split("@");

                                        EmailEndAdapter adapter = new EmailEndAdapter(EmployeeSignUp.this,organizationEmail);
                                        //mEmailEnd.setAdapter(adapter);

                                        *//*ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(EmployeeSignUp.this, R.layout.spinner_dropdown_item, organizationEmail );
                                        arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                                        mEmailEnd.setAdapter(arrayAdapter );*//*
                                    }*/

                                    getDepartment(id);
                                }else{
                                    Toast.makeText(EmployeeSignUp.this, "Organization is wrong", Toast.LENGTH_SHORT).show();
                                    mVerifyCode.setText("Verfiy Code Again");
                                    mVerifyCode.setEnabled(true);
                                }

                            }

                        }else{
                            Toast.makeText(EmployeeSignUp.this, "Organization is not found", Toast.LENGTH_SHORT).show();

                            mVerifyCode.setText("Verfiy Code Again");
                            mVerifyCode.setEnabled(true);
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Organization>> call, Throwable t) {

                        mVerifyCode.setText("Verfiy Code Again");
                        mVerifyCode.setEnabled(true);
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

                EmployeeSignUp.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
