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

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Ems.db";
    public static final String NOTIFICATION_TABLE_NAME = "notification";
    public static final String NOTIFICATION_COLUMN_ID = "id";
    public static final String NOTIFICATION_COLUMN_NAME = "name";
    public static final String NOTIFICATION_COLUMN_status = "status";

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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS notification");
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
}