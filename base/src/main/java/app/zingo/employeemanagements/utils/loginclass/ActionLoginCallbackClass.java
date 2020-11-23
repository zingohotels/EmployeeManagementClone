package app.zingo.employeemanagements.utils.loginclass;
import android.location.Location;
import android.view.View;
import android.widget.TextView;

import app.zingo.employeemanagements.interfaces.logininterface.LoginCallbackInterface;

public class ActionLoginCallbackClass implements LoginCallbackInterface {
   private LoginCallbackClass loginCallbackClass;

   public ActionLoginCallbackClass(LoginCallbackClass loginCallbackClass){
       this.loginCallbackClass = loginCallbackClass;
   }

    @Override
    public void executeBreakTiming ( int organizationTimingId ) {
        loginCallbackClass.breakTiming (organizationTimingId);
    }

    @Override
    public void executeShiftTiming ( int organizationTimingId ) {
        loginCallbackClass.shiftTiming (organizationTimingId);
    }

    @Override
    public void executePunchIn ( String loginStatus , String type, Location currentLocation, View punchIn , View punchOut , TextView punchInText , TextView punchOutText) {
        loginCallbackClass.punchIn (loginStatus, type, currentLocation, punchIn , punchOut , punchInText , punchOutText);
    }

    @Override
    public void executePunchOut ( String loginStatus , String type, Location currentLocation, View punchIn , View punchOut , TextView punchInText , TextView punchOutText) {
        loginCallbackClass.punchOut (loginStatus, type, currentLocation, punchIn , punchOut , punchInText , punchOutText);
    }

    @Override
    public void executeTeaBreak ( Location currentLocation , TextView textView ) {
       loginCallbackClass.teaBreak (currentLocation, textView);
    }
    @Override
    public void executeLunchBreak ( Location currentLocation , TextView textView ) {
       loginCallbackClass.lunchBreak ( currentLocation, textView);
    }
}
