package app.zingo.employeemanagements.ui.NewAdminDesigns;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import app.zingo.employeemanagements.adapter.ExpenseListDataAdapter;
import app.zingo.employeemanagements.Custom.CustomCalendar.CustomMonthPicker;
import app.zingo.employeemanagements.Custom.CustomCalendar.DateMonthDialogListener;
import app.zingo.employeemanagements.Custom.CustomCalendar.OnCancelMonthDialogListener;
import app.zingo.employeemanagements.model.Employee;
import app.zingo.employeemanagements.model.EmployeeImages;
import app.zingo.employeemanagements.model.ExpenseAdminData;
import app.zingo.employeemanagements.model.Expenses;
import app.zingo.employeemanagements.ui.Common.ExpensesListAdmin;
import app.zingo.employeemanagements.utils.PreferenceHandler;
import app.zingo.employeemanagements.utils.ThreadExecuter;
import app.zingo.employeemanagements.utils.Util;
import app.zingo.employeemanagements.WebApi.ExpensesApi;
import app.zingo.employeemanagements.base.R;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExpenseDashBoardAdmin extends AppCompatActivity {

    Employee employee;

    TextView mEmployeeName,mDate,mTotalAmount,mClaimedAmount,mTotalCount,
            mApprovedCound,mRejectedCount,mPendingCount;
    CircleImageView mProfilePic;
    // CustomSpinner mDay;
    ImageView mPrevious,mNext;
    LinearLayout mNoExpensesLay;
    RecyclerView mExpenseList;

    SimpleDateFormat dateFormat;

    ArrayList<Expenses> totalExpenses;
    ArrayList< ExpenseAdminData > approvedExpenses;
    ArrayList< ExpenseAdminData > rejectedExpenses;
    ArrayList< ExpenseAdminData > pendingExpenses;
    ArrayList< ExpenseAdminData > monthtotalExpenses;
    ArrayList< ExpenseAdminData > monthapprovedExpenses;
    ArrayList< ExpenseAdminData > monthrejectedExpenses;
    ArrayList< ExpenseAdminData > monthpendingExpenses;
    ArrayList< ExpenseAdminData > monthclamiedExpenses;

    ExpenseListDataAdapter adapter;

    int monthValue = 0;
    int yearValue = 0;
    
    double totalAmt=0,claimedAmt=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{

            setContentView(R.layout.activity_expense_dash_board_admin);

            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Expense Dashboard");

            Bundle bundle = getIntent().getExtras();

            if(bundle!=null){

                employee = (Employee)bundle.getSerializable("Profile");
            }

            totalExpenses = new ArrayList<>();
            approvedExpenses = new ArrayList<>();
            rejectedExpenses = new ArrayList<>();
            pendingExpenses = new ArrayList<>();

            mEmployeeName = findViewById(R.id.name);
            mProfilePic = findViewById(R.id.employee_pic);
            mDate = findViewById(R.id.presentDate);
            mTotalAmount = findViewById(R.id.expense_amount);
            mClaimedAmount = findViewById(R.id.claimed_amount);
            mTotalCount = findViewById(R.id.total_expenses);
            mApprovedCound = findViewById(R.id.approved_expenses);
            mRejectedCount = findViewById(R.id.rejected_expenses);
            mPendingCount = findViewById(R.id.pending_expenses);
            mPrevious = findViewById(R.id.previousDay);
            mNext = findViewById(R.id.nextDay);
            mExpenseList = findViewById(R.id.expenses_list_dash);
            mExpenseList.setLayoutManager(new LinearLayoutManager( ExpenseDashBoardAdmin.this));
            mNoExpensesLay = findViewById(R.id.noRecordFound);

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
                            Picasso.with( ExpenseDashBoardAdmin.this).load(base).placeholder(R.drawable.profile_image).error(R.drawable.profile_image).into(mProfilePic);


                        }
                    }

                }

                Calendar cal = Calendar.getInstance();
                int monthCal = cal.get(Calendar.MONTH);
                int yearCal = cal.get(Calendar.YEAR);

                monthValue = monthCal+1;
                yearValue = yearCal;


                getExpenses(employee.getEmployeeId(),monthValue,yearValue);


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

                        if(totalExpenses!=null&&totalExpenses.size()!=0){
                            getMonthlyExpenses(totalExpenses,monthValue,yearValue);
                        }else{

                            mNoExpensesLay.setVisibility(View.VISIBLE);
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

                            Toast.makeText( ExpenseDashBoardAdmin.this, "Data will not available for future date", Toast.LENGTH_SHORT).show();

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

                            if(totalExpenses!=null&&totalExpenses.size()!=0){
                                getMonthlyExpenses(totalExpenses,monthValue,yearValue);
                            }else{

                                mNoExpensesLay.setVisibility(View.VISIBLE);
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

            mPendingCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(monthpendingExpenses!=null&&monthpendingExpenses.size()!=0){
                        Intent pending = new Intent( ExpenseDashBoardAdmin.this, ExpensesListAdmin.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("Expenses",monthpendingExpenses);
                        bundle.putString("Title","Pending Expenses");
                        pending.putExtras(bundle);
                        startActivity(pending);
                    }
                }
            });

            mApprovedCound.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(monthapprovedExpenses!=null&&monthapprovedExpenses.size()!=0){
                        Intent pending = new Intent( ExpenseDashBoardAdmin.this, ExpensesListAdmin.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("Expenses",monthapprovedExpenses);
                        bundle.putString("Title","Approved Expenses");
                        pending.putExtras(bundle);
                        startActivity(pending);
                    }
                }
            });


            mRejectedCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(monthrejectedExpenses!=null&&monthrejectedExpenses.size()!=0){

                        Intent pending = new Intent( ExpenseDashBoardAdmin.this, ExpensesListAdmin.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("Expenses",monthrejectedExpenses);
                        bundle.putString("Title","Rejected Expenses");
                        pending.putExtras(bundle);
                        startActivity(pending);
                    }
                }
            });


            mTotalCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(monthtotalExpenses!=null&&monthtotalExpenses.size()!=0){
                        Intent pending = new Intent( ExpenseDashBoardAdmin.this, ExpensesListAdmin.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("Expenses",monthtotalExpenses);
                        bundle.putString("Title","Total Expenses");
                        pending.putExtras(bundle);
                        startActivity(pending);
                    }
                }
            });

            mClaimedAmount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(monthclamiedExpenses!=null&&monthclamiedExpenses.size()!=0){
                        Intent pending = new Intent( ExpenseDashBoardAdmin.this, ExpensesListAdmin.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("Expenses",monthclamiedExpenses);
                        bundle.putString("Title","Claimed Expenses");
                        pending.putExtras(bundle);
                        startActivity(pending);
                    }
                }
            });


        }catch(Exception e){
            e.printStackTrace();
        }

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

                        if(totalExpenses!=null&&totalExpenses.size()!=0){
                            try {
                                getMonthlyExpenses(totalExpenses,monthValue,yearValue);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }else{

                            mNoExpensesLay.setVisibility(View.VISIBLE);
                        }




                        //String date =
                    }
                })
                .setNegativeButton(new OnCancelMonthDialogListener () {
                    @Override
                    public void onCancel( AlertDialog dialog) {
                        dialog.dismiss();
                    }
                });

        return customMonthPicker;
    }

    private void getExpenses(final int employeeId,final int month,final int year){

        totalExpenses = new ArrayList<>();


        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading Expenses Details..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new ThreadExecuter ().execute( new Runnable() {
            @Override
            public void run() {
                ExpensesApi apiService = Util.getClient().create(ExpensesApi.class);
                Call<ArrayList<Expenses>> call = apiService.getExpenseByEmployeeIdAndOrganizationId(PreferenceHandler.getInstance( ExpenseDashBoardAdmin.this).getCompanyId(),employeeId);

                call.enqueue(new Callback<ArrayList<Expenses>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Expenses>> call, Response<ArrayList<Expenses>> response) {
                        int statusCode = response.code();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {


                            if (progressDialog!=null)
                                progressDialog.dismiss();

                            try{


                                totalExpenses = response.body();
                                approvedExpenses = new ArrayList<>();
                                rejectedExpenses = new ArrayList<>();

                                pendingExpenses = new ArrayList<>();



                                if (totalExpenses !=null && totalExpenses.size()!=0) {

                                    getMonthlyExpenses(totalExpenses,month,year);


                                }else{

                                    mNoExpensesLay.setVisibility(View.VISIBLE);

                                }
                            }catch (Exception e){
                                e.printStackTrace();
                                mNoExpensesLay.setVisibility(View.VISIBLE);
                            }


                        }else {

                            if (progressDialog!=null)
                                progressDialog.dismiss();

                            mNoExpensesLay.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Expenses>> call, Throwable t) {
                        // Log error here since request failed
                        if (progressDialog!=null)
                            progressDialog.dismiss();
                        Log.e("TAG", t.toString());
                        mNoExpensesLay.setVisibility(View.VISIBLE);
                    }
                });
            }


        });
    }


    public void getMonthlyExpenses(ArrayList<Expenses> list, final int month, final int year) throws Exception{

        monthtotalExpenses = new ArrayList<>();
        monthapprovedExpenses = new ArrayList<>();
        monthrejectedExpenses = new ArrayList<>();
        monthpendingExpenses = new ArrayList<>();
        monthclamiedExpenses = new ArrayList<>();

        totalAmt = 0;
        claimedAmt = 0;


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


        for (Expenses leaves:list) {

            String froms = leaves.getDate();

            Date afromDate = null;

            if(froms!=null&&!froms.isEmpty()){

                if(froms.contains("T")){

                    String dojs[] = froms.split("T");

                    afromDate = new SimpleDateFormat("yyyy-MM-dd").parse(dojs[0]);


                }

            }




            if(afromDate!=null){

                if((edate.getTime()>=afromDate.getTime())
                        &&(adate.getTime()<=afromDate.getTime()))
                {

                    ExpenseAdminData taskAdminData = new ExpenseAdminData ();
                    taskAdminData.setEmployee(employee);
                    taskAdminData.setExpenses(leaves);
                    monthtotalExpenses.add(taskAdminData);

                    totalAmt = totalAmt+leaves.getAmount();
                    claimedAmt = claimedAmt+leaves.getClaimedAmount();

                    if(leaves.getClaimedAmount()!=0){
                        monthclamiedExpenses.add(taskAdminData);
                    }

                    if(leaves.getStatus()!=null&&!leaves.getStatus().isEmpty()){

                        if(leaves.getStatus().equalsIgnoreCase("Approved")){

                            monthapprovedExpenses.add(taskAdminData);

                        }else if(leaves.getStatus().equalsIgnoreCase("Rejected")){

                            monthrejectedExpenses.add(taskAdminData);

                        }else if(leaves.getStatus().equalsIgnoreCase("Pending")){

                            monthpendingExpenses.add(taskAdminData);

                        }
                    }



                }else{

                }

                if(leaves.getStatus()!=null&&!leaves.getStatus().isEmpty()){

                    ExpenseAdminData taskAdminData = new ExpenseAdminData ();
                    taskAdminData.setEmployee(employee);
                    taskAdminData.setExpenses(leaves);

                    if(leaves.getStatus().equalsIgnoreCase("Approved")){

                        approvedExpenses.add(taskAdminData);

                    }else if(leaves.getStatus().equalsIgnoreCase("Rejected")){

                        rejectedExpenses.add(taskAdminData);

                    }else if(leaves.getStatus().equalsIgnoreCase("Pending")){

                        pendingExpenses.add(taskAdminData);

                    }
                }


            }






        }

        if(monthtotalExpenses!=null&&monthtotalExpenses.size()!=0){

            mTotalCount.setText(""+monthtotalExpenses.size());
            mNoExpensesLay.setVisibility(View.GONE);
            mExpenseList.setVisibility(View.VISIBLE);

            adapter = new ExpenseListDataAdapter ( ExpenseDashBoardAdmin.this,monthtotalExpenses);
            mExpenseList.setAdapter(adapter);

            mTotalAmount.setText("₹ "+new DecimalFormat("#.##").format(totalAmt));
            mClaimedAmount.setText("₹ "+new DecimalFormat("#.##").format(claimedAmt));

            try{


                mApprovedCound.setText(""+monthapprovedExpenses.size());
                mRejectedCount.setText(""+monthrejectedExpenses.size());
                mPendingCount.setText(""+monthpendingExpenses.size());

            }catch (Exception e){
                e.printStackTrace();
            }

        }else{
            mNoExpensesLay.setVisibility(View.VISIBLE);
            mExpenseList.setVisibility(View.GONE);

            mTotalCount.setText("0");
            mTotalAmount.setText("₹ 0");
            mClaimedAmount.setText("₹ 0");
            mApprovedCound.setText("0");
            mRejectedCount.setText("0");
            mPendingCount.setText("0");
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getExpenses(employee.getEmployeeId(), monthValue,yearValue);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id)
        {
            case android.R.id.home:

                ExpenseDashBoardAdmin.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
