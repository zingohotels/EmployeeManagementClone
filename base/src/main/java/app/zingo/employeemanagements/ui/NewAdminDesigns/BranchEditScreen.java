package app.zingo.employeemanagements.ui.NewAdminDesigns;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputEditText;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import app.zingo.employeemanagements.Custom.MapViewScroll;
import app.zingo.employeemanagements.model.Organization;
import app.zingo.employeemanagements.utils.TrackGPS;
import app.zingo.employeemanagements.utils.Util;
import app.zingo.employeemanagements.WebApi.OrganizationApi;
import app.zingo.employeemanagements.base.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BranchEditScreen extends AppCompatActivity {

    TextInputEditText mOrgName;
    EditText mAbout;
    AppCompatButton mUpdate;


    private EditText  lat, lng;
    private TextView location;
    Button saveMapDetailsBtn;
    private GoogleMap mMap;
    MapViewScroll mapView;
    Marker marker;
    TrackGPS trackGPS;
    double mLatitude, mLongitude;
    String city="",state="";


    DecimalFormat df2 = new DecimalFormat(".##########");
    public static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    public String TAG = "MAPLOCATION",placeId;



    int year = 0;
    Organization organization;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
            setContentView(R.layout.activity_branch_edit_screen);

            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Edit Branch");

            Bundle bun = getIntent().getExtras();

            if(bun!=null){

                organization = ( Organization )bun.getSerializable("Organization");
            }


            mOrgName = findViewById(R.id.name);

            //mCheckIn = findViewById(R.id.check_time);
            mAbout = findViewById(R.id.about);
            mUpdate = findViewById(R.id.updateCompany);

            mapView = findViewById(R.id.google_map_view);
            location = findViewById(R.id.location_et);

            lat = findViewById(R.id.lat_et);
            lng = findViewById(R.id.lng_et);


            trackGPS = new TrackGPS (this);

            mapView.onCreate(savedInstanceState);
            mapView.onResume();

            try {
                MapsInitializer.initialize( BranchEditScreen.this);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent =
                                new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY/*MODE_FULLSCREEN*/)
                                        .build( BranchEditScreen.this);
                        startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                    } catch (GooglePlayServicesRepairableException e) {
                        e.printStackTrace();
                        // TODO: Handle the error.
                    } catch (GooglePlayServicesNotAvailableException e) {
                        // TODO: Handle the error.
                        e.printStackTrace();
                    }
                }
            });
            mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mMap = googleMap;


                    if ( ActivityCompat.checkSelfPermission( BranchEditScreen.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission( BranchEditScreen.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling

                        return;
                    }
                    mMap.getUiSettings().setZoomControlsEnabled(true);
                    mMap.setMyLocationEnabled(true);
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    mMap.getProjection().getVisibleRegion().latLngBounds.getCenter();



                    mLatitude = Double.parseDouble(organization.getLatitude());
                    mLongitude = Double.parseDouble(organization.getLongitude());
                    LatLng latLng = new LatLng(mLatitude,mLongitude);
                    final CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(17).build();
                    marker = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.
                            defaultMarker(BitmapDescriptorFactory.HUE_RED))
                            .position(latLng));
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(LatLng latLng) {

                            lat.setText(df2.format(latLng.latitude)+"");
                            lng.setText(df2.format(latLng.longitude)+"");
                            String add = getAddress(latLng);
                            location.setText(add);
                            mMap.clear();
                            marker = mMap.addMarker(new MarkerOptions()
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                                    .position(latLng));
                            CameraPosition cameraPosition1 = new CameraPosition.Builder().target(latLng).zoom(80).build();
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition1));
                        }
                    });

                }
            });



            String currentYear = new SimpleDateFormat("yyyyy").format(new Date());

            year = Integer.parseInt(currentYear);

            if(organization!=null){

                setDetails(organization);
            }else{
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }


            mUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    validate();
                }
            });




        }catch(Exception e){
            e.printStackTrace();
        }

    }

    private String getAddress(LatLng latLng) {
        Geocoder geocoder = new Geocoder( BranchEditScreen.this, Locale.getDefault());
        String result = null;
        try {
            List<Address> addressList = geocoder.getFromLocation(
                    latLng.latitude, latLng.longitude, 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    sb.append(address.getAddressLine(i)).append(",");
                }

                result = address.getAddressLine(0);
                state = address.getAdminArea();


                return result;
            }
            return result;
        } catch (IOException e) {
            Log.e("MapLocation", "Unable connect to Geocoder", e);
            return result;
        }

    }

    public void setDetails(final Organization org){

        mOrgName.setText(org.getOrganizationName());

        mAbout.setText(org.getAboutUs());

        String cheIT = org.getPlaceId();

      /*  if(cheIT!=null&&!cheIT.isEmpty()){
            mCheckIn.setText(""+cheIT);
        }*/
        mAbout.setText(org.getAboutUs());
        city = org.getCity();
        state = org.getState();

        try{


            location.setText(org.getAddress());
            lat.setText(org.getLatitude());
            lng.setText(org.getLongitude());
            mMap.clear();
            LatLng orgLatlng = new LatLng(Double.parseDouble(org.getLatitude()),Double.parseDouble(org.getLongitude()));
            marker = mMap.addMarker(new MarkerOptions()
                    .position(orgLatlng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            CameraPosition cameraPosition = new CameraPosition.Builder().zoom(80).target(orgLatlng).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public void validate(){

        String orgName = mOrgName.getText().toString();
        String about = mAbout.getText().toString();

        String address = location.getText().toString();
        String lati = lat.getText().toString();
        String longi = lng.getText().toString();
        // String cheIT = mCheckIn.getText().toString();


        if(orgName.isEmpty()){
            Toast.makeText( BranchEditScreen.this, "Field should not be empty", Toast.LENGTH_SHORT).show();

        }else if(about.isEmpty()){
            Toast.makeText( BranchEditScreen.this, "Field should not be empty", Toast.LENGTH_SHORT).show();

        }else if(address.isEmpty()){
            Toast.makeText( BranchEditScreen.this, "Field should not be empty", Toast.LENGTH_SHORT).show();

        }else if(lati.isEmpty()){
            Toast.makeText( BranchEditScreen.this, "Field should not be empty", Toast.LENGTH_SHORT).show();
        }else if(longi.isEmpty()){
            Toast.makeText( BranchEditScreen.this, "Field should not be empty", Toast.LENGTH_SHORT).show();

        }else{

            Organization orgs = organization;
            orgs.setOrganizationName(orgName);
            orgs.setAboutUs(about);

            orgs.setAddress(address);
            orgs.setLongitude(longi);
            orgs.setLatitude(lati);
            orgs.setState(state);

            /*if(cheIT!=null&&!cheIT.isEmpty()){
                orgs.setPlaceId(cheIT);
            }*/
            try {
                updateOrg(orgs);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id)
        {
            case android.R.id.home:

                BranchEditScreen.this.finish();


        }
        return super.onOptionsItemSelected(item);
    }

    public void updateOrg(final Organization organization) {




        OrganizationApi apiService = Util.getClient().create( OrganizationApi.class);

        Call< Organization > call = apiService.updateOrganization(organization.getOrganizationId(),organization);

        call.enqueue(new Callback< Organization >() {
            @Override
            public void onResponse( Call< Organization > call, Response< Organization > response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {


                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201|| statusCode == 204) {

                        BranchEditScreen.this.finish();

                    }else {
                        Toast.makeText( BranchEditScreen.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception ex)
                {


                    ex.printStackTrace();
                }
//                callGetStartEnd();
            }

            @Override
            public void onFailure( Call< Organization > call, Throwable t) {


                // Toast.makeText(BranchEditScreen.this, "Failed due to Bad Internet Connection", Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        try{
            if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
                if (resultCode == RESULT_OK) {
                    Place place = PlaceAutocomplete.getPlace(this, data);
                    //System.out.println(place.getLatLng());
                    location.setText(place.getName()+","+place.getAddress());
                    //location.setText(""+place.getId());
                    placeId= place.getId();

                    mLatitude = place.getLatLng().latitude;
                    mLongitude = place.getLatLng().longitude;

                    lat.setText(df2.format(place.getLatLng().latitude)+"");
                    lng.setText(df2.format(place.getLatLng().longitude)+"");
                    System.out.println("Star Rating = "+place.getRating());
                    if(mMap != null)
                    {
                        mMap.clear();
                        marker = mMap.addMarker(new MarkerOptions()
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                                .position(place.getLatLng()));
                        CameraPosition cameraPosition1 = new CameraPosition.Builder().target(place.getLatLng()).zoom(17).build();
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition1));
                    }
                    //address.setText(place.getAddress());*/
                    Log.i(TAG, "Place: " + place.getName());
                } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                    Status status = PlaceAutocomplete.getStatus(this, data);
                    // TODO: Handle the error.
                    Log.i(TAG, status.getStatusMessage());

                } else if (resultCode == RESULT_CANCELED) {
                    // The user canceled the operation.
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    public void openTimePicker(final TextInputEditText tv){

        final Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog( BranchEditScreen.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                //tv.setText( selectedHour + ":" + selectedMinute);

                try {



                    boolean isPM =(hourOfDay >= 12);

                    String cin = ""+String.format("%02d:%02d %s", (hourOfDay == 12 || hourOfDay == 0) ? 12 : hourOfDay % 12, minute, isPM ? "PM" : "AM");
                    tv.setText( cin);
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        }, hour, minute, false);//Yes 24 hour time

        mTimePicker.show();
    }
}
