package app.zingo.employeemanagements.utils.loginclass;
import android.location.Location;
import android.widget.TextView;

import app.zingo.employeemanagements.interfaces.logininterface.LoginCallbackInterface;

public class ActionLoginCallbackClass implements LoginCallbackInterface {
   private LoginCallbackClass loginCallbackClass;

   public ActionLoginCallbackClass(LoginCallbackClass loginCallbackClass){
       this.loginCallbackClass = loginCallbackClass;
   }

  /*  @Override
    public void checkIn ( ) {
    }
*/
    @Override
    public void executeTeaBreak ( Location currentLocation , TextView textView ) {
       loginCallbackClass.teaBreak (currentLocation, textView);
    }
    @Override
    public void executeLunchBreak ( Location currentLocation , TextView textView ) {
       loginCallbackClass.lunchBreak ( currentLocation, textView);
    }

}
