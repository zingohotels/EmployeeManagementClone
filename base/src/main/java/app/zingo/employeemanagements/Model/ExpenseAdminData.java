package app.zingo.employeemanagements.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ExpenseAdminData implements Serializable {

    @SerializedName("Employee")
    public Employee employee;

    @SerializedName("Expenses")
    Expenses Expenses;

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Expenses getExpenses() {
        return Expenses;
    }

    public void setExpenses(Expenses expenses) {
        Expenses = expenses;
    }
}
