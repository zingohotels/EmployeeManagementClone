package app.zingo.employeemanagements.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by ZingoHotels Tech on 05-01-2019.
 */

public class Expenses implements Serializable {

    @SerializedName("ExpenseId")
    public int ExpenseId;

    @SerializedName("ExpenseTitle")
    public String ExpenseTitle;

    @SerializedName("Amount")
    public double Amount;

    @SerializedName("ClaimedAmount")
    public double ClaimedAmount;

    @SerializedName("DisbursedAmount")
    public double DisbursedAmount;

    @SerializedName("date")
    public String date;

    @SerializedName("Description")
    public String Description;

    @SerializedName("employee")
    public Employee employee;

    @SerializedName("EmployeeId")
    public int EmployeeId;

    @SerializedName("ManagerId")
    public int ManagerId;

    @SerializedName("ImageUrl")
    public String ImageUrl;

    @SerializedName("Status")
    public String Status;

    @SerializedName("StatusRemarks")
    public String StatusRemarks;

    @SerializedName("Location")
    public String Location;

    @SerializedName("Longititude")
    public String Longititude;

    @SerializedName("latitude")
    public String latitude;

    @SerializedName("organization")
    public Organization organization;

    @SerializedName("OrganizationId")
    public int OrganizationId;

    @SerializedName("ExpenseCategoryId")
    public int ExpenseCategoryId;

    public int getExpenseId() {
        return ExpenseId;
    }

    public void setExpenseId(int expenseId) {
        ExpenseId = expenseId;
    }

    public String getExpenseTitle() {
        return ExpenseTitle;
    }

    public void setExpenseTitle(String expenseTitle) {
        ExpenseTitle = expenseTitle;
    }

    public double getAmount() {
        return Amount;
    }

    public void setAmount(double amount) {
        Amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
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

    public int getManagerId() {
        return ManagerId;
    }

    public void setManagerId(int managerId) {
        ManagerId = managerId;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getLongititude() {
        return Longititude;
    }

    public void setLongititude(String longititude) {
        Longititude = longititude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization( Organization organization) {
        this.organization = organization;
    }

    public int getOrganizationId() {
        return OrganizationId;
    }

    public void setOrganizationId(int organizationId) {
        OrganizationId = organizationId;
    }

    public double getClaimedAmount() {
        return ClaimedAmount;
    }

    public void setClaimedAmount(double claimedAmount) {
        ClaimedAmount = claimedAmount;
    }

    public double getDisbursedAmount() {
        return DisbursedAmount;
    }

    public void setDisbursedAmount(double disbursedAmount) {
        DisbursedAmount = disbursedAmount;
    }

    public String getStatusRemarks() {
        return StatusRemarks;
    }

    public void setStatusRemarks(String statusRemarks) {
        StatusRemarks = statusRemarks;
    }

    public int getExpenseCategoryId() {
        return ExpenseCategoryId;
    }

    public void setExpenseCategoryId(int expenseCategoryId) {
        ExpenseCategoryId = expenseCategoryId;
    }
}
