package org.fqaosp.myActivitys;

import android.Manifest;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import org.fqaosp.threads.alertDialogThread;
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
        setTitle("apk提取");
        Button b1 = findViewById(R.id.aeab1);
        Button b2 = findViewById(R.id.aeab2);
        EditText aeaet1 = findViewById(R.id.aeaet1);
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
                        String cmd = "cp " + pkginfo.getApkpath() + " " + FileOutPath;
                        alertDialogThread dialogThread = new alertDialogThread(apkExtractActivity.this, "正在提取文件中...", cmd, "提示", "提取成功 " + FileOutPath, "提取失败");
                        dialogThread.start();
                    }
                }
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchStr = aeaet1.getText().toString();
                pkginfos = multiFunc.indexOfPKGS(apkExtractActivity.this,searchStr,pkginfos,checkboxs,0);
                showPKGS(listView1);
            }
        });

        listView1.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                PKGINFO pkginfo = pkginfos.get(i);
                ClipboardManager cpm = (ClipboardManager) apkExtractActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                cpm.setText(pkginfo.toString());
                Toast.makeText(apkExtractActivity.this, "已复制", Toast.LENGTH_SHORT).show();
                return false;
            }
        });


    }

    private void getUserEnablePKGS(){
        multiFunc.queryUserEnablePKGS(this,pkginfos,checkboxs,0);
    }

    private void getEnablePKGS(){
        multiFunc.queryEnablePKGS(this,pkginfos,checkboxs,0);
    }

    private void getPKGS(){
        //提取所有已安装的应用列表
        multiFunc.queryPKGS(this,pkginfos,checkboxs,0);
    }

    private void getUserPKGS(){
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
        menu.add(Menu.NONE,1,1,"显示所有应用(包括禁用)");
        menu.add(Menu.NONE,2,2,"显示用户安装的应用");
        menu.add(Menu.NONE,3,3,"显示用户安装的应用(包括禁用)");
        menu.add(Menu.NONE,4,4,"退出");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId){
            case 0:
                getEnablePKGS();
                showPKGS(listView1);
                break;
            case 1:
                getPKGS();
                showPKGS(listView1);
                break;
            case 2:
                getUserPKGS();
                showPKGS(listView1);
                break;
            case 3:
                getUserEnablePKGS();
                showPKGS(listView1);
                break;
            case 4:
                fuckActivity.getIns().killall();
                ;
        }
        return super.onOptionsItemSelected(item);
    }
}
