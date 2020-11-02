package app.zingo.employeemanagements.WebApi;

import app.zingo.employeemanagements.model.EmployeeDeviceMapping;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by ZingoHotels Tech on 26-10-2018.
 */

public interface EmployeeDeviceApi {

        @POST("EmployeeDeviceMappings")
        Call<EmployeeDeviceMapping> addProfileDevice ( @Body EmployeeDeviceMapping body );
}
