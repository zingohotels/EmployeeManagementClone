package app.zingo.employeemanagements.Service;

import android.app.Activity;
import android.app.AlarmManager;
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
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.CountDownTimer;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import app.zingo.employeemanagements.ui.newemployeedesign.EmployeeNewMainScreen;
import app.zingo.employeemanagements.utils.PreferenceHandler;
import app.zingo.employeemanagements.base.R;

public class CheckInAlarmReceiverService extends IntentService {

    protected static final String TAG = CheckInAlarmReceiverService.class.getSimpleName();
    private MediaPlayer mMediaPlayer;

    public CheckInAlarmReceiverService() {
        // Use the TAG to name the worker thread.
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onHandleIntent(Intent intent) {

        mMediaPlayer = new MediaPlayer();

        playSound(getAlarmUri());
        String checkInTime = intent.getStringExtra("Time");
        String nextCheckInTime = intent.getStringExtra("NextTime");

        sendNotification("It is time to put Check-In.Mark your attendance without fail.");

        new CountDownTimer(5000, 1000) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                mMediaPlayer.stop();
                boolean loginPut = PreferenceHandler.getInstance(getApplicationContext()).isLoginPut();

                try {

                    Date cit = new SimpleDateFormat("hh:mm a").parse(checkInTime);
                    Date ncit = new SimpleDateFormat("hh:mm a").parse(nextCheckInTime);
                    Date currentTime = new SimpleDateFormat("hh:mm a").parse(new SimpleDateFormat("hh:mm a").format(new Date()));

                    Calendar checkTime = Calendar.getInstance();
                    int year = checkTime.get( Calendar.YEAR );
                    int month = checkTime.get( Calendar.MONTH );
                    int date = checkTime.get( Calendar.DATE );
                    checkTime.setTime(cit);
                    checkTime.set( Calendar.YEAR , year );
                    checkTime.set( Calendar.MONTH , month );
                    checkTime.set( Calendar.DATE , date );
                    checkTime.add(Calendar.MINUTE, 10);

                    Date checkDate = checkTime.getTime();

                    if (cit.getTime() > currentTime.getTime() && !loginPut) {

                        Calendar alaramTime = Calendar.getInstance();
                        alaramTime.add(Calendar.MINUTE, 5);

                        //Create a new PendingIntent and add it to the AlarmManager
                        Intent intent = new Intent(getApplicationContext(), CheckInAlarmReceiverService.class);
                        intent.putExtra("Time", checkInTime);
                        intent.putExtra("NextTime", nextCheckInTime);
                        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 12345, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                        AlarmManager am = (AlarmManager) getApplicationContext().getSystemService(Activity.ALARM_SERVICE);
                        am.set(AlarmManager.RTC_WAKEUP, alaramTime.getTimeInMillis(),
                                pendingIntent);


                    } else if (checkDate.getTime() > currentTime.getTime() && !loginPut) {

                        Calendar alaramTime = Calendar.getInstance();
                        alaramTime.add(Calendar.MINUTE, 5);

                        //Create a new PendingIntent and add it to the AlarmManager
                        Intent intent = new Intent(getApplicationContext(), CheckInAlarmReceiverService.class);
                        intent.putExtra("Time", checkInTime);
                        intent.putExtra("NextTime", nextCheckInTime);
                        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 12345, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                        AlarmManager am = (AlarmManager) getApplicationContext().getSystemService(Activity.ALARM_SERVICE);
                        am.set(AlarmManager.RTC_WAKEUP, alaramTime.getTimeInMillis(),
                                pendingIntent);

                    } else if (!loginPut) {

                        Calendar alaramTime = Calendar.getInstance();


                        int years = alaramTime.get( Calendar.YEAR );
                        int months = alaramTime.get( Calendar.MONTH );
                        int dates = alaramTime.get( Calendar.DATE );
                        alaramTime.setTime(ncit);
                        alaramTime.set( Calendar.YEAR , years );
                        alaramTime.set( Calendar.MONTH , months );
                        alaramTime.set( Calendar.DATE , dates );
                        alaramTime.add(Calendar.DAY_OF_YEAR, 1);
                        alaramTime.add(Calendar.MINUTE, -10);


                        //Create a new PendingIntent and add it to the AlarmManager
                        Intent intent = new Intent(getApplicationContext(), CheckInAlarmReceiverService.class);
                        intent.putExtra("Time", checkInTime);
                        intent.putExtra("NextTime", nextCheckInTime);
                        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 12345, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                        AlarmManager am = (AlarmManager) getApplicationContext().getSystemService(Activity.ALARM_SERVICE);
                        am.set(AlarmManager.RTC_WAKEUP, alaramTime.getTimeInMillis(),
                                pendingIntent);

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }.start();

    }

    private void playSound(Uri alert) {
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(getApplicationContext(), alert);
            final AudioManager audioManager = (AudioManager) getApplicationContext()
                    .getSystemService(Context.AUDIO_SERVICE);
            if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            }
        } catch (IOException e) {
            System.out.println("OOPS");
        }
    }

    //Get an alarm sound. Try for an alarm. If none set, try notification,
    //Otherwise, ringtone.
    private Uri getAlarmUri() {
        Uri alert = RingtoneManager
                .getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alert == null) {
            alert = RingtoneManager
                    .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            if (alert == null) {
                alert = RingtoneManager
                        .getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
        }
        return alert;
    }

    private void sendNotification(String msg) {

        Uri sound = null;
        sound = Uri.parse("android:resource://" + this.getPackageName() + "/" + R.raw.solemn);

        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        String message = "";
        message = msg;

        Intent intent = new Intent(this, EmployeeNewMainScreen.class);

        int m = 116;
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, m, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        int notifyID = 1;
        String CHANNEL_ID = "" + 116;// The id of the channel.
        CharSequence name = "CheckIn";// The user-visible name of the channel.
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(CHANNEL_ID, name, importance);

            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build();

            mChannel.enableLights(true);
            mChannel.enableVibration(true);
            mChannel.setSound(sound, audioAttributes);
        }

        Notification.Builder notificationBuilder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notificationBuilder = new Notification.Builder(this)
                    .setTicker("Reminder").setWhen(0)
                    .setContentTitle("Reminder")
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setSound(sound)
                    .setContentIntent(pendingIntent)
                    .setContentInfo("message")
                    .setLargeIcon(icon)
                    .setStyle(new Notification.BigTextStyle().bigText(message))
                    .setChannelId("116")
                    .setPriority(Notification.PRIORITY_MAX)
                    .setSmallIcon(R.mipmap.ic_launcher);
        } else {
            notificationBuilder = new Notification.Builder(this)
                    .setTicker("Reminder").setWhen(0)
                    .setContentTitle("Reminder")
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setSound(sound)
                    .setContentIntent(pendingIntent)
                    .setContentInfo(message)
                    .setLargeIcon(icon)
                    .setStyle(new Notification.BigTextStyle().bigText(message))
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
