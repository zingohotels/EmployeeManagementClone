package app.zingo.employeemanagements.ui.Reseller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

import app.zingo.employeemanagements.adapter.PaymentListAdapter;
import app.zingo.employeemanagements.model.OrganizationPayments;
import app.zingo.employeemanagements.ui.landing.InternalServerErrorScreen;
import app.zingo.employeemanagements.utils.PreferenceHandler;
import app.zingo.employeemanagements.utils.ThreadExecuter;
import app.zingo.employeemanagements.utils.Util;
import app.zingo.employeemanagements.WebApi.ResellerAPI;
import app.zingo.employeemanagements.base.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResellerPaymentListScreen extends AppCompatActivity {


    RecyclerView mPaymentList;
    LinearLayout mNoRecord;

    private int mResellerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {

            setContentView(R.layout.activity_reseller_payment_list_screen);

            mPaymentList = findViewById(R.id.payment_list);
            mNoRecord = findViewById(R.id.noRecordFound);
            mPaymentList.setLayoutManager(new LinearLayoutManager(ResellerPaymentListScreen.this));

            mResellerId = PreferenceHandler.getInstance(ResellerPaymentListScreen.this).getResellerUserId();


            if(mResellerId!=0){
                getOrganizationsPayment(mResellerId);
            }else{
                mNoRecord.setVisibility(View.VISIBLE);
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }




        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void getOrganizationsPayment(final int mResellerId){


        final ProgressDialog progressDialog = new ProgressDialog(ResellerPaymentListScreen.this);
        progressDialog.setTitle("Loading Details..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {
                ResellerAPI apiService = Util.getClient().create(ResellerAPI.class);
                Call<ArrayList<OrganizationPayments>> call = apiService.getOrganizationPaymentBySellerId(mResellerId);

                call.enqueue(new Callback<ArrayList<OrganizationPayments>>() {
                    @Override
                    public void onResponse(Call<ArrayList<OrganizationPayments>> call, Response<ArrayList<OrganizationPayments>> response) {
                        int statusCode = response.code();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {


                            if (progressDialog!=null)
                                progressDialog.dismiss();
                            ArrayList<OrganizationPayments> list = response.body();




                            if (list !=null && list.size()!=0) {

                                mNoRecord.setVisibility(View.GONE);

                                PaymentListAdapter adapter = new PaymentListAdapter(ResellerPaymentListScreen.this,list);
                                mPaymentList.setAdapter(adapter);


                            }else{
                                mNoRecord.setVisibility(View.VISIBLE);

                                Toast.makeText(ResellerPaymentListScreen.this, "No Payment done from your organizations ", Toast.LENGTH_SHORT).show();

                            }

                        }else {

                            if (progressDialog!=null)
                                progressDialog.dismiss();

                            if(statusCode==500){
                                Intent error = new Intent(ResellerPaymentListScreen.this, InternalServerErrorScreen.class);
                                startActivity(error);
                            }else{
                                mNoRecord.setVisibility(View.VISIBLE);
                                Toast.makeText(ResellerPaymentListScreen.this,  "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
                            }


                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<OrganizationPayments>> call, Throwable t) {
                        // Log error here since request failed
                       /* if (progressDialog!=null)
                            progressDialog.dismiss();*/
                        Log.e("TAG", t.toString());
                    }
                });
            }


        });
    }
}
