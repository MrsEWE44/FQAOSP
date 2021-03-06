package org.fqaosp.myActivitys;

/**
 *
 * 用于更改组件状态
 * 可以禁用/启用应用的service、activity
 * 可以授权/撤销应用的permission
 *
 * */

import static org.fqaosp.utils.fileTools.copyFile;
import static org.fqaosp.utils.fileTools.execFileSelect;
import static org.fqaosp.utils.fileTools.extactAssetsFile;
import static org.fqaosp.utils.fileTools.getMyHomeFilesPath;
import static org.fqaosp.utils.fileTools.getPathByLastName;
import static org.fqaosp.utils.fileTools.getPathByLastNameType;
import static org.fqaosp.utils.multiFunc.checkBoxs;
import static org.fqaosp.utils.multiFunc.clearList;
import static org.fqaosp.utils.multiFunc.getMyUID;
import static org.fqaosp.utils.multiFunc.jump;
import static org.fqaosp.utils.multiFunc.preventDismissDialog;
import static org.fqaosp.utils.multiFunc.sendHandlerMSG;
import static org.fqaosp.utils.multiFunc.showMyDialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import org.fqaosp.R;
import org.fqaosp.adapter.PKGINFOAdapter;
import org.fqaosp.entity.PKGINFO;
import org.fqaosp.utils.CMD;
import org.fqaosp.utils.fileTools;
import org.fqaosp.utils.fuckActivity;
import org.fqaosp.utils.iptablesManage;
import org.fqaosp.utils.makeWP;
import org.fqaosp.utils.multiFunc;
import org.fqaosp.utils.permissionRequest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class appopsActivity extends AppCompatActivity {

    private Button appopsab2,apopsab4,apopsab5;
    private ListView lv1;
    private EditText apopsaet1;
    private Switch apopsasb1 , apopsasb2,apopsasb3;
    private ArrayList<PKGINFO> pkginfos = new ArrayList<>();
    private ArrayList<Boolean> checkboxs = new ArrayList<>();
    private String uid;
    private iptablesManage ipMange = new iptablesManage();
    private boolean switch_mode_tmp,switch_mode_autostart,switch_mode_all;
    private String magiskDir="/data/adb/post-fs-data.d";
    private int nowItemIndex=-1;
    private View nowItemView = null;
    private static final int PKGByUIDCompleted=0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appops_activity);
        fuckActivity.getIns().add(this);
        setTitle("应用管理");

        apopsasb1 = findViewById(R.id.apopsasb1);
        apopsasb2 = findViewById(R.id.apopsasb2);
        apopsasb3 = findViewById(R.id.apopsasb3);
        appopsab2 = findViewById(R.id.apopsab2);
        apopsab4 = findViewById(R.id.apopsab4);
        apopsab5 = findViewById(R.id.apopsab5);
        apopsaet1 = findViewById(R.id.apopsaet1);
        lv1 = findViewById(R.id.apopsalv1);
        /**
         * 如果uid为null，就走默认操作
         * 如果uid不为null，则走分身部分
         * */
        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");
        if(uid == null){
            uid=getMyUID();
        }
        apopsasb1.setChecked(true);

        apopsasb1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                switch_mode_tmp=b;
            }
        });

        apopsasb2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                switch_mode_autostart=b;
            }
        });

        apopsasb3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                switch_mode_all=b;
            }
        });

        appopsab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchStr = apopsaet1.getText().toString();
                pkginfos = multiFunc.indexOfPKGS(appopsActivity.this,searchStr,pkginfos,checkboxs,0);
                showPKGS(lv1);
            }
        });

        apopsab4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog show = showMyDialog(appopsActivity.this,"提示","正在禁用应用联网,请稍后(可能会出现无响应，请耐心等待)....");
                preventDismissDialog(show);
               view.post(new Runnable() {
                   @Override
                   public void run() {
                       if((switch_mode_tmp || switch_mode_tmp==false) && switch_mode_autostart && switch_mode_all){
                           auto_start_clicked(0,true,show);
                       }

                       if((switch_mode_tmp || switch_mode_tmp==false) && switch_mode_autostart==false && switch_mode_all){
                           default_clicked(0,true,show);
                       }

                       if((switch_mode_tmp || switch_mode_tmp==false) && switch_mode_autostart && switch_mode_all==false){
                           auto_start_clicked(0,false,show);
                       }

                       if((switch_mode_tmp || switch_mode_tmp==false) && switch_mode_autostart==false && switch_mode_all ==false){
                           default_clicked(0,false,show);
                       }
                   }
               });
            }
        });

        apopsab5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog show = showMyDialog(appopsActivity.this,"提示","正在启用应用联网,请稍后(可能会出现无响应，请耐心等待)....");
                preventDismissDialog(show);
               view.post(new Runnable() {
                   @Override
                   public void run() {
                       if((switch_mode_tmp || switch_mode_tmp==false) && switch_mode_autostart && switch_mode_all){
                           auto_start_clicked(1,true,show);
                       }

                       if((switch_mode_tmp || switch_mode_tmp==false) && switch_mode_autostart==false && switch_mode_all){
                           default_clicked(1,true,show);
                       }

                       if((switch_mode_tmp || switch_mode_tmp==false) && switch_mode_autostart && switch_mode_all==false){
                           auto_start_clicked(1,false,show);
                       }

                       if((switch_mode_tmp || switch_mode_tmp==false) && switch_mode_autostart==false && switch_mode_all ==false){
                           default_clicked(1,false,show);
                       }
                   }
               });
            }
        });

        lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                PKGINFO pkginfo = pkginfos.get(i);
                try {
                    PackageInfo packageInfo = getPackageManager().getPackageInfo(pkginfo.getPkgname(), 0);
                    Intent intent = new Intent(appopsActivity.this,appopsInfoActivity.class);
                    intent.putExtra("pkgname",pkginfo.getPkgname());
                    intent.putExtra("uid",uid);
                    startActivity(intent);
                } catch (PackageManager.NameNotFoundException e) {
                    Toast.makeText(appopsActivity.this, "未安装该应用", Toast.LENGTH_SHORT).show();
                }

            }
        });

        lv1.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                nowItemIndex=i;
                nowItemView=view;
                createLVMenu();
                return false;
            }
        });
    }

    //跳转到系统自带的应用详情界面
    private void intoSYSApp(int i){
        PKGINFO pkginfo = pkginfos.get(i);
        Intent intent2 = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent2.setData(Uri.parse("package:" + pkginfo.getPkgname()));
        startActivity(intent2);
    }

    //长按listview中的元素，显示一个菜单选项
    private void createLVMenu(){
        PackageManager pm = getPackageManager();
        PKGINFO pkginfo = pkginfos.get(nowItemIndex);
        lv1.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                contextMenu.add(0,0,0,"复制信息");
                try {
                    PackageInfo packageInfo= pm.getPackageInfo(pkginfo.getPkgname(),0);
                    contextMenu.add(0,1,0,"跳转至应用详情");
                    contextMenu.add(0,2,0,"导出所有勾选应用包名");
                    contextMenu.add(0,3,0,"导出并附加所有勾选应用包名");
                    contextMenu.add(0,4,0,"提取应用");
                    contextMenu.add(0,5,0,"卸载应用");
                } catch (PackageManager.NameNotFoundException e) {
                    contextMenu.add(0,6,0,"尝试安装应用");
                }
            }
        });

    }

    private Boolean extractAssertFile(String sysupfile,String filesDir){
        File sysupF = new File(sysupfile);
        File fileD = new File(filesDir);
        if(!fileD.exists()){
            fileD.mkdirs();
        }
        if(!sysupF.exists()){
            extactAssetsFile(this,"bar.sh",sysupfile);
        }
        return sysupF.exists();
    }

    private boolean installAPKS(String apksFilePath){
        String filesDir =getMyHomeFilesPath(this);
        String barfile = filesDir+"/bar.sh";
        if(extractAssertFile(barfile,filesDir)){
            Toast.makeText(this, "禁用脚本已存在", Toast.LENGTH_SHORT).show();
            String cmdstr = "sh "+barfile+" inapks " + apksFilePath;
            CMD cmd = new CMD(cmdstr);
            return cmd.getResultCode() ==0;
        }else{
            Toast.makeText(this, "apks安装脚本无法获取，请退出重试或者重新安装app", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(appopsActivity.this);
            alertDialog.setTitle("警告");
            alertDialog.setMessage("apks安装脚本没有找到,请补全脚本再尝试安装.");
            alertDialog.setPositiveButton("继续", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    jump(appopsActivity.this,importToolsActivity.class);
                }
            });
            alertDialog.setNegativeButton("补全组件脚本", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    jump(appopsActivity.this,importToolsActivity.class);
                }
            });
            alertDialog.show();
        }
     return false;
    }

    //安装本地文件
    private void installLocalPKG(){
        AlertDialog show = showMyDialog(appopsActivity.this,"提示","正在安装应用,请稍后(可能会出现无响应，请耐心等待)....");
        preventDismissDialog(show);
        nowItemView.post(new Runnable() {
            @Override
            public void run() {
                makeWP makewp = new makeWP();
                int hit=0;
                for (int i = 0; i < checkboxs.size(); i++) {
                    if(checkboxs.get(i)){
                        PKGINFO pkginfo = pkginfos.get(i);
                        if(getPathByLastNameType(pkginfo.getApkpath()).equals("apks")){
                            installAPKS(pkginfo.getApkpath());
                        }else{
                            CMD cmd = new CMD(makewp.getInstallLocalPkgCMD(uid, pkginfo.getApkpath()));
                            checkCMDResult(cmd,"成功安装","安装失败");
                        }
                        hit++;
                    }
                }
                if(hit ==0){
                    PKGINFO pkginfo = pkginfos.get(nowItemIndex);
                    if(getPathByLastNameType(pkginfo.getApkpath()).equals("apks")){
                        installAPKS(pkginfo.getApkpath());
                    }else{
                        CMD cmd = new CMD(makewp.getInstallLocalPkgCMD(uid, pkginfo.getApkpath()));
                        checkCMDResult(cmd,"成功安装","安装失败");
                    }

                }
                multiFunc.dismissDialog(show);
            }
        });
    }

    private void checkCMDResult(CMD cmd,String msg , String msg2){
        if( cmd.getResultCode() ==0){
            Toast.makeText(appopsActivity.this, msg, Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(appopsActivity.this, msg2+" :: "+cmd.getResult(), Toast.LENGTH_SHORT).show();
        }
    }

    //卸载应用
    private void uninstallPKG(){
        AlertDialog show = showMyDialog(appopsActivity.this,"提示","正在卸载应用,请稍后(可能会出现无响应，请耐心等待)....");
        preventDismissDialog(show);
        nowItemView.post(new Runnable() {
            @Override
            public void run() {
                makeWP makewp = new makeWP();
                int hit=0;
                for (int i = 0; i < checkboxs.size(); i++) {
                    if(checkboxs.get(i)){
                        PKGINFO pkginfo = pkginfos.get(i);
                        CMD cmd = new CMD(makewp.getUninstallPkgByUIDCMD(uid, pkginfo.getPkgname()));
                        checkCMDResult(cmd,"成功卸载","卸载失败");
                        hit++;
                    }
                }
                if(hit ==0){
                    PKGINFO pkginfo = pkginfos.get(nowItemIndex);
                    CMD cmd = new CMD(makewp.getUninstallPkgByUIDCMD(uid, pkginfo.getPkgname()));
                    checkCMDResult(cmd,"成功卸载","卸载失败");
                }
                multiFunc.dismissDialog(show);
            }
        });
    }

    //提取apk文件
    private void extractPKGFileToLocal(){
        AlertDialog show = showMyDialog(appopsActivity.this,"提示","正在提取应用,请稍后(可能会出现无响应，请耐心等待)....");
        preventDismissDialog(show);
        File cacheDir = this.getExternalCacheDir();
        nowItemView.post(new Runnable() {
            @Override
            public void run() {
                String myStorageHomePath = cacheDir.toString()+"/apks";
                File file = new File(myStorageHomePath);
                if(!file.exists()){
                    //创建/sdcard/Android/data/包名/cache文件夹,可以不需要申请存储权限实现
                    cacheDir.mkdirs();
                    boolean mkdirs = file.mkdirs();
                }
                int hit=0;
                for (int i = 0; i < checkboxs.size(); i++) {
                    if(checkboxs.get(i)){
                        PKGINFO pkginfo = pkginfos.get(i);
                        String apkpath = pkginfo.getApkpath();
                        String outpath = myStorageHomePath+"/"+pkginfo.getPkgname()+".apk";
                        copyFile(apkpath,outpath);
                        hit++;
                    }
                }
                if(hit == 0){
                    PKGINFO pkginfo = pkginfos.get(nowItemIndex);
                    String apkpath = pkginfo.getApkpath();
                    String outpath = myStorageHomePath+"/"+pkginfo.getPkgname()+".apk";
                    copyFile(apkpath,outpath);
                }

                multiFunc.dismissDialog(show);
                Toast.makeText(appopsActivity.this, "文件保存在: "+myStorageHomePath, Toast.LENGTH_SHORT).show();
            }
        });

    }

    //导出包名列表到本地
    private void extractPKGList(Boolean isApp){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < checkboxs.size(); i++) {
            if(checkboxs.get(i)){
                PKGINFO pkginfo = pkginfos.get(i);
                sb.append(pkginfo.getPkgname()+"\n");
            }
        }
        File cacheDir = this.getExternalCacheDir();
        String myStorageHomePath = cacheDir.toString()+"/pkgs";
        String outFile = myStorageHomePath+"/pkglist.txt";
        File file = new File(myStorageHomePath);
        File file2 = new File(outFile);
        if(!file.exists()){
            //创建/sdcard/Android/data/包名/cache文件夹,可以不需要申请存储权限实现
            cacheDir.mkdirs();
            boolean mkdirs = file.mkdirs();
        }
        if(file.exists()){
            if(file2.exists() && isApp == false){
                file2.delete();
            }
            try {
                file2.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(fileTools.writeDataToPath(sb.toString(),outFile,isApp)){
                Toast.makeText(this, "保存在: " + outFile, Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case 0:
                PKGINFO pkginfo = pkginfos.get(nowItemIndex);
                ClipboardManager cpm = (ClipboardManager) appopsActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                cpm.setText(pkginfo.toString());
                Toast.makeText(appopsActivity.this, "已复制", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                intoSYSApp(nowItemIndex);
                break;
            case 2:
                extractPKGList(false);
                break;
            case 3:
                extractPKGList(true);
                break;
            case 4:
                extractPKGFileToLocal();
                break;
            case 5:
                uninstallPKG();
                break;
            case 6:
                installLocalPKG();
                break;

        }
        return super.onContextItemSelected(item);
    }

    private void getUserEnablePKGS(){
        multiFunc.queryUserEnablePKGS(this,pkginfos,checkboxs,0);
    }

    //获取启用的应用程序
    private void getEnablePKGS(){
        multiFunc.queryEnablePKGS(this,pkginfos,checkboxs,0);
    }

    //获取对应的应用程序
    private void getUserPKGS(){
        multiFunc.queryUserPKGS(this,pkginfos,checkboxs,0);
    }

    //获取对应的应用程序
    private void getPKGS(){
        multiFunc.queryPKGS(this,pkginfos,checkboxs,0);
    }

    private void showPKGS(ListView listView){
        PKGINFOAdapter pkginfoAdapter = new PKGINFOAdapter(pkginfos, appopsActivity.this, checkboxs);
        listView.setAdapter(pkginfoAdapter);
    }

    private  void default_clicked(int mode,boolean isall,AlertDialog show){
        if(isall){
            for (int i = 0; i < checkboxs.size(); i++) {
                PKGINFO pkginfo = pkginfos.get(i);
                default_click(pkginfo,mode);
            }
        }else{
            for (int i = 0; i < checkboxs.size(); i++) {
                if(checkboxs.get(i)){
                    PKGINFO pkginfo = pkginfos.get(i);
                    default_click(pkginfo,mode);
                }
            }
        }
        multiFunc.dismissDialog(show);
    }

    private void default_click(PKGINFO pkginfo,int mode){
        String cmdStr = ipMange.disableAppByAPPUIDCMD(pkginfo.getApkuid());
        if(mode == 1){
            cmdStr = ipMange.enableAppByAPPUIDCMD(pkginfo.getApkuid());
        }
        CMD cmd = new CMD(cmdStr);
        if(cmd.getResultCode() == 0){
            Toast.makeText(appopsActivity.this, pkginfo.getAppname() +" 已经执行完成", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(appopsActivity.this, pkginfo.getAppname() +" 看样子不能执行", Toast.LENGTH_SHORT).show();
        }
    }

    private void auto_start(PKGINFO pkginfo,String magiskDir,String filepath,int mode){
        String cmdStr = "[ -d "+magiskDir+" ] && echo '"+ipMange.disableAppByAPPUIDCMD(pkginfo.getApkuid()) +" #"+pkginfo.getAppname()+"' >> "+filepath +" && chmod 755 "+filepath;
        if(mode == 1){
            cmdStr = "[ -d "+magiskDir+" ] && echo '"+ipMange.enableAppByAPPUIDCMD(pkginfo.getApkuid()) +" #"+pkginfo.getAppname()+"' >> "+filepath +" && chmod 755 "+filepath;
        }
        CMD cmd = new CMD(cmdStr);
        if(cmd.getResultCode() != 0){
            Toast.makeText(appopsActivity.this, "需要安装alpha面具", Toast.LENGTH_SHORT).show();
        }
        Log.i("auto_start :: ",cmd.getResultCode() + " -- "+cmd.getResult());
    }

    private  void auto_start_clicked(int mode,boolean isall,AlertDialog show){
        String autoStartScriptName = "disableIptables.sh";
        if(mode == 1){
            autoStartScriptName = "enableIptables.sh";
        }
        String filepath=magiskDir + "/" + autoStartScriptName;
        String firstcmd="[ -f "+filepath +" ] && rm -rf "+ filepath;
        CMD cmd1 = new CMD(firstcmd);
        cmd1.getResultCode();
        if(isall){
            for (int i = 0; i < checkboxs.size(); i++) {
                PKGINFO pkginfo = pkginfos.get(i);
                auto_start(pkginfo,magiskDir,filepath,mode);
            }
        }else{
            for (int i = 0; i < checkboxs.size(); i++) {
                if(checkboxs.get(i)){
                    PKGINFO pkginfo = pkginfos.get(i);
                    auto_start(pkginfo,magiskDir,filepath,mode);
                }
            }
        }
        multiFunc.dismissDialog(show);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE,0,0,"显示所有应用");
        menu.add(Menu.NONE,1,1,"显示所有应用(包括禁用)");
        menu.add(Menu.NONE,2,2,"显示用户安装的应用");
        menu.add(Menu.NONE,3,3,"显示用户安装的应用(包括禁用)");
        menu.add(Menu.NONE,4,4,"选择本地应用");
        menu.add(Menu.NONE,5,5,"退出");
        return super.onCreateOptionsMenu(menu);
    }

    private void getPKGByUID(String cmdstr){
        AlertDialog show = showMyDialog(appopsActivity.this,"提示","正在检索用户 "+uid+" 下安装的应用,请稍后(可能会出现无响应，请耐心等待)....");
        preventDismissDialog(show);
        Handler handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                if(msg.what==0){
                    showPKGS(lv1);
                    multiFunc.dismissDialog(show);
                }
            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                pkginfos.clear();
                checkboxs.clear();
                CMD cmd = new CMD(cmdstr);
                String result = cmd.getResult();
                String[] split = result.split("\n");
                if(split != null){
                    for (String s : cmd.getResult().split("\n")) {
                        PackageManager pm = getPackageManager();
                        PackageInfo packageInfo = null;
                        try {
                            packageInfo = pm.getPackageInfo(s, 0);
                            checkBoxs(pkginfos, checkboxs, packageInfo, pm);
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    Collections.sort(pkginfos, new Comparator<PKGINFO>() {
                        @Override
                        public int compare(PKGINFO pkginfo, PKGINFO t1) {
                            return pkginfo.getAppname().compareTo(t1.getAppname());
                        }
                    });
                }
                sendHandlerMSG(handler,0);
            }
        }).start();

    }

    private void selectLocalFile(){
        permissionRequest.getExternalStorageManager(appopsActivity.this);
        execFileSelect(appopsActivity.this,appopsActivity.this,"请选择要安装的文件");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        makeWP wp = new makeWP();
        switch (itemId){
            case 0:
                if(uid == null || uid.equals(getMyUID())){
                    getEnablePKGS();
                    showPKGS(lv1);
                }else{
                    getPKGByUID(wp.getPkgByUIDCMD(uid));
                }
                break;
            case 1:
                if(uid == null || uid.equals(getMyUID())){
                    getPKGS();
                    showPKGS(lv1);
                }else{
                    getPKGByUID(wp.getPkgByUIDCMD(uid));
                }

                break;
            case 2:
                if(uid == null|| uid.equals(getMyUID())){
                    getUserEnablePKGS();
                    showPKGS(lv1);
                }else{
                    getPKGByUID(wp.getUserPkgByUIDCMD(uid));
                }

                break;
            case 3:
                if(uid == null|| uid.equals(getMyUID())){
                    getUserPKGS();
                    showPKGS(lv1);
                }else{
                    getPKGByUID(wp.getUserPkgByUIDCMD(uid));
                }

                break;
            case 4:
                selectLocalFile();
                break;
            case 5:
                fuckActivity.getIns().killall();
                ;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            clearList(pkginfos,checkboxs);
            PackageManager pm = getPackageManager();
            String storage = Environment.getExternalStorageDirectory().toString();
            if(data.getClipData() != null) {//有选择多个文件
                int count = data.getClipData().getItemCount();
                for(int i =0;i<count;i++){
                    Uri uri = data.getClipData().getItemAt(i).getUri();
                    String filePath = storage + "/" +uri.getPath().replaceAll("/document/primary:","");
                    if(getPathByLastNameType(filePath).equals("apks") || getPathByLastNameType(filePath).equals("apk")){
                        try {
                            PackageInfo packageInfo = pm.getPackageArchiveInfo(filePath, PackageManager.GET_PERMISSIONS);
                            checkBoxs(pkginfos,checkboxs,packageInfo,pm);
                        }catch (Exception e){
                            Drawable d = ContextCompat.getDrawable(appopsActivity.this,R.drawable.ic_launcher_foreground);
                            pkginfos.add(new PKGINFO(getPathByLastName(filePath),"未知",filePath,"未知","未知", d,new File(filePath).length()));
                            checkboxs.add(false);
                        }
                    }

                }

            } else if(data.getData() != null) {//只有一个文件咯
                Uri uri = data.getData();
                String filePath = storage + "/" +uri.getPath().replaceAll("/document/primary:","");
                if(getPathByLastNameType(filePath).equals("apks") || getPathByLastNameType(filePath).equals("apk")){
                    try {
                        PackageInfo packageInfo = pm.getPackageArchiveInfo(filePath, PackageManager.GET_PERMISSIONS);
                        checkBoxs(pkginfos,checkboxs,packageInfo,pm);
                    }catch (Exception e){
                        Drawable d = ContextCompat.getDrawable(appopsActivity.this,R.drawable.ic_launcher_foreground);
                        pkginfos.add(new PKGINFO(getPathByLastName(filePath),"未知",filePath,"未知","未知", d,new File(filePath).length()));
                        checkboxs.add(false);
                    }
                }
            }
            showPKGS(lv1);

        }
    }
}
