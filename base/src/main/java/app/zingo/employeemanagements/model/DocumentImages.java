package app.zingo.employeemanagements.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by ZingoHotels Tech on 25-10-2018.
 */

public class DocumentImages implements Serializable {

    @SerializedName("DocumentImagesId")
    public int DocumentImagesId;

    @SerializedName("Image")
    public String Image;

    @SerializedName("EmployeeDocumentId")
    public int EmployeeDocumentId;

    @SerializedName("employeeDocuments")
    public EmployeeDocuments employeeDocuments;

    public int getDocumentImagesId() {
        return DocumentImagesId;
    }

    public void setDocumentImagesId(int documentImagesId) {
        DocumentImagesId = documentImagesId;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public int getEmployeeDocumentId() {
        return EmployeeDocumentId;
    }

    public void setEmployeeDocumentId(int employeeDocumentId) {
        EmployeeDocumentId = employeeDocumentId;
    }

    public EmployeeDocuments getEmployeeDocuments() {
        return employeeDocuments;
    }

    public void setEmployeeDocuments(EmployeeDocuments employeeDocuments) {
        this.employeeDocuments = employeeDocuments;
    }
}
