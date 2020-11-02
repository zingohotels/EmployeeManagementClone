package app.zingo.employeemanagements.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class StockCategoryModel implements Serializable {

    @SerializedName ("StockCategoryId")
    private int StockCategoryId;

    @SerializedName ("organization")
    private Organization organization;

    @SerializedName ("OrganizationId")
    private int OrganizationId;

    @SerializedName ("StockCategoryName")
    private String StockCategoryName;

    @SerializedName ("StockCategoryDescription")
    private String StockCategoryDescription;

    @SerializedName ("StockCategoryImage")
    private String StockCategoryImage;

    @SerializedName ("StockSubCatList")
    private ArrayList<StockSubCategoryModel> StockSubCatList;


    public int getStockCategoryId() {
        return StockCategoryId;
    }

    public void setStockCategoryId(int stockCategoryId) {
        StockCategoryId = stockCategoryId;
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

    public String getStockCategoryName() {
        return StockCategoryName;
    }

    public void setStockCategoryName(String stockCategoryName) {
        StockCategoryName = stockCategoryName;
    }

    public String getStockCategoryDescription() {
        return StockCategoryDescription;
    }

    public void setStockCategoryDescription(String stockCategoryDescription) {
        StockCategoryDescription = stockCategoryDescription;
    }

    public String getStockCategoryImage() {
        return StockCategoryImage;
    }

    public void setStockCategoryImage(String stockCategoryImage) {
        StockCategoryImage = stockCategoryImage;
    }

    public ArrayList<StockSubCategoryModel>  getStockSubCatList() {
        return StockSubCatList;
    }

    public void setStockSubCatList(ArrayList<StockSubCategoryModel>  stockSubCatList) {
        StockSubCatList = stockSubCatList;
    }
}
