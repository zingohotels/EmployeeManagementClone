package app.zingo.employeemanagements.UI.Common;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import java.util.ArrayList;

import app.zingo.employeemanagements.Adapter.BranchAdapter;
import app.zingo.employeemanagements.Adapter.CustomerAdapter;
import app.zingo.employeemanagements.Model.Customer;
import app.zingo.employeemanagements.Model.Organization;
import app.zingo.employeemanagements.base.R;
import app.zingo.employeemanagements.Utils.PreferenceHandler;
import app.zingo.employeemanagements.Utils.ThreadExecuter;
import app.zingo.employeemanagements.Utils.Util;
import app.zingo.employeemanagements.WebApi.CustomerAPI;
import app.zingo.employeemanagements.WebApi.OrganizationApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerList extends AppCompatActivity {

    LinearLayout mNoCustomers;
    RecyclerView mCustomerList;
    FloatingActionButton mAddCustomer;
    ImageView mLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{

            setContentView(R.layout.activity_customer_list);

            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Customers");

            mNoCustomers = findViewById(R.id.noCustomerFound);
            mCustomerList = findViewById(R.id.customer_list_data);
            mAddCustomer = findViewById(R.id.add_customer_float);
            mLoader = findViewById(R.id.spin_loader);

            Glide.with(this)
                    .load(R.drawable.spin)
                    .into(new GlideDrawableImageViewTarget(mLoader));


            getCustomers(PreferenceHandler.getInstance(CustomerList.this).getCompanyId());

            mAddCustomer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent branch = new Intent(CustomerList.this,CustomerCreation.class);
                    startActivity(branch);
                }
            });


        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public void getCustomers(final int id) {



        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {

                final CustomerAPI orgApi = Util.getClient().create(CustomerAPI.class);
                Call<ArrayList<Customer>> getProf = orgApi.getCustomerByOrganizationId(id);
                //Call<ArrayList<Blogs>> getBlog = blogApi.getBlogs();

                getProf.enqueue(new Callback<ArrayList<Customer>>() {

                    @Override
                    public void onResponse(Call<ArrayList<Customer>> call, Response<ArrayList<Customer>> response) {



                        if (response.code() == 200||response.code() == 201||response.code() == 204)
                        {
                            mLoader.setVisibility(View.GONE);

                            ArrayList<Customer> branches = response.body();

                            if(branches!=null&&branches.size()!=0){

                                mLoader.setVisibility(View.GONE);
                                mNoCustomers.setVisibility(View.GONE);

                                mCustomerList.removeAllViews();

                                CustomerAdapter adapter = new CustomerAdapter(CustomerList.this,branches);
                                mCustomerList.setAdapter(adapter);


                            }


                        }else{

                            mLoader.setVisibility(View.GONE);

                            Toast.makeText(CustomerList.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Customer>> call, Throwable t) {

                        mLoader.setVisibility(View.GONE);


                        Toast.makeText(CustomerList.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                    }
                });

            }

        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getCustomers(PreferenceHandler.getInstance(CustomerList.this).getCompanyId());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id)
        {
            case android.R.id.home:

                CustomerList.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
