package app.zingo.employeemanagements.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.net.URL;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

import app.zingo.employeemanagements.Model.LeaveNotificationManagers;
import app.zingo.employeemanagements.base.R;
import app.zingo.employeemanagements.UI.Admin.LoginDetailsHost;
import app.zingo.employeemanagements.UI.Admin.TaskListScreen;
import app.zingo.employeemanagements.UI.Common.GeneralNotificationScreen;
import app.zingo.employeemanagements.UI.Employee.EmployeeMeetingHost;
import app.zingo.employeemanagements.UI.NewAdminDesigns.UpdateLeaveScreen;
import app.zingo.employeemanagements.Utils.PreferenceHandler;

public class MyFirebaseServicemessage  extends FirebaseMessagingService {


    public int count = 0;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        Map<String, String> map = remoteMessage.getData();


        if(Objects.requireNonNull(notification.getTitle()).equalsIgnoreCase("App Info Notification")){

            sendPopNotificationGeneral(notification.getTitle(), notification.getBody(), map);

        }else if(PreferenceHandler.getInstance(getApplicationContext()).getUserId()==Integer.parseInt(map.get("EmployeeId"))&&notification.getTitle().contains("Apply For Leave")) {
            sendPopNotification(notification.getTitle(), notification.getBody(), map);
        }

    }


    //Notification for all User and From Firebase

    private void sendPopNotificationGeneral(String title, String body, Map<String, String> map) {


        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        URL url = null;
        Bitmap bigPicture  = null;
        try {
            url = new URL(map.get("PictureUrl"));
            bigPicture = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }

        String message="";

        message = body;

        Intent intent = new Intent(this, GeneralNotificationScreen.class);

        intent.putExtra("Title",title);
        intent.putExtra("Message",body);

        //  Uri sound = Uri.parse("android.resource://" + this.getPackageName() + "/raw/good_morning");
        int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, m, intent, PendingIntent.FLAG_UPDATE_CURRENT);

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
                    .setTicker(title).setWhen(0)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true)
                    //.setFullScreenIntent(pendingIntent,false)
                    //.setNumber()
                    .setContentIntent(pendingIntent)
                    .setContentInfo(title)
                    .setLargeIcon(icon)
                    .setChannelId("1")
                    .setStyle(new Notification.BigPictureStyle()
                            .bigPicture(bigPicture))
                    .setPriority(Notification.PRIORITY_MAX)

                    // .setPriority(NotificationManager.IMPORTANCE_HIGH)
                    .setSmallIcon(R.mipmap.ic_launcher);
        }else{
            notificationBuilder = new Notification.Builder(this)
                    .setTicker(title).setWhen(0)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true)
                    //.setFullScreenIntent(pendingIntent,false)
                    .setContentIntent(pendingIntent)
                    .setContentInfo(title)
                    .setLargeIcon(icon)
                    .setStyle(new Notification.BigPictureStyle()
                            .bigPicture(bigPicture))
                    .setPriority(Notification.PRIORITY_MAX)

                    .setNumber(count)
                    // .setPriority(NotificationManager.IMPORTANCE_HIGH)
                    .setSmallIcon(R.mipmap.ic_launcher);
        }



        notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
        notificationBuilder.setLights(Color.YELLOW, 1000, 300);



        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            assert notificationManager != null;
            notificationManager.createNotificationChannel(mChannel);
        }

        notificationManager.notify(m, notificationBuilder.build());
    }


    //Admin or Manager Notiication
    private void sendPopNotification(String title, String body, Map<String, String> map) {
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);



        String message="";
        String messageText="";

        message = body;

        Intent intent = null;

        if(title.contains("Apply For Leave")){
            intent = new Intent(this, UpdateLeaveScreen.class);
            int employeeId = Integer.parseInt(map.get("ManagerId"));
            String employeeName = map.get("EmployeeName");
            String from = map.get("FromDate");
            String to = map.get("ToDate");
            String reason = map.get("Reason");
            messageText = "From "+employeeName;
            String LeaveId = map.get("LeaveId");
            LeaveNotificationManagers lm = new LeaveNotificationManagers();
            lm.setEmployeeName(employeeName);
            lm.setFromDate(from);
            lm.setToDate(to);
            lm.setReason(reason);
            lm.setEmployeeId(employeeId);
            Bundle bundle = new Bundle();
            bundle.putSerializable("LeaveNotification",lm);
            bundle.putInt("EmployeeId",employeeId);
            bundle.putInt("LeaveId", Integer.parseInt(LeaveId));
            intent.putExtras(bundle);


        }


        //  Uri sound = Uri.parse("android.resource://" + this.getPackageName() + "/raw/good_morning");
        int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, m, intent, PendingIntent.FLAG_UPDATE_CURRENT);

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
                    .setTicker(title).setWhen(0)
                    .setContentTitle(title)
                    .setContentText(messageText)
                    .setAutoCancel(true)
                    //.setFullScreenIntent(pendingIntent,false)
                    //.setNumber()
                    .setContentIntent(pendingIntent)
                    .setContentInfo(title)
                    .setLargeIcon(icon)
                    .setChannelId("1")
                    .setStyle(new Notification.BigTextStyle()
                            .bigText(message))
                    /* .setStyle(new Notification.BigPictureStyle()
                             .bigPicture(bigPicture))*/
                    .setPriority(Notification.PRIORITY_MAX)

                    // .setPriority(NotificationManager.IMPORTANCE_HIGH)
                    .setSmallIcon(R.mipmap.ic_launcher);
        }else{
            notificationBuilder = new Notification.Builder(this)
                    .setTicker(title).setWhen(0)
                    .setContentTitle(title)
                    .setContentText(messageText)
                    .setAutoCancel(true)
                    //.setFullScreenIntent(pendingIntent,false)
                    .setContentIntent(pendingIntent)
                    .setContentInfo(title)
                    .setLargeIcon(icon)
                    .setStyle(new Notification.BigTextStyle()
                            .bigText(message))
                    /*.setStyle(new Notification.BigPictureStyle()
                            .bigPicture(bigPicture))*/
                    .setPriority(Notification.PRIORITY_MAX)

                    .setNumber(count)
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


    //Invoke when notification come for Ver 11.4.2
  /*  @Override
    public void handleIntent(Intent intent) {
        try
        {
            if (intent.getExtras() != null)
            {
                RemoteMessage.Builder builder = new RemoteMessage.Builder("MyFirebaseMessagingService");

                for (String key : intent.getExtras().keySet())
                {
                    builder.addData(key, intent.getExtras().get(key).toString());
                }

                //call to pass value
                onMessageReceived(builder.build());
            }
            else
            {
                super.handleIntent(intent);
            }
        }
        catch (Exception e)
        {
            super.handleIntent(intent);
        }
    }*/

}
