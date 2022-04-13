package org.fqaosp.entity;

import android.graphics.drawable.Drawable;

public class PKGINFO {

    public PKGINFO(String pkgname, String appname, String apkpath, String apkuid, Drawable appicon) {
        this.pkgname = pkgname;
        this.appname = appname;
        this.apkpath = apkpath;
        this.apkuid = apkuid;
        this.appicon = appicon;
    }

    public String getPkgname() {
        return pkgname;
    }

    public void setPkgname(String pkgname) {
        this.pkgname = pkgname;
    }

    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public String getApkpath() {
        return apkpath;
    }

    public void setApkpath(String apkpath) {
        this.apkpath = apkpath;
    }

    public String getApkuid() {
        return apkuid;
    }

    public void setApkuid(String apkuid) {
        this.apkuid = apkuid;
    }

    public Drawable getAppicon() {
        return appicon;
    }

    public void setAppicon(Drawable appicon) {
        this.appicon = appicon;
    }

    @Override
    public String toString() {
        return "PKGINFO{" +
                "pkgname='" + pkgname + '\'' +
                ", appname='" + appname + '\'' +
                ", apkpath='" + apkpath + '\'' +
                ", apkuid='" + apkuid + '\'' +
                ", appicon=" + appicon +
                '}';
    }

    private  String pkgname , appname , apkpath,apkuid;
    private Drawable appicon;


}
