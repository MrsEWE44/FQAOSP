package org.fqaosp.entity;

import android.net.Uri;

import java.io.File;

public class SearchFileInfo {

    public SearchFileInfo(String fname, String fpath, String ftype, Long fsize, Long flastModified, Uri uri) {
        this.fname = fname;
        this.fpath = fpath;
        this.ftype = ftype;
        this.fsize = fsize;
        this.flastModified = flastModified;
        this.uri = uri;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getFpath() {
        return fpath;
    }

    public void setFpath(String fpath) {
        this.fpath = fpath;
    }

    public String getFtype() {
        return ftype;
    }

    public void setFtype(String ftype) {
        this.ftype = ftype;
    }

    public Long getFsize() {
        return fsize;
    }

    public void setFsize(Long fsize) {
        this.fsize = fsize;
    }

    public Long getFlastModified() {
        return flastModified;
    }

    public void setFlastModified(Long flastModified) {
        this.flastModified = flastModified;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    @Override
    public String toString() {
        return "SearchFileInfo{" +
                "fname='" + fname + '\'' +
                ", fpath='" + fpath + '\'' +
                ", ftype='" + ftype + '\'' +
                ", fsize=" + fsize +
                ", flastModified=" + flastModified +
                ", uri=" + uri +
                '}';
    }

    private String fname,fpath,ftype;
    private Long fsize,flastModified;
    private Uri uri;

}
