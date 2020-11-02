package app.zingo.employeemanagements.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ZingoHotels Tech on 25-10-2018.
 */

public class EmployeeDocuments implements Serializable {


    @SerializedName("EmployeeDocumentId")
    public int EmployeeDocumentId;

    @SerializedName("employees")
    public Employee employees;

    @SerializedName("EmployeeId")
    public int EmployeeId;

    @SerializedName("DocumentName")
    public String DocumentName;

    @SerializedName("DocumentType")
    public String DocumentType;

    @SerializedName("DocumentNumber")
    public String DocumentNumber;

    @SerializedName("DocumentStatus")
    public String DocumentStatus;

    @SerializedName("documentImages")
    public ArrayList<DocumentImages> documentImages;

    public int getEmployeeDocumentId() {
        return EmployeeDocumentId;
    }

    public void setEmployeeDocumentId(int employeeDocumentId) {
        EmployeeDocumentId = employeeDocumentId;
    }

    public Employee getEmployees() {
        return employees;
    }

    public void setEmployees( Employee employees) {
        this.employees = employees;
    }

    public int getEmployeeId() {
        return EmployeeId;
    }

    public void setEmployeeId(int employeeId) {
        EmployeeId = employeeId;
    }

    public String getDocumentName() {
        return DocumentName;
    }

    public void setDocumentName(String documentName) {
        DocumentName = documentName;
    }

    public String getDocumentType() {
        return DocumentType;
    }

    public void setDocumentType(String documentType) {
        DocumentType = documentType;
    }

    public String getDocumentNumber() {
        return DocumentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        DocumentNumber = documentNumber;
    }

    public String getDocumentStatus() {
        return DocumentStatus;
    }

    public void setDocumentStatus(String documentStatus) {
        DocumentStatus = documentStatus;
    }

    public ArrayList<DocumentImages> getDocumentImages() {
        return documentImages;
    }

    public void setDocumentImages(ArrayList<DocumentImages> documentImages) {
        this.documentImages = documentImages;
    }
}
