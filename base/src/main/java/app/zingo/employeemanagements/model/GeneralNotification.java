package app.zingo.employeemanagements.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GeneralNotification implements Serializable {

    @SerializedName("GeneralNotificationManagerId")
    public int GeneralNotificationManagerId;

    @SerializedName("SenderId")
    public String SenderId;

    @SerializedName("ServerId")
    public String ServerId;

    @SerializedName("Title")
    public String Title;

    @SerializedName("Message")
    public String Message;

    @SerializedName("Comments")
    public String Comments;

    @SerializedName("Reason")
    public String Reason;

    @SerializedName("EmployeeId")
    public int EmployeeId;

    @SerializedName("OrganizationId")
    public int OrganizationId;

    public int getGeneralNotificationManagerId() {
        return GeneralNotificationManagerId;
    }

    public void setGeneralNotificationManagerId(int generalNotificationManagerId) {
        GeneralNotificationManagerId = generalNotificationManagerId;
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

    public String getComments() {
        return Comments;
    }

    public void setComments(String comments) {
        Comments = comments;
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

    public int getOrganizationId ( ) {
        return OrganizationId;
    }

    public void setOrganizationId ( int organizationId ) {
        OrganizationId = organizationId;
    }
}
