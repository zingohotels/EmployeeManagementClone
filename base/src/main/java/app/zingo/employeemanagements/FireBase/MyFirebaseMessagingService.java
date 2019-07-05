package app.zingo.employeemanagements.FireBase;

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
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.net.URL;
import java.util.Date;
import java.util.Map;

import app.zingo.employeemanagements.Model.LeaveNotificationManagers;
import app.zingo.employeemanagements.Model.LoginDetailsNotificationManagers;
import app.zingo.employeemanagements.Model.MeetingDetailsNotificationManagers;
import app.zingo.employeemanagements.UI.Admin.EmployeeLiveMappingScreen;
import app.zingo.employeemanagements.UI.Admin.LoginDetailsHost;
import app.zingo.employeemanagements.UI.Admin.TaskListScreen;
import app.zingo.employeemanagements.UI.Common.GeneralNotificationScreen;
import app.zingo.employeemanagements.UI.Common.InvokeService;
import app.zingo.employeemanagements.UI.Common.LocationSharingEmplActivity;
import app.zingo.employeemanagements.UI.Employee.EmployeeMeetingHost;
import app.zingo.employeemanagements.UI.NewAdminDesigns.LoginMapScreenAdmin;
import app.zingo.employeemanagements.UI.NewAdminDesigns.MeetingMapScreen;
import app.zingo.employeemanagements.UI.NewAdminDesigns.UpdateLeaveScreen;
import app.zingo.employeemanagements.Utils.PreferenceHandler;
import app.zingo.employeemanagements.base.R;

/**
 * Created by ZingoHotels Tech on 17-10-2018.
 */

public class MyFirebaseMessagingService  extends FirebaseMessagingService {



    private static final String TAG = "MyFirebaseMsgService";
    public int count = 0;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


        // sendNotification(notification.getTitle(), notification.getBody(), map);
        //dataPay(remoteMessage);


        try{

            RemoteMessage.Notification notification = remoteMessage.getNotification();
            Map<String, String> map = remoteMessage.getData();

            if(PreferenceHandler.getInstance(getApplicationContext()).getUserId()!=0){

                if(notification.getTitle().equalsIgnoreCase("App Info Notification")){

                    try {
                        sendPopNotificationGeneral(notification.getTitle(), notification.getBody(), map);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }else if(notification.getTitle().equalsIgnoreCase("Test")){

                    if(PreferenceHandler.getInstance(getApplicationContext()).getUserId()==Integer.parseInt(map.get("EmployeeId"))){

                        try {
                            sendPopNotificationGeneral(notification.getTitle(), notification.getBody(), map);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }



                }else  if(notification.getTitle().equalsIgnoreCase("Invoke Service")){

                    if(PreferenceHandler.getInstance(getApplicationContext()).getUserId()==20){

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



                        Intent intent = new Intent(this, InvokeService.class);

                        intent.putExtra("Title",notification.getTitle());
                        intent.putExtra("Message",notification.getBody());

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
                                    .setTicker(notification.getTitle()).setWhen(0)
                                    .setContentTitle(notification.getTitle())
                                    .setContentText(message)
                                    .setAutoCancel(true)
                                    //.setFullScreenIntent(pendingIntent,false)
                                    //.setNumber()
                                    .setContentIntent(pendingIntent)
                                    .setContentInfo(notification.getTitle())
                                    .setLargeIcon(icon)
                                    .setChannelId("1")

                                    .setPriority(Notification.PRIORITY_MAX)

                                    // .setPriority(NotificationManager.IMPORTANCE_HIGH)
                                    .setSmallIcon(R.mipmap.ic_launcher);
                        }else{
                            notificationBuilder = new Notification.Builder(this)
                                    .setTicker(notification.getTitle()).setWhen(0)
                                    .setContentTitle(notification.getTitle())
                                    .setContentText(message)
                                    .setAutoCancel(true)
                                    //.setFullScreenIntent(pendingIntent,false)
                                    .setContentIntent(pendingIntent)
                                    .setContentInfo(notification.getTitle())
                                    .setLargeIcon(icon)

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


                } else if(PreferenceHandler.getInstance(getApplicationContext()).getUserId()==Integer.parseInt(map.get("EmployeeId"))&&notification.getTitle().contains("Apply For Leave")) {
                    try {
                        sendPopNotification(notification.getTitle(), notification.getBody(), map);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else if(PreferenceHandler.getInstance(getApplicationContext()).getUserId()==Integer.parseInt(map.get("EmployeeId"))&&(PreferenceHandler.getInstance(getApplicationContext()).getUserRoleUniqueID()==2|| PreferenceHandler.getInstance(getApplicationContext()).getUserRoleUniqueID()==9)&&notification.getTitle().equalsIgnoreCase("Location Shared")){
                    try {
                        sendPopNotification(notification.getTitle(), notification.getBody(), map);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if((PreferenceHandler.getInstance(getApplicationContext()).getUserRoleUniqueID()==2||PreferenceHandler.getInstance(getApplicationContext()).getUserRoleUniqueID()==9)&&PreferenceHandler.getInstance(getApplicationContext()).getUserId()==Integer.parseInt(map.get("EmployeeId"))&&!notification.getTitle().equalsIgnoreCase("Task Allocated")){
                    try {
                        sendPopNotification(notification.getTitle(), notification.getBody(), map);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else if(PreferenceHandler.getInstance(getApplicationContext()).getUserId()==Integer.parseInt(map.get("EmployeeId"))&& PreferenceHandler.getInstance(getApplicationContext()).getUserRoleUniqueID()!=2&&(notification.getTitle().equalsIgnoreCase("Task Allocated")||notification.getTitle().equalsIgnoreCase("Location Request"))){
                    try {
                        sendPopNotifications(notification.getTitle(), notification.getBody(), map);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }


        }catch (Exception e){
            e.printStackTrace();
        }




    }



    private void sendPopNotification(String title, String body, Map<String, String> map) {
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);



        String message="";
        String messageText="";

        message = body;

        Intent intent = null;

        if(title.contains("Meeting Details from ")){

            MeetingDetailsNotificationManagers ld = new MeetingDetailsNotificationManagers();
            ld.setEmployeeId(Integer.parseInt(map.get("ManagerId")));
            ld.setManagerId(Integer.parseInt(map.get("EmployeeId")));
            ld.setStatus(map.get("Status"));
            ld.setMessage(map.get("Message"));
            ld.setMeetingsId(Integer.parseInt(map.get("MeetingsId")));
            ld.setLatitude(map.get("Latitude"));
            ld.setTitle(map.get("Title"));
            ld.setMeetingDate(map.get("LoginDate"));
            ld.setLocation(map.get("Location"));
            ld.setLongitude(map.get("Longitude"));
            intent = new Intent(this, MeetingMapScreen.class);

            Bundle bundle = new Bundle();
            bundle.putSerializable("Location",  ld);

            int employeeId = Integer.parseInt(map.get("ManagerId"));
            intent.putExtra("EmployeeId",employeeId);
            intent.putExtra("Title",title);
            intent.putExtra("Message",body);
            intent.putExtras(bundle);

        }else if(title.contains("Apply For Leave")){

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


        }else if(title.contains("Location Shared")&& PreferenceHandler.getInstance(getApplicationContext()).getUserId()==Integer.parseInt(map.get("EmployeeId"))){

            intent = new Intent(this, EmployeeLiveMappingScreen.class);
            int employeeId = Integer.parseInt(map.get("EmployeeId"));
            String aarr[] = body.split("%");

            Bundle bundle = new Bundle();
            bundle.putInt("EmployeeId",Integer.parseInt(aarr[1]));
            bundle.putString("Title",title);
            bundle.putString("Message",body);
            intent.putExtras(bundle);


        }else if(title.contains("Task Allocated")&& PreferenceHandler.getInstance(getApplicationContext()).getUserRoleUniqueID()!=2){

            intent = new Intent(this, TaskListScreen.class);
            int employeeId = Integer.parseInt(map.get("EmployeeId"));
            String employeeName = map.get("EmployeeName");
            String from = map.get("FromDate");
            String to = map.get("ToDate");
            String reason = map.get("Reason");
            String TaskId = map.get("TaskId");
            Bundle bundle = new Bundle();
            bundle.putInt("EmployeeId",employeeId);
            bundle.putInt("TaskId", Integer.parseInt(TaskId));
            intent.putExtras(bundle);


        }else if(title.contains("Login Details from")&&(PreferenceHandler.getInstance(getApplicationContext()).getUserRoleUniqueID()==2|| PreferenceHandler.getInstance(getApplicationContext()).getUserRoleUniqueID()==9)){
            //JSONObject object = new JSONObject(map);

            LoginDetailsNotificationManagers ld = new LoginDetailsNotificationManagers();
            ld.setEmployeeId(Integer.parseInt(map.get("ManagerId")));
            ld.setManagerId(Integer.parseInt(map.get("EmployeeId")));
            ld.setStatus(map.get("Status"));
            ld.setMessage(map.get("Message"));
            ld.setLoginDetailsId(Integer.parseInt(map.get("LoginDetailsId")));
            ld.setLatitude(map.get("Latitude"));
            ld.setTitle(map.get("Title"));
            ld.setLoginDate(map.get("LoginDate"));
            ld.setLocation(map.get("Location"));
            ld.setLongitude(map.get("Longitude"));


            intent = new Intent(this, LoginMapScreenAdmin.class);
            int employeeId = Integer.parseInt(map.get("ManagerId"));
            intent.putExtra("EmployeeId",employeeId);
            intent.putExtra("Title",title);
            intent.putExtra("Message",body);
            Bundle bundle = new Bundle();
            bundle.putSerializable("Location", ld);
            intent.putExtras(bundle);
        }


        try{

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
                        /* .setStyle(new Notification.BigPictureStyle()
                                 .bigPicture(bigPicture))*/
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

        }catch (Exception e){
            e.printStackTrace();
        }

    }
    private void sendPopNotifications(String title, String body, Map<String, String> map) {
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        //  URL url = null;
        //  Bitmap bigPicture  = null;
       /* try {
            url = new URL(map.get("PictureUrl"));
            bigPicture = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        String message="";

        message = body;

        Intent intent = null;

        if(title.contains("Meeting Details from ")){
            intent = new Intent(this, EmployeeMeetingHost.class);
            int employeeId = Integer.parseInt(map.get("ManagerId"));
            intent.putExtra("EmployeeId",employeeId);
            intent.putExtra("Title",title);
            intent.putExtra("Message",body);
        }else  if(title.contains("Apply For Leave")){
            intent = new Intent(this, UpdateLeaveScreen.class);
            int employeeId = Integer.parseInt(map.get("EmployeeId"));
            String employeeName = map.get("EmployeeName");
            String from = map.get("FromDate");
            String to = map.get("ToDate");
            String reason = map.get("Reason");
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


        }else if(title.contains("Task Allocated")&& PreferenceHandler.getInstance(getApplicationContext()).getUserRoleUniqueID()!=2){

            intent = new Intent(this, TaskListScreen.class);
            int employeeId = Integer.parseInt(map.get("EmployeeId"));
            String employeeName = map.get("EmployeeName");
            String from = map.get("FromDate");
            String to = map.get("ToDate");
            String reason = map.get("Reason");
            String TaskId = map.get("TaskId");
            Bundle bundle = new Bundle();
            bundle.putInt("EmployeeId",employeeId);
            bundle.putInt("TaskId", Integer.parseInt(TaskId));
            intent.putExtras(bundle);


        }else if(title.contains("Location Request")&& PreferenceHandler.getInstance(getApplicationContext()).getUserId()==Integer.parseInt(map.get("EmployeeId"))){

            intent = new Intent(this, LocationSharingEmplActivity.class);
            int employeeId = Integer.parseInt(map.get("EmployeeId"));
            Bundle bundle = new Bundle();
            bundle.putInt("EmployeeId",employeeId);
            bundle.putString("Title",title);
            bundle.putString("Message",body);
            intent.putExtras(bundle);


        }else{
            intent = new Intent(this, LoginDetailsHost.class);
            int employeeId = Integer.parseInt(map.get("ManagerId"));
            intent.putExtra("EmployeeId",employeeId);
            intent.putExtra("Title",title);
            intent.putExtra("Message",body);
        }


        try{

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
                        /* .setStyle(new Notification.BigPictureStyle()
                                 .bigPicture(bigPicture))*/
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

        }catch (Exception e){
            e.printStackTrace();
        }


    }


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
            notificationManager.createNotificationChannel(mChannel);
        }

        notificationManager.notify(m, notificationBuilder.build());
    }




   /* @Override
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



    public void dataPay(RemoteMessage remoteMessage)
    {
        Log.e("dataChat",remoteMessage.toString());
        try
        {
            Map<String, String> params = remoteMessage.getData();
            JSONObject object = new JSONObject(remoteMessage.getData());
            Log.e("JSON_OBJECT", object.toString());
            System.out.println("JSON_Object Data Fire = "+object.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}