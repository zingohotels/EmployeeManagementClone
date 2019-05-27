package app.zingo.employeemanagements.UI.Common;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
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
import app.zingo.employeemanagements.Model.Organization;
import app.zingo.employeemanagements.Utils.PreferenceHandler;
import app.zingo.employeemanagements.Utils.ThreadExecuter;
import app.zingo.employeemanagements.Utils.Util;
import app.zingo.employeemanagements.WebApi.OrganizationApi;
import app.zingo.employeemanagements.base.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BranchListScreen extends AppCompatActivity {

    LinearLayout mNoBranches;
    RecyclerView mBranchList;
    FloatingActionButton mAddBranch;
    ImageView mLoader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{

            setContentView(R.layout.activity_branch_list_screen);

            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Branches");

            mNoBranches = findViewById(R.id.noBranchFound);
            mBranchList = findViewById(R.id.branch_list_data);
            mAddBranch = findViewById(R.id.add_branches_float);
            mLoader = findViewById(R.id.spin_loader);

            Glide.with(this)
                    .load(R.drawable.spin)
                    .into(new GlideDrawableImageViewTarget(mLoader));


            getBranches(PreferenceHandler.getInstance(BranchListScreen.this).getCompanyId());

            mAddBranch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent branch = new Intent(BranchListScreen.this, CreateBranchesScreen.class);
                    startActivity(branch);
                }
            });



        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public void getBranches(final int id) {



        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {

                final OrganizationApi orgApi = Util.getClient().create(OrganizationApi.class);
                Call<ArrayList<Organization>> getProf = orgApi.getBranchesByHeadOrganizationId(id);
                //Call<ArrayList<Blogs>> getBlog = blogApi.getBlogs();

                getProf.enqueue(new Callback<ArrayList<Organization>>() {

                    @Override
                    public void onResponse(Call<ArrayList<Organization>> call, Response<ArrayList<Organization>> response) {



                        if (response.code() == 200||response.code() == 201||response.code() == 204)
                        {
                            mLoader.setVisibility(View.GONE);

                            ArrayList<Organization> branches = response.body();

                            if(branches!=null&&branches.size()!=0){

                               mLoader.setVisibility(View.GONE);
                               mNoBranches.setVisibility(View.GONE);

                                BranchAdapter adapter = new BranchAdapter(BranchListScreen.this,branches);
                                mBranchList.setAdapter(adapter);


                            }


                        }else{

                            mLoader.setVisibility(View.GONE);

                            Toast.makeText(BranchListScreen.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Organization>> call, Throwable t) {

                        mLoader.setVisibility(View.GONE);


                        Toast.makeText(BranchListScreen.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                    }
                });

            }

        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id)
        {
            case android.R.id.home:

                BranchListScreen.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
