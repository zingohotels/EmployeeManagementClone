package app.zingo.employeemanagements.ui.Common;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

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
