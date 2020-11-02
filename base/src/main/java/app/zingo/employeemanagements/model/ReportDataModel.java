package app.zingo.employeemanagements.model;

import java.io.Serializable;
import java.util.ArrayList;

public class ReportDataModel implements Serializable {

    String preEmply;
    String totalEmp;
    String complTask;
    String totalTas;
    String visit;
    String expenses;
    ArrayList<ReportDataEmployee> reportDataEmployees;
    String date;
    String kmt;
    String mailID;

    public String getMailID() {
        return mailID;
    }

    public void setMailID(String mailID) {
        this.mailID = mailID;
    }

    public String getPreEmply() {
        return preEmply;
    }

    public void setPreEmply(String preEmply) {
        this.preEmply = preEmply;
    }

    public String getTotalEmp() {
        return totalEmp;
    }

    public void setTotalEmp(String totalEmp) {
        this.totalEmp = totalEmp;
    }

    public String getComplTask() {
        return complTask;
    }

    public void setComplTask(String complTask) {
        this.complTask = complTask;
    }

    public String getTotalTas() {
        return totalTas;
    }

    public void setTotalTas(String totalTas) {
        this.totalTas = totalTas;
    }

    public String getVisit() {
        return visit;
    }

    public void setVisit(String visit) {
        this.visit = visit;
    }

    public String getExpenses() {
        return expenses;
    }

    public void setExpenses(String expenses) {
        this.expenses = expenses;
    }

    public ArrayList<ReportDataEmployee> getReportDataEmployees() {
        return reportDataEmployees;
    }

    public void setReportDataEmployees(ArrayList<ReportDataEmployee> reportDataEmployees) {
        this.reportDataEmployees = reportDataEmployees;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getKmt() {
        return kmt;
    }

    public void setKmt(String kmt) {
        this.kmt = kmt;
    }
}
