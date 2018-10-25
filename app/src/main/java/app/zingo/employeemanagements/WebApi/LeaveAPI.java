package app.zingo.employeemanagements.WebApi;

import java.util.ArrayList;

import app.zingo.employeemanagements.Model.Employee;
import app.zingo.employeemanagements.Model.Leaves;
import app.zingo.employeemanagements.Model.LoginDetails;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by ZingoHotels Tech on 17-10-2018.
 */

public interface LeaveAPI {

    @POST("Leaves")
    Call<Leaves> addLeave(@Body Leaves details);

    @GET("Leaves/GetAllLeavesByEmployeeId/{EmployeeId}")
    Call<ArrayList<Leaves>> getLeavesByEmployeeId(@Path("EmployeeId") int id);

    @GET("Leaves/GetAllLeavesByStatusandEmployeeId/{Status}/{EmployeeId}")
    Call<ArrayList<Leaves>> getLeavesByStatusAndEmployeeId(@Path("Status") String status,@Path("EmployeeId") int id);


}
