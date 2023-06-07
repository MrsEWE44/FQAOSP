package org.fqaosp.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import org.fqaosp.R;
import org.fqaosp.adapter.PKGINFOAdapter;
import org.fqaosp.adapter.USERAdapter;
import org.fqaosp.entity.PKGINFO;
import org.fqaosp.myActivitys.importToolsActivity;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

public class dialogUtils {

    private fileTools ft = new fileTools();

    public dialogUtils(){}

    public void queryPKGProcessDialog(Context context,Activity activity,ListView lv1 , ArrayList<PKGINFO> pkginfos,ArrayList<Boolean> checkboxs,Integer mode,String cmdstr,boolean isRoot) {
        queryPKGProcessDialog(context,activity,"正在获取本地应用列表(请稍后...)",lv1,pkginfos,null,checkboxs,mode,cmdstr,isRoot);
    }

    public void queryPKGProcessDialog(Context context,Activity activity,ListView lv1 , ArrayList<PKGINFO> pkginfos,HashMap<String, PKGINFO> pkginfoHashMap,ArrayList<Boolean> checkboxs,Integer mode,String cmdstr,boolean isRoot) {
        queryPKGProcessDialog(context,activity,"正在获取本地应用列表(请稍后...)",lv1,pkginfos,pkginfoHashMap,checkboxs,mode,cmdstr,isRoot);
    }

    public void queryPKGProcessDialog(Context context, Activity activity, String msg, ListView lv1 , ArrayList<PKGINFO> pkginfos, HashMap<String, PKGINFO> pkginfoHashMap, ArrayList<Boolean> checkboxs, Integer mode, String cmdstr, boolean isRoot){
        packageUtils pkgutils = new packageUtils();
        ProgressDialog show = showMyDialog(context,msg);
        Handler handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                if(msg.what==0){
                    show.dismiss();
                    showPKGS(context,lv1,pkginfos,checkboxs);
                }
            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {

                if(mode != null){
                    switch (mode){
                        case 0:
                            pkgutils.queryEnablePKGS(activity,pkginfos,checkboxs,0);
                            break;
                        case 1:
                            pkgutils.queryPKGS(activity,pkginfos,checkboxs,0);
                            break;
                        case 2:
                            pkgutils.queryUserEnablePKGS(activity,pkginfos,checkboxs,0);
                            break;
                        case 3:
                            pkgutils.queryUserPKGS(activity,pkginfos,checkboxs,0);
                            break;
                        case 4:
                            pkgutils.queryDisablePKGS(activity,pkginfos,checkboxs,0);
                            break;
                    }
                }else if(cmdstr != null && mode == null ){
                    pkgutils.getPKGByUID(context,cmdstr,pkginfos,pkginfoHashMap,checkboxs,isRoot);
                }

                pkgutils.sortPKGINFOS(pkginfos);
                sendHandlerMSG(handler,0);
            }
        }).start();

    }


    public void showIndexOfPKGSDialog(Context context , Activity activity, ListView lv1 , EditText editText , ArrayList<PKGINFO> pkginfos, ArrayList<String> strList, ArrayList<Boolean> checkboxs) {
        showIndexOfPKGSDialog(context,activity,lv1,"正在搜索(请稍后....)",editText,pkginfos,strList,checkboxs);
    }

    public void showIndexOfPKGSDialog(Context context , Activity activity,ListView lv1,String msg , EditText editText , ArrayList<PKGINFO> pkginfos,ArrayList<String> strList,ArrayList<Boolean> checkboxs){
        ProgressDialog show = showMyDialog(context,msg);
        Handler handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                if(msg.what==0){
                    if(strList == null){
                        showPKGS(context,lv1,pkginfos,checkboxs);
                    }else{
                        showUsers(context,lv1,strList,checkboxs);
                    }
                    show.dismiss();
                }
            }
        };
        textUtils txtutils = new textUtils();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(strList == null){
                    pkginfos.addAll(txtutils.indexOfPKGS(activity,editText.getText().toString(),pkginfos,checkboxs,0));
                }else{
                    strList.addAll(txtutils.indexOfLIST(strList,checkboxs,editText.getText().toString()));
                }
                sendHandlerMSG(handler,0);
            }
        }).start();

    }

    public Handler dismissDialogHandler(int value,ProgressDialog show){
        return new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                if(msg.what==value){
                    show.dismiss();
                }
            }
        };
    }



    public void showCMDInfoMSG(Context context,boolean isOutLog,String cmdstr,Boolean isRoot,String msg,String msg2){
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
                CMD cmd = new shellUtils().getCMD(cmdstr,isRoot);
                sendHandlerMSG(handler,0,cmd.getResultCode()+"---->"+cmd.getResult());
            }
        }).start();

    }

    public void sendHandlerMSG(Handler handler , int value){
        Message msg = new Message();
        msg.what=value;
        handler.sendMessage(msg);
    }

    public void sendHandlerMSG(Handler handler , int value,Object obj){
        Message msg = new Message();
        msg.what=value;
        msg.obj=obj;
        handler.sendMessage(msg);
    }


    //显示提示框
    public ProgressDialog showMyDialog(Context context, String msg){
        ProgressDialog pd = new ProgressDialog(context);//初始化等待条
        pd.setMessage(msg);//等待显示条的信息
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(false);
        pd.show();//等待显示条
        return pd;
    }


    public void showImportToolsDialog(Context context,String toastMsg , String msg,Boolean isRoot,Boolean isADB){
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
                multiFunc m = new multiFunc();
                m.jump(context, importToolsActivity.class,isRoot,isADB);
            }
        });
        alertDialog.show();
    }

    public void sendHandlerMsg(Handler handler,int n,Object obj){
        Message message = new Message();
        message.what=n;
        message.obj=obj;
        handler.sendMessage(message);
    }

    public Handler getProcessBarDialogHandler(Context context , ProgressBar mProgressBar, AlertDialog alertDialog, TextView dpbtv1, TextView dpbtv2, TextView dpbtv3, String text){
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

    public void sendProcessBarHandlerSum(Handler handler,int i , int size , PKGINFO pkginfo){
        // 计算进度条当前位置
        sendHandlerMsg(handler,0,(int) (((float) i / size) * 100));
        sendHandlerMsg(handler,3,i);
        sendHandlerMsg(handler,2,pkginfo);
    }

    public void showProcessBarDialogByCMD(Context context, ArrayList<PKGINFO> list,String title,String text , int ppmode,Integer install_mode,Boolean isDisable,Boolean isRoot,String uid,Integer mode , Integer apops_opt_index,Integer apops_permis_index,String...parm){
        String app_cache_storage = context.getExternalCacheDir().toString();
        String scriptName="fqtools.sh";
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
                String filesDir =ft.getMyHomeFilesPath(context);
                if(isRoot == false){
                    filesDir=context.getExternalFilesDir(null).toString();
                }
                String barfile = filesDir+"/"+scriptName;
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
                            cmdstr = acs.spliceCMDStr(pkginfo,mode,apops_opt_index,apops_permis_index);
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
                            String outpath = app_cache_storage+"/"+pkginfo.getPkgname()+".apk";
                            ft.copyFile(pkginfo.getApkpath(),outpath);
                            break;
                    }
                    if(ppmode == 8 || ppmode == 9){
                        CMD cmd = new CMD(cmdstr, false);
                        int code = cmd.getResultCode();
                        if(ppmode == 9 && code != 0){
                            String ff=outDir+"/"+System.currentTimeMillis()+".log";
                            ft.writeDataToPath(cmd.toString(),ff,false);
                            sendHandlerMSG(mUpdateProgressHandler,6,(mode==0?"解":"打")+"包失败,日志存放在 >>  "+ff);
                        }
                    }else if(ppmode == 10){
                        CMD cmd = new CMD(acs.getRunAppopsBySwtichCMD(pkginfo.getAppversionname().equals("true")?true:false,mode,pkginfo.getPkgname(),pkginfo.getAppname(),uid));
                    }else{
                        CMD cmd = new shellUtils().getCMD(cmdstr, isRoot);
                    }

                }
                mUpdateProgressHandler.sendEmptyMessage(1);
            }
        }).start();
    }



    /**
     * 通过反射 阻止关闭对话框
     */
    public void preventDismissDialog(AlertDialog ddd) {
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
    public void permittedDismissDialog(AlertDialog ddd) {
        try {
            Field field = ddd.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(ddd, true);
        } catch (Exception e) {
        }
        ddd.dismiss();
    }

    public void showLowMemDialog(Context context){
        showLowMemDialog(context,"警告","当前设备配置较低,运行此页面功能会出现问题!");
    }

    public void showLowMemDialog(Context context,String title , String msg){
        ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        if(activityManager.isLowRamDevice() || (memoryInfo != null && (memoryInfo.totalMem*1.0/(1024*1024)) < 4096)){
            showInfoMsg(context,title,msg);
        }
    }

    //显示一个弹窗
    public void showInfoMsg(Context con,String title , String msg){
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

    //显示检索完毕后的应用列表
    public void showPKGS(Context context , ListView listView,ArrayList<PKGINFO> pkginfos,ArrayList<Boolean> checkboxs) {
        PKGINFOAdapter pkginfoAdapter = new PKGINFOAdapter(pkginfos, context, checkboxs);
        listView.setAdapter(pkginfoAdapter);
    }

    //显示检索完毕后的字符串列表
    public void showUsers(Context context ,ListView listView,ArrayList<String> userList,ArrayList<Boolean> checkboxsByUser) {
        USERAdapter userAdapter = new USERAdapter(userList, context, checkboxsByUser);
        listView.setAdapter(userAdapter);
    }

}
