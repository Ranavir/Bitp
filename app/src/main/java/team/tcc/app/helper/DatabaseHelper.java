package team.tcc.app.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;
import android.util.Log;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import team.tcc.app.util.AppContstants;
import team.tcc.app.util.AppUtils;

public class DatabaseHelper {
    public static final String TAG = DatabaseHelper.class.getSimpleName() + ": ";


    private SQLiteDatabase db;
    private MyDBHelper helper;

    public DatabaseHelper(Context context) {
        Log.d(TAG, "context " + context);
        helper = new MyDBHelper(context);
        //db = helper.getWritableDatabase();
    }//end of constructor


    private class MyDBHelper extends SQLiteOpenHelper {

        public MyDBHelper(Context context) {
            super(context, AppContstants.DB_NAME, null, AppContstants.DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.w(TAG, "create db started.............");
            try {
                db.execSQL("create table if not exists host_detail(ip_address text, port_no text)");
                db.execSQL("insert or replace into host_detail(ip_address, port_no) values('192.168.43.212','8080')");

                /*##################################################################################
                Generating the profile information table in local for storing the Welcome Survey
                response details by the user after ETL from server
                Date - 21-03-2017
                ##################################################################################*/
                //user_profile_details(select first_name,last_name,gender,dob,address_line1,address_line2 from stl_user_profile_info where user_id = %s)
                db.execSQL("CREATE TABLE  if not exists user_profile (" +//
                                "id integer PRIMARY KEY," +//serial no
                                "user_id INTEGER," +
                                "gender TEXT," +
                                "dob TEXT," +
                                "name_father TEXT," +
                                "name_mother TEXT," +
                                "occupation_father TEXT," +
                                "occupation_mother TEXT," +
                                "nationality TEXT," +
                                "address TEXT," +

                                "quota TEXT," +
                                "disability TEXT," +
                                "identification_mark TEXT," +

                                "hsc_board TEXT," +
                                "percentage_hsc TEXT," +
                                "yop_hsc TEXT," +
                                "intrm_board TEXT," +
                                "percentage_intrm REAL," +
                                "yop_intrm TEXT," +
                                "grad_board TEXT," +
                                "percentage_grad TEXT," +
                                "yop_grad TEXT," +
                                "pg_board TEXT," +
                                "percentage_pg TEXT," +
                                "yop_pg TEXT," +
                                "img_cert_hsc TEXT," +
                                "img_cert_intemediate TEXT," +
                                "img_cert_grad TEXT," +
                                "img_cert_pg TEXT," +
                                "img_signature TEXT," +
                                "img_profile TEXT);"
                );

            } catch (Exception e) {
                e.printStackTrace();
            }

            Log.d(TAG, "create db success....");
        }


        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "begin upgrade db from version " + oldVersion + " to " + newVersion + ", which will destroy all old data and tables...");
            //db.execSQL("drop table if exists user_details" );

            try {
                // query to obtain the names of all tables in your database
                Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
                List<String> tablesToDrop = new ArrayList<>();

                // iterate over the result set, adding every table name to a list
                while (c.moveToNext()) {
                    tablesToDrop.add(c.getString(0));
                }

                tablesToDrop.remove("android_metadata");
                Log.w(TAG, "Tables to drop--->" + tablesToDrop);

                // call DROP TABLE on every table name
                for (String table : tablesToDrop) {
                    String dropQuery = "DROP TABLE IF EXISTS " + table;
                    db.execSQL(dropQuery);
                    Log.e(TAG, "Table dropped--->" + table);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            onCreate(db);
            Log.w(TAG, "end db upgrade to version " + newVersion);
        }

    }//end of MyDBHelper class

    /*public void truncateTable(String table){
        db.execSQL("DELETE FROM " + table);
    }*/
    public void close() {
        helper.close();
    }


    //================================================================================================================\\

    /********************************************************************
     * This method clears all the available data of any user in device
     * local database
     * Ex use case :-
     * User sign-out, again user try to login in same device with
     * different credentials.Then to remove previous user details the
     * database should be cleared.
     *
     * @return
     * @date 06-Jan-2016
     * @author Ranavir Dash
     *********************************************************************/
    public void clearDB() {
        Log.d(TAG, "Entry---> clearDB");
        try {
            db = helper.getWritableDatabase();
            //db.beginTransaction();

            // query to obtain the names of all tables in your database
            Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
            List<String> tablesToDeleteRecords = new ArrayList<>();

            // iterate over the result set, adding every table name to a list
            while (c.moveToNext()) {
                tablesToDeleteRecords.add(c.getString(0));
            }

            //tablesToDeleteRecords.remove("user_details");
            tablesToDeleteRecords.remove("host_detail");
            tablesToDeleteRecords.remove("android_metadata");
            Log.w(TAG, "Tables to clear--->" + tablesToDeleteRecords);

            // call DROP TABLE on every table name
            for (String table : tablesToDeleteRecords) {
                String deleteQuery = "delete from " + table;
                db.execSQL(deleteQuery);
                Log.e(TAG, "Table cleared--->" + table);
            }


            //db.setTransactionSuccessful();
            Log.i(TAG, "Done!!!");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //db.endTransaction();
            db.close();
        }
        Log.d(TAG, "Exit---> clearDB");
        return;
    }//end clearDB
    /********************************************************************************
     * This method used to log the profile information of panelist in local
     *
     * @param input
     * @return
     * @author Ranavir Dash
     * @date 27-Dec-2016
     ********************************************************************************/
    public long logUserProfileInfo(JSONObject input) {
        Log.d(TAG,"Entry---> logUserProfileInfo");
        Log.d(TAG,"logUserProfileInfo:: input obj:: "+((null != input)?input.toString():"null"));
        db = helper.getWritableDatabase();
        //db.beginTransaction();

        long rowId = 0;
        try{
            /*First delete all the garbage User details records present in user table
              if present due to any failure registration transaction*/
            db.execSQL("delete from user_profile");

            ContentValues value = new ContentValues();



            value.put("user_id",  	    input.getString("user_id").trim());
            value.put("gender",          input.getString("gender").trim());
            value.put("dob",  		    input.getString("dob").trim());
            value.put("name_father", 		input.getString("name_father").trim());
            value.put("name_mother",  	    input.getString("name_mother").trim());
            value.put("occupation_father",  	    input.getString("occupation_father").trim());
            value.put("occupation_mother", input.getString("occupation_mother").trim());
            value.put("nationality", input.getString("nationality").trim());
            value.put("address", input.getString("address").trim());

            value.put("quota", input.getString("quota").trim());
            value.put("disability", input.getString("disability").trim());
            value.put("identification_mark", input.getString("identification_mark").trim());

            value.put("hsc_board", input.getString("hsc_board").trim());
            value.put("percentage_hsc", input.getString("percentage_hsc").trim());
            value.put("yop_hsc", input.getString("yop_hsc").trim());
            value.put("intrm_board", input.getString("intrm_board").trim());
            value.put("percentage_intrm", input.getString("percentage_intrm").trim());
            value.put("yop_intrm", input.getString("yop_intrm").trim());
            value.put("grad_board", input.getString("grad_board").trim());
            value.put("percentage_grad", input.getString("percentage_grad").trim());
            value.put("yop_grad", input.getString("yop_grad").trim());
            value.put("pg_board", input.getString("pg_board").trim());
            value.put("percentage_pg", input.getString("percentage_pg").trim());
            value.put("yop_pg", input.getString("yop_pg").trim());
            value.put("img_cert_hsc", input.getString("img_cert_hsc").trim());
            value.put("img_cert_intemediate", input.getString("img_cert_intemediate").trim());
            value.put("img_cert_grad", input.getString("img_cert_grad").trim());
            value.put("img_cert_pg", input.getString("img_cert_pg").trim());
            value.put("img_signature", input.getString("img_signature").trim());
            value.put("img_profile", input.getString("img_profile").trim());



            rowId=db.insert("user_profile", null, value);
            Log.d(TAG, "No of rows inserted::" + rowId);

            //db.setTransactionSuccessful();
            Log.i(TAG, "Done!!!");
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            //db.endTransaction();
            db.close();
        }
        Log.d(TAG,"Exit---> logUserProfileInfo");
        return rowId;
    }//end of logUserProfileInfo
    /********************************************************************
     * This method used to retrieve user profile details from database
     * @return JSONObject
     * @author Ranavir Dash
     * @date 27-Dec-2016
     *******************************************************************/
    public JSONObject getUserProfileInfo() {
        Log.d(TAG, "Entry---> getUserProfileInfo");
        JSONObject obj = null;
        try {
            db = helper.getReadableDatabase();
            //db.beginTransaction();
            String qry = String.format("select * from user_profile");
            Cursor c = db.rawQuery(qry, null);

            if (c.moveToLast()) {//move to the latest registration details from device
                obj = new JSONObject();
                obj.put("id", c.getString(c.getColumnIndex("id")));
                obj.put("user_id", c.getString(c.getColumnIndex("user_id")));
                obj.put("gender", c.getString(c.getColumnIndex("gender")));
                obj.put("dob", c.getString(c.getColumnIndex("dob")));
                obj.put("name_father", c.getString(c.getColumnIndex("name_father")));
                obj.put("name_mother", c.getString(c.getColumnIndex("name_mother")));
                obj.put("occupation_father", c.getString(c.getColumnIndex("occupation_father")));
                obj.put("occupation_mother", c.getString(c.getColumnIndex("occupation_mother")));
                obj.put("nationality", c.getString(c.getColumnIndex("nationality")));
                obj.put("address", c.getString(c.getColumnIndex("address")));

                obj.put("quota", c.getString(c.getColumnIndex("quota")));
                obj.put("disability", c.getString(c.getColumnIndex("disability")));
                obj.put("identification_mark", c.getString(c.getColumnIndex("identification_mark")));

                obj.put("hsc_board", c.getString(c.getColumnIndex("hsc_board")));
                obj.put("percentage_hsc", c.getString(c.getColumnIndex("percentage_hsc")));
                obj.put("yop_hsc", c.getString(c.getColumnIndex("yop_hsc")));
                obj.put("intrm_board", c.getString(c.getColumnIndex("intrm_board")));
                obj.put("percentage_intrm", c.getString(c.getColumnIndex("percentage_intrm")));
                obj.put("yop_intrm", c.getString(c.getColumnIndex("yop_intrm")));
                obj.put("grad_board", c.getString(c.getColumnIndex("grad_board")));
                obj.put("percentage_grad", c.getString(c.getColumnIndex("percentage_grad")));
                obj.put("yop_grad", c.getString(c.getColumnIndex("yop_grad")));
                obj.put("pg_board", c.getString(c.getColumnIndex("pg_board")));
                obj.put("percentage_pg", c.getString(c.getColumnIndex("percentage_pg")));
                obj.put("yop_pg", c.getString(c.getColumnIndex("yop_pg")));
                obj.put("img_cert_hsc", c.getString(c.getColumnIndex("img_cert_hsc")));
                obj.put("img_cert_intemediate", c.getString(c.getColumnIndex("img_cert_intemediate")));
                obj.put("img_cert_grad", c.getString(c.getColumnIndex("img_cert_grad")));
                obj.put("img_cert_pg", c.getString(c.getColumnIndex("img_cert_pg")));
                obj.put("img_signature", c.getString(c.getColumnIndex("img_signature")));
                obj.put("img_profile", c.getString(c.getColumnIndex("img_profile")));
            }

            //db.setTransactionSuccessful();
            Log.i(TAG, "Done!!!");
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            //db.endTransaction();
            db.close();
        }
        Log.d(TAG, "Exit---> getUserProfileInfo");
        return obj;
    }//end of getUserProfileInfo
    /********************************************************************
     * This method used to get the IP and Port address
     * stored in local database
     *
     * @return
     * @date 18-13-2017
     * @author Amod
     *********************************************************************/
    public JSONObject getHostDetails() {
        JSONObject obj = null;
        try {
            db = helper.getReadableDatabase();
            //host_detail(ip_address, port_no)
            String qry = String.format("select * from host_detail");
            Cursor c = db.rawQuery(qry, null);

            if (c.moveToFirst()) {
                do {
                    obj = new JSONObject();
                    obj.put("ip_address", c.getString(c.getColumnIndex("ip_address")));
                    obj.put("port_no", c.getString(c.getColumnIndex("port_no")));
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }
    /********************************************************************
     * This method used to update the service host details in local
     * storage
     *
     * @return
     * @date 18-13-2017
     * @author Amod
     *********************************************************************/
    public String updateHostDetails(String ip_address, String port_no) {
        String msg = "Failure";
        try{
            db = helper.getWritableDatabase();
            db.execSQL("DELETE FROM host_detail");
            ContentValues value = new ContentValues();
            value.put("ip_address", ip_address);
            value.put("port_no", port_no);
            long rowId = db.insert("host_detail", null, value);
            if(rowId>0){
                msg = "Success";
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return msg;
    }
}//end class