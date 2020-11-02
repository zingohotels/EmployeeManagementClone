package app.zingo.employeemanagements.ui.Common;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import app.zingo.employeemanagements.Custom.MyEditText;
import app.zingo.employeemanagements.Custom.MyTextView;
import app.zingo.employeemanagements.model.Organization;
import app.zingo.employeemanagements.model.OrganizationPayments;
import app.zingo.employeemanagements.ui.landing.PhoneVerificationScreen;
import app.zingo.employeemanagements.utils.PreferenceHandler;
import app.zingo.employeemanagements.utils.TrackGPS;
import app.zingo.employeemanagements.utils.Util;
import app.zingo.employeemanagements.WebApi.OrganizationApi;
import app.zingo.employeemanagements.WebApi.OrganizationPaymentAPI;
import app.zingo.employeemanagements.base.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.text.TextUtils.isEmpty;

public class CreateBranchesScreen extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener{


    MyTextView myLocation;

    MyEditText mOrganizationName;//mEmailExt
    MyTextView mCity,mState;

    MyEditText mAbout,mAddress;
    MyTextView mCreate;


    public static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    String country,placeId;
    Organization organization;

    TrackGPS gps;
    double latitude;
    double longitude;

    int year = 0;


    //PaymentGateway
    private static final String TAG = CreateBranchesScreen.class.getSimpleName();

    //Google Api Locatin
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mLocationClient;
    Location currentLocation;

    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 10000;  /* 15 secs */
    private long FASTEST_INTERVAL = 5000; /* 5 secs */

    private static final int REQUEST_PERMISSIONS = 100;
    boolean boolean_permission;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
            setContentView(R.layout.activity_create_branches_screen);

            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Create Branch");

            gps = new TrackGPS ( CreateBranchesScreen.this);

            fn_permission();
            if (boolean_permission){

                if (mLocationClient == null) {



                    mLocationClient = new GoogleApiClient.Builder( CreateBranchesScreen.this)
                            .addConnectionCallbacks(this)
                            .addOnConnectionFailedListener(this)
                            .addApi(LocationServices.API)
                            .build();
                }

            }


            String currentYear = new SimpleDateFormat("yyyyy").format(new Date());

            year = Integer.parseInt(currentYear);

            mOrganizationName = findViewById(R.id.name);
            //  mEmailExt = (MyEditText)findViewById(R.id.org_email);
            mCity = findViewById(R.id.city);
            mState = findViewById(R.id.state);

            mAbout = findViewById(R.id.about);
            mAddress = findViewById(R.id.address);
            myLocation = findViewById(R.id.my_location);

            mCreate = findViewById(R.id.createCompany);

            // onAddField();

            mCity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent =
                                new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY/*MODE_FULLSCREEN*/)
                                        .build( CreateBranchesScreen.this);
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

            mCreate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try{
                        validate();
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            });

            myLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (locationCheck()) {

                        if (currentLocation != null) {

                            latitude = currentLocation.getLatitude();
                            longitude = currentLocation.getLongitude();
                            getAddress(latitude,longitude);

                        }

                    }


                }
            });





        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public void validate() {

        String company =mOrganizationName.getText().toString();
        //  String mail =mEmailExt.getText().toString();
        String about = mAbout.getText().toString();
        String address = mAddress.getText().toString();
        String city = mCity.getText().toString();
        String state = mState.getText().toString();

        //boolean value = checkcondition();

        if(company.isEmpty()){

            Toast.makeText( CreateBranchesScreen.this, "Organization Name required", Toast.LENGTH_SHORT).show();

        }else if(about.isEmpty()){

            Toast.makeText( CreateBranchesScreen.this, "About Organization Name required", Toast.LENGTH_SHORT).show();

        }/*else if(mail.isEmpty()){

            Toast.makeText(CreateBranchesScreen.this, "Organization email extension required", Toast.LENGTH_SHORT).show();

        }*/else if(address.isEmpty()){

            Toast.makeText( CreateBranchesScreen.this, "Address required", Toast.LENGTH_SHORT).show();

        }else if(city.isEmpty()){

            Toast.makeText( CreateBranchesScreen.this, "City required", Toast.LENGTH_SHORT).show();

        }else if(state.isEmpty()){

            Toast.makeText( CreateBranchesScreen.this, "State required", Toast.LENGTH_SHORT).show();

        }else{


            LatLng latLng = convertAddressToLatLang(address+"," +city+","+state+","+country);

            if(latLng!=null){

                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                SimpleDateFormat sdfs = new SimpleDateFormat("MM/dd/yyyy");
                organization = new Organization ();
                organization.setOrganizationName(company);
                organization.setAboutUs(about);
                organization.setAddress(address);
                organization.setCity(city);
                organization.setState(state);
                organization.setLatitude(String.valueOf(latLng.latitude));
                organization.setLongitude(String.valueOf(latLng.longitude));
                organization.setAppType(PreferenceHandler.getInstance( CreateBranchesScreen.this).getAppType());
                organization.setPlanType(PreferenceHandler.getInstance( CreateBranchesScreen.this).getPlanType());
                organization.setSignupDate(sdf.format(new Date()));
                organization.setLicenseStartDate(PreferenceHandler.getInstance( CreateBranchesScreen.this).getLicenseStartDate());
                organization.setLicenseEndDate(PreferenceHandler.getInstance( CreateBranchesScreen.this).getLicenseEndDate());
                organization.setPlanId(PreferenceHandler.getInstance( CreateBranchesScreen.this).getPlanId());
                organization.setHeadOrganizationId(PreferenceHandler.getInstance( CreateBranchesScreen.this).getCompanyId());
                addOrganization(organization);




            }else{

                Toast.makeText(this, "Something went wrong.Please try again Later", Toast.LENGTH_SHORT).show();
            }



        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        try{
            if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
                if (resultCode == RESULT_OK) {
                    Place place = PlaceAutocomplete.getPlace(this, data);
                    //System.out.println(place.getLatLng());


                    LatLng latLang = place.getLatLng();
                    double lat  = latLang.latitude;
                    double longi  = latLang.longitude;
                    try {
                        Geocoder geocoder = new Geocoder( CreateBranchesScreen.this);
                        List<Address> addresses = geocoder.getFromLocation(lat,longi,1);
                        System.out.println("addresses = "+addresses+"Place id"+place.getId());
                        mCity.setText(place.getName()+"");

                        mState.setText(addresses.get(0).getAdminArea());

                        country = ""+addresses.get(0).getCountryName();


                    }
                    catch (IOException ex)
                    {
                        ex.printStackTrace();
                    }


                } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                    Status status = PlaceAutocomplete.getStatus(this, data);
                    // TODO: Handle the error.
                    Log.i("CreateCity", status.getStatusMessage());

                } else if (resultCode == RESULT_CANCELED) {
                    // The user canceled the operation.
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    boolean_permission = true;

                } else {
                    Toast.makeText(getApplicationContext(), "Please allow the permission", Toast.LENGTH_LONG).show();

                }
            }
        }
    }

    public LatLng convertAddressToLatLang(String strAddress)
    {
        Geocoder coder = new Geocoder(this);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
                //System.out.println("null");
            }

            Address location = address.get(0);
            System.out.println("LatLang = "+location.getLatitude()+","+ location.getLongitude()+" ");
            p1 = new LatLng(location.getLatitude(), location.getLongitude() );





        } catch (Exception ex) {

            ex.printStackTrace();
        }

        return p1;
    }



    public void addOrganization(final Organization organization) {


        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.show();

        OrganizationApi apiService = Util.getClient().create( OrganizationApi.class);

        Call< Organization > call = apiService.addOrganization(organization);

        call.enqueue(new Callback< Organization >() {
            @Override
            public void onResponse( Call< Organization > call, Response< Organization > response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {
                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201) {

                        Organization s = response.body();

                        if(s!=null){

                            Toast.makeText( CreateBranchesScreen.this, "Branch created successfully", Toast.LENGTH_SHORT).show();
                            CreateBranchesScreen.this.finish();

                        }




                    }else {
                        Toast.makeText( CreateBranchesScreen.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
            public void onFailure( Call< Organization > call, Throwable t) {

                if(dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
                Toast.makeText( CreateBranchesScreen.this , "Failed due to Bad Internet Connection" , Toast.LENGTH_SHORT ).show( );
                Log.e("TAG", t.toString());
            }
        });



    }


    public void addOrgaPay( final Organization organization, final OrganizationPayments organizationPayments) {


        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Saving Details..");
        dialog.setCancelable(false);
        dialog.show();

        OrganizationPaymentAPI apiService = Util.getClient().create( OrganizationPaymentAPI.class);

        Call< OrganizationPayments > call = apiService.addOrganizationPayments(organizationPayments);

        call.enqueue(new Callback< OrganizationPayments >() {
            @Override
            public void onResponse( Call< OrganizationPayments > call, Response< OrganizationPayments > response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {
                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201) {

                        OrganizationPayments s = response.body();

                        if(s!=null){

                            Toast.makeText( CreateBranchesScreen.this, "Your Organization Creted Successfully ", Toast.LENGTH_SHORT).show();





                            Bundle bundle = new Bundle();
                            bundle.putSerializable("Company",organization);
                            Intent profile = new Intent( CreateBranchesScreen.this, PhoneVerificationScreen.class);
                            profile.putExtras(bundle);
                            profile.putExtra("Screen","Organization");
                            startActivity(profile);
                            CreateBranchesScreen.this.finish();




                        }




                    }else {
                        Toast.makeText( CreateBranchesScreen.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
            public void onFailure( Call< OrganizationPayments > call, Throwable t) {

                if(dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
                Toast.makeText( CreateBranchesScreen.this , "Failed due to Bad Internet Connection" , Toast.LENGTH_SHORT ).show( );
                Log.e("TAG", t.toString());
            }
        });



    }



    @Override
    public void onBackPressed() {


        CreateBranchesScreen.this.finish();

    }

    public void getAddress(final double latitude,final double longitude)
    {

        try
        {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder( CreateBranchesScreen.this, Locale.ENGLISH);


            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

            String currentLocation;

            if(!isEmpty(address))
            {
                currentLocation=address;
                mAddress.setText(currentLocation);

            }
            else
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();


        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (mLocationClient != null) {
            mLocationClient.connect();
        }
    }

    public boolean locationCheck(){

        final boolean status = false;
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {
            // notify user
            android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder( CreateBranchesScreen.this);
            dialog.setMessage("Location is not enable");
            dialog.setPositiveButton("Open Settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                    //get gps
                }
            });
            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub


                }
            });
            dialog.show();
            return false;
        }else{
            return true;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i("salam", " Connected");

        if ( ActivityCompat.checkSelfPermission( CreateBranchesScreen.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission( CreateBranchesScreen.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        currentLocation = LocationServices.FusedLocationApi.getLastLocation(mLocationClient);


        if (currentLocation != null) {
            //  latLong.setText("Latitude : " + currentLocation.getLatitude() + " , Longitude : " + currentLocation.getLongitude());

            latitude = currentLocation.getLatitude();
            longitude = currentLocation.getLongitude();


        }
        startLocationUpdates();



    }


    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void onLocationChanged(Location location) {


        if(location!=null){

            latitude = location.getLatitude();
            longitude = location.getLongitude();



        }



    }



    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable( CreateBranchesScreen.this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog( CreateBranchesScreen.this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else
                Toast.makeText( CreateBranchesScreen.this, "Google Play Services not install", Toast.LENGTH_SHORT).show();

            return false;
        }
        return true;
    }
    protected void startLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        if ( ActivityCompat.checkSelfPermission( CreateBranchesScreen.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission( CreateBranchesScreen.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText( CreateBranchesScreen.this, "Enable Permissions", Toast.LENGTH_LONG).show();
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mLocationClient, mLocationRequest, this);


    }

    private void fn_permission() {
        if (( ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {

            if (( ActivityCompat.shouldShowRequestPermissionRationale( CreateBranchesScreen.this, android.Manifest.permission.ACCESS_FINE_LOCATION))) {


            } else {
                ActivityCompat.requestPermissions( CreateBranchesScreen.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION

                        },
                        REQUEST_PERMISSIONS);

            }
        } else {
            boolean_permission = true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id)
        {
            case android.R.id.home:

                CreateBranchesScreen.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
