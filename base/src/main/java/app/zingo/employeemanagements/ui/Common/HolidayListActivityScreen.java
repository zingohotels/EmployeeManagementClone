package app.zingo.employeemanagements.ui.Common;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import app.zingo.employeemanagements.adapter.HolidayListAdapter;
import app.zingo.employeemanagements.Custom.MyEditText;
import app.zingo.employeemanagements.model.HolidayList;
import app.zingo.employeemanagements.utils.PreferenceHandler;
import app.zingo.employeemanagements.utils.ThreadExecuter;
import app.zingo.employeemanagements.utils.Util;
import app.zingo.employeemanagements.WebApi.OrganizationHolidayListsAPI;
import app.zingo.employeemanagements.base.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HolidayListActivityScreen extends AppCompatActivity {

    LinearLayout mNoBranches;
    RecyclerView mBranchList;
    FloatingActionButton mAddBranch;
    ImageView mLoader;

    int orgId;

    private String current = "";
    private String ddmmyyyy = "DDMMYYYY";
    private Calendar cal = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{

            setContentView(R.layout.activity_holiday_list_screen);

            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Holiday List");

            mNoBranches = findViewById(R.id.noBranchFound);
            mBranchList = findViewById(R.id.branch_list_data);
            mAddBranch = findViewById(R.id.add_branches_float);
            mLoader = findViewById(R.id.spin_loader);

            Glide.with(this)
                    .load(R.drawable.spin)
                    .into(new GlideDrawableImageViewTarget(mLoader));


           // getBranches(PreferenceHandler.getInstance(HolidayListActivityScreen.this).getCompanyId());

            Bundle bun = getIntent().getExtras();

            if(bun!=null){

                orgId = bun.getInt("OrganizationId",0);
            }

            if(orgId!=0){

                getHolidays(orgId);
            }else{
                getHolidays(PreferenceHandler.getInstance( HolidayListActivityScreen.this).getCompanyId());
            }

            mAddBranch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(orgId!=0){

                        holidayAlert(orgId);
                    }else{
                        holidayAlert(PreferenceHandler.getInstance( HolidayListActivityScreen.this).getCompanyId());
                    }

                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void getHolidays(final int id) {



        new ThreadExecuter ().execute( new Runnable() {
            @Override
            public void run() {

                final OrganizationHolidayListsAPI orgApi = Util.getClient().create(OrganizationHolidayListsAPI.class);
                Call<ArrayList<HolidayList>> getProf = orgApi.getHolidaysByOrgId(id);
                //Call<ArrayList<Blogs>> getBlog = blogApi.getBlogs();

                getProf.enqueue(new Callback<ArrayList<HolidayList>>() {

                    @Override
                    public void onResponse(Call<ArrayList<HolidayList>> call, Response<ArrayList<HolidayList>> response) {



                        if (response.code() == 200||response.code() == 201||response.code() == 204)
                        {
                            mLoader.setVisibility(View.GONE);

                            ArrayList<HolidayList> holidayLists = response.body();

                            if(holidayLists!=null&&holidayLists.size()!=0){

                                mLoader.setVisibility(View.GONE);
                                mNoBranches.setVisibility(View.GONE);

                                HolidayListAdapter adapter = new HolidayListAdapter( HolidayListActivityScreen.this,holidayLists);
                                mBranchList.setAdapter(adapter);


                            }


                        }else{

                            mLoader.setVisibility(View.GONE);

                            Toast.makeText( HolidayListActivityScreen.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<HolidayList>> call, Throwable t) {

                        mLoader.setVisibility(View.GONE);


                        Toast.makeText( HolidayListActivityScreen.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                    }
                });

            }

        });
    }

    private void holidayAlert(final int id){

        AlertDialog.Builder builder = new AlertDialog.Builder( HolidayListActivityScreen.this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View views = inflater.inflate(R.layout.custom_alert_holiday_create, null);

        builder.setView(views);
        final Button mSave = views.findViewById(R.id.save);
        final MyEditText desc = views.findViewById(R.id.date_holiday);
        final TextInputEditText mName = views.findViewById(R.id.holiday_name);


        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);



        desc.addTextChangedListener(new TextWatcher() {



            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!s.toString().equals(current)) {
                    String clean = s.toString().replaceAll("[^\\d.]|\\.", "");
                    String cleanC = current.replaceAll("[^\\d.]|\\.", "");

                    int cl = clean.length();
                    int sel = cl;
                    for (int i = 2; i <= cl && i < 6; i += 2) {
                        sel++;
                    }
                    //Fix for pressing delete next to a forward slash
                    if (clean.equals(cleanC)) sel--;

                    if (clean.length() < 8){
                        clean = clean + ddmmyyyy.substring(clean.length());
                    }else{
                        //This part makes sure that when we finish entering numbers
                        //the date is correct, fixing it otherwise
                        int day  = Integer.parseInt(clean.substring(0,2));
                        int mon  = Integer.parseInt(clean.substring(2,4));
                        int year = Integer.parseInt(clean.substring(4,8));

                        String currentYear = new SimpleDateFormat("yyyy").format(new Date());

                        int years = Integer.parseInt(currentYear);

                        mon = mon < 1 ? 1 : mon > 12 ? 12 : mon;
                        cal.set(Calendar.MONTH, mon-1);
                        year = (year<1900)?1900:(year>2100)?years:year;
                        cal.set(Calendar.YEAR, year);
                        // ^ first set year for the line below to work correctly
                        //with leap years - otherwise, date e.g. 29/02/2012
                        //would be automatically corrected to 28/02/2012

                        day = (day > cal.getActualMaximum(Calendar.DATE))? cal.getActualMaximum(Calendar.DATE):day;
                        clean = String.format("%02d%02d%02d",day, mon, year);
                    }

                    clean = String.format("%s/%s/%s", clean.substring(0, 2),
                            clean.substring(2, 4),
                            clean.substring(4, 8));

                    sel = sel < 0 ? 0 : sel;
                    current = clean;
                    desc.setText(current);
                    desc.setSelection(sel < current.length() ? sel : current.length());
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String name = mName.getText().toString();
                String descrp = desc.getText().toString();

                if(name.isEmpty()){

                    Toast.makeText( HolidayListActivityScreen.this, "Please enter Holiday Name", Toast.LENGTH_SHORT).show();

                }else if (descrp.isEmpty()){

                    Toast.makeText( HolidayListActivityScreen.this, "Please enter Holiday Date", Toast.LENGTH_SHORT).show();
                }else{

                    HolidayList holidayList = new HolidayList();
                    holidayList.setHolidayDescription(name);

                    try {

                        Date date = new SimpleDateFormat("dd/MM/yyyy").parse(descrp);
                        holidayList.setHolidayDate(new SimpleDateFormat("MM/dd/yyyy").format(date));
                        holidayList.setHolidayDay(new SimpleDateFormat("EEEE").format(date));

                        if(orgId!=0){

                            holidayList.setOrganizationId(orgId);
                        }else{
                            holidayList.setOrganizationId(PreferenceHandler.getInstance( HolidayListActivityScreen.this).getCompanyId());
                        }

                        addHolidays(holidayList,dialog);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


            }
        });

    }

    public void addHolidays(final HolidayList departments, final AlertDialog dialogs) {


        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Saving Details..");
        dialog.setCancelable(false);
        dialog.show();

        OrganizationHolidayListsAPI apiService = Util.getClient().create(OrganizationHolidayListsAPI.class);

        Call<HolidayList> call = apiService.addHolidays(departments);

        call.enqueue(new Callback<HolidayList>() {
            @Override
            public void onResponse(Call<HolidayList> call, Response<HolidayList> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                try
                {
                    if(dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }

                    int statusCode = response.code();
                    if (statusCode == 200 || statusCode == 201 || statusCode == 204) {

                        HolidayList s = response.body();

                        if(s!=null){

                            Toast.makeText( HolidayListActivityScreen.this, "Saved Successfully ", Toast.LENGTH_SHORT).show();

                            dialogs.dismiss();

                            if(orgId!=0){

                                getHolidays(orgId);
                            }else{
                                getHolidays(PreferenceHandler.getInstance( HolidayListActivityScreen.this).getCompanyId());
                            }


                        }




                    }else {
                        Toast.makeText( HolidayListActivityScreen.this, "Failed Due to "+response.message(), Toast.LENGTH_SHORT).show();
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
            public void onFailure(Call<HolidayList> call, Throwable t) {

                if(dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
                Toast.makeText( HolidayListActivityScreen.this , "Failed due to Bad Internet Connection" , Toast.LENGTH_SHORT ).show( );
                Log.e("TAG", t.toString());
            }
        });



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id)
        {
            case android.R.id.home:

                HolidayListActivityScreen.this.finish();
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}
