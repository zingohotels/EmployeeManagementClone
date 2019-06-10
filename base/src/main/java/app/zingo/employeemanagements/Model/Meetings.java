package app.zingo.employeemanagements.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Created by ZingoHotels Tech on 28-09-2018.
 */

public class Meetings implements Serializable {

    @SerializedName("MeetingsId")
    private int MeetingsId;

    @SerializedName("MeetingDetails")
    private String MeetingDetails;

    @SerializedName("Description")
    private String Description;

    @SerializedName("MeetingAgenda")
    private String MeetingAgenda;

    @SerializedName("Status")
    private String Status;

    @SerializedName("MeetingPersonDetails")
    private String MeetingPersonDetails;

    @SerializedName("MeetingDate")
    private String MeetingDate;

    @SerializedName("StartTime")
    private String StartTime;

    @SerializedName("EndTime")
    private String EndTime;

    @SerializedName("StartLatitude")
    private String StartLatitude;

    @SerializedName("StartLongitude")
    private String StartLongitude;

    @SerializedName("StartLocation")
    private String StartLocation;

    @SerializedName("StartPlaceID")
    private String StartPlaceID;

    @SerializedName("EndLatitude")
    private String EndLatitude;

    @SerializedName("EndLongitude")
    private String EndLongitude;

    @SerializedName("EndLocation")
    private String EndLocation;

    @SerializedName("EndPlaceID")
    private String EndPlaceID;

    @SerializedName("employees")
    private Employee employees;

    @SerializedName("EmployeeId")
    private int EmployeeId;

    @SerializedName("CustomerId")
    private int CustomerId;

    @SerializedName("IsPicture")
    private boolean IsPicture;

    public int getMeetingsId() {
        return MeetingsId;
    }

    public void setMeetingsId(int meetingsId) {
        MeetingsId = meetingsId;
    }

    public String getMeetingDetails() {
        return MeetingDetails;
    }

    public void setMeetingDetails(String meetingDetails) {
        MeetingDetails = meetingDetails;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getMeetingAgenda() {
        return MeetingAgenda;
    }

    public void setMeetingAgenda(String meetingAgenda) {
        MeetingAgenda = meetingAgenda;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getMeetingPersonDetails() {
        return MeetingPersonDetails;
    }

    public void setMeetingPersonDetails(String meetingPersonDetails) {
        MeetingPersonDetails = meetingPersonDetails;
    }

    public String getMeetingDate() {
        return MeetingDate;
    }

    public void setMeetingDate(String meetingDate) {
        MeetingDate = meetingDate;
    }

    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String startTime) {
        StartTime = startTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }

    public String getStartLatitude() {
        return StartLatitude;
    }

    public void setStartLatitude(String startLatitude) {
        StartLatitude = startLatitude;
    }

    public String getStartLongitude() {
        return StartLongitude;
    }

    public void setStartLongitude(String startLongitude) {
        StartLongitude = startLongitude;
    }

    public String getStartLocation() {
        return StartLocation;
    }

    public void setStartLocation(String startLocation) {
        StartLocation = startLocation;
    }

    public String getStartPlaceID() {
        return StartPlaceID;
    }

    public void setStartPlaceID(String startPlaceID) {
        StartPlaceID = startPlaceID;
    }

    public String getEndLatitude() {
        return EndLatitude;
    }

    public void setEndLatitude(String endLatitude) {
        EndLatitude = endLatitude;
    }

    public String getEndLongitude() {
        return EndLongitude;
    }

    public void setEndLongitude(String endLongitude) {
        EndLongitude = endLongitude;
    }

    public String getEndLocation() {
        return EndLocation;
    }

    public void setEndLocation(String endLocation) {
        EndLocation = endLocation;
    }

    public String getEndPlaceID() {
        return EndPlaceID;
    }

    public void setEndPlaceID(String endPlaceID) {
        EndPlaceID = endPlaceID;
    }

    public Employee getEmployees() {
        return employees;
    }

    public void setEmployees(Employee employees) {
        this.employees = employees;
    }

    public int getEmployeeId() {
        return EmployeeId;
    }

    public void setEmployeeId(int employeeId) {
        EmployeeId = employeeId;
    }

    public int getCustomerId() {
        return CustomerId;
    }

    public void setCustomerId(int customerId) {
        CustomerId = customerId;
    }

    public boolean isPicture() {
        return IsPicture;
    }

    public void setPicture(boolean picture) {
        IsPicture = picture;
    }

    public static Comparator compareMeetings = new Comparator() {
        @Override
        public int compare(Object o, Object t1) {
            Meetings profile = (Meetings) o;
            Meetings profile1 = (Meetings) t1;
            return profile.getMeetingDate().compareTo(profile1.getMeetingDate());
        }
    };
}
