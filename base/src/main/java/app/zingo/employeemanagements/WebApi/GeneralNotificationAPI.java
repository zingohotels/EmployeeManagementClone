package app.zingo.employeemanagements.WebApi;

import java.util.ArrayList;

import app.zingo.employeemanagements.Model.GeneralNotification;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface GeneralNotificationAPI {


    @POST("GeneralNotificationManagers/SendGeneralNotificationToEmployeeId")
    Call<ArrayList<String>> sendGeneralNotification(@Body GeneralNotification details);

}
