package app.zingo.employeemanagements.WebApi;

import java.util.ArrayList;

import app.zingo.employeemanagements.Model.LoginDetailsNotificationManagers;
import app.zingo.employeemanagements.Model.MeetingDetailsNotificationManagers;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by ZingoHotels Tech on 17-10-2018.
 */

public interface LoginNotificationAPI {

    @POST("LoginDetailsNotificationManagers")
    Call<LoginDetailsNotificationManagers> saveLoginNotification(@Body LoginDetailsNotificationManagers details);

    @POST("LoginDetailsNotificationManagers/SendLoginDetailsNotification")
    Call<ArrayList<String>> sendLoginNotification(@Body LoginDetailsNotificationManagers details);
}
