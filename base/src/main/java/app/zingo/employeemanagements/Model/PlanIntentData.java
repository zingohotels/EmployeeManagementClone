package app.zingo.employeemanagements.Model;

import java.io.Serializable;

public class PlanIntentData implements Serializable {

    String passStartDate;
    String viewStartDate;
    String passEndDate;
    String viewEndDate;
    String planName;
    int planId;
    String planType;
    double amount;

    public String getPassStartDate() {
        return passStartDate;
    }

    public void setPassStartDate(String passStartDate) {
        this.passStartDate = passStartDate;
    }

    public String getViewStartDate() {
        return viewStartDate;
    }

    public void setViewStartDate(String viewStartDate) {
        this.viewStartDate = viewStartDate;
    }

    public String getPassEndDate() {
        return passEndDate;
    }

    public void setPassEndDate(String passEndDate) {
        this.passEndDate = passEndDate;
    }

    public String getViewEndDate() {
        return viewEndDate;
    }

    public void setViewEndDate(String viewEndDate) {
        this.viewEndDate = viewEndDate;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public int getPlanId() {
        return planId;
    }

    public void setPlanId(int planId) {
        this.planId = planId;
    }

    public String getPlanType() {
        return planType;
    }

    public void setPlanType(String planType) {
        this.planType = planType;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
