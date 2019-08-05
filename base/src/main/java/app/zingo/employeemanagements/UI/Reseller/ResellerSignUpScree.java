package app.zingo.employeemanagements.UI.Reseller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import app.zingo.employeemanagements.Custom.MyEditText;
import app.zingo.employeemanagements.Custom.MyTextView;
import app.zingo.employeemanagements.Model.ResellerProfiles;
import app.zingo.employeemanagements.UI.LandingScreen;
import app.zingo.employeemanagements.Utils.PreferenceHandler;
import app.zingo.employeemanagements.Utils.ThreadExecuter;
import app.zingo.employeemanagements.Utils.Util;
import app.zingo.employeemanagements.WebApi.ResellerAPI;
import app.zingo.employeemanagements.base.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResellerSignUpScree extends AppCompatActivity {

    MyEditText mName,mUserName,mEmail,mMobile,mPassword,mConfirm;
    MyTextView mCity,mCountry;

    RadioButton mMale,mFemale,mOthers;
    MyTextView mCreate;

    CheckBox mShowPwd;

    public static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{

            setContentView(R.layout.activity_reseller_sign_up_scree);

            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Reseller Signup");


            mName = findViewById(R.id.reseller_name);
            mUserName = findViewById(R.id.reseller_user_name);
            mEmail = findViewById(R.id.reseller_email);
            mMobile = findViewById(R.id.reseller_mobile);
            mPassword = findViewById(R.id.reseller_password);
            mConfirm = findViewById(R.id.reseller_confirmpwd);
            mCity = findViewById(R.id.reseller_city);
            mCountry = findViewById(R.id.reseller_country);

            mMale = findViewById(R.id.reseller_male);
            mFemale = findViewById(R.id.reseller_female);
            mOthers = findViewById(R.id.reseller_other);

            mCreate = findViewById(R.id.createReseller);
            mShowPwd = findViewById(R.id.show_hide_password);

            mShowPwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton button,
                                             boolean isChecked) {

                    // If it is checked then show password else hide password
                    if (isChecked) {

                        mShowPwd.setText("Hide Password");// change checkbox text

                        mPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                        mPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());// show password

                        mConfirm.setInputType(InputType.TYPE_CLASS_TEXT);
                        mConfirm.setTransformationMethod(HideReturnsTransformationMethod.getInstance());// show password
                    } else {
                        mShowPwd.setText("Show Password");// change checkbox text

                        mPassword.setInputType(InputType.TYPE_CLASS_TEXT
                                | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        mPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());// hide password

                        mConfirm.setInputType(InputType.TYPE_CLASS_TEXT
                                | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        mConfirm.setTransformationMethod(PasswordTransformationMethod.getInstance());// hide password

                    }

                }
            });

            mCity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY/*MODE_FULLSCREEN*/)
                                        .build(ResellerSignUpScree.this);
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

            mCreate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    validate();
                }
            });



        }catch (Exception e){
            e.printStackTrace();
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        try{
            if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
                if (resultCode == RESULT_OK) {
                    Place place = PlaceAutocomplete.getPlace(this, data);
                    //System.out.println(place.getLatLng());


                    LatLng latLang = place.getLatLng();
                    double lat  = latLang.latitude;
                    double longi  = latLang.longitude;
                    try {
                        Geocoder geocoder = new Geocoder(ResellerSignUpScree.this);
                        List<Address> addresses = geocoder.getFromLocation(lat,longi,1);
                        System.out.println("addresses = "+addresses+"Place id"+place.getId());
                        mCity.setText(place.getName()+"");

                        mCountry.setText(addresses.get(0).getCountryName());



                    }
                    catch (IOException ex)
                    {
                        ex.printStackTrace();
                    }


                } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                    Status status = PlaceAutocomplete.getStatus(this, data);
                    // TODO: Handle the error.
                    Log.i("CreateCity", status.getStatusMessage());

                } else if (resultCode == RESULT_CANCELED) {
                    // The user canceled the operation.
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    public void validate(){
        String name = mName.getText().toString();
        String username = mUserName.getText().toString();
        String email = mEmail.getText().toString();
        String mobile = mMobile.getText().toString();
        String password = mPassword.getText().toString();
        String confirmPassword = mConfirm.getText().toString();
        String city = mCity.getText().toString();
        String country = mCountry.getText().toString();


        if(name==null||name.isEmpty()){

            Toast.makeText(ResellerSignUpScree.this, "Full name is required", Toast.LENGTH_SHORT).show();

        }else if(username==null||username.isEmpty()){

            Toast.makeText(ResellerSignUpScree.this, "User name is required", Toast.LENGTH_SHORT).show();

        }else if(email==null||email.isEmpty()){

            Toast.makeText(ResellerSignUpScree.this, "Email is required", Toast.LENGTH_SHORT).show();

        }else if(mobile==null||mobile.isEmpty()){

            Toast.makeText(ResellerSignUpScree.this, "Mobile number is required", Toast.LENGTH_SHORT).show();

        }else if(password==null||password.isEmpty()){

            Toast.makeText(ResellerSignUpScree.this, "Password is required", Toast.LENGTH_SHORT).show();

        }else if(confirmPassword==null||confirmPassword.isEmpty()){

            Toast.makeText(ResellerSignUpScree.this, "Confirm password is required", Toast.LENGTH_SHORT).show();

        }else if(city==null||city.isEmpty()){

            Toast.makeText(ResellerSignUpScree.this, "City is required", Toast.LENGTH_SHORT).show();

        }else if(country==null||country.isEmpty()){

            Toast.makeText(ResellerSignUpScree.this, "Country is required", Toast.LENGTH_SHORT).show();

        }else if(!password.isEmpty()&&!confirmPassword.isEmpty()&&!password.equals(confirmPassword)){

            Toast.makeText(this, "Confirm password should be same as Password", Toast.LENGTH_SHORT).show();
        }else if(!mMale.isChecked()&&!mFemale.isChecked()&&!mOthers.isChecked()){

            Toast.makeText(this, "Please Select Gender", Toast.LENGTH_SHORT).show();

        }else{

            ResellerProfiles rs = new ResellerProfiles();
            rs.setFullName(name);
            rs.setUserName(username);
            rs.setPassword(password);
            rs.setEmail(email);
            rs.setMobileNumber(mobile);
            rs.setCity(city);
            rs.setCountry(country);
            rs.setUserRoleId(8);
            rs.setStatus("Active");
            rs.setCommissionPercentage(10);

            if(mMale.isChecked()){
                rs.setGender("Male");
            }else if(mFemale.isChecked()){

                rs.setGender("Female");
            }else if(mOthers.isChecked()){

                rs.setGender("Others");
            }

            rs.setSignUpDate(new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new Date()));
            checkUserByEmailId(rs);
        }

    }


    public void addReseller(final ResellerProfiles rs){

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Saving Details..");
        dialog.setCancelable(false);
        dialog.show();

        ResellerAPI apiService = Util.getClient().create(ResellerAPI.class);

        Call<ResellerProfiles> call = apiService.addResellers(rs);

        call.enqueue(new Callback<ResellerProfiles>() {
            @Override
            public void onResponse(Call<ResellerProfiles> call, Response<ResellerProfiles> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {
                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201) {

                        ResellerProfiles s = response.body();

                        if(s!=null){

                            Toast.makeText(ResellerSignUpScree.this, "Account created Success fully", Toast.LENGTH_SHORT).show();


                            PreferenceHandler.getInstance(ResellerSignUpScree.this).clear();
                            PreferenceHandler.getInstance(ResellerSignUpScree.this).setResellerUserId(s.getResellerProfileId());

                            Intent i = new Intent(ResellerSignUpScree.this, LandingScreen.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                            finish();



                        }




                    }else {
                        Toast.makeText(ResellerSignUpScree.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
            public void onFailure(Call<ResellerProfiles> call, Throwable t) {

                if(dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
                Toast.makeText( ResellerSignUpScree.this , "Failed due to Bad Internet Connection" , Toast.LENGTH_SHORT ).show( );
                Log.e("TAG", t.toString());
            }
        });



    }

    private void checkUserByEmailId(final ResellerProfiles userProfile){



        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setTitle("Please wait..");
        dialog.show();


        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {


                ResellerAPI apiService =
                        Util.getClient().create(ResellerAPI.class);

                Call<ArrayList<ResellerProfiles>> call = apiService.getResellerByEmail(userProfile);

                call.enqueue(new Callback<ArrayList<ResellerProfiles>>() {
                    @Override
                    public void onResponse(Call<ArrayList<ResellerProfiles>> call, Response<ArrayList<ResellerProfiles>> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                        int statusCode = response.code();

                        if(dialog != null)
                        {
                            dialog.dismiss();
                        }

                        if(statusCode == 200 || statusCode == 204)
                        {

                            ArrayList<ResellerProfiles> responseProfile = response.body();
                            if(responseProfile != null && responseProfile.size()!=0 )
                            {

                                mEmail.setError("Email Exists");
                                Toast.makeText(ResellerSignUpScree.this, "Email already Exists", Toast.LENGTH_SHORT).show();


                            }
                            else
                            {
                                checkUserByPhone(userProfile);
                            }
                        }
                        else
                        {

                            Toast.makeText(ResellerSignUpScree.this,response.message(),Toast.LENGTH_SHORT).show();
                        }
//                callGetStartEnd();
                    }

                    @Override
                    public void onFailure(Call<ArrayList<ResellerProfiles>> call, Throwable t) {
                        // Log error here since request failed

                        if(dialog != null)
                        {
                            dialog.dismiss();
                        }

                        Log.e("TAG", t.toString());
                    }
                });
            }
        });
    }

    private void checkUserByPhone(final ResellerProfiles userProfile){

        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {


                ResellerAPI apiService =
                        Util.getClient().create(ResellerAPI.class);

                Call<ArrayList<ResellerProfiles>> call = apiService.getResellerByPhone(userProfile.getMobileNumber());

                call.enqueue(new Callback<ArrayList<ResellerProfiles>>() {
                    @Override
                    public void onResponse(Call<ArrayList<ResellerProfiles>> call, Response<ArrayList<ResellerProfiles>> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                        int statusCode = response.code();

                        if(statusCode == 200 || statusCode == 204)
                        {
                            ArrayList<ResellerProfiles> responseProfile = response.body();
                            if(responseProfile != null && responseProfile.size()!=0 )
                            {
                                mMobile.setError("Number Already Exists");
                                Toast.makeText(ResellerSignUpScree.this, "Mobile already Exists", Toast.LENGTH_SHORT).show();

                            }
                            else
                            {

                                try {
                                    checkUserByUserName(userProfile);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        else
                        {

                            Toast.makeText(ResellerSignUpScree.this,response.message(),Toast.LENGTH_SHORT).show();
                        }
//                callGetStartEnd();
                    }

                    @Override
                    public void onFailure(Call<ArrayList<ResellerProfiles>> call, Throwable t) {
                        // Log error here since request failed

                        Log.e("TAG", t.toString());
                    }
                });
            }
        });
    }

    private void checkUserByUserName(final ResellerProfiles userProfile){

        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {


                ResellerAPI apiService =
                        Util.getClient().create(ResellerAPI.class);

                Call<ArrayList<ResellerProfiles>> call = apiService.getResellerByUserName(userProfile.getUserName());

                call.enqueue(new Callback<ArrayList<ResellerProfiles>>() {
                    @Override
                    public void onResponse(Call<ArrayList<ResellerProfiles>> call, Response<ArrayList<ResellerProfiles>> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                        int statusCode = response.code();

                        if(statusCode == 200 || statusCode == 204)
                        {
                            ArrayList<ResellerProfiles> responseProfile = response.body();
                            if(responseProfile != null && responseProfile.size()!=0 )
                            {
                                mUserName.setError("User name Already Exists");
                                Toast.makeText(ResellerSignUpScree.this, "Mobile already Exists", Toast.LENGTH_SHORT).show();

                            }
                            else
                            {

                                try {
                                    addReseller(userProfile);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        else
                        {

                            Toast.makeText(ResellerSignUpScree.this,response.message(),Toast.LENGTH_SHORT).show();
                        }
//                callGetStartEnd();
                    }

                    @Override
                    public void onFailure(Call<ArrayList<ResellerProfiles>> call, Throwable t) {
                        // Log error here since request failed

                        Log.e("TAG", t.toString());
                    }
                });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id)
        {
            case android.R.id.home:

                ResellerSignUpScree.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
