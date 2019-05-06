package app.zingo.employeemanagements.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class OrgDashBoard implements Serializable {

    @SerializedName("todayMeetingCount")
    public int todayMeetingCount;

    @SerializedName("todayAvgMeetingTime")
    public int todayAvgMeetingTime;

    @SerializedName("TodayMeetingHours")
    public int TodayMeetingHours;

    @SerializedName("TotalDepartment")
    public int TotalDepartment;

    @SerializedName("TotalEmployee")
    public int TotalEmployee;

    public int getTodayMeetingCount() {
        return todayMeetingCount;
    }

    public void setTodayMeetingCount(int todayMeetingCount) {
        this.todayMeetingCount = todayMeetingCount;
    }

    public int getTodayAvgMeetingTime() {
        return todayAvgMeetingTime;
    }

    public void setTodayAvgMeetingTime(int todayAvgMeetingTime) {
        this.todayAvgMeetingTime = todayAvgMeetingTime;
    }

    public int getTodayMeetingHours() {
        return TodayMeetingHours;
    }

    public void setTodayMeetingHours(int todayMeetingHours) {
        TodayMeetingHours = todayMeetingHours;
    }

    public int getTotalDepartment() {
        return TotalDepartment;
    }

    public void setTotalDepartment(int totalDepartment) {
        TotalDepartment = totalDepartment;
    }

    public int getTotalEmployee() {
        return TotalEmployee;
    }

    public void setTotalEmployee(int totalEmployee) {
        TotalEmployee = totalEmployee;
    }
}
