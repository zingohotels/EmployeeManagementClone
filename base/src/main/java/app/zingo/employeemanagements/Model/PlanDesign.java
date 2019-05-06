package app.zingo.employeemanagements.Model;

import java.util.ArrayList;

public class PlanDesign {

    String name;
    String rupees;
    String color;
    int drawable;
    ArrayList<PlanFeatures> features;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRupees() {
        return rupees;
    }

    public void setRupees(String rupees) {
        this.rupees = rupees;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getDrawable() {
        return drawable;
    }

    public void setDrawable(int drawable) {
        this.drawable = drawable;
    }

    public ArrayList<PlanFeatures> getFeatures() {
        return features;
    }

    public void setFeatures(ArrayList<PlanFeatures> features) {
        this.features = features;
    }
}
