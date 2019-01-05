package app.zingo.employeemanagements.UI.Admin;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
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
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import app.zingo.employeemanagements.Adapter.DepartmentAdapter;
import app.zingo.employeemanagements.Adapter.DepartmentGridAdapter;
import app.zingo.employeemanagements.Adapter.NavigationListAdapter;
import app.zingo.employeemanagements.Custom.MyRegulerText;
import app.zingo.employeemanagements.FireBase.SharedPrefManager;
import app.zingo.employeemanagements.Model.Departments;
import app.zingo.employeemanagements.Model.Employee;
import app.zingo.employeemanagements.Model.EmployeeDeviceMapping;
import app.zingo.employeemanagements.Model.EmployeeImages;
import app.zingo.employeemanagements.Model.NavBarItems;
import app.zingo.employeemanagements.Model.Organization;
import app.zingo.employeemanagements.R;
import app.zingo.employeemanagements.UI.Common.ChangePasswordScreen;
import app.zingo.employeemanagements.UI.Company.OrganizationDetailScree;
import app.zingo.employeemanagements.UI.Employee.DashBoardEmployee;
import app.zingo.employeemanagements.UI.Employee.EmployeeListScreen;
import app.zingo.employeemanagements.UI.GetStartedScreen;
import app.zingo.employeemanagements.UI.LandingScreen;
import app.zingo.employeemanagements.Utils.Constants;
import app.zingo.employeemanagements.Utils.PreferenceHandler;
import app.zingo.employeemanagements.Utils.ThreadExecuter;
import app.zingo.employeemanagements.Utils.Util;
import app.zingo.employeemanagements.WebApi.DepartmentApi;
import app.zingo.employeemanagements.WebApi.EmployeeApi;
import app.zingo.employeemanagements.WebApi.EmployeeDeviceApi;
import app.zingo.employeemanagements.WebApi.EmployeeImageAPI;
import app.zingo.employeemanagements.WebApi.OrganizationApi;
import app.zingo.employeemanagements.WebApi.UploadApi;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.support.v4.view.GravityCompat.START;

public class DashBoardAdmin extends AppCompatActivity {

    DrawerLayout drawer;
    ListView navbar;
    TextView mUserName,mUserEmail;
    CircleImageView mProfileImage;
    GridView mDepartmentGrid;


    Employee profile;
    EmployeeImages employeeImages;
    int userId=0,imageId=0;
    String userName="",userEmail="";
    String appType="",planType="",licensesStartDate="",licenseEndDate="";
    int planId=0;

    public static final int MY_PERMISSIONS_REQUEST_RESULT = 1;

    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    String status,selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{

            setContentView(R.layout.activity_dash_board_admin);

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN |
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            navbar = (ListView) findViewById(R.id.navbar_list);
            mUserName = (TextView) findViewById(R.id.main_user_name);
            mUserEmail = (TextView) findViewById(R.id.user_mail);
            mProfileImage = (CircleImageView) findViewById(R.id.user_image);
            mDepartmentGrid = (GridView) findViewById(R.id.department_grid);

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

            userId = PreferenceHandler.getInstance(DashBoardAdmin.this).getUserId();
            userName = PreferenceHandler.getInstance(DashBoardAdmin.this).getUserFullName();
            userEmail = PreferenceHandler.getInstance(DashBoardAdmin.this).getUserEmail();
            int mapId = PreferenceHandler.getInstance(DashBoardAdmin.this).getMappingId();



            if(userName!=null&&!userName.isEmpty()){


                mUserName.setText(""+userName);
            }

            if(userEmail!=null&&!userEmail.isEmpty()){

                mUserEmail.setVisibility(View.VISIBLE);
                mUserEmail.setText(""+userEmail);

            }

            EmployeeDeviceMapping hm = new EmployeeDeviceMapping();
            String token = SharedPrefManager.getInstance(DashBoardAdmin.this).getDeviceToken();

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
                            Picasso.with(DashBoardAdmin.this).load(base).placeholder(R.drawable.profile_image).
                                    error(R.drawable.profile_image).into(mProfileImage);


                        }
                    }

                }

            }

            getDepartment(PreferenceHandler.getInstance(DashBoardAdmin.this).getCompanyId());
            getCompany(PreferenceHandler.getInstance(DashBoardAdmin.this).getCompanyId());




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

        TypedArray icons = getResources().obtainTypedArray(R.array.navnar_item_images_advance);
        String[] title  = getResources().getStringArray(R.array.navbar_items_advance);

        String planName = PreferenceHandler.getInstance(DashBoardAdmin.this).getPlanType();

        if(planName.contains(",")){

            String plansName[] = planName.split(",");

            if(plansName[0].equalsIgnoreCase("Basic"))
            {

                icons = getResources().obtainTypedArray(R.array.navnar_item_images);
                title  = getResources().getStringArray(R.array.navbar_items);




            }
            else if(plansName[0].equalsIgnoreCase("Advance"))
            {
                icons = getResources().obtainTypedArray(R.array.navnar_item_images_advance);
                title  = getResources().getStringArray(R.array.navbar_items_advance);


            }


        }

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
                Call<ArrayList<Employee>> getProf = subCategoryAPI.getProfileById(id);
                //Call<ArrayList<Blogs>> getBlog = blogApi.getBlogs();

                getProf.enqueue(new Callback<ArrayList<Employee>>() {

                    @Override
                    public void onResponse(Call<ArrayList<Employee>> call, Response<ArrayList<Employee>> response) {

                        if (response.code() == 200)
                        {
                            System.out.println("Inside api");

                            profile = response.body().get(0);

                            ArrayList<EmployeeImages> images = profile.getEmployeeImages();

                            if(images!=null&&images.size()!=0){
                               employeeImages = images.get(0);

                                if(employeeImages!=null){
                                    imageId = employeeImages.getEmployeeImageId();
                                    String base=employeeImages.getImage();
                                    if(base != null && !base.isEmpty()){
                                        Picasso.with(DashBoardAdmin.this).load(base).placeholder(R.drawable.profile_image).
                                                error(R.drawable.profile_image).into(mProfileImage);


                                    }
                                }

                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Employee>> call, Throwable t) {

                    }
                });

            }

        });
    }

    private void selectImage() {
        final CharSequence[] items = {"Choose from Library",
                "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(DashBoardAdmin.this);
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
            String picturePath = getPath( DashBoardAdmin.this, selectedImageUri );
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
                final ProgressDialog dialog = new ProgressDialog(DashBoardAdmin.this);
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
                            employeeImages.setEmployeeId(employee.getEmployeeId());


                            addProfileImage(employeeImages);
                        }else{

                            EmployeeImages employeeImagess = employeeImages;
                            employeeImagess.setImage(Constants.IMAGE_URL+response.body().toString());
                            employeeImagess.setEmployeeImageId(employee.getEmployeeId());
                            employeeImages.setEmployeeId(employee.getEmployeeId());

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
                            Toast.makeText(DashBoardAdmin.this,"Profile Image Updated",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(DashBoardAdmin.this,response.message(),Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<EmployeeImages> call, Throwable t) {
                        if(dialog != null)
                        {
                            dialog.dismiss();
                        }
                        Toast.makeText(DashBoardAdmin.this,t.getMessage(),Toast.LENGTH_SHORT).show();

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
                            Toast.makeText(DashBoardAdmin.this,"Profile Image Updated",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(DashBoardAdmin.this,response.message(),Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<EmployeeImages> call, Throwable t) {
                        if(dialog != null)
                        {
                            dialog.dismiss();
                        }
                        Toast.makeText(DashBoardAdmin.this,t.getMessage(),Toast.LENGTH_SHORT).show();

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

                Intent organization = new Intent(DashBoardAdmin.this, OrganizationDetailScree.class);
                startActivity(organization);
                break;

            case "Employees":
                Intent employee = new Intent(DashBoardAdmin.this, EmployeeListScreen.class);
                startActivity(employee);
                break;

            case "Meetings":
                Intent employees = new Intent(DashBoardAdmin.this, EmployeeListScreen.class);
                employees.putExtra("Type","Meetings");
                startActivity(employees);
                break;

            case "Task Management":
            Intent task = new Intent(DashBoardAdmin.this, EmployeeListScreen.class);
            task.putExtra("Type","Task");
            startActivity(task);
            break;

            case "Live Tracking":
                Intent live = new Intent(DashBoardAdmin.this, EmployeeListScreen.class);
                live.putExtra("Type","Live");
                startActivity(live);
                break;

            case "Salary":
                Intent salary = new Intent(DashBoardAdmin.this, EmployeeListScreen.class);
                salary.putExtra("Type","Salary");
                startActivity(salary);
                break;

            case "Field Employees":

                break;

            case "Change Password":
                Intent chnage = new Intent(DashBoardAdmin.this, ChangePasswordScreen.class);
                startActivity(chnage);
                break;

            case "Logout":

                PreferenceHandler.getInstance(DashBoardAdmin.this).clear();

                Intent log = new Intent(DashBoardAdmin.this, LandingScreen.class);
                log.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                log.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                Toast.makeText(DashBoardAdmin.this,"Logout",Toast.LENGTH_SHORT).show();
                startActivity(log);
                finish();
                break;

        }
    }

    private void getDepartment(final int id){

        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {


                DepartmentApi apiService =
                        Util.getClient().create(DepartmentApi.class);

                Call<ArrayList<Departments>> call = apiService.getDepartmentByOrganization(id);

                call.enqueue(new Callback<ArrayList<Departments>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Departments>> call, Response<ArrayList<Departments>> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                        int statusCode = response.code();

                        if(statusCode == 200 || statusCode == 204)
                        {
                            ArrayList<Departments> departmentsList = response.body();
                            if(departmentsList != null && departmentsList.size()!=0 )
                            {

                                ArrayList<Departments> departmentsArrayList = new ArrayList<>();

                                for(int i=0;i<departmentsList.size();i++){

                                    if(!departmentsList.get(i).getDepartmentName().equalsIgnoreCase("Founders")){

                                        departmentsArrayList.add(departmentsList.get(i));
                                    }
                                }

                                if(departmentsArrayList!=null&&departmentsArrayList.size()!=0){


                                    DepartmentGridAdapter adapter = new DepartmentGridAdapter(DashBoardAdmin.this,departmentsArrayList);
                                    mDepartmentGrid.setAdapter(adapter);
                                }



                            }
                            else
                            {


                            }
                        }
                        else
                        {

                            Toast.makeText(DashBoardAdmin.this,response.message(),Toast.LENGTH_SHORT).show();
                        }
//                callGetStartEnd();
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Departments>> call, Throwable t) {
                        // Log error here since request failed

                        Log.e("TAG", t.toString());
                    }
                });
            }
        });
    }

    public void getCompany(final int id){

        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {

                final OrganizationApi subCategoryAPI = Util.getClient().create(OrganizationApi.class);
                Call<ArrayList<Organization>> getProf = subCategoryAPI.getOrganizationById(id);
                //Call<ArrayList<Blogs>> getBlog = blogApi.getBlogs();

                getProf.enqueue(new Callback<ArrayList<Organization>>() {

                    @Override
                    public void onResponse(Call<ArrayList<Organization>> call, Response<ArrayList<Organization>> response) {

                        if (response.code() == 200||response.code() == 201||response.code() == 204&&response.body().size()!=0)
                        {

                            Organization organization = response.body().get(0);
                            System.out.println("Inside api");
                            PreferenceHandler.getInstance(DashBoardAdmin.this).setCompanyId(organization.getOrganizationId());
                            PreferenceHandler.getInstance(DashBoardAdmin.this).setCompanyName(organization.getOrganizationName());
                            PreferenceHandler.getInstance(DashBoardAdmin.this).setAppType(organization.getAppType());

                            PreferenceHandler.getInstance(DashBoardAdmin.this).setAppType(organization.getAppType());
                            PreferenceHandler.getInstance(DashBoardAdmin.this).setLicenseStartDate(organization.getLicenseStartDate());
                            PreferenceHandler.getInstance(DashBoardAdmin.this).setLicenseEndDate(organization.getLicenseEndDate());
                            PreferenceHandler.getInstance(DashBoardAdmin.this).setSignupDate(organization.getSignupDate());
                            PreferenceHandler.getInstance(DashBoardAdmin.this).setPlanType(organization.getPlanType());
                            PreferenceHandler.getInstance(DashBoardAdmin.this).setEmployeeLimit(organization.getEmployeeLimit());
                            PreferenceHandler.getInstance(DashBoardAdmin.this).setPlanId(organization.getPlanId());

                            appType = PreferenceHandler.getInstance(DashBoardAdmin.this).getAppType();
                            planType = PreferenceHandler.getInstance(DashBoardAdmin.this).getPlanType();
                            licensesStartDate = PreferenceHandler.getInstance(DashBoardAdmin.this).getLicenseStartDate();
                            licenseEndDate = PreferenceHandler.getInstance(DashBoardAdmin.this).getLicenseEndDate();
                            planId = PreferenceHandler.getInstance(DashBoardAdmin.this).getPlanId();

                            try{

                                if(appType!=null){

                                    if(appType.equalsIgnoreCase("Trial")){

                                        SimpleDateFormat smdf = new SimpleDateFormat("MM/dd/yyyy");

                                        long days = dateCal(licenseEndDate);



                                        if((smdf.parse(licenseEndDate).getTime()<smdf.parse(smdf.format(new Date())).getTime())){

                                            Toast.makeText(DashBoardAdmin.this, "Trial Version Expired.Please Update Paid Version", Toast.LENGTH_SHORT).show();
                                            PreferenceHandler.getInstance(DashBoardAdmin.this).clear();

                                            Intent log = new Intent(DashBoardAdmin.this, LandingScreen.class);
                                            log.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            log.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                            Toast.makeText(DashBoardAdmin.this,"Logout",Toast.LENGTH_SHORT).show();
                                            startActivity(log);
                                            finish();

                                        }else{

                                            if(days>=1&&days<=5){
                                                popupUpgrade("Hope your enjoying to use our Trial version.Get more features You need to Upgrade App","Your trial period is going to expire in "+days+" days");

                                            }else if(days==0){
                                                popupUpgrade("Hope your enjoying to use our Trial version.Get more features You need to Upgrade App","Today is last day for your free trial");

                                            }else if(days<0){
                                                Toast.makeText(DashBoardAdmin.this, "Your Trial Period is Expired", Toast.LENGTH_SHORT).show();
                                                PreferenceHandler.getInstance(DashBoardAdmin.this).clear();

                                                Intent log = new Intent(DashBoardAdmin.this, LandingScreen.class);
                                                log.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                log.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                                Toast.makeText(DashBoardAdmin.this,"Logout",Toast.LENGTH_SHORT).show();
                                                startActivity(log);
                                                finish();
                                            }

                                        }

                                    }else if(appType.equalsIgnoreCase("Paid")){

                                    }
                                }

                            }catch (Exception e){
                                e.printStackTrace();
                            }






                        }else{


                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Organization>> call, Throwable t) {

                    }
                });

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
                        DashBoardAdmin.this.finish();
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

                                    PreferenceHandler.getInstance(DashBoardAdmin.this).setMappingId(pr.getEmployeeDeviceMappingId());



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

    public long dateCal(String date){

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");



        Date fd=null,td=null;

        try {
            fd = sdf.parse(""+date);
            td = sdf.parse(""+sdf.format(new Date()));

            long diff = fd.getTime() - td.getTime();
            long days = diff / (24 * 60 * 60 * 1000);



            return  days;
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }


    }

    public void popupUpgrade(final String text,final String days){

        try{

            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(DashBoardAdmin.this);
            LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View views = inflater.inflate(R.layout.app_upgrade_pop, null);

            builder.setView(views);

            final Button mPaid = (Button) views.findViewById(R.id.paid_version_upgrade);
            final MyRegulerText mCompanyName = (MyRegulerText) views.findViewById(R.id.company_name_upgrade);
            final MyRegulerText mText = (MyRegulerText) views.findViewById(R.id.alert_message_upgrade);
            final MyRegulerText mDay = (MyRegulerText) views.findViewById(R.id.day_count_upgrade);

            final android.support.v7.app.AlertDialog dialogs = builder.create();
            dialogs.show();
            dialogs.setCanceledOnTouchOutside(true);

            mCompanyName.setText("Dear "+PreferenceHandler.getInstance(DashBoardAdmin.this).getCompanyName());
            mText.setText(""+text);
            mDay.setText(""+days);



            mPaid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    dialogs.dismiss();

                }
            });









        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
