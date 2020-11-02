package app.zingo.employeemanagements.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class StockItemModel implements Serializable {
    @SerializedName("StockItemId")
    private int StockItemId;
    @SerializedName("stockSubCategory")
    private StockSubCategoryModel stockSubCategory;
    @SerializedName("StockSubCategoryId")
    private int StockSubCategoryId;
    @SerializedName("StockCategory")
    private StockCategoryModel StockCategory;
    @SerializedName("StockCategoryId")
    private int StockCategoryId;
    @SerializedName("organization")
    private Organization organization;
    @SerializedName("OrganizationId")
    private int OrganizationId;
    @SerializedName("brand")
    private BrandsModel brand;
    @SerializedName("BrandId")
    private int BrandId;
    @SerializedName("StockItemName")
    private String StockItemName;
    @SerializedName("StockItemDescription")
    private String StockItemDescription;
    @SerializedName("InStock")
    private boolean InStock;
    @SerializedName("StockItemImage")
    private String StockItemImage;
    @SerializedName("StockItemPricingList")
    private ArrayList<StockItemPricingModel> StockItemPricingList;
    @SerializedName("aboutStockItem")
    private ArrayList<AboutStockItemModel> aboutStockItem;

    int quantity;

    public int getQuantity ( ) {
        return quantity;
    }

    public void setQuantity ( int quantity ) {
        this.quantity = quantity;
    }


    public int getStockItemId() {
        return StockItemId;
    }

    public void setStockItemId(int stockItemId) {
        StockItemId = stockItemId;
    }

    public StockSubCategoryModel getStockSubCategory() {
        return stockSubCategory;
    }

    public void setStockSubCategory(StockSubCategoryModel stockSubCategory) {
        this.stockSubCategory = stockSubCategory;
    }

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

    public BrandsModel getBrand() {
        return brand;
    }

    public void setBrand(BrandsModel brand) {
        this.brand = brand;
    }

    public int getBrandId() {
        return BrandId;
    }

    public void setBrandId(int brandId) {
        BrandId = brandId;
    }

    public String getStockItemName() {
        return StockItemName;
    }

    public void setStockItemName(String stockItemName) {
        StockItemName = stockItemName;
    }

    public String getStockItemDescription() {
        return StockItemDescription;
    }

    public void setStockItemDescription(String stockItemDescription) {
        StockItemDescription = stockItemDescription;
    }

    public boolean isInStock() {
        return InStock;
    }

    public void setInStock(boolean inStock) {
        InStock = inStock;
    }

    public String getStockItemImage() {
        return StockItemImage;
    }

    public void setStockItemImage(String stockItemImage) {
        StockItemImage = stockItemImage;
    }

    public  ArrayList<StockItemPricingModel> getStockItemPricingList() {
        return StockItemPricingList;
    }

    public void setStockItemPricingList( ArrayList<StockItemPricingModel> stockItemPricingList) {
        StockItemPricingList = stockItemPricingList;
    }

    public ArrayList<AboutStockItemModel> getAboutStockItem() {
        return aboutStockItem;
    }

    public void setAboutStockItem(ArrayList<AboutStockItemModel> aboutStockItem) {
        this.aboutStockItem = aboutStockItem;
    }
}
