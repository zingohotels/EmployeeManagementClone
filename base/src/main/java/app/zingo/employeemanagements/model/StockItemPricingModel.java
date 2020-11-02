package app.zingo.employeemanagements.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class StockItemPricingModel implements Serializable {

    @SerializedName ("StockItemPricingId")
    private int StockItemPricingId;
    @SerializedName ("AvailableQuantity")
    private int AvailableQuantity;
    @SerializedName ("SIUnit")
    private String SIUnit;
    @SerializedName ("PriceFor")
    private double PriceFor;
    @SerializedName ("DisplayPrice")
    private double DisplayPrice;
    @SerializedName ("SellingPrice")
    private double SellingPrice;
    @SerializedName ("stockItem")
    private StockItemModel stockItem;
    @SerializedName ("StockItemId")
    private int StockItemId;


    public int getStockItemPricingId() {
        return StockItemPricingId;
    }

    public void setStockItemPricingId(int stockItemPricingId) {
        StockItemPricingId = stockItemPricingId;
    }

    public int getAvailableQuantity() {
        return AvailableQuantity;
    }

    public void setAvailableQuantity(int availableQuantity) {
        AvailableQuantity = availableQuantity;
    }

    public String getSIUnit() {
        return SIUnit;
    }

    public void setSIUnit(String SIUnit) {
        this.SIUnit = SIUnit;
    }

    public double getPriceFor() {
        return PriceFor;
    }

    public void setPriceFor(double priceFor) {
        PriceFor = priceFor;
    }

    public double getDisplayPrice() {
        return DisplayPrice;
    }

    public void setDisplayPrice(double displayPrice) {
        DisplayPrice = displayPrice;
    }

    public double getSellingPrice() {
        return SellingPrice;
    }

    public void setSellingPrice(double sellingPrice) {
        SellingPrice = sellingPrice;
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
