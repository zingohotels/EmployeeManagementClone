package app.zingo.employeemanagements.Model;


import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class HolidayList {


    public String mDate;
    public String detail;

    public HolidayList() {
    }

    public String getmDate() {
        return mDate;
    }

    public void setmDate(String mDate) {
        this.mDate = mDate;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }


}
