package app.zingo.employeemanagements.UI;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.itextpdf.text.Utilities;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import app.zingo.employeemanagements.Adapter.EmployeeAdapter;
import app.zingo.employeemanagements.Custom.MyEditText;
import app.zingo.employeemanagements.Custom.MyTextView;
import app.zingo.employeemanagements.Model.Departments;
import app.zingo.employeemanagements.Model.Employee;
import app.zingo.employeemanagements.Model.Meetings;
import app.zingo.employeemanagements.Model.Organization;
import app.zingo.employeemanagements.Model.OrganizationPayments;
import app.zingo.employeemanagements.Model.Plans;
import app.zingo.employeemanagements.Model.ResellerProfiles;
import app.zingo.employeemanagements.base.R;
import app.zingo.employeemanagements.UI.Admin.CreatePaySlip;
import app.zingo.employeemanagements.UI.Admin.EmployeesDashBoard;
import app.zingo.employeemanagements.UI.Company.CreateCompany;
import app.zingo.employeemanagements.UI.Company.CreateFounderScreen;
import app.zingo.employeemanagements.UI.Employee.DashBoardEmployee;
import app.zingo.employeemanagements.UI.Employee.EmployeeMeetingHost;
import app.zingo.employeemanagements.UI.Landing.PhoneVerificationScreen;
import app.zingo.employeemanagements.UI.NewEmployeeDesign.EmployeeNewMainScreen;
import app.zingo.employeemanagements.UI.Reseller.ResellerMainActivity;
import app.zingo.employeemanagements.UI.Reseller.ResellerSignUpScree;
import app.zingo.employeemanagements.Utils.PreferenceHandler;
import app.zingo.employeemanagements.Utils.ThreadExecuter;
import app.zingo.employeemanagements.Utils.TrackGPS;
import app.zingo.employeemanagements.Utils.Util;
import app.zingo.employeemanagements.WebApi.DepartmentApi;
import app.zingo.employeemanagements.WebApi.MeetingsAPI;
import app.zingo.employeemanagements.WebApi.OrganizationApi;
import app.zingo.employeemanagements.WebApi.OrganizationPaymentAPI;
import app.zingo.employeemanagements.WebApi.PlansAndRatesAPI;
import app.zingo.employeemanagements.WebApi.ResellerAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.text.TextUtils.isEmpty;
import static java.security.AccessController.getContext;

public class GetStartedScreen extends AppCompatActivity  implements PaymentResultListener , GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener{

    boolean popUp = false;
    String appType = "Trial",planType="",paymentId="";
    int addtionalDay = 0,planId=0;
    double price = 0,resellerPercentage=0;


    RecyclerView mPlanList;
    LinearLayout mPlanLayout;//mEmailExtnLay
    MyTextView myLocation;
    ImageView mAddEmail,mDeleteEmail;
    MyEditText mOrganizationName,mBuildYear,mNoEmployee,mWebsite,mResellerCode;//mEmailExt
   // MyTextView mCity,mState;
    //TextInputEditText mOrganizationName,mCity,mState,mBuildYear,mNoEmployee,mWebsite;
    //TextInputEditText ;
    MyEditText mAbout,mAddress;
    MyTextView mCreate;


    public static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    String country,placeId;
    Organization organization;

    TrackGPS gps;
    double latitude;
    double longitude;

    int year = 0;


    //PaymentGateway
    private static final String TAG = GetStartedScreen.class.getSimpleName();

    //Google Api Locatin
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mLocationClient;
    Location currentLocation;

    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 10000;  /* 15 secs */
    private long FASTEST_INTERVAL = 5000; /* 5 secs */

    private static final int REQUEST_PERMISSIONS = 100;
    boolean boolean_permission;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
            setContentView(R.layout.activity_new_company_create);

            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Create Organization");
            popupOne();

            mPlanList = findViewById(R.id.plans);
            mPlanLayout = findViewById(R.id.plan_layout);
           // mEmailExtnLay = (LinearLayout) findViewById(R.id.add_email_organization);
            mAddEmail = findViewById(R.id.add_mail);
            mDeleteEmail = findViewById(R.id.delete_mail);
            mPlanLayout.setVisibility(View.GONE);
            getPlans();
            gps = new TrackGPS(GetStartedScreen.this);

            fn_permission();
            if (boolean_permission){

                if (mLocationClient == null) {



                    mLocationClient = new GoogleApiClient.Builder(GetStartedScreen.this)
                            .addConnectionCallbacks(this)
                            .addOnConnectionFailedListener(this)
                            .addApi(LocationServices.API)
                            .build();
                }

            }


            String currentYear = new SimpleDateFormat("yyyyy").format(new Date());

            year = Integer.parseInt(currentYear);

            mOrganizationName = findViewById(R.id.name);
          //  mEmailExt = (MyEditText)findViewById(R.id.org_email);
           /* mCity = findViewById(R.id.city);
            mState = findViewById(R.id.state);*/
            mBuildYear = findViewById(R.id.build);
            mWebsite = findViewById(R.id.website);
            mResellerCode = findViewById(R.id.reseller_code);
            mNoEmployee = findViewById(R.id.employee_count);

            mAbout = findViewById(R.id.about);
            mAddress = findViewById(R.id.address);
            myLocation = findViewById(R.id.my_location);

            mCreate = findViewById(R.id.createCompany);

           // onAddField();

            /*mCity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent =
                                new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY*//*MODE_FULLSCREEN*//*)
                                        .build(GetStartedScreen.this);
                        startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                    } catch (GooglePlayServicesRepairableException e) {
                        e.printStackTrace();
                        // TODO: Handle the error.
                    } catch (GooglePlayServicesNotAvailableException e) {
                        // TODO: Handle the error.
                        e.printStackTrace();
                    }
                }
            });*/

            mCreate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try{
                        validate();
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            });

            myLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (locationCheck()) {

                        if (currentLocation != null) {

                            latitude = currentLocation.getLatitude();
                            longitude = currentLocation.getLongitude();
                            getAddress(latitude,longitude);

                        }

                    }

                    /*if(gps.canGetLocation())
                    {
                        System.out.println(gps.getLatitude()+" = "+gps.getLongitude());
                        latitude = gps.getLatitude();
                        longitude = gps.getLongitude();
                        getAddress(latitude,longitude);
                    }
                    else
                    {
                        Toast.makeText(GetStartedScreen.this, "Couldn't get the location. Make sure location is enabled on the device", Toast.LENGTH_SHORT).show();
                    }*/
                }
            });


        /*    mAddEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    onAddField();
                }
            });

            mDeleteEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    removeView();
                }
            });*/

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
                            mBuildYear.setError("Enter valid year");
                            Toast.makeText(GetStartedScreen.this, "Please enter valid Build Year", Toast.LENGTH_SHORT).show();
                        }
                    }



                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });


        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //Show popup
    public void popupOne(){

        try{
            popUp = true;
            AlertDialog.Builder builder = new AlertDialog.Builder(GetStartedScreen.this);
            LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View views = inflater.inflate(R.layout.info_get_started, null);

            builder.setView(views);
           /* builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {

                        return true; // Consumed
                    }
                    else {
                        return false; // Not consumed
                    }
                }
            });*/
            final Button mPaid = views.findViewById(R.id.paid_version);
            final Button mTrial = views.findViewById(R.id.trial_version);
            final AlertDialog dialogs = builder.create();
            dialogs.show();
            dialogs.setCanceledOnTouchOutside(false);



            mPaid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    dialogs.dismiss();
                    popUp = false;
                    appType = "Paid";
                    mPlanLayout.setVisibility(View.VISIBLE);
                }
            });

            mTrial.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    dialogs.dismiss();
                    popUp = false;
                    appType = "Trial";
                    mPlanLayout.setVisibility(View.GONE);

                }
            });







        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void getPlans(){


        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {
                PlansAndRatesAPI apiService = Util.getClient().create(PlansAndRatesAPI.class);
                Call<ArrayList<Plans>> call = apiService.getPlans();

                call.enqueue(new Callback<ArrayList<Plans>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Plans>> call, Response<ArrayList<Plans>> response) {
                        int statusCode = response.code();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {



                            ArrayList<Plans> list = response.body();


                            if (list !=null && list.size()!=0) {

                                PlanAdapter adapter = new PlanAdapter(GetStartedScreen.this,list);
                                mPlanList.setAdapter(adapter);

                            }else{

                            }

                        }else {


                            Toast.makeText(GetStartedScreen.this, "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Plans>> call, Throwable t) {
                        // Log error here since request failed

                        Log.e("TAG", t.toString());
                    }
                });
            }


        });
    }


    //RecyclerView Adapter
    public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.ViewHolder>{

        private Context context;
        private ArrayList<Plans> list;


        public PlanAdapter(Context context, ArrayList<Plans> list) {

            this.context = context;
            this.list = list;


        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.adapter_plans_prices, parent, false);
            ViewHolder viewHolder = new ViewHolder(v);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final Plans dto = list.get(position);

            if(dto!=null){

                try{
                    holder.mPlanName.setText(dto.getPlanName());


                    if(dto.getRatesList()!=null&&dto.getRatesList().size()>=3){

                        holder.mRate1.setText(""+dto.getRatesList().get(0).getPrice());
                        holder.mRate2.setText(""+dto.getRatesList().get(1).getPrice());
                        holder.mRate3.setText(""+dto.getRatesList().get(2).getPrice());

                        holder.mPrice1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                            @Override
                            public void onCheckedChanged(CompoundButton button,
                                                         boolean isChecked) {

                                // If it is checked then show password else hide password
                                if (isChecked) {

                                    holder.mPrice2.setChecked(false);
                                    holder.mPrice3.setChecked(false);
                                    planType = dto.getPlanName()+","+dto.getRatesList().get(0).getRatesId();
                                    addtionalDay = dto.getRatesList().get(0).getDuration();
                                    price = dto.getRatesList().get(0).getPrice()*3;
                                    planId = dto.getPlansId();

                                } else {


                                }

                            }
                        });

                        holder.mPrice2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                            @Override
                            public void onCheckedChanged(CompoundButton button,
                                                         boolean isChecked) {

                                // If it is checked then show password else hide password
                                if (isChecked) {
                                    holder.mPrice1.setChecked(false);
                                    holder.mPrice3.setChecked(false);
                                    planType = dto.getPlanName()+","+dto.getRatesList().get(1).getRatesId();
                                    addtionalDay = dto.getRatesList().get(1).getDuration();
                                    planId = dto.getPlansId();
                                    price = dto.getRatesList().get(1).getPrice()*6;

                                } else {


                                }

                            }
                        });

                        holder.mPrice3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                            @Override
                            public void onCheckedChanged(CompoundButton button,
                                                         boolean isChecked) {

                                // If it is checked then show password else hide password
                                if (isChecked) {
                                    holder.mPrice1.setChecked(false);
                                    holder.mPrice2.setChecked(false);
                                    planType = dto.getPlanName()+","+dto.getRatesList().get(2).getRatesId();
                                    addtionalDay = dto.getRatesList().get(2).getDuration();
                                    planId = dto.getPlansId();
                                    price = dto.getRatesList().get(2).getPrice()*12;

                                } else {


                                }

                            }
                        });

                    }else{

                        holder.mPrice1.setClickable(false);
                        holder.mPrice2.setClickable(false);
                        holder.mPrice3.setClickable(false);
                    }



                }catch (Exception e){
                    e.printStackTrace();
                }
            }

        }




        @Override
        public int getItemCount() {
            return list.size();
        }


        class ViewHolder extends RecyclerView.ViewHolder /*implements View.OnClickListener*/ {

            public TextView mPlanName,mRate1,mRate2,mRate3;
            public CheckBox mPrice1,mPrice2,mPrice3;

            public ViewHolder(View itemView) {
                super(itemView);
                itemView.setClickable(true);

                mPlanName = itemView.findViewById(R.id.plan_name);
                mRate1 = itemView.findViewById(R.id.rate_price1);
                mRate2 = itemView.findViewById(R.id.rate_price2);
                mRate3 = itemView.findViewById(R.id.rate_price3);
                mPrice1 = itemView.findViewById(R.id.rate_check1);
                mPrice2 = itemView.findViewById(R.id.rate_check2);
                mPrice3 = itemView.findViewById(R.id.rate_check3);



            }
        }
    }
    public void validate() {

        String company =mOrganizationName.getText().toString();
      //  String mail =mEmailExt.getText().toString();
        String about = mAbout.getText().toString();
        String address = mAddress.getText().toString();
       /* String city = mCity.getText().toString();
        String state = mState.getText().toString();*/
        String build = mBuildYear.getText().toString();
        String web = mWebsite.getText().toString();
        String employeeCount = mNoEmployee.getText().toString();
        //boolean value = checkcondition();

        if(company.isEmpty()){

            Toast.makeText(GetStartedScreen.this, "Organization Name required", Toast.LENGTH_SHORT).show();

        }else if(address.isEmpty()){

            Toast.makeText(GetStartedScreen.this, "Address required", Toast.LENGTH_SHORT).show();

        }/*else if(about.isEmpty()){

            Toast.makeText(GetStartedScreen.this, "About Organization Name required", Toast.LENGTH_SHORT).show();

        }*//*else if(mail.isEmpty()){

            Toast.makeText(GetStartedScreen.this, "Organization email extension required", Toast.LENGTH_SHORT).show();

        }*//*else if(city.isEmpty()){

            Toast.makeText(GetStartedScreen.this, "City required", Toast.LENGTH_SHORT).show();

        }else if(state.isEmpty()){

            Toast.makeText(GetStartedScreen.this, "State required", Toast.LENGTH_SHORT).show();

        }*//*else if(build.isEmpty()){

            Toast.makeText(GetStartedScreen.this, "Build year required", Toast.LENGTH_SHORT).show();

        }*//*else if(web.isEmpty()){

            Toast.makeText(GetStartedScreen.this, "Websites required", Toast.LENGTH_SHORT).show();

        }*/else if(employeeCount.isEmpty()){

            Toast.makeText(GetStartedScreen.this, "Employee Nos required", Toast.LENGTH_SHORT).show();

        }else{
            //String message = mail+"@"+"gmail.com";

           /* if(checkcondition()){
                int i = mEmailExtnLay.getChildCount();

                for (int j = 0; j < i; j++) {
                    EditText editText = (EditText) mEmailExtnLay.getChildAt(j);

                    //System.out.println();
                    String email = editText.getText().toString()+"@";


                    message = message+email;
                }
            }*/

            LatLng latLng = convertAddressToLatLang(address+country);

            if(latLng!=null){

                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                SimpleDateFormat sdfs = new SimpleDateFormat("MM/dd/yyyy");
                organization = new Organization();
                organization.setOrganizationName(company);

                organization.setAddress(address);

                if(about==null||about.isEmpty()){
                    organization.setAboutUs("");
                }else{
                    organization.setAboutUs(about);
                }
                /*organization.setCity(city);
                organization.setState(state);*/

                if(build!=null&&!build.isEmpty()){
                    organization.setBuiltYear(build);
                }

                if(web!=null&&!web.isEmpty()){
                    organization.setWebsite(web);
                }


                organization.setEmployeeLimit(Integer.parseInt(employeeCount));
                organization.setLatitude(String.valueOf(latLng.latitude));
                organization.setLongitude(String.valueOf(latLng.longitude));
                organization.setSignupDate(sdf.format(new Date()));
                organization.setAppType(appType);
                organization.setLicenseStartDate(sdfs.format(new Date()));

                Departments departments = new Departments();
                departments.setDepartmentName("Founders");
                departments.setDepartmentDescription("The owner or operator of a foundry");

                ArrayList<Departments> depList = new ArrayList<>();
                depList.add(departments);

                organization.setDepartment(depList);

                String resellerCode = mResellerCode.getText().toString();

                if(resellerCode!=null&&!resellerCode.isEmpty()){

                    organization.setReferralCodeOfReseller(resellerCode);
                }
                if(placeId!=null){
                    organization.setPlaceId(placeId);
                }
                //organization.setLocation(message);
                Calendar c = Calendar.getInstance();
                c.setTime(new Date());

                if(appType!=null&&appType.equalsIgnoreCase("Paid")){

                    //organization.s

                    if(planId!=0&&!planType.isEmpty()){
                        organization.setPlanType(planType);
                        c.add(Calendar.DATE, addtionalDay);
                        organization.setPlanId(planId);

                        // convert calendar to date
                        Date additionalDate = c.getTime();
                        organization.setLicenseEndDate(sdfs.format(additionalDate));

                        if(resellerCode!=null&&!resellerCode.isEmpty()){
                            String code = resellerCode.replaceAll("[^0-9]", "");
                            getProfiles(Integer.parseInt(code),organization,resellerCode);
                        }else{
                            startPayment();
                        }



                    }else{
                        Toast.makeText(this, "Please Choose your plan", Toast.LENGTH_SHORT).show();
                    }

                }else{


                    organization.setPlanType("Trial");
                    c.add(Calendar.DATE, 30);

                    // convert calendar to date
                    Date additionalDate = c.getTime();
                    organization.setLicenseEndDate(sdfs.format(additionalDate));

                    if(resellerCode!=null&&!resellerCode.isEmpty()){
                        String code = resellerCode.replaceAll("[^0-9]", "");
                        getProfile(Integer.parseInt(code),organization,resellerCode);
                    }else{
                        addOrganization(organization,null);
                    }



                }




            }else{

                Toast.makeText(this, "Something went wrong.Please try again Later", Toast.LENGTH_SHORT).show();
            }



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
                        Geocoder geocoder = new Geocoder(GetStartedScreen.this);
                        List<Address> addresses = geocoder.getFromLocation(lat,longi,1);
                        System.out.println("addresses = "+addresses+"Place id"+place.getId());
                       /* mCity.setText(place.getName()+"");

                        mState.setText(addresses.get(0).getAdminArea());*/

                        country = ""+addresses.get(0).getCountryName();


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
        }
    }

    public LatLng convertAddressToLatLang(String strAddress)
    {
        Geocoder coder = new Geocoder(this);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
                //System.out.println("null");
            }

            Address location = address.get(0);
            System.out.println("LatLang = "+location.getLatitude()+","+ location.getLongitude()+" ");
            p1 = new LatLng(location.getLatitude(), location.getLongitude() );





        } catch (Exception ex) {

            ex.printStackTrace();
        }

        return p1;
    }



    public void addOrganization(final Organization organization,final String payment) {


        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.show();

        OrganizationApi apiService = Util.getClient().create(OrganizationApi.class);

        Call<Organization> call = apiService.addOrganization(organization);

        call.enqueue(new Callback<Organization>() {
            @Override
            public void onResponse(Call<Organization> call, Response<Organization> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {
                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201) {

                        Organization s = response.body();

                        if(s!=null){


                            PreferenceHandler.getInstance(GetStartedScreen.this).setCompanyId(s.getOrganizationId());
                            PreferenceHandler.getInstance(GetStartedScreen.this).setCompanyName(s.getOrganizationName());


                            PreferenceHandler.getInstance(GetStartedScreen.this).setAppType(response.body().getAppType());
                            PreferenceHandler.getInstance(GetStartedScreen.this).setLicenseStartDate(response.body().getLicenseStartDate());
                            PreferenceHandler.getInstance(GetStartedScreen.this).setLicenseEndDate(response.body().getLicenseEndDate());
                            PreferenceHandler.getInstance(GetStartedScreen.this).setSignupDate(response.body().getSignupDate());
                            PreferenceHandler.getInstance(GetStartedScreen.this).setOrganizationLongi(organization.getLongitude());
                            PreferenceHandler.getInstance(GetStartedScreen.this).setOrganizationLati(organization.getLatitude());
                            PreferenceHandler.getInstance(GetStartedScreen.this).setPlanType(response.body().getPlanType());
                            PreferenceHandler.getInstance(GetStartedScreen.this).setEmployeeLimit(response.body().getEmployeeLimit());
                            PreferenceHandler.getInstance(GetStartedScreen.this).setPlanId(response.body().getPlanId());


                            if(response.body().getDepartment()!=null&&response.body().getDepartment().size()!=0){

                                PreferenceHandler.getInstance(GetStartedScreen.this).setDepartmentId(response.body().getDepartment().get(0).getDepartmentId());

                            }




                            //addDepartments(s,departments,payment);

                            if(payment!=null&&payment.equalsIgnoreCase("Payment")){

                                OrganizationPayments op = new OrganizationPayments();
                                op.setTitle("Plan Subscription for Creating organization");
                                op.setDescription("Plan Name "+organization.getPlanId()+" License End date "+organization.getLicenseEndDate());
                                op.setPaymentDate(new SimpleDateFormat("MM/dd/yyyy").format(new Date()));
                                op.setOrganizationId(organization.getOrganizationId());
                                op.setPaymentBy(organization.getOrganizationName()+"");
                                op.setAmount(price * 100*organization.getEmployeeLimit());
                                op.setTransactionId(""+paymentId);
                                op.setTransactionMethod("");
                                op.setZingyPaymentStatus("Pending");
                                op.setZingyPaymentDate(new SimpleDateFormat("MM/dd/yyyy").format(new Date()));
                                op.setResellerCommissionPercentage(resellerPercentage);

                                addOrgaPay(organization,op);

                            }else{
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("Company",organization);
                                Intent profile = new Intent(GetStartedScreen.this,PhoneVerificationScreen.class);
                                profile.putExtras(bundle);
                                profile.putExtra("Screen","Organization");
                                startActivity(profile);
                                GetStartedScreen.this.finish();
                            }


                        }




                    }else {
                        Toast.makeText(GetStartedScreen.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
            public void onFailure(Call<Organization> call, Throwable t) {

                if(dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
                Toast.makeText(GetStartedScreen.this, "Failed Due to "+t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());
            }
        });



    }


    public void addOrgaPay(final Organization organization,final OrganizationPayments organizationPayments) {


        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Saving Details..");
        dialog.setCancelable(false);
        dialog.show();

        OrganizationPaymentAPI apiService = Util.getClient().create(OrganizationPaymentAPI.class);

        Call<OrganizationPayments> call = apiService.addOrganizationPayments(organizationPayments);

        call.enqueue(new Callback<OrganizationPayments>() {
            @Override
            public void onResponse(Call<OrganizationPayments> call, Response<OrganizationPayments> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {
                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201) {

                        OrganizationPayments s = response.body();

                        if(s!=null){

                            Toast.makeText(GetStartedScreen.this, "Your Organization Creted Successfully ", Toast.LENGTH_SHORT).show();





                                Bundle bundle = new Bundle();
                                bundle.putSerializable("Company",organization);
                                Intent profile = new Intent(GetStartedScreen.this,PhoneVerificationScreen.class);
                                profile.putExtras(bundle);
                                profile.putExtra("Screen","Organization");
                                startActivity(profile);
                                GetStartedScreen.this.finish();




                        }




                    }else {
                        Toast.makeText(GetStartedScreen.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
            public void onFailure(Call<OrganizationPayments> call, Throwable t) {

                if(dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
                Toast.makeText(GetStartedScreen.this, "Failed Due to "+t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());
            }
        });



    }

    //PaymentGateway Function
    public void startPayment() {
        /*
          You need to pass current activity in order to let Razorpay create CheckoutActivity
         */
        final Activity activity = this;

        final Checkout co = new Checkout();

        try {
            JSONObject options = new JSONObject();

            options.put("name", "EMS" );
            options.put("description", "For  "+planType);
            //You can omit the image option to fetch the image from dashboard
            //options.put("image", R.drawable.app_logo);
            options.put("currency", "INR");
            options.put("amount",price * 100*organization.getEmployeeLimit());
            //options.put("amount","100");

            JSONObject preFill = new JSONObject();
            preFill.put("email", "");
            preFill.put("contact","" );

            options.put("prefill", preFill);

            co.open(activity, options);
        } catch (Exception e) {
            Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT)
                    .show();
            e.printStackTrace();
        }
    }



    @SuppressWarnings("unused")
    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        try {

            paymentId = razorpayPaymentID;

            addOrganization(organization,"Payment");
            Toast.makeText(this, "Payment Successful: " + razorpayPaymentID, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Exception in onPaymentSuccess", e);
        }
    }


    @SuppressWarnings("unused")
    @Override
    public void onPaymentError(int code, String response) {
        try {
            Toast.makeText(this, "Payment failed: " + code + " " + response, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Exception in onPaymentError", e);
        }
    }



    @Override
    public void onBackPressed() {


            GetStartedScreen.this.finish();

    }

    public void getAddress(final double latitude,final double longitude)
    {

        try
        {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(GetStartedScreen.this, Locale.ENGLISH);


            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();



            System.out.println("address = "+address);

            String currentLocation;

            if(!isEmpty(address))
            {
                currentLocation=address;
                mAddress.setText(currentLocation);

            }
            else
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();


        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
   /* public void onAddField() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.add_email_layout, null);

        mEmailExtnLay.addView(rowView);


    }*/

   /* public void removeView() {

        int no = mEmailExtnLay.getChildCount();
        if(no >1)
        {

            mEmailExtnLay.removeView(mEmailExtnLay.getChildAt(no-1));

        }
        else
        {
            Toast.makeText(GetStartedScreen.this,"Atleast one email extension needed",Toast.LENGTH_SHORT).show();
        }

    }*/

  /*  public boolean checkcondition()
    {
        boolean value = false;
        int i = mEmailExtnLay.getChildCount();
        if(i != 0) {



                for (int j = 0; j < i; j++) {
                    EditText editText = (EditText) mEmailExtnLay.getChildAt(j);

                    if (editText.getText().toString().isEmpty()) {
                        Toast.makeText(GetStartedScreen.this, "Please Fill the Blank Space", Toast.LENGTH_SHORT).show();
                        value = false;
                    }else{
                        value =  true;
                    }
                }



        }
        else
        {
            Toast.makeText(GetStartedScreen.this, "Please add email extension for your employee", Toast.LENGTH_SHORT).show();
            value = false;
        }
        //return true;
        return value;

    }*/


    public void getProfile(final int id,final Organization organization,final String code ){

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


                            if(response.body()!=null){

                                String upToNCharacters = response.body().getFullName().substring(0, Math.min(response.body().getFullName().length(), 4));
                                if(code.equalsIgnoreCase(upToNCharacters+id)){

                                    try {

                                        organization.setResellerProfileId(response.body().getResellerProfileId());
                                        resellerPercentage = response.body().getCommissionPercentage();
                                        addOrganization(organization,null);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }else{
                                    Toast.makeText(GetStartedScreen.this, "Reseller code is wrong", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(GetStartedScreen.this, "Reseller code is wrong", Toast.LENGTH_SHORT).show();
                            }



                        }else{
                            Toast.makeText(GetStartedScreen.this, "Reseller code is wrong", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResellerProfiles> call, Throwable t) {

                        Toast.makeText(GetStartedScreen.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                    }
                });

            }

        });
    }

    public void getProfiles(final int id,final Organization organization,final String code ){

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


                            if(response.body()!=null){

                                String upToNCharacters = response.body().getFullName().substring(0, Math.min(response.body().getFullName().length(), 4));
                                if(code.equalsIgnoreCase(upToNCharacters+id)){

                                    try {
                                        organization.setResellerProfileId(response.body().getResellerProfileId());
                                        resellerPercentage = response.body().getCommissionPercentage();
                                        startPayment();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }else{
                                    Toast.makeText(GetStartedScreen.this, "Reseller code is wrong", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(GetStartedScreen.this, "Reseller code is wrong", Toast.LENGTH_SHORT).show();
                            }



                        }else{
                            Toast.makeText(GetStartedScreen.this, "Reseller code is wrong", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResellerProfiles> call, Throwable t) {

                        Toast.makeText(GetStartedScreen.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                    }
                });

            }

        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mLocationClient != null) {
            mLocationClient.connect();
        }
    }

    public boolean locationCheck(){

        final boolean status = false;
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
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
            android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(GetStartedScreen.this);
            dialog.setMessage("Location is not enable");
            dialog.setPositiveButton("Open Settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
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
    public void onConnected(@Nullable Bundle bundle) {
        Log.i("salam", " Connected");

        if (ActivityCompat.checkSelfPermission(GetStartedScreen.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(GetStartedScreen.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        currentLocation = LocationServices.FusedLocationApi.getLastLocation(mLocationClient);


        if (currentLocation != null) {
            //  latLong.setText("Latitude : " + currentLocation.getLatitude() + " , Longitude : " + currentLocation.getLongitude());

            latitude = currentLocation.getLatitude();
            longitude = currentLocation.getLongitude();


        }
        startLocationUpdates();



    }


    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void onLocationChanged(Location location) {


        if(location!=null){

            latitude = location.getLatitude();
            longitude = location.getLongitude();



        }



    }



    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(GetStartedScreen.this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(GetStartedScreen.this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else
                Toast.makeText(GetStartedScreen.this, "Google Play Services not install", Toast.LENGTH_SHORT).show();

            return false;
        }
        return true;
    }
    protected void startLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        if (ActivityCompat.checkSelfPermission(GetStartedScreen.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(GetStartedScreen.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(GetStartedScreen.this, "Enable Permissions", Toast.LENGTH_LONG).show();
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mLocationClient, mLocationRequest, this);


    }

    private void fn_permission() {
        if ((ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {

            if ((ActivityCompat.shouldShowRequestPermissionRationale(GetStartedScreen.this, android.Manifest.permission.ACCESS_FINE_LOCATION))) {


            } else {
                ActivityCompat.requestPermissions(GetStartedScreen.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION

                        },
                        REQUEST_PERMISSIONS);

            }
        } else {
            boolean_permission = true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id)
        {
            case android.R.id.home:

                GetStartedScreen.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
