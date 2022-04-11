package org.fqaosp.myActivitys;

import static org.fqaosp.utils.multiFunc.execFileSelect;
import static org.fqaosp.utils.multiFunc.getMyHomeFilesPath;
import static org.fqaosp.utils.multiFunc.preventDismissDialog;
import static org.fqaosp.utils.multiFunc.queryUserPKGS;
import static org.fqaosp.utils.multiFunc.selectFile;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.fqaosp.R;
import org.fqaosp.adapter.PKGINFOAdapter;
import org.fqaosp.adapter.USERAdapter;
import org.fqaosp.entity.PKGINFO;
import org.fqaosp.threads.alertDialogThread;
import org.fqaosp.threads.cmdThread;
import org.fqaosp.utils.CMD;
import org.fqaosp.utils.fuckActivity;
import org.fqaosp.utils.multiFunc;
import org.fqaosp.utils.permissionRequest;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class apkDecompileActivity extends AppCompatActivity {

    private ArrayList<String> list = new ArrayList<>();
    private ArrayList<Boolean> checkboxs = new ArrayList<>();
    private ArrayList<PKGINFO> pkginfos = new ArrayList<>();
    private ListView lv1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.apk_decompile_activity);
        fuckActivity.getIns().add(this);
        setTitle("apk反编译");
        Button b1 = findViewById(R.id.adab1);
        Button b2 = findViewById(R.id.adab2);
        Button b3 = findViewById(R.id.adab3);
        Button b4 = findViewById(R.id.adab4);
        EditText adaet1 = findViewById(R.id.adaet1);
        lv1 = findViewById(R.id.adalv1);
        permissionRequest.getExternalStorageManager(apkDecompileActivity.this);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < checkboxs.size(); i++) {
                    if(checkboxs.get(i)){
                        String filesDir = getMyHomeFilesPath(apkDecompileActivity.this);
                        String filePath = pkginfos.size() > 0 ?pkginfos.get(i).getApkpath() : list.get(i);
                        PackageManager packageManager = getPackageManager();
                        PackageInfo archiveInfo = packageManager.getPackageArchiveInfo(filePath, 0);
                        String pkgname =  archiveInfo.packageName;
                        String storage = Environment.getExternalStorageDirectory().toString();
                        String outDir = storage+"/Android/data/"+getPackageName()+"/files/decompile/"+pkgname;
                        String cmd = "cd " + filesDir + " && sh de.sh " + outDir + " " + filePath;
                        alertDialogThread dialogThread = new alertDialogThread(apkDecompileActivity.this, "请稍后，正在反编译中...", cmd, "提示", "反编译成功 " + outDir, "反编译失败");
                        dialogThread.start();
                    }
                }
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUserEnablePKGS();
                showPKGS(lv1);
            }
        });

        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                execFileSelect(apkDecompileActivity.this,apkDecompileActivity.this,"请选择.apk文件");
            }
        });

        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchStr = adaet1.getText().toString();
                pkginfos = multiFunc.indexOfPKGS(apkDecompileActivity.this,searchStr,pkginfos,checkboxs,0);
                showPKGS(lv1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            clearList();
            String storage = Environment.getExternalStorageDirectory().toString();
            if(data.getClipData() != null) {//有选择多个文件
                int count = data.getClipData().getItemCount();
                for(int i =0;i<count;i++){
                    Uri uri = data.getClipData().getItemAt(i).getUri();
                    selectFile(apkDecompileActivity.this,storage,uri,list,checkboxs,"请选择正确的apk文件","apk");
                }
            } else if(data.getData() != null) {//只有一个文件咯
                Uri uri = data.getData();
                selectFile(apkDecompileActivity.this,storage,uri,list,checkboxs,"请选择正确的apk文件","apk");
            }

            showSelectApkPath(lv1);
        }
    }

    private void clearList(){
        checkboxs.clear();
        pkginfos.clear();
        list.clear();
    }

    private void getUserEnablePKGS(){
        multiFunc.queryUserEnablePKGS(this,pkginfos,checkboxs,0);
    }

    private void getEnablePKGS(){
        multiFunc.queryEnablePKGS(this,pkginfos,checkboxs,0);
    }

    //获取对应的应用程序
    private void getPKGS(){
       multiFunc.queryPKGS(this,pkginfos,checkboxs,0);
    }

    private void getUserPKGS(){
        queryUserPKGS(this,pkginfos,checkboxs,0);
    }

    private void showPKGS(ListView listView){
        PKGINFOAdapter pkginfoAdapter = new PKGINFOAdapter(pkginfos, apkDecompileActivity.this, checkboxs);
        listView.setAdapter(pkginfoAdapter);
    }

    private void showSelectApkPath(ListView listView){
        if(list.size() > 0 && checkboxs.size() > 0){
            USERAdapter userAdapter = new USERAdapter(list, apkDecompileActivity.this, checkboxs);
            listView.setAdapter(userAdapter);
        }
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
                showPKGS(lv1);
                break;
            case 1:
                getPKGS();
                showPKGS(lv1);
                break;
            case 2:
                getUserPKGS();
                showPKGS(lv1);
                break;
            case 3:
                getUserEnablePKGS();
                showPKGS(lv1);
                break;
            case 4:
                fuckActivity.getIns().killall();
                ;
        }
        return super.onOptionsItemSelected(item);
    }
}
