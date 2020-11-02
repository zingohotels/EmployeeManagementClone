package app.zingo.employeemanagements.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TaskNotificationManagers implements Serializable {

    @SerializedName("TaskNotificationManagerId")
    public int TaskNotificationManagerId;

    @SerializedName("SenderId")
    public String SenderId;

    @SerializedName("ServerId")
    public String ServerId;

    @SerializedName("Title")
    public String Title;

    @SerializedName("Message")
    public String Message;

    @SerializedName("EmployeeId")
    public String EmployeeId;

    @SerializedName("TaskName")
    public String TaskName;

    @SerializedName("TaskDescription")
    public String TaskDescription;

    @SerializedName("DeadLine")
    public String DeadLine;

    @SerializedName("Comments")
    public String Comments;

    @SerializedName("Remarks")
    public String Remarks;

    @SerializedName("StartDate")
    public String StartDate;

    @SerializedName("EndDate")
    public String EndDate;

    @SerializedName("ToReportEmployeeId")
    public int ToReportEmployeeId;

    @SerializedName("Status")
    public String Status;

    @SerializedName("Longitude")
    public String Longitude;

    @SerializedName("Latitude")
    public String Latitude;

    @SerializedName("TaskId")
    public int TaskId;

    @SerializedName("DepartmentId")
    public int DepartmentId;

    @SerializedName("department")
    public Departments department;

    public int getTaskNotificationManagerId() {
        return TaskNotificationManagerId;
    }

    public void setTaskNotificationManagerId(int taskNotificationManagerId) {
        TaskNotificationManagerId = taskNotificationManagerId;
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

    public String getEmployeeId() {
        return EmployeeId;
    }

    public void setEmployeeId(String employeeId) {
        EmployeeId = employeeId;
    }

    public String getTaskName() {
        return TaskName;
    }

    public void setTaskName(String taskName) {
        TaskName = taskName;
    }

    public String getTaskDescription() {
        return TaskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        TaskDescription = taskDescription;
    }

    public String getDeadLine() {
        return DeadLine;
    }

    public void setDeadLine(String deadLine) {
        DeadLine = deadLine;
    }

    public String getComments() {
        return Comments;
    }

    public void setComments(String comments) {
        Comments = comments;
    }

    public String getRemarks() {
        return Remarks;
    }

    public void setRemarks(String remarks) {
        Remarks = remarks;
    }

    public String getStartDate() {
        return StartDate;
    }

    public void setStartDate(String startDate) {
        StartDate = startDate;
    }

    public String getEndDate() {
        return EndDate;
    }

    public void setEndDate(String endDate) {
        EndDate = endDate;
    }

    public int getToReportEmployeeId() {
        return ToReportEmployeeId;
    }

    public void setToReportEmployeeId(int toReportEmployeeId) {
        ToReportEmployeeId = toReportEmployeeId;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public int getTaskId() {
        return TaskId;
    }

    public void setTaskId(int taskId) {
        TaskId = taskId;
    }

    public int getDepartmentId() {
        return DepartmentId;
    }

    public void setDepartmentId(int departmentId) {
        DepartmentId = departmentId;
    }

    public Departments getDepartment() {
        return department;
    }

    public void setDepartment(Departments department) {
        this.department = department;
    }
}
