package app.zingo.employeemanagements.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

public class StockOrderPaymentsModel implements Serializable {
    @SerializedName ("StockOrderPaymentsId")
    private int StockOrderPaymentsId;
    @SerializedName ("stockOrder")
    private StockOrdersModel stockOrder;
    @SerializedName ("StockOrderId")
    private int StockOrderId;
    @SerializedName ("PaymentDate")
    private Date PaymentDate;
    @SerializedName ("AmountPaid")
    private double AmountPaid;
    @SerializedName ("PaymentMode")
    private String PaymentMode;
    @SerializedName ("Description")
    private String Description;


    public int getStockOrderPaymentsId() {
        return StockOrderPaymentsId;
    }

    public void setStockOrderPaymentsId(int stockOrderPaymentsId) {
        StockOrderPaymentsId = stockOrderPaymentsId;
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

    public Date getPaymentDate() {
        return PaymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        PaymentDate = paymentDate;
    }

    public double getAmountPaid() {
        return AmountPaid;
    }

    public void setAmountPaid(double amountPaid) {
        AmountPaid = amountPaid;
    }

    public String getPaymentMode() {
        return PaymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        PaymentMode = paymentMode;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }
}
