package app.zingo.employeemanagements.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class ResellerProfiles implements Serializable {

    @SerializedName("ResellerProfileId")
    public int ResellerProfileId;

    @SerializedName("FullName")
    public String FullName;

    @SerializedName("Email")
    public String Email;

    @SerializedName("Password")
    public String Password;

    @SerializedName("UserName")
    public String UserName;

    @SerializedName("SignUpDate")
    public String SignUpDate;

    @SerializedName("ProfilePhoto")
    public String ProfilePhoto;

    @SerializedName("MobileNumber")
    public String MobileNumber;

    @SerializedName("Status")
    public String Status;

    @SerializedName("CommissionPercentage")
    public double CommissionPercentage;

    @SerializedName("UserRoleId")
    public int UserRoleId;

    @SerializedName("UserRole")
    public UserRoles UserRole;

    @SerializedName("Gender")
    public String Gender;

    @SerializedName("ReferalCodeForOrganization")
    public String ReferalCodeForOrganization;

    @SerializedName("organzations")
    public ArrayList<Organization> organzations;

    @SerializedName("Country")
    public String Country;

    @SerializedName("City")
    public String City;


    public int getResellerProfileId() {
        return ResellerProfileId;
    }

    public void setResellerProfileId(int resellerProfileId) {
        ResellerProfileId = resellerProfileId;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getSignUpDate() {
        return SignUpDate;
    }

    public void setSignUpDate(String signUpDate) {
        SignUpDate = signUpDate;
    }

    public String getProfilePhoto() {
        return ProfilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        ProfilePhoto = profilePhoto;
    }

    public String getMobileNumber() {
        return MobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        MobileNumber = mobileNumber;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public double getCommissionPercentage() {
        return CommissionPercentage;
    }

    public void setCommissionPercentage(double commissionPercentage) {
        CommissionPercentage = commissionPercentage;
    }

    public int getUserRoleId() {
        return UserRoleId;
    }

    public void setUserRoleId(int userRoleId) {
        UserRoleId = userRoleId;
    }

    public UserRoles getUserRole() {
        return UserRole;
    }

    public void setUserRole(UserRoles userRole) {
        UserRole = userRole;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public String getReferalCodeForOrganization() {
        return ReferalCodeForOrganization;
    }

    public void setReferalCodeForOrganization(String referalCodeForOrganization) {
        ReferalCodeForOrganization = referalCodeForOrganization;
    }

    public ArrayList<Organization> getOrganzations() {
        return organzations;
    }

    public void setOrganzations(ArrayList<Organization> organzations) {
        this.organzations = organzations;
    }

    public String getCountry() {
        return Country;
    }

    public void setCountry(String country) {
        Country = country;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }
}
