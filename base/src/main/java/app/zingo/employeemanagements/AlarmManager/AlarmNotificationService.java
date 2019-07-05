package app.zingo.employeemanagements.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import app.zingo.employeemanagements.UI.NewEmployeeDesign.EmployeeLoginFragment;
import app.zingo.employeemanagements.UI.NewEmployeeDesign.EmployeeNewMainScreen;
import app.zingo.employeemanagements.Utils.PreferenceHandler;
import app.zingo.employeemanagements.base.R;


public class AlarmNotificationService extends IntentService {

    private NotificationManager alarmNotificationManager;
    public static final int NOTIFICATION_ID = 1;
    private Context context;
    private final String TAG = "AlarmNotificationService";

    public AlarmNotificationService() {
        super("AlarmNotificationService");
    }

    @Override
    public void onHandleIntent(Intent intent) {

        if(intent.getStringExtra ("type")!=null&&intent.getStringExtra ("type").equalsIgnoreCase ("Lunch")){
            System.out.println ("Suree Notification "+intent.getStringExtra ("type"));
            if(PreferenceHandler.getInstance(AlarmNotificationService.this).getLunchBreakStatus ().equalsIgnoreCase ("true")) {
                sendNotification ("You are Still in Lunch Break.  if you completed your Break, Please end break");
            }
        }
        else if(intent.getStringExtra ("type")!=null&&intent.getStringExtra ("type").equalsIgnoreCase ("Tea")){
            System.out.println ("Suree Notification "+intent.getStringExtra ("type"));
            if(PreferenceHandler.getInstance(AlarmNotificationService.this).getTeaBreakStatus ().equalsIgnoreCase ("true")){
                sendNotification("You are Still in Tea Break.  if you completed your Break, Please end break");
            }
        }
        else {
            System.out.println ("Suree Notification "+intent.getStringExtra ("type"));
            sendNotification ("Wake Up! Wake Up! Alarm started!!");
        }
    }

    private void sendNotification(String msg) {

        Uri sound = null;
        sound = Uri.parse("android:resource://"+this.getPackageName()+"/"+R.raw.solemn);

        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        String message="";
        message = msg;

        Intent intent = new Intent(this, EmployeeNewMainScreen.class);

        int m = 115;
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, m, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        int notifyID = 1;
        String CHANNEL_ID = ""+ 115;// The id of the channel.
        CharSequence name = "EMSBreak" ;// The user-visible name of the channel.
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel=null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(CHANNEL_ID, name, importance);

            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build();

            mChannel.enableLights(true);
            mChannel.enableVibration(true);
            mChannel.setSound(sound,audioAttributes);
        }

        Notification.Builder notificationBuilder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notificationBuilder = new Notification.Builder(this)
                    .setTicker("Alert").setWhen(0)
                    .setContentTitle("Alert")
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setSound(sound)
                    .setContentIntent(pendingIntent)
                    .setContentInfo("message")
                    .setLargeIcon(icon)
                    .setStyle(new Notification.BigTextStyle().bigText (message))
                    .setChannelId("115")
                    .setPriority(Notification.PRIORITY_MAX)
                    .setSmallIcon(R.mipmap.ic_launcher);
        }else{
            notificationBuilder = new Notification.Builder(this)
                    .setTicker("Alert").setWhen(0)
                    .setContentTitle("Alert")
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setSound(sound)
                    .setContentIntent(pendingIntent)
                    .setContentInfo("Alert")
                    .setLargeIcon(icon)
                    .setStyle(new Notification.BigTextStyle().bigText (message))
                    .setPriority(Notification.PRIORITY_MAX)
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
}