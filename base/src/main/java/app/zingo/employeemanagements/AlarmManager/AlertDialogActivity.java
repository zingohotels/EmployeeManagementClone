package app.zingo.employeemanagements.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import app.zingo.employeemanagements.ui.newemployeedesign.EmployeeNewMainScreen;

public class AlertDialogActivity extends Activity
{

    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle ("Check Out Alert");
        builder.setMessage ("You didn`t put Check Out still now." + "do you want to do ?");
        builder.setCancelable (false);
        builder.setPositiveButton ("Check Out", new DialogInterface.OnClickListener () {
            public void onClick (DialogInterface dialog, int id) {
                dialog.cancel ();
                Intent intent = new Intent ( AlertDialogActivity.this, EmployeeNewMainScreen.class);
                intent.putExtra ("viewpager_position", 2);
                startActivity (intent);
            }
        });
        builder.setNegativeButton ("Cancel", new DialogInterface.OnClickListener () {
            public void onClick (DialogInterface dialog, int id) {
                dialog.cancel ();
                AlertDialogActivity.this.finish ();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
