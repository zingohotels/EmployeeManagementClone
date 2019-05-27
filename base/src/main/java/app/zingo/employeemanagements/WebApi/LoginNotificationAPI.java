package app.zingo.employeemanagements.WebApi;

import java.util.ArrayList;

import app.zingo.employeemanagements.Model.LoginDetailsNotificationManagers;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by ZingoHotels Tech on 17-10-2018.
 */

public interface LoginNotificationAPI {

    @POST("LoginDetailsNotificationManagers")
    Call<LoginDetailsNotificationManagers> saveLoginNotification(@Body LoginDetailsNotificationManagers details);

    @GET("LoginDetailsNotificationManager/GetLoginDetailsNotificationByManagerId/{ManagerId}")
    Call<ArrayList<LoginDetailsNotificationManagers>> getNotificationByManagerId(@Path("ManagerId") int ManagerId);

    @GET("LoginDetailsNotificationManager/GetLoginDetailsNotificationByEmployeeId/{EmployeeId}")
    Call<ArrayList<LoginDetailsNotificationManagers>> getNotificationByEmployeeId(@Path("EmployeeId") int EmployeeId);

    @POST("LoginDetailsNotificationManagers/SendLoginDetailsNotification")
    Call<ArrayList<String>> sendLoginNotification(@Body LoginDetailsNotificationManagers details);
}
