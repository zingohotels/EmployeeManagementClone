package app.zingo.employeemanagements.WebApi;

import app.zingo.employeemanagements.Model.Designations;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by ZingoHotels Tech on 27-09-2018.
 */

public interface DesignationsAPI {

    @POST("Designations")
    Call<Designations> addDesignations(@Body Designations details);

    @GET("Designations/{id}")
    Call<Designations> getDesignationById(@Path("id") int id);
}
