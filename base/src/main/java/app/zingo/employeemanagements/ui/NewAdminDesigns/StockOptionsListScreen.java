package app.zingo.employeemanagements.ui.NewAdminDesigns;

import android.app.ProgressDialog;
import android.content.Intent;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;

import app.zingo.employeemanagements.WebApi.BrandsApi;
import app.zingo.employeemanagements.WebApi.GeneralNotificationAPI;
import app.zingo.employeemanagements.WebApi.StockCategoriesApi;
import app.zingo.employeemanagements.WebApi.StockItemPricingsApi;
import app.zingo.employeemanagements.WebApi.StockItemsApi;
import app.zingo.employeemanagements.WebApi.StockSubCategoriesApi;
import app.zingo.employeemanagements.adapter.StockBrandsAdapter;
import app.zingo.employeemanagements.adapter.StockCategoryListAdapter;
import app.zingo.employeemanagements.adapter.StockItemAdapter;
import app.zingo.employeemanagements.adapter.StockOrderListAdapter;
import app.zingo.employeemanagements.adapter.StockSubCategoryAdapter;
import app.zingo.employeemanagements.base.R;
import app.zingo.employeemanagements.model.BrandsModel;
import app.zingo.employeemanagements.model.GeneralNotification;
import app.zingo.employeemanagements.model.StockCategoryModel;
import app.zingo.employeemanagements.model.StockItemModel;
import app.zingo.employeemanagements.model.StockItemPricingModel;
import app.zingo.employeemanagements.model.StockOrdersModel;
import app.zingo.employeemanagements.model.StockSubCategoryModel;
import app.zingo.employeemanagements.utils.PreferenceHandler;
import app.zingo.employeemanagements.utils.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StockOptionsListScreen extends AppCompatActivity {

    RecyclerView mList;
    FloatingActionButton fab;
    String type="";

    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate ( savedInstanceState );
        try{

            setContentView(R.layout.activity_stock_options_list_screen);

            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            //setTitle("Room Categories");

            Bundle bundle = getIntent().getExtras();

            if(bundle!=null){
                type = bundle.getString ("Type");

            }

            mList = (RecyclerView)findViewById(R.id.stock_options_type_list);
            fab = findViewById(R.id.fab_cate_list);
           callFunction();


            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(!type.isEmpty ()){

                        if(type.equalsIgnoreCase ( "Stock Categories" )){

                            Intent category = new Intent ( StockOptionsListScreen.this, StockCategoryScreen.class );
                            category.putExtra ( "Method","Create" );
                            startActivity ( category );


                        }else if(type.equalsIgnoreCase ( "Stock SubCategories" )){

                            Intent category = new Intent ( StockOptionsListScreen.this, StockSubCategoryScreen.class );
                            category.putExtra ( "Method","Create" );
                            startActivity ( category );


                        }else if(type.equalsIgnoreCase ( "Stock Brands" )){

                            Intent category = new Intent ( StockOptionsListScreen.this, BrandScreen.class );
                            category.putExtra ( "Method","Create" );
                            startActivity ( category );


                        }else if(type.equalsIgnoreCase ( "Stock Items" )){

                            Intent category = new Intent ( StockOptionsListScreen.this, StockItemScreen.class );
                            category.putExtra ( "Method","Create" );
                            startActivity ( category );


                        }

                    }

                }
            });


        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public void getStockCategories() {


        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.show();

        StockCategoriesApi apiService = Util.getClient().create(StockCategoriesApi.class);

        Call < ArrayList< StockCategoryModel > > call = apiService.getStockCategoryByOrganizationId( PreferenceHandler.getInstance ( StockOptionsListScreen.this ).getCompanyId ());

        call.enqueue(new Callback <ArrayList< StockCategoryModel >> () {
            @Override
            public void onResponse( Call<ArrayList< StockCategoryModel >> call, Response <ArrayList< StockCategoryModel >> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {
                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201||statusCode==204) {


                        ArrayList< StockCategoryModel > stockCategoryModelsList = response.body();
                        if(stockCategoryModelsList != null && stockCategoryModelsList.size()!=0 )
                        {

                            StockCategoryListAdapter adapter = new StockCategoryListAdapter ( StockOptionsListScreen.this,stockCategoryModelsList);
                            mList.setAdapter(adapter);

                        }
                        else
                        {


                        }






                    }else {
                        Toast.makeText( StockOptionsListScreen.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
            public void onFailure( Call<ArrayList< StockCategoryModel >> call, Throwable t) {

                if(dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
                //Toast.makeText(CustomerCreation.this, "Failed due to Bad Internet Connection", Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());
            }
        });



    }

    public void getStockSubCategories() {


        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.show();

        StockSubCategoriesApi apiService = Util.getClient().create( StockSubCategoriesApi.class);

        Call< ArrayList< StockSubCategoryModel > > call = apiService.getStockSubCategoryByOrganizationId(PreferenceHandler.getInstance ( StockOptionsListScreen.this ).getCompanyId ());

        call.enqueue(new Callback<ArrayList<StockSubCategoryModel>>() {
            @Override
            public void onResponse(Call<ArrayList<StockSubCategoryModel>> call, Response<ArrayList<StockSubCategoryModel>> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {
                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201||statusCode==204) {


                        ArrayList<StockSubCategoryModel> stockCategoryModelsList = response.body();
                        if(stockCategoryModelsList != null && stockCategoryModelsList.size()!=0 )
                        {
                            StockSubCategoryAdapter adapter = new StockSubCategoryAdapter( StockOptionsListScreen.this,stockCategoryModelsList);
                            mList.setAdapter(adapter);

                        }


                    }else {
                        Toast.makeText( StockOptionsListScreen.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
            public void onFailure(Call<ArrayList<StockSubCategoryModel>> call, Throwable t) {

                if(dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
                //Toast.makeText(CustomerCreation.this, "Failed due to Bad Internet Connection", Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());
            }
        });



    }

    public void getStockBrands() {


        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.show();

        BrandsApi apiService = Util.getClient().create( BrandsApi.class);

        Call< ArrayList< BrandsModel > > call = apiService.getBrandsByOrganizationId(PreferenceHandler.getInstance ( StockOptionsListScreen.this ).getCompanyId ());

        call.enqueue(new Callback<ArrayList< BrandsModel >>() {
            @Override
            public void onResponse( Call<ArrayList< BrandsModel >> call, Response<ArrayList< BrandsModel >> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {
                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201||statusCode==204) {


                        ArrayList< BrandsModel > stockCategoryModelsList = response.body();
                        if(stockCategoryModelsList != null && stockCategoryModelsList.size()!=0 )
                        {


                            StockBrandsAdapter adapter = new StockBrandsAdapter ( StockOptionsListScreen.this,stockCategoryModelsList);
                            mList.setAdapter(adapter);
                        }
                        else
                        {


                        }






                    }else {
                        Toast.makeText( StockOptionsListScreen.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
            public void onFailure( Call<ArrayList< BrandsModel >> call, Throwable t) {

                if(dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
                //Toast.makeText(CustomerCreation.this, "Failed due to Bad Internet Connection", Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());
            }
        });



    }

    public void getItems() {


        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.show();

        StockItemsApi apiService = Util.getClient().create( StockItemsApi.class);

        Call< ArrayList< StockItemModel > > call = apiService.getStockItemsByOrganizationId (PreferenceHandler.getInstance ( StockOptionsListScreen.this ).getCompanyId ());

        call.enqueue(new Callback<ArrayList<StockItemModel>>() {
            @Override
            public void onResponse(Call<ArrayList<StockItemModel>> call, Response<ArrayList<StockItemModel>> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {
                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201||statusCode==204) {


                        ArrayList< StockItemModel > stockCategoryModelsList = response.body();
                        if(stockCategoryModelsList != null && stockCategoryModelsList.size()!=0 )
                        {

                           //getStockItemPricings ( stockCategoryModelsList );
                            StockItemAdapter adapter = new StockItemAdapter ( StockOptionsListScreen.this,stockCategoryModelsList);
                            mList.setAdapter(adapter);

                        }
                        else
                        {


                        }






                    }else {
                        Toast.makeText( StockOptionsListScreen.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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

    public void getStockOrders() {


        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.show();

        GeneralNotificationAPI apiService = Util.getClient().create( GeneralNotificationAPI.class);

        Call< ArrayList< GeneralNotification > > call = apiService.getGeneralNotification ();

        call.enqueue(new Callback<ArrayList< GeneralNotification >>() {
            @Override
            public void onResponse( Call<ArrayList< GeneralNotification >> call, Response<ArrayList< GeneralNotification >> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {
                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201||statusCode==204) {


                        ArrayList< GeneralNotification > stockCategoryModelsList = response.body();
                        ArrayList< StockOrdersModel > stockOrderList = new ArrayList <> (  );
                        if(stockCategoryModelsList != null && stockCategoryModelsList.size()!=0 )
                        {

                            for( GeneralNotification gm:stockCategoryModelsList){

                                if(gm.getOrganizationId ()==PreferenceHandler.getInstance ( StockOptionsListScreen.this ).getCompanyId ()&&gm.getTitle ().equalsIgnoreCase ( "Stock Order" )){

                                    Gson gson = new Gson ();
                                    StockOrdersModel som = gson.fromJson(gm.getMessage (), StockOrdersModel.class);
                                    stockOrderList.add ( som );


                                }
                            }

                            if(stockOrderList != null && stockOrderList.size()!=0 )
                            {

                                   StockOrderListAdapter adapter = new StockOrderListAdapter( StockOptionsListScreen.this,stockOrderList);
                                   mList.setAdapter(adapter);


                            }

                        }
                        else
                        {


                        }






                    }else {
                        Toast.makeText( StockOptionsListScreen.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
            public void onFailure( Call<ArrayList< GeneralNotification >> call, Throwable t) {

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


        StockItemPricingsApi apiService = Util.getClient().create( StockItemPricingsApi.class);

        Call < ArrayList < StockItemPricingModel > > call = apiService.getStockItemPricing ( );

        call.enqueue(new Callback <ArrayList< StockItemPricingModel >> () {
            @Override
            public void onResponse( Call<ArrayList< StockItemPricingModel >> call, Response <ArrayList< StockItemPricingModel >> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {


                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201||statusCode==204) {

                        ArrayList< StockItemPricingModel > stockItemPricingsArrayList = response.body ();

                        ArrayList<StockItemModel> itemsArrayList = new ArrayList <> (  );

                        if(stockItemPricingsArrayList!=null&&stockItemPricingsArrayList.size ()!=0){

                            for(StockItemModel stockItemModel:stockItemsArrayList){
                                ArrayList< StockItemPricingModel > stockItemPricings = new ArrayList <> ();
                                for ( StockItemPricingModel stockItemPricingModel:stockItemPricingsArrayList){
                                    if(stockItemModel.getStockItemId () == stockItemPricingModel.getStockItemId ()){
                                        stockItemPricings.add (stockItemPricingModel);
                                    }
                                }
                                stockItemModel.setStockItemPricingList (stockItemPricings);
                                itemsArrayList.add (stockItemModel);
                            }


                            StockItemAdapter adapter = new StockItemAdapter ( StockOptionsListScreen.this,itemsArrayList);
                            mList.setAdapter(adapter);

                        }else{
                            StockItemAdapter adapter = new StockItemAdapter ( StockOptionsListScreen.this,itemsArrayList);
                            mList.setAdapter(adapter);
                        }



                    }else {
                        Toast.makeText( StockOptionsListScreen.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception ex)
                {


                    ex.printStackTrace();
                }
//                callGetStartEnd();
            }

            @Override
            public void onFailure( Call<ArrayList< StockItemPricingModel >> call, Throwable t) {


                //Toast.makeText(CustomerCreation.this, "Failed due to Bad Internet Connection", Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());
            }
        });



    }

    @Override
    protected void onRestart ( ) {
        super.onRestart ( );
        callFunction ();
    }

    public void callFunction(){

        if(!type.isEmpty ()){
            setTitle(type);



            if(type.equalsIgnoreCase ( "Stock Categories" )){

                getStockCategories();


            }else if(type.equalsIgnoreCase ( "Stock SubCategories" )){

                getStockSubCategories ();


            }else if(type.equalsIgnoreCase ( "Stock Brands" )){

                getStockBrands ();



            }else if(type.equalsIgnoreCase ( "Stock Items" )){


                getItems();


            }else if(type.equalsIgnoreCase ( "Stock Orders" )){

                fab.setVisibility ( View.GONE );

                getStockOrders();


            }
        }else{
            setTitle("Stocks");
        }
    }
    @Override
    public boolean onOptionsItemSelected( MenuItem item) {

        int id = item.getItemId();

        switch (id)
        {
            case android.R.id.home:

                StockOptionsListScreen.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
