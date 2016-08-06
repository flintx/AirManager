package com.flint.airmanager;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.WindowManager;

public class AirReceiver extends BroadcastReceiver {


//	    ACTION_PACKAGE_ADDED 一个新应用包已经安装在设备上，数据包括包名（最新安装的包程序不能接收到这个广播）
//	    ACTION_PACKAGE_REPLACED 一个新版本的应用安装到设备，替换之前已经存在的版本
//	    ACTION_PACKAGE_CHANGED 一个已存在的应用程序包已经改变，包括包名
//	    ACTION_PACKAGE_REMOVED 一个已存在的应用程序包已经从设备上移除，包括包名（正在被安装的包程序不能接收到这个广播）
//	    ACTION_PACKAGE_RESTARTED 用户重新开始一个包，包的所有进程将被杀死，所有与其联系的运行时间状态应该被移除，包括包名（重新开始包程序不能接收到这个广播）
//	    ACTION_PACKAGE_DATA_CLEARED 用户已经清楚一个包的数据，包括包名（清除包程序不能接收到这个广播）

    private final String ADD_APP = "android.intent.action.PACKAGE_ADDED";


    public AirReceiver() {
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        String action = intent.getAction();
        if (action.equals(ADD_APP)) {
            String packageName = intent.getDataString();
            Log.d("Flint", "onReceive: " + packageName);
            AirDatabaseHelper dbHelper = new AirDatabaseHelper(context, "AirManager.db", null, AirDatabaseHelper.AIR_VERSION);
            SQLiteDatabase sdb = dbHelper.getReadableDatabase();

            String SQL_ADD_APP;
            int mode;
            SQL_ADD_APP = "select * from BWAppList where package_name=? and bw=?";
            Cursor cursor = sdb.query("DevInfo", new String[]{"app_mode"}, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                mode = cursor.getInt(cursor.getColumnIndex("app_mode"));


//                Log.d("FLint", "SQL: " + SQL_ADD_APP);
            } else {
                Log.d("Flint", "onReceive: " + "Can't Find.");
                return;
            }

            cursor.close();

            packageName = packageName.split(":")[1];
            cursor = sdb.rawQuery(SQL_ADD_APP, new String[]{packageName, Integer.toString(mode)});
            PackageManager manager = context.getPackageManager();

            if (mode == 0 && cursor.moveToFirst()) {
//                Toast.makeText(context, "Black! Dangerous!", Toast.LENGTH_SHORT).show();

                MainActivity.updateUI();

                try {
                    ApplicationInfo app = manager.getApplicationInfo(packageName, 0);
                    String name = app.loadLabel(manager).toString();
                    Drawable icon = app.loadIcon(manager);

                    Dialog alertDialog = new AlertDialog.Builder(context)
                            .setTitle("行为警告")
                            .setMessage("您安装的应用 " + name + " 在黑名单内，请卸载该应用；否则本设备将无法进入工作网络")
                            .setIcon(icon)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
//                                    Toast.makeText(context, "hehe", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setCancelable(false)
                            .create();
                    alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                    alertDialog.show();


                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

            } else if (mode == 1 && !cursor.moveToFirst()) {
//                Toast.makeText(context, "Not White! Dangerous!", Toast.LENGTH_SHORT).show();

                MainActivity.updateUI();
                try {
                    ApplicationInfo app = manager.getApplicationInfo(packageName, 0);
                    String name = app.loadLabel(manager).toString();
                    Drawable icon = app.loadIcon(manager);
                    Dialog alertDialog = new AlertDialog.Builder(context)
                            .setTitle("行为警告")
                            .setMessage("安装的应用" + name + "不在应用白名单内")
                            .setIcon(icon).create();
                    alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                    alertDialog.show();



                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }


            }
//        throw new UnsupportedOperationException("Not yet implemented");

        }
    }
}
