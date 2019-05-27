package app.zingo.employeemanagements.WebApi;

import java.util.ArrayList;

import app.zingo.employeemanagements.Model.Customer;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface CustomerAPI {

    @POST("Customers")
    Call<Customer> addCustomer(@Body Customer details);

    @PUT("Customers/{id}")
    Call<Customer> updateCustomer(@Path("id") int id, @Body Customer details);

    @GET("Customers/GetCustomersByOrganizationId/{OrganizationId}")
    Call<ArrayList<Customer>> getCustomerByOrganizationId(@Path("OrganizationId") int OrganizationId);

}
