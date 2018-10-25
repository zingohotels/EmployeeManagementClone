package app.zingo.employeemanagements.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Created by ZingoHotels Tech on 28-09-2018.
 */

public class LoginDetails implements Serializable {

    @SerializedName("LoginDetailsId")
    private int LoginDetailsId;

    @SerializedName("Latitude")
    private String Latitude;

    @SerializedName("Longitude")
    private String Longitude;

    @SerializedName("Location")
    private String Location;

    @SerializedName("PlaceId")
    private String PlaceId;

    @SerializedName("LoginDate")
    private String LoginDate;

    @SerializedName("LoginTime")
    private String LoginTime;

    @SerializedName("LogOutTime")
    private String LogOutTime;

    @SerializedName("employees")
    private Employee employees;

    @SerializedName("EmployeeId")
    private int EmployeeId;


    public int getLoginDetailsId() {
        return LoginDetailsId;
    }

    public void setLoginDetailsId(int loginDetails) {
        LoginDetailsId = loginDetails;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getPlaceId() {
        return PlaceId;
    }

    public void setPlaceId(String placeId) {
        PlaceId = placeId;
    }

    public String getLoginDate() {
        return LoginDate;
    }

    public void setLoginDate(String loginDate) {
        LoginDate = loginDate;
    }

    public String getLoginTime() {
        return LoginTime;
    }

    public void setLoginTime(String loginTime) {
        LoginTime = loginTime;
    }

    public String getLogOutTime() {
        return LogOutTime;
    }

    public void setLogOutTime(String logOutTime) {
        LogOutTime = logOutTime;
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

    public static Comparator compareLogin = new Comparator() {
        @Override
        public int compare(Object o, Object t1) {
            LoginDetails profile = (LoginDetails) o;
            LoginDetails profile1 = (LoginDetails) t1;
            return profile.getLoginDate().compareTo(profile1.getLoginDate());
        }
    };
}
