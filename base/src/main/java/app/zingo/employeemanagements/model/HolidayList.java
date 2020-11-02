package app.zingo.employeemanagements.model;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class HolidayList implements Serializable {


    @SerializedName("OrganizationHolidayListId")
    public  int OrganizationHolidayListId;

    @SerializedName("HolidayDate")
    public  String HolidayDate;

    @SerializedName("HolidayDescription")
    public  String HolidayDescription;

    @SerializedName("HolidayDay")
    public  String HolidayDay;

    @SerializedName("organization")
    public Organization organization;

    @SerializedName("OrganizationId")
    public  int OrganizationId;

    public int getOrganizationHolidayListId() {
        return OrganizationHolidayListId;
    }

    public void setOrganizationHolidayListId(int organizationHolidayListId) {
        OrganizationHolidayListId = organizationHolidayListId;
    }

    public String getHolidayDate() {
        return HolidayDate;
    }

    public void setHolidayDate(String holidayDate) {
        HolidayDate = holidayDate;
    }

    public String getHolidayDescription() {
        return HolidayDescription;
    }

    public void setHolidayDescription(String holidayDescription) {
        HolidayDescription = holidayDescription;
    }

    public String getHolidayDay() {
        return HolidayDay;
    }

    public void setHolidayDay(String holidayDay) {
        HolidayDay = holidayDay;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization( Organization organization) {
        this.organization = organization;
    }

    public int getOrganizationId() {
        return OrganizationId;
    }

    public void setOrganizationId(int organizationId) {
        OrganizationId = organizationId;
    }
}
