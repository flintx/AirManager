package com.flint.airmanager;

//import android.app.AlertDialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ALL")
public class MainActivity extends AppCompatActivity {

    /**
     * 所有的应用程序包信息
     */
    private List<AppInfo> appInfos;
    /**
     * 用户应用程序的集合
     */
    private List<AppInfo> userAppInfos;

    /**
     * 系统应用程序的集合
     */
    private List<AppInfo> systemAppInfos;


    //    private AppInfo appInfo;
    private String TAG = "Flint";


    /**
     * 应用黑白名单模式
     * BWMode = 0 为黑名单模式
     * BWMode = 1 为白名单模式
     * 默认白名单模式
     */
    private static int BWMode = 1;

    public static void setBWMode(int mode) {
        BWMode = mode;
    }

    public static int getBWMode() {
        return BWMode;
    }

    private static int blackAppNum = 0;
    private static int notWhiteAppNum = 0;

    public static int unsafeAppNum = 0;

    private static boolean networkState;

    //    private static String macAddress;
//    private static String serverName = "192.168.0.100";
    private static String serverName = "10.0.2.2:8080";
    private static String url_update_device = "http://" + serverName + "/ext/AirManager/update_appinfo.php";
    private static String url_check_open = "http://" + serverName + "/ext/AirManager/check_open.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_APP = "app";
    //    private static final String TAG_DEV_ID = "dev_id";
    private static final String TAG_MAC = "mac";
    private static final String TAG_BWMODE = "bwmode";
    private static final String TAG_UNSAFE_APP_NUM = "unsafe_app_num";
    private static final String TAG_DESCRIPTION = "description";
    private static final String TAG_OPEN_TIME = "open_time";
    private static final String TAG_LAST_CLOSE_TIME = "last_close_time";
    private static final String TAG_IS_OPEN = "is_open";


    private TextView macView = null;
    private TextView networkView = null;
    private TextView bwmodeView = null;
    public static TextView allappView = null;
    public static TextView unsafeappView = null;
    private TextView actionView = null;

    private AirReceiver airReceiver = null;
    private IntentFilter intentFilter = null;

    private static MainActivity instance = null;

    public static MainActivity getInstance() {
        return instance;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        macView = (TextView) findViewById(R.id.textView3);
        networkView = (TextView) findViewById(R.id.textView9);
        bwmodeView = (TextView) findViewById(R.id.textView5);
        allappView = (TextView) findViewById(R.id.textView13);
        unsafeappView = (TextView) findViewById(R.id.textView7);
        actionView = (TextView) findViewById(R.id.textView16);

        macView.setText("");
        networkView.setText("");
        bwmodeView.setText("");
        allappView.setText("");
        unsafeappView.setText("");
        actionView.setText("");

        airReceiver = new AirReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.PACKAGE_ADDED");
        intentFilter.addDataScheme("package");
        this.registerReceiver(airReceiver, intentFilter);

        String mac_address = DeviceInfoProvider.getMacAddress();
        macView.setText(mac_address);

        actionView.setText("0条");

//        regReceiver();

        boolean check = SafeInfoEditor.editAndSaveInfo(MainActivity.this, "open_time");
        if (check) Log.d(TAG, "Edit Open Time Succeed!");
//        else {
//            Log.d(TAG, "Edit Open Time Failed!");
////            finish();
//        }
        networkState = DeviceInfoProvider.getNetworkState(MainActivity.this);
        String networkText = networkState ? "可用" : "不可用";
        networkView.setText(networkText);

        if (networkState) new openManager().execute(1);

        checkAppSafe();

        this.instance = this;

    }

    @Override
    protected void onDestroy() {
        if (airReceiver != null) {
            this.unregisterReceiver(airReceiver);
        }

        super.onDestroy();

        SafeInfoEditor.editAndSaveInfo(MainActivity.this, "last_close_time");
        if (DeviceInfoProvider.getNetworkState(MainActivity.this)) new openManager().execute(0);
    }


    class EditAlertNote extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... args) {

            String DebugMessage = "Update Failed";

            String mac_address = DeviceInfoProvider.getMacAddress();
            String bwmode = Integer.toString(BWMode);
            String unsafe_num = Integer.toString(unsafeAppNum);

            Log.d("Flint", "updateAppinfo(mac): " + mac_address);
            Log.d("Flint", "updateAppinfo(mode): " + bwmode);
            Log.d("Flint", "updateAppinfo(num): " + unsafe_num);

            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair(TAG_MAC, mac_address));
            params.add(new BasicNameValuePair(TAG_BWMODE, bwmode));
            params.add(new BasicNameValuePair(TAG_UNSAFE_APP_NUM, unsafe_num));

//            JSONObject json = new JSONParser().makeHttpRequest(url_update_device, "POST", params);
            JSONObject json = new JSONParser().makeHttpRequest(url_update_device, "GET", params);

//            try {
//                int success = json.getInt(TAG_SUCCESS);
//                if (success == 1) {
//
//                    DebugMessage = "Update Succeed";
//                    Intent i = getIntent();
//                    setResult(100, i);
//                    finish();
//
//                } else {
//                    DebugMessage = "Update Failed";
//                }
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//                DebugMessage = "Update Failed";
//
//            }


            return null;
        }
    }

    class openManager extends AsyncTask<Integer, String, String> {


        @Override
        protected String doInBackground(Integer... params) {

            String macAddress = null;
            String openTime = null;
            String lastCloseTime = null;
            AirDatabaseHelper dbHelper = new AirDatabaseHelper(MainActivity.this, "AirManager.db", null, 4);
            SQLiteDatabase sdb = dbHelper.getWritableDatabase();

            Cursor cursor = sdb.query("DevInfo", null, null, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                macAddress = cursor.getString(cursor.getColumnIndex("mac_address"));
                openTime = cursor.getString(cursor.getColumnIndex("open_time"));
                lastCloseTime = cursor.getString(cursor.getColumnIndex("last_close_time"));
            }
            cursor.close();

            List<NameValuePair> pairs = new ArrayList<NameValuePair>();

            pairs.add(new BasicNameValuePair(TAG_MAC, macAddress));
            pairs.add(new BasicNameValuePair(TAG_OPEN_TIME, openTime));
            pairs.add(new BasicNameValuePair(TAG_LAST_CLOSE_TIME, lastCloseTime));
            pairs.add(new BasicNameValuePair(TAG_IS_OPEN, Integer.toString(params[0])));

            JSONObject jsonObject = new JSONParser().makeHttpRequest(url_check_open, "GET", pairs);

//            try {
//                int success = jsonObject.getInt(TAG_SUCCESS);
//                if (success == 1) {
//
////                    DebugMessage = "Update Succeed";
//                    Intent i = getIntent();
//                    setResult(100, i);
//                    finish();
//
//                } else {
////                    DebugMessage = "Update Failed";
//                }
//
//            } catch (JSONException e) {
//                e.printStackTrace();
////                DebugMessage = "Update Failed";
//
//            }
            Log.d(TAG, "doInBackground: " + params[0]);
            Log.d(TAG, "doInBackground: open_time: " + openTime);
            Log.d(TAG, "doInBackground: last_close_time: " + lastCloseTime);
            return null;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(MainActivity.this, "click it!", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (id == R.id.check_app_safe) {
            Toast.makeText(MainActivity.this, "click him!", Toast.LENGTH_SHORT).show();
            checkAppSafe();
            return true;
        }

        if (id == R.id.update_app_list) {
            Toast.makeText(MainActivity.this, "click her!", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void checkAppSafe() {


        // 取得所有已安装app的信息
        appInfos = AppInfoProvider.getAppInfos(MainActivity.this);


        // 刷新界面内容
        allappView.setText(Integer.toString(appInfos.size()) + "个");
        bwmodeView.setText(BWMode == 0 ? "黑名单模式" : "白名单模式");

        // 区分系统应用与用户应用
        userAppInfos = new ArrayList<AppInfo>();
        systemAppInfos = new ArrayList<AppInfo>();
        for (AppInfo appInfo : appInfos) {
            if (appInfo.isUserApp()) {
                userAppInfos.add(appInfo);
            } else {
                systemAppInfos.add(appInfo);
            }
        }

        // 检查已安装用户应用安全性
        blackAppNum = 0;
        notWhiteAppNum = 0;
        unsafeAppNum = 0;

        for (final AppInfo appinfo : userAppInfos) {
            if (BWMode == 0 && appinfo.isInBlack()) {

                blackAppNum += 1;
                Dialog alertDialog = new AlertDialog.Builder(MainActivity.this).
                        setTitle("安全警告").
                        setIcon(appinfo.getIcon()).
                        setMessage(appinfo.getName() + "在应用黑名单内!").
                        setPositiveButton("卸载" + appinfo.getName(), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Uri uri = Uri.parse("package:" + appinfo.getPackname());
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_DELETE);
                                intent.setData(uri);
                                startActivity(intent);
                                blackAppNum -= 1;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        unsafeappView.setText(Integer.toString(blackAppNum) + "个");
                                        String AllAppNum = Integer.toString(appInfos.size() - 1);
                                        allappView.setText(AllAppNum + "个");
                                    }
                                });

                            }
                        }).
                        setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).
                        create();
                alertDialog.show();


            } else if (BWMode == 1 && !appinfo.isInWhite()) {
                notWhiteAppNum += 1;
                Dialog alertDialog = new AlertDialog.Builder(MainActivity.this).
                        setTitle("安全警告").
                        setIcon(appinfo.getIcon()).
                        setMessage(appinfo.getName() + "不在应用白名单内!").
                        setPositiveButton("卸载" + appinfo.getName(), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Uri uri = Uri.parse("package:" + appinfo.getPackname());
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_DELETE);
                                intent.setData(uri);
                                startActivity(intent);
                                notWhiteAppNum -= 1;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        unsafeappView.setText(Integer.toString(notWhiteAppNum) + "个");
                                        String AllAppNum = Integer.toString(appInfos.size() - 1);
                                        allappView.setText(AllAppNum + "个");
                                    }
                                });
                            }
                        }).
                        setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).
                        create();
                alertDialog.show();
            }

        }

        // 更新本地数据库与监控中心数据库
        if (BWMode == 0 && blackAppNum > 0) {
            unsafeAppNum = blackAppNum;
            SafeInfoEditor.updateAppInfo(MainActivity.this, "unsafe_app_num", unsafeAppNum);
            if (networkState) new EditAlertNote().execute();

        } else if (BWMode == 1 && notWhiteAppNum > 0) {
            unsafeAppNum = notWhiteAppNum;
            SafeInfoEditor.updateAppInfo(MainActivity.this, "unsafe_app_num", unsafeAppNum);
            if (networkState) new EditAlertNote().execute();
        }

        // 更新主界面
        unsafeappView.setText(Integer.toString(unsafeAppNum) + "个");
    }

    public static void updateUI() {
        MainActivity.getInstance().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        TextView unsafeAppNumView = (TextView) MainActivity.getInstance().findViewById(R.id.textView7);
                        TextView allAppNumView = (TextView) MainActivity.getInstance().findViewById(R.id.textView13);

                        String unsafeAppNum = unsafeAppNumView.getText().toString();
                        unsafeAppNum = Integer.toString(Integer.parseInt(unsafeAppNum.replace("个", "")) + 1);
                        String allAppNum = allAppNumView.getText().toString();
                        allAppNum = Integer.toString(Integer.parseInt(allAppNum.replace("个", "")) + 1);

                        unsafeAppNumView.setText(unsafeAppNum);
                        allAppNumView.setText(allAppNum);
                    }
                });
    }


}
