package app.zingo.employeemanagements.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by ZingoHotels Tech on 03-01-2019.
 */

public class OrganizationImage implements Serializable {

    @SerializedName("OrganizationImageId")
    public int OrganizationImageId;

    @SerializedName("organization")
    public Organization organization;

    @SerializedName("OrganizationId")
    public int OrganizationId;

    @SerializedName("Title")
    public String Title;

    @SerializedName("Image")
    public String Image;

    public int getOrganizationImageId() {
        return OrganizationImageId;
    }

    public void setOrganizationImageId(int organizationImageId) {
        OrganizationImageId = organizationImageId;
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

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }
}
