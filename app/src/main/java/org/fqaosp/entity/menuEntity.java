package org.fqaosp.entity;

import android.graphics.drawable.Drawable;

public class menuEntity{


    public menuEntity(String name, String info, Class<?> classz, Drawable menuIcon, Boolean needRoot, Boolean needShizuku) {
        this.name = name;
        this.info = info;
        this.classz = classz;
        this.menuIcon = menuIcon;
        this.needRoot = needRoot;
        this.needShizuku = needShizuku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Class<?> getClassz() {
        return classz;
    }

    public void setClassz(Class<?> classz) {
        this.classz = classz;
    }

    public Drawable getMenuIcon() {
        return menuIcon;
    }

    public void setMenuIcon(Drawable menuIcon) {
        this.menuIcon = menuIcon;
    }

    public Boolean getNeedRoot() {
        return needRoot;
    }

    public void setNeedRoot(Boolean needRoot) {
        this.needRoot = needRoot;
    }

    public Boolean getNeedShizuku() {
        return needShizuku;
    }

    public void setNeedShizuku(Boolean needShizuku) {
        this.needShizuku = needShizuku;
    }

    @Override
    public String toString() {
        return "menuEntity{" +
                "name='" + name + '\'' +
                ", info='" + info + '\'' +
                ", classz=" + classz +
                ", menuIcon=" + menuIcon +
                ", needRoot=" + needRoot +
                ", needShizuku=" + needShizuku +
                '}';
    }

    private String name,info;
    private Class<?> classz;
    private Drawable menuIcon;
    private Boolean needRoot , needShizuku;
}

