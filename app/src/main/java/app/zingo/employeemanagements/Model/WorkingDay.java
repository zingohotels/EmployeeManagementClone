package app.zingo.employeemanagements.Model;


import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;

@IgnoreExtraProperties
public class WorkingDay {

    public boolean isSunday;
    public boolean ismonday;
    public boolean isTuesday;
    public boolean isWednesday;
    public boolean isThursday;
    public boolean isFriday;
    public boolean isSaturday;

    public String sunStartTime;
    public String sunEndTime;
    public String monStartTime;
    public String monEndTime;
    public String tueStartTime;
    public String tueEndTime;
    public String wedStartTime;
    public String wedEndTime;
    public String thuStartTime;
    public String thuEndTime;
    public String friStartTime;
    public String friEndTime;
    public String satStartTime;
    public String satEndTime;

    public  int orgId;
    public ArrayList<HolidayList> holidayLists;

    public WorkingDay(){

    }



    public boolean isSunday() {
        return isSunday;
    }

    public void setSunday(boolean sunday) {
        isSunday = sunday;
    }

    public boolean isIsmonday() {
        return ismonday;
    }

    public void setIsmonday(boolean ismonday) {
        this.ismonday = ismonday;
    }

    public boolean isTuesday() {
        return isTuesday;
    }

    public void setTuesday(boolean tuesday) {
        isTuesday = tuesday;
    }

    public boolean isWednesday() {
        return isWednesday;
    }

    public void setWednesday(boolean wednesday) {
        isWednesday = wednesday;
    }

    public boolean isThursday() {
        return isThursday;
    }

    public void setThursday(boolean thursday) {
        isThursday = thursday;
    }

    public boolean isFriday() {
        return isFriday;
    }

    public void setFriday(boolean friday) {
        isFriday = friday;
    }

    public boolean isSaturday() {
        return isSaturday;
    }

    public void setSaturday(boolean saturday) {
        isSaturday = saturday;
    }

    public int getOrgId() {
        return orgId;
    }

    public void setOrgId(int orgId) {
        this.orgId = orgId;
    }

    public String getSunStartTime() {
        return sunStartTime;
    }

    public void setSunStartTime(String sunStartTime) {
        this.sunStartTime = sunStartTime;
    }

    public String getSunEndTime() {
        return sunEndTime;
    }

    public void setSunEndTime(String sunEndTime) {
        this.sunEndTime = sunEndTime;
    }

    public String getMonStartTime() {
        return monStartTime;
    }

    public void setMonStartTime(String monStartTime) {
        this.monStartTime = monStartTime;
    }

    public String getMonEndTime() {
        return monEndTime;
    }

    public void setMonEndTime(String monEndTime) {
        this.monEndTime = monEndTime;
    }

    public String getTueStartTime() {
        return tueStartTime;
    }

    public void setTueStartTime(String tueStartTime) {
        this.tueStartTime = tueStartTime;
    }

    public String getTueEndTime() {
        return tueEndTime;
    }

    public void setTueEndTime(String tueEndTime) {
        this.tueEndTime = tueEndTime;
    }

    public String getWedStartTime() {
        return wedStartTime;
    }

    public void setWedStartTime(String wedStartTime) {
        this.wedStartTime = wedStartTime;
    }

    public String getWedEndTime() {
        return wedEndTime;
    }

    public void setWedEndTime(String wedEndTime) {
        this.wedEndTime = wedEndTime;
    }

    public String getThuStartTime() {
        return thuStartTime;
    }

    public void setThuStartTime(String thuStartTime) {
        this.thuStartTime = thuStartTime;
    }

    public String getThuEndTime() {
        return thuEndTime;
    }

    public void setThuEndTime(String thuEndTime) {
        this.thuEndTime = thuEndTime;
    }

    public String getFriStartTime() {
        return friStartTime;
    }

    public void setFriStartTime(String friStartTime) {
        this.friStartTime = friStartTime;
    }

    public String getFriEndTime() {
        return friEndTime;
    }

    public void setFriEndTime(String friEndTime) {
        this.friEndTime = friEndTime;
    }

    public String getSatStartTime() {
        return satStartTime;
    }

    public void setSatStartTime(String satStartTime) {
        this.satStartTime = satStartTime;
    }

    public String getSatEndTime() {
        return satEndTime;
    }

    public void setSatEndTime(String satEndTime) {
        this.satEndTime = satEndTime;
    }

    public ArrayList<HolidayList> getHolidayLists() {
        return holidayLists;
    }

    public void setHolidayLists(ArrayList<HolidayList> holidayLists) {
        this.holidayLists = holidayLists;
    }
}
