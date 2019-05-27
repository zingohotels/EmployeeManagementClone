package app.zingo.employeemanagements.WebApi;

import java.util.ArrayList;

import app.zingo.employeemanagements.Model.Departments;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by ZingoHotels Tech on 26-09-2018.
 */

public interface DepartmentApi {

    @POST("Departments")
    Call<Departments> addDepartments(@Body Departments details);

    @GET("Departments/{id}")
    Call<Departments> getDepartmentById(@Path("id") int id);

    @POST("Departments/GetDepartmentsByOrganizationId/{OrganizationId}")
    Call<ArrayList<Departments>> getDepartmentByOrganization(@Path("OrganizationId") int id);

}
