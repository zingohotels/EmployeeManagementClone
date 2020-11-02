package app.zingo.employeemanagements.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class StockSubCategoryModel implements Serializable {

    @SerializedName ("StockSubCategoryId")
    private int StockSubCategoryId;

    @SerializedName ("StockCategory")
    private StockCategoryModel StockCategory;

    @SerializedName ("StockCategoryId")
    private int StockCategoryId;

    @SerializedName ("organization")
    private Organization organization;

    @SerializedName ("OrganizationId")
    private int OrganizationId;

    @SerializedName ("StockSubCategoryName")
    private String StockSubCategoryName;

    @SerializedName ("StockSubCategoryDescription")
    private String StockSubCategoryDescription;

    @SerializedName ("StockSubCategoryImage")
    private String StockSubCategoryImage;

    @SerializedName ("stockItemList")
    private ArrayList<StockItemModel> stockItemList;


    public int getStockSubCategoryId() {
        return StockSubCategoryId;
    }

    public void setStockSubCategoryId(int stockSubCategoryId) {
        StockSubCategoryId = stockSubCategoryId;
    }

    public StockCategoryModel getStockCategory() {
        return StockCategory;
    }

    public void setStockCategory(StockCategoryModel stockCategory) {
        StockCategory = stockCategory;
    }

    public int getStockCategoryId() {
        return StockCategoryId;
    }

    public void setStockCategoryId(int stockCategoryId) {
        StockCategoryId = stockCategoryId;
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

    public String getStockSubCategoryName() {
        return StockSubCategoryName;
    }

    public void setStockSubCategoryName(String stockSubCategoryName) {
        StockSubCategoryName = stockSubCategoryName;
    }

    public String getStockSubCategoryDescription() {
        return StockSubCategoryDescription;
    }

    public void setStockSubCategoryDescription(String stockSubCategoryDescription) {
        StockSubCategoryDescription = stockSubCategoryDescription;
    }

    public String getStockSubCategoryImage() {
        return StockSubCategoryImage;
    }

    public void setStockSubCategoryImage(String stockSubCategoryImage) {
        StockSubCategoryImage = stockSubCategoryImage;
    }

    public ArrayList<StockItemModel>  getStockItemList() {
        return stockItemList;
    }

    public void setStockItemList(ArrayList<StockItemModel>  stockItemList) {
        this.stockItemList = stockItemList;
    }
}
