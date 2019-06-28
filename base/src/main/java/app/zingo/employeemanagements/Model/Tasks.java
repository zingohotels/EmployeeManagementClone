package app.zingo.employeemanagements.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by ZingoHotels Tech on 05-01-2019.
 */

public class Tasks implements Serializable {

    @SerializedName("TaskId")
    public int TaskId;

    @SerializedName("EmployeeId")
    public int EmployeeId;

    @SerializedName("employee")
    public Employee employee;

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

    @SerializedName("Category")
    public String Category;

    @SerializedName("ReminderDate")
    public String ReminderDate;

    @SerializedName("DepartmentId")
    public int DepartmentId;

    public int getTaskId() {
        return TaskId;
    }

    public void setTaskId(int taskId) {
        TaskId = taskId;
    }

    public int getEmployeeId() {
        return EmployeeId;
    }

    public void setEmployeeId(int employeeId) {
        EmployeeId = employeeId;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
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

    public String getReminderDate() {
        return ReminderDate;
    }

    public void setReminderDate(String reminderDate) {
        ReminderDate = reminderDate;
    }

    public int getDepartmentId() {
        return DepartmentId;
    }

    public void setDepartmentId(int departmentId) {
        DepartmentId = departmentId;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }
}
