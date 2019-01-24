package app.zingo.employeemanagements.UI.Admin;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import app.zingo.employeemanagements.Model.LeaveNotificationManagers;
import app.zingo.employeemanagements.Model.Leaves;
import app.zingo.employeemanagements.Model.TaskNotificationManagers;
import app.zingo.employeemanagements.Model.Tasks;
import app.zingo.employeemanagements.R;
import app.zingo.employeemanagements.UI.Employee.ApplyLeaveScreen;
import app.zingo.employeemanagements.Utils.Constants;
import app.zingo.employeemanagements.Utils.PreferenceHandler;
import app.zingo.employeemanagements.Utils.Util;
import app.zingo.employeemanagements.WebApi.LeaveAPI;
import app.zingo.employeemanagements.WebApi.LeaveNotificationAPI;
import app.zingo.employeemanagements.WebApi.TaskNotificationAPI;
import app.zingo.employeemanagements.WebApi.TasksAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateTaskScreen extends AppCompatActivity {

    TextInputEditText mTaskName,mFrom,mTo,mDead;
    EditText mdesc;
    AppCompatButton mCreate;

    int employeeId,deptId;
    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
            setContentView(R.layout.activity_create_task_screen);

            mTaskName = (TextInputEditText)findViewById(R.id.task_name);
            mFrom = (TextInputEditText)findViewById(R.id.from_date);
            mTo = (TextInputEditText)findViewById(R.id.to_date);
            mDead = (TextInputEditText)findViewById(R.id.dead_line);
            mdesc = (EditText)findViewById(R.id.task_description);
            mCreate = (AppCompatButton) findViewById(R.id.apply_leave);

            Bundle bundle = getIntent().getExtras();

            if(bundle!=null){

                employeeId = bundle.getInt("EmployeeId");
                deptId = bundle.getInt("DepartmentId");
                type = bundle.getString("Type");
            }


            mFrom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    openDatePicker(mFrom);
                }
            });

            mTo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    openDatePicker(mTo);
                }
            });

            mDead.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    openDatePicker(mDead);
                }
            });

            mCreate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    validate();
                }
            });

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

        final Calendar newDate = Calendar.getInstance();

        //launch datepicker modal
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, final int year, final int monthOfYear, final int dayOfMonth) {
                        try {
                            Log.d("Date", "DATE SELECTED "+dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                            newDate.set(year,monthOfYear,dayOfMonth);

                            new TimePickerDialog(CreateTaskScreen.this, new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    newDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                    newDate.set(Calendar.MINUTE, minute);

                                    String date1 = (monthOfYear + 1)  + "/" + (dayOfMonth) + "/" + year +" "+hourOfDay+":"+minute;

                                    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");



                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
                                    try {
                                        Date fdate = simpleDateFormat.parse(date1);

                                        String from1 = sdf.format(fdate);


                                        tv.setText(from1);


                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), false).show();






                        }
                        catch (Exception ex)
                        {
                            ex.printStackTrace();
                        }


                    }
                }, mYear, mMonth, mDay);


        datePickerDialog.show();

    }

    public void validate(){


        String from = mFrom.getText().toString();
        String to = mTo.getText().toString();
        String dead = mDead.getText().toString();
        String taskName = mTaskName.getText().toString();
        String desc = mdesc.getText().toString();


        if(taskName.isEmpty()){

            Toast.makeText(this, "Task Name is required", Toast.LENGTH_SHORT).show();

        }else if(from.isEmpty()){

            Toast.makeText(this, "From date is required", Toast.LENGTH_SHORT).show();

        }else if(to.isEmpty()){

            Toast.makeText(this, "To date is required", Toast.LENGTH_SHORT).show();

        }else if(dead.isEmpty()){

            Toast.makeText(this, "Dead line is required", Toast.LENGTH_SHORT).show();

        }else if(desc.isEmpty()){

            Toast.makeText(this, "Leave Comment is required", Toast.LENGTH_SHORT).show();

        }else{

            try{

                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
                Tasks tasks = new Tasks();
                tasks.setTaskName(taskName);
                tasks.setTaskDescription(desc);
                tasks.setDeadLine(dead);
                tasks.setStartDate(new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(sdf.parse(from)));
                tasks.setReminderDate(new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(sdf.parse(from)));
                tasks.setEndDate(new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(sdf.parse(to)));
                tasks.setStatus("Pending");
                tasks.setComments("");
                tasks.setRemarks("");

                if(type!=null&&type.equalsIgnoreCase("Employee")){
                    tasks.setToReportEmployeeId(PreferenceHandler.getInstance(CreateTaskScreen.this).getManagerId());
                    tasks.setEmployeeId(employeeId);

                }else{
                    tasks.setToReportEmployeeId(PreferenceHandler.getInstance(CreateTaskScreen.this).getUserId());
                    tasks.setEmployeeId(employeeId);

                }

                tasks.setDepartmentId(0);


                try {
                    addTask(tasks);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }catch (Exception e){
                e.printStackTrace();
            }


        }
    }

    public long dateCal(String start,String end){

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy");
        System.out.println("Loigin "+start);
        System.out.println("Logout "+end);


        Date fd=null,td=null;



        try {
            fd = sdf.parse(""+start);
            td = sdf.parse(""+end);

            long diff = td.getTime() - fd.getTime();
            long Hours = diff / (60 * 60 * 1000) % 24;
            long Minutes = diff / (60 * 1000) % 60;
            long diffDays = diff / (24 * 60 * 60 * 1000);
            System.out.println("Diff "+diff);
            System.out.println("Hours "+Hours);
            System.out.println("Minutes "+Minutes);


            return  diffDays;
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }


    }

    public void addTask(final Tasks tasks) throws Exception{



        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Saving Details..");
        dialog.setCancelable(false);
        dialog.show();

        TasksAPI apiService = Util.getClient().create(TasksAPI.class);

        Call<Tasks> call = apiService.addTasks(tasks);

        call.enqueue(new Callback<Tasks>() {
            @Override
            public void onResponse(Call<Tasks> call, Response<Tasks> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {
                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201) {

                        Tasks s = response.body();

                        if(s!=null){


                            Toast.makeText(CreateTaskScreen.this, "Task Created Successfully", Toast.LENGTH_SHORT).show();
                         //  CreateTaskScreen.this.finish();
                            TaskNotificationManagers tn = new TaskNotificationManagers();
                            tn.setEmployeeId(""+s.getEmployeeId());
                            tn.setTaskName(s.getTaskName());
                            tn.setTaskDescription(s.getTaskDescription());
                            tn.setDeadLine(s.getDeadLine());
                            tn.setComments(s.getComments());
                            tn.setRemarks(s.getRemarks());
                            tn.setToReportEmployeeId(s.getToReportEmployeeId());
                            tn.setTitle("Task Allocated");
                            tn.setMessage(""+s.getTaskName());

                            tn.setTaskId(s.getTaskId());
                            tn.setDepartmentId(1);
                            savetask(tn);


                        }




                    }else {
                        Toast.makeText(CreateTaskScreen.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
            public void onFailure(Call<Tasks> call, Throwable t) {

                if(dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
                Toast.makeText(CreateTaskScreen.this, "Failed Due to "+t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());
            }
        });



    }

    public void savetask(final TaskNotificationManagers task) throws Exception{



        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Saving Details..");
        dialog.setCancelable(false);
        dialog.show();

        TaskNotificationAPI apiService = Util.getClient().create(TaskNotificationAPI.class);

        Call<TaskNotificationManagers> call = apiService.saveTask(task);

        call.enqueue(new Callback<TaskNotificationManagers>() {
            @Override
            public void onResponse(Call<TaskNotificationManagers> call, Response<TaskNotificationManagers> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {
                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201) {

                        TaskNotificationManagers s = response.body();

                        if(s!=null){

                            task.setSenderId(Constants.SENDER_ID);
                            task.setServerId(Constants.SERVER_ID);

                            sendTask(task);
                            //ApplyLeaveScreen.this.finish();


                        }




                    }else {
                        Toast.makeText(CreateTaskScreen.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
            public void onFailure(Call<TaskNotificationManagers> call, Throwable t) {

                if(dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
                Toast.makeText(CreateTaskScreen.this, "Failed Due to "+t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());
            }
        });



    }

    public void sendTask(final TaskNotificationManagers lm) throws Exception{



        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Sending Details..");
        dialog.setCancelable(false);
        dialog.show();

        TaskNotificationAPI apiService = Util.getClient().create(TaskNotificationAPI.class);

        Call<ArrayList<String>> call = apiService.sendTask(lm);

        call.enqueue(new Callback<ArrayList<String>>() {
            @Override
            public void onResponse(Call<ArrayList<String>> call, Response<ArrayList<String>> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {
                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201) {


                        CreateTaskScreen.this.finish();


                    }else {
                        Toast.makeText(CreateTaskScreen.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
            public void onFailure(Call<ArrayList<String>> call, Throwable t) {

                if(dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
                Toast.makeText(CreateTaskScreen.this, "Failed Due to "+t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());
            }
        });



    }
}
