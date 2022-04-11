package org.fqaosp.myActivitys;

/**
 *
 * 用于更改组件状态
 * 可以禁用/启用应用的service、activity
 * 可以授权/撤销应用的permission
 *
 * */

import android.app.AppOpsManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import org.fqaosp.adapter.USERAdapter;
import org.fqaosp.utils.CMD;
import org.fqaosp.utils.fuckActivity;
import org.fqaosp.utils.multiFunc;

import java.util.ArrayList;

public class appopsInfoActivity extends AppCompatActivity {

    private ArrayList<String> list = new ArrayList<>();
    private ArrayList<Boolean> checkboxs = new ArrayList<>();
    private ArrayList<Boolean> switbs = new ArrayList<>();

    private ListView lv1;
    private PackageManager pm;
    private PackageInfo packageInfo;
    private ApplicationInfo appInfo;
    private Button b1,b2,b3,b4,b5,b6;
    private String pkgname,uid;
    private int mode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appopsinfo_activity);
        fuckActivity.getIns().add(this);
        setTitle("应用详细操作");
        Intent intent = getIntent();
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
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE,0,0,"显示活动项");
        menu.add(Menu.NONE,1,1,"显示服务项");
        menu.add(Menu.NONE,2,2,"显示权限列表");
        menu.add(Menu.NONE,3,3,"退出");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId){
            case 0:
                getPKGActivitys(packageInfo);
                showListView(lv1);
                break;
            case 1:
                getPKGServices(pm,appInfo);
                showListView(lv1);
                break;
            case 2:
                getPKGPermission(packageInfo);
                showListView(lv1);
                break;
            case 3:
                fuckActivity.getIns().killall();
                ;
        }
        return super.onOptionsItemSelected(item);
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
            for (ActivityInfo activity : packageInfo.activities) {
                int enabledSetting = pm.getComponentEnabledSetting(new ComponentName(packageInfo.packageName, activity.name));
                if(enabledSetting == PackageManager.COMPONENT_ENABLED_STATE_DISABLED){
                    switbs.add(false);
                }else{
                    switbs.add(true);
                }
                list.add(activity.name);
                checkboxs.add(false);
            }
        }else{
            Toast.makeText(this, packageInfo.applicationInfo.loadLabel(pm) + " 没有找到活动项哦!", Toast.LENGTH_SHORT).show();
        }
    }

    private  void getPKGPermission(PackageInfo packageInfo){
        mode=2;
        clearList();
        if(packageInfo.requestedPermissions != null){
           AppOpsManager opsManager = (AppOpsManager) getSystemService(this.APP_OPS_SERVICE);
            for (String permission : packageInfo.requestedPermissions) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    String permissionToOp = AppOpsManager.permissionToOp(permission);
                    if(permissionToOp != null){
                        list.add(permission);
                        checkboxs.add(false);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            if((opsManager.unsafeCheckOpNoThrow(permissionToOp, packageInfo.applicationInfo.uid,packageInfo.packageName) == AppOpsManager.MODE_ALLOWED)){
                                switbs.add(true);
                            }else{
                                switbs.add(false);
                            }
                        }else{
                            if((opsManager.checkOpNoThrow(permissionToOp,packageInfo.applicationInfo.uid,packageInfo.packageName) == AppOpsManager.MODE_ALLOWED)){
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
                int enabledSetting = pm.getComponentEnabledSetting(new ComponentName(packageInfo.packageName, service.name));
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

    private void showListView(ListView listView){
        APPOPSINFOAdapter adapter = new APPOPSINFOAdapter(list, appopsInfoActivity.this, checkboxs, switbs,appInfo.packageName,mode,uid);
        listView.setAdapter(adapter);
    }

    private void initButton(){
        b1 = findViewById(R.id.apabt1);
        b2 = findViewById(R.id.apabt2);
        b3 = findViewById(R.id.apabt3);
        b4 = findViewById(R.id.apabt4);
        b5 = findViewById(R.id.apabt5);
        b6 = findViewById(R.id.apabt6);
        clickButton();
    }

    private void clickFun(int ss, String msg ,String msg2){
        for (int i = 0; i < checkboxs.size(); i++) {
            if(checkboxs.get(i)){
                String pkgcate = list.get(i);
                multiFunc.runAppopsCMD(appopsInfoActivity.this,pkgname,pkgcate,ss,msg,msg2,uid);
            }
        }
    }

    private void clickButton(){
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickFun(0,"撤销权限成功","撤销权限失败");
                getPKGPermission(packageInfo);
                showListView(lv1);
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickFun(1,"关闭服务成功","关闭服务失败");
                getPKGServices(pm,appInfo);
                showListView(lv1);
            }
        });
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickFun(2,"关闭活动成功","关闭活动失败");
                getPKGActivitys(packageInfo);
                showListView(lv1);
            }
        });
        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickFun(3,"授权成功","授权失败");
                getPKGPermission(packageInfo);
                showListView(lv1);
            }
        });
        b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickFun(4,"开启服务成功","开启服务失败");
                getPKGServices(pm,appInfo);
                showListView(lv1);
            }
        });
        b6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickFun(5,"开启活动项成功","开启活动项失败");
                getPKGActivitys(packageInfo);
                showListView(lv1);
            }
        });


    }


}
