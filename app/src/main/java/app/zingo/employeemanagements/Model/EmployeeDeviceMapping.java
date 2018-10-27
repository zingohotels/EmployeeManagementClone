package app.zingo.employeemanagements.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by ZingoHotels Tech on 26-10-2018.
 */

public class EmployeeDeviceMapping implements Serializable {

    @SerializedName("EmployeeDeviceMappingId")
    public int EmployeeDeviceMappingId;

    @SerializedName("DeviceId")
    public String DeviceId;

    @SerializedName("EmployeeId")
    public int EmployeeId;


    public int getEmployeeDeviceMappingId() {
        return EmployeeDeviceMappingId;
    }

    public void setEmployeeDeviceMappingId(int employeeDeviceMappingId) {
        EmployeeDeviceMappingId = employeeDeviceMappingId;
    }

    public String getDeviceId() {
        return DeviceId;
    }

    public void setDeviceId(String deviceId) {
        DeviceId = deviceId;
    }

    public int getEmployeeId() {
        return EmployeeId;
    }

    public void setEmployeeId(int employeeId) {
        EmployeeId = employeeId;
    }
}
