package app.zingo.employeemanagements.UI.Admin;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.DrawableRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.squareup.picasso.Picasso;

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

import app.zingo.employeemanagements.Model.Employee;
import app.zingo.employeemanagements.Model.EmployeeImages;
import app.zingo.employeemanagements.Model.GeneralNotification;
import app.zingo.employeemanagements.Model.LiveTraackingTime;
import app.zingo.employeemanagements.Model.LiveTracking;
import app.zingo.employeemanagements.Model.MarkerData;
import app.zingo.employeemanagements.R;
import app.zingo.employeemanagements.Utils.Constants;
import app.zingo.employeemanagements.Utils.DataParser;
import app.zingo.employeemanagements.Utils.PreferenceHandler;
import app.zingo.employeemanagements.Utils.ThreadExecuter;
import app.zingo.employeemanagements.Utils.Util;
import app.zingo.employeemanagements.WebApi.EmployeeApi;
import app.zingo.employeemanagements.WebApi.GeneralNotificationAPI;
import app.zingo.employeemanagements.WebApi.LiveTrackingAPI;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.text.TextUtils.isEmpty;

public class EmployeeLiveMappingScreen extends AppCompatActivity {

    //maps related
    private GoogleMap mMap;
    MapView mapView;
    LinearLayout mlocation;
    LinearLayout scrollableLay,mMapShow;
    TextView mName,mUpdatingTime,mAddress,mLoadMap;
    ImageView mShowHide;

    ArrayList<LatLng> MarkerPoints;
    private LatLng lastKnownLatLng;
    private Polyline gpsTrack;

    int employeeId;
    Employee employee;
    String employeeImage="";
    ArrayList<Integer> colorValue;

    boolean arrayValue = false;
    int screenheight,screenwidth;

    //Global var
    ArrayList<LiveTracking> list;

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
            scrollableLay = (LinearLayout) findViewById(R.id.nestedScrollView);
            mMapShow = (LinearLayout) findViewById(R.id.map_lay_short);
            mShowHide = (ImageView) findViewById(R.id.show_lay_hide);
            mName = (TextView) findViewById(R.id.emp_name_loca);
            mUpdatingTime = (TextView) findViewById(R.id.updated_details);
            mAddress = (TextView) findViewById(R.id.address);
            mLoadMap = (TextView) findViewById(R.id.load_details);

            final ViewGroup.LayoutParams params = scrollableLay.getLayoutParams();
            final ViewGroup.LayoutParams paramsm = mMapShow.getLayoutParams();

            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            screenheight = displayMetrics.heightPixels;
            screenwidth = displayMetrics.widthPixels;

            mapView.onCreate(savedInstanceState);
            mapView.onResume();

            MarkerPoints = new ArrayList<>();



            Bundle bundle = getIntent().getExtras();

            if(bundle!=null){

                employeeId = bundle.getInt("EmployeeId");
                employee = (Employee)bundle.getSerializable("Employee");
            }

            if(employee!=null){

                mName.setText(""+employee.getEmployeeName());

                ArrayList<EmployeeImages> images = employee.getEmployeeImages();

                if(images!=null&&images.size()!=0){
                    EmployeeImages employeeImages = images.get(0);

                    if(employeeImages!=null){


                        employeeImage = employeeImages.getImage();

                    }

                }
            }else{

                getProfile(employeeId);

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
                    mMap.setMyLocationEnabled(true);


                    try{

                        mLoadMap.setText(""+new SimpleDateFormat("dd-MM-yyyy").format(new Date()));

                        LiveTracking lv = new LiveTracking();
                        lv.setEmployeeId(employeeId);
                        lv.setTrackingDate(new SimpleDateFormat("MM/dd/yyyy").format(new Date()));
                        getLiveLocation(lv);

                    }catch (Exception e){
                        e.printStackTrace();
                    }


                }
            });

            mShowHide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(mlocation.getVisibility()==View.VISIBLE){


                        // Changes the height and width to the specified *pixels*
                        params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                        params.width = screenwidth;
                        scrollableLay.setLayoutParams(params);

                        paramsm.height = LinearLayout.LayoutParams.MATCH_PARENT;
                        paramsm.width = screenwidth;
                        mMapShow.setLayoutParams(paramsm);

                        mlocation.setVisibility(View.GONE);
                        mShowHide.setImageResource(R.drawable.up_arrow_location);


                    }else{

                        // Changes the height and width to the specified *pixels*

                        params.width = screenwidth;
                        params.height = screenheight/2;
                        scrollableLay.setLayoutParams(params);

                        paramsm.width = screenwidth;
                        paramsm.height = screenheight/2;
                        mMapShow .setLayoutParams(paramsm);

                        mlocation.setVisibility(View.VISIBLE);
                        mShowHide.setImageResource(R.drawable.down_arrow_location);
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

                            list = response.body();


                            if (list !=null && list.size()!=0) {

                                arrayValue = true;

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

                                int k=0;


                                /*for(int i=0;i<list.size();i++){



                                    if(list.get(i).getLongitude()!=null||list.get(i).getLatitude()!=null){



                                        if(Double.parseDouble(list.get(i).getLatitude())==0&&Double.parseDouble(list.get(i).getLongitude())==0){

                                        }else{
                                            lastKnownLatLng = new LatLng(Double.parseDouble(list.get(i).getLatitude()), Double.parseDouble(list.get(i).getLongitude()));

                                            updateTrack(lastKnownLatLng);

                                            k=i;

                                        }



                                    }


                                }

*/
                               /* if(lastKnownLatLng!=null){
                                    CameraPosition cameraPosition = new CameraPosition.Builder().zoom(60).target(lastKnownLatLng).build();
                                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                }
*/


                                if(list.size()==3){




                                    updateTrack(new LatLng(Double.parseDouble(list.get(0).getLatitude()), Double.parseDouble(list.get(0).getLongitude())));

                                    lt.add(list.get(0));



                                    createMarkerwithCustom(Double.parseDouble(list.get(0).getLatitude()),
                                            Double.parseDouble(list.get(0).getLongitude()),employee.getEmployeeName()+"\nLocation 1",""+getAddress(Double.parseDouble(list.get(0).getLongitude()),Double.parseDouble(list.get(0).getLatitude())),
                                            createCustomMarker(EmployeeLiveMappingScreen.this,null,employee.getEmployeeName(),R.drawable.home_map));

                                    lt.add(list.get(1));
                                    updateTrack(new LatLng(Double.parseDouble(list.get(1).getLatitude()), Double.parseDouble(list.get(1).getLongitude())));



                                    createMarker(Double.parseDouble(list.get(1).getLatitude()),
                                            Double.parseDouble(list.get(1).getLongitude()),
                                            employee.getEmployeeName()+"\nLocation 2",""+getAddress(Double.parseDouble(list.get(1).getLongitude()),Double.parseDouble(list.get(1).getLatitude())));


                                    lt.add(list.get(2));
                                    updateTrack(new LatLng(Double.parseDouble(list.get(2).getLatitude()), Double.parseDouble(list.get(2).getLongitude())));



                                    createMarkerwithCustom(Double.parseDouble(list.get(2).getLatitude()),
                                            Double.parseDouble(list.get(2).getLongitude()),
                                            employee.getEmployeeName()+"\nLocation 3",""+getAddress(Double.parseDouble(list.get(2).getLongitude()),Double.parseDouble(list.get(2).getLatitude())),
                                            createCustomMarker(EmployeeLiveMappingScreen.this,employeeImage,employee.getEmployeeName(),R.drawable.live_location_black));


                                }else if(list.size()>3){

                                    ArrayList<LatLng> latLngs = new ArrayList<>();


                                    lt.add(list.get(0));

                                    String snippets = getAddress(Double.parseDouble(list.get(0).getLongitude()),Double.parseDouble(list.get(0).getLatitude()));

                                    createMarkerwithCustom(Double.parseDouble(list.get(0).getLatitude()), Double.parseDouble(list.get(0).getLongitude()),employee.getEmployeeName()+"\nLocation "+lt.size(),""+snippets,createCustomMarker(EmployeeLiveMappingScreen.this,null,employee.getEmployeeName(),R.drawable.home_map));
                                    latLngs.add(new LatLng(Double.parseDouble(list.get(0).getLatitude()), Double.parseDouble(list.get(0).getLongitude())));

                                    String time = list.get(0).getTrackingTime();

                                    int durationValue =0;
                                    long diffHrs =0;

                                    boolean addValue = false;
                                    int indexValue = 0;

                                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                                    Date fd=null,td=null;
                                    String logoutT = "",loginT="";
                                    long diff=0;
                                    int minutes =0;
                                    Location locationA = new Location("point A");
                                    Location locationB = new Location("point B");
                                    float distance=0;
                                    String snippet="";

                                    for(int i=1;i<list.size()-1;i++){

                                        logoutT = list.get(i).getTrackingTime();
                                        loginT = time;
                                       latLngs.add(new LatLng(Double.parseDouble(list.get(i).getLatitude()), Double.parseDouble(list.get(i).getLongitude())));

                                        if(list.get(i-1).getLatitude().equalsIgnoreCase(list.get(i).getLatitude())
                                                &&list.get(i-1).getLongitude().equalsIgnoreCase(list.get(i).getLongitude())
                                                &&(loginT!=null&&!loginT.isEmpty())&&(logoutT!=null&&!logoutT.isEmpty())){


                                            try {
                                                fd = sdf.parse(""+loginT);
                                                td = sdf.parse(""+logoutT);

                                                diff = td.getTime() - fd.getTime();

                                                minutes = (int) ((diff / (1000*60)) % 60);

                                                if(minutes>=10){

                                                   addValue = true;
                                                   indexValue = i;

                                                }



                                            } catch (ParseException e) {
                                                e.printStackTrace();

                                            }


                                        }else{

                                            if(addValue){



                                                locationA.setLatitude(Double.parseDouble(list.get(indexValue).getLatitude()));
                                                locationA.setLongitude(Double.parseDouble(list.get(indexValue).getLongitude()));



                                                locationB.setLatitude(Double.parseDouble(list.get(i).getLatitude()));
                                                locationB.setLongitude(Double.parseDouble(list.get(i).getLongitude()));

                                                distance = locationA.distanceTo(locationB);

                                                if(distance>=50){

                                                    addValue = false;
                                                    time = list.get(indexValue).getTrackingTime();
                                                    lt.add(list.get(indexValue));


                                                    snippet = getAddress(Double.parseDouble(list.get(indexValue).getLongitude()),Double.parseDouble(list.get(indexValue).getLatitude()));

                                                    createMarker(Double.parseDouble(list.get(indexValue).getLatitude()), Double.parseDouble(list.get(indexValue).getLongitude()),employee.getEmployeeName()+"\nLocation "+lt.size(),""+snippet);

                                                }


                                            }else if((logoutT!=null&&!logoutT.isEmpty())&&(loginT!=null&&!loginT.isEmpty())){

                                                locationA.setLatitude(Double.parseDouble(list.get(i-1).getLatitude()));
                                                locationA.setLongitude(Double.parseDouble(list.get(i-1).getLongitude()));

                                                locationB.setLatitude(Double.parseDouble(list.get(i).getLatitude()));
                                                locationB.setLongitude(Double.parseDouble(list.get(i).getLongitude()));

                                                distance = locationA.distanceTo(locationB);


                                                logoutT = list.get(i).getTrackingTime();
                                                loginT = list.get(i-1).getTrackingTime();


                                                try {
                                                    fd = sdf.parse(""+loginT);
                                                    td = sdf.parse(""+logoutT);

                                                    diff = td.getTime() - fd.getTime();

                                                    minutes = (int) ((diff / (1000*60)) % 60);

                                                    if(minutes>=10&&distance>=50){

                                                        lt.add(list.get(i));


                                                        snippet = getAddress(Double.parseDouble(list.get(i).getLongitude()),Double.parseDouble(list.get(i).getLatitude()));

                                                        createMarker(Double.parseDouble(list.get(i).getLatitude()), Double.parseDouble(list.get(i).getLongitude()),employee.getEmployeeName()+"\nLocation "+lt.size(),""+snippet);

                                                    }



                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                    if(distance>=50){
                                                        lt.add(list.get(i));


                                                        snippet = getAddress(Double.parseDouble(list.get(i).getLatitude()),Double.parseDouble(list.get(i).getLongitude()));

                                                        createMarker(Double.parseDouble(list.get(i).getLatitude()), Double.parseDouble(list.get(i).getLongitude()),employee.getEmployeeName()+"\nLocation "+lt.size(),""+snippet);
                                                    }

                                                }

                                            }


                                        }
                                    }


                                    //createMarker(Double.parseDouble(list.get(list.size()-1).getLatitude()), Double.parseDouble(list.get(list.size()-1).getLongitude()),employee.getEmployeeName()+"\nLocation "+lt.size(),""+snippetsr);
                                    createMarkerwithCustom(Double.parseDouble(list.get(list.size()-1).getLatitude()),
                                            Double.parseDouble(list.get(list.size()-1).getLongitude()),
                                            employee.getEmployeeName()+"\nLocation "+lt.size(),
                                            ""+getAddress(Double.parseDouble(list.get(list.size()-1).getLongitude()),Double.parseDouble(list.get(list.size()-1).getLatitude())),createCustomMarker(EmployeeLiveMappingScreen.this,employeeImage,employee.getEmployeeName(),R.drawable.live_location_black));
                                    latLngs.add(new LatLng(Double.parseDouble(list.get(list.size()-1).getLatitude()), Double.parseDouble(list.get(list.size()-1).getLongitude())));
                                    lt.add(list.get(list.size()-1));


                                    if(latLngs!=null&&latLngs.size()!=0){

                                        loadMap(latLngs);
                                    }

                                } else if(list.size()<3){
                                    lt.add(list.get(0));


                                    //createMarker(Double.parseDouble(list.get(0).getLatitude()), Double.parseDouble(list.get(0).getLongitude()),employee.getEmployeeName()+"\nLocation 1",""+snippet);
                                    createMarkerwithCustom(Double.parseDouble(list.get(0).getLatitude()),
                                            Double.parseDouble(list.get(0).getLongitude()),employee.getEmployeeName()+"\nLocation 1",""+getAddress(Double.parseDouble(list.get(0).getLongitude()),Double.parseDouble(list.get(0).getLatitude())),
                                            createCustomMarker(EmployeeLiveMappingScreen.this,null,employee.getEmployeeName(),R.drawable.home_map));

                                    lt.add(list.get(0));

                                    updateTrack(new LatLng(Double.parseDouble(list.get(0).getLatitude()), Double.parseDouble(list.get(0).getLongitude())));

                                    createMarker(Double.parseDouble(list.get(0).getLatitude()),
                                            Double.parseDouble(list.get(0).getLongitude()),
                                            employee.getEmployeeName()+"\nLocation 2",""+getAddress(Double.parseDouble(list.get(0).getLongitude()),Double.parseDouble(list.get(0).getLatitude())));

                                    lt.add(list.get(list.size()-1));

                                    updateTrack(new LatLng(Double.parseDouble(list.get(list.size()-1).getLatitude()), Double.parseDouble(list.get(list.size()-1).getLongitude())));
                                  //  createMarker(Double.parseDouble(list.get(list.size()-1).getLatitude()), Double.parseDouble(list.get(list.size()-1).getLongitude()),employee.getEmployeeName()+"\nLocation 3",""+snippetr);
                                   createMarkerwithCustom(Double.parseDouble(list.get(list.size()-1).getLatitude()),
                                           Double.parseDouble(list.get(list.size()-1).getLongitude()),
                                           employee.getEmployeeName()+"\nLocation 3",""+getAddress(Double.parseDouble(list.get(list.size()-1).getLongitude()),Double.parseDouble(list.get(list.size()-1).getLatitude())),createCustomMarker(EmployeeLiveMappingScreen.this,employeeImage,employee.getEmployeeName(),R.drawable.live_location_black));
                                }


                                if(lt!=null&&lt.size()!=0){
                                    mlocation.setVisibility(View.VISIBLE);
                                    mlocation.removeAllViews();
                                    onAddField(lt);
                                }else{

                                    mAddress.setText("No Location found");
                                    mShowHide.setVisibility(View.GONE);
                                }





                            }else{

                                //Toast.makeText(EmployeeLiveMappingScreen.this, "No Location found", Toast.LENGTH_SHORT).show();
                                mAddress.setText("No Location found");
                                mShowHide.setVisibility(View.GONE);

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

    protected Marker createMarkerwithCustom(double latitude, double longitude, String title, String snippet,Bitmap ima) {



        return mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .anchor(0.5f, 0.5f)
                .title(title)
                .snippet(snippet)
                .icon( BitmapDescriptorFactory.fromBitmap(ima)));


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

    private void updateTrack(final LatLng lastKnownLatLng) {

        List<LatLng> points = gpsTrack.getPoints();
        points.add(lastKnownLatLng);
        gpsTrack.setPoints(points);


    }

     public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.refresh_calendar_menu, menu);
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
                break;

            case R.id.action_calendar:

               openDatePicker();
               break;

            case R.id.action_request:

                requestLocation();
                break;

            case R.id.action_refresh:

                restartActivity(EmployeeLiveMappingScreen.this);
                //recreate();
                break;
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

                            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");



                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
                            try {
                                Date fdate = simpleDateFormat.parse(date1);

                                mLoadMap.setText(""+sdf.format(fdate));
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

        ViewGroup.LayoutParams params = scrollableLay.getLayoutParams();
// Changes the height and width to the specified *pixels*
        params.height = screenheight/2;
        params.width = screenwidth;
        scrollableLay.setLayoutParams(params);

        ViewGroup.LayoutParams paramsm = mMapShow.getLayoutParams();
        paramsm.width = screenwidth;
        paramsm.height = screenheight/2;
        mMapShow.setLayoutParams(paramsm);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.bottom_location_places, null);



        TextView mNo = (TextView) rowView.findViewById(R.id.no_result);
        mNo.setVisibility(View.GONE);
        RecyclerView list = (RecyclerView)rowView.findViewById(R.id.location_lists);

        list.removeAllViews();

        if(liveTrackingArrayList!=null&&liveTrackingArrayList.size()!=0){
            mShowHide.setVisibility(View.VISIBLE);

            if(colorValue!=null&&colorValue.size()!=0){
                colorValue.remove(0);
            }

            LocationLiveAdapter adapter = new LocationLiveAdapter(EmployeeLiveMappingScreen.this,liveTrackingArrayList,colorValue);
            list.setAdapter(adapter);
            double lng = Double.parseDouble(liveTrackingArrayList.get(liveTrackingArrayList.size()-1).getLongitude());
            double lat = Double.parseDouble(liveTrackingArrayList.get(liveTrackingArrayList.size()-1).getLatitude());
            getAddressValue(lng,lat,mAddress);
            mUpdatingTime.setText("Last updated at : "+liveTrackingArrayList.get(liveTrackingArrayList.size()-1).getTrackingTime());
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
                        holder.mItemTime.setText("Last Updated at "+parses);
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

    public void getAddressValue(final double longitude,final double latitude,final TextView textView  )
    {

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
            String local = addresses.get(0).getSubLocality();



            System.out.println("address = "+address);

            String currentLocation;

            if(!isEmpty(address))
            {
                currentLocation=address;



                textView.setText(currentLocation);

            }
            else{

                textView.setText("Unknown");

            }



        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public  Bitmap createCustomMarker(Context context, String urlImage, String _name, @DrawableRes int resource) {

        View marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_location, null);

        CircleImageView markerImage = (CircleImageView) marker.findViewById(R.id.user_dp);




        if(urlImage != null && !urlImage.isEmpty()){
            Picasso.with(EmployeeLiveMappingScreen.this).load(urlImage).placeholder(R.drawable.profile).error(R.drawable.profile).into(markerImage);


        }else{

            try {

                markerImage.setImageResource(resource);

            }catch (Exception e){
                e.printStackTrace();
                markerImage.setImageResource(R.drawable.location);
            }

        }
        //markerImage.setImageResource(resource);
       /* TextView txt_name = (TextView)marker.findViewById(R.id.name);
        txt_name.setText(_name);*/

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        marker.setLayoutParams(new ViewGroup.LayoutParams(52, ViewGroup.LayoutParams.WRAP_CONTENT));
        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(marker.getMeasuredWidth(), marker.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        marker.draw(canvas);

        return bitmap;
    }

    public static void restartActivity(Activity activity){
        if (Build.VERSION.SDK_INT >= 11) {
            activity.recreate();
        } else {
            activity.finish();
            activity.startActivity(activity.getIntent());
        }
    }


    public void requestLocation(){

        GeneralNotification gm = new GeneralNotification();
        gm.setEmployeeId(employeeId);
        gm.setSenderId(Constants.SENDER_ID);
        gm.setServerId(Constants.SERVER_ID);
        gm.setTitle("Location Request");
        gm.setMessage(PreferenceHandler.getInstance(EmployeeLiveMappingScreen.this).getUserFullName()+" is requesting your live location.%"+PreferenceHandler.getInstance(EmployeeLiveMappingScreen.this).getUserId());
        sendNotification(gm);
    }

    public void sendNotification(final GeneralNotification md){


        final ProgressDialog progressDialog = new ProgressDialog(EmployeeLiveMappingScreen.this);
        progressDialog.setTitle("Sending Request..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        GeneralNotificationAPI apiService = Util.getClient().create(GeneralNotificationAPI.class);

        Call<ArrayList<String>> call = apiService.sendGeneralNotification(md);

        call.enqueue(new Callback<ArrayList<String>>() {
            @Override
            public void onResponse(Call<ArrayList<String>> call, Response<ArrayList<String>> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();

                if(progressDialog!=null){

                    progressDialog.dismiss();
                }
                try
                {


                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201) {


                        Toast.makeText(EmployeeLiveMappingScreen.this, "Request Sent", Toast.LENGTH_SHORT).show();



                    }else {
                    }
                }
                catch (Exception ex)
                {


                    ex.printStackTrace();
                }
//                callGetStartEnd();
            }

            @Override
            public void onFailure(Call<ArrayList<String>> call, Throwable t) {

                if(progressDialog!=null){

                    progressDialog.dismiss();
                }
                Toast.makeText(EmployeeLiveMappingScreen.this, "Request failed", Toast.LENGTH_SHORT).show();

                Log.e("TAG", t.toString());
            }
        });



    }


    public void getProfile(final int id){

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setTitle("Please Wait..");
        dialog.show();

        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {

                final EmployeeApi subCategoryAPI = Util.getClient().create(EmployeeApi.class);
                Call<ArrayList<Employee>> getProf = subCategoryAPI.getProfileById(id);
                //Call<ArrayList<Blogs>> getBlog = blogApi.getBlogs();

                getProf.enqueue(new Callback<ArrayList<Employee>>() {

                    @Override
                    public void onResponse(Call<ArrayList<Employee>> call, Response<ArrayList<Employee>> response) {

                        if(dialog != null)
                        {
                            dialog.dismiss();
                        }
                        if (response.code() == 200)
                        {
                            System.out.println("Inside api");



                            if(response.body()!=null){
                                employee = response.body().get(0);

                                mName.setText(""+employee.getEmployeeName());

                                ArrayList<EmployeeImages> images = employee.getEmployeeImages();

                                if(images!=null&&images.size()!=0){
                                    EmployeeImages employeeImages = images.get(0);

                                    if(employeeImages!=null){


                                        employeeImage = employeeImages.getImage();

                                    }

                                }
                            }

                        }else{

                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Employee>> call, Throwable t) {

                        if(dialog != null)
                        {
                            dialog.dismiss();
                        }


                    }
                });

            }

        });
    }


    public void loadMap(final ArrayList<LatLng> latLngs){

        List<LatLng> points = gpsTrack.getPoints();
        points.addAll(latLngs);
        gpsTrack.setPoints(points);

    }


}
