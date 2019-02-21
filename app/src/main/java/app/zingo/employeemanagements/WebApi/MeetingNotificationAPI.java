package app.zingo.employeemanagements.WebApi;

import java.util.ArrayList;

import app.zingo.employeemanagements.Model.Employee;
import app.zingo.employeemanagements.Model.LoginDetailsNotificationManagers;
import app.zingo.employeemanagements.Model.MeetingDetailsNotificationManagers;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by ZingoHotels Tech on 17-10-2018.
 */

public interface MeetingNotificationAPI {

    @POST("MeetingDetailsNotificationManagers")
    Call<MeetingDetailsNotificationManagers> saveMeetingNotification(@Body MeetingDetailsNotificationManagers details);

    @POST("MeetingDetailsNotificationManagers/SendMeetingDetailsNotification")
    Call<ArrayList<String>> sendMeetingNotification(@Body MeetingDetailsNotificationManagers details);

    @GET("MeetingDetailsNotificationManagers/GetMeetingDetailsNotificationByManagerId/{ManagerId}")
    Call<ArrayList<MeetingDetailsNotificationManagers>> getMeetingNotificationByManagerId(@Path("ManagerId") int ManagerId);

    @GET("MeetingDetailsNotificationManagers/GetMeetingDetailsNotificationByEmployeeId/{EmployeeId}")
    Call<ArrayList<MeetingDetailsNotificationManagers>> getMeetingNotificationByEmployeeId(@Path("EmployeeId") int EmployeeId);

}
