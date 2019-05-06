package app.zingo.employeemanagements.WebApi;

import java.util.ArrayList;

import app.zingo.employeemanagements.Model.HolidayList;
import app.zingo.employeemanagements.Model.Organization;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface OrganizationHolidayListsAPI {

    @POST("OrganizationHolidayLists")
    Call<HolidayList> addHolidays(@Body HolidayList details);

    @GET("OrganizationHolidayLists/GetHolidayListByOrganizationId/{OrganizationId}")
    Call<ArrayList<HolidayList>> getHolidaysByOrgId(@Path("OrganizationId") int OrganizationId);
}
