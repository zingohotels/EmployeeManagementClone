package app.zingo.employeemanagements.UI.NewAdminDesigns;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import app.zingo.employeemanagements.Adapter.DepartmentAdapter;
import app.zingo.employeemanagements.Model.Departments;
import app.zingo.employeemanagements.R;
import app.zingo.employeemanagements.UI.Company.OrganizationDetailScree;
import app.zingo.employeemanagements.Utils.PreferenceHandler;
import app.zingo.employeemanagements.Utils.ThreadExecuter;
import app.zingo.employeemanagements.Utils.Util;
import app.zingo.employeemanagements.WebApi.DepartmentApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DepartmentLilstScreen extends AppCompatActivity {

    RecyclerView mDepartmentList;
    LinearLayout mDepartmentLay,mDepartmentMain;
    CardView mDepartmentCard;
    AppCompatButton mAddDepartment;
    TextView mDepartmentCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{

            setContentView(R.layout.activity_department_lilst_screen);

            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Department List");

            mDepartmentCount = (TextView)findViewById(R.id.department_count);

            mDepartmentList = (RecyclerView) findViewById(R.id.department_list);
            mDepartmentLay = (LinearLayout) findViewById(R.id.department_lay);
            mDepartmentMain = (LinearLayout) findViewById(R.id.department_layout_main);
            mDepartmentCard = (CardView) findViewById(R.id.department_layout);
            mAddDepartment = (AppCompatButton) findViewById(R.id.add_department);

            int userRoleId = PreferenceHandler.getInstance(DepartmentLilstScreen.this).getUserRoleUniqueID();

            if(userRoleId==2){
                mDepartmentMain.setVisibility(View.VISIBLE);
            }else{
                mDepartmentMain.setVisibility(View.GONE);
            }

            mDepartmentCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(mDepartmentLay.getVisibility()==View.GONE){

                        mDepartmentLay.setVisibility(View.VISIBLE);

                    }else{

                        mDepartmentLay.setVisibility(View.GONE);
                    }
                }
            });

            getDepartment();
            mDepartmentLay.setVisibility(View.VISIBLE);

            mAddDepartment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    departmentAlert();
                }
            });

        }catch(Exception e){
            e.printStackTrace();
        }

    }
    private void getDepartment(){

        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {


                DepartmentApi apiService =
                        Util.getClient().create(DepartmentApi.class);

                Call<ArrayList<Departments>> call = apiService.getDepartmentByOrganization(PreferenceHandler.getInstance(DepartmentLilstScreen.this).getCompanyId());

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

                                ArrayList<Departments> departmentsArrayList = new ArrayList<>();

                                for(int i=0;i<departmentsList.size();i++){

                                    if(!departmentsList.get(i).getDepartmentName().equalsIgnoreCase("Founders")){

                                        departmentsArrayList.add(departmentsList.get(i));
                                    }
                                }

                                if(departmentsArrayList!=null&&departmentsArrayList.size()!=0){

                                    mDepartmentCount.setText(""+departmentsArrayList.size());
                                    DepartmentAdapter adapter = new DepartmentAdapter(DepartmentLilstScreen.this,departmentsArrayList);
                                    mDepartmentList.setAdapter(adapter);
                                }



                            }
                            else
                            {


                            }
                        }
                        else
                        {

                            Toast.makeText(DepartmentLilstScreen.this,response.message(),Toast.LENGTH_SHORT).show();
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


    private void departmentAlert(){

        AlertDialog.Builder builder = new AlertDialog.Builder(DepartmentLilstScreen.this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View views = inflater.inflate(R.layout.custom_alert_box_department, null);

        builder.setView(views);
        final Button mSave = (Button) views.findViewById(R.id.save);
        final EditText desc = (EditText) views.findViewById(R.id.department_description);
        final TextInputEditText mName = (TextInputEditText) views.findViewById(R.id.department_name);


        final android.support.v7.app.AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);

        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String name = mName.getText().toString();
                String descrp = desc.getText().toString();

                if(name.isEmpty()){

                    Toast.makeText(DepartmentLilstScreen.this, "Please enter Department Name", Toast.LENGTH_SHORT).show();

                }else if (descrp.isEmpty()){

                    Toast.makeText(DepartmentLilstScreen.this, "Please enter Department Description", Toast.LENGTH_SHORT).show();
                }else{

                    Departments departments = new Departments();
                    departments.setDepartmentName(name);
                    departments.setDepartmentDescription(descrp);
                    departments.setOrganizationId(PreferenceHandler.getInstance(DepartmentLilstScreen.this).getCompanyId());

                    try {
                        addDepartments(departments,dialog);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


            }
        });

    }


    public void addDepartments(final Departments departments,final AlertDialog dialogs) throws Exception{


        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Saving Details..");
        dialog.setCancelable(false);
        dialog.show();

        DepartmentApi apiService = Util.getClient().create(DepartmentApi.class);

        Call<Departments> call = apiService.addDepartments(departments);

        call.enqueue(new Callback<Departments>() {
            @Override
            public void onResponse(Call<Departments> call, Response<Departments> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {
                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201) {

                        Departments s = response.body();

                        if(s!=null){

                            Toast.makeText(DepartmentLilstScreen.this, "Your Organization Creted Successfully ", Toast.LENGTH_SHORT).show();

                            dialogs.dismiss();

                            getDepartment();


                        }




                    }else {
                        Toast.makeText(DepartmentLilstScreen.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
            public void onFailure(Call<Departments> call, Throwable t) {

                if(dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
                Toast.makeText(DepartmentLilstScreen.this, "Failed Due to "+t.getMessage(), Toast.LENGTH_SHORT).show();
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

                DepartmentLilstScreen.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
