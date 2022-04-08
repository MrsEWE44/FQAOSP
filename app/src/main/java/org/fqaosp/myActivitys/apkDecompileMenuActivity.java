package org.fqaosp.myActivitys;

import static org.fqaosp.utils.multiFunc.copyFile;
import static org.fqaosp.utils.multiFunc.extactAssetsFile;
import static org.fqaosp.utils.multiFunc.jump;
import static org.fqaosp.utils.multiFunc.preventDismissDialog;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.fqaosp.R;
import org.fqaosp.threads.cmdThread;
import org.fqaosp.utils.fuckActivity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

public class apkDecompileMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.apk_decompile_menu_activity);
        fuckActivity.getIns().add(this);
        Button b1 = findViewById(R.id.admab1);
        Button b2 = findViewById(R.id.admab2);
        jump(b1,this,apkDecompileActivity.class);
        jump(b2,this,apkRecompileActivity.class);
        extractAssetsFiles();
    }


    private  Boolean checkjdkfile(File storageHomeJDKF){
        if (!storageHomeJDKF.exists()){
            AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(apkDecompileMenuActivity.this);
            alertDialog2.setTitle("提示");
            alertDialog2.setMessage("请下载该连接jdk文件 https://github.com/MrsEWE44/FQAOSP/releases/tag/V1.0-test-1 并把jdk.tar.xz放置在 "+storageHomeJDKF.toString());
            alertDialog2.setNegativeButton("已下载", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    if(storageHomeJDKF.exists()){
                        Toast.makeText(apkDecompileMenuActivity.this, "请重新进入app", Toast.LENGTH_LONG).show();
                        fuckActivity.getIns().killall();
                    }else{
                        checkjdkfile(storageHomeJDKF);
                    }
                }
            });
            alertDialog2.show();
        }else{
            return true;
        }
        return false;
    }

    private  void extractAssetsFiles()  {
        try {
            String datadir="/data/data/"+getPackageName();
            String filesDir = datadir+"/files";
            String busyboxFile = filesDir+"/busybox";
            String jdkFile = filesDir+"/jdk.tar.xz";
            String jdkDir = filesDir+"/jdk";
            String storage = Environment.getExternalStorageDirectory().toString();
            String storageHome = storage+"/Download";
            String storageHomeJDKFile = storageHome+"/jdk.tar.xz";
            String makeScriptFile = filesDir+"/make.sh";
            String deScriptFile = filesDir+"/de.sh";
            String reScriptFile = filesDir+"/re.sh";
            String apktoolFile = filesDir+ "/apktool.jar";
            File file1 = new File(filesDir);
            File bboxF = new File(busyboxFile);
            File jdkF = new File(jdkFile);
            File jdkD = new File(jdkDir);
            File storageHomeJDKF = new File(storageHomeJDKFile);
            File storageHomeDir = new File(storageHome);
            File deScriptF = new File(deScriptFile);
            File reScriptF = new File(reScriptFile);
            File makeScriptF = new File(makeScriptFile);
            File apkToolF = new File(apktoolFile);
            if(!storageHomeDir.exists()){
               storageHomeDir.mkdirs();
            }
            if(!file1.exists()){
               file1.mkdirs();
            }

            if (!jdkD.exists() && !jdkF.exists() ){
                if(checkjdkfile(storageHomeJDKF)){
                    if(copyFile(storageHomeJDKFile,jdkFile)){
                        Toast.makeText(this, "发现jdk!", Toast.LENGTH_SHORT).show();
                        extractAssetsFiles();
                    }
                }
            }else{
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(apkDecompileMenuActivity.this);
                alertDialog.setTitle("提示");
                alertDialog.setMessage("请稍后，正在解压apktool相关资源文件");
                AlertDialog show = alertDialog.show();
                preventDismissDialog(show);
                if(!bboxF.exists() ){
                    extactAssetsFile(this,"busybox",busyboxFile);
                }

                if(jdkD.exists() && jdkF.exists()){
                    jdkF.delete();
                }
                if(!makeScriptF.exists()){
                    extactAssetsFile(this,"make.sh",makeScriptFile);
                }
                if(!apkToolF.exists()){
                    extactAssetsFile(this,"apktool.jar",apktoolFile);
                }
                if(!deScriptF.exists()){
                    extactAssetsFile(this,"de.sh",deScriptFile);
                }
                if(!reScriptF.exists()){
                    extactAssetsFile(this,"re.sh",reScriptFile);
                }
                cmdThread ee = new cmdThread("cd " + filesDir + " && sh make.sh","解压成功","解压失败",this,show);
                ee.start();
            }


        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("退出");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId){
            case 0:
                fuckActivity.getIns().killall();
                ;
        }
        return super.onOptionsItemSelected(item);
    }


}
