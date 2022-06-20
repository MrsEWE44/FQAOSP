package org.fqaosp.entity;

import android.graphics.drawable.Drawable;

public class PKGINFO {

    public PKGINFO(String pkgname, String appname, String apkpath, String apkuid, String appversionname, Drawable appicon, Long filesize) {
        this.pkgname = pkgname;
        this.appname = appname;
        this.apkpath = apkpath;
        this.apkuid = apkuid;
        this.appversionname = appversionname;
        this.appicon = appicon;
        this.filesize = filesize;
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

    public String getAppversionname() {
        return appversionname;
    }

    public void setAppversionname(String appversionname) {
        this.appversionname = appversionname;
    }

    public Drawable getAppicon() {
        return appicon;
    }

    public void setAppicon(Drawable appicon) {
        this.appicon = appicon;
    }

    public Long getFilesize() {
        return filesize;
    }

    public void setFilesize(Long filesize) {
        this.filesize = filesize;
    }

    @Override
    public String toString() {
        return "PKGINFO{" +
                "pkgname='" + pkgname + '\'' +
                ", appname='" + appname + '\'' +
                ", apkpath='" + apkpath + '\'' +
                ", apkuid='" + apkuid + '\'' +
                ", appversionname='" + appversionname + '\'' +
                ", appicon=" + appicon +
                ", filesize=" + filesize +
                '}';
    }

    private  String pkgname , appname , apkpath,apkuid,appversionname;
    private Drawable appicon;
    private Long filesize;


}
