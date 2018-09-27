package app.zingo.employeemanagements.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by ZingoHotels Tech on 27-09-2018.
 */

public class EmployeeImages implements Serializable {


 @SerializedName("EmployeeImageId")
 private int EmployeeImageId;

 @SerializedName("Image")
 private String Image;

 public int getEmployeeImageId() {
  return EmployeeImageId;
 }

 public void setEmployeeImageId(int employeeImageId) {
  EmployeeImageId = employeeImageId;
 }

 public String getImage() {
  return Image;
 }

 public void setImage(String image) {
  Image = image;
 }
}
