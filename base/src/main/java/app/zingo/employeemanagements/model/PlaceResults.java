package app.zingo.employeemanagements.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ZingoHotels Tech on 26-09-2018.
 */

public class PlaceResults {

    @SerializedName("place_id")
    private String place_id;

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }
}
