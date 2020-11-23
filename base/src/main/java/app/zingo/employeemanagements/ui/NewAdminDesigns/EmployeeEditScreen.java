package app.zingo.employeemanagements.ui.NewAdminDesigns;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SwitchCompat;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import app.zingo.employeemanagements.adapter.DepartmentSpinnerAdapter;
import app.zingo.employeemanagements.adapter.ManagerSpinnerAdapter;
import app.zingo.employeemanagements.adapter.ShiftSpinnerAdapter;
import app.zingo.employeemanagements.Custom.MyEditText;
import app.zingo.employeemanagements.model.Departments;
import app.zingo.employeemanagements.model.Designations;
import app.zingo.employeemanagements.model.Employee;
import app.zingo.employeemanagements.model.WorkingDay;
import app.zingo.employeemanagements.utils.PreferenceHandler;
import app.zingo.employeemanagements.utils.ThreadExecuter;
import app.zingo.employeemanagements.utils.Util;
import app.zingo.employeemanagements.WebApi.DepartmentApi;
import app.zingo.employeemanagements.WebApi.DesignationsAPI;
import app.zingo.employeemanagements.WebApi.EmployeeApi;
import app.zingo.employeemanagements.WebApi.OrganizationTimingsAPI;
import app.zingo.employeemanagements.base.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmployeeEditScreen extends AppCompatActivity {
    TextInputEditText mName,mPrimaryEmail,mSecondaryEmail,
            mMobile,mDesignation,mSalary,mPassword,mConfirm,mNoWeekOff,mAddress;
    LinearLayout mWeekLay,mWeekContainer;
    MyEditText mDob,mDoj;
    CheckBox mLocationCondition,mCheckTime,mWeekOffCheck;
    Spinner mDepartment,mtoReport,mShift;
    SwitchCompat mAdmin,mActive;
    RadioButton mMale,mFemale,mOthers;
    TextView mCreate;
    ArrayList<Departments> departmentData;
    ArrayList< Employee > employeeList;
    ArrayList< WorkingDay > workingDays;
    Employee employees;
    private String current = "";
    private String ddmmyyyy = "DDMMYYYY";
    private Calendar cal = Calendar.getInstance();
    int orgId;
    boolean weekCondi = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
            setContentView(R.layout.activity_employee_edit_screen);

            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Update Employee");

            Bundle bun = getIntent().getExtras();

            if(bun!=null){

                orgId = bun.getInt("OrganizationId",0);
            }

            mAdmin = findViewById(R.id.admin_switch);
            mActive = findViewById(R.id.active_employee);
            mName = findViewById(R.id.name);
            mDob = findViewById(R.id.dob);
            mDoj = findViewById(R.id.doj);
            mDesignation = findViewById(R.id.designation);
            mSalary = findViewById(R.id.salary);
            mPrimaryEmail = findViewById(R.id.email);
            mSecondaryEmail = findViewById(R.id.semail);
            mMobile = findViewById(R.id.mobile);
            mLocationCondition = findViewById(R.id.location_condition);
            mCheckTime = findViewById(R.id.time_condition);
            mPassword = findViewById(R.id.password);
            mConfirm = findViewById(R.id.confirmpwd);

            mDepartment = findViewById(R.id.android_material_design_spinner);
            mtoReport = findViewById(R.id.managers_list);
            mShift = findViewById(R.id.shift_list);


            mAddress = findViewById(R.id.address);

            mMale = findViewById(R.id.founder_male);
            mFemale = findViewById(R.id.founder_female);
            mOthers = findViewById(R.id.founder_other);

            mCreate = findViewById(R.id.createFounder);

            mWeekOffCheck = findViewById(R.id.week_condition);
            mNoWeekOff = findViewById(R.id.week_off);
            mWeekLay = findViewById(R.id.week_lay);
            mWeekContainer = findViewById(R.id.container_week_off);



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

            if(mWeekOffCheck.isChecked()){
                mWeekOffCheck.setText("Custom Week-Off (If Checked Employee has to take week-off on particular day only.)");
                mWeekLay.setVisibility(View.VISIBLE);
            }else{
                mWeekOffCheck.setText("Custom Week-Off");
                mWeekLay.setVisibility(View.GONE);
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

            mWeekOffCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if(mWeekOffCheck.isChecked()){
                        mWeekOffCheck.setText("Custom Week-Off (If Checked Employee has to take week-off on particular day only.)");
                        mWeekLay.setVisibility(View.VISIBLE);
                    }else{
                        mWeekOffCheck.setText("Custom Week-Off");
                        mWeekLay.setVisibility(View.GONE);
                    }

                }
            });

            Bundle bundle = getIntent().getExtras();

            if(bundle!=null){
                employees = ( Employee )bundle.getSerializable("Employee");

                if(employees!=null){
                    setupData(employees);
                }
            }

            mNoWeekOff.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    if (weekCondi) {

                        String text = mNoWeekOff.getText().toString();

                        if (text != null && !text.isEmpty()) {

                            try {

                                int value = Integer.parseInt(text);

                                int child = mWeekContainer.getChildCount();

                                addView(value - child);
                                // addView(value);


                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else {

                            addView(0);

                        }


                    }

                }

                @Override
                public void afterTextChanged(Editable s) {

                    weekCondi = true;

                }
            });


        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void addView(final int value){

        if(value==0){

            mWeekContainer.removeAllViews();

        } else if (value < 0) {

            mWeekContainer.removeViews((mWeekContainer.getChildCount() + value), (mWeekContainer.getChildCount() - 1));

        }else{

            for(int i = 0; i<value; i++){

                LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View addView = layoutInflater.inflate(R.layout.container_week_off, null);
                final Spinner week = (Spinner)addView.findViewById(R.id.week);
                final Spinner weekday = (Spinner)addView.findViewById(R.id.week_day);

                mWeekContainer.addView(addView);


            }

        }



    }

    public void setupData( Employee employee){

        mName.setText(""+employee.getEmployeeName());

        String dob = employee.getDateOfBirth();
        String doj = employee.getDateOfJoining();

        String gender = employee.getGender();
        String weekList = employee.getDeviceAndroidVersion();
        if(employee.getUserRoleId()==9){

            mAdmin.setChecked(true);
        }else{
            mAdmin.setChecked(false);
        }

        String statusEmp = employee.getStatus();

        if(statusEmp!=null&&statusEmp.equalsIgnoreCase("Active")){

            mActive.setChecked(true);

        }else{

            mActive.setChecked(false);
        }

        if(gender!=null&&!gender.isEmpty()){

            if(gender.equalsIgnoreCase("Male")){

                mMale.setChecked(true);

            }else if(gender.equalsIgnoreCase("Female")){

                mFemale.setChecked(true);
            }else {

                mOthers.setChecked(true);
            }
        }

        if(weekList!=null&&!weekList.isEmpty()){

            if(weekList.contains(",")){

                String[] weekSep = weekList.split(",");

                if(weekSep.length!=0){

                    mWeekOffCheck.setChecked(true);
                    mWeekOffCheck.setText("Custom Week-Off (If Checked Employee has to take week-off on particular day only.)");
                    mWeekLay.setVisibility(View.VISIBLE);
                    weekCondi = false;
                    mNoWeekOff.setText("" + weekSep.length);

                    for (int j=0;j<weekSep.length;j++){

                        String inWeek = weekSep[j];
                        if(inWeek.contains("-")){
                            String[] inWeekSep = inWeek.split("-");
                            if(inWeekSep.length==2){

                                LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                final View addView = layoutInflater.inflate(R.layout.container_week_off, null);
                                final Spinner week = (Spinner)addView.findViewById(R.id.week);
                                final Spinner weekday = (Spinner)addView.findViewById(R.id.week_day);

                                String weekText = inWeekSep[0];
                                String weekdayText = inWeekSep[1];

                                if(weekText!=null&&!weekText.isEmpty()){

                                    if(weekText.equalsIgnoreCase("Week 1")){

                                        week.setSelection(0);

                                    }else if(weekText.equalsIgnoreCase("Week 2")){
                                        week.setSelection(1);

                                    }else if(weekText.equalsIgnoreCase("Week 3")){

                                        week.setSelection(2);

                                    }else if(weekText.equalsIgnoreCase("Week 4")){

                                        week.setSelection(3);

                                    }else if(weekText.equalsIgnoreCase("Week 5")){

                                        week.setSelection(4);

                                    }
                                }

                                if(weekdayText!=null&&!weekdayText.isEmpty()){

                                    if(weekdayText.equalsIgnoreCase("Sun")){

                                        weekday.setSelection(0);

                                    }else if(weekdayText.equalsIgnoreCase("Mon")){
                                        weekday.setSelection(1);

                                    }else if(weekdayText.equalsIgnoreCase("Tue")){

                                        weekday.setSelection(2);

                                    }else if(weekdayText.equalsIgnoreCase("Wed")){

                                        weekday.setSelection(3);

                                    }else if(weekdayText.equalsIgnoreCase("Thu")){

                                        weekday.setSelection(4);

                                    }else if(weekdayText.equalsIgnoreCase("Fri")){

                                        weekday.setSelection(5);

                                    }else if(weekdayText.equalsIgnoreCase("Sat")){

                                        weekday.setSelection(6);

                                    }
                                }

                                mWeekContainer.addView(addView);
                            }
                        }
                    }
                }
            }
        }
        getDesignation(employee.getDesignationId());
        if(dob.contains("T")){

            String dojs[] = dob.split("T");

            try {
                Date afromDate = new SimpleDateFormat("yyyy-MM-dd").parse(dojs[0]);
                dob = new SimpleDateFormat("dd/MM/yyyy").format(afromDate);
                mDob.setText(""+dob);

            } catch (ParseException e) {
                e.printStackTrace();
            }


        }

        if(doj.contains("T")){

            String dojs[] = doj.split("T");

            try {
                Date afromDate = new SimpleDateFormat("yyyy-MM-dd").parse(dojs[0]);
                doj = new SimpleDateFormat("dd/MM/yyyy").format(afromDate);
                mDoj.setText(""+doj);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        //mDesignation.setText();
        boolean location = employee.isLocationOn();

        if(location){
            mLocationCondition.setChecked(false);
        }else{
            mLocationCondition.setChecked(true);
        }

        //mDesignation.setText();
        boolean time = employee.isDataOn();

        if(time){
            mCheckTime.setChecked(false);
        }else{
            mCheckTime.setChecked(true);
        }

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

        mSalary.setText(""+employee.getSalary());
        mPrimaryEmail.setText(""+employee.getPrimaryEmailAddress());
        mSecondaryEmail.setText(""+employee.getAlternateEmailAddress());
        mMobile.setText(""+employee.getPhoneNumber());
        mAddress.setText(""+employee.getAddress());
        mPassword.setText(""+employee.getPassword());
        mConfirm.setText(""+employee.getPassword());

        if(orgId!=0){

            getDepartment(orgId,employee.getDepartmentId());
            getmanagerProfile(orgId,employee.getManagerId());
            getShiftTimings(orgId,employee.getDeviceModel());
        }else{
            getDepartment(PreferenceHandler.getInstance( EmployeeEditScreen.this).getCompanyId(),employee.getDepartmentId());
            getmanagerProfile(PreferenceHandler.getInstance( EmployeeEditScreen.this).getCompanyId(),employee.getManagerId());
            getShiftTimings(PreferenceHandler.getInstance( EmployeeEditScreen.this).getCompanyId(),employee.getDeviceModel());
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
        String noWeek = mNoWeekOff.getText().toString();

        if(name.isEmpty()){

            Toast.makeText(this, "Name is required", Toast.LENGTH_SHORT).show();

        }else if(dob.isEmpty()){

            Toast.makeText(this, "DOB is required", Toast.LENGTH_SHORT).show();

        }else if(doj.isEmpty()){

            Toast.makeText(this, "Founded date is required", Toast.LENGTH_SHORT).show();

        }else if(primary.isEmpty()){

            Toast.makeText(this, "Primary Email is required", Toast.LENGTH_SHORT).show();

        }/*else if(secondary.isEmpty()){

            Toast.makeText(this, "Secondary Email is required", Toast.LENGTH_SHORT).show();

        }*/else if(password.isEmpty()){

            Toast.makeText(this, "Password is required", Toast.LENGTH_SHORT).show();

        }else if(confirm.isEmpty()){

            Toast.makeText(this, "Confirm Password is required", Toast.LENGTH_SHORT).show();

        }else if(!password.isEmpty()&&!confirm.isEmpty()&&!password.equals(confirm)){

            Toast.makeText(this, "Confirm password should be same as Password", Toast.LENGTH_SHORT).show();

        }else if(mobile.isEmpty()){

            Toast.makeText(this, "Mobile is required", Toast.LENGTH_SHORT).show();

        }else if(designation.isEmpty()){

            Toast.makeText(this, "Designation is required", Toast.LENGTH_SHORT).show();

        }else if(salary.isEmpty()){

            Toast.makeText(this, "Salary is required", Toast.LENGTH_SHORT).show();

        }/*else if(address.isEmpty()) {

            Toast.makeText(this, "Address is required", Toast.LENGTH_SHORT).show();

        }*/else if(!mMale.isChecked()&&!mFemale.isChecked()&&!mOthers.isChecked()){

            Toast.makeText(this, "Please Select Gender", Toast.LENGTH_SHORT).show();

        }else if(mWeekOffCheck.isChecked()&&(noWeek==null||noWeek.isEmpty())){

            Toast.makeText(this, "Please Enter no of Week-off", Toast.LENGTH_SHORT).show();

        }else{



            Employee employee = employees;
            employee.setEmployeeName(name);

            if(address!=null&&!address.isEmpty()){
                employee.setAddress(address);
            }
            //employee.setAddress(address);
            employee.setPassword(password);


            if(mAdmin.isChecked()){

                employee.setUserRoleId(9);
            }else{
                employee.setUserRoleId(1);
            }

            if(mActive.isChecked()){

                employee.setStatus("Active");
            }else{
                employee.setStatus("Deactive");
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

            if(employeeList!=null&&employeeList.size()!=0){
                employee.setManagerId(employeeList.get(mtoReport.getSelectedItemPosition()).getEmployeeId());
            }
            if(workingDays!=null&&workingDays.size()!=0){
                employee.setDeviceModel(""+workingDays.get(mShift.getSelectedItemPosition()).getOrganizationTimingId());
            }

            int childCounts = mWeekContainer.getChildCount();
            String weeklist = "";
            for(int i=0; i<childCounts; i++){
                View thisChild = mWeekContainer.getChildAt(i);
                final Spinner week = (Spinner)thisChild.findViewById(R.id.week);
                final Spinner weekday = (Spinner)thisChild.findViewById(R.id.week_day);
                String weekText = week.getSelectedItem().toString();
                String weekdayText = weekday.getSelectedItem().toString();

                if(weekText!=null&&!weekText.isEmpty()&&weekdayText!=null&&!weekdayText.isEmpty()){

                    weeklist = weekText+"-"+weekdayText+","+weeklist;
                }
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
            employee.setDeviceAndroidVersion(weeklist);
            employee.setSalary(Double.parseDouble(salary));
            if(secondary!=null&&!secondary.isEmpty()){
                employee.setAlternateEmailAddress(secondary);
            }
            employee.setPhoneNumber(mobile);

            employee.setDepartmentId(departmentData.get(mDepartment.getSelectedItemPosition()).getDepartmentId());





            Designations designations = new Designations ();
            designations.setDesignationTitle(designation);
            designations.setDescription(designation);
            addDesignations(designations,employee);


        }

    }

    private void getDepartment(final int id,final int deptId){

        new ThreadExecuter ().execute( new Runnable() {
            @Override
            public void run() {


                DepartmentApi apiService =
                        Util.getClient().create( DepartmentApi.class);

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

                                    DepartmentSpinnerAdapter arrayAdapter = new DepartmentSpinnerAdapter ( EmployeeEditScreen.this, departmentData);
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

                            Toast.makeText( EmployeeEditScreen.this,response.message(),Toast.LENGTH_SHORT).show();
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

    public void addDesignations( final Designations designations, final Employee employee) {



        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Saving Details..");
        dialog.setCancelable(false);
        dialog.show();

        DesignationsAPI apiService = Util.getClient().create( DesignationsAPI.class);

        Call< Designations > call = apiService.addDesignations(designations);

        call.enqueue(new Callback< Designations >() {
            @Override
            public void onResponse( Call< Designations > call, Response< Designations > response) {
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
                            employee.setDesignation(s);

                            checkUserByEmailId(employee);

//                            updateProfile(employee);


                        }




                    }else {
                        Toast.makeText( EmployeeEditScreen.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
            public void onFailure( Call< Designations > call, Throwable t) {

                if(dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
                Toast.makeText( EmployeeEditScreen.this , "Failed due to Bad Internet Connection" , Toast.LENGTH_SHORT ).show( );
                Log.e("TAG", t.toString());
            }
        });



    }

    public void updateProfile(final Employee employee){

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setTitle("Updaitng...");
        dialog.show();

        Gson gson = new Gson();
        String json = gson.toJson(employee);
        System.out.println("Employee "+json);

        new ThreadExecuter ().execute( new Runnable() {
            @Override
            public void run() {

                final EmployeeApi subCategoryAPI = Util.getClient().create( EmployeeApi.class);
                Call< Employee > getProf = subCategoryAPI.updateEmployee(employee.getEmployeeId(),employee);
                //Call<ArrayList<Blogs>> getBlog = blogApi.getBlogs();

                getProf.enqueue(new Callback< Employee >() {

                    @Override
                    public void onResponse( Call< Employee > call, Response< Employee > response) {

                        if(dialog != null)
                        {
                            dialog.dismiss();
                        }
                        if (response.code() == 200||response.code()==201||response.code()==204)
                        {

                            EmployeeEditScreen.this.finish();

                        }else{
                            Toast.makeText( EmployeeEditScreen.this, "Failed due to status code"+response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure( Call< Employee > call, Throwable t) {

                        if(dialog != null)
                        {
                            dialog.dismiss();
                        }
                        Toast.makeText( EmployeeEditScreen.this , "Something went wrong due to " + "Bad Internet Connection" , Toast.LENGTH_SHORT ).show( );

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

    private void getmanagerProfile(final int id,final int managerId){


        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading...");
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
                        int statusCode = response.code();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {


                            if (progressDialog!=null)
                                progressDialog.dismiss();
                            ArrayList< Employee > list = response.body();


                            if (list !=null && list.size()!=0) {

                                employeeList = new ArrayList<>();

                                int value = 0;


                                for(int i=0;i<list.size();i++){

                                    if(list.get(i).getEmployeeId()!=employees.getEmployeeId()){

                                        employeeList.add(list.get(i));

                                        if(list.get(i).getEmployeeId()==managerId){

                                            value = employeeList.size()-1;
                                        }

                                    }
                                }

                                if(employeeList!=null&&employeeList.size()!=0){
                                    //
                                    // Collections.sort(employeeList,Employee.compareEmployee);
                                    ManagerSpinnerAdapter arrayAdapter = new ManagerSpinnerAdapter( EmployeeEditScreen.this, employeeList);
                                    mtoReport.setAdapter(arrayAdapter);
                                    mtoReport.setSelection(value);


                                }else{
                                    //   Toast.makeText(EmployeeEditScreen.this,"No Employees added",Toast.LENGTH_LONG).show();

                                }


                                //}

                            }else{
                                // Toast.makeText(EmployeeEditScreen.this,"No Employees added",Toast.LENGTH_LONG).show();

                            }

                        }else {


                            Toast.makeText( EmployeeEditScreen.this, "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure( Call<ArrayList< Employee >> call, Throwable t) {
                        // Log error here since request failed
                        if (progressDialog!=null)
                            progressDialog.dismiss();
                        Log.e("TAG", t.toString());
                    }
                });
            }


        });
    }

    public void getDesignation(final int id){

        new ThreadExecuter ().execute( new Runnable() {
            @Override
            public void run() {

                final DesignationsAPI subCategoryAPI = Util.getClient().create( DesignationsAPI.class);
                Call< Designations > getProf = subCategoryAPI.getDesignationById(id);
                //Call<ArrayList<Blogs>> getBlog = blogApi.getBlogs();

                getProf.enqueue(new Callback< Designations >() {

                    @Override
                    public void onResponse( Call< Designations > call, Response< Designations > response) {

                        if (response.code() == 200||response.code() == 201||response.code() == 204)
                        {

                            if(response.body()!=null){
                                mDesignation.setText(""+response.body().getDesignationTitle());
                            }



                        }else{


                        }
                    }

                    @Override
                    public void onFailure( Call< Designations > call, Throwable t) {

                    }
                });

            }

        });
    }

    private void checkUserByEmailId(final Employee userProfile){


        userProfile.setEmail(userProfile.getPrimaryEmailAddress());


        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setTitle("Please wait..");
        dialog.show();


        new ThreadExecuter ().execute( new Runnable() {
            @Override
            public void run() {


                EmployeeApi apiService =
                        Util.getClient().create( EmployeeApi.class);

                Call<ArrayList< Employee >> call = apiService.getUserByEmail(userProfile);

                call.enqueue(new Callback<ArrayList< Employee >>() {
                    @Override
                    public void onResponse( Call<ArrayList< Employee >> call, Response<ArrayList< Employee >> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                        int statusCode = response.code();

                        if(dialog != null)
                        {
                            dialog.dismiss();
                        }

                        if(statusCode == 200 || statusCode == 204)
                        {

                            ArrayList< Employee > responseProfile = response.body();
                            if(responseProfile != null && responseProfile.size()!=0 )
                            {


                                boolean check = false;
                                for ( Employee empl:responseProfile) {

                                    if(empl.getEmployeeId()!=userProfile.getEmployeeId()){

                                        check = true;
                                    }

                                }

                                if(check){
                                    mPrimaryEmail.setError("Email Exists");
                                    Toast.makeText( EmployeeEditScreen.this, "Email already Exists", Toast.LENGTH_SHORT).show();
                                }else{
                                    checkUserByPhone(userProfile);
                                }




                            }
                            else
                            {
                                checkUserByPhone(userProfile);
                            }
                        }
                        else
                        {

                            Toast.makeText( EmployeeEditScreen.this,response.message(),Toast.LENGTH_SHORT).show();
                        }
//                callGetStartEnd();
                    }

                    @Override
                    public void onFailure( Call<ArrayList< Employee >> call, Throwable t) {
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

        new ThreadExecuter ().execute( new Runnable() {
            @Override
            public void run() {


                EmployeeApi apiService =
                        Util.getClient().create( EmployeeApi.class);

                Call<ArrayList< Employee >> call = apiService.getUserByPhone(userProfile.getPhoneNumber());

                call.enqueue(new Callback<ArrayList< Employee >>() {
                    @Override
                    public void onResponse( Call<ArrayList< Employee >> call, Response<ArrayList< Employee >> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                        int statusCode = response.code();

                        if(statusCode == 200 || statusCode == 204)
                        {
                            ArrayList< Employee > responseProfile = response.body();
                            if(responseProfile != null && responseProfile.size()!=0 )
                            {
                                boolean check = false;
                                for ( Employee empl:responseProfile) {

                                    if(empl.getEmployeeId()!=userProfile.getEmployeeId()){

                                        check = true;
                                    }

                                }

                                if(check){
                                    mMobile.setError("Number Already Exists");
                                    Toast.makeText( EmployeeEditScreen.this, "Mobile already Exists", Toast.LENGTH_SHORT).show();
                                }else{

                                    updateProfile(userProfile);
                                }


                            }
                            else
                            {

                                try {



                                    updateProfile(userProfile);
                                    //addDesignations(designations,userProfile);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        else
                        {

                            Toast.makeText( EmployeeEditScreen.this,response.message(),Toast.LENGTH_SHORT).show();
                        }
//                callGetStartEnd();
                    }

                    @Override
                    public void onFailure( Call<ArrayList< Employee >> call, Throwable t) {
                        // Log error here since request failed

                        Log.e("TAG", t.toString());
                    }
                });
            }
        });
    }

    public void getShiftTimings(final int id,final String shift) {



        new ThreadExecuter ().execute( new Runnable() {
            @Override
            public void run() {

                final OrganizationTimingsAPI orgApi = Util.getClient().create( OrganizationTimingsAPI.class);
                Call<ArrayList< WorkingDay >> getProf = orgApi.getOrganizationTimingByOrgId(id);
                //Call<ArrayList<Blogs>> getBlog = blogApi.getBlogs();

                getProf.enqueue(new Callback<ArrayList< WorkingDay >>() {

                    @Override
                    public void onResponse( Call<ArrayList< WorkingDay >> call, Response<ArrayList< WorkingDay >> response) {



                        if (response.code() == 200||response.code() == 201||response.code() == 204)
                        {

                            workingDays = response.body();
                            //workingDays = new ArrayList<>();

                            int value = 0;
                            int timingId = 0;

                            if(shift!=null&&android.text.TextUtils.isDigitsOnly(shift)){

                                timingId = Integer.parseInt(shift);

                            }

                            if(timingId!=0){

                                for(int i =0 ;i <workingDays.size();i++){

                                    if(workingDays.get(i).getOrganizationTimingId()==timingId){

                                        value = i;
                                        break;
                                    }


                                }
                            }





                            if(workingDays!=null&&workingDays.size()!=0){

                                ShiftSpinnerAdapter arrayAdapter = new ShiftSpinnerAdapter ( EmployeeEditScreen.this, workingDays);
                                mShift.setAdapter(arrayAdapter);
                                mShift.setSelection(value);

                            }


                        }else{



                            Toast.makeText( EmployeeEditScreen.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onFailure( Call<ArrayList< WorkingDay >> call, Throwable t) {




                        Toast.makeText( EmployeeEditScreen.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                    }
                });

            }

        });
    }
}
