package app.zingo.employeemanagements.interfaces.logininterface;
import android.location.Location;
import android.view.View;
import android.widget.TextView;

public interface LoginCallbackInterface {
    void executeBreakTiming ( int organizationTimingId );
    void executeShiftTiming ( int organizationTimingId );
    void executeTeaBreak (Location currentLocation, TextView textView );
    void executeLunchBreak (Location currentLocation, TextView textView );
    void executePunchIn ( String loginStatus , String type, Location currentLocation, View punchIn , View punchOut , TextView punchInText , TextView punchOutText );
    void executePunchOut ( String loginStatus , String type, Location currentLocation, View punchIn , View punchOut , TextView punchInText , TextView punchOutText );
}
