package app.zingo.employeemanagements.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by ZingoHotels Tech on 17-10-2018.
 */

public class Leaves implements Serializable {

    @SerializedName("LeaveId")
    public int LeaveId;

    @SerializedName("LeaveType")
    public String LeaveType;

    @SerializedName("LeaveComment")
    public String LeaveComment;

    @SerializedName("Status")
    public String Status;

    @SerializedName("ApproverComment")
    public String ApproverComment;

    @SerializedName("FromDate")
    public String FromDate;

    @SerializedName("ToDate")
    public String ToDate;

    @SerializedName("NoOfDays")
    public int NoOfDays;

    @SerializedName("ApprovedBy")
    public String ApprovedBy;

    @SerializedName("ApprovedDate")
    public String ApprovedDate;

    @SerializedName("employees")
    public Employee employees;

    @SerializedName("EmployeeId")
    public int EmployeeId;


    public int getLeaveId() {
        return LeaveId;
    }

    public void setLeaveId(int leaveId) {
        LeaveId = leaveId;
    }

    public String getLeaveType() {
        return LeaveType;
    }

    public void setLeaveType(String leaveType) {
        LeaveType = leaveType;
    }

    public String getLeaveComment() {
        return LeaveComment;
    }

    public void setLeaveComment(String leaveComment) {
        LeaveComment = leaveComment;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getApproverComment() {
        return ApproverComment;
    }

    public void setApproverComment(String approverComment) {
        ApproverComment = approverComment;
    }

    public String getFromDate() {
        return FromDate;
    }

    public void setFromDate(String fromDate) {
        FromDate = fromDate;
    }

    public String getToDate() {
        return ToDate;
    }

    public void setToDate(String toDate) {
        ToDate = toDate;
    }

    public int getNoOfDays() {
        return NoOfDays;
    }

    public void setNoOfDays(int noOfDays) {
        NoOfDays = noOfDays;
    }

    public String getApprovedBy() {
        return ApprovedBy;
    }

    public void setApprovedBy(String approvedBy) {
        ApprovedBy = approvedBy;
    }

    public String getApprovedDate() {
        return ApprovedDate;
    }

    public void setApprovedDate(String approvedDate) {
        ApprovedDate = approvedDate;
    }

    public Employee getEmployees() {
        return employees;
    }

    public void setEmployees(Employee employees) {
        this.employees = employees;
    }

    public int getEmployeeId() {
        return EmployeeId;
    }

    public void setEmployeeId(int employeeId) {
        EmployeeId = employeeId;
    }
}
