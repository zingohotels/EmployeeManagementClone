package app.zingo.employeemanagements.ui.newemployeedesign;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputEditText;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;



import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import app.zingo.employeemanagements.adapter.CustomerSpinnerAdapter;
import app.zingo.employeemanagements.AlarmManager.LunchBreakAlarm;
import app.zingo.employeemanagements.AlarmManager.TeaBreakAlarm;
import app.zingo.employeemanagements.interfaces.logininterface.LoginCallbackInterface;
import app.zingo.employeemanagements.model.Customer;
import app.zingo.employeemanagements.model.LoginDetails;
import app.zingo.employeemanagements.model.LoginDetailsNotificationManagers;
import app.zingo.employeemanagements.model.MeetingDetailsNotificationManagers;
import app.zingo.employeemanagements.model.Meetings;
import app.zingo.employeemanagements.model.WorkingDay;
import app.zingo.employeemanagements.Service.CheckInAlarmReceiverService;
import app.zingo.employeemanagements.Service.LocationAndDataServiceWithTimer;
import app.zingo.employeemanagements.Service.LocationForegroundService;
import app.zingo.employeemanagements.utils.Constants;
import app.zingo.employeemanagements.utils.PreferenceHandler;
import app.zingo.employeemanagements.utils.ThreadExecuter;
import app.zingo.employeemanagements.utils.TrackGPS;
import app.zingo.employeemanagements.utils.Util;
import app.zingo.employeemanagements.WebApi.CustomerAPI;
import app.zingo.employeemanagements.WebApi.LoginDetailsAPI;
import app.zingo.employeemanagements.WebApi.LoginNotificationAPI;
import app.zingo.employeemanagements.WebApi.MeetingNotificationAPI;
import app.zingo.employeemanagements.WebApi.MeetingsAPI;
import app.zingo.employeemanagements.WebApi.OrganizationTimingsAPI;
import app.zingo.employeemanagements.WebApi.UploadApi;
import app.zingo.employeemanagements.base.R;
import app.zingo.employeemanagements.utils.loginclass.ActionLoginCallbackClass;
import app.zingo.employeemanagements.utils.loginclass.LoginCallbackClass;
import app.zingo.employeemanagements.utils.loginclass.LoginMenuOption;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.ALARM_SERVICE;

public class EmployeeLoginFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener, OnMapReadyCallback{

    static final String TAG = "EmpPunchInPunchOut";
    private LoginCallbackClass loginCallbackClass;
    private EmployeeNewMainScreen mContext;
    private TextView latLong;
    View layout;
    LinearLayout mRefreshLocation;
    private View punchIn;
    private TextView punchInText;
    private View punchOut;
    private TextView punchOutText;

    private View punchInQr;
    private TextView punchInTextQr;
    private View punchOutQr;
    private TextView punchOutTextQr;

    private View punchInMeeting;
    private TextView punchInTextMeeting;
    private View punchOutMeeting;
    private TextView punchOutTextMeeting;

    private View teaBreakLay;
    private TextView teaBreakText;
    private View lunchBreakLay;
    private TextView lunchBreakText;
    ImageView mImageView;
    File file;
    Dialog dialogs;
    LinearLayout mContent;
    View view;
    signature mSignature;
    Button  mClear, mGetSigns, mCancel;
    Bitmap bitmap;
    // Creating Separate Directory for saving Generated Images
    String DIRECTORY = Environment.getExternalStorageDirectory().getPath() + "/Mysolite Apps/";
    String pic_name = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
    String StoredPath = DIRECTORY + pic_name + ".png";
    String StoredPathSelfie = DIRECTORY + pic_name+"selfie" + ".png";
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private GoogleMap mMap;
    private GoogleApiClient mLocationClient;
    Location currentLocation;
    //Location
    TrackGPS gps;
    double latitude, longitude;
    String  planType = "";
    //Google Place API
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 60000;  /* 60 secs */
    private long FASTEST_INTERVAL = 30000; /* 30 secs */
    Meetings loginDetails;
    MeetingDetailsNotificationManagers md;
    boolean methodAdd = false;
    boolean firstTime = true;
    boolean firstChck = true;
    ArrayList< Customer > customerArrayList;
    Spinner customerSpinner;
    LinearLayout ClientNameLayout;
    int clientId = 0;
    private static final int ALARM_REQUEST_CODE = 133;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    int timingId = 0;
    String checkInTime = "", nextCheckInTime = "";
    boolean location;
    private ActionLoginCallbackClass loginCallbackInterface;
    private LoginMenuOption menuOption;

    public void centreMapOnLocationWithLatLng(LatLng location, String title) {
        LatLng userLocation = location;
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(userLocation).title(title));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12));

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if ( ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Location lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(mLocationClient);
            }
        }
    }

    public static EmployeeLoginFragment getInstance() {
        EmployeeLoginFragment employeePunchInOutFragment = new EmployeeLoginFragment ();
        return employeePunchInOutFragment;
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        layout = layoutInflater.inflate(R.layout.fragment_employee_login, viewGroup, false);
        loginCallbackClass = new LoginCallbackClass (mContext);
        mRefreshLocation = layout.findViewById(R.id.refreshLocation);
        punchIn = layout.findViewById(R.id.punchIn);
        punchInText = layout.findViewById(R.id.punchInText);
        punchOut = layout.findViewById(R.id.punchOut);
        punchOutText = layout.findViewById(R.id.punchOutText);

        punchInQr = layout.findViewById(R.id.punchIn_qr);
        punchInTextQr = layout.findViewById(R.id.punchInText_qr);
        punchOutQr = layout.findViewById(R.id.punchOut_qr);
        punchOutTextQr = layout.findViewById(R.id.punchOutText_qr);

        punchInMeeting = layout.findViewById(R.id.punchInMeeting);
        punchInTextMeeting = layout.findViewById(R.id.punchInTextMeeting);
        punchOutMeeting = layout.findViewById(R.id.punchOutMeeting);
        punchOutTextMeeting = layout.findViewById(R.id.punchOutTextMeeting);

        teaBreakLay = layout.findViewById(R.id.tea_break);
        teaBreakText = layout.findViewById(R.id.tea_break_text);
        lunchBreakLay = layout.findViewById(R.id.lunch_break);
        lunchBreakText = layout.findViewById(R.id.lunch_break_text);

        planType = PreferenceHandler.getInstance(getActivity()).getPlanType();
        getMeetingDetails();
        String shift = PreferenceHandler.getInstance(getActivity()).getShiftName();
        if ( shift != null && !shift.isEmpty( ) ) {
            if ( android.text.TextUtils.isDigitsOnly( shift ) ) {
                try {
                    timingId = Integer.parseInt( shift );
                    getShiftTimingById( timingId );
                } catch ( Exception e ) {
                    e.printStackTrace( );
                }
            }
        }

        String loginStatus = PreferenceHandler.getInstance(getActivity()).getLoginStatus();
        gps = new TrackGPS (getActivity());

        if (!PreferenceHandler.getInstance(getActivity()).getLoginTime().isEmpty()) {
            punchInText.setText("" + PreferenceHandler.getInstance(getActivity()).getLoginTime());
        }

        this.latLong = this.layout.findViewById(R.id.latLong);
        ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);

        if (mLocationClient == null) {
            mLocationClient = new GoogleApiClient.Builder(this.getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        mRefreshLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (locationCheck()) {
                    if(currentLocation!=null){
                        latitude = currentLocation.getLatitude();
                        longitude = currentLocation.getLongitude();

                        LatLng master = new LatLng(latitude,longitude);
                        String address = null;
                        try {
                            address = getAddress(master);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        latLong.setText(address);
                        centreMapOnLocationWithLatLng(master,""+ PreferenceHandler.getInstance(getActivity()).getUserFullName());
                    }
                }
            }
        });

        punchIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String loginStatus = PreferenceHandler.getInstance(getActivity()).getLoginStatus();
                if (loginStatus != null && !loginStatus.isEmpty()) {
                    if (loginStatus.equalsIgnoreCase("Logout")) {
                        masterLoginalert ("Logout","gps");
                    }
                } else {
                    masterLoginalert ("Logout","gps");
                }
            }
        });

        punchOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String loginStatus = PreferenceHandler.getInstance(getActivity()).getLoginStatus();
                String meetingStatus = PreferenceHandler.getInstance(getActivity()).getMeetingLoginStatus();
                if (loginStatus != null && !loginStatus.isEmpty()) {
                    if (loginStatus.equalsIgnoreCase("Login")) {
                        if (meetingStatus != null && meetingStatus.equalsIgnoreCase("Login")) {
                            Toast.makeText(getActivity(), "You are in some meeting .So Please checkout", Toast.LENGTH_SHORT).show();
                        } else {
                            getLoginDetails(PreferenceHandler.getInstance(getActivity()).getLoginId(),"gps");
                        }
                    }
                }
            }
        });

        punchInQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String loginStatus = PreferenceHandler.getInstance(getActivity()).getLoginStatus();
                String meetingStatus = PreferenceHandler.getInstance(getActivity()).getMeetingLoginStatus();

                if (loginStatus != null && !loginStatus.isEmpty()) {
                    if (loginStatus.equalsIgnoreCase("Logout")) {
                        masterLoginalert ("Logout","Qr");
                    }
                } else {
                    masterLoginalert ("Logout","Qr");
                }
            }
        });

        punchOutQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String loginStatus = PreferenceHandler.getInstance(getActivity()).getLoginStatus();
                String meetingStatus = PreferenceHandler.getInstance(getActivity()).getMeetingLoginStatus();
                if (loginStatus != null && !loginStatus.isEmpty()) {
                    if (loginStatus.equalsIgnoreCase("Login")) {
                        if (meetingStatus != null && meetingStatus.equalsIgnoreCase("Login")) {
                            Toast.makeText(getActivity(), "You are in some meeting .So Please checkout", Toast.LENGTH_SHORT).show();
                        } else {
                            getLoginDetails(PreferenceHandler.getInstance(getActivity()).getLoginId(),"Qr");
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
                if (masterloginStatus.equals("Login")) {
                    if (loginStatus != null && !loginStatus.isEmpty()) {
                        if (loginStatus.equalsIgnoreCase("Login")) {
                        } else if (loginStatus.equalsIgnoreCase("Logout")) {
                            meetingloginalert("Logout");
                        }

                    } else {
                        meetingloginalert("Logout");
                    }
                } else {
                    Toast.makeText(getActivity(), "First Check-in Master", Toast.LENGTH_SHORT).show();
                }
            }
        });

        punchOutMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String loginStatus = PreferenceHandler.getInstance(getActivity()).getMeetingLoginStatus();
                String masterloginStatus = PreferenceHandler.getInstance(getActivity()).getLoginStatus();

                if (masterloginStatus.equals("Login")) {
                    if (loginStatus != null && !loginStatus.isEmpty()) {
                        if (loginStatus.equalsIgnoreCase("Login")) {
                            getMeetings(PreferenceHandler.getInstance(getActivity()).getMeetingId());
                        } else if (loginStatus.equalsIgnoreCase("Logout")) {
                            meetingloginalert("Logout");
                        }

                    } else {
                        meetingloginalert("Logout");
                    }
                } else {
                    Toast.makeText(getActivity(), "First Check-in Master", Toast.LENGTH_SHORT).show();
                }
            }
        });

        teaBreakLay.setOnClickListener( v -> {
            if ( locationCheck ( ) ) {
                if ( currentLocation != null ) {
                    loginCallbackInterface = new ActionLoginCallbackClass ( loginCallbackClass );
                    menuOption = new LoginMenuOption ( loginCallbackInterface , "TeaBreak" );
                    menuOption.clickTeaBreak ( currentLocation , teaBreakText );
                }
            }
        } );

        lunchBreakLay.setOnClickListener( v -> {
            if (locationCheck()) {
                if ( currentLocation != null ) {
                    loginCallbackInterface = new ActionLoginCallbackClass ( loginCallbackClass );
                    menuOption = new LoginMenuOption ( loginCallbackInterface , "LunchBreak" );
                    menuOption.clickLunchBreak ( currentLocation , lunchBreakText );
                }
            }
        } );

        return this.layout;
    }
    private boolean locationCheck () {
        final LocationManager manager = ( LocationManager ) mContext.getSystemService ( Context.LOCATION_SERVICE );
        if ( ! manager.isProviderEnabled ( LocationManager.GPS_PROVIDER ) ) {
            gps.showSettingsAlert ( );
        }else{
            location = true;
        }
        return location;
    }

    private void getLoginDetails() {
        new ThreadExecuter ().execute( new Runnable() {
            @Override
            public void run() {
                LoginDetailsAPI apiService = Util.getClient().create( LoginDetailsAPI.class);
                Call<ArrayList< LoginDetails >> call = apiService.getLoginByEmployeeId(PreferenceHandler.getInstance(getActivity()).getUserId());
                call.enqueue(new Callback<ArrayList< LoginDetails >>() {
                    @Override
                    public void onResponse( Call<ArrayList< LoginDetails >> call, Response<ArrayList< LoginDetails >> response) {
                        int statusCode = response.code();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {
                            ArrayList< LoginDetails > list = response.body();
                            Collections.sort(list, LoginDetails.compareLogin);
                            if (list != null && list.size() != 0) {
                                LoginDetails loginDetails = list.get(list.size() - 1);
                                if (loginDetails != null) {

                                    String logout = loginDetails.getLogOutTime();
                                    String login = loginDetails.getLoginTime();
                                    String dates = loginDetails.getLoginDate();//9710979393
                                    String date = null;

                                    if (dates != null && !dates.isEmpty()) {
                                        if (dates.contains("T")) {
                                            String logins[] = dates.split("T");
                                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                            SimpleDateFormat sdfs = new SimpleDateFormat("MMM dd,yyyy");

                                            Date dt = null;
                                            try {
                                                dt = sdf.parse(logins[0]);
                                                date = sdfs.format(dt);
                                                String newDateS = sdf.format(new Date());
                                                Date newDate = sdf.parse(newDateS);
                                                if(newDate.getTime()>dt.getTime()&& PreferenceHandler.getInstance(getActivity()).isFirstCheck()){
                                                    final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                                                    LayoutInflater inflater = getLayoutInflater();
                                                    View views = inflater.inflate(R.layout.check_in_pop,null);
                                                    Button agree = views.findViewById(R.id.dialog_ok);
                                                    dialogBuilder.setView(views);
                                                    final AlertDialog dialog = dialogBuilder.create();
                                                    dialog.setCancelable(true);
                                                    dialog.show();

                                                    agree.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            dialog.dismiss();
                                                            Intent intent = new Intent(getActivity(), EmployeeNewMainScreen.class);
                                                            intent.putExtra("viewpager_position", 2);
                                                            intent.putExtra("Condition", false);
                                                            startActivity(intent);
                                                        }
                                                    });
                                                    firstChck = false;
                                                }

                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }

                                    if (logout != null && !logout.isEmpty() && (login != null && !login.isEmpty())) {
                                        punchInText.setText("Check-In");
                                        PreferenceHandler.getInstance(getActivity()).setLoginStatus("Logout");
                                    } else if (login != null && !login.isEmpty() && (logout == null || logout.isEmpty())) {
                                        if (date != null && !date.isEmpty()) {
                                            punchInText.setText("" + login);
                                        } else {
                                            punchInText.setText("" + login);
                                        }
                                        punchOutText.setText("Check-out");
                                        PreferenceHandler.getInstance(getActivity()).setLoginStatus("Login");
                                        PreferenceHandler.getInstance(getActivity()).setLoginId(loginDetails.getLoginDetailsId());
                                    }
                                }

                                if(PreferenceHandler.getInstance(getActivity()).getLoginStatus().equalsIgnoreCase("Login")){
                                    if(isMyServiceRunning( LocationForegroundService.class)){

                                    }else{
                                        if ( mContext != null ) {
                                            Intent intent = new Intent( getActivity( ) , LocationForegroundService.class );
                                            intent.setAction( LocationForegroundService.ACTION_START_FOREGROUND_SERVICE );
                                            if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ) {
                                                Objects.requireNonNull( getActivity( ) ).startForegroundService( intent );
                                            } else {
                                                getActivity( ).startService( intent );
                                            }
                                            Intent startNeGp = new Intent( getActivity( ) , LocationAndDataServiceWithTimer.class );
                                            getActivity( ).startService( startNeGp );
                                        }
                                    }

                                }else if(PreferenceHandler.getInstance(getActivity()).getLoginStatus().equalsIgnoreCase("Logout")) {

                                    if(isMyServiceRunning( LocationForegroundService.class)){
                                        Intent intent = new Intent(getActivity(), LocationForegroundService.class);
                                        intent.setAction( LocationForegroundService.ACTION_STOP_FOREGROUND_SERVICE);
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            getActivity().startForegroundService(intent);
                                        } else {
                                            getActivity().startService(intent);
                                        }

                                        Intent stopNeGp = new Intent(getActivity(), LocationAndDataServiceWithTimer.class);
                                        getActivity().stopService(stopNeGp);

                                    }else{

                                    }
                                }

                            } else {
                                if(PreferenceHandler.getInstance(getActivity()).isFirstCheck()){
                                    final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                                    LayoutInflater inflater = getLayoutInflater();
                                    View views = inflater.inflate(R.layout.check_in_pop,null);
                                    Button agree = views.findViewById(R.id.dialog_ok);

                                    dialogBuilder.setView(views);
                                    final AlertDialog dialog = dialogBuilder.create();
                                    dialog.setCancelable(true);
                                    dialog.show();
                                    agree.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialog.dismiss();
                                            Intent intent = new Intent(getActivity(), EmployeeNewMainScreen.class);
                                            intent.putExtra("viewpager_position", 2);
                                            intent.putExtra("Condition", false);
                                            startActivity(intent);
                                        }
                                    });

                                    firstChck = false;
                                }
                            }

                        } else {
                            Toast.makeText(getActivity(), "Failed due to : " + response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure( Call<ArrayList< LoginDetails >> call, Throwable t) {
                        // Log error here since request failed

                        Log.e("TAG", t.toString());
                    }
                });
            }
        });
    }

    public void getLoginDetails(final int id,final String type) {
        new ThreadExecuter ().execute( new Runnable() {
            @Override
            public void run() {
                final LoginDetailsAPI subCategoryAPI = Util.getClient().create( LoginDetailsAPI.class);
                Call< LoginDetails > getProf = subCategoryAPI.getLoginById(id);
                getProf.enqueue(new Callback< LoginDetails >() {
                    @Override
                    public void onResponse( Call< LoginDetails > call, Response< LoginDetails > response) {
                        if (response.code() == 200 || response.code() == 201 || response.code() == 204) {
                            System.out.println("Inside api");
                            final LoginDetails dto = response.body();
                            if (dto != null) {
                                try {
                                    String message = "Login";
                                    String option = "Check-In";

                                    message = "Do you want to Check-Out?";
                                    option = "Check-Out";

                                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                    builder.setTitle(message);
                                    builder.setPositiveButton(option, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if (locationCheck()) {
                                                ArrayList<String> appNames = new ArrayList<>();
                                                if(currentLocation!=null) {
                                                    if(Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION).equals("0")){
                                                        //Toast.makeText(mContext, "Mock Location Enabled" , Toast.LENGTH_SHORT).show();
                                                        if(gps.isMockLocationOn(currentLocation,getActivity())){
                                                            appNames.addAll(gps.listofApps(getActivity()));
                                                        }
                                                    }

                                                    if(appNames!=null&&appNames.size()!=0){

                                                    }else{
                                                        latitude = currentLocation.getLatitude();
                                                        longitude = currentLocation.getLongitude();

                                                        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                                                        SimpleDateFormat sdt = new SimpleDateFormat("MMM dd,yyyy hh:mm a");

                                                        LatLng master = new LatLng(latitude, longitude);
                                                        String address = null;
                                                        try {
                                                            address = getAddress(master);
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }

                                                        LoginDetails loginDetails = dto;
                                                        loginDetails.setEmployeeId(PreferenceHandler.getInstance(getActivity()).getUserId());
                                                        loginDetails.setLogoutLatitude ("" + latitude);
                                                        loginDetails.setLogoutLongitude ("" + longitude);
                                                        loginDetails.setLogoutLocation ("" + address);
                                                        loginDetails.setLogOutTime("" + sdt.format(new Date()));
                                                        try {
                                                            LoginDetailsNotificationManagers md = new LoginDetailsNotificationManagers ();
                                                            md.setTitle("Login Details from " + PreferenceHandler.getInstance(getActivity()).getUserFullName());
                                                            md.setMessage("Log out at  " + "" + sdt.format(new Date()));
                                                            md.setLatitude ("" + latitude);
                                                            md.setLongitude ("" + longitude);
                                                            md.setLocation ("" + address);
                                                            md.setLoginDate("" + sdt.format(new Date()));
                                                            md.setStatus("Log out");
                                                            md.setEmployeeId(PreferenceHandler.getInstance(getActivity()).getUserId());
                                                            md.setManagerId(PreferenceHandler.getInstance(getActivity()).getManagerId());
                                                            md.setLoginDetailsId(dto.getLoginDetailsId());

                                                            if(type!=null&&type.equalsIgnoreCase("gps")){
                                                                updateLogin(loginDetails, builder.create(), md);
                                                                Intent intent = new Intent(getActivity(), LocationForegroundService.class);
                                                                intent.setAction( LocationForegroundService.ACTION_STOP_FOREGROUND_SERVICE);
                                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                                    getActivity().startForegroundService(intent);
                                                                } else {
                                                                    getActivity().startService(intent);
                                                                }
                                                                Intent stopNeGp = new Intent(getActivity(), LocationAndDataServiceWithTimer.class);
                                                                getActivity().stopService(stopNeGp);

                                                            }else if(type!=null&&type.equalsIgnoreCase("Qr")){
                                                                builder.create().dismiss();
                                                                Intent qr = new Intent(getActivity(), ScannedQrScreen.class);
                                                                Bundle bundle = new Bundle();
                                                                bundle.putSerializable("LoginDetails",loginDetails);
                                                                bundle.putSerializable("LoginNotification",md);
                                                                bundle.putString("Type","Check-out");
                                                                qr.putExtras(bundle);
                                                                startActivity(qr);
                                                            }

                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }

                                                }else if(latitude!=0&&longitude!=0){
                                                    LatLng masters = new LatLng(latitude, longitude);
                                                    String addresss = null;
                                                    try {
                                                        addresss = getAddress(masters);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }

                                                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                                                    SimpleDateFormat sdt = new SimpleDateFormat("MMM dd,yyyy hh:mm a");

                                                    LatLng master = new LatLng(latitude, longitude);
                                                    String address = null;
                                                    try {
                                                        address = getAddress(master);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }

                                                    LoginDetails loginDetails = dto;
                                                    loginDetails.setEmployeeId(PreferenceHandler.getInstance(getActivity()).getUserId());
                                                    loginDetails.setLogoutLatitude ("" + latitude);
                                                    loginDetails.setLogoutLongitude ("" + longitude);
                                                    loginDetails.setLogoutLocation ("" + address);
                                                    loginDetails.setLogOutTime("" + sdt.format(new Date()));
                                                    // loginDetails.setLoginDate(""+sdf.format(new Date()));

                                                    try {
                                                        LoginDetailsNotificationManagers md = new LoginDetailsNotificationManagers ();
                                                        md.setTitle("Login Details from " + PreferenceHandler.getInstance(getActivity()).getUserFullName());
                                                        md.setMessage("Log out at  " + "" + sdt.format(new Date()));
                                                        md.setLatitude ("" + latitude);
                                                        md.setLongitude ("" + longitude);
                                                        md.setLocation ("" + address);
                                                        md.setLoginDate("" + sdt.format(new Date()));
                                                        md.setStatus("Log out");
                                                        md.setEmployeeId(PreferenceHandler.getInstance(getActivity()).getUserId());
                                                        md.setManagerId(PreferenceHandler.getInstance(getActivity()).getManagerId());
                                                        md.setLoginDetailsId(dto.getLoginDetailsId());

                                                        if(type!=null&&type.equalsIgnoreCase("gps")){
                                                            updateLogin(loginDetails, builder.create(), md);
                                                            Intent intent = new Intent(getActivity(), LocationForegroundService.class);
                                                            intent.setAction( LocationForegroundService.ACTION_STOP_FOREGROUND_SERVICE);
                                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                                getActivity().startForegroundService(intent);
                                                            } else {
                                                                getActivity().startService(intent);
                                                            }
                                                            Intent stopNeGp = new Intent(getActivity(), LocationAndDataServiceWithTimer.class);
                                                            getActivity().stopService(stopNeGp);

                                                        }else if(type!=null&&type.equalsIgnoreCase("Qr")){
                                                            builder.create().dismiss();
                                                            Intent qr = new Intent(getActivity(), ScannedQrScreen.class);
                                                            Bundle bundle = new Bundle();
                                                            bundle.putSerializable("LoginDetails",loginDetails);
                                                            bundle.putSerializable("LoginNotification",md);
                                                            bundle.putString("Type","Check-out");
                                                            qr.putExtras(bundle);
                                                            startActivity(qr);
                                                        }

                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
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

                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure( Call< LoginDetails > call, Throwable t) {

                    }
                });
            }
        });
    }

    public void updateLogin( final LoginDetails loginDetails, final AlertDialog dialogs, final LoginDetailsNotificationManagers md) {
        Gson gson = new Gson ();
        String json1 = gson.toJson ( loginDetails );
        String json2 = gson.toJson ( loginDetails );
        System.out.println("Suree 1 :"+json1);
        System.out.println("Suree 2 :"+json2);
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Saving Details..");
        dialog.setCancelable(false);
        dialog.show();
        LoginDetailsAPI apiService = Util.getClient().create( LoginDetailsAPI.class);
        Call< LoginDetails > call = apiService.updateLoginById(loginDetails.getLoginDetailsId(), loginDetails);
        call.enqueue(new Callback< LoginDetails >() {
            @Override
            public void onResponse( Call< LoginDetails > call, Response< LoginDetails > response) {
                try {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201 || response.code() == 204) {
                        if(dialogs!=null){
                            dialogs.dismiss();
                        }

                        saveLoginNotification(md);

                        Toast.makeText(getActivity(), "You logged out", Toast.LENGTH_SHORT).show();

                        PreferenceHandler.getInstance(getActivity()).setLoginId(0);
                        PreferenceHandler.getInstance(getActivity()).setFar(false);


                        punchInText.setText("Check in");
                        PreferenceHandler.getInstance(getActivity()).setLoginTime("");
                        PreferenceHandler.getInstance(getActivity()).setLoginStatus("Logout");

                        punchIn.setEnabled(true);
                        punchOut.setEnabled(false);

                        String date = loginDetails.getLoginDate();

                        if (date != null && !date.isEmpty()) {

                            if (date.contains("T")) {

                                String logins[] = date.split("T");
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                SimpleDateFormat sdfs = new SimpleDateFormat("MMM dd,yyyy");

                                Date dt = sdf.parse(logins[0]);
                                punchOutText.setText("" + loginDetails.getLogOutTime());
                            }
                        } else {
                            punchOutText.setText("" + loginDetails.getLogOutTime());
                        }


                    } else {
                        Toast.makeText(getActivity(), "Failed Due to " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception ex) {

                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    ex.printStackTrace();
                }
//                callGetStartEnd();
            }

            @Override
            public void onFailure( Call< LoginDetails > call, Throwable t) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                Log.e("TAG", t.toString());
            }
        });
    }

    public void masterLoginalert ( final String status, final String type) {
        try {
            String message = "Do you want to Check-In?";
            String option = "Check-In";
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(message);
            builder.setPositiveButton(option, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (locationCheck()) {
                        ArrayList<String> appNames = new ArrayList<>();
                        if(currentLocation!=null){
                            if(Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION).equals("0")){
                                if(gps.isMockLocationOn(currentLocation,getActivity())){
                                    appNames.addAll(gps.listofApps(getActivity()));
                                }
                            }

                            if(appNames!=null&&appNames.size()!=0){
                            }else{
                                latitude = currentLocation.getLatitude();
                                longitude = currentLocation.getLongitude();

                                String title = "Login Details from " + PreferenceHandler.getInstance(getActivity()).getUserFullName();

                                Location locationA = new Location("point A");
                                locationA.setLatitude(Double.parseDouble(PreferenceHandler.getInstance(getActivity()).getOrganizationLati()));
                                locationA.setLongitude(Double.parseDouble(PreferenceHandler.getInstance(getActivity()).getOrganizationLongi()));

                                Location locationB = new Location("point B");
                                locationB.setLatitude(latitude);
                                locationB.setLongitude(longitude);

                                float distance = locationA.distanceTo(locationB);

                                if (PreferenceHandler.getInstance(getActivity()).isLocationOn()) {
                                    distance = 0;
                                }

                                // Toast.makeText(getActivity(), "distance "+distance, Toast.LENGTH_SHORT).show();
                                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                                SimpleDateFormat sdt = new SimpleDateFormat("MMM dd,yyyy hh:mm a");
                                SimpleDateFormat sdtT = new SimpleDateFormat("hh:mm a");

                                LatLng master = new LatLng(latitude, longitude);
                                String address = null;
                                try {
                                    address = getAddress(master);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                String currentTime = sdtT.format(new Date());
                                LoginDetails loginDetails = new LoginDetails ();

                                if (PreferenceHandler.getInstance(getActivity()).isDataOn()) {
                                    loginDetails.setIdleTime("0");
                                } else {
                                    if(timingId!=0&&checkInTime!=null&&!checkInTime.isEmpty()){
                                        try {
                                            Date curT = sdtT.parse(currentTime);
                                            Date offT = sdtT.parse(checkInTime);

                                            long diff = curT.getTime() - offT.getTime();

                                            if (diff > 0) {
                                                long diffMinutes = diff / (60 * 1000) % 60;
                                                long diffHours = diff / (60 * 60 * 1000) % 24;

                                                loginDetails.setIdleTime("100");
                                                title = "Late Login Details from " + PreferenceHandler.getInstance(getActivity()).getUserFullName();

                                                Toast.makeText(mContext, "You are late " + (int) diffHours + " Hours " + (int) diffMinutes + " mins ", Toast.LENGTH_SHORT).show();
                                            } else {
                                                loginDetails.setIdleTime("0");
                                            }

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }

                                loginDetails.setEmployeeId(PreferenceHandler.getInstance(getActivity()).getUserId());
                                loginDetails.setLatitude("" + latitude);
                                loginDetails.setLongitude("" + longitude);
                                loginDetails.setLocation("" + address);
                                loginDetails.setLoginTime("" + sdt.format(new Date()));
                                loginDetails.setLoginDate("" + sdf.format(new Date()));
                                loginDetails.setLogOutTime("");

                                LoginDetailsNotificationManagers md = new LoginDetailsNotificationManagers ();
                                try {
                                    md.setTitle(title);
                                    md.setMessage("Log in at  " + "" + sdt.format(new Date()));
                                    md.setLocation(address);
                                    md.setLongitude("" + longitude);
                                    md.setLatitude("" + latitude);
                                    md.setLoginDate("" + sdt.format(new Date()));
                                    md.setStatus("In meeting");
                                    md.setEmployeeId(PreferenceHandler.getInstance(getActivity()).getUserId());
                                    md.setManagerId(PreferenceHandler.getInstance(getActivity()).getManagerId());

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                if(type!=null&&type.equalsIgnoreCase("gps")){
                                    float limit = PreferenceHandler.getInstance(getActivity()).getLocationLimit();
                                    if (distance >= 0 && distance <= limit) {
                                        try {
                                            addLogin(loginDetails, builder.create(), md);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                    } else {
                                        Toast.makeText(getActivity(), "You are far away " + distance + " meter from your office", Toast.LENGTH_SHORT).show();
                                    }

                                }else if(type!=null&&type.equalsIgnoreCase("Qr")){
                                    dialogInterface.dismiss();
                                    Intent qr = new Intent(getActivity(), ScannedQrScreen.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("LoginDetails",loginDetails);
                                    bundle.putSerializable("LoginNotification",md);
                                    bundle.putString("Type","Check-in");
                                    qr.putExtras(bundle);
                                    startActivity(qr);
                                }
                            }

                        }else if(latitude!=0&&longitude!=0){
                            LatLng masters = new LatLng(latitude,longitude);
                            String addresss = null;
                            try {
                                addresss = getAddress(masters);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            Location locationA = new Location("point A");

                            locationA.setLatitude(Double.parseDouble(PreferenceHandler.getInstance(getActivity()).getOrganizationLati()));
                            locationA.setLongitude(Double.parseDouble(PreferenceHandler.getInstance(getActivity()).getOrganizationLongi()));

                            Location locationB = new Location("point B");

                            locationB.setLatitude(latitude);
                            locationB.setLongitude(longitude);

                            float distance = locationA.distanceTo(locationB);

                            if (PreferenceHandler.getInstance(getActivity()).isLocationOn()) {
                                distance = 0;
                            }

                            // Toast.makeText(getActivity(), "distance "+distance, Toast.LENGTH_SHORT).show();
                            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                            SimpleDateFormat sdt = new SimpleDateFormat("MMM dd,yyyy hh:mm a");
                            SimpleDateFormat sdtT = new SimpleDateFormat("hh:mm a");

                            LatLng master = new LatLng(latitude, longitude);
                            String address = null;
                            try {
                                address = getAddress(master);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            String currentTime = sdtT.format(new Date());
                            LoginDetails loginDetails = new LoginDetails ();

                            if (PreferenceHandler.getInstance(getActivity()).isDataOn()) {
                                loginDetails.setIdleTime("0");
                            } else {
                                if (currentTime.equalsIgnoreCase(PreferenceHandler.getInstance(getActivity()).getCheckInTime())) {
                                    loginDetails.setIdleTime("0");
                                } else {
                                    try {
                                        Date curT = sdtT.parse(currentTime);
                                        Date offT = sdtT.parse(PreferenceHandler.getInstance(getActivity()).getCheckInTime());
                                        long diff = curT.getTime() - offT.getTime();

                                        if (diff > 0) {
                                            long diffMinutes = diff / (60 * 1000) % 60;
                                            long diffHours = diff / (60 * 60 * 1000) % 24;

                                            loginDetails.setIdleTime("100");
                                            Toast.makeText(mContext, "You are late " + (int) diffHours + " Hours " + (int) diffMinutes + " mins ", Toast.LENGTH_SHORT).show();
                                        } else {
                                            loginDetails.setIdleTime("0");
                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            loginDetails.setEmployeeId(PreferenceHandler.getInstance(getActivity()).getUserId());
                            loginDetails.setLatitude("" + latitude);
                            loginDetails.setLongitude("" + longitude);
                            loginDetails.setLocation("" + address);
                            loginDetails.setLoginTime("" + sdt.format(new Date()));
                            loginDetails.setLoginDate("" + sdf.format(new Date()));
                            loginDetails.setLogOutTime("");

                            LoginDetailsNotificationManagers md = new LoginDetailsNotificationManagers ();
                            try {
                                md.setTitle("Login Details from " + PreferenceHandler.getInstance(getActivity()).getUserFullName());
                                md.setMessage("Log in at  " + "" + sdt.format(new Date()));
                                md.setLocation(address);
                                md.setLongitude("" + longitude);
                                md.setLatitude("" + latitude);
                                md.setLoginDate("" + sdt.format(new Date()));
                                md.setStatus("In meeting");
                                md.setEmployeeId(PreferenceHandler.getInstance(getActivity()).getUserId());
                                md.setManagerId(PreferenceHandler.getInstance(getActivity()).getManagerId());

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            if(type!=null&&type.equalsIgnoreCase("gps")){
                                float limit = PreferenceHandler.getInstance(getActivity()).getLocationLimit();
                                if (distance >= 0 && distance <= limit) {
                                    try {
                                        addLogin(loginDetails, builder.create(), md);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    Toast.makeText(getActivity(), "You are far away " + distance + " meter from your office", Toast.LENGTH_SHORT).show();
                                }

                            }else if(type!=null&&type.equalsIgnoreCase("Qr")){
                                dialogInterface.dismiss();
                                Intent qr = new Intent(getActivity(), ScannedQrScreen.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("LoginDetails",loginDetails);
                                bundle.putSerializable("LoginNotification",md);
                                bundle.putString("Type","Check-in");
                                qr.putExtras(bundle);
                                startActivity(qr);
                            }
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

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void addLogin( final LoginDetails loginDetails, final AlertDialog dialogs, final LoginDetailsNotificationManagers md) {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Saving Details..");
        dialog.setCancelable(false);
        dialog.show();
        LoginDetailsAPI apiService = Util.getClient().create( LoginDetailsAPI.class);
        Call< LoginDetails > call = apiService.addLogin(loginDetails);
        call.enqueue(new Callback< LoginDetails >() {
            @Override
            public void onResponse( Call< LoginDetails > call, Response< LoginDetails > response) {
                try {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201) {

                        LoginDetails s = response.body();
                        if (s != null) {
                            punchIn.setEnabled(false);
                            punchOut.setEnabled(true);

                            if(dialogs!=null){
                                dialogs.dismiss();
                            }
                            md.setLoginDetailsId(s.getLoginDetailsId());
                            saveLoginNotification(md);
                            Toast.makeText(getActivity(), "You Logged in", Toast.LENGTH_SHORT).show();
                            PreferenceHandler.getInstance(getActivity()).setLoginId(s.getLoginDetailsId());
                            String date = s.getLoginDate();

                            if (date != null && !date.isEmpty()) {
                                if (date.contains("T")) {
                                    String logins[] = date.split("T");
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                    SimpleDateFormat sdfs = new SimpleDateFormat("MMM dd,yyyy");

                                    Date dt = sdf.parse(logins[0]);
                                    punchInText.setText("" + s.getLoginTime());
                                    punchOutText.setText("Check-Out" );
                                }
                            } else {
                                punchInText.setText("" + s.getLoginTime());
                                punchOutText.setText("Check-Out" );
                            }
                            PreferenceHandler.getInstance(getActivity()).setLoginStatus("Login");
                            PreferenceHandler.getInstance(getActivity()).setLoginTime("" + s.getLoginTime());

                            Intent intent = new Intent(getActivity(), LocationForegroundService.class);
                            intent.setAction( LocationForegroundService.ACTION_START_FOREGROUND_SERVICE);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                getActivity().startForegroundService(intent);
                            } else {
                                getActivity().startService(intent);
                            }

                            Intent startNeGp = new Intent(getActivity(), LocationAndDataServiceWithTimer.class);
                            getActivity().startService(startNeGp);
                        }

                    } else {
                        Toast.makeText(getActivity(), "Failed Due to " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception ex) {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure( Call< LoginDetails > call, Throwable t) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                Log.e("TAG", t.toString());
            }
        });
    }

    public void saveLoginNotification(final LoginDetailsNotificationManagers md) {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Saving Details..");
        dialog.setCancelable(false);
        dialog.show();
        LoginNotificationAPI apiService = Util.getClient().create( LoginNotificationAPI.class);
        Call< LoginDetailsNotificationManagers > call = apiService.saveLoginNotification(md);
        call.enqueue(new Callback< LoginDetailsNotificationManagers >() {
            @Override
            public void onResponse( Call< LoginDetailsNotificationManagers > call, Response< LoginDetailsNotificationManagers > response) {
                try {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201) {
                        LoginDetailsNotificationManagers s = response.body();
                        if (s != null) {
                            s.setEmployeeId(md.getManagerId());
                            s.setManagerId(md.getEmployeeId());
                            s.setSenderId( Constants.SENDER_ID);
                            s.setServerId( Constants.SERVER_ID);
                            sendLoginNotification(s);
                        }

                    } else {
                        Toast.makeText(getActivity(), "Failed Due to " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception ex) {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure( Call< LoginDetailsNotificationManagers > call, Throwable t) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                Log.e("TAG", t.toString());
            }
        });
    }

    public void sendLoginNotification(final LoginDetailsNotificationManagers md) {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Sending Details..");
        dialog.setCancelable(false);
        dialog.show();
        LoginNotificationAPI apiService = Util.getClient().create( LoginNotificationAPI.class);
        Call<ArrayList<String>> call = apiService.sendLoginNotification(md);
        call.enqueue(new Callback<ArrayList<String>>() {
            @Override
            public void onResponse(Call<ArrayList<String>> call, Response<ArrayList<String>> response) {
                try {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201) {

                    } else {
                        Toast.makeText(getActivity(), "Failed Due to " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception ex) {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<String>> call, Throwable t) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                Log.e("TAG", t.toString());
            }
        });
    }

    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
        this.mMap.setMaxZoomPreference(24.0f);
    }

    @SuppressLint ("SetTextI18n")
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if ( ActivityCompat.checkSelfPermission( Objects.requireNonNull( getActivity( ) ) , Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission( getActivity( ) , Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            return;
        }

        currentLocation = LocationServices.FusedLocationApi.getLastLocation(mLocationClient);
        ArrayList<String> appNames = new ArrayList<>();
        if (currentLocation != null) {
            if(getActivity().getContentResolver()!=null){
                if(Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION).equals("0")){
                    if ( TrackGPS.isMockLocationOn( currentLocation , getActivity( ) ) ) {
                        appNames.addAll( TrackGPS.listofApps( getActivity( ) ) );
                    }
                }
            }

            if ( appNames.size( ) != 0 ) {
                latitude = 0;
                longitude = 0;
                latLong.setText( "Fake Location found!" );

            } else {
                getLoginDetails();

                latitude = currentLocation.getLatitude( );
                longitude = currentLocation.getLongitude( );

                if ( firstTime ) {
                    LatLng master = new LatLng( latitude , longitude );
                    String address = null;
                    try {
                        address = getAddress( master );
                    } catch ( Exception e ) {
                        e.printStackTrace( );
                    }

                    latLong.setText( address );
                    centreMapOnLocationWithLatLng( master , "" + PreferenceHandler.getInstance( getActivity( ) ).getUserFullName( ) );
                    firstTime = false;
                }
                startLocationUpdates( );
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    protected void startLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        if ( ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getActivity(), "Enable Permissions", Toast.LENGTH_LONG).show();
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mLocationClient, mLocationRequest, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        ArrayList<String> appNames = new ArrayList<>();
        if ( location != null && mContext != null ) {
            if ( Objects.requireNonNull( getActivity( ) ).getContentResolver( ) != null ) {
                if(Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION).equals("0")){
                    if ( TrackGPS.isMockLocationOn( location , getActivity( ) ) ) {
                        appNames.addAll( TrackGPS.listofApps( getActivity( ) ) );
                    }
                }
            }

            if ( appNames.size( ) != 0 ) {
                latitude = 0;
                longitude = 0;

            }else{
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                if ( firstTime ) {

                    LatLng master = new LatLng( latitude , longitude );
                    String address = null;
                    try {
                        address = getAddress( master );
                    } catch ( Exception e ) {
                        e.printStackTrace( );
                    }
                    latLong.setText(address);
                    centreMapOnLocationWithLatLng(master,""+ PreferenceHandler.getInstance(getActivity()).getUserFullName());
                    firstTime =false;
                }
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    public void onResume() {
        super.onResume();
        if (!checkPlayServices()) {
            Toast.makeText(getActivity(), "Please install Google Play Services", Toast.LENGTH_SHORT).show();
        }
    }
/*
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }*/

    public void setUserVisibleHint(boolean z) {
        super.setUserVisibleHint(z);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = (EmployeeNewMainScreen ) context;
    }
    private String getAddress(LatLng latLng) {
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        String result = null;
        try {
            List<Address> addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
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
        MeetingsAPI apiService = Util.getClient().create( MeetingsAPI.class);
        Call<ArrayList< Meetings >> call = apiService.getMeetingsByEmployeeId(PreferenceHandler.getInstance(getActivity()).getUserId());
        call.enqueue(new Callback<ArrayList< Meetings >>() {
            @Override
            public void onResponse( Call<ArrayList< Meetings >> call, Response<ArrayList< Meetings >> response) {
                int statusCode = response.code();
                if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {
                    ArrayList< Meetings > list = response.body();
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
                    }

                }else {
                    Toast.makeText(getActivity(), "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure( Call<ArrayList< Meetings >> call, Throwable t) {
                // Log error here since request failed

                Log.e("TAG", t.toString());
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
                View views = inflater.inflate(R.layout.activity_meeting_add_with_sign_screen, null);
                builder.setView(views);
                final Button  mSave = views.findViewById(R.id.save);
                mSave.setText(option);
                final  EditText mDetails = views.findViewById(R.id.meeting_remarks);
                final TextInputEditText mClientName = views.findViewById(R.id.client_name);
                final TextInputEditText mClientMobile = views.findViewById(R.id.client_contact_number);
                final TextInputEditText  mClientMail = views.findViewById(R.id.client_contact_email);
                final TextInputEditText mPurpose = views.findViewById(R.id.purpose_meeting);

                final CheckBox mGetSign = views.findViewById(R.id.get_sign_check);
                final CheckBox mTakeImage = views.findViewById(R.id.get_image_check);
                final LinearLayout mTakeImageLay = views.findViewById(R.id.selfie_lay);
                final LinearLayout mGetSignLay = views.findViewById(R.id.sign_lay);
                mImageView = views.findViewById(R.id.selfie_pic);
                customerSpinner = views.findViewById(R.id.customer_spinner_adpter);
                ClientNameLayout =  views.findViewById(R.id.client_name_layout);

                getCustomers(PreferenceHandler.getInstance(getActivity()).getCompanyId());

                mGetSignLay.setVisibility(View.GONE);
                mTakeImageLay.setVisibility(View.GONE);

                final AlertDialog dialog = builder.create();
                dialog.show();
                dialog.setCanceledOnTouchOutside(false);

                customerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if(customerArrayList!=null && customerArrayList.size()!=0){
                            if(customerArrayList.get(position).getCustomerName()!=null && customerArrayList.get(position).getCustomerName().equalsIgnoreCase("Others")) {
                                mClientMobile.setText("");
                                mClientName.setText("");
                                mClientMail.setText("");
                                ClientNameLayout.setVisibility(View.VISIBLE);

                            }
                            else {
                                mClientMobile.setText(""+customerArrayList.get(position).getCustomerMobile());
                                mClientName.setText(""+customerArrayList.get(position).getCustomerName());
                                mClientMail.setText(""+customerArrayList.get(position).getCustomerEmail());
                                clientId = customerArrayList.get(position).getCustomerId();
                                ClientNameLayout.setVisibility(View.GONE);

                            }
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                mSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String client = mClientName.getText().toString();
                        String purpose = mPurpose.getText().toString();
                        String detail = mDetails.getText().toString();
                        String mobile = mClientMobile.getText().toString();
                        String email = mClientMail.getText().toString();
                        String customer = customerSpinner.getSelectedItem().toString();

                        if(client==null||client.isEmpty()){
                            Toast.makeText(getActivity(), "Please mention client name", Toast.LENGTH_SHORT).show();
                        }else if(purpose==null||purpose.isEmpty()){
                            Toast.makeText(getActivity(), "Please mention purpose of meeting", Toast.LENGTH_SHORT).show();
                        }else if(detail==null||detail.isEmpty()){
                            Toast.makeText(getActivity(), "Please mention remarks about meeting", Toast.LENGTH_SHORT).show();
                        }else{
                            if(locationCheck()){
                                ArrayList<String> appNames = new ArrayList<>();
                                if(currentLocation!=null) {
                                    if(Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION).equals("0")){
                                        if(gps.isMockLocationOn(currentLocation,getActivity())){
                                            appNames.addAll(gps.listofApps(getActivity()));
                                        }
                                    }

                                    if(appNames!=null&&appNames.size()!=0){

                                    }else{
                                        latitude = currentLocation.getLatitude();
                                        longitude = currentLocation.getLongitude();

                                        LatLng masters = new LatLng(latitude, longitude);
                                        String addresss = null;
                                        try {
                                            addresss = getAddress(masters);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                                        SimpleDateFormat sdt = new SimpleDateFormat("MMM dd,yyyy hh:mm a");

                                        LatLng master = new LatLng(latitude,longitude);
                                        String address = null;
                                        try {
                                            address = getAddress(master);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                        loginDetails = new Meetings ();
                                        loginDetails.setEmployeeId(PreferenceHandler.getInstance(getActivity()).getUserId());
                                        loginDetails.setStartLatitude(""+latitude);
                                        loginDetails.setStartLongitude(""+longitude);
                                        loginDetails.setStartLocation(""+address);
                                        loginDetails.setStartTime(""+sdt.format(new Date()));

                                        loginDetails.setMeetingDate(""+sdf.format(new Date()));
                                        loginDetails.setMeetingAgenda(purpose);
                                        loginDetails.setMeetingDetails(detail);
                                        loginDetails.setStatus("In Meeting");

                                        if(customer!=null&&!customer.equalsIgnoreCase("Others")){

                                            if(customerArrayList!=null&&customerArrayList.size()!=0)
                                                loginDetails.setCustomerId(clientId);
                                        }

                                        methodAdd = false;

                                        String contact = "";

                                        if(email!=null&&!email.isEmpty()){
                                            contact = contact+"%"+email;
                                        }

                                        if(mobile!=null&&!mobile.isEmpty()){
                                            contact = contact+"%"+mobile;
                                        }

                                        if(contact!=null&&!contact.isEmpty()){
                                            loginDetails.setMeetingPersonDetails(client+""+contact);
                                        }else{
                                            loginDetails.setMeetingPersonDetails(client);
                                        }

                                        try {

                                            md = new MeetingDetailsNotificationManagers ();
                                            md.setTitle("Meeting Details from "+ PreferenceHandler.getInstance(getActivity()).getUserFullName());
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
                                            md.setMeetingComments(detail);

                                            if (mGetSign.isChecked()&&!mTakeImage.isChecked()){
                                                // Method to create Directory, if the Directory doesn't exists
                                                file = new File(DIRECTORY);
                                                if (!file.exists()) {
                                                    file.mkdir();
                                                }

                                                // Dialog Function
                                                dialogs = new Dialog(getActivity());
                                                // Removing the features of Normal Dialogs
                                                dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                dialogs.setContentView(R.layout.dialog_signature);
                                                dialogs.setCancelable(true);

                                                dialog_action(loginDetails,md,"null",dialog);

                                            }else if (mGetSign.isChecked()&&mTakeImage.isChecked()){

                                                file = new File(DIRECTORY);
                                                if (!file.exists()) {
                                                    file.mkdir();
                                                }

                                                // Dialog Function
                                                dialogs = new Dialog(getActivity());
                                                // Removing the features of Normal Dialogs
                                                dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                dialogs.setContentView(R.layout.dialog_signature);
                                                dialogs.setCancelable(true);

                                                dialog_action(loginDetails,md,"Selfie",dialog);

                                            }else{
                                                addMeeting(loginDetails,md);
                                            }
                                            dialog.dismiss();
                                            //   addMeeting(loginDetails,dialog,md);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            dialog.dismiss();
                                        }
                                    }

                                }else if(latitude!=0&&longitude!=0){


                                    LatLng masters = new LatLng(latitude, longitude);
                                    String addresss = null;
                                    try {
                                        addresss = getAddress(masters);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                                    SimpleDateFormat sdt = new SimpleDateFormat("MMM dd,yyyy hh:mm a");

                                    LatLng master = new LatLng(latitude,longitude);
                                    String address = null;
                                    try {
                                        address = getAddress(master);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    loginDetails = new Meetings ();
                                    loginDetails.setEmployeeId(PreferenceHandler.getInstance(getActivity()).getUserId());
                                    loginDetails.setStartLatitude(""+latitude);
                                    loginDetails.setStartLongitude(""+longitude);
                                    loginDetails.setStartLocation(""+address);
                                    loginDetails.setStartTime(""+sdt.format(new Date()));

                                    loginDetails.setMeetingDate(""+sdf.format(new Date()));
                                    loginDetails.setMeetingAgenda(purpose);
                                    loginDetails.setMeetingDetails(detail);
                                    loginDetails.setStatus("In Meeting");
                                    methodAdd = false;

                                    String contact = "";

                                    if(email!=null&&!email.isEmpty()){
                                        contact = contact+"%"+email;
                                    }

                                    if(mobile!=null&&!mobile.isEmpty()){
                                        contact = contact+"%"+mobile;
                                    }

                                    if(contact!=null&&!contact.isEmpty()){
                                        loginDetails.setMeetingPersonDetails(client+""+contact);
                                    }else{
                                        loginDetails.setMeetingPersonDetails(client);
                                    }

                                    try {

                                        md = new MeetingDetailsNotificationManagers ();
                                        md.setTitle("Meeting Details from "+ PreferenceHandler.getInstance(getActivity()).getUserFullName());
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
                                        md.setMeetingComments(detail);

                                        if (mGetSign.isChecked()&&!mTakeImage.isChecked()){
                                            // Method to create Directory, if the Directory doesn't exists
                                            file = new File(DIRECTORY);
                                            if (!file.exists()) {
                                                file.mkdir();
                                            }

                                            // Dialog Function
                                            dialogs = new Dialog(getActivity());
                                            // Removing the features of Normal Dialogs
                                            dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                            dialogs.setContentView(R.layout.dialog_signature);
                                            dialogs.setCancelable(true);

                                            dialog_action(loginDetails,md,"null",dialog);

                                        }else if (mGetSign.isChecked()&&mTakeImage.isChecked()){

                                            file = new File(DIRECTORY);
                                            if (!file.exists()) {
                                                file.mkdir();
                                            }

                                            // Dialog Function
                                            dialogs = new Dialog(getActivity());
                                            // Removing the features of Normal Dialogs
                                            dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                            dialogs.setContentView(R.layout.dialog_signature);
                                            dialogs.setCancelable(true);

                                            dialog_action(loginDetails,md,"Selfie",dialog);

                                        }else{
                                            addMeeting(loginDetails,md);
                                        }

                                        dialog.dismiss();

                                        //   addMeeting(loginDetails,dialog,md);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        dialog.dismiss();
                                    }
                                }
                            }
                        }
                    }
                });
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void addMeeting( final Meetings loginDetails, final MeetingDetailsNotificationManagers md) {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Saving Details..");
        dialog.setCancelable(false);
        dialog.show();
        MeetingsAPI apiService = Util.getClient().create( MeetingsAPI.class);
        Call< Meetings > call = apiService.addMeeting(loginDetails);
        call.enqueue(new Callback< Meetings >() {
            @Override
            public void onResponse( @NotNull Call< Meetings > call,  Response< Meetings > response) {
                try {
                    if(dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201) {
                        Meetings s = response.body();
                        if(s!=null){
                            if(dialogs!=null){
                                dialogs.dismiss();
                            }
                            md.setMeetingsId(s.getMeetingsId());
                            Toast.makeText(getActivity(), "You Checked in", Toast.LENGTH_SHORT).show();

                            PreferenceHandler.getInstance(getActivity()).setMeetingId(s.getMeetingsId());
                            PreferenceHandler.getInstance(getActivity()).setMeetingLoginStatus("Login");
                            saveMeetingNotification(md);
                            punchOutTextMeeting.setText("Meeting-Out");
                            punchInTextMeeting.setText(""+s.getStartTime());
                            punchInMeeting.setEnabled(false);
                            punchOutMeeting.setEnabled(true);
                        }

                    }else {
                        Toast.makeText(getActivity(), "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception ex) {
                    if(dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure( Call< Meetings > call, Throwable t) {
                if(dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                Log.e("TAG", t.toString());
            }
        });
    }

    public void getMeetings(final int id){
        new ThreadExecuter ().execute( new Runnable() {
            @Override
            public void run() {
                final MeetingsAPI subCategoryAPI = Util.getClient().create( MeetingsAPI.class);
                Call< Meetings > getProf = subCategoryAPI.getMeetingById(id);
                getProf.enqueue(new Callback< Meetings >() {
                    @Override
                    public void onResponse( Call< Meetings > call, Response< Meetings > response) {
                        if (response.code() == 200||response.code() == 201||response.code() == 204) {
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
                                        View views = inflater.inflate(R.layout.activity_meeting_add_with_sign_screen, null);
                                        builder.setView(views);
                                        final Button  mSave = views.findViewById(R.id.save);
                                        mSave.setText(option);
                                        final  EditText mDetails = views.findViewById(R.id.meeting_remarks);
                                        final  LinearLayout mSpinnerLay = views.findViewById(R.id.spinner_lay);
                                        final TextInputEditText mClientName = views.findViewById(R.id.client_name);
                                        final TextInputEditText mClientMobile = views.findViewById(R.id.client_contact_number);
                                        final TextInputEditText  mClientMail = views.findViewById(R.id.client_contact_email);
                                        final TextInputEditText mPurpose = views.findViewById(R.id.purpose_meeting);
                                        final CheckBox mGetSign = views.findViewById(R.id.get_sign_check);
                                        final CheckBox mTakeImage = views.findViewById(R.id.get_image_check);
                                        final ImageView mImageView = views.findViewById(R.id.selfie_pic);
                                        customerSpinner = views.findViewById(R.id.customer_spinner_adpter);
                                        ClientNameLayout =  views.findViewById(R.id.client_name_layout);
                                        mSpinnerLay.setVisibility(View.GONE);
                                        ClientNameLayout.setVisibility ( View.VISIBLE);

                                        mDetails.setText(""+dto.getMeetingDetails());
                                        methodAdd = true;
                                        if(dto.getMeetingPersonDetails().contains("%")){
                                            String person[] = dto.getMeetingPersonDetails().split("%");
                                            if(person.length==1){
                                                mClientName.setText(""+dto.getMeetingPersonDetails());
                                            }else if(person.length==2){
                                                mClientName.setText(""+person[0]);
                                                mClientMail.setText(""+person[1]);
                                            }else if(person.length==3){
                                                mClientName.setText(""+person[0]);
                                                mClientMail.setText(""+person[1]);
                                                mClientMobile.setText(""+person[2]);
                                            }

                                        }else{
                                            mClientName.setText(""+dto.getMeetingPersonDetails());
                                        }

                                        mPurpose.setText(""+dto.getMeetingAgenda());
                                        if(dto.getEndPlaceID()!=null&&!dto.getEndPlaceID().isEmpty()){
                                            Picasso.with(getActivity()).load(dto.getEndPlaceID()).placeholder(R.drawable.profile_image).error(R.drawable.no_image).into(mImageView);
                                        }

                                        final AlertDialog dialog = builder.create();
                                        dialog.show();
                                        dialog.setCanceledOnTouchOutside(true);
                                        customerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                            @Override
                                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                if(customerArrayList!=null && customerArrayList.size()!=0){
                                                    if(customerArrayList.get(position).getCustomerName()!=null && customerArrayList.get(position).getCustomerName().equalsIgnoreCase("Others")) {
                                                        mClientMobile.setText("");
                                                        mClientName.setText("");
                                                        mClientMail.setText("");
                                                        ClientNameLayout.setVisibility(View.VISIBLE);
                                                    }
                                                    else {
                                                        mClientMobile.setText(""+customerArrayList.get(position).getCustomerMobile());
                                                        mClientName.setText(""+customerArrayList.get(position).getCustomerName());
                                                        mClientMail.setText(""+customerArrayList.get(position).getCustomerEmail());
                                                        clientId = customerArrayList.get(position).getCustomerId();
                                                        ClientNameLayout.setVisibility(View.GONE);
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onNothingSelected(AdapterView<?> parent) {

                                            }
                                        });

                                        mSave.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                String client = mClientName.getText().toString();
                                                String purpose = mPurpose.getText().toString();
                                                String detail = mDetails.getText().toString();
                                                String mobile = mClientMobile.getText().toString();
                                                String email = mClientMail.getText().toString();

                                                if(client==null||client.isEmpty()){
                                                    Toast.makeText(getActivity(), "Please mention client name", Toast.LENGTH_SHORT).show();
                                                }else if(purpose==null||purpose.isEmpty()){
                                                    Toast.makeText(getActivity(), "Please mention purpose of meeting", Toast.LENGTH_SHORT).show();
                                                }else if(detail==null||detail.isEmpty()){
                                                    Toast.makeText(getActivity(), "Please mention remarks about meeting", Toast.LENGTH_SHORT).show();
                                                }else{
                                                    if(locationCheck()){
                                                        ArrayList<String> appNames = new ArrayList<>();
                                                        if(currentLocation!=null) {
                                                            if(Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION).equals("0")){
                                                                if(gps.isMockLocationOn(currentLocation,getActivity())){
                                                                    appNames.addAll(gps.listofApps(getActivity()));
                                                                }
                                                            }

                                                            if(appNames!=null&&appNames.size()!=0){

                                                            }else{

                                                                latitude = currentLocation.getLatitude();
                                                                longitude = currentLocation.getLongitude();

                                                                LatLng masters = new LatLng(latitude, longitude);
                                                                String addresss = null;
                                                                try {
                                                                    addresss = getAddress(masters);
                                                                } catch (Exception e) {
                                                                    e.printStackTrace();
                                                                }

                                                                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                                                                SimpleDateFormat sdt = new SimpleDateFormat("MMM dd,yyyy hh:mm a");

                                                                LatLng master = new LatLng(latitude,longitude);
                                                                String address = null;
                                                                try {
                                                                    address = getAddress(master);
                                                                } catch (Exception e) {
                                                                    e.printStackTrace();
                                                                }

                                                                loginDetails = dto;
                                                                loginDetails.setEmployeeId(PreferenceHandler.getInstance(getActivity()).getUserId());

                                                                loginDetails.setEndLatitude(""+latitude);
                                                                loginDetails.setEndLongitude(""+longitude);
                                                                loginDetails.setEndLocation(""+address);
                                                                loginDetails.setEndTime(""+sdt.format(new Date()));
                                                                loginDetails.setMeetingDate(""+sdf.format(new Date()));
                                                                loginDetails.setMeetingAgenda(purpose);
                                                                loginDetails.setMeetingDetails(detail);
                                                                loginDetails.setStatus("Completed");

                                                                String contact = "";

                                                                if(email!=null&&!email.isEmpty()){
                                                                    contact = contact+"%"+email;
                                                                }

                                                                if(mobile!=null&&!mobile.isEmpty()){
                                                                    contact = contact+"%"+mobile;
                                                                }

                                                                if(contact!=null&&!contact.isEmpty()){
                                                                    loginDetails.setMeetingPersonDetails(client+""+contact);
                                                                }else{
                                                                    loginDetails.setMeetingPersonDetails(client);
                                                                }

                                                                try {

                                                                    md = new MeetingDetailsNotificationManagers ();
                                                                    md.setTitle("Meeting Details from "+ PreferenceHandler.getInstance(getActivity()).getUserFullName());
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
                                                                    md.setMeetingComments(detail);

                                                                    if (mGetSign.isChecked()&&!mTakeImage.isChecked()){
                                                                        // Method to create Directory, if the Directory doesn't exists
                                                                        file = new File(DIRECTORY);
                                                                        if (!file.exists()) {
                                                                            file.mkdir();
                                                                        }

                                                                        // Dialog Function
                                                                        dialogs = new Dialog(getActivity());
                                                                        // Removing the features of Normal Dialogs
                                                                        dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                                        dialogs.setContentView(R.layout.dialog_signature);
                                                                        dialogs.setCancelable(true);

                                                                        dialog_action(loginDetails,md,"null",dialog);

                                                                    }else if (!mGetSign.isChecked()&&mTakeImage.isChecked()){

                                                                        file = new File(DIRECTORY);
                                                                        if (!file.exists()) {
                                                                            file.mkdir();
                                                                        }

                                                                       /* // Dialog Function
                                                                        dialog = new Dialog(MeetingAddWithSignScreen.this);
                                                                        // Removing the features of Normal Dialogs
                                                                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                                        dialog.setContentView(R.layout.dialog_signature);
                                                                        dialog.setCancelable(true);*/

                                                                        dispatchTakePictureIntent();
                                                                        dialog.dismiss();

                                                                        //dialog_action(loginDetails,md,"Selfie");

                                                                    }else if (mGetSign.isChecked()&&mTakeImage.isChecked()){

                                                                        file = new File(DIRECTORY);
                                                                        if (!file.exists()) {
                                                                            file.mkdir();
                                                                        }

                                                                        // Dialog Function
                                                                        dialogs = new Dialog(getActivity());
                                                                        // Removing the features of Normal Dialogs
                                                                        dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                                        dialogs.setContentView(R.layout.dialog_signature);
                                                                        dialogs.setCancelable(true);

                                                                        dialog_action(loginDetails,md,"Selfie",dialog);

                                                                    }else{
                                                                        updateMeeting(loginDetails,md);
                                                                    }
                                                                    dialog.dismiss();

                                                                } catch (Exception e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }

                                                        }else if(latitude!=0&&longitude!=0){
                                                            /*  latitude = currentLocation.getLatitude();
                                                            longitude = currentLocation.getLongitude();*/

                                                            LatLng masters = new LatLng(latitude, longitude);
                                                            String addresss = null;
                                                            try {
                                                                addresss = getAddress(masters);
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }

                                                           /* latLong.setText(addresss);
                                                            centreMapOnLocationWithLatLng(masters, "" + PreferenceHandler.getInstance(getActivity()).getUserFullName());
*/

                                                            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                                                            SimpleDateFormat sdt = new SimpleDateFormat("MMM dd,yyyy hh:mm a");

                                                            LatLng master = new LatLng(latitude,longitude);
                                                            String address = null;
                                                            try {
                                                                address = getAddress(master);
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }

                                                            loginDetails = dto;
                                                            loginDetails.setEmployeeId(PreferenceHandler.getInstance(getActivity()).getUserId());
                                                          /*  loginDetails.setStartLatitude(""+latitude);
                                                            loginDetails.setStartLongitude(""+longitude);
                                                            loginDetails.setStartLocation(""+address);
                                                            loginDetails.setStartTime(""+sdt.format(new Date()));*/
                                                            loginDetails.setEndLatitude(""+latitude);
                                                            loginDetails.setEndLongitude(""+longitude);
                                                            loginDetails.setEndLocation(""+address);
                                                            loginDetails.setEndTime(""+sdt.format(new Date()));
                                                            loginDetails.setMeetingDate(""+sdf.format(new Date()));
                                                            loginDetails.setMeetingAgenda(purpose);
                                                            loginDetails.setMeetingDetails(detail);
                                                            loginDetails.setStatus("Completed");

                                                            String contact = "";

                                                            if(email!=null&&!email.isEmpty()){
                                                                contact = contact+"%"+email;
                                                            }

                                                            if(mobile!=null&&!mobile.isEmpty()){
                                                                contact = contact+"%"+mobile;
                                                            }

                                                            if(contact!=null&&!contact.isEmpty()){
                                                                loginDetails.setMeetingPersonDetails(client+""+contact);
                                                            }else{
                                                                loginDetails.setMeetingPersonDetails(client);
                                                            }

                                                            try {

                                                                md = new MeetingDetailsNotificationManagers ();
                                                                md.setTitle("Meeting Details from "+ PreferenceHandler.getInstance(getActivity()).getUserFullName());
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
                                                                md.setMeetingComments(detail);

                                                                if (mGetSign.isChecked()&&!mTakeImage.isChecked()){
                                                                    // Method to create Directory, if the Directory doesn't exists
                                                                    file = new File(DIRECTORY);
                                                                    if (!file.exists()) {
                                                                        file.mkdir();
                                                                    }

                                                                    // Dialog Function
                                                                    dialogs = new Dialog(getActivity());
                                                                    // Removing the features of Normal Dialogs
                                                                    dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                                    dialogs.setContentView(R.layout.dialog_signature);
                                                                    dialogs.setCancelable(true);

                                                                    dialog_action(loginDetails,md,"null",dialog);

                                                                }else if (mGetSign.isChecked()&&mTakeImage.isChecked()){

                                                                    file = new File(DIRECTORY);
                                                                    if (!file.exists()) {
                                                                        file.mkdir();
                                                                    }

                                                                    // Dialog Function
                                                                    dialogs = new Dialog(getActivity());
                                                                    // Removing the features of Normal Dialogs
                                                                    dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                                    dialogs.setContentView(R.layout.dialog_signature);
                                                                    dialogs.setCancelable(true);

                                                                    dialog_action(loginDetails,md,"Selfie",dialog);

                                                                }else if (!mGetSign.isChecked()&&mTakeImage.isChecked()){

                                                                    file = new File(DIRECTORY);
                                                                    if (!file.exists()) {
                                                                        file.mkdir();
                                                                    }

                                                                       /* // Dialog Function
                                                                        dialog = new Dialog(MeetingAddWithSignScreen.this);
                                                                        // Removing the features of Normal Dialogs
                                                                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                                        dialog.setContentView(R.layout.dialog_signature);
                                                                        dialog.setCancelable(true);*/

                                                                    dispatchTakePictureIntent();
                                                                    dialog.dismiss();

                                                                    //dialog_action(loginDetails,md,"Selfie");

                                                                }else{
                                                                    updateMeeting(loginDetails,md);
                                                                }

                                                                dialog.dismiss();


                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        });
                                    }

                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure( Call< Meetings > call, Throwable t) {

                    }
                });
            }
        });
    }

    public void updateMeeting( final Meetings loginDetails, final MeetingDetailsNotificationManagers md) {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Saving Details..");
        dialog.setCancelable(false);
        dialog.show();
        MeetingsAPI apiService = Util.getClient().create( MeetingsAPI.class);
        Call< Meetings > call = apiService.updateMeetingById(loginDetails.getMeetingsId(),loginDetails);
        call.enqueue(new Callback< Meetings >() {
            @Override
            public void onResponse( Call< Meetings > call, Response< Meetings > response) {
                try {
                    if(dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201||response.code()==204) {
                        if(dialogs!=null){
                            dialogs.dismiss();
                        }

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
                catch (Exception ex) {
                    if(dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure( Call< Meetings > call, Throwable t) {
                if(dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                Log.e("TAG", t.toString());
            }
        });
    }

    public void saveMeetingNotification(final MeetingDetailsNotificationManagers md) {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Saving Details..");
        dialog.setCancelable(false);
        dialog.show();
        MeetingNotificationAPI apiService = Util.getClient().create( MeetingNotificationAPI.class);
        Call< MeetingDetailsNotificationManagers > call = apiService.saveMeetingNotification(md);
        call.enqueue(new Callback< MeetingDetailsNotificationManagers >() {
            @Override
            public void onResponse( Call< MeetingDetailsNotificationManagers > call, Response< MeetingDetailsNotificationManagers > response) {
                try {
                    if(dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201) {
                        MeetingDetailsNotificationManagers s = response.body();
                        if(s!=null){
                            MeetingDetailsNotificationManagers md = new MeetingDetailsNotificationManagers ();
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
                            md.setSenderId( Constants.SENDER_ID);
                            md.setServerId( Constants.SERVER_ID);
                            sendMeetingNotification(md);

                        }
                    }else {
                        Toast.makeText(getActivity(), "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception ex) {

                    if(dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure( Call< MeetingDetailsNotificationManagers > call, Throwable t) {
                if(dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                // Toast.makeText(getActivity(), "Failed due to Bad Internet Connection", Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());
            }
        });
    }

    public void sendMeetingNotification(final MeetingDetailsNotificationManagers md) {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Sending Details..");
        dialog.setCancelable(false);
        dialog.show();
        MeetingNotificationAPI apiService = Util.getClient().create( MeetingNotificationAPI.class);
        Call<ArrayList<String>> call = apiService.sendMeetingNotification(md);
        call.enqueue(new Callback<ArrayList<String>>() {
            @Override
            public void onResponse(Call<ArrayList<String>> call, Response<ArrayList<String>> response) {
                try {
                    if(dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201) {


                    }else {
                        Toast.makeText(getActivity(), "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception ex) {

                    if(dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<String>> call, Throwable t) {
                if(dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                Log.e("TAG", t.toString());
            }
        });
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(getActivity());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(getActivity(), resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else
                Toast.makeText(getActivity(), "Google Play Services not install", Toast.LENGTH_SHORT).show();

            return false;
        }
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mLocationClient != null) {
            mLocationClient.connect();
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        if ( mContext != null ) {
            ActivityManager manager = ( ActivityManager ) mContext.getSystemService( Context.ACTIVITY_SERVICE );
            for ( ActivityManager.RunningServiceInfo service : manager.getRunningServices( Integer.MAX_VALUE ) ) {
                if ( serviceClass.getName( ).equals( service.service.getClassName( ) ) ) {
                    Log.i( "isMyServiceRunning?" , true + "" );
                    return true;
                }
            }
            Log.i( "isMyServiceRunning?" , false + "" );
            return false;
        } else {
            return false;
        }
    }

    // Function for Digital Signature
    public void dialog_action( final Meetings loginDetails, final MeetingDetailsNotificationManagers md, final String type, final AlertDialog alertDialog) {
        mContent = dialogs.findViewById(R.id.linearLayout);
        mSignature = new signature(getActivity(), null);
        mSignature.setBackgroundColor(Color.WHITE);
        // Dynamically generating Layout through java code
        mContent.addView(mSignature, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mClear = dialogs.findViewById(R.id.clear);
        mGetSigns = dialogs.findViewById(R.id.getsign);
        mGetSigns.setEnabled(false);
        mCancel = dialogs.findViewById(R.id.cancel);
        view = mContent;

        mClear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.v("log_tag", "Panel Cleared");
                mSignature.clear();
                mGetSigns.setEnabled(false);
            }
        });

        mGetSigns.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Log.v("log_tag", "Panel Saved");
                view.setDrawingCacheEnabled(true);
                mSignature.save(view, StoredPath,loginDetails,md,type,alertDialog);
                if(dialogs!=null){
                    dialogs.dismiss();
                }

                Toast.makeText(getActivity(), "Successfully Saved", Toast.LENGTH_SHORT).show();
                // Calling the same class
                //recreate();
            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.v("log_tag", "Panel Canceled");
                if(dialogs!=null){

                    dialogs.dismiss();
                }

                // Calling the same class
                getActivity().recreate();
            }
        });
        dialogs.show();
    }

    public class signature extends View {
        private static final float STROKE_WIDTH = 5f;
        private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        private Paint paint = new Paint();
        private Path path = new Path();

        private float lastTouchX;
        private float lastTouchY;
        private final RectF dirtyRect = new RectF();

        public signature(Context context, AttributeSet attrs) {
            super(context, attrs);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);
        }

        public void save( View v, String StoredPath, final Meetings loginDetails, final MeetingDetailsNotificationManagers md, final String type, final AlertDialog alertDialog) {
            Log.v("log_tag", "Width: " + v.getWidth());
            Log.v("log_tag", "Height: " + v.getHeight());
            if (bitmap == null) {
                bitmap = Bitmap.createBitmap(mContent.getWidth(), mContent.getHeight(), Bitmap.Config.RGB_565);
            }
            Canvas canvas = new Canvas(bitmap);
            try {
                // Output the file
                FileOutputStream mFileOutStream = new FileOutputStream(StoredPath);
                v.draw(canvas);

                // Convert the output file to Image such as .png
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, mFileOutStream);
                mFileOutStream.flush();
                mFileOutStream.close();

                File file = new File(StoredPath);

                if(file.length() <= 1*1024*1024) {
                    FileOutputStream out = null;
                    String[] filearray = StoredPath.split("/");
                    final String filename = getFilename(filearray[filearray.length-1]);

                    out = new FileOutputStream(filename);
                    Bitmap myBitmap = BitmapFactory.decodeFile(StoredPath);

                    //write the compressed bitmap at the field_icon specified by filename.
                    myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    uploadImage(filename,loginDetails,md,type);
                }
                else {
                    compressImage(StoredPath,loginDetails,md,type);
                }

            } catch (Exception e) {
                Log.v("log_tag", e.toString());
            }
        }

        public void clear() {
            path.reset();
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawPath(path, paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float eventX = event.getX();
            float eventY = event.getY();
            mGetSigns.setEnabled(true);

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(eventX, eventY);
                    lastTouchX = eventX;
                    lastTouchY = eventY;
                    return true;

                case MotionEvent.ACTION_MOVE:

                case MotionEvent.ACTION_UP:

                    resetDirtyRect(eventX, eventY);
                    int historySize = event.getHistorySize();
                    for (int i = 0; i < historySize; i++) {
                        float historicalX = event.getHistoricalX(i);
                        float historicalY = event.getHistoricalY(i);
                        expandDirtyRect(historicalX, historicalY);
                        path.lineTo(historicalX, historicalY);
                    }
                    path.lineTo(eventX, eventY);
                    break;

                default:
                    debug("Ignored touch event: " + event.toString());
                    return false;
            }

            invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                    (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

            lastTouchX = eventX;
            lastTouchY = eventY;

            return true;
        }

        private void debug(String string) {

            Log.v("log_tag", string);

        }

        private void expandDirtyRect(float historicalX, float historicalY) {
            if (historicalX < dirtyRect.left) {
                dirtyRect.left = historicalX;
            } else if (historicalX > dirtyRect.right) {
                dirtyRect.right = historicalX;
            }

            if (historicalY < dirtyRect.top) {
                dirtyRect.top = historicalY;
            } else if (historicalY > dirtyRect.bottom) {
                dirtyRect.bottom = historicalY;
            }
        }

        private void resetDirtyRect(float eventX, float eventY) {
            dirtyRect.left = Math.min(lastTouchX, eventX);
            dirtyRect.right = Math.max(lastTouchX, eventX);
            dirtyRect.top = Math.min(lastTouchY, eventY);
            dirtyRect.bottom = Math.max(lastTouchY, eventY);
        }
    }

    public String getFilename(String filePath) {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "MyFolder/Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        System.out.println("getFilePath = "+filePath);
        String uriSting;
        if(filePath.contains(".jpg")) {
            uriSting = (file.getAbsolutePath() + "/" + filePath);
        }
        else {
            uriSting = (file.getAbsolutePath() + "/" + filePath+".jpg" );
        }
        return uriSting;

    }

    private void uploadImage( final String filePath, final Meetings loginDetails, final MeetingDetailsNotificationManagers md, final String type) {
        final File file = new File(filePath);
        int size = 1*1024*1024;
        if(file != null) {
            if(file.length() > size) {
                System.out.println(file.length());
                compressImage(filePath,loginDetails,md,type);
            }
            else {
                final ProgressDialog dialog = new ProgressDialog(getActivity());
                dialog.setCancelable(false);
                dialog.setTitle("Uploading Image..");
                dialog.show();
                Log.d("Image Upload", "Filename " + file.getName());
                RequestBody mFile = RequestBody.create(MediaType.parse("image"), file);
                MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), mFile);
                RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());
                UploadApi uploadImage = Util.getClient().create( UploadApi.class);
                Call<String> fileUpload = uploadImage.uploadProfileImages(fileToUpload, filename);
                fileUpload.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if(dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        try {

                            if(type!=null&&type.equalsIgnoreCase("Selfie")){
                                if( Util.IMAGE_URL==null){
                                    loginDetails.setEndPlaceID( Constants.IMAGE_URL+ response.body());
                                }else{
                                    loginDetails.setEndPlaceID( Util.IMAGE_URL+ response.body());
                                }

                                dispatchTakePictureIntent();

                            }else if(type!=null&&type.equalsIgnoreCase("Done")){

                                if( Util.IMAGE_URL==null){
                                    loginDetails.setStartPlaceID( Constants.IMAGE_URL+ response.body());
                                }else{
                                    loginDetails.setStartPlaceID( Util.IMAGE_URL+ response.body());
                                }

                                if(methodAdd){
                                    updateMeeting(loginDetails,md);
                                }else{
                                    addMeeting(loginDetails,md);
                                }


                            }else{

                                if( Util.IMAGE_URL==null){
                                    loginDetails.setEndPlaceID( Constants.IMAGE_URL+ response.body());
                                }else{
                                    loginDetails.setEndPlaceID( Util.IMAGE_URL+ response.body());
                                }
                                if(methodAdd){
                                    updateMeeting(loginDetails,md);
                                }else{
                                    addMeeting(loginDetails,md);
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if(filePath.contains("MyFolder/Images")) {
                            file.delete();
                        }
                    }
                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        // Log.d("UpdateCate", "Error " + "Bad Internet Connection");
                    }
                });
            }
        }
    }

    public String compressImage( String filePath, final Meetings loginDetails, final MeetingDetailsNotificationManagers md, final String type) {

        //String filePath = getRealPathFromURI(imageUri);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = actualHeight/2;//2033.0f;
        float maxWidth = actualWidth/2;//1011.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String[] filearray = filePath.split("/");
        final String filename = getFilename(filearray[filearray.length-1]);
        try {
            out = new FileOutputStream(filename);
            //  write the compressed bitmap at the field_icon specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            uploadImage(filename,loginDetails,md,type);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filename;
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            saveSelfie(imageBitmap,StoredPathSelfie);
        }
    }

    public void saveSelfie(Bitmap bitmap, String StoredPath) {
        if (bitmap == null) {
            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
        }
        try {
            // Output the file
            FileOutputStream mFileOutStream = new FileOutputStream(StoredPath);
            // Convert the output file to Image such as .png
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, mFileOutStream);
            mFileOutStream.flush();
            mFileOutStream.close();

            File file = new File(StoredPath);

            if(file.length() <= 1*1024*1024)
            {
                FileOutputStream out = null;
                String[] filearray = StoredPath.split("/");
                final String filename = getFilename(filearray[filearray.length-1]);

                out = new FileOutputStream(filename);
                Bitmap myBitmap = BitmapFactory.decodeFile(StoredPath);

//          write the compressed bitmap at the field_icon specified by filename.
                myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

                uploadImage(filename,loginDetails,md,"Done");

                mImageView.setVisibility(View.VISIBLE);
                mImageView.setImageBitmap(bitmap);

            }
            else
            {
                compressImage(StoredPath,loginDetails,md,"Done");
            }

        } catch (Exception e) {
            Log.v("log_tag", e.toString());
        }

    }

    public void getCustomers(final int id) {
        customerArrayList = new ArrayList <> (  );
        final CustomerAPI orgApi = Util.getClient().create( CustomerAPI.class);
        Call<ArrayList< Customer >> getProf = orgApi.getCustomerByOrganizationId(id);
        getProf.enqueue(new Callback<ArrayList< Customer >>() {

            @Override
            public void onResponse( Call<ArrayList< Customer >> call, Response<ArrayList< Customer >> response) {
                if (response.code() == 200||response.code() == 201||response.code() == 204) {
                    customerArrayList = response.body();
                    if(customerArrayList!=null&&customerArrayList.size()!=0){
                        Customer customer = new Customer ();
                        customer.setCustomerName("Others");
                        customerArrayList.add(customer);
                        CustomerSpinnerAdapter adapter = new CustomerSpinnerAdapter (getActivity(),customerArrayList);
                        customerSpinner.setAdapter(adapter);
                    }
                    else {
                        Customer customer = new Customer ();
                        customer.setCustomerName("Others");
                        customerArrayList.add(customer);
                        CustomerSpinnerAdapter adapter = new CustomerSpinnerAdapter (getActivity(),customerArrayList);
                        customerSpinner.setAdapter(adapter);
                    }

                }else{
                    Customer customer = new Customer ();
                    customer.setCustomerName("Others");
                    customerArrayList.add(customer);
                    CustomerSpinnerAdapter adapter = new CustomerSpinnerAdapter (getActivity(),customerArrayList);
                    customerSpinner.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure( Call<ArrayList< Customer >> call, Throwable t) {
                Customer customer = new Customer ();
                customer.setCustomerName("Others");
                customerArrayList.add(customer);
                CustomerSpinnerAdapter adapter = new CustomerSpinnerAdapter (getActivity(),customerArrayList);
                customerSpinner.setAdapter(adapter);
            }
        });
    }

    public void getCustomersWithId(final int id,final int customerId) {
        customerArrayList = new ArrayList<>();
        final CustomerAPI orgApi = Util.getClient().create( CustomerAPI.class);
        Call<ArrayList< Customer >> getProf = orgApi.getCustomerByOrganizationId(id);
        getProf.enqueue(new Callback<ArrayList< Customer >>() {

            @Override
            public void onResponse( Call<ArrayList< Customer >> call, Response<ArrayList< Customer >> response) {
                if (response.code() == 200||response.code() == 201||response.code() == 204) {
                    customerArrayList = response.body();
                    if(customerArrayList!=null&&customerArrayList.size()!=0){
                        Customer customer = new Customer ();
                        customer.setCustomerName("Others");
                        customerArrayList.add(customer);
                        CustomerSpinnerAdapter adapter = new CustomerSpinnerAdapter (getActivity(),customerArrayList);
                        customerSpinner.setAdapter(adapter);
                        if(customerId==0){
                            customerSpinner.setSelection((customerArrayList.size()-1));
                        }else{
                            for (int i=0;i<customerArrayList.size();i++) {
                                if(customerArrayList.get(i).getCustomerId()==customerId){
                                    customerSpinner.setSelection(i);
                                    break;
                                }
                            }
                        }
                    }
                    else {
                        ClientNameLayout.setVisibility(View.VISIBLE);
                        customerSpinner.setVisibility(View.GONE);
                    }

                }else{
                    ClientNameLayout.setVisibility(View.VISIBLE);
                    customerSpinner.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure( Call<ArrayList< Customer >> call, Throwable t) {
                ClientNameLayout.setVisibility(View.VISIBLE);
                customerSpinner.setVisibility(View.GONE);
            }
        });
    }

    public void getShiftTimingById(final int id) {
        new ThreadExecuter ().execute( new Runnable() {
            @Override
            public void run() {
                final OrganizationTimingsAPI orgApi = Util.getClient().create( OrganizationTimingsAPI.class);
                Call< WorkingDay > getProf = orgApi.getOrganizationTimings(id);
                getProf.enqueue(new Callback< WorkingDay >() {

                    @Override
                    public void onResponse( Call< WorkingDay > call, Response< WorkingDay > response) { if (response.code() == 200||response.code() == 201||response.code() == 204) {
                        WorkingDay workingDay = response.body();
                        if(workingDay!=null){
                            Calendar calendar = Calendar.getInstance();
                            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                            String weekday = new DateFormatSymbols().getShortWeekdays()[dayOfWeek];

                            if(dayOfWeek==1){

                                if(workingDay.isSuday()){
                                    checkInTime = workingDay.getSundayCheckInTime();
                                }

                                if (workingDay.isMonday()) {
                                    nextCheckInTime = workingDay.getMondayCheckInTime();
                                }

                            }else if(dayOfWeek==2){

                                if(workingDay.isMonday()){
                                    checkInTime = workingDay.getMondayCheckInTime();
                                }

                                if (workingDay.isiSTuesday()) {
                                    nextCheckInTime = workingDay.getTuesdayCheckInTime();
                                }

                            }else if(dayOfWeek==3){

                                if(workingDay.isiSTuesday()){
                                    checkInTime = workingDay.getTuesdayCheckInTime();
                                }

                                if (workingDay.isWednesday()) {
                                    nextCheckInTime = workingDay.getWednesdayCheckInTime();
                                }

                            }else if(dayOfWeek==4){

                                if(workingDay.isWednesday()){
                                    checkInTime = workingDay.getWednesdayCheckInTime();
                                }

                            }else if(dayOfWeek==5){

                                if(workingDay.isThursday()){
                                    checkInTime = workingDay.getThursdayCheckInTime();
                                }

                                if (workingDay.isFriday()) {
                                    nextCheckInTime = workingDay.getFridayCheckInTime();
                                }

                            }else if(dayOfWeek==6){
                                if(workingDay.isFriday()){
                                    checkInTime = workingDay.getFridayCheckInTime();
                                }
                                if (workingDay.isSaturday()) {
                                    nextCheckInTime = workingDay.getSaturdayCheckInTime();
                                }

                            }else if(dayOfWeek==7){
                                if(workingDay.isSaturday()){
                                    checkInTime = workingDay.getSaturdayCheckInTime();
                                }

                                if (workingDay.isSuday()) {
                                    nextCheckInTime = workingDay.getSundayCheckInTime();
                                }
                            }

                            try {

                                if (checkInTime != null && !checkInTime.isEmpty() && nextCheckInTime != null && !nextCheckInTime.isEmpty()) {
                                    Date cit = new SimpleDateFormat("hh:mm a").parse(checkInTime);
                                    Date ncit = new SimpleDateFormat("hh:mm a").parse(nextCheckInTime);
                                    Date currentTime = new SimpleDateFormat("hh:mm a").parse(new SimpleDateFormat("hh:mm a").format(new Date()));
                                    boolean loginPut = PreferenceHandler.getInstance(getActivity()).isLoginPut();
                                    if (cit.getTime() > currentTime.getTime() && !loginPut) {
                                        Calendar alaramTime = Calendar.getInstance();
                                        int year = alaramTime.get( Calendar.YEAR );
                                        int month = alaramTime.get( Calendar.MONTH );
                                        int date = alaramTime.get( Calendar.DATE );
                                        alaramTime.setTime(cit);
                                        alaramTime.set( Calendar.YEAR , year );
                                        alaramTime.set( Calendar.MONTH , month );
                                        alaramTime.set( Calendar.DATE , date );
                                        alaramTime.add(Calendar.MINUTE, -10);

                                        Date minus10 = alaramTime.getTime();

                                        SimpleDateFormat dateFormatter2 = new SimpleDateFormat("MMM dd, yyyy, hh:mm a");
                                        System.out.println( "Date checj " + dateFormatter2.format( minus10 ) );

                                        //Create a new PendingIntent and add it to the AlarmManager
                                        Intent intent = new Intent(getActivity(), CheckInAlarmReceiverService.class);
                                        intent.putExtra("Time", checkInTime);
                                        intent.putExtra("NextTime", nextCheckInTime);
                                        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 12345, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                                        AlarmManager am = (AlarmManager) getActivity().getSystemService(Activity.ALARM_SERVICE);
                                        am.set(AlarmManager.RTC_WAKEUP, alaramTime.getTimeInMillis(), pendingIntent);

                                    } else {

                                        Calendar alaramTime = Calendar.getInstance();
                                        int year = alaramTime.get( Calendar.YEAR );
                                        int month = alaramTime.get( Calendar.MONTH );
                                        int date = alaramTime.get( Calendar.DATE );
                                        alaramTime.setTime(ncit);
                                        alaramTime.set( Calendar.YEAR , year );
                                        alaramTime.set( Calendar.MONTH , month );
                                        alaramTime.set( Calendar.DATE , date );
                                        alaramTime.add(Calendar.DAY_OF_YEAR, 1);
                                        alaramTime.add(Calendar.MINUTE, -10);

                                        //Create a new PendingIntent and add it to the AlarmManager
                                        Intent intent = new Intent(getActivity(), CheckInAlarmReceiverService.class);
                                        intent.putExtra("Time", checkInTime);
                                        intent.putExtra("NextTime", nextCheckInTime);
                                        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 12345, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                                        AlarmManager am = (AlarmManager) getActivity().getSystemService(Activity.ALARM_SERVICE);
                                        am.set(AlarmManager.RTC_WAKEUP, alaramTime.getTimeInMillis(), pendingIntent);

                                    }
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    }else{

                    }
                    }

                    @Override
                    public void onFailure( Call< WorkingDay > call, Throwable t) {

                    }
                });
            }
        });
    }
}

