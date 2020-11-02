package app.zingo.employeemanagements.adapter;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import app.zingo.employeemanagements.model.LoginDetailsNotificationManagers;
import app.zingo.employeemanagements.ui.NewAdminDesigns.LoginMapScreenAdmin;
import app.zingo.employeemanagements.base.R;

import static android.text.TextUtils.isEmpty;

/**
 * Created by ZingoHotels Tech on 07-01-2019.
 */

public class LoginDetailsNotificationAdapter  extends RecyclerView.Adapter< LoginDetailsNotificationAdapter.ViewHolder>{
    private Context context;
    private ArrayList< LoginDetailsNotificationManagers > list;

    public LoginDetailsNotificationAdapter(Context context, ArrayList< LoginDetailsNotificationManagers > list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_login_notifications, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final LoginDetailsNotificationManagers dto = list.get(position);
        Gson gson = new Gson ();
        String json = gson.toJson ( dto );
        System.out.println("Suree :"+json);
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
            holder.mTime.setText(dto.getLoginDate());

            if(dto.getLocation()==null&&dto.getLatitude()!=null&&dto.getLongitude ()!=null){
                getAddress(Double.parseDouble(dto.getLatitude()),Double.parseDouble(dto.getLongitude()),holder.mAddress);
            }else if(dto.getLogoutLocation ()==null&&dto.getLogoutLatitude()!=null&&dto.getLogoutLongitude ()!=null){
                getAddress(Double.parseDouble(dto.getLogoutLatitude ()),Double.parseDouble(dto.getLogoutLongitude ()),holder.mAddress);
            }else{
                holder.mAddress.setText(dto.getLocation());
            }

            holder.mNotificationMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent map = new Intent(context, LoginMapScreenAdmin.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("Location",dto);
                    map.putExtras(bundle);
                    context.startActivity(map);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder /*implements View.OnClickListener*/ {
        public TextView mTitle,mTime,mAddress;
        public LinearLayout mNotificationMain;
        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(true);
            mTitle = itemView.findViewById(R.id.title_login_notifications);
            mTime = itemView.findViewById(R.id.time_login);
            mAddress = itemView.findViewById(R.id.login_address);
            mNotificationMain = itemView.findViewById(R.id.attendanceItem);
        }
    }

    public void getAddress(final double latitude,final double longitude,final TextView mAddress) {
        try {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(context, Locale.ENGLISH);
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            if(addresses!=null&&addresses.size()!=0){
                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName();
                String currentLocation;

                if(!isEmpty(address)) {
                    currentLocation=address;
                    mAddress.setText(currentLocation);
                }
                else{
                    mAddress.setVisibility(View.GONE);
                }

            }else{
                mAddress.setVisibility(View.GONE);
            }
            //Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
