package app.zingo.employeemanagements.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.squareup.picasso.Picasso;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import app.zingo.employeemanagements.base.R;
import app.zingo.employeemanagements.model.StockItemModel;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private Context context;
    private ArrayList < StockItemModel > items;

    public CartAdapter(Context context, ArrayList <StockItemModel> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder( @NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from (context).inflate (R.layout.add_cart_adapter, null);
        return new ViewHolder (itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        if(items!=null&&items.size ()!=0){
            if (items.get (position).getStockItemPricingList () != null && items.get (position).getStockItemPricingList ().size () != 0) {
                holder.txt_price.setText ("\u20B9 " + (items.get (position).getStockItemPricingList ().get (0).getSellingPrice ()*items.get (position).getQuantity ()));
                holder.txt_product_name.setText (items.get (position).getStockItemName ());
                holder.txt_quantity.setText ("Quantity: "+items.get (position).getQuantity ());

                Picasso.with (context).load (items.get (position).getStockItemImage ()).into (holder.img_product);
            }
            else{

                holder.txt_price.setText ("\u20B9 0" );
                holder.txt_product_name.setText (items.get (position).getStockItemName ());
                holder.txt_quantity.setText ("Quantity: "+items.get (position).getQuantity ());

                Picasso.with (context).load (items.get (position).getStockItemImage ()).into (holder.img_product);

            }


            holder.removeItem.setOnClickListener (new View.OnClickListener () {
                @Override
                public void onClick(View v) {
                    deleteItem(position);
                    notifyDataSetChanged();
                    //deleteListener.onDelete(id);
                }
            });



        }else{

        }
    }

    private void deleteItem(int position) {
        items.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, items.size());

        addToCart(items);
    }

    private void addToCart( List <StockItemModel> items) {

        Gson gson = new GsonBuilder ().create();
        JsonArray myCustomArray = gson.toJsonTree(this.items).getAsJsonArray();

        try {
            FileWriter file = new FileWriter(context.getFilesDir().getPath() + "/" + "AddToCart.json");
            file.write(myCustomArray.toString ());
            file.flush();
            file.close();
            // Toast.makeText(context, "Composition saved", Toast.LENGTH_LONG).show();
        } catch ( IOException e) {
            Log.e("TAG", "Error in Writing: " + e.getLocalizedMessage());
        }
    }


    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size ();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView img_product;
        private TextView txt_product_name,txt_price,txt_quantity;
        Button removeItem;


        public ViewHolder(View itemView) {
            super(itemView);

            img_product=itemView.findViewById( R.id.image_cartlist);
            txt_product_name=itemView.findViewById(R.id.cart_item_name);
            txt_price=itemView.findViewById(R.id.cart_item_price);
            txt_quantity=itemView.findViewById(R.id.cart_item_quantity);
            removeItem=itemView.findViewById(R.id.layout_removeItem);

        }

        @Override
        public void onClick(View v) {

            //iitemClickListner.onClick(v);

        }
    }
}