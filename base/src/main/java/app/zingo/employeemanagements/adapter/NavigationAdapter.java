package app.zingo.employeemanagements.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import app.zingo.employeemanagements.model.Navigation_Model;
import app.zingo.employeemanagements.ui.Common.BranchListScreen;
import app.zingo.employeemanagements.ui.Common.BreakTimeListScreen;
import app.zingo.employeemanagements.ui.Common.HolidayListActivityScreen;
import app.zingo.employeemanagements.ui.Company.OrganizationDetailScree;
import app.zingo.employeemanagements.ui.NewAdminDesigns.BranchInfoScreen;
import app.zingo.employeemanagements.ui.NewAdminDesigns.DepartmentLilstScreen;
import app.zingo.employeemanagements.ui.NewAdminDesigns.EmployeeUpdateListScreen;
import app.zingo.employeemanagements.ui.NewAdminDesigns.PriceUpdateListScreen;
import app.zingo.employeemanagements.ui.NewAdminDesigns.ShiftScreenList;
import app.zingo.employeemanagements.ui.NewAdminDesigns.StockOptionsListScreen;
import app.zingo.employeemanagements.utils.PreferenceHandler;
import app.zingo.employeemanagements.base.R;

public class NavigationAdapter extends RecyclerView.Adapter< NavigationAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Navigation_Model> nevigation_models;

    public NavigationAdapter(Context context, ArrayList<Navigation_Model> nevigation_models) {
        this.context = context;
        this.nevigation_models = nevigation_models;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nevigation,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        holder.imagehistory.setImageResource(nevigation_models.get(position).getImagehistory());
        holder.image1.setImageResource(nevigation_models.get(position).getImage1());

        holder.tv1.setText(nevigation_models.get(position).getTv1());

        holder.mNavLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String text = nevigation_models.get(position).getTv1();

                if(text!=null&&!text.isEmpty()){

                    switch (text)
                    {
                        case "About Organization":

                            if(PreferenceHandler.getInstance(context).getHeadOrganizationId()!=0){
                                PreferenceHandler.getInstance(context).setCompanyId(PreferenceHandler.getInstance(context).getHeadOrganizationId());
                            }

                            Intent organization = new Intent(context, OrganizationDetailScree.class);
                            ((Activity)context).startActivity(organization);
                            break;

                        case "Branches":
                            Intent branches = new Intent(context, BranchListScreen.class);
                            ((Activity)context).startActivity(branches);
                            break;

                        case "Holidays":
                            Intent holidays = new Intent(context, HolidayListActivityScreen.class);
                            holidays.putExtra("OrganizationId", PreferenceHandler.getInstance(context).getBranchId());
                            ((Activity)context).startActivity(holidays);
                            break;

                        case "Office Timing":
                            Intent office = new Intent(context, ShiftScreenList.class);
                            office.putExtra("OrganizationId", PreferenceHandler.getInstance(context).getBranchId());
                            ((Activity)context).startActivity(office);
                            break;

                        case "Break Timing":
                            Intent breakTime = new Intent(context, BreakTimeListScreen.class);
                            breakTime.putExtra("OrganizationId", PreferenceHandler.getInstance(context).getBranchId());
                            ((Activity)context).startActivity(breakTime);
                            break;

                        case "Branch Info":
                            Intent branch = new Intent(context, BranchInfoScreen.class);
                            ((Activity)context).startActivity(branch);
                            break;

                        case "Departments":
                            Intent dept = new Intent(context, DepartmentLilstScreen.class);
                            dept.putExtra("OrganizationId", PreferenceHandler.getInstance(context).getBranchId());
                            ((Activity)context).startActivity(dept);
                            break;

                            case "Employees":
                            Intent employees = new Intent(context, EmployeeUpdateListScreen.class);
                            employees.putExtra("OrganizationId", PreferenceHandler.getInstance(context).getBranchId());
                            ((Activity)context).startActivity(employees);
                            break;

                            //"Stock Categories","Stock SubCategories","Stock Brands","Stock Items"
                        case "Stock Categories":
                            Intent stocklist = new Intent(context, StockOptionsListScreen.class);
                            stocklist.putExtra ( "Type","Stock Categories" );
                            ((Activity)context).startActivity(stocklist);
                            break;

                        case "Stock SubCategories":
                            Intent stocksublist = new Intent(context, StockOptionsListScreen.class);
                            stocksublist.putExtra ( "Type","Stock SubCategories" );
                            ((Activity)context).startActivity(stocksublist);
                            break;

                        case "Stock Brands":
                            Intent stockbrandlist = new Intent(context, StockOptionsListScreen.class);
                            stockbrandlist.putExtra ( "Type","Stock Brands" );
                            ((Activity)context).startActivity(stockbrandlist);
                            break;

                        case "Stock Items":
                            Intent stockitemlist = new Intent(context, StockOptionsListScreen.class);
                            stockitemlist.putExtra ( "Type","Stock Items" );
                            ((Activity)context).startActivity(stockitemlist);
                            break;

                        case "Stock Orders":
                            Intent stockorderslist = new Intent(context, StockOptionsListScreen.class);
                            stockorderslist.putExtra ( "Type","Stock Orders" );
                            ((Activity)context).startActivity(stockorderslist);
                            break;

                            case "Price Update":
                            Intent priceUpdate = new Intent(context, PriceUpdateListScreen.class);
                            priceUpdate.putExtra ( "Type","Price Update" );
                            ((Activity)context).startActivity(priceUpdate);
                            break;

                    }

                }
            }
        });

    }

    @Override
    public int getItemCount() {return nevigation_models.size();}

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv1;
        ImageView imagehistory,image1;
        LinearLayout mNavLay;


        public ViewHolder(View itemView) {
            super(itemView);


            tv1 = itemView.findViewById(R.id.tv1);

            imagehistory = itemView.findViewById(R.id.imagehistory);
            image1 = itemView.findViewById(R.id.image1);
            mNavLay = itemView.findViewById(R.id.navigation_lay);

        }
    }
}
