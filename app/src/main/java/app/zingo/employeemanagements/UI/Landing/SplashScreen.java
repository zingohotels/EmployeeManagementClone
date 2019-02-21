package app.zingo.employeemanagements.UI.Landing;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.ArrayList;

import app.zingo.employeemanagements.BuildConfig;
import app.zingo.employeemanagements.FireBase.SharedPrefManager;
import app.zingo.employeemanagements.Model.Plans;
import app.zingo.employeemanagements.R;
import app.zingo.employeemanagements.UI.Admin.DashBoardAdmin;
import app.zingo.employeemanagements.UI.Company.CreateCompany;
import app.zingo.employeemanagements.UI.Company.CreateFounderScreen;
import app.zingo.employeemanagements.UI.Employee.DashBoardEmployee;
import app.zingo.employeemanagements.UI.EmployeeSignUp;
import app.zingo.employeemanagements.UI.GetStartedScreen;
import app.zingo.employeemanagements.UI.LandingScreen;


import app.zingo.employeemanagements.UI.NewAdminDesigns.AdminNewMainScreen;
import app.zingo.employeemanagements.UI.NewEmployeeDesign.EmployeeNewMainScreen;
import app.zingo.employeemanagements.UI.Reseller.ResellerMainActivity;
import app.zingo.employeemanagements.Utils.Constants;
import app.zingo.employeemanagements.Utils.PreferenceHandler;
import app.zingo.employeemanagements.Utils.ThreadExecuter;
import app.zingo.employeemanagements.Utils.Util;
import app.zingo.employeemanagements.WebApi.PlansAndRatesAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashScreen extends AppCompatActivity {

    TextView mVersionName,mPowered,mCopyRights,appLogo;
    //ImageView appLogo;

    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1,MY_PERMISSIONS_REQUEST_RESULT = 1;

    private static final int REQUEST= 112;
    Context mContext = this;

    // Remote Config keys
    private static final String LOADING_PHRASE_CONFIG_KEY = "loading_phrase";
    private static final String WELCOME_MESSAGE_KEY = "welcome_message";
    private static final String WELCOME_MESSAGE_KEY_IMAGE = "welcome_image_url";
    private static final String WELCOME_MESSAGE_CAPS_KEY = "welcome_message_caps";
    private static final String APP_VERSION = "app_version";

    private FirebaseRemoteConfig mFirebaseRemoteConfig;

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

            // Get Remote Config instance.
            // [START get_remote_config_instance]
            mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
            // [END get_remote_config_instance]

            // Create a Remote Config Setting to enable developer mode, which you can use to increase
            // the number of fetches available per hour during development. See Best Practices in the
            // README for more information.
            // [START enable_dev_mode]
            FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                    .setDeveloperModeEnabled(BuildConfig.DEBUG)
                    .build();
            mFirebaseRemoteConfig.setConfigSettings(configSettings);
            // [END enable_dev_mode]

            // Set default Remote Config parameter values. An app uses the in-app default values, and
            // when you need to adjust those defaults, you set an updated value for only the values you
            // want to change in the Firebase console. See Best Practices in the README for more
            // information.
            // [START set_default_values]
            mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);
            // [END set_default_values]

            fetchWelcome();


            init();

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void fetchWelcome() {


        long cacheExpiration = 3600; // 1 hour in seconds.
        // If your app is using developer mode, cacheExpiration is set to 0, so each fetch will
        // retrieve values from the service.
        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }

        // [START fetch_config_with_callback]
        // cacheExpirationSeconds is set to cacheExpiration here, indicating the next fetch request
        // will use fetch data from the Remote Config service, rather than cached parameter values,
        // if cached parameter values are more than cacheExpiration seconds old.
        // See Best Practices in the README for more information.
        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                           /* Toast.makeText(SplashScreen.this, "Fetch Succeeded",
                                    Toast.LENGTH_SHORT).show();*/

                            // After config data is successfully fetched, it must be activated before newly fetched
                            // values are returned.
                            mFirebaseRemoteConfig.activateFetched();

                            String app_version = mFirebaseRemoteConfig.getString(APP_VERSION);
                         //   Toast.makeText(SplashScreen.this, ""+app_version, Toast.LENGTH_SHORT).show();
                            PreferenceHandler.getInstance(SplashScreen.this).setAppVersion(app_version);

                            if(mFirebaseRemoteConfig.getString(WELCOME_MESSAGE_CAPS_KEY).equalsIgnoreCase("false")){
                                Util.BASE_URL = Constants.BASE_URL;
                            }else{
                                Util.BASE_URL = mFirebaseRemoteConfig.getString(WELCOME_MESSAGE_KEY);
                                Util.IMAGE_URL = mFirebaseRemoteConfig.getString(WELCOME_MESSAGE_KEY_IMAGE);
                            }

                           // getPlans();


                        } else {
                            /*Toast.makeText(SplashScreen.this, "Fetch Failed",
                                    Toast.LENGTH_SHORT).show();*/
                        }
                        // displayWelcomeMessage();
                    }
                });
        // [END fetch_config_with_callback]
    }

    private void init(){
        try{

            mVersionName.setText("Ver: "+ BuildConfig.VERSION_NAME+"");

            String token = SharedPrefManager.getInstance(SplashScreen.this).getDeviceToken();

            System.out.println("Splash Token  = "+token);


            Animation fade_in = AnimationUtils.loadAnimation(this,R.anim.fade_in);
            Animation right_anim = AnimationUtils.loadAnimation(this,R.anim.right_trans);
            Animation left_anim = AnimationUtils.loadAnimation(this,R.anim.left_trans);
            //appLogo.startAnimation(fade_in);
            mPowered.startAnimation(right_anim);
            //mCopyRights.startAnimation(left_anim);

            if (Build.VERSION.SDK_INT >= 23) {
                Log.d("TAG","@@@ IN IF Build.VERSION.SDK_INT >= 23");
                String[] PERMISSIONS = {android.Manifest.permission.CAMERA,
                        android.Manifest.permission.READ_PHONE_STATE,
                        android.Manifest.permission.INTERNET,
                        android.Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.ACCESS_WIFI_STATE,
                        android. Manifest.permission.NFC,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.GET_ACCOUNTS,
                };


                if (!hasPermissions(mContext, PERMISSIONS)) {
                    Log.d("TAG","@@@ IN IF hasPermissions");
                    ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, REQUEST );
                } else {
                    Log.d("TAG","@@@ IN ELSE hasPermissions");
                    callNextActivity();
                }
            } else {
                Log.d("TAG","@@@ IN ELSE  Build.VERSION.SDK_INT >= 23");
                callNextActivity();
            }



        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void
    getPlans(){


        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {
                PlansAndRatesAPI apiService = Util.getClient().create(PlansAndRatesAPI.class);
                Call<ArrayList<Plans>> call = apiService.getPlans();

                call.enqueue(new Callback<ArrayList<Plans>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Plans>> call, Response<ArrayList<Plans>> response) {
                        int statusCode = response.code();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {



                            ArrayList<Plans> list = response.body();


                            if (list !=null && list.size()!=0) {



                            }else{

                            }

                        }else {

                            if(response.code()==403){

                                if(Util.BASE_URL.equalsIgnoreCase("http://locals.zingyapp.com/api/")){
                                    Util.BASE_URL = Constants.BASE_URL_MAIN;
                                    Util.IMAGE_URL = Constants.IMAGE_URL_MAIN;
                                }else{
                                    Util.BASE_URL = Constants.BASE_URL;
                                    Util.IMAGE_URL = Constants.IMAGE_URL;
                                }

                            }



                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Plans>> call, Throwable t) {
                        // Log error here since request failed

                        Log.e("TAG", t.toString());
                    }
                });
            }


        });
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

    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("TAG","@@@ PERMISSIONS grant");
                    callNextActivity();
                } else {
                    Log.d("TAG","@@@ PERMISSIONS Denied");
                    Toast.makeText(mContext, "PERMISSIONS Denied", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
    public void callNextActivity()
    {

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {


                String mobilenumber = PreferenceHandler.getInstance(SplashScreen.this).getPhoneNumber();
                int profileId = PreferenceHandler.getInstance(SplashScreen.this).getUserId();
                int resprofileId = PreferenceHandler.getInstance(SplashScreen.this).getResellerUserId();

                if(!checkPermission()){

                }else{

                    if(resprofileId!=0&&profileId==0){

                        Intent verify = new Intent(SplashScreen.this,ResellerMainActivity.class);
                        startActivity(verify);
                        SplashScreen.this.finish();

                    }else if(mobilenumber.equals("")&&profileId==0&&resprofileId==0)
                    {
                        //Intent verify = new Intent(SplashScreen.this,LoginScreen.class);
                        Intent verify = new Intent(SplashScreen.this,LandingScreen.class);
                        startActivity(verify);
                        SplashScreen.this.finish();
                    }
                    else
                    {
                        int companyId = PreferenceHandler.getInstance(SplashScreen.this).getCompanyId();

                        if(!checkPermission()){

                        }else{
                            if(companyId!=0&&profileId==0){
                                Intent verify = new Intent(SplashScreen.this,CreateFounderScreen.class);
                                startActivity(verify);
                                SplashScreen.this.finish();
                            }else if(companyId==0&&profileId!=0){
                                Intent verify = new Intent(SplashScreen.this,LandingScreen.class);
                                startActivity(verify);
                                SplashScreen.this.finish();
                            }else if(companyId!=0&&profileId!=0){

                                if(PreferenceHandler.getInstance(SplashScreen.this).getUserRoleUniqueID()==2){
                                    //Intent verify = new Intent(SplashScreen.this,DashBoardEmployee.class);
                                    Intent verify = new Intent(SplashScreen.this,AdminNewMainScreen.class);
                                    startActivity(verify);
                                    SplashScreen.this.finish();
                                }else{
                                    Intent verify = new Intent(SplashScreen.this,EmployeeNewMainScreen.class);
                                    startActivity(verify);
                                    SplashScreen.this.finish();
                                }

                            }else{

                                String type = PreferenceHandler.getInstance(SplashScreen.this).getSignUpType();

                                if(profileId==0&&type.equalsIgnoreCase("Organization")){
                                    Intent verify = new Intent(SplashScreen.this,CreateFounderScreen.class);
                                    startActivity(verify);
                                    SplashScreen.this.finish();
                                }else  if(profileId==0&&type.equalsIgnoreCase("Employee")){
                                    Intent verify = new Intent(SplashScreen.this,LandingScreen.class);
                                    startActivity(verify);
                                    SplashScreen.this.finish();
                                }

                            }
                        }



                    }
                }





            }
        }, 3000);
    }
}
