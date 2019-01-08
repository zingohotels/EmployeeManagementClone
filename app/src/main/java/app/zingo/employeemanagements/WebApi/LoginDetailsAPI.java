package app.zingo.employeemanagements.WebApi;

import java.util.ArrayList;

import app.zingo.employeemanagements.Model.LoginDetails;
import app.zingo.employeemanagements.Model.Meetings;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by ZingoHotels Tech on 28-09-2018.
 */

public interface LoginDetailsAPI {

    @GET("LoginDetails")
    Call<ArrayList<LoginDetails>> getLoginDetails();

    @POST("LoginDetails")
    Call<LoginDetails> addLogin(@Body LoginDetails details);

    @GET("LoginDetails/{id}")
    Call<LoginDetails> getLoginById(@Path("id") int id);

    @PUT("LoginDetails/{id}")
    Call<LoginDetails> updateLoginById(@Path("id") int id,@Body LoginDetails details);

    @GET("LoginDetails/GetLoginDetailsByEmployeeId/{EmployeeId}")
    Call<ArrayList<LoginDetails>> getLoginByEmployeeId(@Path("EmployeeId") int id);

    @POST("LoginDetails/GetLoginDetailsByEmployeeIdAndLoginDate")
    Call<ArrayList<LoginDetails>> getLoginByEmployeeIdAndDate(@Body LoginDetails details);
}
