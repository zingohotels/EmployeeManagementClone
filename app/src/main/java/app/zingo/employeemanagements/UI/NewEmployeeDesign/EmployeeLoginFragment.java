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
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import java.text.ParseException;
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
import app.zingo.employeemanagements.Model.MeetingDetailsNotificationManagers;
import app.zingo.employeemanagements.Model.Meetings;
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
import app.zingo.employeemanagements.WebApi.MeetingNotificationAPI;
import app.zingo.employeemanagements.WebApi.MeetingsAPI;
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

    private View punchInMeeting;
    private TextView punchInTextMeeting;
    private View punchOutMeeting;
    private TextView punchOutTextMeeting;


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

        punchInMeeting = layout.findViewById(R.id.punchInMeeting);
        punchInTextMeeting = (TextView) layout.findViewById(R.id.punchInTextMeeting);
        punchOutMeeting = layout.findViewById(R.id.punchOutMeeting);
        punchOutTextMeeting = (TextView) layout.findViewById(R.id.punchOutTextMeeting);


        planType = PreferenceHandler.getInstance(getActivity()).getPlanType();
        getLoginDetails();
        getMeetingDetails();
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

                        if(planType.contains("Advance")||appType.equalsIgnoreCase("Trial")){
                            Intent serviceIntent = new Intent(getActivity(),LocationSharingServices.class);
                            getActivity().startService(serviceIntent);
                        }

                    }

                }else{
                    masterloginalert("Logout");
                    if(planType.contains("Advance")||appType.equalsIgnoreCase("Trial")){
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

        punchInMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String loginStatus = PreferenceHandler.getInstance(getActivity()).getMeetingLoginStatus();
                String masterloginStatus = PreferenceHandler.getInstance(getActivity()).getLoginStatus();


                if(masterloginStatus.equals("Login")){
                    if(loginStatus!=null&&!loginStatus.isEmpty()){

                        if(loginStatus.equalsIgnoreCase("Login")){
                            //meetingloginalert("Login");
                           // getMeetings(PreferenceHandler.getInstance(getActivity()).getMeetingId());

                        }else if(loginStatus.equalsIgnoreCase("Logout")){

                            meetingloginalert("Logout");
                        }

                    }else{
                        meetingloginalert("Logout");
                    }
                }else{
                    Toast.makeText(getActivity(), "First Check-in Master", Toast.LENGTH_SHORT).show();
                }

            }
        });

        punchOutMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                String loginStatus = PreferenceHandler.getInstance(getActivity()).getMeetingLoginStatus();
                String masterloginStatus = PreferenceHandler.getInstance(getActivity()).getLoginStatus();


                if(masterloginStatus.equals("Login")){
                    if(loginStatus!=null&&!loginStatus.isEmpty()){

                        if(loginStatus.equalsIgnoreCase("Login")){
                            //meetingloginalert("Login");
                             getMeetings(PreferenceHandler.getInstance(getActivity()).getMeetingId());

                        }else if(loginStatus.equalsIgnoreCase("Logout")){

                            meetingloginalert("Logout");
                        }

                    }else{
                        meetingloginalert("Logout");
                    }
                }else{
                    Toast.makeText(getActivity(), "First Check-in Master", Toast.LENGTH_SHORT).show();
                }

            }
        });

        //setupLocalBucket();

        return this.layout;
    }

    private void getLoginDetails(){




        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {
                LoginDetailsAPI apiService = Util.getClient().create(LoginDetailsAPI.class);
                Call<ArrayList<LoginDetails>> call = apiService.getLoginByEmployeeId(PreferenceHandler.getInstance(getActivity()).getUserId());

                call.enqueue(new Callback<ArrayList<LoginDetails>>() {
                    @Override
                    public void onResponse(Call<ArrayList<LoginDetails>> call, Response<ArrayList<LoginDetails>> response) {
                        int statusCode = response.code();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {



                            ArrayList<LoginDetails> list = response.body();


                            if (list !=null && list.size()!=0) {

                                LoginDetails loginDetails = list.get(list.size()-1);

                                if(loginDetails!=null){

                                    String logout = loginDetails.getLogOutTime();
                                    String login = loginDetails.getLoginTime();
                                    String dates = loginDetails.getLoginDate();
                                    String date= null;



                                    if(dates!=null&&!dates.isEmpty()){

                                        if(dates.contains("T")){

                                            String logins[] = dates.split("T");
                                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                            SimpleDateFormat sdfs = new SimpleDateFormat("MMM dd,yyyy");

                                            Date dt = null;
                                            try {
                                                dt = sdf.parse(logins[0]);
                                                date = sdfs.format(dt);
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    }

                                    if(logout!=null&&!logout.isEmpty()&&(login!=null&&!login.isEmpty())){


                                        punchInText.setText("Check-In");
                                        PreferenceHandler.getInstance(getActivity()).setLoginStatus("Logout");

                                    }else if(login!=null&&!login.isEmpty()&&(logout==null||logout.isEmpty())){

                                        if(date!=null&&!date.isEmpty()){

                                            punchInText.setText(""+login);

                                        }else{

                                            punchInText.setText(""+login);
                                        }
                                        punchOutText.setText("Check-out");
                                        PreferenceHandler.getInstance(getActivity()).setLoginStatus("Login");
                                        PreferenceHandler.getInstance(getActivity()).setLoginId(loginDetails.getLoginDetailsId());
                                    }

                                }




                                //}

                            }else{

                            }

                        }else {



                            Toast.makeText(getActivity(), "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<LoginDetails>> call, Throwable t) {
                        // Log error here since request failed

                        Log.e("TAG", t.toString());
                    }
                });
            }


        });
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

                            if(PreferenceHandler.getInstance(getActivity()).isLocationOn()){
                                distance = 0;
                            }

                            if(distance>=0&&distance<=30){
                               // Toast.makeText(getActivity(), "distance "+distance, Toast.LENGTH_SHORT).show();
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
                            punchOut.setEnabled(true);

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

    private void getMeetingDetails(){




        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {
                MeetingsAPI apiService = Util.getClient().create(MeetingsAPI.class);
                Call<ArrayList<Meetings>> call = apiService.getMeetingsByEmployeeId(PreferenceHandler.getInstance(getActivity()).getUserId());

                call.enqueue(new Callback<ArrayList<Meetings>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Meetings>> call, Response<ArrayList<Meetings>> response) {
                        int statusCode = response.code();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {



                            ArrayList<Meetings> list = response.body();


                            if (list !=null && list.size()!=0) {




                                Meetings loginDetails = list.get(list.size()-1);

                                if(loginDetails!=null){
                                    PreferenceHandler.getInstance(getActivity()).setMeetingId(loginDetails.getMeetingsId());

                                    String logout = loginDetails.getEndTime();
                                    String login = loginDetails.getStartTime();


                                    if(logout!=null&&!logout.isEmpty()&&(login!=null&&!login.isEmpty())){



                                        punchInTextMeeting.setText("Meeting-in");

                                        punchOutMeeting.setEnabled(false);
                                        punchInMeeting.setEnabled(true);
                                        PreferenceHandler.getInstance(getActivity()).setMeetingLoginStatus("Logout");

                                    }else if(login!=null&&!login.isEmpty()&&(logout==null||logout.isEmpty())){


                                        punchOutTextMeeting.setText("Meeting-out");
                                        punchInTextMeeting.setText(""+login);
                                        punchOutMeeting.setEnabled(true);
                                        punchInMeeting.setEnabled(false);
                                        PreferenceHandler.getInstance(getActivity()).setMeetingLoginStatus("Login");
                                    }

                                }




                                //}

                            }else{

                            }

                        }else {


                            Toast.makeText(getActivity(), "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Meetings>> call, Throwable t) {
                        // Log error here since request failed

                        Log.e("TAG", t.toString());
                    }
                });
            }


        });
    }


    public void meetingloginalert(final String status){

        try{

            if(locationCheck()){
                String message = "Login";
                String option = "Meeting-In";

                if(status.equalsIgnoreCase("Login")){

                    message = "Do you want to Check-Out?";
                    option = "Meeting-Out";

                }else if(status.equalsIgnoreCase("Logout")){

                    message = "Do you want to Check-In?";
                    option = "Check-In";
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View views = inflater.inflate(R.layout.custom_alert_box_meeting, null);

                builder.setView(views);
                final Button mSave = (Button) views.findViewById(R.id.save);
                mSave.setText(option);
                final EditText mRemarks = (EditText) views.findViewById(R.id.meeting_remarks);
                final TextInputEditText mClient = (TextInputEditText) views.findViewById(R.id.client_name);
                final TextInputEditText mContact = (TextInputEditText) views.findViewById(R.id.client_contact);
                final TextInputEditText mPurpose = (TextInputEditText) views.findViewById(R.id.purpose_meeting);


                final AlertDialog dialog = builder.create();
                dialog.show();
                dialog.setCanceledOnTouchOutside(false);

                mSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String client = mClient.getText().toString();
                        String purpose = mPurpose.getText().toString();
                        String remark = mRemarks.getText().toString();
                        String contact = mContact.getText().toString();

                        if(client==null||client.isEmpty()){

                            Toast.makeText(getActivity(), "Please mention client/hotel name", Toast.LENGTH_SHORT).show();

                        }else if(purpose==null||purpose.isEmpty()){

                            Toast.makeText(getActivity(), "Please mention purpose of meeting", Toast.LENGTH_SHORT).show();

                        }else if(remark==null||remark.isEmpty()){

                            Toast.makeText(getActivity(), "Please mention remarks about meeting", Toast.LENGTH_SHORT).show();

                        }else{

                            gps = new TrackGPS(getActivity());
                            if(gps.canGetLocation())
                            {
                                System.out.println("Long and lat Rev"+gps.getLatitude()+" = "+gps.getLongitude());
                                latitude = gps.getLatitude();
                                longitude = gps.getLongitude();


                                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                                SimpleDateFormat sdt = new SimpleDateFormat("MMM dd,yyyy hh:mm a");

                                LatLng master = new LatLng(latitude,longitude);
                                String address = getAddress(master);

                                Meetings loginDetails = new Meetings();
                                loginDetails.setEmployeeId(PreferenceHandler.getInstance(getActivity()).getUserId());
                                loginDetails.setStartLatitude(""+latitude);
                                loginDetails.setStartLongitude(""+longitude);
                                loginDetails.setStartLocation(""+address);
                                loginDetails.setStartTime(""+sdt.format(new Date()));
                                loginDetails.setMeetingDate(""+sdf.format(new Date()));
                                loginDetails.setMeetingAgenda(purpose);
                                loginDetails.setMeetingDetails(remark);
                                loginDetails.setStatus("In Meeting");

                                if(contact!=null&&!contact.isEmpty()){
                                    loginDetails.setMeetingPersonDetails(client+"%"+contact);
                                }else{
                                    loginDetails.setMeetingPersonDetails(client);
                                }
                                try {

                                    MeetingDetailsNotificationManagers md = new MeetingDetailsNotificationManagers();
                                    md.setTitle("Meeting Details from "+PreferenceHandler.getInstance(getActivity()).getUserFullName());
                                    md.setMessage("Meeting with "+client+" for "+purpose);
                                    md.setLocation(address);
                                    md.setLongitude(""+longitude);
                                    md.setLatitude(""+latitude);
                                    md.setMeetingDate(""+sdt.format(new Date()));
                                    md.setStatus("In meeting");
                                    md.setEmployeeId(PreferenceHandler.getInstance(getActivity()).getUserId());
                                    md.setManagerId(PreferenceHandler.getInstance(getActivity()).getManagerId());
                                    md.setMeetingPerson(client);
                                    md.setMeetingsDetails(purpose);
                                    md.setMeetingComments(remark);


                                    addMeeting(loginDetails,dialog,md);
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

            }else{

            }




        }catch (Exception e){
            e.printStackTrace();
        }


    }

    public void addMeeting(final Meetings loginDetails,final AlertDialog dialogs,final MeetingDetailsNotificationManagers md) throws Exception{



        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Saving Details..");
        dialog.setCancelable(false);
        dialog.show();

        MeetingsAPI apiService = Util.getClient().create(MeetingsAPI.class);

        Call<Meetings> call = apiService.addMeeting(loginDetails);

        call.enqueue(new Callback<Meetings>() {
            @Override
            public void onResponse(Call<Meetings> call, Response<Meetings> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {
                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201) {

                        Meetings s = response.body();

                        if(s!=null){

                            dialogs.dismiss();

                            md.setMeetingsId(s.getMeetingsId());
                            Toast.makeText(getActivity(), "You Checked in", Toast.LENGTH_SHORT).show();

                            PreferenceHandler.getInstance(getActivity()).setMeetingId(s.getMeetingsId());

                            saveMeetingNotification(md);


                            punchOutTextMeeting.setText("Meeting-Out");
                            punchInTextMeeting.setText(""+s.getStartTime());
                            punchInMeeting.setEnabled(false);
                            punchOutMeeting.setEnabled(true);
                            PreferenceHandler.getInstance(getActivity()).setMeetingLoginStatus("Login");


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
            public void onFailure(Call<Meetings> call, Throwable t) {

                if(dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
                Toast.makeText(getActivity(), "Failed Due to "+t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());
            }
        });



    }


    public void getMeetings(final int id){

        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {

                final MeetingsAPI subCategoryAPI = Util.getClient().create(MeetingsAPI.class);
                Call<Meetings> getProf = subCategoryAPI.getMeetingById(id);
                //Call<ArrayList<Blogs>> getBlog = blogApi.getBlogs();

                getProf.enqueue(new Callback<Meetings>() {

                    @Override
                    public void onResponse(Call<Meetings> call, Response<Meetings> response) {

                        if (response.code() == 200||response.code() == 201||response.code() == 204)
                        {
                            System.out.println("Inside api");

                            final Meetings dto = response.body();

                            if(dto!=null){

                                try{

                                    if(locationCheck()){
                                        String message = "Login";




                                        message = "Do you want to Check-Out?";
                                        String option = "Meeting-Out";



                                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                        View views = inflater.inflate(R.layout.custom_alert_box_meeting, null);

                                        builder.setView(views);
                                        final Button mSave = (Button) views.findViewById(R.id.save);
                                        mSave.setText(option);
                                        final EditText mRemarks = (EditText) views.findViewById(R.id.meeting_remarks);
                                        final TextInputEditText mClient = (TextInputEditText) views.findViewById(R.id.client_name);
                                        final TextInputEditText mContact = (TextInputEditText) views.findViewById(R.id.client_contact);
                                        final TextInputEditText mPurpose = (TextInputEditText) views.findViewById(R.id.purpose_meeting);

                                        mRemarks.setText(""+dto.getMeetingDetails());
                                        if(dto.getMeetingPersonDetails().contains("%")){

                                            String person[] = dto.getMeetingPersonDetails().split("%");
                                            mContact.setText(""+person[1]);
                                            mClient.setText(""+person[0]);
                                        }else{
                                            mClient.setText(""+dto.getMeetingPersonDetails());
                                        }

                                        mPurpose.setText(""+dto.getMeetingAgenda());

                                        final AlertDialog dialogs = builder.create();
                                        dialogs.show();
                                        dialogs.setCanceledOnTouchOutside(true);

                                        mSave.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {

                                                String client = mClient.getText().toString();
                                                String purpose = mPurpose.getText().toString();
                                                String remark = mRemarks.getText().toString();
                                                String contact = mContact.getText().toString();

                                                if(client==null||client.isEmpty()){

                                                    Toast.makeText(getActivity(), "Please mention client/hotel name", Toast.LENGTH_SHORT).show();

                                                }else if(purpose==null||purpose.isEmpty()){

                                                    Toast.makeText(getActivity(), "Please mention purpose of meeting", Toast.LENGTH_SHORT).show();

                                                }else if(remark==null||remark.isEmpty()){

                                                    Toast.makeText(getActivity(), "Please mention remarks about meeting", Toast.LENGTH_SHORT).show();

                                                }else{

                                                    gps = new TrackGPS(getActivity());
                                                    if(gps.canGetLocation())
                                                    {
                                                        System.out.println("Long and lat Rev"+gps.getLatitude()+" = "+gps.getLongitude());
                                                        latitude = gps.getLatitude();
                                                        longitude = gps.getLongitude();


                                                        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                                                        SimpleDateFormat sdt = new SimpleDateFormat("MMM dd,yyyy hh:mm a");

                                                        LatLng master = new LatLng(latitude,longitude);
                                                        String address = getAddress(master);

                                                        Meetings loginDetails = dto;
                                                        loginDetails.setEmployeeId(PreferenceHandler.getInstance(getActivity()).getUserId());
                                                        loginDetails.setEndLatitude(""+latitude);
                                                        loginDetails.setEndLongitude(""+longitude);
                                                        loginDetails.setEndLocation(""+address);
                                                        loginDetails.setEndTime(""+sdt.format(new Date()));
                                                        loginDetails.setMeetingDate(""+sdf.format(new Date()));
                                                        loginDetails.setMeetingAgenda(purpose);
                                                        loginDetails.setMeetingDetails(remark);
                                                        loginDetails.setStatus("Completed");

                                                        if(contact!=null&&!contact.isEmpty()){
                                                            loginDetails.setMeetingPersonDetails(client+"%"+contact);
                                                        }else{
                                                            loginDetails.setMeetingPersonDetails(client);
                                                        }
                                                        try {

                                                            MeetingDetailsNotificationManagers md = new MeetingDetailsNotificationManagers();
                                                            md.setTitle("Meeting Details from "+PreferenceHandler.getInstance(getActivity()).getUserFullName());
                                                            md.setMessage("Meeting with "+client+" for "+purpose);
                                                            md.setLocation(address);
                                                            md.setLongitude(""+longitude);
                                                            md.setLatitude(""+latitude);
                                                            md.setMeetingDate(""+sdt.format(new Date()));
                                                            md.setStatus("Completed");
                                                            md.setEmployeeId(PreferenceHandler.getInstance(getActivity()).getUserId());
                                                            md.setManagerId(PreferenceHandler.getInstance(getActivity()).getManagerId());
                                                            md.setMeetingPerson(client);
                                                            md.setMeetingsId(loginDetails.getMeetingsId());
                                                            md.setMeetingsDetails(purpose);
                                                            md.setMeetingComments(remark);
                                                            updateMeeting(loginDetails,dialogs,md);

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

                                    }else{

                                    }




                                }catch (Exception e){
                                    e.printStackTrace();
                                }

                            }




                        }else{


                            //meet
                        }
                    }

                    @Override
                    public void onFailure(Call<Meetings> call, Throwable t) {

                    }
                });

            }

        });
    }

    public void updateMeeting(final Meetings loginDetails,final AlertDialog dialogs,final MeetingDetailsNotificationManagers md) throws Exception{



        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Saving Details..");
        dialog.setCancelable(false);
        dialog.show();

        MeetingsAPI apiService = Util.getClient().create(MeetingsAPI.class);

        Call<Meetings> call = apiService.updateMeetingById(loginDetails.getMeetingsId(),loginDetails);

        call.enqueue(new Callback<Meetings>() {
            @Override
            public void onResponse(Call<Meetings> call, Response<Meetings> response) {
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
                        saveMeetingNotification(md);

                        Toast.makeText(getActivity(), "You Checked out", Toast.LENGTH_SHORT).show();

                        PreferenceHandler.getInstance(getActivity()).setMeetingId(0);
                        getMeetingDetails();

                        punchInMeeting.setEnabled(true);
                        punchOutMeeting.setEnabled(false);


                        punchInTextMeeting.setText("Meeting-In");
                        PreferenceHandler.getInstance(getActivity()).setMeetingLoginStatus("Logout");



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
            public void onFailure(Call<Meetings> call, Throwable t) {

                if(dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
                Toast.makeText(getActivity(), "Failed Due to "+t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());
            }
        });



    }

    public void saveMeetingNotification(final MeetingDetailsNotificationManagers md) throws Exception{



        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Saving Details..");
        dialog.setCancelable(false);
        dialog.show();

        MeetingNotificationAPI apiService = Util.getClient().create(MeetingNotificationAPI.class);

        Call<MeetingDetailsNotificationManagers> call = apiService.saveMeetingNotification(md);

        call.enqueue(new Callback<MeetingDetailsNotificationManagers>() {
            @Override
            public void onResponse(Call<MeetingDetailsNotificationManagers> call, Response<MeetingDetailsNotificationManagers> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {
                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201) {

                        MeetingDetailsNotificationManagers s = response.body();

                        if(s!=null){

                            MeetingDetailsNotificationManagers md = new MeetingDetailsNotificationManagers();
                            md.setTitle(s.getTitle());
                            md.setMessage(s.getMessage());
                            md.setLocation(s.getLocation());
                            md.setLongitude(""+s.getLongitude());
                            md.setLatitude(""+s.getLatitude());
                            md.setMeetingDate(""+s.getMeetingDate());
                            md.setStatus(s.getStatus());
                            md.setEmployeeId(s.getManagerId());
                            md.setManagerId(s.getEmployeeId());
                            md.setMeetingPerson(s.getMeetingPerson());
                            md.setMeetingsDetails(s.getMeetingsDetails());
                            md.setMeetingComments(s.getMeetingComments());
                            md.setMeetingsId(s.getMeetingsId());
                            md.setSenderId(Constants.SENDER_ID);
                            md.setServerId(Constants.SERVER_ID);

                            sendMeetingNotification(md);

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
            public void onFailure(Call<MeetingDetailsNotificationManagers> call, Throwable t) {

                if(dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
                Toast.makeText(getActivity(), "Failed Due to "+t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());
            }
        });



    }

    public void sendMeetingNotification(final MeetingDetailsNotificationManagers md) throws Exception{



        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Sending Details..");
        dialog.setCancelable(false);
        dialog.show();

        MeetingNotificationAPI apiService = Util.getClient().create(MeetingNotificationAPI.class);

        Call<ArrayList<String>> call = apiService.sendMeetingNotification(md);

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

}

