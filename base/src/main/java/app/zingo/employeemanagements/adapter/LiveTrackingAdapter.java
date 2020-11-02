package app.zingo.employeemanagements.adapter;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import app.zingo.employeemanagements.model.Employee;
import app.zingo.employeemanagements.model.LiveTracking;
import app.zingo.employeemanagements.ui.Admin.EmployeeLiveMappingScreen;
import app.zingo.employeemanagements.utils.ThreadExecuter;
import app.zingo.employeemanagements.utils.Util;
import app.zingo.employeemanagements.WebApi.EmployeeApi;
import app.zingo.employeemanagements.base.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.text.TextUtils.isEmpty;

public class LiveTrackingAdapter extends RecyclerView.Adapter< LiveTrackingAdapter.ViewHolder>{
    private Context context;
    private ArrayList<LiveTracking> list;

    public LiveTrackingAdapter(Context context, ArrayList<LiveTracking> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.dash_live_update, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final LiveTracking liveTrackingArrayList = list.get(position);
        if(liveTrackingArrayList!=null){
            getEmployees(liveTrackingArrayList.getEmployeeId(),holder.mEmpName);
            double lng = Double.parseDouble(liveTrackingArrayList.getLongitude());
            double lat = Double.parseDouble(liveTrackingArrayList.getLatitude());
            try {
                getAddress(lng,lat,holder.mAddress);
            } catch (Exception e) {
                e.printStackTrace();
            }
            holder.mupdated.setText("Last updated at : "+liveTrackingArrayList.getTrackingTime()+" ( Battery "+liveTrackingArrayList.getBatteryPercentage ()+" % )");

            holder.mLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(context, EmployeeLiveMappingScreen.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("EmployeeId",list.get(position).getEmployeeId());

                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            });

        }


    }

    private void getEmployees(final int id, final TextView dto){




        new ThreadExecuter ().execute( new Runnable() {
            @Override
            public void run() {
                EmployeeApi apiService = Util.getClient().create( EmployeeApi.class);
                Call<ArrayList<Employee>> call = apiService.getProfileById(id);

                call.enqueue(new Callback<ArrayList<Employee>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Employee>> call, Response<ArrayList<Employee>> response) {
                        int statusCode = response.code();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {


                           /* if (progressDialog!=null)
                                progressDialog.dismiss();*/
                            ArrayList<Employee> list = response.body();


                            if (list !=null && list.size()!=0) {

                                final Employee employees = list.get(0);
                                if(employees!=null){

                                    dto.setText(""+employees.getEmployeeName());
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
                      /*  if (progressDialog!=null)
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

        TextView mEmpName,mupdated,mAddress;


        /*  public TextView mTaskName,mTaskDesc,mDuration,mDeadLine,mStatus,mCreatedBy,mLocation,mToAllocate;*/

        //   public LinearLayout mNotificationMain,mContact,mtaskUpdate;
        public LinearLayout mLay;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(true);

            mEmpName = itemView.findViewById(R.id.emp_name_loca);
            mupdated = itemView.findViewById(R.id.updated_details);
            mAddress = itemView.findViewById(R.id.address);
            mLay = itemView.findViewById(R.id.live_lay);





        }
    }



    public void getAddress(final double longitude,final double latitude,final TextView textView ) {

        try
        {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(context, Locale.ENGLISH);


            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()


            String currentLocation;

            if(!isEmpty(address))
            {
                currentLocation=address;
                textView.setText(currentLocation);

            }
            else{

                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                textView.setText("Unnamed Location");

            }



        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            textView.setText("Unnamed Location");
        }
    }
}
