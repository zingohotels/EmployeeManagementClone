package app.zingo.employeemanagements.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by ZingoHotels Tech on 27-09-2018.
 */

public class EmployeeImages implements Serializable {


 @SerializedName("EmployeeImageId")
 private int EmployeeImageId;

 @SerializedName("employee")
 private Employee employee;

 @SerializedName("EmployeeId")
 private int EmployeeId;

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

 public Employee getEmployee() {
  return employee;
 }

 public void setEmployee(Employee employee) {
  this.employee = employee;
 }

 public int getEmployeeId() {
  return EmployeeId;
 }

 public void setEmployeeId(int employeeId) {
  EmployeeId = employeeId;
 }
}
