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

    public void setManagerId(int id)
    {
        sh.edit().putInt(Constants.MANAGER_ID,id).apply();
    }

    public int getManagerId()
    {
        return sh.getInt(Constants.MANAGER_ID,0);
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

    public void setAppType(String appType) {
        sh.edit().putString(Constants.APP_TYPE,appType).apply();
    }

    public String getAppType()
    {
        return sh.getString(Constants.APP_TYPE,"");
    }

    public void setSignUpType(String appType) {
        sh.edit().putString(Constants.SIGN_TYPE,appType).apply();
    }

    public String getSignUpType()
    {
        return sh.getString(Constants.SIGN_TYPE,"");
    }

    public void setLicenseStartDate(String licenseStartDate) {
        sh.edit().putString(Constants.LIC_START,licenseStartDate).apply();
    }

    public String getLicenseStartDate()
    {
        return sh.getString(Constants.LIC_START,"");
    }

    public void setLicenseEndDate(String licenseEndDate) {
        sh.edit().putString(Constants.LIC_END,licenseEndDate).apply();
    }

    public String getLicenseEndDate()
    {
        return sh.getString(Constants.LIC_END,"");
    }

    public void setPlanType(String appType) {
        sh.edit().putString(Constants.PLAN_TYPE,appType).apply();
    }

    public String getPlanType()
    {
        return sh.getString(Constants.PLAN_TYPE,"");
    }

    public void setSignupDate(String signupDate) {
        sh.edit().putString(Constants.SIGNUP_DATE,signupDate).apply();
    }

    public String getSignupDate()
    {
        return sh.getString(Constants.SIGNUP_DATE,"");
    }

    public void setEmployeeLimit(int employeeLimit) {
        sh.edit().putInt(Constants.EMP_LIMIT,employeeLimit).apply();
    }

    public int getEmployeeLimit()
    {
        return sh.getInt(Constants.EMP_LIMIT,0);
    }

    public void setPlanId(int planId) {
        sh.edit().putInt(Constants.PLAN_ID,planId).apply();
    }

    public int getPlanId()
    {
        return sh.getInt(Constants.PLAN_ID,0);
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

    public void setUserRoleUniqueID(int approved)
    {
        sh.edit().putInt(Constants.USER_ROLE_UNIQUE_ID,approved).apply();
    }

    public int getUserRoleUniqueID()
    {
        return sh.getInt(Constants.USER_ROLE_UNIQUE_ID,0);
    }

    public void setLoginStatus(String loginStatus)
    {
        sh.edit().putString(Constants.LOGIN_STATUS,loginStatus).apply();
    }

    public String getLoginStatus()
    {
        return sh.getString(Constants.LOGIN_STATUS,"Logout");
    }

    public void setMeetingLoginStatus(String loginStatus)
    {
        sh.edit().putString(Constants.MEET_LOGIN_STATUS,loginStatus).apply();
    }

    public String getMeetingLoginStatus()
    {
        return sh.getString(Constants.MEET_LOGIN_STATUS,"Logout");
    }

    public void setLoginId(int id)
    {
        sh.edit().putInt(Constants.LOGIN_ID,id).apply();
    }

    public int getLoginId()
    {
        return sh.getInt(Constants.LOGIN_ID,0);
    }


    public void setMeetingId(int id)
    {
        sh.edit().putInt(Constants.MEETING_ID,id).apply();
    }

    public int getMeetingId()
    {
        return sh.getInt(Constants.MEETING_ID,0);
    }

    public void setMappingId(int mapid)
    {
        sh.edit().putInt(Constants.MAPPING_ID,mapid).apply();
    }

    public int getMappingId()
    {
        return sh.getInt(Constants.MAPPING_ID,0);
    }

}
