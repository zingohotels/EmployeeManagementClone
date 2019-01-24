package app.zingo.employeemanagements.UI.NewEmployeeDesign;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import app.zingo.employeemanagements.R;

public class ExpensesListScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{

            setContentView(R.layout.activity_expenses_list_screen);

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
