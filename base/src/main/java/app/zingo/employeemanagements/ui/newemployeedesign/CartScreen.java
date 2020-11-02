package app.zingo.employeemanagements.ui.newemployeedesign;

import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import app.zingo.employeemanagements.adapter.CartAdapter;
import app.zingo.employeemanagements.base.R;
import app.zingo.employeemanagements.model.StockItemModel;

public class CartScreen extends AppCompatActivity {

    private ArrayList < StockItemModel > items;
    private Context context;
    private RecyclerView recyclerView;

    LinearLayout mTotalLay;
    TextView mTotalAmt,mOrder;

    ArrayList<StockItemModel> stockItemsList = new ArrayList <> (  );

    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate ( savedInstanceState );

        try{

            setContentView ( R.layout.activity_cart_screen );
            getSupportActionBar ().setDisplayHomeAsUpEnabled (true);
            getSupportActionBar ().setHomeButtonEnabled (true);
            setTitle ("My Cart");

            recyclerView=findViewById(R.id.recycler_addToCart);
            mTotalLay = (LinearLayout) findViewById(R.id.total_pay);
            mTotalAmt = (TextView ) findViewById(R.id.txttotal);
            mOrder = (TextView ) findViewById(R.id.txtorder);
            context = getApplicationContext ();
            getSavedCartData(context);

            mOrder.setOnClickListener ( new View.OnClickListener ( ) {
                @Override
                public void onClick ( View v ) {

                    if(stockItemsList!=null&&stockItemsList.size ()!=0){
                        Intent book = new Intent ( CartScreen.this,BookingScreen.class );
                        startActivity ( book );
                    }else{
                        Toast.makeText ( CartScreen.this , "Cart is empty" , Toast.LENGTH_SHORT ).show ( );
                    }

                }
            } );

        }catch ( Exception e ){
            e.printStackTrace ();
        }

    }

    public String getSavedCartData(Context context) {
        String json = null;
        try {
            File f = new File(context.getFilesDir().getPath() + "/" + "AddToCart.json");
            //check whether file exists
            System.out.println ("File path "+context.getFilesDir().getPath() + "/" + "AddToCart.json");
            FileInputStream is = new FileInputStream (f);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            // return new String(buffer);
            json = new String(buffer, "UTF-8");
            parseJSON(json);
        } catch ( IOException e) {
            Log.e("TAG", "Error in Reading: " + e.getLocalizedMessage());
            return null;
        }
        return json;

    }

    private void parseJSON(String jsonString) {
        Gson gson = new Gson();
        Type type = new TypeToken < List <StockItemModel> > (){}.getType();
        stockItemsList = gson.fromJson(jsonString, type);

        CartAdapter itemsAdapter = new CartAdapter (CartScreen.this,stockItemsList);
        recyclerView.setAdapter (itemsAdapter);

        double total = 0;

        for ( StockItemModel stockItems:stockItemsList ) {

            if(stockItems.getStockItemPricingList ()!=null&&stockItems.getStockItemPricingList ().size ()!=0){

                total = total+(stockItems.getQuantity ()*stockItems.getStockItemPricingList ().get ( 0 ).getSellingPrice ());
            }else{
                total = total+0;
            }

        }

        mTotalAmt.setText("TOTAL : ₹ "+total);
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected (item);
    }
}
