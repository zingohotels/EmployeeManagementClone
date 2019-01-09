package app.zingo.employeemanagements.UI.NewEmployeeDesign;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Calendar;

import java.util.Timer;

import app.zingo.employeemanagements.Model.Employee;
import app.zingo.employeemanagements.R;

public class EmployeeLoginFragment extends Fragment implements  OnMapReadyCallback {
    static final String TAG = "EmpPunchInPunchOut";

    FusedLocationProviderClient client;
    private TextView latLong;
    View layout;
    private Timer localTimer;
    LocationCallback locationCallback;
    private View locationSender;
    private TextView lunchText;
    private Activity mActivity;

    String mBucket = "";

    Calendar mCalendar = null;
    private Context mContext;

    Employee mEmployee;
    String mKey = "";

    Marker mMarker;
    String mTimeZoneId = "";
    long[] mTimer = new long[1];
    int maxIntime = 0;
    private View onLunchBreak;
    private View onTeaBreak;
    Bitmap profileBitmap;
    private View punchIn;
    private TextView punchInText;
    private View punchOut;
    private TextView punchOutText;
    boolean remoteTimerSet;
    Boolean selfieEnabled = Boolean.valueOf(false);
    //LoadingProcessDialog serverProcessDialog;
    private TextView teaText;

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;


    public void centreMapOnLocation(Location location, String title){

        LatLng userLocation = new LatLng(location.getLatitude(),location.getLongitude());
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(userLocation).title(title));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,12));

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);

                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                centreMapOnLocation(lastKnownLocation,"Your Location");
            }
        }
    }


    public static EmployeeLoginFragment getInstance() {
        EmployeeLoginFragment employeePunchInOutFragment = new EmployeeLoginFragment();

        return employeePunchInOutFragment;
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.layout = layoutInflater.inflate(R.layout.fragment_employee_login, viewGroup, false);

        this.punchIn = this.layout.findViewById(R.id.punchIn);
        this.punchInText = (TextView) this.layout.findViewById(R.id.punchInText);
        this.punchOut = this.layout.findViewById(R.id.punchOut);
        this.punchOutText = (TextView) this.layout.findViewById(R.id.punchOutText);

        this.latLong = (TextView) this.layout.findViewById(R.id.latLong);
        this.locationSender = this.layout.findViewById(R.id.locationSender);
        ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);

        //setupLocalBucket();

        return this.layout;
    }



    @Override
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







    public void onStop() {
        if (!(this.client == null || this.locationCallback == null)) {
            this.client.removeLocationUpdates(this.locationCallback);
        }
        super.onStop();
    }


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


}

