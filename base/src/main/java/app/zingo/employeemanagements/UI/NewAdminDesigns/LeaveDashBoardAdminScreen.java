package app.zingo.employeemanagements.UI.NewAdminDesigns;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import app.zingo.employeemanagements.Adapter.LeaveDashBoardAdapter;
import app.zingo.employeemanagements.Custom.CustomCalendar.CustomMonthPicker;
import app.zingo.employeemanagements.Custom.CustomCalendar.DateMonthDialogListener;
import app.zingo.employeemanagements.Custom.CustomCalendar.OnCancelMonthDialogListener;
import app.zingo.employeemanagements.Model.Employee;
import app.zingo.employeemanagements.Model.EmployeeImages;
import app.zingo.employeemanagements.Model.Leaves;
import app.zingo.employeemanagements.UI.Common.LeaveListScreen;
import app.zingo.employeemanagements.Utils.ThreadExecuter;
import app.zingo.employeemanagements.Utils.Util;
import app.zingo.employeemanagements.WebApi.LeaveAPI;
import app.zingo.employeemanagements.base.R;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LeaveDashBoardAdminScreen extends AppCompatActivity {


    Employee employee;

    TextView mEmployeeName,mDate,mPaidCount,mUnpaidCount,mTotalCount,
                mApprovedCound,mRejectedCount,mPendingCount;
    CircleImageView mProfilePic;
    // CustomSpinner mDay;
    ImageView mPrevious,mNext;
    LinearLayout mNoLeavesLay;
    RecyclerView mLeaveList;

    SimpleDateFormat dateFormat;

    ArrayList<Leaves> totalLeaves;
    ArrayList<Leaves> paidLeaves;
    ArrayList<Leaves> unpaidLeaves;
    ArrayList<Leaves> approvedLeaves;
    ArrayList<Leaves> rejectedLeaves;
    ArrayList<Leaves> pendingLeaves;
    ArrayList<Leaves> monthtotalLeaves;
    ArrayList<Leaves> monthpaidLeaves;
    ArrayList<Leaves> monthunpaidLeaves;
    ArrayList<Leaves> monthapprovedLeaves;
    ArrayList<Leaves> monthrejectedLeaves;
    ArrayList<Leaves> monthpendingLeaves;

    LeaveDashBoardAdapter adapter;

    int monthValue = 0;
    int yearValue = 0;
    boolean employeeScreen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{

            setContentView(R.layout.activity_leave_dash_board_admin_screen);


            Bundle bundle = getIntent().getExtras();

            if(bundle!=null){

                employee = (Employee)bundle.getSerializable("Employee");
                employeeScreen = bundle.getBoolean("EmployeeScreen");
            }

            if(employeeScreen){

            }else{
                getSupportActionBar().setHomeButtonEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                setTitle("Leave Dashboard");
            }

            totalLeaves = new ArrayList<>();
            paidLeaves = new ArrayList<>();
            unpaidLeaves = new ArrayList<>();
            approvedLeaves = new ArrayList<>();
            rejectedLeaves = new ArrayList<>();
            pendingLeaves = new ArrayList<>();

            mEmployeeName = findViewById(R.id.name);
            mProfilePic = findViewById(R.id.employee_pic);
            mDate = findViewById(R.id.presentDate);
            mPaidCount = findViewById(R.id.paid_leaves);
            mUnpaidCount = findViewById(R.id.un_paid_leaves);
            mTotalCount = findViewById(R.id.total_leaves);
            mApprovedCound = findViewById(R.id.approved_leaves);
            mRejectedCount = findViewById(R.id.rejected_leaves);
            mPendingCount = findViewById(R.id.pending_leaves);
            mPrevious = findViewById(R.id.previousDay);
            mNext = findViewById(R.id.nextDay);
            mLeaveList = findViewById(R.id.leaves_list_dash);
            mLeaveList.setLayoutManager(new LinearLayoutManager(LeaveDashBoardAdminScreen.this));
            mNoLeavesLay = findViewById(R.id.noRecordFound);

            dateFormat = new SimpleDateFormat("MMM yyyy");
            mDate.setText(dateFormat.format(new Date()));

            if(employee!=null){

                mEmployeeName.setText(""+employee.getEmployeeName());

                ArrayList<EmployeeImages> images = employee.getEmployeeImages();

                if(images!=null&&images.size()!=0){
                    EmployeeImages employeeImages = images.get(0);

                    if(employeeImages!=null){


                        String base=employeeImages.getImage();
                        if(base != null && !base.isEmpty()){
                            Picasso.with(LeaveDashBoardAdminScreen.this).load(base).placeholder(R.drawable.profile_image).error(R.drawable.profile_image).into(mProfilePic);


                        }
                    }

                }

                Calendar cal = Calendar.getInstance();
                int monthCal = cal.get(Calendar.MONTH);
                int yearCal = cal.get(Calendar.YEAR);

                monthValue = monthCal+1;
                yearValue = yearCal;

                getLeaveDetails(employee.getEmployeeId(), monthValue,yearValue);



            }

            mPrevious.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try{
                        final Date date = dateFormat.parse(mDate.getText().toString());
                        final Calendar calendar = Calendar.getInstance();
                        calendar.setTime(date);
                        calendar.add(Calendar.MONTH, -1);

                        Date date2 = calendar.getTime();


                        mDate.setText(dateFormat.format(date2));
                        int month = calendar.get(Calendar.MONTH);
                        int year = calendar.get(Calendar.YEAR);

                        monthValue = month+1;
                        yearValue = year;

                        if(totalLeaves!=null&&totalLeaves.size()!=0){
                            getMonthlyLeave(totalLeaves,monthValue,yearValue);
                        }else{

                            mNoLeavesLay.setVisibility(View.VISIBLE);
                        }


                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });

            mNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    try{
                        final Date date = dateFormat.parse(mDate.getText().toString());
                        String newDate = dateFormat.format(new Date());
                        final Date newDates = dateFormat.parse(newDate);

                        if(newDates.getTime()<=date.getTime()){

                            Toast.makeText(LeaveDashBoardAdminScreen.this, "Data will not available for future date", Toast.LENGTH_SHORT).show();

                        }else{
                            final Calendar calendar = Calendar.getInstance();
                            calendar.setTime(date);
                            calendar.add(Calendar.MONTH, 1);

                            Date date2 = calendar.getTime();

                            mDate.setText(dateFormat.format(date2));

                            int month = calendar.get(Calendar.MONTH);
                            int year = calendar.get(Calendar.YEAR);

                            monthValue = month+1;
                            yearValue = year;

                            if(totalLeaves!=null&&totalLeaves.size()!=0){
                                getMonthlyLeave(totalLeaves,monthValue,yearValue);
                            }else{

                                mNoLeavesLay.setVisibility(View.VISIBLE);
                            }


                        }




                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            });


            mDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   // openDatePicker(mDate);


                    monthYearPicker().show();


                }
            });


            mPaidCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(monthpaidLeaves!=null&&monthpaidLeaves.size()!=0){
                        Intent pending = new Intent(LeaveDashBoardAdminScreen.this, LeaveListScreen.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("Leaves",monthpaidLeaves);
                        bundle.putString("Title","Paid Leaves");
                        pending.putExtras(bundle);
                        startActivity(pending);
                    }

                }
            });

            mUnpaidCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(monthunpaidLeaves!=null&&monthunpaidLeaves.size()!=0){
                        Intent pending = new Intent(LeaveDashBoardAdminScreen.this, LeaveListScreen.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("Leaves",monthunpaidLeaves);
                        bundle.putString("Title","Un paid Leaves");
                        pending.putExtras(bundle);
                        startActivity(pending);
                    }

                }
            });

            mTotalCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(monthtotalLeaves!=null&&monthtotalLeaves.size()!=0){
                        Intent pending = new Intent(LeaveDashBoardAdminScreen.this, LeaveListScreen.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("Leaves",monthtotalLeaves);
                        bundle.putString("Title","Total Leaves");
                        pending.putExtras(bundle);
                        startActivity(pending);
                    }

                }
            });

            mApprovedCound.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(monthapprovedLeaves!=null&&monthapprovedLeaves.size()!=0){
                        Intent pending = new Intent(LeaveDashBoardAdminScreen.this, LeaveListScreen.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("Leaves",monthapprovedLeaves);
                        bundle.putString("Title","Approved Leaves");
                        pending.putExtras(bundle);
                        startActivity(pending);
                    }

                }
            });

            mRejectedCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(monthrejectedLeaves!=null&&monthrejectedLeaves.size()!=0){
                        Intent pending = new Intent(LeaveDashBoardAdminScreen.this, LeaveListScreen.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("Leaves",monthrejectedLeaves);
                        bundle.putString("Title","Rejected Leaves");
                        pending.putExtras(bundle);
                        startActivity(pending);
                    }

                }
            });

            mPendingCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(monthpendingLeaves!=null&&monthpendingLeaves.size()!=0){
                        Intent pending = new Intent(LeaveDashBoardAdminScreen.this, LeaveListScreen.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("Leaves",monthpendingLeaves);
                        bundle.putString("Title","Pending Leaves");
                        pending.putExtras(bundle);
                        startActivity(pending);
                    }

                }
            });

        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public void openDatePicker(final TextView tv) {
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
                            String date2 = year  + "-" +(monthOfYear + 1)+ "-" +  (dayOfMonth);

                            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy");



                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
                            try {
                                Date fdate = simpleDateFormat.parse(date1);


                                String dateValue = new SimpleDateFormat("yyyy-MM-dd").format(fdate);
                                //mTitle.setText(new SimpleDateFormat("dd-MM-yyyy").format(fdate)+" Summary");//dd-MM-yyyy
                                tv.setText(new SimpleDateFormat("MMM yyyy").format(fdate)+"");



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

        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();



    }

    public CustomMonthPicker monthYearPicker(){

        Calendar calendar = Calendar.getInstance();
        int years = calendar.get(Calendar.YEAR);
        int months = calendar.get(Calendar.MONTH);

        final CustomMonthPicker customMonthPicker = new CustomMonthPicker(this)
                .setLocale(Locale.ENGLISH)
                .setSelectedMonth(months)
                .setSelectedYear(years)
                .setColorTheme(R.color.colorPrimary)
                .setPositiveButton(new DateMonthDialogListener() {
                    @Override
                    public void onDateMonth(int month, int startDate, int endDate, int year, String monthLabel) {
                        System.out.println(month);
                        System.out.println(startDate);
                        System.out.println(endDate);
                        System.out.println(year);
                        System.out.println(monthLabel);

                        String[] months = { "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };

                        String selectedMonthName = months[month-1];

                        mDate.setText(selectedMonthName+" "+year);


                        monthValue = month;
                        yearValue = year;

                        if(totalLeaves!=null&&totalLeaves.size()!=0){
                            try {
                                getMonthlyLeave(totalLeaves,monthValue,yearValue);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }else{

                            mNoLeavesLay.setVisibility(View.VISIBLE);
                        }

                        //String date =
                    }
                })
                .setNegativeButton(new OnCancelMonthDialogListener() {
                    @Override
                    public void onCancel(AlertDialog dialog) {
                        dialog.dismiss();
                    }
                });

        return customMonthPicker;
    }

    private void getLeaveDetails(final int employeeId,final int month,final int year){

        totalLeaves = new ArrayList<>();


        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading Leaves Details..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {
                LeaveAPI apiService = Util.getClient().create(LeaveAPI.class);
                Call<ArrayList<Leaves>> call = apiService.getLeavesByEmployeeId(employeeId);

                call.enqueue(new Callback<ArrayList<Leaves>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Leaves>> call, Response<ArrayList<Leaves>> response) {
                        int statusCode = response.code();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {


                            if (progressDialog!=null)
                                progressDialog.dismiss();

                            try{

                                totalLeaves = response.body();
                                approvedLeaves = new ArrayList<>();
                                rejectedLeaves = new ArrayList<>();
                                paidLeaves = new ArrayList<>();
                                unpaidLeaves = new ArrayList<>();
                                pendingLeaves = new ArrayList<>();



                                if (totalLeaves !=null && totalLeaves.size()!=0) {

                                    getMonthlyLeave(totalLeaves,month,year);


                                }else{

                                    mNoLeavesLay.setVisibility(View.VISIBLE);

                                }
                            }catch (Exception e){
                                e.printStackTrace();
                                mNoLeavesLay.setVisibility(View.VISIBLE);
                            }


                        }else {

                            if (progressDialog!=null)
                                progressDialog.dismiss();

                            mNoLeavesLay.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Leaves>> call, Throwable t) {
                        // Log error here since request failed
                        if (progressDialog!=null)
                            progressDialog.dismiss();
                        Log.e("TAG", t.toString());
                        mNoLeavesLay.setVisibility(View.VISIBLE);
                    }
                });
            }


        });
    }


    public void getMonthlyLeave(ArrayList<Leaves> list, final int month, final int year) throws Exception{

        monthtotalLeaves = new ArrayList<>();
        monthpaidLeaves = new ArrayList<>();
        monthunpaidLeaves = new ArrayList<>();
        monthapprovedLeaves = new ArrayList<>();
        monthrejectedLeaves = new ArrayList<>();
        monthpendingLeaves = new ArrayList<>();


        Date date = new Date();
        Date adate = new Date();
        Date edate = new Date();

        if(month!=0&&year!=0){

            try {
                date = new SimpleDateFormat("MM yyyy").parse(month+" "+year);
                adate = new SimpleDateFormat("yyyy-MM-dd").parse(year+"-"+month+"-01");
                //edate = new SimpleDateFormat("yyyy-MMM-dd").parse(year+"-"+month+"-0");
                Date edates = new SimpleDateFormat("MM").parse(month+"");

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, (Integer.parseInt(new SimpleDateFormat("MM").format(edates))-1));
                int daysInMonth = calendar.getActualMaximum(Calendar.DATE);
                edate = new SimpleDateFormat("yyyy-MM-dd").parse(year+"-"+month+"-"+daysInMonth);


            } catch (ParseException e) {
                e.printStackTrace();

            }


        }


        for (Leaves leaves:list) {

            String froms = leaves.getFromDate();
            String tos = leaves.getToDate();

            Date afromDate = null;
            Date atoDate = null;

            if(froms!=null&&!froms.isEmpty()){

                if(froms.contains("T")){

                    String dojs[] = froms.split("T");

                    afromDate = new SimpleDateFormat("yyyy-MM-dd").parse(dojs[0]);


                }

            }

            if(tos!=null&&!tos.isEmpty()){

                if(tos.contains("T")){

                    String dojs[] = tos.split("T");

                    atoDate = new SimpleDateFormat("yyyy-MM-dd").parse(dojs[0]);


                }

            }


            if(afromDate!=null&&atoDate!=null){

                if((edate.getTime()>=afromDate.getTime())
                        &&(adate.getTime()<=atoDate.getTime()))
                {

                    monthtotalLeaves.add(leaves);

                    if(leaves.getStatus()!=null&&!leaves.getStatus().isEmpty()){

                        if(leaves.getStatus().equalsIgnoreCase("Approved")){

                            monthapprovedLeaves.add(leaves);

                        }else if(leaves.getStatus().equalsIgnoreCase("Rejected")){

                            monthrejectedLeaves.add(leaves);

                        }else if(leaves.getStatus().equalsIgnoreCase("Pending")){

                            monthpendingLeaves.add(leaves);

                        }
                    }

                    if(leaves.getLeaveType()!=null&&!leaves.getLeaveType().isEmpty()){

                        if(leaves.getLeaveType().equalsIgnoreCase("Paid")){

                            monthpaidLeaves.add(leaves);

                        }else if(leaves.getLeaveType().equalsIgnoreCase("Un Paid")){

                            monthunpaidLeaves.add(leaves);

                        }
                    }

                }else{

                }

                if(leaves.getStatus()!=null&&!leaves.getStatus().isEmpty()){

                    if(leaves.getStatus().equalsIgnoreCase("Approved")){

                        approvedLeaves.add(leaves);

                    }else if(leaves.getStatus().equalsIgnoreCase("Rejected")){

                        rejectedLeaves.add(leaves);

                    }else if(leaves.getStatus().equalsIgnoreCase("Pending")){

                        pendingLeaves.add(leaves);

                    }
                }

                if(leaves.getLeaveType()!=null&&!leaves.getLeaveType().isEmpty()){

                    if(leaves.getLeaveType().equalsIgnoreCase("Paid")){

                        paidLeaves.add(leaves);

                    }else if(leaves.getLeaveType().equalsIgnoreCase("Un Paid")){

                        unpaidLeaves.add(leaves);

                    }
                }
            }






        }

        if(monthtotalLeaves!=null&&monthtotalLeaves.size()!=0){

            mTotalCount.setText(""+monthtotalLeaves.size());
            mNoLeavesLay.setVisibility(View.GONE);
            mLeaveList.setVisibility(View.VISIBLE);

            adapter = new LeaveDashBoardAdapter(LeaveDashBoardAdminScreen.this,monthtotalLeaves);
            mLeaveList.setAdapter(adapter);

            try{

                mPaidCount.setText(""+monthunpaidLeaves.size());
                mUnpaidCount.setText(""+monthunpaidLeaves.size());
                mApprovedCound.setText(""+monthapprovedLeaves.size());
                mRejectedCount.setText(""+monthrejectedLeaves.size());
                mPendingCount.setText(""+monthpendingLeaves.size());

            }catch (Exception e){
                e.printStackTrace();
            }

        }else{
            mNoLeavesLay.setVisibility(View.VISIBLE);
            mLeaveList.setVisibility(View.GONE);

            mTotalCount.setText("0");
            mPaidCount.setText("0");
            mUnpaidCount.setText("0");
            mApprovedCound.setText("0");
            mRejectedCount.setText("0");
            mPendingCount.setText("0");
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getLeaveDetails(employee.getEmployeeId(), monthValue,yearValue);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id)
        {
            case android.R.id.home:

                LeaveDashBoardAdminScreen.this.finish();
                break;


        }
        return super.onOptionsItemSelected(item);
    }
}
