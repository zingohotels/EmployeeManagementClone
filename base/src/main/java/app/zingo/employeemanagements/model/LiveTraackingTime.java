package app.zingo.employeemanagements.model;

public class LiveTraackingTime {

   LiveTracking liveTracking;
    String previousTime;
    String km;
    String duration;

    public LiveTracking getLiveTracking() {
        return liveTracking;
    }

    public void setLiveTracking( LiveTracking liveTracking) {
        this.liveTracking = liveTracking;
    }

    public String getPreviousTime() {
        return previousTime;
    }

    public void setPreviousTime(String previousTime) {
        this.previousTime = previousTime;
    }

    public String getKm() {
        return km;
    }

    public void setKm(String km) {
        this.km = km;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
