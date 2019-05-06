package app.zingo.employeemanagements.Model;


import com.google.firebase.database.IgnoreExtraProperties;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;


public class WorkingDay implements Serializable {

    @SerializedName("OrganizationTimingId")
    public int OrganizationTimingId;

    @SerializedName("isSuday")
    public boolean isSuday;

    @SerializedName("isMonday")
    public boolean isMonday;

    @SerializedName("iSTuesday")
    public boolean iSTuesday;

    @SerializedName("isWednesday")
    public boolean isWednesday;

    @SerializedName("isThursday")
    public boolean isThursday;

    @SerializedName("isFriday")
    public boolean isFriday;

    @SerializedName("isSaturday")
    public boolean isSaturday;

    @SerializedName("SundayCheckInTime")
    public String SundayCheckInTime;

    @SerializedName("SundayCheckOutTime")
    public String SundayCheckOutTime;

    @SerializedName("MondayCheckInTime")
    public String MondayCheckInTime;

    @SerializedName("MondayCheckOutTime")
    public String MondayCheckOutTime;

    @SerializedName("TuesdayCheckInTime")
    public String TuesdayCheckInTime;

    @SerializedName("TuesdayCheckOutTime")
    public String TuesdayCheckOutTime;

    @SerializedName("WednesdayCheckInTime")
    public String WednesdayCheckInTime;

    @SerializedName("WednesdayCheckOutTime")
    public String WednesdayCheckOutTime;

    @SerializedName("ThursdayCheckInTime")
    public String ThursdayCheckInTime;

    @SerializedName("ThursdayCheckOutTime")
    public String ThursdayCheckOutTime;

    @SerializedName("FridayCheckInTime")
    public String FridayCheckInTime;

    @SerializedName("FridayCheckOutTime")
    public String FridayCheckOutTime;

    @SerializedName("SaturdayCheckInTime")
    public String SaturdayCheckInTime;

    @SerializedName("SaturdayCheckOutTime")
    public String SaturdayCheckOutTime;

    @SerializedName("organization")
    public Organization organization;

    @SerializedName("OrganizationId")
    public int OrganizationId;

    public int getOrganizationTimingId() {
        return OrganizationTimingId;
    }

    public void setOrganizationTimingId(int organizationTimingId) {
        OrganizationTimingId = organizationTimingId;
    }

    public boolean isSuday() {
        return isSuday;
    }

    public void setSuday(boolean suday) {
        isSuday = suday;
    }

    public boolean isMonday() {
        return isMonday;
    }

    public void setMonday(boolean monday) {
        isMonday = monday;
    }

    public boolean isiSTuesday() {
        return iSTuesday;
    }

    public void setiSTuesday(boolean iSTuesday) {
        this.iSTuesday = iSTuesday;
    }

    public boolean isWednesday() {
        return isWednesday;
    }

    public void setWednesday(boolean wednesday) {
        isWednesday = wednesday;
    }

    public boolean isThursday() {
        return isThursday;
    }

    public void setThursday(boolean thursday) {
        isThursday = thursday;
    }

    public boolean isFriday() {
        return isFriday;
    }

    public void setFriday(boolean friday) {
        isFriday = friday;
    }

    public boolean isSaturday() {
        return isSaturday;
    }

    public void setSaturday(boolean saturday) {
        isSaturday = saturday;
    }

    public String getSundayCheckInTime() {
        return SundayCheckInTime;
    }

    public void setSundayCheckInTime(String sundayCheckInTime) {
        SundayCheckInTime = sundayCheckInTime;
    }

    public String getSundayCheckOutTime() {
        return SundayCheckOutTime;
    }

    public void setSundayCheckOutTime(String sundayCheckOutTime) {
        SundayCheckOutTime = sundayCheckOutTime;
    }

    public String getMondayCheckInTime() {
        return MondayCheckInTime;
    }

    public void setMondayCheckInTime(String mondayCheckInTime) {
        MondayCheckInTime = mondayCheckInTime;
    }

    public String getMondayCheckOutTime() {
        return MondayCheckOutTime;
    }

    public void setMondayCheckOutTime(String mondayCheckOutTime) {
        MondayCheckOutTime = mondayCheckOutTime;
    }

    public String getTuesdayCheckInTime() {
        return TuesdayCheckInTime;
    }

    public void setTuesdayCheckInTime(String tuesdayCheckInTime) {
        TuesdayCheckInTime = tuesdayCheckInTime;
    }

    public String getTuesdayCheckOutTime() {
        return TuesdayCheckOutTime;
    }

    public void setTuesdayCheckOutTime(String tuesdayCheckOutTime) {
        TuesdayCheckOutTime = tuesdayCheckOutTime;
    }

    public String getWednesdayCheckInTime() {
        return WednesdayCheckInTime;
    }

    public void setWednesdayCheckInTime(String wednesdayCheckInTime) {
        WednesdayCheckInTime = wednesdayCheckInTime;
    }

    public String getWednesdayCheckOutTime() {
        return WednesdayCheckOutTime;
    }

    public void setWednesdayCheckOutTime(String wednesdayCheckOutTime) {
        WednesdayCheckOutTime = wednesdayCheckOutTime;
    }

    public String getThursdayCheckInTime() {
        return ThursdayCheckInTime;
    }

    public void setThursdayCheckInTime(String thursdayCheckInTime) {
        ThursdayCheckInTime = thursdayCheckInTime;
    }

    public String getThursdayCheckOutTime() {
        return ThursdayCheckOutTime;
    }

    public void setThursdayCheckOutTime(String thursdayCheckOutTime) {
        ThursdayCheckOutTime = thursdayCheckOutTime;
    }

    public String getFridayCheckInTime() {
        return FridayCheckInTime;
    }

    public void setFridayCheckInTime(String fridayCheckInTime) {
        FridayCheckInTime = fridayCheckInTime;
    }

    public String getFridayCheckOutTime() {
        return FridayCheckOutTime;
    }

    public void setFridayCheckOutTime(String fridayCheckOutTime) {
        FridayCheckOutTime = fridayCheckOutTime;
    }

    public String getSaturdayCheckInTime() {
        return SaturdayCheckInTime;
    }

    public void setSaturdayCheckInTime(String saturdayCheckInTime) {
        SaturdayCheckInTime = saturdayCheckInTime;
    }

    public String getSaturdayCheckOutTime() {
        return SaturdayCheckOutTime;
    }

    public void setSaturdayCheckOutTime(String saturdayCheckOutTime) {
        SaturdayCheckOutTime = saturdayCheckOutTime;
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
}
