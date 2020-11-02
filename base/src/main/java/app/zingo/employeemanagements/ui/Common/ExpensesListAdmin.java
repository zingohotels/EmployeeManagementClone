package app.zingo.employeemanagements.ui.Common;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MenuItem;

import java.util.ArrayList;

import app.zingo.employeemanagements.adapter.ExpenseListDataAdapter;
import app.zingo.employeemanagements.model.ExpenseAdminData;
import app.zingo.employeemanagements.base.R;

public class ExpensesListAdmin extends AppCompatActivity {

    RecyclerView mExpList;

    ArrayList< ExpenseAdminData > expenses;
    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
            setContentView(R.layout.activity_expenses_list_admin2);

            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            // title = "Tasks";
            //setTitle("Pending Tasks");

            mExpList = findViewById(R.id.expenselist_dash);

            Bundle bundle = getIntent().getExtras();

            if(bundle!=null){

                expenses = (ArrayList< ExpenseAdminData >)bundle.getSerializable("Expenses");
                title =bundle.getString("Title");
            }
            if(title!=null&&!title.isEmpty()){
                setTitle(title);
            }else{
                setTitle("Expenses");
            }
            if(expenses!=null&&expenses.size()!=0){

                ExpenseListDataAdapter mAdapter = new ExpenseListDataAdapter ( ExpensesListAdmin.this,expenses);
                mExpList.setAdapter(mAdapter);


            }

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

                ExpensesListAdmin.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
