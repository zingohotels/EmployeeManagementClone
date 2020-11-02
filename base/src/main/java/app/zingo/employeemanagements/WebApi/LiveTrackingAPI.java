package app.zingo.employeemanagements.WebApi;

import java.util.ArrayList;

import app.zingo.employeemanagements.model.LiveTracking;
import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Streaming;

/**
 * Created by ZingoHotels Tech on 02-01-2019.
 */

public interface LiveTrackingAPI {

    @GET("LiveTrackingDetails/GetliveTrackingDetailsByEmployeeId/{EmployeeId}")
    Call<ArrayList<LiveTracking>> getLiveTrackingByEmployeeId ( @Path ("EmployeeId") int id );

    @Streaming
    @POST("LiveTrackingDetailsAsync/GetliveTrackingDetailsByEmployeeIdAndDate")
    Call<ArrayList<LiveTracking>> getLiveTrackingByEmployeeIdAndDate ( @Body LiveTracking details );

    @POST("LiveTrackingDetails/GetLiveTrackingByOrganizationIdAndDate")
    Call<ArrayList<LiveTracking>> getLiveTrackingByOrganzationIdAndDate ( @Body LiveTracking details );

    @POST("LiveTrackingDetails")
    Call<LiveTracking> addLiveTracking ( @Body LiveTracking details );

    @GET("LiveTrackingDetails/{id}")
    Call<LiveTracking> getLiveTrackingById ( @Path ("id") int id );

    @PUT("LiveTrackingDetails/{id}")
    Call<LiveTracking> updateLiveTrackingById ( @Path ("id") int id , @Body LiveTracking details );

    ////////RxJava
    @Streaming
    @POST("LiveTrackingDetailsAsync/GetliveTrackingDetailsByEmployeeIdAndDate")
    Observable <ArrayList<LiveTracking>> getLiveTrackingByEmployeeIdAndDateRx ( @Body LiveTracking details );
}
