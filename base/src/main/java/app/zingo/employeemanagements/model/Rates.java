package app.zingo.employeemanagements.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by ZingoHotels Tech on 03-01-2019.
 */

public class Rates implements Serializable {

    @SerializedName("RatesId")
    public int RatesId;

    @SerializedName("Duration")
    public int Duration;//days

    @SerializedName("Price")
    public double Price;

    @SerializedName("ExtensionPeriod")
    public int ExtensionPeriod;//days

    @SerializedName("plan")
    public Plans plan;

    @SerializedName("PlansId")
    public int PlansId;

    public int getRatesId() {
        return RatesId;
    }

    public void setRatesId(int ratesId) {
        RatesId = ratesId;
    }

    public int getDuration() {
        return Duration;
    }

    public void setDuration(int duration) {
        Duration = duration;
    }

    public double getPrice() {
        return Price;
    }

    public void setPrice(double price) {
        Price = price;
    }

    public int getExtensionPeriod() {
        return ExtensionPeriod;
    }

    public void setExtensionPeriod(int extensionPeriod) {
        ExtensionPeriod = extensionPeriod;
    }

    public Plans getPlan() {
        return plan;
    }

    public void setPlan( Plans plan) {
        this.plan = plan;
    }

    public int getPlansId() {
        return PlansId;
    }

    public void setPlansId(int plansId) {
        PlansId = plansId;
    }
}
