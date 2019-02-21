package app.zingo.employeemanagements.UI.NewAdminDesigns;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import junit.framework.Assert;

import java.util.ArrayList;

import app.zingo.employeemanagements.Adapter.ExpenseReportAdapter;
import app.zingo.employeemanagements.Adapter.TaskListAdapter;
import app.zingo.employeemanagements.Model.Expenses;
import app.zingo.employeemanagements.Model.Tasks;
import app.zingo.employeemanagements.R;

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

            mTaskList = (RecyclerView) findViewById(R.id.expense_list_report);
            mTaskList.setLayoutManager(new LinearLayoutManager(this));


            if(employeeExpenses!=null&&employeeExpenses.size()!=0){

                mAdapter = new ExpenseReportAdapter(ReportExpenseList.this,employeeExpenses);
                mTaskList.setAdapter(mAdapter);

            }else{
                Toast.makeText(ReportExpenseList.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }


        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void setupToolbar() {
        this.mToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(this.mToolbar);
        Assert.assertNotNull(getSupportActionBar());
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
