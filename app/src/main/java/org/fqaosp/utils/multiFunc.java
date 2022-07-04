package org.fqaosp.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;

import org.fqaosp.entity.PKGINFO;
import org.fqaosp.myActivitys.backupRestoreActivity;

import java.io.File;
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
    private static String TAG="multiFunc";

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

    //搜索列表匹配项
    public static ArrayList<String> indexOfLIST(ArrayList<String> list , ArrayList<Boolean> checkboxs,String findStr){
        checkboxs.clear();
        ArrayList<String> strings = new ArrayList<>();
        for (String s : list) {
            if(isIndexOfStr(s,findStr)){
                strings.add(s);
                checkboxs.add(false);
            }
        }
        return strings;
    }

    public static void checkBoxs(ArrayList<PKGINFO> pkginfos,ArrayList<Boolean> checkboxs,PackageInfo packageInfo,PackageManager packageManager){
        if(checkboxs != null){
            checkboxs.add(false);
        }
        ApplicationInfo applicationInfo = packageInfo.applicationInfo;
        pkginfos.add(new PKGINFO(applicationInfo.packageName, applicationInfo.loadLabel(packageManager).toString(), applicationInfo.sourceDir,applicationInfo.uid+"",packageInfo.versionName, applicationInfo.loadIcon(packageManager),new File(applicationInfo.sourceDir).length())) ;
    }

    public static void checkBoxsHashMap(HashMap<String,PKGINFO> pkginfos, ArrayList<Boolean> checkboxs, PackageInfo packageInfo, PackageManager packageManager){
        if(checkboxs != null){
            checkboxs.add(false);
        }
        ApplicationInfo applicationInfo = packageInfo.applicationInfo;
        pkginfos.put(applicationInfo.packageName,
                new PKGINFO(applicationInfo.packageName, applicationInfo.loadLabel(packageManager).toString(), applicationInfo.sourceDir,applicationInfo.uid+"",packageInfo.versionName, applicationInfo.loadIcon(packageManager),new File(applicationInfo.sourceDir).length())) ;
    }

    public static void appInfoAdd(PackageManager packageManager,PackageInfo packageInfo,ArrayList<PKGINFO> pkginfos , ArrayList<Boolean> checkboxs,Integer state){
        ApplicationInfo applicationInfo = packageInfo.applicationInfo;
        switch (state){
            case 0:
                if(applicationInfo.enabled){
                    checkBoxs(pkginfos,checkboxs,packageInfo,packageManager);
                }
                break;
            case 1:
                if(!applicationInfo.enabled){
                    checkBoxs(pkginfos,checkboxs,packageInfo,packageManager);
                }
                break;
            case 2:
                if((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0 && applicationInfo.enabled){
                    checkBoxs(pkginfos,checkboxs,packageInfo,packageManager);
                }
                break;
            case 3:
                if((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0){
                    checkBoxs(pkginfos,checkboxs,packageInfo,packageManager);
                }
                break;
            case 4:
                checkBoxs(pkginfos,checkboxs,packageInfo,packageManager);
                break;
        }

    }

    public static void sortPKGINFOS(ArrayList<PKGINFO> pkginfos){
        Collections.sort(pkginfos, new Comparator<PKGINFO>() {
            @Override
            public int compare(PKGINFO pkginfo, PKGINFO t1) {
                return pkginfo.getAppname().compareTo(t1.getAppname());
            }
        });
    }

    public static void queryPKGSCore(Activity activity, ArrayList<PKGINFO> pkginfos , ArrayList<Boolean> checkboxs,Integer types,Integer state){
        PackageManager packageManager = activity.getPackageManager();
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(types);
        for (PackageInfo packageInfo : installedPackages) {
            appInfoAdd(packageManager,packageInfo,pkginfos,checkboxs,state);
        }
        sortPKGINFOS(pkginfos);
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


    public static Handler dismissDialogHandler(int value,AlertDialog show){
        preventDismissDialog(show);
        return new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                if(msg.what==value){
                    dismissDialog(show);
                }
            }
        };
    }

    public static void sendHandlerMSG(Handler handler , int value){
        Message msg = new Message();
        msg.what=value;
        handler.sendMessage(msg);
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

    public static void queryRunningPKGS(Activity activity, ArrayList<PKGINFO> pkginfos){
        queryRunningPKGS(activity,pkginfos,null,0);
    }

    public static void queryRunningPKGSCore(List<PackageInfo> installedPackages,ArrayList<PKGINFO> pkginfos , ArrayList<Boolean> checkboxs,PackageManager packageManager,boolean isAll){
        for (PackageInfo packageInfo : installedPackages) {
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            if (isAll) {
                if (((ApplicationInfo.FLAG_STOPPED & applicationInfo.flags) == 0)) {
                    checkBoxs(pkginfos,checkboxs,packageInfo,packageManager);
                }
            } else {
                if (((ApplicationInfo.FLAG_SYSTEM & applicationInfo.flags) == 0)
                        && ((ApplicationInfo.FLAG_UPDATED_SYSTEM_APP & applicationInfo.flags) == 0)
                        && ((ApplicationInfo.FLAG_STOPPED & applicationInfo.flags) == 0)) {
                    checkBoxs(pkginfos,checkboxs,packageInfo,packageManager);
                }

            }
        }
      sortPKGINFOS(pkginfos);
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

    //显示提示框
    public static AlertDialog showMyDialog(Context context,String title , String msg){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(title);
        alertDialog.setMessage(msg);
        return alertDialog.show();
    }


}
