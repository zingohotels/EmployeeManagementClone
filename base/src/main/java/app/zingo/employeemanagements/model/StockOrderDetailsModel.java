package app.zingo.employeemanagements.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class StockOrderDetailsModel implements Serializable {
    @SerializedName ("StockOrderDetailsId")
    private int StockOrderDetailsId;
    @SerializedName ("stockItem")
    private StockItemModel stockItem;
    @SerializedName ("StockItemId")
    private int StockItemId;
    @SerializedName ("Quantity")
    private int Quantity;
    @SerializedName ("TotalPrice")
    private double TotalPrice;
    @SerializedName ("stockOrder")
    private StockOrdersModel stockOrder;
    @SerializedName ("StockOrderId")
    private int StockOrderId;


    public int getStockOrderDetailsId() {
        return StockOrderDetailsId;
    }

    public void setStockOrderDetailsId(int stockOrderDetailsId) {
        StockOrderDetailsId = stockOrderDetailsId;
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

    public int getQuantity() {
        return Quantity;
    }

    public void setQuantity(int quantity) {
        Quantity = quantity;
    }

    public double getTotalPrice() {
        return TotalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        TotalPrice = totalPrice;
    }

    public StockOrdersModel getStockOrder() {
        return stockOrder;
    }

    public void setStockOrder(StockOrdersModel stockOrder) {
        this.stockOrder = stockOrder;
    }

    public int getStockOrderId() {
        return StockOrderId;
    }

    public void setStockOrderId(int stockOrderId) {
        StockOrderId = stockOrderId;
    }
}
