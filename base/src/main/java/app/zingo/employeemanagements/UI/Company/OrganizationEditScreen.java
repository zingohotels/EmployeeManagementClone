package app.zingo.employeemanagements.UI.Company;

import android.Manifest;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import app.zingo.employeemanagements.Custom.MapViewScroll;
import app.zingo.employeemanagements.Model.Organization;
import app.zingo.employeemanagements.Utils.Constants;
import app.zingo.employeemanagements.Utils.TrackGPS;
import app.zingo.employeemanagements.Utils.Util;
import app.zingo.employeemanagements.WebApi.OrganizationApi;
import app.zingo.employeemanagements.WebApi.UploadApi;
import app.zingo.employeemanagements.base.R;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrganizationEditScreen extends AppCompatActivity {

    TextInputEditText mOrgName, mBuildYear, mWebsite, mLimit;
    CircleImageView mOrgLogo;
    EditText mAbout;
    AppCompatButton mUpdate;


    private EditText  lat, lng;
    private TextView location;
    Button saveMapDetailsBtn;
    private GoogleMap mMap;
    MapViewScroll mapView;
    Marker marker;
    TrackGPS trackGPS;
    double mLatitude, mLongitude;
    String city="",state="";


    DecimalFormat df2 = new DecimalFormat(".##########");
    public static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    public String TAG = "MAPLOCATION",placeId;



    int year = 0;
    Organization organization;

    private static final int REQUEST_TAKE_PHOTO = 0;
    private static final int REQUEST_PICK_PHOTO = 2;
    private Uri mMediaUri;
    private static final int CAMERA_PIC_REQUEST = 1111;

    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;

    public static final int MEDIA_TYPE_IMAGE = 1;
    private Uri fileUri;
    private String mediaPath;
    private String mImageFileLocation = "";
    public static final String IMAGE_DIRECTORY_NAME = "Android File Upload";
    private String postPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
            setContentView(R.layout.activity_organization_edit_screen);

            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Edit Organization");

            Bundle bun = getIntent().getExtras();

            if(bun!=null){

                organization = (Organization)bun.getSerializable("Organization");
            }


            mOrgLogo = findViewById(R.id.org_logo);
            mOrgName = findViewById(R.id.name);
            mBuildYear = findViewById(R.id.build);
            mWebsite = findViewById(R.id.website);
            mLimit = findViewById(R.id.location_limit);
            //mCheckIn = findViewById(R.id.check_time);
            mAbout = findViewById(R.id.about);
            mUpdate = findViewById(R.id.updateCompany);

            mapView = findViewById(R.id.google_map_view);
            location = findViewById(R.id.location_et);

            lat = findViewById(R.id.lat_et);
            lng = findViewById(R.id.lng_et);

            /*mCheckIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    openTimePicker(mCheckIn);
                }
            });*/

            trackGPS = new TrackGPS(this);

            mapView.onCreate(savedInstanceState);
            mapView.onResume();

            try {
                MapsInitializer.initialize(OrganizationEditScreen.this);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent =
                                new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY/*MODE_FULLSCREEN*/)
                                        .build(OrganizationEditScreen.this);
                        startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                    } catch (GooglePlayServicesRepairableException e) {
                        e.printStackTrace();
                        // TODO: Handle the error.
                    } catch (GooglePlayServicesNotAvailableException e) {
                        // TODO: Handle the error.
                        e.printStackTrace();
                    }
                }
            });

            //imageview onclicklistner for choose image
            mOrgLogo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new MaterialDialog.Builder(OrganizationEditScreen.this)
                            .title(R.string.uploadImages)
                            .items(R.array.uploadImages)
                            .itemsIds(R.array.itemIds)
                            .itemsCallback(new MaterialDialog.ListCallback() {
                                @Override
                                public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                    switch (which) {
                                        case 0:
                                            Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                            startActivityForResult(galleryIntent, REQUEST_PICK_PHOTO);
                                            break;
                                        case 1:
                                            captureImage();
                                            break;
                                        case 2:
                                            mOrgLogo.setImageResource(R.drawable.org_logo_holder);
                                            postPath = "";
                                            break;
                                    }
                                }
                            })
                            .show();
                }
            });

            mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mMap = googleMap;


                    if (ActivityCompat.checkSelfPermission(OrganizationEditScreen.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(OrganizationEditScreen.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling

                        return;
                    }
                    mMap.getUiSettings().setZoomControlsEnabled(true);
                    mMap.setMyLocationEnabled(true);
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    mMap.getProjection().getVisibleRegion().latLngBounds.getCenter();



                    mLatitude = Double.parseDouble(organization.getLatitude());
                    mLongitude = Double.parseDouble(organization.getLongitude());
                    LatLng latLng = new LatLng(mLatitude,mLongitude);
                    final CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(17).build();
                    marker = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.
                            defaultMarker(BitmapDescriptorFactory.HUE_RED))
                            .position(latLng));
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(LatLng latLng) {

                            lat.setText(df2.format(latLng.latitude)+"");
                            lng.setText(df2.format(latLng.longitude)+"");
                            String add = getAddress(latLng);
                            location.setText(add);
                            mMap.clear();
                            marker = mMap.addMarker(new MarkerOptions()
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                                    .position(latLng));
                            CameraPosition cameraPosition1 = new CameraPosition.Builder().target(latLng).zoom(80).build();
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition1));
                        }
                    });

                }
            });



            String currentYear = new SimpleDateFormat("yyyyy").format(new Date());

            year = Integer.parseInt(currentYear);

            if(organization!=null){

                setDetails(organization);
            }else{
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }


            mUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    validate();
                }
            });


            mBuildYear.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    String currentYear = mBuildYear.getText().toString();

                    if(currentYear==null||currentYear.isEmpty()){

                    }else{
                        int value = Integer.parseInt(currentYear);

                        if(value>year){
                            mBuildYear.setError("Build year is not valid");
                            Toast.makeText(OrganizationEditScreen.this, "Build year is not validate", Toast.LENGTH_SHORT).show();
                        }
                    }



                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });


        }catch(Exception e){
            e.printStackTrace();
        }

    }

    private String getAddress(LatLng latLng) {
        Geocoder geocoder = new Geocoder(OrganizationEditScreen.this, Locale.getDefault());
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
                state = address.getAdminArea();


                return result;
            }
            return result;
        } catch (IOException e) {
            Log.e("MapLocation", "Unable connect to Geocoder", e);
            return result;
        }

    }

    public void setDetails(final Organization org){

        mOrgName.setText(org.getOrganizationName());
        if(org.getBuiltYear()!=null&&!org.getBuiltYear().isEmpty()){
            mBuildYear.setText(""+org.getBuiltYear());
        }else{
            //mBuildYear.setText("");
        }

        mWebsite.setText(org.getWebsite());
        mLimit.setText(org.getLocationLimit() + "");
        mAbout.setText(org.getAboutUs());

        String cheIT = org.getPlaceId();

      /*  if(cheIT!=null&&!cheIT.isEmpty()){
            mCheckIn.setText(""+cheIT);
        }*/
        mAbout.setText(org.getAboutUs());
        city = org.getCity();
        state = org.getState();

        String image = org.getDeductionType();

        if(image!=null&&!image.isEmpty()){

            Picasso.with(OrganizationEditScreen.this).load(image).placeholder(R.drawable.org_logo_holder).error(R.drawable.org_logo_holder).into(mOrgLogo);
        }

        try{


            location.setText(org.getAddress());
            lat.setText(org.getLatitude());
            lng.setText(org.getLongitude());
            mMap.clear();
            LatLng orgLatlng = new LatLng(Double.parseDouble(org.getLatitude()),Double.parseDouble(org.getLongitude()));
            marker = mMap.addMarker(new MarkerOptions()
                    .position(orgLatlng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            CameraPosition cameraPosition = new CameraPosition.Builder().zoom(80).target(orgLatlng).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public void validate(){

        String orgName = mOrgName.getText().toString();
        String about = mAbout.getText().toString();
        String build = mBuildYear.getText().toString();
        String web = mWebsite.getText().toString();
        String address = location.getText().toString();
        String lati = lat.getText().toString();
        String longi = lng.getText().toString();
        String limit = mLimit.getText().toString();
       // String cheIT = mCheckIn.getText().toString();


        if(orgName.isEmpty()){
            Toast.makeText(OrganizationEditScreen.this, "Field should not be empty", Toast.LENGTH_SHORT).show();

        }else if(about.isEmpty()){
            Toast.makeText(OrganizationEditScreen.this, "Field should not be empty", Toast.LENGTH_SHORT).show();

        }else if(build.isEmpty()){
            Toast.makeText(OrganizationEditScreen.this, "Field should not be empty", Toast.LENGTH_SHORT).show();

        }else if(web.isEmpty()){
            Toast.makeText(OrganizationEditScreen.this, "Field should not be empty", Toast.LENGTH_SHORT).show();

        }else if(address.isEmpty()){
            Toast.makeText(OrganizationEditScreen.this, "Field should not be empty", Toast.LENGTH_SHORT).show();

        }else if(lati.isEmpty()){
            Toast.makeText(OrganizationEditScreen.this, "Field should not be empty", Toast.LENGTH_SHORT).show();
        }else if(longi.isEmpty()){
            Toast.makeText(OrganizationEditScreen.this, "Field should not be empty", Toast.LENGTH_SHORT).show();

        } else if (limit.isEmpty()) {
            Toast.makeText(OrganizationEditScreen.this, "Field should not be empty", Toast.LENGTH_SHORT).show();

        }else{

            Organization orgs = organization;
            orgs.setOrganizationName(orgName);
            orgs.setAboutUs(about);
            orgs.setBuiltYear(build);
            orgs.setWebsite(web);
            orgs.setAddress(address);
            orgs.setLongitude(longi);
            orgs.setLatitude(lati);
            orgs.setLocationLimit(Double.parseDouble(limit));
            orgs.setState(state);

            /*if(cheIT!=null&&!cheIT.isEmpty()){
                orgs.setPlaceId(cheIT);
            }*/
            try {

                if(postPath!=null&&!postPath.isEmpty()){

                    File file = new File(postPath);

                    if(file.length() <= 1*1024*1024) {
                        FileOutputStream out = null;
                        String[] filearray = postPath.split("/");
                        final String filename = getFilename(filearray[filearray.length-1]);

                        try {
                            out = new FileOutputStream(filename);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        Bitmap myBitmap = BitmapFactory.decodeFile(postPath);
                        //write the compressed bitmap at the field_icon specified by filename.
                        myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

                        uploadImage(filename,orgs);
                    } else {
                        compressImage(postPath,orgs);
                    }

                }else{

                    if(postPath!=null&&postPath.isEmpty()){

                        orgs.setDeductionType(null);
                    }
                    updateOrg(orgs);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id)
        {
            case android.R.id.home:

                OrganizationEditScreen.this.finish();


        }
        return super.onOptionsItemSelected(item);
    }

    public void updateOrg(final Organization organization) {




        OrganizationApi apiService = Util.getClient().create(OrganizationApi.class);

        Call<Organization> call = apiService.updateOrganization(organization.getOrganizationId(),organization);

        call.enqueue(new Callback<Organization>() {
            @Override
            public void onResponse(Call<Organization> call, Response<Organization> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {


                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201|| statusCode == 204) {



                        OrganizationEditScreen.this.finish();



                    }else {
                        Toast.makeText(OrganizationEditScreen.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception ex)
                {


                    ex.printStackTrace();
                }
//                callGetStartEnd();
            }

            @Override
            public void onFailure(Call<Organization> call, Throwable t) {


                Toast.makeText( OrganizationEditScreen.this , "Failed due to Bad Internet Connection" , Toast.LENGTH_SHORT ).show( );
                Log.e("TAG", t.toString());
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        try{

            if(resultCode == RESULT_OK){

                if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {

                        Place place = PlaceAutocomplete.getPlace(this, data);
                        //System.out.println(place.getLatLng());
                        location.setText(place.getName()+","+place.getAddress());
                        //location.setText(""+place.getId());
                        placeId= place.getId();

                        mLatitude = place.getLatLng().latitude;
                        mLongitude = place.getLatLng().longitude;

                        lat.setText(df2.format(place.getLatLng().latitude)+"");
                        lng.setText(df2.format(place.getLatLng().longitude)+"");
                        System.out.println("Star Rating = "+place.getRating());
                        if(mMap != null)
                        {
                            mMap.clear();
                            marker = mMap.addMarker(new MarkerOptions()
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                                    .position(place.getLatLng()));
                            CameraPosition cameraPosition1 = new CameraPosition.Builder().target(place.getLatLng()).zoom(17).build();
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition1));
                        }
                        //address.setText(place.getAddress());*/
                        Log.i(TAG, "Place: " + place.getName());

                }else if (requestCode == REQUEST_TAKE_PHOTO || requestCode == REQUEST_PICK_PHOTO) {
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
                        mOrgLogo.setImageBitmap(BitmapFactory.decodeFile(mediaPath));
                        cursor.close();

                        postPath = mediaPath;
                    }


                }else if (requestCode == CAMERA_PIC_REQUEST){
                    if (Build.VERSION.SDK_INT > 21) {

                        Glide.with(this).load(mImageFileLocation).into(mOrgLogo);
                        postPath = mImageFileLocation;

                    }else{
                        Glide.with(this).load(fileUri).into(mOrgLogo);
                        postPath = fileUri.getPath();
                    }
                }

            }else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {


                if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {

                    Status status = PlaceAutocomplete.getStatus(this, data);
                    // TODO: Handle the error.
                    Log.i(TAG, status.getStatusMessage());

                }else{
                    Toast.makeText(this, "Sorry, there was an error!", Toast.LENGTH_LONG).show();
                }


            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.

                if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {

                    Status status = PlaceAutocomplete.getStatus(this, data);
                    // TODO: Handle the error.
                    Log.i(TAG, status.getStatusMessage());

                }else{
                    Toast.makeText(this, "Sorry, there was an error!", Toast.LENGTH_LONG).show();
                }

            }

        }catch (Exception e){
            e.printStackTrace();
        }


    }

    public void openTimePicker(final TextInputEditText tv){

        final Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(OrganizationEditScreen.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                //tv.setText( selectedHour + ":" + selectedMinute);

                try {



                    boolean isPM =(hourOfDay >= 12);

                    String cin = ""+String.format("%02d:%02d %s", (hourOfDay == 12 || hourOfDay == 0) ? 12 : hourOfDay % 12, minute, isPM ? "PM" : "AM");
                    tv.setText( cin);
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        }, hour, minute, false);//Yes 24 hour time

        mTimePicker.show();
    }

    //Image get

    /**
     * Launching camera app to capture image
     */
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
            } catch (IOException e) {
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
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmSS").format(new Date());
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

    private void uploadImage(final String filePath,final Organization organization)
    {
        //String filePath = getRealPathFromURIPath(uri, ImageUploadActivity.this);

        final File file = new File(filePath);
        int size = 1*1024*1024;

        if(file != null)
        {
            if(file.length() > size)
            {
                System.out.println(file.length());
                compressImage(filePath,organization);
            }
            else
            {
                final ProgressDialog dialog = new ProgressDialog(OrganizationEditScreen.this);
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


                        if(Util.IMAGE_URL==null){
                            organization.setDeductionType(Constants.IMAGE_URL+ response.body());
                        }else{
                            organization.setDeductionType(Util.IMAGE_URL+ response.body());
                        }
                        // expenses.setImageUrl(Constants.IMAGE_URL+response.body().toString());

                        if(organization.getOrganizationId()!=0){

                            updateOrg(organization);
                        }else{
                           // addCustomer(customer);
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

    public String compressImage(String filePath,final Organization organization) {

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

            uploadImage(filename,organization);


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
}
