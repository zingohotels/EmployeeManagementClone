package app.zingo.employeemanagements.ui.Admin;

import android.app.TabActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;

import app.zingo.employeemanagements.base.R;

//activity_login_details_host

public class LoginDetailsHost extends TabActivity implements TabHost.OnTabChangeListener {

    TabHost tabHost;
    View tabMeeting,tabMap;

    public static String TEXT_TAB = "Text Tab";
    public static String MAP_TAB = "Map Tab";



    TextView labelText, labelMap;


    int defaultValue = 0;
    public static final int MY_PERMISSIONS_REQUEST_RESULT = 1;

    int employeeId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{

            setContentView(R.layout.activity_login_details_host);

            tabHost = findViewById(android.R.id.tabhost);

            tabMeeting = LayoutInflater.from(this).inflate(R.layout.tabhost_items_layout, null);
            tabMap= LayoutInflater.from(this).inflate(R.layout.tabhost_items_layout, null);

            labelText = tabMeeting.findViewById(R.id.tab_label);


            labelMap = tabMap.findViewById(R.id.tab_label);

            TabHost.TabSpec tabText = tabHost.newTabSpec(TEXT_TAB);
            TabHost.TabSpec tabMaps= tabHost.newTabSpec(MAP_TAB);

            Bundle bundle = getIntent().getExtras();

            if(bundle!=null){

                employeeId = bundle.getInt("EmployeeId");
            }

/*9C9C9C*/
            labelText.setText(getResources().getString(R.string.textMeeting));
            tabText.setIndicator(tabMeeting);
            Intent dash = new Intent(this, EmployeeLoginList.class);
            dash.putExtra("EmployeeId", employeeId);
            tabText.setContent(dash);

            labelMap.setText(getResources().getString(R.string.mapView));
            tabMaps.setIndicator(tabMap);
            Intent maps = new Intent(this, EmployeeLoginMapView.class);
            maps.putExtra("EmployeeId", employeeId);
            tabMaps.setContent(maps);


            tabHost.setOnTabChangedListener(this);

            checkPermission();





            /** Add the tabs to the TabHost to display. */
            tabHost.addTab(tabText);
            tabHost.addTab(tabMaps);



            int page = getIntent().getIntExtra("ARG_PAGE", defaultValue);



            int pageno = getIntent().getIntExtra("TABNAME",0);
            if(pageno != 0)
            {
                tabHost.setCurrentTab(pageno);
            }
            else
            {
                tabHost.setCurrentTab(page);
            }



        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onTabChanged(String tabId) {

        labelText.setTextColor(Color.parseColor("#4D4D4D"));
        labelText.setTypeface(Typeface.DEFAULT);


        labelMap.setTextColor(Color.parseColor("#4D4D4D"));
        labelMap.setTypeface(Typeface.DEFAULT);



        changeTabSelection(tabId);

    }

    public void changeTabSelection(String tabId) {
        if (TEXT_TAB.equals(tabId)) {
            labelText.setTextColor(Color.parseColor("#6200EE"));
            labelText.setTypeface(null, Typeface.BOLD);

        } else if (MAP_TAB.equals(tabId)) {
            labelMap.setTextColor(Color.parseColor("#6200EE"));
            labelMap.setTypeface(null, Typeface.BOLD);

        }
    }

    public boolean checkPermission() {
        if (( ContextCompat.checkSelfPermission( LoginDetailsHost.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)){



            if (( ActivityCompat.shouldShowRequestPermissionRationale( LoginDetailsHost.this, android.Manifest.permission.ACCESS_FINE_LOCATION))) {

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions( LoginDetailsHost.this,
                        new String[]{
                                android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_RESULT);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions( LoginDetailsHost.this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_RESULT);


            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id)
        {
            case android.R.id.home:

                LoginDetailsHost.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
