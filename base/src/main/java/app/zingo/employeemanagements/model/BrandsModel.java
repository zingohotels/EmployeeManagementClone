package app.zingo.employeemanagements.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BrandsModel implements Serializable {

    @SerializedName ("BrandId")
    private int BrandId;

    @SerializedName ("organization")
    private Organization organization;

    @SerializedName ("OrganizationId")
    private int OrganizationId;

    @SerializedName ("BrandName")
    private String BrandName;

    @SerializedName ("BrandDescription")
    private String BrandDescription;

    @SerializedName ("BrandImage")
    private String BrandImage;


    public int getBrandId() {
        return BrandId;
    }

    public void setBrandId(int brandId) {
        BrandId = brandId;
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

    public String getBrandName() {
        return BrandName;
    }

    public void setBrandName(String brandName) {
        BrandName = brandName;
    }

    public String getBrandDescription() {
        return BrandDescription;
    }

    public void setBrandDescription(String brandDescription) {
        BrandDescription = brandDescription;
    }

    public String getBrandImage() {
        return BrandImage;
    }

    public void setBrandImage(String brandImage) {
        BrandImage = brandImage;
    }
}
