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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Logger;

import app.zingo.employeemanagements.WebApi.BrandsApi;
import app.zingo.employeemanagements.WebApi.StockCategoriesApi;
import app.zingo.employeemanagements.WebApi.StockItemPricingsApi;
import app.zingo.employeemanagements.WebApi.StockItemsApi;
import app.zingo.employeemanagements.WebApi.StockSubCategoriesApi;
import app.zingo.employeemanagements.WebApi.UploadApi;
import app.zingo.employeemanagements.base.R;
import app.zingo.employeemanagements.model.BrandsModel;
import app.zingo.employeemanagements.model.StockCategoryModel;
import app.zingo.employeemanagements.model.StockItemModel;
import app.zingo.employeemanagements.model.StockItemPricingModel;
import app.zingo.employeemanagements.model.StockSubCategoryModel;
import app.zingo.employeemanagements.utils.Constants;
import app.zingo.employeemanagements.utils.PreferenceHandler;
import app.zingo.employeemanagements.utils.Util;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StockItemScreen extends AppCompatActivity {

    StockItemModel stockItemModel;
    ArrayList < StockCategoryModel > stockCategoryModelArrayList;
    ArrayList < StockSubCategoryModel > stocksubCategoryModelArrayList;
    ArrayList < BrandsModel > brandsModelArrayList;

    EditText mCategoryName,mCategoryDesc,si_unit,availablity,price_for,display,selling;
    LinearLayout mCategoryImages,mUploadImages,mPriceLayout,mPriceContainer;
    Button mSave;
    Spinner mCategoryList,mSubCategoryList,mBrandList;

    private static final int REQUEST_TAKE_PHOTO = 0;
    private static final int REQUEST_PICK_PHOTO = 2;
    private static final int CAMERA_PIC_REQUEST = 1111;


    public static final int MEDIA_TYPE_IMAGE = 1;
    private Uri fileUri;
    private String mediaPath;
    private String mImageFileLocation = "";
    public static final String IMAGE_DIRECTORY_NAME = "Android File Upload";
    private String postPath="";

    boolean boolean_permission_photo,boolean_data=false,first=false;
    private static final int REQUEST= 112;
    StockItemPricingModel stockItemPricingModel;

    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate ( savedInstanceState );

        try{

            setContentView ( R.layout.activity_stock_item_screen );

            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Stock Item");

            Bundle bundle = getIntent().getExtras();

            if(bundle!=null){
                stockItemModel = (StockItemModel ) bundle.getSerializable("StockItem");


            }

            mCategoryName = findViewById(R.id.stock_category_name);
            mCategoryDesc = findViewById(R.id.stock_description);
            mUploadImages = findViewById(R.id.image_layout);
            mPriceLayout = findViewById(R.id.price_layout);
            mPriceContainer = findViewById(R.id.container_price);
            si_unit = (EditText)findViewById(R.id.si_unit);
            availablity = (EditText)findViewById(R.id.availablity_item);
            price_for = (EditText)findViewById(R.id.price_for);
            display = (EditText)findViewById(R.id.display_price);
            selling = (EditText)findViewById(R.id.selling_price);
            mCategoryList = findViewById(R.id.stock_category_spinner);
            mSubCategoryList = findViewById(R.id.stock_sub_category_spinner);
            mBrandList = findViewById(R.id.stock_brand_spinner);
            mCategoryImages = findViewById(R.id.stock_category_image);
            mSave = findViewById(R.id.save);


            if(stockItemModel!=null){
                boolean_data = true;
                mCategoryName.setText(stockItemModel.getStockItemName ());
                mCategoryDesc.setText(stockItemModel.getStockItemDescription ());

                String image = stockItemModel.getStockItemImage ();

                if(image!=null&&!image.isEmpty ()){
                    mCategoryImages.setVisibility ( View.VISIBLE );
                    addImageRes("Url",image);

                }

                if(stockItemModel.getStockItemPricingList ()!=null&&stockItemModel.getStockItemPricingList ().size ()!=0){

                    mPriceContainer.setVisibility ( View.VISIBLE );
                    stockItemPricingModel = stockItemModel.getStockItemPricingList ().get ( 0 );
                    si_unit.setText ( ""+ stockItemModel.getStockItemPricingList ().get ( 0 ).getSIUnit ());
                    availablity.setText ( ""+ stockItemModel.getStockItemPricingList ().get ( 0 ).getAvailableQuantity ());
                    price_for.setText ( ""+ stockItemModel.getStockItemPricingList ().get ( 0 ).getPriceFor ());
                    display.setText ( ""+ stockItemModel.getStockItemPricingList ().get ( 0 ).getDisplayPrice ());
                    selling.setText ( ""+ stockItemModel.getStockItemPricingList ().get ( 0 ).getSellingPrice ());

                }

                getStockCategories (stockItemModel.getStockCategoryId ());

                getStockBrands (stockItemModel.getBrandId ());

            }else{
                getStockCategories (0);

                getStockBrands (0);
            }



            mCategoryList.setOnItemSelectedListener ( new AdapterView.OnItemSelectedListener ( ) {
                @Override
                public void onItemSelected ( AdapterView < ? > parent , View view , int position , long id ) {
                    getStockSubCategories (stockCategoryModelArrayList.get ( position ).getStockCategoryId ());
                }

                @Override
                public void onNothingSelected ( AdapterView < ? > parent ) {

                }
            } );


            mPriceLayout.setOnClickListener ( new View.OnClickListener ( ) {
                @Override
                public void onClick ( View v ) {

                    if(mPriceContainer.getVisibility ()==View.GONE){

                        mPriceContainer.setVisibility ( View.VISIBLE );

                    }else{
                        mPriceContainer.setVisibility ( View.GONE );
                    }


                }
            } );

            mUploadImages.setOnClickListener ( v -> {

                fn_permission_photo ();

                if(boolean_permission_photo){


                    new MaterialDialog.Builder( StockItemScreen.this)
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

                String si = si_unit.getText().toString();
                String avail = availablity.getText().toString();
                String priceFor = price_for.getText().toString();
                String displayPrce = display.getText().toString();
                String sellingPrice = selling.getText().toString();

                if(cateName.isEmpty ()){
                    Toast.makeText ( StockItemScreen.this , "Category name required" , Toast.LENGTH_SHORT ).show ( );
                }else if(desc.isEmpty ()){
                    Toast.makeText ( StockItemScreen.this , "Description name required" , Toast.LENGTH_SHORT ).show ( );
                }else if(postPath.isEmpty ()){
                    Toast.makeText ( StockItemScreen.this , "Image is required" , Toast.LENGTH_SHORT ).show ( );
                }else if(si.isEmpty()){
                    Toast.makeText ( StockItemScreen.this , "SI Unit required" , Toast.LENGTH_SHORT ).show ( );

                }else if(avail.isEmpty()){
                    Toast.makeText ( StockItemScreen.this , "Availablity Unit required" , Toast.LENGTH_SHORT ).show ( );

                }else if(priceFor.isEmpty()){
                    Toast.makeText ( StockItemScreen.this , "Price For Unit required" , Toast.LENGTH_SHORT ).show ( );

                }else if(displayPrce.isEmpty()){
                    Toast.makeText ( StockItemScreen.this , "Display Price Unit required" , Toast.LENGTH_SHORT ).show ( );

                }else if(sellingPrice.isEmpty()){
                    Toast.makeText ( StockItemScreen.this , "Selling Price required" , Toast.LENGTH_SHORT ).show ( );

                }else{
                    ArrayList< StockItemPricingModel > spList = new ArrayList <> (  );
                    StockItemPricingModel sp = new StockItemPricingModel ();
                    if(stockItemPricingModel!=null){
                        sp = stockItemPricingModel;
                    }
                    sp.setAvailableQuantity ( Integer.parseInt ( avail ) );
                    sp.setSIUnit ( si );
                    sp.setPriceFor ( Double.parseDouble ( priceFor ) );
                    sp.setDisplayPrice ( Double.parseDouble  ( displayPrce ) );
                    sp.setSellingPrice ( Double.parseDouble ( sellingPrice ) );
                    if(stockItemModel!=null){
                        sp.setStockItemId ( stockItemModel.getStockItemId () );
                    }else{
                        spList.add(sp);
                    }


                    StockItemModel scm = new StockItemModel ();
                    if(stockItemModel!=null){
                        boolean_data = true;
                        scm = stockItemModel;
                        //

                        if(stockItemPricingModel!=null){
                            updateStockItemPrice(sp);
                        }else{
                            scm.setStockItemPricingList ( spList );
                        }

                        scm.setStockItemName ( cateName );
                        scm.setStockItemDescription ( desc );
                        scm.setStockCategoryId (stockCategoryModelArrayList.get(mCategoryList.getSelectedItemPosition()).getStockCategoryId ());
                        scm.setStockSubCategoryId (stocksubCategoryModelArrayList.get(mSubCategoryList.getSelectedItemPosition()).getStockSubCategoryId ());
                        scm.setBrandId (brandsModelArrayList.get(mBrandList.getSelectedItemPosition()).getBrandId ());
                        scm.setOrganizationId ( PreferenceHandler.getInstance ( StockItemScreen.this ).getCompanyId () );


                    }else {
                        boolean_data = false;
                        scm = new StockItemModel ();
                        scm.setStockItemPricingList ( spList );
                        scm.setStockItemName ( cateName );
                        scm.setStockItemDescription ( desc );
                        scm.setStockCategoryId (stockCategoryModelArrayList.get(mCategoryList.getSelectedItemPosition()).getStockCategoryId ());
                        scm.setStockSubCategoryId (stocksubCategoryModelArrayList.get(mSubCategoryList.getSelectedItemPosition()).getStockSubCategoryId ());
                        scm.setBrandId (brandsModelArrayList.get(mBrandList.getSelectedItemPosition()).getBrandId ());
                        scm.setOrganizationId ( PreferenceHandler.getInstance ( StockItemScreen.this ).getCompanyId () );
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

                        Gson gson = new Gson ();
                        String json = gson.toJson ( scm );
                        System.out.println ("JSON item "+json );
                        uploadImage(filename,scm);
                    }
                    else
                    {
                        compressImage(postPath,scm);
                    }


                }

                }
                );

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


            if (!hasPermissions( StockItemScreen.this, PERMISSIONS)) {
                Log.d("TAG","@@@ IN IF hasPermissions");
                ActivityCompat.requestPermissions(( Activity ) StockItemScreen.this, PERMISSIONS, REQUEST );
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

    private static boolean hasPermissions( Context context, String... permissions) {
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

                    Toast.makeText( StockItemScreen.this, "Permission Required. So Please allow the permission", Toast.LENGTH_SHORT).show();
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
                Picasso.with( StockItemScreen.this).load(value).placeholder(R.drawable.no_image).
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

    private void uploadImage(final String filePath,final StockItemModel scm)
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
                final ProgressDialog dialog = new ProgressDialog( StockItemScreen.this);
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


                        scm.setStockItemImage ( Constants.IMAGE_URL+ response.body());
                        // expenses.setImageUrl(Constants.IMAGE_URL+response.body().toString());

                        if(scm.getStockItemId ()!=0){

                            updateStockItem(scm);
                        }else{
                            addStockItem(scm);
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

    public String compressImage(String filePath,final StockItemModel scm) {

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

    public void getStockCategories(final int id) {


        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.show();

        StockCategoriesApi apiService = Util.getClient().create(StockCategoriesApi.class);

        Call< ArrayList< StockCategoryModel > > call = apiService.getStockCategoryByOrganizationId(PreferenceHandler.getInstance ( StockItemScreen.this ).getCompanyId ());

        call.enqueue(new Callback<ArrayList< StockCategoryModel >>() {
            @Override
            public void onResponse( Call<ArrayList< StockCategoryModel >> call, Response<ArrayList< StockCategoryModel >> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {
                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201||statusCode==204) {


                        ArrayList< StockCategoryModel > stockCategoryModelsList = response.body();
                        if(stockCategoryModelsList != null && stockCategoryModelsList.size()!=0 )
                        {

                            stockCategoryModelArrayList = new ArrayList<>();
                            ArrayList<String> cateName = new ArrayList<>();

                            int value = 0;
                            for(int i=0;i<stockCategoryModelsList.size();i++){

                                stockCategoryModelArrayList.add(stockCategoryModelsList.get(i));
                                cateName.add(stockCategoryModelsList.get(i).getStockCategoryName ());

                                if(stockCategoryModelsList.get(i).getStockCategoryId ()==id){
                                    value = stockCategoryModelArrayList.size()-1;
                                }
                            }

                            if(stockCategoryModelArrayList!=null&&stockCategoryModelArrayList.size()!=0){

                                ArrayAdapter adapter = new ArrayAdapter<>( StockItemScreen.this, R.layout.spinner_item_selected, cateName);
                                adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

                                mCategoryList.setAdapter(adapter);
                                mCategoryList.setSelection(value);

                            }



                        }
                        else
                        {


                        }






                    }else {
                        Toast.makeText( StockItemScreen.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
            public void onFailure( Call<ArrayList< StockCategoryModel >> call, Throwable t) {

                if(dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
                //Toast.makeText(CustomerCreation.this, "Failed due to Bad Internet Connection", Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());
            }
        });



    }

    public void getStockSubCategories(final int id) {


        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.show();

        StockSubCategoriesApi apiService = Util.getClient().create(StockSubCategoriesApi.class);

        Call< ArrayList<StockSubCategoryModel> > call = apiService.getStockSubCategoriesByStockCategoryId(id);

        call.enqueue(new Callback<ArrayList<StockSubCategoryModel>>() {
            @Override
            public void onResponse(Call<ArrayList<StockSubCategoryModel>> call, Response<ArrayList<StockSubCategoryModel>> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {
                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201||statusCode==204) {


                        ArrayList<StockSubCategoryModel> stockCategoryModelsList = response.body();
                        if(stockCategoryModelsList != null && stockCategoryModelsList.size()!=0 )
                        {

                            stocksubCategoryModelArrayList = new ArrayList<>();
                            ArrayList<String> cateName = new ArrayList<>();
                            int value = 0;

                            for(int i=0;i<stockCategoryModelsList.size();i++){

                                stocksubCategoryModelArrayList.add(stockCategoryModelsList.get(i));
                                cateName.add(stockCategoryModelsList.get(i).getStockSubCategoryName ());


                                if(stockItemModel!=null){
                                    if(!first){
                                        if(stockItemModel.getStockSubCategoryId ()!=0&&stockCategoryModelsList.get(i).getStockSubCategoryId ()==stockItemModel.getStockSubCategoryId ()){
                                            value = stocksubCategoryModelArrayList.size()-1;
                                            first = true;
                                        }
                                    }
                                }
                            }

                            if(stockCategoryModelArrayList!=null&&stockCategoryModelArrayList.size()!=0){

                                ArrayAdapter adapter = new ArrayAdapter<>( StockItemScreen.this, R.layout.spinner_item_selected, cateName);
                                adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

                                mSubCategoryList.setAdapter(adapter);
                                mSubCategoryList.setSelection ( value );

                            }



                        }


                    }else {
                        Toast.makeText( StockItemScreen.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
            public void onFailure(Call<ArrayList<StockSubCategoryModel>> call, Throwable t) {

                if(dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
                //Toast.makeText(CustomerCreation.this, "Failed due to Bad Internet Connection", Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());
            }
        });



    }

    public void getStockBrands(final int id) {


        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.show();

        BrandsApi apiService = Util.getClient().create(BrandsApi.class);

        Call< ArrayList<BrandsModel> > call = apiService.getBrandsByOrganizationId(PreferenceHandler.getInstance ( StockItemScreen.this ).getCompanyId ());

        call.enqueue(new Callback<ArrayList<BrandsModel>>() {
            @Override
            public void onResponse(Call<ArrayList<BrandsModel>> call, Response<ArrayList<BrandsModel>> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {
                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201||statusCode==204) {


                        ArrayList< BrandsModel > stockCategoryModelsList = response.body();
                        if(stockCategoryModelsList != null && stockCategoryModelsList.size()!=0 )
                        {

                            brandsModelArrayList = new ArrayList<>();
                            ArrayList<String> cateName = new ArrayList<>();

                            int value = 0;
                            for(int i=0;i<stockCategoryModelsList.size();i++){

                                brandsModelArrayList.add(stockCategoryModelsList.get(i));
                                cateName.add(stockCategoryModelsList.get(i).getBrandName ());
                                if(id!=0&&stockCategoryModelsList.get(i).getBrandId ()==id){
                                    value = brandsModelArrayList.size()-1;
                                }
                            }

                            if(stockCategoryModelArrayList!=null&&stockCategoryModelArrayList.size()!=0){

                                ArrayAdapter adapter = new ArrayAdapter<>( StockItemScreen.this, R.layout.spinner_item_selected, cateName);
                                adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

                                mBrandList.setAdapter(adapter);
                                mBrandList.setSelection ( value );

                            }



                        }
                        else
                        {


                        }






                    }else {
                        Toast.makeText( StockItemScreen.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
            public void onFailure(Call<ArrayList<BrandsModel>> call, Throwable t) {

                if(dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
                //Toast.makeText(CustomerCreation.this, "Failed due to Bad Internet Connection", Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());
            }
        });



    }

    public void addStockItem(final StockItemModel scm) {


        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.show();

        StockItemsApi apiService = Util.getClient().create( StockItemsApi.class);

        Call<StockItemModel> call = apiService.addStockItems(scm);

        call.enqueue(new Callback<StockItemModel>() {
            @Override
            public void onResponse(Call<StockItemModel> call, Response<StockItemModel> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {
                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201) {

                        StockItemModel s = response.body();

                        if(s!=null){

                            Toast.makeText( StockItemScreen.this, "Stock Item created successfully", Toast.LENGTH_SHORT).show();
                            StockItemScreen.this.finish();

                        }




                    }else {
                        Toast.makeText( StockItemScreen.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
            public void onFailure( Call < StockItemModel > call, Throwable t) {

                if(dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
                //Toast.makeText(CustomerCreation.this, "Failed due to Bad Internet Connection", Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());
            }
        });



    }


    public void updateStockItem(final StockItemModel scm) {


        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.show();

        StockItemsApi apiService = Util.getClient().create( StockItemsApi.class);

        Call<StockItemModel> call = apiService.updateStockItemsById(scm.getStockItemId (),scm);

        call.enqueue(new Callback<StockItemModel>() {
            @Override
            public void onResponse(Call<StockItemModel> call, Response<StockItemModel> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {
                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201||statusCode==204) {



                        Toast.makeText( StockItemScreen.this, "Stock Item updated successfully", Toast.LENGTH_SHORT).show();
                        StockItemScreen.this.finish();




                    }else {
                        Toast.makeText( StockItemScreen.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
            public void onFailure(Call<StockItemModel> call, Throwable t) {

                if(dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
                //     Toast.makeText(CustomerCreation.this, "Failed due to Bad Internet Connection", Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());
            }
        });



    }

    public void updateStockItemPrice(final StockItemPricingModel scm) {


        StockItemPricingsApi apiService = Util.getClient().create( StockItemPricingsApi.class);

        Call< StockItemPricingModel > call = apiService.updateStockItemPricingById (scm.getStockItemPricingId (),scm);

        call.enqueue(new Callback< StockItemPricingModel >() {
            @Override
            public void onResponse( Call< StockItemPricingModel > call, Response< StockItemPricingModel > response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201||statusCode==204) {




                    }else {

                    }
                }
                catch (Exception ex)
                {

                    ex.printStackTrace();
                }
//                callGetStartEnd();
            }

            @Override
            public void onFailure( Call< StockItemPricingModel > call, Throwable t) {


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

                StockItemScreen.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
