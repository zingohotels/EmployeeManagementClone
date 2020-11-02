package app.zingo.employeemanagements.WebApi;
import java.util.ArrayList;

import app.zingo.employeemanagements.model.TaskNotificationManagers;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface TaskNotificationAPI {
    @POST("TaskNotificationManagers")
    Call<TaskNotificationManagers> saveTask ( @Body TaskNotificationManagers details );

    @POST("TaskNotificationManagers/SendTaskNotification")
    Call<ArrayList<String>> sendTask ( @Body TaskNotificationManagers details );
}
