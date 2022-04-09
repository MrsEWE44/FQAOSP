package org.fqaosp.myActivitys;

import static org.fqaosp.utils.multiFunc.execFileSelect;
import static org.fqaosp.utils.multiFunc.getMyHomeFilesPath;
import static org.fqaosp.utils.multiFunc.getPathByLastName;
import static org.fqaosp.utils.multiFunc.preventDismissDialog;
import static org.fqaosp.utils.multiFunc.selectFile;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.fqaosp.R;
import org.fqaosp.adapter.USERAdapter;
import org.fqaosp.threads.alertDialogThread;
import org.fqaosp.threads.cmdThread;
import org.fqaosp.utils.fuckActivity;
import org.fqaosp.utils.permissionRequest;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class apkRecompileActivity extends AppCompatActivity {
    private ArrayList<String> list = new ArrayList<>();
    private ArrayList<Boolean> checkboxs = new ArrayList<>();
    private ListView lv1;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.apk_recompile_activity);
        fuckActivity.getIns().add(this);
        Button b1 = findViewById(R.id.arab1);
        Button b2 = findViewById(R.id.arab2);
        Button b3 = findViewById(R.id.arab3);
        lv1 = findViewById(R.id.aralv1);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                for (int i = 0; i < checkboxs.size(); i++) {
                    if(checkboxs.get(i)){
                        String filesDir = getMyHomeFilesPath(apkRecompileActivity.this);
                        String decompilepath = list.get(i);
                        String outname = decompilepath.substring(decompilepath.lastIndexOf("/")+1);
                        String storage = Environment.getExternalStorageDirectory().toString();
                        String outDir = storage+"/Android/data/"+getPackageName()+"/files/recompile";
                        File file = new File(outDir);
                        if(!file.exists()){
                           file.mkdirs();
                        }
                        String outFile = outDir+"/"+outname+".apk";
                        String cmd = "cd " + filesDir + " && sh re.sh " + outFile + " " + decompilepath;
                        alertDialogThread dialogThread = new alertDialogThread(apkRecompileActivity.this, "请稍后，正在回编译中...", cmd, "提示", "回编译成功 " + outFile, "回编译失败");
                        dialogThread.start();
                    }
                }

            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearList();
                String storage = Environment.getExternalStorageDirectory().toString();
                String defaultDecompileDir = storage+"/Android/data/"+getPackageName()+"/files/decompile";
                File file1 = new File(defaultDecompileDir);
                if(file1.exists()){
                    File[] files = file1.listFiles();
                    if(files.length > 0){
                        for (File file : files) {
                            list.add(file.toString());
                            checkboxs.add(false);
                        }
                        showSelectApkToolPath(lv1);
                    }else{
                        Toast.makeText(apkRecompileActivity.this, "默认路径没有反编译后的内容", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(apkRecompileActivity.this, "默认路径没有反编译后的内容", Toast.LENGTH_SHORT).show();
                }

            }
        });

        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                execFileSelect(apkRecompileActivity.this,apkRecompileActivity.this,"请选择 apktool.yml 文件");
            }
        });
        permissionRequest.getExternalStorageManager(apkRecompileActivity.this);
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
                    selectFile(apkRecompileActivity.this,storage,uri,list,checkboxs,"请选择正确的apktool.yml文件","yml");
                }
            } else if(data.getData() != null) {//只有一个文件咯
                Uri uri = data.getData();
                selectFile(apkRecompileActivity.this,storage,uri,list,checkboxs,"请选择正确的apktool.yml文件","yml");
            }
            showSelectApkToolPath(lv1);
        }
    }

    private void showSelectApkToolPath(ListView listView){
        if(list.size() > 0 && checkboxs.size() > 0){
            USERAdapter userAdapter = new USERAdapter(list, apkRecompileActivity.this, checkboxs);
            listView.setAdapter(userAdapter);
        }
    }

    private void clearList(){
        checkboxs.clear();
        list.clear();
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
