package app.zingo.employeemanagements.ui.newemployeedesign;


import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

import app.zingo.employeemanagements.adapter.LoginDetailsNotificationAdapter;
import app.zingo.employeemanagements.model.LoginDetailsNotificationManagers;
import app.zingo.employeemanagements.utils.PreferenceHandler;
import app.zingo.employeemanagements.utils.ThreadExecuter;
import app.zingo.employeemanagements.utils.Util;
import app.zingo.employeemanagements.WebApi.LoginNotificationAPI;
import app.zingo.employeemanagements.base.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class EmployeeNotificationsScreen extends Fragment {

    final String TAG = "Employer Task Employee";
    View layout;
    LinearLayout mNoNotification;
    private static LoginDetailsNotificationAdapter mAdapter;
    static Context mContext;



    private RecyclerView mNotificatioinRecyclerView;


    public EmployeeNotificationsScreen() {
        // Required empty public constructor
    }

    public static EmployeeNotificationsScreen getInstance() {
        return new EmployeeNotificationsScreen ();
    }

    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        // GAUtil.trackScreen(getActivity(), "Employer Dashboard");
    }


    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        super.onCreateView(layoutInflater, viewGroup, savedInstanceState);
        try{
            this.layout = layoutInflater.inflate(R.layout.fragment_employee_notifications_screen, viewGroup, false);
            mNotificatioinRecyclerView = this.layout.findViewById(R.id.listNotifications);
            mNoNotification = this.layout.findViewById(R.id.noRecordFound);
            mNotificatioinRecyclerView.setVisibility(View.VISIBLE);
            mNotificatioinRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mContext = getContext();

            getLoginNotifications();

            return this.layout;
        }catch(Exception e){
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

    private void getLoginNotifications(){


       /* final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Loading Notifications");
        progressDialog.setCancelable(false);
        progressDialog.show();*/

        new ThreadExecuter ().execute( new Runnable() {
            @Override
            public void run() {
                LoginNotificationAPI apiService = Util.getClient().create( LoginNotificationAPI.class);
                Call<ArrayList<LoginDetailsNotificationManagers>> call = apiService.getNotificationByEmployeeId(PreferenceHandler.getInstance(getActivity()).getUserId());

                call.enqueue(new Callback<ArrayList<LoginDetailsNotificationManagers>>() {
                    @Override
                    public void onResponse(Call<ArrayList<LoginDetailsNotificationManagers>> call, Response<ArrayList<LoginDetailsNotificationManagers>> response) {
                        int statusCode = response.code();
                        if (statusCode == 200 || statusCode == 201 || statusCode == 203 || statusCode == 204) {


                       /*     if (progressDialog != null&&progressDialog.isShowing())
                                progressDialog.dismiss();*/
                            ArrayList<LoginDetailsNotificationManagers> list = response.body();


                            if (list !=null && list.size()!=0) {





                                mAdapter = new LoginDetailsNotificationAdapter(getActivity(), list);
                                mNotificatioinRecyclerView.setAdapter(mAdapter);

                            }else{

                                mNoNotification.setVisibility(View.VISIBLE);

                            }

                        }else {
                            mNoNotification.setVisibility(View.VISIBLE);


                            Toast.makeText(getActivity(), "Failed due to : "+response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<LoginDetailsNotificationManagers>> call, Throwable t) {
                        // Log error here since request failed
                     /*   if (progressDialog != null&&progressDialog.isShowing())
                            progressDialog.dismiss();*/
                        mNoNotification.setVisibility(View.VISIBLE);
                        Log.e("TAG", t.toString());
                    }
                });
            }


        });
    }

}
