package app.zingo.employeemanagements.ui.NewAdminDesigns;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import app.zingo.employeemanagements.adapter.ExpenseReportAdapter;
import app.zingo.employeemanagements.model.Expenses;
import app.zingo.employeemanagements.base.R;

public class ReportExpenseList extends AppCompatActivity {

    View layout;
    private ExpenseReportAdapter mAdapter;
    RecyclerView mTaskList;

    Toolbar mToolbar;




    ArrayList<Expenses> employeeExpenses;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{

            setContentView(R.layout.activity_report_expense_list);

            setupToolbar();
            Bundle bundle = getIntent().getExtras();
            if (bundle!=null) {
                employeeExpenses = (ArrayList<Expenses>)bundle.getSerializable("EmployeeExpense");
            }

            mTaskList = findViewById(R.id.expense_list_report);
            mTaskList.setLayoutManager(new LinearLayoutManager(this));


            if(employeeExpenses!=null&&employeeExpenses.size()!=0){

                mAdapter = new ExpenseReportAdapter ( ReportExpenseList.this,employeeExpenses);
                mTaskList.setAdapter(mAdapter);

            }else{
                Toast.makeText( ReportExpenseList.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }


        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void setupToolbar() {
        this.mToolbar = findViewById(R.id.app_bar);
        setSupportActionBar(this.mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Expenses");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id)
        {
            case android.R.id.home:

                ReportExpenseList.this.finish();

        }
        return super.onOptionsItemSelected(item);
    }
}
