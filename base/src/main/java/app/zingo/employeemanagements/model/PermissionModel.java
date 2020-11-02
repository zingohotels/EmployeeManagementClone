package app.zingo.employeemanagements.model;

public class PermissionModel {

    private String permissionName;
    private String permissionDescription;
    private int permissionImage;

    public PermissionModel ( String permissionName , String permissionDescription , int permissionImage ) {
        this.permissionName = permissionName;
        this.permissionDescription = permissionDescription;
        this.permissionImage = permissionImage;
    }

    public String getPermissionName ( ) {
        return permissionName;
    }

    public void setPermissionName ( String permissionName ) {
        this.permissionName = permissionName;
    }

    public String getPermissionDescription ( ) {
        return permissionDescription;
    }

    public void setPermissionDescription ( String permissionDescription ) {
        this.permissionDescription = permissionDescription;
    }

    public int getPermissionImage ( ) {
        return permissionImage;
    }

    public void setPermissionImage ( int permissionImage ) {
        this.permissionImage = permissionImage;
    }
}
