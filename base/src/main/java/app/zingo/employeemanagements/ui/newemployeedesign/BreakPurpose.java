package app.zingo.employeemanagements.ui.newemployeedesign;

import android.app.ProgressDialog;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import app.zingo.employeemanagements.model.LoginDetailsNotificationManagers;
import app.zingo.employeemanagements.model.OrganizationBreakTimes;
import app.zingo.employeemanagements.utils.PreferenceHandler;
import app.zingo.employeemanagements.utils.ThreadExecuter;
import app.zingo.employeemanagements.utils.Util;
import app.zingo.employeemanagements.WebApi.LoginNotificationAPI;
import app.zingo.employeemanagements.WebApi.OrganizationBreakTimesAPI;
import app.zingo.employeemanagements.base.R;
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

            mReason = findViewById(R.id.task_status_update);
            mSave = findViewById(R.id.save);
            mDesc = findViewById(R.id.task_comments);

            Bundle bundle = getIntent().getExtras();

            if(bundle!=null){

                longitude = bundle.getString("Longi");
                latitude = bundle.getString("Lati");
            }

            getBreaks(PreferenceHandler.getInstance( BreakPurpose.this).getCompanyId());

            mSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    SimpleDateFormat sdt = new SimpleDateFormat("MMM dd,yyyy hh:mm a");
                    LoginDetailsNotificationManagers tasks = new LoginDetailsNotificationManagers();

                    LoginDetailsNotificationManagers md = new LoginDetailsNotificationManagers();
                    md.setTitle("Break taken from "+ PreferenceHandler.getInstance( BreakPurpose.this).getUserFullName());
                    md.setMessage("Break taken at"+""+sdt.format(new Date()));
                    LatLng master = new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude));
                    String address = getAddress(master);
                    md.setLocation(address);
                    md.setLongitude(""+longitude);
                    md.setLatitude(""+latitude);
                    md.setLoginDate(""+sdt.format(new Date()));
                    md.setStatus(""+mReason.getSelectedItem().toString());
                    md.setEmployeeId(PreferenceHandler.getInstance( BreakPurpose.this).getUserId());
                    md.setManagerId(PreferenceHandler.getInstance( BreakPurpose.this).getManagerId());
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

    public void saveLoginNotification(final LoginDetailsNotificationManagers md) {



        final ProgressDialog dialog = new ProgressDialog( BreakPurpose.this);
        dialog.setMessage("Saving Details..");
        dialog.setCancelable(false);
        dialog.show();

        LoginNotificationAPI apiService = Util.getClient().create( LoginNotificationAPI.class);

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

                        PreferenceHandler.getInstance( BreakPurpose.this).setFar(true);




                    }else {
                        Toast.makeText( BreakPurpose.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText( BreakPurpose.this , "Failed due to Bad Internet Connection" , Toast.LENGTH_SHORT ).show( );
                Log.e("TAG", t.toString());
            }
        });



    }

    private String getAddress(LatLng latLng) {
        Geocoder geocoder = new Geocoder( BreakPurpose.this, Locale.getDefault());
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

    public void getBreaks(final int id) {

        final ProgressDialog progressDialog = new ProgressDialog( BreakPurpose.this);
        progressDialog.setTitle("Loading Details");
        progressDialog.setCancelable(false);
        progressDialog.show();


        new ThreadExecuter ().execute( new Runnable() {
            @Override
            public void run() {

                final OrganizationBreakTimesAPI orgApi = Util.getClient().create( OrganizationBreakTimesAPI.class);
                Call<ArrayList< OrganizationBreakTimes >> getProf = orgApi.getBreaksByOrgId(id);
                //Call<ArrayList<Blogs>> getBlog = blogApi.getBlogs();

                getProf.enqueue(new Callback<ArrayList< OrganizationBreakTimes >>() {

                    @Override
                    public void onResponse( Call<ArrayList< OrganizationBreakTimes >> call, Response<ArrayList< OrganizationBreakTimes >> response) {


                           if (progressDialog!=null&&progressDialog.isShowing())
                                progressDialog.dismiss();


                        if (response.code() == 200||response.code() == 201||response.code() == 204)
                        {


                            ArrayList< OrganizationBreakTimes > holidayLists = response.body();


                            if(holidayLists!=null&&holidayLists.size()!=0){

                                ArrayList<String> breakName = new ArrayList<>();

                                for(int i=0;i<holidayLists.size();i++){

                                        breakName.add(holidayLists.get(i).getBreakName());

                                }

                                if(breakName!=null&&breakName.size()!=0){

                                    ArrayAdapter adapter = new ArrayAdapter<>( BreakPurpose.this, R.layout.spinner_item_selected, breakName);
                                    adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

                                    // DepartmentSpinnerAdapter arrayAdapter = new DepartmentSpinnerAdapter(EmployeeSignUp.this, departmentData);
                                    mReason.setAdapter(adapter);

                                }


                            }else{


                            }


                        }else{



                            Toast.makeText( BreakPurpose.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onFailure( Call<ArrayList< OrganizationBreakTimes >> call, Throwable t) {

                        if (progressDialog!=null&&progressDialog.isShowing())
                            progressDialog.dismiss();


                        Toast.makeText( BreakPurpose.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                    }
                });

            }

        });
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
