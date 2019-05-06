package app.zingo.employeemanagements.UI;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import app.zingo.employeemanagements.base.R;

public class FAQScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{

            setContentView(R.layout.activity_faqscreen);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Join Us");



        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
