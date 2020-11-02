package app.zingo.employeemanagements.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import androidx.viewpager.widget.PagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import app.zingo.employeemanagements.Custom.MyRegulerText;
import app.zingo.employeemanagements.model.PlanDesign;
import app.zingo.employeemanagements.ui.Plan.AdvancePlanSubscription;
import app.zingo.employeemanagements.ui.Plan.BasicPlanSubscription;
import app.zingo.employeemanagements.base.R;

public class PlanDesignAdapter extends PagerAdapter {

    //Activity activity;

    Context context;
    private ArrayList<PlanDesign> planDesigns;
    private LayoutInflater inflater;


    public PlanDesignAdapter(Context context, ArrayList<PlanDesign> planDesigns)
    {
        this.context = context;
        this.planDesigns = planDesigns;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }
    @Override
    public int getCount() {
        return planDesigns.size();
    }

    @Override
    public boolean isViewFromObject( @NotNull View view, @NotNull Object object) {
        return view == ((RelativeLayout) object);
    }

    @NotNull
    @Override
    public Object instantiateItem( @NotNull ViewGroup container, final int position) {
        View itemView = inflater.inflate(R.layout.basic_plan_design, container, false);

        FrameLayout backFram = (FrameLayout) itemView.findViewById(R.id.frame_lay);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.back_theme);
        MyRegulerText planName = (MyRegulerText) itemView.findViewById(R.id.name_plan);
        MyRegulerText planPrice = (MyRegulerText) itemView.findViewById(R.id.plan_price);
        MyRegulerText buy = (MyRegulerText) itemView.findViewById(R.id.button_buy_basic);
        RecyclerView featureList = (RecyclerView) itemView.findViewById(R.id.feature_list);

        container.addView(itemView);

        PlanDesign design = planDesigns.get(position);

        if(design!=null){

            String name= design.getName ();
            String amount= design.getRupees ();
            planName.setText(name);
            planPrice.setText(amount);


            final  String planNames  = design.getName();

            backFram.setBackgroundColor(Color.parseColor(""+design.getColor()));
            imageView.setImageResource(design.getDrawable());
            buy.setBackgroundColor(Color.parseColor(""+design.getColor()));

            if(design.getFeatures()!=null&&design.getFeatures().size()!=0){

                PlanFeaturesRecAdapter adapter = new PlanFeaturesRecAdapter(context,design.getFeatures());
                featureList.setAdapter(adapter);
            }


            //listening to image click
            buy.setOnClickListener( v -> {

                if(planNames!=null&&planNames.equalsIgnoreCase("Basic")){

                    Intent basic = new Intent(context,BasicPlanSubscription.class);
                    ((Activity)context).startActivity(basic);


                }else if(planNames!=null&&planNames.equalsIgnoreCase("Advance")){

                    Intent basic = new Intent(context, AdvancePlanSubscription.class);
                    ((Activity)context).startActivity(basic);

                }


            } );

        }






        return itemView;
    }

    @Override
    public void destroyItem( @NotNull ViewGroup container, int position, @NotNull Object object) {
        container.removeView((LinearLayout) object);
    }
}