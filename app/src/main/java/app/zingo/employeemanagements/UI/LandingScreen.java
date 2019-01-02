package app.zingo.employeemanagements.UI;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import app.zingo.employeemanagements.Custom.MyEditText;
import app.zingo.employeemanagements.Custom.MyRegulerText;
import app.zingo.employeemanagements.R;

public class LandingScreen extends AppCompatActivity {

    TextView mSignIn,mSupport;
    MyEditText mEmail,mPassword;
    MyRegulerText mSignInButton,mGetStarted,mContactUs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{

            setContentView(R.layout.activity_landing_screen);

            mSupport = (TextView)findViewById(R.id.landing_support);
            mEmail = (MyEditText)findViewById(R.id.landing_email);
            mPassword = (MyEditText)findViewById(R.id.landing_password);
            mSignInButton = (MyRegulerText)findViewById(R.id.buttonsignin);
            mGetStarted = (MyRegulerText)findViewById(R.id.button_get_started);
            mContactUs = (MyRegulerText)findViewById(R.id.button_contact_us);



        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
