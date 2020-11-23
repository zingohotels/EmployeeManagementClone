package app.zingo.employeemanagements.ui.NewAdminDesigns;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.logging.Logger;

import app.zingo.employeemanagements.Custom.Floating.RFABShape;
import app.zingo.employeemanagements.Custom.Floating.RFABTextUtil;
import app.zingo.employeemanagements.Custom.Floating.RFACLabelItem;
import app.zingo.employeemanagements.Custom.Floating.RapidFloatingActionButton;
import app.zingo.employeemanagements.Custom.Floating.RapidFloatingActionContentLabelList;
import app.zingo.employeemanagements.Custom.Floating.RapidFloatingActionHelper;
import app.zingo.employeemanagements.Custom.Floating.RapidFloatingActionLayout;
import app.zingo.employeemanagements.Custom.MyRegulerText;
import app.zingo.employeemanagements.FireBase.SharedPrefManager;
import app.zingo.employeemanagements.model.Departments;
import app.zingo.employeemanagements.model.Employee;
import app.zingo.employeemanagements.model.EmployeeDeviceMapping;
import app.zingo.employeemanagements.model.EmployeeImages;
import app.zingo.employeemanagements.model.Organization;
import app.zingo.employeemanagements.ui.Common.CustomerCreation;
import app.zingo.employeemanagements.ui.Common.PlanExpireScreen;
import app.zingo.employeemanagements.ui.Employee.CreateEmployeeScreen;
import app.zingo.employeemanagements.ui.Employee.EmployeeListScreen;
import app.zingo.employeemanagements.ui.landing.InternalServerErrorScreen;
import app.zingo.employeemanagements.ui.newemployeedesign.EmployeeLoginFragment;
import app.zingo.employeemanagements.utils.Const;
import app.zingo.employeemanagements.utils.Constants;
import app.zingo.employeemanagements.utils.PreferenceHandler;
import app.zingo.employeemanagements.utils.ThreadExecuter;
import app.zingo.employeemanagements.utils.TrackGPS;
import app.zingo.employeemanagements.utils.Util;
import app.zingo.employeemanagements.WebApi.DepartmentApi;
import app.zingo.employeemanagements.WebApi.EmployeeApi;
import app.zingo.employeemanagements.WebApi.EmployeeDeviceApi;
import app.zingo.employeemanagements.WebApi.EmployeeImageAPI;
import app.zingo.employeemanagements.WebApi.OrganizationApi;
import app.zingo.employeemanagements.WebApi.UploadApi;
import app.zingo.employeemanagements.base.BuildConfig;
import app.zingo.employeemanagements.base.R;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;
import uk.co.deanwild.materialshowcaseview.ShowcaseTooltip;

public class AdminNewMainScreen extends AppCompatActivity implements RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener{
    static final String TAG = "FounderMainScreen";
    CircleImageView mProfileImage;
    TextView mTrialMsgInfo;
    TabLayout tabLayout;
    LinearLayout mTrialInfoLay,mShareLayout,mQrLayout,mDemo;
    LinearLayout mWhatsapp;
    TextView organizationNameMain;
    Spinner organizationName;
    boolean doubleBackToExitPressedOnce = false;
  //  ImageView mLoader;
    public long[] mTimer = new long[1];
    /* renamed from: t */
    private Timer t;

    Employee profile;
    ArrayList<Employee> profiles;
    EmployeeImages employeeImages;
    int userId=0,imageId=0;

    String appType="",planType="",licensesStartDate="",licenseEndDate="";
    int planId=0;

    String selectedImage;

    String currentVersion;
    Dialog dialog;

    int pos;

    boolean boolean_permissions;
    private static final int REQUEST= 112;
    Context mContext = this;

    public static final int MEDIA_TYPE_IMAGE = 1;
    private Uri fileUri;
    private String mImageFileLocation = "";
    public static final String IMAGE_DIRECTORY_NAME = "Android File Upload";
    private String postPath;

    private static final int REQUEST_TAKE_PHOTO = 0;
    private static final int REQUEST_PICK_PHOTO = 2;
    private static final int CAMERA_PIC_REQUEST = 1111;
    ArrayList< Organization > organizationArrayList;

    int spinnerPos = -1;
    boolean don  = false;

    private  String SHOWCASE_ID_ADMIN ;

    private RapidFloatingActionHelper rfabHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
            setContentView(R.layout.activity_admin_new_main_screen);
            mWhatsapp = findViewById(R.id.whatsapp_open);
            RapidFloatingActionLayout rfaLayout = findViewById ( R.id.rfab_group_sample_fragment_a_rfal );
            RapidFloatingActionButton rfaButton =  findViewById ( R.id.label_list_sample_rfab );
            SHOWCASE_ID_ADMIN = "ToolsAdmin"+PreferenceHandler.getInstance( AdminNewMainScreen.this).getUserId();

            Bundle extras = getIntent().getExtras();
            if(extras != null) {
                pos = extras.getInt("viewpager_position");
                spinnerPos = extras.getInt("OrgPos",-1);
                don = extras.getBoolean("Dont");
            }

            setupData();

            setupViewPager(( ViewPager ) findViewById(R.id.viewPager));

            mWhatsapp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String message = "Hi I'm "+PreferenceHandler.getInstance( AdminNewMainScreen.this).getUserFullName()+",\n My Organization Name is "+PreferenceHandler.getInstance( AdminNewMainScreen.this).getCompanyName()+".I am writing about the feedback of Mysolite app Ver: "+BuildConfig.VERSION_NAME+".";
                    PackageManager packageManager = getPackageManager();
                    Intent i = new Intent(Intent.ACTION_VIEW);

                    try {
                        String url = "https://api.whatsapp.com/send?phone=+919986128021" + "&text=" + URLEncoder.encode(message, "UTF-8");
                        i.setPackage("com.whatsapp");
                        i.setData(Uri.parse(url));
                        if (i.resolveActivity(packageManager) != null) {
                            startActivity(i);
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText( AdminNewMainScreen.this, "WhatsApp not installed.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            try{

                if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.LOLLIPOP) {
                    presentShowcaseSequence();
                }

            }catch (Exception e){
                e.printStackTrace();
            }


            RapidFloatingActionContentLabelList rfaContent = new RapidFloatingActionContentLabelList(this);
            rfaContent.setOnRapidFloatingActionContentLabelListListener(this);
            List< RFACLabelItem > items = new ArrayList<>();
            items.add(new RFACLabelItem <Integer> ()
                    .setLabel("Create Department")
                    .setResId(R.drawable.maintenance)
                    .setIconNormalColor(0xffd84315)
                    .setIconPressedColor(0xffbf360c)
                    .setWrapper(0)
            );
            items.add(new RFACLabelItem <Integer> ()
                            .setLabel("Create Employee")
//                        .setResId(R.mipmap.ico_test_c)
                            .setDrawable(getResources().getDrawable(R.drawable.employee_menu))
                            .setIconNormalColor(0xff4e342e)
                            .setIconPressedColor(0xff3e2723)
                            .setLabelColor(Color.WHITE)
                            .setLabelSizeSp(14)
                            .setLabelBackgroundDrawable( RFABShape.generateCornerShapeDrawable(0xaa000000, RFABTextUtil.dip2px(this, 4)))
                            .setWrapper(1)
            );
            items.add(new RFACLabelItem <Integer> ()
                    .setLabel("Create Task")
                    .setResId(R.drawable.employee_menu)
                    .setIconNormalColor(0xff056f00)
                    .setIconPressedColor(0xff0d5302)
                    .setLabelColor(0xff056f00)
                    .setWrapper(2)
            );
            items.add(new RFACLabelItem <Integer> ()
                    .setLabel("Create Customer")
                    .setResId(R.drawable.rating_client)
                    .setIconNormalColor(0xff283593)
                    .setIconPressedColor(0xff1a237e)
                    .setLabelColor(0xff283593)
                    .setWrapper(3)
            );
            rfaContent
                    .setItems(items)
                    .setIconShadowRadius( RFABTextUtil.dip2px(this, 5))
                    .setIconShadowColor(0xff888888)
                    .setIconShadowDy( RFABTextUtil.dip2px(this, 5))
            ;

            rfabHelper = new RapidFloatingActionHelper (
                    this,
                    rfaLayout ,
                    rfaButton ,
                    rfaContent
            ).build();


        }catch(Exception e){
            e.printStackTrace();
        }

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List< Fragment > mFragmentList = new ArrayList();
        private final List<String> mFragmentTitleList = new ArrayList();

        public ViewPagerAdapter( FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        public Fragment getItem( int i) {
            return this.mFragmentList.get(i);
        }

        public int getCount() {
            return this.mFragmentList.size();
        }

        public void addFragment( Fragment fragment, String str) {
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

        if(PreferenceHandler.getInstance( AdminNewMainScreen.this).getUserRoleUniqueID()==9){
            tabLayout.getTabAt(4).setIcon(R.drawable.white_navigation);
        }else{
            tabLayout.getTabAt(3).setIcon(R.drawable.white_navigation);
        }

    }

    private void setupViewPager( ViewPager viewPager) {
        viewPager.setOffscreenPageLimit(4);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        viewPagerAdapter.addFragment( AdminDashBoardFragment.getInstance(), "Dash Board");
        viewPagerAdapter.addFragment(EmployerNotificationFragment.getInstance(), "Notifications");
        if(PreferenceHandler.getInstance( AdminNewMainScreen.this).getUserRoleUniqueID()==9){
            viewPagerAdapter.addFragment(EmployeeLoginFragment.getInstance(), "Attendance");
        }else{
            //tabLayout.getTabAt(3).setIcon(R.drawable.white_navigation);
        }
        viewPagerAdapter.addFragment(TaskAdminFragment.getInstance(), "Tasks");
        viewPagerAdapter.addFragment(AdminHomeFragment.getInstance(), "");
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons(tabLayout);

        if(pos!=0){
            viewPager.setCurrentItem(pos);
        }
    }

    public void onBackPressed() {
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        if (drawerLayout.isDrawerOpen( GravityCompat.START)) {
            drawerLayout.closeDrawer( GravityCompat.START);
        } else if (this.doubleBackToExitPressedOnce) {
            super.onBackPressed();
            finish();
        } else {
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_LONG).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    AdminNewMainScreen.this.doubleBackToExitPressedOnce = false;
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



    public void setupData() {
        organizationName = findViewById(R.id.branch_name);
        organizationName.setVisibility(View.GONE);
        organizationNameMain = findViewById(R.id.organizationName);
        View profileView = findViewById(R.id.profile);
        TextView userName = findViewById(R.id.userName);
        mProfileImage = findViewById(R.id.profilePicture);
        mTrialInfoLay = findViewById(R.id.trial_version_info_layout);
        mShareLayout = findViewById(R.id.share_layout);
        mQrLayout = findViewById(R.id.qr_layout);
        mDemo = findViewById(R.id.demo_layout);
        mQrLayout.setVisibility(View.VISIBLE);
        mTrialMsgInfo = findViewById(R.id.trial_version_info_msg);


        userName.setText(PreferenceHandler.getInstance( AdminNewMainScreen.this).getUserFullName());


        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){

            profile = (Employee) bundle.getSerializable("Profile");

        }
        userId = PreferenceHandler.getInstance( AdminNewMainScreen.this).getUserId();
        int mapId = PreferenceHandler.getInstance( AdminNewMainScreen.this).getMappingId();


        mDemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try{

                    MaterialShowcaseView.resetAll( AdminNewMainScreen.this);
                    if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.LOLLIPOP) {
                        presentShowcaseSequence();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }



            }
        });


        if(PreferenceHandler.getInstance( AdminNewMainScreen.this).getUserRoleUniqueID()==2){

            if(spinnerPos!=-1){

                organizationName.setVisibility(View.VISIBLE);

                if(spinnerPos==0){

                    organizationNameMain.setText(PreferenceHandler.getInstance( AdminNewMainScreen.this).getCompanyName());

                }else{
                    organizationNameMain.setText(PreferenceHandler.getInstance( AdminNewMainScreen.this).getHeadName()+" - "+PreferenceHandler.getInstance( AdminNewMainScreen.this).getCompanyName());
                }

               // mLoader.setVisibility(View.VISIBLE);
              /*  if(PreferenceHandler.getInstance(AdminNewMainScreen.this).getHeadOrganizationId()!=0){

                    getBranches(PreferenceHandler.getInstance(AdminNewMainScreen.this).getHeadOrganizationId(),spinnerPos);
                }else{

                    getBranches(PreferenceHandler.getInstance(AdminNewMainScreen.this).getCompanyId(),spinnerPos);
                }*/
            }else{

                organizationNameMain.setText(PreferenceHandler.getInstance( AdminNewMainScreen.this).getCompanyName());
                //mLoader.setVisibility(View.VISIBLE);
               /* if(PreferenceHandler.getInstance(AdminNewMainScreen.this).getHeadOrganizationId()!=0){

                    getBranches(PreferenceHandler.getInstance(AdminNewMainScreen.this).getHeadOrganizationId(),-1);
                }else{

                    getBranches(PreferenceHandler.getInstance(AdminNewMainScreen.this).getCompanyId(),-1);
                }*/
            }

        }






        organizationName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

              /*  if(don){

                }else{

                    don = false;

                }
*/
                if(i==0){

                    if(spinnerPos==0&&don){

                    }else if(spinnerPos==-1){

                    }
                    else{

                        if(PreferenceHandler.getInstance( AdminNewMainScreen.this).getHeadOrganizationId()!=0&&(PreferenceHandler.getInstance( AdminNewMainScreen.this).getHeadOrganizationId()!=PreferenceHandler.getInstance( AdminNewMainScreen.this).getCompanyId())){

                           // mLoader.setVisibility(View.VISIBLE);
                            getCompanys(PreferenceHandler.getInstance( AdminNewMainScreen.this).getHeadOrganizationId(),0);

                        }

                        don = false;
                    }




                }else{

                    if(spinnerPos==i&&don){

                    }else{

                       // mLoader.setVisibility(View.VISIBLE);
                        getCompanys(organizationArrayList.get(i-1).getOrganizationId(),i);
                        don = false;
                    }



                }



            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        EmployeeDeviceMapping hm = new EmployeeDeviceMapping();
        String token = SharedPrefManager.getInstance( AdminNewMainScreen.this).getDeviceToken();

        if(userId!=0&&token!=null&&mapId==0){
            hm.setEmployeeId(userId);
            hm.setDeviceId(token);
            addDeviceId(hm);
        }


        if(PreferenceHandler.getInstance( AdminNewMainScreen.this).getUserRoleUniqueID()==2||PreferenceHandler.getInstance( AdminNewMainScreen.this).getUserRoleUniqueID()==9){

            mShareLayout.setVisibility(View.VISIBLE);

        }else{
            mShareLayout.setVisibility(View.GONE);
        }
        mQrLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent qr = new Intent( AdminNewMainScreen.this, AdminQrTabScreen.class);
                startActivity(qr);
            }
        });

        mShareLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String upToNCharacters = PreferenceHandler.getInstance( AdminNewMainScreen.this).getCompanyName().substring(0, Math.min(PreferenceHandler.getInstance( AdminNewMainScreen.this).getCompanyName().length(), 4));


                String body = "<html><head>" +
                        "</head>" +
                        "<body>" +
                        "<h2>Hello,</h2>" +
                        "<p><br>I'm "+PreferenceHandler.getInstance( AdminNewMainScreen.this).getUserFullName()+" your manager. You are invited to join the Mysolite Employee App Platform. </p></br></br>"+
                        "<br><p>Here is a Procedure to Join the Platform using the Below Procedures. Make sure you store them safely. </p>"+
                        "</br><p><br>Our Organization Code- "+upToNCharacters+PreferenceHandler.getInstance( AdminNewMainScreen.this).getCompanyId()+
                        "</br></p><br><b>Step 1:  </b>"+"Download the app by clicking here <a href=\"https://play.google.com/store/apps/details?id=app.zingo.employeemanagements\">https://play.google.com/store/apps/details?id=app.zingo.employeemanagements</a>"+
                        "</br><br><b>Step 2: </b>"+"Click on Get Started and \"Join us as an Employee\""+
                        "</br><br><b>Step 3: </b>"+"Verify your Mobile number and then Enter the Organization Code - "+upToNCharacters+PreferenceHandler.getInstance( AdminNewMainScreen.this).getCompanyId()+
                        "</br><br><b>Step 4:</b>"+"Enter your basic details and the complete the Sign up process"+
                        "</br><p>From now on, Please login to your account using your organization email id and your password on a daily basis for attendance system,leave management,Expense management, sales visit etc., via mobile app. </p>"+
                        "</br><p>If you have any questions then contact the Admin/HR of the company.</p>"+
                        "</br><p><b>Cheers,</b><br><br>"+PreferenceHandler.getInstance( AdminNewMainScreen.this).getUserFullName()+"</p></body></html>";

              //  Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                //emailIntent.setType("text/plain");


/*
                emailIntent.putExtra(Intent.EXTRA_EMAIL, aEmailList);


                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Employee Management App Invitation");


                emailIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(new StringBuilder()
                        .append(body)
                        .toString()));
                //emailIntent.putExtra(Intent.EXTRA_HTML_TEXT, body);
                startActivity(emailIntent);*/
                Intent email = new Intent(Intent.ACTION_SENDTO);
                String aEmailList[] = { ""};
                email.setData(Uri.parse("mailto:"));
                email.putExtra(Intent.EXTRA_EMAIL,aEmailList);
                email.putExtra(Intent.EXTRA_SUBJECT, "Employee Management App Invitation");
                email.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(new StringBuilder()
                        .append(body)
                        .toString()));
                try {
                    startActivity(Intent.createChooser(email, "Select Email app"));
                } catch ( ActivityNotFoundException activityNotFoundException) {
                    Toast.makeText( AdminNewMainScreen.this , "No App Found" , Toast.LENGTH_SHORT ).show( );
                }
            }
        });

        mProfileImage.setOnClickListener( v -> {
            fn_permission_photo();
            if(boolean_permissions){

                new MaterialDialog.Builder( AdminNewMainScreen.this)
                        .title(R.string.uploadImages)
                        .items(R.array.uploadImages)
                        .itemsIds(R.array.itemIds)
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
                                case 2:
                                    mProfileImage.setImageResource(R.drawable.ic_account_circle_black);
                                    postPath = "";

                                    if (employeeImages == null) {
                                        EmployeeImages employeeImages = new EmployeeImages();
                                        employeeImages.setImage(null);

                                        employeeImages.setEmployeeId(PreferenceHandler.getInstance ( AdminNewMainScreen.this ).getUserId ());
                                        addProfileImage(employeeImages);
                                    } else {

                                        EmployeeImages employeeImagess = employeeImages;
                                        employeeImages.setImage(null);
                                        employeeImagess.setEmployeeImageId(employeeImages.getEmployeeImageId());
                                        employeeImages.setEmployeeId(PreferenceHandler.getInstance ( AdminNewMainScreen.this ).getUserId ());
                                        updateProfileImage(employeeImages);
                                    }

                                    break;
                            }
                        } )
                        .show();

            }else{
                Toast.makeText( AdminNewMainScreen.this, "Permission required to upload Image", Toast.LENGTH_LONG).show();
            }


        } );

        if(profile==null){
            if(userId!=0){
                System.out.println("Going it");
                getProfile(userId,mProfileImage);
            }

        }else{

            profile.setAppOpen(true);
            String app_version = PreferenceHandler.getInstance( AdminNewMainScreen.this).getAppVersion();
            profile.setLastUpdated(""+ BuildConfig.VERSION_NAME);
            profile.setLastseen(new SimpleDateFormat("MM/dd/yyyy").format(new Date()));
            updateProfile(profile);

            ArrayList<EmployeeImages> images = profile.getEmployeeImages();

            if(images!=null&&images.size()!=0){
                employeeImages = images.get(0);

                if(employeeImages!=null){

                    imageId = employeeImages.getEmployeeImageId();
                    String base=employeeImages.getImage();
                    if(base != null && !base.isEmpty()){
                        Picasso.with( AdminNewMainScreen.this).load(base).placeholder(R.drawable.profile_image).error(R.drawable.profile_image).into(mProfileImage);
                    }
                }
            }
        }

        profileView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        try {

            if(PreferenceHandler.getInstance( AdminNewMainScreen.this).getCompanyId()!=0){
                getCompany(PreferenceHandler.getInstance( AdminNewMainScreen.this).getCompanyId());
            }else{

            }

        } catch (Exception e) {
            e.printStackTrace();
            Intent i = new Intent( AdminNewMainScreen.this, InternalServerErrorScreen.class);

            startActivity(i);
        }


    }


    private void fn_permission_photo() {

        if (Build.VERSION.SDK_INT >= 23) {
            Log.d("TAG","@@@ IN IF Build.VERSION.SDK_INT >= 23");
            String[] PERMISSIONS = {
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE

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


    private void stopTimer() {
        if (this.t != null) {
            this.t.cancel();
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
    }


    public void getProfile(final int id,final ImageView mProfileImage ){
        new ThreadExecuter ().execute( new Runnable() {
            @Override
            public void run() {
                final EmployeeApi subCategoryAPI = Util.getClient().create( EmployeeApi.class);
                Call<ArrayList<Employee>> getProf = subCategoryAPI.getProfileById(id);
                getProf.enqueue(new Callback<ArrayList<Employee>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Employee>> call, Response<ArrayList<Employee>> response) {
                        if (response.code() == 200) {
                            System.out.println("Inside api");
                            profile = response.body().get(0);
                            ArrayList<EmployeeImages> images = profile.getEmployeeImages();
                            if(images!=null&&images.size()!=0){
                                employeeImages = images.get(0);
                                if(employeeImages!=null){
                                    imageId = employeeImages.getEmployeeImageId();
                                    String base=employeeImages.getImage();
                                    if(base != null && !base.isEmpty()){
                                        Picasso.with( AdminNewMainScreen.this).load(base).placeholder(R.drawable.profile_image).error(R.drawable.profile_image).into(mProfileImage);
                                    }
                                }
                            }

                       /* if(profiles.size ()!=0&&!profiles.isEmpty ()){
                        for(Employee employee:profiles){
                            if(employee.getEmployeeImages ().size ()!=0&&!employee.getEmployeeImages ().isEmpty ()){
                                image=employee.getEmployeeImages ();
                            }
                        }
                        if(image.size ()!=0&&!image.isEmpty ()){
                            for(EmployeeImages employeeImages:image){
                                imageId = employeeImages.getEmployeeImageId();
                                String base=employeeImages.getImage();
                                if(base != null && !base.isEmpty()){
                                    Picasso.with( AdminNewMainScreen.this).load(base).placeholder(R.drawable.profile_image).error(R.drawable.profile_image).into(mProfileImage);
                                }
                            }
                        }
                    }*/
                            profile.setAppOpen(true);
                            String app_version = PreferenceHandler.getInstance( AdminNewMainScreen.this).getAppVersion();
                            profile.setLastUpdated(""+ BuildConfig.VERSION_NAME);
                            profile.setLastseen(new SimpleDateFormat("MM/dd/yyyy").format(new Date()));
                            updateProfile(profile);
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Employee>> call, Throwable t) {

                    }
                });
            }
        });
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
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmSS",Locale.US).format(new Date());
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
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + ".jpg");
        }  else {
            return null;
        }

        return mediaFile;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_TAKE_PHOTO || requestCode == REQUEST_PICK_PHOTO) {
                if (data != null) {
                    // Get the Image from data
                    Uri selectedImages = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(selectedImages, filePathColumn, null, null, null);
                    assert cursor != null;
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String mediaPath = cursor.getString ( columnIndex );
                    // Set the Image in ImageView for Previewing the Media
                    mProfileImage.setImageBitmap(BitmapFactory.decodeFile( mediaPath ));
                    cursor.close();

                    postPath = mediaPath;

                    String[] all_path = {postPath};
                    selectedImage = all_path[0];
                    for (String s:all_path) {
                        File imgFile = new  File(s);
                        if(imgFile.exists()) {
                            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                            addImage( Util.getResizedBitmap(myBitmap,700));
                        }
                    }
                }

            }else if (requestCode == CAMERA_PIC_REQUEST){
                if (Build.VERSION.SDK_INT > 21) {

                    Glide.with(this).load(mImageFileLocation).into(mProfileImage);
                    postPath = mImageFileLocation;

                }else{
                    Glide.with(this).load(fileUri).into(mProfileImage);
                    postPath = fileUri.getPath();
                }

                String[] all_path = {postPath};
                selectedImage = all_path[0];
                for (String s:all_path)
                {
                    File imgFile = new  File(s);
                    if(imgFile.exists()) {
                        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        addImage( Util.getResizedBitmap(myBitmap,700));
                    }
                }
            }
        }
    }

    public static String getPath(Context context, Uri uri ) {
        String result = null;

        Cursor cursor = context.getContentResolver().query(uri,null,null,null,null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
      //  return result;
    }

    public void addImage(Bitmap bitmap)
    {
        try{
            if(bitmap != null)
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
                final ProgressDialog dialog = new ProgressDialog( AdminNewMainScreen.this);
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
                        if(dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        String ImageUrl = Const.IMAGE_URL+response.body ().replaceAll("\"", "");
                        if(employeeImages==null){
                            EmployeeImages employeeImages = new EmployeeImages();
                            if( Util.IMAGE_URL==null){
                                employeeImages.setImage(ImageUrl);
                            }else{
                                employeeImages.setImage(ImageUrl);
                            }
                            employeeImages.setEmployeeId(employee.getEmployeeId());
                            addProfileImage(employeeImages);
                        }else{

                            EmployeeImages employeeImagess = employeeImages;
                            if( Util.IMAGE_URL==null){
                                employeeImages.setImage(ImageUrl);
                            }else{
                                employeeImages.setImage(ImageUrl);
                            }
                           // employeeImagess.setImage(Constants.IMAGE_URL+response.body().toString());
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
                        Log.d( "UpdateCate" , "Error " + "Bad Internet Connection" );
                    }
                });
            }
        }
    }

    public String compressImage(String filePath,final  Employee Employee) {
        //String filePath = getRealPathFromURI(imageUri);
        Bitmap scaledBitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        // by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
        //you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

        // max Height and width values of the compressed image is taken as 816x612
        float maxHeight = actualHeight/2;//2033.0f;
        float maxWidth = actualWidth/2;//1011.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

        // width and height values are set maintaining the aspect ratio of the image

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
        options.inJustDecodeBounds = false;
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

        new ThreadExecuter ().execute( new Runnable() {
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
                            Toast.makeText( AdminNewMainScreen.this,"Profile Image Updated",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText( AdminNewMainScreen.this,response.message(),Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<EmployeeImages> call, Throwable t) {
                        if(dialog != null)
                        {
                            dialog.dismiss();
                        }
                        Toast.makeText( AdminNewMainScreen.this , "Bad Internet Connection" , Toast.LENGTH_SHORT ).show( );

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

        new ThreadExecuter ().execute( new Runnable() {
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
                            Toast.makeText( AdminNewMainScreen.this,"Profile Image Updated",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText( AdminNewMainScreen.this,response.message(),Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<EmployeeImages> call, Throwable t) {
                        if(dialog != null)
                        {
                            dialog.dismiss();
                        }
                        Toast.makeText( AdminNewMainScreen.this , "Bad Internet Connection" , Toast.LENGTH_SHORT ).show( );

                    }
                });
            }
        });
    }

    public void addDeviceId(final EmployeeDeviceMapping pf)
    {

        new ThreadExecuter ().execute( new Runnable() {
            @Override
            public void run() {


                EmployeeDeviceApi hotelOperation = Util.getClient().create( EmployeeDeviceApi.class);
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

                                    PreferenceHandler.getInstance( AdminNewMainScreen.this).setMappingId(pr.getEmployeeDeviceMappingId());



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

        new ThreadExecuter ().execute( new Runnable() {
            @Override
            public void run() {

                final OrganizationApi subCategoryAPI = Util.getClient().create( OrganizationApi.class);
                Call<ArrayList< Organization >> getProf = subCategoryAPI.getOrganizationById(id);
                //Call<ArrayList<Blogs>> getBlog = blogApi.getBlogs();

                getProf.enqueue(new Callback<ArrayList< Organization >>() {

                    @Override
                    public void onResponse( Call<ArrayList< Organization >> call, Response<ArrayList< Organization >> response) {

                        if (response.code() == 200||response.code() == 201||response.code() == 204&&response.body().size()!=0)
                        {

                            Organization organization = response.body().get(0);
                            System.out.println("Inside api");
                            PreferenceHandler.getInstance( AdminNewMainScreen.this).setCompanyId(organization.getOrganizationId());
                            PreferenceHandler.getInstance( AdminNewMainScreen.this).setCompanyName(organization.getOrganizationName());
                            PreferenceHandler.getInstance( AdminNewMainScreen.this).setAppType(organization.getAppType());

                            PreferenceHandler.getInstance( AdminNewMainScreen.this).setAppType(organization.getAppType());
                            PreferenceHandler.getInstance( AdminNewMainScreen.this).setLicenseStartDate(organization.getLicenseStartDate());
                            PreferenceHandler.getInstance( AdminNewMainScreen.this).setLicenseEndDate(organization.getLicenseEndDate());
                            PreferenceHandler.getInstance( AdminNewMainScreen.this).setSignupDate(organization.getSignupDate());
                            PreferenceHandler.getInstance( AdminNewMainScreen.this).setOrganizationLongi(organization.getLongitude());
                            PreferenceHandler.getInstance( AdminNewMainScreen.this).setOrganizationLati(organization.getLatitude());
                            PreferenceHandler.getInstance( AdminNewMainScreen.this).setPlanType(organization.getPlanType());
                            PreferenceHandler.getInstance( AdminNewMainScreen.this).setEmployeeLimit(organization.getEmployeeLimit());
                            PreferenceHandler.getInstance( AdminNewMainScreen.this).setPlanId(organization.getPlanId());
                            PreferenceHandler.getInstance( AdminNewMainScreen.this).setResellerUserId(organization.getResellerProfileId());

                            appType = PreferenceHandler.getInstance( AdminNewMainScreen.this).getAppType();
                            planType = PreferenceHandler.getInstance( AdminNewMainScreen.this).getPlanType();
                            licensesStartDate = PreferenceHandler.getInstance( AdminNewMainScreen.this).getLicenseStartDate();
                            licenseEndDate = PreferenceHandler.getInstance( AdminNewMainScreen.this).getLicenseEndDate();
                            planId = PreferenceHandler.getInstance( AdminNewMainScreen.this).getPlanId();

                            try{

                                if(appType!=null){

                                    if(appType.equalsIgnoreCase("Trial")){

                                        SimpleDateFormat smdf = new SimpleDateFormat("MM/dd/yyyy");

                                        long days = dateCal(licenseEndDate);


                                        mTrialInfoLay.setVisibility(View.VISIBLE);
                                        if((smdf.parse(licenseEndDate).getTime()<smdf.parse(smdf.format(new Date())).getTime())){

                                            Toast.makeText( AdminNewMainScreen.this, "Your Trial Period is Expired", Toast.LENGTH_SHORT).show();
                                            PreferenceHandler.getInstance( AdminNewMainScreen.this).clear();

                                            Intent log = new Intent( AdminNewMainScreen.this, PlanExpireScreen.class);
                                            log.putExtra("Screen","Admin");
                                            log.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            log.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                            //Toast.makeText(AdminNewMainScreen.this,"Logout",Toast.LENGTH_SHORT).show();
                                            startActivity(log);
                                            finish();

                                        }else{
                                            mTrialMsgInfo.setText("Your Trial version is going to expiry in "+days+" days");
                                            if(days>=1&&days<=5){
                                                popupUpgrade("Hope your enjoying to use our Trial version.Get more features You need to Upgrade App","Your trial period is going to expire in "+days+" days");



                                            }else if(days==0){
                                               popupUpgrade("Hope your enjoying to use our Trial version.Get more features You need to Upgrade App","Today is last day for your free trial");
                                                mTrialMsgInfo.setText("Your Trial version is going to expiry in today");

                                            }else if(days<0){
                                                Toast.makeText( AdminNewMainScreen.this, "Your Trial Period is Expired", Toast.LENGTH_SHORT).show();
                                                PreferenceHandler.getInstance( AdminNewMainScreen.this).clear();

                                                Intent log = new Intent( AdminNewMainScreen.this, PlanExpireScreen.class);
                                                log.putExtra("Screen","Admin");
                                                log.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                log.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                                //Toast.makeText(AdminNewMainScreen.this,"Logout",Toast.LENGTH_SHORT).show();
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
                    public void onFailure( Call<ArrayList< Organization >> call, Throwable t) {

                    }
                });

            }

        });
    }
    public void getCompanys(final int id,final int pos) {

        new ThreadExecuter ().execute( new Runnable() {
            @Override
            public void run() {

                final OrganizationApi subCategoryAPI = Util.getClient().create( OrganizationApi.class);
                Call<ArrayList< Organization >> getProf = subCategoryAPI.getOrganizationById(id);
                //Call<ArrayList<Blogs>> getBlog = blogApi.getBlogs();

                getProf.enqueue(new Callback<ArrayList< Organization >>() {

                    @Override
                    public void onResponse( Call<ArrayList< Organization >> call, Response<ArrayList< Organization >> response) {

                        if (response.code() == 200||response.code() == 201||response.code() == 204&&response.body().size()!=0)
                        {
                            //mLoader.setVisibility(View.GONE);
                            Organization organization = response.body().get(0);
                            System.out.println("Inside api");
                            PreferenceHandler.getInstance( AdminNewMainScreen.this).setCompanyId(organization.getOrganizationId());
                            PreferenceHandler.getInstance( AdminNewMainScreen.this).setHeadOrganizationId(organization.getHeadOrganizationId());
                            PreferenceHandler.getInstance( AdminNewMainScreen.this).setCompanyName(organization.getOrganizationName());
                            PreferenceHandler.getInstance( AdminNewMainScreen.this).setAppType(organization.getAppType());

                            PreferenceHandler.getInstance( AdminNewMainScreen.this).setAppType(organization.getAppType());
                            PreferenceHandler.getInstance( AdminNewMainScreen.this).setLicenseStartDate(organization.getLicenseStartDate());
                            PreferenceHandler.getInstance( AdminNewMainScreen.this).setLicenseEndDate(organization.getLicenseEndDate());
                            PreferenceHandler.getInstance( AdminNewMainScreen.this).setSignupDate(organization.getSignupDate());
                            PreferenceHandler.getInstance( AdminNewMainScreen.this).setOrganizationLongi(organization.getLongitude());
                            PreferenceHandler.getInstance( AdminNewMainScreen.this).setOrganizationLati(organization.getLatitude());
                            PreferenceHandler.getInstance( AdminNewMainScreen.this).setPlanType(organization.getPlanType());
                            PreferenceHandler.getInstance( AdminNewMainScreen.this).setEmployeeLimit(organization.getEmployeeLimit());
                            PreferenceHandler.getInstance( AdminNewMainScreen.this).setPlanId(organization.getPlanId());
                            PreferenceHandler.getInstance( AdminNewMainScreen.this).setResellerUserId(organization.getResellerProfileId());

                            appType = PreferenceHandler.getInstance( AdminNewMainScreen.this).getAppType();
                            planType = PreferenceHandler.getInstance( AdminNewMainScreen.this).getPlanType();
                            licensesStartDate = PreferenceHandler.getInstance( AdminNewMainScreen.this).getLicenseStartDate();
                            licenseEndDate = PreferenceHandler.getInstance( AdminNewMainScreen.this).getLicenseEndDate();
                            planId = PreferenceHandler.getInstance( AdminNewMainScreen.this).getPlanId();

                            try{

                                Intent i = new Intent( AdminNewMainScreen.this, AdminNewMainScreen.class);  //your class
                                i.putExtra("OrgPos",pos);
                                i.putExtra("Dont",true);
                                startActivity(i);
                                finish();

                            }catch (Exception e){
                                e.printStackTrace();
                            }






                        }else{
                           // mLoader.setVisibility(View.GONE);

                        }
                    }

                    @Override
                    public void onFailure( Call<ArrayList< Organization >> call, Throwable t) {
                       // mLoader.setVisibility(View.GONE);
                    }
                });

            }

        });
    }

    public void popupUpgrade(final String text,final String days){

        try{

            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder( AdminNewMainScreen.this);
            LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View views = inflater.inflate(R.layout.app_upgrade_pop, null);

            builder.setView(views);

            final Button mPaid = views.findViewById(R.id.paid_version_upgrade);
            mPaid.setVisibility(View.GONE);
            final MyRegulerText mCompanyName = views.findViewById(R.id.company_name_upgrade);
            final MyRegulerText mText = views.findViewById(R.id.alert_message_upgrade);
            final MyRegulerText mDay = views.findViewById(R.id.day_count_upgrade);

            final androidx.appcompat.app.AlertDialog dialogs = builder.create();
            dialogs.show();
            dialogs.setCanceledOnTouchOutside(true);

            mCompanyName.setText("Dear "+PreferenceHandler.getInstance( AdminNewMainScreen.this).getCompanyName());
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
        String app_version = PreferenceHandler.getInstance( AdminNewMainScreen.this).getAppVersion();

        if(app_version!=null&&!app_version.isEmpty()){


            if(currentVersion.equalsIgnoreCase(app_version)){

            }else{
                final AlertDialog.Builder builder = new AlertDialog.Builder( AdminNewMainScreen.this);
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

    class GetVersionCode extends AsyncTask<Void, String, String> {

        @Override
        protected String doInBackground(Void... voids) {
            String newVersion = null;
            try {
                /*Connection connection = Jsoup.connect("https://play.google.com/store/apps/details?id=app.zingo.employeemanagements" + "&hl=en");
                Document document = connection.timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get();

                Elements versions = document.getElementsByClass("htlgb");

                for (int i = 0; i < versions.size(); i++) {
                    newVersion = versions.get(i).text();
                    if (Pattern.matches("^[0-9]{1}.[0-9]{1}.[0-9]{1}$", newVersion)) {
                        break;
                    }
                }*/

                newVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + AdminNewMainScreen.this.getPackageName() + "&hl=en")
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get()
                        .select(".hAyfc .htlgb")
                        .get(7)
                        .ownText();
                return newVersion;

            } catch (Exception e) {
                return newVersion;
            }

        }

        @Override
        protected void onPostExecute(String onlineVersion) {
            super.onPostExecute(onlineVersion);
            if (onlineVersion != null && !onlineVersion.isEmpty()) {
                System.out.println("Online Version==" + onlineVersion);
                System.out.println("Current Version==" + currentVersion);
                if (!onlineVersion.equalsIgnoreCase(currentVersion)) {
                    showUpdateDialog();
                } else {
                    // Toast.makeText(MainActivity.this, "Check", Toast.LENGTH_SHORT).show();
                    System.out.println("Check");
                }
            } else {
                System.out.println("Check out");
            }
        }


        private void showUpdateDialog() {
            final AlertDialog.Builder builder = new AlertDialog.Builder( AdminNewMainScreen.this);
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

    public void updateProfile(final Employee employee){



        new ThreadExecuter ().execute( new Runnable() {
            @Override
            public void run() {

                final EmployeeApi subCategoryAPI = Util.getClient().create( EmployeeApi.class);
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


                        //  Toast.makeText(ChangePasswordScreen.this, "Something went wrong due to "+"Bad Internet Connection", Toast.LENGTH_SHORT).show();

                    }
                });

            }

        });
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {


            case REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("TAG","@@@ PERMISSIONS grant");
                    boolean_permissions = false;

                } else {
                    Log.d("TAG","@@@ PERMISSIONS Denied");

                    boolean_permissions = false;

                    Toast.makeText(mContext, "Permission Required. So Please allow the permission", Toast.LENGTH_SHORT).show();
                }
            }
            break;
        }
    }

    public void getBranches(final int id,final int posi) {



        new ThreadExecuter ().execute( new Runnable() {
            @Override
            public void run() {

                final OrganizationApi orgApi = Util.getClient().create( OrganizationApi.class);
                Call<ArrayList< Organization >> getProf = orgApi.getBranchesByHeadOrganizationId(id);
                //Call<ArrayList<Blogs>> getBlog = blogApi.getBlogs();

                getProf.enqueue(new Callback<ArrayList< Organization >>() {

                    @Override
                    public void onResponse( Call<ArrayList< Organization >> call, Response<ArrayList< Organization >> response) {



                        if (response.code() == 200||response.code() == 201||response.code() == 204)
                        {
                           // mLoader.setVisibility(View.GONE);

                            ArrayList< Organization > branches = response.body();
                            organizationArrayList = response.body();
                            ArrayList<String> orgName = new ArrayList<>();

                            if(branches!=null&&branches.size()!=0){
                               // mLoader.setVisibility(View.GONE);
                                orgName.add("Select Branch");

                                for(int i=0;i<branches.size();i++){

                                    orgName.add(branches.get(i).getOrganizationName());
                                }

                                if(organizationArrayList!=null&&organizationArrayList.size()!=0){

                                    organizationName.setVisibility(View.VISIBLE);

                                    ArrayAdapter adapter = new ArrayAdapter<>( AdminNewMainScreen.this, R.layout.spinner_item_selected, orgName);
                                    adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

                                    // DepartmentSpinnerAdapter arrayAdapter = new DepartmentSpinnerAdapter(EmployeeSignUp.this, departmentData);
                                    organizationName.setAdapter(adapter);

                                    if(posi!=-1){
                                        organizationName.setSelection(spinnerPos);
                                    }else{
                                        organizationName.setSelection(0);
                                    }

                                }


                            }


                        }else{

                           // mLoader.setVisibility(View.GONE);

                            Toast.makeText( AdminNewMainScreen.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onFailure( Call<ArrayList< Organization >> call, Throwable t) {


                       // mLoader.setVisibility(View.GONE);

                        Toast.makeText( AdminNewMainScreen.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                    }
                });

            }

        });
    }

    void presentShowcaseView() {

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, SHOWCASE_ID_ADMIN);


        ShowcaseTooltip toolTip1 = ShowcaseTooltip.build(this)
                .corner(30)
                .textColor(Color.parseColor("#007686"))
                .text("This is a <b>Organization QR</b> code.<br><br>Click on QR code symbol, Scan employee QR code for marking employee's attendance & You can download your Organization QR Code.. <br><br>Tap anywhere to continue");


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


        ShowcaseTooltip toolTip2 = ShowcaseTooltip.build(this)
                .corner(30)
                .textColor(Color.parseColor("#007686"))
                .text("This is a <b>Share Icon</b>.<br><br>Click on this icon, share options will come and select email to Share details about this app with your Organization code to your employees<br><br>Tap anywhere to continue");

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(mShareLayout)
                        .setToolTip(toolTip2)
                        .setTooltipMargin(20)
                        .setShapePadding(30)
                        .setDismissOnTouch(true)
                        .setMaskColour(getResources().getColor(R.color.tooltip_mask))
                        .build()
        );



        sequence.start();


    }

    public void presentShowcaseSequence() throws Exception{

        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500); // half second between each showcase view

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, SHOWCASE_ID_ADMIN);

        sequence.setOnItemShownListener(new MaterialShowcaseSequence.OnSequenceItemShownListener() {
            @Override
            public void onShow(MaterialShowcaseView itemView, int position) {

            }
        });

        sequence.setConfig(config);

        sequence.addSequenceItem(mQrLayout, "This is a Organization QR code.\nClick on QR code symbol, Scan employee QR code for marking employee's attendance & You can download your Organization QR Code.. ", "GOT IT");

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setSkipText("SKIP")
                        .setTarget(mShareLayout)
                        .setDismissText("GOT IT")
                        .setContentText("This is a Share Icon .Click on this icon, share options will come and select email to Share details about this app with your Organization code to your employees")
                        .withRectangleShape(true)
                        .build()
        );

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setSkipText("SKIP")
                        .setTarget(mProfileImage)
                        .setDismissText("GOT IT")
                        .setContentText("Click here to Upload profile picture")
                        .withRectangleShape()
                        .build()
        );




        sequence.start();

        PreferenceHandler.getInstance( AdminNewMainScreen.this).setMainCheck(true);

    }

    private void initRFAB() {



    }

    @Override
    public void onRFACItemLabelClick(int position, RFACLabelItem item) {
        rfabHelper.toggleContent();
        if(position==0){
            departmentAlert();
        }else if(position==1){
            Intent branch = new Intent( AdminNewMainScreen.this,CreateEmployeeScreen.class);
            startActivity(branch);
        }else if(position==2){
            Intent task = new Intent( AdminNewMainScreen.this, EmployeeListScreen.class);
            task.putExtra("Type","Task");
            startActivity(task);
        }else if(position==3){
            Intent branch = new Intent( AdminNewMainScreen.this,CustomerCreation.class);
            startActivity(branch);
        }
    }

    @Override
    public void onRFACItemIconClick(int position, RFACLabelItem item) {
        rfabHelper.toggleContent();
        if(position==0){
            departmentAlert();
        }else if(position==1){
            Intent branch = new Intent( AdminNewMainScreen.this,CreateEmployeeScreen.class);
            startActivity(branch);

        }else if(position==2){
            Intent task = new Intent( AdminNewMainScreen.this, EmployeeListScreen.class);
            task.putExtra("Type","Task");
            startActivity(task);

        }else if(position==3){
            Intent branch = new Intent( AdminNewMainScreen.this,CustomerCreation.class);
            startActivity(branch);
        }
    }

    private void departmentAlert(){

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder( AdminNewMainScreen.this);
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View views = inflater.inflate(R.layout.custom_alert_box_department, null);

        builder.setView(views);
        builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                // Consumed
// Not consumed
                return keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP;
            }
        });

        final Button mSave = views.findViewById(R.id.save);
        final EditText desc = views.findViewById(R.id.department_description);
        final TextInputEditText mName = views.findViewById(R.id.department_name);


        final androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(true);

        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String name = mName.getText().toString();
                String descrp = desc.getText().toString();

                if(name.isEmpty()){

                    Toast.makeText( AdminNewMainScreen.this, "Please enter Department Name", Toast.LENGTH_SHORT).show();

                }else if (descrp.isEmpty()){

                    Toast.makeText( AdminNewMainScreen.this, "Please enter Department Description", Toast.LENGTH_SHORT).show();
                }else{

                    Departments departments = new Departments();
                    departments.setDepartmentName(name);
                    departments.setDepartmentDescription(descrp);
                    departments.setOrganizationId(PreferenceHandler.getInstance( AdminNewMainScreen.this).getCompanyId());

                    try {
                        addDepartments(departments,dialog);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


            }
        });

    }


    public void addDepartments(final Departments departments,final androidx.appcompat.app.AlertDialog dialogs) {


        final ProgressDialog dialog = new ProgressDialog( AdminNewMainScreen.this);
        dialog.setMessage("Saving Details..");
        dialog.setCancelable(false);
        dialog.show();

        DepartmentApi apiService = Util.getClient().create( DepartmentApi.class);

        Call<Departments> call = apiService.addDepartments(departments);

        call.enqueue(new Callback<Departments>() {
            @Override
            public void onResponse(Call<Departments> call, Response<Departments> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {
                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201) {

                        Departments s = response.body();

                        if(s!=null){

                            Toast.makeText( AdminNewMainScreen.this, "Department Creted Successfully ", Toast.LENGTH_SHORT).show();

                            dialogs.dismiss();




                        }




                    }else {
                        Toast.makeText( AdminNewMainScreen.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
            public void onFailure(Call<Departments> call, Throwable t) {

                if(dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
                Toast.makeText( AdminNewMainScreen.this , "Failed due to Bad Internet Connection" , Toast.LENGTH_SHORT ).show( );
                Log.e("TAG", t.toString());
            }
        });



    }

}
