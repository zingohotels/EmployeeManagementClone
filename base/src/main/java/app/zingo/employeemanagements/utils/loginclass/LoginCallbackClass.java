package app.zingo.employeemanagements.utils.loginclass;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import app.zingo.employeemanagements.AlarmManager.LunchBreakAlarm;
import app.zingo.employeemanagements.AlarmManager.TeaBreakAlarm;
import app.zingo.employeemanagements.Service.CheckInAlarmReceiverService;
import app.zingo.employeemanagements.Service.LocationAndDataServiceWithTimer;
import app.zingo.employeemanagements.Service.LocationForegroundService;
import app.zingo.employeemanagements.WebApi.LoginDetailsAPI;
import app.zingo.employeemanagements.WebApi.LoginNotificationAPI;
import app.zingo.employeemanagements.WebApi.OrganizationBreakTimesAPI;
import app.zingo.employeemanagements.WebApi.OrganizationTimingsAPI;
import app.zingo.employeemanagements.adapter.BreakAdapter;
import app.zingo.employeemanagements.base.R;
import app.zingo.employeemanagements.model.Customer;
import app.zingo.employeemanagements.model.LoginDetails;
import app.zingo.employeemanagements.model.LoginDetailsNotificationManagers;
import app.zingo.employeemanagements.model.Organization;
import app.zingo.employeemanagements.model.OrganizationBreakTimes;
import app.zingo.employeemanagements.model.WorkingDay;
import app.zingo.employeemanagements.ui.Common.BreakTimeListScreen;
import app.zingo.employeemanagements.ui.newemployeedesign.ScannedQrScreen;
import app.zingo.employeemanagements.utils.Constants;
import app.zingo.employeemanagements.utils.PreferenceHandler;
import app.zingo.employeemanagements.utils.ProgressBarUtil;
import app.zingo.employeemanagements.utils.ThreadExecuter;
import app.zingo.employeemanagements.utils.TrackGPS;
import app.zingo.employeemanagements.utils.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginCallbackClass extends AppCompatActivity {
    private Context mContext;
    private AlarmManager alarmManager;
    private TrackGPS gps;
    private ProgressBarUtil progressBarUtil;
    private ArrayList<LoginDetailsNotificationManagers> notificationManagers;
    private String checkInTime = "", nextCheckInTime = "", breakTime = "";
    private int timingId = 0;
    private ArrayList< Customer > customerArrayList;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private LoginDetails loginDetails;

    public LoginCallbackClass(Context context){
        this.mContext = context;
        gps = new TrackGPS ( mContext);
        progressBarUtil = new ProgressBarUtil ( mContext );
    }

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void lunchBreak ( Location currentLocation , TextView lunchBreakText ) {
        //getLoginNotification();
        String masterloginStatus = PreferenceHandler.getInstance ( mContext ).getLoginStatus ( );
        if ( masterloginStatus.equals ( "Login" ) ) {
            String value = PreferenceHandler.getInstance ( mContext ).getLunchBreakStatus ( );
            if ( currentLocation != null && value != null && value.equalsIgnoreCase ( "true" ) ) {
                ArrayList < String > appNames = new ArrayList <> ( );
                if ( Settings.Secure.getString ( mContext.getContentResolver ( ) , Settings.Secure.ALLOW_MOCK_LOCATION ).equals ( "0" ) ) {
                    if ( gps.isMockLocationOn ( currentLocation , mContext ) ) {
                        appNames.addAll ( gps.listofApps ( mContext ) );
                    }
                }
                if ( appNames != null && appNames.size ( ) != 0 ) {
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder ( mContext );
                    builder.setTitle ( "Do you want  to do?" );
                    builder.setPositiveButton ( "End Lunch" , ( dialogInterface , i ) -> {
                        dialogInterface.dismiss ( );
                        SimpleDateFormat sdt = new SimpleDateFormat ( "MMM dd,yyyy hh:mm a" );
                        LoginDetailsNotificationManagers md = new LoginDetailsNotificationManagers ( );
                        md.setTitle ( "Break taken from " + PreferenceHandler.getInstance ( mContext ).getUserFullName ( ) );
                        md.setMessage ( "Break ended at " + "" + sdt.format ( new Date ( ) ) );
                        LatLng master = new LatLng ( currentLocation.getLatitude ( ) ,  currentLocation.getLongitude ( ) );
                        String address = getAddress ( master );
                        md.setLocation ( address );
                        md.setLatitude ( "" + currentLocation.getLatitude ( ) );
                        md.setLongitude ( "" +  currentLocation.getLongitude ( ) );
                        md.setLoginDate ( "" + sdt.format ( new Date ( ) ) );
                        md.setStatus ( "Lunch Break" );
                        md.setEmployeeId ( PreferenceHandler.getInstance ( mContext ).getUserId ( ) );
                        md.setManagerId ( PreferenceHandler.getInstance ( mContext ).getManagerId ( ) );
                        try {
                            PreferenceHandler.getInstance ( mContext ).setLunchBreakStatus ( "false" );
                            lunchBreakText.setText ( "Lunch Break" );
                            // stopAlarmManager();
                            saveLoginNotification ( md );
                        } catch ( Exception e ) {
                            e.printStackTrace ( );
                        }
                    } );
                    builder.setNegativeButton ( "Cancel" , ( dialogInterface , i ) -> dialogInterface.dismiss ( ) );
                    final AlertDialog dialog = builder.create ( );
                    dialog.show ( );
                }

            } else if ( currentLocation != null ) {
                ArrayList < String > appNames = new ArrayList <> ( );
                if ( Settings.Secure.getString ( mContext.getContentResolver ( ) , Settings.Secure.ALLOW_MOCK_LOCATION ).equals ( "0" ) ) {
                    if ( gps.isMockLocationOn ( currentLocation , mContext ) ) {
                        appNames.addAll ( gps.listofApps ( mContext ) );
                    }
                }
                if ( appNames != null && appNames.size ( ) != 0 ) {
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder ( mContext );
                    builder.setTitle ( "Do you want  to do?" );
                    builder.setPositiveButton ( "Start Lunch" , ( dialogInterface , i ) -> {
                        SimpleDateFormat sdt = new SimpleDateFormat ( "MMM dd,yyyy hh:mm a" );
                        LoginDetailsNotificationManagers md = new LoginDetailsNotificationManagers ( );
                        md.setTitle ( "Break taken from " + PreferenceHandler.getInstance ( mContext ).getUserFullName ( ) );
                        md.setMessage ( "Break taken at " + "" + sdt.format ( new Date ( ) ) );
                        LatLng master = new LatLng ( currentLocation.getLatitude ( ) , currentLocation.getLongitude ( ) );
                        String address = getAddress ( master );
                        md.setLocation ( address );
                        md.setLatitude ( "" + currentLocation.getLatitude ( ) );
                        md.setLongitude ( "" + currentLocation.getLongitude ( ) );
                        md.setLoginDate ( "" + sdt.format ( new Date ( ) ) );
                        md.setStatus ( "Lunch Break" );
                        md.setEmployeeId ( PreferenceHandler.getInstance ( mContext ).getUserId ( ) );
                        md.setManagerId ( PreferenceHandler.getInstance ( mContext ).getManagerId ( ) );
                        try {
                            PreferenceHandler.getInstance ( mContext ).setLunchBreakStatus ( "true" );
                            lunchBreakText.setText ( "" + new SimpleDateFormat ( "hh:mm a" ).format ( new Date ( ) ) );
                            Calendar calendar = Calendar.getInstance ( );
                            calendar.setTime ( new Date ( ) );
                            calendar.add ( Calendar.HOUR , 1 );
                            triggerAlarmManager ( calendar , "Lunch" );
                            saveLoginNotification ( md );
                        } catch ( Exception e ) {
                            e.printStackTrace ( );
                        }

                    });
                    builder.setNegativeButton ( "Cancel" , ( dialogInterface , i ) -> dialogInterface.dismiss ( ) );
                    final AlertDialog dialog = builder.create ( );
                    dialog.show ( );
                }
            }
        }else{
            Toast.makeText ( mContext , "First Check-in Master" , Toast.LENGTH_SHORT ).show ( );
        }
    }

    public void teaBreak ( Location currentLocation , TextView teaBreakText ) {
        // getLoginNotification();
        String masterloginStatus = PreferenceHandler.getInstance (mContext).getLoginStatus ( );
        if ( masterloginStatus.equals ( "Login" ) ) {
            String value = PreferenceHandler.getInstance ( mContext ).getTeaBreakStatus ( );
            String lunchvalue = PreferenceHandler.getInstance ( mContext ).getLunchBreakStatus ( );
            if ( currentLocation != null && value != null && value.equalsIgnoreCase ( "true" ) && ! lunchvalue.equalsIgnoreCase ( "true" ) ) {
                ArrayList < String > appNames = new ArrayList <> ( );
                if ( Settings.Secure.getString ( mContext.getContentResolver ( ) , Settings.Secure.ALLOW_MOCK_LOCATION ).equals ( "0" ) ) {
                    if ( gps.isMockLocationOn ( currentLocation , mContext ) ) {
                        appNames.addAll ( gps.listofApps ( mContext ) );
                    }
                }
                if ( appNames != null && appNames.size ( ) != 0 ) {
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder ( mContext );
                    builder.setTitle ( "Do you want  to do?" );
                    builder.setPositiveButton ( "End Break" , ( dialogInterface , i ) -> {
                        dialogInterface.dismiss ( );
                        SimpleDateFormat sdt = new SimpleDateFormat ( "MMM dd,yyyy hh:mm a" );
                        LoginDetailsNotificationManagers md = new LoginDetailsNotificationManagers ( );
                        md.setTitle ( "Break taken from " + PreferenceHandler.getInstance ( mContext ).getUserFullName ( ) );
                        md.setMessage ( "Break ended at " + "" + sdt.format ( new Date ( ) ) );
                        LatLng latLng = new LatLng ( currentLocation.getLatitude ( ) , currentLocation.getLongitude ( ) );
                        String address = getAddress ( latLng );
                        md.setLocation ( address );
                        md.setLatitude ( "" + currentLocation.getLatitude ( ) );
                        md.setLongitude ( "" + currentLocation.getLongitude ( ) );
                        md.setLoginDate ( "" + sdt.format ( new Date ( ) ) );
                        md.setStatus ( "Tea Break" );
                        md.setEmployeeId ( PreferenceHandler.getInstance ( mContext ).getUserId ( ) );
                        md.setManagerId ( PreferenceHandler.getInstance ( mContext ).getManagerId ( ) );
                        try {
                            PreferenceHandler.getInstance ( mContext ).setTeaBreakStatus ( "false" );
                            teaBreakText.setText ( "Tea Break" );
                            saveLoginNotification ( md );
                        } catch ( Exception e ) {
                            e.printStackTrace ( );
                        }
                    } );
                    builder.setNegativeButton ( "Cancel" , ( dialogInterface , i ) -> dialogInterface.dismiss ( ) );
                    final AlertDialog dialog = builder.create ( );
                    dialog.show ( );
                }

            } else if ( currentLocation != null && ! lunchvalue.equalsIgnoreCase ( "true" ) ) {
                ArrayList < String > appNames = new ArrayList <> ( );
                if ( Settings.Secure.getString ( mContext.getContentResolver ( ) , Settings.Secure.ALLOW_MOCK_LOCATION ).equals ( "0" ) ) {
                    if ( gps.isMockLocationOn ( currentLocation , mContext ) ) {
                        appNames.addAll ( gps.listofApps ( mContext ) );
                    }
                }
                if ( appNames != null && appNames.size ( ) != 0 ) {

                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder ( mContext );
                    builder.setTitle ( "Do you want  to do?" );
                    builder.setPositiveButton ( "Start Break" , ( dialogInterface , i ) -> {
                        SimpleDateFormat sdt = new SimpleDateFormat ( "MMM dd,yyyy hh:mm a" );
                        LoginDetailsNotificationManagers md = new LoginDetailsNotificationManagers ( );
                        md.setTitle ( "Break taken from " + PreferenceHandler.getInstance ( mContext ).getUserFullName ( ) );
                        md.setMessage ( "Break taken at " + "" + sdt.format ( new Date ( ) ) );
                        LatLng latLng = new LatLng ( currentLocation.getLatitude ( ) , currentLocation.getLongitude ( ) );
                        String address = getAddress ( latLng );
                        md.setLocation ( address );
                        md.setLatitude ( "" + currentLocation.getLatitude ( ) );
                        md.setLongitude ( "" + currentLocation.getLongitude ( ) );
                        md.setLoginDate ( "" + sdt.format ( new Date ( ) ) );
                        md.setStatus ( "Tea Break" );
                        md.setEmployeeId ( PreferenceHandler.getInstance ( mContext ).getUserId ( ) );
                        md.setManagerId ( PreferenceHandler.getInstance ( mContext ).getManagerId ( ) );
                        try {
                            PreferenceHandler.getInstance ( mContext ).setTeaBreakStatus ( "true" );
                            teaBreakText.setText ( new SimpleDateFormat ( "hh:mm a" ).format ( new Date ( ) ) );
                            Calendar calendar = Calendar.getInstance ( );
                            calendar.setTime ( new Date ( ) );
                            calendar.add ( Calendar.MINUTE , 15 );
                            triggerAlarmManager ( calendar , "Tea" );
                            saveLoginNotification ( md );
                        } catch ( Exception e ) {
                            e.printStackTrace ( );
                        }
                    } );
                    builder.setNegativeButton ( "Cancel" , ( dialogInterface , i ) -> dialogInterface.dismiss ( ) );
                    final AlertDialog dialog = builder.create ( );
                    dialog.show ( );
                }
            }
        }else{
            Toast.makeText ( mContext , "First Check-in Master" , Toast.LENGTH_SHORT ).show ( );
        }
    }

    public void punchIn ( final String loginStatus, final String type, Location currentLocation,View punchIn , View punchOut , TextView punchInText , TextView punchOutText) {
        String loginStatus1 = PreferenceHandler.getInstance(mContext).getLoginStatus();
        if (loginStatus1 != null && ! loginStatus1.isEmpty()) {
            if ( loginStatus1.equalsIgnoreCase("Logout")) {
                updatePunchIn(loginStatus, type, currentLocation, punchIn, punchOut, punchInText, punchOutText);
            }
        } else {

            updatePunchIn(loginStatus, type, currentLocation, punchIn, punchOut, punchInText, punchOutText);
        }
    }

    public void punchOut (final String loginStatus, final String type , Location currentLocation , View punchIn , View punchOut , TextView punchInText , TextView punchOutText ) {
        String status = PreferenceHandler.getInstance(mContext).getLoginStatus();
        String meetingStatus = PreferenceHandler.getInstance(mContext).getMeetingLoginStatus();
        if (status != null && !status.isEmpty()) {
            if (status.equalsIgnoreCase("Login")) {
                if (meetingStatus != null && meetingStatus.equalsIgnoreCase("Login")) {
                    Toast.makeText(mContext, "You are in some meeting .So Please checkout", Toast.LENGTH_SHORT).show();
                } else {
                    getLoginDetails ( PreferenceHandler.getInstance ( mContext ).getLoginId (), type, currentLocation, punchIn, punchOut, punchInText, punchOutText  );
                }
            }
        }
    }

    public void getLoginDetails(final int id,final String type, Location currentLocation,View punchIn , View punchOut , TextView punchInText , TextView punchOutText ) {
        final LoginDetailsAPI subCategoryAPI = Util.getClient().create( LoginDetailsAPI.class);
        Call< LoginDetails > getProf = subCategoryAPI.getLoginById(id);
        getProf.enqueue(new Callback< LoginDetails >() {
            @Override
            public void onResponse( Call< LoginDetails > call, Response< LoginDetails > response) {
                if (response.code() == 200 || response.code() == 201 || response.code() == 204) {
                    loginDetails = response.body();
                    updatePunchOut ( loginDetails,type,currentLocation, punchIn, punchOut, punchInText, punchOutText  );
                }
            }

            @Override
            public void onFailure( Call< LoginDetails > call, Throwable t) {
            }
        });
    }

    private void updatePunchIn ( String loginStatus , String type , Location currentLocation , View punchIn , View punchOut , TextView punchInText , TextView punchOutText ) {
        try {
            String message = "Do you want to Check-In ?";
            String option = "Check-In";
            final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle(message);
            builder.setPositiveButton(option, (dialogInterface, i) -> {
                if (locationCheck()) {
                    ArrayList<String> appNames = new ArrayList<>();
                    if(currentLocation!=null){
                        if(Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION).equals("0")){
                            if(gps.isMockLocationOn(currentLocation,mContext)){
                                appNames.addAll(gps.listofApps(mContext));
                            }
                        }

                        if(appNames!=null&&appNames.size()!=0){
                        }else{
                            String title = "Login Details from " + PreferenceHandler.getInstance(mContext).getUserFullName();

                            Location locationA = new Location("point A");
                            locationA.setLatitude(Double.parseDouble(PreferenceHandler.getInstance(mContext).getOrganizationLati()));
                            locationA.setLongitude(Double.parseDouble(PreferenceHandler.getInstance(mContext).getOrganizationLongi()));

                            Location locationB = new Location("point B");
                            locationB.setLatitude(currentLocation.getLatitude ());
                            locationB.setLongitude(currentLocation.getLongitude ());

                            float distance = locationA.distanceTo(locationB);

                            if (PreferenceHandler.getInstance(mContext).isLocationOn()) {
                                distance = 0;
                            }
                            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                            SimpleDateFormat sdt = new SimpleDateFormat("MMM dd,yyyy hh:mm a");
                            SimpleDateFormat sdtT = new SimpleDateFormat("hh:mm a");

                            LatLng master = new LatLng(currentLocation.getLatitude (), currentLocation.getLongitude ());
                            String address = null;
                            try {
                                address = getAddress(master);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            String currentTime = sdtT.format(new Date());
                            LoginDetails loginDetailsPunchIn = new LoginDetails ();

                            if (PreferenceHandler.getInstance(mContext).isDataOn()) {
                                loginDetailsPunchIn.setIdleTime("0");
                            } else {
                                if(timingId!=0&&checkInTime!=null&&!checkInTime.isEmpty()){
                                    try {
                                        Date curT = sdtT.parse(currentTime);
                                        Date offT = sdtT.parse(checkInTime);

                                        long diff = curT.getTime() - offT.getTime();

                                        if (diff > 0) {
                                            long diffMinutes = diff / (60 * 1000) % 60;
                                            long diffHours = diff / (60 * 60 * 1000) % 24;

                                            loginDetailsPunchIn.setIdleTime("100");
                                            title = "Late Login Details from " + PreferenceHandler.getInstance(mContext).getUserFullName();
                                            Toast.makeText(mContext, "You are late " + (int) diffHours + " Hours " + (int) diffMinutes + " mins ", Toast.LENGTH_SHORT).show();
                                        } else {
                                            loginDetailsPunchIn.setIdleTime("0");
                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            loginDetailsPunchIn.setEmployeeId(PreferenceHandler.getInstance(mContext).getUserId());
                            loginDetailsPunchIn.setLatitude("" + currentLocation.getLatitude ());
                            loginDetailsPunchIn.setLongitude("" + currentLocation.getLongitude ());
                            loginDetailsPunchIn.setLocation("" + address);
                            loginDetailsPunchIn.setLoginTime("" + sdt.format(new Date()));
                            loginDetailsPunchIn.setLoginDate("" + sdf.format(new Date()));
                            loginDetailsPunchIn.setLogOutTime("");

                            LoginDetailsNotificationManagers md = new LoginDetailsNotificationManagers ();
                            try {
                                md.setTitle(title);
                                md.setMessage("Log in at  " + "" + sdt.format(new Date()));
                                md.setLocation(address);
                                md.setLongitude("" + currentLocation.getLatitude ());
                                md.setLatitude("" + currentLocation.getLongitude ());
                                md.setLoginDate("" + sdt.format(new Date()));
                                md.setStatus("In meeting");
                                md.setEmployeeId(PreferenceHandler.getInstance(mContext).getUserId());
                                md.setManagerId(PreferenceHandler.getInstance(mContext).getManagerId());

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            if(type!=null&&type.equalsIgnoreCase("gps")){
                                float limit = PreferenceHandler.getInstance(mContext).getLocationLimit();
                                if (distance >= 0 && distance <= limit) {
                                    try {
                                        addLoginService(loginDetailsPunchIn, builder.create(), md, punchIn , punchOut , punchInText , punchOutText);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                } else {
                                    Toast.makeText(mContext, "You are far away " + distance + " meter from your office", Toast.LENGTH_SHORT).show();
                                }

                            }else if(type!=null&&type.equalsIgnoreCase("Qr")){
                                dialogInterface.dismiss();
                                Intent qr = new Intent(mContext, ScannedQrScreen.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("LoginDetails",loginDetailsPunchIn);
                                bundle.putSerializable("LoginNotification",md);
                                bundle.putString("Type","Check-in");
                                qr.putExtras(bundle);
                                mContext.startActivity(qr);
                            }
                        }
                    }

                    /*else if(currentLocation.getLatitude ()!=0&&currentLocation.getLongitude ()!=0){
                        LatLng masters = new LatLng(currentLocation.getLatitude (),currentLocation.getLongitude ());
                        String addresss = null;
                        try {
                            addresss = getAddress(masters);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        Location locationA = new Location("point A");

                        locationA.setLatitude(Double.parseDouble(PreferenceHandler.getInstance(mContext).getOrganizationLati()));
                        locationA.setLongitude(Double.parseDouble(PreferenceHandler.getInstance(mContext).getOrganizationLongi()));

                        Location locationB = new Location("point B");

                        locationB.setLatitude(currentLocation.getLatitude ());
                        locationB.setLongitude(currentLocation.getLongitude ());

                        float distance = locationA.distanceTo(locationB);

                        if (PreferenceHandler.getInstance(mContext).isLocationOn()) {
                            distance = 0;
                        }

                        // Toast.makeText(mContext, "distance "+distance, Toast.LENGTH_SHORT).show();
                        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                        SimpleDateFormat sdt = new SimpleDateFormat("MMM dd,yyyy hh:mm a");
                        SimpleDateFormat sdtT = new SimpleDateFormat("hh:mm a");

                        LatLng master = new LatLng(currentLocation.getLatitude (), currentLocation.getLongitude ());
                        String address = null;
                        try {
                            address = getAddress(master);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        String currentTime = sdtT.format(new Date());
                        LoginDetails loginDetails = new LoginDetails ();

                        if (PreferenceHandler.getInstance(mContext).isDataOn()) {
                            loginDetails.setIdleTime("0");
                        } else {
                            if (currentTime.equalsIgnoreCase(PreferenceHandler.getInstance(mContext).getCheckInTime())) {
                                loginDetails.setIdleTime("0");
                            } else {
                                try {
                                    Date curT = sdtT.parse(currentTime);
                                    Date offT = sdtT.parse(PreferenceHandler.getInstance(mContext).getCheckInTime());
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
                        loginDetails.setEmployeeId(PreferenceHandler.getInstance(mContext).getUserId());
                        loginDetails.setLatitude("" + currentLocation.getLatitude ());
                        loginDetails.setLongitude("" + currentLocation.getLongitude ());
                        loginDetails.setLocation("" + address);
                        loginDetails.setLoginTime("" + sdt.format(new Date()));
                        loginDetails.setLoginDate("" + sdf.format(new Date()));
                        loginDetails.setLogOutTime("");

                        LoginDetailsNotificationManagers md = new LoginDetailsNotificationManagers ();
                        try {
                            md.setTitle("Login Details from " + PreferenceHandler.getInstance(mContext).getUserFullName());
                            md.setMessage("Log in at  " + "" + sdt.format(new Date()));
                            md.setLocation(address);
                            md.setLongitude("" + currentLocation.getLatitude ());
                            md.setLatitude("" + currentLocation.getLongitude ());
                            md.setLoginDate("" + sdt.format(new Date()));
                            md.setStatus("In meeting");
                            md.setEmployeeId(PreferenceHandler.getInstance(mContext).getUserId());
                            md.setManagerId(PreferenceHandler.getInstance(mContext).getManagerId());

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if(type!=null&&type.equalsIgnoreCase("gps")){
                            float limit = PreferenceHandler.getInstance(mContext).getLocationLimit();
                            if (distance >= 0 && distance <= limit) {
                                try {
                                    addLogin(loginDetails, builder.create(), md , punchIn , punchOut , punchInText , punchOutText );
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Toast.makeText(mContext, "You are far away " + distance + " meter from your office", Toast.LENGTH_SHORT).show();
                            }
                        }else if(type!=null&&type.equalsIgnoreCase("Qr")){
                            dialogInterface.dismiss();
                            Intent qr = new Intent(mContext, ScannedQrScreen.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("LoginDetails",loginDetails);
                            bundle.putSerializable("LoginNotification",md);
                            bundle.putString("Type","Check-in");
                            qr.putExtras(bundle);
                            startActivity(qr);
                        }
                    }*/
                }
            });

            builder.setNegativeButton("Cancel", ( dialogInterface , i ) -> dialogInterface.dismiss() );

            final AlertDialog dialog = builder.create();
            dialog.show();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void updatePunchOut ( LoginDetails loginDetails, String type, Location currentLocation, View punchIn , View punchOut , TextView punchInText , TextView punchOutText  ) {
        if (loginDetails != null) {
            try {
                String message = "Login";
                String option = "Check-In";

                message = "Do you want to Check-Out?";
                option = "Check-Out";

                final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle(message);
                builder.setPositiveButton(option, ( dialogInterface , i ) -> {

                    ArrayList<String> appNames = new ArrayList<>();
                    if(currentLocation!=null) {
                        if(Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION).equals("0")){
                            if(gps.isMockLocationOn(currentLocation,mContext)){
                                appNames.addAll(gps.listofApps(mContext));
                            }
                        }

                        if(appNames!=null&&appNames.size()!=0){

                        }else{
                            SimpleDateFormat sdt = new SimpleDateFormat("MMM dd,yyyy hh:mm a");
                            LatLng master = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                            String address = null;
                            try {
                                address = getAddress(master);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            LoginDetails loginDetailsPunchOut = loginDetails;
                            loginDetailsPunchOut.setEmployeeId(PreferenceHandler.getInstance(mContext).getUserId());
                            loginDetailsPunchOut.setLogoutLatitude ("" + currentLocation.getLatitude ());
                            loginDetailsPunchOut.setLogoutLongitude ("" + currentLocation.getLongitude());
                            loginDetailsPunchOut.setLogoutLocation ("" + address);
                            loginDetailsPunchOut.setLogOutTime("" + sdt.format(new Date()));
                            try {
                                LoginDetailsNotificationManagers md = new LoginDetailsNotificationManagers ();
                                md.setTitle("Login Details from " + PreferenceHandler.getInstance(mContext).getUserFullName());
                                md.setMessage("Log out at  " + "" + sdt.format(new Date()));
                                md.setLatitude ("" + currentLocation.getLatitude ());
                                md.setLongitude ("" + currentLocation.getLongitude());
                                md.setLocation ("" + address);
                                md.setLoginDate("" + sdt.format(new Date()));
                                md.setStatus("Log out");
                                md.setEmployeeId(PreferenceHandler.getInstance(mContext).getUserId());
                                md.setManagerId(PreferenceHandler.getInstance(mContext).getManagerId());
                                md.setLoginDetailsId(loginDetails.getLoginDetailsId());

                                if(type!=null&&type.equalsIgnoreCase("gps")){
                                    updateLoginService(loginDetailsPunchOut, builder.create(), md, punchIn , punchOut , punchInText , punchOutText);
                                    Intent intent = new Intent(mContext, LocationForegroundService.class);
                                    intent.setAction( LocationForegroundService.ACTION_STOP_FOREGROUND_SERVICE);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        mContext.startForegroundService(intent);
                                    } else {
                                        mContext.startService(intent);
                                    }
                                    Intent stopNeGp = new Intent(mContext, LocationAndDataServiceWithTimer.class);
                                    mContext.stopService(stopNeGp);

                                }else if(type!=null&&type.equalsIgnoreCase("Qr")){
                                    builder.create().dismiss();
                                    Intent qr = new Intent(mContext, ScannedQrScreen.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("LoginDetails",loginDetailsPunchOut);
                                    bundle.putSerializable("LoginNotification",md);
                                    bundle.putString("Type","Check-out");
                                    qr.putExtras(bundle);
                                    mContext.startActivity(qr);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    /*else if(currentLocation.getLatitude ()!=0&&currentLocation.getLongitude()!=0){
                        SimpleDateFormat sdt = new SimpleDateFormat("MMM dd,yyyy hh:mm a");
                        LatLng master = new LatLng(currentLocation.getLatitude (), currentLocation.getLongitude());
                        String address = null;
                        try {
                            address = getAddress(master);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        LoginDetails loginDetailsPunchOut = loginDetails;
                        loginDetailsPunchOut.setEmployeeId(PreferenceHandler.getInstance(mContext).getUserId());
                        loginDetailsPunchOut.setLogoutLatitude ("" + currentLocation.getLatitude ());
                        loginDetailsPunchOut.setLogoutLongitude ("" + currentLocation.getLongitude());
                        loginDetailsPunchOut.setLogoutLocation ("" + address);
                        loginDetailsPunchOut.setLogOutTime("" + sdt.format(new Date()));
                        try {
                            LoginDetailsNotificationManagers md = new LoginDetailsNotificationManagers ();
                            md.setTitle("Login Details from " + PreferenceHandler.getInstance(mContext).getUserFullName());
                            md.setMessage("Log out at  " + "" + sdt.format(new Date()));
                            md.setLatitude ("" + currentLocation.getLatitude ());
                            md.setLongitude ("" + currentLocation.getLongitude());
                            md.setLocation ("" + address);
                            md.setLoginDate("" + sdt.format(new Date()));
                            md.setStatus("Log out");
                            md.setEmployeeId(PreferenceHandler.getInstance(mContext).getUserId());
                            md.setManagerId(PreferenceHandler.getInstance(mContext).getManagerId());
                            md.setLoginDetailsId(loginDetails.getLoginDetailsId());

                            if(type!=null&&type.equalsIgnoreCase("gps")){
                                updateLogin(loginDetailsPunchOut, builder.create(), md);
                                Intent intent = new Intent(mContext, LocationForegroundService.class);
                                intent.setAction( LocationForegroundService.ACTION_STOP_FOREGROUND_SERVICE);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    mContext.startForegroundService(intent);
                                } else {
                                    mContext.startService(intent);
                                }
                                Intent stopNeGp = new Intent(mContext, LocationAndDataServiceWithTimer.class);
                                mContext.stopService(stopNeGp);

                            }else if(type!=null&&type.equalsIgnoreCase("Qr")){
                                builder.create().dismiss();
                                Intent qr = new Intent(mContext, ScannedQrScreen.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("LoginDetails",loginDetailsPunchOut);
                                bundle.putSerializable("LoginNotification",md);
                                bundle.putString("Type","Check-out");
                                qr.putExtras(bundle);
                                startActivity(qr);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }*/
                });

                builder.setNegativeButton("Cancel", ( dialogInterface , i ) -> dialogInterface.dismiss() );
                final AlertDialog dialog = builder.create();
                dialog.show();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void addLoginService ( final LoginDetails loginDetails , final AlertDialog dialogs , final LoginDetailsNotificationManagers md , View punchIn , View punchOut , TextView punchInText , TextView punchOutText ) {
        progressBarUtil.showProgress ( "Please wait..." );
        LoginDetailsAPI apiService = Util.getClient().create( LoginDetailsAPI.class);
        Call< LoginDetails > call = apiService.addLogin(loginDetails);
        call.enqueue(new Callback< LoginDetails >() {
            @Override
            public void onResponse( Call< LoginDetails > call, Response< LoginDetails > response) {
                try {
                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201) {
                        progressBarUtil.hideProgress ();
                        LoginDetails s = response.body();
                        if (s != null) {
                            punchIn.setEnabled(false);
                            punchOut.setEnabled(true);

                            if(dialogs!=null){
                                dialogs.dismiss();
                            }
                            md.setLoginDetailsId(s.getLoginDetailsId());
                            saveLoginNotification(md);
                            Toast.makeText(mContext, "You Logged in", Toast.LENGTH_SHORT).show();
                            PreferenceHandler.getInstance(mContext).setLoginId(s.getLoginDetailsId());
                            String date = s.getLoginDate();

                            if (date != null && !date.isEmpty()) {
                                if (date.contains("T")) {
                                    String logins[] = date.split("T");
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                    punchInText.setText("" + s.getLoginTime());
                                    punchOutText.setText("Check-Out" );
                                }
                            } else {
                                punchInText.setText("" + s.getLoginTime());
                                punchOutText.setText("Check-Out" );
                            }
                            PreferenceHandler.getInstance(mContext).setLoginStatus("Login");
                            PreferenceHandler.getInstance(mContext).setLoginTime("" + s.getLoginTime());

                            Intent intent = new Intent(mContext, LocationForegroundService.class);
                            intent.setAction( LocationForegroundService.ACTION_START_FOREGROUND_SERVICE);
                            if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                mContext.startForegroundService(intent);
                            } else {
                                mContext.startService(intent);
                            }

                            Intent startNeGp = new Intent(mContext, LocationAndDataServiceWithTimer.class);
                            mContext.startService(startNeGp);
                        }

                    } else {
                        Toast.makeText(mContext, "Failed Due to " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception ex) {
                    progressBarUtil.hideProgress ();
                    ex.printStackTrace();
                }
            }
            @Override
            public void onFailure( Call< LoginDetails > call, Throwable t) {
                progressBarUtil.hideProgress ();
                Log.e("TAG", t.toString());
            }
        });
    }

    public void updateLoginService( final LoginDetails loginDetails, final AlertDialog dialogs, final LoginDetailsNotificationManagers md, View punchIn , View punchOut , TextView punchInText , TextView punchOutText) {
        progressBarUtil.showProgress ( "Please Wait..." );
        LoginDetailsAPI apiService = Util.getClient().create( LoginDetailsAPI.class);
        Call< LoginDetails > call = apiService.updateLoginById(loginDetails.getLoginDetailsId(), loginDetails);
        call.enqueue(new Callback< LoginDetails >() {
            @Override
            public void onResponse( Call< LoginDetails > call, Response< LoginDetails > response) {
                try {
                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201 || response.code() == 204) {
                        progressBarUtil.hideProgress ();
                        saveLoginNotification(md);
                        updateLoginData ( punchIn, punchOut, punchInText, punchOutText  );

                    } else {
                        Toast.makeText(mContext, "Failed Due to " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception ex) {
                    progressBarUtil.hideProgress ();
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure( Call< LoginDetails > call, Throwable t) {
                progressBarUtil.hideProgress ();
                Log.e("TAG", t.toString());
            }
        });
    }

    private void updateLoginData ( View punchIn , View punchOut , TextView punchInText , TextView punchOutText ) {
        Toast.makeText(mContext, "You logged out", Toast.LENGTH_SHORT).show();
        PreferenceHandler.getInstance(mContext).setLoginId(0);
        PreferenceHandler.getInstance(mContext).setFar(false);

        punchInText.setText("Check in");
        PreferenceHandler.getInstance(mContext).setLoginTime("");
        PreferenceHandler.getInstance(mContext).setLoginStatus("Logout");

        punchIn.setEnabled(true);
        punchOut.setEnabled(false);

        String date = loginDetails.getLoginDate();

        if (date != null && !date.isEmpty()) {

            if (date.contains("T")) {

                String logins[] = date.split("T");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                punchOutText.setText("" + loginDetails.getLogOutTime());
            }
        } else {
            punchOutText.setText("" + loginDetails.getLogOutTime());
        }

    }

    /*


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

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View views = inflater.inflate(R.layout.activity_meeting_add_with_sign_screen, null);
                builder.setView(views);
                final Button mSave = views.findViewById(R.id.save);
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
                ImageView mImageView = views.findViewById ( R.id.selfie_pic );
                Spinner customerSpinner = views.findViewById(R.id.customer_spinner_adpter);
                LinearLayout ClientNameLayout =  views.findViewById(R.id.client_name_layout);

                getCustomers(PreferenceHandler.getInstance(mContext).getCompanyId());

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
                            Toast.makeText(mContext, "Please mention client name", Toast.LENGTH_SHORT).show();
                        }else if(purpose==null||purpose.isEmpty()){
                            Toast.makeText(mContext, "Please mention purpose of meeting", Toast.LENGTH_SHORT).show();
                        }else if(detail==null||detail.isEmpty()){
                            Toast.makeText(mContext, "Please mention remarks about meeting", Toast.LENGTH_SHORT).show();
                        }else{
                            if(locationCheck()){
                                ArrayList<String> appNames = new ArrayList<>();
                                if(currentLocation!=null) {
                                    if(Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION).equals("0")){
                                        if(gps.isMockLocationOn(currentLocation,mContext)){
                                            appNames.addAll(gps.listofApps(mContext));
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
                                        loginDetails.setEmployeeId(PreferenceHandler.getInstance(mContext).getUserId());
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
                                            md.setTitle("Meeting Details from "+ PreferenceHandler.getInstance(mContext).getUserFullName());
                                            md.setMessage("Meeting with "+client+" for "+purpose);
                                            md.setLocation(address);
                                            md.setLongitude(""+longitude);
                                            md.setLatitude(""+latitude);
                                            md.setMeetingDate(""+sdt.format(new Date()));
                                            md.setStatus("In meeting");
                                            md.setEmployeeId(PreferenceHandler.getInstance(mContext).getUserId());
                                            md.setManagerId(PreferenceHandler.getInstance(mContext).getManagerId());
                                            md.setMeetingPerson(client);
                                            md.setMeetingsDetails(purpose);
                                            md.setMeetingComments(detail);

                                            if (mGetSign.isChecked()&&!mTakeImage.isChecked()){
                                                // Method to create Directory, if the Directory doesn't exists
                                                file = new File (DIRECTORY);
                                                if (!file.exists()) {
                                                    file.mkdir();
                                                }

                                                // Dialog Function
                                                dialogs = new Dialog(mContext);
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
                                                dialogs = new Dialog(mContext);
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
                                    loginDetails.setEmployeeId(PreferenceHandler.getInstance(mContext).getUserId());
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
                                        md.setTitle("Meeting Details from "+ PreferenceHandler.getInstance(mContext).getUserFullName());
                                        md.setMessage("Meeting with "+client+" for "+purpose);
                                        md.setLocation(address);
                                        md.setLongitude(""+longitude);
                                        md.setLatitude(""+latitude);
                                        md.setMeetingDate(""+sdt.format(new Date()));
                                        md.setStatus("In meeting");
                                        md.setEmployeeId(PreferenceHandler.getInstance(mContext).getUserId());
                                        md.setManagerId(PreferenceHandler.getInstance(mContext).getManagerId());
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
                                            dialogs = new Dialog(mContext);
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
                                            dialogs = new Dialog(mContext);
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
                        CustomerSpinnerAdapter adapter = new CustomerSpinnerAdapter (mContext,customerArrayList);
                        customerSpinner.setAdapter(adapter);
                    }
                    else {
                        Customer customer = new Customer ();
                        customer.setCustomerName("Others");
                        customerArrayList.add(customer);
                        CustomerSpinnerAdapter adapter = new CustomerSpinnerAdapter (mContext,customerArrayList);
                        customerSpinner.setAdapter(adapter);
                    }

                }else{
                    Customer customer = new Customer ();
                    customer.setCustomerName("Others");
                    customerArrayList.add(customer);
                    CustomerSpinnerAdapter adapter = new CustomerSpinnerAdapter (mContext,customerArrayList);
                    customerSpinner.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure( Call<ArrayList< Customer >> call, Throwable t) {
                Customer customer = new Customer ();
                customer.setCustomerName("Others");
                customerArrayList.add(customer);
                CustomerSpinnerAdapter adapter = new CustomerSpinnerAdapter (mContext,customerArrayList);
                customerSpinner.setAdapter(adapter);
            }
        });
    }

    public void addMeeting( final Meetings loginDetails, final MeetingDetailsNotificationManagers md) {
        final ProgressDialog dialog = new ProgressDialog(mContext);
        dialog.setMessage("Saving Details..");
        dialog.setCancelable(false);
        dialog.show();
        MeetingsAPI apiService = Util.getClient().create( MeetingsAPI.class);
        Call< Meetings > call = apiService.addMeeting(loginDetails);
        call.enqueue(new Callback< Meetings >() {
            @Override
            public void onResponse( @NotNull Call< Meetings > call, Response< Meetings > response) {
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
                            Toast.makeText(mContext, "You Checked in", Toast.LENGTH_SHORT).show();

                            PreferenceHandler.getInstance(mContext).setMeetingId(s.getMeetingsId());
                            PreferenceHandler.getInstance(mContext).setMeetingLoginStatus("Login");
                            saveMeetingNotification(md);
                            punchOutTextMeeting.setText("Meeting-Out");
                            punchInTextMeeting.setText(""+s.getStartTime());
                            punchInMeeting.setEnabled(false);
                            punchOutMeeting.setEnabled(true);
                        }

                    }else {
                        Toast.makeText(mContext, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
                                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                        View views = inflater.inflate( R.layout.activity_meeting_add_with_sign_screen, null);
                                        builder.setView(views);
                                        final Button  mSave = views.findViewById(R.id.save);
                                        mSave.setText(option);
                                        final EditText mDetails = views.findViewById(R.id.meeting_remarks);
                                        final LinearLayout mSpinnerLay = views.findViewById(R.id.spinner_lay);
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
                                            Picasso.with(mContext).load(dto.getEndPlaceID()).placeholder(R.drawable.profile_image).error(R.drawable.no_image).into(mImageView);
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
                                                    Toast.makeText(mContext, "Please mention client name", Toast.LENGTH_SHORT).show();
                                                }else if(purpose==null||purpose.isEmpty()){
                                                    Toast.makeText(mContext, "Please mention purpose of meeting", Toast.LENGTH_SHORT).show();
                                                }else if(detail==null||detail.isEmpty()){
                                                    Toast.makeText(mContext, "Please mention remarks about meeting", Toast.LENGTH_SHORT).show();
                                                }else{
                                                    if(locationCheck()){
                                                        ArrayList<String> appNames = new ArrayList<>();
                                                        if(currentLocation!=null) {
                                                            if(Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION).equals("0")){
                                                                if(gps.isMockLocationOn(currentLocation,mContext)){
                                                                    appNames.addAll(gps.listofApps(mContext));
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
                                                                loginDetails.setEmployeeId(PreferenceHandler.getInstance(mContext).getUserId());

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
                                                                    md.setTitle("Meeting Details from "+ PreferenceHandler.getInstance(mContext).getUserFullName());
                                                                    md.setMessage("Meeting with "+client+" for "+purpose);
                                                                    md.setLocation(address);
                                                                    md.setLongitude(""+longitude);
                                                                    md.setLatitude(""+latitude);
                                                                    md.setMeetingDate(""+sdt.format(new Date()));
                                                                    md.setStatus("Completed");
                                                                    md.setEmployeeId(PreferenceHandler.getInstance(mContext).getUserId());
                                                                    md.setManagerId(PreferenceHandler.getInstance(mContext).getManagerId());
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
                                                                        dialogs = new Dialog (mContext);
                                                                        // Removing the features of Normal Dialogs
                                                                        dialogs.requestWindowFeature( Window.FEATURE_NO_TITLE);
                                                                        dialogs.setContentView(R.layout.dialog_signature);
                                                                        dialogs.setCancelable(true);

                                                                        dialog_action(loginDetails,md,"null",dialog);

                                                                    }else if (!mGetSign.isChecked()&&mTakeImage.isChecked()){

                                                                        file = new File(DIRECTORY);
                                                                        if (!file.exists()) {
                                                                            file.mkdir();
                                                                        }

                                                                        // Dialog Function
                                                                        dialog = new Dialog( MeetingAddWithSignScreen.this);
                                                                        // Removing the features of Normal Dialogs
                                                                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                                        dialog.setContentView(R.layout.dialog_signature);
                                                                        dialog.setCancelable(true);

                                                                        dispatchTakePictureIntent();
                                                                        dialog.dismiss();

                                                                        //dialog_action(loginDetails,md,"Selfie");

                                                                    }else if (mGetSign.isChecked()&&mTakeImage.isChecked()){

                                                                        file = new File(DIRECTORY);
                                                                        if (!file.exists()) {
                                                                            file.mkdir();
                                                                        }

                                                                        // Dialog Function
                                                                        dialogs = new Dialog(mContext);
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
                                                              latitude = currentLocation.getLatitude();
                                                            longitude = currentLocation.getLongitude();

                                                            LatLng masters = new LatLng(latitude, longitude);
                                                            String addresss = null;
                                                            try {
                                                                addresss = getAddress(masters);
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }

                                                            latLong.setText(addresss);
                                                            centreMapOnLocationWithLatLng(masters, "" + PreferenceHandler.getInstance(mContext).getUserFullName());


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
                                                            loginDetails.setEmployeeId(PreferenceHandler.getInstance(mContext).getUserId());
                                                            loginDetails.setStartLatitude(""+latitude);
                                                            loginDetails.setStartLongitude(""+longitude);
                                                            loginDetails.setStartLocation(""+address);
                                                            loginDetails.setStartTime(""+sdt.format(new Date()));
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
                                                                md.setTitle("Meeting Details from "+ PreferenceHandler.getInstance(mContext).getUserFullName());
                                                                md.setMessage("Meeting with "+client+" for "+purpose);
                                                                md.setLocation(address);
                                                                md.setLongitude(""+longitude);
                                                                md.setLatitude(""+latitude);
                                                                md.setMeetingDate(""+sdt.format(new Date()));
                                                                md.setStatus("Completed");
                                                                md.setEmployeeId(PreferenceHandler.getInstance(mContext).getUserId());
                                                                md.setManagerId(PreferenceHandler.getInstance(mContext).getManagerId());
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
                                                                    dialogs = new Dialog(mContext);
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
                                                                    dialogs = new Dialog(mContext);
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

                                                                        // Dialog Function
                                                                        dialog = new Dialog(MeetingAddWithSignScreen.this);
                                                                        // Removing the features of Normal Dialogs
                                                                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                                        dialog.setContentView(R.layout.dialog_signature);
                                                                        dialog.setCancelable(true);

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
        final ProgressDialog dialog = new ProgressDialog(mContext);
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
                        Toast.makeText(mContext, "You Checked out", Toast.LENGTH_SHORT).show();
                        PreferenceHandler.getInstance(mContext).setMeetingId(0);
                        getMeetingDetails();
                        punchInMeeting.setEnabled(true);
                        punchOutMeeting.setEnabled(false);
                        punchInTextMeeting.setText("Meeting-In");
                        PreferenceHandler.getInstance(mContext).setMeetingLoginStatus("Logout");

                    }else {
                        Toast.makeText(mContext, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
    }*/

    private boolean locationCheck ( ) {
        return true;
    }

    public void triggerAlarmManager (final Calendar firingCal, String string) {
        if(string!=null&&string.equalsIgnoreCase ("Lunch")) {
            Intent myIntent = new Intent (mContext, LunchBreakAlarm.class);
            myIntent.putExtra ("type", string);
            PendingIntent pendingIntent = PendingIntent.getBroadcast (mContext, 0, myIntent, 0);
            alarmManager = ( AlarmManager ) mContext.getSystemService (ALARM_SERVICE);

            Calendar currentCal = Calendar.getInstance ();

            long intendedTime = firingCal.getTimeInMillis ();
            long currentTime = currentCal.getTimeInMillis ();

            if (intendedTime >= currentTime) {
                alarmManager.setRepeating (AlarmManager.RTC, intendedTime, AlarmManager.INTERVAL_DAY, pendingIntent);
            } else {
                firingCal.add (Calendar.HOUR, 1);
                intendedTime = firingCal.getTimeInMillis ();
                alarmManager.setRepeating (AlarmManager.RTC, intendedTime, AlarmManager.INTERVAL_DAY, pendingIntent);
            }
        }
        else if(string!=null&&string.equalsIgnoreCase ("Tea")) {
            Intent myIntent = new Intent (mContext, TeaBreakAlarm.class);
            myIntent.putExtra ("type", string);
            PendingIntent pendingIntent = PendingIntent.getBroadcast (mContext, 0, myIntent, 0);
            alarmManager = (AlarmManager) mContext.getSystemService (ALARM_SERVICE);

            Calendar currentCal = Calendar.getInstance ();
            long intendedTime = firingCal.getTimeInMillis ();
            long currentTime = currentCal.getTimeInMillis ();

            if (intendedTime >= currentTime) {
                alarmManager.setRepeating( AlarmManager.RTC , intendedTime , AlarmManager.INTERVAL_FIFTEEN_MINUTES , pendingIntent );
            } else {
                firingCal.add( Calendar.MINUTE , 5 );
                intendedTime = firingCal.getTimeInMillis ();
                alarmManager.setRepeating (AlarmManager.RTC, intendedTime, AlarmManager.INTERVAL_DAY, pendingIntent);
            }
        }
    }

    public String getAddress ( LatLng latLng ) {
        Geocoder geocoder = new Geocoder( mContext, Locale.getDefault());
        String result = null;
        try {
            List < Address > addressList = geocoder.getFromLocation( latLng.latitude, latLng.longitude, 1);
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
        } catch ( IOException e) {
            Log.e("MapLocation", "Unable connect to Geocoder", e);
            return result;
        }
    }

    public void breakTiming(final int id) {
        final OrganizationBreakTimesAPI orgApi = Util.getClient().create( OrganizationBreakTimesAPI.class);
        Call<ArrayList< OrganizationBreakTimes >> getProf = orgApi.getBreaksByOrgId(id);
        getProf.enqueue(new Callback<ArrayList<OrganizationBreakTimes>>() {
            @Override
            public void onResponse(Call<ArrayList<OrganizationBreakTimes>> call, Response<ArrayList<OrganizationBreakTimes>> response) {
                if (response.code() == 200||response.code() == 201||response.code() == 204) {
                    ArrayList<OrganizationBreakTimes> breakTimes = response.body();
                    if(breakTimes!=null&&breakTimes.size()!=0){
                        Gson gson = new Gson ();
                        String json = gson.toJson ( breakTimes );
                        System.out.println ( "Suree BreakTiming : "+json );
                    }

                }else{
                    Toast.makeText( mContext, "Something went wrong"+response.code (), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<OrganizationBreakTimes>> call, Throwable t) {
                Toast.makeText( mContext, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void shiftTiming(final int id) {
        timingId = id;
        final OrganizationTimingsAPI orgApi = Util.getClient().create( OrganizationTimingsAPI.class);
        Call< WorkingDay > getProf = orgApi.getOrganizationTimings(id);
        getProf.enqueue(new Callback< WorkingDay >() {
            @Override
            public void onResponse( Call< WorkingDay > call, Response< WorkingDay > response) { if (response.code() == 200||response.code() == 201||response.code() == 204) {
                WorkingDay workingDay = response.body();
                if(workingDay!=null){
                    Calendar calendar = Calendar.getInstance();
                    int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                    String weekday = new DateFormatSymbols ().getShortWeekdays()[dayOfWeek];

                    switch (dayOfWeek) {
                        case Calendar.SUNDAY:
                            if(workingDay.isSuday){ checkInTime = workingDay.getSundayCheckInTime (); }
                            break;
                        case Calendar.MONDAY:
                            if(workingDay.isMonday){ checkInTime = workingDay.getMondayCheckInTime (); }
                            break;
                        case Calendar.TUESDAY:
                            if(workingDay.iSTuesday){ checkInTime = workingDay.getTuesdayCheckInTime (); }
                            break;
                        case Calendar.WEDNESDAY:
                            if(workingDay.isWednesday){ checkInTime = workingDay.getWednesdayCheckInTime (); }
                            break;
                        case Calendar.THURSDAY:
                            if(workingDay.isThursday){ checkInTime = workingDay.getThursdayCheckInTime (); }
                            break;
                        case Calendar.FRIDAY:
                            if(workingDay.isFriday){ checkInTime = workingDay.getFridayCheckInTime (); }
                            break;
                        case Calendar.SATURDAY:
                            if(workingDay.isSaturday){ checkInTime = workingDay.getSaturdayCheckInTime (); }
                            break;
                    }

                    try {
                        if (checkInTime != null && !checkInTime.isEmpty() && nextCheckInTime != null && !nextCheckInTime.isEmpty()) {
                            Date cit = new SimpleDateFormat("hh:mm a").parse(checkInTime);
                            Date ncit = new SimpleDateFormat("hh:mm a").parse(nextCheckInTime);
                            Date currentTime = new SimpleDateFormat("hh:mm a").parse(new SimpleDateFormat("hh:mm a").format(new Date()));
                            boolean loginPut = PreferenceHandler.getInstance(mContext).isLoginPut();
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
                                Intent intent = new Intent(mContext, CheckInAlarmReceiverService.class);
                                intent.putExtra("Time", checkInTime);
                                intent.putExtra("NextTime", nextCheckInTime);
                                PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 12345, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                                AlarmManager am = (AlarmManager) mContext.getSystemService( Activity.ALARM_SERVICE);
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
                                Intent intent = new Intent(mContext, CheckInAlarmReceiverService.class);
                                intent.putExtra("Time", checkInTime);
                                intent.putExtra("NextTime", nextCheckInTime);
                                PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 12345, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                                AlarmManager am = (AlarmManager) mContext.getSystemService(Activity.ALARM_SERVICE);
                                am.set(AlarmManager.RTC_WAKEUP, alaramTime.getTimeInMillis(), pendingIntent);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                 }
                }
            }

            @Override
            public void onFailure( Call< WorkingDay > call, Throwable t) {

            }
        });
    }

    public void getLoginNotification() {
        progressBarUtil.showProgress ( "Please Wait..." );
        LoginNotificationAPI apiService = Util.getClient().create( LoginNotificationAPI.class);
        Call < ArrayList<LoginDetailsNotificationManagers> > call = apiService.getNotificationByEmployeeId(PreferenceHandler.getInstance ( mContext ).getUserId ());
        call.enqueue(new Callback < ArrayList<LoginDetailsNotificationManagers> > () {
            @Override
            public void onResponse( Call< ArrayList<LoginDetailsNotificationManagers> > call, Response < ArrayList<LoginDetailsNotificationManagers> > response) {
                try {
                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201) {
                        progressBarUtil.hideProgress ();
                        notificationManagers = new ArrayList <> (  );
                        if(response.body ().size ()!=0) {
                            notificationManagers = response.body ( );
                        }

                    } else {
                        Toast.makeText(mContext, "Failed Due to " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception ex) {
                    progressBarUtil.hideProgress ();
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure( Call< ArrayList<LoginDetailsNotificationManagers> > call, Throwable t) {
                progressBarUtil.hideProgress ();
                Log.e("TAG", t.toString());
            }
        });
    }

    public void saveLoginNotification(final LoginDetailsNotificationManagers md) {
        progressBarUtil.showProgress ( "Please Wait..." );
        LoginNotificationAPI apiService = Util.getClient().create( LoginNotificationAPI.class);
        Call < LoginDetailsNotificationManagers > call = apiService.saveLoginNotification(md);
        call.enqueue(new Callback < LoginDetailsNotificationManagers > () {
            @Override
            public void onResponse( Call< LoginDetailsNotificationManagers > call, Response < LoginDetailsNotificationManagers > response) {
                try {
                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201) {
                        progressBarUtil.hideProgress ();
                        LoginDetailsNotificationManagers s = response.body();
                        if (s != null) {
                            s.setEmployeeId(md.getManagerId());
                            s.setManagerId(md.getEmployeeId());
                            s.setSenderId( Constants.SENDER_ID);
                            s.setServerId( Constants.SERVER_ID);
                            sendLoginNotification(s);
                        }

                    } else {
                        Toast.makeText(mContext, "Failed Due to " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception ex) {
                    progressBarUtil.hideProgress ();
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure( Call< LoginDetailsNotificationManagers > call, Throwable t) {
                progressBarUtil.hideProgress ();
                Log.e("TAG", t.toString());
            }
        });
    }

    public void sendLoginNotification(final LoginDetailsNotificationManagers md) {
        progressBarUtil.showProgress ( "Please Wait..." );
        LoginNotificationAPI apiService = Util.getClient ( ).create ( LoginNotificationAPI.class );
        Call < ArrayList < String > > call = apiService.sendLoginNotification ( md );
        call.enqueue ( new Callback < ArrayList < String > > ( ) {
            @Override
            public void onResponse ( Call < ArrayList < String > > call , Response < ArrayList < String > > response ) {
                try {
                    int statusCode = response.code ( );
                    if ( statusCode == 200 || statusCode == 201 ) {
                        progressBarUtil.hideProgress ( );
                    } else {
                        Toast.makeText ( mContext , "Failed Due to " + response.message ( ) , Toast.LENGTH_SHORT ).show ( );
                    }
                } catch ( Exception ex ) {
                    progressBarUtil.hideProgress ( );
                    ex.printStackTrace ( );
                }
            }
            @Override
            public void onFailure ( Call < ArrayList < String > > call , Throwable t ) {
                progressBarUtil.hideProgress ( );
                Log.e ( "TAG" , t.toString ( ) );
            }
        });
    }

    public static boolean isMockSettingsON(Context context) {
        // returns true if mock location enabled, false if not enabled.
        if (Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION).equals("0"))
            return false;
        else
            return true;
    }
}
