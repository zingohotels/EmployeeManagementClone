package app.zingo.employeemanagements.UI.NewAdminDesigns;

import android.app.ProgressDialog;
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

import app.zingo.employeemanagements.Adapter.CustomerAdapter;
import app.zingo.employeemanagements.Adapter.ShiftAdapter;
import app.zingo.employeemanagements.Model.Customer;
import app.zingo.employeemanagements.Model.WorkingDay;
import app.zingo.employeemanagements.UI.Common.CustomerCreation;
import app.zingo.employeemanagements.UI.Common.CustomerList;
import app.zingo.employeemanagements.UI.Company.WorkingDaysScreen;
import app.zingo.employeemanagements.Utils.PreferenceHandler;
import app.zingo.employeemanagements.Utils.ThreadExecuter;
import app.zingo.employeemanagements.Utils.Util;
import app.zingo.employeemanagements.WebApi.CustomerAPI;
import app.zingo.employeemanagements.WebApi.OrganizationTimingsAPI;
import app.zingo.employeemanagements.base.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShiftScreenList extends AppCompatActivity {

    LinearLayout mNoShifts;
    RecyclerView mShiftsList;
    FloatingActionButton mAddShifts;
    ImageView mLoader;

    int orgId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{

            setContentView(R.layout.activity_shift_screen_list);

            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Shift List");

            Bundle bun = getIntent().getExtras();

            if(bun!=null){

                orgId = bun.getInt("OrganizationId",0);
            }

            mNoShifts = findViewById(R.id.noShiftFound);
            mShiftsList = findViewById(R.id.shift_list_data);
            mAddShifts = findViewById(R.id.add_shift_float);
            mLoader = findViewById(R.id.spin_loader);

            Glide.with(this)
                    .load(R.drawable.spin)
                    .into(new GlideDrawableImageViewTarget(mLoader));

            if(orgId!=0){

                getShiftTimings(orgId);

            }else{
                getShiftTimings(PreferenceHandler.getInstance(ShiftScreenList.this).getCompanyId());
            }

            mAddShifts.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent branch = new Intent(ShiftScreenList.this, WorkingDaysScreen.class);
                    branch.putExtra("OrganizationId",orgId);
                    startActivity(branch);
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void getShiftTimings(final int id) {



        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {

                final OrganizationTimingsAPI orgApi = Util.getClient().create(OrganizationTimingsAPI.class);
                Call<ArrayList<WorkingDay>> getProf = orgApi.getOrganizationTimingByOrgId(id);
                //Call<ArrayList<Blogs>> getBlog = blogApi.getBlogs();

                getProf.enqueue(new Callback<ArrayList<WorkingDay>>() {

                    @Override
                    public void onResponse(Call<ArrayList<WorkingDay>> call, Response<ArrayList<WorkingDay>> response) {



                        if (response.code() == 200||response.code() == 201||response.code() == 204)
                        {
                            mLoader.setVisibility(View.GONE);

                            ArrayList<WorkingDay> branches = response.body();

                            if(branches!=null&&branches.size()!=0){

                                mLoader.setVisibility(View.GONE);
                                mNoShifts.setVisibility(View.GONE);

                                mShiftsList.removeAllViews();

                                ShiftAdapter adapter = new ShiftAdapter(ShiftScreenList.this,branches);
                                mShiftsList.setAdapter(adapter);


                            }


                        }else{

                            mLoader.setVisibility(View.GONE);

                            Toast.makeText(ShiftScreenList.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<WorkingDay>> call, Throwable t) {

                        mLoader.setVisibility(View.GONE);


                        Toast.makeText(ShiftScreenList.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                    }
                });

            }

        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(orgId!=0){

            getShiftTimings(orgId);

        }else{
            getShiftTimings(PreferenceHandler.getInstance(ShiftScreenList.this).getCompanyId());
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id)
        {
            case android.R.id.home:

                ShiftScreenList.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
