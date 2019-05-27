package app.zingo.employeemanagements.WebApi;

import java.util.ArrayList;

import app.zingo.employeemanagements.Model.Organization;
import app.zingo.employeemanagements.Model.OrganizationPayments;
import app.zingo.employeemanagements.Model.ResellerProfiles;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ResellerAPI {

    @POST("ResellerProfiles")
    Call<ResellerProfiles> addResellers(@Body ResellerProfiles details);

    @POST("ResellerProfiles/GetResellerProfilesByEmail")
    Call<ArrayList<ResellerProfiles>> getResellerByEmail(@Body ResellerProfiles userProfile);

    @GET("ResellerProfiles/GetResellerProfilesByMobileNumber/{MobileNumber}")
    Call<ArrayList<ResellerProfiles>> getResellerByPhone(@Path("MobileNumber") String phone);

    @GET("ResellerProfiles/GetResellerProfilesByUserName/{UserName}")
    Call<ArrayList<ResellerProfiles>> getResellerByUserName(@Path("UserName") String UserName);

    @POST("ResellerProfiles/GetResellerProfilesByUserNameAndPassword")
    Call<ArrayList<ResellerProfiles>> getResellerProfilesforLogin(@Body ResellerProfiles body);

    @PUT("ResellerProfiles/{id}")
    Call<ResellerProfiles> updateResellerProfiles(@Path("id") int id, @Body ResellerProfiles body);

    @GET("ResellerProfiles/{id}")
    Call<ResellerProfiles> getResellerProfileById(@Path("id") int id);

    @GET("Organizations/GetOrganizationByResellerId/{ResellerProfileId}")
    Call<ArrayList<Organization>> getOrganizationBySellerId(@Path("ResellerProfileId") int ResellerProfileId);

    @GET("OrganizationPayments/GetOrganizationsPaymentByResellerId/{ResellerProfileId}")
    Call<ArrayList<OrganizationPayments>> getOrganizationPaymentBySellerId(@Path("ResellerProfileId") int ResellerProfileId);
}
