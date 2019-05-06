package app.zingo.employeemanagements.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import app.zingo.employeemanagements.Model.NotificationSettingsData;
import app.zingo.employeemanagements.Model.WorkingDay;

import static com.itextpdf.text.pdf.PdfName.ORDER;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Ems.db";
    public static final String NOTIFICATION_TABLE_NAME = "notification";
    public static final String NOTIFICATION_COLUMN_ID = "id";
    public static final String NOTIFICATION_COLUMN_NAME = "name";
    public static final String NOTIFICATION_COLUMN_status = "status";

    public static final String TIMING_TABLE_NAME = "timing";
    public static final String TIMING_COLUMN_ID = "id";

    public static final String TIMING_ORGID = "orgid";
    public static final String TIMING_SUNDAY = "sunday";
    public static final String TIMING_SUN_CIT = "suncit";
    public static final String TIMING_SUN_COT = "sunc0t";

    public static final String TIMING_MONDAY = "monday";
    public static final String TIMING_MON_CIT = "moncit";
    public static final String TIMING_MON_COT = "moncot";

    public static final String TIMING_TUESDAY = "tuesday";
    public static final String TIMING_TUE_CIT = "tuecit";
    public static final String TIMING_TUE_COT = "tuecot";

    public static final String TIMING_WEDNESDAY = "wednesday";
    public static final String TIMING_WED_CIT = "wedcit";
    public static final String TIMING_WED_COT = "wedcot";

    public static final String TIMING_THURSDAY = "thursday";
    public static final String TIMING_THU_CIT = "thucit";
    public static final String TIMING_THU_COT = "thucot";

    public static final String TIMING_FRIDAY = "friday";
    public static final String TIMING_FRI_CIT = "fricit";
    public static final String TIMING_FRI_COT = "fricot";

    public static final String TIMING_SATURDAY= "saturday";
    public static final String TIMING_SAT_CIT = "satcit";
    public static final String TIMING_SAT_COT = "satcot";

    private HashMap hp;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table notification " +
                        "(id integer primary key, name text,status text)"
        );

        db.execSQL(
                "create table timing " +
                        "(id integer primary key,"+TIMING_ORGID+" integer,"+TIMING_SUNDAY+" integer,"+TIMING_SUN_CIT+" text," +TIMING_SUN_COT+" text,"+
                        TIMING_MONDAY+" integer,"+TIMING_MON_CIT+" text," +TIMING_MON_COT+" text,"+
                        TIMING_TUESDAY+" integer,"+TIMING_TUE_CIT+" text," +TIMING_TUE_COT+" text,"+
                        TIMING_WEDNESDAY+" integer,"+TIMING_WED_CIT+" text," +TIMING_WED_COT+" text,"+
                        TIMING_THURSDAY+" integer,"+TIMING_THU_CIT+" text," +TIMING_THU_COT+" text,"+
                        TIMING_FRIDAY+" integer,"+TIMING_FRI_CIT+" text," +TIMING_FRI_COT+" text,"+
                        TIMING_SATURDAY+" integer,"+TIMING_SAT_CIT+" text," +TIMING_SAT_COT+" text"+
                        ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS notification");
        db.execSQL("DROP TABLE IF EXISTS timing");
        onCreate(db);
    }

    public boolean addNotification (NotificationSettingsData ndf) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", ndf.getName());
        contentValues.put("status", ""+ndf.getStatus());

        db.insert("notification", null, contentValues);
        return true;
    }

    public boolean addTimings (WorkingDay workingDay) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TIMING_ORGID, workingDay.getOrganizationId());
        contentValues.put(TIMING_SUNDAY, workingDay.isSuday());
        contentValues.put(TIMING_SUN_CIT,workingDay.getSundayCheckInTime() );
        contentValues.put(TIMING_SUN_COT,workingDay.getSundayCheckOutTime() );

        contentValues.put(TIMING_MONDAY, workingDay.isSuday());
        contentValues.put(TIMING_MON_CIT,workingDay.getSundayCheckInTime() );
        contentValues.put(TIMING_MON_COT,workingDay.getSundayCheckOutTime() );

        contentValues.put(TIMING_TUESDAY, workingDay.isSuday());
        contentValues.put(TIMING_TUE_CIT,workingDay.getSundayCheckInTime() );
        contentValues.put(TIMING_TUE_COT,workingDay.getSundayCheckOutTime() );

        contentValues.put(TIMING_WEDNESDAY, workingDay.isSuday());
        contentValues.put(TIMING_WED_CIT,workingDay.getSundayCheckInTime() );
        contentValues.put(TIMING_WED_COT,workingDay.getSundayCheckOutTime() );

        contentValues.put(TIMING_THURSDAY, workingDay.isSuday());
        contentValues.put(TIMING_THU_CIT,workingDay.getSundayCheckInTime() );
        contentValues.put(TIMING_THU_COT,workingDay.getSundayCheckOutTime() );

        contentValues.put(TIMING_FRIDAY, workingDay.isSuday());
        contentValues.put(TIMING_FRI_CIT,workingDay.getSundayCheckInTime() );
        contentValues.put(TIMING_FRI_COT,workingDay.getSundayCheckOutTime() );

        contentValues.put(TIMING_SATURDAY, workingDay.isSuday());
        contentValues.put(TIMING_SAT_CIT,workingDay.getSundayCheckInTime() );
        contentValues.put(TIMING_SAT_COT,workingDay.getSundayCheckOutTime() );



        db.insert("timing", null, contentValues);
        return true;
    }

    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from notification where id="+id+"", null );
        return res;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, NOTIFICATION_TABLE_NAME);
        return numRows;
    }

    public boolean updateNotification (NotificationSettingsData ndfs) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", ndfs.getName());
        contentValues.put("status", ""+ndfs.getStatus());

        db.update("notification", contentValues, "name = ? ", new String[] { ndfs.getName() } );
        return true;
    }

    public Integer deleteNotification (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("notification",
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    public ArrayList<NotificationSettingsData> getAllNotifications() {
        ArrayList<NotificationSettingsData> array_list = new ArrayList<NotificationSettingsData>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from notification", null );
      //  res.moveToFirst();

        if(res!=null){

            res.moveToFirst();
        }

        if(res.moveToFirst()){

            while (!res.isAfterLast()){

                NotificationSettingsData object = new NotificationSettingsData();
                object.setName(res.getString(res.getColumnIndex(NOTIFICATION_COLUMN_NAME)));
                object.setStatus(Integer.parseInt(res.getString(res.getColumnIndex(NOTIFICATION_COLUMN_status))));
                array_list.add(object);
                res.moveToNext();

            }
        }else{

            System.out.println("Tas");
        }


        return array_list;
    }

    public WorkingDay getOfficeTimingByOrgId(final int id) {


        WorkingDay object = new WorkingDay();
        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =   db.rawQuery( "select * from timing where "+TIMING_ORGID+" = "+id, null );
        res.moveToLast();

        if(res!=null){


            object.setOrganizationId(res.getInt(res.getColumnIndex(TIMING_ORGID)));
            if(res.getInt(res.getColumnIndex(TIMING_SUNDAY))==1){
                object.setSuday(true);
            }else{
                object.setSuday(false);
            }
            object.setSundayCheckInTime(res.getString(res.getColumnIndex(TIMING_SUN_CIT)));
            object.setSundayCheckOutTime(res.getString(res.getColumnIndex(TIMING_SUN_COT)));

            if(res.getInt(res.getColumnIndex(TIMING_MONDAY))==1){
                object.setMonday(true);
            }else{
                object.setMonday(false);
            }
            object.setMondayCheckInTime(res.getString(res.getColumnIndex(TIMING_MON_CIT)));
            object.setMondayCheckOutTime(res.getString(res.getColumnIndex(TIMING_MON_COT)));

            if(res.getInt(res.getColumnIndex(TIMING_TUESDAY))==1){
                object.setiSTuesday(true);
            }else{
                object.setiSTuesday(false);
            }
            object.setTuesdayCheckInTime(res.getString(res.getColumnIndex(TIMING_TUE_CIT)));
            object.setTuesdayCheckOutTime(res.getString(res.getColumnIndex(TIMING_TUE_COT)));

            if(res.getInt(res.getColumnIndex(TIMING_WEDNESDAY))==1){
                object.setWednesday(true);
            }else{
                object.setWednesday(false);
            }
            object.setWednesdayCheckInTime(res.getString(res.getColumnIndex(TIMING_WED_CIT)));
            object.setWednesdayCheckOutTime(res.getString(res.getColumnIndex(TIMING_WED_COT)));

            if(res.getInt(res.getColumnIndex(TIMING_THURSDAY))==1){
                object.setThursday(true);
            }else{
                object.setThursday(false);
            }
            object.setThursdayCheckInTime(res.getString(res.getColumnIndex(TIMING_THU_CIT)));
            object.setThursdayCheckOutTime(res.getString(res.getColumnIndex(TIMING_THU_COT)));

            if(res.getInt(res.getColumnIndex(TIMING_FRIDAY))==1){
                object.setFriday(true);
            }else{
                object.setFriday(false);
            }
            object.setFridayCheckInTime(res.getString(res.getColumnIndex(TIMING_FRI_CIT)));
            object.setFridayCheckOutTime(res.getString(res.getColumnIndex(TIMING_FRI_COT)));

            if(res.getInt(res.getColumnIndex(TIMING_SATURDAY))==1){
                object.setSaturday(true);
            }else{
                object.setSaturday(false);
            }
            object.setSaturdayCheckInTime(res.getString(res.getColumnIndex(TIMING_SAT_CIT)));
            object.setSaturdayCheckOutTime(res.getString(res.getColumnIndex(TIMING_SAT_COT)));

        }



        return object;
    }
}