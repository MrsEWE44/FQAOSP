package org.fqaosp.myActivitys;

/**
 *
 * 用于更改组件状态
 * 可以禁用/启用应用的service、activity
 * 可以授权/撤销应用的permission
 *
 * */

import static org.fqaosp.utils.multiFunc.checkBoxs;
import static org.fqaosp.utils.multiFunc.preventDismissDialog;

import android.Manifest;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.fqaosp.R;
import org.fqaosp.adapter.PKGINFOAdapter;
import org.fqaosp.entity.PKGINFO;
import org.fqaosp.utils.CMD;
import org.fqaosp.utils.fuckActivity;
import org.fqaosp.utils.iptablesManage;
import org.fqaosp.utils.makeWP;
import org.fqaosp.utils.multiFunc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class appopsActivity extends AppCompatActivity {

    private Button apopsab1,appopsab2,apopsab3,apopsab4,apopsab5;
    private ListView lv1;
    private EditText apopsaet1;

    private ArrayList<PKGINFO> pkginfos = new ArrayList<>();
    private ArrayList<Boolean> checkboxs = new ArrayList<>();
    private String uid;
    private iptablesManage ipMange = new iptablesManage();

    private String magiskDir="/data/adb/post-fs-data.d";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appops_activity);
        fuckActivity.getIns().add(this);
        setTitle("应用管理");
        apopsab1 = findViewById(R.id.apopsab1);
        appopsab2 = findViewById(R.id.apopsab2);
        apopsab3 = findViewById(R.id.apopsab3);
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
        apopsab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < checkboxs.size(); i++) {
                    if(checkboxs.get(i)){
                        PKGINFO pkginfo = pkginfos.get(i);
                        String cmdStr = ipMange.disableAppByAPPUIDCMD(pkginfo.getApkuid());
                        CMD cmd = new CMD(cmdStr);
                        if(cmd.getResultCode() == 0){
                            Toast.makeText(appopsActivity.this, pkginfo.getAppname() +" 已经禁用完成", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(appopsActivity.this, pkginfo.getAppname() +" 看样子不能禁用", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
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

        apopsab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < checkboxs.size(); i++) {
                    if(checkboxs.get(i)){
                        PKGINFO pkginfo = pkginfos.get(i);
                        String cmdStr = ipMange.enableAppByAPPUIDCMD(pkginfo.getApkuid());
                        CMD cmd = new CMD(cmdStr);
                        if(cmd.getResultCode() == 0){
                            Toast.makeText(appopsActivity.this, pkginfo.getAppname() +" 已经启用完成", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(appopsActivity.this, pkginfo.getAppname() +" 看样子不能启用", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });


        apopsab4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String autoStartScriptName = "disableIptables.sh";
                String filepath=magiskDir + "/" + autoStartScriptName;
                String firstcmd="[ -f "+filepath +" ] && rm -rf "+ filepath;
                CMD cmd1 = new CMD(firstcmd);
                cmd1.getResultCode();
                for (int i = 0; i < checkboxs.size(); i++) {
                    if(checkboxs.get(i)){
                        PKGINFO pkginfo = pkginfos.get(i);
                        String cmdStr = "[ -d "+magiskDir+" ] && echo "+ipMange.disableAppByAPPUIDCMD(pkginfo.getApkuid()) +" #"+pkginfo.getAppname()+" >> "+filepath +" && chmod 755 "+filepath;
                        CMD cmd = new CMD(cmdStr);
                        if(cmd.getResultCode() != 0){
                            Toast.makeText(appopsActivity.this, "需要安装alpha面具", Toast.LENGTH_SHORT).show();
                        }
                        Log.i("ipdis :: ",cmd.getResultCode()+ " -- " + cmd.getResult());
                    }
                }

            }
        });

        apopsab5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String autoStartScriptName = "enableIptables.sh";
                String filepath=magiskDir + "/" + autoStartScriptName;
                String firstcmd="[ -f "+filepath +" ] && rm -rf "+ filepath;
                CMD cmd1 = new CMD(firstcmd);
                cmd1.getResultCode();
                for (int i = 0; i < checkboxs.size(); i++) {
                    if(checkboxs.get(i)){
                        PKGINFO pkginfo = pkginfos.get(i);
                        String cmdStr = "[ -d "+magiskDir+" ] && echo "+ipMange.enableAppByAPPUIDCMD(pkginfo.getApkuid()) +" #"+pkginfo.getAppname()+" >> "+filepath +" && chmod 755 "+filepath;
                        CMD cmd = new CMD(cmdStr);
                        if(cmd.getResultCode() != 0){
                            Toast.makeText(appopsActivity.this, "需要安装alpha面具", Toast.LENGTH_SHORT).show();
                        }
                        Log.i("ipen :: ",cmd.getResultCode()+ " -- " + cmd.getResult());
                    }
                }
            }
        });

        lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(appopsActivity.this,appopsInfoActivity.class);
                intent.putExtra("pkgname",pkginfos.get(i).getPkgname());
                intent.putExtra("uid",uid);
                startActivity(intent);
            }
        });

        lv1.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(uid != null){
                    PKGINFO pkginfo = pkginfos.get(i);
                    Intent intent2 = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent2.setData(Uri.parse("package:" + pkginfo.getPkgname()));
                    startActivity(intent2);
                }
                return false;
            }
        });
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE,0,0,"显示所有应用");
        menu.add(Menu.NONE,1,1,"显示所有应用(包括禁用)");
        menu.add(Menu.NONE,2,2,"显示用户安装的应用");
        menu.add(Menu.NONE,3,3,"显示用户安装的应用(包括禁用)");
        menu.add(Menu.NONE,4,4,"退出");
        return super.onCreateOptionsMenu(menu);
    }


    private void getPKGByUID(String cmdstr){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(appopsActivity.this);
        alertDialog.setTitle("提示");
        alertDialog.setMessage("正在检索用户 "+uid+" 下安装的应用,请稍后(可能会出现无响应，请耐心等待)....");
        AlertDialog show = alertDialog.show();
        preventDismissDialog(show);
        ExecutorService cacheThreadPool = Executors.newFixedThreadPool(4);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pkginfos.clear();
                checkboxs.clear();
                CMD cmd = new CMD(cmdstr);
                if(cmd.getResultCode() ==0){
                    for (String s : cmd.getResult().split("\n")) {
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                PackageManager pm = getPackageManager();
                                PackageInfo packageInfo = null;
                                try {
                                    packageInfo = pm.getPackageInfo(s, 0);
                                } catch (PackageManager.NameNotFoundException e) {
                                    e.printStackTrace();
                                }
                                checkBoxs(pkginfos, checkboxs, packageInfo.applicationInfo, pm);
                            }
                        };
                        cacheThreadPool.execute(runnable);
                    }
                    cacheThreadPool.shutdown();
                    while(true){
                        if(cacheThreadPool.isTerminated()){
                            showPKGS(lv1);
                            multiFunc.dismissDialog(show);
                            break;
                        }
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        makeWP wp = new makeWP();
        switch (itemId){
            case 0:
                if(uid == null){
                    getEnablePKGS();
                    showPKGS(lv1);
                }else{
                    getPKGByUID(wp.getPkgByUIDCMD(uid));
                }
                break;
            case 1:
                if(uid == null){
                    getPKGS();
                    showPKGS(lv1);
                }else{
                    getPKGByUID(wp.getPkgByUIDCMD(uid));
                }

                break;
            case 2:
                if(uid == null){
                    getUserPKGS();
                    showPKGS(lv1);
                }else{
                    getPKGByUID(wp.getUserPkgByUIDCMD(uid));
                }

                break;
            case 3:
                if(uid == null){
                    getUserEnablePKGS();
                    showPKGS(lv1);
                }else{
                    getPKGByUID(wp.getUserPkgByUIDCMD(uid));
                }

                break;
            case 4:
                fuckActivity.getIns().killall();
                ;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


}
