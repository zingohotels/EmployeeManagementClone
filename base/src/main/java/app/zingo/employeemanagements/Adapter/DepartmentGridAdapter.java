package app.zingo.employeemanagements.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import app.zingo.employeemanagements.Model.Departments;
import app.zingo.employeemanagements.base.R;
import app.zingo.employeemanagements.UI.Admin.DepartmentEmployeeListScreen;

/**
 * Created by ZingoHotels Tech on 29-09-2018.
 */

public class DepartmentGridAdapter extends BaseAdapter {

    Context context;
    ArrayList<Departments> mList;
    public DepartmentGridAdapter(Context context, ArrayList<Departments> mList)
    {
        this.context = context;
        this.mList = mList;
    }
    @Override
    public int getCount() {
        //System.out.println("class SelectRoomGridViewAdapter = "+rooms.size());

        return mList.size();

    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        try{
            if(convertView == null)
            {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.department_grid_view,parent,false);
            }

            final ImageView mSubImage = convertView.findViewById(R.id.event_banner_image);
            final TextView mSubName = convertView.findViewById(R.id.sub_name);
            final CardView mainLayout = convertView.findViewById(R.id.event_layout);

            if(mList.get(position)!=null){
                mSubName.setText(""+mList.get(position).getDepartmentName());

            }

            mainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(context, DepartmentEmployeeListScreen.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("Department",mList.get(position));
                    intent.putExtras(bundle);
                    context.startActivity(intent);

                }
            });

            return convertView;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }


    }
}
