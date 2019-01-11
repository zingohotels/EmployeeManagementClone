package app.zingo.employeemanagements.UI.NewEmployeeDesign;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;

import app.zingo.employeemanagements.Model.Employee;
import app.zingo.employeemanagements.Model.LoginDetails;
import app.zingo.employeemanagements.Model.LoginDetailsNotificationManagers;
import app.zingo.employeemanagements.R;
import app.zingo.employeemanagements.Service.LocationSharingServices;
import app.zingo.employeemanagements.UI.Employee.DashBoardEmployee;
import app.zingo.employeemanagements.Utils.Constants;
import app.zingo.employeemanagements.Utils.PreferenceHandler;
import app.zingo.employeemanagements.Utils.ThreadExecuter;
import app.zingo.employeemanagements.Utils.TrackGPS;
import app.zingo.employeemanagements.Utils.Util;
import app.zingo.employeemanagements.WebApi.LoginDetailsAPI;
import app.zingo.employeemanagements.WebApi.LoginNotificationAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmployeeLoginFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener, OnMapReadyCallback {


    static final String TAG = "EmpPunchInPunchOut";

    private TextView latLong;
    View layout;
    LinearLayout mRefreshLocation;
    private View punchIn;
    private TextView punchInText;
    private View punchOut;
    private TextView punchOutText;


    private Context mContext;

    Employee mEmployee;

    Bitmap profileBitmap;


    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;

    private GoogleApiClient mLocationClient;
    Location currentLocation;

    //Location
    TrackGPS gps;
    double latitude,longitude;

    ArrayList<LatLng> MarkerPoints;
    String appType="",planType="",licensesStartDate="",licenseEndDate="";


    public void centreMapOnLocation(Location location, String title) {

        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(userLocation).title(title));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12));

    }

    public void centreMapOnLocationWithLatLng(LatLng location, String title) {

        LatLng userLocation = location;
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(userLocation).title(title));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12));

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            if (ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                Location lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(mLocationClient);
            }


           /* if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);

                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                centreMapOnLocation(lastKnownLocation,"Your Location");
            }*/
        }
    }


    public static EmployeeLoginFragment getInstance() {
        EmployeeLoginFragment employeePunchInOutFragment = new EmployeeLoginFragment();

        return employeePunchInOutFragment;
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        layout = layoutInflater.inflate(R.layout.fragment_employee_login, viewGroup, false);

        mRefreshLocation = (LinearLayout)layout.findViewById(R.id.refreshLocation);
        punchIn = layout.findViewById(R.id.punchIn);
        punchInText = (TextView) layout.findViewById(R.id.punchInText);
        punchOut = layout.findViewById(R.id.punchOut);
        punchOutText = (TextView) layout.findViewById(R.id.punchOutText);


        planType = PreferenceHandler.getInstance(getActivity()).getPlanType();
        if (mLocationClient == null) {
            mLocationClient = new GoogleApiClient.Builder(this.getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }


            if(!PreferenceHandler.getInstance(getActivity()).getLoginTime().isEmpty()){
                punchInText.setText(""+PreferenceHandler.getInstance(getActivity()).getLoginTime());
            }

        this.latLong = (TextView) this.layout.findViewById(R.id.latLong);
        ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);

        mLocationClient.connect();

        mRefreshLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(locationCheck()){
                    gps = new TrackGPS(getActivity());
                    if(gps.canGetLocation())
                    {

                        latitude = gps.getLatitude();
                        longitude = gps.getLongitude();

                        LatLng master = new LatLng(latitude,longitude);
                        String address = getAddress(master);

                        latLong.setText(address);
                        centreMapOnLocationWithLatLng(master,""+PreferenceHandler.getInstance(getActivity()).getUserFullName());



                    }
                    else
                    {

                    }
                }

            }
        });

        punchIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String loginStatus = PreferenceHandler.getInstance(getActivity()).getLoginStatus();
                String meetingStatus = PreferenceHandler.getInstance(getActivity()).getMeetingLoginStatus();

                if(loginStatus!=null&&!loginStatus.isEmpty()){

                   if(loginStatus.equalsIgnoreCase("Logout")){

                        masterloginalert("Logout");

                        if(planType.contains("Advance")){
                            Intent serviceIntent = new Intent(getActivity(),LocationSharingServices.class);
                            getActivity().startService(serviceIntent);
                        }

                    }

                }else{
                    masterloginalert("Logout");
                    if(planType.contains("Advance")){
                        Intent serviceIntent = new Intent(getActivity(),LocationSharingServices.class);
                        getActivity().startService(serviceIntent);
                    }
                }


            }
        });

        punchOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String loginStatus = PreferenceHandler.getInstance(getActivity()).getLoginStatus();
                String meetingStatus = PreferenceHandler.getInstance(getActivity()).getMeetingLoginStatus();

                if(loginStatus!=null&&!loginStatus.isEmpty()){

                    if(loginStatus.equalsIgnoreCase("Login")){

                        if(meetingStatus!=null&&meetingStatus.equalsIgnoreCase("Login")){

                            Toast.makeText(getActivity(), "You are in some meeting .So Please checkout", Toast.LENGTH_SHORT).show();

                        }else{

                            getLoginDetails(PreferenceHandler.getInstance(getActivity()).getLoginId());
                        }


                    }

                }


            }
        });

        //setupLocalBucket();

        return this.layout;
    }

    public void getLoginDetails(final int id){

        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {

                final LoginDetailsAPI subCategoryAPI = Util.getClient().create(LoginDetailsAPI.class);
                Call<LoginDetails> getProf = subCategoryAPI.getLoginById(id);
                //Call<ArrayList<Blogs>> getBlog = blogApi.getBlogs();

                getProf.enqueue(new Callback<LoginDetails>() {

                    @Override
                    public void onResponse(Call<LoginDetails> call, Response<LoginDetails> response) {

                        if (response.code() == 200||response.code() == 201||response.code() == 204)
                        {
                            System.out.println("Inside api");

                            final LoginDetails dto = response.body();

                            if(dto!=null){

                                try {

                                    String message = "Login";
                                    String option = "Check-In";



                                    message = "Do you want to Log-Out?";
                                    option = "Log-Out";



                                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                    builder.setTitle(message);



                                    builder.setPositiveButton(option, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {



                                            if(locationCheck()){
                                                gps = new TrackGPS(getActivity());
                                                if(gps.canGetLocation())
                                                {

                                                    latitude = gps.getLatitude();
                                                    longitude = gps.getLongitude();
                                                    Intent myService = new Intent(getActivity(), LocationSharingServices.class);
                                                    getActivity().stopService(myService);


                                                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                                                    SimpleDateFormat sdt = new SimpleDateFormat("MMM dd,yyyy hh:mm a");

                                                    LatLng master = new LatLng(latitude,longitude);
                                                    String address = getAddress(master);

                                                    LoginDetails loginDetails = dto;
                                                    loginDetails.setEmployeeId(PreferenceHandler.getInstance(getActivity()).getUserId());
                                                    loginDetails.setLatitude(""+latitude);
                                                    loginDetails.setLongitude(""+longitude);
                                                    loginDetails.setLocation(""+address);
                                                    loginDetails.setLogOutTime(""+sdt.format(new Date()));
                                                    loginDetails.setLoginDate(""+sdf.format(new Date()));

                                                    try {
                                                        LoginDetailsNotificationManagers md = new LoginDetailsNotificationManagers();
                                                        md.setTitle("Login Details from "+PreferenceHandler.getInstance(getActivity()).getUserFullName());
                                                        md.setMessage("Log out at  "+""+sdt.format(new Date()));
                                                        md.setLocation(address);
                                                        md.setLongitude(""+longitude);
                                                        md.setLatitude(""+latitude);
                                                        md.setLoginDate(""+sdt.format(new Date()));
                                                        md.setStatus("Log out");
                                                        md.setEmployeeId(PreferenceHandler.getInstance(getActivity()).getUserId());
                                                        md.setManagerId(PreferenceHandler.getInstance(getActivity()).getManagerId());
                                                        md.setLoginDetailsId(dto.getLoginDetailsId());

                                                        updateLogin(loginDetails,builder.create(),md);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }


                                                }
                                                else
                                                {

                                                }
                                            }





                                        }
                                    });

                                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    });

                                    final AlertDialog dialog = builder.create();
                                    dialog.show();


                                }
                                catch (Exception ex)
                                {
                                    ex.printStackTrace();
                                }

                            }




                        }else{


                            //meet
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginDetails> call, Throwable t) {

                    }
                });

            }

        });
    }

    public void updateLogin(final LoginDetails loginDetails,final AlertDialog dialogs,final LoginDetailsNotificationManagers md) throws Exception{



        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Saving Details..");
        dialog.setCancelable(false);
        dialog.show();

        LoginDetailsAPI apiService = Util.getClient().create(LoginDetailsAPI.class);

        Call<LoginDetails> call = apiService.updateLoginById(loginDetails.getLoginDetailsId(),loginDetails);

        call.enqueue(new Callback<LoginDetails>() {
            @Override
            public void onResponse(Call<LoginDetails> call, Response<LoginDetails> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {
                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201||response.code()==204) {


                        dialogs.dismiss();
                        saveLoginNotification(md);

                        Toast.makeText(getActivity(), "You logged out", Toast.LENGTH_SHORT).show();

                        PreferenceHandler.getInstance(getActivity()).setLoginId(0);



                        punchInText.setText("Check in");
                        PreferenceHandler.getInstance(getActivity()).setLoginStatus("Logout");

                        punchIn.setEnabled(true);
                        punchOut.setEnabled(false);

                        String date = loginDetails.getLoginDate();

                        if(date!=null&&!date.isEmpty()){

                            if(date.contains("T")){

                                String logins[] = date.split("T");
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                SimpleDateFormat sdfs = new SimpleDateFormat("MMM dd,yyyy");

                                Date dt = sdf.parse(logins[0]);
                                punchOutText.setText(""+loginDetails.getLogOutTime());
                            }
                        }else{
                            punchOutText.setText(""+loginDetails.getLogOutTime());
                        }







                    }else {
                        Toast.makeText(getActivity(), "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
            public void onFailure(Call<LoginDetails> call, Throwable t) {

                if(dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
                Toast.makeText(getActivity(), "Failed Due to "+t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());
            }
        });



    }

    public void masterloginalert(final String status){

        try {

            String message = "Do you want to Check-In?";
            String option = "Check-In";



            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(message);




            builder.setPositiveButton(option, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {



                    if(locationCheck()){
                        gps = new TrackGPS(getActivity());
                        if(gps.canGetLocation())
                        {
                            System.out.println("Long and lat Rev"+gps.getLatitude()+" = "+gps.getLongitude());
                            latitude = gps.getLatitude();
                            longitude = gps.getLongitude();

                            Location locationA = new Location("point A");

                            locationA.setLatitude(Double.parseDouble(PreferenceHandler.getInstance(getActivity()).getOrganizationLati()));
                            locationA.setLongitude(Double.parseDouble(PreferenceHandler.getInstance(getActivity()).getOrganizationLongi()));

                            Location locationB = new Location("point B");

                            locationB.setLatitude(latitude);
                            locationB.setLongitude(longitude);

                            float distance = locationA.distanceTo(locationB);

                            if(distance>=0&&distance<=30){
                                Toast.makeText(getActivity(), "distance "+distance, Toast.LENGTH_SHORT).show();
                                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                                SimpleDateFormat sdt = new SimpleDateFormat("MMM dd,yyyy hh:mm a");

                                LatLng master = new LatLng(latitude,longitude);
                                String address = getAddress(master);

                                LoginDetails loginDetails = new LoginDetails();
                                loginDetails.setEmployeeId(PreferenceHandler.getInstance(getActivity()).getUserId());
                                loginDetails.setLatitude(""+latitude);
                                loginDetails.setLongitude(""+longitude);
                                loginDetails.setLocation(""+address);
                                loginDetails.setLoginTime(""+sdt.format(new Date()));
                                loginDetails.setLoginDate(""+sdf.format(new Date()));
                                loginDetails.setLogOutTime("");
                                try {

                                    LoginDetailsNotificationManagers md = new LoginDetailsNotificationManagers();
                                    md.setTitle("Login Details from "+PreferenceHandler.getInstance(getActivity()).getUserFullName());
                                    md.setMessage("Log in at  "+""+sdt.format(new Date()));
                                    md.setLocation(address);
                                    md.setLongitude(""+longitude);
                                    md.setLatitude(""+latitude);
                                    md.setLoginDate(""+sdt.format(new Date()));
                                    md.setStatus("In meeting");
                                    md.setEmployeeId(PreferenceHandler.getInstance(getActivity()).getUserId());
                                    md.setManagerId(PreferenceHandler.getInstance(getActivity()).getManagerId());

                                    addLogin(loginDetails,builder.create(),md);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }else{
                                Toast.makeText(getActivity(), "You are far away "+distance+" meter from your office", Toast.LENGTH_SHORT).show();
                            }



                        }
                        else
                        {

                        }
                    }





                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });

            final AlertDialog dialog = builder.create();
            dialog.show();


        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void addLogin(final LoginDetails loginDetails,final AlertDialog dialogs,final LoginDetailsNotificationManagers md) throws Exception{



        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Saving Details..");
        dialog.setCancelable(false);
        dialog.show();

        LoginDetailsAPI apiService = Util.getClient().create(LoginDetailsAPI.class);

        Call<LoginDetails> call = apiService.addLogin(loginDetails);

        call.enqueue(new Callback<LoginDetails>() {
            @Override
            public void onResponse(Call<LoginDetails> call, Response<LoginDetails> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {
                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201) {

                        LoginDetails s = response.body();

                        if(s!=null){

                            punchIn.setEnabled(false);

                            dialogs.dismiss();
                            md.setLoginDetailsId(s.getLoginDetailsId());
                            saveLoginNotification(md);

                            Toast.makeText(getActivity(), "You Logged in", Toast.LENGTH_SHORT).show();

                            PreferenceHandler.getInstance(getActivity()).setLoginId(s.getLoginDetailsId());

                            String date = s.getLoginDate();

                            if(date!=null&&!date.isEmpty()){

                                if(date.contains("T")){

                                    String logins[] = date.split("T");
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                    SimpleDateFormat sdfs = new SimpleDateFormat("MMM dd,yyyy");

                                    Date dt = sdf.parse(logins[0]);
                                    punchInText.setText(""+s.getLoginTime());
                                }
                            }else{
                                punchInText.setText(""+s.getLoginTime());
                            }


                            PreferenceHandler.getInstance(getActivity()).setLoginStatus("Login");
                            PreferenceHandler.getInstance(getActivity()).setLoginTime(""+s.getLoginTime());


                        }




                    }else {
                        Toast.makeText(getActivity(), "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
            public void onFailure(Call<LoginDetails> call, Throwable t) {

                if(dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
                Toast.makeText(getActivity(), "Failed Due to "+t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());
            }
        });



    }

    public void saveLoginNotification(final LoginDetailsNotificationManagers md) throws Exception{



        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Saving Details..");
        dialog.setCancelable(false);
        dialog.show();

        LoginNotificationAPI apiService = Util.getClient().create(LoginNotificationAPI.class);

        Call<LoginDetailsNotificationManagers> call = apiService.saveLoginNotification(md);

        call.enqueue(new Callback<LoginDetailsNotificationManagers>() {
            @Override
            public void onResponse(Call<LoginDetailsNotificationManagers> call, Response<LoginDetailsNotificationManagers> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {
                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201) {

                        LoginDetailsNotificationManagers s = response.body();

                        if(s!=null){


                            s.setEmployeeId(md.getManagerId());
                            s.setManagerId(md.getEmployeeId());
                            s.setSenderId(Constants.SENDER_ID);
                            s.setServerId(Constants.SERVER_ID);
                            sendLoginNotification(s);



                        }




                    }else {
                        Toast.makeText(getActivity(), "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
            public void onFailure(Call<LoginDetailsNotificationManagers> call, Throwable t) {

                if(dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
                Toast.makeText(getActivity(), "Failed Due to "+t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());
            }
        });



    }

    public void sendLoginNotification(final LoginDetailsNotificationManagers md) throws Exception{



        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Sending Details..");
        dialog.setCancelable(false);
        dialog.show();

        LoginNotificationAPI apiService = Util.getClient().create(LoginNotificationAPI.class);

        Call<ArrayList<String>> call = apiService.sendLoginNotification(md);

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





                    }else {
                        Toast.makeText(getActivity(), "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getActivity(), "Failed Due to "+t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());
            }
        });



    }



    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
        this.mMap.setMaxZoomPreference(24.0f);

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i("salam", " Connected");

        if (ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            final boolean status = false;
            LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            boolean gps_enabled = false;
            boolean network_enabled = false;

            try {
                gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch (Exception ex) {
            }

            try {
                network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch (Exception ex) {
            }
            if (!gps_enabled && !network_enabled) {
                // notify user
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setMessage("Location is not enable");
                dialog.setPositiveButton("Open Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        // TODO Auto-generated method stub
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        getActivity().startActivity(myIntent);
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

            } else {
                currentLocation = LocationServices.FusedLocationApi.getLastLocation(mLocationClient);

                LatLng master = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
                String address = getAddress(master);

                latLong.setText(address);
                centreMapOnLocation(currentLocation, "" + PreferenceHandler.getInstance(getActivity()).getUserFullName());
            }


        }


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

        if (ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            final boolean status = false;
            LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            boolean gps_enabled = false;
            boolean network_enabled = false;

            try {
                gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch (Exception ex) {
            }

            try {
                network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch (Exception ex) {
            }
            if (!gps_enabled && !network_enabled) {
                // notify user
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setMessage("Location is not enable");
                dialog.setPositiveButton("Open Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        // TODO Auto-generated method stub
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        getActivity().startActivity(myIntent);
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

            } else {
                currentLocation = LocationServices.FusedLocationApi.getLastLocation(mLocationClient);

                centreMapOnLocation(currentLocation, "" + PreferenceHandler.getInstance(getActivity()).getUserFullName());
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

  /*  @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Intent intent = getActivity().getIntent();
        if (intent.getIntExtra("Place Number",0) == 0 ){

            // Zoom into users location
            locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    centreMapOnLocation(location,"Your Location");
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            };

            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                centreMapOnLocation(lastKnownLocation,"Your Location");
            } else {

                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }
        }


    }


*/




    /*public void onStop() {
        if (!(this.client == null || this.locationCallback == null)) {
            this.client.removeLocationUpdates(this.locationCallback);
        }
        super.onStop();
    }*/


    public void onResume() {
        super.onResume();
    }

    public void onDetach() {
        super.onDetach();
    }

    public void setUserVisibleHint(boolean z) {
        super.setUserVisibleHint(z);
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }
    public boolean locationCheck(){

        final boolean status = false;
        LocationManager lm = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
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
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setMessage("Location is not enable");
            dialog.setPositiveButton("Open Settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                   getActivity().startActivity(myIntent);
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
    private String getAddress(LatLng latLng) {
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
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

                return result;
            }
            return result;
        } catch (IOException e) {
            Log.e("MapLocation", "Unable connect to Geocoder", e);
            return result;
        }

    }


}

