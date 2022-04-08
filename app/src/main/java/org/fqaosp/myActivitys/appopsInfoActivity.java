package org.fqaosp.myActivitys;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.fqaosp.R;
import org.fqaosp.adapter.USERAdapter;
import org.fqaosp.utils.CMD;
import org.fqaosp.utils.fuckActivity;

import java.util.ArrayList;

public class appopsInfoActivity extends AppCompatActivity {

    private ArrayList<String> list = new ArrayList<>();
    private ArrayList<Boolean> checkboxs = new ArrayList<>();
    private ListView lv1;
    private PackageManager pm;
    private PackageInfo packageInfo;
    private ApplicationInfo appInfo;
    private Button b1,b2,b3,b4,b5,b6;
    private String pkgname;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appopsinfo_activity);
        fuckActivity.getIns().add(this);
        Intent intent = getIntent();
        pkgname = intent.getStringExtra("pkgname");
        pm = getPackageManager();
        try {
            packageInfo = pm.getPackageInfo(pkgname, PackageManager.GET_PERMISSIONS|PackageManager.GET_ACTIVITIES);
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
    }

    private  void getPKGActivitys(PackageInfo packageInfo){
        clearList();
        for (ActivityInfo activity : packageInfo.activities) {
            Log.d("aaa : ",activity.name);
            list.add(activity.name);
            checkboxs.add(false);
        }
    }

    private  void getPKGPermission(PackageInfo packageInfo){
        clearList();
        for (String permission : packageInfo.requestedPermissions) {
            list.add(permission);
            checkboxs.add(false);
        }
    }

    private  void getPKGServices(PackageManager pm,ApplicationInfo appinfo){
        clearList();
        PackageInfo archiveInfo = pm.getPackageArchiveInfo(appinfo.sourceDir, PackageManager.GET_SERVICES);
        for (ServiceInfo service : archiveInfo.services) {
            list.add(service.name);
            checkboxs.add(false);
        }
    }

    private void showListView(ListView listView){
        USERAdapter userAdapter = new USERAdapter(list, appopsInfoActivity.this, checkboxs);
        listView.setAdapter(userAdapter);
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
                String cmdstr = "";
                switch (ss){
                    case 0:
                        cmdstr="pm revoke "+pkgname + " " + pkgcate;
                        break;
                    case 1:
                    case 2:
                        cmdstr="pm disable " +pkgname+"/"+ pkgcate;
                        break;
                    case 3:
                        cmdstr="pm grant "+pkgname + " " + pkgcate;
                        break;
                    case 4:
                    case 5:
                        cmdstr="pm enable " +pkgname+"/"+ pkgcate;
                        break;
                }
                CMD cmd = new CMD(cmdstr);
                if(cmd.getResultCode() == 0){
                    Toast.makeText(appopsInfoActivity.this, msg, Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(appopsInfoActivity.this, msg2, Toast.LENGTH_SHORT).show();
                }
            }
        }



    }

    private void clickButton(){
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickFun(0,"禁用成功","禁用失败");
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickFun(1,"关闭服务成功","关闭服务失败");
            }
        });
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickFun(2,"关闭活动成功","关闭活动失败");
            }
        });
        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickFun(3,"授权成功","授权失败");
            }
        });
        b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickFun(4,"开启服务成功","开启服务失败");
            }
        });
        b6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickFun(5,"开启活动项成功","开启活动项失败");
            }
        });


    }


}
