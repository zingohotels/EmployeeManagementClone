package app.zingo.employeemanagements.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import app.zingo.employeemanagements.base.R;
import app.zingo.employeemanagements.model.StockCategoryModel;
import app.zingo.employeemanagements.ui.NewAdminDesigns.StockCategoryScreen;

public class StockCategoryListAdapter extends RecyclerView.Adapter< StockCategoryListAdapter.ViewHolder> {

    Context context;
    ArrayList < StockCategoryModel > stockCategoryModelArrayList;

    public StockCategoryListAdapter(Context context,  ArrayList < StockCategoryModel > stockCategoryModelArrayList)
    {
        this.context = context;
        this.stockCategoryModelArrayList = stockCategoryModelArrayList;
    }
    @Override
    public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stock_category_adapter,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final StockCategoryModel stockCategoryModel = stockCategoryModelArrayList.get(position);

        if(stockCategoryModel!=null){
            holder.mCategoryName.setText(stockCategoryModel.getStockCategoryName ());

            holder.mCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent updating = new Intent(context, StockCategoryScreen.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("StockCategory",stockCategoryModel);
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
