package app.zingo.employeemanagements.utils.loginclass;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.maps.model.LatLng;
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
import app.zingo.employeemanagements.WebApi.LoginNotificationAPI;
import app.zingo.employeemanagements.WebApi.OrganizationTimingsAPI;
import app.zingo.employeemanagements.model.LoginDetails;
import app.zingo.employeemanagements.model.LoginDetailsNotificationManagers;
import app.zingo.employeemanagements.model.WorkingDay;
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
    private String checkInTime = "", nextCheckInTime = "";
    private int timingId = 0;
    public LoginCallbackClass(Context context){
        this.mContext = context;
        gps = new TrackGPS ( mContext);
        progressBarUtil = new ProgressBarUtil ( mContext );
    }

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String shift = PreferenceHandler.getInstance(mContext).getShiftName();
        if ( shift != null && !shift.isEmpty( ) ) {
            if ( TextUtils.isDigitsOnly( shift ) ) {
                try {
                    timingId = Integer.parseInt( shift );
                    getShiftTimingById( timingId );

                } catch ( Exception e ) {

                    e.printStackTrace( );
                }
            }
        }
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

    /*public void checkIn ( final String status, final String type, Location currentLocation) {
        try {
            String message = "Do you want to Check-In?";
            String option = "Check-In";
            final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle(message);
            builder.setPositiveButton(option, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
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
                                    if(timingId!=0&&checkInTime!=null&&!checkInTime.isEmpty()){
                                        try {
                                            Date curT = sdtT.parse(currentTime);
                                            Date offT = sdtT.parse(checkInTime);

                                            long diff = curT.getTime() - offT.getTime();

                                            if (diff > 0) {
                                                long diffMinutes = diff / (60 * 1000) % 60;
                                                long diffHours = diff / (60 * 60 * 1000) % 24;

                                                loginDetails.setIdleTime("100");
                                                title = "Late Login Details from " + PreferenceHandler.getInstance(mContext).getUserFullName();

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
                                            addLogin(loginDetails, builder.create(), md);
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
                            }

                        }else if(currentLocation.getLatitude ()!=0&&currentLocation.getLongitude ()!=0){
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
                                        addLogin(loginDetails, builder.create(), md);
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
    */

    private void addLogin ( LoginDetails loginDetails , AlertDialog alertDialog , LoginDetailsNotificationManagers md ) {
    }
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

    public void getShiftTimingById(final int id) {
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

            }else{

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
        Call < ArrayList<LoginDetailsNotificationManagers> > call = apiService.getNotificationByEmployeeId(PreferenceHandler.getInstance ( LoginCallbackClass.this ).getUserId ());
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
        LoginNotificationAPI apiService = Util.getClient().create( LoginNotificationAPI.class);
        Call<ArrayList<String>> call = apiService.sendLoginNotification(md);
        call.enqueue(new Callback<ArrayList<String>>() {
            @Override
            public void onResponse(Call<ArrayList<String>> call, Response<ArrayList<String>> response) {
                try {
                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201) {
                        progressBarUtil.hideProgress ();
                    } else {
                        Toast.makeText(mContext, "Failed Due to " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception ex) {
                    progressBarUtil.hideProgress ();
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<String>> call, Throwable t) {
                progressBarUtil.hideProgress ();
                Log.e("TAG", t.toString());
            }
        });
    }

}
