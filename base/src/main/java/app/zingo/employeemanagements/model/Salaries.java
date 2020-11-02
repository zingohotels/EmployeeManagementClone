package app.zingo.employeemanagements.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by ZingoHotels Tech on 05-01-2019.
 */

public class Salaries implements Serializable {

    @SerializedName("SalaryId")
    public int SalaryId;

    @SerializedName("employee")
    public Employee employee;

    @SerializedName("EmployeeId")
    public int EmployeeId;

    @SerializedName("HRA")
    public double HRA;

    @SerializedName("Basic")
    public double Basic;

    @SerializedName("PF")
    public double PF;

    @SerializedName("AdvanceBonus")
    public double AdvanceBonus;

    @SerializedName("Conveyance")
    public double Conveyance;

    @SerializedName("Medical")
    public double Medical;

    @SerializedName("PD")
    public double PD;

    @SerializedName("Exgratia")
    public double Exgratia;

    @SerializedName("ShiftAllowance")
    public double ShiftAllowance;

    @SerializedName("OtherAllowance")
    public double OtherAllowance;

    @SerializedName("TravelAllowance")
    public double TravelAllowance;

    public int getSalaryId() {
        return SalaryId;
    }

    public void setSalaryId(int salaryId) {
        SalaryId = salaryId;
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

    public double getHRA() {
        return HRA;
    }

    public void setHRA(double HRA) {
        this.HRA = HRA;
    }

    public double getBasic() {
        return Basic;
    }

    public void setBasic(double basic) {
        Basic = basic;
    }

    public double getPF() {
        return PF;
    }

    public void setPF(double PF) {
        this.PF = PF;
    }

    public double getAdvanceBonus() {
        return AdvanceBonus;
    }

    public void setAdvanceBonus(double advanceBonus) {
        AdvanceBonus = advanceBonus;
    }

    public double getConveyance() {
        return Conveyance;
    }

    public void setConveyance(double conveyance) {
        Conveyance = conveyance;
    }

    public double getMedical() {
        return Medical;
    }

    public void setMedical(double medical) {
        Medical = medical;
    }

    public double getPD() {
        return PD;
    }

    public void setPD(double PD) {
        this.PD = PD;
    }

    public double getExgratia() {
        return Exgratia;
    }

    public void setExgratia(double exgratia) {
        Exgratia = exgratia;
    }

    public double getShiftAllowance() {
        return ShiftAllowance;
    }

    public void setShiftAllowance(double shiftAllowance) {
        ShiftAllowance = shiftAllowance;
    }

    public double getOtherAllowance() {
        return OtherAllowance;
    }

    public void setOtherAllowance(double otherAllowance) {
        OtherAllowance = otherAllowance;
    }

    public double getTravelAllowance() {
        return TravelAllowance;
    }

    public void setTravelAllowance(double travelAllowance) {
        TravelAllowance = travelAllowance;
    }
}
