package app.zingo.employeemanagements.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ActivityLogs implements Serializable {

    @SerializedName("ActivityLogId")
    public  int ActivityLogId;

    @SerializedName("ActivityName")
    public  String ActivityName;

    @SerializedName("ActivityDate")
    public  String ActivityDate;

    @SerializedName("ActivityTime")
    public  String ActivityTime;

    @SerializedName("AppVersion")
    public  String AppVersion;

    @SerializedName("Longitude")
    public  String Longitude;

    @SerializedName("Latitude")
    public  String Latitude;

    @SerializedName("EmployeeId")
    public  int EmployeeId;

    @SerializedName("employee")
    public  Employee employee;

    @SerializedName("ActivityDetails")
    public  String ActivityDetails;

    @SerializedName("ParticularId")
    public  int ParticularId;

    public int getActivityLogId() {
        return ActivityLogId;
    }

    public void setActivityLogId(int activityLogId) {
        ActivityLogId = activityLogId;
    }

    public String getActivityName() {
        return ActivityName;
    }

    public void setActivityName(String activityName) {
        ActivityName = activityName;
    }

    public String getActivityDate() {
        return ActivityDate;
    }

    public void setActivityDate(String activityDate) {
        ActivityDate = activityDate;
    }

    public String getActivityTime() {
        return ActivityTime;
    }

    public void setActivityTime(String activityTime) {
        ActivityTime = activityTime;
    }

    public String getAppVersion() {
        return AppVersion;
    }

    public void setAppVersion(String appVersion) {
        AppVersion = appVersion;
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

    public String getActivityDetails() {
        return ActivityDetails;
    }

    public void setActivityDetails(String activityDetails) {
        ActivityDetails = activityDetails;
    }

    public int getParticularId() {
        return ParticularId;
    }

    public void setParticularId(int particularId) {
        ParticularId = particularId;
    }
}
