package com.flint.airmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by whufl on 2016/7/30.
 */
public class SafeInfoEditor {

    public static boolean editAndSaveInfo(Context context, String state) {
        boolean flag = false;

        AirDatabaseHelper dbHelper = new AirDatabaseHelper(context, "AirManager.db", null, 4);
        SQLiteDatabase sdb = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        long time = System.currentTimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(time);
        String dateTimeStr = format.format(curDate);
        values.put(state, dateTimeStr);
        sdb.update("DevInfo", values, "mac_address = ?",
                new String[]{DeviceInfoProvider.getMacAddress()});
        Log.d("Flint", "editAndSaveInfo: open_time: " + dateTimeStr);

        flag = true;
        return flag;
    }

    public static boolean updateAppInfo(Context context, String tag, int unsafeNum) {
        boolean flag = false;

        AirDatabaseHelper dbHelper = new AirDatabaseHelper(context, "AirManager.db", null, 4);
        SQLiteDatabase sdb = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(tag, unsafeNum);
        sdb.update("DevInfo", values, "mac_address = ?",
                new String[]{DeviceInfoProvider.getMacAddress()});

        flag = true;
        return flag;
    }
}
