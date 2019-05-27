package app.zingo.employeemanagements.UI.Common;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import app.zingo.employeemanagements.Custom.MyRegulerText;
import app.zingo.employeemanagements.Model.GeneralNotification;
import app.zingo.employeemanagements.Service.LocationForegroundService;
import app.zingo.employeemanagements.Utils.Constants;
import app.zingo.employeemanagements.Utils.PreferenceHandler;
import app.zingo.employeemanagements.Utils.Util;
import app.zingo.employeemanagements.WebApi.GeneralNotificationAPI;
import app.zingo.employeemanagements.base.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationSharingEmplActivity extends AppCompatActivity {

    TextView mTitle;
    MyRegulerText mShare;

    String message;
    int managerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{

            setContentView(R.layout.activity_location_sharing_empl);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Location Sharing");

            mTitle = findViewById(R.id.title_message);
            mShare = findViewById(R.id.button_share_location);

            Bundle bundle = getIntent().getExtras();

            if(bundle!=null){

                message = bundle.getString("Message");


            }

            if(message!=null&&!message.isEmpty()){

                if(message.contains("%")){

                    String arra[] = message.split("%");
                    if(arra.length>1){
                        mTitle.setText(""+arra[0]);

                        try{

                            managerId = Integer.parseInt(arra[1]);

                        }catch (Exception e){
                            e.printStackTrace();
                        }


                    }else if(arra.length==1){

                        mTitle.setText(""+arra[0]);
                        managerId = PreferenceHandler.getInstance(LocationSharingEmplActivity.this).getManagerId();

                    }else{

                        mTitle.setText("Share your live location with your manager.");
                        managerId = PreferenceHandler.getInstance(LocationSharingEmplActivity.this).getManagerId();
                    }

                }
            }else{
                mTitle.setText("Share your live location with your manager.");
                managerId = PreferenceHandler.getInstance(LocationSharingEmplActivity.this).getManagerId();
            }

            mShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(isNetworkAvailable()){

                        if(locationCheck()){


                            Intent intent = new Intent(LocationSharingEmplActivity.this, LocationForegroundService.class);
                            intent.setAction(LocationForegroundService.ACTION_START_FOREGROUND_SERVICE);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                startForegroundService(intent);
                            } else {
                                startService(intent);
                            }

                            requestLocation();
                        }

                    }else{

                        Toast.makeText(LocationSharingEmplActivity.this, "Turn on Internet", Toast.LENGTH_SHORT).show();

                    }


                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id)
        {
            case android.R.id.home:

                LocationSharingEmplActivity.this.finish();
                break;


        }
        return super.onOptionsItemSelected(item);
    }

    public boolean locationCheck(){

        final boolean status = false;
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(LocationSharingEmplActivity.this);
            dialog.setMessage("Location is not enable");
            dialog.setPositiveButton("Open Settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                    //get gps
                }
            });
            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub


                }
            });
            dialog.show();
            return false;
        }else{
            return true;
        }
    }

    public  boolean isNetworkAvailable() {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }


    public void requestLocation(){

        GeneralNotification gm = new GeneralNotification();
        gm.setEmployeeId(managerId);
        gm.setSenderId(Constants.SENDER_ID);
        gm.setServerId(Constants.SERVER_ID);
        gm.setTitle("Location Shared");
        gm.setMessage(PreferenceHandler.getInstance(LocationSharingEmplActivity.this).getUserFullName()+" shared live location with you.%"+ PreferenceHandler.getInstance(LocationSharingEmplActivity.this).getUserId());
        sendNotification(gm);
    }

    public void sendNotification(final GeneralNotification md){


        final ProgressDialog progressDialog = new ProgressDialog(LocationSharingEmplActivity.this);
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


                        Toast.makeText(LocationSharingEmplActivity.this, "Request Sent", Toast.LENGTH_SHORT).show();
                        LocationSharingEmplActivity.this.finish();



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
                Toast.makeText(LocationSharingEmplActivity.this, "Request failed", Toast.LENGTH_SHORT).show();

                Log.e("TAG", t.toString());
            }
        });



    }
}
