package app.zingo.employeemanagements.model;

import java.io.Serializable;
import java.util.Comparator;

public class ReportDataEmployee implements Serializable {


    String name;
    String date;
    String loginTime;
    String logoutTime;
    String hours;
    String BreakHours;
    String visits;
    String tasks;
    String expenses;
    String expensesAmt;
    String kms;

    public String getBreakHours() {
        return BreakHours;
    }

    public void setBreakHours(String breakHours) {
        BreakHours = breakHours;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(String loginTime) {
        this.loginTime = loginTime;
    }

    public String getLogoutTime() {
        return logoutTime;
    }

    public void setLogoutTime(String logoutTime) {
        this.logoutTime = logoutTime;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public String getVisits() {
        return visits;
    }

    public void setVisits(String visits) {
        this.visits = visits;
    }

    public String getTasks() {
        return tasks;
    }

    public void setTasks(String tasks) {
        this.tasks = tasks;
    }

    public String getExpenses() {
        return expenses;
    }

    public void setExpenses(String expenses) {
        this.expenses = expenses;
    }

    public String getExpensesAmt() {
        return expensesAmt;
    }

    public void setExpensesAmt(String expensesAmt) {
        this.expensesAmt = expensesAmt;
    }

    public String getKms() {
        return kms;
    }

    public void setKms(String kms) {
        this.kms = kms;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public static Comparator compareList = new Comparator() {
        @Override
        public int compare(Object o, Object t1) {
            ReportDataEmployee reportDataEmployee = ( ReportDataEmployee ) o;
            ReportDataEmployee reportDataEmployee1 = ( ReportDataEmployee ) t1;

            return reportDataEmployee.getDate().compareToIgnoreCase(reportDataEmployee1.getDate());
            //return profile.getHotelId().eq(profile1.getHotelId());
        }
    };
}
