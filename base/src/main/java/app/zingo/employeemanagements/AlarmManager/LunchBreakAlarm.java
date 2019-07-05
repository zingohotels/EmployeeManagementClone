package app.zingo.employeemanagements.AlarmManager;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import java.net.URISyntaxException;

import app.zingo.employeemanagements.Service.LocationForegroundService;
import app.zingo.employeemanagements.UI.NewEmployeeDesign.EmployeeNewMainScreen;

import static android.support.v4.content.WakefulBroadcastReceiver.startWakefulService;


public class LunchBreakAlarm extends BroadcastReceiver {

    private static final String TAG = "LunchBreakAlarm";
    Intent activityIntent = null;


    @Override
    public void onReceive(Context context, Intent intent) {

        Toast.makeText(context, "Break ALARM!! ALARM!!", Toast.LENGTH_SHORT).show();
        if(intent!=null){

           String extra = intent.getStringExtra ("type");
           System.out.println("Suree LunchBreakAlarm "+extra);
          // context.startService(new Intent(context, AlarmSoundService.class));
           ComponentName comp = new ComponentName(context.getPackageName(), AlarmNotificationService.class.getName());
           startWakefulService(context, (intent.setComponent(comp)));
        }
    }
}