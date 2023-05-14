package org.fqaosp.utils;

import static org.fqaosp.utils.fileTools.extactAssetsFile;
import static org.fqaosp.utils.fileTools.getMyHomeFilesPath;
import static org.fqaosp.utils.fileTools.writeDataToPath;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import org.fqaosp.entity.PKGINFO;
import org.fqaosp.entity.ProcessEntity;
import org.fqaosp.myActivitys.importToolsActivity;
import org.fqaosp.service.adbSocketClient;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public static void jump(Context srcA , Class<?> cls,Boolean isRoot,Boolean isADB){
        Intent intent = new Intent(srcA, cls);
        intent.putExtra("isRoot", isRoot);
        intent.putExtra("isADB",isADB);
        srcA.startActivity(intent);
    }

    /**
     * 是否存在su命令，并且有执行权限
     *
     * @return 存在su命令，并且有执行权限返回true
     */
    public static boolean isSuEnable() {
        File file = null;
        String[] paths = {"/system/bin/", "/system/xbin/", "/system/sbin/", "/sbin/", "/vendor/bin/", "/su/bin/"};
        try {
            for (String path : paths) {
                file = new File(path + "su");
                if (file.exists() && file.canExecute()) {
                    return testRoot();
                }
            }
        } catch (Exception x) {
            x.printStackTrace();
        }
        return false;
    }

    public static boolean testRoot(){
        CMD cmd = new CMD("id");
        return cmd.getResultCode() == 0;
    }

    public static boolean isADB(){
        CMD cmd = runADBCmd("id|grep shell");
        return !cmd.toString().isEmpty();
    }

    public static CMD runADBCmd(String cmdstr){
        adbSocketClient adbSocketClient2 = new adbSocketClient(cmdstr,new adbSocketClient.SocketListener() {
            @Override
            public void getCMD(CMD cmd) {
//                Log.d("runADBCMD",cmd.toString());
            }
        });
        return adbSocketClient2.getCMD();
    }

    //页面布局跳转
    public static void jump(Button b , Context srcA , Class<?> cls,Boolean isRoot,Boolean isADB){
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jump(srcA,cls,isRoot,isADB);
            }
        });
    }

    public static void checkTools(Context context,boolean isADB){
        String filesDir =getMyHomeFilesPath(context);
        if(isADB){
            filesDir=context.getExternalCacheDir().toString();
        }
        String scriptName = "fqtools.sh";
        String barfile = filesDir+"/"+scriptName;
        String busyFile = filesDir+"/busybox";
        String extractScriptFile = filesDir+"/extract.sh";
        File busyfile = new File(busyFile);
        File barFile = new File(barfile);
        File extractScriptFilef = new File(extractScriptFile);
        if(!busyfile.exists()){
            if(Build.CPU_ABI.equals("arm64-v8a")){
                extactAssetsFile(context,"busybox",busyFile);
            }else if(Build.CPU_ABI.equals("armeabi-v7a") || Build.CPU_ABI2.equals("armeabi")){
                extactAssetsFile(context,"busybox_arm",busyFile);
            }
        }
        if(!barFile.exists()){
            extactAssetsFile(context,scriptName,barfile);
        }

        if(!extractScriptFilef.exists()) {
            extactAssetsFile(context, "extract.sh", extractScriptFile);
        }

        if(isADB){
            CMD cmd = getCMD(context, "cp " + busyFile + " /data/local/tmp/", false);
        }

    }

    //显示一个弹窗
    public static void showInfoMsg(Context con,String title , String msg){
        AlertDialog.Builder ab = new AlertDialog.Builder(con);
        ab.setTitle(title);
        ab.setMessage(msg);
        ab.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        AlertDialog alertDialog = ab.create();
        alertDialog.show();
        TextView tv = alertDialog.getWindow().getDecorView().findViewById(android.R.id.message);
        tv.setTextIsSelectable(true);
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                for (UserHandle userHandle : um.getUserProfiles()) {
                    String uid = getUID(userHandle.toString());
                    if(!uid.equals(getMyUID())){
                        us.add(uid);
                        checkboxs.add(false);
                    }
                }
            }
        }else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                for (UserHandle userHandle : um.getUserProfiles()) {
                    String uid = getUID(userHandle.toString());
                    if(!uid.equals(getMyUID())){
                        us.add(uid);
                    }
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

    public static CMD getCMD(Context context , String cmdstr,Boolean isRoot){
        String ss = context.getExternalCacheDir().toString();
        String tmpFile = ss + "/temp.sh";
        Boolean aBoolean = writeDataToPath(cmdstr, tmpFile, false);
        if(aBoolean){
            if(isRoot){
                return new CMD("sh "+tmpFile);
            }else{
                return runADBCmd("sh "+tmpFile);
            }
        }else{
            Log.e("error","write temp script error");
        }
        return null;
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

    public static Handler dismissDialogHandler(int value,ProgressDialog show){
        return new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                if(msg.what==value){
                    show.dismiss();
                }
            }
        };
    }

    public static String getRunTraverseCMDStr(ArrayList<PKGINFO> pkginfos,ArrayList<Boolean> checkboxs ,String cmdstr){
        int hit=0;
        StringBuilder sb = new StringBuilder();
        sb.append("aaa=(");
        for (int i = 0; i < checkboxs.size(); i++) {
            if(checkboxs.get(i)){
                sb.append("\""+pkginfos.get(i).getPkgname()+"\" ");
                hit++;
            }
        }
        if(hit == 0){
            for (PKGINFO pkginfo : pkginfos) {
                sb.append("\""+pkginfo.getPkgname()+"\" ");
            }
        }

        sb.append(");for pp in ${aaa[@]};do "+cmdstr+";done;");
        return sb.toString();
    }

    public static void checkCMDResult(Context context, CMD cmd,String msg , String msg2){
        if( cmd.getResultCode() ==0){
            Log.d("checkCMDResult",msg);
            showInfoMsg(context,"提示",msg);
        }else{
            Log.d("checkCMDResult",msg2+" :: "+cmd.getResult());
            showInfoMsg(context,"错误",msg2 + " -- " + cmd.getResult());
        }
    }

    public static void showCMDInfoMSG(Context context,boolean isOutLog,String cmdstr,Boolean isRoot,String msg,String msg2){
        ProgressDialog progressDialog = showMyDialog(context, msg);
        Handler handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if(msg.what == 0){
                    progressDialog.dismiss();
                    if(isOutLog){
                        showInfoMsg(context,"提示",msg2+" : \r\n\r\n"+msg.obj.toString());
                    }else{
                        showInfoMsg(context,"提示",msg2);
                    }

                }
            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                CMD cmd = getCMD(context,cmdstr,isRoot);
                sendHandlerMSG(handler,0,cmd.getResultCode()+"---->"+cmd.getResult());
            }
        }).start();

    }

    public static void sendHandlerMSG(Handler handler , int value){
        Message msg = new Message();
        msg.what=value;
        handler.sendMessage(msg);
    }

    public static void sendHandlerMSG(Handler handler , int value,Object obj){
        Message msg = new Message();
        msg.what=value;
        msg.obj=obj;
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
                    cmdstr="pm disable --user "+uid+" \"" +pkgname+"/"+ pkgcate+"\"";
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
                    cmdstr="pm enable --user "+uid + " \"" +pkgname+"/"+ pkgcate+"\"";
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

    /**
     * <p>
     * 进行字符串正则提取
     */
    public static String getByString(String src, String regex, String re_str) {
        StringBuilder tmp = new StringBuilder();
        Matcher m = Pattern.compile(regex).matcher(src);
        if (m.find()) {
            tmp.append(m.group().replaceAll(re_str, "") + "\n");
        }
        return tmp.toString();
    }

    /**
     * <p>
     * 进行字符串正则提取
     */
    public static String getByAllString(String src, String regex, String re_str) {
        StringBuilder tmp = new StringBuilder();
        Matcher m = Pattern.compile(regex).matcher(src);
        while (m.find()) {
            tmp.append(m.group().replaceAll(re_str, "") + "\n");
        }
        return tmp.toString();
    }

    public static String getProcessValue(String src,String key){
        return getByString(src,key+":(.+?\\n)",key+":");
    }

    //查询/proc路径下所有进程
    public static Set<ProcessEntity> queryAllProcess(Context context , boolean isRoot){
        Set<ProcessEntity> set = new HashSet<>();
        String cmdstr = "for p in `ls /proc |grep -E \"[0-9]\"`;do cat \"/proc/$p/status\";echo \"FQAOSPLINE\";done";
        CMD cmd = getCMD(context, cmdstr, isRoot);
        if(cmd.getResultCode()==0){
            for (String s : getByAllString(cmd.getResult(), "Name:([\\s\\S]*)FQAOSPLINE", "").split("FQAOSPLINE")) {
                if(!s.trim().isEmpty()){
                    set.add(new ProcessEntity(getProcessValue(s,"Name"),getProcessValue(s,"Umask"),getProcessValue(s,"State"),getProcessValue(s,"Tgid"),
                            getProcessValue(s,"Ngid"),getProcessValue(s,"Pid"),getProcessValue(s,"PPid"),getProcessValue(s,"TracerPid"),getProcessValue(s,"Uid"),
                            getProcessValue(s,"Gid"),getProcessValue(s,"FDSize"),getProcessValue(s,"Groups"),getProcessValue(s,"VmPeak"),getProcessValue(s,"VmSize"),getProcessValue(s,"VmLck"),
                            getProcessValue(s,"VmPin"),getProcessValue(s,"VmHWM"),getProcessValue(s,"VmRSS"),getProcessValue(s,"RssAnon"),getProcessValue(s,"RssFile"),
                            getProcessValue(s,"RssShmem"),getProcessValue(s,"VmData"),getProcessValue(s,"VmStk"),getProcessValue(s,"VmExe"),getProcessValue(s,"VmLib"),getProcessValue(s,"VmPTE"),
                            getProcessValue(s,"VmSwap"),getProcessValue(s,"CoreDumping"),getProcessValue(s,"Threads"),getProcessValue(s,"SigQ"),getProcessValue(s,"SigPnd"),getProcessValue(s,"ShdPnd"),
                            getProcessValue(s,"SigBlk"),getProcessValue(s,"SigIgn"),getProcessValue(s,"SigCgt"),getProcessValue(s,"CapInh"),getProcessValue(s,"CapPrm"),getProcessValue(s,"CapEff"),getProcessValue(s,"CapBnd"),
                            getProcessValue(s,"CapAmb"),getProcessValue(s,"NoNewPrivs"),getProcessValue(s,"Seccomp"),getProcessValue(s,"Speculation_Store_Bypass"),getProcessValue(s,"Cpus_allowed"),getProcessValue(s,"Cpus_allowed_list"),getProcessValue(s,"Mems_allowed"),getProcessValue(s,"Mems_allowed_list"),
                            getProcessValue(s,"voluntary_ctxt_switches"),getProcessValue(s,"nonvoluntary_ctxt_switches")
                    ));
                }
            }
        }
        return  set;
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
    public static ProgressDialog showMyDialog(Context context, String msg){
        ProgressDialog pd = new ProgressDialog(context);//初始化等待条
        pd.setMessage(msg);//等待显示条的信息
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(false);
        pd.show();//等待显示条
        return pd;
    }


    public static void showImportToolsDialog(Context context,String toastMsg , String msg,Boolean isRoot,Boolean isADB){
        Toast.makeText(context, toastMsg, Toast.LENGTH_SHORT).show();
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("警告");
        alertDialog.setMessage(msg);
        alertDialog.setPositiveButton("继续", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.setNegativeButton("补全功能组件", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                jump(context, importToolsActivity.class,isRoot,isADB);
            }
        });
        alertDialog.show();
    }

}
