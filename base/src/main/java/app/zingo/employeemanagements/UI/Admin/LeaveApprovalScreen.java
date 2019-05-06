package app.zingo.employeemanagements.UI.Admin;

import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import app.zingo.employeemanagements.Model.LeaveNotificationManagers;
import app.zingo.employeemanagements.base.R;

public class LeaveApprovalScreen extends AppCompatActivity {

    TextInputEditText mFrom,mTo;
    TextView mEmployeeName;
    EditText mComment,mReason;
    Spinner mLType,mLStatus;
    AppCompatButton mLReply;

    LeaveNotificationManagers leaveNotificationManagers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{

            setContentView(R.layout.activity_leave_approval_screen);

            mEmployeeName = findViewById(R.id.employee_name);
            mFrom = findViewById(R.id.from_date);
            mTo = findViewById(R.id.to_date);
            mComment = findViewById(R.id.leave_comment);
            mReason = findViewById(R.id.leave_reason);
            mLType = findViewById(R.id.leave_type_spinner);
            mLStatus = findViewById(R.id.leave_status_spinner);
            mLReply = findViewById(R.id.leave_reply);


            Bundle bundle = getIntent().getExtras();

            if(bundle!=null){

                leaveNotificationManagers = (LeaveNotificationManagers)bundle.getSerializable("LeaveNotification");
            }


            if(leaveNotificationManagers!=null){
                setLeaveNotificationManagers(leaveNotificationManagers);
            }


        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void setLeaveNotificationManagers(LeaveNotificationManagers leaveNotificationManagers) {

        mEmployeeName.setText(""+leaveNotificationManagers.getEmployeeName());

    }
}
