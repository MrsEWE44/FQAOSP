package org.fqaosp.myActivitys;

import android.Manifest;
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
import org.fqaosp.utils.multiFunc;

import java.util.ArrayList;

public class appopsActivity extends AppCompatActivity {

    private Button apopsab1;
    private ListView lv1;

    private ArrayList<PKGINFO> pkginfos = new ArrayList<>();
    private ArrayList<Boolean> checkboxs = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appops_activity);
        fuckActivity.getIns().add(this);
        apopsab1 = findViewById(R.id.apopsab1);
        lv1 = findViewById(R.id.apopsalv1);
        apopsab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < checkboxs.size(); i++) {
                    if(checkboxs.get(i)){
                        PKGINFO pkginfo = pkginfos.get(i);
                        CMD cmd = new CMD("pm revoke "+pkginfo.getPkgname() + " " + Manifest.permission.WRITE_EXTERNAL_STORAGE + " && pm revoke " + pkginfo.getPkgname() + " " + Manifest.permission.READ_EXTERNAL_STORAGE + " && pm revoke " + pkginfo.getPkgname()+ " " + Manifest.permission.MANAGE_EXTERNAL_STORAGE);
                        if(cmd.getResultCode() == 0){
                            Toast.makeText(appopsActivity.this, pkginfo.getAppname() +" 已经禁用完成", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(appopsActivity.this, pkginfo.getAppname() +" 看样子不能禁用", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(appopsActivity.this,appopsInfoActivity.class);
                intent.putExtra("pkgname",pkginfos.get(i).getPkgname());
                startActivity(intent);
            }
        });

        lv1.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                PKGINFO pkginfo = pkginfos.get(i);
                Intent intent2 = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent2.setData(Uri.parse("package:" + pkginfo.getPkgname()));
                startActivity(intent2);
                return false;
            }
        });


    }


    //获取对应的应用程序
    private void getUserPKGS(){
        checkboxs.clear();
        pkginfos.clear();
        multiFunc.queryUserPKGS(this,pkginfos,checkboxs,0);
    }

    //获取对应的应用程序
    private void getPKGS(){
        checkboxs.clear();
        pkginfos.clear();
        multiFunc.queryPKGS(this,pkginfos,checkboxs,0);
    }

    private void showPKGS(ListView listView){
        PKGINFOAdapter pkginfoAdapter = new PKGINFOAdapter(pkginfos, appopsActivity.this, checkboxs);
        listView.setAdapter(pkginfoAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE,0,0,"显示用户安装应用");
        menu.add(Menu.NONE,1,1,"显示所有应用");
        menu.add(Menu.NONE,2,2,"退出");

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId){
            case 0:
                getUserPKGS();
                showPKGS(lv1);
                break;
            case 1:
                getPKGS();
                showPKGS(lv1);
                break;
            case 2:
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
