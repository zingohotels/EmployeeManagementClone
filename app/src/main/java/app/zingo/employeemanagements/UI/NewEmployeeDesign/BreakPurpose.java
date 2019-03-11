package app.zingo.employeemanagements.UI.NewEmployeeDesign;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import app.zingo.employeemanagements.Model.LoginDetailsNotificationManagers;
import app.zingo.employeemanagements.Model.Tasks;
import app.zingo.employeemanagements.R;
import app.zingo.employeemanagements.Service.DistanceCheck;
import app.zingo.employeemanagements.Utils.Constants;
import app.zingo.employeemanagements.Utils.PreferenceHandler;
import app.zingo.employeemanagements.Utils.Util;
import app.zingo.employeemanagements.WebApi.LoginNotificationAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BreakPurpose extends AppCompatActivity {

    Spinner mReason;
    EditText mDesc;
    Button mSave;

    String longitude="",latitude="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{

            setContentView(R.layout.activity_break_purpose);

            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Purpose");

            mReason = (Spinner) findViewById(R.id.task_status_update);
            mSave = (Button) findViewById(R.id.save);
            mDesc = (EditText) findViewById(R.id.task_comments);

            Bundle bundle = getIntent().getExtras();

            if(bundle!=null){

                longitude = bundle.getString("Longi");
                latitude = bundle.getString("Lati");
            }

            mSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    SimpleDateFormat sdt = new SimpleDateFormat("MMM dd,yyyy hh:mm a");
                    LoginDetailsNotificationManagers tasks = new LoginDetailsNotificationManagers();

                    LoginDetailsNotificationManagers md = new LoginDetailsNotificationManagers();
                    md.setTitle("Break taken from "+PreferenceHandler.getInstance(BreakPurpose.this).getUserFullName());
                    md.setMessage("Break taken at"+""+sdt.format(new Date()));
                    LatLng master = new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude));
                    String address = getAddress(master);
                    md.setLocation(address);
                    md.setLongitude(""+longitude);
                    md.setLatitude(""+latitude);
                    md.setLoginDate(""+sdt.format(new Date()));
                    md.setStatus("In meeting");
                    md.setEmployeeId(PreferenceHandler.getInstance(BreakPurpose.this).getUserId());
                    md.setManagerId(PreferenceHandler.getInstance(BreakPurpose.this).getManagerId());
                    try {
                        saveLoginNotification(md);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
            });


        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void saveLoginNotification(final LoginDetailsNotificationManagers md) throws Exception{



        final ProgressDialog dialog = new ProgressDialog(BreakPurpose.this);
        dialog.setMessage("Saving Details..");
        dialog.setCancelable(false);
        dialog.show();

        LoginNotificationAPI apiService = Util.getClient().create(LoginNotificationAPI.class);

        Call<LoginDetailsNotificationManagers> call = apiService.saveLoginNotification(md);

        call.enqueue(new Callback<LoginDetailsNotificationManagers>() {
            @Override
            public void onResponse(Call<LoginDetailsNotificationManagers> call, Response<LoginDetailsNotificationManagers> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {
                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201) {

                        LoginDetailsNotificationManagers s = response.body();

                        if(s!=null){

                            Intent lintent = new Intent(BreakPurpose.this, DistanceCheck.class);
                            stopService(lintent);
                            BreakPurpose.this.finish();

                        }




                    }else {
                        Toast.makeText(BreakPurpose.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
            public void onFailure(Call<LoginDetailsNotificationManagers> call, Throwable t) {

                if(dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
                Toast.makeText(BreakPurpose.this, "Failed Due to "+t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());
            }
        });



    }

    private String getAddress(LatLng latLng) {
        Geocoder geocoder = new Geocoder(BreakPurpose.this, Locale.getDefault());
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
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id)
        {
            case android.R.id.home:

                BreakPurpose.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
