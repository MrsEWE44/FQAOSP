package org.fqaosp.entity;

import android.graphics.drawable.Drawable;

public class menuEntity{

    public menuEntity(String name, String info, Class<?> classz, Drawable menuIcon) {
        this.name = name;
        this.info = info;
        this.classz = classz;
        this.menuIcon = menuIcon;
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

    @Override
    public String toString() {
        return "menuEntity{" +
                "name='" + name + '\'' +
                ", info='" + info + '\'' +
                ", classz=" + classz +
                ", menuIcon=" + menuIcon +
                '}';
    }

    private String name,info;
    private Class<?> classz;
    private Drawable menuIcon;

}

