package app.zingo.employeemanagements.ui.NewAdminDesigns;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Logger;

import app.zingo.employeemanagements.WebApi.StockCategoriesApi;
import app.zingo.employeemanagements.WebApi.UploadApi;
import app.zingo.employeemanagements.base.R;
import app.zingo.employeemanagements.model.StockCategoryModel;
import app.zingo.employeemanagements.utils.Constants;
import app.zingo.employeemanagements.utils.PreferenceHandler;
import app.zingo.employeemanagements.utils.Util;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StockCategoryScreen extends AppCompatActivity {

    StockCategoryModel stockCategoryModel;

    EditText mCategoryName,mCategoryDesc;
    LinearLayout mCategoryImages,mUploadImages;
    Button mSave;

    private static final int REQUEST_TAKE_PHOTO = 0;
    private static final int REQUEST_PICK_PHOTO = 2;
    private static final int CAMERA_PIC_REQUEST = 1111;


    public static final int MEDIA_TYPE_IMAGE = 1;
    private Uri fileUri;
    private String mediaPath;
    private String mImageFileLocation = "";
    public static final String IMAGE_DIRECTORY_NAME = "Android File Upload";
    private String postPath="";

    boolean boolean_permission_photo,boolean_data=false;
    private static final int REQUEST= 112;

    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate ( savedInstanceState );

        try{

            setContentView ( R.layout.activity_stock_category_screen );

            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Stock Categories");

            Bundle bundle = getIntent().getExtras();

            if(bundle!=null){
                stockCategoryModel = (StockCategoryModel ) bundle.getSerializable("StockCategory");


            }

            mCategoryName = findViewById(R.id.stock_category_name);
            mCategoryDesc = findViewById(R.id.stock_description);
            mUploadImages = findViewById(R.id.image_layout);
            mCategoryImages = findViewById(R.id.stock_category_image);
            mSave = findViewById(R.id.save);


            if(stockCategoryModel!=null){
                boolean_data = true;
                mCategoryName.setText(stockCategoryModel.getStockCategoryName ());
                mCategoryDesc.setText(stockCategoryModel.getStockCategoryDescription ());

                String image = stockCategoryModel.getStockCategoryImage ();

                if(image!=null&&!image.isEmpty ()){
                    mCategoryImages.setVisibility ( View.VISIBLE );
                    addImageRes("Url",image);

                }

            }


            mUploadImages.setOnClickListener ( v -> {

                fn_permission_photo ();

                if(boolean_permission_photo){


                    new MaterialDialog.Builder( StockCategoryScreen.this)
                            .title(R.string.uploadImages)
                            .items(R.array.uploadImages_non)
                            .itemsIds(R.array.itemIds_non)
                            .itemsCallback( ( dialog , view , which , text ) -> {
                                switch (which) {
                                    case 0:
                                        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                        startActivityForResult(galleryIntent, REQUEST_PICK_PHOTO);
                                        break;
                                    case 1:
                                        captureImage();
                                        break;

                                }
                            } )
                            .show();

                }else{

                }
            } );

            mSave.setOnClickListener ( v -> {

                String cateName = mCategoryName.getText().toString();
                String desc = mCategoryDesc.getText().toString();

                if(cateName.isEmpty ()){
                    Toast.makeText ( StockCategoryScreen.this , "Category name required" , Toast.LENGTH_SHORT ).show ( );
                }else if(desc.isEmpty ()){
                    Toast.makeText ( StockCategoryScreen.this , "Description name required" , Toast.LENGTH_SHORT ).show ( );
                }else if(postPath.isEmpty ()){
                    Toast.makeText ( StockCategoryScreen.this , "Image is required" , Toast.LENGTH_SHORT ).show ( );
                }else{
                    StockCategoryModel scm = new StockCategoryModel ();
                    if(stockCategoryModel!=null){
                        boolean_data = true;
                        scm = stockCategoryModel;
                        scm.setStockCategoryName ( cateName );
                        scm.setStockCategoryDescription ( desc );
                        scm.setOrganizationId ( PreferenceHandler.getInstance ( StockCategoryScreen.this ).getCompanyId () );

                    }else {
                        boolean_data = false;
                        scm = new StockCategoryModel ();
                        scm.setStockCategoryName ( cateName );
                        scm.setStockCategoryDescription ( desc );
                        scm.setOrganizationId ( PreferenceHandler.getInstance ( StockCategoryScreen.this ).getCompanyId () );
                    }



                    File file = new File(postPath);

                    if(file.length() <= 1*1024*1024)
                    {
                        FileOutputStream out = null;
                        String[] filearray = postPath.split("/");
                        final String filename = getFilename(filearray[filearray.length-1]);

                        try {
                            out = new FileOutputStream(filename);
                        } catch ( FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        Bitmap myBitmap = BitmapFactory.decodeFile(postPath);
                        //write the compressed bitmap at the field_icon specified by filename.
                        myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

                        uploadImage(filename,scm);
                    }
                    else
                    {
                        compressImage(postPath,scm);
                    }
                }




            } );

        }catch ( Exception e ){
            e.printStackTrace ();
        }

    }

    private void fn_permission_photo() {

        if ( Build.VERSION.SDK_INT >= 23) {
            Log.d("TAG","@@@ IN IF Build.VERSION.SDK_INT >= 23");
            String[] PERMISSIONS = {
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE

            };


            if (!hasPermissions( StockCategoryScreen.this, PERMISSIONS)) {
                Log.d("TAG","@@@ IN IF hasPermissions");
                ActivityCompat.requestPermissions(( Activity ) StockCategoryScreen.this, PERMISSIONS, REQUEST );
            } else {
                Log.d("TAG","@@@ IN ELSE hasPermissions");
                boolean_permission_photo = true;

            }
        } else {
            Log.d("TAG","@@@ IN ELSE  Build.VERSION.SDK_INT >= 23");
            //callNextActivity();
            boolean_permission_photo = true;
        }

    }

    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if ( ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult( int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {


            case REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("TAG","@@@ PERMISSIONS grant");
                    boolean_permission_photo = true;

                } else {
                    Log.d("TAG","@@@ PERMISSIONS Denied");

                    boolean_permission_photo = false;

                    Toast.makeText( StockCategoryScreen.this, "Permission Required. So Please allow the permission", Toast.LENGTH_SHORT).show();
                }
            }
            break;
        }
    }

    public void addImageRes(String type,String value){




        mCategoryImages.removeAllViews();

        final LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE);
        try{
            View v = vi.inflate(R.layout.gallery_layout, null);
            ImageView imageView = v.findViewById(R.id.blog_images);

            if(type!=null&&type.equalsIgnoreCase ( "url" )){
                Picasso.with( StockCategoryScreen.this).load(value).placeholder(R.drawable.no_image).
                        error(R.drawable.no_image).into(imageView);
            }else if(type!=null&&type.equalsIgnoreCase ( "bitmap" )){

                imageView.setImageBitmap( BitmapFactory.decodeFile(mediaPath));

            }else if(type!=null&&type.equalsIgnoreCase ( "camera" )){

                if (Build.VERSION.SDK_INT > 21) {


                    Glide.with(this).load(mImageFileLocation).into(imageView);

                }else{
                    Glide.with(this).load(fileUri).into(imageView);
                }

            }



            mCategoryImages.addView(v);
        }catch (Exception e){
            e.printStackTrace();
        }

        //mExpenseImages.setImageBitmap(bitmap);

    }

    private void captureImage() {
        if (Build.VERSION.SDK_INT > 21) { //use this if Lollipop_Mr1 (API 22) or above
            Intent callCameraApplicationIntent = new Intent();
            callCameraApplicationIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

            // We give some instruction to the intent to save the image
            File photoFile = null;

            try {
                // If the createImageFile will be successful, the photo file will have the address of the file
                photoFile = createImageFile();
                // Here we call the function that will try to catch the exception made by the throw function
            } catch ( IOException e) {
                Logger.getAnonymousLogger().info("Exception error in generating the file");
                e.printStackTrace();
            }
            // Here we add an extra file to the intent to put the address on to. For this purpose we use the FileProvider, declared in the AndroidManifest.
            Uri outputUri = FileProvider.getUriForFile(
                    this,
                    "app.zingo.employeemanagements.fileprovider",
                    photoFile);
            callCameraApplicationIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);

            // The following is a new line with a trying attempt
            callCameraApplicationIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

            Logger.getAnonymousLogger().info("Calling the camera App by intent");

            // The following strings calls the camera app and wait for his file in return.
            startActivityForResult(callCameraApplicationIntent, CAMERA_PIC_REQUEST);
        } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

            // start the image capture Intent
            startActivityForResult(intent, CAMERA_PIC_REQUEST);
        }
    }

    File createImageFile() throws IOException {
        Logger.getAnonymousLogger().info("Generating the image - method started");

        // Here we create a "non-collision file name", alternatively said, "an unique filename" using the "timeStamp" functionality
        String timeStamp = new SimpleDateFormat ("yyyyMMdd_HHmmSS").format(new Date ());
        String imageFileName = "IMAGE_" + timeStamp;
        // Here we specify the environment location and the exact path where we want to save the so-created file
        File storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/photo_saving_app");
        Logger.getAnonymousLogger().info("Storage directory set");

        // Then we create the storage directory if does not exists
        if (!storageDirectory.exists()) storageDirectory.mkdir();

        // Here we create the file using a prefix, a suffix and a directory
        File image = new File(storageDirectory, imageFileName + ".jpg");
        // File image = File.createTempFile(imageFileName, ".jpg", storageDirectory);

        // Here the location is saved into the string mImageFileLocation
        Logger.getAnonymousLogger().info("File name and path set");

        mImageFileLocation = image.getAbsolutePath();
        fileUri = Uri.parse(mImageFileLocation);
        // The file is returned to the previous intent across the camera application
        return image;
    }

    /**
     * Here we store the file url as it will be null after returning from camera
     * app
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * returning image / video
     */
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("Image", "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + ".jpg");
        }  else {
            return null;
        }

        return mediaFile;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        try{



            if (resultCode == RESULT_OK) {

               if (requestCode == REQUEST_TAKE_PHOTO || requestCode == REQUEST_PICK_PHOTO) {
                    if (data != null) {
                        // Get the Image from data
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};

                        Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                        assert cursor != null;
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        mediaPath = cursor.getString(columnIndex);
                        // Set the Image in ImageView for Previewing the Media
                        addImageRes ( "bitmap",mediaPath );

                        cursor.close();

                        postPath = mediaPath;
                    }


                }else if (requestCode == CAMERA_PIC_REQUEST){

                   postPath = mImageFileLocation;
                   addImageRes ( "camera",mImageFileLocation );

                }
            }
            else if (resultCode != RESULT_CANCELED) {

                Toast.makeText(this, "Sorry, there was an error!", Toast.LENGTH_LONG).show();




            }


        }catch (Exception e){
            e.printStackTrace();
        }


    }

    private void uploadImage(final String filePath,final StockCategoryModel scm)
    {
        //String filePath = getRealPathFromURIPath(uri, ImageUploadActivity.this);

        final File file = new File(filePath);
        int size = 1*1024*1024;

        if(file != null)
        {
            if(file.length() > size)
            {
                System.out.println(file.length());
                compressImage(filePath,scm);
            }
            else
            {
                final ProgressDialog dialog = new ProgressDialog( StockCategoryScreen.this);
                dialog.setCancelable(false);
                dialog.setTitle("Uploading Image..");
                dialog.show();
                Log.d("Image Upload", "Filename " + file.getName());

                RequestBody mFile = RequestBody.create( MediaType.parse("image"), file);
                MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), mFile);
                RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());
                UploadApi uploadImage = Util.getClient().create(UploadApi.class);

                Call <String> fileUpload = uploadImage.uploadProfileImages(fileToUpload, filename);
                fileUpload.enqueue(new Callback <String> () {
                    @Override
                    public void onResponse(Call<String> call, Response <String> response) {
                        if(dialog != null && dialog.isShowing())
                        {
                            dialog.dismiss();
                        }


                        scm.setStockCategoryImage ( Constants.IMAGE_URL+ response.body());
                        // expenses.setImageUrl(Constants.IMAGE_URL+response.body().toString());

                        if(scm.getStockCategoryId ()!=0){

                            updateStockCategory(scm);
                        }else{
                            addStockCategory(scm);
                        }





                        if(filePath.contains("MyFolder/Images"))
                        {
                            file.delete();
                        }
                    }
                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.d( "UpdateCate" , "Error " + "Bad Internet Connection" );
                    }
                });
            }
        }
    }

    public String compressImage(String filePath,final StockCategoryModel scm) {

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
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint (Paint.FILTER_BITMAP_FLAG));

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

            uploadImage(filename,scm);


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

    public void addStockCategory(final StockCategoryModel scm) {


        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.show();

        StockCategoriesApi apiService = Util.getClient().create(StockCategoriesApi.class);

        Call<StockCategoryModel> call = apiService.addStockCategory(scm);

        call.enqueue(new Callback<StockCategoryModel>() {
            @Override
            public void onResponse(Call<StockCategoryModel> call, Response<StockCategoryModel> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {
                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201) {

                        StockCategoryModel s = response.body();

                        if(s!=null){

                            Toast.makeText( StockCategoryScreen.this, "Stock Category created successfully", Toast.LENGTH_SHORT).show();
                            StockCategoryScreen.this.finish();

                        }




                    }else {
                        Toast.makeText( StockCategoryScreen.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
            public void onFailure(Call<StockCategoryModel> call, Throwable t) {

                if(dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
                //Toast.makeText(CustomerCreation.this, "Failed due to Bad Internet Connection", Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());
            }
        });



    }


    public void updateStockCategory(final StockCategoryModel scm) {


        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.show();

        StockCategoriesApi apiService = Util.getClient().create(StockCategoriesApi.class);

        Call<StockCategoryModel> call = apiService.updateStockCategoryById(scm.getStockCategoryId (),scm);

        call.enqueue(new Callback<StockCategoryModel>() {
            @Override
            public void onResponse(Call<StockCategoryModel> call, Response<StockCategoryModel> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {
                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201||statusCode==204) {



                        Toast.makeText( StockCategoryScreen.this, "Stock Category updated successfully", Toast.LENGTH_SHORT).show();
                        StockCategoryScreen.this.finish();




                    }else {
                        Toast.makeText( StockCategoryScreen.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
            public void onFailure(Call<StockCategoryModel> call, Throwable t) {

                if(dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
                //     Toast.makeText(CustomerCreation.this, "Failed due to Bad Internet Connection", Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());
            }
        });



    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item) {

        int id = item.getItemId();

        switch (id)
        {
            case android.R.id.home:

                StockCategoryScreen.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
