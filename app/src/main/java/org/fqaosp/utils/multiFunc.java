package org.fqaosp.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Process;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.fqaosp.entity.PKGINFO;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * 通用功能函数集合
 * */

public class multiFunc {

    public final static Integer QUERY_ALL_USER_ENABLE_PKG=2;
    public final static Integer QUERY_ALL_ENABLE_PKG=0;
    public final static Integer QUERY_ALL_USER_PKG=3;
    public final static Integer QUERY_ALL_DISABLE_PKG=1;
    public final static Integer QUERY_ALL_DEFAULT_PKG=4;

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
        us.clear();
        UserManager um = (UserManager) activity.getSystemService(Context.USER_SERVICE);
        if(checkboxs != null){
            checkboxs.clear();
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

        Collections.sort(us,String::compareTo);

    }


    //查询当前机主安装的应用
    public static void queryPKGS(Activity activity, ArrayList<PKGINFO> pkginfos , ArrayList<Boolean> checkboxs,Integer types){
        clearList(pkginfos,checkboxs);
        queryPKGSCore(activity,pkginfos,checkboxs,types,QUERY_ALL_DEFAULT_PKG);
    }

    //查询当前机主安装的应用,用户部分
    public static void queryUserPKGS(Activity activity, ArrayList<PKGINFO> pkginfos , ArrayList<Boolean> checkboxs,Integer types){
        clearList(pkginfos,checkboxs);
        queryPKGSCore(activity,pkginfos,checkboxs,types,QUERY_ALL_USER_PKG);

    }

    //查询当前机主安装的应用,用户启用部分
    public static void queryUserEnablePKGS(Activity activity, ArrayList<PKGINFO> pkginfos , ArrayList<Boolean> checkboxs,Integer types){
        clearList(pkginfos,checkboxs);
        queryPKGSCore(activity,pkginfos,checkboxs,types,QUERY_ALL_USER_ENABLE_PKG);

    }

    //查询当前机主安装的应用,禁用部分
    public static void queryDisablePKGS(Activity activity, ArrayList<PKGINFO> pkginfos , ArrayList<Boolean> checkboxs,Integer types){
        clearList(pkginfos,checkboxs);
        queryPKGSCore(activity,pkginfos,checkboxs,types,QUERY_ALL_DISABLE_PKG);

    }

    //搜索列表匹配项
    public static ArrayList<PKGINFO> indexOfPKGS(Activity activity,String findStr, ArrayList<PKGINFO> pkginfos , ArrayList<Boolean> checkboxs,Integer types){
        if(pkginfos.size() == 0){
            queryEnablePKGS(activity,pkginfos,checkboxs,types);
        }
        return indexOfPKGS(pkginfos,checkboxs,findStr);
    }

    public static Boolean isIndexOfStr(String str,String instr){
        return str.toLowerCase(Locale.ROOT).indexOf(instr.toLowerCase(Locale.ROOT)) != -1;
    }

    //搜索列表匹配项
    public static ArrayList<PKGINFO> indexOfPKGS(ArrayList<PKGINFO> pkginfos , ArrayList<Boolean> checkboxs,String findStr){
        checkboxs.clear();
        ArrayList<PKGINFO> pkginfos2 = new ArrayList<>();
        for (PKGINFO pkginfo : pkginfos) {
            if(isIndexOfStr(pkginfo.getAppname(),findStr) || isIndexOfStr(pkginfo.getPkgname(),findStr)){
                pkginfos2.add(pkginfo);
                checkboxs.add(false);
            }
        }
        return pkginfos2;
    }

    public static void checkBoxs(ArrayList<PKGINFO> pkginfos,ArrayList<Boolean> checkboxs,ApplicationInfo applicationInfo,PackageManager packageManager){
        if(checkboxs != null){
            checkboxs.add(false);
        }
        pkginfos.add(new PKGINFO(applicationInfo.packageName, applicationInfo.loadLabel(packageManager).toString(), applicationInfo.sourceDir,applicationInfo.uid+"", applicationInfo.loadIcon(packageManager))) ;

    }

    public static void checkBoxsHashMap(HashMap<String,PKGINFO> pkginfos, ArrayList<Boolean> checkboxs, ApplicationInfo applicationInfo, PackageManager packageManager){
        if(checkboxs != null){
            checkboxs.add(false);
        }
        pkginfos.put(applicationInfo.packageName, new PKGINFO(applicationInfo.packageName, applicationInfo.loadLabel(packageManager).toString(), applicationInfo.sourceDir, applicationInfo.uid+"",applicationInfo.loadIcon(packageManager))) ;
    }

    public static void appInfoAdd(PackageManager packageManager,PackageInfo packageInfo,ArrayList<PKGINFO> pkginfos , ArrayList<Boolean> checkboxs,Integer state){
        ApplicationInfo applicationInfo = packageInfo.applicationInfo;
        switch (state){
            case 0:
                if(applicationInfo.enabled){
                    checkBoxs(pkginfos,checkboxs,applicationInfo,packageManager);
                }
                break;
            case 1:
                if(!applicationInfo.enabled){
                    checkBoxs(pkginfos,checkboxs,applicationInfo,packageManager);
                }
                break;
            case 2:
                if((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0 && applicationInfo.enabled){
                    checkBoxs(pkginfos,checkboxs,applicationInfo,packageManager);
                }
                break;
            case 3:
                if((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0){
                    checkBoxs(pkginfos,checkboxs,applicationInfo,packageManager);
                }
                break;
            case 4:
                checkBoxs(pkginfos,checkboxs,applicationInfo,packageManager);
                break;
        }

    }

    public static void queryPKGSCore(Activity activity, ArrayList<PKGINFO> pkginfos , ArrayList<Boolean> checkboxs,Integer types,Integer state){
        PackageManager packageManager = activity.getPackageManager();
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(types);
        for (PackageInfo packageInfo : installedPackages) {
            appInfoAdd(packageManager,packageInfo,pkginfos,checkboxs,state);
        }
        Collections.sort(pkginfos, new Comparator<PKGINFO>() {
            @Override
            public int compare(PKGINFO pkginfo, PKGINFO t1) {
                return pkginfo.getAppname().compareTo(t1.getAppname());
            }
        });
    }

    //查询当前机主安装的应用,启用部分
    public static void queryEnablePKGS(Activity activity, ArrayList<PKGINFO> pkginfos , ArrayList<Boolean> checkboxs,Integer types){
        clearList(pkginfos,checkboxs);
        queryPKGSCore(activity,pkginfos,checkboxs,types,QUERY_ALL_ENABLE_PKG);
    }

    public static void clearList(ArrayList<PKGINFO> pkginfos , ArrayList<Boolean> checkboxs){
        checkboxs.clear();
        pkginfos.clear();
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
    public static void runAppopsCMD(Context context,String pkgname,String pkgcate , int ss , String msg , String msg2,String uid){
        String cmdstr = "";
        if(pkgcate.indexOf("$") != -1){
            pkgcate = pkgcate.replaceAll("\\$","\\\\\\$");
        }
        switch (ss){
            case 0:
                if(uid == null){
                    cmdstr="pm revoke "+pkgname + " " + pkgcate;
                }else{
                    cmdstr="pm revoke --user "+uid + " "+pkgname + " " + pkgcate;
                }
                break;
            case 1:
            case 2:
                if(uid == null){
                    cmdstr="pm disable \"" +pkgname+"/"+ pkgcate+"\"";
                }else{
                    cmdstr="pm disable --user \""+uid+" " +pkgname+"/"+ pkgcate+"\"";
                }

                break;
            case 3:
                if(uid == null){
                    cmdstr="pm grant "+pkgname + " " + pkgcate;
                }else{
                    cmdstr="pm grant --user " + uid + " " +pkgname + " " + pkgcate;
                }
                break;
            case 4:
            case 5:
                if(uid == null){
                    cmdstr="pm enable " +pkgname+"/"+ pkgcate;
                }else{
                    cmdstr="pm enable --user "+uid + " " +pkgname+"/"+ pkgcate;
                }
                break;
        }
        String TAG=context.getClass().getName();
        CMD cmd = new CMD(cmdstr);
        if(cmd.getResultCode() == 0){
            Log.d(TAG,pkgcate + " " +msg);
        }else{
            Log.d(TAG,pkgcate + " " +msg2);
        }
    }

    public static void runAppopsBySwtich(Boolean b,int mode ,Context context,String pkgname,String pkgcate , String uid){
        if(b){
            switch (mode){
                case 0:
                case 1:
                    runAppopsCMD(context,pkgname,pkgcate,4,"开启组件成功","开启组件失败",uid);
                    break;
                case 2:
                    runAppopsCMD(context,pkgname,pkgcate,3,"开启权限成功","开启权限失败",uid);
                    break;
            }
        }else{
            switch (mode){
                case 0:
                case 1:
                    runAppopsCMD(context,pkgname,pkgcate,1,"关闭组件成功","关闭组件失败",uid);
                    break;
                case 2:
                    runAppopsCMD(context,pkgname,pkgcate,0,"关闭权限成功","关闭权限失败",uid);
                    break;
            }
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

    public static void queryRunningPKGSCore(List<PackageInfo> installedPackages,ArrayList<PKGINFO> pkginfos , ArrayList<Boolean> checkboxs,PackageManager packageManager,boolean isAll){
        for (PackageInfo packageInfo : installedPackages) {
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            if (isAll) {
                if (((ApplicationInfo.FLAG_STOPPED & applicationInfo.flags) == 0)) {
                    pkginfos.add(new PKGINFO(applicationInfo.packageName, applicationInfo.loadLabel(packageManager).toString(), applicationInfo.sourceDir, applicationInfo.uid + "", applicationInfo.loadIcon(packageManager)));
                    if (checkboxs != null) {
                        checkboxs.add(false);
                    }
                }
            } else {
                if (((ApplicationInfo.FLAG_SYSTEM & applicationInfo.flags) == 0)
                        && ((ApplicationInfo.FLAG_UPDATED_SYSTEM_APP & applicationInfo.flags) == 0)
                        && ((ApplicationInfo.FLAG_STOPPED & applicationInfo.flags) == 0)) {
                    pkginfos.add(new PKGINFO(applicationInfo.packageName, applicationInfo.loadLabel(packageManager).toString(), applicationInfo.sourceDir, applicationInfo.uid + "", applicationInfo.loadIcon(packageManager)));
                    if (checkboxs != null) {
                        checkboxs.add(false);
                    }
                }

            }
        }
        Collections.sort(pkginfos, new Comparator<PKGINFO>() {
            @Override
            public int compare(PKGINFO pkginfo, PKGINFO t1) {
                return pkginfo.getAppname().compareTo(t1.getAppname());
            }
        });
    }

    //查询当前运行在后台的应用，用户安装部分
    public static void queryRunningPKGS(Activity activity, ArrayList<PKGINFO> pkginfos , ArrayList<Boolean> checkboxs,Integer types){
        PackageManager packageManager = activity.getPackageManager();
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(types);
        queryRunningPKGSCore(installedPackages,pkginfos,checkboxs,packageManager,false);
    }


    //查询当前运行在后台的应用，所有
    public static void queryAllRunningPKGS(Activity activity, ArrayList<PKGINFO> pkginfos , ArrayList<Boolean> checkboxs,Integer types){
        PackageManager packageManager = activity.getPackageManager();
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(types);
        queryRunningPKGSCore(installedPackages,pkginfos,checkboxs,packageManager,true);
    }



}
