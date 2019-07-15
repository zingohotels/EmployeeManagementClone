package app.zingo.employeemanagements.UI.Common;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import app.zingo.employeemanagements.Custom.MyRegulerText;
import app.zingo.employeemanagements.base.R;

public class FakeActivityScreen extends AppCompatActivity {

    TextView mTitle;

    String message = "Your employee is doing fake activity.";
    int managerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_fake_screen);

            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Fake Activity Alert");
            mTitle = findViewById(R.id.title_message);

            Bundle bundle = getIntent().getExtras();

            if(bundle!=null){

                message = bundle.getString("Message");


            }

            mTitle.setText(""+message);


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id)
        {
            case android.R.id.home:

                FakeActivityScreen.this.finish();
                break;


        }
        return super.onOptionsItemSelected(item);
    }
}
