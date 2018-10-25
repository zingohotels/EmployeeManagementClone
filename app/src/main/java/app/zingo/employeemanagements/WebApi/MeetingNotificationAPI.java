package app.zingo.employeemanagements.WebApi;

import app.zingo.employeemanagements.Model.Employee;
import app.zingo.employeemanagements.Model.MeetingDetailsNotificationManagers;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by ZingoHotels Tech on 17-10-2018.
 */

public interface MeetingNotificationAPI {

    @POST("MeetingDetailsNotificationManagers")
    Call<MeetingDetailsNotificationManagers> saveMeetingNotification(@Body MeetingDetailsNotificationManagers details);

}
