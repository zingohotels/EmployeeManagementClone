package app.zingo.employeemanagements.WebApi;

import app.zingo.employeemanagements.Model.Departments;
import app.zingo.employeemanagements.Model.Designations;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by ZingoHotels Tech on 27-09-2018.
 */

public interface DesignationsAPI {

    @POST("Designations")
    Call<Designations> addDesignations(@Body Designations details);
}
