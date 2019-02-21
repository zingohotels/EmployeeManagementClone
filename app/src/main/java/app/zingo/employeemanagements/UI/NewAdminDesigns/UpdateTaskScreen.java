package app.zingo.employeemanagements.UI.NewAdminDesigns;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import app.zingo.employeemanagements.Custom.MapViewScroll;
import app.zingo.employeemanagements.Model.Tasks;
import app.zingo.employeemanagements.R;
import app.zingo.employeemanagements.UI.Admin.CreateTaskScreen;
import app.zingo.employeemanagements.Utils.PreferenceHandler;
import app.zingo.employeemanagements.Utils.TrackGPS;
import app.zingo.employeemanagements.Utils.Util;
import app.zingo.employeemanagements.WebApi.TasksAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateTaskScreen extends AppCompatActivity {

    TextInputEditText mTaskName, mFrom, mTo;//mDead
    EditText mdesc,mComments;
    Spinner mStatus;
    AppCompatButton mCreate;
    RelativeLayout mMapLay;
    Switch mShow;

    private EditText  lat, lng;
    private TextView location;

    private GoogleMap mMap;
    MapViewScroll mapView;
    Marker marker;


    double lati, lngi;
    Tasks updateTask;

    DecimalFormat df2 = new DecimalFormat(".##########");
    public static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    public String TAG = "MAPLOCATION",placeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_update_task_screen);

            mTaskName = (TextInputEditText) findViewById(R.id.task_name);
            mFrom = (TextInputEditText) findViewById(R.id.from_date);
            mTo = (TextInputEditText) findViewById(R.id.to_date);
            // mDead = (TextInputEditText) findViewById(R.id.dead_line);
            mdesc = (EditText) findViewById(R.id.task_description);
            mComments = (EditText) findViewById(R.id.task_comments);
            mCreate = (AppCompatButton) findViewById(R.id.apply_leave);
            mapView = (MapViewScroll) findViewById(R.id.task_location_map);
            mShow = (Switch) findViewById(R.id.show_map);
            mMapLay = (RelativeLayout) findViewById(R.id.map_layout);
            mStatus = (Spinner) findViewById(R.id.task_status_update);

            location = (TextView)findViewById(R.id.location_et);

            lat = (EditText)findViewById(R.id.lat_et);
            lng = (EditText)findViewById(R.id.lng_et);



            Bundle bundle = getIntent().getExtras();

            if (bundle != null) {

                updateTask = (Tasks)bundle.getSerializable("Task");

            }


            if(updateTask!=null){

                mTaskName.setText(""+updateTask.getTaskName());


                String froms = updateTask.getStartDate();
                String tos = updateTask.getEndDate();

                Date afromDate = null;
                Date atoDate = null;

                if(froms!=null&&!froms.isEmpty()){

                    if(froms.contains("T")){

                        String dojs[] = froms.split("T");

                        if(dojs[1].equalsIgnoreCase("00:00:00")){
                            try {
                                afromDate = new SimpleDateFormat("yyyy-MM-dd").parse(dojs[0]);
                                froms = new SimpleDateFormat("MMM dd,yyyy").format(afromDate);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }else{
                            try {
                                afromDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dojs[0]+" "+dojs[1]);
                                froms = new SimpleDateFormat("MMM dd,yyyy HH:mm").format(afromDate);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }




                    }

                }

                if(tos!=null&&!tos.isEmpty()){

                    if(tos.contains("T")){

                        String dojs[] = tos.split("T");

                        if(dojs[1].equalsIgnoreCase("00:00:00")){
                            try {
                                atoDate = new SimpleDateFormat("yyyy-MM-dd").parse(dojs[0]);
                                tos = new SimpleDateFormat("MMM dd,yyyy").format(atoDate);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }else{
                            try {
                                atoDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dojs[0]+" "+dojs[1]);
                                tos = new SimpleDateFormat("MMM dd,yyyy HH:mm").format(atoDate);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }


                                              /*  String parse = new SimpleDateFormat("MMM yyyy").format(atoDate);
                                                toDate = new SimpleDateFormat("MMM yyyy").parse(parse);*/

                    }

                }

                mFrom.setText(""+froms);
                mTo.setText(""+tos);
                mComments.setText(""+updateTask.getComments());
                mdesc.setText(""+updateTask.getTaskDescription());

                String status = updateTask.getStatus();

                if(status.equalsIgnoreCase("Pending")){

                    mStatus.setSelection(0);
                }else if(status.equalsIgnoreCase("On-Going")){
                    mStatus.setSelection(1);

                }else if(status.equalsIgnoreCase("Completed")){
                    mStatus.setSelection(2);

                }else if(status.equalsIgnoreCase("Closed")){
                    mStatus.setSelection(3);

                }

            }else {
                Toast.makeText(UpdateTaskScreen.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }


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
                MapsInitializer.initialize(UpdateTaskScreen.this);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent =
                                new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY/*MODE_FULLSCREEN*/)
                                        .build(UpdateTaskScreen.this);
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


                    if (ActivityCompat.checkSelfPermission(UpdateTaskScreen.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(UpdateTaskScreen.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

                    TrackGPS trackGPS = new TrackGPS(UpdateTaskScreen.this);


                    if(updateTask!=null){

                        lat.setText(updateTask.getLatitude()+"");
                        lng.setText(updateTask.getLongitude()+"");

                        if(updateTask.getLongitude()!=null&&updateTask.getLatitude()!=null){

                            lati = Double.parseDouble(updateTask.getLatitude());
                            lngi = Double.parseDouble(updateTask.getLongitude());

                            LatLng latLng = new LatLng(lati,lngi);

                            String add = getAddress(latLng);
                            location.setText(add);
                            mMap.clear();
                            marker = mMap.addMarker(new MarkerOptions()
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                                    .position(latLng));
                            CameraPosition cameraPosition1 = new CameraPosition.Builder().target(latLng).zoom(80).build();
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition1));

                        }else{
                            if(trackGPS.canGetLocation())
                            {
                                lati = trackGPS.getLatitude();
                                lngi = trackGPS.getLongitude();
                            }
                        }
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
                        try {
                            Log.d("Date", "DATE SELECTED "+dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                            newDate.set(year,monthOfYear,dayOfMonth);

                            new TimePickerDialog(UpdateTaskScreen.this, new TimePickerDialog.OnTimeSetListener() {
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
                            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), false).show();






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
        //   String dead = mDead.getText().toString();
        String taskName = mTaskName.getText().toString();
        String desc = mdesc.getText().toString();


        if(taskName.isEmpty()){

            Toast.makeText(this, "Task Name is required", Toast.LENGTH_SHORT).show();

        }else if(from.isEmpty()){

            Toast.makeText(this, "From date is required", Toast.LENGTH_SHORT).show();

        }else if(to.isEmpty()){

            Toast.makeText(this, "To date is required", Toast.LENGTH_SHORT).show();

        }else if(desc.isEmpty()){

            Toast.makeText(this, "Leave Comment is required", Toast.LENGTH_SHORT).show();

        }else{

            try{

                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
                Tasks tasks = updateTask;
                tasks.setTaskName(taskName);
                tasks.setTaskDescription(desc);
                tasks.setDeadLine(to);
                tasks.setStartDate(new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(sdf.parse(from)));
                tasks.setReminderDate(new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(sdf.parse(from)));
                tasks.setEndDate(new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(sdf.parse(to)));
                tasks.setStatus(mStatus.getSelectedItem().toString());
                tasks.setComments(mComments.getText().toString());
                tasks.setRemarks("");

                if(mShow.isChecked()){
                    tasks.setLatitude(lati+"");
                    tasks.setLongitude(lngi+"");
                }

                tasks.setDepartmentId(0);
                try {
                    updateTasks(tasks);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }catch (Exception e){
                e.printStackTrace();
            }


        }
    }

    private String getAddress(LatLng latLng) {
        Geocoder geocoder = new Geocoder(UpdateTaskScreen.this, Locale.getDefault());
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

    public void updateTasks(final Tasks tasks) throws Exception{



        final ProgressDialog dialog = new ProgressDialog(UpdateTaskScreen.this);
        dialog.setMessage("Saving Details..");
        dialog.setCancelable(false);
        dialog.show();

        TasksAPI apiService = Util.getClient().create(TasksAPI.class);

        Call<Tasks> call = apiService.updateTasks(tasks.getTaskId(),tasks);

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
                    if (statusCode == 200 || statusCode == 201|| statusCode == 204) {


                        Toast.makeText(UpdateTaskScreen.this, "Update Task succesfully", Toast.LENGTH_SHORT).show();



                    }else {
                        Toast.makeText(UpdateTaskScreen.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(UpdateTaskScreen.this, "Failed Due to "+t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());
            }
        });



    }
}