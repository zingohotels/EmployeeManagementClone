package app.zingo.employeemanagements.ui.NewAdminDesigns;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
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

import app.zingo.employeemanagements.adapter.DepartmentAdapter;
import app.zingo.employeemanagements.model.Departments;
import app.zingo.employeemanagements.utils.PreferenceHandler;
import app.zingo.employeemanagements.utils.ThreadExecuter;
import app.zingo.employeemanagements.utils.Util;
import app.zingo.employeemanagements.WebApi.DepartmentApi;
import app.zingo.employeemanagements.base.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DepartmentLilstScreen extends AppCompatActivity {

    RecyclerView mDepartmentList;
    LinearLayout mDepartmentLay,mDepartmentMain;
    CardView mDepartmentCard;
    AppCompatButton mAddDepartment;
    TextView mDepartmentCount;
    int orgId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{

            setContentView(R.layout.activity_department_lilst_screen);

            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Department List");

            mDepartmentCount = findViewById(R.id.department_count);

            mDepartmentList = findViewById(R.id.department_list);
            mDepartmentLay = findViewById(R.id.department_lay);
            mDepartmentMain = findViewById(R.id.department_layout_main);
            mDepartmentCard = findViewById(R.id.department_layout);
            mAddDepartment = findViewById(R.id.add_department);

            Bundle bun = getIntent().getExtras();

            if(bun!=null){

                orgId = bun.getInt("OrganizationId",0);
            }

            int userRoleId = PreferenceHandler.getInstance( DepartmentLilstScreen.this).getUserRoleUniqueID();

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

            if(orgId!=0){

                getDepartment(orgId);
            }else{
                getDepartment(PreferenceHandler.getInstance( DepartmentLilstScreen.this).getCompanyId());
            }

            mDepartmentLay.setVisibility(View.VISIBLE);

            mAddDepartment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(orgId!=0){

                        departmentAlert(orgId);
                    }else{
                        departmentAlert(PreferenceHandler.getInstance( DepartmentLilstScreen.this).getCompanyId());
                    }

                }
            });

        }catch(Exception e){
            e.printStackTrace();
        }

    }
    private void getDepartment(final int id){

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

                                ArrayList<Departments> departmentsArrayList = new ArrayList<>();

                                for(int i=0;i<departmentsList.size();i++){

                                    if(!departmentsList.get(i).getDepartmentName().equalsIgnoreCase("Founders")){

                                        departmentsArrayList.add(departmentsList.get(i));
                                    }
                                }

                                if(departmentsArrayList!=null&&departmentsArrayList.size()!=0){

                                    mDepartmentCount.setText(""+departmentsArrayList.size());
                                    DepartmentAdapter adapter = new DepartmentAdapter ( DepartmentLilstScreen.this,departmentsArrayList);
                                    mDepartmentList.setAdapter(adapter);
                                }



                            }
                            else
                            {


                            }
                        }
                        else
                        {

                            Toast.makeText( DepartmentLilstScreen.this,response.message(),Toast.LENGTH_SHORT).show();
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


    private void departmentAlert(final int id){

        AlertDialog.Builder builder = new AlertDialog.Builder( DepartmentLilstScreen.this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View views = inflater.inflate(R.layout.custom_alert_box_department, null);

        builder.setView(views);
        final Button mSave = views.findViewById(R.id.save);
        final EditText desc = views.findViewById(R.id.department_description);
        final TextInputEditText mName = views.findViewById(R.id.department_name);


        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);

        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String name = mName.getText().toString();
                String descrp = desc.getText().toString();

                if(name.isEmpty()){

                    Toast.makeText( DepartmentLilstScreen.this, "Please enter Department Name", Toast.LENGTH_SHORT).show();

                }else if (descrp.isEmpty()){

                    Toast.makeText( DepartmentLilstScreen.this, "Please enter Department Description", Toast.LENGTH_SHORT).show();
                }else{

                    Departments departments = new Departments();
                    departments.setDepartmentName(name);
                    departments.setDepartmentDescription(descrp);
                    departments.setOrganizationId(id);

                    try {
                        addDepartments(departments,dialog);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


            }
        });

    }


    public void addDepartments(final Departments departments, final AlertDialog dialogs) {


        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Saving Details..");
        dialog.setCancelable(false);
        dialog.show();

        DepartmentApi apiService = Util.getClient().create( DepartmentApi.class);

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

                            Toast.makeText( DepartmentLilstScreen.this, "Department Creted Successfully ", Toast.LENGTH_SHORT).show();

                            dialogs.dismiss();

                            if(orgId!=0){

                                getDepartment(orgId);
                            }else{
                                getDepartment(PreferenceHandler.getInstance( DepartmentLilstScreen.this).getCompanyId());
                            }


                        }




                    }else {
                        Toast.makeText( DepartmentLilstScreen.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
                //Toast.makeText(DepartmentLilstScreen.this, "Failed due to Bad Internet Connection", Toast.LENGTH_SHORT).show();
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
