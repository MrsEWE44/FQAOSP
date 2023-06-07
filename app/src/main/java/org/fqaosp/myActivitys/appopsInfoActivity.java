package org.fqaosp.myActivitys;

/**
 *
 * 用于更改组件状态
 * 可以禁用/启用应用的service、activity
 * 可以授权/撤销应用的permission
 *
 * */

import android.app.Activity;
import android.app.AppOpsManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.fqaosp.R;
import org.fqaosp.adapter.APPOPSINFOAdapter;
import org.fqaosp.entity.PKGINFO;
import org.fqaosp.utils.dialogUtils;
import org.fqaosp.utils.fuckActivity;
import org.fqaosp.utils.textUtils;

import java.util.ArrayList;

public class appopsInfoActivity extends AppCompatActivity {

    private ArrayList<String> list = new ArrayList<>();
    private ArrayList<Boolean> checkboxs = new ArrayList<>();
    private ArrayList<Boolean> switbs = new ArrayList<>();

    private ListView lv1;
    private PackageManager pm;
    private PackageInfo packageInfo;
    private ApplicationInfo appInfo;
    private Button b1,apasearchbt1;
    private EditText apaet1;
    private Switch apasb1,apasb2;
    private String pkgname,uid;
    private Boolean apasb1Bool,apasb2Bool;
    private int mode;
    private boolean isRoot = false,isADB=false;
    private dialogUtils du = new dialogUtils();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appopsinfo_activity);
        fuckActivity.getIns().add(this);
        setTitle("应用详细操作");
        Intent intent = getIntent();
        isRoot = intent.getBooleanExtra("isRoot",false);
        isADB = intent.getBooleanExtra("isADB",false);
        pkgname = intent.getStringExtra("pkgname");
        uid = intent.getStringExtra("uid");
        pm = getPackageManager();
        try {
            packageInfo = pm.getPackageInfo(pkgname, PackageManager.GET_PERMISSIONS|PackageManager.GET_ACTIVITIES|PackageManager.GET_DISABLED_COMPONENTS);
            appInfo =  packageInfo.applicationInfo;
            ImageView iv1 = findViewById(R.id.apaiv1);
            TextView tv1 = findViewById(R.id.apatv1);
            TextView tv2 = findViewById(R.id.apatv2);
            initButton();
            lv1 = findViewById(R.id.apalv1);
            iv1.setImageDrawable(appInfo.loadIcon(pm));
            tv1.setText(appInfo.packageName);
            tv2.setText(appInfo.loadLabel(pm));
        } catch (PackageManager.NameNotFoundException e) {
            du.showInfoMsg(this,e.getClass().getName(),e.toString());
        }
        if(!isRoot){
            du.showInfoMsg(this,"提示","本功能需要root才能正常使用");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE,0,0,"显示活动项");
        menu.add(Menu.NONE,1,1,"显示服务项");
        menu.add(Menu.NONE,2,2,"显示权限列表");
        menu.add(Menu.NONE,3,3,"显示广播接收器列表");
        menu.add(Menu.NONE,4,4,"帮助");
        menu.add(Menu.NONE,5,5,"退出");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        ProgressDialog show = du.showMyDialog(this,"正在获取组件信息中(请稍后...)");
        Handler handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                if(msg.what ==0){
                    show.dismiss();
                    showListView(lv1);
                }else{
                    show.dismiss();
                }
            }
        };
        switch (itemId){
            case 0:
                getPKGActivitys(packageInfo);

                break;
            case 1:
                getPKGServices(pm,appInfo);
                break;
            case 2:
                getPKGPermission(packageInfo);
                break;
            case 3:
                getPKGReceivers(pm,appInfo);
                break;
            case 4:
                du.sendHandlerMSG(handler,1);
                du.showInfoMsg(this,"帮助信息","该页面是用于应用配置的,支持应用权限设置、服务禁/启用、活动项禁/启用，需要安装fqtools,如果没有安装，则会自动跳转安装页面，按照页面提示安装即可。\r\n" +
                        "1.右上角三个点，活动项，列出该应用的所有可修改的活动项，支持批量启动或者禁用.\r\n" +
                        "2.右上角三个点，服务项，列出该应用的所有可修改的服务项，支持批量启动或者禁用.\r\n"+
                        "3.右上角三个点，权限列表，列出该应用的所有可修改的权限列表，支持批量启动或者禁用.\r\n"+
                        "4.右上角三个点，广播接收器列表，列出该应用的所有可修改的广播接收器列表，支持批量启动或者禁用.\r\n"+
                        "5.勾选与未勾选，勾选即是勾选的，未勾选即使勾选以外的.\r\n"+
                        "6.应用更改，点击这个选项后，将会设置相反的状态，即如果该应用有个mm服务，它在现在是启用的状态，但是你勾选并选择了勾选，然后点击应用更改，那它就会变成禁用状态了，反之同理。\r\n" +
                        "7.搜索框，可以搜索相关服务、活动项、权限，不区分大小写.\r\n"
                );
                break;
            case 5:
                du.sendHandlerMSG(handler,1);
                fuckActivity.getIns().killall();
                ;
        }
        du.sendHandlerMSG(handler,0);
        return super.onOptionsItemSelected(item);
    }

    private  void checkMode(){
        switch (mode){
            case 0:
                getPKGActivitys(packageInfo);
                break;
            case 1:
                getPKGServices(pm,appInfo);
                break;
            case 2:
                getPKGPermission(packageInfo);
                break;
        }
    }

    private void clearList(){
        list.clear();
        checkboxs.clear();
        switbs.clear();
    }

    private  void getPKGActivitys(PackageInfo packageInfo){
        mode=0;
        clearList();
        if(packageInfo.activities != null){
            ArrayList<String> mainActivityList = new ArrayList<>();
            ArrayList<String> otherActivityList = new ArrayList<>();
            for (ActivityInfo activity : packageInfo.activities) {
                int enabledSetting = pm.getComponentEnabledSetting(new ComponentName(packageInfo.packageName, activity.name));
                if(activity.exported){
                    mainActivityList.add(activity.name);
                }else{
                    otherActivityList.add(activity.name);
                }
                if(enabledSetting == PackageManager.COMPONENT_ENABLED_STATE_DISABLED){
                    switbs.add(false);
                }else {
                    switbs.add(true);
                }
                checkboxs.add(false);
            }
            list.addAll(mainActivityList);
            list.addAll(otherActivityList);
        }else{
            Toast.makeText(this, packageInfo.applicationInfo.loadLabel(pm) + " 没有找到活动项哦!", Toast.LENGTH_SHORT).show();
        }
    }

    private  void getPKGPermission(PackageInfo packageInfo){
        mode=2;
        clearList();
        if(packageInfo.requestedPermissions != null){
           AppOpsManager opsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            for (String permission : packageInfo.requestedPermissions) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    String permissionToOp = AppOpsManager.permissionToOp(permission);
                    if(permissionToOp != null){
                        list.add(permission);
                        checkboxs.add(false);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            if((opsManager.unsafeCheckOpNoThrow(permissionToOp, android.os.Process.myUid(),packageInfo.packageName) == AppOpsManager.MODE_ALLOWED)){
                                switbs.add(true);
                            }else{
                                switbs.add(false);
                            }
                        }else{
                            if((opsManager.checkOpNoThrow(permissionToOp,android.os.Process.myUid(),packageInfo.packageName) == AppOpsManager.MODE_ALLOWED)){
                                switbs.add(true);
                            }else{
                                switbs.add(false);
                            }
                        }
                    }
                }
            }
        }

    }

    private  void getPKGServices(PackageManager pm,ApplicationInfo appinfo){
        mode=1;
        clearList();
        PackageInfo archiveInfo = pm.getPackageArchiveInfo(appinfo.sourceDir, PackageManager.GET_SERVICES);
        if(archiveInfo.services != null){
            for (ServiceInfo service : archiveInfo.services) {
                ComponentName componentName = new ComponentName(packageInfo.packageName, service.name);
                int enabledSetting = pm.getComponentEnabledSetting(componentName);
                if(enabledSetting == PackageManager.COMPONENT_ENABLED_STATE_DISABLED){
                    switbs.add(false);
                }else{
                    switbs.add(true);
                }
                list.add(service.name);
                checkboxs.add(false);
            }
        }else{
            Toast.makeText(this, appinfo.loadLabel(pm) + " 没有找到服务哦!", Toast.LENGTH_SHORT).show();
        }
    }

    private  void getPKGReceivers(PackageManager pm,ApplicationInfo appinfo){
        mode=1;
        clearList();
        PackageInfo archiveInfo = pm.getPackageArchiveInfo(appinfo.sourceDir, PackageManager.GET_RECEIVERS);
        if(archiveInfo.receivers != null){
            for (ActivityInfo receiver : archiveInfo.receivers) {
                ComponentName componentName = new ComponentName(packageInfo.packageName, receiver.name);
                int enabledSetting = pm.getComponentEnabledSetting(componentName);
                if(enabledSetting == PackageManager.COMPONENT_ENABLED_STATE_DISABLED){
                    switbs.add(false);
                }else{
                    switbs.add(true);
                }
                list.add(receiver.name);
                checkboxs.add(false);
            }

        }else{
            Toast.makeText(this, appinfo.loadLabel(pm) + " 没有找到广播接收器哦!", Toast.LENGTH_SHORT).show();
        }
    }

    private void sort(){
        ArrayList<String> list2 = new ArrayList<>();
        ArrayList<String> list3 = new ArrayList<>();
        for (int i = 0; i < switbs.size(); i++) {
            if(switbs.get(i)){
                list2.add(list.get(i));
            }else{
                list3.add(list.get(i));
            }
        }
        list.clear();
        switbs.clear();
        for (int i = 0; i < list3.size(); i++) {
            list.add(list3.get(i));
            switbs.add(false);
        }
        for (int i = 0; i < list2.size(); i++) {
            list.add(list2.get(i));
            switbs.add(true);
        }

    }

    private void showListView(ListView listView){
        sort();
        APPOPSINFOAdapter adapter = new APPOPSINFOAdapter(list, appopsInfoActivity.this, checkboxs, switbs,appInfo.packageName,mode,uid);
        listView.setAdapter(adapter);
    }

    private void initButton(){
        b1 = findViewById(R.id.apabt1);
        apasearchbt1 = findViewById(R.id.apasearchbt1);
        apaet1 =findViewById(R.id.apaet1);
        apasb1 =findViewById(R.id.apasb1);
        apasb2 =findViewById(R.id.apasb2);
        apasb1Bool =true;
        apasb2Bool =false;
        apasb1.setChecked(apasb1Bool);
        apasb2.setChecked(apasb2Bool);
        clickButton();
    }

    private void clickButton(){

        Activity that = this;
        apasb1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                apasb1Bool=b;
                apasb2Bool=!b;
                apasb2.setChecked(apasb2Bool);
            }
        });
        apasb2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                apasb1Bool=!b;
                apasb2Bool=b;
                apasb1.setChecked(apasb1Bool);
            }
        });
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList <PKGINFO> pplist =new ArrayList<>();
                //应用更改
                for (int i = 0; i < checkboxs.size(); i++) {
                    if(apasb1Bool && checkboxs.get(i)){
                        pplist.add(new PKGINFO(pkgname,list.get(i),list.get(i),null,switbs.get(i)?"false":"true",null,null));
                    }
                    if(apasb2Bool && !checkboxs.get(i)){
                        pplist.add(new PKGINFO(pkgname,list.get(i),list.get(i),null,switbs.get(i)?"false":"true",null,null));
                    }
                }
                du.showProcessBarDialogByCMD(appopsInfoActivity.this,pplist,"正在修改组件状态中...","当前正在修改的组件名称: ",10,null ,null,isRoot,uid,mode,null,null);
            }
        });

        apasearchbt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Handler handler = new Handler(){
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        if(msg.what == 0){
                            showListView(lv1);
                        }
                    }
                };
                String searchStr = apaet1.getText().toString();
                ArrayList<String> strings = new ArrayList<>();
                ArrayList<Boolean> switbs2 = new ArrayList<>();
                if(list.size() <1 || searchStr.isEmpty()){
                    checkMode();
                }
                checkboxs.clear();
                for (int i = 0; i < list.size(); i++) {
                    String s=list.get(i);
                    if(new textUtils().isIndexOfStr(s,searchStr)){
                        strings.add(s);
                        switbs2.add(switbs.get(i));
                        checkboxs.add(false);
                    }
                }
                list=strings;
                switbs=switbs2;
                du.sendHandlerMSG(handler,0);
            }
        });

    }

}
