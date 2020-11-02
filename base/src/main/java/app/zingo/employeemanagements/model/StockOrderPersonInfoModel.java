package app.zingo.employeemanagements.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class StockOrderPersonInfoModel implements Serializable {

    @SerializedName ("StockOrderPersonInfoId")
    private int StockOrderPersonInfoId;
    @SerializedName ("PersonName")
    private String PersonName;
    @SerializedName ("Mobile")
    private String Mobile;
    @SerializedName ("Email")
    private String Email;
    @SerializedName ("ShippingAddress")
    private String ShippingAddress;
    @SerializedName ("BillingAddress")
    private String BillingAddress;
    @SerializedName ("stockOrder")
    private StockOrdersModel stockOrder;
    @SerializedName ("StockOrderId")
    private int StockOrderId;


    public int getStockOrderPersonInfoId() {
        return StockOrderPersonInfoId;
    }

    public void setStockOrderPersonInfoId(int stockOrderPersonInfoId) {
        StockOrderPersonInfoId = stockOrderPersonInfoId;
    }

    public String getPersonName() {
        return PersonName;
    }

    public void setPersonName(String personName) {
        PersonName = personName;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getShippingAddress() {
        return ShippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        ShippingAddress = shippingAddress;
    }

    public StockOrdersModel getStockOrder() {
        return stockOrder;
    }

    public void setStockOrder( StockOrdersModel stockOrder) {
        this.stockOrder = stockOrder;
    }

    public int getStockOrderId() {
        return StockOrderId;
    }

    public void setStockOrderId(int stockOrderId) {
        StockOrderId = stockOrderId;
    }

    public String getBillingAddress ( ) {
        return BillingAddress;
    }

    public void setBillingAddress ( String billingAddress ) {
        BillingAddress = billingAddress;
    }
}
