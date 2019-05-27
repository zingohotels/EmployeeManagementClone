package app.zingo.employeemanagements.UI.NewEmployeeDesign;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.telephony.TelephonyManager;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import app.zingo.employeemanagements.Custom.EncryptionHelper;
import app.zingo.employeemanagements.Custom.QRCodeHelper;
import app.zingo.employeemanagements.Model.EmployeeQrCode;
import app.zingo.employeemanagements.Utils.PreferenceHandler;
import app.zingo.employeemanagements.Utils.TrackGPS;
import app.zingo.employeemanagements.base.R;

public class EmployeeQrCodeGenerate extends AppCompatActivity {

    ImageView qrCodeImageView;
    AppCompatButton downloadImage;
    TextView qr_OrganizationName,qr_OrgId,qr_Emp_Id;
    Bitmap bitmap;

    TrackGPS gps;
    TelephonyManager telephonyManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        try {

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

            setContentView(R.layout.activity_employee_qr_code_generate);

            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Employee QR Code");

            telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);

            qrCodeImageView = findViewById(R.id.qrCodeImageView);
            downloadImage = findViewById(R.id.download_qr);
            qr_OrganizationName = findViewById(R.id.organization_name_qr);
            qr_OrgId = findViewById(R.id.organization_id);
            qr_Emp_Id = findViewById(R.id.employee_id);

            gps = new TrackGPS(EmployeeQrCodeGenerate.this);

            qr_OrganizationName.setText(""+ PreferenceHandler.getInstance(EmployeeQrCodeGenerate.this).getCompanyName());
            qr_OrgId.setText("Organization Id: "+ PreferenceHandler.getInstance(EmployeeQrCodeGenerate.this).getCompanyId());
            qr_Emp_Id.setText("Employee Id: "+ PreferenceHandler.getInstance(EmployeeQrCodeGenerate.this).getUserId());


            EmployeeQrCode qrCode = new EmployeeQrCode();
            qrCode.setOrganizationId(PreferenceHandler.getInstance(EmployeeQrCodeGenerate.this).getCompanyId());
            qrCode.setEmployeeId(PreferenceHandler.getInstance(EmployeeQrCodeGenerate.this).getUserId());
            qrCode.setFullName(PreferenceHandler.getInstance(EmployeeQrCodeGenerate.this).getUserFullName());
            qrCode.setTodayDate("" + new SimpleDateFormat("MM/dd/yyyy").format(new Date()));
            qrCode.setTodayTime("" + new SimpleDateFormat("HH:mm:ss").format(new Date()));

            try {

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if(telephonyManager.getPhoneCount()>1) {

                        String imei = "";

                        for(int i =0 ;i<telephonyManager.getPhoneCount();i++){

                            imei = imei+","+telephonyManager.getDeviceId(i);
                        }

                        qrCode.setIMEI("" + imei);

                    }else{

                        qrCode.setIMEI("" + telephonyManager.getDeviceId());
                    }

                }else{

                    qrCode.setIMEI("" + telephonyManager.getDeviceId());
                }


            }catch (Exception e){
                e.printStackTrace();
            }

            String loginStatus = PreferenceHandler.getInstance(EmployeeQrCodeGenerate.this).getLoginStatus();
            String meetingStatus = PreferenceHandler.getInstance(EmployeeQrCodeGenerate.this).getMeetingLoginStatus();
            if (loginStatus != null && !loginStatus.isEmpty()) {

                if (loginStatus.equalsIgnoreCase("Logout")) {

                    qrCode.setType("Check-In");

                }else if (loginStatus.equalsIgnoreCase("Login")) {

                    qrCode.setType("Check-Out");

                    if (meetingStatus != null && meetingStatus.equalsIgnoreCase("Login")) {

                        qrCode.setMeeting(true);
                        qrCode.setLoginId(PreferenceHandler.getInstance(EmployeeQrCodeGenerate.this).getLoginId());

                    } else {

                        qrCode.setMeeting(false);
                        qrCode.setLoginId(PreferenceHandler.getInstance(EmployeeQrCodeGenerate.this).getLoginId());
                    }

                    //  getLoginDetails(PreferenceHandler.getInstance(getActivity()).getLoginId(),"gps");
                }


            }else{

                qrCode.setType("Check-In");
            }


            System.out.println("JSON_Object Data Fire = "+qrCode.toString());

            Gson gson = new Gson();

            String serializeString = gson.toJson(qrCode);

            String encryptedString = EncryptionHelper.getInstance().encryptionString(serializeString).encryptMsg();
            setImageBitmap(serializeString);



            downloadImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(bitmap!=null){

                        OutputStream fOut = null;
                        Uri outputFileUri ;
                        String fileName = "";
                        try {
                            File root = new File(Environment.getExternalStorageDirectory()
                                    + File.separator + "Zingy QR" + File.separator);
                            root.mkdirs();
                            File sdImageMainDirectory = new File(root, PreferenceHandler.getInstance(EmployeeQrCodeGenerate.this).getCompanyName()+"_QR.jpg");
                            outputFileUri = Uri.fromFile(sdImageMainDirectory);
                            fileName = sdImageMainDirectory.getAbsolutePath();
                            fOut = new FileOutputStream(sdImageMainDirectory);
                        } catch (Exception e) {
                            Toast.makeText(EmployeeQrCodeGenerate.this, "Error occured. Please try again later.", Toast.LENGTH_SHORT).show();
                        }
                        try {
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                            fOut.flush();
                            fOut.close();
                            Toast.makeText(EmployeeQrCodeGenerate.this, "QR Code stored in "+fileName, Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                        }
                    }



                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void setImageBitmap(String encryptedString) {
        bitmap = QRCodeHelper.newInstance(this).setContent(encryptedString).setErrorCorrectionLevel(ErrorCorrectionLevel.Q).setMargin(5).getQRCOde();
        qrCodeImageView.setImageBitmap(bitmap);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id)
        {
            case android.R.id.home:

                EmployeeQrCodeGenerate.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }


}
