package app.zingo.employeemanagements.WebApi;

import java.util.ArrayList;

import app.zingo.employeemanagements.Model.LoginDetails;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by ZingoHotels Tech on 28-09-2018.
 */

public interface LoginDetailsAPI {

    @GET("LoginDetails")
    Call<ArrayList<LoginDetails>> getLoginDetails();

    @POST("LoginDetails")
    Call<LoginDetails> addLogin(@Body LoginDetails details);

    @GET("LoginDetails/GetLoginDetailsByEmployeeId/{EmployeeId}")
    Call<ArrayList<LoginDetails>> getLoginByEmployeeId(@Path("EmployeeId") int id);
}
