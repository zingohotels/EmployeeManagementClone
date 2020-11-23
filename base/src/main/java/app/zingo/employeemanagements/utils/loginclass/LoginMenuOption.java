package app.zingo.employeemanagements.utils.loginclass;
import android.location.Location;
import android.view.View;
import android.widget.TextView;

import app.zingo.employeemanagements.interfaces.logininterface.LoginCallbackInterface;

public class LoginMenuOption {
    private LoginCallbackInterface mCommand;
    private String mCommandStatus;

    public LoginMenuOption ( LoginCallbackInterface loginCallbackInterface , String mCommandStatus ) {
        this.mCommand = loginCallbackInterface;
    }

    public void clickBreakTiming (int OrganizationTimingId){
        mCommand.executeBreakTiming (OrganizationTimingId);
    }
    public void clickShiftTiming (int OrganizationTimingId){
        mCommand.executeShiftTiming (OrganizationTimingId);
    }
    public void clickTeaBreak (Location currentLocation, TextView textView ){
        mCommand.executeTeaBreak (currentLocation,textView);
    }
    public void clickLunchBreak ( Location currentLocation , TextView textView ) {
        mCommand.executeLunchBreak (currentLocation,textView);
    }
    public void clickPunchIn ( String loginStatus , String type , Location currentLocation , View punchIn , View punchOut , TextView punchInText , TextView punchOutText ) {
        mCommand.executePunchIn (loginStatus,type, currentLocation, punchIn, punchOut, punchInText, punchOutText);
    }
    public void clickPunchOut ( String loginStatus , String type , Location currentLocation , View punchIn , View punchOut , TextView punchInText , TextView punchOutText ) {
        mCommand.executePunchOut (loginStatus,type, currentLocation, punchIn, punchOut, punchInText, punchOutText);
    }
}


