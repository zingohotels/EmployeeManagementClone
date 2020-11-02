package app.zingo.employeemanagements.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

public class StockOrderLogsModel implements Serializable {
    @SerializedName ("StockOrderLogsId")
    private int StockOrderLogsId;
    @SerializedName ("Status")
    private String Status;
    @SerializedName ("LogDate")
    private Date LogDate;
    @SerializedName ("Description")
    private String Description;
    @SerializedName ("stockOrder")
    private StockOrdersModel stockOrder;
    @SerializedName ("StockOrderId")
    private int StockOrderId;


    public int getStockOrderLogsId() {
        return StockOrderLogsId;
    }

    public void setStockOrderLogsId(int stockOrderLogsId) {
        StockOrderLogsId = stockOrderLogsId;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public Date getLogDate() {
        return LogDate;
    }

    public void setLogDate(Date logDate) {
        LogDate = logDate;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
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
