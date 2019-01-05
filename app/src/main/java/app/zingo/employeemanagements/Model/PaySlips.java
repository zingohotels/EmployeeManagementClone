package app.zingo.employeemanagements.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by ZingoHotels Tech on 05-01-2019.
 */

public class PaySlips implements Serializable {

    @SerializedName("PaySlipId")
    public int PaySlipId;

    @SerializedName("EmployeeName")
    public String EmployeeName;

    @SerializedName("DesignationName")
    public String DesignationName;

    @SerializedName("employee")
    public Employee employee;

    @SerializedName("EmployeeId")
    public int EmployeeId;

    @SerializedName("PANnumber")
    public String PANnumber;

    @SerializedName("Month")
    public String Month;

    @SerializedName("Year")
    public String Year;

    @SerializedName("DateOfJoining")
    public String DateOfJoining;

    @SerializedName("DepartmentName")
    public String DepartmentName;

    @SerializedName("LeaveTaken")
    public int LeaveTaken;

    @SerializedName("BasicPay")
    public double BasicPay;

    @SerializedName("RentAllowance")
    public double RentAllowance;

    @SerializedName("ConveyAllowance")
    public double ConveyAllowance;

    @SerializedName("MedicalAllowance")
    public double MedicalAllowance;

    @SerializedName("PF")
    public double PF;

    @SerializedName("ESI")
    public double ESI;

    @SerializedName("Loan")
    public double Loan;

    @SerializedName("ProfessionalTax")
    public double ProfessionalTax;

    @SerializedName("Leaves")
    public double Leaves;

    @SerializedName("Advance")
    public double Advance;

    @SerializedName("CreatedBy")
    public String CreatedBy;

    @SerializedName("CreatedDate")
    public String CreatedDate;

    public int getPaySlipId() {
        return PaySlipId;
    }

    public void setPaySlipId(int paySlipId) {
        PaySlipId = paySlipId;
    }

    public String getEmployeeName() {
        return EmployeeName;
    }

    public void setEmployeeName(String employeeName) {
        EmployeeName = employeeName;
    }

    public String getDesignationName() {
        return DesignationName;
    }

    public void setDesignationName(String designationName) {
        DesignationName = designationName;
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

    public String getPANnumber() {
        return PANnumber;
    }

    public void setPANnumber(String PANnumber) {
        this.PANnumber = PANnumber;
    }

    public String getMonth() {
        return Month;
    }

    public void setMonth(String month) {
        Month = month;
    }

    public String getYear() {
        return Year;
    }

    public void setYear(String year) {
        Year = year;
    }

    public String getDateOfJoining() {
        return DateOfJoining;
    }

    public void setDateOfJoining(String dateOfJoining) {
        DateOfJoining = dateOfJoining;
    }

    public String getDepartmentName() {
        return DepartmentName;
    }

    public void setDepartmentName(String departmentName) {
        DepartmentName = departmentName;
    }

    public int getLeaveTaken() {
        return LeaveTaken;
    }

    public void setLeaveTaken(int leaveTaken) {
        LeaveTaken = leaveTaken;
    }

    public double getBasicPay() {
        return BasicPay;
    }

    public void setBasicPay(double basicPay) {
        BasicPay = basicPay;
    }

    public double getRentAllowance() {
        return RentAllowance;
    }

    public void setRentAllowance(double rentAllowance) {
        RentAllowance = rentAllowance;
    }

    public double getConveyAllowance() {
        return ConveyAllowance;
    }

    public void setConveyAllowance(double conveyAllowance) {
        ConveyAllowance = conveyAllowance;
    }

    public double getMedicalAllowance() {
        return MedicalAllowance;
    }

    public void setMedicalAllowance(double medicalAllowance) {
        MedicalAllowance = medicalAllowance;
    }

    public double getPF() {
        return PF;
    }

    public void setPF(double PF) {
        this.PF = PF;
    }

    public double getESI() {
        return ESI;
    }

    public void setESI(double ESI) {
        this.ESI = ESI;
    }

    public double getLoan() {
        return Loan;
    }

    public void setLoan(double loan) {
        Loan = loan;
    }

    public double getProfessionalTax() {
        return ProfessionalTax;
    }

    public void setProfessionalTax(double professionalTax) {
        ProfessionalTax = professionalTax;
    }

    public double getLeaves() {
        return Leaves;
    }

    public void setLeaves(double leaves) {
        Leaves = leaves;
    }

    public double getAdvance() {
        return Advance;
    }

    public void setAdvance(double advance) {
        Advance = advance;
    }

    public String getCreatedBy() {
        return CreatedBy;
    }

    public void setCreatedBy(String createdBy) {
        CreatedBy = createdBy;
    }

    public String getCreatedDate() {
        return CreatedDate;
    }

    public void setCreatedDate(String createdDate) {
        CreatedDate = createdDate;
    }
}
