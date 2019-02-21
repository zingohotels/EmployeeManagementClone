package app.zingo.employeemanagements.UI.Common;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import app.zingo.employeemanagements.R;

public class GeneralNotificationScreen extends AppCompatActivity {

    TextView mMessage,mTitle;

    String title,message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {


            setContentView(R.layout.activity_general_notification_screen);

            mTitle = (TextView) findViewById(R.id.app_title);
            mMessage = (TextView) findViewById(R.id.message);

            Bundle bundle = getIntent().getExtras();

            if (bundle != null) {

                title = bundle.getString("Title");
                message = bundle.getString("Message");


            }

            if(title!=null&&!title.isEmpty()){
                mTitle.setText(title+"");
            }

            if(message!=null&&!message.isEmpty()){
                mMessage.setText(message+"");
            }


        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
