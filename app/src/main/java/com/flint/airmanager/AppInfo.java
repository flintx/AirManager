package com.flint.airmanager;

/**
 * Created by whufl on 2016/7/23.
 */

import android.graphics.drawable.Drawable;

public class AppInfo {

    private Drawable icon;
    private String name;
    private String packname;
    private int versionCode;
    private boolean inRom;
    private boolean userApp;
    private int uid;
    private boolean inBlack = false;
    private boolean inWhite = true;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackname() {
        return packname;
    }

    public void setPackname(String packname) {
        this.packname = packname;
    }

    public void setVersionCode(int versionCode) { this.versionCode = versionCode; }

    public int getVersionCode() { return versionCode; }

    public boolean isInRom() {
        return inRom;
    }

    public void setInRom(boolean inRom) {
        this.inRom = inRom;
    }

    public boolean isUserApp() {
        return userApp;
    }

    public void setUserApp(boolean userApp) {
        this.userApp = userApp;
    }

    public void setInBlack(boolean inBlack) { this.inBlack = inBlack; }

    public boolean isInBlack() { return inBlack; }

    public void setInWhite(boolean inWhite) { this.inWhite = inWhite; }

    public boolean isInWhite() { return inWhite; }

    @Override
    public String toString() {
        return "AppInfo [icon=" + icon + ", name=" + name + ", packname="
                + packname + ", inRom=" + inRom + ", userApp=" + userApp
                + ", inBlack=" + inBlack +  ", inWhite=" + inWhite + "]";
    }

}