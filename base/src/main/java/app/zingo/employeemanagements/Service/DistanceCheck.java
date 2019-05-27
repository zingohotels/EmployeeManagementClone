package app.zingo.employeemanagements.Service;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.net.URL;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import app.zingo.employeemanagements.UI.NewEmployeeDesign.BreakPurpose;
import app.zingo.employeemanagements.Utils.PreferenceHandler;
import app.zingo.employeemanagements.base.R;


public  class DistanceCheck extends Service implements LocationListener {


    boolean isGPSEnable = false;
    boolean isNetworkEnable = false;
    double latitude, longitude;
    LocationManager locationManager;
    Location location;
    private Handler mHandler = new Handler();
    private Timer mTimer = null;
    long notify_interval = 5000;
    //public static String str_receiver = "servicetutorial.service.receiver";
    Intent intent;
    SharedPreferences mPref;
    SharedPreferences.Editor medit;

    boolean check = false;

    public DistanceCheck() {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();

        mTimer = new Timer();
        mTimer.schedule(new TimerTaskToGetLocation(), 60000, notify_interval);
       // intent = new Intent(str_receiver);

        //start a separate thread and start listening to your network object
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void fn_getlocation() {
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGPSEnable && !isNetworkEnable) {

        } else {

            if (isNetworkEnable) {
                location = null;
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }else{

                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, this);
                    if (locationManager!=null){
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location!=null){

                            Log.e("latitude",location.getLatitude()+"");
                            Log.e("longitude",location.getLongitude()+"");

                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            fn_update(location);
                        }
                    }
                }


            }


            if (isGPSEnable){
                location = null;
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,0,this);
                if (locationManager!=null){
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location!=null){
                        Log.e("latitude",location.getLatitude()+"");
                        Log.e("longitude",location.getLongitude()+"");
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        fn_update(location);
                    }
                }
            }


        }

    }

    private class TimerTaskToGetLocation extends TimerTask {
        @Override
        public void run() {

            mHandler.post(new Runnable() {
                @Override
                public void run() {

                    if(PreferenceHandler.getInstance(DistanceCheck.this).getUserId()!=0&& PreferenceHandler.getInstance(DistanceCheck.this).getUserRoleUniqueID()==1){

                        fn_getlocation();
                    }else{
                        Intent lintent = new Intent(DistanceCheck.this, DistanceCheck.class);
                        stopService(lintent);
                        mPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        medit = mPref.edit();
                        medit.putString("service", "").commit();
                    }

                }
            });

        }
    }

    private void fn_update(Location location){

        //intent.putExtra("latutide",location.getLatitude()+"");
        //intent.putExtra("longitude",location.getLongitude()+"");
        //sendBroadcast(intent);

        if(location.getLatitude()!=0&&location.getLongitude()!=0){

            Location locationA = new Location("point A");

            locationA.setLatitude(Double.parseDouble(PreferenceHandler.getInstance(DistanceCheck.this).getOrganizationLati()));
            locationA.setLongitude(Double.parseDouble(PreferenceHandler.getInstance(DistanceCheck.this).getOrganizationLongi()));

            Location locationB = new Location("point B");

            locationB.setLatitude(location.getLatitude());
            locationB.setLongitude(location.getLongitude());

            float distance = locationA.distanceTo(locationB);

           if(distance>200&&!check){



               Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

               URL url = null;

               Intent intents = new Intent(DistanceCheck.this, BreakPurpose.class);
               intents.putExtra("Longi",""+location.getLongitude());
               intents.putExtra("Lati",""+location.getLatitude());

               //  Uri sound = Uri.parse("android.resource://" + this.getPackageName() + "/raw/good_morning");
               int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);

               intents.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );

               intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
               PendingIntent pendingIntent = PendingIntent.getActivity(this, m, intents, PendingIntent.FLAG_UPDATE_CURRENT);

               int notifyID = 1;
               String CHANNEL_ID = ""+ 1;// The id of the channel.
               CharSequence name = "Zingo" ;// The user-visible name of the channel.
               int importance = NotificationManager.IMPORTANCE_LOW;
               NotificationChannel mChannel=null;
               if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                   mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
               }

               Notification.Builder notificationBuilder = null;
               if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
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
               if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                   notificationManager.createNotificationChannel(mChannel);
               }


               notificationManager.notify(m, notificationBuilder.build());

               check = true;
           }





        }else{

        }


    }


    @Override
    public void onTaskRemoved(Intent rootIntent) {
        // TODO Auto-generated method stub
        /*Intent restartService = new Intent(getApplicationContext(), this.getClass());
        restartService.setPackage(getPackageName());
        PendingIntent restartServicePI = PendingIntent.getService(
                getApplicationContext(), 1, restartService,
                PendingIntent.FLAG_ONE_SHOT);

        //Restart the service once it has been killed android
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 100, restartServicePI);*/

    }


    @Override
    public void onDestroy() {

        super.onDestroy();
      /*  Intent restartService = new Intent(getApplicationContext(), this.getClass());
        restartService.setPackage(getPackageName());
        PendingIntent restartServicePI = PendingIntent.getService(
                getApplicationContext(), 1, restartService,
                PendingIntent.FLAG_ONE_SHOT);

        //Restart the service once it has been killed android
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 100, restartServicePI);*/
    }



}