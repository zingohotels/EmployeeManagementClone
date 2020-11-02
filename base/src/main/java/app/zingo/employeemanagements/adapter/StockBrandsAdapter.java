package app.zingo.employeemanagements.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;//
import android.widget.TextView;

import java.util.ArrayList;

import app.zingo.employeemanagements.base.R;
import app.zingo.employeemanagements.model.BrandsModel;
import app.zingo.employeemanagements.ui.NewAdminDesigns.BrandScreen;

public class StockBrandsAdapter extends RecyclerView.Adapter< StockBrandsAdapter.ViewHolder> {

    Context context;
    ArrayList < BrandsModel > stockCategoryModelArrayList;

    public StockBrandsAdapter(Context context,  ArrayList < BrandsModel > stockCategoryModelArrayList)
    {
        this.context = context;
        this.stockCategoryModelArrayList = stockCategoryModelArrayList;
    }
    @Override
    public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate( R.layout.stock_category_adapter,parent,false);
        return new ViewHolder (view);
    }

    @Override
    public void onBindViewHolder( final ViewHolder holder, final int position) {

        final BrandsModel stockCategoryModel = stockCategoryModelArrayList.get(position);

        if(stockCategoryModel!=null){
            holder.mCategoryName.setText(stockCategoryModel.getBrandName ());

            holder.mCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent updating = new Intent(context, BrandScreen.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("Brands",stockCategoryModel);
                    updating.putExtras(bundle);
                    (( Activity )context).startActivity(updating);


                }
            });

        }else{

        }





    }

    @Override
    public int getItemCount() {
        return stockCategoryModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView mCategoryName;
        CardView mCategory;
        public ViewHolder( View itemView) {
            super(itemView);
            mCategoryName = (TextView)itemView.findViewById( R.id.category_name);
            mCategory = (CardView) itemView.findViewById(R.id.category);

        }
    }

}