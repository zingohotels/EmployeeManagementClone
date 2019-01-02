package app.zingo.employeemanagements.UI.Landing;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import app.zingo.employeemanagements.BuildConfig;
import app.zingo.employeemanagements.FireBase.SharedPrefManager;
import app.zingo.employeemanagements.R;
import app.zingo.employeemanagements.UI.Admin.DashBoardAdmin;
import app.zingo.employeemanagements.UI.Company.CreateCompany;
import app.zingo.employeemanagements.UI.Company.CreateFounderScreen;
import app.zingo.employeemanagements.UI.Employee.DashBoardEmployee;
import app.zingo.employeemanagements.UI.Login.LoginScreen;
import app.zingo.employeemanagements.Utils.PreferenceHandler;

public class SplashScreen extends AppCompatActivity {

    TextView mVersionName,mPowered,mCopyRights;
    ImageView appLogo;

    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1,MY_PERMISSIONS_REQUEST_RESULT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{

            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);

            setContentView(R.layout.activity_splash_screen);

            //Ui initialization
            mVersionName = findViewById(R.id.version_name);
            mPowered = findViewById(R.id.powered_by);
            mCopyRights = findViewById(R.id.copyrights_by);
            appLogo = findViewById(R.id.employee_splash);

            init();

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void init(){
        try{

            mVersionName.setText("Version code : "+ BuildConfig.VERSION_NAME+"");

            String token = SharedPrefManager.getInstance(SplashScreen.this).getDeviceToken();

            System.out.println("Splash Token  = "+token);


            Animation fade_in = AnimationUtils.loadAnimation(this,R.anim.fade_in);
            Animation right_anim = AnimationUtils.loadAnimation(this,R.anim.right_trans);
            Animation left_anim = AnimationUtils.loadAnimation(this,R.anim.left_trans);
            appLogo.startAnimation(fade_in);
            mPowered.startAnimation(right_anim);
            mCopyRights.startAnimation(left_anim);

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {


                    String mobilenumber = PreferenceHandler.getInstance(SplashScreen.this).getPhoneNumber();
                    int profileId = PreferenceHandler.getInstance(SplashScreen.this).getUserId();


                    if(mobilenumber.equals("")&&profileId==0)
                    {
                        //Intent verify = new Intent(SplashScreen.this,LoginScreen.class);
                        Intent verify = new Intent(SplashScreen.this,SlideScreen.class);
                        startActivity(verify);
                        SplashScreen.this.finish();
                    }
                    else
                    {
                        int companyId = PreferenceHandler.getInstance(SplashScreen.this).getCompanyId();

                        if(!checkPermission()){

                        }else{
                            if(companyId==0&&profileId==0){
                                Intent verify = new Intent(SplashScreen.this,CreateCompany.class);
                                startActivity(verify);
                                SplashScreen.this.finish();
                            }else if(companyId==0&&profileId!=0){
                                Intent verify = new Intent(SplashScreen.this,LoginScreen.class);
                                startActivity(verify);
                                SplashScreen.this.finish();
                            }else if(companyId!=0&&profileId!=0){

                                if(PreferenceHandler.getInstance(SplashScreen.this).getUserRoleUniqueID()==2){
                                    //Intent verify = new Intent(SplashScreen.this,DashBoardEmployee.class);
                                    Intent verify = new Intent(SplashScreen.this,DashBoardAdmin.class);
                                    startActivity(verify);
                                    SplashScreen.this.finish();
                                }else{
                                    Intent verify = new Intent(SplashScreen.this,DashBoardEmployee.class);
                                    startActivity(verify);
                                    SplashScreen.this.finish();
                                }

                            }else{

                                if(profileId==0){
                                    Intent verify = new Intent(SplashScreen.this,CreateFounderScreen.class);
                                    startActivity(verify);
                                    SplashScreen.this.finish();
                                }

                            }
                        }



                    }



                }
            }, 3000);

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public boolean checkPermission() {
        if ((ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED)
                &&(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                && (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                && (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                && (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)) {
            if ((ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.SEND_SMS))
                    && (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION))
                    && (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE))
                    && (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE))
                    && (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CALL_PHONE))) {

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{
                                android.Manifest.permission.SEND_SMS,
                                android.Manifest.permission.ACCESS_FINE_LOCATION,
                                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                                android.Manifest.permission.CALL_PHONE},
                        MY_PERMISSIONS_REQUEST_RESULT);
                Log.d("checkPermission if","false");

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.SEND_SMS,
                                android.Manifest.permission.ACCESS_FINE_LOCATION,
                                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                                android.Manifest.permission.CALL_PHONE},
                        MY_PERMISSIONS_REQUEST_RESULT);
                Log.d("checkPermission else","true");

            }
            return false;
        } else {
            Log.d("checkPermission else","trur");

            return true;
        }
    }
}
