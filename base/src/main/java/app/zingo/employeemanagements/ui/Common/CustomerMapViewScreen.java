package app.zingo.employeemanagements.ui.Common;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Random;

import app.zingo.employeemanagements.WebApi.CustomerAPI;
import app.zingo.employeemanagements.base.R;
import app.zingo.employeemanagements.model.Customer;
import app.zingo.employeemanagements.utils.PreferenceHandler;
import app.zingo.employeemanagements.utils.TrackGPS;
import app.zingo.employeemanagements.utils.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerMapViewScreen extends AppCompatActivity {

    MapView mapView;
    LinearLayout mlocation;
    LinearLayout scrollableLay,mMapShow;

    ImageView mShowHide;
    ArrayList< LatLng > MarkerPoints;
    ArrayList< Marker > markerList;
    ArrayList <Integer> colorValue;
    int screenheight,screenwidth;

    //maps related
    private GoogleMap mMap;
    private LatLng lastKnownLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
            setContentView(R.layout.activity_customer_map_view_screen);

            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Customer List");

            mapView = findViewById(R.id.employee_live_list_map);
            mlocation = findViewById(R.id.location_list);
            scrollableLay = findViewById(R.id.nestedScrollView);
            mMapShow = findViewById(R.id.map_lay_short);
            mShowHide = findViewById(R.id.show_lay_hide);

            final ViewGroup.LayoutParams params = scrollableLay.getLayoutParams();
            final ViewGroup.LayoutParams paramsm = mMapShow.getLayoutParams();

            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            screenheight = displayMetrics.heightPixels;
            screenwidth = displayMetrics.widthPixels;

            mapView.onCreate(savedInstanceState);
            mapView.onResume();

            MarkerPoints = new ArrayList<>();
            markerList = new ArrayList<>();


            try {
                MapsInitializer.initialize( CustomerMapViewScreen.this);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            mapView.getMapAsync(new OnMapReadyCallback () {
                @Override
                public void onMapReady( GoogleMap googleMap) {
                    mMap = googleMap;


                    if ( ActivityCompat.checkSelfPermission( CustomerMapViewScreen.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission( CustomerMapViewScreen.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        return;
                    }

                    TrackGPS gp = new TrackGPS ( CustomerMapViewScreen.this);

                    try{

                        LatLng SomePos = new LatLng(Double.parseDouble( PreferenceHandler.getInstance( CustomerMapViewScreen.this).getOrganizationLati()),Double.parseDouble(PreferenceHandler.getInstance( CustomerMapViewScreen.this).getOrganizationLongi()));

                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        mMap.setMyLocationEnabled(true);
                        mMap.setTrafficEnabled(false);
                        mMap.setIndoorEnabled(false);
                        mMap.setBuildingsEnabled(true);
                        mMap.getUiSettings().setZoomControlsEnabled(true);
                        mMap.moveCamera( CameraUpdateFactory.newLatLng(SomePos));
                        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                                .target(googleMap.getCameraPosition().target)
                                .zoom(10)
                                .bearing(30)
                                .tilt(45)
                                .build()));

                        try{


                            getCustomers(PreferenceHandler.getInstance ( CustomerMapViewScreen.this ).getCompanyId ());

                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }



                }
            });

            mShowHide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(mlocation.getVisibility()==View.VISIBLE){


                        // Changes the height and width to the specified *pixels*
                        params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                        params.width = screenwidth;
                        scrollableLay.setLayoutParams(params);

                        paramsm.height = LinearLayout.LayoutParams.MATCH_PARENT;
                        paramsm.width = screenwidth;
                        mMapShow.setLayoutParams(paramsm);

                        mlocation.setVisibility( View.GONE);
                        mShowHide.setImageResource(R.drawable.up_arrow_location);


                    }else{

                        // Changes the height and width to the specified *pixels*

                        params.width = screenwidth;
                        params.height = screenheight/2;
                        scrollableLay.setLayoutParams(params);

                        paramsm.width = screenwidth;
                        paramsm.height = screenheight/2;
                        mMapShow .setLayoutParams(paramsm);

                        mlocation.setVisibility(View.VISIBLE);
                        mShowHide.setImageResource(R.drawable.down_arrow_location);
                    }

                }
            });



        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void getCustomers(final int id) {


        final ProgressDialog progressDialog = new ProgressDialog( CustomerMapViewScreen.this);
        progressDialog.setTitle("Loading Details...");
        progressDialog.setCancelable(false);
        progressDialog.show();


        colorValue = new ArrayList<>();
        final CustomerAPI orgApi = Util.getClient().create( CustomerAPI.class);
        Call <ArrayList< Customer >> getProf = orgApi.getCustomerByOrganizationId(id);
        //Call<ArrayList<Blogs>> getBlog = blogApi.getBlogs();

        getProf.enqueue(new Callback <ArrayList< Customer >> () {

            @Override
            public void onResponse( Call<ArrayList< Customer >> call, Response <ArrayList< Customer >> response) {

                if (progressDialog!=null&&progressDialog.isShowing())
                    progressDialog.dismiss();

                if (response.code() == 200||response.code() == 201||response.code() == 204)
                {


                    ArrayList< Customer > customers = response.body();
                    ArrayList< Customer > customersList = new ArrayList <> (  );

                    if(customers!=null&&customers.size()!=0){



                        if(mMap!=null){
                            mMap.clear();
                        }

                        if(markerList!=null&&markerList.size()!=0){
                            markerList.clear();
                        }

                        for( Customer customer:customers){


                            String lati = customer.getLatitude ();
                            String lngi = customer.getLongitude ();

                            double latiValue= Double.parseDouble ( lati );
                            double lngiValue= Double.parseDouble ( lngi );

                            if(lngi!=null&&lati!=null&&latiValue!=0&&lngiValue!=0){

                                customersList.add ( customer );
                                lastKnownLatLng = new LatLng(Double.parseDouble(PreferenceHandler.getInstance( CustomerMapViewScreen.this).getOrganizationLati()),Double.parseDouble(PreferenceHandler.getInstance( CustomerMapViewScreen.this).getOrganizationLongi()));

                                markerList.add(createMarker(latiValue, lngiValue,customer.getCustomerName ()+"\nLast Location ",""+""+customer.getCustomerAddress ()));



                            }



                        }



                    }


                }else{

                    if (progressDialog!=null&&progressDialog.isShowing())
                        progressDialog.dismiss();

                    Toast.makeText( CustomerMapViewScreen.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure( Call<ArrayList< Customer >> call, Throwable t) {


                if (progressDialog!=null&&progressDialog.isShowing())
                    progressDialog.dismiss();

                Toast.makeText( CustomerMapViewScreen.this, "Something went wrong", Toast.LENGTH_SHORT).show();

            }
        });
    }

    protected Marker createMarker(double latitude, double longitude, String title, String snippet) {

        //BitmapDescriptorFactory.defaultMarker(new Random().nextInt(360));
        float color = new Random ().nextInt(360);
        colorValue.add((int)color);

        return mMap.addMarker(new MarkerOptions ()
                .position(new LatLng(latitude, longitude))
                .anchor(0.5f, 0.5f)
                .title(title)
                .snippet(snippet)
                .icon( BitmapDescriptorFactory.defaultMarker(color)));


    }

    public boolean onCreateOptionsMenu( Menu menu) {

        getMenuInflater().inflate(R.menu.employee_icon, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            CustomerMapViewScreen.this.finish();

        } else if(id==R.id.action_employee){
            Intent chnage = new Intent( CustomerMapViewScreen.this, CustomerList.class);
            startActivity(chnage);
        }
        return super.onOptionsItemSelected(item);
    }

}
