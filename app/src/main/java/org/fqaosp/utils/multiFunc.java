package org.fqaosp.utils;

import static org.fqaosp.utils.fileTools.copyFile;
import static org.fqaosp.utils.fileTools.extactAssetsFile;
import static org.fqaosp.utils.fileTools.getMyHomeFilesPath;
import static org.fqaosp.utils.fileTools.writeDataToPath;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import org.fqaosp.R;
import org.fqaosp.adapter.PKGINFOAdapter;
import org.fqaosp.adapter.USERAdapter;
import org.fqaosp.entity.PKGINFO;
import org.fqaosp.entity.ProcessEntity;
import org.fqaosp.myActivitys.importToolsActivity;
import org.fqaosp.myActivitys.workProfileMenuActivity;
import org.fqaosp.service.adbSocketClient;

import java.io.File;
import java.lang.reflect.Field;
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
            CMD cmd = getCMD("cp " + busyFile + " /data/local/tmp/", false);
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

    public static void showPKGS(Context context , ListView listView,ArrayList<PKGINFO> pkginfos,ArrayList<Boolean> checkboxs) {
        PKGINFOAdapter pkginfoAdapter = new PKGINFOAdapter(pkginfos, context, checkboxs);
        listView.setAdapter(pkginfoAdapter);
    }

    public static void showUsers(Context context ,ListView listView,ArrayList<String> userList,ArrayList<Boolean> checkboxsByUser) {
        USERAdapter userAdapter = new USERAdapter(userList, context, checkboxsByUser);
        listView.setAdapter(userAdapter);
    }

    public static void getPKGByUID(Context context, String cmdstr,ArrayList<PKGINFO> pkginfos,HashMap<String, PKGINFO> pkginfoHashMap,ArrayList<Boolean> checkboxs,boolean isRoot){
        pkginfos.clear();
        checkboxs.clear();
        CMD cmd = getCMD(cmdstr,isRoot);
        String result = cmd.getResult();
        String[] split = result.split("\n");
        if(split != null){
            PackageManager pm = context.getPackageManager();
            for (String s : cmd.getResult().split("\n")) {
                PackageInfo packageInfo = null;
                try {
                    packageInfo = pm.getPackageInfo(s, 0);
                    if(pkginfoHashMap!=null){
                        checkBoxsHashMap(pkginfoHashMap, checkboxs, packageInfo, pm);
                    }else{
                        checkBoxs(pkginfos, checkboxs, packageInfo, pm);
                    }

                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static CMD getCMD(String cmdstr,Boolean isRoot){
        if(isRoot){
            return new CMD(cmdstr);
        }else{
            return runADBCmd(cmdstr);
        }
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
                CMD cmd = getCMD(cmdstr,isRoot);
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
    public static String getRunAppopsCMD(String pkgname,String pkgcate , int ss ,String uid){
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
        return cmdstr;
    }

    public static String getRunAppopsBySwtichCMD(Boolean b,int mode ,String pkgname,String pkgcate , String uid){
        String cmdstr = null;
        if(b){
            switch (mode){
                case 0:
                case 1:
                    cmdstr = getRunAppopsCMD(pkgname,pkgcate,4,uid);
                    break;
                case 2:
                    cmdstr = getRunAppopsCMD(pkgname,pkgcate,3,uid);
                    break;
            }
        }else{
            switch (mode){
                case 0:
                case 1:
                    cmdstr = getRunAppopsCMD(pkgname,pkgcate,1,uid);
                    break;
                case 2:
                    cmdstr = getRunAppopsCMD(pkgname,pkgcate,0,uid);
                    break;
            }
        }
        return cmdstr;
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
        CMD cmd = getCMD( cmdstr, isRoot);
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

    public static void sendHandlerMsg(Handler handler,int n,Object obj){
        Message message = new Message();
        message.what=n;
        message.obj=obj;
        handler.sendMessage(message);
    }

    public static Handler getProcessBarDialogHandler(Context context ,ProgressBar mProgressBar,AlertDialog alertDialog,TextView dpbtv1,TextView dpbtv2,TextView dpbtv3,String text){
        return new Handler() {

            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        // 设置进度条
                        mProgressBar.setProgress((int)msg.obj);
                        break;
                    case 1:
                        // 隐藏当前下载对话框
                        permittedDismissDialog(alertDialog);
                        showInfoMsg(context,"提示","已运行完毕,请手动刷新.");
                        break;
                    case 2:
                        PKGINFO pkginfo = (PKGINFO) msg.obj;
                        dpbtv3.setText(text+(pkginfo.getAppname()==null?pkginfo.getPkgname():pkginfo.getAppname()));
                        break;
                    case 3:
                        dpbtv1.setText(((int)(msg.obj))+"");
                        break;
                    case 4:
                        alertDialog.setTitle(msg.obj.toString());
                        break;
                    case 5:
                        dpbtv2.setText(msg.obj.toString());
                        break;
                    case 6:
                        permittedDismissDialog(alertDialog);
                        showInfoMsg(context,"错误",msg.obj.toString());
                        break;
                }
            }
        };
    }

    //拼接命令参数字符串
    public static String spliceCMDStr(PKGINFO pkginfo,int mode , int apops_opt_index,int apops_permis_index){
        StringBuilder sb = new StringBuilder();
        String cmdHead = "appops set --uid  "+pkginfo.getPkgname()+" ";
        String cmdWrite = "appops write-settings ";
        String modestr="";

        if(mode == 0){
            switch (apops_opt_index){
                case 0:
                    modestr = "default";
                    break;
                case 1:
                    modestr = "ignore";
                    break;
                case 2:
                    modestr = "allow";
                    break;
                case 3:
                    modestr = "foreground";
                    break;
            }
        }
        if(mode ==1){
            switch (apops_opt_index){
                case 0:
                    modestr = "true";
                    break;
                case 1:
                    modestr = "false";
                    break;
            }
        }
        if(mode ==2){
            switch (apops_opt_index){
                case 0:
                    modestr = "active";
                    break;
                case 1:
                    modestr = "working_set";
                    break;
                case 2:
                    modestr = "frequent";
                    break;
                case 3:
                    modestr = "rare";
                    break;
                case 4:
                    modestr = "restricted";
                    break;
            }
        }

        switch (apops_permis_index){
            case 0 :
                sb.append(cmdHead+" READ_PHONE_STATE "+modestr+";");
                sb.append(cmdHead+" READ_CONTACTS "+modestr+";");
                sb.append(cmdHead+" WRITE_CONTACTS "+modestr+";");
                sb.append(cmdHead+" READ_CALL_LOG "+modestr+";");
                sb.append(cmdHead+" WRITE_CALL_LOG "+modestr+";");
                sb.append(cmdHead+" CALL_PHONE "+modestr+";");
                sb.append(cmdHead+" READ_SMS "+modestr+";");
                sb.append(cmdHead+" WRITE_SMS "+modestr+";");
                sb.append(cmdHead+" SEND_SMS "+modestr+";");
                sb.append(cmdHead+" RECEIVE_SMS "+modestr+";");
                sb.append(cmdHead+" RECEIVE_EMERGECY_SMS "+modestr+";");
                sb.append(cmdHead+" RECEIVE_MMS "+modestr+";");
                sb.append(cmdHead+" RECEIVE_WAP_PUSH "+modestr+";");
                sb.append(cmdHead+" READ_ICC_SMS "+modestr+";");
                sb.append(cmdHead+" WRITE_ICC_SMS "+modestr+";");
                sb.append(cmdHead+" PROCESS_OUTGOING_CALLS "+modestr+";");
                sb.append(cmdHead+" READ_CELL_BROADCASTS "+modestr+";");
                sb.append(cmdHead+" android:add_voicemail "+modestr+";");
                sb.append(cmdHead+" android:answer_phone_calls "+modestr+";");
                sb.append(cmdHead+" android:call_phone "+modestr+";");
                sb.append(cmdHead+" android:read_call_log "+modestr+";");
                sb.append(cmdHead+" android:read_contacts "+modestr+";");
                sb.append(cmdHead+" android:read_cell_broadcasts "+modestr+";");
                sb.append(cmdHead+" android:read_phone_numbers "+modestr+";");
                sb.append(cmdHead+" android:read_phone_state "+modestr+";");
                sb.append(cmdHead+" android:read_sms "+modestr+";");
                sb.append(cmdHead+" android:receive_mms "+modestr+";");
                sb.append(cmdHead+" android:receive_sms "+modestr+";");
                sb.append(cmdHead+" android:receive_wap_push "+modestr+";");
                sb.append(cmdHead+" android:send_sms "+modestr+";");
                sb.append(cmdHead+" android:write_call_log "+modestr+";");
                sb.append(cmdHead+" android:write_contacts "+modestr+";");
                sb.append(cmdHead+" android:process_outgoing_calls "+modestr+";");
                sb.append(cmdHead+" android.permission-group.CALL_LOG "+modestr+";");
                sb.append(cmdHead+" android.permission-group.CONTACTS "+modestr+";");
                sb.append(cmdHead+" android.permission-group.PHONE "+modestr+";");
                sb.append(cmdHead+" android.permission-group.SMS "+modestr+";");
                break;
            case 1:
                sb.append(cmdHead+" READ_EXTERNAL_STORAGE "+modestr+";");
                sb.append(cmdHead+" WRITE_EXTERNAL_STORAGE "+modestr+";");
                sb.append(cmdHead+" ACCESS_MEDIA_LOCATION "+modestr+";");
                sb.append(cmdHead+" LEGACY_STORAGE "+modestr+";");
                sb.append(cmdHead+" WRITE_MEDIA_AUDIO "+modestr+";");
                sb.append(cmdHead+" READ_MEDIA_AUDIO "+modestr+";");
                sb.append(cmdHead+" WRITE_MEDIA_VIDEO "+modestr+";");
                sb.append(cmdHead+" READ_MEDIA_VIDEO "+modestr+";");
                sb.append(cmdHead+" READ_MEDIA_IMAGES "+modestr+";");
                sb.append(cmdHead+" WRITE_MEDIA_IMAGES "+modestr+";");
                sb.append(cmdHead+" MANAGE_EXTERNAL_STORAGE "+modestr+";");
                sb.append(cmdHead+" android:picture_in_picture "+modestr+";");
                sb.append(cmdHead+" android.permission-group.READ_MEDIA_AURAL "+modestr+";");
                sb.append(cmdHead+" android.permission-group.READ_MEDIA_VISUAL "+modestr+";");
                sb.append(cmdHead+" android.permission-group.READ_MEDIA_VISUAL "+modestr+";");
                sb.append(cmdHead+" android.permission-group.STORAGE "+modestr+";");
                break;
            case 2:
                sb.append(cmdHead+" READ_CLIPBOARD "+modestr+";");
                sb.append(cmdHead+" WRITE_CLIPBOARD "+modestr+";");
                break;
            case 3:
                sb.append(cmdHead+" RUN_ANY_IN_BACKGROUND "+modestr+";");
                break;
            case 4:
                sb.append(cmdHead+" RUN_IN_BACKGROUND "+modestr+";");
                break;
            case 5:
                sb.append(cmdHead+" CAMERA "+modestr+";");
                sb.append(cmdHead+" android:camera "+modestr+";");
                sb.append(cmdHead+" android.permission-group.CAMERA "+modestr+";");
                break;
            case 6:
                sb.append(cmdHead+" RECORD_AUDIO "+modestr+";");
                sb.append(cmdHead+" android:record_audio "+modestr+";");
                sb.append(cmdHead+" TAKE_AUDIO_FOCUS "+modestr+";");
                sb.append(cmdHead+" android.permission-group.MICROPHONE "+modestr+";");
                break;
            case 7:
                sb.append(cmdHead+" COARSE_LOCATION "+modestr+";");
                sb.append(cmdHead+" FINE_LOCATION "+modestr+";");
                sb.append(cmdHead+" android:coarse_location "+modestr+";");
                sb.append(cmdHead+" android:fine_location "+modestr+";");
                sb.append(cmdHead+" android:mock_location "+modestr+";");
                sb.append(cmdHead+" android:monitor_location_high_power "+modestr+";");
                sb.append(cmdHead+" android:monitor_location "+modestr+";");
                sb.append(cmdHead+" android.permission-group.LOCATION "+modestr+";");
                break;
            case 8:
                sb.append(cmdHead+" READ_CALENDAR "+modestr+";");
                sb.append(cmdHead+" WRITE_CALENDAR "+modestr+";");
                sb.append(cmdHead+" android:write_calendar "+modestr+";");
                sb.append(cmdHead+" android:read_calendar "+modestr+";");
                sb.append(cmdHead+" android.permission-group.CALENDAR "+modestr+";");
                break;
            case 9:
                sb.append(cmdHead+" WIFI_SCAN "+modestr+";");
                sb.append(cmdHead+" android:use_sip "+modestr+";");
                sb.append(cmdHead+" BLUETOOTH_SCAN "+modestr+";");
                sb.append(cmdHead+" BLUETOOTH_ADVERTISE "+modestr+";");
                sb.append(cmdHead+" BLUETOOTH_CONNECT "+modestr+";");
                sb.append(cmdHead+" BLUETOOTH_ADMIN "+modestr+";");
                sb.append(cmdHead+" BLUETOOTH "+modestr+";");
                sb.append(cmdHead+" NEARBY_DEVICES "+modestr+";");
                sb.append(cmdHead+" android.permission-group.NEARBY_DEVICES "+modestr+";");
                sb.append(cmdHead+" android.permission-group.SENSORS "+modestr+";");
                break;
            case 10:
                sb.append(cmdHead+" android.permission-group.NOTIFICATIONS "+modestr+";");
                sb.append(cmdHead+" ACCESS_NOTIFICATIONS "+modestr+";");
                sb.append(cmdHead+" POST_NOTIFICATION "+modestr+";");
                sb.append(cmdHead+" android.permission.POST_NOTIFICATIONS "+modestr+";");
                break;
            case 11:
                sb.append("am set-inactive  "+pkginfo.getPkgname()+" "+modestr);
                break;
            case 12:
                sb.append("am set-standby-bucket "+pkginfo.getPkgname()+" " + modestr );
                break;
            case 13:
                appopsCmdStr acs = new appopsCmdStr();
                sb.append(modestr.equals("true")?acs.enableAppByAPPUIDCMD(Integer.valueOf(pkginfo.getApkuid().trim())):acs.disableAppByAPPUIDCMD(Integer.valueOf(pkginfo.getApkuid().trim())));
                break;
        }
        if(apops_permis_index < 11){
            sb.append(cmdWrite);
        }
        return sb.toString();
    }

    public static void sendProcessBarHandlerSum(Handler handler,int i , int size , PKGINFO pkginfo){
        // 计算进度条当前位置
        sendHandlerMsg(handler,0,(int) (((float) i / size) * 100));
        sendHandlerMsg(handler,3,i);
        sendHandlerMsg(handler,2,pkginfo);
    }

    public static void showProcessBarDialogByCMD(Context context, ArrayList<PKGINFO> list,String title,String text , int ppmode,Integer install_mode,Boolean isDisable,Boolean isRoot,String uid,Integer mode , Integer apops_opt_index,Integer apops_permis_index,String...parm){
        String app_cache_storage = context.getExternalCacheDir().toString();
        String scriptName="fqtools.sh";
        String filesDir =getMyHomeFilesPath(context);
        String barfile = filesDir+"/"+scriptName;
        makeWP makewp = new makeWP();
        StringBuilder sb = new StringBuilder();
        sb.append(" ");
        if(parm !=null && parm.length > 0){
            for (int i = 0; i < parm.length; i++) {
                sb.append(parm[i]+"  ");
            }
        }
        if(ppmode == 9){
            sb.setLength(0);
        }
        int size = list.size();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        View vvv = LayoutInflater.from(context).inflate(R.layout.download_process_bar, null);
        ProgressBar mProgressBar = (ProgressBar) vvv.findViewById(R.id.dpbpb);
        TextView dpbtv1 = vvv.findViewById(R.id.dpbtv1);
        TextView dpbtv2 = vvv.findViewById(R.id.dpbtv2);
        TextView dpbtv3 = vvv.findViewById(R.id.dpbtv3);
        builder.setView(vvv);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        preventDismissDialog(alertDialog);
        dpbtv2.setText(size+"");
        Handler mUpdateProgressHandler = getProcessBarDialogHandler(context,mProgressBar,alertDialog,dpbtv1,dpbtv2,dpbtv3,text);
        appopsCmdStr acs = new appopsCmdStr();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String outDir=null;
                for (int i = 1; i <= size; i++) {
                    PKGINFO pkginfo = list.get(i - 1);
                    sendProcessBarHandlerSum(mUpdateProgressHandler,i,size,pkginfo);
                    String cmdstr=null;
                    switch (ppmode){
                        case 0:
                            cmdstr = isDisable?makewp.getChangePkgOnEnableByUIDCMD(uid, pkginfo.getPkgname()):makewp.getChangePkgOnDisableByUIDCMD(uid,pkginfo.getPkgname());
                            break;
                        case 1:
                            cmdstr = makewp.getUninstallPkgByUIDCMD(uid, pkginfo.getPkgname());
                            break;
                        case 2:
                            cmdstr = spliceCMDStr(pkginfo,mode,apops_opt_index,apops_permis_index);
                            break;
                        case 3:
                            switch (install_mode){
                                case 0:
                                    cmdstr = acs.getInstallLocalPkgCMD(uid, pkginfo.getApkpath());
                                    break;
                                case 1:
                                    cmdstr = acs.getInstallLocalPkgOnDowngradeCMD(uid, pkginfo.getApkpath());
                                    break;
                                case 2:
                                    cmdstr = acs.getInstallLocalPkgOnDebugCMD(uid, pkginfo.getApkpath());
                                    break;
                                case 3:
                                    cmdstr = acs.getInstallLocalPkgOnExistsCMD(uid, pkginfo.getApkpath());
                                    break;
                            }
                            break;
                        case 4:
                            cmdstr = acs.getInstallLocalPkgOnExistsCMD(uid,pkginfo.getApkpath());
                            break;
                        case 5:
                            cmdstr = "am force-stop  "+pkginfo.getPkgname();
                            break;
                        case 6:
                            cmdstr = "sh "+barfile+" backup "+pkginfo.getPkgname() +" "+sb.toString()+ " "+uid;
                            break;
                        case 7:
                            cmdstr = "sh "+barfile+" restory "+pkginfo.getPkgname() +" "+sb.toString()+ " "+uid;
                            break;
                        case 8:
                            if(mode == 0){
                                String filePath = pkginfo.getApkpath();
                                String outname = new File(filePath).getName();
                                outDir = app_cache_storage + "/recompile";
                                File file = new File(outDir);
                                if (!file.exists()) {
                                    file.mkdirs();
                                }
                                String outFile = outDir + "/" + outname + ".apk";
                                cmdstr = "cd " + filesDir + " && sh fqtools.sh apktool reapk "+ filePath+ " "  + outFile ;
                            }

                            if(mode == 1){
                                String filePath = pkginfo.getApkpath();
                                PackageManager packageManager = context.getPackageManager();
                                PackageInfo archiveInfo = packageManager.getPackageArchiveInfo(filePath, 0);
                                String pkgname = archiveInfo.packageName;
                                outDir = app_cache_storage + "/decompile/" + pkgname;
                                cmdstr = "cd " + filesDir + " && sh fqtools.sh apktool deapk " + filePath+ " "  + outDir ;
                            }
                            break;
                        case 9:
                            outDir = app_cache_storage+"/"+(mode ==0 ?"romunpack":"romrepack");
                            String path = pkginfo.getApkpath();
                            String outdir = outDir+"/"+System.currentTimeMillis();
                            String filesDir = getMyHomeFilesPath(context);
                            File file = new File(outdir);
                            if(!file.exists()){
                                file.mkdirs();
                            }
                            if(mode ==0){
                                cmdstr = "cd "+filesDir+" && sh fqtools.sh unpackrom "+parm[0] + " " +path + " " + outdir + " " + parm[1];
                            }
                            if(mode ==1){
                                cmdstr = "cd "+filesDir+" && sh fqtools.sh repackrom "+path+ " " + outdir + " "  +parm[0] + " " + parm[1];
                            }
                            sb.append(path+" -----> "+outdir+".\r\n");
                            break;
                        case 11:
                            String outpath = context.getExternalCacheDir().getAbsolutePath()+"/"+pkginfo.getPkgname()+".apk";
                            copyFile(pkginfo.getApkpath(),outpath);
                            break;
                    }
                    if(ppmode == 8 || ppmode == 9){
                        CMD cmd = new CMD(cmdstr, false);
                        int code = cmd.getResultCode();
                        if(ppmode == 9 && code != 0){
                            String ff=outDir+"/"+System.currentTimeMillis()+".log";
                            writeDataToPath(cmd.toString(),ff,false);
                            sendHandlerMSG(mUpdateProgressHandler,6,(mode==0?"解":"打")+"包失败,日志存放在 >>  "+ff);
                        }
                    }else if(ppmode == 10){
                        CMD cmd = new CMD(getRunAppopsBySwtichCMD(pkginfo.getAppversionname().equals("true")?true:false,mode,pkginfo.getPkgname(),pkginfo.getAppname(),uid));
                    }else{
                        CMD cmd = getCMD(cmdstr, isRoot);
                    }

                }
                mUpdateProgressHandler.sendEmptyMessage(1);
            }
        }).start();
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
    public static void permittedDismissDialog(AlertDialog ddd) {
        try {
            Field field = ddd.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(ddd, true);
        } catch (Exception e) {
        }
        ddd.dismiss();
    }

    public static void showLowMemDialog(Context context){
        showLowMemDialog(context,"警告","当前设备配置较低,运行此页面功能会出现问题!");
    }

    public static void showLowMemDialog(Context context,String title , String msg){
        ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        if(activityManager.isLowRamDevice() || (memoryInfo != null && (memoryInfo.totalMem*1.0/(1024*1024)) < 4096)){
            showInfoMsg(context,title,msg);
        }
    }

}
