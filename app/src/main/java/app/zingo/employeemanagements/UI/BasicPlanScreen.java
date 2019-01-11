package app.zingo.employeemanagements.UI;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import app.zingo.employeemanagements.R;

public class BasicPlanScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{

            setContentView(R.layout.activity_basic_plan_screen);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
