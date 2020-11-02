package app.zingo.employeemanagements.utils.loginclass;
import android.location.Location;
import android.widget.TextView;

import app.zingo.employeemanagements.interfaces.logininterface.LoginCallbackInterface;

public class LoginMenuOption {
    private LoginCallbackInterface mCommand;
    private String mCommandStatus;

    public LoginMenuOption ( LoginCallbackInterface loginCallbackInterface , String mCommandStatus ) {
        this.mCommand = loginCallbackInterface;
    }

    public void clickTeaBreak (Location currentLocation, TextView textView ){
        mCommand.executeTeaBreak (currentLocation,textView);
    }
    public void clickLunchBreak ( Location currentLocation , TextView textView ) {
        mCommand.executeLunchBreak (currentLocation,textView);
    }
}


