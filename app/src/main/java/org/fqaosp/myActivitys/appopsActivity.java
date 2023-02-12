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
import static org.fqaosp.utils.fileTools.extactAssetsFile;
import static org.fqaosp.utils.fileTools.getMyHomeFilesPath;
import static org.fqaosp.utils.fileTools.getMyStorageHomePath;
import static org.fqaosp.utils.fileTools.getPathByLastName;
import static org.fqaosp.utils.fileTools.getPathByLastNameType;
import static org.fqaosp.utils.fileTools.writeDataToPath;
import static org.fqaosp.utils.multiFunc.checkBoxs;
import static org.fqaosp.utils.multiFunc.checkShizukuPermission;
import static org.fqaosp.utils.multiFunc.clearList;
import static org.fqaosp.utils.multiFunc.dismissDialogHandler;
import static org.fqaosp.utils.multiFunc.getMyUID;
import static org.fqaosp.utils.multiFunc.isSuEnable;
import static org.fqaosp.utils.multiFunc.preventDismissDialog;
import static org.fqaosp.utils.multiFunc.sendHandlerMSG;
import static org.fqaosp.utils.multiFunc.showImportToolsDialog;
import static org.fqaosp.utils.multiFunc.showInfoMsg;
import static org.fqaosp.utils.multiFunc.showMyDialog;

import android.app.AlertDialog;
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
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
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
import org.fqaosp.utils.makeWP;
import org.fqaosp.utils.multiFunc;
import org.fqaosp.utils.netUtils;
import org.fqaosp.utils.permissionRequest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class appopsActivity extends AppCompatActivity {

    private Button appopsab2,apopsab4,apopsab5,apopsab6;
    private ListView lv1;
    private EditText apopsaet1;
    private Switch apopsasb1 , apopsasb2,apopsasb3;
    private ArrayList<PKGINFO> pkginfos = new ArrayList<>();
    private ArrayList<Boolean> checkboxs = new ArrayList<>();
    private String uid;
    private Spinner apopsasp1,apopsasp2;
    private netUtils ipMange = new netUtils();
    private boolean switch_mode_tmp,switch_mode_autostart,switch_mode_all;
    private String magiskDir="/data/adb/post-fs-data.d";
    private int nowItemIndex=-1;
    private View nowItemView = null;
    private boolean isRoot=false;
    private String apops_permis[],apops_opt[];
    private int apops_permis_index,apops_opt_index;

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
        apopsasb1 = findViewById(R.id.apopsasb1);
        apopsasb2 = findViewById(R.id.apopsasb2);
        apopsasb3 = findViewById(R.id.apopsasb3);
        appopsab2 = findViewById(R.id.apopsab2);
        apopsab4 = findViewById(R.id.apopsab4);
        apopsab5 = findViewById(R.id.apopsab5);
        apopsab6 = findViewById(R.id.apopsab6);
        apopsaet1 = findViewById(R.id.apopsaet1);
        lv1 = findViewById(R.id.apopsalv1);
        apopsasp1 = findViewById(R.id.apopsasp1);
        apopsasp2 = findViewById(R.id.apopsasp2);
        apops_permis = new String[]{"通话/短信相关", "存储","剪切板","电池优化","后台运行","摄像头","麦克风","定位","日历","传感器扫描","通知"};
        apops_opt = new String[]{"默认", "拒绝","允许","仅在运行时允许"};
        apopsasb1.setChecked(true);
        apopsasp1.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, apops_permis));
        apopsasp2.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, apops_opt));

        clickedBt();
    }

    private void clickedBt(){
        Context con = this;

        apopsasp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                apops_permis_index = i;
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

        appopsab2.setOnClickListener((v)->{
            String searchStr = apopsaet1.getText().toString();
            pkginfos = multiFunc.indexOfPKGS(appopsActivity.this,searchStr,pkginfos,checkboxs,0);
            showPKGS(lv1);
        });

        apopsab4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isRoot){
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
                }else{
                    showMyDialog(con,"提示","本功能需要root才能正常使用");
                }

            }
        });

        apopsab5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isRoot){
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
                }else{
                    showMyDialog(con,"提示","本功能需要root才能正常使用");
                }

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

        //应用appops权限更改
        apopsab6.setOnClickListener((v)->{
            AlertDialog show = showMyDialog(appopsActivity.this,"提示","正在应用更改,请稍后(可能会出现无响应，请耐心等待)....");
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
            v.post(new Runnable() {
                @Override
                public void run() {
                    int hit=0;
                    StringBuilder sb = new StringBuilder();
                    sb.append("aaa=(");
                    Log.d("asdf",apops_permis[apops_permis_index]+" -- " + apops_opt[apops_opt_index]);
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
                    sb.append(");for pp in ${aaa[@]};do "+spliceCMDStr()+";done;");

                    CMD cmd = getCMD(sb.toString());
                    checkCMDResult(cmd,"权限修改完成","权限修改出现错误");
                    sendHandlerMSG(handler,0);
                }
            });
        });

    }

    //拼接命令参数字符串
    private String spliceCMDStr(){
        StringBuilder sb = new StringBuilder();
        String cmdHead = "appops set --uid $pp ";
        String cmdWrite = "appops write-settings ";
        String modestr="";

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
                break;
        }
        sb.append(cmdWrite);

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

    private CMD getCMD(String cmdstr){
        String myStorageHomePath = getMyStorageHomePath(this);
        String tmpFile = myStorageHomePath + "/cache/temp.sh";
        if(isRoot){
            return new CMD(cmdstr);
        }else{
            Boolean aBoolean = writeDataToPath(cmdstr, tmpFile, false);
            if(aBoolean){
                return new CMD(new String[]{"sh",tmpFile});
            }else{
                Log.e("error","write temp script error");
            }
        }
        return null;
    }

    //安装文件夹里面所有apk文件
    private boolean installApkOnDir(String dir){
        Context that = this;
        String filesDir =getMyHomeFilesPath(that);
        String barfile = filesDir+"/bar.sh";
        if(isRoot && extractAssertFile(barfile,filesDir)){
            Log.d("installApkOnDir","禁用脚本已存在");
            String cmdstr = "sh "+barfile+" inapkonpath " + dir;
            CMD cmd = getCMD(cmdstr);
            return cmd.getResultCode() ==0;
        }else if(isRoot == false && checkShizukuPermission(6)){
            String tmpPath = "/data/local/tmp/aa.apk";
            String cmdstr2 = " for p in $(find "+dir+" -name \"*.apk\") ;do cp $p "+tmpPath+"; pm install "+tmpPath+"; rm -rf "+tmpPath+"; done; exit 0;";
            CMD cmd = getCMD(cmdstr2);
            return cmd.getResultCode() ==0;
        }else{
            showImportToolsDialog(that,"apks安装脚本无法获取，请退出重试或者重新安装app","apks安装脚本没有找到,请补全脚本再尝试安装.");
        }
        return false;
    }

    //安装apks文件
    private boolean installAPKS(String apksFilePath){
        String filesDir =getMyHomeFilesPath(this);
        String barfile = filesDir+"/bar.sh";
        if(extractAssertFile(barfile,filesDir)){
            Log.d("installAPKS","禁用脚本已存在");
            String cmdstr = "sh "+barfile+" inapks " + apksFilePath;
            CMD cmd = isRoot ? new CMD(cmdstr) : new CMD(cmdstr.split(" "));
            return cmd.getResultCode() ==0;
        }else{
            showImportToolsDialog(this,"apks/apk安装脚本无法获取，请退出重试或者重新安装app","apks/apk安装脚本没有找到,请补全脚本再尝试安装.");
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
                            String cmdstr = makewp.getInstallLocalPkgCMD(uid, pkginfo.getApkpath());
                            CMD cmd = getCMD(cmdstr);
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
                        String cmdstr = makewp.getInstallLocalPkgCMD(uid, pkginfo.getApkpath());
                        CMD cmd = getCMD(cmdstr);
                        checkCMDResult(cmd,"成功安装","安装失败");
                    }

                }
                multiFunc.dismissDialog(show);
            }
        });
    }

    private void checkCMDResult(CMD cmd,String msg , String msg2){
        if( cmd.getResultCode() ==0){
            Log.d("checkCMDResult",msg);
        }else{
            Log.d("checkCMDResult",msg2+" :: "+cmd.getResult());
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
                        String cmdstr = makewp.getUninstallPkgByUIDCMD(uid, pkginfo.getPkgname());
                        CMD cmd = isRoot ? new CMD(cmdstr) : new CMD(cmdstr.split(" "));
                        checkCMDResult(cmd,"成功卸载","卸载失败");
                        hit++;
                    }
                }

                if(hit ==0){
                    PKGINFO pkginfo = pkginfos.get(nowItemIndex);
                    String cmdstr = makewp.getUninstallPkgByUIDCMD(uid, pkginfo.getPkgname());
                    CMD cmd = isRoot ? new CMD(cmdstr) : new CMD(cmdstr.split(" "));
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
        menu.add(Menu.NONE,5,5,"选择本地安装包文件夹");
        menu.add(Menu.NONE,6,6,"帮助");
        menu.add(Menu.NONE,7,7,"退出");
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

    private void selectLocalDir(){
        permissionRequest.getExternalStorageManager(appopsActivity.this);
        execDirSelect(appopsActivity.this,appopsActivity.this,"请选择要安装的文件");
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
                selectLocalDir();
                break;
            case 6:
                showInfoMsg(this,"帮助信息","该页面是用于应用管理的,支持应用提取、详情跳转、卸载应用、导出应用信息、安装apks/apk应用，需要安装fqtools,如果没有安装，则会自动跳转安装页面，按照页面提示安装即可。\r\n" +
                        "1.禁用联网，勾选列出来的应用，即可批量禁用联网权限.\r\n" +
                        "2.启用联网，勾选列出来的应用，即可批量禁用联网权限.\r\n" +
                        "3.上面有个搜索框，支持中英文搜索，无大小写限制.\r\n" +
                        "4.长按应用列表会出现相关操作菜单，根据自己需求点击即可。支持批量操作。\r\n" +
                        "5.右上角\"选择本地应用\",支持选择apks进行安装，传统apk文件可以加载出图标。\r\n" +
                        "6.点击应用列表，则会进入到应用配置页面.\r\n" +
                        "7.右上角\"选择本地安装包文件夹\"，选择一个文件夹，会自动安装里面所有apk/apks文件.如果你是通过mt直接从/data/app连同文件夹一起提取的，效果会更好。\r\n" +
                        "8.应用更改，该按钮的作用是用来配置应用权限，可以批量或者勾选操作，不勾选则默认全部生效。左边是你想要操作的权限名称，右边是设置的模式。\r\n假如你想要将fqaosp的存储权限给拒绝掉：可以先点击左边的权限列表，选中\"存储\"，然后在右边列表中选择\"拒绝\"，最后勾选中fqaosp(如果不勾选而直接点击按钮，则会默认生效所有应用，请斟酌！)，点击\"应用更改\"即可生效。\r\n");
                break;
            case 7:
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
            pkginfos.add(new PKGINFO(applicationInfo.packageName, applicationInfo.loadLabel(pm).toString(), applicationInfo.sourceDir,applicationInfo.uid+"",packageInfo.versionName, applicationInfo.loadIcon(pm),new File(filePath).length())) ;
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

        if(requestCode == 43){
            if(data.getData() != null) {//只有一个文件咯
                Uri uri = data.getData();
                String filePath = storage + "/" +uri.getPath().replaceAll("/tree/primary:","");
                AlertDialog show = showMyDialog(appopsActivity.this,"提示","正在安装应用,请稍后(可能会出现无响应，请耐心等待)....");
                Handler handler = dismissDialogHandler(0,show);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        installApkOnDir(filePath);
                        sendHandlerMSG(handler,0);
                    }
                }).start();
            }
        }
    }

}
