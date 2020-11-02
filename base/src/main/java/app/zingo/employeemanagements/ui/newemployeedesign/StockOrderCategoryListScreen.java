package app.zingo.employeemanagements.ui.newemployeedesign;

import android.app.ProgressDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;

import app.zingo.employeemanagements.WebApi.StockCategoriesApi;
import app.zingo.employeemanagements.adapter.StockCategoriesGridAdapter;
import app.zingo.employeemanagements.base.R;
import app.zingo.employeemanagements.model.ProductCustomModel;
import app.zingo.employeemanagements.model.StockCategoryModel;
import app.zingo.employeemanagements.utils.PreferenceHandler;
import app.zingo.employeemanagements.utils.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StockOrderCategoryListScreen extends AppCompatActivity {

    GridView mCategoryGrid;
    ArrayList< StockCategoryModel > categoriesArrayList = new ArrayList <> (  );
    ArrayList< ProductCustomModel > productCustomModelArrayList = new ArrayList <> (  );

    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate ( savedInstanceState );

        try{

            setContentView ( R.layout.activity_stock_order_category_list_screen );

            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Stock Orders");

            mCategoryGrid = ( GridView ) findViewById(R.id.stock_category_grid);

            getStockCategories ();

        }catch ( Exception e ){
            e.printStackTrace ();
        }

    }
    public void getStockCategories() {


        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.show();

        int companyId = PreferenceHandler.getInstance ( StockOrderCategoryListScreen.this ).getCompanyId ();


        StockCategoriesApi apiService = Util.getClient().create(StockCategoriesApi.class);

        Call < ArrayList< StockCategoryModel > > call = apiService.getStockCategoryByOrganizationId ( companyId );

        call.enqueue(new Callback <ArrayList<StockCategoryModel>> () {
            @Override
            public void onResponse(Call<ArrayList<StockCategoryModel>> call, Response <ArrayList<StockCategoryModel>> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {
                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201||statusCode==204) {

                        categoriesArrayList= response.body ();

                        if(categoriesArrayList!=null&&categoriesArrayList.size ()!=0){

                            StockCategoriesGridAdapter adapter = new StockCategoriesGridAdapter (StockOrderCategoryListScreen.this,categoriesArrayList);
                            mCategoryGrid.setAdapter(adapter);

                        }


                    }else {
                        Toast.makeText(StockOrderCategoryListScreen.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
            public void onFailure(Call<ArrayList<StockCategoryModel>> call, Throwable t) {

                if(dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
                //Toast.makeText(CustomerCreation.this, "Failed due to Bad Internet Connection", Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.toString());
            }
        });



    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            StockOrderCategoryListScreen.this.finish();

        }
        return super.onOptionsItemSelected(item);
    }
}
