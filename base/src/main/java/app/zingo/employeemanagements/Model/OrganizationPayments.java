package app.zingo.employeemanagements.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class OrganizationPayments implements Serializable {


    @SerializedName("OrganizationPaymentsId")
    public int OrganizationPaymentsId;

    @SerializedName("Title")
    public String Title;

    @SerializedName("Description")
    public String Description;

    @SerializedName("PaymentDate")
    public String PaymentDate;

    @SerializedName("OrganizationId")
    public int OrganizationId;

    @SerializedName("Organization")
    public Organization Organization;

    @SerializedName("PaymentBy")
    public String PaymentBy;

    @SerializedName("Amount")
    public double Amount;

    @SerializedName("TransactionId")
    public String TransactionId;

    @SerializedName("TransactionMethod")
    public String TransactionMethod;

    @SerializedName("zingyPaymentStatus")
    public String zingyPaymentStatus ;

    @SerializedName("ZingyPaymentDate")
    public String ZingyPaymentDate;

    @SerializedName("ResellerCommissionPercentage")
    public double ResellerCommissionPercentage;

    public int getOrganizationPaymentsId() {
        return OrganizationPaymentsId;
    }

    public void setOrganizationPaymentsId(int organizationPaymentsId) {
        OrganizationPaymentsId = organizationPaymentsId;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getPaymentDate() {
        return PaymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        PaymentDate = paymentDate;
    }

    public int getOrganizationId() {
        return OrganizationId;
    }

    public void setOrganizationId(int organizationId) {
        OrganizationId = organizationId;
    }

    public app.zingo.employeemanagements.Model.Organization getOrganization() {
        return Organization;
    }

    public void setOrganization(app.zingo.employeemanagements.Model.Organization organization) {
        Organization = organization;
    }

    public String getPaymentBy() {
        return PaymentBy;
    }

    public void setPaymentBy(String paymentBy) {
        PaymentBy = paymentBy;
    }

    public double getAmount() {
        return Amount;
    }

    public void setAmount(double amount) {
        Amount = amount;
    }

    public String getTransactionId() {
        return TransactionId;
    }

    public void setTransactionId(String transactionId) {
        TransactionId = transactionId;
    }

    public String getTransactionMethod() {
        return TransactionMethod;
    }

    public void setTransactionMethod(String transactionMethod) {
        TransactionMethod = transactionMethod;
    }

    public String getZingyPaymentStatus() {
        return zingyPaymentStatus;
    }

    public void setZingyPaymentStatus(String zingyPaymentStatus) {
        this.zingyPaymentStatus = zingyPaymentStatus;
    }

    public String getZingyPaymentDate() {
        return ZingyPaymentDate;
    }

    public void setZingyPaymentDate(String zingyPaymentDate) {
        ZingyPaymentDate = zingyPaymentDate;
    }

    public double getResellerCommissionPercentage() {
        return ResellerCommissionPercentage;
    }

    public void setResellerCommissionPercentage(double resellerCommissionPercentage) {
        ResellerCommissionPercentage = resellerCommissionPercentage;
    }
}
