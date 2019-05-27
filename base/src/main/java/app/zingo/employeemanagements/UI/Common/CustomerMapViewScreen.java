package app.zingo.employeemanagements.UI.Common;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import app.zingo.employeemanagements.base.R;

public class CustomerMapViewScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
            setContentView(R.layout.activity_customer_map_view_screen);

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
