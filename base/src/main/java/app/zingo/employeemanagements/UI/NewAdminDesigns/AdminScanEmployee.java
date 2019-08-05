package app.zingo.employeemanagements.UI.NewAdminDesigns;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import app.zingo.employeemanagements.Model.LoginDetails;
import app.zingo.employeemanagements.Model.LoginDetailsNotificationManagers;
import app.zingo.employeemanagements.Utils.Constants;
import app.zingo.employeemanagements.Utils.PreferenceHandler;
import app.zingo.employeemanagements.Utils.ThreadExecuter;
import app.zingo.employeemanagements.Utils.TrackGPS;
import app.zingo.employeemanagements.Utils.Util;
import app.zingo.employeemanagements.WebApi.LoginDetailsAPI;
import app.zingo.employeemanagements.WebApi.LoginNotificationAPI;
import app.zingo.employeemanagements.base.R;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AdminScanEmployee extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    private final int MY_CAMERA_REQUEST_CODE = 6515;

    ImageView flashOnOffImageView;

    ZXingScannerView qrCodeScanner;
    RelativeLayout scanQrCodeRootView;

    TrackGPS gps;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setContentView(R.layout.activity_admin_scan_employee);

            gps = new TrackGPS(AdminScanEmployee.this);

            flashOnOffImageView = findViewById(R.id.flashOnOffImageView);
            qrCodeScanner = findViewById(R.id.qrCodeScanner);
            scanQrCodeRootView = findViewById(R.id.scanQrCodeRootView);

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
                ActivityCompat.requestPermissions(AdminScanEmployee.this,PERMISSIONS, MY_CAMERA_REQUEST_CODE);

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
                    Toast.makeText(AdminScanEmployee.this, "Camera Permission required", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(AdminScanEmployee.this, "Camera Permission required", Toast.LENGTH_SHORT).show();
            }

        }
    }




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
//            String decryptionString = EncryptionHelper.getInstance().getDecryptionString(result.getText());
            try {
                JSONObject jsonObj = new JSONObject(result.getText());

                String type = jsonObj.getString("Type");

                if(PreferenceHandler.getInstance(AdminScanEmployee.this).getCompanyId()==jsonObj.getInt("OrganizationId")){


                    if(jsonObj.getString("TodayDate").equalsIgnoreCase(""+new SimpleDateFormat("MM/dd/yyyy").format(new Date()))){


                        if(locationCheck()){

                            if(gps.canGetLocation()){

                                double longi = gps.getLongitude();
                                double lati = gps.getLatitude();

                                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                                SimpleDateFormat sdt = new SimpleDateFormat("MMM dd,yyyy hh:mm a");
                                SimpleDateFormat sdtT = new SimpleDateFormat("hh:mm a");

                                if(longi!=0&&lati!=0){

                                    if(type!=null&&type.equalsIgnoreCase("Check-In")){


                                        try {
                                            String address =  getAddress(new LatLng(lati,longi));
                                            LoginDetails loginDetails = new LoginDetails();
                                            loginDetails.setEmployeeId(jsonObj.getInt("EmployeeId"));
                                            loginDetails.setLatitude("" + lati);
                                            loginDetails.setLongitude("" + longi);
                                            loginDetails.setPlaceId(""+jsonObj.getString("IMEI"));
                                            loginDetails.setLocation("" + address);
                                            loginDetails.setLoginTime("" + sdt.format(new Date()));
                                            loginDetails.setLoginDate("" + sdf.format(new Date()));
                                            loginDetails.setLogOutTime("");

                                            LoginDetailsNotificationManagers md = new LoginDetailsNotificationManagers();

                                            md.setTitle("Login Details from " + jsonObj.getString("FullName"));
                                            md.setMessage("Log in at  " + "" + sdt.format(new Date()));
                                            md.setLocation(address);
                                            md.setLongitude("" + longi);
                                            md.setLatitude("" + lati);
                                            md.setLoginDate("" + sdt.format(new Date()));
                                            md.setStatus("In meeting");

                                            md.setEmployeeId(jsonObj.getInt("EmployeeId"));
                                            md.setManagerId(PreferenceHandler.getInstance(AdminScanEmployee.this).getUserId());

                                            addLogin(loginDetails,md);


                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }


//{"EmployeeId":20,"FullName":"Bean","LoginId":795,"Meeting":false,"OrganizationId":196,"TodayDate":"03/13/2019","TodayTime":"16:28:10","Type":"Check-Out"}

                                    }else if(type!=null&&type.equalsIgnoreCase("Check-Out")&&jsonObj.getInt("LoginId")!=0){

                                        getLoginDetails(jsonObj.getInt("LoginId"),jsonObj.getString("FullName"),jsonObj.getInt("EmployeeId"),jsonObj.getString("IMEI"));



                                    }


                                }else{

                                    Toast.makeText(AdminScanEmployee.this, "Your GPS is slow Please Try again sometime", Toast.LENGTH_SHORT).show();
                                }
                            }

                        }

                    }





                }else{
                    Toast.makeText(AdminScanEmployee.this, "Your organization code is not matching with QR Code", Toast.LENGTH_SHORT).show();
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
                qrCodeScanner.resumeCameraPreview(AdminScanEmployee.this);

            }
        },2000);
    }



    public void addLogin(final LoginDetails loginDetails, final LoginDetailsNotificationManagers md) {


        final ProgressDialog dialog = new ProgressDialog(AdminScanEmployee.this);
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

                            Toast.makeText(AdminScanEmployee.this, "You Logged in", Toast.LENGTH_SHORT).show();



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







                        }


                    } else {
                        Toast.makeText(AdminScanEmployee.this, "Failed Due to " + response.message(), Toast.LENGTH_SHORT).show();
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
                //Toast.makeText(AdminScanEmployee.this, "Failed due to Bad Internet Connection", Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());
            }
        });


    }

    public void saveLoginNotification(final LoginDetailsNotificationManagers md) {


        final ProgressDialog dialog = new ProgressDialog(AdminScanEmployee.this);
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
                        Toast.makeText(AdminScanEmployee.this, "Failed Due to " + response.message(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText( AdminScanEmployee.this , "Failed due to Bad Internet Connection" , Toast.LENGTH_SHORT ).show( );
                Log.e("TAG", t.toString());
            }
        });


    }

    public void sendLoginNotification(final LoginDetailsNotificationManagers md) {


        final ProgressDialog dialog = new ProgressDialog(AdminScanEmployee.this);
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


                        resumeCamera();


                    } else {
                        Toast.makeText(AdminScanEmployee.this, "Failed Due to " + response.message(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText( AdminScanEmployee.this , "Failed due to Bad Internet Connection" , Toast.LENGTH_SHORT ).show( );
                Log.e("TAG", t.toString());
            }
        });


    }


    public void updateLogin(final LoginDetails loginDetails, final LoginDetailsNotificationManagers md) {


        final ProgressDialog dialog = new ProgressDialog(AdminScanEmployee.this);
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

                        Toast.makeText(AdminScanEmployee.this, "You logged out", Toast.LENGTH_SHORT).show();

                        PreferenceHandler.getInstance(AdminScanEmployee.this).setLoginId(0);


                        //punchInText.setText("Check in");
                        PreferenceHandler.getInstance(AdminScanEmployee.this).setLoginStatus("Logout");

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
                        Toast.makeText(AdminScanEmployee.this, "Failed Due to " + response.message(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText( AdminScanEmployee.this , "Failed due to Bad Internet Connection" , Toast.LENGTH_SHORT ).show( );
                Log.e("TAG", t.toString());
            }
        });


    }

    public boolean locationCheck(){

        final boolean status = false;
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(AdminScanEmployee.this);
            dialog.setMessage("Location is not enable");
            dialog.setPositiveButton("Open Settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                    //get gps
                }
            });
            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub


                }
            });
            dialog.show();
            return false;
        }else{
            return true;
        }
    }
    private String getAddress(LatLng latLng) {
        Geocoder geocoder = new Geocoder(AdminScanEmployee.this, Locale.getDefault());
        String result = null;
        try {
            List<Address> addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    sb.append(address.getAddressLine(i)).append(",");
                }

                result = address.getAddressLine(0);

                return result;
            }
            return result;
        } catch (IOException e) {
            Log.e("MapLocation", "Unable connect to Geocoder", e);
            return result;
        }

    }

    public void getLoginDetails(final int id,final String employeeName,final int employeeId,final String imei) {

        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {

                final LoginDetailsAPI subCategoryAPI = Util.getClient().create(LoginDetailsAPI.class);
                Call<LoginDetails> getProf = subCategoryAPI.getLoginById(id);
                //Call<ArrayList<Blogs>> getBlog = blogApi.getBlogs();

                getProf.enqueue(new Callback<LoginDetails>() {

                    @Override
                    public void onResponse(Call<LoginDetails> call, Response<LoginDetails> response) {

                        if (response.code() == 200 || response.code() == 201 || response.code() == 204) {
                            System.out.println("Inside api");

                            final LoginDetails dto = response.body();

                            if (dto != null) {

                                try {

                                    if(locationCheck()) {

                                        if (gps.canGetLocation()) {

                                            double longi = gps.getLongitude();
                                            double lati = gps.getLatitude();

                                            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                                            SimpleDateFormat sdt = new SimpleDateFormat("MMM dd,yyyy hh:mm a");

                                            LatLng master = new LatLng(lati, longi);
                                            String address = null;
                                            try {
                                                address = getAddress(master);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                            LoginDetails loginDetails = dto;
                                            loginDetails.setEmployeeId(employeeId);
                                            loginDetails.setLatitude("" + lati);
                                            loginDetails.setLongitude("" + longi);
                                            loginDetails.setPlaceId(dto.getPlaceId()+"&"+imei);
                                            loginDetails.setLocation("" + address);
                                            loginDetails.setLogOutTime("" + sdt.format(new Date()));
                                            // loginDetails.setLoginDate(""+sdf.format(new Date()));



                                            try {
                                                LoginDetailsNotificationManagers md = new LoginDetailsNotificationManagers();
                                                md.setTitle("Login Details from " + employeeName);
                                                md.setMessage("Log out at  " + "" + sdt.format(new Date()));
                                                md.setLocation(address);
                                                md.setLongitude("" + longi);
                                                md.setLatitude("" + lati);
                                                md.setLoginDate("" + sdt.format(new Date()));
                                                md.setStatus("Log out");
                                                md.setEmployeeId(employeeId);
                                                md.setManagerId(PreferenceHandler.getInstance(AdminScanEmployee.this).getUserId());
                                                md.setLoginDetailsId(dto.getLoginDetailsId());

                                                updateLogin(loginDetails,  md);


                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                        }

                                    }





                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }

                            }


                        } else {


                            //meet
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginDetails> call, Throwable t) {

                    }
                });

            }

        });
    }
}
