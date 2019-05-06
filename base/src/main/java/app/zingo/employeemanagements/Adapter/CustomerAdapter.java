package app.zingo.employeemanagements.Adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import app.zingo.employeemanagements.Model.Customer;
import app.zingo.employeemanagements.Model.Employee;
import app.zingo.employeemanagements.Model.Organization;
import app.zingo.employeemanagements.base.R;
import app.zingo.employeemanagements.UI.Common.CustomerCreation;
import app.zingo.employeemanagements.UI.NewAdminDesigns.BranchOptionScreen;
import app.zingo.employeemanagements.Utils.PreferenceHandler;
import app.zingo.employeemanagements.Utils.ThreadExecuter;
import app.zingo.employeemanagements.Utils.Util;
import app.zingo.employeemanagements.WebApi.EmployeeApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Customer> customers;

    public CustomerAdapter(Context context, ArrayList<Customer> customers)
    {
        this.context = context;
        this.customers = customers;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.branch_list_adapter,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final Customer customer = customers.get(position);

        if(customer!=null){

            holder.mOrgName.setText(""+customer.getCustomerName());
            holder.mAddress.setText(""+customer.getCustomerAddress());

            holder.mBranch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    Intent customers = new Intent(context,CustomerCreation.class);
                    Bundle bun = new Bundle();
                    bun.putSerializable("Customer",customer);
                    customers.putExtras(bun);
                    ((Activity)context).startActivity(customers);

                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return customers.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        MyTextView mOrgName;
        MyRegulerText mAddress;
        LinearLayout mBranch;

        public ViewHolder(View itemView) {
            super(itemView);

            mOrgName = itemView.findViewById(R.id.branch_name_adapter);
            mAddress = itemView.findViewById(R.id.branch_address);
            mBranch = itemView.findViewById(R.id.branch_click_layout);


        }
    }


}

