package app.zingo.employeemanagements.ui;

import android.os.Bundle;
import android.os.Handler;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.Timer;
import java.util.TimerTask;

import app.zingo.employeemanagements.adapter.SplashSlider;
import app.zingo.employeemanagements.base.R;

public class SupportScreen extends AppCompatActivity {
    ViewPager slidePager;
    int[] layouts = {R.layout.slide_one_screen, R.layout.slide_two_screen};
    LinearLayout dots;
    ImageView[] dot;
    int currentPage = 0,start = 0,end = 0;
    Timer timer;
    final long DELAY_MS = 2000;
    final long PERIOD_MS = 7000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
            setContentView(R.layout.activity_support_screen);

            slidePager = findViewById(R.id.pager_image_support);

            SplashSlider slider = new SplashSlider(SupportScreen.this,layouts);
            slidePager.setAdapter(slider);

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
                dot[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.active_dots));
            }else {
                dot[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.inactive_dots));
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(4,0,4,0);
            dots.addView(dot[i],params);
        }
    }
}
