package app.zingo.employeemanagements.Model;

public class LiveTrackingAdminData {

    Employee empName;
    LiveTracking liveTracking;
    double pLati;
    double pLongi;
    int size;

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Employee getEmpName() {
        return empName;
    }

    public void setEmpName(Employee empName) {
        this.empName = empName;
    }

    public LiveTracking getLiveTracking() {
        return liveTracking;
    }

    public void setLiveTracking(LiveTracking liveTracking) {
        this.liveTracking = liveTracking;
    }

    public double getpLati() {
        return pLati;
    }

    public void setpLati(double pLati) {
        this.pLati = pLati;
    }

    public double getpLongi() {
        return pLongi;
    }

    public void setpLongi(double pLongi) {
        this.pLongi = pLongi;
    }
}
