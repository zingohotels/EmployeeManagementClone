package app.zingo.employeemanagements.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by ZingoHotels Tech on 17-10-2018.
 */

public class LoginDetailsNotificationManagers  implements Serializable {

    @SerializedName("NotificationManagerId")
    public int NotificationManagerId;

    @SerializedName("SenderId")
    public String SenderId;

    @SerializedName("ServerId")
    public String ServerId;

    @SerializedName("Title")
    public String Title;

    @SerializedName("Message")
    public String Message;

    @SerializedName("Location")
    public String Location;

    @SerializedName("Longitude")
    public String Longitude;

    @SerializedName("Latitude")
    public String Latitude;

    @SerializedName("LoginDate")
    public String LoginDate;

    @SerializedName("Status")
    public String Status;

    @SerializedName("EmployeeId")
    public int EmployeeId;

    @SerializedName("ManagerId")
    public int ManagerId;

    @SerializedName("LoginDetailsId")
    public int LoginDetailsId;

    public int getNotificationManagerId() {
        return NotificationManagerId;
    }

    public void setNotificationManagerId(int notificationManagerId) {
        NotificationManagerId = notificationManagerId;
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

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
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

    public String getLoginDate() {
        return LoginDate;
    }

    public void setLoginDate(String loginDate) {
        LoginDate = loginDate;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
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

    public int getLoginDetailsId() {
        return LoginDetailsId;
    }

    public void setLoginDetailsId(int loginDetailsId) {
        LoginDetailsId = loginDetailsId;
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
