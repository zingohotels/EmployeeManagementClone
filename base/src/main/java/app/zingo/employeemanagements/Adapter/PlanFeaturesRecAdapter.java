package app.zingo.employeemanagements.Adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

import app.zingo.employeemanagements.Custom.MyRegulerText;
import app.zingo.employeemanagements.Custom.MyTextView;
import app.zingo.employeemanagements.Model.Employee;
import app.zingo.employeemanagements.Model.Organization;
import app.zingo.employeemanagements.Model.PlanFeatures;
import app.zingo.employeemanagements.UI.NewAdminDesigns.BranchOptionScreen;
import app.zingo.employeemanagements.Utils.PreferenceHandler;
import app.zingo.employeemanagements.Utils.ThreadExecuter;
import app.zingo.employeemanagements.Utils.Util;
import app.zingo.employeemanagements.WebApi.EmployeeApi;
import app.zingo.employeemanagements.base.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlanFeaturesRecAdapter extends RecyclerView.Adapter<PlanFeaturesRecAdapter.ViewHolder> {

    Context context;
    private ArrayList<PlanFeatures> planFeatures;

    public PlanFeaturesRecAdapter(Context context, ArrayList<PlanFeatures> planFeatures)
    {
        this.context = context;
        this.planFeatures = planFeatures;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_plan_features,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final PlanFeatures features = planFeatures.get(position);

        if(features!=null){


            holder.mAddress.setText(""+features.getFeature());
        }


    }

    @Override
    public int getItemCount() {
        return planFeatures.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        MyRegulerText mAddress;


        public ViewHolder(View itemView) {
            super(itemView);


            mAddress = itemView.findViewById(R.id.feature_name);



        }
    }


}

