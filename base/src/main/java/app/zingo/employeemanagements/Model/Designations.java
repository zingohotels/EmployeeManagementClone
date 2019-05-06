package app.zingo.employeemanagements.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by ZingoHotels Tech on 26-09-2018.
 */

public class Designations implements Serializable{

    @SerializedName("DesignationId")
    private int DesignationId;

    @SerializedName("DesignationTitle")
    private String DesignationTitle;

    @SerializedName("Description")
    private String Description;

    public int getDesignationId() {
        return DesignationId;
    }

    public void setDesignationId(int designationId) {
        DesignationId = designationId;
    }

    public String getDesignationTitle() {
        return DesignationTitle;
    }

    public void setDesignationTitle(String designationTitle) {
        DesignationTitle = designationTitle;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }
}
