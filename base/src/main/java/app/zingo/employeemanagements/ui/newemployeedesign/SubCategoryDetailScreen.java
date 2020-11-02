package app.zingo.employeemanagements.ui.newemployeedesign;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import app.zingo.employeemanagements.WebApi.StockItemPricingsApi;
import app.zingo.employeemanagements.WebApi.StockItemsApi;
import app.zingo.employeemanagements.base.R;
import app.zingo.employeemanagements.model.StockItemModel;
import app.zingo.employeemanagements.model.StockItemPricingModel;
import app.zingo.employeemanagements.model.StockSubCategoryModel;
import app.zingo.employeemanagements.utils.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubCategoryDetailScreen extends AppCompatActivity {

    StockSubCategoryModel category;
    TextView mCategoryDesc,mCateDes;
    RecyclerView item_list;
    LinearLayout mDesLay;
    NestedScrollView mMain;
    Animation uptodown,downtoup;

    LinearLayout mTotalLay;
    TextView mTotalAmt,mOrder;

    double total=0;
    private ArrayList< StockItemModel > stockItemQuantityArrayList = new ArrayList <> (  );
    private ArrayList< StockItemModel > stockItemQuantityCartArrayList = new ArrayList <> (  );

    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate ( savedInstanceState );

        try{

            setContentView(R.layout.activity_sub_category_detail_screen);
            // Get Toolbar component.
            Toolbar toolbar = (Toolbar)findViewById(R.id.collapsing_toolbar);

            mMain = (NestedScrollView) findViewById(R.id.main_layout);
            mCateDes = (TextView) findViewById(R.id.category_desc_name);
            item_list = (RecyclerView ) findViewById(R.id.item_list);
            mCategoryDesc = (TextView) findViewById(R.id.category_desc);
            mDesLay = (LinearLayout) findViewById(R.id.des_lay);
            uptodown = AnimationUtils.loadAnimation(this,R.anim.uptodown);
            downtoup = AnimationUtils.loadAnimation(this,R.anim.downtoup);
            mDesLay.setAnimation(downtoup);

            mTotalLay = (LinearLayout) findViewById(R.id.total_pay);
            mTotalAmt = (TextView ) findViewById(R.id.txttotal);
            mOrder = (TextView ) findViewById(R.id.txtorder);
            // Use Toolbar to replace default activity action bar.
            setSupportActionBar(toolbar);

            ActionBar actionBar = getSupportActionBar();


            if(actionBar!=null)
            {
                // Display home menu item.
                actionBar.setDisplayHomeAsUpEnabled(true);
            }

            recyclerAnimation();

            Bundle bundle = getIntent().getExtras();

            if(bundle!=null){

                category = (StockSubCategoryModel ) bundle.getSerializable("SubCategory");

            }

            // Set collapsing tool bar title.
            CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar_layout);
            // Set collapsing tool bar image.
            ImageView collapsingToolbarImageView = (ImageView)findViewById(R.id.collapsing_toolbar_image_view);
            //collapsingToolbarImageView.setImageResource(R.drawable.img1);

            if(category!=null){
                collapsingToolbarLayout.setTitle(""+category.getStockSubCategoryName ());
                Picasso.with(SubCategoryDetailScreen.this).load(category.getStockSubCategoryImage ()).placeholder(R.drawable.no_image).
                        error(R.drawable.no_image).into(collapsingToolbarImageView);
                mCateDes.setText("About "+category.getStockSubCategoryName ());

                if( Build.VERSION.SDK_INT>=Build.VERSION_CODES.N) {


                    mCategoryDesc.setText( category.getStockSubCategoryDescription ());

                }else {


                    mCategoryDesc.setText(category.getStockSubCategoryDescription ());
                }


                if(category.getStockItemList ()!=null&&category.getStockItemList().size()!=0){

                    getStockItemPricings(category.getStockItemList ());
                    /*ItemAdapter adapter = new ItemAdapter(SubCategoryDetailScreen.this, category.getStockItemList ());
                    item_list.setAdapter(adapter);*/

                }else{
                    getStockItem(category.getStockSubCategoryId ());
                }

            }else{
                collapsingToolbarLayout.setTitle("Category Detail");
                Picasso.with(SubCategoryDetailScreen.this).load(category.getStockSubCategoryImage ()).placeholder(R.drawable.no_image).
                        error(R.drawable.no_image).into(collapsingToolbarImageView);
            }



            mTotalLay.setOnClickListener ( new View.OnClickListener ( ) {
                @Override
                public void onClick ( View v ) {

                    Intent cart = new Intent ( SubCategoryDetailScreen.this,CartScreen.class );
                    startActivity ( cart );

                }
            } );


        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void recyclerAnimation(){
        mMain.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {

                    @Override
                    public boolean onPreDraw() {
                        mMain.getViewTreeObserver().removeOnPreDrawListener(this);

                        for (int i = 0; i < mMain.getChildCount(); i++) {
                            View v = mMain.getChildAt(i);
                            v.setAlpha(0.0f);
                            v.animate().alpha(1.0f)
                                    .setDuration(300)
                                    .setStartDelay(i * 50)
                                    .start();
                        }

                        return true;
                    }
                });
    }

    public void getStockItem(final int id) {


        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.show();

        StockItemsApi apiService = Util.getClient().create(StockItemsApi.class);

        Call < ArrayList < StockItemModel > > call = apiService.getStockItemsByStockSubCategoryId (id );

        call.enqueue(new Callback <ArrayList<StockItemModel>> () {
            @Override
            public void onResponse(Call<ArrayList<StockItemModel>> call, Response <ArrayList<StockItemModel>> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {
                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201||statusCode==204) {

                        ArrayList<StockItemModel> stockItemsArrayList = response.body ();

                        if(stockItemsArrayList!=null&&stockItemsArrayList.size ()!=0){

                            getStockItemPricings(stockItemsArrayList);

                        }



                    }else {
                        Toast.makeText(SubCategoryDetailScreen.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
            public void onFailure(Call<ArrayList<StockItemModel>> call, Throwable t) {

                if(dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
                //Toast.makeText(CustomerCreation.this, "Failed due to Bad Internet Connection", Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());
            }
        });



    }
    public void getStockItemPricings(final ArrayList<StockItemModel> stockItemsArrayList) {


        StockItemPricingsApi apiService = Util.getClient().create(StockItemPricingsApi.class);

        Call < ArrayList < StockItemPricingModel > > call = apiService.getStockItemPricing ( );

        call.enqueue(new Callback <ArrayList<StockItemPricingModel>> () {
            @Override
            public void onResponse(Call<ArrayList<StockItemPricingModel>> call, Response <ArrayList<StockItemPricingModel>> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {


                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201||statusCode==204) {

                        ArrayList<StockItemPricingModel> stockItemPricingsArrayList = response.body ();

                        ArrayList<StockItemModel> itemsArrayList = new ArrayList <> (  );

                        if(stockItemPricingsArrayList!=null&&stockItemPricingsArrayList.size ()!=0){

                            for(StockItemModel stockItemModel:stockItemsArrayList){
                                ArrayList<StockItemPricingModel> stockItemPricings = new ArrayList <> ();
                                for (StockItemPricingModel stockItemPricingModel:stockItemPricingsArrayList){
                                    if(stockItemModel.getStockItemId () == stockItemPricingModel.getStockItemId ()){
                                        stockItemPricings.add (stockItemPricingModel);
                                    }
                                }
                                stockItemModel.setStockItemPricingList (stockItemPricings);
                                itemsArrayList.add (stockItemModel);
                            }


                            ItemAdapter adapter = new ItemAdapter(SubCategoryDetailScreen.this, itemsArrayList);
                            item_list.setAdapter(adapter);

                        }else{
                            ItemAdapter adapter = new ItemAdapter(SubCategoryDetailScreen.this, itemsArrayList);
                            item_list.setAdapter(adapter);
                        }



                    }else {
                        Toast.makeText(SubCategoryDetailScreen.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception ex)
                {


                    ex.printStackTrace();
                }
//                callGetStartEnd();
            }

            @Override
            public void onFailure(Call<ArrayList<StockItemPricingModel>> call, Throwable t) {


                //Toast.makeText(CustomerCreation.this, "Failed due to Bad Internet Connection", Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());
            }
        });



    }

    @Override
    protected void onResume() {
        super.onResume();
        mCateDes.requestFocus();
    }


    public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder>{

        private Context context;
        private ArrayList<StockItemModel> list;
        public ItemAdapter(Context context, ArrayList<StockItemModel> list) {

            this.context = context;
            this.list = list;

        }

        @Override
        public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
            try{
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_adapter, parent, false);
                ViewHolder viewHolder = new ViewHolder(v);
                return viewHolder;
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }

        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {

            try {
                final StockItemModel dto = list.get(position);
                holder.display_name.setText(""+dto.getStockItemName ());

                double selling_price = 0,displ_price=0;
                if(dto.getStockItemPricingList ()!=null&&dto.getStockItemPricingList ().size ()!=0){


                    selling_price =dto.getStockItemPricingList().get ( 0 ).getSellingPrice ();
                    displ_price =dto.getStockItemPricingList().get ( 0 ).getDisplayPrice ();

                }


                holder.start_sell.setText("₹ "+selling_price);
                holder.disp_price.setText("₹ "+displ_price);


                if(dto.getStockItemImage ()!=null){
                    String cateIma = dto.getStockItemImage();

                    if(cateIma!=null&&!cateIma.isEmpty()){
                        Picasso.with(context).load(cateIma).placeholder(R.drawable.no_image).
                                error(R.drawable.no_image).into(holder.mCaeImg);
                    }
                }

                holder.mAddFood.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        holder.mAddFood.setVisibility(View.GONE);
                        holder.mFoodLayout.setVisibility(View.VISIBLE);
                        mTotalLay.setVisibility(View.VISIBLE);
                        holder.mFoodCount.setText("1");
                        double selling_prices = 0,displ_prices=0;
                        if(dto.getStockItemPricingList ()!=null&&dto.getStockItemPricingList ().size ()!=0){


                            selling_prices =dto.getStockItemPricingList().get ( 0 ).getSellingPrice ();
                            displ_prices =dto.getStockItemPricingList().get ( 0 ).getDisplayPrice ();

                        }
                        total = total+selling_prices;
                        mTotalAmt.setText("TOTAL : ₹ "+total);

                        storeCart(dto,1);

                    }
                });

                holder.mAddItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        int valueFood = Integer.parseInt(holder.mFoodCount.getText().toString());
                        holder.mFoodCount.setText((valueFood+1)+"");

                        double selling_prices = 0,displ_prices=0;
                        if(dto.getStockItemPricingList ()!=null&&dto.getStockItemPricingList ().size ()!=0){


                            selling_prices =dto.getStockItemPricingList().get ( 0 ).getSellingPrice ();
                            displ_prices =dto.getStockItemPricingList().get ( 0 ).getDisplayPrice ();

                        }
                        total = total+selling_prices;
                        mTotalAmt.setText("TOTAL : ₹ "+total);

                        storeCart(dto,(valueFood+1));

                    }
                });

                holder.mRemoveItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        int valueFood = Integer.parseInt(holder.mFoodCount.getText().toString())-1;
                        storeCart(dto,(valueFood));

                        if(valueFood<1){
                            holder.mFoodLayout.setVisibility(View.GONE);
                            holder.mAddFood.setVisibility(View.VISIBLE);
                            holder.mFoodCount.setText("0");
                        }else{
                            holder.mFoodCount.setText((valueFood)+"");
                        }

                        double selling_prices = 0,displ_prices=0;
                        if(dto.getStockItemPricingList ()!=null&&dto.getStockItemPricingList ().size ()!=0){


                            selling_prices =dto.getStockItemPricingList().get ( 0 ).getSellingPrice ();
                            displ_prices =dto.getStockItemPricingList().get ( 0 ).getDisplayPrice ();

                        }
                        total = total-selling_prices;

                        if(total<=0){
                            mTotalLay.setVisibility(View.GONE);

                        }else{
                            mTotalAmt.setText("TOTAL : ₹ "+total);
                        }



                    }
                });


            }catch (Exception e){
                e.printStackTrace();
            }

        }

        @Override
        public int getItemCount() {
            return list.size();
        }


        class ViewHolder extends RecyclerView.ViewHolder /*implements View.OnClickListener*/ {

            public TextView display_name,start_sell,disp_price,item;
            ImageView mCaeImg,mAddItem,mRemoveItem;
            TextView mFoodCount;
            Button mAddFood;
            LinearLayout mFoodLayout;
//

            public ViewHolder(View itemView) {
                super(itemView);
                itemView.setClickable(true);
                try{
                    display_name = (TextView) itemView.findViewById(R.id.itemname3);
                    start_sell = (TextView) itemView.findViewById(R.id.sell_rate);
                    disp_price = (TextView) itemView.findViewById(R.id.displ_rate);
                    item = (TextView) itemView.findViewById(R.id.displ_rate_item);
                    mCaeImg = (ImageView) itemView.findViewById(R.id.category_image_list);
                    //mAdd = (ImageView) itemView.findViewById(R.id.add_icon);
                    mFoodLayout = (LinearLayout) itemView.findViewById(R.id.item_add_layout);
                    mAddItem = (ImageView) itemView.findViewById(R.id.item_add);
                    mRemoveItem = (ImageView) itemView.findViewById(R.id.item_remove);
                    mFoodCount = (TextView) itemView.findViewById(R.id.item_count);
                    mAddFood = (Button) itemView.findViewById(R.id.add_item);

                }catch (Exception e){
                    e.printStackTrace();
                }


            }
        }

    }

    public void storeCart(final StockItemModel stockItems,final int quantity){


        if(stockItemQuantityArrayList!=null&&stockItemQuantityArrayList.size ()!=0){

            if(stockItemQuantityArrayList.contains ( stockItems )){

                int index = stockItemQuantityArrayList.indexOf ( stockItems );

                if(quantity>0){

                    stockItems.setQuantity ( quantity );

                    stockItemQuantityArrayList.set(index,stockItems);
                    addJsonData ();

                }else{

                    stockItemQuantityArrayList.remove ( index );
                    addJsonData ();
                }
            }else{

                if(quantity>0){

                    stockItems.setQuantity ( quantity );

                    stockItemQuantityArrayList.add(stockItems);
                    addJsonData ();

                }

            }



        }else  if(stockItemQuantityArrayList!=null&&stockItemQuantityArrayList.size ()==0){

            if(quantity>0){

                StockItemModel stockItemQuantity = stockItems;
                stockItemQuantity.setQuantity ( quantity );
                stockItemQuantityArrayList.add ( stockItemQuantity );
                addJsonData ();

            }else{

                stockItemQuantityArrayList = new ArrayList <> (  );
                addJsonData ();
            }


        }

    }

    private void addToCart(Context context, ArrayList<StockItemModel> addToCart) {

        Gson gson = new GsonBuilder ().create();
        JsonArray myCustomArray = gson.toJsonTree(addToCart).getAsJsonArray();

        try {
            FileWriter file = new FileWriter(context.getFilesDir().getPath() + "/" + "AddToCart.json");
            file.write(myCustomArray.toString ());
            file.flush();
            file.close();
            //Toast.makeText(context, "Add to Cart", Toast.LENGTH_LONG).show();
        } catch ( IOException e) {
            Log.e("TAG", "Error in Writing: " + e.getLocalizedMessage());
        }
    }

    public String getSavedCartData(Context context) {
        String json = null;
        try {
            File f = new File(context.getFilesDir().getPath() + "/" + "AddToCart.json");
            FileInputStream is = new FileInputStream (f);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
            parseCartJSON(json);
        } catch ( IOException e) {
            Log.e("TAG", "Error in Reading: " + e.getLocalizedMessage());
            return null;
        }
        return json;
    }

    private void parseCartJSON(String jsonString) {

        System.out.println ("String json = "+jsonString );
        Gson gson = new Gson();
        Type type = new TypeToken < List <StockItemModel> > (){}.getType();
        stockItemQuantityCartArrayList = gson.fromJson(jsonString, type);
    }

    void addJsonData(){



        getSavedCartData(SubCategoryDetailScreen.this);

        if(stockItemQuantityCartArrayList!=null&&stockItemQuantityCartArrayList.size ()!=0){


            for ( StockItemModel stockItems: stockItemQuantityArrayList) {

                boolean available = false;
                for(int i=0;i<stockItemQuantityCartArrayList.size ();i++){

                    if(stockItems.getStockItemId ()==stockItemQuantityCartArrayList.get ( i ).getStockItemId ()){
                        stockItemQuantityCartArrayList.set ( i , stockItems );
                        available = true;
                        break;
                    }
                }

                if(!available){
                    stockItemQuantityCartArrayList.add ( stockItems );
                    available = false;
                }


               /* if(stockItemQuantityCartArrayList.contains ( stockItems )){

                    int index = stockItemQuantityCartArrayList.indexOf ( stockItems );
                    stockItemQuantityCartArrayList.set ( index , stockItems );
                }else{
                    stockItemQuantityCartArrayList.add (  stockItems);
                }*/

            }

            addToCart(SubCategoryDetailScreen.this,stockItemQuantityCartArrayList);

        }else if(stockItemQuantityCartArrayList!=null){
            stockItemQuantityCartArrayList = new ArrayList <> ();
            stockItemQuantityCartArrayList.addAll (  stockItemQuantityArrayList);
            addToCart(SubCategoryDetailScreen.this,stockItemQuantityCartArrayList);
        }else{
            stockItemQuantityCartArrayList = new ArrayList <> ();
            stockItemQuantityCartArrayList.addAll (  stockItemQuantityArrayList);
            addToCart(SubCategoryDetailScreen.this,stockItemQuantityCartArrayList);
        }

    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected (item);
    }
}
