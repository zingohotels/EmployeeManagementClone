package app.zingo.employeemanagements.Model;

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
    public int PlanName;

    @SerializedName("Description")
    public int Description;

    @SerializedName("ratesList")
    public ArrayList<Rates> ratesList;

    public int getPlansId() {
        return PlansId;
    }

    public void setPlansId(int plansId) {
        PlansId = plansId;
    }

    public int getPlanName() {
        return PlanName;
    }

    public void setPlanName(int planName) {
        PlanName = planName;
    }

    public int getDescription() {
        return Description;
    }

    public void setDescription(int description) {
        Description = description;
    }

    public ArrayList<Rates> getRatesList() {
        return ratesList;
    }

    public void setRatesList(ArrayList<Rates> ratesList) {
        this.ratesList = ratesList;
    }
}
