package app.zingo.employeemanagements.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Created by ZingoHotels Tech on 02-01-2019.
 */

public class LiveTracking implements Serializable {

    @SerializedName("LiveTrackingDetailsId")
    public int LiveTrackingDetailsId;

    @SerializedName("employee")
    public Employee employee;


    @SerializedName("EmployeeId")
    public int EmployeeId;

    @SerializedName("Longitude")
    public String Longitude;

    @SerializedName("Latitude")
    public String Latitude;

    @SerializedName("TrackingDate")
    public String TrackingDate;

    public int getLiveTrackingDetailsId() {
        return LiveTrackingDetailsId;
    }

    public void setLiveTrackingDetailsId(int liveTrackingDetailsId) {
        LiveTrackingDetailsId = liveTrackingDetailsId;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public int getEmployeeId() {
        return EmployeeId;
    }

    public void setEmployeeId(int employeeId) {
        EmployeeId = employeeId;
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

    public String getTrackingDate() {
        return TrackingDate;
    }

    public void setTrackingDate(String trackingDate) {
        TrackingDate = trackingDate;
    }

    public static Comparator compareLiveTrack = new Comparator() {
        @Override
        public int compare(Object o, Object t1) {
            LiveTracking profile = (LiveTracking) o;
            LiveTracking profile1 = (LiveTracking) t1;
            return profile.getTrackingDate().compareTo(profile1.getTrackingDate());
        }
    };
}
