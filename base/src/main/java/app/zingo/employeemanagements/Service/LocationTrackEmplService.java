package app.zingo.employeemanagements.Service;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

import app.zingo.employeemanagements.model.LiveTracking;
import app.zingo.employeemanagements.utils.Const;
import app.zingo.employeemanagements.utils.PreferenceHandler;
import app.zingo.employeemanagements.utils.TrackGPS;
import app.zingo.employeemanagements.utils.Util;
import app.zingo.employeemanagements.WebApi.LiveTrackingAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationTrackEmplService extends Service {
    TrackGPS gps;
    LiveTracking liveTracking;
    int SECONDS = 60; // The delay in minutes

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive( Context ctxt, Intent intent) {
            int level = intent.getIntExtra( BatteryManager.EXTRA_LEVEL, 0);
            Const.BATTERY_LEVEL = level;
            System.out.println ( "Suree LocationTrackEmplService :"+Const.BATTERY_LEVEL );
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        gps = new TrackGPS( LocationTrackEmplService.this);
        //getLiveLocation(PreferenceHandler.getInstance(LocationSharingServices.this).getUserId()); // If the function you wanted was static
        try {

            if(locationCheck()){
                gps= new TrackGPS( LocationTrackEmplService.this);
                if(gps.canGetLocation()) {
                    System.out.println("Long and lat Rev"+gps.getLatitude()+" = "+gps.getLongitude());
                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();

                    if(latitude!=0&&longitude!=0){
                        LiveTracking liveTracking = new LiveTracking();
                        liveTracking.setEmployeeId(PreferenceHandler.getInstance( LocationTrackEmplService.this).getUserId());
                        liveTracking.setLatitude(""+latitude);
                        liveTracking.setLongitude(""+longitude);
                        liveTracking.setTrackingDate(new SimpleDateFormat("MM/dd/yyyy").format(new Date()));
                        liveTracking.setTrackingTime(new SimpleDateFormat("HH:mm:ss").format(new Date()));
                        if(Const.BATTERY_LEVEL!=0){
                            liveTracking.setBatteryPercentage ( String.valueOf ( Const.BATTERY_LEVEL ) );
                        }
                        addLiveTracking(liveTracking);
                    }
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        Intent restartService = new Intent(getApplicationContext(), this.getClass());
        restartService.setPackage(getPackageName());
        PendingIntent restartServicePI = PendingIntent.getService( getApplicationContext(), 1, restartService, PendingIntent.FLAG_ONE_SHOT);
        //Restart the service once it has been killed android
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 6000, restartServicePI);

        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        // TODO Auto-generated method stub
        Intent restartService = new Intent(getApplicationContext(), this.getClass());
        restartService.setPackage(getPackageName());
        PendingIntent restartServicePI = PendingIntent.getService( getApplicationContext(), 1, restartService, PendingIntent.FLAG_ONE_SHOT);
        //Restart the service once it has been killed android
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 100, restartServicePI);
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        this.registerReceiver(this.mBatInfoReceiver, new IntentFilter (Intent.ACTION_BATTERY_CHANGED));
        System.out.println("Location 1");
        //start a separate thread and start listening to your network object
    }

    public boolean locationCheck(){
        final boolean status = false;
        LocationManager lm = (LocationManager) LocationTrackEmplService.this.getSystemService(Context.LOCATION_SERVICE);
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
        Call<LiveTracking> call = apiService.addLiveTracking(liveTracking);
        call.enqueue(new Callback<LiveTracking>() {
            @Override
            public void onResponse(Call<LiveTracking> call, Response<LiveTracking> response) {
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
            public void onFailure(Call<LiveTracking> call, Throwable t) {
                Log.e("TAG", t.toString());
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}