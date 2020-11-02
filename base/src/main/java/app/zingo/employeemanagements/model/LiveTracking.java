package app.zingo.employeemanagements.model;

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

    @SerializedName("OrganizationId")
    public int OrganizationId;

    @SerializedName("Longitude")
    public String Longitude;

    @SerializedName("Latitude")
    public String Latitude;

    @SerializedName("TrackingDate")
    public String TrackingDate;

    @SerializedName("AppVersion")
    public String AppVersion;

    @SerializedName("TrackingTime")
    public String TrackingTime;

    @SerializedName("DeviceName")
    public String DeviceName;

    @SerializedName("InternetOn")
    public String InternetOn;

    @SerializedName("MobileOSVersion")
    public String MobileOSVersion;

    @SerializedName("BatteryPercentage")
    public String BatteryPercentage;

    @SerializedName("GPSStatus")
    public String GPSStatus;

    @SerializedName("MockLocation")
    public String MockLocation;

    String previousTime;
    String km;
    String duration;
    String lastTime;


    public String getDeviceName() {
        return DeviceName;
    }

    public void setDeviceName(String deviceName) {
        DeviceName = deviceName;
    }

    public String getMobileOSVersion ( ) {
        return MobileOSVersion;
    }
    public void setMobileOSVersion ( String mobileOSVersion ) {
        MobileOSVersion = mobileOSVersion;
    }
    public String getInternetOn ( ) {
        return InternetOn;
    }
    public void setInternetOn ( String internetOn ) {
        InternetOn = internetOn;
    }
    public String getMockLocation ( ) {
        return MockLocation;
    }
    public void setMockLocation ( String mockLocation ) {
        MockLocation = mockLocation;
    }
    public String getBatteryPercentage() {
        return BatteryPercentage;
    }

    public void setBatteryPercentage(String batteryPercentage) {
        BatteryPercentage = batteryPercentage;
    }

    public static Comparator getCompareLiveTrack() {
        return compareLiveTrack;
    }

    public static void setCompareLiveTrack(Comparator compareLiveTrack) {
        LiveTracking.compareLiveTrack = compareLiveTrack;
    }

    public String getAppVersion() {
        return AppVersion;
    }

    public void setAppVersion(String appVersion) {
        AppVersion = appVersion;
    }

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

    public String getTrackingTime() {
        return TrackingTime;
    }

    public void setTrackingTime(String trackingTime) {
        TrackingTime = trackingTime;
    }

    public String getPreviousTime() {
        return previousTime;
    }

    public void setPreviousTime(String previousTime) {
        this.previousTime = previousTime;
    }

    public String getKm() {
        return km;
    }

    public void setKm(String km) {
        this.km = km;
    }

    public String getDuration() {
        return duration;
    }

    public int getOrganizationId() {
        return OrganizationId;
    }

    public void setOrganizationId(int organizationId) {
        OrganizationId = organizationId;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getGPSStatus() {
        return GPSStatus;
    }

    public void setGPSStatus(String GPSStatus) {
        this.GPSStatus = GPSStatus;
    }

    public String getLastTime ( ) {
        return lastTime;
    }

    public void setLastTime ( String lastTime ) {
        this.lastTime = lastTime;
    }

    public static Comparator compareLiveTrack = new Comparator() {
        @Override
        public int compare(Object o, Object t1) {
            LiveTracking profile = ( LiveTracking ) o;
            LiveTracking profile1 = ( LiveTracking ) t1;
            return profile.getTrackingTime().compareTo(profile1.getTrackingTime());
        }
    };
}
