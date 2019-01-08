package app.zingo.employeemanagements.UI;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;

import app.zingo.employeemanagements.R;

public class SignUpOptioins extends AppCompatActivity {

    CardView mOrganization,mEmployee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            setContentView(R.layout.activity_sign_up_optioins);

            mOrganization = (CardView)findViewById(R.id.organization_signup_card);
            mEmployee = (CardView)findViewById(R.id.employee_signup_card);


            mOrganization.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent started = new Intent(SignUpOptioins.this,GetStartedScreen.class);
                    startActivity(started);

                }
            });

            mEmployee.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent started = new Intent(SignUpOptioins.this,GetStartedScreen.class);
                    startActivity(started);

                }
            });
        }catch(Exception e){
            e.printStackTrace();
        }

    }
}
