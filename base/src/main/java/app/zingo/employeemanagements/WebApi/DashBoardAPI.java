package app.zingo.employeemanagements.WebApi;

import app.zingo.employeemanagements.Model.OrgDashBoard;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface DashBoardAPI {

    @GET("EmployeeDashboard/GetDashboardForOrganization/{OrganizationId}")
    Call<OrgDashBoard> getDashBoardByOrgId(@Path("OrganizationId") int id);

    /*@GET("Departments/{id}")
    Call<Departments> getDepartmentById(@Path("id") int id);*/
}
