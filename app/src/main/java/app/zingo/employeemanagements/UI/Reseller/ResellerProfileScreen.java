package app.zingo.employeemanagements.UI.Reseller;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import app.zingo.employeemanagements.Custom.MyRegulerText;
import app.zingo.employeemanagements.Custom.RoundImageView;
import app.zingo.employeemanagements.Model.Employee;
import app.zingo.employeemanagements.Model.ResellerProfiles;
import app.zingo.employeemanagements.R;
import app.zingo.employeemanagements.UI.Admin.EmployeesDashBoard;
import app.zingo.employeemanagements.UI.Admin.TaskManagementHost;
import app.zingo.employeemanagements.UI.Common.ChangePasswordScreen;
import app.zingo.employeemanagements.UI.Company.OrganizationDetailScree;
import app.zingo.employeemanagements.UI.Employee.EmployeeListScreen;
import app.zingo.employeemanagements.UI.Employee.LeaveManagementHost;
import app.zingo.employeemanagements.UI.LandingScreen;
import app.zingo.employeemanagements.UI.NewAdminDesigns.EmployeeUpdateListScreen;
import app.zingo.employeemanagements.UI.NewEmployeeDesign.ExpenseManageHost;
import app.zingo.employeemanagements.Utils.PreferenceHandler;

public class ResellerProfileScreen extends AppCompatActivity {

    LinearLayout logout,share,chngPwd;

    ResellerProfiles employeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
            setContentView(R.layout.activity_reseller_profile_screen);

            chngPwd = (LinearLayout) findViewById(R.id.change_password);

            share = (LinearLayout) findViewById(R.id.share_layout);
            logout = (LinearLayout) findViewById(R.id.logout);

            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openMenuViews(logout);
                }
            });

            chngPwd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openMenuViews(chngPwd);
                }
            });
            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String upToNCharacters = PreferenceHandler.getInstance(ResellerProfileScreen.this).getResellerName().substring(0, Math.min(PreferenceHandler.getInstance(ResellerProfileScreen.this).getResellerName().length(), 4));


                    String text = "Hello this is Zingy Reseller Employee Management App built for resellers to earn more money. You can resell the app and make more money for every new referral and earn commission for lifetime.\n\n"+
                            "Step to join the Zingy Reseller Referral Programme-\n" +
                            "1.  Signup using your phone number.\n" +
                            "\n" +
                            "2.  Open the Zingy Employee Management App and visit the profile Section, and find out your referral code. It’s an alpha-numeric code like: "+upToNCharacters+PreferenceHandler.getInstance(ResellerProfileScreen.this).getResellerUserId()+"\n" +
                            "\n" +
                            "3.  Share the App with your Referral Companies using your Referral Code\n" +
                            "\n" +
                            "4.  When  your referred company signs up. You can make money for every new signup and earn commission for lifetime.\n" +
                            "\n" +
                            " \n" +
                            "\n" +
                            "My Zingy Referral Code is "+upToNCharacters+PreferenceHandler.getInstance(ResellerProfileScreen.this).getResellerUserId()+". Don’t Forget to use my Referral Code.\n" +
                            "\n" +
                            "Keep Sharing\n" +
                            "\n" +
                            " \n" +
                            "\n" +
                            "To Download the app click here:\n"+
                            "https://play.google.com/store/apps/details?id=app.zingo.employeemanagements";


                    Intent emailIntent = new Intent(Intent.ACTION_SEND);
                    emailIntent.setType("text/plain");


                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Employee Management App Invitation");


                    emailIntent.putExtra(Intent.EXTRA_TEXT, text);
                    //emailIntent.putExtra(Intent.EXTRA_HTML_TEXT, body);
                    startActivity(Intent.createChooser(emailIntent, "Send"));

                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void openMenuViews(View view) {

        Intent intent;
        if (view.getId() == R.id.change_password) {
            Intent chnage = new Intent(ResellerProfileScreen.this, ChangePasswordReseller.class);
            startActivity(chnage);
        }else if (view.getId() == R.id.logout) {
            PreferenceHandler.getInstance(ResellerProfileScreen.this).clear();

            Intent log = new Intent(ResellerProfileScreen.this, LandingScreen.class);
            log.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            log.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            Toast.makeText(ResellerProfileScreen.this,"Logout",Toast.LENGTH_SHORT).show();
            startActivity(log);
            ResellerProfileScreen.this.finish();
        }
    }
}
