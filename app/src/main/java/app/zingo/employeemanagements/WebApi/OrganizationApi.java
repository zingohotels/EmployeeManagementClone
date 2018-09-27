package app.zingo.employeemanagements.WebApi;

import app.zingo.employeemanagements.Model.Departments;
import app.zingo.employeemanagements.Model.Organization;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by ZingoHotels Tech on 26-09-2018.
 */

public interface OrganizationApi {

    @POST("Organizations")
    Call<Organization> addOrganization(@Body Organization details);

    @GET("Organizations/{id}")
    Call<Organization> getOrganizationById(@Path("id") int id);

}
