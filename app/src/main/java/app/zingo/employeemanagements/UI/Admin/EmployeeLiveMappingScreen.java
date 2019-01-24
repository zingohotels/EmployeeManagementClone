package app.zingo.employeemanagements.UI.Admin;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.FrameLayout;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import app.zingo.employeemanagements.Adapter.LocationLiveAdapter;
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

                                mMap.clear();

                                LatLng calymayor = new LatLng(Double.parseDouble(list.get(0).getLatitude()), Double.parseDouble(list.get(0).getLongitude()));
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(calymayor));
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(calymayor, 15));

                                PolylineOptions polylineOptions = new PolylineOptions();
                                polylineOptions.color(Color.BLUE);
                                polylineOptions.width(30);

                                gpsTrack = mMap.addPolyline(polylineOptions);

                                ArrayList<LiveTracking> lt = new ArrayList<>();

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

                                createMarker(Double.parseDouble(list.get(k).getLatitude()), Double.parseDouble(list.get(k).getLongitude()),"Last Location","");

                                if(lastKnownLatLng!=null){
                                    CameraPosition cameraPosition = new CameraPosition.Builder().zoom(80).target(lastKnownLatLng).build();
                                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                }



                                if(list.size()==3){

                                    lt.add(list.get(0));
                                    lt.add(list.get(1));
                                    lt.add(list.get(2));

                                }else if(list.size()>3){

                                    lt.add(list.get(0));

                                    String lati = list.get(0).getLatitude();
                                    String lngi = list.get(0).getLongitude();

                                    for(int i=1;i<list.size()-1;i++){

                                        if(list.get(i-1).getLatitude().equalsIgnoreCase(list.get(i).getLatitude())&&list.get(i-1).getLongitude().equalsIgnoreCase(list.get(i).getLongitude())){

                                            Location locationA = new Location("point A");

                                            locationA.setLatitude(Double.parseDouble(lati));
                                            locationA.setLongitude(Double.parseDouble(lngi));

                                            Location locationB = new Location("point B");

                                            locationB.setLatitude(Double.parseDouble(list.get(i).getLatitude()));
                                            locationB.setLongitude(Double.parseDouble(list.get(i).getLongitude()));

                                            float distance = locationA.distanceTo(locationB);

                                            if(distance>=100){
                                                lt.add(list.get(i));
                                                 lati = list.get(i).getLatitude();
                                                 lngi = list.get(i).getLongitude();
                                            }

                                        }
                                    }

                                    lt.add(list.get(list.size()-1));

                                } else if(list.size()<3){
                                    lt.add(list.get(0));
                                    lt.add(list.get(0));
                                    lt.add(list.get(list.size()-1));
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

        return mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .anchor(0.5f, 0.5f)
                .title(title)
                .snippet(snippet)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));


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
            LocationLiveAdapter adapter = new LocationLiveAdapter(EmployeeLiveMappingScreen.this,liveTrackingArrayList);
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

}
