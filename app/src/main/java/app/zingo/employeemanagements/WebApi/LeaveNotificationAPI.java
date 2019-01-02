package app.zingo.employeemanagements.WebApi;

import java.util.ArrayList;

import app.zingo.employeemanagements.Model.LeaveNotificationManagers;
import app.zingo.employeemanagements.Model.Leaves;
import app.zingo.employeemanagements.Model.MeetingDetailsNotificationManagers;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by ZingoHotels Tech on 26-10-2018.
 */

public interface LeaveNotificationAPI {


    @POST("LeaveNotificationManagers")
    Call<LeaveNotificationManagers> saveLeave(@Body LeaveNotificationManagers details);

    @POST("LeaveNotificationManagers/SendLeaveDetailsNotification")
    Call<ArrayList<String>> sendLeaveNotification(@Body LeaveNotificationManagers details);

}
