package app.zingo.employeemanagements.Service;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


import app.zingo.employeemanagements.model.LiveTracking;
import app.zingo.employeemanagements.ui.newemployeedesign.BreakPurpose;
import app.zingo.employeemanagements.utils.Const;
import app.zingo.employeemanagements.utils.PreferenceHandler;
import app.zingo.employeemanagements.utils.TrackGPS;
import app.zingo.employeemanagements.utils.Util;
import app.zingo.employeemanagements.WebApi.LiveTrackingAPI;
import app.zingo.employeemanagements.base.BuildConfig;
import app.zingo.employeemanagements.base.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationForegroundService extends Service {
    private static final String TAG_FOREGROUND_SERVICE = "FOREGROUND_SERVICE";
    public static final String ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE";
    public static final String ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE";
    boolean check = false;
    TrackGPS gps;
    TelephonyManager telephonyManager;

    public LocationForegroundService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive( Context context, Intent intent) {
            int level = intent.getIntExtra( BatteryManager.EXTRA_LEVEL, 0);
            Const.BATTERY_LEVEL = level;
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG_FOREGROUND_SERVICE, "My foreground service onCreate().");
        this.registerReceiver(this.mBatInfoReceiver, new IntentFilter (Intent.ACTION_BATTERY_CHANGED));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startMyOwnForeground() {
        String NOTIFICATION_CHANNEL_ID = "app.zingo.employeemanagements";
        String channelName = "Location Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Location Service Running")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            gps = new TrackGPS ( LocationForegroundService.this);
            switch (action) {
                case ACTION_START_FOREGROUND_SERVICE:
                    startForegroundService();
                    //Toast.makeText(getApplicationContext(), "Foreground service is started.", Toast.LENGTH_LONG).show();
                    break;
                case ACTION_STOP_FOREGROUND_SERVICE:
                    stopForegroundService();
                    //Toast.makeText(getApplicationContext(), "Foreground service is stopped.", Toast.LENGTH_LONG).show();
                    break;

            }
        }
        return START_STICKY;
    }

    /* Used to build and start foreground service. */
    private void startForegroundService() {
        Log.d(TAG_FOREGROUND_SERVICE, "Start foreground service.");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //triggerAlarmManager();
            String NOTIFICATION_CHANNEL_ID = "app.zingo.employeemanagements";
            String channelName = "Location Background Service";
            NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(chan);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
            Notification notification = notificationBuilder.setOngoing(true)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Location Service Running")
                    .setPriority(NotificationManager.IMPORTANCE_MIN)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .build();
            startForeground(2, notification);
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    // do your task here
                    locationPassing();
                }
            }, 10000, 15000);

            //Alarm
            if(PreferenceHandler.getInstance(getApplicationContext()).getUserId()!=0&& PreferenceHandler.getInstance(getApplicationContext()).getUserRoleUniqueID()!=2&& PreferenceHandler.getInstance(getApplicationContext()).getLoginStatus().equalsIgnoreCase("Login")){
                //triggerAlarmManager();
            }

        } else {

            // Create notification default intent.
            Intent intent = new Intent();
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

            //Above Android 8 create channel Id for notification
            String CHANNEL_ID = "" + 1;// The id of the channel.
            CharSequence name = "Zingo";// The user-visible name of the channel.
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel mChannel = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            }

            // Create notification builder.
            Notification.Builder builder = null;

            builder = new Notification.Builder(this)
                    .setContentTitle("Krony Location Service")
                    .setPriority(Notification.PRIORITY_MAX)
                    .setWhen(System.currentTimeMillis())
                    .setFullScreenIntent(pendingIntent, false)
                    .setSmallIcon(R.mipmap.ic_launcher);

            builder.setDefaults(Notification.DEFAULT_VIBRATE);
            builder.setLights(Color.YELLOW, 1000, 300);

            Notification notification = builder.build();

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                assert notificationManager != null;
                notificationManager.createNotificationChannel(mChannel);
            }
            // notificationManager.notify(m, builder.build());
            // Start foreground service.
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    // do your task here
                    locationPassing();
                }
            }, 10000, 15000);

            startForeground(1, notification);
        }
        //Alarm
        if(PreferenceHandler.getInstance(getApplicationContext()).getUserId()!=0&& PreferenceHandler.getInstance(getApplicationContext()).getUserRoleUniqueID()!=2&& PreferenceHandler.getInstance(getApplicationContext()).getLoginStatus().equalsIgnoreCase("Login")){
            // triggerAlarmManager();
        }
    }

    private void stopForegroundService() {
        Log.d(TAG_FOREGROUND_SERVICE, "Stop foreground service.");
        // Stop foreground service and remove the notification.
        stopForeground(true);
        // Stop the foreground service.
        stopSelf();
    }

    private void locationPassing(){

        try {
            telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            if(locationCheck()&& PreferenceHandler.getInstance(getApplicationContext()).getUserId()!=0&& PreferenceHandler.getInstance(getApplicationContext()).getUserRoleUniqueID()!=2&& PreferenceHandler.getInstance(getApplicationContext()).getLoginStatus().equalsIgnoreCase("Login")){
                if(gps.canGetLocation()) {
                    System.out.println("Long and lat Rev "+gps.getLatitude()+" = "+gps.getLongitude());
                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();
                    if(latitude!=0&&longitude!=0){
                        if(PreferenceHandler.getInstance( LocationForegroundService.this).isFar()){
                            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                            notificationManager.cancel(12);
                        }else{
                            distance(latitude,longitude);
                        }
                        LiveTracking liveTracking = new LiveTracking ();
                        liveTracking.setEmployeeId(PreferenceHandler.getInstance( LocationForegroundService.this).getUserId());
                        liveTracking.setLatitude(""+latitude);
                        liveTracking.setLongitude(""+longitude);
                        liveTracking.setAppVersion(""+ BuildConfig.VERSION_NAME);
                        liveTracking.setTrackingDate(new SimpleDateFormat("MM/dd/yyyy").format(new Date()));
                        liveTracking.setTrackingTime(new SimpleDateFormat("HH:mm:ss").format(new Date()));
                        if(Const.BATTERY_LEVEL!=0){
                            liveTracking.setBatteryPercentage(""+Const.BATTERY_LEVEL);
                        }
                        if(getMobileOSVersion ()!=null){
                            liveTracking.setMobileOSVersion ( getMobileOSVersion () );
                        }if(isInternetOn (this)){
                            liveTracking.setInternetOn ("Internet ON");
                        }else{
                            liveTracking.setInternetOn ("Internet ON");
                        }


                        try {
                            if ( ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions

                                return;
                            }

                            String imei = ""+Build.MANUFACTURER
                                    + "*" + Build.MODEL + "*" + Build.VERSION.RELEASE
                                    + "*" + Build.VERSION_CODES.class.getFields()[Build.VERSION.SDK_INT].getName();

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if(telephonyManager.getPhoneCount()>1) {
                                    for(int i =0 ;i<telephonyManager.getPhoneCount();i++){
                                        imei = imei+","+/*telephonyManager.getDeviceId(i)*/Settings.System.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
                                    }
                                    liveTracking.setDeviceName("" + imei+"");
                                }else{
                                    liveTracking.setDeviceName(imei+"," + /*telephonyManager.getDeviceId()*/Settings.System.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID));
                                }

                            }else{
                                liveTracking.setDeviceName(imei+","+ /*telephonyManager.getDeviceId()*/Settings.System.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID));
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        //liveTracking.setTrackingDate("01/02/2019");
                        addLiveTracking(liveTracking);
                    }
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean locationCheck(){

        LocationManager lm = (LocationManager) LocationForegroundService.this.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        return gps_enabled || network_enabled;
    }

    public void addLiveTracking(final LiveTracking liveTracking) {
        LiveTrackingAPI apiService = Util.getClient().create( LiveTrackingAPI.class);
        Call< LiveTracking > call = apiService.addLiveTracking(liveTracking);
        call.enqueue(new Callback< LiveTracking >() {
            @Override
            public void onResponse( Call< LiveTracking > call, Response< LiveTracking > response) {
                try {
                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201) {
                        LiveTracking s = response.body();
                        if(s!=null){
                            Log.e("TAG", "Success");
                        }
                    }
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure( Call< LiveTracking > call, Throwable t) {
                Log.e("TAG", t.toString());
            }
        });
    }


    private void distance(final double lati,final double longi){
        Location locationA = new Location("point A");
        locationA.setLatitude(Double.parseDouble(PreferenceHandler.getInstance( LocationForegroundService.this).getOrganizationLati()));
        locationA.setLongitude(Double.parseDouble(PreferenceHandler.getInstance( LocationForegroundService.this).getOrganizationLongi()));
        Location locationB = new Location("point B");
        locationB.setLatitude(lati);
        locationB.setLongitude(longi);

        float distance = locationA.distanceTo(locationB);
        if ( PreferenceHandler.getInstance( LocationForegroundService.this ).isLocationOn( ) ) {
            distance = 0;
        }

        if(distance>200){

            if(PreferenceHandler.getInstance( getApplicationContext() ).isMovingFar()){

            }else{

                Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                URL url = null;

                Intent intents = new Intent( LocationForegroundService.this, BreakPurpose.class);
                intents.putExtra("Longi",""+longi);
                intents.putExtra("Lati",""+lati);
                //  Uri sound = Uri.parse("android.resource://" + this.getPackageName() + "/raw/good_morning");
                int m =12;

                intents.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );
                intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, m, intents, PendingIntent.FLAG_UPDATE_CURRENT);

                int notifyID = 1;
                String CHANNEL_ID = ""+ 1;// The id of the channel.
                CharSequence name = "Zingo" ;// The user-visible name of the channel.
                int importance = NotificationManager.IMPORTANCE_LOW;
                NotificationChannel mChannel=null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
                }

                Notification.Builder notificationBuilder = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    notificationBuilder = new Notification.Builder(this)
                            .setTicker("You are moving far away from your organization").setWhen(0)
                            .setContentTitle("You are moving far away from your organization")
                            .setContentText("You are moving far away from your organization")
                            .setAutoCancel(true)
                            //.setFullScreenIntent(pendingIntent,false)
                            //.setNumber()
                            .setContentIntent(pendingIntent)
                            .setOngoing(true)
                            .setContentInfo("You are moving far away from your organization")
                            .setLargeIcon(icon)
                            .setChannelId("1")
                            .setPriority(Notification.PRIORITY_MAX)
                            // .setPriority(NotificationManager.IMPORTANCE_HIGH)
                            .setSmallIcon(R.mipmap.ic_launcher);
                }else{
                    notificationBuilder = new Notification.Builder(this)
                            .setTicker("You are moving far away from your organization").setWhen(0)
                            .setContentTitle("You are moving far away from your organization")
                            .setContentText("You are moving far away from your organization")
                            .setAutoCancel(true)
                            //.setFullScreenIntent(pendingIntent,false)

                            .setContentIntent(pendingIntent)
                            .setOngoing(true)
                            .setContentInfo("You are moving far away from your organization")
                            .setLargeIcon(icon)
                            .setPriority(Notification.PRIORITY_MAX)
                            .setNumber(1)
                            // .setPriority(NotificationManager.IMPORTANCE_HIGH)
                            .setSmallIcon(R.mipmap.ic_launcher);
                }

                notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
                notificationBuilder.setLights(Color.YELLOW, 1000, 300);

                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    notificationManager.createNotificationChannel(mChannel);
                }
                notificationManager.notify(m, notificationBuilder.build());
                check = true;
                PreferenceHandler.getInstance( getApplicationContext() ).setMovingFar( true );
            }
        }
    }
    private String getMobileOSVersion(){
        String OSVersiondetails;
        String versionRelease = Build.VERSION.RELEASE;
        String versonName = Build.VERSION_CODES.class.getFields()[Build.VERSION.SDK_INT].getName();
        OSVersiondetails = "Version : " + versionRelease+" * "+versonName;
        return OSVersiondetails;
    }

    public boolean isInternetOn(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in airplane mode it will be null
        return (netInfo != null && netInfo.isConnected());
    }
}