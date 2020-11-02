package app.zingo.employeemanagements.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ZingoHotels Tech on 03-01-2019.
 */

public class Plans implements Serializable {

    @SerializedName("PlansId")
    public int PlansId;

    @SerializedName("PlanName")
    public String PlanName;

    @SerializedName("Description")
    public String Description;

    @SerializedName("ratesList")
    public ArrayList<Rates> ratesList;

    public int getPlansId() {
        return PlansId;
    }

    public void setPlansId(int plansId) {
        PlansId = plansId;
    }

    public String getPlanName() {
        return PlanName;
    }

    public void setPlanName(String planName) {
        PlanName = planName;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public ArrayList<Rates> getRatesList() {
        return ratesList;
    }

    public void setRatesList(ArrayList<Rates> ratesList) {
        this.ratesList = ratesList;
    }
}
