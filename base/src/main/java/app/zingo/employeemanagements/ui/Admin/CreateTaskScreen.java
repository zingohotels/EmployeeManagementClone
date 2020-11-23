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
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.textfield.TextInputEditText;

import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import app.zingo.employeemanagements.Custom.MapViewScroll;
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

public class CreateTaskScreen extends AppCompatActivity {

    TextInputEditText mTaskName, mFrom, mTo, mFromTime, mToTime,mdesc,lat, lng;//mDead
    TextView mCreate;
    RelativeLayout mMapLay;
    SwitchCompat mShow;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_create_task_screen);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Create Task");
            mTaskName = findViewById(R.id.task_name);
            mFrom = findViewById(R.id.from_date);
            mTo = findViewById(R.id.to_date);
            mFromTime = findViewById(R.id.from_time);
            mToTime = findViewById(R.id.to_time);
           // mDead = (TextInputEditText) findViewById(R.id.dead_line);
            mdesc = findViewById(R.id.task_desc);
            mCreate = findViewById(R.id.create_task);
            mapView = findViewById(R.id.task_location_map);
            mShow = findViewById(R.id.show_map);
            mMapLay = findViewById(R.id.map_layout);
            location = findViewById(R.id.location_et);
            lat = findViewById(R.id.lat_et);
            lng = findViewById(R.id.lng_et);

            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                employeeId = bundle.getInt("EmployeeId");
                deptId = bundle.getInt("DepartmentId");
                type = bundle.getString("Type");
            }

                try{
                    Places.initialize ( getApplicationContext (),Constants.mapKey);
                }catch ( Exception e ){
                    e.printStackTrace ();
                }

            mFrom.setOnClickListener( view -> openDatePicker(mFrom) );

            mTo.setOnClickListener( view -> openDatePicker(mTo) );

            mFromTime.setOnClickListener( v -> {
                //openTimePicker();
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(CreateTaskScreen.this, new TimePickerDialog.OnTimeSetListener() {
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
            } );

            mToTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //openTimePicker();
                    // TODO Auto-generated method stub
                    Calendar mcurrentTime = Calendar.getInstance();
                    int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                    int minute = mcurrentTime.get(Calendar.MINUTE);
                    TimePickerDialog mTimePicker;
                    mTimePicker = new TimePickerDialog(CreateTaskScreen.this, new TimePickerDialog.OnTimeSetListener() {
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

            mCreate.setOnClickListener( view -> validate() );

            mShow.setOnCheckedChangeListener( ( buttonView , isChecked ) -> {
                if (isChecked) {
                    mMapLay.setVisibility(View.VISIBLE);
                }else{
                    mMapLay.setVisibility(View.GONE);
                }
            } );

            mapView.onCreate(savedInstanceState);
            mapView.onResume();

            try {
                MapsInitializer.initialize(CreateTaskScreen.this);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            location.setOnClickListener( v -> {
                try {
                    List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME, Place.Field.ADDRESS);
                    Intent intent = new Autocomplete.IntentBuilder( AutocompleteActivityMode.FULLSCREEN, fields).build(getApplicationContext());
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (Exception e) {
                    e.printStackTrace();
                    // TODO: Handle the error.
                }
            } );

            mapView.getMapAsync( googleMap -> {
                mMap = googleMap;
                if (ActivityCompat.checkSelfPermission(CreateTaskScreen.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(CreateTaskScreen.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                TrackGPS trackGPS = new TrackGPS(CreateTaskScreen.this);
                if(trackGPS.canGetLocation()) {
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
            } );

        }catch (Exception e){
            e.printStackTrace();
        }
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
                ( view , year , monthOfYear , dayOfMonth ) -> {
                    try {
                        newDate.set(year,monthOfYear,dayOfMonth);
                        String date = ((monthOfYear+1)+"/"+dayOfMonth+"/"+year);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
                        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy");
                        try {
                            Date parse_date = simpleDateFormat.parse(date);
                            String date_format = sdf.format(parse_date);

                            if(tv.equals(mFrom)) {
                                tv.setText(date_format);
                            }
                            else if(tv.equals(mTo)) {
                                tv.setText(date_format);
                            }

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } , mYear, mMonth, mDay);

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    public void validate(){
        String from = mFrom.getText().toString();
        String to = mTo.getText().toString();
        String fromTime = mFromTime.getText().toString();
        String toTime = mToTime.getText().toString();
        String taskName = mTaskName.getText().toString();
        String desc = mdesc.getText().toString();

        if(taskName.isEmpty()){
            Toast.makeText(this, "Task Name is required", Toast.LENGTH_SHORT).show();
        }else if(from.isEmpty()){
            Toast.makeText(this, "From Date is required", Toast.LENGTH_SHORT).show();
        }else if(to.isEmpty()){
            Toast.makeText(this, "To Date is required", Toast.LENGTH_SHORT).show();
        }else if(to.equals ( from )){
            Toast.makeText(this, "To date should be greater than From date", Toast.LENGTH_SHORT).show();
        }else if(fromTime.isEmpty()){
            Toast.makeText(this, "From Time is required", Toast.LENGTH_SHORT).show();
        }else if(toTime.isEmpty()){
            Toast.makeText(this, "To Time is required", Toast.LENGTH_SHORT).show();
        }else if(desc.isEmpty()){
            Toast.makeText(this, "Leave Comment is required", Toast.LENGTH_SHORT).show();
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
                tasks.setComments("");
                tasks.setRemarks("");

                if(mShow.isChecked()){
                    tasks.setLatitude(lati+"");
                    tasks.setLongitude(lngi+"");
                }
                if(type!=null&&type.equalsIgnoreCase("Employee")){
                    tasks.setToReportEmployeeId(PreferenceHandler.getInstance(CreateTaskScreen.this).getManagerId());
                    tasks.setEmployeeId(employeeId);

                }else{
                    tasks.setToReportEmployeeId(PreferenceHandler.getInstance(CreateTaskScreen.this).getUserId());
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
        TasksAPI apiService = Util.getClient().create(TasksAPI.class);
        Call<Tasks> call = apiService.addTasks(tasks);
        call.enqueue(new Callback<Tasks>() {
            @Override
            public void onResponse(Call<Tasks> call, Response<Tasks> response) {
                try {
                    if(dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201) {
                        Tasks s = response.body();

                        if(s!=null){
                            Toast.makeText(CreateTaskScreen.this, "Task Created Successfully", Toast.LENGTH_SHORT).show();
                            CreateTaskScreen.this.finish();
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
                        Toast.makeText(CreateTaskScreen.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception ex) {
                    if(dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Tasks> call, Throwable t) {
                if(dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
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
                try {
                    if(dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201) {
                        TaskNotificationManagers s = response.body();
                        if(s!=null){
                            task.setSenderId(Constants.SENDER_ID);
                            task.setServerId(Constants.SERVER_ID);
                            sendTask(task);
                        }

                    }else {
                        Toast.makeText(CreateTaskScreen.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception ex) {

                    if(dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<TaskNotificationManagers> call, Throwable t) {
                if(dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                Log.e("TAG", t.toString());
            }
        });
    }

    public void sendTask(final TaskNotificationManagers lm) {
        Gson gson = new Gson ();
        String json = gson.toJson ( lm );
        System.out.println("Suree :"+json);
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Sending Details..");
        dialog.setCancelable(false);
        dialog.show();
        TaskNotificationAPI apiService = Util.getClient().create(TaskNotificationAPI.class);
        Call<ArrayList<String>> call = apiService.sendTask(lm);
        call.enqueue(new Callback<ArrayList<String>>() {
            @Override
            public void onResponse(Call<ArrayList<String>> call, Response<ArrayList<String>> response) {
                try {
                    if(dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201) {
                        Toast.makeText(CreateTaskScreen.this, "Notification Sent "+response.message(), Toast.LENGTH_SHORT).show();
                        CreateTaskScreen.this.finish();
                    }else {
                        Toast.makeText(CreateTaskScreen.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                catch (Exception ex) {
                    if(dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<String>> call, Throwable t) {
                if(dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                Log.e("TAG", t.toString());
            }
        });
    }

    private String getAddress(LatLng latLng) {
        Geocoder geocoder = new Geocoder(CreateTaskScreen.this, Locale.getDefault());
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
        super.onActivityResult(requestCode, resultCode, data);
        try{
            if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
                if (resultCode == RESULT_OK) {
                    Place place = Autocomplete.getPlaceFromIntent (data);
                    location.setText(place.getName()+","+place.getAddress());
                    placeId= place.getId();
                    lati = place.getLatLng().latitude;
                    lngi = place.getLatLng().longitude;
                    lat.setText(df2.format(place.getLatLng().latitude)+"");
                    lng.setText(df2.format(place.getLatLng().longitude)+"");
                    System.out.println("Star Rating = "+place.getRating());
                    if(mMap != null) {
                        mMap.clear();
                        marker = mMap.addMarker(new MarkerOptions()
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                                .position(place.getLatLng()));
                        CameraPosition cameraPosition1 = new CameraPosition.Builder().target(place.getLatLng()).zoom(17).build();
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition1));
                    }
                    Log.i(TAG, "Place: " + place.getName());
                } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                    Status status = Autocomplete.getStatusFromIntent (data);
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

    @Override
    public boolean onOptionsItemSelected ( MenuItem item ) {
        int id = item.getItemId ();
        if(id==android.R.id.home){
            CreateTaskScreen.this.finish ();
        }
        return super.onOptionsItemSelected ( item );
    }
    @Override
    public void onBackPressed ( ) {
        super.onBackPressed ( );
        CreateTaskScreen.this.finish ();
    }
}
