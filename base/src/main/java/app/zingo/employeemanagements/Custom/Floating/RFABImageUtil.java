package app.zingo.employeemanagements.Custom.Floating;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;

public class RFABImageUtil {
    public static final String TAG = RFABImageUtil.class.getSimpleName();


    public static Drawable getResourceDrawableBounded(Context context, int drawableResId, int bound) {
        Drawable drawable = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                drawable = context.getResources().getDrawable(drawableResId, null);
            } else {
                drawable = context.getResources().getDrawable(drawableResId);
            }
            drawable.setBounds(0, 0, bound, bound);
        } catch (Exception ex) {
            Log.e(TAG, "", ex);
        }
        return drawable;
    }

}
