package app.zingo.employeemanagements.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TaskAdminData implements Serializable {

    @SerializedName("Employee")
    public Employee employee;

    @SerializedName("Tasks")
    app.zingo.employeemanagements.Model.Tasks Tasks;

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public app.zingo.employeemanagements.Model.Tasks getTasks() {
        return Tasks;
    }

    public void setTasks(app.zingo.employeemanagements.Model.Tasks tasks) {
        Tasks = tasks;
    }
}
