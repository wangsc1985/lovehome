package com.wangsc.lovehome;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by 阿弥陀佛 on 2015/11/18.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "mp.db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建数据库后，对数据库的操作
        try {
            db.execSQL("create table if not exists setting("
                    + "key TEXT PRIMARY KEY,"
                    + "value TEXT)");
            db.execSQL("create table if not exists runLog("
                    + "id TEXT PRIMARY KEY,"
                    + "runTime LONG,"
                    + "tag TEXT,"
                    + "item TEXT,"
                    + "message TEXT)");

            db.execSQL("create table if not exists location("
                    + "Id TEXT PRIMARY KEY,"
                    + "UserId TEXT,"
                    + "LocationType INT,"
                    + "Longitude REAL,"
                    + "Latitude REAL,"
                    + "Accuracy REAL,"
                    + "Provider TEXT,"
                    + "Speed REAL,"
                    + "Bearing REAL,"
                    + "Satellites INT,"
                    + "Country TEXT,"
                    + "Province TEXT,"
                    + "City TEXT,"
                    + "CityCode TEXT,"
                    + "District TEXT,"
                    + "AdCode TEXT,"
                    + "Address TEXT,"
                    + "PoiName TEXT,"
                    + "Time LONG)");
        } catch (SQLException e) {
            Log.e("wangsc", e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 更改数据库版本的操作
        try {
            switch (oldVersion) {
                case 0:
                    db.execSQL("drop table if exists phoneMessage");
                    db.execSQL("create table if not exists phoneMessage("
                            + "id int PRIMARY KEY,"
                            + "threadId int,"
                            + "phoneNumber TEXT,"
                            + "address TEXT,"
                            + "body TEXT,"
                            + "type int,"
                            + "status int,"
                            + "read int,"
                            + "createTime LONG)");
            }
        } catch (SQLException e) {
            Log.e("wangsc", e.getMessage());
        }
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        // 每次成功打开数据库后首先被执行
    }


}
