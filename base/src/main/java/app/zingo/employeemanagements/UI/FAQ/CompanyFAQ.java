package app.zingo.employeemanagements.UI.FAQ;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.itextpdf.text.pdf.parser.Line;

import java.util.Timer;
import java.util.TimerTask;

import app.zingo.employeemanagements.UI.NewAdminDesigns.AdminHomeFragment;
import app.zingo.employeemanagements.base.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CompanyFAQ extends Fragment {

    LinearLayout add1,add2,add3,add4,add5,add6,add7,add8;
    LinearLayout aadd1,aadd2,aadd3,aadd4,aadd5,aadd6,aadd7,aadd8;
    ImageView iadd1,iadd2,iadd3,iadd4,iadd5,iadd6,iadd7,iadd8;

    ViewPager company_create_vp;

    LinearLayout dotsCompany;
    ImageView[] dot;
    int currentPage = 0,start = 0,end = 0;
    Timer timer;

    ViewPager dep_create_vp;

    LinearLayout dotsdep;
    ImageView[] dotdep;
    int currentPagedep = 0,startdep = 0,enddep = 0;
    Timer timerdep;

    ViewPager emp_create_vp;

    LinearLayout dotsemp;
    ImageView[] dotemp;
    int currentPageemp = 0,startemp = 0,endemp = 0;
    Timer timeremp;

    ViewPager emp_attendance_vp;

    LinearLayout dotsatndz;
    ImageView[] dotatndz;
    int currentPageatndz = 0,startatndz = 0,endatndz = 0;
    Timer timeratndz;


    ViewPager emp_location_vp;

    LinearLayout dotsloc;
    ImageView[] dotloc;
    int currentPageloc = 0,startloc = 0,endloc = 0;
    Timer timerloc;

    ViewPager emp_task_vp;

    LinearLayout dotstask;
    ImageView[] dottask;
    int currentPagetask = 0,starttask = 0,endtask = 0;
    Timer timertask;

    ViewPager emp_exp_vp;

    LinearLayout dotsexp;
    ImageView[] dotexp;
    int currentPageexp = 0,startexp = 0,endexp = 0;
    Timer timerexp;

    ViewPager emp_leave_vp;

    LinearLayout dotsleave;
    ImageView[] dotleave;
    int currentPageleave= 0,startleave = 0,endleave = 0;
    Timer timerleave;

    final long DELAY_MS = 2000;
    final long PERIOD_MS = 3000;


    FAQImageVp adapter;


    public CompanyFAQ() {
        // Required empty public constructor
    }

    public static CompanyFAQ getInstance() {
        return new CompanyFAQ();
    }

    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        try{

            View view =  inflater.inflate(R.layout.fragment_company_faq, container, false);

            add1 = view.findViewById(R.id.add_one);
            add2 = view.findViewById(R.id.add_two);
            add3 = view.findViewById(R.id.add_three);
            add4 = view.findViewById(R.id.add_four);
            add5 = view.findViewById(R.id.add_five);
            add6 = view.findViewById(R.id.add_six);
            add7 = view.findViewById(R.id.add_seven);
            add8 = view.findViewById(R.id.add_eight);

            aadd1 = view.findViewById(R.id.com_one);
            aadd2 = view.findViewById(R.id.com_two);
            aadd3 = view.findViewById(R.id.com_three);
            aadd4 = view.findViewById(R.id.com_four);
            aadd5 = view.findViewById(R.id.com_five);
            aadd6 = view.findViewById(R.id.com_six);
            aadd7 = view.findViewById(R.id.com_seven);
            aadd8 = view.findViewById(R.id.com_eight);

            iadd1 = view.findViewById(R.id.image_one_com);
            iadd2 = view.findViewById(R.id.image_two_com);
            iadd3 = view.findViewById(R.id.image_three_com);
            iadd4 = view.findViewById(R.id.image_four_com);
            iadd5 = view.findViewById(R.id.image_five_com);
            iadd6 = view.findViewById(R.id.image_six_com);
            iadd7 = view.findViewById(R.id.image_seven_com);
            iadd8 = view.findViewById(R.id.image_eight_com);

            company_create_vp = view.findViewById(R.id.company_create_vp);
            dotsCompany = view.findViewById(R.id.dots_layout_company_create);

            final int images[] = {R.drawable.get_started_faq, R.drawable.user_type_faq, R.drawable.plan_type_faq, R.drawable.org_trial_faq};

            adapter = new FAQImageVp(getActivity(), images);
            company_create_vp.setAdapter(adapter);

            final Handler handler = new Handler();
            final Runnable Update = new Runnable() {
                public void run() {
                    if (currentPage == images.length && start == 0) {
                        currentPage = --currentPage;
                        start = 1;
                        end = 0;
                    }else if(currentPage < images.length && currentPage != 0 && end == 0&& start == 1){
                        currentPage = --currentPage;
                    }else if(currentPage == 0 && end == 0 && start == 1){
                        currentPage = 0;
                        end = 1;
                        start = 0;
                    }else if(currentPage <= images.length&& start == 0){

                        currentPage = ++currentPage;
                    }else if(currentPage == 0&& start == 0){

                        currentPage = ++currentPage;
                    }else{

                    }
                    company_create_vp.setCurrentItem(currentPage, true);
                }
            };

            timer = new Timer(); // This will create a new Thread
            timer .schedule(new TimerTask() { // task to be scheduled

                @Override
                public void run() {
                    handler.post(Update);
                }
            }, DELAY_MS, PERIOD_MS);


            createDot(0,dotsCompany,images);

            company_create_vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {

                    createDot(position,dotsCompany,images);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });


            //dep create
            dep_create_vp = view.findViewById(R.id.dep_create_vp);
            dotsdep = view.findViewById(R.id.dots_layout_dep_create);

            final int imagesdep[] = {R.drawable.dash, R.drawable.home, R.drawable.dep_list, R.drawable.dep_create};

            adapter = new FAQImageVp(getActivity(), imagesdep);
            dep_create_vp.setAdapter(adapter);

            final Handler handlers = new Handler();
            final Runnable Updates = new Runnable() {
                public void run() {
                    if (currentPagedep == imagesdep.length && startdep == 0) {
                        currentPagedep = --currentPagedep;
                        startdep = 1;
                        enddep = 0;
                    }else if(currentPagedep < imagesdep.length && currentPagedep != 0 && enddep == 0&& startdep == 1){
                        currentPagedep = --currentPagedep;
                    }else if(currentPagedep == 0 && enddep == 0 && startdep == 1){
                        currentPagedep = 0;
                        enddep = 1;
                        startdep = 0;
                    }else if(currentPagedep <= imagesdep.length&& startdep == 0){

                        currentPagedep = ++currentPagedep;
                    }else if(currentPagedep == 0&& startdep == 0){

                        currentPagedep = ++currentPagedep;
                    }else{

                    }
                    dep_create_vp.setCurrentItem(currentPagedep, true);
                }
            };

            timerdep = new Timer(); // This will create a new Thread
            timerdep .schedule(new TimerTask() { // task to be scheduled

                @Override
                public void run() {
                    handlers.post(Updates);
                }
            }, DELAY_MS, PERIOD_MS);


            createDot(0,dotsdep,imagesdep);

            dep_create_vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {

                    createDot(position,dotsdep,imagesdep);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });


            //Employee cre
            emp_create_vp = view.findViewById(R.id.empl_create_vp);
            dotsemp = view.findViewById(R.id.dots_layout_empl_create);

            final int imagesempl[] = {R.drawable.dash, R.drawable.home, R.drawable.admin_empl_list, R.drawable.admin_create_emp,R.drawable.admin_create_emp_two};

            adapter = new FAQImageVp(getActivity(), imagesempl);
            emp_create_vp.setAdapter(adapter);

            final Handler handlersem = new Handler();
            final Runnable Updatesem = new Runnable() {
                public void run() {


                    if (currentPageemp == imagesempl.length && startemp == 0) {
                        currentPageemp = --currentPageemp;
                        startemp= 1;
                        endemp = 0;
                    }else if(currentPageemp < imagesempl.length && currentPageemp != 0 && endemp == 0&& startemp == 1){
                        currentPageemp = --currentPageemp;
                    }else if(currentPageemp == 0 && endemp == 0 && startemp == 1){
                        currentPageemp = 0;
                        endemp = 1;
                        startemp = 0;
                    }else if(currentPageemp <= imagesempl.length&& startemp == 0){

                        currentPageemp = ++currentPageemp;
                    }else if(currentPageemp == 0&& startemp == 0){

                        currentPageemp = ++currentPageemp;
                    }else{

                    }
                    emp_create_vp.setCurrentItem(currentPageemp, true);
                }
            };

            timeremp = new Timer(); // This will create a new Thread
            timeremp .schedule(new TimerTask() { // task to be scheduled

                @Override
                public void run() {
                    handlersem.post(Updatesem);
                }
            }, DELAY_MS, PERIOD_MS);


            createDot(0,dotsemp,imagesempl);

            emp_create_vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {

                    createDot(position,dotsemp,imagesempl);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });


            //Employee attendance
            emp_attendance_vp = view.findViewById(R.id.empl_atndz_vp);
            dotsatndz = view.findViewById(R.id.dots_layout_empl_atndz);

            final int imagesatndz[] = {R.drawable.dash, R.drawable.home, R.drawable.emp_list};

            adapter = new FAQImageVp(getActivity(), imagesatndz);
            emp_attendance_vp.setAdapter(adapter);

            final Handler handleratndz = new Handler();
            final Runnable Updateatndz = new Runnable() {
                public void run() {


                    if (currentPageatndz == imagesatndz.length && startatndz == 0) {
                        currentPageatndz = --currentPageatndz;
                        startatndz= 1;
                        endatndz = 0;
                    }else if(currentPageatndz < imagesatndz.length && currentPageatndz != 0 && endatndz == 0&& startatndz == 1){
                        currentPageatndz = --currentPageatndz;
                    }else if(currentPageatndz == 0 && endatndz == 0 && startatndz == 1){
                        currentPageatndz = 0;
                        endatndz = 1;
                        startatndz = 0;
                    }else if(currentPageatndz <= imagesatndz.length&& startatndz == 0){

                        currentPageatndz = ++currentPageatndz;
                    }else if(currentPageatndz == 0&& startatndz == 0){

                        currentPageatndz = ++currentPageatndz;
                    }else{

                    }
                    emp_attendance_vp.setCurrentItem(currentPageatndz, true);
                }
            };

            timeratndz = new Timer(); // This will create a new Thread
            timeratndz .schedule(new TimerTask() { // task to be scheduled

                @Override
                public void run() {
                    handleratndz.post(Updateatndz);
                }
            }, DELAY_MS, PERIOD_MS);


            createDot(0,dotsatndz,imagesatndz);

            emp_attendance_vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {

                    createDot(position,dotsatndz,imagesatndz);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });



            //Employee Location
            emp_location_vp = view.findViewById(R.id.empl_loc_vp);
            dotsloc = view.findViewById(R.id.dots_layout_empl_loc);

            final int imagesloc[] = {R.drawable.dash, R.drawable.home, R.drawable.emp_list, R.drawable.admin_loc_his};

            adapter = new FAQImageVp(getActivity(), imagesloc);
            emp_location_vp.setAdapter(adapter);

            final Handler handlerloc = new Handler();
            final Runnable Updateloc = new Runnable() {
                public void run() {


                    if (currentPageloc == imagesloc.length && startloc == 0) {
                        currentPageloc = --currentPageloc;
                        startloc= 1;
                        endloc = 0;
                    }else if(currentPageloc < imagesloc.length && currentPageloc != 0 && endloc == 0&& startloc == 1){
                        currentPageloc = --currentPageloc;
                    }else if(currentPageloc == 0 && endloc == 0 && startloc == 1){
                        currentPageloc = 0;
                        endloc = 1;
                        startloc = 0;
                    }else if(currentPageloc <= imagesloc.length&& startloc == 0){

                        currentPageloc = ++currentPageloc;
                    }else if(currentPageloc == 0&& startloc == 0){

                        currentPageloc = ++currentPageloc;
                    }else{

                    }
                    emp_location_vp.setCurrentItem(currentPageloc, true);
                }
            };

            timerloc = new Timer(); // This will create a new Thread
            timerloc .schedule(new TimerTask() { // task to be scheduled

                @Override
                public void run() {
                    handlerloc.post(Updateloc);
                }
            }, DELAY_MS, PERIOD_MS);


            createDot(0,dotsloc,imagesloc);

            emp_location_vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {

                    createDot(position,dotsloc,imagesloc);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });



            //Employee task
            emp_task_vp = view.findViewById(R.id.empl_task_vp);
            dotstask = view.findViewById(R.id.dots_layout_empl_task);

            final int imagestask[] = {R.drawable.dash, R.drawable.home, R.drawable.task_dash, R.drawable.create_task_admin};

            adapter = new FAQImageVp(getActivity(), imagestask);
            emp_task_vp.setAdapter(adapter);

            final Handler handlertask = new Handler();
            final Runnable Updatetask = new Runnable() {
                public void run() {


                    if (currentPagetask == imagestask.length && starttask == 0) {
                        currentPagetask = --currentPagetask;
                        starttask= 1;
                        endtask = 0;
                    }else if(currentPagetask < imagestask.length && currentPagetask != 0 && endtask == 0&& starttask == 1){
                        currentPagetask = --currentPagetask;
                    }else if(currentPagetask == 0 && endtask == 0 && starttask == 1){
                        currentPagetask = 0;
                        endtask = 1;
                        starttask = 0;
                    }else if(currentPagetask <= imagestask.length&& starttask == 0){

                        currentPagetask = ++currentPagetask;
                    }else if(currentPagetask == 0&& starttask == 0){

                        currentPagetask = ++currentPagetask;
                    }else{

                    }
                    emp_task_vp.setCurrentItem(currentPagetask, true);
                }
            };

            timertask = new Timer(); // This will create a new Thread
            timertask .schedule(new TimerTask() { // task to be scheduled

                @Override
                public void run() {
                    handlertask.post(Updatetask);
                }
            }, DELAY_MS, PERIOD_MS);


            createDot(0,dotstask,imagestask);

            emp_task_vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {

                    createDot(position,dotstask,imagestask);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });


            //Employee expe
            emp_exp_vp = view.findViewById(R.id.empl_exp_vp);
            dotsexp = view.findViewById(R.id.dots_layout_empl_exp);

            final int imagesexp[] = {R.drawable.dash, R.drawable.home, R.drawable.emp_list, R.drawable.expense_dash,R.drawable.update_expense,R.drawable.update_expense_two};

            adapter = new FAQImageVp(getActivity(), imagesexp);
            emp_exp_vp.setAdapter(adapter);

            final Handler handlerexp = new Handler();
            final Runnable Updateexp = new Runnable() {
                public void run() {


                    if (currentPageexp == imagesexp.length && startexp == 0) {
                        currentPageexp = --currentPageexp;
                        startexp= 1;
                        endexp = 0;
                    }else if(currentPageexp < imagesexp.length && currentPageexp != 0 && endexp == 0&& startexp == 1){
                        currentPageexp = --currentPageexp;
                    }else if(currentPageexp == 0 && endexp == 0 && startexp == 1){
                        currentPageexp = 0;
                        endexp = 1;
                        startexp = 0;
                    }else if(currentPageexp <= imagesexp.length&& startexp == 0){

                        currentPageexp = ++currentPageexp;
                    }else if(currentPageexp == 0&& startexp == 0){

                        currentPageexp = ++currentPageexp;
                    }else{

                    }
                    emp_exp_vp.setCurrentItem(currentPageexp, true);
                }
            };

            timerexp = new Timer(); // This will create a new Thread
            timerexp .schedule(new TimerTask() { // task to be scheduled

                @Override
                public void run() {
                    handlerexp.post(Updateexp);
                }
            }, DELAY_MS, PERIOD_MS);


            createDot(0,dotsexp,imagesexp);

            emp_exp_vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {

                    createDot(position,dotsexp,imagesexp);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });


            //Employee leave
            emp_leave_vp = view.findViewById(R.id.empl_lea_vp);
            dotsleave = view.findViewById(R.id.dots_layout_empl_lea);

            final int imagesleave[] = {R.drawable.dash, R.drawable.home, R.drawable.emp_list, R.drawable.leave_dashboard,R.drawable.update_leave};

            adapter = new FAQImageVp(getActivity(), imagesleave);
            emp_leave_vp.setAdapter(adapter);

            final Handler handlerleave = new Handler();
            final Runnable Updateleave = new Runnable() {
                public void run() {


                    if (currentPageleave == imagesleave.length && startleave == 0) {
                        currentPageleave = --currentPageleave;
                        startleave= 1;
                        endleave = 0;
                    }else if(currentPageleave < imagesleave.length && currentPageleave != 0 && endleave == 0&& startleave == 1){
                        currentPageleave = --currentPageleave;
                    }else if(currentPageleave == 0 && endleave == 0 && startleave == 1){
                        currentPageleave = 0;
                        endleave = 1;
                        startleave = 0;
                    }else if(currentPageleave <= imagesleave.length&& startleave == 0){

                        currentPageleave = ++currentPageleave;
                    }else if(currentPageleave == 0&& startleave == 0){

                        currentPageleave = ++currentPageleave;
                    }else{

                    }
                    emp_leave_vp.setCurrentItem(currentPageleave, true);
                }
            };

            timerleave = new Timer(); // This will create a new Thread
            timerleave .schedule(new TimerTask() { // task to be scheduled

                @Override
                public void run() {
                    handlerleave.post(Updateleave);
                }
            }, DELAY_MS, PERIOD_MS);


            createDot(0,dotsleave,imagesleave);

            emp_leave_vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {

                    createDot(position,dotsleave,imagesleave);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });



            add1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(aadd1.getVisibility()==View.VISIBLE){

                        aadd1.setVisibility(View.GONE);
                        iadd1.setImageResource(R.drawable.follow_add);

                    }else{
                        aadd1.setVisibility(View.VISIBLE);
                        iadd1.setImageResource(R.drawable.remove_icon);
                    }

                }
            });

            add2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(aadd2.getVisibility()==View.VISIBLE){

                        aadd2.setVisibility(View.GONE);
                        iadd2.setImageResource(R.drawable.follow_add);

                    }else{
                        aadd2.setVisibility(View.VISIBLE);
                        iadd2.setImageResource(R.drawable.remove_icon);
                    }

                }
            });

            add3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(aadd3.getVisibility()==View.VISIBLE){

                        aadd3.setVisibility(View.GONE);
                        iadd3.setImageResource(R.drawable.follow_add);

                    }else{
                        aadd3.setVisibility(View.VISIBLE);
                        iadd3.setImageResource(R.drawable.remove_icon);
                    }

                }
            });

            add4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(aadd4.getVisibility()==View.VISIBLE){

                        aadd4.setVisibility(View.GONE);
                        iadd4.setImageResource(R.drawable.follow_add);

                    }else{
                        aadd4.setVisibility(View.VISIBLE);
                        iadd4.setImageResource(R.drawable.remove_icon);
                    }

                }
            });

            add5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(aadd5.getVisibility()==View.VISIBLE){

                        aadd5.setVisibility(View.GONE);
                        iadd5.setImageResource(R.drawable.follow_add);

                    }else{
                        aadd5.setVisibility(View.VISIBLE);
                        iadd5.setImageResource(R.drawable.remove_icon);
                    }

                }
            });

            add6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(aadd6.getVisibility()==View.VISIBLE){

                        aadd6.setVisibility(View.GONE);
                        iadd6.setImageResource(R.drawable.follow_add);

                    }else{
                        aadd6.setVisibility(View.VISIBLE);
                        iadd6.setImageResource(R.drawable.remove_icon);
                    }

                }
            });

            add7.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(aadd7.getVisibility()==View.VISIBLE){

                        aadd7.setVisibility(View.GONE);
                        iadd7.setImageResource(R.drawable.follow_add);

                    }else{
                        aadd7.setVisibility(View.VISIBLE);
                        iadd7.setImageResource(R.drawable.remove_icon);
                    }

                }
            });

            add8.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(aadd8.getVisibility()==View.VISIBLE){

                        aadd8.setVisibility(View.GONE);
                        iadd8.setImageResource(R.drawable.follow_add);

                    }else{
                        aadd8.setVisibility(View.VISIBLE);
                        iadd8.setImageResource(R.drawable.remove_icon);
                    }

                }
            });

            return  view;

        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    private void createDot(int current, final LinearLayout dots,final int[] layouts){
        if(dots != null){
            dots.removeAllViews();
        }
        dot = new ImageView[layouts.length];
        for (int i =0;i<layouts.length;i++){
            dot[i] = new ImageView(getActivity());
            if(i==current){
                dot[i].setImageDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.active_dots));
            }else {
                dot[i].setImageDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.inactive_dots));
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(4,0,4,0);
            dots.addView(dot[i],params);
        }
    }

}
