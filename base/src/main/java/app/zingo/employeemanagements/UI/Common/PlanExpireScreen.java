package app.zingo.employeemanagements.UI.Common;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import app.zingo.employeemanagements.base.R;

public class PlanExpireScreen extends AppCompatActivity {

    TextView mErrorMsg;

    String screen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{

            setContentView(R.layout.activity_plan_expire_screen);

            mErrorMsg = findViewById(R.id.expDetailMsg);

            Bundle bundle = getIntent().getExtras();

            if(bundle!=null){

                screen = bundle.getString("Screen");
            }

            if(screen!=null&&screen.equalsIgnoreCase("Admin")){


            }else{
                mErrorMsg.setText("Access Denied.Please contact your admin/Manager");
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
