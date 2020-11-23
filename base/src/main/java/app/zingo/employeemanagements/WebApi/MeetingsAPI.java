package app.zingo.employeemanagements.WebApi;

import java.util.ArrayList;

import app.zingo.employeemanagements.model.Meetings;
import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by ZingoHotels Tech on 28-09-2018.
 */

public interface MeetingsAPI {

    @GET("Meetings")
    Call<ArrayList< Meetings >> getMeetingDetails ( );

    @POST("Meetings")
    Call< Meetings > addMeeting( @Body Meetings details );

    @GET("Meetings/{id}")
    Call< Meetings > getMeetingById ( @Path ("id") int id );

    @PUT("Meetings/{id}")
    Call< Meetings > updateMeetingById( @Path ("id") int id , @Body Meetings details );

    @GET("Meetings/GetMeetingsByEmployeeId/{EmployeeId}")
    Call<ArrayList< Meetings >> getMeetingsByEmployeeId ( @Path ("EmployeeId") int id );

    @POST("Meetings/GetMeetingsDetailsByEmployeeIdAndLoginDate")
    Call<ArrayList< Meetings >> getMeetingsByEmployeeIdAndDate( @Body Meetings details );

    /////RxJava
    @POST("Meetings/GetMeetingsDetailsByEmployeeIdAndLoginDate")
    Observable <ArrayList< Meetings >> getMeetingsByEmployeeIdAndDateRx( @Body Meetings details );
    /////RxJava
    @GET("Meetings/GetMeetingsByEmployeeId/{EmployeeId}")
    Observable<ArrayList< Meetings >> getMeetingsByEmployeeIdRx ( @Path ("EmployeeId") int id );
}
