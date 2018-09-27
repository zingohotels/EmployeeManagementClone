package app.zingo.employeemanagements.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by ZingoHotels Tech on 26-09-2018.
 */

public class PreferenceHandler {

    private static PreferenceHandler preferenceHandler = null;
    private SharedPreferences sh = null;

    private PreferenceHandler(Context context)
    {
        sh = PreferenceManager.getDefaultSharedPreferences(context);
    }


    public static PreferenceHandler getInstance(Context context)
    {
        if(preferenceHandler == null) {
            preferenceHandler = new PreferenceHandler(context);
        }
        return preferenceHandler;

    }

    public void clear(){
        sh.edit().clear().apply();

    }

    public void setUserId(int id)
    {
        sh.edit().putInt(Constants.USER_ID,id).apply();
    }

    public int getUserId()
    {
        return sh.getInt(Constants.USER_ID,0);
    }

    public void setUserName(String hotelName) {
        sh.edit().putString(Constants.USER_NAME,hotelName).apply();
    }

    public String getUserName()
    {
        return sh.getString(Constants.USER_NAME,"");
    }

    public void setPhoneNumber(String mobilenumber)
    {
        sh.edit().putString(Constants.USER_PHONENUMBER,mobilenumber).apply();
    }

    public String getPhoneNumber()
    {
        return sh.getString(Constants.USER_PHONENUMBER,"");
    }

    public void setCompanyId(int id)
    {
        sh.edit().putInt(Constants.COMPANY_ID,id).apply();
    }

    public int getCompanyId()
    {
        return sh.getInt(Constants.COMPANY_ID,0);
    }

    public void setDesignationId(int id)
    {
        sh.edit().putInt(Constants.DESIGNATION_ID,id).apply();
    }

    public int getDesignationId()
    {
        return sh.getInt(Constants.DESIGNATION_ID,0);
    }

    public void setDepartmentId(int id)
    {
        sh.edit().putInt(Constants.DEPARTMENT_ID,id).apply();
    }

    public int getDepartmentId()
    {
        return sh.getInt(Constants.DEPARTMENT_ID,0);
    }


    public void setCompanyName(String hotelName) {
        sh.edit().putString(Constants.COMPANY_NAME,hotelName).apply();
    }

    public String getCompanyName()
    {
        return sh.getString(Constants.COMPANY_NAME,"");
    }

    public void setHotelPlace(String hotelPlace) {
        sh.edit().putString(Constants.HOTEL_PLACE,hotelPlace).apply();
    }

    public String getHotelPlace()
    {
        return sh.getString(Constants.HOTEL_PLACE,"");
    }

    public void setUserFullName(String fullname) {
        sh.edit().putString(Constants.USER_FULLNAME,fullname).apply();
    }

    public String getUserFullName()
    {
        return sh.getString(Constants.USER_FULLNAME,"");
    }

    public void setUserEmail(String email) {
        sh.edit().putString(Constants.USER_EMAIL,email).apply();
    }

    public String getUserEmail()
    {
        return sh.getString(Constants.USER_EMAIL,"");
    }

    public void setUserRoleUniqueID(String approved)
    {
        sh.edit().putString(Constants.USER_ROLE_UNIQUE_ID,approved).apply();
    }

    public String getUserRoleUniqueID()
    {
        return sh.getString(Constants.USER_ROLE_UNIQUE_ID,"");
    }

}
