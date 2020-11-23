package app.zingo.employeemanagements.ui.Employee;

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

import app.zingo.employeemanagements.model.Employee;
import app.zingo.employeemanagements.ui.NewAdminDesigns.LeaveDashBoardAdminScreen;
import app.zingo.employeemanagements.base.R;

//activity_leave_management_host

public class LeaveManagementHost extends TabActivity implements TabHost.OnTabChangeListener {
    public TabHost tabHost;
    public View tabNew,tabRequest;
    public static String NEW_LEAVE = "New Tab";
    public static String REQUEST_LEAVE = "Request Tab";
    public TextView labelNew, labelRequest;
    public int defaultValue = 0;
    public static final int MY_PERMISSIONS_REQUEST_RESULT = 1;
    public int employeeId;
    public Employee employee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            setContentView(R.layout.activity_leave_management_host);
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
                employee = (Employee)bundle.getSerializable("Employee");
            }

            labelNew.setText(getResources().getString(R.string.leaveNew));
            tabText.setIndicator(tabNew);
            Intent dash = new Intent(this, ApplyLeaveScreen.class);
            dash.putExtra("EmployeeId", employeeId);
            tabText.setContent(dash);

            labelRequest.setText(getResources().getString(R.string.leaveRequest));
            tabMaps.setIndicator(tabRequest);
            Intent maps = new Intent(this, LeaveDashBoardAdminScreen.class);
            Bundle bundles = new Bundle();
            bundles.putBoolean("EmployeeScreen",true);
            bundles.putInt("EmployeeId",employeeId);
            bundles.putSerializable("Employee",employee);
            maps.putExtras(bundles);
            tabMaps.setContent(maps);
            tabHost.setOnTabChangedListener(this);

            /** Add the tabs to the TabHost to display. */
            tabHost.addTab(tabText);
            tabHost.addTab(tabMaps);
            int page = getIntent().getIntExtra("ARG_PAGE", defaultValue);

            int pageno = getIntent().getIntExtra("TABNAME",0);
            if(pageno != 0) {
                tabHost.setCurrentTab(pageno);
            }
            else {
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
        if ( id == android.R.id.home ) {
            LeaveManagementHost.this.finish ( );
        }
        return super.onOptionsItemSelected(item);
    }
}
