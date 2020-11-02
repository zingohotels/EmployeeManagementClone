package app.zingo.employeemanagements.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import app.zingo.employeemanagements.utils.PreferenceHandler;

public class OnGPSNotificationClick extends BroadcastReceiver {
    @Override
    public void onReceive( Context context, Intent intent) {
        //Open your activity here
        Intent intents = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        intents.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );

        intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intents);

        // Do your onClick related logic here
        PreferenceHandler.getInstance( context ).setGPSNotificationClick(false);
    }

}