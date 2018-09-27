package app.zingo.employeemanagements.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ZingoHotels Tech on 26-09-2018.
 */

public class Organization implements Serializable {

    @SerializedName("OrganizationId")
    private int OrganizationId;

    @SerializedName("OrganizationName")
    private String OrganizationName;

    @SerializedName("Address")
    private String Address;

    @SerializedName("City")
    private String City;

    @SerializedName("State")
    private String State;

    @SerializedName("Latitude")
    private String Latitude;

    @SerializedName("Longitude")
    private String Longitude;

    @SerializedName("Location")
    private String Location;

    @SerializedName("PlaceId")
    private String PlaceId;

    @SerializedName("Website")
    private String Website;

    @SerializedName("BuiltYear")
    private String BuiltYear;

    @SerializedName("AboutUs")
    private String AboutUs;

    @SerializedName("department")
    private ArrayList<Departments> department;

    public int getOrganizationId() {
        return OrganizationId;
    }

    public void setOrganizationId(int organizationId) {
        OrganizationId = organizationId;
    }

    public String getOrganizationName() {
        return OrganizationName;
    }

    public void setOrganizationName(String organizationName) {
        OrganizationName = organizationName;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
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

    public String getWebsite() {
        return Website;
    }

    public void setWebsite(String website) {
        Website = website;
    }

    public String getBuiltYear() {
        return BuiltYear;
    }

    public void setBuiltYear(String builtYear) {
        BuiltYear = builtYear;
    }

    public String getAboutUs() {
        return AboutUs;
    }

    public void setAboutUs(String aboutUs) {
        AboutUs = aboutUs;
    }

    public ArrayList<Departments> getDepartment() {
        return department;
    }

    public void setDepartment(ArrayList<Departments> department) {
        this.department = department;
    }
}
