package org.fqaosp.myActivitys;

import static org.fqaosp.utils.fileTools.copyFile;
import static org.fqaosp.utils.fileTools.execFileSelect;
import static org.fqaosp.utils.fileTools.extactAssetsFile;
import static org.fqaosp.utils.fileTools.getMyHomeFilesPath;
import static org.fqaosp.utils.fileTools.selectFile;
import static org.fqaosp.utils.multiFunc.dismissDialogHandler;
import static org.fqaosp.utils.multiFunc.preventDismissDialog;
import static org.fqaosp.utils.multiFunc.sendHandlerMSG;
import static org.fqaosp.utils.multiFunc.showMyDialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.fqaosp.R;
import org.fqaosp.utils.CMD;
import org.fqaosp.utils.fuckActivity;
import org.fqaosp.utils.multiFunc;
import org.fqaosp.utils.permissionRequest;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class importToolsActivity extends AppCompatActivity {

    private ArrayList<String> list = new ArrayList<>();
    private ArrayList<Boolean> checkboxs = new ArrayList<>();
    private TextView itatv1 , itatv2;
    private Button itab1 , itab2,itab3,itab4;
    private int mode;
    private String fqfile="fqtools.tar" , jdkfile="jdk.tar.xz";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.import_tools_activity);
        fuckActivity.getIns().add(this);
        setTitle("工具导入与检测");
        initButton();
        permissionRequest.getExternalStorageManager(importToolsActivity.this);
    }

    private  void initButton(){
        itatv1 = findViewById(R.id.itatv1);
        itatv2 = findViewById(R.id.itatv2);
        itab1 = findViewById(R.id.itab1);
        itab2 = findViewById(R.id.itab2);
        itab3 = findViewById(R.id.itab3);
        itab4 = findViewById(R.id.itab4);
        clickButton();
    }

    private void clickButton(){
        itab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String filesPath = getMyHomeFilesPath(importToolsActivity.this);
                String fqtoolsd = filesPath+"/fqtools";
                File file = new File(fqtoolsd);
                itatv1.setText(file.exists() ? "fqtools已经安装" : "fqtools未安装,请前往 https://github.com/MrsEWE44/FQAOSP/releases 下载最新工具包");
            }
        });
        itab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mode=0;
                execFileSelect(importToolsActivity.this,importToolsActivity.this,"请选择 "+fqfile+" 文件");
            }
        });

        itab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String filesPath = getMyHomeFilesPath(importToolsActivity.this);
                String fqtoolsd = filesPath+"/jdk";
                File file = new File(fqtoolsd);
                itatv2.setText(file.exists() ? "jdk已经安装" : "jdk未安装\r\n 64位请复制 https://github.com/MrsEWE44/FQAOSP/releases/download/V1.1.8/jdk.tar.xz 下载工具包");
            }
        });
        itab4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mode=1;
                execFileSelect(importToolsActivity.this,importToolsActivity.this,"请选择 "+jdkfile+" 文件");
            }
        });
    }

    private void clearList(){
        list.clear();
        checkboxs.clear();
    }

    private void extractFile(String s, String fff){
        String myuid = Process.myUid()+"";
        String filesPath = getMyHomeFilesPath(importToolsActivity.this);
        new File(filesPath).mkdirs();
        String outName = filesPath+"/"+fff;
        if(copyFile(s,outName)){
            String busyboxFile = filesPath+"/busybox";
            String extractScriptFile = filesPath+"/extract.sh";
            File extractScriptF = new File(extractScriptFile);
            File busyF = new File(busyboxFile);
            if(!busyF.exists()){
                extactAssetsFile(this,"busybox",busyboxFile);
            }
            if(!extractScriptF.exists()){
                extactAssetsFile(this,"extract.sh",extractScriptFile);
            }
            String cmd = "cd " + filesPath + " && sh extract.sh && cd ../ && chown -R "+myuid+":"+myuid+ " files/";
            AlertDialog show = showMyDialog(importToolsActivity.this,"提示","正在安装插件,请稍后(可能会出现无响应，请耐心等待)....");
            Handler handler = dismissDialogHandler(0,show);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    CMD cmd1 = new CMD(cmd);
                    cmd1.getResultCode();
                    sendHandlerMSG(handler,0);
                }
            }).start();
        }
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
                    if(mode == 0){
                        selectFile(importToolsActivity.this,storage,uri,list,checkboxs,"请选择正确的 "+fqfile+" 文件","tar");
                    }else{
                        selectFile(importToolsActivity.this,storage,uri,list,checkboxs,"请选择正确的 "+jdkfile+" 文件","xz");
                    }
                }
            } else if(data.getData() != null) {//只有一个文件咯
                Uri uri = data.getData();
                if(mode == 0){
                    selectFile(importToolsActivity.this,storage,uri,list,checkboxs,"请选择正确的 "+fqfile+" 文件","tar");
                }else{
                    selectFile(importToolsActivity.this,storage,uri,list,checkboxs,"请选择正确的 "+jdkfile+" 文件","xz");
                }
            }
            for (String s : list) {
                if(s.indexOf(fqfile) != -1){
                    extractFile(s,fqfile);
                }
                if(s.indexOf(jdkfile) != -1){
                    extractFile(s,jdkfile);
                }
            }

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
