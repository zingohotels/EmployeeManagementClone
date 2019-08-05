package app.zingo.employeemanagements.Adapter;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import app.zingo.employeemanagements.Custom.MyRegulerText;
import app.zingo.employeemanagements.Model.Employee;
import app.zingo.employeemanagements.Model.Meetings;
import app.zingo.employeemanagements.Utils.PreferenceHandler;
import app.zingo.employeemanagements.Utils.ThreadExecuter;
import app.zingo.employeemanagements.Utils.Util;
import app.zingo.employeemanagements.WebApi.EmployeeApi;
import app.zingo.employeemanagements.WebApi.MeetingsAPI;
import app.zingo.employeemanagements.base.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.text.TextUtils.isEmpty;

public class MeetingDetailAdapter extends RecyclerView.Adapter<MeetingDetailAdapter.ViewHolder>{

    private Context context;
    private ArrayList<Meetings> list;

    GoogleMap mMap;
    Marker marker;
    public MeetingDetailAdapter(Context context, ArrayList<Meetings> list) {

        this.context = context;
        this.list = list;


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_meeting_report_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Meetings dto = list.get(position);

        if(dto!=null){


            String status = dto.getStatus();


            holder.mTaskName.setText(dto.getMeetingDetails());
            holder.mTaskDesc.setText("Description: \n"+dto.getMeetingAgenda());

            String froms = dto.getStartTime();
            String tos = dto.getEndTime();

            if(froms!=null&&tos!=null){
                holder.mDuration.setText(froms+" to "+tos);
            }else if(froms!=null&&tos==null){
                holder.mDuration.setText(froms+"");
            }else{
                holder.mDuration.setVisibility(View.GONE);
            }


            holder.mDeadLine.setVisibility(View.GONE);
            //holder.mDeadLine.setText(dto.getDeadLine());
            holder.mStatus.setText(dto.getStatus());

            String lngi = dto.getEndLongitude();
            String lati = dto.getEndLatitude();
            String lngis = dto.getEndLongitude();
            String latis = dto.getEndLatitude();

            if(lngi!=null&&lati!=null&&!lngi.isEmpty()&&!lati.isEmpty()){

                try{

                    double lngiValue  = Double.parseDouble(lngi);
                    double latiValue  = Double.parseDouble(lati);

                    if(lngiValue!=0&&latiValue!=0){
                        getAddress(lngiValue,latiValue,holder.mLocation);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    holder.mLocation.setText("Not Available");
                }


            }else if(lngis!=null&&latis!=null&&!lngis.isEmpty()&&!latis.isEmpty()){

                try{

                    double lngiValue  = Double.parseDouble(lngis);
                    double latiValue  = Double.parseDouble(latis);

                    if(lngiValue!=0&&latiValue!=0){
                        getAddress(lngiValue,latiValue,holder.mLocation);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    holder.mLocation.setText("Not Available");
                }


            }

            getManagers(dto.getEmployeeId(),holder.mCreatedBy);
            // holder.mCreatedBy.setText(dto.getStatus());

           if(status.equalsIgnoreCase("Completed")){
                holder.mStatus.setBackgroundColor(Color.parseColor("#00FF00"));
            }else if(status.equalsIgnoreCase("In-Meeing")){
                holder.mStatus.setBackgroundColor(Color.parseColor("#D81B60"));
            }

            if(PreferenceHandler.getInstance(context).getUserRoleUniqueID()==2){
                holder.mContact.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        getEmployees(dto.getEmployeeId(),dto);

                    }
                });


            }else{
                holder.mContact.setVisibility(View.GONE);
                holder.mEdit.setVisibility(View.GONE);
            }

            holder.mEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    try{

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View views = inflater.inflate(R.layout.alert_meeting_update, null);

                        builder.setView(views);
                        String[] taskStatus = context.getResources().getStringArray(R.array.task_status);





                        final Spinner mTask = views.findViewById(R.id.task_status_update);
                        final Button mSave = views.findViewById(R.id.save);
                        final EditText desc = views.findViewById(R.id.task_comments);

                        final AlertDialog dialogs = builder.create();
                        dialogs.show();
                        dialogs.setCanceledOnTouchOutside(true);

                        if(dto.getStatus().equalsIgnoreCase("Pending")){

                            mTask.setSelection(0);
                        }else if(dto.getStatus().equalsIgnoreCase("On-Going")){
                            mTask.setSelection(1);

                        }else if(dto.getStatus().equalsIgnoreCase("Completed")){
                            mTask.setSelection(2);

                        }else if(dto.getStatus().equalsIgnoreCase("Closed")){
                            mTask.setSelection(3);

                        }



                        mSave.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Meetings tasks = dto;
                                tasks.setStatus(mTask.getSelectedItem().toString());
                                tasks.setDescription(desc.getText().toString());
                                try {
                                    updateMeetings(dto,dialogs);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        });











                    }catch (Exception e){
                        e.printStackTrace();
                    }


                }
            });


            holder.mMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    try{

                        final Dialog dialog = new Dialog(context);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        /////make map clear
                        //dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

                        dialog.setContentView(R.layout.activity_maps);////your custom content

                        MapView mMapView = dialog.findViewById(R.id.organization_map);
                        MapsInitializer.initialize(context);

                        mMapView.onCreate(dialog.onSaveInstanceState());
                        mMapView.onResume();


                        mMapView.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(final GoogleMap googleMap) {

                                try{

                                    //LatLng posisiabsen = new LatLng(Double.parseDouble(dto.getEndLatitude()), Double.parseDouble(dto.getEndLongitude())); ////your lat lng
                                   googleMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(dto.getEndLatitude()), Double.parseDouble(dto.getEndLongitude()))).title("Map View"));

                                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(Double.parseDouble(dto.getEndLatitude()), Double.parseDouble(dto.getEndLongitude()))));
                                    googleMap.getUiSettings().setZoomControlsEnabled(true);
                                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);

                                }catch (Exception e){
                                    e.printStackTrace();
                                    LatLng posisiabsen = new LatLng(Double.parseDouble(PreferenceHandler.getInstance(context).getOrganizationLati()), Double.parseDouble(PreferenceHandler.getInstance(context).getOrganizationLongi())); ////your lat lng
                                    googleMap.addMarker(new MarkerOptions().position(posisiabsen).title("Map View"));
                                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(posisiabsen));
                                    googleMap.getUiSettings().setZoomControlsEnabled(true);
                                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
                                }

                            }
                        });


                        dialog.show();
                        dialog.setCancelable(true);
                        dialog.setCanceledOnTouchOutside(true);


                    }catch (Exception e){
                        e.printStackTrace();
                    }


                }
            });

        }






    }

    private void getEmployees(final int id, final Meetings dto){




        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {
                EmployeeApi apiService = Util.getClient().create(EmployeeApi.class);
                Call<ArrayList<Employee>> call = apiService.getProfileById(id);

                call.enqueue(new Callback<ArrayList<Employee>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Employee>> call, Response<ArrayList<Employee>> response) {
                        int statusCode = response.code();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {


                           /* if (progressDialog != null&&progressDialog.isShowing())
                                progressDialog.dismiss();*/
                            ArrayList<Employee> list = response.body();


                            if (list !=null && list.size()!=0) {

                                final Employee employees = list.get(0);
                                if(employees!=null){
                                    try{

                                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                        View views = inflater.inflate(R.layout.alert_contact_employee, null);

                                        builder.setView(views);



                                        final MyRegulerText mEmpName = views.findViewById(R.id.employee_name);
                                        final MyRegulerText mPhone = views.findViewById(R.id.call_employee);
                                        final MyRegulerText mEmail = views.findViewById(R.id.email_employee);

                                        final AlertDialog dialogs = builder.create();
                                        dialogs.show();
                                        dialogs.setCanceledOnTouchOutside(true);


                                        mEmpName.setText("Contact "+employees.getEmployeeName());
                                        mPhone.setText("Call "+employees.getPhoneNumber());
                                        mEmail.setText("Email "+employees.getPrimaryEmailAddress());


                                        mPhone.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                                intent.setData(Uri.parse("tel:"+employees.getPhoneNumber()));
                                                context.startActivity(intent);
                                            }
                                        });

                                        mEmail.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                final Intent emailIntent = new Intent(Intent.ACTION_SEND);

                                                /* Fill it with Data */
                                                emailIntent.setType("plain/text");
                                                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{""+employees.getPrimaryEmailAddress()});
                                                emailIntent.putExtra(Intent.EXTRA_SUBJECT, ""+dto.getMeetingDetails());
                                                emailIntent.putExtra(Intent.EXTRA_TEXT, "");

                                                /* Send it off to the Activity-Chooser */
                                                context.startActivity(Intent.createChooser(emailIntent, "Send mail..."));

                                            }
                                        });









                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }





                                //}

                            }else{

                            }

                        }else {



                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Employee>> call, Throwable t) {
                        // Log error here since request failed
                      /*  if (progressDialog != null&&progressDialog.isShowing())
                            progressDialog.dismiss();*/

                        Log.e("TAG", t.toString());
                    }
                });
            }


        });
    }
    private void getManagers(final int id, final TextView textView){




        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {
                EmployeeApi apiService = Util.getClient().create(EmployeeApi.class);
                Call<ArrayList<Employee>> call = apiService.getProfileById(id);

                call.enqueue(new Callback<ArrayList<Employee>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Employee>> call, Response<ArrayList<Employee>> response) {
                        int statusCode = response.code();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {


                           /* if (progressDialog != null&&progressDialog.isShowing())
                                progressDialog.dismiss();*/
                            ArrayList<Employee> list = response.body();


                            if (list !=null && list.size()!=0) {

                                final Employee employees = list.get(0);
                                if(employees!=null){
                                    try{

                                        textView.setText("Created By "+employees.getEmployeeName());


                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }





                                //}

                            }else{

                            }

                        }else {



                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Employee>> call, Throwable t) {
                        // Log error here since request failed
                      /*  if (progressDialog != null&&progressDialog.isShowing())
                            progressDialog.dismiss();*/

                        Log.e("TAG", t.toString());
                    }
                });
            }


        });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder /*implements View.OnClickListener*/ {

        public TextView mTaskName,mTaskDesc,mDuration,mDeadLine,mStatus,mCreatedBy,mLocation;

        public LinearLayout mNotificationMain,mContact,mtaskUpdate;

        public  ImageView mEdit,mMap;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(true);

            mTaskName = itemView.findViewById(R.id.title_task);
            mTaskDesc = itemView.findViewById(R.id.title_description);
            mDuration = itemView.findViewById(R.id.time_task);
            mDeadLine = itemView.findViewById(R.id.dead_line_task);
            mStatus = itemView.findViewById(R.id.status);
            mCreatedBy = itemView.findViewById(R.id.created_by);
            mLocation = itemView.findViewById(R.id.task_location);
            mEdit = itemView.findViewById(R.id.update);
            mMap = itemView.findViewById(R.id.map);

            mNotificationMain = itemView.findViewById(R.id.attendanceItem);
            mContact = itemView.findViewById(R.id.contact_employee);
            mtaskUpdate = itemView.findViewById(R.id.task_update);


        }
    }

    public void updateMeetings(final Meetings tasks, final AlertDialog dialogs) {



        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage("Saving Details..");
        dialog.setCancelable(false);
        dialog.show();

        MeetingsAPI apiService = Util.getClient().create(MeetingsAPI.class);

        Call<Meetings> call = apiService.updateMeetingById(tasks.getMeetingsId(),tasks);

        call.enqueue(new Callback<Meetings>() {
            @Override
            public void onResponse(Call<Meetings> call, Response<Meetings> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {
                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201|| statusCode == 204) {


                        Toast.makeText(context, "Update Task succesfully", Toast.LENGTH_SHORT).show();

                        dialogs.dismiss();

                    }else {
                        Toast.makeText(context, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
            public void onFailure(Call<Meetings> call, Throwable t) {

                if(dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
                Toast.makeText( context , "Failed due to Bad Internet Connection" , Toast.LENGTH_SHORT ).show( );
                Log.e("TAG", t.toString());
            }
        });



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
}
