package app.zingo.employeemanagements.Model;

import java.io.Serializable;
import java.util.ArrayList;

public class ObservableReportData implements Serializable {

    String empName;
    ArrayList<LoginDetails> loginDetailsArrayList;
    ArrayList<Meetings> meetingsArrayList;
    ArrayList<Tasks> tasksArrayList;
    ArrayList<LiveTracking> liveTrackingArrayList;
    ArrayList<Expenses> expensesArrayList;

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public ArrayList<LoginDetails> getLoginDetailsArrayList() {
        return loginDetailsArrayList;
    }

    public void setLoginDetailsArrayList(ArrayList<LoginDetails> loginDetailsArrayList) {
        this.loginDetailsArrayList = loginDetailsArrayList;
    }

    public ArrayList<Meetings> getMeetingsArrayList() {
        return meetingsArrayList;
    }

    public void setMeetingsArrayList(ArrayList<Meetings> meetingsArrayList) {
        this.meetingsArrayList = meetingsArrayList;
    }

    public ArrayList<Tasks> getTasksArrayList() {
        return tasksArrayList;
    }

    public void setTasksArrayList(ArrayList<Tasks> tasksArrayList) {
        this.tasksArrayList = tasksArrayList;
    }

    public ArrayList<LiveTracking> getLiveTrackingArrayList() {
        return liveTrackingArrayList;
    }

    public void setLiveTrackingArrayList(ArrayList<LiveTracking> liveTrackingArrayList) {
        this.liveTrackingArrayList = liveTrackingArrayList;
    }

    public ArrayList<Expenses> getExpensesArrayList() {
        return expensesArrayList;
    }

    public void setExpensesArrayList(ArrayList<Expenses> expensesArrayList) {
        this.expensesArrayList = expensesArrayList;
    }

    public ObservableReportData(ArrayList<LoginDetails> loginDetailsArrayList,  ArrayList<LiveTracking> liveTrackingArrayList, ArrayList<Meetings> meetingsArrayList,ArrayList<Tasks> tasksArrayList, ArrayList<Expenses> expensesArrayList) {
        this.loginDetailsArrayList = loginDetailsArrayList;
        this.meetingsArrayList = meetingsArrayList;
        this.tasksArrayList = tasksArrayList;
        this.liveTrackingArrayList = liveTrackingArrayList;
        this.expensesArrayList = expensesArrayList;
    }

    public ObservableReportData() {
    }
}
