package org.fqaosp.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.FileUtils;
import android.os.Process;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;

import org.fqaosp.entity.PKGINFO;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * 通用功能函数集合
 * */

public class multiFunc {

    //页面布局跳转
    public static void jump(Button b , Context srcA , Class<?> cls){
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(srcA, cls);
                srcA.startActivity(intent);
            }
        });
    }


    //复制文件
    public static Boolean copyFile(InputStream is, String outfile)  {
        return  copyFile(is,new File(outfile));
    }

    //复制文件
    public static Boolean copyFile(InputStream is, File outFile)  {
        try {
            return copyFile(is,new FileOutputStream(outFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    //复制文件
    public static Boolean copyFile(InputStream is, OutputStream os)  {
        try {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
            os.close();
            is.close();
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    //复制文件
    public static Boolean copyFile(String srcFile , String outFile){
        return copyFile(new File(srcFile),new File(outFile));
    }
    //复制文件
    public static Boolean copyFile(File srcFile , File outFile){
        try {
            return copyFile(new FileInputStream(srcFile),outFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }


    //移除uid多余字符串
    public static String getUID(String str){
        return str.replaceAll("UserHandle|\\{|\\}","");
    }

    //获取自己当前应用的uid
    public static String getMyUID(){
        return getUID(Process.myUserHandle().toString());
    }

    //查询用户，默认
    public static void queryUSERS(Activity activity ,  ArrayList<String> us){
        queryUSERS(activity,us,null);
    }

    //查询用户
    public static void queryUSERS(Activity activity ,  ArrayList<String> us , ArrayList<Boolean> checkboxs){
        UserManager um = (UserManager) activity.getSystemService(Context.USER_SERVICE);
        if(checkboxs != null){
            for (UserHandle userHandle : um.getUserProfiles()) {
                String uid = getUID(userHandle.toString());
                if(!uid.equals(getMyUID())){
                    us.add(uid);
                    checkboxs.add(false);
                }
            }
        }else{
            for (UserHandle userHandle : um.getUserProfiles()) {
                String uid = getUID(userHandle.toString());
                if(!uid.equals(getMyUID())){
                    us.add(uid);
                }
            }
        }
    }


    //查询当前机主安装的应用，默认
    public static void queryPKGS(Activity activity, ArrayList<PKGINFO> pkginfos){
        queryPKGS(activity,pkginfos,null,0);
    }
    //查询当前机主安装的应用
    public static void queryPKGS(Activity activity, ArrayList<PKGINFO> pkginfos , ArrayList<Boolean> checkboxs,Integer types){
        PackageManager packageManager = activity.getPackageManager();
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(types);
        if(checkboxs != null){
            for (PackageInfo packageInfo : installedPackages) {
                ApplicationInfo applicationInfo = packageInfo.applicationInfo;
                pkginfos.add(new PKGINFO(applicationInfo.packageName, applicationInfo.loadLabel(packageManager).toString(), applicationInfo.sourceDir, applicationInfo.loadIcon(packageManager))) ;
                checkboxs.add(false);
            }
        }else{
            for (PackageInfo packageInfo : installedPackages) {
                ApplicationInfo applicationInfo = packageInfo.applicationInfo;
                pkginfos.add(new PKGINFO(applicationInfo.packageName, applicationInfo.loadLabel(packageManager).toString(), applicationInfo.sourceDir, applicationInfo.loadIcon(packageManager))) ;

            }
        }

    }

    public static void queryRunningPKGS(Activity activity, ArrayList<PKGINFO> pkginfos){
        queryRunningPKGS(activity,pkginfos,null,0);
    }

    //查询当前运行在后台的应用，用户安装部分
    public static void queryRunningPKGS(Activity activity, ArrayList<PKGINFO> pkginfos , ArrayList<Boolean> checkboxs,Integer types){
        PackageManager packageManager = activity.getPackageManager();
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(types);
        if(checkboxs != null){
            for (PackageInfo packageInfo : installedPackages) {
                ApplicationInfo applicationInfo = packageInfo.applicationInfo;
                if(((ApplicationInfo.FLAG_SYSTEM & applicationInfo.flags) == 0)
                        && ((ApplicationInfo.FLAG_UPDATED_SYSTEM_APP & applicationInfo.flags) == 0)
                        && ((ApplicationInfo.FLAG_STOPPED & applicationInfo.flags) == 0)){
                    pkginfos.add(new PKGINFO(applicationInfo.packageName, applicationInfo.loadLabel(packageManager).toString(), applicationInfo.sourceDir, applicationInfo.loadIcon(packageManager))) ;
                    checkboxs.add(false);
                }

            }
        }else{
            for (PackageInfo packageInfo : installedPackages) {
                ApplicationInfo applicationInfo = packageInfo.applicationInfo;
                if(((ApplicationInfo.FLAG_SYSTEM & applicationInfo.flags) == 0)
                        && ((ApplicationInfo.FLAG_UPDATED_SYSTEM_APP & applicationInfo.flags) == 0)
                        && ((ApplicationInfo.FLAG_STOPPED & applicationInfo.flags) == 0)){
                    pkginfos.add(new PKGINFO(applicationInfo.packageName, applicationInfo.loadLabel(packageManager).toString(), applicationInfo.sourceDir, applicationInfo.loadIcon(packageManager))) ;
                }
            }
        }

    }


    //查询当前运行在后台的应用，所有
    public static void queryAllRunningPKGS(Activity activity, ArrayList<PKGINFO> pkginfos , ArrayList<Boolean> checkboxs,Integer types){
        PackageManager packageManager = activity.getPackageManager();
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(types);
        if(checkboxs != null){
            for (PackageInfo packageInfo : installedPackages) {
                ApplicationInfo applicationInfo = packageInfo.applicationInfo;
                if(((ApplicationInfo.FLAG_STOPPED & applicationInfo.flags) == 0)){
                    pkginfos.add(new PKGINFO(applicationInfo.packageName, applicationInfo.loadLabel(packageManager).toString(), applicationInfo.sourceDir, applicationInfo.loadIcon(packageManager))) ;
                    checkboxs.add(false);
                }

            }
        }else{
            for (PackageInfo packageInfo : installedPackages) {
                ApplicationInfo applicationInfo = packageInfo.applicationInfo;
                if(((ApplicationInfo.FLAG_STOPPED & applicationInfo.flags) == 0)){
                    pkginfos.add(new PKGINFO(applicationInfo.packageName, applicationInfo.loadLabel(packageManager).toString(), applicationInfo.sourceDir, applicationInfo.loadIcon(packageManager))) ;
                }
            }
        }

    }



}
