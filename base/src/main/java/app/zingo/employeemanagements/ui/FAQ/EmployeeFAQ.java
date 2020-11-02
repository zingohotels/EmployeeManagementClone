package app.zingo.employeemanagements.ui.FAQ;


import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import app.zingo.employeemanagements.base.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class EmployeeFAQ extends Fragment {

    LinearLayout add1,add2,add3,add4,add5,add6,add7,add8;
    LinearLayout aadd1,aadd2,aadd3,aadd4,aadd5,aadd6,aadd7,aadd8;
    ImageView iadd1,iadd2,iadd3,iadd4,iadd5,iadd6,iadd7,iadd8;


    public EmployeeFAQ() {
        // Required empty public constructor
    }

    public static EmployeeFAQ getInstance() {
        return new EmployeeFAQ ();
    }

    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        try{

            View view =  inflater.inflate(R.layout.fragment_employee_faq, container, false);

            add1 = view.findViewById(R.id.add_one);

            add4 = view.findViewById(R.id.add_four);
            add5 = view.findViewById(R.id.add_five);
            add6 = view.findViewById(R.id.add_six);
            add7 = view.findViewById(R.id.add_seven);
            add8 = view.findViewById(R.id.add_eight);

            aadd1 = view.findViewById(R.id.com_one);

            aadd4 = view.findViewById(R.id.com_four);
            aadd5 = view.findViewById(R.id.com_five);
            aadd6 = view.findViewById(R.id.com_six);
            aadd7 = view.findViewById(R.id.com_seven);
            aadd8 = view.findViewById(R.id.com_eight);

            iadd1 = view.findViewById(R.id.image_one_com);

            iadd4 = view.findViewById(R.id.image_four_com);
            iadd5 = view.findViewById(R.id.image_five_com);
            iadd6 = view.findViewById(R.id.image_six_com);
            iadd7 = view.findViewById(R.id.image_seven_com);
            iadd8 = view.findViewById(R.id.image_eight_com);


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

}
