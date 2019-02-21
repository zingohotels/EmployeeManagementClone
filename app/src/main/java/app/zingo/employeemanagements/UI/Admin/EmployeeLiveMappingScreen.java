package app.zingo.employeemanagements.UI.Admin;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import app.zingo.employeemanagements.Adapter.LocationLiveAdapter;
import app.zingo.employeemanagements.Model.Employee;
import app.zingo.employeemanagements.Model.LiveTraackingTime;
import app.zingo.employeemanagements.Model.LiveTracking;
import app.zingo.employeemanagements.Model.LoginDetails;
import app.zingo.employeemanagements.Model.MarkerData;
import app.zingo.employeemanagements.R;
import app.zingo.employeemanagements.UI.Employee.EmployeeListScreen;
import app.zingo.employeemanagements.UI.GetStartedScreen;
import app.zingo.employeemanagements.Utils.DataParser;
import app.zingo.employeemanagements.Utils.PreferenceHandler;
import app.zingo.employeemanagements.Utils.ThreadExecuter;
import app.zingo.employeemanagements.Utils.Util;
import app.zingo.employeemanagements.WebApi.LiveTrackingAPI;
import app.zingo.employeemanagements.WebApi.LoginDetailsAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.text.TextUtils.isEmpty;

public class EmployeeLiveMappingScreen extends AppCompatActivity {

    //maps related
    private GoogleMap mMap;
    MapView mapView;
    LinearLayout mlocation;
    int activityId;
    Marker marker;
    private MarkerOptions options = new MarkerOptions();

    ArrayList<LatLng> MarkerPoints;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    private LatLng lastKnownLatLng;
    private Polyline gpsTrack;

    int employeeId;
    Employee employee;
    ArrayList<Integer> colorValue;

    Handler h = new Handler();
    int delay = 15*1000; //1 second=1000 milisecond, 15*1000=15seconds
    Runnable runnable;

    Dialog slideDialog;

    private static final int VIEW_TYPE_TOP = 0;
    private static final int VIEW_TYPE_MIDDLE = 1;
    private static final int VIEW_TYPE_BOTTOM = 2;
    private List<LiveTracking> mItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
            setContentView(R.layout.activity_employee_live_mapping_screen);

            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Location History");

            mapView = (MapView) findViewById(R.id.employee_live_list_map);
            mlocation = (LinearLayout) findViewById(R.id.location_list);

            mapView.onCreate(savedInstanceState);
            mapView.onResume();

            MarkerPoints = new ArrayList<>();

           /* mDepartmentCard.setVisibility(View.GONE);
            mDepartmentLay.setVisibility(View.GONE);*/

            Bundle bundle = getIntent().getExtras();

            if(bundle!=null){

                employeeId = bundle.getInt("EmployeeId");
                employee = (Employee)bundle.getSerializable("Employee");
            }

            try {
                MapsInitializer.initialize(EmployeeLiveMappingScreen.this);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mMap = googleMap;


                    if (ActivityCompat.checkSelfPermission(EmployeeLiveMappingScreen.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(EmployeeLiveMappingScreen.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        return;
                    }
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    mMap.getProjection().getVisibleRegion().latLngBounds.getCenter();


                    try{


                        LiveTracking lv = new LiveTracking();
                        lv.setEmployeeId(employeeId);
                        lv.setTrackingDate(new SimpleDateFormat("MM/dd/yyyy").format(new Date()));

                        getLiveLocation(lv);
                        //getMeetingDetails(11);
                    }catch (Exception e){
                        e.printStackTrace();
                    }


                }
            });


        }catch(Exception e)
        {
            e.printStackTrace();
        }

    }

    private void getLiveLocation(final LiveTracking lv){


        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading Details..");
        progressDialog.setCancelable(false);
        progressDialog.show();



        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {
                LiveTrackingAPI apiService = Util.getClient().create(LiveTrackingAPI.class);
                Call<ArrayList<LiveTracking>> call = apiService.getLiveTrackingByEmployeeIdAndDate(lv);

                call.enqueue(new Callback<ArrayList<LiveTracking>>() {
                    @Override
                    public void onResponse(Call<ArrayList<LiveTracking>> call, Response<ArrayList<LiveTracking>> response) {
                        int statusCode = response.code();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {


                            if (progressDialog!=null)
                                progressDialog.dismiss();
                            ArrayList<LiveTracking> list = response.body();
                            long hours=0;

                            ArrayList<MarkerData> markerData = new ArrayList<>();


                            if (list !=null && list.size()!=0) {

                                Collections.sort(list,LiveTracking.compareLiveTrack);
                                colorValue = new ArrayList<>();

                                mMap.clear();

                                LatLng calymayor = new LatLng(Double.parseDouble(list.get(0).getLatitude()), Double.parseDouble(list.get(0).getLongitude()));
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(calymayor));
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(calymayor, 15));

                                PolylineOptions polylineOptions = new PolylineOptions();
                                polylineOptions.color(Color.parseColor("#EE596C"));
                                polylineOptions.width(10);

                                gpsTrack = mMap.addPolyline(polylineOptions);

                                ArrayList<LiveTracking> lt = new ArrayList<>();
                                ArrayList<LiveTraackingTime> lts = new ArrayList<>();

                                int k=0;


                                for(int i=0;i<list.size();i++){



                                    if(list.get(i).getLongitude()!=null||list.get(i).getLatitude()!=null){

                                        double lat = Double.parseDouble(list.get(i).getLatitude());
                                        double lng = Double.parseDouble(list.get(i).getLongitude());

                                        if(lat==0&&lng==0){

                                        }else{
                                            lastKnownLatLng = new LatLng(Double.parseDouble(list.get(i).getLatitude()), Double.parseDouble(list.get(i).getLongitude()));
                                            updateTrack();

                                            k=i;

                                        }



                                    }


                                }

                                String snippetL = getAddress(Double.parseDouble(list.get(k).getLongitude()),Double.parseDouble(list.get(k).getLatitude()));

                                createMarker(Double.parseDouble(list.get(k).getLatitude()), Double.parseDouble(list.get(k).getLongitude()),employee.getEmployeeName()+"\nLast Location",""+snippetL);

                                if(lastKnownLatLng!=null){
                                    CameraPosition cameraPosition = new CameraPosition.Builder().zoom(60).target(lastKnownLatLng).build();
                                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                }



                                if(list.size()==3){


                                    LiveTraackingTime ltt = new LiveTraackingTime();
                                    ltt.setLiveTracking(list.get(0));
                                    ltt.setPreviousTime("Starts at "+list.get(0).getTrackingTime());
                                    ltt.setDuration("0");
                                    ltt.setKm("0");
                                    lts.add(ltt);
                                    lt.add(list.get(0));

                                    String snippet = getAddress(Double.parseDouble(list.get(0).getLongitude()),Double.parseDouble(list.get(0).getLatitude()));

                                    createMarker(Double.parseDouble(list.get(0).getLatitude()), Double.parseDouble(list.get(0).getLongitude()),employee.getEmployeeName()+"\nLocation 1",""+snippet);


                                   /* ltt = new LiveTraackingTime();
                                    ltt.setLiveTracking(list.get(1));
                                    ltt.setPreviousTime(list.get(0).getTrackingTime());
                                    ltt.setDuration("0");
                                    ltt.setKm("0");
                                    lts.add(ltt);*/
                                    lt.add(list.get(1));

                                    String snippets = getAddress(Double.parseDouble(list.get(1).getLongitude()),Double.parseDouble(list.get(1).getLatitude()));

                                    createMarker(Double.parseDouble(list.get(1).getLatitude()), Double.parseDouble(list.get(1).getLongitude()),employee.getEmployeeName()+"\nLocation 2",""+snippets);

                                    ltt = new LiveTraackingTime();
                                    ltt.setLiveTracking(list.get(0));
                                    ltt.setPreviousTime("Ends at "+list.get(2).getTrackingTime());
                                    ltt.setDuration("0");
                                    ltt.setKm("0");
                                    lts.add(ltt);
                                    lt.add(list.get(2));

                                    String snippetr = getAddress(Double.parseDouble(list.get(2).getLongitude()),Double.parseDouble(list.get(2).getLatitude()));

                                    createMarker(Double.parseDouble(list.get(2).getLatitude()), Double.parseDouble(list.get(2).getLongitude()),employee.getEmployeeName()+"\nLocation 3",""+snippetr);

                                }else if(list.size()>3){

                                    LiveTraackingTime ltt = new LiveTraackingTime();
                                    ltt.setLiveTracking(list.get(0));
                                    ltt.setPreviousTime("Starts at "+list.get(0).getTrackingTime());
                                    ltt.setDuration("0");
                                    ltt.setKm("0");
                                    lts.add(ltt);

                                    lt.add(list.get(0));

                                    String snippets = getAddress(Double.parseDouble(list.get(0).getLongitude()),Double.parseDouble(list.get(0).getLatitude()));

                                    createMarker(Double.parseDouble(list.get(0).getLatitude()), Double.parseDouble(list.get(0).getLongitude()),employee.getEmployeeName()+"\nLocation "+lt.size(),""+snippets);

                                    String lati = list.get(0).getLatitude();
                                    String lngi = list.get(0).getLongitude();
                                    String time = list.get(0).getTrackingTime();

                                    int durationValue =0;
                                    long diffHrs =0;

                                    boolean addValue = false;
                                    int indexValue = 0;

                                    for(int i=1;i<list.size()-1;i++){

                                        if(list.get(i-1).getLatitude().equalsIgnoreCase(list.get(i).getLatitude())&&list.get(i-1).getLongitude().equalsIgnoreCase(list.get(i).getLongitude())){

                                          /*  int minutes = (int) ((diffHrs / (1000*60)) % 60);
                                            int hoursValue  = (int) ((diffHrs / (1000*60*60)) % 24);
                                            int days   = (int) ((diffHrs / (1000*60*60*24)));*/

                                            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");


                                            Date fd=null,td=null;

                                            String logoutT = list.get(i).getTrackingTime();
                                            String loginT = time;

                                            if(loginT==null||loginT.isEmpty()){

                                                loginT = "00:00:00";
                                            }

                                            if(logoutT==null||logoutT.isEmpty()){

                                                logoutT ="00:00:00";
                                            }

                                            try {
                                                fd = sdf.parse(""+loginT);
                                                td = sdf.parse(""+logoutT);

                                                long diff = td.getTime() - fd.getTime();

                                                int minutes = (int) ((diff / (1000*60)) % 60);
                                                int hoursValue   = (int) ((diff / (1000*60*60)) % 24);
                                                int days   = (int) ((diff / (1000*60*60*24)));

                                                if(minutes>=5){

                                                   addValue = true;
                                                   indexValue = i;

                                                }



                                            } catch (ParseException e) {
                                                e.printStackTrace();

                                            }




                                        }else{

                                            if(addValue){
                                                addValue = false;
                                                time = list.get(indexValue).getTrackingTime();
                                                lt.add(list.get(indexValue));


                                                String snippet = getAddress(Double.parseDouble(list.get(indexValue).getLongitude()),Double.parseDouble(list.get(indexValue).getLatitude()));

                                                createMarker(Double.parseDouble(list.get(indexValue).getLatitude()), Double.parseDouble(list.get(indexValue).getLongitude()),employee.getEmployeeName()+"\nLocation "+lt.size(),""+snippet);
                                            }

                                            Location locationA = new Location("point A");

                                            locationA.setLatitude(Double.parseDouble(list.get(i-1).getLatitude()));
                                            locationA.setLongitude(Double.parseDouble(list.get(i-1).getLongitude()));

                                            Location locationB = new Location("point B");

                                            locationB.setLatitude(Double.parseDouble(list.get(i).getLatitude()));
                                            locationB.setLongitude(Double.parseDouble(list.get(i).getLongitude()));

                                            float distance = locationA.distanceTo(locationB);

                                            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");


                                            Date fd=null,td=null;

                                            String logoutT = list.get(i).getTrackingTime();
                                            String loginT = list.get(i-1).getTrackingTime();

                                            if(loginT==null||loginT.isEmpty()){

                                                loginT = "00:00:00";
                                            }

                                            if(logoutT==null||logoutT.isEmpty()){

                                                logoutT ="00:00:00";
                                            }

                                            try {
                                                fd = sdf.parse(""+loginT);
                                                td = sdf.parse(""+logoutT);

                                                long diff = td.getTime() - fd.getTime();

                                                int minutes = (int) ((diff / (1000*60)) % 60);
                                                int hoursValue   = (int) ((diff / (1000*60*60)) % 24);
                                                int days   = (int) ((diff / (1000*60*60*24)));

                                                if(minutes>=5&&distance>=500){

                                                    lt.add(list.get(i));
                                                    lati = list.get(i).getLatitude();
                                                    lngi = list.get(i).getLongitude();

                                                    String snippet = getAddress(Double.parseDouble(lngi),Double.parseDouble(lati));

                                                    createMarker(Double.parseDouble(list.get(i).getLatitude()), Double.parseDouble(list.get(i).getLongitude()),employee.getEmployeeName()+"\nLocation "+lt.size(),""+snippet);

                                                }



                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                                if(distance>=500){
                                                    lt.add(list.get(i));
                                                    lati = list.get(i).getLatitude();
                                                    lngi = list.get(i).getLongitude();

                                                    String snippet = getAddress(Double.parseDouble(lngi),Double.parseDouble(lati));

                                                    createMarker(Double.parseDouble(list.get(i).getLatitude()), Double.parseDouble(list.get(i).getLongitude()),employee.getEmployeeName()+"\nLocation "+lt.size(),""+snippet);
                                                }

                                            }




                                        }
                                    }
                                    String snippetsr = getAddress(Double.parseDouble(list.get(list.size()-1).getLongitude()),Double.parseDouble(list.get(list.size()-1).getLatitude()));

                                    createMarker(Double.parseDouble(list.get(list.size()-1).getLatitude()), Double.parseDouble(list.get(list.size()-1).getLongitude()),employee.getEmployeeName()+"\nLocation "+lt.size(),""+snippetsr);
                                    lt.add(list.get(list.size()-1));

                                } else if(list.size()<3){
                                    lt.add(list.get(0));
                                    String snippet = getAddress(Double.parseDouble(list.get(0).getLongitude()),Double.parseDouble(list.get(0).getLatitude()));

                                    createMarker(Double.parseDouble(list.get(0).getLatitude()), Double.parseDouble(list.get(0).getLongitude()),employee.getEmployeeName()+"\nLocation 1",""+snippet);

                                    lt.add(list.get(0));

                                    String snippets = getAddress(Double.parseDouble(list.get(0).getLongitude()),Double.parseDouble(list.get(0).getLatitude()));

                                    createMarker(Double.parseDouble(list.get(0).getLatitude()), Double.parseDouble(list.get(0).getLongitude()),employee.getEmployeeName()+"\nLocation 2",""+snippets);
                                    lt.add(list.get(list.size()-1));

                                    String snippetr = getAddress(Double.parseDouble(list.get(list.size()-1).getLongitude()),Double.parseDouble(list.get(list.size()-1).getLatitude()));

                                    createMarker(Double.parseDouble(list.get(list.size()-1).getLatitude()), Double.parseDouble(list.get(list.size()-1).getLongitude()),employee.getEmployeeName()+"\nLocation 3",""+snippetr);
                                }


                                if(lt!=null&&lt.size()!=0){
                                    mlocation.setVisibility(View.VISIBLE);
                                    mlocation.removeAllViews();
                                    onAddField(lt);
                                }else{

                                }





                            }else{

                                Toast.makeText(EmployeeLiveMappingScreen.this, "No Location found", Toast.LENGTH_SHORT).show();

                            }

                        }else {


                            Toast.makeText(EmployeeLiveMappingScreen.this, "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<LiveTracking>> call, Throwable t) {
                        // Log error here since request failed
                        if (progressDialog!=null)
                            progressDialog.dismiss();
                        Log.e("TAG", t.toString());
                    }
                });
            }


        });
    }

    protected Marker createMarker(double latitude, double longitude, String title, String snippet) {

        //BitmapDescriptorFactory.defaultMarker(new Random().nextInt(360));
        float color = new Random().nextInt(360);
        colorValue.add((int)color);

        return mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .anchor(0.5f, 0.5f)
                .title(title)
                .snippet(snippet)
                .icon( BitmapDescriptorFactory.defaultMarker(color)));


    }

    // Fetches data from url passed
    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask",jsonData[0].toString());


                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask","Executing routes");
                Log.d("ParserTask",routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask",e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.RED);

                Log.d("onPostExecute","onPostExecute lineoptions decoded");

            }

            // Drawing polyline in the Google Map for the i-th route
            if(lineOptions != null) {
                mMap.addPolyline(lineOptions);
            }
            else {
                Log.d("onPostExecute","without Polylines drawn");
            }
        }
    }

    private String getUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;


        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters+"&key=AIzaSyD7wKDeCjNaLc8OjxHhYFVieOsL9lXhFZQ" ;


        return url;
    }
    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private void updateTrack() {
        List<LatLng> points = gpsTrack.getPoints();
        points.add(lastKnownLatLng);
        gpsTrack.setPoints(points);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(lastKnownLatLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
    }

     public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_calendar, menu);
        return true;
    }

/*
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != R.id.action_setting) {
            return super.onOptionsItemSelected(menuItem);
        }
        startActivity(new Intent(this, SettingsActivity.class));
        return true;
    }
*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id)
        {
            case android.R.id.home:

                EmployeeLiveMappingScreen.this.finish();

            case R.id.action_calendar:

               openDatePicker();
        }
        return super.onOptionsItemSelected(item);
    }

    public void openDatePicker() {
        // Get Current Date

        final Calendar c = Calendar.getInstance();
        int mYear  = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay   = c.get(Calendar.DAY_OF_MONTH);
        //launch datepicker modal
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        try {
                            Log.d("Date", "DATE SELECTED "+dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                            Calendar newDate = Calendar.getInstance();
                            newDate.set(year,monthOfYear,dayOfMonth);


                            String date1 = (monthOfYear + 1)  + "/" + (dayOfMonth) + "/" + year;

                            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy");



                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
                            try {
                                Date fdate = simpleDateFormat.parse(date1);

                                LiveTracking lv = new LiveTracking();
                                lv.setEmployeeId(employeeId);
                                lv.setTrackingDate(date1);
                                getLiveLocation(lv);




                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                        }
                        catch (Exception ex)
                        {
                            ex.printStackTrace();
                        }


                    }
                }, mYear, mMonth, mDay);

        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();

    }

    public void onAddField(final  ArrayList<LiveTracking> liveTrackingArrayList) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.bottom_location_places, null);

        TextView mNo = (TextView) rowView.findViewById(R.id.no_result);
        mNo.setVisibility(View.GONE);
        RecyclerView list = (RecyclerView)rowView.findViewById(R.id.location_list);

        list.removeAllViews();

        if(liveTrackingArrayList!=null&&liveTrackingArrayList.size()!=0){
            colorValue.remove(0);
            LocationLiveAdapter adapter = new LocationLiveAdapter(EmployeeLiveMappingScreen.this,liveTrackingArrayList,colorValue);
            list.setAdapter(adapter);
        }

        mlocation.addView(rowView);


    }


    public void removeView() {

        int no = mlocation.getChildCount();
        if(no >1)
        {

            mlocation.removeView(mlocation.getChildAt(no-1));

        }
        else
        {
            Toast.makeText(EmployeeLiveMappingScreen.this,"Atleast one email extension needed",Toast.LENGTH_SHORT).show();
        }

    }

    public String getAddress(final double longitude,final double latitude )
    {

        String addressValue = "";
        try
        {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(EmployeeLiveMappingScreen.this, Locale.ENGLISH);


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
                addressValue = address;

            }
            else
                System.out.println("Wrong");



        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            addressValue = "";
        }

        return addressValue;
    }

    public class LocationLiveAdapter extends RecyclerView.Adapter<LocationLiveAdapter.ViewHolder>{

        private Context context;
        private ArrayList<LiveTracking> list;
        private ArrayList<Integer> colorDesign;
        private static final int VIEW_TYPE_TOP = 0;
        private static final int VIEW_TYPE_MIDDLE = 1;
        private static final int VIEW_TYPE_BOTTOM = 2;


        public LocationLiveAdapter(Context context, ArrayList<LiveTracking> list,ArrayList<Integer> colorDesign) {

            this.context = context;
            this.list = list;
            this.colorDesign = colorDesign;


        }

        @Override
        public LocationLiveAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.adapter_location_like_google, parent, false);
            ViewHolder viewHolder = new ViewHolder(v);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {


            LiveTracking item = list.get(position);


            String froms = item.getTrackingDate();
            String times = item.getTrackingTime();

            Date fromDate = null;

            /*if(colorDesign.size()==list.size()){

               // holder.mLocationColor.setColorFilter(colorDesign.get(position));
                ColorFilter cf = new PorterDuffColorFilter(colorDesign.get(position),PorterDuff.Mode.OVERLAY);

                holder.mLocationColor.setImageResource(R.drawable.location_dynamic);
                holder.mLocationColor.setColorFilter(cf);

            }*/

            Date afromDate = null;
            if(position!=(list.size()-1)&&list.size()>1){

                Location locationA = new Location("point A");

                locationA.setLatitude(Double.parseDouble(list.get(position).getLatitude()));
                locationA.setLongitude(Double.parseDouble(list.get(position).getLongitude()));

                Location locationB = new Location("point B");

                locationB.setLatitude(Double.parseDouble(list.get(position+1).getLatitude()));
                locationB.setLongitude(Double.parseDouble(list.get(position+1).getLongitude()));

                float distance = locationA.distanceTo(locationB);

                double kms = distance/1000.0;

                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");


                Date fd=null,td=null;

                String logoutT = list.get(position+1).getTrackingTime();
                String loginT = list.get(position).getTrackingTime();

                if(loginT==null||loginT.isEmpty()){

                    loginT = "00:00:00";
                }

                if(logoutT==null||logoutT.isEmpty()){

                    logoutT ="00:00:00";
                }

                try {
                    fd = sdf.parse(""+loginT);
                    td = sdf.parse(""+logoutT);

                    long diff = td.getTime() - fd.getTime();

                    int seconds = (int) ((diff / (1000)) % 60);
                    int minutes = (int) ((diff / (1000*60)) % 60);
                    int hours   = (int) ((diff / (1000*60*60)) % 24);
                    int days   = (int) ((diff / (1000*60*60*24)));

                    holder.mItemKm.setText(new DecimalFormat("#.##").format(kms)+" Km"+",   "+String.format("%02d", hours)+"hr" +":"+String.format("%02d", minutes)+"min");

                } catch (ParseException e) {
                    e.printStackTrace();
                    holder.mItemKm.setText(new DecimalFormat("#.##").format(kms)+" Km");

                }
            }
            final int sdk = android.os.Build.VERSION.SDK_INT;

            if(position==0){
                if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    holder.mItemLine.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.line_bg_top) );
                } else {
                    holder.mItemLine.setBackground(ContextCompat.getDrawable(context, R.drawable.line_bg_top));
                }
            }else if(position==(list.size()-1)){



            }else{
                if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    holder.mItemLine.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.line_bg_middle) );
                } else {
                    holder.mItemLine.setBackground(ContextCompat.getDrawable(context, R.drawable.line_bg_middle));
                }
            }




            if(times!=null&&!times.isEmpty()) {



                String dojs[] = froms.split("T");

                try {


                    if(times==null||times.isEmpty()){
                        times = "00:00:00";
                    }
                    fromDate = new SimpleDateFormat("HH:mm:ss").parse(times);


                    String parses = new SimpleDateFormat("HH:mm").format(fromDate);
                    if(position==0){
                        holder.mItemTime.setText("Started at "+parses);
                    }else if(position==(list.size()-1)){
                        holder.mItemTime.setText("Ended at "+parses);
                    }else{

                        if(list.get(position-1).getTrackingTime()!=null){
                            try {


                                String timevalue = list.get(position-1).getTrackingTime();
                                if(timevalue==null||timevalue.isEmpty()){
                                    timevalue = "00:00:00";
                                }
                                fromDate = new SimpleDateFormat("HH:mm:ss").parse(timevalue);


                                String parsesValue = new SimpleDateFormat("HH:mm").format(fromDate);

                                holder.mItemTime.setText(parsesValue+"-"+parses);


                            } catch (ParseException e) {
                                e.printStackTrace();
                                holder.mItemTime.setText(""+parses);
                            }

                        }else{
                            holder.mItemTime.setText(""+parses);
                        }

                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }



            }
            double lng = Double.parseDouble(item.getLongitude());
            double lat = Double.parseDouble(item.getLatitude());
            getAddress(lng,lat,holder.mItemSubtitle,holder.mItemTitle);

            holder.mItemSubtitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(mMap!=null){

                        LatLng calymayor = new LatLng(Double.parseDouble(list.get(position).getLatitude()), Double.parseDouble(list.get(position).getLongitude()));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(calymayor));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(calymayor, 100));
                    }



                }
            });


        }

        @Override
        public int getItemViewType(int position) {
            if(position == 0) {
                return VIEW_TYPE_TOP;
            }else if(position == list.size() - 1) {
                return VIEW_TYPE_BOTTOM;
            }else {
                return VIEW_TYPE_MIDDLE;
            }

        }



        @Override
        public int getItemCount() {
            return list.size();
        }


        class ViewHolder extends RecyclerView.ViewHolder /*implements View.OnClickListener*/ {

            TextView mItemTitle;
            TextView mItemSubtitle;
            TextView mItemTime;
            TextView mItemKm;
            FrameLayout mItemLine;
            ImageView mLocationColor;

            public ViewHolder(View itemView) {
                super(itemView);
                itemView.setClickable(true);

                mItemTitle = (TextView) itemView.findViewById(R.id.item_title);
                mItemSubtitle = (TextView) itemView.findViewById(R.id.item_subtitle);
                mItemTime = (TextView) itemView.findViewById(R.id.item_time);
                mItemKm = (TextView) itemView.findViewById(R.id.item_km);
                mItemLine = (FrameLayout) itemView.findViewById(R.id.item_line);
                mLocationColor = (ImageView) itemView.findViewById(R.id.location_color);


            }
        }

        public void getAddress(final double longitude,final double latitude,final TextView textView ,final TextView textViewTitle )
        {

            try
            {
                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(context, Locale.ENGLISH);


                addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName();
                String local = addresses.get(0).getSubLocality();



                System.out.println("address = "+address);

                String currentLocation;

                if(!isEmpty(address))
                {
                    currentLocation=address;


                    if(!isEmpty(local)){
                        textViewTitle.setText(""+local);
                    }else{
                        textViewTitle.setText("Unknown");
                    }
                    textView.setText(currentLocation);

                }
                else{
                    if(!isEmpty(local)){
                        textViewTitle.setText(""+local);
                    }else{
                        textViewTitle.setText("Unknown");
                    }
                    textView.setText("Unknown");

                }



            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }


}
