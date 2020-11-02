package app.zingo.employeemanagements.ui.Admin;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputEditText;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import app.zingo.employeemanagements.Custom.MapViewScroll;
import app.zingo.employeemanagements.WebApi.CustomerAPI;
import app.zingo.employeemanagements.adapter.CustomerSpinnerAdapter;
import app.zingo.employeemanagements.model.Customer;
import app.zingo.employeemanagements.model.TaskNotificationManagers;
import app.zingo.employeemanagements.model.Tasks;
import app.zingo.employeemanagements.utils.Constants;
import app.zingo.employeemanagements.utils.PreferenceHandler;
import app.zingo.employeemanagements.utils.TrackGPS;
import app.zingo.employeemanagements.utils.Util;
import app.zingo.employeemanagements.WebApi.TaskNotificationAPI;
import app.zingo.employeemanagements.WebApi.TasksAPI;
import app.zingo.employeemanagements.base.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateOrderScreen extends AppCompatActivity {

    TextInputEditText mTaskName, mFrom, mTo, mFromTime, mToTime,mdesc,mOrderAmount;//mDead
    TextInputEditText mClientName,mClientMobile,mClientMail;
    Spinner customerSpinner;
    LinearLayout ClientNameLayout,mSpinnerLay;
    AppCompatButton mCreate;
    RelativeLayout mMapLay;
    Switch mShow;

    private EditText  lat, lng;
    private TextView location;

    private GoogleMap mMap;
    MapViewScroll mapView;
    Marker marker;

    int employeeId, deptId;
    double lati, lngi;
    String type;

    DecimalFormat df2 = new DecimalFormat(".##########");
    public static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    public String TAG = "MAPLOCATION",placeId;

    ArrayList< Customer > customerArrayList = new ArrayList <> (  );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_create_order_screen);

            mTaskName = findViewById(R.id.task_name);
            mFrom = findViewById(R.id.from_date);
            mTo = findViewById(R.id.to_date);
            mFromTime = findViewById(R.id.from_time);
            mToTime = findViewById(R.id.to_time);
            // mDead = (TextInputEditText) findViewById(R.id.dead_line);
            mdesc = findViewById(R.id.task_desc);
            mOrderAmount = findViewById(R.id.amount_order);
            mCreate = findViewById(R.id.create_order);
            mapView = findViewById(R.id.task_location_map);
            mShow = findViewById(R.id.show_map);
            mMapLay = findViewById(R.id.map_layout);
            location = findViewById(R.id.location_et);
            mClientName = findViewById(R.id.client_name);
            mClientMobile = findViewById(R.id.client_contact_number);
            mClientMail = findViewById(R.id.client_contact_email);
            customerSpinner = findViewById(R.id.customer_spinner_adpter);
            ClientNameLayout = findViewById(R.id.client_name_layout);
            mSpinnerLay = findViewById(R.id.spinner_lay);
            lat = findViewById(R.id.lat_et);
            lng = findViewById(R.id.lng_et);

            Bundle bundle = getIntent().getExtras();

            Customer customer = new Customer ();
            customer.setCustomerName("Others");
            customerArrayList.add(customer);

            getCustomers(PreferenceHandler.getInstance( CreateOrderScreen.this).getCompanyId());

            if (bundle != null) {

                employeeId = bundle.getInt("EmployeeId");
                deptId = bundle.getInt("DepartmentId");
                type = bundle.getString("Type");
            }

            customerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    if(customerArrayList!=null && customerArrayList.size()!=0){

                        if(customerArrayList.get(position).getCustomerName()!=null && customerArrayList.get(position).getCustomerName().equalsIgnoreCase("Others"))
                        {
                            mClientMobile.setText("");
                            mClientName.setText("");
                            mClientMail.setText("");
                            ClientNameLayout.setVisibility(View.GONE);

                        }
                        else {
                            mClientMobile.setText(""+customerArrayList.get(position).getCustomerMobile());
                            mClientName.setText(""+customerArrayList.get(position).getCustomerName());
                            mClientMail.setText(""+customerArrayList.get(position).getCustomerEmail());
                           // clientId = customerArrayList.get(position).getCustomerId();
                            ClientNameLayout.setVisibility(View.GONE);

                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


            mFrom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    openDatePicker(mFrom);
                }
            });

            mTo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    openDatePicker(mTo);
                }
            });

            mFromTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //openTimePicker();
                    // TODO Auto-generated method stub
                    Calendar mcurrentTime = Calendar.getInstance();
                    int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                    int minute = mcurrentTime.get(Calendar.MINUTE);
                    TimePickerDialog mTimePicker;
                    mTimePicker = new TimePickerDialog( CreateOrderScreen.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

                            try{
                                Date fromTime = sdf.parse(selectedHour + ":" + selectedMinute);
                                mFromTime.setText( sdf.format(fromTime));
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                        }
                    }, hour, minute, true);//Yes 24 hour time
                    mTimePicker.setTitle("Select Time");
                    mTimePicker.show();
                }
            });

            mToTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //openTimePicker();

                    // TODO Auto-generated method stub
                    Calendar mcurrentTime = Calendar.getInstance();
                    int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                    int minute = mcurrentTime.get(Calendar.MINUTE);
                    TimePickerDialog mTimePicker;
                    mTimePicker = new TimePickerDialog( CreateOrderScreen.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

                            try{
                                Date totime = sdf.parse(selectedHour + ":" + selectedMinute);
                                mToTime.setText( sdf.format(totime));
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                        }
                    }, hour, minute, true);//Yes 24 hour time
                    mTimePicker.setTitle("Select Time");
                    mTimePicker.show();
                }
            });

           /* mDead.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    openDatePicker(mDead);
                }
            });
*/
            mCreate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    validate();
                }
            });

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
                MapsInitializer.initialize( CreateOrderScreen.this);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent =
                                new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY/*MODE_FULLSCREEN*/)
                                        .build( CreateOrderScreen.this);
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


                    if ( ActivityCompat.checkSelfPermission( CreateOrderScreen.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission( CreateOrderScreen.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

                    TrackGPS trackGPS = new TrackGPS ( CreateOrderScreen.this);

                    if(trackGPS.canGetLocation())
                    {
                        lati = trackGPS.getLatitude();
                        lngi = trackGPS.getLongitude();
                    }



                    mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(LatLng latLng) {
                            DecimalFormat df2 = new DecimalFormat(".##########");


                            lati = latLng.latitude;
                            lngi = latLng.longitude;




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

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void openTimePicker() {


    }

    public void openDatePicker(final TextInputEditText tv) {
        // Get Current Date

        final Calendar c = Calendar.getInstance();
        int mYear  = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay   = c.get(Calendar.DAY_OF_MONTH);

        final Calendar newDate = Calendar.getInstance();

        //launch datepicker modal
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, final int year, final int monthOfYear, final int dayOfMonth) {
                        try
                        {
                            newDate.set(year,monthOfYear,dayOfMonth);
                            String date = ((monthOfYear+1)+"/"+dayOfMonth+"/"+year);
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
                            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy");
                            try {
                                Date parse_date = simpleDateFormat.parse(date);
                                String date_format = sdf.format(parse_date);

                                if(tv.equals(mFrom))
                                {
                                    tv.setText(date_format);
                                }
                                else if(tv.equals(mTo))
                                {
                                    tv.setText(date_format);
                                }

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                           /* new TimePickerDialog(CreateOrderScreen.this, new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    newDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                    newDate.set(Calendar.MINUTE, minute);

                                    String date1 = (monthOfYear + 1)  + "/" + (dayOfMonth) + "/" + year +" "+hourOfDay+":"+minute;

                                    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");



                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
                                    try {
                                        Date fdate = simpleDateFormat.parse(date1);

                                        String from1 = sdf.format(fdate);


                                        tv.setText(from1);


                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), false).show();*/
                        }
                        catch (Exception ex)
                        {
                            ex.printStackTrace();
                        }
                    }
                }, mYear, mMonth, mDay);


        datePickerDialog.show();

    }

    public void validate(){


        String from = mFrom.getText().toString();
        String to = mTo.getText().toString();
        String fromTime = mFromTime.getText().toString();
        String toTime = mToTime.getText().toString();
        //   String dead = mDead.getText().toString();
        String taskName = mTaskName.getText().toString();
        String desc = mdesc.getText().toString();
        String orderAmount = mOrderAmount.getText().toString();

        String client = mClientName.getText().toString();
        String mobile = mClientMobile.getText().toString();
        String email = mClientMail.getText().toString();


        if(taskName.isEmpty()){
            Toast.makeText(this, "Order Name is required", Toast.LENGTH_SHORT).show();
        }else if(from.isEmpty()){
            Toast.makeText(this, "Order date is required", Toast.LENGTH_SHORT).show();
        }else if(to.isEmpty()){
            Toast.makeText(this, "Payment date is required", Toast.LENGTH_SHORT).show();
        }else if(fromTime.isEmpty()){
            Toast.makeText(this, "Please Select Order time", Toast.LENGTH_SHORT).show();
        }else if(toTime.isEmpty()){
            Toast.makeText(this, "Please Select Payment Time", Toast.LENGTH_SHORT).show();
        }else if(desc.isEmpty()){
            Toast.makeText(this, "Comment is required", Toast.LENGTH_SHORT).show();
        }else if(orderAmount.isEmpty()){
            Toast.makeText(this, "Order Amount is required", Toast.LENGTH_SHORT).show();
        }else if(client.isEmpty()){

            Toast.makeText( CreateOrderScreen.this, "Please mention client name", Toast.LENGTH_SHORT).show();

        }else{

            try{

                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
                Tasks tasks = new Tasks();
                tasks.setTaskName(taskName);
                tasks.setTaskDescription(desc);
                tasks.setDeadLine(to);
                tasks.setStartDate(new SimpleDateFormat("MM/dd/yyyy HH:mm").format(sdf.parse(from+" "+fromTime)));
                tasks.setReminderDate(new SimpleDateFormat("MM/dd/yyyy HH:mm").format(sdf.parse(from+" "+fromTime)));
                tasks.setEndDate(new SimpleDateFormat("MM/dd/yyyy HH:mm").format(sdf.parse(to+" "+toTime)));
                tasks.setStatus("Pending");
                tasks.setPriority(orderAmount);
                tasks.setCategory("Order");
                tasks.setComments("");

                String contact = "";

                if(email!=null&&!email.isEmpty()){
                    contact = contact+"%"+email;
                }

                if(mobile!=null&&!mobile.isEmpty()){
                    contact = contact+"%"+mobile;
                }
                tasks.setRemarks(""+client+""+contact);

                if(mShow.isChecked()){
                    tasks.setLatitude(lati+"");
                    tasks.setLongitude(lngi+"");
                }
                if(type!=null&&type.equalsIgnoreCase("Employee")){
                    tasks.setToReportEmployeeId(PreferenceHandler.getInstance( CreateOrderScreen.this).getManagerId());
                    tasks.setEmployeeId(employeeId);

                }else{
                    tasks.setToReportEmployeeId(PreferenceHandler.getInstance( CreateOrderScreen.this).getUserId());
                    tasks.setEmployeeId(employeeId);

                }

                tasks.setDepartmentId(0);


                try {
                    addTask(tasks);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }catch (Exception e){
                e.printStackTrace();
            }


        }
    }

    public long dateCal(String start,String end){

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy");
        System.out.println("Loigin "+start);
        System.out.println("Logout "+end);


        Date fd=null,td=null;



        try {
            fd = sdf.parse(""+start);
            td = sdf.parse(""+end);

            long diff = td.getTime() - fd.getTime();
            long Hours = diff / (60 * 60 * 1000) % 24;
            long Minutes = diff / (60 * 1000) % 60;
            long diffDays = diff / (24 * 60 * 60 * 1000);
            System.out.println("Diff "+diff);
            System.out.println("Hours "+Hours);
            System.out.println("Minutes "+Minutes);


            return  diffDays;
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void addTask(final Tasks tasks) {

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Saving Details..");
        dialog.setCancelable(false);
        dialog.show();

        TasksAPI apiService = Util.getClient().create( TasksAPI.class);

        Call<Tasks> call = apiService.addTasks(tasks);

        call.enqueue(new Callback<Tasks>() {
            @Override
            public void onResponse(Call<Tasks> call, Response<Tasks> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {
                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201) {

                        Tasks s = response.body();

                        if(s!=null){


                            Toast.makeText( CreateOrderScreen.this, "Order Created Successfully", Toast.LENGTH_SHORT).show();
                            //  CreateOrderScreen.this.finish();
                            TaskNotificationManagers tn = new TaskNotificationManagers();
                            tn.setEmployeeId(""+s.getEmployeeId());
                            tn.setTaskName(s.getTaskName());
                            tn.setTaskDescription(s.getTaskDescription());
                            tn.setDeadLine(s.getDeadLine());
                            tn.setComments(s.getComments());
                            tn.setRemarks(s.getRemarks());
                            tn.setToReportEmployeeId(s.getToReportEmployeeId());
                            tn.setTitle("Task Allocated");
                            tn.setMessage(""+s.getTaskName());
                            tn.setTaskId(s.getTaskId());
                            tn.setDepartmentId(1);
                            savetask(tn);
                        }

                    }else {
                        Toast.makeText( CreateOrderScreen.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
            public void onFailure(Call<Tasks> call, Throwable t) {

                if(dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
                Toast.makeText( CreateOrderScreen.this , "Failed due to Bad Internet Connection" , Toast.LENGTH_SHORT ).show( );
                Log.e("TAG", t.toString());
            }
        });



    }

    public void savetask(final TaskNotificationManagers task) {



        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Saving Details..");
        dialog.setCancelable(false);
        dialog.show();

        TaskNotificationAPI apiService = Util.getClient().create(TaskNotificationAPI.class);

        Call<TaskNotificationManagers> call = apiService.saveTask(task);

        call.enqueue(new Callback<TaskNotificationManagers>() {
            @Override
            public void onResponse(Call<TaskNotificationManagers> call, Response<TaskNotificationManagers> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {
                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201) {

                        TaskNotificationManagers s = response.body();

                        if(s!=null){

                            task.setSenderId( Constants.SENDER_ID);
                            task.setServerId( Constants.SERVER_ID);

                            sendTask(task);
                            //ApplyLeaveScreen.this.finish();


                        }




                    }else {
                        Toast.makeText( CreateOrderScreen.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
            public void onFailure(Call<TaskNotificationManagers> call, Throwable t) {

                if(dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
                Toast.makeText( CreateOrderScreen.this , "Failed due to Bad Internet Connection" , Toast.LENGTH_SHORT ).show( );
                Log.e("TAG", t.toString());
            }
        });



    }

    public void sendTask(final TaskNotificationManagers lm) {



        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Sending Details..");
        dialog.setCancelable(false);
        dialog.show();

        TaskNotificationAPI apiService = Util.getClient().create(TaskNotificationAPI.class);

        Call<ArrayList<String>> call = apiService.sendTask(lm);

        call.enqueue(new Callback<ArrayList<String>>() {
            @Override
            public void onResponse(Call<ArrayList<String>> call, Response<ArrayList<String>> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {
                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201) {


                        CreateOrderScreen.this.finish();


                    }else {
                        Toast.makeText( CreateOrderScreen.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
            public void onFailure(Call<ArrayList<String>> call, Throwable t) {

                if(dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
                Toast.makeText( CreateOrderScreen.this , "Failed due to Bad Internet Connection" , Toast.LENGTH_SHORT ).show( );
                Log.e("TAG", t.toString());
            }
        });



    }

    private String getAddress(LatLng latLng) {
        Geocoder geocoder = new Geocoder( CreateOrderScreen.this, Locale.getDefault());
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

    public void getCustomers(final int id) {

        final ProgressDialog dialog = new ProgressDialog( CreateOrderScreen.this);
        dialog.setMessage("Loading Details..");
        dialog.setCancelable(false);
        dialog.show();

        final CustomerAPI orgApi = Util.getClient().create( CustomerAPI.class);
        Call<ArrayList< Customer >> getProf = orgApi.getCustomerByOrganizationId(id);
        getProf.enqueue(new Callback<ArrayList< Customer >>() {

            @Override
            public void onResponse( Call<ArrayList< Customer >> call, Response<ArrayList< Customer >> response) {

                if (response.code() == 200||response.code() == 201||response.code() == 204)
                {
                    dialog.dismiss();
                    customerArrayList.addAll ( response.body () );

                    if(customerArrayList!=null&&customerArrayList.size()!=0){


                        CustomerSpinnerAdapter adapter = new CustomerSpinnerAdapter( CreateOrderScreen.this,customerArrayList);
                        customerSpinner.setAdapter(adapter);
                    }
                    else {
                     /*   ClientNameLayout.setVisibility(View.VISIBLE);
                        CustomerSpinnerAdapter adapter = new CustomerSpinnerAdapter(CreateOrderScreen.this,customerArrayList);
                        customerSpinner.setAdapter(adapter);*/
                        //customerSpinner.setVisibility(View.GONE);
                    }

                }else{
                    dialog.dismiss();
                    Toast.makeText( CreateOrderScreen.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    if(customerArrayList!=null&&customerArrayList.size()!=0){


                        CustomerSpinnerAdapter adapter = new CustomerSpinnerAdapter( CreateOrderScreen.this,customerArrayList);
                        customerSpinner.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onFailure( Call<ArrayList< Customer >> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText( CreateOrderScreen.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                if(customerArrayList!=null&&customerArrayList.size()!=0){


                    CustomerSpinnerAdapter adapter = new CustomerSpinnerAdapter( CreateOrderScreen.this,customerArrayList);
                    customerSpinner.setAdapter(adapter);
                }
            }
        });
    }
}
