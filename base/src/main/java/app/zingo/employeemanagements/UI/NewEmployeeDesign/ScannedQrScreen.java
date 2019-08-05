package app.zingo.employeemanagements.UI.NewEmployeeDesign;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import app.zingo.employeemanagements.Model.LoginDetails;
import app.zingo.employeemanagements.Model.LoginDetailsNotificationManagers;
import app.zingo.employeemanagements.Service.LocationForegroundService;
import app.zingo.employeemanagements.UI.NewAdminDesigns.AdminNewMainScreen;
import app.zingo.employeemanagements.Utils.Constants;
import app.zingo.employeemanagements.Utils.PreferenceHandler;
import app.zingo.employeemanagements.Utils.Util;
import app.zingo.employeemanagements.WebApi.LoginDetailsAPI;
import app.zingo.employeemanagements.WebApi.LoginNotificationAPI;
import app.zingo.employeemanagements.base.R;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScannedQrScreen extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    private final int MY_CAMERA_REQUEST_CODE = 6515;

    ImageView flashOnOffImageView;

    ZXingScannerView qrCodeScanner;
    RelativeLayout scanQrCodeRootView;

    LoginDetails loginDetails;
    LoginDetailsNotificationManagers loginDetailsNotificationManagers;
    String type;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setContentView(R.layout.activity_scanned_qr_screen);

            flashOnOffImageView = findViewById(R.id.flashOnOffImageView);
            qrCodeScanner = findViewById(R.id.qrCodeScanner);
            scanQrCodeRootView = findViewById(R.id.scanQrCodeRootView);

            Bundle bun = getIntent().getExtras();
            if(bun!=null){

                loginDetails = (LoginDetails)bun.getSerializable("LoginDetails");
                loginDetailsNotificationManagers = (LoginDetailsNotificationManagers) bun.getSerializable("LoginNotification");
                type = bun.getString("Type");
            }

            setScannerProperties();


        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void setScannerProperties() {
        ArrayList<BarcodeFormat> list = new ArrayList<>();
        list.add(BarcodeFormat.QR_CODE);
        qrCodeScanner.setFormats(list);
        qrCodeScanner.setAutoFocus(true);
        qrCodeScanner.setLaserColor(R.color.colorAccent);
        qrCodeScanner.setMaskColor(R.color.colorAccent);
       // if (Build.MANUFACTURER.equals(HUAWEI, ignoreCase = true))
        qrCodeScanner.setAspectTolerance(0.5f);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                String[] PERMISSIONS = {Manifest.permission.CAMERA};
                ActivityCompat.requestPermissions(ScannedQrScreen.this,PERMISSIONS, MY_CAMERA_REQUEST_CODE);

                return;
            }
        }
        qrCodeScanner.startCamera();
        qrCodeScanner.setResultHandler(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if(grantResults!=null&&grantResults.length!=0){
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    openCamera();
                else if (grantResults[0] == PackageManager.PERMISSION_DENIED)
                    //showCameraSnackBar();
                    Toast.makeText(ScannedQrScreen.this, "Camera Permission required", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(ScannedQrScreen.this, "Camera Permission required", Toast.LENGTH_SHORT).show();
            }
        }
    }


    /*private void showCameraSnackBar() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            Snackbar snackbar = Snackbar.make(scanQrCodeRootView, "App needs camera permission to scan QR Code", Snackbar.LENGTH_LONG);
            View view1 = snackbar.getView();
            view1.setBackgroundColor(ContextCompat.getColor(this, R.color.text_grey));
            TextView textView = view1.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
            snackbar.show();
        }
    }*/

    private void openCamera() {
        qrCodeScanner.startCamera();
        qrCodeScanner.setResultHandler(this);
    }




    /**
     * stop the qr code camera scanner when activity is in onPause state.
     */

    @Override
    protected void onPause() {
        super.onPause();
        qrCodeScanner.stopCamera();
    }


    @Override
    public void handleResult(Result result) {

        if (result != null) {

            System.out.println("Scanned code "+result.getText());
            // startActivity(ScannedActivity.getScannedActivity(this, result.getText()));
            //resumeCamera();

            try {


                if(result.getText()!=null&&!result.getText().isEmpty()){

                    if(result.getText().contains("=")){

                        String[] array = result.getText().split("=");

                        if(PreferenceHandler.getInstance(ScannedQrScreen.this).getCompanyId()==Integer.parseInt(array[1])){


                            if(loginDetails!=null&&loginDetailsNotificationManagers!=null){

                                if(type!=null&&type.equalsIgnoreCase("Check-in")){

                                    addLogin(loginDetails,loginDetailsNotificationManagers);
                                }else if(type!=null&&type.equalsIgnoreCase("Check-out")){

                                    updateLogin(loginDetails, loginDetailsNotificationManagers);

                                                       /* Intent myService = new Intent(getActivity(), LocationSharingServices.class);
                                                        getActivity().stopService(myService);*/

                                    Intent intent = new Intent(ScannedQrScreen.this, LocationForegroundService.class);
                                    intent.setAction(LocationForegroundService.ACTION_STOP_FOREGROUND_SERVICE);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        startForegroundService(intent);
                                    } else {
                                        startService(intent);
                                    }

                                }

                            }


                        }else{
                            Toast.makeText(ScannedQrScreen.this, "Your organization code is not matching with QR Code", Toast.LENGTH_SHORT).show();
                        }

                    }else{
                        if(loginDetails!=null&&loginDetailsNotificationManagers!=null){

                            if(type!=null&&type.equalsIgnoreCase("Check-in")){

                                addLogin(loginDetails,loginDetailsNotificationManagers);
                            }else if(type!=null&&type.equalsIgnoreCase("Check-out")){

                                updateLogin(loginDetails, loginDetailsNotificationManagers);

                                                       /* Intent myService = new Intent(getActivity(), LocationSharingServices.class);
                                                        getActivity().stopService(myService);*/

                                Intent intent = new Intent(ScannedQrScreen.this, LocationForegroundService.class);
                                intent.setAction(LocationForegroundService.ACTION_STOP_FOREGROUND_SERVICE);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    startForegroundService(intent);
                                } else {
                                    startService(intent);
                                }

                            }

                        }
                    }
                }


            } catch (Exception e) {
                e.printStackTrace();
            }




        }
    }



    /**
     * Resume the camera after 2 seconds when qr code successfully scanned through bar code reader.
     */

    private void resumeCamera() {
       // Toast.LENGTH_LONG;
        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                qrCodeScanner.resumeCameraPreview(ScannedQrScreen.this);

            }
        },2000);
    }

    public void addLogin(final LoginDetails loginDetails, final LoginDetailsNotificationManagers md) {


        final ProgressDialog dialog = new ProgressDialog(ScannedQrScreen.this);
        dialog.setMessage("Saving Details..");
        dialog.setCancelable(false);
        dialog.show();

        LoginDetailsAPI apiService = Util.getClient().create(LoginDetailsAPI.class);

        Call<LoginDetails> call = apiService.addLogin(loginDetails);

        call.enqueue(new Callback<LoginDetails>() {
            @Override
            public void onResponse(Call<LoginDetails> call, Response<LoginDetails> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201) {

                        LoginDetails s = response.body();

                        if (s != null) {

                           /* punchIn.setEnabled(false);
                            punchOut.setEnabled(true);*/


                            md.setLoginDetailsId(s.getLoginDetailsId());
                            saveLoginNotification(md);

                            Toast.makeText(ScannedQrScreen.this, "You Logged in", Toast.LENGTH_SHORT).show();

                            PreferenceHandler.getInstance(ScannedQrScreen.this).setLoginId(s.getLoginDetailsId());

                            String date = s.getLoginDate();

                            if (date != null && !date.isEmpty()) {

                                if (date.contains("T")) {

                                    String logins[] = date.split("T");
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                    SimpleDateFormat sdfs = new SimpleDateFormat("MMM dd,yyyy");

                                    Date dt = sdf.parse(logins[0]);
                                  // punchInText.setText("" + s.getLoginTime());
                                }
                            } else {
                              //  punchInText.setText("" + s.getLoginTime());
                            }


                            PreferenceHandler.getInstance(ScannedQrScreen.this).setLoginStatus("Login");
                            PreferenceHandler.getInstance(ScannedQrScreen.this).setLoginTime("" + s.getLoginTime());

                            Intent intent = new Intent(ScannedQrScreen.this, LocationForegroundService.class);
                            intent.setAction(LocationForegroundService.ACTION_START_FOREGROUND_SERVICE);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                startForegroundService(intent);
                            } else {
                                startService(intent);
                            }


                        }


                    } else {
                        Toast.makeText(ScannedQrScreen.this, "Failed Due to " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception ex) {

                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    ex.printStackTrace();
                }
//                callGetStartEnd();
            }

            @Override
            public void onFailure(Call<LoginDetails> call, Throwable t) {

                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                Toast.makeText( ScannedQrScreen.this , "Failed due to Bad Internet Connection" , Toast.LENGTH_SHORT ).show( );
                Log.e("TAG", t.toString());
            }
        });


    }

    public void saveLoginNotification(final LoginDetailsNotificationManagers md) {


        final ProgressDialog dialog = new ProgressDialog(ScannedQrScreen.this);
        dialog.setMessage("Saving Details..");
        dialog.setCancelable(false);
        dialog.show();

        LoginNotificationAPI apiService = Util.getClient().create(LoginNotificationAPI.class);

        Call<LoginDetailsNotificationManagers> call = apiService.saveLoginNotification(md);

        call.enqueue(new Callback<LoginDetailsNotificationManagers>() {
            @Override
            public void onResponse(Call<LoginDetailsNotificationManagers> call, Response<LoginDetailsNotificationManagers> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201) {

                        LoginDetailsNotificationManagers s = response.body();

                        if (s != null) {


                            s.setEmployeeId(md.getManagerId());
                            s.setManagerId(md.getEmployeeId());
                            s.setSenderId(Constants.SENDER_ID);
                            s.setServerId(Constants.SERVER_ID);
                            sendLoginNotification(s);


                        }


                    } else {
                        Toast.makeText(ScannedQrScreen.this, "Failed Due to " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception ex) {

                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    ex.printStackTrace();
                }
//                callGetStartEnd();
            }

            @Override
            public void onFailure(Call<LoginDetailsNotificationManagers> call, Throwable t) {

                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                Toast.makeText( ScannedQrScreen.this , "Failed due to Bad Internet Connection" , Toast.LENGTH_SHORT ).show( );
                Log.e("TAG", t.toString());
            }
        });


    }

    public void sendLoginNotification(final LoginDetailsNotificationManagers md) {


        final ProgressDialog dialog = new ProgressDialog(ScannedQrScreen.this);
        dialog.setMessage("Sending Details..");
        dialog.setCancelable(false);
        dialog.show();

        LoginNotificationAPI apiService = Util.getClient().create(LoginNotificationAPI.class);

        Call<ArrayList<String>> call = apiService.sendLoginNotification(md);

        call.enqueue(new Callback<ArrayList<String>>() {
            @Override
            public void onResponse(Call<ArrayList<String>> call, Response<ArrayList<String>> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201) {


                        if(PreferenceHandler.getInstance(ScannedQrScreen.this).getUserRoleUniqueID()==9){

                            Intent main = new Intent(ScannedQrScreen.this, AdminNewMainScreen.class);
                            startActivity(main);
                            ScannedQrScreen.this.finish();
                        }else if(PreferenceHandler.getInstance(ScannedQrScreen.this).getUserRoleUniqueID()==1){

                            Intent main = new Intent(ScannedQrScreen.this, EmployeeNewMainScreen.class);
                            startActivity(main);
                            ScannedQrScreen.this.finish();
                        }


                    } else {
                        Toast.makeText(ScannedQrScreen.this, "Failed Due to " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception ex) {

                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    ex.printStackTrace();
                }
//                callGetStartEnd();
            }

            @Override
            public void onFailure(Call<ArrayList<String>> call, Throwable t) {

                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                Toast.makeText( ScannedQrScreen.this , "Failed due to Bad Internet Connection" , Toast.LENGTH_SHORT ).show( );
                Log.e("TAG", t.toString());
            }
        });


    }


    public void updateLogin(final LoginDetails loginDetails, final LoginDetailsNotificationManagers md) {


        final ProgressDialog dialog = new ProgressDialog(ScannedQrScreen.this);
        dialog.setMessage("Saving Details..");
        dialog.setCancelable(false);
        dialog.show();

        LoginDetailsAPI apiService = Util.getClient().create(LoginDetailsAPI.class);

        Call<LoginDetails> call = apiService.updateLoginById(loginDetails.getLoginDetailsId(), loginDetails);

        call.enqueue(new Callback<LoginDetails>() {
            @Override
            public void onResponse(Call<LoginDetails> call, Response<LoginDetails> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201 || response.code() == 204) {




                        saveLoginNotification(md);

                        Toast.makeText(ScannedQrScreen.this, "You logged out", Toast.LENGTH_SHORT).show();

                        PreferenceHandler.getInstance(ScannedQrScreen.this).setLoginId(0);


                        //punchInText.setText("Check in");
                        PreferenceHandler.getInstance(ScannedQrScreen.this).setLoginStatus("Logout");

                       /* punchIn.setEnabled(true);
                        punchOut.setEnabled(false);*/

                        String date = loginDetails.getLoginDate();

                        if (date != null && !date.isEmpty()) {

                            if (date.contains("T")) {

                                String logins[] = date.split("T");
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                SimpleDateFormat sdfs = new SimpleDateFormat("MMM dd,yyyy");

                                Date dt = sdf.parse(logins[0]);
                               // punchOutText.setText("" + loginDetails.getLogOutTime());
                            }
                        } else {
                           // punchOutText.setText("" + loginDetails.getLogOutTime());
                        }


                    } else {
                        Toast.makeText(ScannedQrScreen.this, "Failed Due to " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception ex) {

                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    ex.printStackTrace();
                }
//                callGetStartEnd();
            }

            @Override
            public void onFailure(Call<LoginDetails> call, Throwable t) {

                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                Toast.makeText( ScannedQrScreen.this , "Failed due to Bad Internet Connection" , Toast.LENGTH_SHORT ).show( );
                Log.e("TAG", t.toString());
            }
        });


    }
}
