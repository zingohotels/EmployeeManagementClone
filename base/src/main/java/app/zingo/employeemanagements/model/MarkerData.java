package app.zingo.employeemanagements.model;

/**
 * Created by ZingoHotels Tech on 01-10-2018.
 */

public class MarkerData {

    double longi;
    double lati;
    String title;
    String person;

    public MarkerData(double longi, double lati, String title, String person) {
        this.longi = longi;
        this.lati = lati;
        this.title = title;
        this.person = person;
    }

    public double getLongi() {
        return longi;
    }

    public void setLongi(double longi) {
        this.longi = longi;
    }

    public double getLati() {
        return lati;
    }

    public void setLati(double lati) {
        this.lati = lati;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }
}
