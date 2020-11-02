package app.zingo.employeemanagements.WebApi;

import app.zingo.employeemanagements.model.ActivityLogs;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ActivityLogsAPI {

    @POST("ActivityLogs")
    Call< ActivityLogs > addActivityLogs( @Body ActivityLogs details );


}
