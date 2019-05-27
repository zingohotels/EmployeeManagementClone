package app.zingo.employeemanagements.UI.NewAdminDesigns;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import app.zingo.employeemanagements.Model.LoginDetails;
import app.zingo.employeemanagements.Model.MarkerData;
import app.zingo.employeemanagements.Utils.DataParser;
import app.zingo.employeemanagements.base.R;

public class CheckInMapScreen extends AppCompatActivity {

    private GoogleMap mMap;
    MapView mapView;

    Marker mapMarker ;
    ArrayList<LatLng> MarkerPoints;


    LoginDetails ln;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        

        try{

            setContentView(R.layout.activity_check_in_map_screen);

            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Map View");

            mapView = findViewById(R.id.employee_login);

            mapView.onCreate(savedInstanceState);
            mapView.onResume();

            MarkerPoints = new ArrayList<>();



            Bundle bundle = getIntent().getExtras();

            if(bundle!=null){

                ln = (LoginDetails) bundle.getSerializable("Location");
            }

            try {
                MapsInitializer.initialize(CheckInMapScreen.this);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {


                    mMap = googleMap;

                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    mMap.getProjection().getVisibleRegion().latLngBounds.getCenter();


                    try{

                        if(ln!=null){

                            String title = ln.getTitle();
                            String status = "";



                            mMap.clear();
                            ArrayList<MarkerData> markerData = new ArrayList<>();
                            if(ln.getLongitude()!=null&&ln.getLatitude()!=null){


                                markerData.add(new MarkerData(Double.parseDouble(ln.getLongitude()),Double.parseDouble(ln.getLatitude()),""+title+" - "+status,ln.getLocation()));
                                markerData.add(new MarkerData(Double.parseDouble(ln.getLongitude()),Double.parseDouble(ln.getLatitude()),""+title+" - "+status,ln.getLocation()));

                                MarkerPoints.add(new LatLng(Double.parseDouble(ln.getLongitude()),Double.parseDouble(ln.getLatitude())));
                                MarkerPoints.add(new LatLng(Double.parseDouble(ln.getLongitude()),Double.parseDouble(ln.getLatitude())));

                            }

                            for (MarkerData point : markerData) {
                                mapMarker =   mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(point.getLati(), point.getLongi()))
                                        .anchor(0.5f, 0.5f)
                                        .title(point.getTitle())
                                        .snippet(point.getPerson())
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

                                //createMarker(point.getLati(),point.getLongi(), point.getTitle(), point.getPerson());
                            }

                            if(markerData!=null&&markerData.size()!=0){
                                LatLng latlng = new LatLng(markerData.get(0).getLati(),markerData.get(0).getLongi());

                                if (MarkerPoints.size() > 2) {
                                    LatLng origin = MarkerPoints.get(0);
                                    LatLng dest = MarkerPoints.get(3);

                                    // Getting URL to the Google Directions API
                                    String url = getUrl(origin, dest);
                                    Log.d("onMapClick", url);
                                    FetchUrl FetchUrl = new FetchUrl();

                                    // Start downloading json data from Google Directions API
                                    FetchUrl.execute(url);
                                    //move map camera
                                    mMap.moveCamera(CameraUpdateFactory.newLatLng(origin));
                                    mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
                                }


                                CameraPosition cameraPosition = new CameraPosition.Builder().zoom(100).target(latlng).build();
                                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                mapMarker.showInfoWindow();
                            }
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }


                }
            });

        }catch(Exception e){
            e.printStackTrace();
        }

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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id)
        {
            case android.R.id.home:

                CheckInMapScreen.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }


}
