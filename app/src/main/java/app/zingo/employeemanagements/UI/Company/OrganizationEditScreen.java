package app.zingo.employeemanagements.UI.Company;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.google.android.gms.maps.MapView;
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
import app.zingo.employeemanagements.Custom.MyTextView;
import app.zingo.employeemanagements.Model.Organization;
import app.zingo.employeemanagements.R;
import app.zingo.employeemanagements.UI.Common.ReportManagementScreen;
import app.zingo.employeemanagements.UI.GetStartedScreen;
import app.zingo.employeemanagements.Utils.TrackGPS;
import app.zingo.employeemanagements.Utils.Util;
import app.zingo.employeemanagements.WebApi.OrganizationApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrganizationEditScreen extends AppCompatActivity {

    TextInputEditText mOrgName,mBuildYear,mWebsite,mCheckIn;
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
            setContentView(R.layout.activity_organization_edit_screen);

            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Edit Organization");

            Bundle bun = getIntent().getExtras();

            if(bun!=null){

                organization = (Organization)bun.getSerializable("Organization");
            }


            mOrgName = (TextInputEditText)findViewById(R.id.name);
            mBuildYear = (TextInputEditText)findViewById(R.id.build);
            mWebsite = (TextInputEditText)findViewById(R.id.website);
            mCheckIn = (TextInputEditText)findViewById(R.id.check_time);
            mAbout = (EditText) findViewById(R.id.about);
            mUpdate = (AppCompatButton) findViewById(R.id.updateCompany);

            mapView = (MapViewScroll) findViewById(R.id.google_map_view);
            location = (TextView)findViewById(R.id.location_et);

            lat = (EditText)findViewById(R.id.lat_et);
            lng = (EditText)findViewById(R.id.lng_et);

            mCheckIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    openTimePicker(mCheckIn);
                }
            });

            trackGPS = new TrackGPS(this);

            mapView.onCreate(savedInstanceState);
            mapView.onResume();

            try {
                MapsInitializer.initialize(OrganizationEditScreen.this);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent =
                                new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY/*MODE_FULLSCREEN*/)
                                        .build(OrganizationEditScreen.this);
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


                    if (ActivityCompat.checkSelfPermission(OrganizationEditScreen.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(OrganizationEditScreen.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling

                        return;
                    }
                    mMap.getUiSettings().setZoomControlsEnabled(true);
                    mMap.setMyLocationEnabled(true);
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    mMap.getProjection().getVisibleRegion().latLngBounds.getCenter();

//                    if(trackGPS.canGetLocation())
//                    {
//                        mLatitude = trackGPS.getLatitude();
//                        mLongitude = trackGPS.getLongitude();
//                    }

                  /*  LatLng latLng = new LatLng(mLatitude,mLongitude);
                    String add = getAddress(latLng);
                    location.setText(add);
                    lat.setText(df2.format(mLatitude)+"");
                    lng.setText(df2.format(mLongitude)+"");*/
                  /*  final CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(17).build();
                    marker = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.
                            defaultMarker(BitmapDescriptorFactory.HUE_RED))
                            .position(latLng));
*/
                    //mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

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


            mBuildYear.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    String currentYear = mBuildYear.getText().toString();

                    if(currentYear==null||currentYear.isEmpty()){

                    }else{
                        int value = Integer.parseInt(currentYear);

                        if(value>year){
                            mBuildYear.setError("Build year is not valid");
                            Toast.makeText(OrganizationEditScreen.this, "Build year is not validate", Toast.LENGTH_SHORT).show();
                        }
                    }



                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });


        }catch(Exception e){
            e.printStackTrace();
        }

    }

    private String getAddress(LatLng latLng) {
        Geocoder geocoder = new Geocoder(OrganizationEditScreen.this, Locale.getDefault());
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
        if(org.getBuiltYear()!=null&&!org.getBuiltYear().isEmpty()){
            mBuildYear.setText(""+org.getBuiltYear());
        }else{
            //mBuildYear.setText("");
        }

        mWebsite.setText(org.getWebsite());
        mAbout.setText(org.getAboutUs());

        String cheIT = org.getPlaceId();

        if(cheIT!=null&&!cheIT.isEmpty()){
            mCheckIn.setText(""+cheIT);
        }
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
        String build = mBuildYear.getText().toString();
        String web = mWebsite.getText().toString();
        String address = location.getText().toString();
        String lati = lat.getText().toString();
        String longi = lng.getText().toString();
        String cheIT = mCheckIn.getText().toString();


        if(orgName.isEmpty()){
            Toast.makeText(OrganizationEditScreen.this, "Field should not be empty", Toast.LENGTH_SHORT).show();

        }else if(about.isEmpty()){
            Toast.makeText(OrganizationEditScreen.this, "Field should not be empty", Toast.LENGTH_SHORT).show();

        }else if(build.isEmpty()){
            Toast.makeText(OrganizationEditScreen.this, "Field should not be empty", Toast.LENGTH_SHORT).show();

        }else if(web.isEmpty()){
            Toast.makeText(OrganizationEditScreen.this, "Field should not be empty", Toast.LENGTH_SHORT).show();

        }else if(address.isEmpty()){
            Toast.makeText(OrganizationEditScreen.this, "Field should not be empty", Toast.LENGTH_SHORT).show();

        }else if(lati.isEmpty()){
            Toast.makeText(OrganizationEditScreen.this, "Field should not be empty", Toast.LENGTH_SHORT).show();
        }else if(longi.isEmpty()){
            Toast.makeText(OrganizationEditScreen.this, "Field should not be empty", Toast.LENGTH_SHORT).show();

        }else{

            Organization orgs = organization;
            orgs.setOrganizationName(orgName);
            orgs.setAboutUs(about);
            orgs.setBuiltYear(build);
            orgs.setWebsite(web);
            orgs.setAddress(address);
            orgs.setLongitude(longi);
            orgs.setLatitude(lati);
            orgs.setState(state);

            if(cheIT!=null&&!cheIT.isEmpty()){
                orgs.setPlaceId(cheIT);
            }
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

                OrganizationEditScreen.this.finish();


        }
        return super.onOptionsItemSelected(item);
    }

    public void updateOrg(final Organization organization) throws Exception{




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



                        OrganizationEditScreen.this.finish();



                    }else {
                        Toast.makeText(OrganizationEditScreen.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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


                Toast.makeText(OrganizationEditScreen.this, "Failed Due to "+t.getMessage(), Toast.LENGTH_SHORT).show();
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
        mTimePicker = new TimePickerDialog(OrganizationEditScreen.this, new TimePickerDialog.OnTimeSetListener() {
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
