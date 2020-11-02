package app.zingo.employeemanagements.Custom.Floating;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

public class CircleButtonDrawable extends Drawable {
    private Context context;
    private Paint paint;

    private RectF bounds = new RectF();
    private float halfLen;

    private CircleButtonProperties circleButtonProperties;
    private int realSizePx;

    public CircleButtonDrawable( Context context, CircleButtonProperties circleButtonProperties, int color) {
        this.context = context;
        this.circleButtonProperties = circleButtonProperties;
        paint = new Paint();
        paint.setAntiAlias(true);

        paint.setFilterBitmap(true);
        paint.setDither(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);

        int dx = circleButtonProperties.getShadowDx();
        int dy = circleButtonProperties.getShadowDy();
        paint.setShadowLayer(circleButtonProperties.getShadowRadius(), dx, dy, circleButtonProperties.getShadowColor());

        realSizePx = this.circleButtonProperties.getRealSizePx(context);
        this.setBounds(0, 0, realSizePx, realSizePx);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        if (bounds.right - bounds.left > 0 && bounds.bottom - bounds.top > 0) {
            this.bounds.left = bounds.left;
            this.bounds.right = bounds.right;
            this.bounds.top = bounds.top;
            this.bounds.bottom = bounds.bottom;
            halfLen = Math.min(
                    (this.bounds.right - this.bounds.left) / 2,
                    (this.bounds.bottom - this.bounds.top) / 2
            );
            invalidateSelf();
        }
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawCircle(halfLen, halfLen, circleButtonProperties.getStandardSizePx(context) / 2, paint);
    }

    public Paint getPaint() {
        return paint;
    }

    public CircleButtonDrawable setColor( int color) {
        paint.setColor(color);
        return this;
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter cf) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.UNKNOWN;
    }
}
