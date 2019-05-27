package app.zingo.employeemanagements.UI.NewEmployeeDesign;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;

import app.zingo.employeemanagements.Custom.Floating.RFABTextUtil;
import app.zingo.employeemanagements.Custom.Floating.RFACLabelItem;
import app.zingo.employeemanagements.Custom.Floating.RapidFloatingActionButton;
import app.zingo.employeemanagements.Custom.Floating.RapidFloatingActionContentLabelList;
import app.zingo.employeemanagements.Custom.Floating.RapidFloatingActionHelper;
import app.zingo.employeemanagements.Custom.Floating.RapidFloatingActionLayout;
import app.zingo.employeemanagements.Custom.RoundImageView;
import app.zingo.employeemanagements.FireBase.SharedPrefManager;
import app.zingo.employeemanagements.Model.Employee;
import app.zingo.employeemanagements.Model.EmployeeDeviceMapping;
import app.zingo.employeemanagements.Model.EmployeeImages;
import app.zingo.employeemanagements.Model.Organization;
import app.zingo.employeemanagements.Service.CheckDataAndLocation;
import app.zingo.employeemanagements.UI.Admin.CreateTaskScreen;
import app.zingo.employeemanagements.UI.Common.CustomerCreation;
import app.zingo.employeemanagements.UI.Common.PlanExpireScreen;
import app.zingo.employeemanagements.UI.Landing.InternalServerErrorScreen;
import app.zingo.employeemanagements.UI.NewAdminDesigns.AdminNewMainScreen;
import app.zingo.employeemanagements.Utils.Constants;
import app.zingo.employeemanagements.Utils.PreferenceHandler;
import app.zingo.employeemanagements.Utils.ThreadExecuter;
import app.zingo.employeemanagements.Utils.Util;
import app.zingo.employeemanagements.WebApi.EmployeeApi;
import app.zingo.employeemanagements.WebApi.EmployeeDeviceApi;
import app.zingo.employeemanagements.WebApi.EmployeeImageAPI;
import app.zingo.employeemanagements.WebApi.OrganizationApi;
import app.zingo.employeemanagements.WebApi.UploadApi;
import app.zingo.employeemanagements.base.BuildConfig;
import app.zingo.employeemanagements.base.R;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseTooltip;

public class EmployeeNewMainScreen extends AppCompatActivity implements RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener{

    static final String TAG = "FounderMainScreen";
    RoundImageView mProfileImage;
    TextView mCardName,mCardMobile,mCardEmail,mCardDesign,mCardAddress;
    ImageView mCardLogo;
    CardView  mCardView;
    //TextView mTrialMsgInfo;
    LinearLayout mTrialInfoLay,mShareLayout,mQrLayout;

    boolean doubleBackToExitPressedOnce = false;
    public long[] mTimer = new long[1];
    /* renamed from: t */
    private Timer t;

    Employee profile;
    EmployeeImages employeeImages;
    int userId=0,imageId=0;
    String userName="",userEmail="";
    String appType="",planType="",licensesStartDate="",licenseEndDate="";
    int planId=0;

    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    String status,selectedImage;

    String currentVersion, latestVersion;
    Dialog dialog;

    private static final int REQUEST_PERMISSIONS = 100;
    boolean boolean_permission,boolean_permissions,boolean_permissiont;
    private static final int REQUEST= 112;
    private static final int REQUESTS= 113;
    Context mContext = this;
    SharedPreferences mPref;
    SharedPreferences.Editor medit;

    LinearLayout mWhatsapp;



    int pos;
    boolean con = true;

    private static final String SHOWCASE_ID = "Tools";

    private RapidFloatingActionLayout rfaLayout;
    private RapidFloatingActionButton rfaButton;
    private RapidFloatingActionHelper rfabHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{

            setContentView(R.layout.activity_employee_new_main_screen);

            rfaLayout = (RapidFloatingActionLayout) findViewById(R.id.rfab_group_sample_fragment_a_rfal);
            rfaButton = (RapidFloatingActionButton) findViewById(R.id.label_list_sample_rfab);


            Bundle extras = getIntent().getExtras();
            if(extras != null) {
                pos = extras.getInt("viewpager_position");
                con = extras.getBoolean("Condition");
            }

            PreferenceHandler.getInstance(EmployeeNewMainScreen.this).setFirstCheck(con);
            setupViewPager((ViewPager) findViewById(R.id.viewPager));
            mWhatsapp = findViewById(R.id.whatsapp_open);

            mCardLogo = findViewById(R.id.logo);
            mCardName = findViewById(R.id.name_text);
            mCardDesign = findViewById(R.id.designation_text);
            mCardMobile = findViewById(R.id.phone_text);
            mCardEmail = findViewById(R.id.email_text);
            mCardAddress = findViewById(R.id.address_text);
            mCardView = findViewById(R.id.card);

            mCardView.setDrawingCacheEnabled(true);

            setupData();

            mPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            medit = mPref.edit();

            // getCurrentVersion();
            Intent serviceIntent = new Intent(EmployeeNewMainScreen.this, CheckDataAndLocation.class);
            startService(serviceIntent);

            mWhatsapp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String message = "Hi I'm "+ PreferenceHandler.getInstance(EmployeeNewMainScreen.this).getUserFullName()+",\n My Organization Name is "+ PreferenceHandler.getInstance(EmployeeNewMainScreen.this).getCompanyName()+".I am writing about the feedback of Zingy app Ver: "+ BuildConfig.VERSION_NAME+".";

                    PackageManager packageManager = getPackageManager();
                    Intent i = new Intent(Intent.ACTION_VIEW);

                    try {
                        String url = "https://api.whatsapp.com/send?phone=+918987250539" +"&text=" + URLEncoder.encode(message, "UTF-8");
                        i.setPackage("com.whatsapp");
                        i.setData(Uri.parse(url));
                        if (i.resolveActivity(packageManager) != null) {
                            startActivity(i);
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(EmployeeNewMainScreen.this, "WhatsApp not installed.", Toast.LENGTH_SHORT).show();
                    }



                }
            });

            fn_permission();

            if (boolean_permission) {

                if (mPref.getString("service", "").matches("")&& PreferenceHandler.getInstance(EmployeeNewMainScreen.this).getLoginStatus().equalsIgnoreCase("Login")&&!PreferenceHandler.getInstance(EmployeeNewMainScreen.this).isLocationOn()) {
                    medit.putString("service", "service").commit();

                   /* Intent intent = new Intent(getApplicationContext(), DistanceCheck.class);
                    startService(intent);*/

                } else {
                    // Toast.makeText(getApplicationContext(), "Service is already running", Toast.LENGTH_SHORT).show();
                }
            } else {

            }




            RapidFloatingActionContentLabelList rfaContent = new RapidFloatingActionContentLabelList(this);
            rfaContent.setOnRapidFloatingActionContentLabelListListener(this);
            List<RFACLabelItem> items = new ArrayList<>();
            items.add(new RFACLabelItem<Integer>()
                    .setLabel("Create Meeting")
                    .setResId(R.drawable.maintenance)
                    .setIconNormalColor(0xffd84315)
                    .setIconPressedColor(0xffbf360c)
                    .setWrapper(0)
            );
            items.add(new RFACLabelItem<Integer>()
                            .setLabel("Create Task")
//                        .setResId(R.mipmap.ico_test_c)
                            .setDrawable(getResources().getDrawable(R.drawable.employee_menu))
                            .setIconNormalColor(0xff283593)
                            .setIconPressedColor(0xff1a237e)
                            .setLabelColor(Color.BLUE)
                            .setLabelSizeSp(14)

                            .setWrapper(1)
            );
            items.add(new RFACLabelItem<Integer>()
                    .setLabel("Create Expense")
                    .setResId(R.drawable.employee_menu)
                    .setIconNormalColor(0xff056f00)
                    .setIconPressedColor(0xff0d5302)
                    .setLabelColor(0xff056f00)
                    .setWrapper(2)
            );
            items.add(new RFACLabelItem<Integer>()
                    .setLabel("Create Client")
                    .setResId(R.drawable.rating_client)
                    .setIconNormalColor(0xff283593)
                    .setIconPressedColor(0xff1a237e)
                    .setLabelColor(0xff283593)
                    .setWrapper(3)
            );
            rfaContent
                    .setItems(items)
                    .setIconShadowRadius(RFABTextUtil.dip2px(this, 5))
                    .setIconShadowColor(0xff888888)
                    .setIconShadowDy(RFABTextUtil.dip2px(this, 5))
            ;

            rfabHelper = new RapidFloatingActionHelper(
                    this,
                    rfaLayout,
                    rfaButton,
                    rfaContent
            ).build();

            try{
                presentShowcaseView(); // one second delay

            }catch (Exception e){
                e.printStackTrace();
            }


        }catch(Exception e){
            e.printStackTrace();
        }

    }

    private void fn_permission() {

        if (Build.VERSION.SDK_INT >= 23) {
            Log.d("TAG","@@@ IN IF Build.VERSION.SDK_INT >= 23");
            String[] PERMISSIONS = {

                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_WIFI_STATE

            };


            if (!hasPermissions(mContext, PERMISSIONS)) {
                Log.d("TAG","@@@ IN IF hasPermissions");
                ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, REQUEST );
            } else {
                Log.d("TAG","@@@ IN ELSE hasPermissions");
                boolean_permission = true;
                // callNextActivity();
            }
        } else {
            Log.d("TAG","@@@ IN ELSE  Build.VERSION.SDK_INT >= 23");
            //callNextActivity();
        }

    }
    private void fn_permission_photo() {

        if (Build.VERSION.SDK_INT >= 23) {
            Log.d("TAG","@@@ IN IF Build.VERSION.SDK_INT >= 23");
            String[] PERMISSIONS = {
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE

            };


            if (!hasPermissions(mContext, PERMISSIONS)) {
                Log.d("TAG","@@@ IN IF hasPermissions");
                ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, REQUEST );
            } else {
                Log.d("TAG","@@@ IN ELSE hasPermissions");
                boolean_permissions = true;
                // callNextActivity();
            }
        } else {
            Log.d("TAG","@@@ IN ELSE  Build.VERSION.SDK_INT >= 23");
            //callNextActivity();
        }

    }

    private void fn_permission_telephony() {

        if (Build.VERSION.SDK_INT >= 23) {
            Log.d("TAG","@@@ IN IF Build.VERSION.SDK_INT >= 23");
            String[] PERMISSIONS = {
                    Manifest.permission.READ_PHONE_STATE

            };


            if (!hasPermissions(mContext, PERMISSIONS)) {
                Log.d("TAG","@@@ IN IF hasPermissions");
                ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, REQUESTS );
            } else {
                Log.d("TAG","@@@ IN ELSE hasPermissions");
                boolean_permissiont = true;
                // callNextActivity();
            }
        } else {
            Log.d("TAG","@@@ IN ELSE  Build.VERSION.SDK_INT >= 23");
            //callNextActivity();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    boolean_permission = true;

                } else {
                    Toast.makeText(getApplicationContext(), "Please allow the permission", Toast.LENGTH_LONG).show();

                }
            }
            break;

            case REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("TAG","@@@ PERMISSIONS grant");
                    boolean_permission = true;

                } else {
                    Log.d("TAG","@@@ PERMISSIONS Denied");

                    Toast.makeText(mContext, "Permission Required. So Please allow the permission", Toast.LENGTH_SHORT).show();
                }
            }
            break;

            case REQUESTS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("TAG","@@@ PERMISSIONS grant");
                    boolean_permissiont = true;

                } else {
                    Log.d("TAG","@@@ PERMISSIONS Denied");

                    Toast.makeText(mContext, "Permission Required. So Please allow the permission", Toast.LENGTH_SHORT).show();
                }
            }
            break;
        }
    }




    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList();
        private final List<String> mFragmentTitleList = new ArrayList();

        public ViewPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        public Fragment getItem(int i) {
            return this.mFragmentList.get(i);
        }

        public int getCount() {
            return this.mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String str) {
            this.mFragmentList.add(fragment);
            this.mFragmentTitleList.add(str);
        }

        public CharSequence getPageTitle(int i) {
            return this.mFragmentTitleList.get(i);
        }
    }

    /*public void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(PreferenceHandler.getInstance(AdminNewMainScreen.this).getUserFullName());
        toolbar.setLogo(R.mipmap.ic_launcher);
    }*/

    private void setupTabIcons(TabLayout tabLayout) {
        tabLayout.getTabAt(4).setIcon(R.drawable.white_navigation);
    }

    private void setupViewPager(ViewPager viewPager) {
        viewPager.setOffscreenPageLimit(5);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        viewPagerAdapter.addFragment(EmployeeDashBoardFragment.getInstance(), "Dash Board");
        viewPagerAdapter.addFragment(EmployeeNotificationScrenFragment.getInstance(), "Notification");
        viewPagerAdapter.addFragment(EmployeeLoginFragment.getInstance(), "Attendance");
        viewPagerAdapter.addFragment(EmployeeTaskFragment.getInstance(), "Tasks");
        viewPagerAdapter.addFragment(EmployeeHomeFragment.getInstance(), "");
        viewPager.setAdapter(viewPagerAdapter);
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons(tabLayout);

        if(pos!=0){
            viewPager.setCurrentItem(pos);
        }
    }

    public void onBackPressed() {

        if (this.doubleBackToExitPressedOnce) {
            super.onBackPressed();
            finish();
        } else {
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_LONG).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    EmployeeNewMainScreen.this.doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

   /* public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_employer, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != R.id.action_setting) {
            return super.onOptionsItemSelected(menuItem);
        }
        startActivity(new Intent(this, SettingsActivity.class));
        return true;
    }*/

    protected void onStart() {
        super.onStart();
    }

   /* public void setupNotification() {
        String token = FirebaseInstanceId.getInstance().getToken();
        Map hashMap = new HashMap();
        hashMap.put(VariableConstants.FCM_ID_TOKEN, token);
        FirebaseDatabase instance = FirebaseDatabase.getInstance();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("AppUser/");
        stringBuilder.append(FirebaseAuth.getInstance().getCurrentUser().getUid());
        instance.getReference(stringBuilder.toString()).updateChildren(hashMap);
    }*/

   /* public void managePermissions() {
        ((Builder) ((Builder) ((Builder) ((Builder) ((Builder) ((Builder) ((Builder) TedPermission.with(this).setPermissionListener(new C19413())).setRationaleTitle((int) C1404R.string.rationale_title)).setRationaleMessage((int) C1404R.string.rationale_message)).setDeniedTitle((CharSequence) "Permission denied")).setDeniedMessage((CharSequence) "If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")).setGotoSettingButtonText((CharSequence) "Go To Settings")).setPermissions("android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION", "android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE", "android.permission.READ_PHONE_STATE")).check();
    }*/

    public void setupData() {
        // View findViewById = findViewById(R.id.subscriptionIcon);
        //View findViewById2 = findViewById(R.id.settingIcon);
        TextView organizationName = findViewById(R.id.organizationName);
        View profileView = findViewById(R.id.profile);
        TextView userName = findViewById(R.id.userName);
        mProfileImage = findViewById(R.id.profilePicture);
        mTrialInfoLay = findViewById(R.id.trial_version_info_layout);
       /* mTrialMsgInfo = (TextView) findViewById(R.id.trial_version_info_msg);
        mTrialMsgInfo.setVisibility(View.GONE);*/
        mShareLayout = findViewById(R.id.share_layout);
        mQrLayout = findViewById(R.id.qr_layout);


        organizationName.setText(PreferenceHandler.getInstance(EmployeeNewMainScreen.this).getCompanyName());
        userName.setText(PreferenceHandler.getInstance(EmployeeNewMainScreen.this).getUserFullName());



        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){

            profile = (Employee) bundle.getSerializable("Profile");

        }
        userId = PreferenceHandler.getInstance(EmployeeNewMainScreen.this).getUserId();
        int mapId = PreferenceHandler.getInstance(EmployeeNewMainScreen.this).getMappingId();

        EmployeeDeviceMapping hm = new EmployeeDeviceMapping();
        String token = SharedPrefManager.getInstance(EmployeeNewMainScreen.this).getDeviceToken();

        if(userId!=0&&token!=null&&mapId==0){
            hm.setEmployeeId(userId);
            hm.setDeviceId(token);
            addDeviceId(hm);
        }

        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fn_permission_photo();

                if(boolean_permissions){
                    selectImage();
                }else{

                    Toast.makeText(EmployeeNewMainScreen.this, "Permission required to upload Image", Toast.LENGTH_LONG).show();
                }

            }
        });

        if(profile==null){
            if(userId!=0){
                System.out.println("Going it");
                getProfile(userId,mProfileImage);
            }

        }else{

            profile.setAppOpen(true);
            String app_version = PreferenceHandler.getInstance(EmployeeNewMainScreen.this).getAppVersion();
            profile.setLastUpdated(""+ BuildConfig.VERSION_NAME);
            profile.setLastseen(new SimpleDateFormat("MM/dd/yyyy").format(new Date()));
            mCardName.setText(""+ PreferenceHandler.getInstance(EmployeeNewMainScreen.this).getUserFullName());
            if(profile.getDesignation()!=null){
                mCardDesign.setText(""+profile.getDesignation().getDesignationTitle());
            }else{
                mCardDesign.setText("");
            }

            String imageLogo = PreferenceHandler.getInstance(EmployeeNewMainScreen.this).getLogo();
            if(imageLogo!=null&&!imageLogo.isEmpty()){

                Picasso.with(EmployeeNewMainScreen.this).load(imageLogo).placeholder(R.drawable.profile_image).error(R.drawable.profile_image).into(mCardLogo);
            }
            mCardMobile.setText(""+ PreferenceHandler.getInstance(EmployeeNewMainScreen.this).getPhoneNumber());
            mCardEmail.setText(""+ PreferenceHandler.getInstance(EmployeeNewMainScreen.this).getUserEmail());
            mCardAddress.setText(""+ PreferenceHandler.getInstance(EmployeeNewMainScreen.this).getCompanyName()+"\n"+ PreferenceHandler.getInstance(EmployeeNewMainScreen.this).getCompanyAddress());



            updateProfile(profile);

            ArrayList<EmployeeImages> images = profile.getEmployeeImages();

            if(images!=null&&images.size()!=0){
                employeeImages = images.get(0);

                if(employeeImages!=null){

                    imageId = employeeImages.getEmployeeImageId();
                    String base=employeeImages.getImage();
                    if(base != null && !base.isEmpty()){
                        Picasso.with(EmployeeNewMainScreen.this).load(base).placeholder(R.drawable.profile_image).
                                error(R.drawable.profile_image).into(mProfileImage);


                    }
                }

            }

        }

        mQrLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fn_permission_telephony();

                if(boolean_permissiont){

                    Intent qr = new Intent(EmployeeNewMainScreen.this, EmployeeQrCodeGenerate.class);
                    startActivity(qr);
                }else{

                    Toast.makeText(EmployeeNewMainScreen.this, "Permission required to upload Image", Toast.LENGTH_LONG).show();
                }





            }
        });

        profileView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        try {

            if(PreferenceHandler.getInstance(EmployeeNewMainScreen.this).getCompanyId()!=0){
                getCompany(PreferenceHandler.getInstance(EmployeeNewMainScreen.this).getCompanyId());
            }else{
               /* Intent i = new Intent(EmployeeNewMainScreen.this, InternalServerErrorScreen.class);

                startActivity(i);*/
            }


        } catch (Exception e) {
            e.printStackTrace();
            Intent i = new Intent(EmployeeNewMainScreen.this, InternalServerErrorScreen.class);

            startActivity(i);
        }

        mShareLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try{

                  /*  Bitmap bitmap = Bitmap.createBitmap(mCardView.getDrawingCache());
                    //Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                    mCardView.setDrawingCacheEnabled(false);
                    String mPath = Environment.getExternalStorageDirectory().toString() + "/" + PreferenceHandler.getInstance(EmployeeNewMainScreen.this).getUserFullName()+"_Card" + ".jpg";
                    File imageFile = new File(mPath);
                    FileOutputStream outputStream = new FileOutputStream(imageFile);
                    int quality = 100;
                    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
                    outputStream.flush();
                    outputStream.close();

                    saveBitMap(EmployeeNewMainScreen.this,mCardView);*/

                  String imageUrl  = PreferenceHandler.getInstance(EmployeeNewMainScreen.this).getLogo();

                  if(imageUrl!=null&&!imageUrl.isEmpty()){

                      try {
                          URL url = new URL(imageUrl);
                          Bitmap src = BitmapFactory.decodeStream(url.openConnection().getInputStream());


                          Bitmap dest = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);

                          String yourText = "My custom Text adding to Image";

                          Canvas cs = new Canvas(dest);
                          Paint tPaint = new Paint();
                          tPaint.setTextSize(35);
                          tPaint.setColor(Color.BLUE);
                          tPaint.setStyle(Paint.Style.FILL);
                          cs.drawBitmap(src, 0f, 0f, null);
                          float height = tPaint.measureText("yY");
                          float width = tPaint.measureText(yourText);
                          float x_coord = (src.getWidth() - width)/2;
                          cs.drawText(yourText, x_coord, height+15f, tPaint); // 15f is to put space between top edge and the text, if you want to change it, you can
                          try {
                              dest.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(new File("/sdcard/ImageAfterAddingText.jpg")));
                              // dest is Bitmap, if you want to preview the final image, you can display it on screen also before saving
                          } catch (FileNotFoundException e) {
                              // TODO Auto-generated catch block
                              e.printStackTrace();
                          }
                      } catch(IOException e) {
                          System.out.println(e);
                      }


                  }

                    openScreenshot(saveBitMap(EmployeeNewMainScreen.this,mCardView));

                }catch (Exception e){
                    e.printStackTrace();
                }



                /*String upToNCharacters = PreferenceHandler.getInstance(EmployeeNewMainScreen.this).getCompanyName().substring(0, Math.min(PreferenceHandler.getInstance(EmployeeNewMainScreen.this).getCompanyName().length(), 4));


                String body = "<html><head>" +
                        "</head>" +
                        "<body>" +
                        "<h2>Hello,</h2>" +
                        "<p><br>You are invited to join the Zingy Employee App Platform. </p></br></br>"+
                        "<br><p>Here is a Procedure to Join the Platform using the Below Procedures. Make sure you store them safely. </p>"+
                        "</br><p><br>Our Organization Code- "+upToNCharacters+PreferenceHandler.getInstance(EmployeeNewMainScreen.this).getCompanyId()+
                        "</br></p><br><b>Step 1:  </b>"+"Download the app by clicking here <a href=\"https://play.google.com/store/apps/details?id=app.zingo.employeemanagements\">https://play.google.com/store/apps/details?id=app.zingo.employeemanagements</a>"+
                        "</br><br><b>Step 2: </b>"+"Click on Get Started and \"Join us as an Employee\""+
                        "</br><br><b>Step 3: </b>"+"Verify your Mobile number and then Enter the Organization Code - "+upToNCharacters+PreferenceHandler.getInstance(EmployeeNewMainScreen.this).getCompanyId()+
                        "</br><br><b>Step 4:</b>"+"Enter your basic details and the complete the Sign up process"+
                        "</br><p>From now on, Please login to your account using your organization email id and your password on a daily basis for attendance system,leave management,Expense management, sales visit etc., via mobile app. </p>"+
                        "</br><p>If you have any questions then contact the Admin/HR of the company.</p>"+
                        "</br><p><b>Cheers,</b><br><br>"+PreferenceHandler.getInstance(EmployeeNewMainScreen.this).getUserFullName()+"</p></body></html>";

                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("text/plain");


                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Employee Management App Invitation");


                emailIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(new StringBuilder()
                        .append(body)
                        .toString()));
                //emailIntent.putExtra(Intent.EXTRA_HTML_TEXT, body);
                startActivity(Intent.createChooser(emailIntent, "Send email.."));*/


            }
        });
    }

    protected void onDestroy() {
        super.onDestroy();

    }


    public void getProfile(final int id,final ImageView mProfileImage ){

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
                            profile.setAppOpen(true);
                            String app_version = PreferenceHandler.getInstance(EmployeeNewMainScreen.this).getAppVersion();
                            profile.setLastUpdated(""+ BuildConfig.VERSION_NAME);
                            profile.setLastseen(new SimpleDateFormat("MM/dd/yyyy").format(new Date()));
                            updateProfile(profile);

                            mCardName.setText(""+ PreferenceHandler.getInstance(EmployeeNewMainScreen.this).getUserFullName());
                            if(profile.getDesignation()!=null){
                                mCardDesign.setText(""+profile.getDesignation().getDesignationTitle());
                            }else{
                                mCardDesign.setText("");
                            }

                            mCardMobile.setText(""+ PreferenceHandler.getInstance(EmployeeNewMainScreen.this).getPhoneNumber());
                            mCardEmail.setText(""+ PreferenceHandler.getInstance(EmployeeNewMainScreen.this).getUserEmail());
                            mCardAddress.setText(""+ PreferenceHandler.getInstance(EmployeeNewMainScreen.this).getCompanyName()+"\n"+ PreferenceHandler.getInstance(EmployeeNewMainScreen.this).getCompanyAddress());

                            String imageLogo = PreferenceHandler.getInstance(EmployeeNewMainScreen.this).getLogo();
                            if(imageLogo!=null&&!imageLogo.isEmpty()){

                                Picasso.with(EmployeeNewMainScreen.this).load(imageLogo).placeholder(R.drawable.profile_image).error(R.drawable.profile_image).into(mCardLogo);
                            }


                            ArrayList<EmployeeImages> images = profile.getEmployeeImages();

                            if(images!=null&&images.size()!=0){
                                employeeImages = images.get(0);

                                if(employeeImages!=null){
                                    imageId = employeeImages.getEmployeeImageId();
                                    String base=employeeImages.getImage();
                                    if(base != null && !base.isEmpty()){
                                        Picasso.with(EmployeeNewMainScreen.this).load(base).placeholder(R.drawable.profile_image).
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
        AlertDialog.Builder builder = new AlertDialog.Builder(EmployeeNewMainScreen.this);
        builder.setTitle("Add Image!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Choose from Library")) {

                    galleryIntent();
                } else if (items[item].equals("Cancel")) {

                    if(dialog!=null)
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
            String picturePath = getPath( EmployeeNewMainScreen.this, selectedImageUri );
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
                    addImage(null, Util.getResizedBitmap(myBitmap,700));
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
                final ProgressDialog dialog = new ProgressDialog(EmployeeNewMainScreen.this);
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

                            if(Util.IMAGE_URL==null){
                                employeeImages.setImage(Constants.IMAGE_URL+ response.body());
                            }else{
                                employeeImages.setImage(Util.IMAGE_URL+ response.body());
                            }


                            employeeImages.setEmployeeId(employee.getEmployeeId());


                            addProfileImage(employeeImages);
                        }else{

                            EmployeeImages employeeImagess = employeeImages;
                            if(Util.IMAGE_URL==null){
                                employeeImages.setImage(Constants.IMAGE_URL+ response.body());
                            }else{
                                employeeImages.setImage(Util.IMAGE_URL+ response.body());
                            }
                            employeeImagess.setEmployeeImageId(employeeImages.getEmployeeImageId());
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

    public String compressImage(String filePath,final Employee Employee) {

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
                            Toast.makeText(EmployeeNewMainScreen.this,"Profile Image Updated",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(EmployeeNewMainScreen.this,response.message(),Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<EmployeeImages> call, Throwable t) {
                        if(dialog != null)
                        {
                            dialog.dismiss();
                        }
                        Toast.makeText(EmployeeNewMainScreen.this,t.getMessage(),Toast.LENGTH_SHORT).show();

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
                            Toast.makeText(EmployeeNewMainScreen.this,"Profile Image Updated",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(EmployeeNewMainScreen.this,response.message(),Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<EmployeeImages> call, Throwable t) {
                        if(dialog != null)
                        {
                            dialog.dismiss();
                        }
                        Toast.makeText(EmployeeNewMainScreen.this,t.getMessage(),Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });
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

                                    PreferenceHandler.getInstance(EmployeeNewMainScreen.this).setMappingId(pr.getEmployeeDeviceMappingId());



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

    public void getCompany(final int id) {

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
                            PreferenceHandler.getInstance(EmployeeNewMainScreen.this).setCompanyId(organization.getOrganizationId());
                            PreferenceHandler.getInstance(EmployeeNewMainScreen.this).setCompanyName(organization.getOrganizationName());
                            PreferenceHandler.getInstance(EmployeeNewMainScreen.this).setAppType(organization.getAppType());

                            PreferenceHandler.getInstance(EmployeeNewMainScreen.this).setAppType(organization.getAppType());
                            PreferenceHandler.getInstance(EmployeeNewMainScreen.this).setLicenseStartDate(organization.getLicenseStartDate());
                            PreferenceHandler.getInstance(EmployeeNewMainScreen.this).setLicenseEndDate(organization.getLicenseEndDate());
                            PreferenceHandler.getInstance(EmployeeNewMainScreen.this).setSignupDate(organization.getSignupDate());
                            PreferenceHandler.getInstance(EmployeeNewMainScreen.this).setOrganizationLongi(organization.getLongitude());
                            PreferenceHandler.getInstance(EmployeeNewMainScreen.this).setOrganizationLati(organization.getLatitude());
                            PreferenceHandler.getInstance(EmployeeNewMainScreen.this).setPlanType(organization.getPlanType());
                            PreferenceHandler.getInstance(EmployeeNewMainScreen.this).setEmployeeLimit(organization.getEmployeeLimit());
                            PreferenceHandler.getInstance(EmployeeNewMainScreen.this).setPlanId(organization.getPlanId());

                            appType = PreferenceHandler.getInstance(EmployeeNewMainScreen.this).getAppType();
                            planType = PreferenceHandler.getInstance(EmployeeNewMainScreen.this).getPlanType();
                            licensesStartDate = PreferenceHandler.getInstance(EmployeeNewMainScreen.this).getLicenseStartDate();
                            licenseEndDate = PreferenceHandler.getInstance(EmployeeNewMainScreen.this).getLicenseEndDate();
                            planId = PreferenceHandler.getInstance(EmployeeNewMainScreen.this).getPlanId();

                            try{

                                if(appType!=null){

                                    if(appType.equalsIgnoreCase("Trial")){

                                        SimpleDateFormat smdf = new SimpleDateFormat("MM/dd/yyyy");

                                        long days = dateCal(licenseEndDate);


                                        mTrialInfoLay.setVisibility(View.VISIBLE);
                                        if((smdf.parse(licenseEndDate).getTime()<smdf.parse(smdf.format(new Date())).getTime())){

                                            Toast.makeText(EmployeeNewMainScreen.this, "Your Trial Period is Expired", Toast.LENGTH_SHORT).show();
                                            PreferenceHandler.getInstance(EmployeeNewMainScreen.this).clear();

                                            Intent log = new Intent(EmployeeNewMainScreen.this, PlanExpireScreen.class);
                                            log.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            log.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                            //Toast.makeText(EmployeeNewMainScreen.this,"Logout",Toast.LENGTH_SHORT).show();
                                            startActivity(log);
                                            finish();

                                        }else{
                                            //mTrialMsgInfo.setText("Your Trial version is going to expiry in "+days+" days");
                                            if(days>=1&&days<=5){
                                                //popupUpgrade("Hope your enjoying to use our Trial version.Get more features You need to Upgrade App","Your trial period is going to expire in "+days+" days");



                                            }else if(days==0){
                                                // popupUpgrade("Hope your enjoying to use our Trial version.Get more features You need to Upgrade App","Today is last day for your free trial");
                                                // mTrialMsgInfo.setText("Your Trial version is going to expiry in today");

                                            }else if(days<0){
                                                Toast.makeText(EmployeeNewMainScreen.this, "Your Trial Period is Expired", Toast.LENGTH_SHORT).show();
                                                PreferenceHandler.getInstance(EmployeeNewMainScreen.this).clear();

                                                Intent log = new Intent(EmployeeNewMainScreen.this, PlanExpireScreen.class);
                                                log.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                log.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                                //Toast.makeText(EmployeeNewMainScreen.this,"Logout",Toast.LENGTH_SHORT).show();
                                                startActivity(log);
                                                finish();
                                            }

                                        }

                                    }else if(appType.equalsIgnoreCase("Paid")){
                                        mTrialInfoLay.setVisibility(View.GONE);
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


    private void getCurrentVersion() {
        System.out.println("Google inside");
        PackageManager pm = this.getPackageManager();
        PackageInfo pInfo = null;

        try {
            pInfo = pm.getPackageInfo(this.getPackageName(), 0);

        } catch (PackageManager.NameNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        currentVersion = pInfo.versionName;
        String app_version = PreferenceHandler.getInstance(EmployeeNewMainScreen.this).getAppVersion();

        if(app_version!=null&&!app_version.isEmpty()){


            if(currentVersion.equalsIgnoreCase(app_version)){

            }else{
                final AlertDialog.Builder builder = new AlertDialog.Builder(EmployeeNewMainScreen.this);
                builder.setTitle("A New Update is Available");
                builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse
                                ("https://play.google.com/store/apps/details?id=app.zingo.employeemanagements")));
                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //background.start();
                        // Toast.makeText(AdminNewMainScreen.this, "Check", Toast.LENGTH_SHORT).show();
                    }
                });

                builder.setCancelable(false);
                dialog = builder.show();
            }
        }

        // new GetVersionCode().execute();

    }

    public void updateProfile(final Employee employee){



        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {

                final EmployeeApi subCategoryAPI = Util.getClient().create(EmployeeApi.class);
                Call<Employee> getProf = subCategoryAPI.updateEmployee(employee.getEmployeeId(),employee);
                //Call<ArrayList<Blogs>> getBlog = blogApi.getBlogs();

                getProf.enqueue(new Callback<Employee>() {

                    @Override
                    public void onResponse(Call<Employee> call, Response<Employee> response) {


                        if (response.code() == 200||response.code()==201||response.code()==204)
                        {


                        }else{
                            // Toast.makeText(ChangePasswordScreen.this, "Failed due to status code"+response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Employee> call, Throwable t) {


                        //  Toast.makeText(ChangePasswordScreen.this, "Something went wrong due to "+t.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

            }

        });
    }

    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public void presentShowcaseView() throws Exception {

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, SHOWCASE_ID);


        ShowcaseTooltip toolTip1 = ShowcaseTooltip.build(this)
                .corner(30)
                .textColor(Color.parseColor("#007686"))
                .text("This is a <b>Employee QR</b> code.<br><br>Click on QR code symbol, Scan it from Admin mobile & Mark your attendance.. <br><br>Tap anywhere to continue");


        sequence.addSequenceItem(

                new MaterialShowcaseView.Builder(this)
                        .setTarget(mQrLayout)
                        .setToolTip(toolTip1)
                        .withRectangleShape()
                        .setTooltipMargin(30)
                        .setShapePadding(50)
                        .setDismissOnTouch(true)
                        .setMaskColour(getResources().getColor(R.color.tooltip_mask))
                        .build()
        );






        sequence.start();


    }

    @Override
    public void onRFACItemLabelClick(int position, RFACLabelItem item) {

        rfabHelper.toggleContent();

        if(position==0){

            String loginStatus = PreferenceHandler.getInstance(EmployeeNewMainScreen.this).getMeetingLoginStatus();
            String masterloginStatus = PreferenceHandler.getInstance(EmployeeNewMainScreen.this).getLoginStatus();


            if (masterloginStatus.equals("Login")) {

                Intent createTask = new Intent(EmployeeNewMainScreen.this, MeetingAddWithSignScreen.class);
                createTask.putExtra("EmployeeId", PreferenceHandler.getInstance(EmployeeNewMainScreen.this).getUserId());
                startActivity(createTask);

            } else {
                Toast.makeText(EmployeeNewMainScreen.this, "First Check-in Master", Toast.LENGTH_SHORT).show();
            }



        }else if(position==1){

            Intent createTask = new Intent(EmployeeNewMainScreen.this, CreateTaskScreen.class);
            createTask.putExtra("EmployeeId", PreferenceHandler.getInstance(EmployeeNewMainScreen.this).getUserId());
            startActivity(createTask);

        }else if(position==2){

            Intent leave = new Intent(EmployeeNewMainScreen.this, ExpenseManageHost.class);
            Bundle bundle = new Bundle();
            bundle.putInt("EmployeeId",profile.getEmployeeId());
            bundle.putSerializable("Employee",profile);
            leave.putExtras(bundle);
            startActivity(leave);


        }else if(position==3){

            Intent branch = new Intent(EmployeeNewMainScreen.this, CustomerCreation.class);
            startActivity(branch);

        }


    }

    @Override
    public void onRFACItemIconClick(int position, RFACLabelItem item) {

        rfabHelper.toggleContent();

        if(position==0){

            Intent createTask = new Intent(EmployeeNewMainScreen.this, MeetingAddWithSignScreen.class);
            createTask.putExtra("EmployeeId", PreferenceHandler.getInstance(EmployeeNewMainScreen.this).getUserId());
            startActivity(createTask);


        }else if(position==1){

            Intent createTask = new Intent(EmployeeNewMainScreen.this, CreateTaskScreen.class);
            createTask.putExtra("EmployeeId", PreferenceHandler.getInstance(EmployeeNewMainScreen.this).getUserId());
            startActivity(createTask);

        }else if(position==2){

            Intent leave = new Intent(EmployeeNewMainScreen.this, ExpenseManageHost.class);
            Bundle bundle = new Bundle();
            bundle.putInt("EmployeeId",profile.getEmployeeId());
            bundle.putSerializable("Employee",profile);
            leave.putExtras(bundle);
            startActivity(leave);


        }else if(position==3){

            Intent branch = new Intent(EmployeeNewMainScreen.this, CustomerCreation.class);
            startActivity(branch);

        }
    }

    private void openScreenshot(File imageFile) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);

        Uri uri = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            uri = FileProvider.getUriForFile(EmployeeNewMainScreen.this, "app.zingo.employeemanagements.fileprovider", imageFile);
        }else{
            uri = Uri.fromFile(imageFile);
        }

        intent.setDataAndType(uri, "image/*");
        startActivity(intent);
    }

    private File saveBitMap(Context context, View drawView){
        File pictureFileDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"Handcare");
        if (!pictureFileDir.exists()) {
            boolean isDirectoryCreated = pictureFileDir.mkdirs();
            if(!isDirectoryCreated)
                Log.i("ATG", "Can't create directory to save the image");
            return null;
        }
        String filename = pictureFileDir.getPath() +File.separator+ System.currentTimeMillis()+".jpg";
        File pictureFile = new File(filename);
        Bitmap bitmap =getBitmapFromView(drawView);
        try {
            pictureFile.createNewFile();
            FileOutputStream oStream = new FileOutputStream(pictureFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, oStream);
            oStream.flush();
            oStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("TAG", "There was an issue saving the image.");
        }
        scanGallery( context,pictureFile.getAbsolutePath());
        return pictureFile;
    }
    //create bitmap from view and returns it
    private Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        }   else{
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        }
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }
    // used for scanning gallery
    private void scanGallery(Context cntx, String path) {
        try {
            MediaScannerConnection.scanFile(cntx, new String[] { path },null, new MediaScannerConnection.OnScanCompletedListener() {
                public void onScanCompleted(String path, Uri uri) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
