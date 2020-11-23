package app.zingo.employeemanagements.ui.landing;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;

import app.zingo.employeemanagements.FireBase.SharedPrefManager;
import app.zingo.employeemanagements.ui.Company.CreateFounderScreen;
import app.zingo.employeemanagements.ui.LandingScreen;
import app.zingo.employeemanagements.ui.NewAdminDesigns.AdminNewMainScreen;
import app.zingo.employeemanagements.ui.newemployeedesign.EmployeeNewMainScreen;
import app.zingo.employeemanagements.ui.Reseller.ResellerMainActivity;
import app.zingo.employeemanagements.utils.PreferenceHandler;
import app.zingo.employeemanagements.base.BuildConfig;
import app.zingo.employeemanagements.base.R;

public class SplashScreen extends AppCompatActivity {
    TextView mVersionName;
    Context mContext = this;

    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate ( savedInstanceState );
        try {
            requestWindowFeature ( Window.FEATURE_NO_TITLE );
            getWindow ( ).setFlags ( WindowManager.LayoutParams.FLAG_FULLSCREEN , WindowManager.LayoutParams.FLAG_FULLSCREEN );
            setContentView ( R.layout.activity_splash_screen );
            mVersionName = findViewById ( R.id.version_name );
            FirebaseApp.initializeApp ( SplashScreen.this );
            mVersionName.setText ( "Ver: " + BuildConfig.VERSION_NAME  );
            PreferenceHandler.getInstance ( SplashScreen.this ).setAppVersion ( "" + BuildConfig.VERSION_NAME );
            SharedPrefManager.getInstance ( SplashScreen.this ).getDeviceToken ( );

        } catch ( Exception e ) {
            e.printStackTrace ( );
        }
    }

    @Override
    protected void onResume ( ) {
        super.onResume ( );
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean previouslyStarted = prefs.getBoolean(getString(R.string.pref_previously_started), false);
        new Handler ().postDelayed( ( ) -> {
            if(!previouslyStarted) {
                init();
            }else{
                callNextActivity ( );
            }

        } ,3000);
    }

    private void init ( ) {
        try {
            if ( Build.VERSION.SDK_INT >= 23 ) {
                String[] PERMISSIONS = {
                        Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_NETWORK_STATE ,
                        Manifest.permission.READ_PHONE_STATE ,
                        Manifest.permission.SEND_SMS ,
                        Manifest.permission.RECEIVE_SMS ,
                        Manifest.permission.READ_SMS ,
                        Manifest.permission.ACCESS_COARSE_LOCATION ,
                        Manifest.permission.ACCESS_FINE_LOCATION ,
                        Manifest.permission.ACCESS_WIFI_STATE ,
                        Manifest.permission.READ_EXTERNAL_STORAGE ,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                };

                if ( !hasPermissions ( mContext , PERMISSIONS ) ) {
                    //ActivityCompat.requestPermissions ( ( Activity ) mContext , PERMISSIONS , REQUEST );
                    Intent permissionRequest = new Intent ( SplashScreen.this,PermissionRequestScreen.class );
                    startActivity ( permissionRequest );
                    SplashScreen.this.finish ();
                } else {
                    callNextActivity ( );
                }
            } else {
                callNextActivity ( );
            }

        } catch ( Exception e ) {
            e.printStackTrace ( );
        }
    }

    private static boolean hasPermissions ( Context context , String... permissions ) {
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null ) {
            for ( String permission : permissions ) {
                if ( ActivityCompat.checkSelfPermission ( context , permission ) != PackageManager.PERMISSION_GRANTED ) {
                    return false;
                }
            }
        }
        return true;
    }

    public void callNextActivity ( ) {

        String mobilenumber = PreferenceHandler.getInstance ( SplashScreen.this ).getPhoneNumber ( );
        int profileId = PreferenceHandler.getInstance ( SplashScreen.this ).getUserId ( );
        int resprofileId = PreferenceHandler.getInstance ( SplashScreen.this ).getResellerUserId ( );

        if ( resprofileId != 0 && profileId == 0 ) {
            Intent verify = new Intent ( SplashScreen.this , ResellerMainActivity.class );
            startActivity ( verify );
            SplashScreen.this.finish ( );
        } else if ( mobilenumber.equals ( "" ) && profileId == 0 ) {
            Intent verify = new Intent ( SplashScreen.this , LandingScreen.class );
            startActivity ( verify );
            SplashScreen.this.finish ( );
        } else {
            int companyId = PreferenceHandler.getInstance ( SplashScreen.this ).getCompanyId ( );

            if ( companyId != 0 && profileId == 0 ) {
                Intent verify = new Intent ( SplashScreen.this , CreateFounderScreen.class );
                startActivity ( verify );
                SplashScreen.this.finish ( );
            } else if ( companyId == 0 && profileId != 0 ) {
                Intent verify = new Intent ( SplashScreen.this , LandingScreen.class );
                startActivity ( verify );
                SplashScreen.this.finish ( );
            } else if ( companyId != 0 ) {
                if ( PreferenceHandler.getInstance ( SplashScreen.this ).getUserRoleUniqueID ( ) == 2 || PreferenceHandler.getInstance ( SplashScreen.this ).getUserRoleUniqueID ( ) == 9 ) {
                    Intent verify = new Intent ( SplashScreen.this , AdminNewMainScreen.class );
                    startActivity ( verify );
                    SplashScreen.this.finish ( );
                } else {
                    Intent verify = new Intent ( SplashScreen.this , EmployeeNewMainScreen.class );
                    startActivity ( verify );
                    SplashScreen.this.finish ( );
                }
            } else {
                String type = PreferenceHandler.getInstance ( SplashScreen.this ).getSignUpType ( );
                if ( type.equalsIgnoreCase ( "Organization" ) ) {
                    Intent verify = new Intent ( SplashScreen.this , CreateFounderScreen.class );
                    startActivity ( verify );
                    SplashScreen.this.finish ( );
                } else if ( type.equalsIgnoreCase ( "Employee" ) ) {
                    Intent verify = new Intent ( SplashScreen.this , LandingScreen.class );
                    startActivity ( verify );
                    SplashScreen.this.finish ( );
                }
            }
        }
    }
}
