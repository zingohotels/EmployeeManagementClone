package app.zingo.employeemanagements.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AboutStockItemModel implements Serializable {
    @SerializedName("AboutStockItemId")
    private int AboutStockItemId;
    @SerializedName("AboutItem")
    private String AboutItem;
    @SerializedName("Specification")
    private String Specification;
    @SerializedName("HowToUse")
    private String HowToUse;
    @SerializedName("OtherInfo")
    private String OtherInfo;
    @SerializedName("stockItem")
    private StockItemModel stockItem;
    @SerializedName("StockItemId")
    private int StockItemId;


    public int getAboutStockItemId() {
        return AboutStockItemId;
    }

    public void setAboutStockItemId(int aboutStockItemId) {
        AboutStockItemId = aboutStockItemId;
    }

    public String getAboutItem() {
        return AboutItem;
    }

    public void setAboutItem(String aboutItem) {
        AboutItem = aboutItem;
    }

    public String getSpecification() {
        return Specification;
    }

    public void setSpecification(String specification) {
        Specification = specification;
    }

    public String getHowToUse() {
        return HowToUse;
    }

    public void setHowToUse(String howToUse) {
        HowToUse = howToUse;
    }

    public String getOtherInfo() {
        return OtherInfo;
    }

    public void setOtherInfo(String otherInfo) {
        OtherInfo = otherInfo;
    }

    public StockItemModel getStockItem() {
        return stockItem;
    }

    public void setStockItem(StockItemModel stockItem) {
        this.stockItem = stockItem;
    }

    public int getStockItemId() {
        return StockItemId;
    }

    public void setStockItemId(int stockItemId) {
        StockItemId = stockItemId;
    }
}
