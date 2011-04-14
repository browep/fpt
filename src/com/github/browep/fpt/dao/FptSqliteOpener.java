package com.github.browep.fpt.dao;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;
import com.github.browep.fpt.util.Log;

import java.io.IOException;


public class FptSqliteOpener extends SQLiteOpenHelper {
 
    //The Android's default system path of your application database.

    private static String DB_NAME = "main_db";
    private static int DB_VERSION = 1;
    private static final String CREATE_DB_SQL_1 = "CREATE TABLE instances(type INT, created datetime default current_timestamp, modified datetime default current_timestamp, data TEXT);";
    private static final String CREATE_DB_SQL_2 = "CREATE TABLE indexes(instance_id INT, path VARCHAR(512)); ";
    private static final String CREATE_DB_SQL_3 = "CREATE INDEX idx_path ON indexes(path); ";


    public FptSqliteOpener(Context context){
        super(context,DB_NAME,null,DB_VERSION);
    }

	@Override
	public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DB_SQL_1);
        db.execSQL(CREATE_DB_SQL_2);
        db.execSQL(CREATE_DB_SQL_3);

	}
 
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
 
	}
 
        // Add your public helper methods to access and get content from the database.
       // You could return cursors by doing "return myDataBase.query(....)" so it'd be easy
       // to you to create adapters for your views.

  public static String getDbName() {
    return DB_NAME;
  }
}