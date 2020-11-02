package app.zingo.employeemanagements.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by ZingoHotels Tech on 26-09-2018.
 */

public class Places {

    @SerializedName("status")
    private String status;

    @SerializedName("results")
    private ArrayList< PlaceResults > results;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList< PlaceResults > getResults() {
        return results;
    }

    public void setResults(ArrayList< PlaceResults > results) {
        this.results = results;
    }
}
