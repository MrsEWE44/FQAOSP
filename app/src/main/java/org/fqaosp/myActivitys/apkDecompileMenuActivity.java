package org.fqaosp.myActivitys;

import static org.fqaosp.utils.multiFunc.copyFile;
import static org.fqaosp.utils.multiFunc.jump;

import android.app.AlertDialog;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

    private  void extactAssetsFile(String fileName,String toPath){
        AssetManager assets = getAssets();
        InputStream stream = null;
        try {
            stream = assets.open(fileName);
            copyFile(stream,toPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private  void extractAssetsFiles()  {
        try {
            String datadir="/data/data/"+getPackageName();
            String filesDir = datadir+"/files";
            String busyboxFile = filesDir+"/busybox";
            String jdkFile = filesDir+"/jdk.tar.xz";
            String jdkDir = filesDir+"/jdk";
            String makeScriptFile = filesDir+"/make.sh";
            String deScriptFile = filesDir+"/de.sh";
            String reScriptFile = filesDir+"/re.sh";

            String apktoolFile = filesDir+ "/apktool.jar";
            File file1 = new File(filesDir);
            File bboxF = new File(busyboxFile);
            File jdkF = new File(jdkFile);
            File jdkD = new File(jdkDir);
            File deScriptF = new File(deScriptFile);
            File reScriptF = new File(reScriptFile);
            File makeScriptF = new File(makeScriptFile);
            File apkToolF = new File(apktoolFile);
            if(!file1.exists()){
               file1.mkdirs();
            }
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(apkDecompileMenuActivity.this);
            alertDialog.setTitle("提示");
            alertDialog.setMessage("请稍后，正在解压apktool相关资源文件");
            AlertDialog show = alertDialog.show();
            preventDismissDialog(show);
            if(!bboxF.exists() ){
                extactAssetsFile("busybox",busyboxFile);
            }
            if(!jdkD.exists() && !jdkF.exists() ){
                extactAssetsFile("jdk.tar.xz",jdkFile);
            }
            if(jdkD.exists() && jdkF.exists()){
                jdkF.delete();
            }
            if(!makeScriptF.exists()){
                extactAssetsFile("make.sh",makeScriptFile);
            }
            if(!apkToolF.exists()){
                extactAssetsFile("apktool.jar",apktoolFile);
            }
            if(!deScriptF.exists()){
                extactAssetsFile("de.sh",deScriptFile);
            }
            if(!reScriptF.exists()){
                extactAssetsFile("re.sh",reScriptFile);
            }
            cmdThread ee = new cmdThread("cd " + filesDir + " && sh make.sh","解压成功","解压失败",this,show);
            ee.start();
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

    /**
     * 通过反射 阻止关闭对话框
     */
    private void preventDismissDialog(AlertDialog ddd) {
        try {
            Field field = ddd.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            //设置mShowing值，欺骗android系统
            field.set(ddd, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
