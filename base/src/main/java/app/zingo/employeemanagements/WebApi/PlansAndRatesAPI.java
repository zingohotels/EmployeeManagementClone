package app.zingo.employeemanagements.WebApi;

import java.util.ArrayList;

import app.zingo.employeemanagements.model.Plans;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by ZingoHotels Tech on 04-01-2019.
 */

public interface PlansAndRatesAPI {

    @POST("Plans")
    Call<Plans> addPlans ( @Body Plans details );

    @GET("Plans/{id}")
    Call<Plans> getPlansById ( @Path ("id") int id );

    @GET("Plans")
    Call<ArrayList<Plans>> getPlans ( );
}
