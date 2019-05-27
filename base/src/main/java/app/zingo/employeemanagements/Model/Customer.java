package app.zingo.employeemanagements.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Customer implements Serializable {

    @SerializedName("CustomerId")
    public  int CustomerId;

    @SerializedName("CustomerName")
    public  String CustomerName;

    @SerializedName("CustomerMobile")
    public  String CustomerMobile;

    @SerializedName("CustomerEmail")
    public  String CustomerEmail;

    @SerializedName("organization")
    public Organization organization;

    @SerializedName("OrganizationId")
    public  int OrganizationId;

    @SerializedName("CustomerAddress")
    public  String CustomerAddress;

    @SerializedName("Latitude")
    public  String Latitude;

    @SerializedName("Longitude")
    public  String Longitude;

    @SerializedName("SecondryEmailAddress")
    public  String SecondryEmailAddress;

    @SerializedName("SecondaryPhoneNumber")
    public  String SecondaryPhoneNumber;

    @SerializedName("CustomerImage")
    public  String CustomerImage;

    public String getCustomerImage() {
        return CustomerImage;
    }

    public void setCustomerImage(String customerImage) {
        CustomerImage = customerImage;
    }

    public int getCustomerId() {
        return CustomerId;
    }

    public void setCustomerId(int customerId) {
        CustomerId = customerId;
    }

    public String getCustomerName() {
        return CustomerName;
    }

    public void setCustomerName(String customerName) {
        CustomerName = customerName;
    }

    public String getCustomerMobile() {
        return CustomerMobile;
    }

    public void setCustomerMobile(String customerMobile) {
        CustomerMobile = customerMobile;
    }

    public String getCustomerEmail() {
        return CustomerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        CustomerEmail = customerEmail;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public int getOrganizationId() {
        return OrganizationId;
    }

    public void setOrganizationId(int organizationId) {
        OrganizationId = organizationId;
    }

    public String getCustomerAddress() {
        return CustomerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        CustomerAddress = customerAddress;
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

    public String getSecondryEmailAddress() {
        return SecondryEmailAddress;
    }

    public void setSecondryEmailAddress(String secondryEmailAddress) {
        SecondryEmailAddress = secondryEmailAddress;
    }

    public String getSecondaryPhoneNumber() {
        return SecondaryPhoneNumber;
    }

    public void setSecondaryPhoneNumber(String secondaryPhoneNumber) {
        SecondaryPhoneNumber = secondaryPhoneNumber;
    }
}
