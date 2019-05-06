package app.zingo.employeemanagements.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by ZingoHotels Tech on 03-01-2019.
 */

public class EmailData implements Serializable {

    @SerializedName("EmailAddress")
    public String EmailAddress;

    @SerializedName("Body")
    public String Body;

    @SerializedName("Subject")
    public String Subject;

    @SerializedName("UserName")
    public String UserName;

    @SerializedName("Password")
    public String Password;

    @SerializedName("FromName")
    public String FromName;

    @SerializedName("FromEmail")
    public String FromEmail;


    public String getEmailAddress() {
        return EmailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        EmailAddress = emailAddress;
    }

    public String getBody() {
        return Body;
    }

    public void setBody(String body) {
        Body = body;
    }

    public String getSubject() {
        return Subject;
    }

    public void setSubject(String subject) {
        Subject = subject;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getFromName() {
        return FromName;
    }

    public void setFromName(String fromName) {
        FromName = fromName;
    }

    public String getFromEmail() {
        return FromEmail;
    }

    public void setFromEmail(String fromEmail) {
        FromEmail = fromEmail;
    }
}
