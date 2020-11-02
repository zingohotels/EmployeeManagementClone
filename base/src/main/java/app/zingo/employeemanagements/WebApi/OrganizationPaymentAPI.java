package app.zingo.employeemanagements.WebApi;

import app.zingo.employeemanagements.model.OrganizationPayments;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface OrganizationPaymentAPI {

    @POST("OrganizationPayments")
    Call<OrganizationPayments> addOrganizationPayments ( @Body OrganizationPayments details );
}
