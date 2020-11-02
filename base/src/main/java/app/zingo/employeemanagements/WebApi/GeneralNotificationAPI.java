package app.zingo.employeemanagements.WebApi;

import java.util.ArrayList;

import app.zingo.employeemanagements.model.GeneralNotification;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface GeneralNotificationAPI {


    @POST("GeneralNotificationManagers/SendGeneralNotificationToEmployeeId")
    Call<ArrayList<String>> sendGeneralNotification( @Body GeneralNotification details );

    @GET ("GeneralNotificationManagers")
    Call < ArrayList< GeneralNotification > > getGeneralNotification ( );

    @POST ("GeneralNotificationManagers")
    Call < GeneralNotification > saveGeneralNotification( @Body GeneralNotification details);

}
