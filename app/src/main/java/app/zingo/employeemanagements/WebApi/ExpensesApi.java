package app.zingo.employeemanagements.WebApi;

import java.util.ArrayList;

import app.zingo.employeemanagements.Model.Employee;
import app.zingo.employeemanagements.Model.Expenses;
import app.zingo.employeemanagements.Model.Leaves;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ExpensesApi {

    @POST("Expenses")
    Call<Expenses> addExpenses(@Body Expenses details);

    @GET("Expenses/GetExpensesByOrganizationIdAndEmployeeId/{OrganizationId}/{EmployeeId}")
    Call<ArrayList<Expenses>> getExpenseByEmployeeIdAndOrganizationId(@Path("OrganizationId") int OrganizationId,@Path("EmployeeId") int EmployeeId);
}
