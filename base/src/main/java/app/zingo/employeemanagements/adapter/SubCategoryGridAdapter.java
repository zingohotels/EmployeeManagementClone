package app.zingo.employeemanagements.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import app.zingo.employeemanagements.base.R;
import app.zingo.employeemanagements.model.StockSubCategoryModel;
import app.zingo.employeemanagements.ui.newemployeedesign.SubCategoryDetailScreen;

public class SubCategoryGridAdapter extends BaseAdapter {

    Context context;
    ArrayList < StockSubCategoryModel > mList;
    public SubCategoryGridAdapter(Context context, ArrayList<StockSubCategoryModel> mList)
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
                convertView = inflater.inflate(R.layout.category_grid_view,parent,false);
            }

            final ImageView mImage = (ImageView) convertView.findViewById(R.id.category_image);
            final TextView mSName = (TextView) convertView.findViewById( R.id.category_name);
            final CardView mainLayout = (CardView) convertView.findViewById(R.id.category_layout);

            if(mList.get(position)!=null){
                mSName.setText(""+mList.get ( position ).getStockSubCategoryName ());
                Picasso.with(context).load(mList.get(position).getStockSubCategoryImage ()).placeholder( R.drawable.no_image).
                        error(R.drawable.no_image).into( mImage);
            }

            mainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(context, SubCategoryDetailScreen.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("SubCategory",mList.get(position));
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
