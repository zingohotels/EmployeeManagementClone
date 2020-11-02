package app.zingo.employeemanagements.ui.Admin;

import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;

import app.zingo.employeemanagements.base.R;

public class TaskManagementHost extends TabActivity implements TabHost.OnTabChangeListener {

    TabHost tabHost;
    View tabNew,tabRequest;

    public static String NEW_LEAVE = "New Tab";
    public static String REQUEST_LEAVE = "Request Tab";



    TextView labelNew, labelRequest;


    int defaultValue = 0;
    public static final int MY_PERMISSIONS_REQUEST_RESULT = 1;

    int employeeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            setContentView(R.layout.activity_task_management_host);

            tabHost = findViewById(android.R.id.tabhost);

            tabNew = LayoutInflater.from(this).inflate(R.layout.tabhost_items_layout, null);
            tabRequest= LayoutInflater.from(this).inflate(R.layout.tabhost_items_layout, null);

            labelNew = tabNew.findViewById(R.id.tab_label);


            labelRequest = tabRequest.findViewById(R.id.tab_label);

            TabHost.TabSpec tabText = tabHost.newTabSpec(NEW_LEAVE);
            TabHost.TabSpec tabMaps= tabHost.newTabSpec(REQUEST_LEAVE);

            Bundle bundle = getIntent().getExtras();

            if(bundle!=null){

                employeeId = bundle.getInt("EmployeeId");
            }

/*9C9C9C*/
            labelNew.setText("New Task");
            tabText.setIndicator(tabNew);
            Intent dash = new Intent(this, CreateTaskScreen.class);
            dash.putExtra("EmployeeId", employeeId);
            tabText.setContent(dash);

            labelRequest.setText("Task List");
            tabMaps.setIndicator(tabRequest);
            Intent maps = new Intent(this, TaskListScreen.class);
            maps.putExtra("EmployeeId", employeeId);
            tabMaps.setContent(maps);


            tabHost.setOnTabChangedListener(this);



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

        labelNew.setTextColor(Color.parseColor("#4D4D4D"));
        labelNew.setTypeface(Typeface.DEFAULT);


        labelRequest.setTextColor(Color.parseColor("#4D4D4D"));
        labelRequest.setTypeface(Typeface.DEFAULT);



        changeTabSelection(tabId);

    }

    public void changeTabSelection(String tabId) {
        if (NEW_LEAVE.equals(tabId)) {
            labelNew.setTextColor(Color.parseColor("#6200EE"));
            labelNew.setTypeface(null, Typeface.BOLD);

        } else if (REQUEST_LEAVE.equals(tabId)) {
            labelRequest.setTextColor(Color.parseColor("#6200EE"));
            labelRequest.setTypeface(null, Typeface.BOLD);

        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id)
        {
            case android.R.id.home:

                TaskManagementHost.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
