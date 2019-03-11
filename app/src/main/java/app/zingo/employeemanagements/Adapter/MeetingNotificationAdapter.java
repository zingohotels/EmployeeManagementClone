package app.zingo.employeemanagements.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import app.zingo.employeemanagements.Model.LoginDetailsNotificationManagers;
import app.zingo.employeemanagements.Model.MeetingDetailsNotificationManagers;
import app.zingo.employeemanagements.Model.MeetingNotification;
import app.zingo.employeemanagements.R;
import app.zingo.employeemanagements.UI.NewAdminDesigns.LoginMapScreenAdmin;
import app.zingo.employeemanagements.UI.NewAdminDesigns.MeetingMapScreen;

import static android.text.TextUtils.isEmpty;

public class MeetingNotificationAdapter extends RecyclerView.Adapter<MeetingNotificationAdapter.ViewHolder>{

    private Context context;
    private ArrayList<MeetingDetailsNotificationManagers> list;


    public MeetingNotificationAdapter(Context context, ArrayList<MeetingDetailsNotificationManagers> list) {

        this.context = context;
        this.list = list;


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_login_notifications, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final MeetingDetailsNotificationManagers dto = list.get(position);

        if(dto!=null){

            String title = dto.getTitle();
            String status = dto.getStatus();

            if(title.contains("Login Details from ")){
                title = title.replace("Login Details from ","");
            }

            if(status.equalsIgnoreCase("In meeting")||status.equalsIgnoreCase("Login")){
                status = "Login";
            }
            holder.mTitle.setText(title+" - "+status);
            holder.mTime.setText(dto.getMeetingDate());

            if(dto.getLocation()==null){
                getAddress(Double.parseDouble(dto.getLatitude()),Double.parseDouble(dto.getLongitude()),holder.mAddress);
            }else{
                holder.mAddress.setText(dto.getLocation());
            }

            holder.mNotificationMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {



                    Intent map = new Intent(context,MeetingMapScreen.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("Location",dto);
                    map.putExtras(bundle);
                    ((Activity)context).startActivity(map);
                }
            });

        }






    }




    @Override
    public int getItemCount() {
        return list.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder /*implements View.OnClickListener*/ {

        public TextView mTitle,mTime,mAddress;

        public LinearLayout mNotificationMain;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(true);

            mTitle = (TextView)itemView.findViewById(R.id.title_login_notifications);
            mTime = (TextView)itemView.findViewById(R.id.time_login);
            mAddress = (TextView)itemView.findViewById(R.id.login_address);
            mNotificationMain = (LinearLayout) itemView.findViewById(R.id.attendanceItem);


        }
    }

    public void getAddress(final double latitude,final double longitude,final TextView mAddress)
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
                mAddress.setText(currentLocation);

            }
            else{
                mAddress.setVisibility(View.GONE);
            }
            //Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();


        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
