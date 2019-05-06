package app.zingo.employeemanagements.Custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.google.android.gms.maps.MapView;

public class MapViewScroll extends MapView {

    public MapViewScroll(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
                System.out.println("unlocked");
                this.getParent().requestDisallowInterceptTouchEvent(false);
                break;

            case MotionEvent.ACTION_DOWN:
                System.out.println("locked");
                this.getParent().requestDisallowInterceptTouchEvent(true);
                break;
        }
        return super.dispatchTouchEvent(ev);
    }
}
