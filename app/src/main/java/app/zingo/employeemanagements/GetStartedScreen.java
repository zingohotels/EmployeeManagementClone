package app.zingo.employeemanagements;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

public class GetStartedScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
            setContentView(R.layout.activity_get_started_screen);
            popupOne();


        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void popupOne(){

        try{
            AlertDialog.Builder builder = new AlertDialog.Builder(GetStartedScreen.this);
            LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View views = inflater.inflate(R.layout.info_get_started, null);

            builder.setView(views);
            final Button mNext = (Button) views.findViewById(R.id.retry_button);
            final AlertDialog dialogs = builder.create();
            dialogs.show();
            dialogs.setCanceledOnTouchOutside(false);



            mNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    dialogs.dismiss();
                }
            });







        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
