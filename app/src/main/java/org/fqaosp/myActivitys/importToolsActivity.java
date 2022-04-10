package org.fqaosp.myActivitys;

import static org.fqaosp.utils.multiFunc.copyFile;
import static org.fqaosp.utils.multiFunc.execFileSelect;
import static org.fqaosp.utils.multiFunc.extactAssetsFile;
import static org.fqaosp.utils.multiFunc.getMyHomeFilesPath;
import static org.fqaosp.utils.multiFunc.selectFile;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import org.fqaosp.threads.alertDialogThread;
import org.fqaosp.utils.fuckActivity;
import org.fqaosp.utils.multiFunc;
import org.fqaosp.utils.permissionRequest;

import java.io.File;
import java.util.ArrayList;

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
                String filesPath = multiFunc.getMyHomeFilesPath(importToolsActivity.this);
                String fqtoolsd = filesPath+"/fqtools";
                File file = new File(fqtoolsd);
                itatv1.setText(file.exists() ? "fqtools已经安装" : "fqtools未安装,请前往 https://github.com/MrsEWE44/FQAOSP/releases 下载相关工具包");
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
                String filesPath = multiFunc.getMyHomeFilesPath(importToolsActivity.this);
                String fqtoolsd = filesPath+"/jdk";
                File file = new File(fqtoolsd);
                itatv2.setText(file.exists() ? "jdk已经安装" : "jdk未安装,请前往 https://github.com/MrsEWE44/FQAOSP/releases 下载相关工具包");
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

    private void extractFile(String s,String fff){
        String filesPath = getMyHomeFilesPath(importToolsActivity.this);
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
            String cmd = "cd " + filesPath + " && sh extract.sh ";
            alertDialogThread dialogThread = new alertDialogThread(importToolsActivity.this, "解压 " + fff + " ...", cmd, "提示", "解压成功", "解压失败");
            dialogThread.start();
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
