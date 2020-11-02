package app.zingo.employeemanagements.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class OrganizationBreakTimes implements Serializable {

    @SerializedName("OrganizationBreakTimeId")
    public int OrganizationBreakTimeId;

    @SerializedName("organization")
    public Organization organization;

    @SerializedName("OrganizationId")
    public int OrganizationId;

    @SerializedName("BreakName")
    public String BreakName;

    @SerializedName("BreakStartTime")
    public String BreakStartTime;

    @SerializedName("BreakEndTime")
    public String BreakEndTime;

    public int getOrganizationBreakTimeId() {
        return OrganizationBreakTimeId;
    }

    public void setOrganizationBreakTimeId(int organizationBreakTimeId) {
        OrganizationBreakTimeId = organizationBreakTimeId;
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

    public String getBreakName() {
        return BreakName;
    }

    public void setBreakName(String breakName) {
        BreakName = breakName;
    }

    public String getBreakStartTime() {
        return BreakStartTime;
    }

    public void setBreakStartTime(String breakStartTime) {
        BreakStartTime = breakStartTime;
    }

    public String getBreakEndTime() {
        return BreakEndTime;
    }

    public void setBreakEndTime(String breakEndTime) {
        BreakEndTime = breakEndTime;
    }
}
