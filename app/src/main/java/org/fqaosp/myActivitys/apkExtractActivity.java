package org.fqaosp.myActivitys;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.fqaosp.R;
import org.fqaosp.adapter.PKGINFOAdapter;
import org.fqaosp.entity.PKGINFO;
import org.fqaosp.utils.fuckActivity;
import org.fqaosp.utils.multiFunc;

import java.io.File;
import java.util.ArrayList;

/**
 *
 * 提取apk文件部分功能
 *
 *
 * */

public class apkExtractActivity extends AppCompatActivity {

    private ArrayList<PKGINFO> pkginfos = new ArrayList<>();
    private ArrayList<Boolean> checkboxs = new ArrayList<>();
    private ListView listView1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.apk_extract_activity);
        fuckActivity.getIns().add(this);
        Button b1 = findViewById(R.id.aeab1);
        listView1 = findViewById(R.id.aealv1);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < checkboxs.size(); i++) {
                    if (checkboxs.get(i)) {
                        PKGINFO pkginfo = pkginfos.get(i);
                        //获取手机内部存储根目录
                        String storage=Environment.getExternalStorageDirectory().toString();
                        String myExterStoreCacheDir=getExternalCacheDir().toString();
                        //拼接提取后文件输出位置
                        String FileOutPath=storage+"/Download/"+pkginfo.getPkgname()+".apk";
                        File appHome = new File(myExterStoreCacheDir);
                        if(!appHome.exists()){
                           appHome.mkdirs();
                        }
                        if(multiFunc.copyFile(pkginfo.getApkpath(),FileOutPath)){
                            Toast.makeText(apkExtractActivity.this, "提取 "+pkginfo.getAppname() + " 成功", Toast.LENGTH_SHORT).show();
                            Toast.makeText(apkExtractActivity.this, "提取的文件保存在 "+FileOutPath, Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(apkExtractActivity.this, "提取 "+pkginfo.getAppname() + " 失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

    }


    private void getPKGS(){
        checkboxs.clear();
        pkginfos.clear();
        //提取所有已安装的应用列表
        multiFunc.queryPKGS(this,pkginfos,checkboxs,0);
    }

    private void getUserPKGS(){
        checkboxs.clear();
        pkginfos.clear();
        //提取所有已安装的应用列表
        multiFunc.queryUserPKGS(this,pkginfos,checkboxs,0);
    }

    private void showPKGS(ListView listView){
        PKGINFOAdapter pkginfoAdapter = new PKGINFOAdapter(pkginfos, apkExtractActivity.this, checkboxs);
        listView.setAdapter(pkginfoAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE,0,0,"显示所有应用");
        menu.add(Menu.NONE,1,1,"显示用户安装的应用");
        menu.add(Menu.NONE,2,2,"退出");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId){
            case 0:
                getPKGS();
                showPKGS(listView1);
                break;
            case 1:
                getUserPKGS();
                showPKGS(listView1);
                break;
            case 2:
                fuckActivity.getIns().killall();
                ;
        }
        return super.onOptionsItemSelected(item);
    }
}
