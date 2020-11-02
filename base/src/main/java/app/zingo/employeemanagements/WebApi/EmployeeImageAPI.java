package app.zingo.employeemanagements.WebApi;

import app.zingo.employeemanagements.model.EmployeeImages;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by ZingoHotels Tech on 27-09-2018.
 */

public interface EmployeeImageAPI {

    @PUT("EmployeeImages/{id}")
    Call<EmployeeImages> updateEmployeeImage ( @Path ("id") int id , @Body EmployeeImages body );

    @POST("EmployeeImages")
    Call<EmployeeImages> addEmployeeImage ( @Body EmployeeImages details );
}
