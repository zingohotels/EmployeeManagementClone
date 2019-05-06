package app.zingo.employeemanagements.WebApi;

import java.util.ArrayList;

import app.zingo.employeemanagements.Model.ActivityLogs;
import app.zingo.employeemanagements.Model.Departments;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ActivityLogsAPI {

    @POST("ActivityLogs")
    Call<ActivityLogs> addActivityLogs(@Body ActivityLogs details);


}
