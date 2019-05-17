package app.zingo.employeemanagements.UI.Plan;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import app.zingo.employeemanagements.Adapter.PlanDesignAdapter;
import app.zingo.employeemanagements.Adapter.SplashSlider;
import app.zingo.employeemanagements.Model.PlanDesign;
import app.zingo.employeemanagements.Model.PlanFeatures;
import app.zingo.employeemanagements.UI.SupportScreen;
import app.zingo.employeemanagements.base.R;

public class PlanDesignActivity extends AppCompatActivity {

    ViewPager slidePager;
    Button basic_buy;
    int[] layouts = {R.layout.basic_plan_design,R.layout.advance_plan_design};
    LinearLayout dots;
    ImageView[] dot;

    int currentPage = 0,start = 0,end = 0;
    Timer timer;
    final long DELAY_MS = 2000;
    final long PERIOD_MS = 7000;

    ArrayList<PlanDesign> planDesigns;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{

            setContentView(R.layout.activity_plan_design);

            slidePager = findViewById(R.id.pager_image_support);

            planDesigns = new ArrayList<>();

            PlanDesign basicDesign = new PlanDesign();
            basicDesign.setName("Basic");
            basicDesign.setRupees("60");
            basicDesign.setColor("#62DDAE");
            basicDesign.setDrawable(R.drawable.theme1);
            ArrayList<PlanFeatures> basicFeatures = new ArrayList<>();
            PlanFeatures basics = new PlanFeatures();
            basics.setFeature("Attendance Management");
            basicFeatures.add(basics);
            basics = new PlanFeatures();
            basics.setFeature("Leave Management");
            basicFeatures.add(basics);
            basics = new PlanFeatures();
            basics.setFeature("Salary Management");
            basicFeatures.add(basics);
            basicDesign.setFeatures(basicFeatures);

            planDesigns.add(basicDesign);

            PlanDesign advanceDesign = new PlanDesign();
            advanceDesign.setName("Advance");
            advanceDesign.setRupees("72");
            advanceDesign.setColor("#56A8FE");
            advanceDesign.setDrawable(R.drawable.theme2);
            ArrayList<PlanFeatures> advanceFeatures = new ArrayList<>();
            PlanFeatures advances = new PlanFeatures();
            advances.setFeature("Basic plan");
            advanceFeatures.add(advances);
            advances = new PlanFeatures();
            advances.setFeature("Live Tracking");
            advanceFeatures.add(advances);
            advances = new PlanFeatures();
            advances.setFeature("Task Management");
            advanceFeatures.add(advances);
            advances = new PlanFeatures();
            advances.setFeature("Expenses Management");
            advanceFeatures.add(advances);
            advanceDesign.setFeatures(advanceFeatures);
            planDesigns.add(advanceDesign);

            PlanDesignAdapter slider = new PlanDesignAdapter(PlanDesignActivity.this,planDesigns);
            slidePager.setAdapter(slider);
            slidePager.setClipToPadding(false);
            slidePager.setPadding(40,0,40,0);

            final Handler handler = new Handler();
            final Runnable Update = new Runnable() {
                public void run() {
                    if (currentPage == layouts.length && start == 0) {
                        currentPage = --currentPage;
                        start = 1;
                        end = 0;
                    }else if(currentPage < layouts.length && currentPage != 0 && end == 0&& start == 1){
                        currentPage = --currentPage;
                    }else if(currentPage == 0 && end == 0 && start == 1){
                        currentPage = 0;
                        end = 1;
                        start = 0;
                    }else if(currentPage <= layouts.length&& start == 0){

                        currentPage = ++currentPage;
                    }else if(currentPage == 0&& start == 0){

                        currentPage = ++currentPage;
                    }else{

                    }
                    slidePager.setCurrentItem(currentPage, true);
                }
            };

            timer = new Timer(); // This will create a new Thread
            timer .schedule(new TimerTask() { // task to be scheduled

                @Override
                public void run() {
                    handler.post(Update);
                }
            }, DELAY_MS, PERIOD_MS);
            dots = findViewById(R.id.dots_layout);

            createDot(0);

            slidePager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }
                @Override
                public void onPageSelected(int position) {
                    createDot(position);
                }
                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void createDot(int current){
        if(dots != null){
            dots.removeAllViews();
        }
        dot = new ImageView[layouts.length];
        for (int i =0;i<layouts.length;i++){
            dot[i] = new ImageView(this);
            if(i==current){
                dot[i].setImageDrawable(ContextCompat.getDrawable(this,R.drawable.active_dots));
            }else {
                dot[i].setImageDrawable(ContextCompat.getDrawable(this,R.drawable.inactive_dots));
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(4,0,4,0);
            dots.addView(dot[i],params);
        }
    }

}
