package app.zingo.employeemanagements.ui.NewAdminDesigns;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

import app.zingo.employeemanagements.adapter.EmployeeAdapter;
import app.zingo.employeemanagements.model.Employee;
import app.zingo.employeemanagements.base.R;

public class PresentEmployeeListScreen extends AppCompatActivity {

    RecyclerView mProfileList;

    String type;
    ArrayList<Employee> presentEmployeeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        try{

            setContentView(R.layout.activity_present_employee_list_screen);

            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Employee Details");

            mProfileList = findViewById(R.id.profile_list);

            Bundle bundle = getIntent().getExtras();

            if(bundle!=null){
                type = bundle.getString("Type");
                presentEmployeeList = (ArrayList<Employee>)bundle.getSerializable("Employees");
            }

            if(presentEmployeeList!=null&&presentEmployeeList.size()!=0){

                Collections.sort(presentEmployeeList, Employee.compareEmployee);
                EmployeeAdapter adapter = new EmployeeAdapter( PresentEmployeeListScreen.this, presentEmployeeList,type);
                mProfileList.setAdapter(adapter);

            }else{
                Toast.makeText( PresentEmployeeListScreen.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }




        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id)
        {
            case android.R.id.home:

                PresentEmployeeListScreen.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
