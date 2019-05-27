package app.zingo.employeemanagements.UI.Company;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

import app.zingo.employeemanagements.Model.Departments;
import app.zingo.employeemanagements.Model.Organization;
import app.zingo.employeemanagements.Utils.PreferenceHandler;
import app.zingo.employeemanagements.Utils.Util;
import app.zingo.employeemanagements.WebApi.DepartmentApi;
import app.zingo.employeemanagements.WebApi.OrganizationApi;
import app.zingo.employeemanagements.base.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateCompany extends AppCompatActivity {

    TextInputEditText mOrganizationName,mCity,mState,mBuildYear,mWebsite;
    EditText mAbout,mAddress;
    AppCompatButton mCreate;


    public static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    String country,placeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{

            setContentView(R.layout.activity_create_company);

            mOrganizationName = findViewById(R.id.name);
            mCity = findViewById(R.id.city);
            mState = findViewById(R.id.state);
            mBuildYear = findViewById(R.id.build);
            mWebsite = findViewById(R.id.website);

            mAbout = findViewById(R.id.about);
            mAddress = findViewById(R.id.address);

            mCreate = findViewById(R.id.createCompany);

            mCity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent =
                                new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY/*MODE_FULLSCREEN*/)
                                        .build(CreateCompany.this);
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

    public void validate() {

        String company =mOrganizationName.getText().toString();
        String about = mAbout.getText().toString();
        String address = mAddress.getText().toString();
        String city = mCity.getText().toString();
        String state = mState.getText().toString();
        String build = mBuildYear.getText().toString();
        String web = mWebsite.getText().toString();

        if(company.isEmpty()){

            Toast.makeText(CreateCompany.this, "Organization Name required", Toast.LENGTH_SHORT).show();

        }else if(about.isEmpty()){

            Toast.makeText(CreateCompany.this, "About Organization Name required", Toast.LENGTH_SHORT).show();

        }else if(address.isEmpty()){

            Toast.makeText(CreateCompany.this, "Address required", Toast.LENGTH_SHORT).show();

        }else if(city.isEmpty()){

            Toast.makeText(CreateCompany.this, "City required", Toast.LENGTH_SHORT).show();

        }else if(state.isEmpty()){

            Toast.makeText(CreateCompany.this, "State required", Toast.LENGTH_SHORT).show();

        }else if(build.isEmpty()){

            Toast.makeText(CreateCompany.this, "Build year required", Toast.LENGTH_SHORT).show();

        }else if(web.isEmpty()){

            Toast.makeText(CreateCompany.this, "Websites required", Toast.LENGTH_SHORT).show();

        }else{

            LatLng latLng = convertAddressToLatLang(address+"," +city+","+state+","+country);

            if(latLng!=null){

                Organization organization = new Organization();
                organization.setOrganizationName(company);
                organization.setAboutUs(about);
                organization.setAddress(address);
                organization.setCity(city);
                organization.setState(state);
                organization.setBuiltYear(build);
                organization.setWebsite(web);
                organization.setLatitude(String.valueOf(latLng.latitude));
                organization.setLongitude(String.valueOf(latLng.longitude));
                if(placeId!=null){
                    organization.setPlaceId(placeId);
                }
                organization.setLocation(address+"," +city+","+state+","+country);

                addOrganization(organization);


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
                        Geocoder geocoder = new Geocoder(CreateCompany.this);
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



    public void addOrganization(final Organization organization) {


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


                            PreferenceHandler.getInstance(CreateCompany.this).setCompanyId(s.getOrganizationId());
                            PreferenceHandler.getInstance(CreateCompany.this).setCompanyName(s.getOrganizationName());

                            Departments departments = new Departments();
                            departments.setDepartmentName("Founders");
                            departments.setDepartmentDescription("The owner or operator of a foundry");
                            departments.setOrganizationId(s.getOrganizationId());

                            addDepartments(s,departments);


                        }




                    }else {
                        Toast.makeText(CreateCompany.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(CreateCompany.this, "Failed Due to "+t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());
            }
        });



    }

    public void addDepartments(final Organization organization, final Departments departments) {


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

                            Toast.makeText(CreateCompany.this, "Your Organization Creted Successfully ", Toast.LENGTH_SHORT).show();

                            PreferenceHandler.getInstance(CreateCompany.this).setDepartmentId(s.getDepartmentId());

                            Bundle bundle = new Bundle();
                            bundle.putSerializable("Company",organization);
                            Intent profile = new Intent(CreateCompany.this,CreateFounderScreen.class);
                            profile.putExtras(bundle);
                            startActivity(profile);
                            CreateCompany.this.finish();

                        }




                    }else {
                        Toast.makeText(CreateCompany.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(CreateCompany.this, "Failed Due to "+t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());
            }
        });



    }

}
