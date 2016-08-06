package com.flint.airmanager;

import android.content.Context;
import android.net.ConnectivityManager;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

/**
 * Created by whufl on 2016/7/27.
 */
@SuppressWarnings("ALL")
public class DeviceInfoProvider {

    private static String macAddress = null;

    public static boolean getNetworkState(Context context) {
        boolean flag = false;
        //得到网络连接信息
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //去进行判断网络是否连接
        if (manager.getActiveNetworkInfo() != null) {
            flag = manager.getActiveNetworkInfo().isAvailable();
        }
        return flag;
    }

    public static String getMacAddress() {
        if (macAddress != null) {
            return macAddress;
        }
        String macSerial = null;
        String str = "";

        try {
            Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address ");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);

            for (; null != str; ) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();// 去空格
                    break;
                }
            }
        } catch (IOException ex) {
            // 赋予默认值
            ex.printStackTrace();
        }

        if (macSerial == null) {
            macSerial = "14:f6:5a:c0:8a:94";
        }
        return macSerial;
    }
}
