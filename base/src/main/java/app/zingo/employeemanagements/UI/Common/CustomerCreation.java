package app.zingo.employeemanagements.UI.Common;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

import app.zingo.employeemanagements.Custom.MapViewScroll;
import app.zingo.employeemanagements.Model.Customer;
import app.zingo.employeemanagements.Model.Organization;
import app.zingo.employeemanagements.base.R;
import app.zingo.employeemanagements.UI.Admin.CreateTaskScreen;
import app.zingo.employeemanagements.UI.GetStartedScreen;
import app.zingo.employeemanagements.Utils.PreferenceHandler;
import app.zingo.employeemanagements.Utils.TrackGPS;
import app.zingo.employeemanagements.Utils.Util;
import app.zingo.employeemanagements.WebApi.CustomerAPI;
import app.zingo.employeemanagements.WebApi.OrganizationApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerCreation extends AppCompatActivity {

    TextInputEditText mClientName,mClientMobile,mClientMail;
    Button mSave;
    RelativeLayout mMapLay;
    Switch mShow;

    private EditText  lat, lng;
    private TextView location;

    private GoogleMap mMap;
    MapViewScroll mapView;
    Marker marker;
    String add;


    double lati, lngi;
    String type;

    DecimalFormat df2 = new DecimalFormat(".##########");
    public static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    public String TAG = "MAPLOCATION",placeId;

    Customer updateCustomers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{

            setContentView(R.layout.activity_customer_creation);

            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Customer Details");

            mSave = findViewById(R.id.save);
            mClientName = findViewById(R.id.client_name);
            mClientMobile = findViewById(R.id.client_contact_number);
            mClientMail = findViewById(R.id.client_contact_email);
            mapView = findViewById(R.id.task_location_map);
            mShow = findViewById(R.id.show_map);
            mMapLay = findViewById(R.id.map_layout);

            location = findViewById(R.id.location_et);

            lat = findViewById(R.id.lat_et);
            lng = findViewById(R.id.lng_et);

            Bundle bun = getIntent().getExtras();

            if(bun!=null){

                updateCustomers = (Customer)bun.getSerializable("Customer");
            }




            mShow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if (isChecked) {
                        mMapLay.setVisibility(View.VISIBLE);

                    }else{
                        mMapLay.setVisibility(View.GONE);
                    }
                }
            });
            mapView.onCreate(savedInstanceState);
            mapView.onResume();

            try {
                MapsInitializer.initialize(CustomerCreation.this);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent =
                                new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY/*MODE_FULLSCREEN*/)
                                        .build(CustomerCreation.this);
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

            mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mMap = googleMap;


                    if (ActivityCompat.checkSelfPermission(CustomerCreation.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(CustomerCreation.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    mMap.getUiSettings().setZoomControlsEnabled(true);
                    mMap.getUiSettings().setAllGesturesEnabled(true);
                    mMap.setMyLocationEnabled(true);
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    mMap.getProjection().getVisibleRegion().latLngBounds.getCenter();

                    TrackGPS trackGPS = new TrackGPS(CustomerCreation.this);

                    if(trackGPS.canGetLocation())
                    {
                        lati = trackGPS.getLatitude();
                        lngi = trackGPS.getLongitude();
                    }

                    if(updateCustomers!=null){

                        setData(updateCustomers);

                    }


                    mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(LatLng latLng) {
                            DecimalFormat df2 = new DecimalFormat(".##########");


                            lati = latLng.latitude;
                            lngi = latLng.longitude;




                            lat.setText(df2.format(latLng.latitude)+"");
                            lng.setText(df2.format(latLng.longitude)+"");
                            add = getAddress(latLng);
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

            mSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String name = mClientName.getText().toString();
                    String mail = mClientMail.getText().toString();
                    String mobile = mClientMobile.getText().toString();

                    if(name==null||name.isEmpty()){

                        Toast.makeText(CustomerCreation.this, "Please enter name", Toast.LENGTH_SHORT).show();

                    }else if(mail==null||mail.isEmpty()){

                        Toast.makeText(CustomerCreation.this, "Please enter mail", Toast.LENGTH_SHORT).show();

                    }else if(mail==null||mail.isEmpty()){

                        Toast.makeText(CustomerCreation.this, "Please enter mobile number", Toast.LENGTH_SHORT).show();
                    }else{



                        if(updateCustomers!=null){
                            Customer customer = updateCustomers;

                            customer.setCustomerName(name);
                            customer.setCustomerEmail(mail);
                            customer.setCustomerMobile(mobile);
                            customer.setLatitude(""+lati);
                            customer.setLongitude(""+lngi);

                            if(add!=null){
                                customer.setCustomerAddress(""+add);
                            }else{
                                if(lati!=0&&lngi!=0){
                                    customer.setCustomerAddress(""+getAddress(new LatLng(lati,lngi)));
                                }
                            }
                            customer.setOrganizationId(PreferenceHandler.getInstance(CustomerCreation.this).getCompanyId());

                            updateCustomer(customer);

                        }else{

                            Customer customer = new Customer();
                            customer.setCustomerName(name);
                            customer.setCustomerEmail(mail);
                            customer.setCustomerMobile(mobile);
                            customer.setLatitude(""+lati);
                            customer.setLongitude(""+lngi);

                            if(lati!=0&&lngi!=0){
                                customer.setCustomerAddress(""+getAddress(new LatLng(lati,lngi)));
                            }
                            customer.setOrganizationId(PreferenceHandler.getInstance(CustomerCreation.this).getCompanyId());

                            addCustomer(customer);

                        }




                    }
                }
            });



        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private String getAddress(LatLng latLng) {
        Geocoder geocoder = new Geocoder(CustomerCreation.this, Locale.getDefault());
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



                return result;
            }
            return result;
        } catch (IOException e) {
            Log.e("MapLocation", "Unable connect to Geocoder", e);
            return result;
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
                    location.setText(place.getName()+","+place.getAddress());
                    //location.setText(""+place.getId());
                    placeId= place.getId();

                    lati = place.getLatLng().latitude;
                    lngi = place.getLatLng().longitude;

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
                } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                    Status status = PlaceAutocomplete.getStatus(this, data);
                    // TODO: Handle the error.
                    Log.i(TAG, status.getStatusMessage());

                } else if (resultCode == RESULT_CANCELED) {
                    // The user canceled the operation.
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    public void addCustomer(final Customer customer) {


        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.show();

        CustomerAPI apiService = Util.getClient().create(CustomerAPI.class);

        Call<Customer> call = apiService.addCustomer(customer);

        call.enqueue(new Callback<Customer>() {
            @Override
            public void onResponse(Call<Customer> call, Response<Customer> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {
                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201) {

                        Customer s = response.body();

                        if(s!=null){

                            Toast.makeText(CustomerCreation.this, "Customer created successfully", Toast.LENGTH_SHORT).show();
                            CustomerCreation.this.finish();

                        }




                    }else {
                        Toast.makeText(CustomerCreation.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
            public void onFailure(Call<Customer> call, Throwable t) {

                if(dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
                Toast.makeText(CustomerCreation.this, "Failed Due to "+t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());
            }
        });



    }


    public void updateCustomer(final Customer customer) {


        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.show();

        CustomerAPI apiService = Util.getClient().create(CustomerAPI.class);

        Call<Customer> call = apiService.updateCustomer(customer.getCustomerId(),customer);

        call.enqueue(new Callback<Customer>() {
            @Override
            public void onResponse(Call<Customer> call, Response<Customer> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {
                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201||statusCode==204) {



                        Toast.makeText(CustomerCreation.this, "Customer updates successfully", Toast.LENGTH_SHORT).show();
                        CustomerCreation.this.finish();




                    }else {
                        Toast.makeText(CustomerCreation.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
            public void onFailure(Call<Customer> call, Throwable t) {

                if(dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
                Toast.makeText(CustomerCreation.this, "Failed Due to "+t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());
            }
        });



    }

    public void setData(final Customer customer){

        mClientName.setText(""+customer.getCustomerName());
        mClientMobile.setText(""+customer.getCustomerMobile());
        mClientMail.setText(""+customer.getCustomerEmail());
        lat.setText(""+customer.getLongitude());
        lng.setText(""+customer.getLongitude());

        lati = Double.parseDouble(customer.getLatitude());
        lngi = Double.parseDouble(customer.getLongitude());

        add = getAddress(new LatLng(lati,lngi));
        location.setText(add);
        mMap.clear();
        marker = mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .position(new LatLng(lati,lngi)));
        CameraPosition cameraPosition1 = new CameraPosition.Builder().target(new LatLng(lati,lngi)).zoom(80).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition1));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id)
        {
            case android.R.id.home:

                CustomerCreation.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
