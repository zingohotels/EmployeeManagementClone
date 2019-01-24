package app.zingo.employeemanagements.Adapter;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import app.zingo.employeemanagements.Model.Employee;
import app.zingo.employeemanagements.Model.LiveTracking;
import app.zingo.employeemanagements.R;
import app.zingo.employeemanagements.UI.GetStartedScreen;

import static android.text.TextUtils.isEmpty;

public class LocationLiveAdapter extends RecyclerView.Adapter<LocationLiveAdapter.ViewHolder>{

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
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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


    class ViewHolder extends RecyclerView.ViewHolder /*implements View.OnClickListener*/ {

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
}
