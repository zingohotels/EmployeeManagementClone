package app.zingo.employeemanagements.UI.Company;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
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

import java.util.ArrayList;

import app.zingo.employeemanagements.Model.Departments;
import app.zingo.employeemanagements.Model.Employee;
import app.zingo.employeemanagements.Model.Organization;
import app.zingo.employeemanagements.R;
import app.zingo.employeemanagements.UI.Admin.DashBoardAdmin;
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

    Organization organization;

    //maps related
    private GoogleMap mMap;
    MapView mapView;
    int activityId;
    Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{

            setContentView(R.layout.activity_organization_detail_scree);

            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Organization Details");

            mName = (TextView)findViewById(R.id.organization_name);
            mAbout = (TextView)findViewById(R.id.organization_about);
            mAddress = (TextView)findViewById(R.id.organization_address);
            mBuild = (TextView)findViewById(R.id.organization_year);
            mWebsite = (TextView)findViewById(R.id.organization_websites);
            mDepartmentCount = (TextView)findViewById(R.id.department_count);
            mapView = (MapView) findViewById(R.id.organization_map);
            mDepartmentList = (RecyclerView) findViewById(R.id.department_list);

            mapView.onCreate(savedInstanceState);
            mapView.onResume();

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
                        getCompany(PreferenceHandler.getInstance(OrganizationDetailScree.this).getCompanyId());
                    }catch (Exception e){
                        e.printStackTrace();
                    }


                }
            });



        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public void getCompany(final int id){

        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {

                final OrganizationApi subCategoryAPI = Util.getClient().create(OrganizationApi.class);
                Call<Organization> getProf = subCategoryAPI.getOrganizationById(id);
                //Call<ArrayList<Blogs>> getBlog = blogApi.getBlogs();

                getProf.enqueue(new Callback<Organization>() {

                    @Override
                    public void onResponse(Call<Organization> call, Response<Organization> response) {

                        if (response.code() == 200||response.code() == 201||response.code() == 204)
                        {
                            organization = response.body();

                            if(organization!=null){

                                mName.setText(""+organization.getOrganizationName());
                                mAbout.setText(""+organization.getAboutUs());
                                mAddress.setText(""+organization.getAddress()+"\n"+organization.getCity()+"\n"+organization.getState());
                                mBuild.setText(""+organization.getBuiltYear());
                                mWebsite.setText(""+organization.getWebsite());


                                mMap.clear();

                                LatLng latlng = new LatLng(Double.parseDouble(organization.getLatitude()),Double.parseDouble(organization.getLongitude()));
                                marker = mMap.addMarker(new MarkerOptions()
                                        .position(latlng)
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                                CameraPosition cameraPosition = new CameraPosition.Builder().zoom(14).target(latlng).build();
                                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                                if(organization.getDepartment()!=null&&organization.getDepartment().size()!=0){
                                    mDepartmentCount.setText(""+organization.getDepartment().size());
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
                    public void onFailure(Call<Organization> call, Throwable t) {

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

                                mDepartmentCount.setText(""+departmentsList.size());

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id)
        {
            case android.R.id.home:

                OrganizationDetailScree.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
