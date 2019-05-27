package app.zingo.employeemanagements.WebApi;

import java.util.ArrayList;

import app.zingo.employeemanagements.Model.Tasks;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by ZingoHotels Tech on 05-01-2019.
 */

public interface TasksAPI {

    @POST("Tasks")
    Call<Tasks> addTasks(@Body Tasks details);

    @GET("Tasks")
    Call<ArrayList<Tasks>> getTasks();

    @PUT("Tasks/{id}")
    Call<Tasks> updateTasks(@Path("id") int id, @Body Tasks details);

    @DELETE("Tasks/{id}")
    Call<Tasks> deleteTasks(@Path("id") int id);

    @GET("Tasks/GetTasksByEmployeeId/{EmployeeId}")
    Call<ArrayList<Tasks>> getTasksByEmployeeId(@Path("EmployeeId") int EmployeeId);

    @GET("Tasks/GetTasksByEmployeeIdAndStatus/{EmployeeId}/{Status}")
    Call<ArrayList<Tasks>> getTasksByEmployeeIdStatus(@Path("EmployeeId") int EmployeeId, @Path("Status") String Status);



}
