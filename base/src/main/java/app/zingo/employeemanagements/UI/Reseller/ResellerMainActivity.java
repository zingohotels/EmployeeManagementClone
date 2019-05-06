package app.zingo.employeemanagements.UI.Reseller;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;

import app.zingo.employeemanagements.Custom.RoundImageView;
import app.zingo.employeemanagements.Model.Employee;
import app.zingo.employeemanagements.Model.EmployeeImages;
import app.zingo.employeemanagements.Model.ResellerProfiles;
import app.zingo.employeemanagements.base.R;
import app.zingo.employeemanagements.UI.LandingScreen;
import app.zingo.employeemanagements.UI.NewAdminDesigns.AdminNewMainScreen;
import app.zingo.employeemanagements.Utils.Constants;
import app.zingo.employeemanagements.Utils.PreferenceHandler;
import app.zingo.employeemanagements.Utils.ThreadExecuter;
import app.zingo.employeemanagements.Utils.Util;
import app.zingo.employeemanagements.WebApi.EmployeeApi;
import app.zingo.employeemanagements.WebApi.EmployeeImageAPI;
import app.zingo.employeemanagements.WebApi.ResellerAPI;
import app.zingo.employeemanagements.WebApi.UploadApi;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResellerMainActivity extends TabActivity implements TabHost.OnTabChangeListener {

    static final String TAG = "ResellerMain";
    RoundImageView mProfileImage;

    LinearLayout mShareLayout,mLogoutLayout;
    TextView mName,mEmail;

    TabHost tabHost;
    View tabIndicatorHome,tabIndicatorStayView,tabIndicatorMenu,tabIndicatorProfile;

    public static String HOME_TAB = "Home Tab";
    public static String STAY_TAB = "Stay Tab";

    public static String MENU_TAB = "Menu Tab";
    public static String PROFILE_TAB = "Profile Tab";


    TextView labelHome, labelStayView, labelMenu,labProfile;
    ImageView imgHome, imgStayView, imgMenu,imgProfile;

    int defaultValue = 0;
    public static final int MY_PERMISSIONS_REQUEST_RESULT = 1;

    boolean firstTime = false;

    boolean doubleBackToExitPressedOnce = false;
    public long[] mTimer = new long[1];
    /* renamed from: t */
    private Timer t;

    ResellerProfiles profile;

    int userId=0,imageId=0;
    String userName="",userEmail="";

    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    String status,selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
            setContentView(R.layout.activity_reseller_main);

            mProfileImage = findViewById(R.id.profilePicture);
            mName = findViewById(R.id.organizationName);
            mEmail = findViewById(R.id.userName);
            mShareLayout = findViewById(R.id.share_layout);
            mLogoutLayout = findViewById(R.id.settingIcon);

            Bundle bundle = getIntent().getExtras();
            if(bundle!=null){

                profile = (ResellerProfiles) bundle.getSerializable("Profile");
                firstTime = bundle.getBoolean("UpdateVersion");

            }
            userId = PreferenceHandler.getInstance(ResellerMainActivity.this).getResellerUserId();
            userName = PreferenceHandler.getInstance(ResellerMainActivity.this).getResellerName();
            userEmail = PreferenceHandler.getInstance(ResellerMainActivity.this).getResellerEmail();

            mName.setText(""+userName);
            String upToNCharacters = userName.substring(0, Math.min(userName.length(), 4));
            mEmail.setText(""+upToNCharacters+userId);


            tabHost = findViewById(android.R.id.tabhost);

            tabIndicatorHome = LayoutInflater.from(this).inflate(R.layout.reseller_tab_host_item, null);
            tabIndicatorStayView= LayoutInflater.from(this).inflate(R.layout.reseller_tab_host_item, null);

            tabIndicatorMenu = LayoutInflater.from(this).inflate(R.layout.reseller_tab_host_item, null);
            tabIndicatorProfile = LayoutInflater.from(this).inflate(R.layout.reseller_tab_host_item, null);

            labelHome = tabIndicatorHome.findViewById(R.id.tab_label);
            imgHome = tabIndicatorHome.findViewById(R.id.tab_image);

            labelStayView = tabIndicatorStayView.findViewById(R.id.tab_label);
            imgStayView = tabIndicatorStayView.findViewById(R.id.tab_image);



            labelMenu = tabIndicatorMenu.findViewById(R.id.tab_label);
            imgMenu = tabIndicatorMenu.findViewById(R.id.tab_image);

            labProfile= tabIndicatorProfile.findViewById(R.id.tab_label);
            imgProfile = tabIndicatorProfile.findViewById(R.id.tab_image);


            TabHost.TabSpec tabHome = tabHost.newTabSpec(HOME_TAB);
            TabHost.TabSpec tabStay= tabHost.newTabSpec(STAY_TAB);

            TabHost.TabSpec tabMenu = tabHost.newTabSpec(MENU_TAB);
            TabHost.TabSpec tabProfile = tabHost.newTabSpec(PROFILE_TAB);
            /*9C9C9C*/
            labelHome.setText("Home");
            imgHome.setImageResource(R.drawable.home_icon);
            tabHome.setIndicator(tabIndicatorHome);
            Intent dash = new Intent(this, ResellerDashBoard.class);
            dash.putExtra("UpdateVersion",firstTime);
            tabHome.setContent(dash);

            labelStayView.setText("Organizations");
            imgStayView.setImageResource(R.drawable.organization_icons);
            tabStay.setIndicator(tabIndicatorStayView);
            tabStay.setContent(new Intent(this, ResellerOrganizationList.class));
            //tabStay.setContent(new Intent(this, RoomViewStayActivity.class));



            labelMenu.setText("Payments");
            imgMenu.setImageResource(R.drawable.booking_list_tab_icon);
            tabMenu.setIndicator(tabIndicatorMenu);
            tabMenu.setContent(new Intent(this, ResellerPaymentListScreen.class));


            labProfile.setText("Profile");
            imgProfile.setImageResource(R.drawable.profile_image);
            tabProfile.setIndicator(tabIndicatorProfile);
            tabProfile.setContent(new Intent(this, ResellerProfileScreen.class));


            tabHost.setOnTabChangedListener(this);







            /** Add the tabs to the TabHost to display. */
            tabHost.addTab(tabHome);
            tabHost.addTab(tabStay);

            tabHost.addTab(tabMenu);
            tabHost.addTab(tabProfile);


            int page = getIntent().getIntExtra("ARG_PAGE", defaultValue);



            int pageno = getIntent().getIntExtra("TABNAME",0);
            if(pageno != 0)
            {
                tabHost.setCurrentTab(pageno);
            }
            else
            {
                tabHost.setCurrentTab(page);
            }


            mProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    selectImage();
                }
            });

            if(profile==null){
                if(userId!=0){

                    getProfile(userId,mProfileImage);
                }

            }else{

                        String base=profile.getProfilePhoto();
                        if(base != null && !base.isEmpty()){
                            Picasso.with(ResellerMainActivity.this).load(base).placeholder(R.drawable.profile_image).
                                    error(R.drawable.profile_image).into(mProfileImage);


                        }




            }

            mLogoutLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PreferenceHandler.getInstance(ResellerMainActivity.this).clear();

                    Intent log = new Intent(ResellerMainActivity.this, LandingScreen.class);
                    log.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    log.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    Toast.makeText(ResellerMainActivity.this,"Logout",Toast.LENGTH_SHORT).show();
                    startActivity(log);
                    ResellerMainActivity.this.finish();
                }
            });

            mShareLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String upToNCharacters = PreferenceHandler.getInstance(ResellerMainActivity.this).getResellerName().substring(0, Math.min(PreferenceHandler.getInstance(ResellerMainActivity.this).getResellerName().length(), 4));


                    String text = "Hello this is Zingy Reseller Employee Management App built for resellers to earn more money. You can resell the app and make more money for every new referral and earn commission for lifetime.\n\n"+
                                        "Step to join the Zingy Reseller Referral Programme-\n" +
                            "1.  Signup using your phone number.\n" +
                            "\n" +
                            "2.  Open the Zingy Employee Management App and visit the profile Section, and find out your referral code. It’s an alpha-numeric code like: "+upToNCharacters+PreferenceHandler.getInstance(ResellerMainActivity.this).getResellerUserId()+"\n" +
                            "\n" +
                            "3.  Share the App with your Referral Companies using your Referral Code\n" +
                            "\n" +
                            "4.  When  your referred company signs up. You can make money for every new signup and earn commission for lifetime.\n" +
                            "\n" +
                            " \n" +
                            "\n" +
                            "My Zingy Referral Code is "+upToNCharacters+PreferenceHandler.getInstance(ResellerMainActivity.this).getResellerUserId()+". Don’t Forget to use my Referral Code.\n" +
                            "\n" +
                            "Keep Sharing\n" +
                            "\n" +
                            " \n" +
                            "\n" +
                            "To Download the app click here:\n"+
                            "https://play.google.com/store/apps/details?id=app.zingo.employeemanagements";


                    Intent emailIntent = new Intent(Intent.ACTION_SEND);
                    emailIntent.setType("text/plain");


                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Employee Management App Invitation");


                    emailIntent.putExtra(Intent.EXTRA_TEXT, text);
                    //emailIntent.putExtra(Intent.EXTRA_HTML_TEXT, body);
                    startActivity(Intent.createChooser(emailIntent, "Send"));
                }
            });


        }catch (Exception w){
            w.printStackTrace();
        }

    }

    public void getProfile(final int id,final ImageView mProfileImage ){

        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {

                final ResellerAPI subCategoryAPI = Util.getClient().create(ResellerAPI.class);
                Call<ResellerProfiles> getProf = subCategoryAPI.getResellerProfileById(id);
                //Call<ArrayList<Blogs>> getBlog = blogApi.getBlogs();

                getProf.enqueue(new Callback<ResellerProfiles>() {

                    @Override
                    public void onResponse(Call<ResellerProfiles> call, Response<ResellerProfiles> response) {

                        if (response.code() == 200)
                        {
                            System.out.println("Inside api");

                            profile = response.body();

                            mName.setText(""+profile.getFullName());
                            String upToNCharacters = profile.getFullName().substring(0, Math.min(profile.getFullName().length(), 4));
                            mEmail.setText(""+upToNCharacters+userId);

                                    String base=profile.getProfilePhoto();
                                    if(base != null && !base.isEmpty()){
                                        Picasso.with(ResellerMainActivity.this).load(base).placeholder(R.drawable.profile_image).
                                                error(R.drawable.profile_image).into(mProfileImage);


                                    }




                        }
                    }

                    @Override
                    public void onFailure(Call<ResellerProfiles> call, Throwable t) {

                    }
                });

            }

        });
    }

    private void selectImage() {
        final CharSequence[] items = {"Choose from Library",
                "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(ResellerMainActivity.this);
        builder.setTitle("Add Image!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Choose from Library")) {

                    galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);

        }
    }

    private void onSelectFromGalleryResult(Intent data) {

        try{


            Uri selectedImageUri = data.getData( );
            String picturePath = getPath( ResellerMainActivity.this, selectedImageUri );
            Log.d("Picture Path", picturePath);
            String[] all_path = {picturePath};
            selectedImage = all_path[0];
            System.out.println("allpath === "+data.getPackage());
            for (String s:all_path)
            {
                //System.out.println(s);
                File imgFile = new  File(s);
                if(imgFile.exists()) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    //addView(null,Util.getResizedBitmap(myBitmap,400));
                    addImage(null,Util.getResizedBitmap(myBitmap,700));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    public static String getPath(Context context, Uri uri ) {
        String result = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver( ).query( uri, proj, null, null, null );
        if(cursor != null){
            if ( cursor.moveToFirst( ) ) {
                int column_index = cursor.getColumnIndexOrThrow( proj[0] );
                result = cursor.getString( column_index );
            }
            cursor.close( );
        }
        if(result == null) {
            result = "Not found";
        }
        return result;
    }

    public void addImage(String uri,Bitmap bitmap)
    {
        try{


            if(uri != null)
            {

            }
            else if(bitmap != null)
            {
                mProfileImage.setImageBitmap(bitmap);

                if(selectedImage != null && !selectedImage.isEmpty())
                {
                    File file = new File(selectedImage);

                    if(file.length() <= 1*1024*1024)
                    {
                        FileOutputStream out = null;
                        String[] filearray = selectedImage.split("/");
                        final String filename = getFilename(filearray[filearray.length-1]);

                        out = new FileOutputStream(filename);
                        Bitmap myBitmap = BitmapFactory.decodeFile(selectedImage);

//          write the compressed bitmap at the field_icon specified by filename.
                        myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

                        uploadImage(filename,profile);



                    }
                    else
                    {
                        compressImage(selectedImage,profile);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void uploadImage(final String filePath,final ResellerProfiles employee)
    {
        //String filePath = getRealPathFromURIPath(uri, ImageUploadActivity.this);

        final File file = new File(filePath);
        int size = 1*1024*1024;

        if(file != null)
        {
            if(file.length() > size)
            {
                System.out.println(file.length());
                compressImage(filePath,employee);
            }
            else
            {
                final ProgressDialog dialog = new ProgressDialog(ResellerMainActivity.this);
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



                            if(profile!=null){

                                if(Util.IMAGE_URL==null){
                                    profile.setProfilePhoto(Constants.IMAGE_URL+ response.body());
                                }else{
                                    profile.setProfilePhoto(Util.IMAGE_URL+ response.body());
                                }

                                String upToNCharacters = profile.getFullName().substring(0, Math.min(profile.getFullName().length(), 4));
                                profile.setReferalCodeForOrganization(upToNCharacters+""+profile.getResellerProfileId());
                                updateProfileImage(profile);



                            }




                           // addProfileImage(employeeImages);





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

    public String compressImage(String filePath,final  ResellerProfiles Employee) {

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

            uploadImage(filename,Employee);


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

    private void updateProfileImage(final ResellerProfiles resellerProfiles) {


        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setTitle("Updating Image..");
        dialog.show();

        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {
                ResellerAPI auditApi = Util.getClient().create(ResellerAPI.class);
                Call<ResellerProfiles> response = auditApi.updateResellerProfiles(resellerProfiles.getResellerProfileId(),resellerProfiles);
                response.enqueue(new Callback<ResellerProfiles>() {
                    @Override
                    public void onResponse(Call<ResellerProfiles> call, Response<ResellerProfiles> response) {
                        if(dialog != null)
                        {
                            dialog.dismiss();
                        }
                        System.out.println(response.code());

                        if(response.code() == 201||response.code() == 200||response.code() == 204)
                        {
                            Toast.makeText(ResellerMainActivity.this,"Profile Image Updated",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(ResellerMainActivity.this,response.message(),Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResellerProfiles> call, Throwable t) {
                        if(dialog != null)
                        {
                            dialog.dismiss();
                        }
                        Toast.makeText(ResellerMainActivity.this,t.getMessage(),Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });
    }

    @Override
    public void onTabChanged(String tabId) {

        labelHome.setTextColor(Color.parseColor("#4D4D4D"));
        labelHome.setTypeface(Typeface.DEFAULT);
        imgHome.setImageResource(R.drawable.home_icon);

        labelStayView.setTextColor(Color.parseColor("#4D4D4D"));
        labelStayView.setTypeface(Typeface.DEFAULT);
        imgStayView.setImageResource(R.drawable.organization_icons);



        labelMenu.setTextColor(Color.parseColor("#4D4D4D"));
        labelMenu.setTypeface(Typeface.DEFAULT);
        imgMenu.setImageResource(R.drawable.booking_list_tab_icon);


        labProfile.setTextColor(Color.parseColor("#4D4D4D"));
        labProfile.setTypeface(Typeface.DEFAULT);
        imgProfile.setImageResource(R.drawable.profile_image);


        changeTabSelection(tabId);

    }

    public void changeTabSelection(String tabId) {
        if (HOME_TAB.equals(tabId)) {
            labelHome.setTextColor(Color.parseColor("#FA7A7A"));
            labelHome.setTypeface(null, Typeface.BOLD);
            imgHome.setImageResource(R.drawable.selected_home_icon);
        } else if (STAY_TAB.equals(tabId)) {
            labelStayView.setTextColor(Color.parseColor("#FA7A7A"));
            labelStayView.setTypeface(null, Typeface.BOLD);
            imgStayView.setImageResource(R.drawable.selected_organization);
        } else if (MENU_TAB.equals(tabId)) {
            labelMenu.setTextColor(Color.parseColor("#FA7A7A"));
            labelMenu.setTypeface(null, Typeface.BOLD);
            imgMenu.setImageResource(R.drawable.selelcted_booking_list_icon);
        }else if (PROFILE_TAB.equals(tabId)) {
            labProfile.setTextColor(Color.parseColor("#FA7A7A"));
            labProfile.setTypeface(null, Typeface.BOLD);
            imgProfile.setImageResource(R.drawable.profile_image);
        }
    }


}
