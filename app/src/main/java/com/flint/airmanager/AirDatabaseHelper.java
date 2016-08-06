package com.flint.airmanager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by whufl on 2016/7/23.
 */
public class AirDatabaseHelper extends SQLiteOpenHelper {

    public static final int AIR_VERSION = 4;

    public static final String CREATE_TABLE_1 = "create table BWAppList ("
            + "id integer primary key autoincrement, "
            + "app_name text, "
            + "package_name text, "
            + "version_code integer, "
            + "bw integer default 0)"; //bw = 0,黑名单应用;bw = 1,白名单应用

    public static final String CREATE_TABLE_2 = "create table DevInfo ("
            + "mac_address text primary key, "
            + "open_time text, "
            + "last_close_time text, "
            + "app_mode integer default 1, "
            + "unsafe_app_num integer default 0, "
            + "action_mode integer default 0, "
            + "unsafe_action_num default 0)"; //mode = 0,黑名单模式; mode = 1，白名单模式

    public static final String INSERT_TABLE_1 = "insert into BWAppList (app_name, package_name, version_code, bw) "
            + "values (?,?,?,?) ";

    public static final String INSERT_TABLE_2 = "insert into DevInfo (mac_address, open_time, last_close_time, app_mode) "
            + "values (?,?,?,?) ";

    public AirDatabaseHelper(Context context, String name,
                             SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_1);
        db.execSQL(CREATE_TABLE_2);
        db.execSQL(INSERT_TABLE_1, new String[]{"360卫士", "com.qihoo360.mobilesafe", "243", "1"});
        db.execSQL(INSERT_TABLE_1, new String[]{"百度云", "com.baidu.netdisk", "470", "0"});
        db.execSQL(INSERT_TABLE_1, new String[]{"微信", "com.tencent.mobileqq", "390", "0"});
        db.execSQL(INSERT_TABLE_1, new String[]{"计算器", "com.android2.caculate3", "87", "1"});
        db.execSQL(INSERT_TABLE_2, new String[]{"14:f6:5a:c0:8a:94", "2016-7-30 20:01:37", "9999-12-31 12:00:00", "0"});
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists DevInfo");
        db.execSQL("drop table if exists BWAppList");
        onCreate(db);
    }
}
