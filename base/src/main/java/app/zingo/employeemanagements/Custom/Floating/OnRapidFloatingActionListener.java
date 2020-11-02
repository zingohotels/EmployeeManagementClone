package app.zingo.employeemanagements.Custom.Floating;

import android.animation.AnimatorSet;

public interface OnRapidFloatingActionListener {

    void onRFABClick ( );


    void toggleContent ( );


    void expandContent ( );


    void collapseContent ( );


    RapidFloatingActionLayout obtainRFALayout ( );


    RapidFloatingActionButton obtainRFAButton ( );


    RapidFloatingActionContent obtainRFAContent ( );


    void onExpandAnimator ( AnimatorSet animatorSet );


    void onCollapseAnimator ( AnimatorSet animatorSet );

}
