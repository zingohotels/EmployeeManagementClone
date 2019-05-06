package app.zingo.employeemanagements.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserRoles implements Serializable {

    @SerializedName("UserRoleId")
    public int UserRoleId;

    @SerializedName("UserRoleUniqueId")
    public String UserRoleUniqueId;

    @SerializedName("UserRoleName")
    public String UserRoleName;


    public int getUserRoleId() {
        return UserRoleId;
    }

    public void setUserRoleId(int userRoleId) {
        UserRoleId = userRoleId;
    }

    public String getUserRoleUniqueId() {
        return UserRoleUniqueId;
    }

    public void setUserRoleUniqueId(String userRoleUniqueId) {
        UserRoleUniqueId = userRoleUniqueId;
    }

    public String getUserRoleName() {
        return UserRoleName;
    }

    public void setUserRoleName(String userRoleName) {
        UserRoleName = userRoleName;
    }
}
