package app.zingo.employeemanagements.ui;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import app.zingo.employeemanagements.ui.landing.PhoneVerificationScreen;
import app.zingo.employeemanagements.ui.Reseller.ResellerSignUpScree;
import app.zingo.employeemanagements.utils.PreferenceHandler;
import app.zingo.employeemanagements.base.R;

public class SignUpOptioins extends AppCompatActivity {

    CardView mOrganization,mEmployee,mResellerSign;
    LinearLayout mJoinCompany,mJoinEmployee,mReseller;
    LinearLayout mWhatsapp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            setContentView(R.layout.activity_sign_up_optioins);

            mOrganization = findViewById(R.id.organization_signup_card);
            mEmployee = findViewById(R.id.employee_signup_card);
            mResellerSign = findViewById(R.id.reseller_signup_card);

            mJoinCompany = findViewById(R.id.join_company);
            mJoinEmployee = findViewById(R.id.join_employee);
            mReseller = findViewById(R.id.join_reseller);
            mWhatsapp = findViewById(R.id.whatsapp_open);


            mOrganization.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent started = new Intent( SignUpOptioins.this,GetStartedScreen.class);
                    PreferenceHandler.getInstance( SignUpOptioins.this).setSignUpType("Organization");
                    startActivity(started);

                }
            });

            mResellerSign.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent started = new Intent( SignUpOptioins.this, ResellerSignUpScree.class);
                    PreferenceHandler.getInstance( SignUpOptioins.this).setSignUpType("Reseller");
                    startActivity(started);

                }
            });

            mEmployee.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent started = new Intent( SignUpOptioins.this, PhoneVerificationScreen.class);
                    PreferenceHandler.getInstance( SignUpOptioins.this).setSignUpType("Employee");
                    started.putExtra("Screen","Employee");
                    startActivity(started);

                }
            });

            mWhatsapp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String digits = "\\d+";

                    try {

                        Uri uri = Uri.parse("whatsapp://send?phone=+919986128021" );
                        Intent i = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(i);
                    }
                    catch (ActivityNotFoundException e){
                        e.printStackTrace();
                        Toast.makeText( SignUpOptioins.this, "WhatsApp not installed.", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }catch(Exception e){
            e.printStackTrace();
        }

    }
}
