package app.zingo.employeemanagements.utils;
import android.app.ProgressDialog;
import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import app.zingo.employeemanagements.base.R;
import app.zingo.employeemanagements.model.LiveTracking;
import app.zingo.employeemanagements.model.LoginDetails;
import app.zingo.employeemanagements.model.Meetings;
import app.zingo.employeemanagements.model.ReportDataEmployee;
import app.zingo.employeemanagements.model.ReportDataModel;
import app.zingo.employeemanagements.model.Tasks;
import app.zingo.employeemanagements.ui.NewAdminDesigns.EmployeeDashBoardAdminView;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Alignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class BaseActivity extends AppCompatActivity {
    public static final String TAG = "BaseActivity===>";
    ProgressDialog progressDialog;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog (BaseActivity.this);
    }

    public static double calculateDistance(double srcLat, double srcLng, double desLat, double desLng) {
        double earthRadius = 3958.75;
        double dLat = Math.toRadians(desLat - srcLat);
        double dLng = Math.toRadians(desLng - srcLng);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(srcLat))
                * Math.cos(Math.toRadians(desLat)) * Math.sin(dLng / 2)
                * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = earthRadius * c;
        double meterConversion = 1609;
        return (dist * meterConversion);
    }

    public static double metersToMiles(double meters) {
        return meters / 1609.3440057765;
    }

    public static double milesToMeters(double miles) {
        return miles / 0.0006213711;
    }

    public static double metersToKms(double meters) {
        return meters / 1000.00;
    }

    public static long meetingTimeDifference(ArrayList < Meetings > list , String comDate , long diffHrs){
        long meetingDiff = 0;
        diffHrs = 0;
        for ( Meetings meetings:list) {
            if(meetings.getStartTime()!=null&&meetings.getEndTime()!=null){

                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy hh:mm a");
                Date fd=null,td=null;
                String logoutT = meetings.getEndTime();
                String loginT = meetings.getStartTime();
                if(loginT==null||loginT.isEmpty()){
                    loginT = comDate +" 00:00 am";
                }

                if(logoutT==null||logoutT.isEmpty()){
                    logoutT = comDate  +" "+new SimpleDateFormat("hh:mm a").format(new Date()) ;
                }

                try {
                    fd = sdf.parse(""+loginT);
                    td = sdf.parse(""+logoutT);
                    long diff = td.getTime() - fd.getTime();
                    diffHrs = diffHrs+diff;

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        meetingDiff =diffHrs;
        return  meetingDiff;
    }

    public static long workingTimeDifference ( ArrayList < LoginDetails > list , String loginTime , String comDate , long diffHrs) {
       long workingDiff = 0;
        diffHrs= 0;

        for ( LoginDetails lg:list) {
            if((loginTime==null||loginTime.isEmpty())&&(lg.getLogOutTime()==null||lg.getLogOutTime().isEmpty())){
            }else{
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy hh:mm a");
                Date fd=null,td=null;
                String logoutT = lg.getLogOutTime();
                String loginT = lg.getLoginTime();
                if(loginT==null||loginT.isEmpty()){
                    loginT = comDate +" 00:00 am";
                }
                if(logoutT==null||logoutT.isEmpty()){
                    logoutT = comDate  +" "+new SimpleDateFormat("hh:mm a").format(new Date()) ;
                }
                try {
                    fd = sdf.parse(""+loginT);
                    td = sdf.parse(""+logoutT);
                    long diff = td.getTime() - fd.getTime();
                    diffHrs = diffHrs+diff;

                } catch ( ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        workingDiff =diffHrs;
        return  workingDiff;
    }

    public static long taskTimeDifference ( ArrayList < Tasks > list , String comDate , long diffHrs) {
        long workingDiff = 0;
        diffHrs= 0;

        for ( Tasks tasks:list) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            Date fd=null,td=null;
            String startDate = tasks.getStartDate ();
            String endDate = tasks.getEndDate ();
            if(endDate==null||endDate.isEmpty()){
                endDate = comDate +" 00:00";
            }
            if(startDate==null||startDate.isEmpty()){
                startDate = comDate  +" "+new SimpleDateFormat("hh:mm").format(new Date()) ;
            }
            try {
                fd = sdf.parse(""+endDate);
                td = sdf.parse(""+startDate);
                long diff = td.getTime() - fd.getTime();
                diffHrs = diffHrs+diff;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        workingDiff =diffHrs;
        return  workingDiff;
    }

    public static SpannableString workingTime ( long diffHrs , String status ) {
        SpannableString workingTime = null;
        if ( status.equalsIgnoreCase ( "Working" )||status.equalsIgnoreCase ( "Task" ) ||status.equalsIgnoreCase ( "Meetings" ) ||status.equalsIgnoreCase ( "Logout" ) ) {
            int minutes = ( int ) ( ( diffHrs / ( 1000 * 60 ) ) % 60 );
            int hours = ( int ) ( ( diffHrs / ( 1000 * 60 * 60 ) ) % 24 );
            int days = ( int ) ( ( diffHrs / ( 1000 * 60 * 60 * 24 ) ) );
            DecimalFormat df = new DecimalFormat ( "00" );
            String s = String.format ( "%02d" , hours ) + " hr " + String.format ( "%02d" , minutes ) + " mins";
            workingTime = new SpannableString ( s );
            workingTime.setSpan ( new RelativeSizeSpan ( 1f ) , 0 , 2 , 0 ); // set size
            workingTime.setSpan ( new ForegroundColorSpan ( Color.RED ) , 0 , 2 , 0 );// set color
            workingTime.setSpan ( new RelativeSizeSpan ( 1f ) , 6 , 8 , 0 ); // set size
            workingTime.setSpan ( new ForegroundColorSpan ( Color.RED ) , 6 , 8 , 0 );// set color
        }else{
            String s= String.format("%02d", 00) +" hr "+String.format("%02d", 00)+" mins";
            workingTime = new SpannableString ( s );
            workingTime.setSpan ( new RelativeSizeSpan ( 1f ) , 0 , 2 , 0 ); // set size
            workingTime.setSpan ( new ForegroundColorSpan ( Color.RED ) , 0 , 2 , 0 );// set color
            workingTime.setSpan ( new RelativeSizeSpan ( 1f ) , 6 , 8 , 0 ); // set size
            workingTime.setSpan ( new ForegroundColorSpan ( Color.RED ) , 6 , 8 , 0 );// set color
        }
        return workingTime;
    }

    public static SpannableString emptyTime(long diffHrs){
        SpannableString emptyTime = null;
        String s= String.format("%02d", 00) +" hr "+String.format("%02d", 00)+" mins";
        emptyTime = new SpannableString ( s );
        emptyTime.setSpan ( new RelativeSizeSpan ( 1f ) , 0 , 2 , 0 ); // set size
        emptyTime.setSpan ( new ForegroundColorSpan ( Color.RED ) , 0 , 2 , 0 );// set color
        emptyTime.setSpan ( new RelativeSizeSpan ( 1f ) , 6 , 8 , 0 ); // set size
        emptyTime.setSpan ( new ForegroundColorSpan ( Color.RED ) , 6 , 8 , 0 );// set color
        return emptyTime;
    }

    public static SpannableString averageMeetingTime (ArrayList<Meetings> list, long diffHrs , String status ) {
        SpannableString averageWorkingTime = null;
        if ( status.equalsIgnoreCase ( "Meetings" ) ) {
            long avgMeetingdiff = diffHrs / list.size ( );
            int avgminutes = ( int ) ( ( avgMeetingdiff / ( 1000 * 60 ) ) % 60 );
            int avghours = ( int ) ( ( avgMeetingdiff / ( 1000 * 60 * 60 ) ) % 24 );
            String s = String.format ( "%02d" , avghours ) + " hr " + String.format ( "%02d" , avgminutes ) + " mins";
            averageWorkingTime = new SpannableString ( s );
            averageWorkingTime.setSpan ( new RelativeSizeSpan ( 1f ) , 0 , 2 , 0 ); // set size
            averageWorkingTime.setSpan ( new ForegroundColorSpan ( Color.RED ) , 0 , 2 , 0 );// set color
            averageWorkingTime.setSpan ( new RelativeSizeSpan ( 1f ) , 6 , 8 , 0 ); // set size
            averageWorkingTime.setSpan ( new ForegroundColorSpan ( Color.RED ) , 6 , 8 , 0 );// set color
        }
        return averageWorkingTime;
    }

    public static SpannableString averageTaskTime ( ArrayList<Tasks> list, long diffHrs , @NotNull String status ) {
        SpannableString averageTaskTime = null;
        if ( status.equalsIgnoreCase ( "Task" ) ) {
            long avgTaskdiff = diffHrs / list.size ( );
            int avgminutes = ( int ) ( ( avgTaskdiff / ( 1000 * 60 ) ) % 60 );
            int avghours = ( int ) ( ( avgTaskdiff / ( 1000 * 60 * 60 ) ) % 24 );
            String s = String.format ( "%02d" , avghours ) + " hr " + String.format ( "%02d" , avgminutes ) + " mins";
            averageTaskTime = new SpannableString ( s );
            averageTaskTime.setSpan ( new RelativeSizeSpan ( 1f ) , 0 , 2 , 0 ); // set size
            averageTaskTime.setSpan ( new ForegroundColorSpan ( Color.RED ) , 0 , 2 , 0 );// set color
            averageTaskTime.setSpan ( new RelativeSizeSpan ( 1f ) , 6 , 8 , 0 ); // set size
            averageTaskTime.setSpan ( new ForegroundColorSpan ( Color.RED ) , 6 , 8 , 0 );// set color
        }
        return averageTaskTime;
    }

    public static String kilometersAndMiles ( double kilometer , double miles ) {
        String kmValue = new DecimalFormat ("#.##").format(kilometer);
        String mileValue = new DecimalFormat("#.##").format(miles);
        SpannableString ss1=  new SpannableString(kmValue);
        ss1.setSpan(new RelativeSizeSpan (1f), 0,kmValue.length()-1, 0); // set size
        ss1.setSpan(new ForegroundColorSpan ( Color.RED), 0, kmValue.length()-1, 0);// set color
        SpannableString ss12=  new SpannableString(mileValue);
        ss12.setSpan(new RelativeSizeSpan(1f), 0,mileValue.length()-1, 0); // set size
        ss12.setSpan(new ForegroundColorSpan(Color.RED), 0, mileValue.length()-1, 0);// set color
        return kmValue+" Km/"+mileValue+" miles";
    }

    public double setLiveLocationData ( ArrayList< LiveTracking > list  ) {
        double startLattitude = 0,startLongitude = 0,locationALat = 0,locationALong = 0;
        double livetrackingLat = 0, liveTrackingLong = 0;
        Location locationA = new Location("point A");
        Location locationB = new Location("point B");
        for(int i=0; i<list.size (); i++){
            if(i==0){
                startLattitude = Double.parseDouble(list.get(i).getLatitude());
                startLongitude = Double.parseDouble(list.get(i).getLongitude());
            }else if(i==1){
                startLattitude = Double.parseDouble(list.get(i).getLatitude());
                startLongitude = Double.parseDouble(list.get(i).getLongitude());
            }
            break;
        }

        double distance = 0;
        for( LiveTracking tracking:list){
            if(tracking.getLatitude ()!=null||tracking.getLongitude ()!=null) {

                locationALat = startLattitude;
                locationALong = startLongitude;

                locationA.setLatitude(locationALat);
                locationA.setLongitude(locationALong);

                locationB.setLatitude(Double.parseDouble(tracking.getLatitude()));
                locationB.setLongitude(Double.parseDouble(tracking.getLongitude()));

                livetrackingLat = Double.parseDouble ( tracking.getLatitude () );
                liveTrackingLong = Double.parseDouble ( tracking.getLongitude ());
            }
        }

        distance += calculateDistance(locationALat,locationALong,livetrackingLat,liveTrackingLong);
        // mKmTravelled.setText(kilometersAndMiles(metersToKms ( distance ),metersToMiles ( distance )));
        return distance;
    }

    /*Generate Report*/
    public boolean generateReport( ReportDataModel list) {
        WritableWorkbook workbook = null;
        try {
            File sd = Environment.getExternalStorageDirectory();
            String csvFile = "TeamActivity_"+new SimpleDateFormat("MMMyy").format(new Date())+".xls"; //File Name

            File directory = new File(sd.getAbsolutePath()+"/TeamActivity");
            //create directory if not exist
            if (!directory.exists() && !directory.isDirectory()) {
                directory.mkdirs();
            }
            File file = new File(directory, csvFile);
            String sheetName = "TeamActivity_"+new SimpleDateFormat("MMMyy").format(new Date());//Sheet Name

            WorkbookSettings wbSettings = new WorkbookSettings();
            wbSettings.setLocale(new Locale("en", "EN"));
            workbook = Workbook.createWorkbook(file, wbSettings);
            WritableSheet sheet = workbook.createSheet(sheetName, 0);

            sheet.addCell(new Label (3,0, "Organization : "+PreferenceHandler.getInstance( this).getCompanyName()));
            sheet.mergeCells(5,0,10,0);
            sheet.mergeCells(5,1,10,1);
            sheet.addCell(new Label(3,2,"Chart Generated On "+new SimpleDateFormat("dd/MM/yyyy, hh:mm aa").format(new Date())));
            sheet.mergeCells(3,3,6,3);
            sheet.addCell(new Label(3,3,"User : "+ PreferenceHandler.getInstance( this).getUserFullName()));
            sheet.mergeCells(7,3,10,3);

            sheet.setColumnView(0, 20);
            sheet.setColumnView(1, 20);
            sheet.setColumnView(2, 20);
            sheet.setColumnView(3, 25);
            sheet.setColumnView(4, 10);
            sheet.setColumnView(5, 15);
            sheet.setColumnView(6, 20);
            sheet.setColumnView(7, 15);
            sheet.setColumnView(8, 15);
            sheet.setColumnView(9, 15);
            sheet.setColumnView(10, 15);
            sheet.setColumnView(11, 8);
            sheet.setColumnView(12, 18);
            sheet.setColumnView(13, 15);
            sheet.setColumnView(14, 15);
            sheet.setColumnView(15, 15);
            sheet.setColumnView(16, 15);
            sheet.setColumnView(17, 15);
            sheet.setColumnView(18, 15);
            sheet.setColumnView(19, 15);
            sheet.setColumnView(20, 15);
            sheet.setColumnView(21, 15);
            sheet.setColumnView(22, 15);
            sheet.setColumnView(23, 15);
            sheet.setColumnView(24, 15);

            WritableCellFormat cellFormats = new WritableCellFormat();
            cellFormats.setAlignment( Alignment.CENTRE);
            cellFormats.setAlignment(Alignment.LEFT);

            sheet.addCell(new Label(1, 6, "Employee",cellFormats));
            sheet.addCell(new Label(3, 6, "Working Time",cellFormats));
            sheet.addCell(new Label(4, 6, "Break Time",cellFormats));
            sheet.addCell(new Label(5, 6, "All Visits",cellFormats));
            sheet.addCell(new Label(6, 6, "Task",cellFormats));
            sheet.addCell(new Label(7, 6, "Expenses",cellFormats));
            sheet.addCell(new Label(8, 6, "Expenses Amount",cellFormats));
            sheet.addCell(new Label(9, 6, "Distance in Kms",cellFormats));

            sheet.addCell(new Label(0, 11, "Name",cellFormats));
            sheet.addCell(new Label(1, 11, "Login",cellFormats));
            sheet.addCell(new Label(2, 11, "Logout",cellFormats));
            sheet.addCell(new Label(3, 11, "Hours",cellFormats));
            sheet.addCell(new Label(4, 11, "Break Hours",cellFormats));
            sheet.addCell(new Label(5, 11, "Visits",cellFormats));
            sheet.addCell(new Label(6, 11, "Tasks",cellFormats));
            sheet.addCell(new Label(7, 11, "Expense",cellFormats));
            sheet.addCell(new Label(8, 11, "Expense Amount",cellFormats));
            sheet.addCell(new Label(9, 11, "Kms",cellFormats));

            if(list != null) {
                sheet.addCell(new Label(1, 7, list.getPreEmply()+""+list.getTotalEmp(),cellFormats));
                sheet.addCell(new Label(6, 7, list.getComplTask()+""+list.getTotalTas(),cellFormats));
                sheet.addCell(new Label(5, 7, list.getVisit(),cellFormats));
                sheet.addCell(new Label(7, 7, list.getExpenses (),cellFormats));
                sheet.addCell(new Label(9, 7, list.getKmt(),cellFormats));

                for (int i=0;i<list.getReportDataEmployees().size();i++) {
                    ReportDataEmployee rd = list.getReportDataEmployees().get(i);
                    if(rd !=null ) {
                        sheet.addCell(new Label(0, i+13, rd.getName(),cellFormats));
                        sheet.addCell(new Label(1, i+13, rd.getLoginTime(),cellFormats));
                        sheet.addCell(new Label(2, i+13, rd.getLogoutTime(),cellFormats));
                        sheet.addCell(new Label(3, i+13, rd.getHours(),cellFormats));
                        sheet.addCell(new Label(4, i+13, rd.getBreakHours(),cellFormats));
                        sheet.addCell(new Label(5, i+13, rd.getVisits(),cellFormats));
                        sheet.addCell(new Label(6, i+13, rd.getTasks(),cellFormats));
                        sheet.addCell(new Label(7, i+13, rd.getExpenses(),cellFormats));
                        sheet.addCell(new Label(8, i+13, rd.getExpensesAmt(),cellFormats));
                        sheet.addCell(new Label(9, i+13, rd.getKms(),cellFormats));
                    }
                }
            }

            workbook.write();
            System.out.println("Your file is stored in "+file.toString());
            Toast.makeText( this,"Your file is stored in "+file.toString(),Toast.LENGTH_LONG).show();
            return true;
        } catch ( WriteException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        finally {
            if(workbook != null) {
                try {
                    workbook.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (WriteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String getAddress ( LatLng latLng ) {
        Geocoder geocoder = new Geocoder( BaseActivity.this, Locale.getDefault());
        String result = null;
        try {
            List < Address > addressList = geocoder.getFromLocation( latLng.latitude, latLng.longitude, 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    sb.append(address.getAddressLine(i)).append(",");
                }
                result = address.getAddressLine(0);
                return result;
            }
            return result;
        } catch ( IOException e) {
            Log.e("MapLocation", "Unable connect to Geocoder", e);
            return result;
        }
    }

    public void enableGPS() {
        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (!provider.contains("gps")) {
            if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                final Intent poke = new Intent();
                poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
                poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
                poke.setData( Uri.parse("3"));
                sendBroadcast(poke);
            } else {
                Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
                intent.putExtra("enabled", true);
                sendBroadcast(intent);
            }
        }
    }

    public Location getLocation() {
        LocationManager locationManager = (LocationManager) this.getSystemService( Context.LOCATION_SERVICE);
        if (locationManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission( Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    Activity#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for Activity#requestPermissions for more details.
                    return null;
                }
            }
            Location lastKnownLocationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocationGPS != null) {
                return lastKnownLocationGPS;
            } else {
                Location loc =  locationManager.getLastKnownLocation( LocationManager.PASSIVE_PROVIDER);
                return loc;
            }
        } else {
            return null;
        }
    }

    public void showAlert(String message) {
        try {
            if (!this.isFinishing()) {
                new AlertDialog.Builder(this)
                        .setTitle(getResources().getString( R.string.app_name))
                        .setMessage(message)
                        .setCancelable(true)
                        .setPositiveButton(getResources().getString( R.string.ok), null)
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                dialog.dismiss();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        } catch (Exception e) {
            printLog(BaseActivity.class,e.getMessage());
        }

    }

    public void printLog(Class<?> pClassName, String pStrMsg){
        Log.e(pClassName.getSimpleName(),pStrMsg);
    }
}
