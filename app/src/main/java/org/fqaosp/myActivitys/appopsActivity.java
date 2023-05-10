package org.fqaosp.myActivitys;

/**
 *
 * 用于更改组件状态
 * 可以禁用/启用应用的service、activity
 * 可以授权/撤销应用的permission
 *
 * */

import static org.fqaosp.utils.fileTools.copyFile;
import static org.fqaosp.utils.fileTools.execDirSelect;
import static org.fqaosp.utils.fileTools.execFileSelect;
import static org.fqaosp.utils.fileTools.getMyHomeFilesPath;
import static org.fqaosp.utils.fileTools.getPathByLastName;
import static org.fqaosp.utils.fileTools.getPathByLastNameType;
import static org.fqaosp.utils.multiFunc.checkBoxs;
import static org.fqaosp.utils.multiFunc.checkShizukuPermission;
import static org.fqaosp.utils.multiFunc.checkTools;
import static org.fqaosp.utils.multiFunc.clearList;
import static org.fqaosp.utils.multiFunc.getMyUID;
import static org.fqaosp.utils.multiFunc.getRunTraverseCMDStr;
import static org.fqaosp.utils.multiFunc.isSuEnable;
import static org.fqaosp.utils.multiFunc.sendHandlerMSG;
import static org.fqaosp.utils.multiFunc.showCMDInfoMSG;
import static org.fqaosp.utils.multiFunc.showInfoMsg;
import static org.fqaosp.utils.multiFunc.showMyDialog;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import org.fqaosp.R;
import org.fqaosp.adapter.PKGINFOAdapter;
import org.fqaosp.entity.PKGINFO;
import org.fqaosp.utils.CMD;
import org.fqaosp.utils.appopsCmdStr;
import org.fqaosp.utils.fileTools;
import org.fqaosp.utils.fuckActivity;
import org.fqaosp.utils.makeWP;
import org.fqaosp.utils.multiFunc;
import org.fqaosp.utils.permissionRequest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class appopsActivity extends AppCompatActivity {

    private Button appopsab2,apopsab6;
    private ListView lv1;
    private EditText apopsaet1;
    private ArrayList<PKGINFO> pkginfos = new ArrayList<>();
    private ArrayList<Boolean> checkboxs = new ArrayList<>();
    private String uid;
    private Spinner apopsasp1,apopsasp2;
    private int nowItemIndex=-1;
    private View nowItemView = null;
    private boolean isRoot=false;
    private boolean isDisable=false;
    private String apops_permis[] = {"通话/短信相关", "存储","剪切板","电池优化","后台运行","摄像头","麦克风","定位","日历","传感器扫描","通知","待机模式","待机活动","应用联网"};
    private String apops_opt[] = {"默认", "拒绝","允许","仅在运行时允许"};
    private String apops_opt2[] = {"活跃", "工作集","常用","极少使用","受限"};
    private String apops_opt3[] = {"允许","拒绝"};
    private String script_name = "fqtools.sh";
    private int apops_permis_index,apops_opt_index,mode=0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appops_activity);
        fuckActivity.getIns().add(this);
        setTitle("应用管理");
        isRoot=isSuEnable();
        if(isRoot == false && checkShizukuPermission(1) == false){
            Toast.makeText(this, "没有被授权,将无法正常使用该功能", Toast.LENGTH_SHORT).show();
        }

        /**
         * 如果uid为null，就走默认操作
         * 如果uid不为null，则走分身部分
         * */
        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");
        if(uid == null){
            uid=getMyUID();
        }
        initBt();
    }

    private void initBt(){
        appopsab2 = findViewById(R.id.apopsab2);
        apopsab6 = findViewById(R.id.apopsab6);
        apopsaet1 = findViewById(R.id.apopsaet1);
        lv1 = findViewById(R.id.apopsalv1);
        apopsasp1 = findViewById(R.id.apopsasp1);
        apopsasp2 = findViewById(R.id.apopsasp2);
        apopsasp1.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, apops_permis));
        apopsasp2.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, apops_opt));
        checkTools(this);
        clickedBt();
    }

    private void clickedBt(){
        Context con = this;
        Activity activity = this;

        apopsasp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                apops_permis_index = i;
                if(apops_permis_index==11||apops_permis_index==13){
                    mode=1;
                    apopsasp2.setAdapter(new ArrayAdapter<String>(con, android.R.layout.simple_list_item_1, apops_opt3));
                }else if(apops_permis_index==12){
                    mode=2;
                    apopsasp2.setAdapter(new ArrayAdapter<String>(con, android.R.layout.simple_list_item_1, apops_opt2));
                }else{
                    mode=0;
                    apopsasp2.setAdapter(new ArrayAdapter<String>(con, android.R.layout.simple_list_item_1, apops_opt));
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        apopsasp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                apops_opt_index = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        appopsab2.setOnClickListener((v)->{
            String searchStr = apopsaet1.getText().toString();
            pkginfos = multiFunc.indexOfPKGS(activity,searchStr,pkginfos,checkboxs,0);
            showPKGS(lv1);
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
                    Toast.makeText(con, "未安装该应用", Toast.LENGTH_SHORT).show();
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

        //应用appops权限更改
        apopsab6.setOnClickListener((v)->{
            showCMDInfoMSG(con,false,getRunTraverseCMDStr(pkginfos,checkboxs,spliceCMDStr()),isRoot,"正在应用更改,请稍后(可能会出现无响应，请耐心等待)....","应用更改结束.");
        });

    }

    //拼接命令参数字符串
    private String spliceCMDStr(){
        StringBuilder sb = new StringBuilder();
        String cmdHead = "appops set --uid $pp ";
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
                sb.append("am set-inactive $pp "+modestr);
                break;
            case 12:
                sb.append("am set-standby-bucket $pp " + modestr );
                break;
            case 13:
                appopsCmdStr acs = new appopsCmdStr();
                sb.append(modestr.equals("true")?acs.enableAppByAPPUIDCMD("$pp"):acs.disableAppByAPPUIDCMD("$pp"));
                break;
        }
        if(apops_permis_index < 11){
            sb.append(cmdWrite);
        }
        return sb.toString();
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
                    if(isDisable){
                        contextMenu.add(0,10,0,"启用");
                    }else{
                        contextMenu.add(0,11,0,"禁用");
                    }
                    contextMenu.add(0,4,0,"尝试降级安装");
                    contextMenu.add(0,8,0,"尝试覆盖安装");
                    contextMenu.add(0,9,0,"尝试安装debug应用");
                    contextMenu.add(0,5,0,"提取应用");
                    contextMenu.add(0,6,0,"卸载应用");
                } catch (PackageManager.NameNotFoundException e) {
                    contextMenu.add(0,7,0,"尝试安装应用");
                    contextMenu.add(0,9,0,"尝试安装debug应用");
                }
            }
        });

    }

    //安装本地文件
    private void installLocalPKG(int install_mode){
        int hit=0;
        appopsCmdStr acs = new appopsCmdStr();
        StringBuilder sb = new StringBuilder();
        String filesDir =getMyHomeFilesPath(this);
        String barfile = filesDir+"/"+script_name;
        //安装apks文件
        String apkscmdstr = "sh "+barfile+" inapks $pp";
        sb.append("aaa=(");
        for (int i = 0; i < checkboxs.size(); i++) {
            if(checkboxs.get(i)){
                PKGINFO pkginfo = pkginfos.get(i);
                String apkpath = pkginfo.getApkpath();
                sb.append("\""+apkpath+"\" ");
                hit++;
            }
        }
        if(hit ==0){
            PKGINFO pkginfo = pkginfos.get(nowItemIndex);
            String apkpath = pkginfo.getApkpath();
            sb.append("\""+apkpath+"\" ");
        }
        String cmdstr = "";
        switch (install_mode){
            case 0:
                cmdstr = acs.getInstallLocalPkgCMD(uid, "$pp");
                break;
            case 1:
                cmdstr = acs.getInstallLocalPkgOnDowngradeCMD(uid, "$pp");
                break;
            case 2:
                cmdstr = acs.getInstallLocalPkgOnDebugCMD(uid, "$pp");
                break;
            case 3:
                cmdstr = acs.getInstallLocalPkgOnExistsCMD(uid, "$pp");
                break;
        }
        sb.append(");for pp in ${aaa[@]};do if [[ `echo $pp |grep \".apks\"` != \"\" ]];then "+apkscmdstr+";else "+cmdstr+" fi;done;");
        showCMDInfoMSG(appopsActivity.this,true,sb.toString(),isRoot,"正在安装应用,请稍后(可能会出现无响应，请耐心等待)....","安装应用结束.");
    }

    //卸载应用
    private void uninstallPKG(){
        makeWP makewp = new makeWP();
        String cmdstr = makewp.getUninstallPkgByUIDCMD(uid, "$pp");
        showCMDInfoMSG(appopsActivity.this,true,getRunTraverseCMDStr(pkginfos,checkboxs,cmdstr),isRoot,"正在卸载应用,请稍后(可能会出现无响应，请耐心等待)....","卸载应用结束.");
    }

    //修改应用状态,禁用或者启用
    private void changePKGState(){
        makeWP makewp = new makeWP();
        String cmdstr = isDisable?makewp.getChangePkgOnEnableByUIDCMD(uid, "$pp"):makewp.getChangePkgOnDisableByUIDCMD(uid,"$pp");
        showCMDInfoMSG(appopsActivity.this,true,getRunTraverseCMDStr(pkginfos,checkboxs,cmdstr),isRoot,"正在"+(isDisable?"启动":"禁用")+"应用,请稍后(可能会出现无响应，请耐心等待)....",(isDisable?"启动":"禁用")+"应用结束.");
    }

    //提取apk文件
    private void extractPKGFileToLocal(){
        ProgressDialog show = showMyDialog(appopsActivity.this,"正在提取应用,请稍后(可能会出现无响应，请耐心等待)....");
        File cacheDir = this.getExternalCacheDir();
        String myStorageHomePath = cacheDir.toString()+"/apks";
        Handler handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if(msg.what == 0){
                    show.dismiss();
                    showInfoMsg(appopsActivity.this,"提示","文件保存在: "+myStorageHomePath);
                }
            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {

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
                sendHandlerMSG(handler,0);
            }
        }).start();

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
                showInfoMsg(appopsActivity.this,"提示","保存在: " + outFile);
            }else{
                showInfoMsg(appopsActivity.this,"错误","导出包名列表失败");
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
                installLocalPKG(1);
                break;
            case 5:
                extractPKGFileToLocal();
                break;
            case 6:
                uninstallPKG();
                break;
            case 7:
                installLocalPKG(0);
                break;
            case 8:
                installLocalPKG(3);
                break;
            case 9:
                installLocalPKG(2);
                break;
            case 10:
            case 11:
                changePKGState();
                break;

        }
        return super.onContextItemSelected(item);
    }

    private void showPKGS(ListView listView){
        PKGINFOAdapter pkginfoAdapter = new PKGINFOAdapter(pkginfos, appopsActivity.this, checkboxs);
        listView.setAdapter(pkginfoAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE,0,0,"显示所有应用");
        menu.add(Menu.NONE,1,1,"显示所有应用(包括禁用)");
        menu.add(Menu.NONE,2,2,"显示用户安装的应用");
        menu.add(Menu.NONE,3,3,"显示用户安装的应用(包括禁用)");
        menu.add(Menu.NONE,4,4,"显示被禁用的应用");
        menu.add(Menu.NONE,5,5,"选择本地应用");
        menu.add(Menu.NONE,6,6,"选择本地安装包文件夹");
        menu.add(Menu.NONE,7,7,"帮助");
        menu.add(Menu.NONE,8,8,"退出");
        return super.onCreateOptionsMenu(menu);
    }

    private void getPKGByUID(String cmdstr){
        ProgressDialog show = showMyDialog(appopsActivity.this,"正在检索用户 "+uid+" 下安装的应用,请稍后(可能会出现无响应，请耐心等待)....");
        Handler handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                if(msg.what==0){
                    showPKGS(lv1);
                    show.dismiss();
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

    private void selectLocalDir(){
        permissionRequest.getExternalStorageManager(appopsActivity.this);
        execDirSelect(appopsActivity.this,appopsActivity.this,"请选择要安装的文件");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        isDisable=false;
        int itemId = item.getItemId();
        makeWP wp = new makeWP();
        switch (itemId){
            case 0:
                if(uid == null || uid.equals(getMyUID())){
                    multiFunc.queryEnablePKGS(this,pkginfos,checkboxs,0);
                    showPKGS(lv1);
                }else{
                    getPKGByUID(wp.getPkgByUIDCMD(uid));
                }
                break;
            case 1:
                if(uid == null || uid.equals(getMyUID())){
                    multiFunc.queryPKGS(this,pkginfos,checkboxs,0);
                    showPKGS(lv1);
                }else{
                    getPKGByUID(wp.getPkgByUIDCMD(uid));
                }

                break;
            case 2:
                if(uid == null|| uid.equals(getMyUID())){
                    multiFunc.queryUserEnablePKGS(this,pkginfos,checkboxs,0);
                    showPKGS(lv1);
                }else{
                    getPKGByUID(wp.getUserPkgByUIDCMD(uid));
                }

                break;
            case 3:
                if(uid == null|| uid.equals(getMyUID())){
                    multiFunc.queryUserPKGS(this,pkginfos,checkboxs,0);
                    showPKGS(lv1);
                }else{
                    getPKGByUID(wp.getUserPkgByUIDCMD(uid));
                }
                break;
            case 4:
                isDisable=true;
                if(uid == null || uid.equals(getMyUID())){
                    multiFunc.queryDisablePKGS(this,pkginfos,checkboxs,0);
                    showPKGS(lv1);
                }else{
                    getPKGByUID(wp.getDisablePkgByUIDCMD(uid));
                }

                break;
            case 5:
                selectLocalFile();
                break;
            case 6:
                selectLocalDir();
                break;
            case 7:
                showInfoMsg(this,"帮助信息","该页面是用于应用管理的,支持应用提取、详情跳转、卸载应用、导出应用信息、安装apks/apk应用，需要安装fqtools,如果没有安装，则会自动跳转安装页面，按照页面提示安装即可。\r\n" +
                        "1.搜索框，支持中英文搜索，无大小写限制.\r\n" +
                        "2.长按应用列表会出现相关操作菜单，根据自己需求点击即可。支持批量操作。\r\n" +
                        "3.右上角\"选择本地应用\",支持选择apks进行安装，传统apk文件可以加载出图标。\r\n" +
                        "4.点击应用列表，则会进入到应用配置页面.\r\n" +
                        "5.右上角\"选择本地安装包文件夹\"，选择一个文件夹，会自动安装里面所有apk/apks文件.如果你是通过mt直接从/data/app连同文件夹一起提取的，效果会更好。\r\n" +
                        "6.应用更改，该按钮的作用是用来配置应用权限(部分权限配置需要root权限)，可以批量或者勾选操作，不勾选则默认全部生效。左边是你想要操作的权限名称，右边是设置的模式。\r\n假如你想要将fqaosp的存储权限给拒绝掉：可以先点击左边的权限列表，选中\"存储\"，然后在右边列表中选择\"拒绝\"，最后勾选中fqaosp(如果不勾选而直接点击按钮，则会默认生效所有应用，请斟酌！)，点击\"应用更改\"即可生效。\r\n");
                break;
            case 8:
                fuckActivity.getIns().killall();
                ;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void addPKGINFO(PackageManager pm,Uri uri , String storage){
        String filePath = storage + "/" +uri.getPath().replaceAll("/document/primary:","");
        String nameType = getPathByLastNameType(filePath);
        if(nameType.equals("apk")){
            PackageInfo packageInfo = pm.getPackageArchiveInfo(filePath, PackageManager.GET_PERMISSIONS);
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            pkginfos.add(new PKGINFO(applicationInfo.packageName, applicationInfo.loadLabel(pm).toString(), filePath,applicationInfo.uid+"",packageInfo.versionName, applicationInfo.loadIcon(pm),new File(filePath).length())) ;
            checkboxs.add(false);
        }else if(nameType.equals("apks")){
            Drawable d = ContextCompat.getDrawable(appopsActivity.this,R.drawable.ic_launcher_foreground);
            pkginfos.add(new PKGINFO(getPathByLastName(filePath),"未知",filePath,"未知","未知", d,new File(filePath).length()));
            checkboxs.add(false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String storage = Environment.getExternalStorageDirectory().toString();
        Context that = this;
        if(requestCode == 0){
            clearList(pkginfos,checkboxs);
            PackageManager pm = getPackageManager();
            if(data.getClipData() != null) {//有选择多个文件
                int count = data.getClipData().getItemCount();
                for(int i =0;i<count;i++){
                    Uri uri = data.getClipData().getItemAt(i).getUri();
                    addPKGINFO(pm,uri,storage);
                }
            } else if(data.getData() != null) {//只有一个文件咯
                Uri uri = data.getData();
                addPKGINFO(pm,uri,storage);
            }
            showPKGS(lv1);
        }

        //安装文件夹里面所有apk文件
        if(requestCode == 43){
            if(data.getData() != null) {//只有一个文件咯
                Uri uri = data.getData();
                String filePath = storage + "/" +uri.getPath().replaceAll("/tree/primary:","");
                String filesDir =getMyHomeFilesPath(that);
                String barfile = filesDir+"/"+script_name;
                String cmdstr = "";
                if(isRoot){
                    cmdstr = "sh "+barfile+" inapkonpath " + filePath;
                }else if(isRoot == false && checkShizukuPermission(6)){
                    String tmpPath = "/data/local/tmp/aa.apk";
                    cmdstr = " for p in $(find "+filePath+" -name \"*.apk\") ;do cp $p "+tmpPath+"; pm install "+tmpPath+"; rm -rf "+tmpPath+"; done; exit 0;";
                }else{
                    showInfoMsg(that,"错误","该功能需要adb或者root权限才能使用!!!!");
                }
                showCMDInfoMSG(that,true,cmdstr,isRoot,"正在安装"+filePath+"路径下的应用,请稍后(可能会出现无响应，请耐心等待)....","安装"+filePath+"路径下的应用结束.");
            }
        }
    }

}
