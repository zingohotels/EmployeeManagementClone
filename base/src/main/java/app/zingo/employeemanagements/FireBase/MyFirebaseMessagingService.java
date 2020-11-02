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
import android.provider.Settings;
import androidx.core.app.NotificationCompat;
import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import java.net.URL;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import app.zingo.employeemanagements.model.LeaveNotificationManagers;
import app.zingo.employeemanagements.model.LoginDetailsNotificationManagers;
import app.zingo.employeemanagements.model.MeetingDetailsNotificationManagers;
import app.zingo.employeemanagements.model.StockOrdersModel;
import app.zingo.employeemanagements.ui.Admin.EmployeeLiveMappingScreen;
import app.zingo.employeemanagements.ui.Admin.LoginDetailsHost;
import app.zingo.employeemanagements.ui.Admin.TaskListScreen;
import app.zingo.employeemanagements.ui.Common.FakeActivityScreen;
import app.zingo.employeemanagements.ui.Common.GeneralNotificationScreen;
import app.zingo.employeemanagements.ui.Common.InvokeService;
import app.zingo.employeemanagements.ui.Common.LocationSharingEmplActivity;
import app.zingo.employeemanagements.ui.Employee.EmployeeMeetingHost;
import app.zingo.employeemanagements.ui.NewAdminDesigns.ExpensesListAdmin;
import app.zingo.employeemanagements.ui.NewAdminDesigns.LoginMapScreenAdmin;
import app.zingo.employeemanagements.ui.NewAdminDesigns.MeetingMapScreen;
import app.zingo.employeemanagements.ui.NewAdminDesigns.StockOrderDetailsScreen;
import app.zingo.employeemanagements.ui.NewAdminDesigns.UpdateLeaveScreen;
import app.zingo.employeemanagements.ui.newemployeedesign.EmployeeNewMainScreen;
import app.zingo.employeemanagements.ui.newemployeedesign.UpdateWeekOff;
import app.zingo.employeemanagements.utils.PreferenceHandler;
import app.zingo.employeemanagements.base.R;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public int count = 0;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    public static final String NOTIFICATION_CHANNEL_ID = "KRONY1";

    @Override
    public void onMessageReceived ( RemoteMessage remoteMessage ) {
        super.onMessageReceived(remoteMessage);
        try {
            RemoteMessage.Notification notification = remoteMessage.getNotification ( );
            Map < String, String > map = remoteMessage.getData ( );
            Log.e("dataChat",remoteMessage.getData().toString());

            if ( PreferenceHandler.getInstance ( getApplicationContext ( ) ).getUserId ( ) != 0 ) {
                if(map!=null){
                    String title =map.get ( "Title" );
                    String message =map.get ( "Message" );

                    if(title!=null&&!title.isEmpty ()){
                        if(PreferenceHandler.getInstance ( getApplicationContext ( ) ).getUserId ( ) == Integer.parseInt ( Objects.requireNonNull ( map.get ( "EmployeeId" ) ) )){
                            if(( PreferenceHandler.getInstance ( getApplicationContext ( ) ).getUserRoleUniqueID ( ) == 2 || PreferenceHandler.getInstance ( getApplicationContext ( ) ).getUserRoleUniqueID ( ) == 9 )  ){
                                if(( Objects.requireNonNull ( title ).equalsIgnoreCase ( "Location Shared" )|| title.contains ( "Stock Order" ) || title.equalsIgnoreCase ( "Fake Activity" )|| title.equalsIgnoreCase ( "Expenses" )|| ( title.contains ( "Apply For Leave" ) || title.contains ( "Apply For WeekOff" ) || title.contains ( "Meeting Details from " ) ||title.contains ( "Login Details from " ) ||title.contains ( "Break taken from " ) )  )){
                                    try {
                                        sendPopNotification ( title ,message , map );
                                    } catch ( Exception e ) {
                                        e.printStackTrace ( );
                                    }
                                }
                            }else{
                                if(( Objects.requireNonNull ( title ).equalsIgnoreCase ( "Task Allocated" ) || title.equalsIgnoreCase ( "Location Request" ) )){
                                    try {
                                        sendPopNotifications ( title , message , map );
                                    } catch ( Exception e ) {
                                        e.printStackTrace ( );
                                    }
                                }
                            }

                            if ( title.equalsIgnoreCase ( "Test" ) ){
                                try {
                                    sendPopNotificationGeneral ( title , message, map );
                                } catch ( Exception e ) {
                                    e.printStackTrace ( );
                                }
                            }
                        }
                    }
                }else{
                    if ( notification != null ) {
                        if ( Objects.requireNonNull ( notification.getTitle ( ) ).equalsIgnoreCase ( "Checkout Notification" ) ) {
                            try {
                                checkCondition ( notification.getTitle ( ) , notification.getBody ( ) , map );
                            } catch ( Exception e ) {
                                e.printStackTrace ( );
                            }

                        } else if ( notification.getTitle ( ).equalsIgnoreCase ( "App Info Notification" ) ) {
                            try {
                                sendPopNotificationGeneral ( notification.getTitle ( ) , notification.getBody ( ) , map );
                            } catch ( Exception e ) {
                                e.printStackTrace ( );
                            }

                        } else if ( notification.getTitle ( ).equalsIgnoreCase ( "Invoke Service" ) ) {
                            if ( PreferenceHandler.getInstance ( getApplicationContext ( ) ).getUserId ( ) == 20 ) {
                                Bitmap icon = BitmapFactory.decodeResource ( getResources ( ) , R.mipmap.ic_launcher );
                                String message = "";
                                Intent intent = new Intent ( this , InvokeService.class );

                                intent.putExtra ( "Title" , notification.getTitle ( ) );
                                intent.putExtra ( "Message" , notification.getBody ( ) );
                                //  Uri sound = Uri.parse("android.resource://" + this.getPackageName() + "/raw/good_morning");
                                int m = ( int ) ( ( new Date ( ).getTime ( ) / 1000L ) % Integer.MAX_VALUE );

                                intent.setFlags ( Intent.FLAG_ACTIVITY_CLEAR_TOP );
                                intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                                PendingIntent pendingIntent = PendingIntent.getActivity ( this , m , intent , PendingIntent.FLAG_UPDATE_CURRENT );

                                String CHANNEL_ID = "" + 1;// The id of the channel.
                                CharSequence name = "Zingo";// The user-visible name of the channel.
                                int importance = 0;
                                if ( android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N ) {
                                    importance = NotificationManager.IMPORTANCE_LOW;
                                }
                                NotificationChannel mChannel = null;
                                if ( android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O ) {
                                    mChannel = new NotificationChannel ( CHANNEL_ID , name , importance );
                                }

                                Notification.Builder notificationBuilder ;
                                if ( android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O ) {
                                    notificationBuilder = new Notification.Builder ( this )
                                            .setTicker ( notification.getTitle ( ) ).setWhen ( 0 )
                                            .setContentTitle ( notification.getTitle ( ) )
                                            .setContentText ( message )
                                            .setAutoCancel ( true )
                                            //.setFullScreenIntent(pendingIntent,false)
                                            //.setNumber()
                                            .setContentIntent ( pendingIntent )
                                            .setContentInfo ( notification.getTitle ( ) )
                                            .setLargeIcon ( icon )
                                            .setChannelId ( "1" )
                                            .setPriority ( Notification.PRIORITY_MAX )
                                            // .setPriority(NotificationManager.IMPORTANCE_HIGH)
                                            .setSmallIcon ( R.mipmap.ic_launcher );
                                } else {
                                    notificationBuilder = new Notification.Builder ( this )
                                            .setTicker ( notification.getTitle ( ) ).setWhen ( 0 )
                                            .setContentTitle ( notification.getTitle ( ) )
                                            .setContentText ( message )
                                            .setAutoCancel ( true )
                                            //.setFullScreenIntent(pendingIntent,false)
                                            .setContentIntent ( pendingIntent )
                                            .setContentInfo ( notification.getTitle ( ) )
                                            .setLargeIcon ( icon )
                                            .setPriority ( Notification.PRIORITY_MAX )
                                            .setNumber ( count )
                                            // .setPriority(NotificationManager.IMPORTANCE_HIGH)
                                            .setSmallIcon ( R.mipmap.ic_launcher );
                                }

                                notificationBuilder.setDefaults ( Notification.DEFAULT_VIBRATE );
                                notificationBuilder.setLights ( Color.YELLOW , 1000 , 300 );

                                NotificationManager notificationManager = ( NotificationManager ) getSystemService ( Context.NOTIFICATION_SERVICE );
                                if ( android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O ) {
                                    notificationManager.createNotificationChannel ( mChannel );
                                }
                                notificationManager.notify ( m , notificationBuilder.build ( ) );
                            }
                        }
                    }
                }
            }

        } catch ( Exception e ) {
            e.printStackTrace ( );
        }
    }

    private void sendPopNotification ( String title , String body , Map < String, String > map ) {
        Bitmap icon = BitmapFactory.decodeResource ( getResources ( ) , R.mipmap.ic_launcher );
        String message = "";
        message = body;
        Intent intent = null;

        if ( title.contains ( "Meeting Details from " ) ) {
            MeetingDetailsNotificationManagers ld = new MeetingDetailsNotificationManagers ( );
            ld.setEmployeeId ( Integer.parseInt ( Objects.requireNonNull ( map.get ( "ManagerId" ) ) ) );
            ld.setManagerId ( Integer.parseInt ( Objects.requireNonNull ( map.get ( "EmployeeId" ) ) ) );
            ld.setStatus ( map.get ( "Status" ) );
            ld.setMessage ( map.get ( "Message" ) );
            ld.setMeetingsId ( Integer.parseInt ( Objects.requireNonNull ( map.get ( "MeetingsId" ) ) ) );
            ld.setLatitude ( map.get ( "Latitude" ) );
            ld.setTitle ( map.get ( "Title" ) );
            ld.setMeetingDate ( map.get ( "LoginDate" ) );
            ld.setLocation ( map.get ( "Location" ) );
            ld.setLongitude ( map.get ( "Longitude" ) );
            intent = new Intent ( this , MeetingMapScreen.class );

            Bundle bundle = new Bundle ( );
            bundle.putSerializable ( "Location" , ld );

            int employeeId = Integer.parseInt ( Objects.requireNonNull ( map.get ( "ManagerId" ) ) );
            intent.putExtra ( "EmployeeId" , employeeId );
            intent.putExtra ( "Title" , title );
            intent.putExtra ( "Message" , body );
            intent.putExtras ( bundle );

        } else if ( title.contains ( "Fake Activity" ) && PreferenceHandler.getInstance ( getApplicationContext ( ) ).getUserId ( ) == Integer.parseInt ( map.get ( "EmployeeId" ) ) ) {

            intent = new Intent ( this , FakeActivityScreen.class );
            int employeeId = Integer.parseInt ( Objects.requireNonNull ( map.get ( "EmployeeId" ) ) );
            Bundle bundle = new Bundle ( );
            bundle.putInt ( "EmployeeId" , employeeId );
            bundle.putString ( "Title" , title );
            bundle.putString ( "Message" , body );
            intent.putExtras ( bundle );

        } else if ( title.contains ( "Expense" ) && PreferenceHandler.getInstance ( getApplicationContext ( ) ).getUserId ( ) == Integer.parseInt ( map.get ( "EmployeeId" ) ) ) {
           /* intent = new Intent ( this , FakeActivityScreen.class );
            int employeeId = Integer.parseInt ( Objects.requireNonNull ( map.get ( "EmployeeId" ) ) );
            Bundle bundle = new Bundle ( );
            bundle.putInt ( "EmployeeId" , employeeId );
            bundle.putString ( "Title" , title );
            bundle.putString ( "Message" , body );
            intent.putExtras ( bundle );*/

            intent = new Intent(this, ExpensesListAdmin.class);
            intent.putExtra("Type","Expense");

        } else if ( title.contains ( "Stock Order" )  ) {

            Gson gson = new Gson ();
            StockOrdersModel som = gson.fromJson(body, StockOrdersModel.class);
            intent = new Intent ( this , StockOrderDetailsScreen.class );
            intent.putExtra ( "Type","Stock Orders" );
            message = "You got order";
            int employeeId = Integer.parseInt ( Objects.requireNonNull ( map.get ( "EmployeeId" ) ) );
            Bundle bundle = new Bundle ( );
            bundle.putSerializable ( "StockOrders",som );
            bundle.putInt ( "EmployeeId" , employeeId );
            bundle.putString ( "Title" , title );
            bundle.putString ( "Message" , body );
            intent.putExtras ( bundle );

        } else if ( title.contains ( "Apply For Leave" ) ) {

            intent = new Intent ( this , UpdateLeaveScreen.class );
            int employeeId = Integer.parseInt ( Objects.requireNonNull ( map.get ( "ManagerId" ) ) );
            String employeeName = map.get ( "EmployeeName" );
            String from = map.get ( "FromDate" );
            String to = map.get ( "ToDate" );
            String reason = map.get ( "Reason" );
            //messageText = "From " + employeeName;
            String LeaveId = map.get ( "LeaveId" );
            LeaveNotificationManagers lm = new LeaveNotificationManagers ( );
            lm.setEmployeeName ( employeeName );
            lm.setFromDate ( from );
            lm.setToDate ( to );
            lm.setReason ( reason );
            lm.setEmployeeId ( employeeId );
            Bundle bundle = new Bundle ( );
            bundle.putSerializable ( "LeaveNotification" , lm );
            bundle.putInt ( "EmployeeId" , employeeId );
            bundle.putInt ( "LeaveId" , Integer.parseInt ( LeaveId ) );
            intent.putExtras ( bundle );

        } else if ( title.contains ( "Apply For WeekOff" ) ) {

            intent = new Intent ( this , UpdateWeekOff.class );
            int employeeId = Integer.parseInt ( map.get ( "ManagerId" ) );
            String employeeName = map.get ( "EmployeeName" );
            String from = map.get ( "FromDate" );
            String to = map.get ( "ToDate" );
            String reason = map.get ( "Reason" );
            //messageText = "From " + employeeName;
            String LeaveId = map.get ( "LeaveId" );
            LeaveNotificationManagers lm = new LeaveNotificationManagers ( );
            lm.setEmployeeName ( employeeName );
            lm.setFromDate ( from );
            lm.setToDate ( to );
            lm.setReason ( reason );
            lm.setEmployeeId ( employeeId );
            Bundle bundle = new Bundle ( );
            bundle.putSerializable ( "LeaveNotification" , lm );
            bundle.putInt ( "EmployeeId" , employeeId );
            bundle.putInt ( "LeaveId" , Integer.parseInt ( LeaveId ) );
            intent.putExtras ( bundle );

        } else if ( title.contains ( "Location Shared" ) && PreferenceHandler.getInstance ( getApplicationContext ( ) ).getUserId ( ) == Integer.parseInt ( map.get ( "EmployeeId" ) ) ) {

            intent = new Intent ( this , EmployeeLiveMappingScreen.class );
            int employeeId = Integer.parseInt ( Objects.requireNonNull ( map.get ( "EmployeeId" ) ) );
            String[] aarr= body.split ( "%" );

            Bundle bundle = new Bundle ( );
            bundle.putInt ( "EmployeeId" , Integer.parseInt ( aarr[ 1 ] ) );
            bundle.putString ( "Title" , title );
            bundle.putString ( "Message" , body );
            intent.putExtras ( bundle );

        } else if ( title.contains ( "Task Allocated" ) && PreferenceHandler.getInstance ( getApplicationContext ( ) ).getUserRoleUniqueID ( ) != 2 ) {

            intent = new Intent ( this , TaskListScreen.class );
            int employeeId = Integer.parseInt ( Objects.requireNonNull ( map.get ( "EmployeeId" ) ) );
            String employeeName = map.get ( "EmployeeName" );
            String from = map.get ( "FromDate" );
            String to = map.get ( "ToDate" );
            String reason = map.get ( "Reason" );
            String TaskId = map.get ( "TaskId" );
            Bundle bundle = new Bundle ( );
            bundle.putInt ( "EmployeeId" , employeeId );
            if ( TaskId != null ) {
                bundle.putInt ( "TaskId" , Integer.parseInt ( TaskId ) );
            }
            intent.putExtras ( bundle );


        } else if ( (title.contains ( "Login Details from " )||title.contains ( "Break taken from " ) )&& ( PreferenceHandler.getInstance ( getApplicationContext ( ) ).getUserRoleUniqueID ( ) == 2 || PreferenceHandler.getInstance ( getApplicationContext ( ) ).getUserRoleUniqueID ( ) == 9 ) ) {
            LoginDetailsNotificationManagers ld = new LoginDetailsNotificationManagers ( );
            ld.setEmployeeId ( Integer.parseInt ( Objects.requireNonNull ( map.get ( "ManagerId" ) ) ) );
            ld.setManagerId ( Integer.parseInt ( Objects.requireNonNull ( map.get ( "EmployeeId" ) ) ) );
            ld.setStatus ( map.get ( "Status" ) );
            ld.setMessage ( map.get ( "Message" ) );
            ld.setLoginDetailsId ( Integer.parseInt ( Objects.requireNonNull ( map.get ( "LoginDetailsId" ) ) ) );
            ld.setLatitude ( map.get ( "Latitude" ) );
            ld.setTitle ( map.get ( "Title" ) );
            ld.setLoginDate ( map.get ( "LoginDate" ) );
            ld.setLocation ( map.get ( "Location" ) );
            ld.setLongitude ( map.get ( "Longitude" ) );

            intent = new Intent ( this , LoginMapScreenAdmin.class );
            int employeeId = Integer.parseInt ( map.get ( "ManagerId" ) );
            intent.putExtra ( "EmployeeId" , employeeId );
            intent.putExtra ( "Title" , title );
            intent.putExtra ( "Message" , body );
            Bundle bundle = new Bundle ( );
            bundle.putSerializable ( "Location" , ld );
            intent.putExtras ( bundle );
        }

        try {
            //  Uri sound = Uri.parse("android.resource://" + this.getPackageName() + "/raw/good_morning");
            int m = ( int ) ( ( new Date ( ).getTime ( ) / 1000L ) % Integer.MAX_VALUE );
            intent.setFlags ( Intent.FLAG_ACTIVITY_CLEAR_TOP );

            intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            PendingIntent pendingIntent = PendingIntent.getActivity ( this , m , intent , PendingIntent.FLAG_UPDATE_CURRENT );

            mBuilder = new NotificationCompat.Builder(this);
            mBuilder.setSmallIcon(R.mipmap.ic_launcher);
            mBuilder.setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(false)
                    .setSound( Settings.System.DEFAULT_NOTIFICATION_URI)
                    .setContentIntent(pendingIntent);

            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.RED);
                notificationChannel.enableVibration(true);
                notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                assert mNotificationManager != null;
                mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
                mNotificationManager.createNotificationChannel(notificationChannel);
            }
            assert mNotificationManager != null;
            mNotificationManager.notify(0 /* Request Code */, mBuilder.build());

            /*int notifyID = 1;
            String CHANNEL_ID = "" + 1;// The id of the channel.
            CharSequence name = "Zingo";// The user-visible name of the channel.
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = null;
            if ( android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O ) {
                mChannel = new NotificationChannel ( CHANNEL_ID , name , importance );
            }

            Notification.Builder notificationBuilder = null;
            if ( android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O ) {
                notificationBuilder = new Notification.Builder ( this )
                        .setTicker ( title ).setWhen ( 0 )
                        .setContentTitle ( title )
                        .setContentText ( message )
                        .setAutoCancel ( true )
                        //.setFullScreenIntent(pendingIntent,false)
                        //.setNumber()
                        .setContentIntent ( pendingIntent )
                        .setContentInfo ( title )
                        .setLargeIcon ( icon )
                        .setChannelId ( "1" )
                        *//* .setStyle(new Notification.BigPictureStyle()
                                 .bigPicture(bigPicture))*//*
                        .setPriority ( Notification.PRIORITY_MAX )

                        // .setPriority(NotificationManager.IMPORTANCE_HIGH)
                        .setSmallIcon ( R.mipmap.ic_launcher );
            } else {
                notificationBuilder = new Notification.Builder ( this )
                        .setTicker ( title ).setWhen ( 0 )
                        .setContentTitle ( title )
                        .setContentText ( message )
                        .setAutoCancel ( true )
                        //.setFullScreenIntent(pendingIntent,false)
                        .setContentIntent ( pendingIntent )
                        .setContentInfo ( title )
                        .setLargeIcon ( icon )
                        *//*.setStyle(new Notification.BigPictureStyle()
                                .bigPicture(bigPicture))*//*
                        .setPriority ( Notification.PRIORITY_MAX )

                        .setNumber ( count )
                        // .setPriority(NotificationManager.IMPORTANCE_HIGH)
                        .setSmallIcon ( R.mipmap.ic_launcher );
            }


            notificationBuilder.setDefaults ( Notification.DEFAULT_VIBRATE );
            notificationBuilder.setLights ( Color.YELLOW , 1000 , 300 );


            NotificationManager notificationManager = ( NotificationManager ) getSystemService ( Context.NOTIFICATION_SERVICE );
            if ( android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O ) {
                notificationManager.createNotificationChannel ( mChannel );
            }

            notificationManager.notify ( m , notificationBuilder.build ( ) );*/

        } catch ( Exception e ) {
            e.printStackTrace ( );
        }
    }

    private void sendPopNotifications ( String title , String body , Map < String, String > map ) {
        Bitmap icon = BitmapFactory.decodeResource ( getResources ( ) , R.mipmap.ic_launcher );

        //  URL url = null;
        //  Bitmap bigPicture  = null;
       /* try {
            url = new URL(map.get("PictureUrl"));
            bigPicture = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        String message = "";

        message = body;

        Intent intent = null;

        if ( title.contains ( "Meeting Details from " ) ) {
            intent = new Intent ( this , EmployeeMeetingHost.class );
            int employeeId = Integer.parseInt ( map.get ( "ManagerId" ) );
            intent.putExtra ( "EmployeeId" , employeeId );
            intent.putExtra ( "Title" , title );
            intent.putExtra ( "Message" , body );
        } else if ( title.contains ( "Apply For Leave" ) ) {
            intent = new Intent ( this , UpdateLeaveScreen.class );
            int employeeId = Integer.parseInt ( map.get ( "EmployeeId" ) );
            String employeeName = map.get ( "EmployeeName" );
            String from = map.get ( "FromDate" );
            String to = map.get ( "ToDate" );
            String reason = map.get ( "Reason" );
            String LeaveId = map.get ( "LeaveId" );
            LeaveNotificationManagers lm = new LeaveNotificationManagers ( );
            lm.setEmployeeName ( employeeName );
            lm.setFromDate ( from );
            lm.setToDate ( to );
            lm.setReason ( reason );
            lm.setEmployeeId ( employeeId );
            Bundle bundle = new Bundle ( );
            bundle.putSerializable ( "LeaveNotification" , lm );
            bundle.putInt ( "EmployeeId" , employeeId );
            bundle.putInt ( "LeaveId" , Integer.parseInt ( LeaveId ) );
            intent.putExtras ( bundle );

        } else if ( title.contains ( "Task Allocated" ) && PreferenceHandler.getInstance ( getApplicationContext ( ) ).getUserRoleUniqueID ( ) != 2 ) {

            intent = new Intent ( this , TaskListScreen.class );
            int employeeId = Integer.parseInt ( map.get ( "EmployeeId" ) );
            String employeeName = map.get ( "EmployeeName" );
            String from = map.get ( "FromDate" );
            String to = map.get ( "ToDate" );
            String reason = map.get ( "Reason" );
            String TaskId = map.get ( "TaskId" );
            Bundle bundle = new Bundle ( );
            bundle.putInt ( "EmployeeId" , employeeId );
            bundle.putInt ( "TaskId" , Integer.parseInt ( TaskId ) );
            intent.putExtras ( bundle );

        } else if ( title.contains ( "Location Request" ) && PreferenceHandler.getInstance ( getApplicationContext ( ) ).getUserId ( ) == Integer.parseInt ( map.get ( "EmployeeId" ) ) ) {

            intent = new Intent ( this , LocationSharingEmplActivity.class );
            int employeeId = Integer.parseInt ( map.get ( "EmployeeId" ) );
            Bundle bundle = new Bundle ( );
            bundle.putInt ( "EmployeeId" , employeeId );
            bundle.putString ( "Title" , title );
            bundle.putString ( "Message" , body );
            intent.putExtras ( bundle );

        } else {
            intent = new Intent ( this , LoginDetailsHost.class );
            int employeeId = Integer.parseInt ( map.get ( "ManagerId" ) );
            intent.putExtra ( "EmployeeId" , employeeId );
            intent.putExtra ( "Title" , title );
            intent.putExtra ( "Message" , body );
        }

        try {
            //  Uri sound = Uri.parse("android.resource://" + this.getPackageName() + "/raw/good_morning");
            int m = ( int ) ( ( new Date ( ).getTime ( ) / 1000L ) % Integer.MAX_VALUE );
            intent.setFlags ( Intent.FLAG_ACTIVITY_CLEAR_TOP );
            intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            PendingIntent pendingIntent = PendingIntent.getActivity ( this , m , intent , PendingIntent.FLAG_UPDATE_CURRENT );

            mBuilder = new NotificationCompat.Builder(this);
            mBuilder.setSmallIcon(R.mipmap.ic_launcher);
            mBuilder.setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(false)
                    .setSound( Settings.System.DEFAULT_NOTIFICATION_URI)
                    .setContentIntent(pendingIntent);

            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.RED);
                notificationChannel.enableVibration(true);
                notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                assert mNotificationManager != null;
                mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
                mNotificationManager.createNotificationChannel(notificationChannel);
            }
            assert mNotificationManager != null;
            mNotificationManager.notify(0 /* Request Code */, mBuilder.build());

        } catch ( Exception e ) {
            e.printStackTrace ( );
        }
    }

    private void sendPopNotificationGeneral ( String title , String body , Map < String, String > map ) {
        Bitmap icon = BitmapFactory.decodeResource ( getResources ( ) , R.mipmap.ic_launcher );
        URL url = null;
        Bitmap bigPicture = null;
        try {
            url = new URL ( map.get ( "PictureUrl" ) );
            bigPicture = BitmapFactory.decodeStream ( url.openConnection ( ).getInputStream ( ) );
        } catch ( Exception e ) {
            e.printStackTrace ( );
        }

        String message = "";

        message = body;

        Intent intent = new Intent ( this , GeneralNotificationScreen.class );
        intent.putExtra ( "Title" , title );
        intent.putExtra ( "Message" , body );

        //  Uri sound = Uri.parse("android.resource://" + this.getPackageName() + "/raw/good_morning");
        int m = ( int ) ( ( new Date ( ).getTime ( ) / 1000L ) % Integer.MAX_VALUE );

        intent.setFlags ( Intent.FLAG_ACTIVITY_CLEAR_TOP );

        intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
        PendingIntent pendingIntent = PendingIntent.getActivity ( this , m , intent , PendingIntent.FLAG_UPDATE_CURRENT );

        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(false)
                .setSound( Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentIntent(pendingIntent);

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            assert mNotificationManager != null;
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        assert mNotificationManager != null;
        mNotificationManager.notify(0 /* Request Code */, mBuilder.build());
    }

    private void checkCondition ( String title , String body , Map < String, String > map ) {
        if ( PreferenceHandler.getInstance ( getApplicationContext ( ) ).getUserId ( ) != 0 && PreferenceHandler.getInstance ( getApplicationContext ( ) ).getUserRoleUniqueID ( ) != 2 && PreferenceHandler.getInstance ( getApplicationContext ( ) ).getLoginStatus ( ).equalsIgnoreCase ( "Login" ) ) {
        } else {
            Bitmap icon = BitmapFactory.decodeResource ( getResources ( ) , R.mipmap.ic_launcher );
            String message = "";
            message = body;

            Intent intent = new Intent ( this , EmployeeNewMainScreen.class );
            Bundle bundle = new Bundle ( );
            bundle.putBoolean ( "AlarmDialog" , true );
            intent.putExtra ( "Title" , title );
            intent.putExtra ( "Message" , body );
            intent.putExtras ( bundle );

            //  Uri sound = Uri.parse("android.resource://" + this.getPackageName() + "/raw/good_morning");
            int m = ( int ) ( ( new Date ( ).getTime ( ) / 1000L ) % Integer.MAX_VALUE );
            PreferenceHandler.getInstance ( getApplicationContext ( ) ).setNotificationReceive ( true );
            PendingIntent pendingIntent = PendingIntent.getActivity ( this , m , intent , PendingIntent.FLAG_UPDATE_CURRENT );

            mBuilder = new NotificationCompat.Builder(this);
            mBuilder.setSmallIcon(R.mipmap.ic_launcher);
            mBuilder.setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(false)
                    .setSound( Settings.System.DEFAULT_NOTIFICATION_URI)
                    .setContentIntent(pendingIntent);

            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.RED);
                notificationChannel.enableVibration(true);
                notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                assert mNotificationManager != null;
                mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
                mNotificationManager.createNotificationChannel(notificationChannel);
            }
            assert mNotificationManager != null;
            mNotificationManager.notify(0 /* Request Code */, mBuilder.build());
        }
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
}