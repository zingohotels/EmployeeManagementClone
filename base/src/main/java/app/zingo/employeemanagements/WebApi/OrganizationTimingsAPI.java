package app.zingo.employeemanagements.WebApi;

import java.util.ArrayList;

import app.zingo.employeemanagements.Model.WorkingDay;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface OrganizationTimingsAPI {

    @POST("OrganizationTimings")
    Call<WorkingDay> addOrganizationTimings(@Body WorkingDay details);

    @PUT("OrganizationTimings/{id}")
    Call<WorkingDay> updateOrganizationTimings(@Path("id") int id, @Body WorkingDay details);

    @GET("OrganizationTimings/{id}")
    Call<WorkingDay> getOrganizationTimings(@Path("id") int id);

    @GET("OrganizationTimings/GetOrganizationTimingsByOrganizationId/{OrganizationId}")
    Call<ArrayList<WorkingDay>> getOrganizationTimingByOrgId(@Path("OrganizationId") int OrganizationId);

}
