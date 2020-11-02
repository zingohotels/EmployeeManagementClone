package app.zingo.employeemanagements.WebApi;

import java.util.ArrayList;

import app.zingo.employeemanagements.model.Expenses;
import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ExpensesApi {

    @POST("Expenses")
    Call< Expenses > addExpenses( @Body Expenses details );

    @GET("Expenses/GetExpensesByOrganizationIdAndEmployeeId/{OrganizationId}/{EmployeeId}")
    Call<ArrayList< Expenses >> getExpenseByEmployeeIdAndOrganizationId ( @Path ("OrganizationId") int OrganizationId , @Path ("EmployeeId") int EmployeeId );

    @GET("Expenses/GetExpensesByOrganizationIdAndManagerId/{OrganizationId}/{ManagerId}")
    Call<ArrayList< Expenses >> getExpenseByManagerIdAndOrganizationId ( @Path ("OrganizationId") int OrganizationId , @Path ("ManagerId") int EmployeeId );

    @GET("Expenses/GetExpensesByOrganizationId/{OrganizationId}")
    Call<ArrayList< Expenses >> getExpenseByOrganizationId ( @Path ("OrganizationId") int OrganizationId );

    @PUT("Expenses/{id}")
    Call< Expenses > updateExpenses( @Path ("id") int id , @Body Expenses details );

    ///RxJava
    @GET("Expenses/GetExpensesByOrganizationIdAndEmployeeId/{OrganizationId}/{EmployeeId}")
    Observable <ArrayList< Expenses >> getExpenseByEmployeeIdAndOrganizationIdRx ( @Path ("OrganizationId") int OrganizationId , @Path ("EmployeeId") int EmployeeId );

}
