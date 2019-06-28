package app.zingo.employeemanagements.UI.Admin;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import app.zingo.employeemanagements.Model.LiveTracking;
import app.zingo.employeemanagements.Model.Tasks;
import app.zingo.employeemanagements.Utils.DataParser;
import app.zingo.employeemanagements.Utils.ThreadExecuter;
import app.zingo.employeemanagements.Utils.Util;
import app.zingo.employeemanagements.WebApi.TasksAPI;
import app.zingo.employeemanagements.base.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.text.TextUtils.isEmpty;

public class EmployeeOrderMapScreen extends AppCompatActivity {

    //maps related
    private GoogleMap mMap;
    MapView mapView;
    LinearLayout mlocation;


    ArrayList<LatLng> MarkerPoints;

    private LatLng lastKnownLatLng;

    int employeeId;



    String passDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
            setContentView(R.layout.activity_employee_order_map_screen);

            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Task Location");

            mapView = findViewById(R.id.employee_live_list_map);
            mlocation = findViewById(R.id.location_list);

            mapView.onCreate(savedInstanceState);
            mapView.onResume();

            MarkerPoints = new ArrayList<>();



            Bundle bundle = getIntent().getExtras();

            if(bundle!=null){

                employeeId = bundle.getInt("EmployeeId");
                passDate = bundle.getString("Date");
            }

            try {
                MapsInitializer.initialize(EmployeeOrderMapScreen.this);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mMap = googleMap;


                    if (ActivityCompat.checkSelfPermission(EmployeeOrderMapScreen.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(EmployeeOrderMapScreen.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        return;
                    }
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    mMap.getProjection().getVisibleRegion().latLngBounds.getCenter();


                    try{


                        if(passDate!=null&&!passDate.isEmpty()){
                            getTasks(employeeId,passDate);
                        }else{
                            getTasks(employeeId,new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                        }


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

    private void getTasks(final int employeeId,final String dateValue){


        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {
                TasksAPI apiService = Util.getClient().create(TasksAPI.class);
                Call<ArrayList<Tasks>> call = apiService.getTasksByEmployeeId(employeeId);

                call.enqueue(new Callback<ArrayList<Tasks>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Tasks>> call, Response<ArrayList<Tasks>> response) {
                        int statusCode = response.code();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {



                            ArrayList<Tasks> list = response.body();
                            ArrayList<Tasks> todayTasks = new ArrayList<>();


                            Date date = new Date();
                            Date adate = new Date();
                            Date edate = new Date();



                            try {
                                date = new SimpleDateFormat("yyyy-MM-dd").parse(dateValue);


                            } catch (Exception e) {
                                e.printStackTrace();
                            }



                            if (list !=null && list.size()!=0) {



                                for (Tasks task:list) {

                                    if(task.getCategory()==null){


                                        String froms = task.getStartDate();
                                        String tos = task.getEndDate();

                                        Date afromDate = null;
                                        Date atoDate = null;

                                        if(froms!=null&&!froms.isEmpty()){

                                            if(froms.contains("T")){

                                                String dojs[] = froms.split("T");

                                                try {
                                                    afromDate = new SimpleDateFormat("yyyy-MM-dd").parse(dojs[0]);
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }
                                             /*   String parse = new SimpleDateFormat("MMM yyyy").format(afromDate);
                                                fromDate = new SimpleDateFormat("MMM yyyy").parse(parse);*/

                                            }

                                        }

                                        if(tos!=null&&!tos.isEmpty()){

                                            if(tos.contains("T")){

                                                String dojs[] = tos.split("T");

                                                try {
                                                    atoDate = new SimpleDateFormat("yyyy-MM-dd").parse(dojs[0]);
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }
                                              /*  String parse = new SimpleDateFormat("MMM yyyy").format(atoDate);
                                                toDate = new SimpleDateFormat("MMM yyyy").parse(parse);*/

                                            }

                                        }

                                        if(afromDate!=null&&atoDate!=null){



                                            if(date.getTime() >= afromDate.getTime() && date.getTime() <= atoDate.getTime()){


                                                if(task.getLatitude()!=null&&task.getLongitude()!=null){

                                                    double lngiValue  = Double.parseDouble(task.getLongitude());
                                                    double latiValue  = Double.parseDouble(task.getLatitude());

                                                    if(lngiValue!=0&&latiValue!=0){
                                                        todayTasks.add(task);
                                                    }

                                                }



                                            }
                                        }




                                    }






                                }

                                if(todayTasks!=null&&todayTasks.size()!=0){

                                    mMap.clear();

                                    LatLng calymayor = new LatLng(Double.parseDouble(todayTasks.get(0).getLatitude()), Double.parseDouble(todayTasks.get(0).getLongitude()));
                                    mMap.moveCamera(CameraUpdateFactory.newLatLng(calymayor));
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(calymayor, 15));

                                 /*   PolylineOptions polylineOptions = new PolylineOptions();
                                    polylineOptions.color(Color.BLUE);
                                    polylineOptions.width(30);

                                    gpsTrack = mMap.addPolyline(polylineOptions);*/

                                    ArrayList<Tasks> lt = new ArrayList<>();

                                    int k=0;


                                    for(int i=0;i<todayTasks.size();i++){



                                        if(todayTasks.get(i).getLongitude()!=null||todayTasks.get(i).getLatitude()!=null){

                                            double lat = Double.parseDouble(todayTasks.get(i).getLatitude());
                                            double lng = Double.parseDouble(todayTasks.get(i).getLongitude());

                                            if(lat==0&&lng==0){

                                            }else{
                                                lastKnownLatLng = new LatLng(Double.parseDouble(todayTasks.get(i).getLatitude()), Double.parseDouble(todayTasks.get(i).getLongitude()));
                                                //    updateTrack();
                                                String snippetL = getAddress(Double.parseDouble(todayTasks.get(i).getLongitude()),Double.parseDouble(todayTasks.get(i).getLatitude()));

                                                createMarker(Double.parseDouble(todayTasks.get(i).getLatitude()), Double.parseDouble(todayTasks.get(i).getLongitude()),"Location "+(i+1),""+snippetL);

                                                k=i;

                                            }



                                        }


                                    }

                                    String snippetL = getAddress(Double.parseDouble(todayTasks.get(k).getLongitude()),Double.parseDouble(todayTasks.get(k).getLatitude()));

                                    createMarker(Double.parseDouble(todayTasks.get(k).getLatitude()), Double.parseDouble(todayTasks.get(k).getLongitude()),"Last Location",""+snippetL);

                                    if(lastKnownLatLng!=null){
                                        CameraPosition cameraPosition = new CameraPosition.Builder().zoom(35).target(lastKnownLatLng).build();
                                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                    }



                                   /* if(todayTasks.size()==3){

                                        //lt.add(list.get(0));

                                        String snippet = getAddress(Double.parseDouble(todayTasks.get(0).getLongitude()),Double.parseDouble(todayTasks.get(0).getLatitude()));

                                        createMarker(Double.parseDouble(todayTasks.get(0).getLatitude()), Double.parseDouble(todayTasks.get(0).getLongitude()),"Location 1",""+snippet);
                                      //  lt.add(todayTasks.get(1));

                                        String snippets = getAddress(Double.parseDouble(todayTasks.get(1).getLongitude()),Double.parseDouble(todayTasks.get(1).getLatitude()));

                                        createMarker(Double.parseDouble(todayTasks.get(1).getLatitude()), Double.parseDouble(todayTasks.get(1).getLongitude()),"Location 2",""+snippets);
                                       // lt.add(todayTasks.get(2));

                                        String snippetr = getAddress(Double.parseDouble(todayTasks.get(2).getLongitude()),Double.parseDouble(todayTasks.get(2).getLatitude()));

                                        createMarker(Double.parseDouble(todayTasks.get(2).getLatitude()), Double.parseDouble(todayTasks.get(2).getLongitude()),"Location 3",""+snippetr);

                                    }else if(todayTasks.size()>3){

                                       // lt.add(todayTasks.get(0));

                                        String snippets = getAddress(Double.parseDouble(todayTasks.get(0).getLongitude()),Double.parseDouble(todayTasks.get(0).getLatitude()));

                                        createMarker(Double.parseDouble(todayTasks.get(0).getLatitude()), Double.parseDouble(todayTasks.get(0).getLongitude()),"Location "+lt.size(),""+snippets);

                                        String lati = todayTasks.get(0).getLatitude();
                                        String lngi = todayTasks.get(0).getLongitude();

                                        for(int i=1;i<todayTasks.size()-1;i++){

                                            if(todayTasks.get(i-1).getLatitude().equalsIgnoreCase(todayTasks.get(i).getLatitude())&&todayTasks.get(i-1).getLongitude().equalsIgnoreCase(todayTasks.get(i).getLongitude())){

                                                Location locationA = new Location("point A");

                                                locationA.setLatitude(Double.parseDouble(lati));
                                                locationA.setLongitude(Double.parseDouble(lngi));

                                                Location locationB = new Location("point B");

                                                locationB.setLatitude(Double.parseDouble(todayTasks.get(i).getLatitude()));
                                                locationB.setLongitude(Double.parseDouble(todayTasks.get(i).getLongitude()));

                                                float distance = locationA.distanceTo(locationB);

                                                if(distance>=100){
                                                    lt.add(todayTasks.get(i));
                                                    lati = todayTasks.get(i).getLatitude();
                                                    lngi = todayTasks.get(i).getLongitude();

                                                    String snippet = getAddress(Double.parseDouble(lngi),Double.parseDouble(lati));

                                                    createMarker(Double.parseDouble(todayTasks.get(i).getLatitude()), Double.parseDouble(todayTasks.get(i).getLongitude()),"Location "+lt.size(),""+snippet);
                                                }

                                            }
                                        }
                                        String snippetsr = getAddress(Double.parseDouble(todayTasks.get(todayTasks.size()-1).getLongitude()),Double.parseDouble(todayTasks.get(todayTasks.size()-1).getLatitude()));

                                        createMarker(Double.parseDouble(todayTasks.get(todayTasks.size()-1).getLatitude()), Double.parseDouble(todayTasks.get(todayTasks.size()-1).getLongitude()),"Location "+lt.size(),""+snippetsr);
                                        lt.add(todayTasks.get(todayTasks.size()-1));

                                    } else if(todayTasks.size()<3){
                                        lt.add(todayTasks.get(0));
                                        String snippet = getAddress(Double.parseDouble(todayTasks.get(0).getLongitude()),Double.parseDouble(todayTasks.get(0).getLatitude()));

                                        createMarker(Double.parseDouble(todayTasks.get(0).getLatitude()), Double.parseDouble(todayTasks.get(0).getLongitude()),"Location 1",""+snippet);

                                        lt.add(todayTasks.get(0));

                                        String snippets = getAddress(Double.parseDouble(todayTasks.get(0).getLongitude()),Double.parseDouble(todayTasks.get(0).getLatitude()));

                                        createMarker(Double.parseDouble(todayTasks.get(0).getLatitude()), Double.parseDouble(todayTasks.get(0).getLongitude()),"Location 2",""+snippets);
                                        lt.add(todayTasks.get(todayTasks.size()-1));

                                        String snippetr = getAddress(Double.parseDouble(todayTasks.get(todayTasks.size()-1).getLongitude()),Double.parseDouble(todayTasks.get(todayTasks.size()-1).getLatitude()));

                                        createMarker(Double.parseDouble(todayTasks.get(todayTasks.size()-1).getLatitude()), Double.parseDouble(todayTasks.get(todayTasks.size()-1).getLongitude()),"Location 3",""+snippetr);
                                    }*/


                                   /* if(lt!=null&&lt.size()!=0){
                                        mlocation.setVisibility(View.VISIBLE);
                                        mlocation.removeAllViews();
                                        onAddField(lt);
                                    }else{

                                    }*/


                                }else{

                                    Toast.makeText(EmployeeOrderMapScreen.this, "No tasks based on location", Toast.LENGTH_SHORT).show();

                                }



                            }else{

                                // Toast.makeText(DailyTargetsForEmployeeActivity.this, "No Tasks given for this employee ", Toast.LENGTH_SHORT).show();

                            }

                        }else {



                            //Toast.makeText(DailyTargetsForEmployeeActivity.this, "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Tasks>> call, Throwable t) {
                        // Log error here since request failed
                       /* if (progressDialog!=null)
                            progressDialog.dismiss();*/
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
                Log.d("Background Task data", data);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            EmployeeOrderMapScreen.ParserTask parserTask = new EmployeeOrderMapScreen.ParserTask();

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
                Log.d("ParserTask", jsonData[0]);


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
            Log.d("downloadUrl", data);
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
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

        if (id == android.R.id.home) {
            EmployeeOrderMapScreen.this.finish();



        } else if (id == R.id.action_calendar) {
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

                               /* LiveTracking lv = new LiveTracking();
                                lv.setEmployeeId(employeeId);
                                lv.setTrackingDate(date1);
                                getLiveLocation(lv);*/

                                getTasks(employeeId,new SimpleDateFormat("yyyy-MM-dd").format(fdate));




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

        if(datePickerDialog!=null)
            datePickerDialog.show();

    }

    public void onAddField(final  ArrayList<LiveTracking> liveTrackingArrayList) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.bottom_location_places, null);

        TextView mNo = rowView.findViewById(R.id.no_result);
        mNo.setVisibility(View.GONE);
        RecyclerView list = rowView.findViewById(R.id.location_lists);

        list.removeAllViews();

        if(liveTrackingArrayList!=null&&liveTrackingArrayList.size()!=0){
           /* LocationLiveAdapter adapter = new LocationLiveAdapter(EmployeeOrderMapScreen.this,liveTrackingArrayList);
            list.setAdapter(adapter);*/
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
            Toast.makeText(EmployeeOrderMapScreen.this,"Atleast one email extension needed",Toast.LENGTH_SHORT).show();
        }

    }

    public String getAddress(final double longitude,final double latitude )
    {

        String addressValue = "";
        try
        {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(EmployeeOrderMapScreen.this, Locale.ENGLISH);


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

   /* public class LocationLiveAdapter extends RecyclerView.Adapter<LocationLiveAdapter.ViewHolder>{

        private Context context;
        private ArrayList<LiveTracking> list;
        private static final int VIEW_TYPE_TOP = 0;
        private static final int VIEW_TYPE_MIDDLE = 1;
        private static final int VIEW_TYPE_BOTTOM = 2;


        public LocationLiveAdapter(Context context, ArrayList<LiveTracking> list) {

            this.context = context;
            this.list = list;


        }

        @Override
        public LocationLiveAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.adapter_location_like_google, parent, false);
            LocationLiveAdapter.ViewHolder viewHolder = new LocationLiveAdapter.ViewHolder(v);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final LocationLiveAdapter.ViewHolder holder, final int position) {


            LiveTracking item = list.get(position);


            String froms = item.getTrackingDate();
            String times = item.getTrackingTime();

            Date fromDate = null;

            Date afromDate = null;


            if(froms!=null&&!froms.isEmpty()) {

                if (froms.contains("T")) {

                    String dojs[] = froms.split("T");

                    try {
                        afromDate = new SimpleDateFormat("yyyy-MM-dd").parse(dojs[0]);

                        if(times==null||times.isEmpty()){
                            times = "00:00:00";
                        }
                        fromDate = new SimpleDateFormat("HH:mm:ss").parse(times);

                        String parse = new SimpleDateFormat("MMM dd, yyyy").format(afromDate);
                        String parses = new SimpleDateFormat("hh:mm a").format(fromDate);
                        holder.mItemTitle.setText(""+parse+" "+parses);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }


                }

            }
            double lng = Double.parseDouble(item.getLongitude());
            double lat = Double.parseDouble(item.getLatitude());
            getAddress(lng,lat,holder.mItemSubtitle);

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
            final int sdk = android.os.Build.VERSION.SDK_INT;
            // Populate views...
            switch(holder.getItemViewType()) {
                case VIEW_TYPE_TOP:
                    // The top of the line has to be rounded

                    if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        holder.mItemLine.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.line_bg_top) );
                    } else {
                        holder.mItemLine.setBackground(ContextCompat.getDrawable(context, R.drawable.line_bg_top));
                    }

                    break;
                case VIEW_TYPE_MIDDLE:
                    // Only the color could be enough
                    // but a drawable can be used to make the cap rounded also here

                    if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        holder.mItemLine.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.line_bg_middle) );
                    } else {
                        holder.mItemLine.setBackground(ContextCompat.getDrawable(context, R.drawable.line_bg_middle));
                    }

                    break;
                case VIEW_TYPE_BOTTOM:

                    if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        holder.mItemLine.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.line_bg_bottom) );
                    } else {
                        holder.mItemLine.setBackground(ContextCompat.getDrawable(context, R.drawable.line_bg_bottom));
                    }

                    break;
            }

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


        class ViewHolder extends RecyclerView.ViewHolder *//*implements View.OnClickListener*//* {

            TextView mItemTitle;
            TextView mItemSubtitle;
            FrameLayout mItemLine;

            public ViewHolder(View itemView) {
                super(itemView);
                itemView.setClickable(true);

                mItemTitle = (TextView) itemView.findViewById(R.id.item_title);
                mItemSubtitle = (TextView) itemView.findViewById(R.id.item_subtitle);
                mItemLine = (FrameLayout) itemView.findViewById(R.id.item_line);


            }
        }

        public void getAddress(final double longitude,final double latitude,final TextView textView )
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



                System.out.println("address = "+address);

                String currentLocation;

                if(!isEmpty(address))
                {
                    currentLocation=address;
                    textView.setText(currentLocation);

                }
                else
                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();


            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }*/


}
