package app.zingo.employeemanagements.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class EmployeeQrCode  implements Serializable {

    @SerializedName("OrganizationId")
    int OrganizationId;

    @SerializedName("EmployeeId")
    int EmployeeId;

    @SerializedName("TodayDate")
    String TodayDate;

    @SerializedName("TodayTime")
    String TodayTime;

    @SerializedName("Longitute")
    String Longitute;

    @SerializedName("Latitude")
    String Latitude;

    @SerializedName("Type")
    String Type;

    @SerializedName("FullName")
    String FullName;

    @SerializedName("Meeting")
    boolean Meeting;


    @SerializedName("LoginId")
    int LoginId;

    @SerializedName("IMEI")
    String IMEI;

    public String getIMEI() {
        return IMEI;
    }

    public void setIMEI(String IMEI) {
        this.IMEI = IMEI;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public int getOrganizationId() {
        return OrganizationId;
    }

    public void setOrganizationId(int organizationId) {
        OrganizationId = organizationId;
    }

    public int getEmployeeId() {
        return EmployeeId;
    }

    public void setEmployeeId(int employeeId) {
        EmployeeId = employeeId;
    }

    public String getTodayDate() {
        return TodayDate;
    }

    public void setTodayDate(String todayDate) {
        TodayDate = todayDate;
    }


    public String getTodayTime() {
        return TodayTime;
    }

    public void setTodayTime(String todayTime) {
        TodayTime = todayTime;
    }

    public String getLongitute() {
        return Longitute;
    }

    public void setLongitute(String longitute) {
        Longitute = longitute;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public int getLoginId() {
        return LoginId;
    }

    public void setLoginId(int loginId) {
        LoginId = loginId;
    }

    public boolean isMeeting() {
        return Meeting;
    }

    public void setMeeting(boolean meeting) {
        Meeting = meeting;
    }
}
