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

    @SerializedName("Images")
    private ArrayList<OrganizationImage> Images;

    @SerializedName("department")
    private ArrayList<Departments> department;

    @SerializedName("AppType")
    private String AppType;

    @SerializedName("LicenseStartDate")
    private String LicenseStartDate;

    @SerializedName("LicenseEndDate")
    private String LicenseEndDate;

    @SerializedName("PlanType")
    private String PlanType;

    @SerializedName("SignupDate")
    private String SignupDate;

    @SerializedName("EmployeeLimit")
    private int EmployeeLimit;

    @SerializedName("PlanId")
    private int PlanId;

    @SerializedName("plans")
    private Plans plans;

    @SerializedName("ReferralCodeOfReseller")
    private String ReferralCodeOfReseller;

    @SerializedName("TestColumn")
    private String TestColumn;

    @SerializedName("resellerProfile")
    private ResellerProfiles resellerProfile;

    @SerializedName("ResellerProfileId")
    private int ResellerProfileId;

    @SerializedName("IsWorking")
    private boolean IsWorking;

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

    public ArrayList<OrganizationImage> getImages() {
        return Images;
    }

    public void setImages(ArrayList<OrganizationImage> images) {
        Images = images;
    }

    public String getAppType() {
        return AppType;
    }

    public void setAppType(String appType) {
        AppType = appType;
    }

    public String getLicenseStartDate() {
        return LicenseStartDate;
    }

    public void setLicenseStartDate(String licenseStartDate) {
        LicenseStartDate = licenseStartDate;
    }

    public String getLicenseEndDate() {
        return LicenseEndDate;
    }

    public void setLicenseEndDate(String licenseEndDate) {
        LicenseEndDate = licenseEndDate;
    }

    public String getPlanType() {
        return PlanType;
    }

    public void setPlanType(String planType) {
        PlanType = planType;
    }

    public String getSignupDate() {
        return SignupDate;
    }

    public void setSignupDate(String signupDate) {
        SignupDate = signupDate;
    }

    public int getEmployeeLimit() {
        return EmployeeLimit;
    }

    public void setEmployeeLimit(int employeeLimit) {
        EmployeeLimit = employeeLimit;
    }

    public int getPlanId() {
        return PlanId;
    }

    public void setPlanId(int planId) {
        PlanId = planId;
    }

    public Plans getPlans() {
        return plans;
    }

    public void setPlans(Plans plans) {
        this.plans = plans;
    }


    public String getReferralCodeOfReseller() {
        return ReferralCodeOfReseller;
    }

    public void setReferralCodeOfReseller(String referralCodeOfReseller) {
        ReferralCodeOfReseller = referralCodeOfReseller;
    }

    public ResellerProfiles getResellerProfile() {
        return resellerProfile;
    }

    public void setResellerProfile(ResellerProfiles resellerProfile) {
        this.resellerProfile = resellerProfile;
    }

    public int getResellerProfileId() {
        return ResellerProfileId;
    }

    public void setResellerProfileId(int resellerProfileId) {
        ResellerProfileId = resellerProfileId;
    }

    public boolean isWorking() {
        return IsWorking;
    }

    public void setWorking(boolean working) {
        IsWorking = working;
    }

    public String getTestColumn() {
        return TestColumn;
    }

    public void setTestColumn(String testColumn) {
        TestColumn = testColumn;
    }
}
