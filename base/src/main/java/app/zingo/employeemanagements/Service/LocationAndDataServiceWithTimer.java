package app.zingo.employeemanagements.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.net.InetAddress;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import app.zingo.employeemanagements.Utils.PreferenceHandler;
import app.zingo.employeemanagements.base.R;

public class LocationAndDataServiceWithTimer extends Service implements TextToSpeech.OnInitListener
{

    TextToSpeech talker;

    public int timeGPS = 0,timeNetwork = 0;

    public static final long NOTIFY_INTERVAL = 3* 60 * 1000;

    // run on another Thread to avoid crash
    private Handler gpsHandler = new Handler(), NetworkHandler = new Handler();
    // timer handling
    private Timer t = null,tNetwork = null;



    @Override
    public void onCreate()
    {
        super.onCreate();
        Log.e("Location1", "Inside onCreate");

        // cancel if already existed
        if(t != null) {
            t.cancel();
        }
        // recreate new
        t = new Timer();

        // schedule task
        t.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, NOTIFY_INTERVAL);

        if(tNetwork != null) {
            tNetwork.cancel();
        }
        // recreate new
        tNetwork = new Timer();

        // schedule task
        tNetwork.scheduleAtFixedRate(new NetworkDisplayTimerTask(), 0, NOTIFY_INTERVAL);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {


        t = new Timer();
        tNetwork = new Timer();
        Log.e("Location1", "Inside onStart");

        talker = new TextToSpeech(this, this);
        timeGPS = 0;
        timeNetwork = 0;
      //  testMethod();

        return START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void notificationForGPS()
    {
        //Set the schedule function and rate

        if(t!=null){

            t.scheduleAtFixedRate(new TimerTask() {

                @Override
                public void run()    {

                    timeGPS += 1;
                    String todis = String.valueOf(timeGPS);

                    if(todis.contains("60"))
                    {

                        System.out.println("time "+timeGPS+" Actual "+new SimpleDateFormat("HH:mm:ss").format(new Date()));
                        //talker.speak("You have a new Booking", TextToSpeech.QUEUE_ADD,null);
                        timeGPS = 0;


                        if(locationCheck()){

                            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                            notificationManager.cancel(1);

                        }else{

                            Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

                            URL url = null;


                            Intent intents = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);


                            //  Uri sound = Uri.parse("android.resource://" + this.getPackageName() + "/raw/good_morning");
                            int m = 1;

                            intents.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );

                            intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            PendingIntent pendingIntent = PendingIntent.getActivity(LocationAndDataServiceWithTimer.this, m, intents, PendingIntent.FLAG_UPDATE_CURRENT);

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
                                notificationBuilder = new Notification.Builder(LocationAndDataServiceWithTimer.this)
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
                                notificationBuilder = new Notification.Builder(LocationAndDataServiceWithTimer.this)
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






                        }

                        t.cancel();
                        t.purge();


                    }
                }

            }, 0, 2000);



        }

    }

    public void notificationForNetwork()
    {
        //Set the schedule function and rate

        if(tNetwork!=null){

            tNetwork.scheduleAtFixedRate(new TimerTask() {

                @Override
                public void run()    {

                    timeNetwork += 1;
                    String todis = String.valueOf(timeNetwork);

                    if(todis.contains("60"))
                    {

                        System.out.println("time "+timeNetwork+" Actual "+new SimpleDateFormat("HH:mm:ss").format(new Date()));
                        //talker.speak("You have a new Booking", TextToSpeech.QUEUE_ADD,null);
                        timeNetwork = 0;


                        if(checkActiveInternetConnection()){

                            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                            notificationManager.cancel(11);

                        }else{

                            Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

                            URL url = null;

                            Intent intents = new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);

                            //  Uri sound = Uri.parse("android.resource://" + this.getPackageName() + "/raw/good_morning");
                            int m = 11;

                            intents.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );

                            intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            PendingIntent pendingIntent = PendingIntent.getActivity(LocationAndDataServiceWithTimer.this, m, intents, PendingIntent.FLAG_UPDATE_CURRENT);

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
                                notificationBuilder = new Notification.Builder(LocationAndDataServiceWithTimer.this)
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
                                notificationBuilder = new Notification.Builder(LocationAndDataServiceWithTimer.this)
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




                        }




                        t.cancel();
                        t.purge();


                    }
                }

            }, 0, 2000);



        }

    }

    public void onInit(int status)
    {
       // talker.speak("Testing Service in a App",TextToSpeech.QUEUE_ADD,null);
    }

    class TimeDisplayTimerTask extends TimerTask {

        @Override
        public void run() {
            // run on another thread
            gpsHandler.post(new Runnable() {

                @Override
                public void run() {
                    // display toast

                    if(t!=null){

                        System.out.println("time "+timeGPS+" Actual "+new SimpleDateFormat("HH:mm:ss").format(new Date()));
                        t=new Timer();
                        timeGPS = 0;
                        //timeNetwork = 0;

                        if(locationCheck()){

                            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                            notificationManager.cancel(1);

                        }else{

                            notificationForGPS();
                        }




                    }


                }

            });
        }
    }


    class NetworkDisplayTimerTask extends TimerTask {

        @Override
        public void run() {
            // run on another thread
            NetworkHandler.post(new Runnable() {

                @Override
                public void run() {
                    // display toast

                    if(tNetwork!=null){

                        System.out.println("time "+timeNetwork+" Actual "+new SimpleDateFormat("HH:mm:ss").format(new Date()));
                        tNetwork=new Timer();
                        //timeGPS = 0;
                        timeNetwork = 0;



                        if(checkActiveInternetConnection()){

                            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                            notificationManager.cancel(11);

                        }else{
                            notificationForNetwork();
                        }


                    }


                }

            });
        }
    }

    public boolean locationCheck(){

        if(PreferenceHandler.getInstance(LocationAndDataServiceWithTimer.this).getUserId()!=0){

            final boolean status = false;
            LocationManager lm = (LocationManager) LocationAndDataServiceWithTimer.this.getSystemService(Context.LOCATION_SERVICE);
            boolean gps_enabled = false;
            boolean network_enabled = false;

            try {

                gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

            } catch(Exception ex) {}

            try {
                network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch(Exception ex) {}

            return gps_enabled && network_enabled;

        }else{

            stopSelf();

            return true;

        }


    }

    public boolean isNetworkAvailable() {

        final ConnectivityManager connectivityManager = ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public boolean checkActiveInternetConnection() {

        if(PreferenceHandler.getInstance(LocationAndDataServiceWithTimer.this).getUserId()!=0){

            if (isNetworkAvailable()) {

                try {
                    InetAddress ipAddr = InetAddress.getByName("google.com");
                    //You can replace it with your name
                    return !ipAddr.equals("");

                } catch (Exception e) {
                    return false;
                }

            } else {

            }
            return false;

        }else{
            stopSelf();
            return true;

        }

    }


    @Override
    public void onDestroy() {

        super.onDestroy();
        t.cancel();
        tNetwork.cancel();

    }
}
