package app.zingo.employeemanagements.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by ZingoHotels Tech on 05-01-2019.
 */

public class Notes implements Serializable {

    @SerializedName("NotesId")
    public int NotesId;

    @SerializedName("Title")
    public String Title;

    @SerializedName("Comments")
    public String Comments;

    @SerializedName("DateAndTime")
    public String DateAndTime;

    @SerializedName("Location")
    public String Location;

    @SerializedName("Longitude")
    public String Longitude;

    @SerializedName("Latitude")
    public String Latitude;

    @SerializedName("employeeId")
    public int employeeId;

    @SerializedName("ManagerId")
    public int ManagerId;

    public int getNotesId() {
        return NotesId;
    }

    public void setNotesId(int notesId) {
        NotesId = notesId;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getComments() {
        return Comments;
    }

    public void setComments(String comments) {
        Comments = comments;
    }

    public String getDateAndTime() {
        return DateAndTime;
    }

    public void setDateAndTime(String dateAndTime) {
        DateAndTime = dateAndTime;
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

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public int getManagerId() {
        return ManagerId;
    }

    public void setManagerId(int managerId) {
        ManagerId = managerId;
    }
}
