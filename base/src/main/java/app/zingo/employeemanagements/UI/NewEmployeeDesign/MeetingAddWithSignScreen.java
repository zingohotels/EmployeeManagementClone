package app.zingo.employeemanagements.UI.NewEmployeeDesign;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import app.zingo.employeemanagements.Model.Employee;
import app.zingo.employeemanagements.Model.EmployeeImages;
import app.zingo.employeemanagements.Model.MeetingDetailsNotificationManagers;
import app.zingo.employeemanagements.Model.Meetings;
import app.zingo.employeemanagements.base.R;
import app.zingo.employeemanagements.UI.Common.DigitalSignature;
import app.zingo.employeemanagements.UI.EmployeeSignUp;
import app.zingo.employeemanagements.UI.NewAdminDesigns.AdminNewMainScreen;
import app.zingo.employeemanagements.Utils.Constants;
import app.zingo.employeemanagements.Utils.PreferenceHandler;
import app.zingo.employeemanagements.Utils.TrackGPS;
import app.zingo.employeemanagements.Utils.Util;
import app.zingo.employeemanagements.WebApi.MeetingNotificationAPI;
import app.zingo.employeemanagements.WebApi.MeetingsAPI;
import app.zingo.employeemanagements.WebApi.UploadApi;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MeetingAddWithSignScreen extends AppCompatActivity {

    TextInputEditText mClientName,mClientMobile,mClientMail,mPurpose;
    EditText mDetails;
    CheckBox mGetSign,mTakeImage;
    ImageView mImageView;
    Button mSave;

    Toolbar toolbar;
    Button  mClear, mGetSigns, mCancel;

    private GoogleApiClient mLocationClient;
    Location currentLocation;

    //Location
    TrackGPS gps;
    double latitude,longitude;

    // Creating Separate Directory for saving Generated Images
    String DIRECTORY = Environment.getExternalStorageDirectory().getPath() + "/Zingy Apps/";
    String pic_name = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
    String StoredPath = DIRECTORY + pic_name + ".png";
    String StoredPathSelfie = DIRECTORY + pic_name+"selfie" + ".png";

    File file;
    Dialog dialog;
    LinearLayout mContent;
    View view;
    signature mSignature;
    Bitmap bitmap;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    String mCurrentPhotoPath;
    Meetings loginDetails;
    MeetingDetailsNotificationManagers md;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{

            setContentView(R.layout.activity_meeting_add_with_sign_screen);

            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Client Meeting");

            mSave = findViewById(R.id.save);
            mDetails = findViewById(R.id.meeting_remarks);
            mClientName = findViewById(R.id.client_name);
            mClientMobile = findViewById(R.id.client_contact_number);
            mClientMail = findViewById(R.id.client_contact_email);
            mPurpose = findViewById(R.id.purpose_meeting);
            mGetSign = findViewById(R.id.get_sign_check);
            mTakeImage = findViewById(R.id.get_image_check);
            mImageView = findViewById(R.id.selfie_pic);

            mSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    validate();



                }
            });



        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public void validate(){

        String client = mClientName.getText().toString();
        String purpose = mPurpose.getText().toString();
        String detail = mDetails.getText().toString();
        String mobile = mClientMobile.getText().toString();
        String email = mClientMail.getText().toString();

        if(client==null||client.isEmpty()){

            Toast.makeText(MeetingAddWithSignScreen.this, "Please mention client name", Toast.LENGTH_SHORT).show();

        }else if(purpose==null||purpose.isEmpty()){

            Toast.makeText(MeetingAddWithSignScreen.this, "Please mention purpose of meeting", Toast.LENGTH_SHORT).show();

        }else if(detail==null||detail.isEmpty()){

            Toast.makeText(MeetingAddWithSignScreen.this, "Please mention remarks about meeting", Toast.LENGTH_SHORT).show();

        }else{



            gps = new TrackGPS(MeetingAddWithSignScreen.this);
            if(gps.canGetLocation())
            {
                System.out.println("Long and lat Rev"+gps.getLatitude()+" = "+gps.getLongitude());
                latitude = gps.getLatitude();
                longitude = gps.getLongitude();


                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                SimpleDateFormat sdt = new SimpleDateFormat("MMM dd,yyyy hh:mm a");

                LatLng master = new LatLng(latitude,longitude);
                String address = getAddress(master);

                loginDetails = new Meetings();
                loginDetails.setEmployeeId(PreferenceHandler.getInstance(MeetingAddWithSignScreen.this).getUserId());
                loginDetails.setStartLatitude(""+latitude);
                loginDetails.setStartLongitude(""+longitude);
                loginDetails.setStartLocation(""+address);
                loginDetails.setStartTime(""+sdt.format(new Date()));
                loginDetails.setEndLatitude(""+latitude);
                loginDetails.setEndLongitude(""+longitude);
                loginDetails.setEndLocation(""+address);
                loginDetails.setEndTime(""+sdt.format(new Date()));
                loginDetails.setMeetingDate(""+sdf.format(new Date()));
                loginDetails.setMeetingAgenda(purpose);
                loginDetails.setMeetingDetails(detail);
                loginDetails.setStatus("Completed");

                String contact = "";

                if(email!=null&&!email.isEmpty()){
                        contact = contact+"%"+email;
                }

                if(mobile!=null&&!mobile.isEmpty()){
                    contact = contact+"%"+mobile;
                }

                if(contact!=null&&!contact.isEmpty()){
                    loginDetails.setMeetingPersonDetails(client+""+contact);
                }else{
                    loginDetails.setMeetingPersonDetails(client);
                }
                try {

                    md = new MeetingDetailsNotificationManagers();
                    md.setTitle("Meeting Details from "+PreferenceHandler.getInstance(MeetingAddWithSignScreen.this).getUserFullName());
                    md.setMessage("Meeting with "+client+" for "+purpose);
                    md.setLocation(address);
                    md.setLongitude(""+longitude);
                    md.setLatitude(""+latitude);
                    md.setMeetingDate(""+sdt.format(new Date()));
                    md.setStatus("Completed");
                    md.setEmployeeId(PreferenceHandler.getInstance(MeetingAddWithSignScreen.this).getUserId());
                    md.setManagerId(PreferenceHandler.getInstance(MeetingAddWithSignScreen.this).getManagerId());
                    md.setMeetingPerson(client);
                    md.setMeetingsDetails(purpose);
                    md.setMeetingComments(detail);



                    if (mGetSign.isChecked()&&!mTakeImage.isChecked()){
                        // Method to create Directory, if the Directory doesn't exists
                        file = new File(DIRECTORY);
                        if (!file.exists()) {
                            file.mkdir();
                        }

                        // Dialog Function
                        dialog = new Dialog(MeetingAddWithSignScreen.this);
                        // Removing the features of Normal Dialogs
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.dialog_signature);
                        dialog.setCancelable(true);

                        dialog_action(loginDetails,md,"null");

                    }else if (mGetSign.isChecked()&&mTakeImage.isChecked()){

                        file = new File(DIRECTORY);
                        if (!file.exists()) {
                            file.mkdir();
                        }

                        // Dialog Function
                        dialog = new Dialog(MeetingAddWithSignScreen.this);
                        // Removing the features of Normal Dialogs
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.dialog_signature);
                        dialog.setCancelable(true);

                        dialog_action(loginDetails,md,"Selfie");

                    }else if (!mGetSign.isChecked()&&mTakeImage.isChecked()){

                        file = new File(DIRECTORY);
                        if (!file.exists()) {
                            file.mkdir();
                        }

                       /* // Dialog Function
                        dialog = new Dialog(MeetingAddWithSignScreen.this);
                        // Removing the features of Normal Dialogs
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.dialog_signature);
                        dialog.setCancelable(true);*/

                       dispatchTakePictureIntent();

                        //dialog_action(loginDetails,md,"Selfie");

                    }else{
                        addMeeting(loginDetails,md);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            else
            {

            }

        }
    }

    private String getAddress(LatLng latLng) {
        Geocoder geocoder = new Geocoder(MeetingAddWithSignScreen.this, Locale.getDefault());
        String result = null;
        try {
            List<Address> addressList = geocoder.getFromLocation(
                    latLng.latitude, latLng.longitude, 1);
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

    public void addMeeting(final Meetings loginDetails, final MeetingDetailsNotificationManagers md) {



        final ProgressDialog dialog = new ProgressDialog(MeetingAddWithSignScreen.this);
        dialog.setMessage("Saving Details..");
        dialog.setCancelable(false);
        dialog.show();

        MeetingsAPI apiService = Util.getClient().create(MeetingsAPI.class);

        Call<Meetings> call = apiService.addMeeting(loginDetails);

        call.enqueue(new Callback<Meetings>() {
            @Override
            public void onResponse(Call<Meetings> call, Response<Meetings> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {
                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201) {

                        Meetings s = response.body();

                        if(s!=null){



                            md.setMeetingsId(s.getMeetingsId());
                            Toast.makeText(MeetingAddWithSignScreen.this, "Meeting Saved", Toast.LENGTH_SHORT).show();



                            saveMeetingNotification(md);

                        }




                    }else {
                        Toast.makeText(MeetingAddWithSignScreen.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception ex)
                {

                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }
                    ex.printStackTrace();
                }
//                callGetStartEnd();
            }

            @Override
            public void onFailure(Call<Meetings> call, Throwable t) {

                if(dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
                Toast.makeText(MeetingAddWithSignScreen.this, "Failed Due to "+t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());
            }
        });



    }

    public void saveMeetingNotification(final MeetingDetailsNotificationManagers md) {



        final ProgressDialog dialog = new ProgressDialog(MeetingAddWithSignScreen.this);
        dialog.setMessage("Saving Details..");
        dialog.setCancelable(false);
        dialog.show();

        MeetingNotificationAPI apiService = Util.getClient().create(MeetingNotificationAPI.class);

        Call<MeetingDetailsNotificationManagers> call = apiService.saveMeetingNotification(md);

        call.enqueue(new Callback<MeetingDetailsNotificationManagers>() {
            @Override
            public void onResponse(Call<MeetingDetailsNotificationManagers> call, Response<MeetingDetailsNotificationManagers> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {
                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201) {

                        MeetingDetailsNotificationManagers s = response.body();

                        if(s!=null){

                            MeetingDetailsNotificationManagers md = new MeetingDetailsNotificationManagers();
                            md.setTitle(s.getTitle());
                            md.setMessage(s.getMessage());
                            md.setLocation(s.getLocation());
                            md.setLongitude(""+s.getLongitude());
                            md.setLatitude(""+s.getLatitude());
                            md.setMeetingDate(""+s.getMeetingDate());
                            md.setStatus(s.getStatus());
                            md.setEmployeeId(s.getManagerId());
                            md.setManagerId(s.getEmployeeId());
                            md.setMeetingPerson(s.getMeetingPerson());
                            md.setMeetingsDetails(s.getMeetingsDetails());
                            md.setMeetingComments(s.getMeetingComments());
                            md.setMeetingsId(s.getMeetingsId());
                            md.setSenderId(Constants.SENDER_ID);
                            md.setServerId(Constants.SERVER_ID);

                            sendMeetingNotification(md);

                        }




                    }else {
                        Toast.makeText(MeetingAddWithSignScreen.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception ex)
                {

                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }
                    ex.printStackTrace();
                }
//                callGetStartEnd();
            }

            @Override
            public void onFailure(Call<MeetingDetailsNotificationManagers> call, Throwable t) {

                if(dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
                Toast.makeText(MeetingAddWithSignScreen.this, "Failed Due to "+t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());
            }
        });



    }

    public void sendMeetingNotification(final MeetingDetailsNotificationManagers md) {



        final ProgressDialog dialog = new ProgressDialog(MeetingAddWithSignScreen.this);
        dialog.setMessage("Sending Details..");
        dialog.setCancelable(false);
        dialog.show();

        MeetingNotificationAPI apiService = Util.getClient().create(MeetingNotificationAPI.class);

        Call<ArrayList<String>> call = apiService.sendMeetingNotification(md);

        call.enqueue(new Callback<ArrayList<String>>() {
            @Override
            public void onResponse(Call<ArrayList<String>> call, Response<ArrayList<String>> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {
                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201) {


                        MeetingAddWithSignScreen.this.finish();



                    }else {
                        Toast.makeText(MeetingAddWithSignScreen.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception ex)
                {

                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }
                    ex.printStackTrace();
                }
//                callGetStartEnd();
            }

            @Override
            public void onFailure(Call<ArrayList<String>> call, Throwable t) {

                if(dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
                Toast.makeText(MeetingAddWithSignScreen.this, "Failed Due to "+t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());
            }
        });



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id)
        {
            case android.R.id.home:

                MeetingAddWithSignScreen.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    // Function for Digital Signature
    public void dialog_action(final Meetings loginDetails, final MeetingDetailsNotificationManagers md,final String type) {

        mContent = dialog.findViewById(R.id.linearLayout);
        mSignature = new signature(getApplicationContext(), null);
        mSignature.setBackgroundColor(Color.WHITE);
        // Dynamically generating Layout through java code
        mContent.addView(mSignature, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mClear = dialog.findViewById(R.id.clear);
        mGetSigns = dialog.findViewById(R.id.getsign);
        mGetSigns.setEnabled(false);
        mCancel = dialog.findViewById(R.id.cancel);
        view = mContent;

        mClear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.v("log_tag", "Panel Cleared");
                mSignature.clear();
                mGetSigns.setEnabled(false);
            }
        });

        mGetSigns.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                Log.v("log_tag", "Panel Saved");
                view.setDrawingCacheEnabled(true);
                mSignature.save(view, StoredPath,loginDetails,md,type);
                dialog.dismiss();
                Toast.makeText(getApplicationContext(), "Successfully Saved", Toast.LENGTH_SHORT).show();
                // Calling the same class
                //recreate();

            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.v("log_tag", "Panel Canceled");
                dialog.dismiss();
                // Calling the same class
                recreate();
            }
        });
        dialog.show();
    }

    public class signature extends View {

        private static final float STROKE_WIDTH = 5f;
        private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        private Paint paint = new Paint();
        private Path path = new Path();

        private float lastTouchX;
        private float lastTouchY;
        private final RectF dirtyRect = new RectF();

        public signature(Context context, AttributeSet attrs) {
            super(context, attrs);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);
        }

        public void save(View v, String StoredPath,final Meetings loginDetails, final MeetingDetailsNotificationManagers md,final String type) {
            Log.v("log_tag", "Width: " + v.getWidth());
            Log.v("log_tag", "Height: " + v.getHeight());
            if (bitmap == null) {
                bitmap = Bitmap.createBitmap(mContent.getWidth(), mContent.getHeight(), Bitmap.Config.RGB_565);
            }
            Canvas canvas = new Canvas(bitmap);
            try {
                // Output the file
                FileOutputStream mFileOutStream = new FileOutputStream(StoredPath);
                v.draw(canvas);

                // Convert the output file to Image such as .png
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, mFileOutStream);
                mFileOutStream.flush();
                mFileOutStream.close();

                File file = new File(StoredPath);

                if(file.length() <= 1*1024*1024)
                {
                    FileOutputStream out = null;
                    String[] filearray = StoredPath.split("/");
                    final String filename = getFilename(filearray[filearray.length-1]);

                    out = new FileOutputStream(filename);
                    Bitmap myBitmap = BitmapFactory.decodeFile(StoredPath);

//          write the compressed bitmap at the field_icon specified by filename.
                    myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

                    uploadImage(filename,loginDetails,md,type);



                }
                else
                {
                    compressImage(StoredPath,loginDetails,md,type);
                }

            } catch (Exception e) {
                Log.v("log_tag", e.toString());
            }

        }



        public void clear() {
            path.reset();
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawPath(path, paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float eventX = event.getX();
            float eventY = event.getY();
            mGetSigns.setEnabled(true);

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(eventX, eventY);
                    lastTouchX = eventX;
                    lastTouchY = eventY;
                    return true;

                case MotionEvent.ACTION_MOVE:

                case MotionEvent.ACTION_UP:

                    resetDirtyRect(eventX, eventY);
                    int historySize = event.getHistorySize();
                    for (int i = 0; i < historySize; i++) {
                        float historicalX = event.getHistoricalX(i);
                        float historicalY = event.getHistoricalY(i);
                        expandDirtyRect(historicalX, historicalY);
                        path.lineTo(historicalX, historicalY);
                    }
                    path.lineTo(eventX, eventY);
                    break;

                default:
                    debug("Ignored touch event: " + event.toString());
                    return false;
            }

            invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                    (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

            lastTouchX = eventX;
            lastTouchY = eventY;

            return true;
        }

        private void debug(String string) {

            Log.v("log_tag", string);

        }

        private void expandDirtyRect(float historicalX, float historicalY) {
            if (historicalX < dirtyRect.left) {
                dirtyRect.left = historicalX;
            } else if (historicalX > dirtyRect.right) {
                dirtyRect.right = historicalX;
            }

            if (historicalY < dirtyRect.top) {
                dirtyRect.top = historicalY;
            } else if (historicalY > dirtyRect.bottom) {
                dirtyRect.bottom = historicalY;
            }
        }

        private void resetDirtyRect(float eventX, float eventY) {
            dirtyRect.left = Math.min(lastTouchX, eventX);
            dirtyRect.right = Math.max(lastTouchX, eventX);
            dirtyRect.top = Math.min(lastTouchY, eventY);
            dirtyRect.bottom = Math.max(lastTouchY, eventY);
        }
    }

    public String getFilename(String filePath) {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "MyFolder/Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        System.out.println("getFilePath = "+filePath);
        String uriSting;
        if(filePath.contains(".jpg"))
        {
            uriSting = (file.getAbsolutePath() + "/" + filePath);
        }
        else
        {
            uriSting = (file.getAbsolutePath() + "/" + filePath+".jpg" );
        }
        return uriSting;

    }

    private void uploadImage(final String filePath,final Meetings loginDetails, final MeetingDetailsNotificationManagers md,final String type)
    {
        //String filePath = getRealPathFromURIPath(uri, ImageUploadActivity.this);

        final File file = new File(filePath);
        int size = 1*1024*1024;

        if(file != null)
        {
            if(file.length() > size)
            {
                System.out.println(file.length());
                compressImage(filePath,loginDetails,md,type);
            }
            else
            {
                final ProgressDialog dialog = new ProgressDialog(MeetingAddWithSignScreen.this);
                dialog.setCancelable(false);
                dialog.setTitle("Uploading Image..");
                dialog.show();
                Log.d("Image Upload", "Filename " + file.getName());

                RequestBody mFile = RequestBody.create(MediaType.parse("image"), file);
                MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), mFile);
                RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());
                UploadApi uploadImage = Util.getClient().create(UploadApi.class);

                Call<String> fileUpload = uploadImage.uploadProfileImages(fileToUpload, filename);
                fileUpload.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if(dialog != null && dialog.isShowing())
                        {
                            dialog.dismiss();
                        }





                        try {

                            if(type!=null&&type.equalsIgnoreCase("Selfie")){
                                if(Util.IMAGE_URL==null){
                                    loginDetails.setEndPlaceID(Constants.IMAGE_URL+ response.body());
                                }else{
                                    loginDetails.setEndPlaceID(Util.IMAGE_URL+ response.body());
                                }

                                dispatchTakePictureIntent();

                            }else if(type!=null&&type.equalsIgnoreCase("Done")){

                                if(Util.IMAGE_URL==null){
                                    loginDetails.setStartPlaceID(Constants.IMAGE_URL+ response.body());
                                }else{
                                    loginDetails.setStartPlaceID(Util.IMAGE_URL+ response.body());
                                }
                                addMeeting(loginDetails,md);

                            }else{

                                if(Util.IMAGE_URL==null){
                                    loginDetails.setEndPlaceID(Constants.IMAGE_URL+ response.body());
                                }else{
                                    loginDetails.setEndPlaceID(Util.IMAGE_URL+ response.body());
                                }
                                addMeeting(loginDetails,md);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        if(filePath.contains("MyFolder/Images"))
                        {
                            file.delete();
                        }
                    }
                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.d("UpdateCate", "Error " + t.getMessage());
                    }
                });
            }
        }
    }


    public String compressImage(String filePath,final Meetings loginDetails, final MeetingDetailsNotificationManagers md,final String type) {

        //String filePath = getRealPathFromURI(imageUri);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = actualHeight/2;//2033.0f;
        float maxWidth = actualWidth/2;//1011.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String[] filearray = filePath.split("/");
        final String filename = getFilename(filearray[filearray.length-1]);
        try {
            out = new FileOutputStream(filename);


//          write the compressed bitmap at the field_icon specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

            uploadImage(filename,loginDetails,md,type);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filename;

    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            saveSelfie(imageBitmap,StoredPathSelfie);

        }
    }

    public void saveSelfie(Bitmap bitmap, String StoredPath) {

        if (bitmap == null) {
            Toast.makeText(MeetingAddWithSignScreen.this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }

        try {
            // Output the file
            FileOutputStream mFileOutStream = new FileOutputStream(StoredPath);


            // Convert the output file to Image such as .png
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, mFileOutStream);
            mFileOutStream.flush();
            mFileOutStream.close();

            File file = new File(StoredPath);

            if(file.length() <= 1*1024*1024)
            {
                FileOutputStream out = null;
                String[] filearray = StoredPath.split("/");
                final String filename = getFilename(filearray[filearray.length-1]);

                out = new FileOutputStream(filename);
                Bitmap myBitmap = BitmapFactory.decodeFile(StoredPath);

//          write the compressed bitmap at the field_icon specified by filename.
                myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

                uploadImage(filename,loginDetails,md,"Done");

                mImageView.setVisibility(View.VISIBLE);
                mImageView.setImageBitmap(bitmap);

            }
            else
            {
                 compressImage(StoredPath,loginDetails,md,"Done");
            }

        } catch (Exception e) {
            Log.v("log_tag", e.toString());
        }

    }
}
