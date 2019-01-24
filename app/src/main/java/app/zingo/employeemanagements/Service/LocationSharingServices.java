package app.zingo.employeemanagements.Service;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import app.zingo.employeemanagements.Model.LiveTracking;
import app.zingo.employeemanagements.Model.LoginDetails;
import app.zingo.employeemanagements.Model.LoginDetailsNotificationManagers;
import app.zingo.employeemanagements.UI.Employee.DashBoardEmployee;
import app.zingo.employeemanagements.Utils.PreferenceHandler;
import app.zingo.employeemanagements.Utils.ThreadExecuter;
import app.zingo.employeemanagements.Utils.TrackGPS;
import app.zingo.employeemanagements.Utils.Util;
import app.zingo.employeemanagements.WebApi.LiveTrackingAPI;
import app.zingo.employeemanagements.WebApi.LoginDetailsAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ZingoHotels Tech on 02-01-2019.
 */

public class LocationSharingServices extends Service {

    TrackGPS gps;
    LiveTracking liveTracking;
    int SECONDS = 60; // The delay in minutes

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        gps = new TrackGPS(LocationSharingServices.this);

        /*Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() { // Function runs every MINUTES minutes."Longitude": "77.6157732","Latitude": "12.9334382",
                // Run the code you want here

            }
        }, 0, 1000 * SECONDS);*/

        //getLiveLocation(PreferenceHandler.getInstance(LocationSharingServices.this).getUserId()); // If the function you wanted was static
        try {

            if(locationCheck()){
                gps= new TrackGPS(LocationSharingServices.this);
                if(gps.canGetLocation())
                {
                    System.out.println("Long and lat Rev"+gps.getLatitude()+" = "+gps.getLongitude());
                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();

                    if(latitude!=0&&longitude!=0){
                        LiveTracking liveTracking = new LiveTracking();
                        liveTracking.setEmployeeId(PreferenceHandler.getInstance(LocationSharingServices.this).getUserId());
                        liveTracking.setLatitude(""+latitude);
                        liveTracking.setLongitude(""+longitude);
                        liveTracking.setTrackingDate(new SimpleDateFormat("MM/dd/yyyy").format(new Date()));
                        liveTracking.setTrackingTime(new SimpleDateFormat("HH:mm:ss").format(new Date()));
                        //liveTracking.setTrackingDate("01/02/2019");
                        addLiveTracking(liveTracking);
                    }



                }
                else
                {

                }
            }


        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        Intent restartService = new Intent(getApplicationContext(),
                this.getClass());
        restartService.setPackage(getPackageName());
        PendingIntent restartServicePI = PendingIntent.getService(
                getApplicationContext(), 1, restartService,
                PendingIntent.FLAG_ONE_SHOT);

        //Restart the service once it has been killed android


        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 6000, restartServicePI);

        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        // TODO Auto-generated method stub
        Intent restartService = new Intent(getApplicationContext(),
                this.getClass());
        restartService.setPackage(getPackageName());
        PendingIntent restartServicePI = PendingIntent.getService(
                getApplicationContext(), 1, restartService,
                PendingIntent.FLAG_ONE_SHOT);

        //Restart the service once it has been killed android


        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 100, restartServicePI);

    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();

        System.out.println("Location 1");

        //start a separate thread and start listening to your network object
    }


    public void getLiveLocation(final int id){

        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {

                final LiveTrackingAPI subCategoryAPI = Util.getClient().create(LiveTrackingAPI.class);
                Call<ArrayList<LiveTracking>> getProf = subCategoryAPI.getLiveTrackingByEmployeeId(id);
                //Call<ArrayList<Blogs>> getBlog = blogApi.getBlogs();

                getProf.enqueue(new Callback<ArrayList<LiveTracking>>() {

                    @Override
                    public void onResponse(Call<ArrayList<LiveTracking>> call, Response<ArrayList<LiveTracking>> response) {

                        if (response.code() == 200||response.code() == 201||response.code() == 204)
                        {
                            System.out.println("Inside api");

                            final ArrayList<LiveTracking> dto = response.body();

                            if(dto!=null&&dto.size()!=0){

                                try {

                                    if(locationCheck()){
                                    if(gps.canGetLocation())
                                    {
                                        System.out.println("Long and lat Rev"+gps.getLatitude()+" = "+gps.getLongitude());
                                        double latitude = gps.getLatitude();
                                        double longitude = gps.getLongitude();

                                        liveTracking= dto.get(0);
                                        liveTracking.setEmployeeId(PreferenceHandler.getInstance(LocationSharingServices.this).getUserId());
                                        liveTracking.setLatitude(""+latitude);
                                        liveTracking.setLongitude(""+longitude);
                                        updateLiveTracking(liveTracking);

                                    }
                                    else
                                    {

                                    }
                                }


                                }
                                catch (Exception ex)
                                {
                                    ex.printStackTrace();
                                }

                            }else{

                                try {

                                    if(locationCheck()){
                                        if(gps.canGetLocation())
                                        {
                                            System.out.println("Long and lat Rev"+gps.getLatitude()+" = "+gps.getLongitude());
                                            double latitude = gps.getLatitude();
                                            double longitude = gps.getLongitude();

                                            LiveTracking liveTracking = new LiveTracking();
                                            liveTracking.setEmployeeId(PreferenceHandler.getInstance(LocationSharingServices.this).getUserId());
                                            liveTracking.setLatitude(""+latitude);
                                            liveTracking.setLongitude(""+longitude);
                                            liveTracking.setTrackingDate(new SimpleDateFormat("MM/dd/yyyy").format(new Date()));
                                            addLiveTracking(liveTracking);

                                        }
                                        else
                                        {

                                        }
                                    }


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
                    public void onFailure(Call<ArrayList<LiveTracking>> call, Throwable t) {

                    }
                });

            }

        });
    }

    public boolean locationCheck(){

        final boolean status = false;
        LocationManager lm = (LocationManager)LocationSharingServices.this.getSystemService(Context.LOCATION_SERVICE);
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
            /*AlertDialog.Builder dialog = new AlertDialog.Builder(LocationSharingServices.this);
            dialog.setMessage("Location is not enable");
            dialog.setPositiveButton("Open Settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    LocationSharingServices.this.startActivity(myIntent);
                    //get gps
                }
            });
            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub


                }
            });
            dialog.show();*/
            return false;
        }else{
            return true;
        }
    }

    public void addLiveTracking(final LiveTracking liveTracking) throws Exception{





        LiveTrackingAPI apiService = Util.getClient().create(LiveTrackingAPI.class);

        Call<LiveTracking> call = apiService.addLiveTracking(liveTracking);

        call.enqueue(new Callback<LiveTracking>() {
            @Override
            public void onResponse(Call<LiveTracking> call, Response<LiveTracking> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {


                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201) {

                        LiveTracking s = response.body();

                        if(s!=null){

                            Log.e("TAG", "Success");
                        }




                    }else {

                    }
                }
                catch (Exception ex)
                {


                    ex.printStackTrace();
                }
//                callGetStartEnd();
            }

            @Override
            public void onFailure(Call<LiveTracking> call, Throwable t) {


                Log.e("TAG", t.toString());
            }
        });



    }

    public void updateLiveTracking(final LiveTracking liveTracking) throws Exception{





        LiveTrackingAPI apiService = Util.getClient().create(LiveTrackingAPI.class);

        Call<LiveTracking> call = apiService.updateLiveTrackingById(liveTracking.getLiveTrackingDetailsId(),liveTracking);

        call.enqueue(new Callback<LiveTracking>() {
            @Override
            public void onResponse(Call<LiveTracking> call, Response<LiveTracking> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {


                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201||response.code()==204) {


                        Log.e("TAG", "Updated");


                    }else {

                    }
                }
                catch (Exception ex)
                {


                    ex.printStackTrace();
                }
//                callGetStartEnd();
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