package app.zingo.employeemanagements.WebApi;

import java.util.ArrayList;

import app.zingo.employeemanagements.Model.Organization;
import app.zingo.employeemanagements.Model.PaySlips;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by ZingoHotels Tech on 05-01-2019.
 */

public interface PayslipAPI {

    @POST("PaySlips")
    Call<PaySlips> addPaySlips(@Body PaySlips details);

    @GET("Organizations")
    Call<ArrayList<Organization>> getPaySlips();

    @GET("PaySlips/GetpaySlipByEmployeeIdAndMonthAndYear/{EmployeeId}/{Month}/{Year}")
    Call<ArrayList<PaySlips>> getPaySlipByMonYearEmp(@Path("EmployeeId") int EmployeeId, @Path("Month") String Month, @Path("Year") String Year);
}
