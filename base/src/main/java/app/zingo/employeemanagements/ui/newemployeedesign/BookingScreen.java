package app.zingo.employeemanagements.ui.newemployeedesign;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import app.zingo.employeemanagements.WebApi.GeneralNotificationAPI;
import app.zingo.employeemanagements.WebApi.StockOrders;
import app.zingo.employeemanagements.base.R;
import app.zingo.employeemanagements.model.GeneralNotification;
import app.zingo.employeemanagements.model.StockItemModel;
import app.zingo.employeemanagements.model.StockOrderDetailsModel;
import app.zingo.employeemanagements.model.StockOrderPersonInfoModel;
import app.zingo.employeemanagements.model.StockOrdersModel;
import app.zingo.employeemanagements.utils.Constants;
import app.zingo.employeemanagements.utils.PreferenceHandler;
import app.zingo.employeemanagements.utils.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingScreen extends AppCompatActivity {

    EditText mName,mEmail,mPhone,mAddress;
    TextView mPay,mQuantity,mTotal;
    ImageView mBack;

    ArrayList < StockItemModel > stockItemsList= new ArrayList <> (  );
    ArrayList< StockOrderDetailsModel > stockOrderDetailsDummy = new ArrayList <> (  );

    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate ( savedInstanceState );
        try{

            setContentView ( R.layout.activity_booking_screen );

            mBack = (ImageView ) findViewById(R.id.back);
            mName = (EditText)findViewById(R.id.fullname);
            mEmail = (EditText)findViewById(R.id.email);
            mPhone = (EditText)findViewById(R.id.phone);
            mAddress = (EditText)findViewById(R.id.address);

            mPay = ( TextView ) findViewById(R.id.pay);
            mQuantity = ( TextView ) findViewById(R.id.pay_total_item_count);
            mTotal = ( TextView ) findViewById(R.id.pay_amount);

            mName.setText ( ""+ PreferenceHandler.getInstance ( BookingScreen.this ).getUserFullName () );
            mEmail.setText ( ""+PreferenceHandler.getInstance ( BookingScreen.this ).getUserEmail () );
            mPhone.setText ( ""+PreferenceHandler.getInstance ( BookingScreen.this ).getPhoneNumber () );
            mAddress.setText ( ""+PreferenceHandler.getInstance ( BookingScreen.this ).getAddress () );

            getSavedCartData ( BookingScreen.this );

            mBack.setOnClickListener ( new View.OnClickListener ( ) {
                @Override
                public void onClick ( View v ) {
                    BookingScreen.this.finish ();
                }
            } );

            mPay.setOnClickListener ( new View.OnClickListener ( ) {
                @Override
                public void onClick ( View v ) {

                    String name = mName.getText().toString();
                    String email = mEmail.getText().toString();
                    String phone = mPhone.getText().toString();
                    String address = mAddress.getText().toString();

                    if(name.isEmpty()){

                        mName.setError("Name required");
                        mName.requestFocus();

                    }else if( email.isEmpty()){

                        mEmail.setError("Email required");
                        mEmail.requestFocus();

                    }else if( phone.isEmpty()){

                        mPhone.setError("Mobile number required");
                        mPhone.requestFocus();

                    }else if( address.isEmpty()){

                        mAddress.setError("Address required");
                        mAddress.requestFocus();

                    }else{

                        StockOrdersModel so = new StockOrdersModel ();
                        double totalAmount = 0;

                        if(stockItemsList!=null&&stockItemsList.size ()!=0){

                            ArrayList< StockOrderDetailsModel > stockOrderDetails = new ArrayList <> (  );


                            for ( StockItemModel stockItems: stockItemsList) {

                                StockOrderDetailsModel sod = new StockOrderDetailsModel ();

                                // sod.setStockItem ( null );
                                int stockItemId = stockItems.getStockItemId ();
                                int stockQuantity = stockItems.getQuantity ();
                                sod.setStockItemId ( stockItemId );
                                sod.setQuantity ( stockQuantity );

                                if(stockItems.getStockItemPricingList ()!=null&&stockItems.getStockItemPricingList ().size ()!=0){

                                    totalAmount = totalAmount+ stockItems.getQuantity ()* stockItems.getStockItemPricingList ().get ( 0 ).getSellingPrice ();
                                    sod.setTotalPrice ( stockItems.getQuantity ()* stockItems.getStockItemPricingList ().get ( 0 ).getSellingPrice ());

                                }else{
                                    sod.setTotalPrice ( 0 );
                                }

                                // sod.setStockItem ( null );
                                stockOrderDetails.add ( sod );
                              /*  sods = sod;
                                sods.setStockItem ( stockItems );
                                stockOrderDetailsDummy.add ( sods );*/

                            }

                            so.setStockOrderDetailsList ( stockOrderDetails );

                            ArrayList< StockOrderPersonInfoModel > sopList = new ArrayList <> (  );
                            StockOrderPersonInfoModel sop = new StockOrderPersonInfoModel ();
                            sop.setPersonName ( name );
                            sop.setEmail ( email );
                            sop.setMobile ( phone );
                            sop.setBillingAddress ( address );
                            sop.setShippingAddress ( address );
                            sopList.add ( sop );
                            so.set_stockOrderPersonInfo ( sopList );
                            so.setTotalAmount ( totalAmount );
                            so.setOrderNumber (""+ new Date (  ).getTime () );
                            so.setOrganizationId (PreferenceHandler.getInstance ( BookingScreen.this ).getCompanyId () );
                            addStockOrders(so);

                        }else{
                            Toast.makeText ( BookingScreen.this , "Something went wrong" , Toast.LENGTH_SHORT ).show ( );
                        }


                    }


                }
            } );

        }catch ( Exception e ){
            e.printStackTrace ();
        }

    }

    public String getSavedCartData( Context context) {
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
        Type type = new TypeToken < List < StockItemModel > > (){}.getType();
        stockItemsList = gson.fromJson(jsonString, type);


        double total = 0;

        for ( StockItemModel stockItems:stockItemsList ) {

            StockOrderDetailsModel sods = new StockOrderDetailsModel ();

            int stockItemId = stockItems.getStockItemId ();
            int stockQuantity = stockItems.getQuantity ();
            sods.setStockItemId ( stockItemId );
            sods.setQuantity ( stockQuantity );
            sods.setStockItem ( stockItems );

            if(stockItems.getStockItemPricingList ()!=null&&stockItems.getStockItemPricingList ().size ()!=0){

                total = total+(stockItems.getQuantity ()*stockItems.getStockItemPricingList ().get ( 0 ).getSellingPrice ());

                sods.setTotalPrice ( stockItems.getQuantity ()* stockItems.getStockItemPricingList ().get ( 0 ).getSellingPrice ());
            }else{
                total = total+0;
            }

            stockOrderDetailsDummy.add ( sods );

        }

        /// mPay.setText("TOTAL : ₹ "+total);
        mTotal.setText("₹ "+total);
        mQuantity.setText(""+stockItemsList.size ());
    }

    public void addStockOrders(final StockOrdersModel scm) {

        Gson gson = new Gson();
        String jsons = gson.toJson ( scm);
        System.out.println ("JSON "+jsons );

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.show();

        StockOrders apiService = Util.getClient().create(StockOrders.class);

        Call <StockOrdersModel> call = apiService.addStockOrders (scm);

        call.enqueue(new Callback <StockOrdersModel> () {
            @Override
            public void onResponse(Call<StockOrdersModel> call, Response <StockOrdersModel> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {
                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201) {

                        StockOrdersModel s = response.body();

                        if(s!=null){

                            Toast.makeText(BookingScreen.this, "Stock Order created successfully", Toast.LENGTH_SHORT).show();
                            // BookingScreen.this.finish();

                            /*StockOrderNotificationManagers snnm = new StockOrderNotificationManagers ();
                            snnm.setStockOrder ( response.body () );
                            snnm.setStockOrderId ( response.body ().getStockOrderId () );
                            snnm.setOrganizationId ( Constants.ORG_ID);
                            snnm.setTitle ("Stock Order" );
                            snnm.setMessage ("Stock Order" );
                            snnm.setSenderId ( Constants.SENDER_ID );
                            snnm.setServerId (Constants.SERVER_ID );
                            snnm.setNotificationDateTime (new SimpleDateFormat ( "MMM dd,yyyy hh:mm a" , Locale.US).format ( new Date (  ) ) );
                            snnm.setUserId ( PreferenceHandler.getInstance ( BookingScreen.this ).getEmployeeUserId () );
                            snnm.setStockOrderId ( response.body ().getStockOrderId () );
                            sendStockOrderNm(snnm);*/

                            addToCart();

                           /* scm.setStockOrderId ( response.body ().getStockOrderId () );
                            scm.setStockOrderDetailsList (som);*/

                            response.body ().setStockOrderDetailsList ( stockOrderDetailsDummy  );

                            Gson gson = new Gson();
                            String json = gson.toJson (response.body ());
                            GeneralNotification gm = new GeneralNotification();
                            gm.setOrganizationId (PreferenceHandler.getInstance ( BookingScreen.this ).getCompanyId ());
                            gm.setEmployeeId (PreferenceHandler.getInstance ( BookingScreen.this ).getManagerId ());
                            gm.setSenderId( Constants.SENDER_ID);
                            gm.setServerId(Constants.SERVER_ID);
                            gm.setTitle("Stock Order");
                            gm.setMessage(""+json);


                            sendStockOrderNm(gm);

                        }




                    }else {
                        Toast.makeText(BookingScreen.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
            public void onFailure( Call < StockOrdersModel > call, Throwable t) {

                if(dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
                //Toast.makeText(CustomerCreation.this, "Failed due to Bad Internet Connection", Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());
            }
        });



    }

    public void sendStockOrderNm(final GeneralNotification scm) {

        GeneralNotificationAPI apiService = Util.getClient().create(GeneralNotificationAPI.class);

        Call <ArrayList<String>> call = apiService.sendGeneralNotification ( scm);

        call.enqueue(new Callback <ArrayList<String>> () {
            @Override
            public void onResponse(Call<ArrayList<String>> call, Response <ArrayList<String>> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {
                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201|| statusCode == 202) {

                        saveStockOrderNm(scm);

                    }else {
                        Toast.makeText(BookingScreen.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
                        saveStockOrderNm(scm);
                    }
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();

                    saveStockOrderNm(scm);
                }
//                callGetStartEnd();
            }

            @Override
            public void onFailure( Call < ArrayList<String> > call, Throwable t) {

                saveStockOrderNm(scm);
            }
        });



    }

    public void saveStockOrderNm(final GeneralNotification scm) {


        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.show();

        GeneralNotificationAPI apiService = Util.getClient().create(GeneralNotificationAPI.class);

        Call <GeneralNotification> call = apiService.saveGeneralNotification ( scm);

        call.enqueue(new Callback <GeneralNotification> () {
            @Override
            public void onResponse(Call<GeneralNotification> call, Response <GeneralNotification> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {
                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201|| statusCode == 202) {

                        Intent main = new Intent ( BookingScreen.this,StockOrderCategoryListScreen.class );
                        startActivity ( main );
                        finishAffinity ();



                    }else {
                        Toast.makeText(BookingScreen.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
                        Intent main = new Intent ( BookingScreen.this,StockOrderCategoryListScreen.class );
                        startActivity ( main );
                        finishAffinity ();
                    }
                }
                catch (Exception ex)
                {

                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }
                    ex.printStackTrace();

                    Intent main = new Intent ( BookingScreen.this,StockOrderCategoryListScreen.class );
                    startActivity ( main );
                    finishAffinity ();
                }
//                callGetStartEnd();
            }

            @Override
            public void onFailure( Call <GeneralNotification> call, Throwable t) {

                if(dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
                //Toast.makeText(CustomerCreation.this, "Failed due to Bad Internet Connection", Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());

                Intent main = new Intent ( BookingScreen.this,StockOrderCategoryListScreen.class );
                startActivity ( main );
                finishAffinity ();
            }
        });



    }

    private void addToCart() {
        List <StockItemModel> items = new ArrayList <> (  );
        Gson gson = new GsonBuilder ().create();
        JsonArray myCustomArray = gson.toJsonTree(items).getAsJsonArray();

        try {
            FileWriter file = new FileWriter(BookingScreen.this.getFilesDir().getPath() + "/" + "AddToCart.json");
            file.write(myCustomArray.toString ());
            file.flush();
            file.close();
            // Toast.makeText(context, "Composition saved", Toast.LENGTH_LONG).show();
        } catch ( IOException e) {
            Log.e("TAG", "Error in Writing: " + e.getLocalizedMessage());
        }
    }
}
