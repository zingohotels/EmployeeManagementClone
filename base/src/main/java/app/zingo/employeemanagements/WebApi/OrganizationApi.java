package app.zingo.employeemanagements.WebApi;

import java.util.ArrayList;

import app.zingo.employeemanagements.model.Organization;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by ZingoHotels Tech on 26-09-2018.
 */

public interface OrganizationApi {

    @POST("Organizations")
    Call< Organization > addOrganization( @Body Organization details );

    @PUT("Organizations/{id}")
    Call< Organization > updateOrganization( @Path ("id") int id , @Body Organization details );

    @GET("Organizations/{id}")
    Call<ArrayList< Organization >> getOrganizationById ( @Path ("id") int id );

    @GET("Organizations/GetOrganizationByHeadOrganizationId/{HeadOrganizationId}")
    Call<ArrayList< Organization >> getBranchesByHeadOrganizationId ( @Path ("HeadOrganizationId") int HeadOrganizationId );

    @GET("Organizations")
    Call<ArrayList< Organization >> getOrganization ( );

}
