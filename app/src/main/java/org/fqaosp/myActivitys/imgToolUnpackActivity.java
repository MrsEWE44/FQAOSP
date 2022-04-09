package org.fqaosp.myActivitys;

import static org.fqaosp.utils.multiFunc.execFileSelect;
import static org.fqaosp.utils.multiFunc.getMyHomeFilesPath;
import static org.fqaosp.utils.multiFunc.getMyStorageHomePath;
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
import org.fqaosp.utils.CMD;
import org.fqaosp.utils.fuckActivity;
import org.fqaosp.utils.multiFunc;
import org.fqaosp.utils.permissionRequest;

import java.io.File;
import java.util.ArrayList;

public class imgToolUnpackActivity extends AppCompatActivity {

    private ArrayList<String> list = new ArrayList<>();
    private ArrayList<Boolean> checkboxs = new ArrayList<>();
    private ListView itualv1;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.img_tool_unpack_activity);
        fuckActivity.getIns().add(this);
        Button ituab1= findViewById(R.id.ituab1);
        Button ituab2 = findViewById(R.id.ituab2);
        Button ituab3 = findViewById(R.id.ituab3);
        itualv1 = findViewById(R.id.itualv1);
        permissionRequest.getExternalStorageManager(imgToolUnpackActivity.this);

        ituab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < checkboxs.size(); i++) {
                    if(checkboxs.get(i)){
                        String s = list.get(i);
                        String name = getPathByLastName(s).replaceAll(".img","");
                        String mystoragehome = getMyStorageHomePath(imgToolUnpackActivity.this);
                        String filesPath = getMyHomeFilesPath(imgToolUnpackActivity.this);
                        String outPath = mystoragehome+"/unpack/"+name;
                        File file = new File(outPath);
                        if(!file.exists()){
                           file.mkdirs();
                        }
                        String cmd = "cd " + filesPath + " && sh unpack.sh "+s + " "+outPath;
                        alertDialogThread dialogThread = new alertDialogThread(imgToolUnpackActivity.this, "正在解包 " + name + " ...", cmd, "提示", "解包成功 " + outPath, "解包失败");
                        dialogThread.start();
                    }
                }
            }
        });

        ituab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        getLocalImgs();
                        showImgs(itualv1);
                    }
                });

            }
        });

        ituab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                execFileSelect(imgToolUnpackActivity.this,imgToolUnpackActivity.this,"请选择 .img 文件");
            }
        });
    }


    private void getLocalImgs(){
        clearList();
        String storage = Environment.getExternalStorageDirectory().toString();
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(imgToolUnpackActivity.this);
        alertDialog.setTitle("提示");
        alertDialog.setMessage("请稍后，正在扫描本地镜像文件...");
        AlertDialog show = alertDialog.show();
        preventDismissDialog(show);
        String cmd = "find " + storage + "/ -name '*.img'";
        CMD cmd1 = new CMD(cmd);
        if(cmd1.getResultCode() == 0){
            for (String s : cmd1.getResult().split("\n")) {
                list.add(s);
                checkboxs.add(false);
            }
            Toast.makeText(this, "扫描成功", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "扫描失败，请确认当前应用拥有相关存储权限", Toast.LENGTH_SHORT).show();
        }
        multiFunc.dismissDialog(show);
    }

    private void showImgs(ListView listView){
        USERAdapter userAdapter = new USERAdapter(list, imgToolUnpackActivity.this, checkboxs);
        listView.setAdapter(userAdapter);
    }

    private void clearList(){
        list.clear();
        checkboxs.clear();
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
                    selectFile(imgToolUnpackActivity.this,storage,uri,list,checkboxs,"请选择正确的img文件","img");
                }
            } else if(data.getData() != null) {//只有一个文件咯
                Uri uri = data.getData();
                selectFile(imgToolUnpackActivity.this,storage,uri,list,checkboxs,"请选择正确的img文件","img");
            }
            showImgs(itualv1);
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
