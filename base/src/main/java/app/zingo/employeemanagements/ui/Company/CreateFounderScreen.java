package app.zingo.employeemanagements.ui.Company;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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

import app.zingo.employeemanagements.Custom.MyEditText;
import app.zingo.employeemanagements.Custom.MyTextView;
import app.zingo.employeemanagements.model.Departments;
import app.zingo.employeemanagements.model.Designations;
import app.zingo.employeemanagements.model.Employee;
import app.zingo.employeemanagements.model.Organization;
import app.zingo.employeemanagements.ui.landing.InternalServerErrorScreen;
import app.zingo.employeemanagements.ui.NewAdminDesigns.AdminNewMainScreen;
import app.zingo.employeemanagements.ui.newemployeedesign.EmployeeNewMainScreen;
import app.zingo.employeemanagements.utils.Constants;
import app.zingo.employeemanagements.utils.PreferenceHandler;
import app.zingo.employeemanagements.utils.ThreadExecuter;
import app.zingo.employeemanagements.utils.Util;
import app.zingo.employeemanagements.WebApi.DepartmentApi;
import app.zingo.employeemanagements.WebApi.DesignationsAPI;
import app.zingo.employeemanagements.WebApi.EmployeeApi;
import app.zingo.employeemanagements.WebApi.OrganizationApi;
import app.zingo.employeemanagements.base.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateFounderScreen extends AppCompatActivity {

    MyEditText mName,mPrimaryEmail,mSecondaryEmail,mMobile,mPassword,mConfirm;//mDob,mDoj,
    MyEditText mAddress;
    RadioButton mMale,mFemale,mOthers;
    MyTextView mCreate;

    Organization organization;
    String phone;


    CheckBox mShowPwd;

    private String current = "";
    private String ddmmyyyy = "DDMMYYYY";
    private Calendar cal = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{

            setContentView(R.layout.activity_new_create_founder);


            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Founder/Owner Signup");

            mName = findViewById(R.id.name);
           /* mDob = (MyEditText)findViewById(R.id.dob);
            mDoj = (MyEditText)findViewById(R.id.doj);*/
            mPrimaryEmail = findViewById(R.id.email);
            mSecondaryEmail = findViewById(R.id.semail);
            mMobile = findViewById(R.id.mobile);
            mPassword = findViewById(R.id.password);
            mConfirm = findViewById(R.id.confirmpwd);

            mAddress = findViewById(R.id.address);

            mMale = findViewById(R.id.founder_male);
            mFemale = findViewById(R.id.founder_female);
            mOthers = findViewById(R.id.founder_other);

            mCreate = findViewById(R.id.createFounder);
            mShowPwd = findViewById(R.id.show_hide_password);

            Bundle bundle = getIntent().getExtras();

            if(bundle!=null){

                phone = bundle.getString("PhoneNumber");
            }else{
                phone = PreferenceHandler.getInstance(CreateFounderScreen.this).getPhoneNumber();
            }


            mMobile.setText(phone);
            mMobile.setEnabled(false);
            /*mDob.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //openDatePicker(mDob);
                }
            });*/



           /* mDob.addTextChangedListener(new TextWatcher() {



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



          /*  mDoj.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    openDatePicker(mDoj);
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

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void designations(){

        try{

            Designations designations = new Designations();
            designations.setDesignationTitle("Founder");
            designations.setDescription("The owner or operator of a foundry");
            addDesignations(designations);


        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void addDesignations(final Designations designations) {



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

                            PreferenceHandler.getInstance(CreateFounderScreen.this).setDesignationId(s.getDesignationId());


                        }




                    }else {
                        Toast.makeText(CreateFounderScreen.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText( CreateFounderScreen.this , "Failed due to Bad Internet Connection" , Toast.LENGTH_SHORT ).show( );
                Log.e("TAG", t.toString());
            }
        });



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
       /* String dob = mDob.getText().toString();
        String doj = mDoj.getText().toString();*/
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

        }/*else if(secondary.isEmpty()){

            Toast.makeText(this, "Secondary Email is required", Toast.LENGTH_SHORT).show();

        }*/else if(mobile.isEmpty()){

            Toast.makeText(this, "Mobile is required", Toast.LENGTH_SHORT).show();

        }else if(password.isEmpty()){

            Toast.makeText(this, "Password is required", Toast.LENGTH_SHORT).show();

        }else if(confirm.isEmpty()){

            Toast.makeText(this, "Confirm Password is required", Toast.LENGTH_SHORT).show();

        }/*else if(address.isEmpty()) {

            Toast.makeText(this, "Address is required", Toast.LENGTH_SHORT).show();

        }*/else if(!password.isEmpty()&&!confirm.isEmpty()&&!password.equals(confirm)){

            Toast.makeText(this, "Confirm password should be same as Password", Toast.LENGTH_SHORT).show();
        }else if(!mMale.isChecked()&&!mFemale.isChecked()&&!mOthers.isChecked()){

            Toast.makeText(this, "Please Select Gender", Toast.LENGTH_SHORT).show();

        }else{

            Employee employee = new Employee();
            employee.setEmployeeName(name);

            if(address!=null&&!address.isEmpty()){
                employee.setAddress(address);
            }

            employee.setUserRoleId(2);
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
               // Date fdate = sdf.parse(dob);

                String from1 = simpleDateFormat.format(new Date());
                employee.setDateOfBirth(from1);

               //fdate = sdf.parse(doj);

                from1 = simpleDateFormat.format(new Date());
                employee.setDateOfJoining(from1);



            } catch (Exception e) {
                e.printStackTrace();
            }

            employee.setPrimaryEmailAddress(primary);
            if(secondary!=null&&!secondary.isEmpty()){
                employee.setAlternateEmailAddress(secondary);
            }

            employee.setPhoneNumber(mobile);
            employee.setPassword(password);
            //employee.setDepartmentId(1);
            employee.setDepartmentId(PreferenceHandler.getInstance(CreateFounderScreen.this).getDepartmentId());
            Designations designations = new Designations();
            designations.setDesignationTitle("Founder");
            designations.setDescription("The owner or operator of a foundry");
            employee.setDesignation(designations);
            employee.setStatus("Active");
            employee.setUserRoleId(2);

            checkUserByEmailId(employee);

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

                        Employee dto = response.body();

                        if(dto!=null){

                            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(CreateFounderScreen.this);
                            SharedPreferences.Editor spe = sp.edit();
                            spe.putInt(Constants.USER_ID, dto.getEmployeeId());
                            PreferenceHandler.getInstance(CreateFounderScreen.this).setUserId(dto.getEmployeeId());
                            PreferenceHandler.getInstance(CreateFounderScreen.this).setLocationOn(dto.isLocationOn());
                            PreferenceHandler.getInstance(CreateFounderScreen.this).setDataOn(dto.isDataOn());
                            PreferenceHandler.getInstance(CreateFounderScreen.this).setManagerId(dto.getManagerId());
                            PreferenceHandler.getInstance(CreateFounderScreen.this).setUserRoleUniqueID(dto.getUserRoleId());
                            PreferenceHandler.getInstance(CreateFounderScreen.this).setUserName(dto.getEmployeeName());
                            PreferenceHandler.getInstance(CreateFounderScreen.this).setUserEmail(dto.getPrimaryEmailAddress());
                            PreferenceHandler.getInstance(CreateFounderScreen.this).setUserFullName(dto.getEmployeeName());
                            spe.putString("FullName", dto.getEmployeeName());
                            spe.putString("Password", dto.getPassword());
                            spe.putString("Email", dto.getPrimaryEmailAddress());
                            spe.putString("PhoneNumber", dto.getPhoneNumber());
                            spe.apply();

                            Intent i = new Intent(CreateFounderScreen.this, AdminNewMainScreen.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                            finish();
                           /* PreferenceHandler.getInstance(CreateFounderScreen.this).setUserFullName(s.getEmployeeName());
                            PreferenceHandler.getInstance(CreateFounderScreen.this).setUserName(s.getEmployeeName());
                            PreferenceHandler.getInstance(CreateFounderScreen.this).setUserEmail(s.getPrimaryEmailAddress());*/


                        }




                    }else {
                        Toast.makeText(CreateFounderScreen.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText( CreateFounderScreen.this , "Failed due to Bad Internet Connection" , Toast.LENGTH_SHORT ).show( );
                Log.e("TAG", t.toString());
            }
        });



    }

    private void checkUserByEmailId(final Employee userProfile){


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
                                Toast.makeText(CreateFounderScreen.this, "Email already Exists", Toast.LENGTH_SHORT).show();


                            }
                            else
                            {
                                    checkUserByPhone(userProfile);
                            }
                        }
                        else
                        {

                            Toast.makeText(CreateFounderScreen.this,response.message(),Toast.LENGTH_SHORT).show();
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

    private void checkUserByPhone(final Employee userProfile){

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
                                Toast.makeText(CreateFounderScreen.this, "Mobile already Exists", Toast.LENGTH_SHORT).show();

                            }
                            else
                            {

                                try {
                                    addEmployee(userProfile);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        else
                        {

                            Toast.makeText(CreateFounderScreen.this,response.message(),Toast.LENGTH_SHORT).show();
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

    public void getDepartment(final int id,final Employee dto) {

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

                            try {
                                PreferenceHandler.getInstance(CreateFounderScreen.this).setCompanyId(response.body().getOrganizationId());
                                //PreferenceHandler.getInstance(LandingScreen.this).setDepartmentName(response.body().getDepartmentName());
                                getCompany(response.body().getOrganizationId(),dto);

                            } catch (Exception e) {
                                e.printStackTrace();
                                Intent i = new Intent(CreateFounderScreen.this, InternalServerErrorScreen.class);

                                startActivity(i);

                            }


                        }else{

                            Intent i = new Intent(CreateFounderScreen.this, AdminNewMainScreen.class);
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

    public void getCompany(final int id,final Employee dto) {

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
                            PreferenceHandler.getInstance(CreateFounderScreen.this).setCompanyId(organization.getOrganizationId());
                            PreferenceHandler.getInstance(CreateFounderScreen.this).setCompanyName(organization.getOrganizationName());
                            PreferenceHandler.getInstance(CreateFounderScreen.this).setAppType(organization.getAppType());

                            PreferenceHandler.getInstance(CreateFounderScreen.this).setAppType(organization.getAppType());
                            PreferenceHandler.getInstance(CreateFounderScreen.this).setLicenseStartDate(organization.getLicenseStartDate());
                            PreferenceHandler.getInstance(CreateFounderScreen.this).setCheckInTime(organization.getPlaceId());
                            PreferenceHandler.getInstance(CreateFounderScreen.this).setLicenseEndDate(organization.getLicenseEndDate());
                            PreferenceHandler.getInstance(CreateFounderScreen.this).setSignupDate(organization.getSignupDate());
                            PreferenceHandler.getInstance(CreateFounderScreen.this).setOrganizationLongi(organization.getLongitude());
                            PreferenceHandler.getInstance(CreateFounderScreen.this).setOrganizationLati(organization.getLatitude());
                            PreferenceHandler.getInstance(CreateFounderScreen.this).setPlanType(organization.getPlanType());
                            PreferenceHandler.getInstance(CreateFounderScreen.this).setEmployeeLimit(organization.getEmployeeLimit());
                            PreferenceHandler.getInstance(CreateFounderScreen.this).setPlanId(organization.getPlanId());
                            PreferenceHandler.getInstance(CreateFounderScreen.this).setResellerUserId(organization.getResellerProfileId());

                            String licenseStartDate = organization.getLicenseStartDate();
                            String licenseEndDate = organization.getLicenseEndDate();
                            SimpleDateFormat smdf = new SimpleDateFormat("MM/dd/yyyy");

                            if(PreferenceHandler.getInstance(CreateFounderScreen.this).getUserRoleUniqueID()==2){
                                Intent i = new Intent(CreateFounderScreen.this, AdminNewMainScreen.class);
                                //Intent i = new Intent(CreateFounderScreen.this, DashBoardEmployee.class);
                                i.putExtra("Profile",dto);

                                startActivity(i);
                                finish();
                            }else{
                                //Intent i = new Intent(CreateFounderScreen.this, DashBoardAdmin.class);
                                Intent i = new Intent(CreateFounderScreen.this, EmployeeNewMainScreen.class);
                                i.putExtra("Profile",dto);
                                i.putExtra("Organization",organization);

                                if(organization.isWorking()){

                                   /* Intent serviceIntent = new Intent(CreateFounderScreen.this,LocationSharingServices.class);
                                    startService(serviceIntent);*/
                                }
                                startActivity(i);
                                finish();
                            }





                        }else{

                            if(PreferenceHandler.getInstance(CreateFounderScreen.this).getUserRoleUniqueID()==2){
                                Intent i = new Intent(CreateFounderScreen.this, AdminNewMainScreen.class);
                                //Intent i = new Intent(CreateFounderScreen.this, DashBoardEmployee.class);
                                i.putExtra("Profile",dto);
                                startActivity(i);
                                finish();
                            }else{
                                //Intent i = new Intent(CreateFounderScreen.this, DashBoardAdmin.class);
                                Intent i = new Intent(CreateFounderScreen.this, EmployeeNewMainScreen.class);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id)
        {
            case android.R.id.home:

                CreateFounderScreen.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
