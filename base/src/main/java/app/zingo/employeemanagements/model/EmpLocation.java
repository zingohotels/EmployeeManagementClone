package app.zingo.employeemanagements.model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by ZingoHotels Tech on 09-01-2019.
 */

@IgnoreExtraProperties
public class EmpLocation {

    private String lastKnownLocation;
    private Long lastKnownLocationTime;
    private double latitude;
    private double longitude;

    public String getLastKnownLocation() {
        return this.lastKnownLocation;
    }

    public void setLastKnownLocation(String str) {
        this.lastKnownLocation = str;
    }

    public Long getLastKnownLocationTime() {
        return this.lastKnownLocationTime;
    }

    public void setLastKnownLocationTime(Long l) {
        this.lastKnownLocationTime = l;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public void setLatitude(double d) {
        this.latitude = d;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public void setLongitude(double d) {
        this.longitude = d;
    }
}
