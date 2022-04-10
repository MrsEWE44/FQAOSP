package org.fqaosp.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.FileUtils;
import android.os.Process;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.fqaosp.entity.PKGINFO;
import org.fqaosp.myActivitys.appopsInfoActivity;
import org.fqaosp.myActivitys.imgToolUnpackActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * 通用功能函数集合
 * */

public class multiFunc {

    //页面布局跳转
    public static void jump(Context srcA , Class<?> cls){
        Intent intent = new Intent(srcA, cls);
        srcA.startActivity(intent);
    }

    //页面布局跳转
    public static void jump(Button b , Context srcA , Class<?> cls){
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jump(srcA,cls);
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

    //查询当前机主安装的应用,用户部分
    public static void queryUserPKGS(Activity activity, ArrayList<PKGINFO> pkginfos , ArrayList<Boolean> checkboxs,Integer types){
        PackageManager packageManager = activity.getPackageManager();
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(types);
        if(checkboxs != null){
            for (PackageInfo packageInfo : installedPackages) {
                ApplicationInfo applicationInfo = packageInfo.applicationInfo;
                if((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0){
                    pkginfos.add(new PKGINFO(applicationInfo.packageName, applicationInfo.loadLabel(packageManager).toString(), applicationInfo.sourceDir, applicationInfo.loadIcon(packageManager))) ;
                    checkboxs.add(false);
                }
            }
        }else{
            for (PackageInfo packageInfo : installedPackages) {
                ApplicationInfo applicationInfo = packageInfo.applicationInfo;
                if((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0){
                    pkginfos.add(new PKGINFO(applicationInfo.packageName, applicationInfo.loadLabel(packageManager).toString(), applicationInfo.sourceDir, applicationInfo.loadIcon(packageManager))) ;
                }
            }
        }

    }

    //查询当前机主安装的应用,禁用部分
    public static void queryDisablePKGS(Activity activity, ArrayList<PKGINFO> pkginfos , ArrayList<Boolean> checkboxs,Integer types){
        PackageManager packageManager = activity.getPackageManager();
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(types);
        if(checkboxs != null){
            for (PackageInfo packageInfo : installedPackages) {
                ApplicationInfo applicationInfo = packageInfo.applicationInfo;
                if(!applicationInfo.enabled){
                    pkginfos.add(new PKGINFO(applicationInfo.packageName, applicationInfo.loadLabel(packageManager).toString(), applicationInfo.sourceDir, applicationInfo.loadIcon(packageManager))) ;
                    checkboxs.add(false);
                }
            }
        }else{
            for (PackageInfo packageInfo : installedPackages) {
                ApplicationInfo applicationInfo = packageInfo.applicationInfo;
                if(!applicationInfo.enabled){
                    pkginfos.add(new PKGINFO(applicationInfo.packageName, applicationInfo.loadLabel(packageManager).toString(), applicationInfo.sourceDir, applicationInfo.loadIcon(packageManager))) ;
                }
            }
        }

    }

    /**
     * 通过反射 阻止关闭对话框
     */
    public static void preventDismissDialog(AlertDialog ddd) {
        try {
            Field field = ddd.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            //设置mShowing值，欺骗android系统
            field.set(ddd, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //调用系统文件选择器
    public static void execFileSelect(Context context,Activity activity , String msg){
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);//打开多个文件
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        activity.startActivityForResult(intent, 0);
    }

    //获取在内部存储的自家路径
    public static String getMyStorageHomePath(Context context){
        String s = Environment.getExternalStorageDirectory().toString();
        return s+"/Android/data/"+context.getPackageName();
    }

    //获取自家data的files路径
    public static String getMyHomeFilesPath(Context context){
        String datadir="/data/data/"+context.getPackageName();
        return datadir+"/files";
    }

    //选择文件时，判断是否为理想类型
    public static void selectFile(Context context, String storage , Uri uri , ArrayList<String> list , ArrayList<Boolean> checkboxs , String msg , String equalstr){
        String filePath = storage + "/" +uri.getPath().replaceAll("/document/primary:","");
        String fileName = getPathByLastNameType(filePath);
        if(fileName.equals(equalstr)){
//            filePath=filePath.substring(0,filePath.lastIndexOf("/"));
            list.add(filePath);
            checkboxs.add(false);
        }else{
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }
    }

    //获取文件结尾类型
    public static String getPathByLastNameType(String filePath){
        return filePath.substring(filePath.lastIndexOf(".")+1);
    }

    //获取路径文件名称
    public static String getPathByLastName(String filePath){
        return filePath.substring(filePath.lastIndexOf("/")+1);
    }

    /**
     * 关闭对话框
     */
    public static void dismissDialog(AlertDialog ddd) {
        try {
            Field field = ddd.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(ddd, true);
        } catch (Exception e) {
        }
        ddd.dismiss();
    }

    //调用命令对选项进行相应授权、撤销操作
    public static void runAppopsCMD(Context context,String pkgname,String pkgcate , int ss , String msg , String msg2){
        String cmdstr = "";
        switch (ss){
            case 0:
                cmdstr="pm revoke "+pkgname + " " + pkgcate;
                break;
            case 1:
            case 2:
                cmdstr="pm disable " +pkgname+"/"+ pkgcate;
                break;
            case 3:
                cmdstr="pm grant "+pkgname + " " + pkgcate;
                break;
            case 4:
            case 5:
                cmdstr="pm enable " +pkgname+"/"+ pkgcate;
                break;
        }
        CMD cmd = new CMD(cmdstr);
        if(cmd.getResultCode() == 0){
            Toast.makeText(context, pkgcate + " " +msg, Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, pkgcate + " " +msg2, Toast.LENGTH_SHORT).show();
        }
    }


    //释放资源文件
    public static  void extactAssetsFile(Context context, String fileName,String toPath){
        AssetManager assets = context.getAssets();
        InputStream stream = null;
        try {
            stream = assets.open(fileName);
            copyFile(stream,toPath);
        } catch (IOException e) {
            e.printStackTrace();
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
