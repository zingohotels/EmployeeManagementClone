package app.zingo.employeemanagements.UI.NewAdminDesigns;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import app.zingo.employeemanagements.Custom.EncryptionHelper;
import app.zingo.employeemanagements.Custom.QRCodeHelper;
import app.zingo.employeemanagements.Model.QRCode;
import app.zingo.employeemanagements.Utils.PreferenceHandler;
import app.zingo.employeemanagements.base.R;

public class QrCodePrintScreen extends AppCompatActivity {

    ImageView qrCodeImageView;
    AppCompatButton downloadImage;
    TextView qr_OrganizationName,qr_OrgId;
    LinearLayout mScreenShot;

    Bitmap bitmap;

    private static final float GESTURE_THRESHOLD_DIP = 10.0f;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{

            setContentView(R.layout.activity_qr_code_print_screen);

            qrCodeImageView = findViewById(R.id.qrCodeImageView);
            downloadImage = findViewById(R.id.download_qr);
            qr_OrganizationName = findViewById(R.id.organization_name_qr);
            qr_OrgId = findViewById(R.id.organization_id);
            mScreenShot = findViewById(R.id.screen_shot);


            if(PreferenceHandler.getInstance(QrCodePrintScreen.this).getCompanyName().length()>=4){
                String upToNCharacters = PreferenceHandler.getInstance(QrCodePrintScreen.this).getCompanyName().substring(0, Math.min(PreferenceHandler.getInstance(QrCodePrintScreen.this).getCompanyName().length(), 4));
                qr_OrgId.setText("Organization Id: "+upToNCharacters+""+ PreferenceHandler.getInstance(QrCodePrintScreen.this).getCompanyId());
            }else{
                qr_OrgId.setText("Organization Id: "+ PreferenceHandler.getInstance(QrCodePrintScreen.this).getCompanyName()+""+ PreferenceHandler.getInstance(QrCodePrintScreen.this).getCompanyId());
            }


            qr_OrganizationName.setText(""+ PreferenceHandler.getInstance(QrCodePrintScreen.this).getCompanyName());
          //  qr_OrgId.setText("Organization Id: "+PreferenceHandler.getInstance(QrCodePrintScreen.this).getCompanyId());

            QRCode qrCode = new QRCode();
            qrCode.setOrganizationId(PreferenceHandler.getInstance(QrCodePrintScreen.this).getCompanyId());
            qrCode.setURL("www.krony app.com/checkout.php?id="+ PreferenceHandler.getInstance(QrCodePrintScreen.this).getCompanyId());


            System.out.println("JSON_Object Data Fire = "+qrCode.toString());

            Gson gson = new Gson();

           // String serializeString = gson.toJson(qrCode);
            String serializeString = "www.krony app.com/checkout.php?id="+ PreferenceHandler.getInstance(QrCodePrintScreen.this).getCompanyId();

            String encryptedString = EncryptionHelper.getInstance().encryptionString(serializeString).encryptMsg();
            setImageBitmap(serializeString);

            bitmap = QRCodeHelper.newInstance(this).setContent(serializeString).setErrorCorrectionLevel(ErrorCorrectionLevel.Q).setMargin(5).getQRCOde();
            qrCodeImageView.setImageBitmap(bitmap);

            downloadImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                   // mScreenShot.setDrawingCacheEnabled(true);


                    Canvas canvas = new Canvas(bitmap);

                    Paint paint = new Paint();
                    paint.setColor(Color.BLACK); // Text Color

                    final float scale = getResources().getDisplayMetrics().density;
                    int mGestureThreshold = (int) (GESTURE_THRESHOLD_DIP * scale + 0.5f);
                    paint.setTextSize(mGestureThreshold); // Text Size
                    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP)); // Text Overlapping Pattern
                    // some more settings...

                    canvas.drawBitmap(bitmap, 0, 0, paint);
                    canvas.drawText("Organization Name : "+ PreferenceHandler.getInstance(QrCodePrintScreen.this).getCompanyName(), 60, 20, paint);

                    if(PreferenceHandler.getInstance(QrCodePrintScreen.this).getCompanyName().length()>=4){
                        
                        String upToNCharacters = PreferenceHandler.getInstance(QrCodePrintScreen.this).getCompanyName().substring(0, Math.min(PreferenceHandler.getInstance(QrCodePrintScreen.this).getCompanyName().length(), 4));
                        canvas.drawText("Organization Id: "+upToNCharacters+""+ PreferenceHandler.getInstance(QrCodePrintScreen.this).getCompanyId(), 60, 40, paint);
                    }else{
                        canvas.drawText("Organization Id: "+ PreferenceHandler.getInstance(QrCodePrintScreen.this).getCompanyName()+""+ PreferenceHandler.getInstance(QrCodePrintScreen.this).getCompanyId(), 60, 40, paint);
                    }

                    canvas.drawText("Powered By Krony  Visit www.KronyApp.com", 60, bitmap.getHeight()-20, paint);
                    // NEWLY ADDED CODE ENDS HERE ]

                  //  Bitmap bitmaps =  bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);

                   if(bitmap!=null){

                       OutputStream fOut = null;
                       Uri outputFileUri ;
                       String fileName = "";
                       File sdImageMainDirectory = null;
                       try {
                           File root = new File(Environment.getExternalStorageDirectory()
                                   + File.separator + "Krony QR" + File.separator);
                           root.mkdirs();
                           sdImageMainDirectory = new File(root, PreferenceHandler.getInstance(QrCodePrintScreen.this).getCompanyName()+"_QR.jpg");
                           outputFileUri = Uri.fromFile(sdImageMainDirectory);
                           fileName = sdImageMainDirectory.getAbsolutePath();
                           fOut = new FileOutputStream(sdImageMainDirectory);
                       } catch (Exception e) {
                           Toast.makeText(QrCodePrintScreen.this, "Error occured. Please try again later.", Toast.LENGTH_SHORT).show();
                       }
                       try {
                           bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                         //  bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                           fOut.flush();
                           fOut.close();
                           Toast.makeText(QrCodePrintScreen.this, "QR Code stored in "+fileName, Toast.LENGTH_SHORT).show();

                           Uri bmpUri = null;
                           if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                               if(sdImageMainDirectory!=null){

                                   bmpUri = FileProvider.getUriForFile(QrCodePrintScreen.this, "app.zingo.employeemanagements.fileprovider", sdImageMainDirectory);
                               }else{
                                   Toast.makeText(QrCodePrintScreen.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                               }

                           }else{
                               bmpUri = Uri.parse("file://"+fileName);
                           }
                           // Uri bmpUri = Uri.parse("file://"+path);
                           Intent shareIntent = new Intent(Intent.ACTION_SEND);
                           shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                           shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);


                           shareIntent.putExtra(Intent.EXTRA_TEXT,"Scan the QR Code for Attendance Check-In and Check-Out");
                           shareIntent.setType("image/*");
                           startActivity(Intent.createChooser(shareIntent,"Share with"));
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

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id)
        {
            case android.R.id.home:

                QrCodePrintScreen.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }*/

    public Bitmap screenShot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    public Bitmap mark(Bitmap src) {

        Canvas canvas = new Canvas();

        Paint paint = new Paint();

        canvas.drawColor(Color.BLACK);

        Bitmap b = Bitmap.createBitmap(200, 200, src.getConfig());
        Canvas c = new Canvas(b);
        c.drawRect(0, 0, 200, 200, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        paint.setTextSize(40);
        paint.setTextScaleX(1.f);
        paint.setAlpha(0);
        paint.setAntiAlias(true);
        c.drawText("Your text", 30, 40, paint);
        paint.setColor(Color.WHITE);

        canvas.drawBitmap(src, 10,10, paint);

        return b;
    }

}
