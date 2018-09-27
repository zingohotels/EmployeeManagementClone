package app.zingo.employeemanagements.WebApi;

import java.util.ArrayList;

import app.zingo.employeemanagements.Model.Designations;
import app.zingo.employeemanagements.Model.Employee;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by ZingoHotels Tech on 27-09-2018.
 */

public interface EmployeeApi {

    @POST("Employees")
    Call<Employee> addEmployee(@Body Employee details);

    @GET("Employees/GetEmployeeByPhoneNumber/{PhoneNumber}")
    Call<ArrayList<Employee>> getUserByPhone(@Path("PhoneNumber") String phone);

    @POST("Employees/GetEmployeeByEmail")
    Call<ArrayList<Employee>> getUserByEmail(@Body Employee userProfile);


    @POST("Employees/GetEmployeeByEmailAndPassword")
    Call<ArrayList<Employee>> getEmployeeforLogin(@Body Employee body);

    @GET("Employees/{id}")
    Call<Employee> getProfileById(@Path("id") int id);
}
