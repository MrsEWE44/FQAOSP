package org.fqaosp.entity;

public class workProfileDBEntity {

    public String getPkgname() {
        return pkgname;
    }

    public void setPkgname(String pkgname) {
        this.pkgname = pkgname;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public workProfileDBEntity(String pkgname, Integer uid) {
        this.pkgname = pkgname;
        this.uid = uid;
    }

    @Override
    public String toString() {
        return "workProfileDBEntity{" +
                "pkgname='" + pkgname + '\'' +
                ", uid=" + uid +
                '}';
    }

    private String pkgname;
    private Integer uid;

}
