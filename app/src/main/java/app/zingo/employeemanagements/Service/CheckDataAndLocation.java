package app.zingo.employeemanagements.Service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;


import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

import app.zingo.employeemanagements.Model.GeneralNotification;
import app.zingo.employeemanagements.R;

import app.zingo.employeemanagements.Utils.Constants;
import app.zingo.employeemanagements.Utils.PreferenceHandler;
import app.zingo.employeemanagements.Utils.Util;
import app.zingo.employeemanagements.WebApi.GeneralNotificationAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.google.android.gms.plus.PlusOneDummyView.TAG;


public class CheckDataAndLocation extends Service {


    long SECONDS = 10000; // The delay in minutes
    CountDownTimer cdt = null;
    boolean send = false;
    boolean location = false;
    boolean data = false;

    private static final int NOTIFICATION_ID = 101;
    private static final int NOTIFICATION_IDS = 102;

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub


        if(PreferenceHandler.getInstance(CheckDataAndLocation.this).getUserId()!=0&&PreferenceHandler.getInstance(CheckDataAndLocation.this).getUserRoleUniqueID()==1){



            if(isNetworkAvailable()){

                data = false;

            }else{


                if(data){

                }else{

                    Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.ems);

                    URL url = null;

                    Intent intents = new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);

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
                                .setTicker("Your Internet is off").setWhen(0)
                                .setContentTitle("Your Internet is off")
                                .setContentText("Your Internet is off.Please on and get better service from us")
                                .setAutoCancel(true)
                                //.setFullScreenIntent(pendingIntent,false)
                                //.setNumber()
                                .setContentIntent(pendingIntent)
                                .setContentInfo("Your Internet is off.Please on and get better service from us")
                                .setLargeIcon(icon).setOngoing(true)
                                .setChannelId("1")

                                .setPriority(Notification.PRIORITY_MAX)

                                // .setPriority(NotificationManager.IMPORTANCE_HIGH)
                                .setSmallIcon(R.mipmap.ic_launcher);
                    }else{
                        notificationBuilder = new Notification.Builder(this)
                                .setTicker("Your Internet is off").setWhen(0)
                                .setContentTitle("Your Internet is off")
                                .setContentText("Your Internet is off.Please on and get better service from us")
                                .setAutoCancel(true)
                                //.setFullScreenIntent(pendingIntent,false)
                                .setContentIntent(pendingIntent)
                                .setOngoing(true)
                                .setContentInfo("Your Internet is off.Please on and get better service from us")
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

                   data = true;
                }


            }

            if(locationCheck()){

                send = false;
                cdt = null;
                location = false;

            }else{


                if(!send){

                    cdt = new CountDownTimer(900000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {

                            Log.i(TAG, "Countdown seconds remaining: " + millisUntilFinished / 1000);
                            if(locationCheck()){
                                cdt = null;
                            }else{



                            }

                        }

                        @Override
                        public void onFinish() {
                            Log.i(TAG, "Timer finished");

                            if(send){

                            }else{

                                //send
                                GeneralNotification gm = new GeneralNotification();
                                gm.setEmployeeId(PreferenceHandler.getInstance(CheckDataAndLocation.this).getManagerId());
                                gm.setSenderId(Constants.SENDER_ID);
                                gm.setServerId(Constants.SERVER_ID);
                                gm.setTitle("GPS Not Enabled");
                                gm.setMessage(PreferenceHandler.getInstance(CheckDataAndLocation.this).getUserFullName()+" is switch off his gps more than 15 mins.");
                                sendNotification(gm);
                                send = true;

                            }
                        }
                    };

                    cdt.start();

                }else{

                }



                if(location){

                }else{

                    Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.ems);

                    URL url = null;


                    Intent intents = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);


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
                                .setTicker("Your GPS is off").setWhen(0)
                                .setContentTitle("Your GPS is off")
                                .setContentText("Your GPS is off.Please on and get better service from us")
                                .setAutoCancel(true)
                                .setOngoing(true)
                                //.setFullScreenIntent(pendingIntent,false)
                                //.setNumber()
                                .setContentIntent(pendingIntent)
                                .setContentInfo("Your GPS is off.Please on and get better service from us")
                                .setLargeIcon(icon)
                                .setChannelId("1")

                                .setPriority(Notification.PRIORITY_MAX)

                                // .setPriority(NotificationManager.IMPORTANCE_HIGH)
                                .setSmallIcon(R.mipmap.ic_launcher);
                    }else{
                        notificationBuilder = new Notification.Builder(this)
                                .setTicker("Your GPS is off").setWhen(0)
                                .setContentTitle("Your GPS is off")
                                .setContentText("Your GPS is off.Please on and get better service from us")
                                .setAutoCancel(true)
                                .setOngoing(true)
                                //.setFullScreenIntent(pendingIntent,false)
                                .setContentIntent(pendingIntent)
                                .setContentInfo("Your GPS is off.Please on and get better service from us")
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
                    location = true;
                }




            }

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




    public boolean locationCheck(){

        final boolean status = false;
        LocationManager lm = (LocationManager)CheckDataAndLocation.this.getSystemService(Context.LOCATION_SERVICE);
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

            return false;
        }else{
            return true;
        }
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    public  boolean isNetworkAvailable() {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public void sendNotification(final GeneralNotification md){



        GeneralNotificationAPI apiService = Util.getClient().create(GeneralNotificationAPI.class);

        Call<ArrayList<String>> call = apiService.sendGeneralNotification(md);

        call.enqueue(new Callback<ArrayList<String>>() {
            @Override
            public void onResponse(Call<ArrayList<String>> call, Response<ArrayList<String>> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {


                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201) {





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
            public void onFailure(Call<ArrayList<String>> call, Throwable t) {


                Log.e("TAG", t.toString());
            }
        });



    }
}

   /* Button startServiceButton = (Button)findViewById(R.id.start_foreground_service_button);
            startServiceButton.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View v) {
        Intent intent = new Intent(ServiceTestingActivity.this, LocaitionPassWithConditionServices.class);
        intent.setAction(LocaitionPassWithConditionServices.ACTION_START_FOREGROUND_SERVICE);
        startService(intent);
        }
        });

        Button stopServiceButton = (Button)findViewById(R.id.stop_foreground_service_button);
        stopServiceButton.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View v) {
        Intent intent = new Intent(ServiceTestingActivity.this, LocaitionPassWithConditionServices.class);
        intent.setAction(LocaitionPassWithConditionServices.ACTION_STOP_FOREGROUND_SERVICE);
        startService(intent);
        }
        });*/