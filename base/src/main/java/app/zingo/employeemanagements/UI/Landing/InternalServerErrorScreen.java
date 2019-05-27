package app.zingo.employeemanagements.UI.Landing;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import app.zingo.employeemanagements.base.R;

public class InternalServerErrorScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{


            setContentView(R.layout.activity_internal_server_error_screen);

        }catch (Exception w){
            w.printStackTrace();
        }

    }
}
