package app.zingo.employeemanagements.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by ZingoHotels Tech on 26-09-2018.
 */

public class Departments implements Serializable {

    @SerializedName("DepartmentId")
    private int DepartmentId;

    @SerializedName("DepartmentName")
    private String DepartmentName;

    @SerializedName("DepartmentDescription")
    private String DepartmentDescription;

    @SerializedName("organization")
    private Organization organization;

    @SerializedName("OrganizationId")
    private int OrganizationId;

    public int getDepartmentId() {
        return DepartmentId;
    }

    public void setDepartmentId(int departmentId) {
        DepartmentId = departmentId;
    }

    public String getDepartmentName() {
        return DepartmentName;
    }

    public void setDepartmentName(String departmentName) {
        DepartmentName = departmentName;
    }

    public String getDepartmentDescription() {
        return DepartmentDescription;
    }

    public void setDepartmentDescription(String departmentDescription) {
        DepartmentDescription = departmentDescription;
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
