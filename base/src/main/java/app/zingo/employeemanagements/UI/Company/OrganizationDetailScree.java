package app.zingo.employeemanagements.UI.Company;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import app.zingo.employeemanagements.Adapter.DepartmentAdapter;
import app.zingo.employeemanagements.Model.Departments;
import app.zingo.employeemanagements.Model.Employee;
import app.zingo.employeemanagements.Model.Organization;
import app.zingo.employeemanagements.base.R;
import app.zingo.employeemanagements.UI.Admin.DashBoardAdmin;
import app.zingo.employeemanagements.UI.Common.ReportManagementScreen;
import app.zingo.employeemanagements.UI.Landing.InternalServerErrorScreen;
import app.zingo.employeemanagements.UI.LandingScreen;
import app.zingo.employeemanagements.UI.Login.LoginScreen;
import app.zingo.employeemanagements.Utils.PreferenceHandler;
import app.zingo.employeemanagements.Utils.ThreadExecuter;
import app.zingo.employeemanagements.Utils.Util;
import app.zingo.employeemanagements.WebApi.DepartmentApi;
import app.zingo.employeemanagements.WebApi.EmployeeApi;
import app.zingo.employeemanagements.WebApi.OrganizationApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrganizationDetailScree extends AppCompatActivity {

    TextView mName,mAbout,mAddress,mBuild,mWebsite,mDepartmentCount;
    RecyclerView mDepartmentList;
    LinearLayout mDepartmentLay,mDepartmentMain;
    CardView mDepartmentCard;
    AppCompatButton mAddDepartment;
    CheckBox locationTrack;

    Organization organization;

    //maps related
    private GoogleMap mMap;
    MapView mapView;
    Marker marker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{

            setContentView(R.layout.activity_organization_detail_scree);

            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Organization Details");

            mName = findViewById(R.id.organization_name);
            mAbout = findViewById(R.id.organization_about);
            mAddress = findViewById(R.id.organization_address);
            mBuild = findViewById(R.id.organization_year);
            mWebsite = findViewById(R.id.organization_websites);
            mDepartmentCount = findViewById(R.id.department_count);
            mapView = findViewById(R.id.organization_map);
            mDepartmentList = findViewById(R.id.department_list);
            mDepartmentList.setEnabled(false);
            mDepartmentLay = findViewById(R.id.department_lay);
            mDepartmentMain = findViewById(R.id.department_layout_main);
            mDepartmentCard = findViewById(R.id.department_layout);
            mAddDepartment = findViewById(R.id.add_department);
            locationTrack = findViewById(R.id.location_track);

            mapView.onCreate(savedInstanceState);
            mapView.onResume();

           /* mDepartmentCard.setVisibility(View.GONE);
            mDepartmentLay.setVisibility(View.GONE);*/

           int userRoleId = PreferenceHandler.getInstance(OrganizationDetailScree.this).getUserRoleUniqueID();

           if(userRoleId==2){
               mDepartmentMain.setVisibility(View.VISIBLE);
               locationTrack.setVisibility(View.VISIBLE);
           }else{
               mDepartmentMain.setVisibility(View.GONE);
               locationTrack.setVisibility(View.GONE);
           }

            try {
                MapsInitializer.initialize(OrganizationDetailScree.this);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mMap = googleMap;


                    if (ActivityCompat.checkSelfPermission(OrganizationDetailScree.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(OrganizationDetailScree.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        return;
                    }
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    mMap.getProjection().getVisibleRegion().latLngBounds.getCenter();


                    try{
                        if(PreferenceHandler.getInstance(OrganizationDetailScree.this).getCompanyId()!=0){
                            getCompany(PreferenceHandler.getInstance(OrganizationDetailScree.this).getCompanyId());
                        }else{
                            Intent intent = new Intent(OrganizationDetailScree.this,InternalServerErrorScreen.class);
                            startActivity(intent);
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                        Intent i = new Intent(OrganizationDetailScree.this, InternalServerErrorScreen.class);

                        startActivity(i);
                    }


                }
            });

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

            mAddDepartment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    departmentAlert();
                }
            });

            locationTrack.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton button,
                                             boolean isChecked) {

                    // If it is checked then show password else hide password
                    if (isChecked) {

                        if(organization!=null){

                            organization.setWorking(true);
                        }


                    } else {

                        if(organization!=null){

                            organization.setWorking(false);
                        }
                    }

                }
            });




        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public void getCompany(final int id) {

        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {

                final OrganizationApi subCategoryAPI = Util.getClient().create(OrganizationApi.class);
                Call<ArrayList<Organization>> getProf = subCategoryAPI.getOrganizationById(id);
                //Call<ArrayList<Blogs>> getBlog = blogApi.getBlogs();

                getProf.enqueue(new Callback<ArrayList<Organization>>() {

                    @Override
                    public void onResponse(Call<ArrayList<Organization>> call, Response<ArrayList<Organization>> response) {

                        if (response.code() == 200||response.code() == 201||response.code() == 204)
                        {
                            organization = response.body().get(0);

                            if(organization!=null){

                                mName.setText(""+organization.getOrganizationName());
                                mAbout.setText(""+organization.getAboutUs());
                                mAddress.setText(""+organization.getAddress()+"\n"+organization.getCity()+"\n"+organization.getState());

                                if(organization.getBuiltYear()!=null&&!organization.getBuiltYear().isEmpty()){
                                    mBuild.setText(""+organization.getBuiltYear());
                                }else{
                                    mBuild.setVisibility(View.GONE);
                                }

                                mWebsite.setText(""+organization.getWebsite());


                                if(organization.isWorking()){

                                    locationTrack.setChecked(true);

                                }else{

                                    locationTrack.setChecked(false);


                                }


                                mMap.clear();

                                LatLng latlng = new LatLng(Double.parseDouble(organization.getLatitude()),Double.parseDouble(organization.getLongitude()));
                                marker = mMap.addMarker(new MarkerOptions()
                                        .position(latlng)
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                                CameraPosition cameraPosition = new CameraPosition.Builder().zoom(14).target(latlng).build();
                                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                                //Only for founder

                                if(organization.getDepartment()!=null&&organization.getDepartment().size()!=0){


                                    ArrayList<Departments> departmentsArrayList = new ArrayList<>();

                                    for(int i=0;i<organization.getDepartment().size();i++){

                                        if(!organization.getDepartment().get(i).getDepartmentName().equalsIgnoreCase("Founders")){

                                            departmentsArrayList.add(organization.getDepartment().get(i));
                                        }
                                    }

                                    if(departmentsArrayList!=null&&departmentsArrayList.size()!=0){

                                        mDepartmentCount.setText(""+departmentsArrayList.size());
                                        DepartmentAdapter adapter = new DepartmentAdapter(OrganizationDetailScree.this,departmentsArrayList);
                                        mDepartmentList.setAdapter(adapter);
                                    }



                                }else{

                                    getDepartment(organization.getOrganizationId());
                                }


                            }else{
                                Toast.makeText(OrganizationDetailScree.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }


                        }else{

                            Toast.makeText(OrganizationDetailScree.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Organization>> call, Throwable t) {

                        Toast.makeText(OrganizationDetailScree.this, "Something went wrong", Toast.LENGTH_SHORT).show();

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

                                ArrayList<Departments> departmentsArrayList = new ArrayList<>();

                                for(int i=0;i<departmentsList.size();i++){

                                    if(!departmentsList.get(i).getDepartmentName().equalsIgnoreCase("Founders")){

                                        departmentsArrayList.add(departmentsList.get(i));
                                    }
                                }

                                if(departmentsArrayList!=null&&departmentsArrayList.size()!=0){

                                    mDepartmentCount.setText(""+departmentsArrayList.size());
                                    DepartmentAdapter adapter = new DepartmentAdapter(OrganizationDetailScree.this,departmentsArrayList);
                                    mDepartmentList.setAdapter(adapter);
                                }



                            }
                            else
                            {


                            }
                        }
                        else
                        {

                            Toast.makeText(OrganizationDetailScree.this,response.message(),Toast.LENGTH_SHORT).show();
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

        AlertDialog.Builder builder = new AlertDialog.Builder(OrganizationDetailScree.this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View views = inflater.inflate(R.layout.custom_alert_box_department, null);

        builder.setView(views);
        final Button mSave = views.findViewById(R.id.save);
        final EditText desc = views.findViewById(R.id.department_description);
        final TextInputEditText mName = views.findViewById(R.id.department_name);


        final android.support.v7.app.AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);

        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String name = mName.getText().toString();
                String descrp = desc.getText().toString();

                if(name.isEmpty()){

                    Toast.makeText(OrganizationDetailScree.this, "Please enter Department Name", Toast.LENGTH_SHORT).show();

                }else if (descrp.isEmpty()){

                    Toast.makeText(OrganizationDetailScree.this, "Please enter Department Description", Toast.LENGTH_SHORT).show();
                }else{

                    Departments departments = new Departments();
                    departments.setDepartmentName(name);
                    departments.setDepartmentDescription(descrp);
                    departments.setOrganizationId(organization.getOrganizationId());

                    try {
                        addDepartments(departments,dialog);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


            }
        });

    }


    public void addDepartments(final Departments departments,final AlertDialog dialogs) {


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

                            Toast.makeText(OrganizationDetailScree.this, "Your Organization Creted Successfully ", Toast.LENGTH_SHORT).show();

                            dialogs.dismiss();

                            getDepartment(organization.getOrganizationId());


                        }




                    }else {
                        Toast.makeText(OrganizationDetailScree.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(OrganizationDetailScree.this, "Failed Due to "+t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());
            }
        });



    }

    public boolean onCreateOptionsMenu(Menu menu) {

        if(PreferenceHandler.getInstance(OrganizationDetailScree.this).getUserRoleUniqueID()==2){
            getMenuInflater().inflate(R.menu.menu_edit, menu);
            return true;
        }else{
            getMenuInflater().inflate(R.menu.menu_edit, menu);
            return false;
        }

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            try {
                if (organization != null) {
                    updateOrg(organization);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            OrganizationDetailScree.this.finish();

        } else if (id == R.id.action_edit) {
            Intent org = new Intent(OrganizationDetailScree.this, OrganizationEditScreen.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("Organization", organization);
            org.putExtras(bundle);
            startActivity(org);
            OrganizationDetailScree.this.finish();

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            if(organization!=null){
                updateOrg(organization);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateOrg(final Organization organization) {




        OrganizationApi apiService = Util.getClient().create(OrganizationApi.class);

        Call<Organization> call = apiService.updateOrganization(organization.getOrganizationId(),organization);

        call.enqueue(new Callback<Organization>() {
            @Override
            public void onResponse(Call<Organization> call, Response<Organization> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {


                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201|| statusCode == 204) {



                        OrganizationDetailScree.this.finish();



                    }else {
                        Toast.makeText(OrganizationDetailScree.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception ex)
                {


                    ex.printStackTrace();
                }
//                callGetStartEnd();
            }

            @Override
            public void onFailure(Call<Organization> call, Throwable t) {


                Toast.makeText(OrganizationDetailScree.this, "Failed Due to "+t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());
            }
        });



    }
}
