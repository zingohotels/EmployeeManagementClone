package app.zingo.employeemanagements.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by ZingoHotels Tech on 26-10-2018.
 */

public class LeaveNotificationManagers implements Serializable {

    @SerializedName("LeaveNotificationManagerId")
    public int LeaveNotificationManagerId;

    @SerializedName("SenderId")
    public String SenderId;

    @SerializedName("ServerId")
    public String ServerId;

    @SerializedName("Title")
    public String Title;

    @SerializedName("Message")
    public String Message;

    @SerializedName("FromDate")
    public String FromDate;

    @SerializedName("ToDate")
    public String ToDate;

    @SerializedName("Reason")
    public String Reason;

    @SerializedName("EmployeeId")
    public int EmployeeId;

    @SerializedName("ManagerId")
    public int ManagerId;

    @SerializedName("EmployeeName")
    public String EmployeeName;

    @SerializedName("Status")
    public String Status;

    @SerializedName("Comments")
    public String Comments;

    @SerializedName("LeaveId")
    public String LeaveId;


    public int getLeaveNotificationManagerId() {
        return LeaveNotificationManagerId;
    }

    public void setLeaveNotificationManagerId(int leaveNotificationManagerId) {
        LeaveNotificationManagerId = leaveNotificationManagerId;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
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

    public String getReason() {
        return Reason;
    }

    public void setReason(String reason) {
        Reason = reason;
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

    public String getEmployeeName() {
        return EmployeeName;
    }

    public void setEmployeeName(String employeeName) {
        EmployeeName = employeeName;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getComments() {
        return Comments;
    }

    public void setComments(String comments) {
        Comments = comments;
    }

    public String getLeaveId() {
        return LeaveId;
    }

    public void setLeaveId(String leaveId) {
        LeaveId = leaveId;
    }

    public String getSenderId() {
        return SenderId;
    }

    public void setSenderId(String senderId) {
        SenderId = senderId;
    }

    public String getServerId() {
        return ServerId;
    }

    public void setServerId(String serverId) {
        ServerId = serverId;
    }
}
