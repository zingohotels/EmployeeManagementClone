package app.zingo.employeemanagements.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by ZingoHotels Tech on 05-01-2019.
 */

public class SalaryDeductions implements Serializable {

    @SerializedName("SalaryDeductionId")
    public int SalaryDeductionId;

    @SerializedName("employee")
    public Employee employee;

    @SerializedName("EmployeeId")
    public int EmployeeId;

    @SerializedName("Amount")
    public double Amount;

    @SerializedName("DeductionName")
    public String DeductionName;

    @SerializedName("duration")
    public int duration;

    @SerializedName("ProvideDate")
    public String ProvideDate;

    @SerializedName("Statu")
    public String Status;

    @SerializedName("Interest")
    public double Interest;

    @SerializedName("Month")
    public String Month;

    public int getSalaryDeductionId() {
        return SalaryDeductionId;
    }

    public void setSalaryDeductionId(int salaryDeductionId) {
        SalaryDeductionId = salaryDeductionId;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public int getEmployeeId() {
        return EmployeeId;
    }

    public void setEmployeeId(int employeeId) {
        EmployeeId = employeeId;
    }

    public double getAmount() {
        return Amount;
    }

    public void setAmount(double amount) {
        Amount = amount;
    }

    public String getDeductionName() {
        return DeductionName;
    }

    public void setDeductionName(String deductionName) {
        DeductionName = deductionName;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getProvideDate() {
        return ProvideDate;
    }

    public void setProvideDate(String provideDate) {
        ProvideDate = provideDate;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public double getInterest() {
        return Interest;
    }

    public void setInterest(double interest) {
        Interest = interest;
    }

    public String getMonth() {
        return Month;
    }

    public void setMonth(String month) {
        Month = month;
    }
}
