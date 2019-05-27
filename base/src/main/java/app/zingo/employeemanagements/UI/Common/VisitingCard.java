package app.zingo.employeemanagements.UI.Common;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import app.zingo.employeemanagements.base.R;

public class VisitingCard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{

            setContentView(R.layout.activity_visiting_card);

        }catch (Exception e){
            e.printStackTrace();

        }

    }
}
