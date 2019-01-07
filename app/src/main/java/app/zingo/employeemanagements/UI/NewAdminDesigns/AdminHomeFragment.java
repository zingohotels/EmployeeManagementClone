package app.zingo.employeemanagements.UI.NewAdminDesigns;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.itextpdf.text.pdf.StringUtils;

import app.zingo.employeemanagements.R;
import app.zingo.employeemanagements.UI.Employee.EmployeeListScreen;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdminHomeFragment extends Fragment {

    final String TAG = "Employer Dashboard";
    View layout;
    LinearLayout attendance,leaveApplications,employees;


    public AdminHomeFragment() {
        // Required empty public constructor
    }

    public static AdminHomeFragment getInstance() {
        return new AdminHomeFragment();
    }

    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
      // GAUtil.trackScreen(getActivity(), "Employer Dashboard");
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreateView(layoutInflater, viewGroup, savedInstanceState);

        try{
            this.layout = layoutInflater.inflate(R.layout.fragment_admin_home, viewGroup, false);
            setupListeners();
            viewGroup = this.layout.findViewById(R.id.renewWarning);
            viewGroup.setVisibility(View.GONE);
            /*if (layoutInflater != null) {

                *//*long daysDiff = DateUtil.daysDiff(new Date(), new Date(layoutInflater.getSubscriptionEndDate()));
                if (!StringUtils.equals(layoutInflater.getSubscriptionStatus(), "Active") || StringUtils.equalsIgnoreCase(AppConstants.PAY_MODE_PG, layoutInflater.getSubscriptionMode()) != null) {
                    viewGroup.setVisibility(8);
                } else if (daysDiff < 0 || daysDiff >= 4) {
                    viewGroup.setVisibility(8);
                } else {
                    viewGroup.setVisibility(0);
                }*//*
            } else {
                viewGroup.setVisibility(8);
            }*/
            return this.layout;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    public void onResume() {
        super.onResume();
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
    }

    public void setupListeners() {
        attendance = (LinearLayout) this.layout.findViewById(R.id.attendance);
        leaveApplications = (LinearLayout) this.layout.findViewById(R.id.leaveApplications);
        employees = (LinearLayout) this.layout.findViewById(R.id.employees);

        //App new version available
        View updatedText = this.layout.findViewById(R.id.updateText);
/*        if (this.mAppUser == null || this.mAppUser.getAppVersion() == 0 || AMApp.version <= this.mAppUser.getAppVersion()) {
            updatedText.setVisibility(8);
            return;
        }
        updatedText.setVisibility(0);
        findViewById.setOnClickListener(this);*/

        attendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openMenuViews(attendance);
            }
        });

        leaveApplications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMenuViews(leaveApplications);
            }
        });

        employees.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMenuViews(employees);
            }
        });
    }

    public void openMenuViews(View view) {

        Intent intent;
        if (view.getId() == R.id.employees) {
            intent = new Intent(getContext(), EmployeeListScreen.class);
            intent.putExtra("viewId", view.getId());
            getContext().startActivity(intent);
        } else if (view.getId() == R.id.attendance) {
            intent = new Intent(getContext(), EmployeeListScreen.class);
            intent.putExtra("viewId", view.getId());
            getContext().startActivity(intent);
        } else if (view.getId() == R.id.leaveApplications) {
            intent = new Intent(getContext(), EmployeeListScreen.class);
            intent.putExtra("viewId", view.getId());
            getContext().startActivity(intent);
        }
    }


}
