package app.zingo.employeemanagements.UI;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.itextpdf.text.Utilities;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import app.zingo.employeemanagements.Adapter.EmployeeAdapter;
import app.zingo.employeemanagements.Model.Departments;
import app.zingo.employeemanagements.Model.Employee;
import app.zingo.employeemanagements.Model.Meetings;
import app.zingo.employeemanagements.Model.Organization;
import app.zingo.employeemanagements.Model.Plans;
import app.zingo.employeemanagements.R;
import app.zingo.employeemanagements.UI.Admin.CreatePaySlip;
import app.zingo.employeemanagements.UI.Admin.EmployeesDashBoard;
import app.zingo.employeemanagements.UI.Company.CreateCompany;
import app.zingo.employeemanagements.UI.Company.CreateFounderScreen;
import app.zingo.employeemanagements.UI.Employee.DashBoardEmployee;
import app.zingo.employeemanagements.UI.Employee.EmployeeMeetingHost;
import app.zingo.employeemanagements.Utils.PreferenceHandler;
import app.zingo.employeemanagements.Utils.ThreadExecuter;
import app.zingo.employeemanagements.Utils.Util;
import app.zingo.employeemanagements.WebApi.DepartmentApi;
import app.zingo.employeemanagements.WebApi.MeetingsAPI;
import app.zingo.employeemanagements.WebApi.OrganizationApi;
import app.zingo.employeemanagements.WebApi.PlansAndRatesAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.security.AccessController.getContext;

public class GetStartedScreen extends AppCompatActivity  implements PaymentResultListener {

    boolean popUp = false;
    String appType = "",planType="";
    int addtionalDay = 30,planId=1;
    double price = 0;


    RecyclerView mPlanList;
    LinearLayout mPlanLayout;
    TextInputEditText mOrganizationName,mCity,mState,mBuildYear,mNoEmployee,mWebsite;
    EditText mAbout,mAddress;
    AppCompatButton mCreate;


    public static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    String country,placeId;
    Organization organization;


    //PaymentGateway
    private static final String TAG = GetStartedScreen.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
            setContentView(R.layout.activity_get_started_screen);
            popupOne();

            mPlanList = (RecyclerView)findViewById(R.id.plans);
            mPlanLayout = (LinearLayout) findViewById(R.id.plan_layout);
            getPlans();

            mOrganizationName = (TextInputEditText)findViewById(R.id.name);
            mCity = (TextInputEditText)findViewById(R.id.city);
            mState = (TextInputEditText)findViewById(R.id.state);
            mBuildYear = (TextInputEditText)findViewById(R.id.build);
            mWebsite = (TextInputEditText)findViewById(R.id.website);
            mNoEmployee = (TextInputEditText)findViewById(R.id.employee_count);

            mAbout = (EditText)findViewById(R.id.about);
            mAddress = (EditText)findViewById(R.id.address);

            mCreate = (AppCompatButton)findViewById(R.id.createCompany);

            mCity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent =
                                new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY/*MODE_FULLSCREEN*/)
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
            });

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
            builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {

                        return true; // Consumed
                    }
                    else {
                        return false; // Not consumed
                    }
                }
            });
            final Button mPaid = (Button) views.findViewById(R.id.paid_version);
            final Button mTrial = (Button) views.findViewById(R.id.trial_version);
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
                                    price = dto.getRatesList().get(0).getPrice();
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
                                    price = dto.getRatesList().get(1).getPrice();

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
                                    price = dto.getRatesList().get(2).getPrice();

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

                mPlanName = (TextView)itemView.findViewById(R.id.plan_name);
                mRate1 = (TextView)itemView.findViewById(R.id.rate_price1);
                mRate2 = (TextView)itemView.findViewById(R.id.rate_price2);
                mRate3 = (TextView)itemView.findViewById(R.id.rate_price3);
                mPrice1 = (CheckBox)itemView.findViewById(R.id.rate_check1);
                mPrice2 = (CheckBox)itemView.findViewById(R.id.rate_check2);
                mPrice3 = (CheckBox)itemView.findViewById(R.id.rate_check3);



            }
        }
    }
    public void validate() throws Exception{

        String company =mOrganizationName.getText().toString();
        String about = mAbout.getText().toString();
        String address = mAddress.getText().toString();
        String city = mCity.getText().toString();
        String state = mState.getText().toString();
        String build = mBuildYear.getText().toString();
        String web = mWebsite.getText().toString();
        String employeeCount = mNoEmployee.getText().toString();

        if(company.isEmpty()){

            Toast.makeText(GetStartedScreen.this, "Organization Name required", Toast.LENGTH_SHORT).show();

        }else if(about.isEmpty()){

            Toast.makeText(GetStartedScreen.this, "About Organization Name required", Toast.LENGTH_SHORT).show();

        }else if(address.isEmpty()){

            Toast.makeText(GetStartedScreen.this, "Address required", Toast.LENGTH_SHORT).show();

        }else if(city.isEmpty()){

            Toast.makeText(GetStartedScreen.this, "City required", Toast.LENGTH_SHORT).show();

        }else if(state.isEmpty()){

            Toast.makeText(GetStartedScreen.this, "State required", Toast.LENGTH_SHORT).show();

        }else if(build.isEmpty()){

            Toast.makeText(GetStartedScreen.this, "Build year required", Toast.LENGTH_SHORT).show();

        }else if(web.isEmpty()){

            Toast.makeText(GetStartedScreen.this, "Websites required", Toast.LENGTH_SHORT).show();

        }else if(employeeCount.isEmpty()){

            Toast.makeText(GetStartedScreen.this, "Employee Nos required", Toast.LENGTH_SHORT).show();

        }else{

            LatLng latLng = convertAddressToLatLang(address+"," +city+","+state+","+country);

            if(latLng!=null){

                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                SimpleDateFormat sdfs = new SimpleDateFormat("MM/dd/yyyy");
                organization = new Organization();
                organization.setOrganizationName(company);
                organization.setAboutUs(about);
                organization.setAddress(address);
                organization.setCity(city);
                organization.setState(state);
                organization.setBuiltYear(build);
                organization.setWebsite(web);
                organization.setEmployeeLimit(Integer.parseInt(employeeCount));
                organization.setLatitude(String.valueOf(latLng.latitude));
                organization.setLongitude(String.valueOf(latLng.longitude));
                organization.setSignupDate(sdf.format(new Date()));
                organization.setAppType(appType);
                organization.setLicenseStartDate(sdfs.format(new Date()));
                if(placeId!=null){
                    organization.setPlaceId(placeId);
                }
                organization.setLocation(address+"," +city+","+state+","+country);
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
                        startPayment();
                    }else{
                        Toast.makeText(this, "Please Choose your plan", Toast.LENGTH_SHORT).show();
                    }

                }else{


                    organization.setPlanType("Trial");
                    c.add(Calendar.DATE, 14);

                    // convert calendar to date
                    Date additionalDate = c.getTime();
                    organization.setLicenseEndDate(sdfs.format(additionalDate));
                    addOrganization(organization);
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
                        mCity.setText(place.getName()+"");

                        mState.setText(addresses.get(0).getAdminArea());

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



    public void addOrganization(final Organization organization) throws Exception{


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
                            PreferenceHandler.getInstance(GetStartedScreen.this).setPlanType(response.body().getPlanType());
                            PreferenceHandler.getInstance(GetStartedScreen.this).setEmployeeLimit(response.body().getEmployeeLimit());
                            PreferenceHandler.getInstance(GetStartedScreen.this).setPlanId(response.body().getPlanId());


                            Departments departments = new Departments();
                            departments.setDepartmentName("Founders");
                            departments.setDepartmentDescription("The owner or operator of a foundry");
                            departments.setOrganizationId(s.getOrganizationId());

                            addDepartments(s,departments);


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

    public void addDepartments(final Organization organization,final Departments departments) throws Exception{


        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Saving Details..");
        dialog.setCancelable(false);
        dialog.show();

        DepartmentApi apiService = Util.getClient().create(DepartmentApi.class);

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

                            Toast.makeText(GetStartedScreen.this, "Your Organization Creted Successfully ", Toast.LENGTH_SHORT).show();

                            PreferenceHandler.getInstance(GetStartedScreen.this).setDepartmentId(s.getDepartmentId());

                            Bundle bundle = new Bundle();
                            bundle.putSerializable("Company",organization);
                            Intent profile = new Intent(GetStartedScreen.this,CreateFounderScreen.class);
                            profile.putExtras(bundle);
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
            public void onFailure(Call<Departments> call, Throwable t) {

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

            addOrganization(organization);
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

        if(popUp){

        }else{
            GetStartedScreen.this.finish();
        }
    }
}
