package org.fqaosp.myActivitys;

import static org.fqaosp.utils.fileTools.execFileSelect;
import static org.fqaosp.utils.fileTools.getMyHomeFilesPath;
import static org.fqaosp.utils.fileTools.getMyStorageHomePath;
import static org.fqaosp.utils.fileTools.getPathByLastName;
import static org.fqaosp.utils.fileTools.selectFile;
import static org.fqaosp.utils.multiFunc.dismissDialogHandler;
import static org.fqaosp.utils.multiFunc.preventDismissDialog;
import static org.fqaosp.utils.multiFunc.sendHandlerMSG;
import static org.fqaosp.utils.multiFunc.showInfoMsg;
import static org.fqaosp.utils.multiFunc.showMyDialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
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
import org.fqaosp.adapter.USERAdapter;
import org.fqaosp.threads.alertDialogThread;
import org.fqaosp.utils.CMD;
import org.fqaosp.utils.fuckActivity;
import org.fqaosp.utils.permissionRequest;

import java.io.File;
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
        setTitle("apk回编译");
        Button b1 = findViewById(R.id.arab1);
        Button b2 = findViewById(R.id.arab2);
        Button b3 = findViewById(R.id.arab3);
        lv1 = findViewById(R.id.aralv1);
        Activity a = this;
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog show = showMyDialog(apkRecompileActivity.this,"提示","请稍后，正在反编译中...(可能会出现无响应，请耐心等待)....");
                Handler handler = dismissDialogHandler(0,show);
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        String filesDir = getMyHomeFilesPath(apkRecompileActivity.this);
                        String myStorageHomePath = getMyStorageHomePath(a);
                        for (int i = 0; i < checkboxs.size(); i++) {
                            if(checkboxs.get(i)){
                                String decompilepath = list.get(i);
                                String inputDir=decompilepath.substring(0,decompilepath.lastIndexOf("/"));
                                String outname = getPathByLastName(inputDir);
                                String outDir = myStorageHomePath+"/files/recompile";
                                File file = new File(outDir);
                                if(!file.exists()){
                                    file.mkdirs();
                                }
                                String outFile = outDir+"/"+outname+".apk";
                                String cmd = "cd " + filesDir + " && sh re.sh " + outFile + " " + inputDir;
                                CMD cmd1 = new CMD(cmd);
                                Toast.makeText(apkRecompileActivity.this, cmd1.getResultCode()==0?"回编译成功":"回编译失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                        sendHandlerMSG(handler,0);
                    }
                });


            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearList();
                String myStorageHomePath = getMyStorageHomePath(a);
                String defaultDecompileDir = myStorageHomePath+"/files/decompile";
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
        menu.add(Menu.NONE,0,0,"帮助");
        menu.add(Menu.NONE,1,1,"退出");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId){
            case 0:
                showInfoMsg(this,"帮助信息","该页面是用于apk回编译操作的，需要安装jdk与fqtools，采用传统apktool进行回编译操作,如果没有安装，则会自动跳转安装页面，按照页面提示安装即可。\r\n" +
                        "1.点击选择本地文件夹，可以回编译用户本地反编译后的内容（需要选择带apktool.yml文件的项目工程）。\r\n" +
                        "2.点击加载默认，可以列出所有从该应用反编译后的项目名称（推荐这个）。\r\n" +
                        "3.点击上面开始回编译就会开始进行回编译操作.\r\n");
                break;
            case 1:
                fuckActivity.getIns().killall();
                ;
        }
        return super.onOptionsItemSelected(item);
    }

}
