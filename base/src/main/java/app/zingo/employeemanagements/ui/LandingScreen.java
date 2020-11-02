package app.zingo.employeemanagements.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import app.zingo.employeemanagements.Custom.MyEditText;
import app.zingo.employeemanagements.Custom.MyRegulerText;
import app.zingo.employeemanagements.model.Departments;
import app.zingo.employeemanagements.model.Employee;
import app.zingo.employeemanagements.model.Organization;
import app.zingo.employeemanagements.model.ResellerProfiles;
import app.zingo.employeemanagements.ui.landing.InternalServerErrorScreen;
import app.zingo.employeemanagements.ui.Login.ForgotPhoneVerfi;
import app.zingo.employeemanagements.ui.NewAdminDesigns.AdminNewMainScreen;
import app.zingo.employeemanagements.ui.newemployeedesign.EmployeeNewMainScreen;
import app.zingo.employeemanagements.ui.Reseller.ResellerMainActivity;
import app.zingo.employeemanagements.utils.Constants;
import app.zingo.employeemanagements.utils.PreferenceHandler;
import app.zingo.employeemanagements.utils.ProgressBarUtil;
import app.zingo.employeemanagements.utils.Util;
import app.zingo.employeemanagements.WebApi.DepartmentApi;
import app.zingo.employeemanagements.WebApi.EmployeeApi;
import app.zingo.employeemanagements.WebApi.OrganizationApi;
import app.zingo.employeemanagements.WebApi.ResellerAPI;
import app.zingo.employeemanagements.base.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LandingScreen extends AppCompatActivity {
    TextView mSupport, mForgot;
    MyEditText mEmail, mPassword;
    MyRegulerText mSignInButton, mGetStarted, mContactUs;
    CheckBox mShowPwd, mResellerSign;
    ProgressBarUtil progressBarUtil;

    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate ( savedInstanceState );
        try {
            setContentView ( R.layout.activity_landing_screen );
            progressBarUtil = new ProgressBarUtil ( LandingScreen.this );
            mSupport = findViewById ( R.id.landing_support );
            ImageView landing_top_image = findViewById ( R.id.landing_top_image );
            mEmail = findViewById ( R.id.landing_email );
            mPassword = findViewById ( R.id.landing_password );
            mSignInButton = findViewById ( R.id.buttonsignin );
            mGetStarted = findViewById ( R.id.button_get_started );
            mContactUs = findViewById ( R.id.button_contact_us );
            mShowPwd = findViewById ( R.id.show_hide_password );
            mResellerSign = findViewById ( R.id.reseller_sign_in );
            mForgot = findViewById ( R.id.forgot_pwd );

            Glide.with ( this ).load ( R.drawable.working_employee ).into ( new GlideDrawableImageViewTarget ( landing_top_image ) );

            mSupport.setOnClickListener ( view -> {

                Intent support = new Intent ( LandingScreen.this , SupportScreen.class );
                startActivity ( support );

            } );

            mEmail.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String password = Objects.requireNonNull ( mPassword.getText ( ) ).toString();
                    String email = Objects.requireNonNull ( mEmail.getText ( ) ).toString();

                    if(!password.isEmpty()&&!email.isEmpty()){
                        mSignInButton.setBackgroundResource(R.drawable.rounded_landing_button);
                        mSignInButton.setEnabled ( true );
                    }else{
                        mSignInButton.setBackgroundResource(R.drawable.not_enabled_button);
                        mSignInButton.setEnabled ( false );
                    }
                }

                @Override
                public void afterTextChanged( Editable s) {

                }
            });


            mPassword.addTextChangedListener(new TextWatcher () {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String password = Objects.requireNonNull ( mPassword.getText ( ) ).toString();
                    String email = Objects.requireNonNull ( mEmail.getText ( ) ).toString();

                    if(!password.isEmpty()&&!email.isEmpty()){
                        mSignInButton.setBackgroundResource(R.drawable.rounded_landing_button);
                        mSignInButton.setEnabled ( true );
                    }else{
                        mSignInButton.setBackgroundResource(R.drawable.not_enabled_button);
                        mSignInButton.setEnabled ( false );
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            mShowPwd.setOnCheckedChangeListener ( ( button , isChecked ) -> {
                // If it is checked then show password else hide password
                if ( isChecked ) {

                    mShowPwd.setText ( getResources ().getString( R.string.hide_password) );// change checkbox text
                    mPassword.setInputType ( InputType.TYPE_CLASS_TEXT );
                    mPassword.setTransformationMethod ( HideReturnsTransformationMethod.getInstance ( ) );// show password

                } else {

                    mShowPwd.setText ( getResources ().getString( R.string.show_password) );// change checkbox text
                    mPassword.setInputType ( InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD );
                    mPassword.setTransformationMethod ( PasswordTransformationMethod.getInstance ( ) );// hide password
                }
            });

            mForgot.setOnClickListener ( view -> {
                if ( mResellerSign.isChecked ( ) ) {
                    Intent support = new Intent ( LandingScreen.this , ForgotPhoneVerfi.class );
                    support.putExtra ( "Screen" , "Reseller" );
                    startActivity ( support );

                } else {
                    Intent support = new Intent ( LandingScreen.this , ForgotPhoneVerfi.class );
                    support.putExtra ( "Screen" , "Employee" );
                    startActivity ( support );
                }
            });

            mSignInButton.setOnClickListener ( view -> validateField ( ) );

            mGetStarted.setOnClickListener ( view -> {

                Intent started = new Intent ( LandingScreen.this , SignUpOptioins.class );
                startActivity ( started );

              /*  Intent started = new Intent( LandingScreen.this, PhoneVerificationScreen.class);
                PreferenceHandler.getInstance( LandingScreen.this).setSignUpType("Employee");
                started.putExtra("Screen","Employee");
                startActivity(started);*/

            } );

            mContactUs.setOnClickListener ( view -> {
                Intent contact = new Intent ( LandingScreen.this , ContactUsScreen.class );
                startActivity ( contact );
            });

        } catch ( Exception e ) {
            e.printStackTrace ( );
        }
    }


    public void validateField ( ) {
        String email = Objects.requireNonNull ( mEmail.getText ( ) ).toString ( );
        String password = Objects.requireNonNull ( mPassword.getText ( ) ).toString ( );

        if ( email.isEmpty ( ) ) {
            Toast.makeText ( this , "Username/Email is required" , Toast.LENGTH_SHORT ).show ( );
        } else if (  password.isEmpty ( ) ) {
            Toast.makeText ( this , "Password is required" , Toast.LENGTH_SHORT ).show ( );

        } else {
            if ( mResellerSign.isChecked ( ) ) {
                ResellerProfiles rs = new ResellerProfiles ( );
                rs.setUserName ( email );
                rs.setPassword ( password );
                loginReseller ( rs );

            } else {
                Employee employee = new Employee ( );
                employee.setPassword ( password );
                employee.setEmail ( email );
                loginEmployee ( employee );
            }
        }
    }

    private void loginReseller ( final ResellerProfiles p ) {


        progressBarUtil.showProgress ( "Checking Reseller Info..." );
        ResellerAPI apiService = Util.getClient ( ).create ( ResellerAPI.class );


        Call < ArrayList < ResellerProfiles > > call = apiService.getResellerProfilesforLogin ( p );

        call.enqueue ( new Callback < ArrayList < ResellerProfiles > > ( ) {
            @Override
            public void onResponse ( @NonNull Call < ArrayList < ResellerProfiles > > call , @NonNull Response < ArrayList < ResellerProfiles > > response ) {
                int statusCode = response.code ( );
                if(progressBarUtil!=null){
                    progressBarUtil.hideProgress ();
                }

                if ( statusCode == 200 || statusCode == 201 ) {

                    ArrayList < ResellerProfiles > dto1 = response.body ( );
                    if ( dto1 != null && dto1.size ( ) != 0 ) {
                        ResellerProfiles dto = dto1.get ( 0 );


                        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences ( LandingScreen.this );
                        SharedPreferences.Editor spe = sp.edit ( );
                        spe.putInt ( Constants.RESELLER_USER_ID , dto.getResellerProfileId ( ) );
                        PreferenceHandler.getInstance ( LandingScreen.this ).setResellerUserId ( dto.getResellerProfileId ( ) );

                        PreferenceHandler.getInstance ( LandingScreen.this ).setUserRoleUniqueID ( dto.getUserRoleId ( ) );
                        PreferenceHandler.getInstance ( LandingScreen.this ).setResellerName ( dto.getFullName ( ) );
                        PreferenceHandler.getInstance ( LandingScreen.this ).setResellerEmail ( dto.getEmail ( ) );

                        spe.putString ( "FullName" , dto.getFullName ( ) );
                        spe.putString ( "Password" , dto.getPassword ( ) );
                        spe.putString ( "Email" , dto.getEmail ( ) );
                        spe.putString ( "PhoneNumber" , dto.getMobileNumber ( ) );
                        spe.apply ( );

                        if ( dto.getStatus ( ).contains ( "Active" ) ) {

                            Intent i = new Intent ( LandingScreen.this , ResellerMainActivity.class );
                            i.putExtra ( "Profile" , dto );
                            startActivity ( i );
                            finish ( );


                        } else if ( dto.getStatus ( ).equalsIgnoreCase ( "Disabled" ) ) {
                            Toast.makeText ( LandingScreen.this , "Your Account is Disabled" , Toast.LENGTH_SHORT ).show ( );
                        } else {
                            Toast.makeText ( LandingScreen.this , "Your Account is not verified ." , Toast.LENGTH_SHORT ).show ( );
                        }


                    } else {

                        Toast.makeText ( LandingScreen.this , "Login credentials are wrong.." , Toast.LENGTH_SHORT ).show ( );

                    }
                } else {

                    Toast.makeText ( LandingScreen.this , "Login failed due to status code:" + statusCode , Toast.LENGTH_SHORT ).show ( );
                }
            }

            @Override
            public void onFailure ( @NonNull Call < ArrayList < ResellerProfiles > > call , @NonNull Throwable t ) {
                // Log error here since request failed
                if(progressBarUtil!=null){
                    progressBarUtil.hideProgress ();
                }
                Log.e ( "TAG" , t.toString ( ) );
            }
        } );
    }


    private void loginEmployee ( final Employee p ) {

        progressBarUtil.showProgress ( "Checking Profile Info..." );

        EmployeeApi apiService = Util.getClient ( ).create ( EmployeeApi.class );
        Call < ArrayList < Employee > > call = apiService.getEmployeeforLoginCustomApi ( p );

        call.enqueue ( new Callback < ArrayList < Employee > > ( ) {
            @Override
            public void onResponse ( @NonNull Call < ArrayList < Employee > > call , @NonNull Response < ArrayList < Employee > > response ) {
                int statusCode = response.code ( );
                if(progressBarUtil!=null){
                    progressBarUtil.hideProgress ();
                }

                if ( statusCode == 200 || statusCode == 201 ) {

                    ArrayList < Employee > dto1 = response.body ( );

                    if ( dto1 != null && dto1.size ( ) != 0 ) {
                        final Employee dto = dto1.get ( 0 );


                        if ( dto.getStatus ( ).contains ( "Active" ) ) {

                            if ( dto.isAppOpen ( ) ) {
                                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder ( LandingScreen.this );
                                LayoutInflater inflater = getLayoutInflater ( );
                                final ViewGroup nullParent = null;
                                View view = inflater.inflate ( R.layout.logout_alert ,nullParent );
                                Button agree = view.findViewById ( R.id.dialog_ok );


                                dialogBuilder.setView ( view );
                                final AlertDialog dialog = dialogBuilder.create ( );
                                dialog.setCancelable ( true );
                                dialog.show ( );

                                agree.setOnClickListener ( v -> {

                                    if (  dialog.isShowing ( ) ) {

                                        dialog.dismiss ( );
                                    }
                                    try {
                                        if(dto.getDepartment ()!=null){

                                            if(dto.getDepartment ().getOrganization ()!=null){
                                                Organization organization =dto.getDepartment ().getOrganization();

                                                String licenseEndDate = organization.getLicenseEndDate ( );
                                                SimpleDateFormat smdf = new SimpleDateFormat ( "MM/dd/yyyy" , Locale.US);

                                                try{

                                                    if ( ( smdf.parse ( licenseEndDate ).getTime ( ) < smdf.parse ( smdf.format ( new Date ( ) ) ).getTime ( ) ) ) {
                                                        if(progressBarUtil!=null){
                                                            progressBarUtil.hideProgress ();
                                                        }

                                                        if ( dto.getUserRoleId ( ) == 2 || dto.getUserRoleId ( ) == 9 ) {
                                                            popupUpgrade ( getResources ().getString( R.string.supscription_expired) , getResources ().getString( R.string.supscription_message_admin) ,dto);

                                                        } else {
                                                            popupUpgrade ( getResources ().getString( R.string.supscription_expired), getResources ().getString( R.string.subscription_message_employee),dto );

                                                        }

                                                    }else{
                                                        /*Set Organization Data*/
                                                        PreferenceHandler.getInstance ( LandingScreen.this ).setCompanyId ( organization.getOrganizationId ( ) );
                                                        PreferenceHandler.getInstance ( LandingScreen.this ).setHeadOrganizationId ( organization.getHeadOrganizationId ( ) );
                                                        PreferenceHandler.getInstance ( LandingScreen.this ).setCompanyName ( organization.getOrganizationName ( ) );
                                                        PreferenceHandler.getInstance ( LandingScreen.this ).setCompanyAddress ( organization.getAddress ( ) );
                                                        PreferenceHandler.getInstance ( LandingScreen.this ).setHeadName ( organization.getOrganizationName ( ) );
                                                        PreferenceHandler.getInstance ( LandingScreen.this ).setAppType ( organization.getAppType ( ) );
                                                        PreferenceHandler.getInstance ( LandingScreen.this ).setLocationLimit ( ( float ) organization.getLocationLimit ( ) );

                                                        PreferenceHandler.getInstance ( LandingScreen.this ).setLicenseStartDate ( organization.getLicenseStartDate ( ) );
                                                        PreferenceHandler.getInstance ( LandingScreen.this ).setCheckInTime ( organization.getPlaceId ( ) );
                                                        PreferenceHandler.getInstance ( LandingScreen.this ).setLicenseEndDate ( organization.getLicenseEndDate ( ) );
                                                        PreferenceHandler.getInstance ( LandingScreen.this ).setSignupDate ( organization.getSignupDate ( ) );
                                                        PreferenceHandler.getInstance ( LandingScreen.this ).setOrganizationLongi ( organization.getLongitude ( ) );
                                                        PreferenceHandler.getInstance ( LandingScreen.this ).setOrganizationLati ( organization.getLatitude ( ) );
                                                        PreferenceHandler.getInstance ( LandingScreen.this ).setPlanType ( organization.getPlanType ( ) );
                                                        PreferenceHandler.getInstance ( LandingScreen.this ).setEmployeeLimit ( organization.getEmployeeLimit ( ) );
                                                        PreferenceHandler.getInstance ( LandingScreen.this ).setPlanId ( organization.getPlanId ( ) );
                                                        PreferenceHandler.getInstance ( LandingScreen.this ).setResellerUserId ( organization.getResellerProfileId ( ) );
                                                        PreferenceHandler.getInstance ( LandingScreen.this ).setLogo ( organization.getDeductionType ( ) );

                                                        /*Set Employee Data*/
                                                        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences ( LandingScreen.this );
                                                        SharedPreferences.Editor spe = sp.edit ( );
                                                        spe.putInt ( Constants.USER_ID , dto.getEmployeeId ( ) );
                                                        PreferenceHandler.getInstance ( LandingScreen.this ).setUserId ( dto.getEmployeeId ( ) );
                                                        PreferenceHandler.getInstance ( LandingScreen.this ).setLocationOn ( dto.isLocationOn ( ) );
                                                        PreferenceHandler.getInstance ( LandingScreen.this ).setDataOn ( dto.isDataOn ( ) );
                                                        PreferenceHandler.getInstance ( LandingScreen.this ).setManagerId ( dto.getManagerId ( ) );
                                                        PreferenceHandler.getInstance ( LandingScreen.this ).setAddress ( dto.getAddress ( ) );
                                                        PreferenceHandler.getInstance ( LandingScreen.this ).setUserRoleUniqueID ( dto.getUserRoleId ( ) );
                                                        PreferenceHandler.getInstance ( LandingScreen.this ).setUserName ( dto.getEmployeeName ( ) );
                                                        PreferenceHandler.getInstance ( LandingScreen.this ).setUserEmail ( dto.getPrimaryEmailAddress ( ) );
                                                        PreferenceHandler.getInstance ( LandingScreen.this ).setUserFullName ( dto.getEmployeeName ( ) );
                                                        PreferenceHandler.getInstance ( LandingScreen.this ).setPhoneNumber ( dto.getPhoneNumber ( ) );
                                                        PreferenceHandler.getInstance ( LandingScreen.this ).setShftName ( "" + dto.getDeviceModel ( ) );
                                                        spe.putString ( "FullName" , dto.getEmployeeName ( ) );
                                                        spe.putString ( "Password" , dto.getPassword ( ) );
                                                        spe.putString ( "Email" , dto.getPrimaryEmailAddress ( ) );
                                                        spe.putString ( "PhoneNumber" , dto.getPhoneNumber ( ) );
                                                        spe.apply ( );

                                                        if(progressBarUtil!=null){
                                                            progressBarUtil.hideProgress ();
                                                        }

                                                        if ( PreferenceHandler.getInstance ( LandingScreen.this ).getUserRoleUniqueID ( ) == 2 || PreferenceHandler.getInstance ( LandingScreen.this ).getUserRoleUniqueID ( ) == 9 ) {

                                                            Intent i = new Intent ( LandingScreen.this , AdminNewMainScreen.class );
                                                            i.putExtra ( "Profile" , dto );
                                                            startActivity ( i );
                                                            finish ( );
                                                        } else {
                                                            Intent i = new Intent ( LandingScreen.this , EmployeeNewMainScreen.class );
                                                            i.putExtra ( "Profile" , dto );
                                                            i.putExtra ( "Organization" , organization );
                                                            startActivity ( i );
                                                            finish ( );
                                                        }

                                                    }

                                                }catch ( Exception e ){
                                                    e.printStackTrace ();
                                                    if(progressBarUtil!=null){
                                                        progressBarUtil.hideProgress ();
                                                    }

                                                }

                                            }else{
                                                getCompany (dto.getDepartment ().getOrganizationId ( ) , dto );
                                            }

                                        }else{
                                            getDepartment ( dto.getDepartmentId ( ) , dto );
                                        }

                                    } catch ( Exception e ) {
                                        e.printStackTrace ( );
                                        Intent i = new Intent ( LandingScreen.this , InternalServerErrorScreen.class );

                                        startActivity ( i );
                                    }
                                } );


                            } else {


                                try {

                                    if(dto.getDepartment ()!=null){

                                        if(dto.getDepartment ().getOrganization ()!=null){



                                            Organization organization =dto.getDepartment ().getOrganization();

                                            String licenseEndDate = organization.getLicenseEndDate ( );
                                            SimpleDateFormat smdf = new SimpleDateFormat ( "MM/dd/yyyy" , Locale.US);

                                            try{

                                                if ( ( smdf.parse ( licenseEndDate ).getTime ( ) < smdf.parse ( smdf.format ( new Date ( ) ) ).getTime ( ) ) ) {

                                                    if(progressBarUtil!=null){
                                                        progressBarUtil.hideProgress ();
                                                    }


                                                    if ( dto.getUserRoleId ( ) == 2 || dto.getUserRoleId ( ) == 9 ) {

                                                        popupUpgrade ( getResources ().getString( R.string.supscription_expired) , getResources ().getString( R.string.supscription_message_admin) ,dto);

                                                    } else {

                                                        popupUpgrade ( getResources ().getString( R.string.supscription_expired), getResources ().getString( R.string.subscription_message_employee),dto );

                                                    }

                                                }else{


                                                    /*Set Organization Data*/
                                                    PreferenceHandler.getInstance ( LandingScreen.this ).setCompanyId ( organization.getOrganizationId ( ) );
                                                    PreferenceHandler.getInstance ( LandingScreen.this ).setHeadOrganizationId ( organization.getHeadOrganizationId ( ) );
                                                    PreferenceHandler.getInstance ( LandingScreen.this ).setCompanyName ( organization.getOrganizationName ( ) );
                                                    PreferenceHandler.getInstance ( LandingScreen.this ).setCompanyAddress ( organization.getAddress ( ) );
                                                    PreferenceHandler.getInstance ( LandingScreen.this ).setHeadName ( organization.getOrganizationName ( ) );
                                                    PreferenceHandler.getInstance ( LandingScreen.this ).setAppType ( organization.getAppType ( ) );
                                                    PreferenceHandler.getInstance ( LandingScreen.this ).setLocationLimit ( ( float ) organization.getLocationLimit ( ) );


                                                    PreferenceHandler.getInstance ( LandingScreen.this ).setLicenseStartDate ( organization.getLicenseStartDate ( ) );
                                                    PreferenceHandler.getInstance ( LandingScreen.this ).setCheckInTime ( organization.getPlaceId ( ) );
                                                    PreferenceHandler.getInstance ( LandingScreen.this ).setLicenseEndDate ( organization.getLicenseEndDate ( ) );
                                                    PreferenceHandler.getInstance ( LandingScreen.this ).setSignupDate ( organization.getSignupDate ( ) );
                                                    PreferenceHandler.getInstance ( LandingScreen.this ).setOrganizationLongi ( organization.getLongitude ( ) );
                                                    PreferenceHandler.getInstance ( LandingScreen.this ).setOrganizationLati ( organization.getLatitude ( ) );
                                                    PreferenceHandler.getInstance ( LandingScreen.this ).setPlanType ( organization.getPlanType ( ) );
                                                    PreferenceHandler.getInstance ( LandingScreen.this ).setEmployeeLimit ( organization.getEmployeeLimit ( ) );
                                                    PreferenceHandler.getInstance ( LandingScreen.this ).setPlanId ( organization.getPlanId ( ) );
                                                    PreferenceHandler.getInstance ( LandingScreen.this ).setResellerUserId ( organization.getResellerProfileId ( ) );
                                                    PreferenceHandler.getInstance ( LandingScreen.this ).setLogo ( organization.getDeductionType ( ) );

                                                    /*Set Employee Data*/
                                                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences ( LandingScreen.this );
                                                    SharedPreferences.Editor spe = sp.edit ( );
                                                    spe.putInt ( Constants.USER_ID , dto.getEmployeeId ( ) );
                                                    PreferenceHandler.getInstance ( LandingScreen.this ).setUserId ( dto.getEmployeeId ( ) );
                                                    PreferenceHandler.getInstance ( LandingScreen.this ).setLocationOn ( dto.isLocationOn ( ) );
                                                    PreferenceHandler.getInstance ( LandingScreen.this ).setDataOn ( dto.isDataOn ( ) );
                                                    PreferenceHandler.getInstance ( LandingScreen.this ).setAddress ( dto.getAddress ( ) );
                                                    PreferenceHandler.getInstance ( LandingScreen.this ).setManagerId ( dto.getManagerId ( ) );
                                                    PreferenceHandler.getInstance ( LandingScreen.this ).setUserRoleUniqueID ( dto.getUserRoleId ( ) );
                                                    PreferenceHandler.getInstance ( LandingScreen.this ).setUserName ( dto.getEmployeeName ( ) );
                                                    PreferenceHandler.getInstance ( LandingScreen.this ).setUserEmail ( dto.getPrimaryEmailAddress ( ) );
                                                    PreferenceHandler.getInstance ( LandingScreen.this ).setUserFullName ( dto.getEmployeeName ( ) );
                                                    PreferenceHandler.getInstance ( LandingScreen.this ).setPhoneNumber ( dto.getPhoneNumber ( ) );
                                                    PreferenceHandler.getInstance ( LandingScreen.this ).setShftName ( "" + dto.getDeviceModel ( ) );
                                                    spe.putString ( "FullName" , dto.getEmployeeName ( ) );
                                                    spe.putString ( "Password" , dto.getPassword ( ) );
                                                    spe.putString ( "Email" , dto.getPrimaryEmailAddress ( ) );
                                                    spe.putString ( "PhoneNumber" , dto.getPhoneNumber ( ) );
                                                    spe.apply ( );

                                                    if(progressBarUtil!=null){
                                                        progressBarUtil.hideProgress ();
                                                    }

                                                    if ( PreferenceHandler.getInstance ( LandingScreen.this ).getUserRoleUniqueID ( ) == 2 || PreferenceHandler.getInstance ( LandingScreen.this ).getUserRoleUniqueID ( ) == 9 ) {

                                                        Intent i = new Intent ( LandingScreen.this , AdminNewMainScreen.class );
                                                        i.putExtra ( "Profile" , dto );
                                                        startActivity ( i );
                                                        finish ( );
                                                    } else {
                                                        Intent i = new Intent ( LandingScreen.this , EmployeeNewMainScreen.class );
                                                        i.putExtra ( "Profile" , dto );
                                                        i.putExtra ( "Organization" , organization );
                                                        startActivity ( i );
                                                        finish ( );
                                                    }

                                                }

                                            }catch ( Exception e ){
                                                e.printStackTrace ();
                                                if(progressBarUtil!=null){
                                                    progressBarUtil.hideProgress ();
                                                }

                                            }

                                        }else{
                                            getCompany (dto.getDepartment ().getOrganizationId ( ) , dto );
                                        }

                                    }else{
                                        getDepartment ( dto.getDepartmentId ( ) , dto );
                                    }
                                } catch ( Exception e ) {
                                    e.printStackTrace ( );
                                    Intent i = new Intent ( LandingScreen.this , InternalServerErrorScreen.class );

                                    startActivity ( i );
                                }

                            }


                        } else if ( dto.getStatus ( ).equalsIgnoreCase ( "Disabled" ) || dto.getStatus ( ).equalsIgnoreCase ( "Deactive" ) ) {
                            Toast.makeText ( LandingScreen.this , "Your Account is Disabled.Please contact your Admin" , Toast.LENGTH_SHORT ).show ( );
                        }


                    } else {

                        Toast.makeText ( LandingScreen.this , "Invalid credentials/Account is disabled.Please try again sometime or Contact Admin" , Toast.LENGTH_SHORT ).show ( );

                    }
                } else {

                    Toast.makeText ( LandingScreen.this , "Login failed due to status code:" + statusCode , Toast.LENGTH_SHORT ).show ( );
                }
            }

            @Override
            public void onFailure ( @NonNull Call < ArrayList < Employee > > call , @NonNull Throwable t ) {
                // Log error here since request failed
                if(progressBarUtil!=null){
                    progressBarUtil.hideProgress ();
                }
                Log.e ( "TAG" , t.toString ( ) );
            }
        } );

    }

    public void getDepartment ( final int id , final Employee dto ) {

        progressBarUtil.showProgress ( "Getting Profile Info..." );

        final DepartmentApi subCategoryAPI = Util.getClient ( ).create ( DepartmentApi.class );
        Call < Departments > getProf = subCategoryAPI.getDepartmentById ( id );
        //Call<ArrayList<Blogs>> getBlog = blogApi.getBlogs();

        getProf.enqueue ( new Callback < Departments > ( ) {

            @Override
            public void onResponse ( @NonNull Call < Departments > call , @NonNull Response < Departments > response ) {

                if ( response.code ( ) == 200 || response.code ( ) == 201 || response.code ( ) == 204 ) {

                    try {

                        if(response.body ()!=null){
                            getCompany ( response.body ( ).getOrganizationId ( ) , dto );
                        }else{
                            if(progressBarUtil!=null){
                                progressBarUtil.hideProgress ();
                            }
                        }



                    } catch ( Exception e ) {
                        if(progressBarUtil!=null){
                            progressBarUtil.hideProgress ();
                        }
                        e.printStackTrace ( );
                        Intent i = new Intent ( LandingScreen.this , InternalServerErrorScreen.class );
                        startActivity ( i );

                    }


                } else {
                    if(progressBarUtil!=null){
                        progressBarUtil.hideProgress ();
                    }
                    Intent i = new Intent ( LandingScreen.this , AdminNewMainScreen.class );
                    i.putExtra ( "Profile" , dto );
                    startActivity ( i );
                    finish ( );
                }
            }

            @Override
            public void onFailure ( @NonNull Call < Departments > call , @NonNull Throwable t ) {
                if(progressBarUtil!=null){
                    progressBarUtil.hideProgress ();
                }
            }
        } );
    }

    public void getCompany ( final int id , final Employee dto ) {

        final OrganizationApi subCategoryAPI = Util.getClient ( ).create ( OrganizationApi.class );
        Call < ArrayList < Organization > > getProf = subCategoryAPI.getOrganizationById ( id );

        getProf.enqueue ( new Callback < ArrayList < Organization > > ( ) {

            @Override
            public void onResponse ( @NonNull Call < ArrayList < Organization > > call , @NonNull Response < ArrayList < Organization > > response ) {

                if ( (response.code ( ) == 200 || response.code ( ) == 201 || response.code ( ) == 204) && response.body ( ) != null ) {


                    if(response.body ().size ()!=0){

                        Organization organization = response.body ( ).get ( 0 );

                        String licenseEndDate = organization.getLicenseEndDate ( );
                        SimpleDateFormat smdf = new SimpleDateFormat ( "MM/dd/yyyy" , Locale.US);

                        try{

                            if ( ( smdf.parse ( licenseEndDate ).getTime ( ) < smdf.parse ( smdf.format ( new Date ( ) ) ).getTime ( ) ) ) {

                                if(progressBarUtil!=null){
                                    progressBarUtil.hideProgress ();
                                }


                                if ( dto.getUserRoleId ( ) == 2 || dto.getUserRoleId ( ) == 9 ) {

                                    popupUpgrade ( getResources ().getString( R.string.supscription_expired) , getResources ().getString( R.string.supscription_message_admin) ,dto);

                                } else {

                                    popupUpgrade ( getResources ().getString( R.string.supscription_expired), getResources ().getString( R.string.subscription_message_employee),dto );

                                }

                            }else{


                                /*Set Organization Data*/
                                PreferenceHandler.getInstance ( LandingScreen.this ).setCompanyId ( organization.getOrganizationId ( ) );
                                PreferenceHandler.getInstance ( LandingScreen.this ).setHeadOrganizationId ( organization.getHeadOrganizationId ( ) );
                                PreferenceHandler.getInstance ( LandingScreen.this ).setCompanyName ( organization.getOrganizationName ( ) );
                                PreferenceHandler.getInstance ( LandingScreen.this ).setCompanyAddress ( organization.getAddress ( ) );
                                PreferenceHandler.getInstance ( LandingScreen.this ).setHeadName ( organization.getOrganizationName ( ) );
                                PreferenceHandler.getInstance ( LandingScreen.this ).setAppType ( organization.getAppType ( ) );
                                PreferenceHandler.getInstance ( LandingScreen.this ).setLocationLimit ( ( float ) organization.getLocationLimit ( ) );


                                PreferenceHandler.getInstance ( LandingScreen.this ).setLicenseStartDate ( organization.getLicenseStartDate ( ) );
                                PreferenceHandler.getInstance ( LandingScreen.this ).setCheckInTime ( organization.getPlaceId ( ) );
                                PreferenceHandler.getInstance ( LandingScreen.this ).setLicenseEndDate ( organization.getLicenseEndDate ( ) );
                                PreferenceHandler.getInstance ( LandingScreen.this ).setSignupDate ( organization.getSignupDate ( ) );
                                PreferenceHandler.getInstance ( LandingScreen.this ).setOrganizationLongi ( organization.getLongitude ( ) );
                                PreferenceHandler.getInstance ( LandingScreen.this ).setOrganizationLati ( organization.getLatitude ( ) );
                                PreferenceHandler.getInstance ( LandingScreen.this ).setPlanType ( organization.getPlanType ( ) );
                                PreferenceHandler.getInstance ( LandingScreen.this ).setEmployeeLimit ( organization.getEmployeeLimit ( ) );
                                PreferenceHandler.getInstance ( LandingScreen.this ).setPlanId ( organization.getPlanId ( ) );
                                PreferenceHandler.getInstance ( LandingScreen.this ).setResellerUserId ( organization.getResellerProfileId ( ) );
                                PreferenceHandler.getInstance ( LandingScreen.this ).setLogo ( organization.getDeductionType ( ) );

                                /*Set Employee Data*/
                                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences ( LandingScreen.this );
                                SharedPreferences.Editor spe = sp.edit ( );
                                spe.putInt ( Constants.USER_ID , dto.getEmployeeId ( ) );
                                PreferenceHandler.getInstance ( LandingScreen.this ).setAddress ( dto.getAddress ( ) );
                                PreferenceHandler.getInstance ( LandingScreen.this ).setUserId ( dto.getEmployeeId ( ) );
                                PreferenceHandler.getInstance ( LandingScreen.this ).setLocationOn ( dto.isLocationOn ( ) );
                                PreferenceHandler.getInstance ( LandingScreen.this ).setDataOn ( dto.isDataOn ( ) );
                                PreferenceHandler.getInstance ( LandingScreen.this ).setManagerId ( dto.getManagerId ( ) );
                                PreferenceHandler.getInstance ( LandingScreen.this ).setUserRoleUniqueID ( dto.getUserRoleId ( ) );
                                PreferenceHandler.getInstance ( LandingScreen.this ).setUserName ( dto.getEmployeeName ( ) );
                                PreferenceHandler.getInstance ( LandingScreen.this ).setUserEmail ( dto.getPrimaryEmailAddress ( ) );
                                PreferenceHandler.getInstance ( LandingScreen.this ).setUserFullName ( dto.getEmployeeName ( ) );
                                PreferenceHandler.getInstance ( LandingScreen.this ).setPhoneNumber ( dto.getPhoneNumber ( ) );
                                PreferenceHandler.getInstance ( LandingScreen.this ).setShftName ( "" + dto.getDeviceModel ( ) );
                                spe.putString ( "FullName" , dto.getEmployeeName ( ) );
                                spe.putString ( "Password" , dto.getPassword ( ) );
                                spe.putString ( "Email" , dto.getPrimaryEmailAddress ( ) );
                                spe.putString ( "PhoneNumber" , dto.getPhoneNumber ( ) );
                                spe.apply ( );

                                if(progressBarUtil!=null){
                                    progressBarUtil.hideProgress ();
                                }

                                if ( PreferenceHandler.getInstance ( LandingScreen.this ).getUserRoleUniqueID ( ) == 2 || PreferenceHandler.getInstance ( LandingScreen.this ).getUserRoleUniqueID ( ) == 9 ) {

                                    Intent i = new Intent ( LandingScreen.this , AdminNewMainScreen.class );
                                    i.putExtra ( "Profile" , dto );
                                    startActivity ( i );
                                    finish ( );
                                } else {
                                    Intent i = new Intent ( LandingScreen.this , EmployeeNewMainScreen.class );
                                    i.putExtra ( "Profile" , dto );
                                    i.putExtra ( "Organization" , organization );
                                    startActivity ( i );
                                    finish ( );
                                }

                            }

                        }catch ( Exception e ){
                            e.printStackTrace ();
                            if(progressBarUtil!=null){
                                progressBarUtil.hideProgress ();
                            }

                        }

                    }else{
                        if(progressBarUtil!=null){
                            progressBarUtil.hideProgress ();
                        }

                    }

                } else {
                    if(progressBarUtil!=null){
                        progressBarUtil.hideProgress ();
                    }
                    if ( dto.getUserRoleId () == 2 || dto.getUserRoleId () == 9 ) {
                        Intent i = new Intent ( LandingScreen.this , AdminNewMainScreen.class );
                        i.putExtra ( "Profile" , dto );
                        startActivity ( i );
                        finish ( );
                    } else {
                        Intent i = new Intent ( LandingScreen.this , EmployeeNewMainScreen.class );
                        i.putExtra ( "Profile" , dto );
                        startActivity ( i );
                        finish ( );
                    }
                }
            }

            @Override
            public void onFailure ( @NonNull Call < ArrayList < Organization > > call , @NonNull Throwable t ) {
                if(progressBarUtil!=null){
                    progressBarUtil.hideProgress ();
                }
            }
        } );

    }

    public void popupUpgrade ( final String text , final String days ,final Employee dto) {

        try {

            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder ( LandingScreen.this );
            LayoutInflater inflater = ( LayoutInflater ) getSystemService ( Context.LAYOUT_INFLATER_SERVICE );
            final ViewGroup nullParent = null;
            View views = inflater.inflate ( R.layout.app_upgrade_pop , nullParent );
            builder.setView ( views );

            final Button mPaid = views.findViewById ( R.id.paid_version_upgrade );
            final MyRegulerText mCompanyName = views.findViewById ( R.id.company_name_upgrade );
            final MyRegulerText mText = views.findViewById ( R.id.alert_message_upgrade );
            final MyRegulerText mDay = views.findViewById ( R.id.day_count_upgrade );

            final androidx.appcompat.app.AlertDialog dialogs = builder.create ( );
            dialogs.show ( );
            dialogs.setCanceledOnTouchOutside ( true );

            mCompanyName.setText ( getResources ().getString( R.string.dear_user));
            mText.setText (text );
            mDay.setText ( days );


            if ( dto.getUserRoleId () == 2 || dto.getUserRoleId () == 9 ) {

                mPaid.setVisibility ( View.VISIBLE );
            } else {

                mPaid.setVisibility ( View.GONE );

            }
            mPaid.setOnClickListener ( view -> {

                if ( dialogs.isShowing ( ) ) {

                    dialogs.dismiss ( );
                }

            } );


        } catch ( Exception e ) {
            e.printStackTrace ( );
        }

    }




}
