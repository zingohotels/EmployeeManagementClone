package app.zingo.employeemanagements.UI;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;

import app.zingo.employeemanagements.Custom.MyRegulerText;
import app.zingo.employeemanagements.R;
import app.zingo.employeemanagements.UI.Admin.DashBoardAdmin;
import app.zingo.employeemanagements.UI.Landing.PhoneVerificationScreen;
import app.zingo.employeemanagements.UI.Reseller.ResellerSignUpScree;
import app.zingo.employeemanagements.Utils.PreferenceHandler;

public class SignUpOptioins extends AppCompatActivity {

    CardView mOrganization,mEmployee;
    MyRegulerText mJoinCompany,mJoinEmployee,mReseller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            setContentView(R.layout.activity_sign_up_optioins);

            mOrganization = (CardView)findViewById(R.id.organization_signup_card);
            mEmployee = (CardView)findViewById(R.id.employee_signup_card);

            mJoinCompany = (MyRegulerText)findViewById(R.id.join_company);
            mJoinEmployee = (MyRegulerText)findViewById(R.id.join_employee);
            mReseller = (MyRegulerText)findViewById(R.id.join_reseller);


            mJoinCompany.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent started = new Intent(SignUpOptioins.this,GetStartedScreen.class);
                    PreferenceHandler.getInstance(SignUpOptioins.this).setSignUpType("Organization");
                    startActivity(started);

                }
            });

            mReseller.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent started = new Intent(SignUpOptioins.this,ResellerSignUpScree.class);
                    PreferenceHandler.getInstance(SignUpOptioins.this).setSignUpType("Reseller");
                    startActivity(started);

                }
            });

            mJoinEmployee.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent started = new Intent(SignUpOptioins.this,PhoneVerificationScreen.class);
                    PreferenceHandler.getInstance(SignUpOptioins.this).setSignUpType("Employee");
                    started.putExtra("Screen","Employee");
                    startActivity(started);

                }
            });
        }catch(Exception e){
            e.printStackTrace();
        }

    }
}
