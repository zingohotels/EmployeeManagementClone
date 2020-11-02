package app.zingo.employeemanagements.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class StockOrdersModel implements Serializable {

    @SerializedName ("StockOrderId")
    private int StockOrderId;
    @SerializedName ("OrganizationId")
    private int OrganizationId;
    @SerializedName ("OrderNumber")
    private String OrderNumber;
    @SerializedName ("TotalAmount")
    private double TotalAmount;
    @SerializedName ("UserName")
    private String UserName;
    @SerializedName ("UserRefId")
    private String UserRefId;
    @SerializedName ("_stockOrderPersonInfo")
    private ArrayList<StockOrderPersonInfoModel> _stockOrderPersonInfo;
    @SerializedName ("stockOrderLogsList")
    private ArrayList<StockOrderLogsModel> stockOrderLogsList;
    @SerializedName ("StockOrderDetailsList")
    private ArrayList< StockOrderDetailsModel > StockOrderDetailsList;
    @SerializedName ("StockOrderPaymentsList")
    private ArrayList<StockOrderPaymentsModel> StockOrderPaymentsList;


    public int getStockOrderId() {
        return StockOrderId;
    }

    public void setStockOrderId(int stockOrderId) {
        StockOrderId = stockOrderId;
    }

    public String getOrderNumber() {
        return OrderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        OrderNumber = orderNumber;
    }

    public double getTotalAmount() {
        return TotalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        TotalAmount = totalAmount;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getUserRefId() {
        return UserRefId;
    }

    public void setUserRefId(String userRefId) {
        UserRefId = userRefId;
    }

    public ArrayList < StockOrderPersonInfoModel > get_stockOrderPersonInfo ( ) {
        return _stockOrderPersonInfo;
    }

    public void set_stockOrderPersonInfo ( ArrayList < StockOrderPersonInfoModel > _stockOrderPersonInfo ) {
        this._stockOrderPersonInfo = _stockOrderPersonInfo;
    }

    public ArrayList < StockOrderLogsModel > getStockOrderLogsList ( ) {
        return stockOrderLogsList;
    }

    public void setStockOrderLogsList ( ArrayList < StockOrderLogsModel > stockOrderLogsList ) {
        this.stockOrderLogsList = stockOrderLogsList;
    }

    public ArrayList < StockOrderDetailsModel > getStockOrderDetailsList ( ) {
        return StockOrderDetailsList;
    }

    public void setStockOrderDetailsList ( ArrayList < StockOrderDetailsModel > stockOrderDetailsList ) {
        StockOrderDetailsList = stockOrderDetailsList;
    }

    public ArrayList < StockOrderPaymentsModel > getStockOrderPaymentsList ( ) {
        return StockOrderPaymentsList;
    }

    public void setStockOrderPaymentsList ( ArrayList < StockOrderPaymentsModel > stockOrderPaymentsList ) {
        StockOrderPaymentsList = stockOrderPaymentsList;
    }

    public int getOrganizationId ( ) {
        return OrganizationId;
    }

    public void setOrganizationId ( int organizationId ) {
        OrganizationId = organizationId;
    }
}
