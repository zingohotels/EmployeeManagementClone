package app.zingo.employeemanagements.ui.landing;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;

import app.zingo.employeemanagements.Custom.MyRegulerText;
import app.zingo.employeemanagements.utils.PreferenceHandler;
import app.zingo.employeemanagements.adapter.PermissionRequestAdapter;
import app.zingo.employeemanagements.base.R;
import app.zingo.employeemanagements.model.PermissionModel;
import app.zingo.employeemanagements.ui.Company.CreateFounderScreen;
import app.zingo.employeemanagements.ui.LandingScreen;
import app.zingo.employeemanagements.ui.NewAdminDesigns.AdminNewMainScreen;
import app.zingo.employeemanagements.ui.newemployeedesign.EmployeeNewMainScreen;
import app.zingo.employeemanagements.ui.Reseller.ResellerMainActivity;

public class PermissionRequestScreen extends AppCompatActivity {

    String[] permissionName = {"Camera","Location","Phone","Storage"};
    String[] permissionDescription = {"Take photo for Meeting and Profile pic","Attendance,Meetings,Tasks","Check internet connection and device verification","Access image from storage for Profile pic,Expenses"};

    Integer[] permissionImage = {R.drawable.camera_permission, R.drawable.location_permission,
            R.drawable.phone_permission, R.drawable.storage_permission};

    Context mContext = this;
    private static final int REQUEST = 112;

    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate ( savedInstanceState );

        try{
            requestWindowFeature ( Window.FEATURE_NO_TITLE );
            getWindow ( ).setFlags ( WindowManager.LayoutParams.FLAG_FULLSCREEN , WindowManager.LayoutParams.FLAG_FULLSCREEN );
            setContentView ( R.layout.activity_permission_request_screen );

            RecyclerView permission_list_rcv = findViewById ( R.id.permission_list_rcv );
            MyRegulerText allow_access = findViewById ( R.id.allow_access );
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager ( PermissionRequestScreen.this);
            permission_list_rcv.setLayoutManager(layoutManager);
            permission_list_rcv.setItemAnimator(new DefaultItemAnimator ());

            ArrayList < PermissionModel > permission_models_list = new ArrayList <> ( );

            for(int i=0;i < permissionImage.length;i++) {
                PermissionModel permissionModel = new PermissionModel (permissionName[i],permissionDescription[i],permissionImage[i]);
                permission_models_list.add(permissionModel);
            }
            if( permission_models_list.size ()!=0){
                PermissionRequestAdapter adapter = new PermissionRequestAdapter ( PermissionRequestScreen.this , permission_models_list );
                permission_list_rcv.setAdapter( adapter );
            }

            allow_access.setOnClickListener ( v -> {
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

                        ActivityCompat.requestPermissions ( ( Activity ) mContext , PERMISSIONS , REQUEST );
                    } else {

                        callNextActivity ( );
                    }
                }else{
                    callNextActivity ();
                }

            } );

        }catch ( Exception e){
            e.printStackTrace ();
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

    @Override
    public void onRequestPermissionsResult( int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if ( requestCode == REQUEST ) {
            if ( grantResults.length > 0 && grantResults[ 0 ] == PackageManager.PERMISSION_GRANTED ) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                boolean previouslyStarted = prefs.getBoolean(getString(R.string.pref_previously_started), false);
                if(!previouslyStarted) {
                    SharedPreferences.Editor edit = prefs.edit();
                    edit.putBoolean(getString(R.string.pref_previously_started), Boolean.TRUE);
                    edit.apply ();

                }
                callNextActivity ( );

            } else {
                callNextActivity ( );
            }
        }
    }

    public void callNextActivity ( ) {
        String mobilenumber = PreferenceHandler.getInstance ( PermissionRequestScreen.this ).getPhoneNumber ( );
        int profileId = PreferenceHandler.getInstance ( PermissionRequestScreen.this ).getUserId ( );
        int resprofileId = PreferenceHandler.getInstance ( PermissionRequestScreen.this ).getResellerUserId ( );

        if ( resprofileId != 0 && profileId == 0 ) {
            Intent verify = new Intent ( PermissionRequestScreen.this , ResellerMainActivity.class );
            startActivity ( verify );
            PermissionRequestScreen.this.finish ( );

        } else if ( mobilenumber.equals ( "" ) && profileId == 0 ) {

            Intent verify = new Intent ( PermissionRequestScreen.this , LandingScreen.class );
            startActivity ( verify );
            PermissionRequestScreen.this.finish ( );
        } else {
            int companyId = PreferenceHandler.getInstance ( PermissionRequestScreen.this ).getCompanyId ( );
            if ( companyId != 0 && profileId == 0 ) {
                Intent verify = new Intent ( PermissionRequestScreen.this , CreateFounderScreen.class );
                startActivity ( verify );
                PermissionRequestScreen.this.finish ( );
            } else if ( companyId == 0 && profileId != 0 ) {
                Intent verify = new Intent ( PermissionRequestScreen.this , LandingScreen.class );
                startActivity ( verify );
                PermissionRequestScreen.this.finish ( );
            } else if ( companyId != 0 ) {
                if ( PreferenceHandler.getInstance ( PermissionRequestScreen.this ).getUserRoleUniqueID ( ) == 2 || PreferenceHandler.getInstance ( PermissionRequestScreen.this ).getUserRoleUniqueID ( ) == 9 ) {
                    Intent verify = new Intent ( PermissionRequestScreen.this , AdminNewMainScreen.class );
                    startActivity ( verify );
                    PermissionRequestScreen.this.finish ( );
                } else {
                    Intent verify = new Intent ( PermissionRequestScreen.this , EmployeeNewMainScreen.class );
                    startActivity ( verify );
                    PermissionRequestScreen.this.finish ( );
                }
            } else {
                String type = PreferenceHandler.getInstance ( PermissionRequestScreen.this ).getSignUpType ( );
                if ( type.equalsIgnoreCase ( "Organization" ) ) {
                    Intent verify = new Intent ( PermissionRequestScreen.this , CreateFounderScreen.class );
                    startActivity ( verify );
                    PermissionRequestScreen.this.finish ( );
                } else if ( type.equalsIgnoreCase ( "Employee" ) ) {
                    Intent verify = new Intent ( PermissionRequestScreen.this , LandingScreen.class );
                    startActivity ( verify );
                    PermissionRequestScreen.this.finish ( );
                }

            }


        }
    }

}
