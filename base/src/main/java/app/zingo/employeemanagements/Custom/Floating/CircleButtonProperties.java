package app.zingo.employeemanagements.Custom.Floating;

import android.content.Context;

import java.io.Serializable;

public class CircleButtonProperties implements Serializable {

    private RFABSize standardSize;


    private int shadowColor;

    private int shadowRadius;

    private int shadowDx;

    private int shadowDy;

    public RFABSize getStandardSize() {
        return standardSize;
    }

    public CircleButtonProperties setStandardSize( RFABSize standardSize) {
        this.standardSize = standardSize;
        return this;
    }


    public int getRealSizePx(Context context) {
        return getStandardSizePx(context) + getShadowOffsetHalf() * 2;
    }

    public int getShadowOffsetHalf(){
        return 0 >= shadowRadius ? 0 : Math.max(shadowDx, shadowDy) + shadowRadius;
    }

    public int getStandardSizePx(Context context) {
        return RFABTextUtil.dip2px(context, standardSize.getDpSize());
    }

    public int getShadowColor() {
        return shadowColor;
    }

    public CircleButtonProperties setShadowColor( int shadowColor) {
        this.shadowColor = shadowColor;
        return this;
    }

    public float getShadowRadius() {
        return shadowRadius;
    }

    public CircleButtonProperties setShadowRadius( int shadowRadius) {
        this.shadowRadius = shadowRadius;
        return this;
    }

    public int getShadowDx() {
        return shadowDx;
    }

    public CircleButtonProperties setShadowDx( int shadowDx) {
        this.shadowDx = shadowDx;
        return this;
    }

    public int getShadowDy() {
        return shadowDy;
    }

    public CircleButtonProperties setShadowDy( int shadowDy) {
        this.shadowDy = shadowDy;
        return this;
    }
}
