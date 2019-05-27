package app.zingo.employeemanagements.UI.Employee;

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

//activity_attendance_screen

public class AttendanceScreen extends TabActivity implements TabHost.OnTabChangeListener {

    TabHost tabHost;
    View tabWorking,tabLeave;

    public static String WORK_TAB = "Work Tab";
    public static String LEAVE_TAB = "Leave Tab";



    TextView labelWorking, labelLeave;


    int defaultValue = 0;
    public static final int MY_PERMISSIONS_REQUEST_RESULT = 1;

    int employeeId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{

            setContentView(R.layout.activity_attendance_screen);

            tabHost = findViewById(android.R.id.tabhost);

            tabWorking = LayoutInflater.from(this).inflate(R.layout.tabhost_items_layout, null);
            tabLeave= LayoutInflater.from(this).inflate(R.layout.tabhost_items_layout, null);

            labelWorking = tabWorking.findViewById(R.id.tab_label);


            labelLeave = tabLeave.findViewById(R.id.tab_label);

            TabHost.TabSpec tabText = tabHost.newTabSpec(WORK_TAB);
            TabHost.TabSpec tabMaps= tabHost.newTabSpec(LEAVE_TAB);

            Bundle bundle = getIntent().getExtras();

            if(bundle!=null){

                employeeId = bundle.getInt("EmployeeId");
            }

/*9C9C9C*/
            labelWorking.setText(getResources().getString(R.string.workingDays));
            tabText.setIndicator(tabWorking);
            Intent dash = new Intent(this, WorkedDaysListScreen.class);
            dash.putExtra("EmployeeId", employeeId);
            tabText.setContent(dash);

            labelLeave.setText(getResources().getString(R.string.leaveDays));
            tabMaps.setIndicator(tabLeave);
            Intent maps = new Intent(this, LeaveTakenDays.class);
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

        labelWorking.setTextColor(Color.parseColor("#4D4D4D"));
        labelWorking.setTypeface(Typeface.DEFAULT);


        labelLeave.setTextColor(Color.parseColor("#4D4D4D"));
        labelLeave.setTypeface(Typeface.DEFAULT);



        changeTabSelection(tabId);

    }

    public void changeTabSelection(String tabId) {
        if (WORK_TAB.equals(tabId)) {
            labelWorking.setTextColor(Color.parseColor("#6200EE"));
            labelWorking.setTypeface(null, Typeface.BOLD);

        } else if (LEAVE_TAB.equals(tabId)) {
            labelLeave.setTextColor(Color.parseColor("#6200EE"));
            labelLeave.setTypeface(null, Typeface.BOLD);

        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id)
        {
            case android.R.id.home:

                AttendanceScreen.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
