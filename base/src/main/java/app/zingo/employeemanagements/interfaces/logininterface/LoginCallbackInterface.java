package app.zingo.employeemanagements.interfaces.logininterface;
import android.location.Location;
import android.widget.TextView;

public interface LoginCallbackInterface {
    void executeTeaBreak (Location currentLocation, TextView textView );
    void executeLunchBreak (Location currentLocation, TextView textView );
    //void checkIn ();
}
