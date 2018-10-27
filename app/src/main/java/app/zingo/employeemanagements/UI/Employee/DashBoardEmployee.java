package app.zingo.employeemanagements.UI.Employee;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import app.zingo.employeemanagements.Adapter.EmployeeAdapter;
import app.zingo.employeemanagements.Adapter.NavigationListAdapter;
import app.zingo.employeemanagements.FireBase.SharedPrefManager;
import app.zingo.employeemanagements.Model.Departments;
import app.zingo.employeemanagements.Model.Employee;
import app.zingo.employeemanagements.Model.EmployeeDeviceMapping;
import app.zingo.employeemanagements.Model.EmployeeImages;
import app.zingo.employeemanagements.Model.LoginDetails;
import app.zingo.employeemanagements.Model.LoginDetailsNotificationManagers;
import app.zingo.employeemanagements.Model.MeetingDetailsNotificationManagers;
import app.zingo.employeemanagements.Model.Meetings;
import app.zingo.employeemanagements.Model.NavBarItems;
import app.zingo.employeemanagements.R;
import app.zingo.employeemanagements.UI.Admin.DashBoardAdmin;
import app.zingo.employeemanagements.UI.Common.ChangePasswordScreen;
import app.zingo.employeemanagements.UI.Company.OrganizationDetailScree;
import app.zingo.employeemanagements.UI.Login.LoginScreen;
import app.zingo.employeemanagements.Utils.Constants;
import app.zingo.employeemanagements.Utils.PreferenceHandler;
import app.zingo.employeemanagements.Utils.ThreadExecuter;
import app.zingo.employeemanagements.Utils.TrackGPS;
import app.zingo.employeemanagements.Utils.Util;
import app.zingo.employeemanagements.WebApi.DepartmentApi;
import app.zingo.employeemanagements.WebApi.EmployeeApi;
import app.zingo.employeemanagements.WebApi.EmployeeDeviceApi;
import app.zingo.employeemanagements.WebApi.EmployeeImageAPI;
import app.zingo.employeemanagements.WebApi.LoginDetailsAPI;
import app.zingo.employeemanagements.WebApi.LoginNotificationAPI;
import app.zingo.employeemanagements.WebApi.MeetingNotificationAPI;
import app.zingo.employeemanagements.WebApi.MeetingsAPI;
import app.zingo.employeemanagements.WebApi.UploadApi;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.support.v4.view.GravityCompat.START;

public class DashBoardEmployee extends AppCompatActivity {

    DrawerLayout drawer;
    ListView navbar;
    TextView mUserName,mUserEmail,mMeetingCount;
    CircleImageView mProfileImage;
    TextView mLoggedTime,mMasterText,mMeetingText;
    CardView mMasterLogin,mMeetingLogin;


    Employee profile;
    EmployeeImages employeeImages;
    int userId=0,imageId=0;
    String userName="",userEmail="";



    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    String status,selectedImage;

    String option;

    //Location
    TrackGPS gps;
    double latitude,longitude;

    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1,MY_PERMISSIONS_REQUEST_RESULT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{

            setContentView(R.layout.activity_dash_board_employee);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN |
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            gps = new TrackGPS(DashBoardEmployee.this);


            drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            navbar = (ListView) findViewById(R.id.navbar_list);
            mUserName = (TextView) findViewById(R.id.main_user_name);
            mUserEmail = (TextView) findViewById(R.id.user_mail);
            mProfileImage = (CircleImageView) findViewById(R.id.user_image);
            mLoggedTime = (TextView) findViewById(R.id.log_out_time);
            mMeetingText = (TextView) findViewById(R.id.meeting_login_text);
            mMasterText = (TextView) findViewById(R.id.master_login_text);
            mMeetingCount = (TextView) findViewById(R.id.meeting_count);
            mMasterLogin = (CardView) findViewById(R.id.master_login);
            mMeetingLogin = (CardView) findViewById(R.id.meeting_login);

            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();

            checkPermission();
            setUpNavigationDrawer();

            Bundle bundle = getIntent().getExtras();
            if(bundle!=null){

                profile = (Employee) bundle.getSerializable("Profile");

            }

            userId = PreferenceHandler.getInstance(DashBoardEmployee.this).getUserId();
            userName = PreferenceHandler.getInstance(DashBoardEmployee.this).getUserFullName();
            userEmail = PreferenceHandler.getInstance(DashBoardEmployee.this).getUserEmail();

            if(userName!=null&&!userName.isEmpty()){


                mUserName.setText(""+userName);
            }

            if(userEmail!=null&&!userEmail.isEmpty()){

                mUserEmail.setVisibility(View.VISIBLE);
                mUserEmail.setText(""+userEmail);

            }

            int mapId = PreferenceHandler.getInstance(DashBoardEmployee.this).getMappingId();

            EmployeeDeviceMapping hm = new EmployeeDeviceMapping();
            String token = SharedPrefManager.getInstance(DashBoardEmployee.this).getDeviceToken();

            if(userId!=0&&token!=null&&mapId==0){
                hm.setEmployeeId(userId);
                hm.setDeviceId(token);
                addDeviceId(hm);
            }

            mProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    selectImage();

                }
            });//sa00aac30955/001


            if(profile==null){
                if(userId!=0){
                    System.out.println("Going it");
                    getProfile(userId);
                }

            }else{

                ArrayList<EmployeeImages> images = profile.getEmployeeImages();

                if(images!=null&&images.size()!=0){
                    employeeImages = images.get(0);

                    if(employeeImages!=null){

                        imageId = employeeImages.getEmployeeImageId();
                        String base=employeeImages.getImage();
                        if(base != null && !base.isEmpty()){
                            Picasso.with(DashBoardEmployee.this).load(base).placeholder(R.drawable.profile_image).
                                    error(R.drawable.profile_image).into(mProfileImage);


                        }
                    }

                }

            }

            String masterloginStatus = PreferenceHandler.getInstance(DashBoardEmployee.this).getLoginStatus();
            String loginStatus = PreferenceHandler.getInstance(DashBoardEmployee.this).getMeetingLoginStatus();

            /*if(masterloginStatus==null||masterloginStatus.isEmpty()){
                getLoginDetails();
            }else{

                if(masterloginStatus!=null&&!masterloginStatus.isEmpty()){

                    if(masterloginStatus.equalsIgnoreCase("Login")){

                        mMasterText.setText("Logout");
                    }else if(masterloginStatus.equalsIgnoreCase("Logout")){

                        mMasterText.setText("Login");
                    }

                }else{
                    mMasterText.setText("Login");
                }
            }*/

            getLoginDetails();
            getMeetingDetails();

            /*if(loginStatus!=null&&!loginStatus.isEmpty()){

                if(loginStatus.equalsIgnoreCase("Login")){

                    mMeetingText.setText("Check-out");
                }else if(loginStatus.equalsIgnoreCase("Logout")){

                    mMeetingText.setText("Check-in");
                }

            }else{
                getMeetingDetails();
            }
*/

            mMasterLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String loginStatus = PreferenceHandler.getInstance(DashBoardEmployee.this).getLoginStatus();
                    String meetingStatus = PreferenceHandler.getInstance(DashBoardEmployee.this).getMeetingLoginStatus();

                    if(loginStatus!=null&&!loginStatus.isEmpty()){

                        if(loginStatus.equalsIgnoreCase("Login")){

                            if(meetingStatus!=null&&meetingStatus.equalsIgnoreCase("Login")){

                                Toast.makeText(DashBoardEmployee.this, "You are in some meeting .So Please checkout", Toast.LENGTH_SHORT).show();

                            }else{

                                getLoginDetails(PreferenceHandler.getInstance(DashBoardEmployee.this).getLoginId());
                            }


                        }else if(loginStatus.equalsIgnoreCase("Logout")){

                            masterloginalert("Logout");
                        }

                    }else{
                        masterloginalert("Logout");
                    }




                }
            });

            mMeetingLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String loginStatus = PreferenceHandler.getInstance(DashBoardEmployee.this).getMeetingLoginStatus();
                    String masterloginStatus = PreferenceHandler.getInstance(DashBoardEmployee.this).getLoginStatus();


                    if(masterloginStatus.equals("Login")){
                        if(loginStatus!=null&&!loginStatus.isEmpty()){

                            if(loginStatus.equalsIgnoreCase("Login")){
                                //meetingloginalert("Login");
                                getMeetings(PreferenceHandler.getInstance(DashBoardEmployee.this).getMeetingId());

                            }else if(loginStatus.equalsIgnoreCase("Logout")){

                                meetingloginalert("Logout");
                            }

                        }else{
                            meetingloginalert("Logout");
                        }
                    }else{
                        Toast.makeText(DashBoardEmployee.this, "First Check-in Master", Toast.LENGTH_SHORT).show();
                    }

                }
            });



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

    private void setUpNavigationDrawer() {

        TypedArray icons = getResources().obtainTypedArray(R.array.navnar_item_images_employee);
        String[] title  = getResources().getStringArray(R.array.navbar_items_employee);

        final ArrayList<NavBarItems> navBarItemsList = new ArrayList<>();

        for (int i=0;i<title.length;i++)
        {
            NavBarItems navbarItem = new NavBarItems(title[i],icons.getResourceId(i, -1));
            navBarItemsList.add(navbarItem);
        }
        //final ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(title));
        NavigationListAdapter adapter = new NavigationListAdapter(getApplicationContext(),navBarItemsList);
        navbar.setAdapter(adapter);
        navbar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                displayView(navBarItemsList.get(position).getTitle());
            }
        });




    }

    public void getProfile(final int id){

        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {

                final EmployeeApi subCategoryAPI = Util.getClient().create(EmployeeApi.class);
                Call<Employee> getProf = subCategoryAPI.getProfileById(id);
                //Call<ArrayList<Blogs>> getBlog = blogApi.getBlogs();

                getProf.enqueue(new Callback<Employee>() {

                    @Override
                    public void onResponse(Call<Employee> call, Response<Employee> response) {

                        if (response.code() == 200)
                        {
                            System.out.println("Inside api");

                            profile = response.body();

                            ArrayList<EmployeeImages> images = profile.getEmployeeImages();

                            if(images!=null&&images.size()!=0){
                                employeeImages = images.get(0);

                                if(employeeImages!=null){
                                    imageId = employeeImages.getEmployeeImageId();
                                    String base=employeeImages.getImage();
                                    if(base != null && !base.isEmpty()){
                                        Picasso.with(DashBoardEmployee.this).load(base).placeholder(R.drawable.profile_image).
                                                error(R.drawable.profile_image).into(mProfileImage);


                                    }
                                }

                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<Employee> call, Throwable t) {

                    }
                });

            }

        });
    }

    private void selectImage() {
        final CharSequence[] items = {"Choose from Library",
                "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(DashBoardEmployee.this);
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
            String picturePath = getPath( DashBoardEmployee.this, selectedImageUri );
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

    private void uploadImage(final String filePath,final Employee employee)
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
                final ProgressDialog dialog = new ProgressDialog(DashBoardEmployee.this);
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


                        if(employeeImages==null){
                            EmployeeImages employeeImages = new EmployeeImages();
                            employeeImages.setImage(Constants.IMAGE_URL+response.body().toString());
                            employeeImages.setEmployeeImageId(employee.getEmployeeId());


                            addProfileImage(employeeImages);
                        }else{

                            EmployeeImages employeeImagess = employeeImages;
                            employeeImagess.setImage(Constants.IMAGE_URL+response.body().toString());
                            employeeImagess.setEmployeeImageId(employee.getEmployeeId());


                            updateProfileImage(employeeImages);
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

    public String compressImage(String filePath,final  Employee Employee) {

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

    private void updateProfileImage(final EmployeeImages employeeImages) {


        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setTitle("Updating Image..");
        dialog.show();

        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {
                EmployeeImageAPI auditApi = Util.getClient().create(EmployeeImageAPI.class);
                Call<EmployeeImages> response = auditApi.updateEmployeeImage(employeeImages.getEmployeeImageId(),employeeImages);
                response.enqueue(new Callback<EmployeeImages>() {
                    @Override
                    public void onResponse(Call<EmployeeImages> call, Response<EmployeeImages> response) {
                        if(dialog != null)
                        {
                            dialog.dismiss();
                        }
                        System.out.println(response.code());

                        if(response.code() == 201||response.code() == 200||response.code() == 204)
                        {
                            Toast.makeText(DashBoardEmployee.this,"Profile Image Updated",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(DashBoardEmployee.this,response.message(),Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<EmployeeImages> call, Throwable t) {
                        if(dialog != null)
                        {
                            dialog.dismiss();
                        }
                        Toast.makeText(DashBoardEmployee.this,t.getMessage(),Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });
    }

    private void addProfileImage(final EmployeeImages employeeImages) {


        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setTitle("Updating Image..");
        dialog.show();

        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {
                EmployeeImageAPI auditApi = Util.getClient().create(EmployeeImageAPI.class);
                Call<EmployeeImages> response = auditApi.addEmployeeImage(employeeImages);
                response.enqueue(new Callback<EmployeeImages>() {
                    @Override
                    public void onResponse(Call<EmployeeImages> call, Response<EmployeeImages> response) {
                        if(dialog != null)
                        {
                            dialog.dismiss();
                        }
                        System.out.println(response.code());

                        if(response.code() == 201||response.code() == 200||response.code() == 204)
                        {
                            Toast.makeText(DashBoardEmployee.this,"Profile Image Updated",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(DashBoardEmployee.this,response.message(),Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<EmployeeImages> call, Throwable t) {
                        if(dialog != null)
                        {
                            dialog.dismiss();
                        }
                        Toast.makeText(DashBoardEmployee.this,t.getMessage(),Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });
    }


    public void displayView(String i)
    {
        if(drawer != null)
            drawer.closeDrawer(START);


        switch (i)
        {
            case "Profile":
                break;

            case "Organization":

                Intent organization = new Intent(DashBoardEmployee.this, OrganizationDetailScree.class);
                startActivity(organization);
                break;

            case "Employees":
                Intent employee = new Intent(DashBoardEmployee.this, EmployeeListScreen.class);
                startActivity(employee);
                break;

            case "Meetings":
                Intent meetings = new Intent(DashBoardEmployee.this, EmployeeMeetingHost.class);
                meetings.putExtra("EmployeeId",PreferenceHandler.getInstance(DashBoardEmployee.this).getUserId());
                startActivity(meetings);
                break;

            case "Attendance":
                Intent attendance = new Intent(DashBoardEmployee.this, AttendanceScreen.class);
                attendance.putExtra("EmployeeId",PreferenceHandler.getInstance(DashBoardEmployee.this).getUserId());
                startActivity(attendance);
                break;

            case "Change Password":
                Intent chnage = new Intent(DashBoardEmployee.this, ChangePasswordScreen.class);
                startActivity(chnage);
                break;

            case "Leave Management":
            Intent leave = new Intent(DashBoardEmployee.this, LeaveManagementHost.class);
            leave.putExtra("EmployeeId",PreferenceHandler.getInstance(DashBoardEmployee.this).getUserId());
            startActivity(leave);
            break;

            case "Logout":

                PreferenceHandler.getInstance(DashBoardEmployee.this).clear();

                Intent log = new Intent(DashBoardEmployee.this, LoginScreen.class);
                log.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                log.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                Toast.makeText(DashBoardEmployee.this,"Logout",Toast.LENGTH_SHORT).show();
                startActivity(log);
                finish();
                break;

        }
    }

    private void getLoginDetails(){


        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading Details..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {
                LoginDetailsAPI apiService = Util.getClient().create(LoginDetailsAPI.class);
                Call<ArrayList<LoginDetails>> call = apiService.getLoginByEmployeeId(PreferenceHandler.getInstance(DashBoardEmployee.this).getUserId());

                call.enqueue(new Callback<ArrayList<LoginDetails>>() {
                    @Override
                    public void onResponse(Call<ArrayList<LoginDetails>> call, Response<ArrayList<LoginDetails>> response) {
                        int statusCode = response.code();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {


                            if (progressDialog!=null)
                                progressDialog.dismiss();
                            ArrayList<LoginDetails> list = response.body();


                            if (list !=null && list.size()!=0) {

                                LoginDetails loginDetails = list.get(list.size()-1);

                                if(loginDetails!=null){

                                    String logout = loginDetails.getLogOutTime();
                                    String login = loginDetails.getLoginTime();
                                    String dates = loginDetails.getLoginDate();
                                    String date= null;



                                    if(dates!=null&&!dates.isEmpty()){

                                        if(dates.contains("T")){

                                            String logins[] = dates.split("T");
                                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                            SimpleDateFormat sdfs = new SimpleDateFormat("MMM dd,yyyy");

                                            Date dt = null;
                                            try {
                                                dt = sdf.parse(logins[0]);
                                                date = sdfs.format(dt);
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    }

                                    if(logout!=null&&!logout.isEmpty()&&(login!=null&&!login.isEmpty())){

                                        if(date!=null&&!date.isEmpty()){

                                            mLoggedTime.setText("Last Logout : "+logout);

                                        }else{

                                            mLoggedTime.setText("Last Logout : "+logout);
                                        }

                                        mMasterText.setText("Login");
                                        PreferenceHandler.getInstance(DashBoardEmployee.this).setLoginStatus("Logout");

                                    }else if(login!=null&&!login.isEmpty()&&(logout==null||logout.isEmpty())){

                                        if(date!=null&&!date.isEmpty()){

                                            mLoggedTime.setText("Last Logged in : "+login);

                                        }else{

                                            mLoggedTime.setText("Last Logged in : "+login);
                                        }
                                        mMasterText.setText("Logout");
                                        PreferenceHandler.getInstance(DashBoardEmployee.this).setLoginStatus("Login");
                                        PreferenceHandler.getInstance(DashBoardEmployee.this).setLoginId(loginDetails.getLoginDetailsId());
                                    }

                                }




                                //}

                            }else{

                            }

                        }else {

                            if (progressDialog!=null)
                                progressDialog.dismiss();

                            Toast.makeText(DashBoardEmployee.this, "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<LoginDetails>> call, Throwable t) {
                        // Log error here since request failed
                        if (progressDialog!=null)
                            progressDialog.dismiss();
                        Log.e("TAG", t.toString());
                    }
                });
            }


        });
    }

    private void getMeetingDetails(){


        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading Details..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {
                MeetingsAPI apiService = Util.getClient().create(MeetingsAPI.class);
                Call<ArrayList<Meetings>> call = apiService.getMeetingsByEmployeeId(PreferenceHandler.getInstance(DashBoardEmployee.this).getUserId());

                call.enqueue(new Callback<ArrayList<Meetings>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Meetings>> call, Response<ArrayList<Meetings>> response) {
                        int statusCode = response.code();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {


                            if (progressDialog!=null)
                                progressDialog.dismiss();
                            ArrayList<Meetings> list = response.body();


                            if (list !=null && list.size()!=0) {


                                mMeetingCount.setText(""+list.size());

                                Meetings loginDetails = list.get(list.size()-1);

                                if(loginDetails!=null){
                                    PreferenceHandler.getInstance(DashBoardEmployee.this).setMeetingId(loginDetails.getMeetingsId());

                                    String logout = loginDetails.getEndTime();
                                    String login = loginDetails.getStartTime();


                                    if(logout!=null&&!logout.isEmpty()&&(login!=null&&!login.isEmpty())){



                                        mMeetingText.setText("Check-in");
                                        PreferenceHandler.getInstance(DashBoardEmployee.this).setMeetingLoginStatus("Logout");

                                    }else if(login!=null&&!login.isEmpty()&&(logout==null||logout.isEmpty())){


                                        mMeetingText.setText("Check-out");
                                        PreferenceHandler.getInstance(DashBoardEmployee.this).setMeetingLoginStatus("Login");
                                    }

                                }




                                //}

                            }else{

                            }

                        }else {


                            Toast.makeText(DashBoardEmployee.this, "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Meetings>> call, Throwable t) {
                        // Log error here since request failed
                        if (progressDialog!=null)
                            progressDialog.dismiss();
                        Log.e("TAG", t.toString());
                    }
                });
            }


        });
    }

    public void masterloginalert(final String status){

        try {

            String message = "Login";
            option = "Check-In";

            if(status.equalsIgnoreCase("Login")){

                message = "Do you want to Log-Out?";
                option = "Log-Out";

            }else if(status.equalsIgnoreCase("Logout")){

                message = "Do you want to Log-In?";
                option = "Log-In";
            }

            final AlertDialog.Builder builder = new AlertDialog.Builder(DashBoardEmployee.this);
            builder.setTitle(message);



            builder.setPositiveButton(option, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {



                    if(locationCheck()){
                        if(gps.canGetLocation())
                        {
                            System.out.println("Long and lat Rev"+gps.getLatitude()+" = "+gps.getLongitude());
                            latitude = gps.getLatitude();
                            longitude = gps.getLongitude();

                            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                            SimpleDateFormat sdt = new SimpleDateFormat("MMM dd,yyyy hh:mm a");

                            LatLng master = new LatLng(latitude,longitude);
                            String address = getAddress(master);

                            LoginDetails loginDetails = new LoginDetails();
                            loginDetails.setEmployeeId(PreferenceHandler.getInstance(DashBoardEmployee.this).getUserId());
                            loginDetails.setLatitude(""+latitude);
                            loginDetails.setLongitude(""+longitude);
                            loginDetails.setLocation(""+address);
                            loginDetails.setLoginTime(""+sdt.format(new Date()));
                            loginDetails.setLoginDate(""+sdf.format(new Date()));
                            loginDetails.setLogOutTime("");
                            try {

                                LoginDetailsNotificationManagers md = new LoginDetailsNotificationManagers();
                                md.setTitle("Login Details from "+PreferenceHandler.getInstance(DashBoardEmployee.this).getUserFullName());
                                md.setMessage("Log in at  "+""+sdt.format(new Date()));
                                md.setLocation(address);
                                md.setLongitude(""+longitude);
                                md.setLatitude(""+latitude);
                                md.setLoginDate(""+sdt.format(new Date()));
                                md.setStatus("In meeting");
                                md.setEmployeeId(PreferenceHandler.getInstance(DashBoardEmployee.this).getUserId());
                                md.setManagerId(PreferenceHandler.getInstance(DashBoardEmployee.this).getManagerId());

                                addLogin(loginDetails,builder.create(),md);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        }
                        else
                        {

                        }
                    }





                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });

            final AlertDialog dialog = builder.create();
            dialog.show();


        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void meetingloginalert(final String status){

        try{

            if(locationCheck()){
                String message = "Login";
                option = "Check-In";

                if(status.equalsIgnoreCase("Login")){

                    message = "Do you want to Check-Out?";
                    option = "Check-Out";

                }else if(status.equalsIgnoreCase("Logout")){

                    message = "Do you want to Check-In?";
                    option = "Check-In";
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(DashBoardEmployee.this);
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View views = inflater.inflate(R.layout.custom_alert_box_meeting, null);

                builder.setView(views);
                final Button mSave = (Button) views.findViewById(R.id.save);
                mSave.setText(option);
                final EditText mRemarks = (EditText) views.findViewById(R.id.meeting_remarks);
                final TextInputEditText mClient = (TextInputEditText) views.findViewById(R.id.client_name);
                final TextInputEditText mContact = (TextInputEditText) views.findViewById(R.id.client_contact);
                final TextInputEditText mPurpose = (TextInputEditText) views.findViewById(R.id.purpose_meeting);


                final AlertDialog dialog = builder.create();
                dialog.show();
                dialog.setCanceledOnTouchOutside(false);

                mSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String client = mClient.getText().toString();
                        String purpose = mPurpose.getText().toString();
                        String remark = mRemarks.getText().toString();
                        String contact = mContact.getText().toString();

                        if(client==null||client.isEmpty()){

                            Toast.makeText(DashBoardEmployee.this, "Please mention client/hotel name", Toast.LENGTH_SHORT).show();

                        }else if(purpose==null||purpose.isEmpty()){

                            Toast.makeText(DashBoardEmployee.this, "Please mention purpose of meeting", Toast.LENGTH_SHORT).show();

                        }else if(remark==null||remark.isEmpty()){

                            Toast.makeText(DashBoardEmployee.this, "Please mention remarks about meeting", Toast.LENGTH_SHORT).show();

                        }else{

                            if(gps.canGetLocation())
                            {
                                System.out.println("Long and lat Rev"+gps.getLatitude()+" = "+gps.getLongitude());
                                latitude = gps.getLatitude();
                                longitude = gps.getLongitude();


                                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                                SimpleDateFormat sdt = new SimpleDateFormat("MMM dd,yyyy hh:mm a");

                                LatLng master = new LatLng(latitude,longitude);
                                String address = getAddress(master);

                                Meetings loginDetails = new Meetings();
                                loginDetails.setEmployeeId(PreferenceHandler.getInstance(DashBoardEmployee.this).getUserId());
                                loginDetails.setStartLatitude(""+latitude);
                                loginDetails.setStartLongitude(""+longitude);
                                loginDetails.setStartLocation(""+address);
                                loginDetails.setStartTime(""+sdt.format(new Date()));
                                loginDetails.setMeetingDate(""+sdf.format(new Date()));
                                loginDetails.setMeetingAgenda(purpose);
                                loginDetails.setMeetingDetails(remark);
                                loginDetails.setStatus("In Meeting");

                                if(contact!=null&&!contact.isEmpty()){
                                    loginDetails.setMeetingPersonDetails(client+"%"+contact);
                                }else{
                                    loginDetails.setMeetingPersonDetails(client);
                                }
                                try {

                                    MeetingDetailsNotificationManagers md = new MeetingDetailsNotificationManagers();
                                    md.setTitle("Meeting Details from "+PreferenceHandler.getInstance(DashBoardEmployee.this).getUserFullName());
                                    md.setMessage("Meeting with "+client+" for "+purpose);
                                    md.setLocation(address);
                                    md.setLongitude(""+longitude);
                                    md.setLatitude(""+latitude);
                                    md.setMeetingDate(""+sdt.format(new Date()));
                                    md.setStatus("In meeting");
                                    md.setEmployeeId(PreferenceHandler.getInstance(DashBoardEmployee.this).getUserId());
                                    md.setManagerId(PreferenceHandler.getInstance(DashBoardEmployee.this).getManagerId());
                                    md.setMeetingPerson(client);
                                    md.setMeetingsDetails(purpose);
                                    md.setMeetingComments(remark);


                                    addMeeting(loginDetails,dialog,md);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                            else
                            {

                            }

                        }
                    }
                });

            }else{

            }




        }catch (Exception e){
            e.printStackTrace();
        }


    }

    public boolean locationCheck(){

        final boolean status = false;
        LocationManager lm = (LocationManager)DashBoardEmployee.this.getSystemService(Context.LOCATION_SERVICE);
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
            AlertDialog.Builder dialog = new AlertDialog.Builder(DashBoardEmployee.this);
            dialog.setMessage("Location is not enable");
            dialog.setPositiveButton("Open Settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    DashBoardEmployee.this.startActivity(myIntent);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(grantResults.length > 0)
        {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                gps = new TrackGPS(DashBoardEmployee.this);
            }
        }
    }

    private String getAddress(LatLng latLng) {
        Geocoder geocoder = new Geocoder(DashBoardEmployee.this, Locale.getDefault());
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

    public void addLogin(final LoginDetails loginDetails,final AlertDialog dialogs,final LoginDetailsNotificationManagers md) throws Exception{



        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Saving Details..");
        dialog.setCancelable(false);
        dialog.show();

        LoginDetailsAPI apiService = Util.getClient().create(LoginDetailsAPI.class);

        Call<LoginDetails> call = apiService.addLogin(loginDetails);

        call.enqueue(new Callback<LoginDetails>() {
            @Override
            public void onResponse(Call<LoginDetails> call, Response<LoginDetails> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {
                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201) {

                        LoginDetails s = response.body();

                        if(s!=null){

                            dialogs.dismiss();
                            md.setLoginDetailsId(s.getLoginDetailsId());
                            saveLoginNotification(md);

                            Toast.makeText(DashBoardEmployee.this, "You Logged in", Toast.LENGTH_SHORT).show();

                            PreferenceHandler.getInstance(DashBoardEmployee.this).setLoginId(s.getLoginDetailsId());

                            String date = s.getLoginDate();

                            if(date!=null&&!date.isEmpty()){

                                if(date.contains("T")){

                                    String logins[] = date.split("T");
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                    SimpleDateFormat sdfs = new SimpleDateFormat("MMM dd,yyyy");

                                    Date dt = sdf.parse(logins[0]);
                                    mLoggedTime.setText("Last Logged in : "+s.getLoginTime());
                                }
                            }else{
                                mLoggedTime.setText("Last Logged in : "+s.getLoginTime());
                            }

                            mMasterText.setText("Logout");
                            PreferenceHandler.getInstance(DashBoardEmployee.this).setLoginStatus("Login");


                        }




                    }else {
                        Toast.makeText(DashBoardEmployee.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
            public void onFailure(Call<LoginDetails> call, Throwable t) {

                if(dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
                Toast.makeText(DashBoardEmployee.this, "Failed Due to "+t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());
            }
        });



    }

    public void addMeeting(final Meetings loginDetails,final AlertDialog dialogs,final MeetingDetailsNotificationManagers md) throws Exception{



        final ProgressDialog dialog = new ProgressDialog(this);
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

                            dialogs.dismiss();

                            md.setMeetingsId(s.getMeetingsId());
                            Toast.makeText(DashBoardEmployee.this, "You Checked in", Toast.LENGTH_SHORT).show();

                            PreferenceHandler.getInstance(DashBoardEmployee.this).setMeetingId(s.getMeetingsId());

                            saveMeetingNotification(md);


                            mMeetingText.setText("Checkout");
                            PreferenceHandler.getInstance(DashBoardEmployee.this).setMeetingLoginStatus("Login");


                        }




                    }else {
                        Toast.makeText(DashBoardEmployee.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(DashBoardEmployee.this, "Failed Due to "+t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());
            }
        });



    }

    public void updateMeeting(final Meetings loginDetails,final AlertDialog dialogs,final MeetingDetailsNotificationManagers md) throws Exception{



        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Saving Details..");
        dialog.setCancelable(false);
        dialog.show();

        MeetingsAPI apiService = Util.getClient().create(MeetingsAPI.class);

        Call<Meetings> call = apiService.updateMeetingById(loginDetails.getMeetingsId(),loginDetails);

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
                    if (statusCode == 200 || statusCode == 201||response.code()==204) {


                            dialogs.dismiss();
                            saveMeetingNotification(md);

                            Toast.makeText(DashBoardEmployee.this, "You Checked out", Toast.LENGTH_SHORT).show();

                            PreferenceHandler.getInstance(DashBoardEmployee.this).setMeetingId(0);
                            getMeetingDetails();


                            mMeetingText.setText("Check-In");
                            PreferenceHandler.getInstance(DashBoardEmployee.this).setMeetingLoginStatus("Logout");



                    }else {
                        Toast.makeText(DashBoardEmployee.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(DashBoardEmployee.this, "Failed Due to "+t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());
            }
        });



    }

    public void updateLogin(final LoginDetails loginDetails,final AlertDialog dialogs,final LoginDetailsNotificationManagers md) throws Exception{



        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Saving Details..");
        dialog.setCancelable(false);
        dialog.show();

        LoginDetailsAPI apiService = Util.getClient().create(LoginDetailsAPI.class);

        Call<LoginDetails> call = apiService.updateLoginById(loginDetails.getLoginDetailsId(),loginDetails);

        call.enqueue(new Callback<LoginDetails>() {
            @Override
            public void onResponse(Call<LoginDetails> call, Response<LoginDetails> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {
                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201||response.code()==204) {


                        dialogs.dismiss();
                        saveLoginNotification(md);

                        Toast.makeText(DashBoardEmployee.this, "You logged out", Toast.LENGTH_SHORT).show();

                        PreferenceHandler.getInstance(DashBoardEmployee.this).setLoginId(0);
                        getLoginDetails();


                        mMasterText.setText("Log in");
                        PreferenceHandler.getInstance(DashBoardEmployee.this).setLoginStatus("Logout");



                    }else {
                        Toast.makeText(DashBoardEmployee.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
            public void onFailure(Call<LoginDetails> call, Throwable t) {

                if(dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
                Toast.makeText(DashBoardEmployee.this, "Failed Due to "+t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());
            }
        });



    }

    public void getMeetings(final int id){

        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {

                final MeetingsAPI subCategoryAPI = Util.getClient().create(MeetingsAPI.class);
                Call<Meetings> getProf = subCategoryAPI.getMeetingById(id);
                //Call<ArrayList<Blogs>> getBlog = blogApi.getBlogs();

                getProf.enqueue(new Callback<Meetings>() {

                    @Override
                    public void onResponse(Call<Meetings> call, Response<Meetings> response) {

                        if (response.code() == 200||response.code() == 201||response.code() == 204)
                        {
                            System.out.println("Inside api");

                            final Meetings dto = response.body();

                            if(dto!=null){

                                try{

                                    if(locationCheck()){
                                        String message = "Login";




                                        message = "Do you want to Check-Out?";
                                        option = "Check-Out";



                                        AlertDialog.Builder builder = new AlertDialog.Builder(DashBoardEmployee.this);
                                        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                        View views = inflater.inflate(R.layout.custom_alert_box_meeting, null);

                                        builder.setView(views);
                                        final Button mSave = (Button) views.findViewById(R.id.save);
                                        mSave.setText(option);
                                        final EditText mRemarks = (EditText) views.findViewById(R.id.meeting_remarks);
                                        final TextInputEditText mClient = (TextInputEditText) views.findViewById(R.id.client_name);
                                        final TextInputEditText mContact = (TextInputEditText) views.findViewById(R.id.client_contact);
                                        final TextInputEditText mPurpose = (TextInputEditText) views.findViewById(R.id.purpose_meeting);

                                        mRemarks.setText(""+dto.getMeetingDetails());
                                        if(dto.getMeetingPersonDetails().contains("%")){

                                            String person[] = dto.getMeetingPersonDetails().split("%");
                                            mContact.setText(""+person[1]);
                                            mClient.setText(""+person[0]);
                                        }else{
                                            mClient.setText(""+dto.getMeetingPersonDetails());
                                        }

                                        mPurpose.setText(""+dto.getMeetingAgenda());

                                        final AlertDialog dialogs = builder.create();
                                        dialogs.show();
                                        dialogs.setCanceledOnTouchOutside(true);

                                        mSave.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {

                                                String client = mClient.getText().toString();
                                                String purpose = mPurpose.getText().toString();
                                                String remark = mRemarks.getText().toString();
                                                String contact = mContact.getText().toString();

                                                if(client==null||client.isEmpty()){

                                                    Toast.makeText(DashBoardEmployee.this, "Please mention client/hotel name", Toast.LENGTH_SHORT).show();

                                                }else if(purpose==null||purpose.isEmpty()){

                                                    Toast.makeText(DashBoardEmployee.this, "Please mention purpose of meeting", Toast.LENGTH_SHORT).show();

                                                }else if(remark==null||remark.isEmpty()){

                                                    Toast.makeText(DashBoardEmployee.this, "Please mention remarks about meeting", Toast.LENGTH_SHORT).show();

                                                }else{

                                                    if(gps.canGetLocation())
                                                    {
                                                        System.out.println("Long and lat Rev"+gps.getLatitude()+" = "+gps.getLongitude());
                                                        latitude = gps.getLatitude();
                                                        longitude = gps.getLongitude();


                                                        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                                                        SimpleDateFormat sdt = new SimpleDateFormat("MMM dd,yyyy hh:mm a");

                                                        LatLng master = new LatLng(latitude,longitude);
                                                        String address = getAddress(master);

                                                        Meetings loginDetails = dto;
                                                        loginDetails.setEmployeeId(PreferenceHandler.getInstance(DashBoardEmployee.this).getUserId());
                                                        loginDetails.setEndLatitude(""+latitude);
                                                        loginDetails.setEndLongitude(""+longitude);
                                                        loginDetails.setEndLocation(""+address);
                                                        loginDetails.setEndTime(""+sdt.format(new Date()));
                                                        loginDetails.setMeetingDate(""+sdf.format(new Date()));
                                                        loginDetails.setMeetingAgenda(purpose);
                                                        loginDetails.setMeetingDetails(remark);
                                                        loginDetails.setStatus("Completed");

                                                        if(contact!=null&&!contact.isEmpty()){
                                                            loginDetails.setMeetingPersonDetails(client+"%"+contact);
                                                        }else{
                                                            loginDetails.setMeetingPersonDetails(client);
                                                        }
                                                        try {

                                                            MeetingDetailsNotificationManagers md = new MeetingDetailsNotificationManagers();
                                                            md.setTitle("Meeting Details from "+PreferenceHandler.getInstance(DashBoardEmployee.this).getUserFullName());
                                                            md.setMessage("Meeting with "+client+" for "+purpose);
                                                            md.setLocation(address);
                                                            md.setLongitude(""+longitude);
                                                            md.setLatitude(""+latitude);
                                                            md.setMeetingDate(""+sdt.format(new Date()));
                                                            md.setStatus("In meeting");
                                                            md.setEmployeeId(PreferenceHandler.getInstance(DashBoardEmployee.this).getUserId());
                                                            md.setManagerId(PreferenceHandler.getInstance(DashBoardEmployee.this).getManagerId());
                                                            md.setMeetingPerson(client);
                                                            md.setMeetingsId(loginDetails.getMeetingsId());
                                                            md.setMeetingsDetails(purpose);
                                                            md.setMeetingComments(remark);
                                                            updateMeeting(loginDetails,dialogs,md);
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }

                                                    }
                                                    else
                                                    {

                                                    }

                                                }
                                            }
                                        });

                                    }else{

                                    }




                                }catch (Exception e){
                                    e.printStackTrace();
                                }

                            }




                        }else{


                            //meet
                        }
                    }

                    @Override
                    public void onFailure(Call<Meetings> call, Throwable t) {

                    }
                });

            }

        });
    }

    public void getLoginDetails(final int id){

        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {

                final LoginDetailsAPI subCategoryAPI = Util.getClient().create(LoginDetailsAPI.class);
                Call<LoginDetails> getProf = subCategoryAPI.getLoginById(id);
                //Call<ArrayList<Blogs>> getBlog = blogApi.getBlogs();

                getProf.enqueue(new Callback<LoginDetails>() {

                    @Override
                    public void onResponse(Call<LoginDetails> call, Response<LoginDetails> response) {

                        if (response.code() == 200||response.code() == 201||response.code() == 204)
                        {
                            System.out.println("Inside api");

                            final LoginDetails dto = response.body();

                            if(dto!=null){

                                try {

                                    String message = "Login";
                                    option = "Check-In";



                                        message = "Do you want to Log-Out?";
                                        option = "Log-Out";



                                    final AlertDialog.Builder builder = new AlertDialog.Builder(DashBoardEmployee.this);
                                    builder.setTitle(message);



                                    builder.setPositiveButton(option, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {



                                            if(locationCheck()){
                                                if(gps.canGetLocation())
                                                {
                                                    System.out.println("Long and lat Rev"+gps.getLatitude()+" = "+gps.getLongitude());
                                                    latitude = gps.getLatitude();
                                                    longitude = gps.getLongitude();

                                                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                                                    SimpleDateFormat sdt = new SimpleDateFormat("MMM dd,yyyy hh:mm a");

                                                    LatLng master = new LatLng(latitude,longitude);
                                                    String address = getAddress(master);

                                                    LoginDetails loginDetails = dto;
                                                    loginDetails.setEmployeeId(PreferenceHandler.getInstance(DashBoardEmployee.this).getUserId());
                                                    loginDetails.setLatitude(""+latitude);
                                                    loginDetails.setLongitude(""+longitude);
                                                    loginDetails.setLocation(""+address);
                                                    loginDetails.setLogOutTime(""+sdt.format(new Date()));
                                                    loginDetails.setLoginDate(""+sdf.format(new Date()));

                                                    try {
                                                        LoginDetailsNotificationManagers md = new LoginDetailsNotificationManagers();
                                                        md.setTitle("Login Details from "+PreferenceHandler.getInstance(DashBoardEmployee.this).getUserFullName());
                                                        md.setMessage("Log in at  "+""+sdt.format(new Date()));
                                                        md.setLocation(address);
                                                        md.setLongitude(""+longitude);
                                                        md.setLatitude(""+latitude);
                                                        md.setLoginDate(""+sdt.format(new Date()));
                                                        md.setStatus("In meeting");
                                                        md.setEmployeeId(PreferenceHandler.getInstance(DashBoardEmployee.this).getUserId());
                                                        md.setManagerId(PreferenceHandler.getInstance(DashBoardEmployee.this).getManagerId());
                                                        md.setLoginDetailsId(dto.getLoginDetailsId());

                                                        updateLogin(loginDetails,builder.create(),md);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }


                                                }
                                                else
                                                {

                                                }
                                            }





                                        }
                                    });

                                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    });

                                    final AlertDialog dialog = builder.create();
                                    dialog.show();


                                }
                                catch (Exception ex)
                                {
                                    ex.printStackTrace();
                                }

                            }




                        }else{


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

    public void saveMeetingNotification(final MeetingDetailsNotificationManagers md) throws Exception{



        final ProgressDialog dialog = new ProgressDialog(this);
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
                        Toast.makeText(DashBoardEmployee.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(DashBoardEmployee.this, "Failed Due to "+t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());
            }
        });



    }

    public void sendMeetingNotification(final MeetingDetailsNotificationManagers md) throws Exception{



        final ProgressDialog dialog = new ProgressDialog(this);
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

                        /*MeetingDetailsNotificationManagers s = response.body();

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
                            md.setMeetingsId(s.getMeetingsId());*/



                        //}




                    }else {
                        Toast.makeText(DashBoardEmployee.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(DashBoardEmployee.this, "Failed Due to "+t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());
            }
        });



    }

    public void saveLoginNotification(final LoginDetailsNotificationManagers md) throws Exception{



        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Saving Details..");
        dialog.setCancelable(false);
        dialog.show();

        LoginNotificationAPI apiService = Util.getClient().create(LoginNotificationAPI.class);

        Call<LoginDetailsNotificationManagers> call = apiService.saveLoginNotification(md);

        call.enqueue(new Callback<LoginDetailsNotificationManagers>() {
            @Override
            public void onResponse(Call<LoginDetailsNotificationManagers> call, Response<LoginDetailsNotificationManagers> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {
                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201) {

                        LoginDetailsNotificationManagers s = response.body();

                        if(s!=null){





                        }




                    }else {
                        Toast.makeText(DashBoardEmployee.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
            public void onFailure(Call<LoginDetailsNotificationManagers> call, Throwable t) {

                if(dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
                Toast.makeText(DashBoardEmployee.this, "Failed Due to "+t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());
            }
        });



    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();

        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DashBoardEmployee.this.finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    public void addDeviceId(final EmployeeDeviceMapping pf)
    {

        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {


                EmployeeDeviceApi hotelOperation = Util.getClient().create(EmployeeDeviceApi.class);
                Call<EmployeeDeviceMapping> response = hotelOperation.addProfileDevice(pf);

                response.enqueue(new Callback<EmployeeDeviceMapping>() {
                    @Override
                    public void onResponse(Call<EmployeeDeviceMapping> call, Response<EmployeeDeviceMapping> response) {
                        System.out.println("GetHotelByProfileId = "+response.code());


                        if(response.code() == 200||response.code() == 201||response.code() == 202||response.code() == 204)
                        {
                            try{
                                System.out.println("registered");
                                EmployeeDeviceMapping pr = response.body();

                                System.out.println();

                                if(pr != null)
                                {

                                        PreferenceHandler.getInstance(DashBoardEmployee.this).setMappingId(pr.getEmployeeDeviceMappingId());



                                }

                            }catch (Exception e){
                                e.printStackTrace();
                            }




                        }else if(response.code() == 404){
                            System.out.println("already registered");



                        }
                        else
                        {


                        }


                    }

                    @Override
                    public void onFailure(Call<EmployeeDeviceMapping> call, Throwable t) {


                    }
                });
            }
        });
    }

}
