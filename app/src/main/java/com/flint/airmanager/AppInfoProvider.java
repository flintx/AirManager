package com.flint.airmanager;

/**
 * Created by whufl on 2016/7/23.
 */

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.List;


public class AppInfoProvider {

    public static List<AppInfo> getAppInfos(Context context) {

        PackageManager pm = context.getPackageManager();
        // 所有的安装在系统上的应用程序的信息

        List<PackageInfo> packInfos = pm.getInstalledPackages(0);
        List<AppInfo> appInfos = new ArrayList<AppInfo>();

        for (PackageInfo packInfo : packInfos) {
            AppInfo appInfo = new AppInfo();
            // packageInfo 相当于一个应用程序apk包的清单文件
            
            String packageName = packInfo.packageName;
            Drawable icon = packInfo.applicationInfo.loadIcon(pm);
            String name = packInfo.applicationInfo.loadLabel(pm).toString();

            // 应用程序信息的标记
            int flags = packInfo.applicationInfo.flags;
            if ((flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                // 用户程序
                appInfo.setUserApp(true);
            } else {
                // 系统程序
                appInfo.setUserApp(false);
            }
            if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == 0) {
                // 手机的内存里
                appInfo.setInRom(true);
            } else {
                // SD卡里
                appInfo.setInRom(false);
            }

            int uid = packInfo.applicationInfo.uid;    //操作系统分配给应用程序的一个固定的id，
            int versionCode = packInfo.versionCode;

//			File rcvFile = new File("/proc/uid_stat/" + uid + "/tcp_rcv");
//			File sndFile = new File("/proc/uid_stat/" + uid + "/tcp_snd");

            appInfo.setUid(uid);
            appInfo.setVersionCode(versionCode);
            appInfo.setIcon(icon);
            appInfo.setName(name);
            appInfo.setPackname(packageName);

            appInfos.add(appInfo);
        }

        AirDatabaseHelper dbHelper = new AirDatabaseHelper(context, "AirManager.db", null, 4);
        SQLiteDatabase sdb = dbHelper.getReadableDatabase();


        for (AppInfo appInfo: appInfos) {

//            String name = appInfo.getName();
            String packName = appInfo.getPackname();
            String BcheckSQL = "select id, bw from BWAppList where package_name=? and bw=0";
            String WcheckSQL = "select id, bw from BWAppList where package_name=? and bw=1";

            Cursor cursor = sdb.rawQuery(BcheckSQL, new String[]{packName});
            if (cursor.moveToFirst()) {
                appInfo.setInBlack(true);
            } else {
                appInfo.setInBlack(false);
            }

            cursor.close();

            cursor = sdb.rawQuery(WcheckSQL, new String[]{packName});
            if (cursor.moveToFirst()) {
                appInfo.setInWhite(true);
            } else {
                appInfo.setInWhite(false);
            }

            cursor.close();


        }

        String getBWModeSQL = "select app_mode from DevInfo";
        Cursor cursor = sdb.rawQuery(getBWModeSQL, null);

        if (cursor.moveToFirst()) {
            int mode = cursor.getInt(cursor.getColumnIndex("app_mode"));
            MainActivity.setBWMode(mode);
        } else {
            MainActivity.setBWMode(1);
        }

        cursor.close();

        return appInfos;
    }
}