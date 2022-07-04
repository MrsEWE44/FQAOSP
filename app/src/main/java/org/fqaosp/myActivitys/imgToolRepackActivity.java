package org.fqaosp.myActivitys;

import static org.fqaosp.utils.fileTools.execFileSelect;
import static org.fqaosp.utils.fileTools.getMyHomeFilesPath;
import static org.fqaosp.utils.fileTools.getMyStorageHomePath;
import static org.fqaosp.utils.fileTools.getPathByLastName;
import static org.fqaosp.utils.fileTools.selectFile;
import static org.fqaosp.utils.multiFunc.preventDismissDialog;
import static org.fqaosp.utils.multiFunc.showMyDialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
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
import org.fqaosp.adapter.USERAdapter;
import org.fqaosp.threads.alertDialogThread;
import org.fqaosp.utils.CMD;
import org.fqaosp.utils.fuckActivity;
import org.fqaosp.utils.multiFunc;

import java.io.File;
import java.util.ArrayList;

public class imgToolRepackActivity extends AppCompatActivity {

    private ArrayList<String> list = new ArrayList<>();
    private ArrayList<Boolean> checkboxs = new ArrayList<>();
    private ListView itralv1;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.img_tool_repack_activity);
        fuckActivity.getIns().add(this);
        setTitle("镜像打包");
        Button itrab1= findViewById(R.id.itrab1);
        Button itrab2 = findViewById(R.id.itrab2);
        Button itrab3 = findViewById(R.id.itrab3);
        itralv1 = findViewById(R.id.itralv1);

        itrab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < checkboxs.size(); i++) {
                    if(checkboxs.get(i)){
                        String s = list.get(i);
                        String inputDir=s.substring(0,s.lastIndexOf("/"));
                        String name = getPathByLastName(inputDir);
                        String mystoragehome = getMyStorageHomePath(imgToolRepackActivity.this);
                        String filesPath = getMyHomeFilesPath(imgToolRepackActivity.this);
                        String outPath = mystoragehome+"/files/repack/"+name;
                        String outName = outPath+"/"+name;
                        File file = new File(outPath);
                        if(!file.exists()){
                            file.mkdirs();
                        }
                        String cmd = "cd " + filesPath + " && sh repack.sh "+inputDir + " "+outName+ " " + name;

                        alertDialogThread dialogThread = new alertDialogThread(imgToolRepackActivity.this, "正在打包 " + name + " ...", cmd, "提示", "打包成功 " + outName, "打包失败");
                        dialogThread.start();
                    }
                }
            }
        });

        itrab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        getDefaultImgProject();
                        showImgs(itralv1);
                    }
                });
            }
        });

        itrab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                execFileSelect(imgToolRepackActivity.this,imgToolRepackActivity.this,"请选择 boot.txt 文件");
            }
        });

    }

    private void showImgs(ListView listView){
        USERAdapter userAdapter = new USERAdapter(list, imgToolRepackActivity.this, checkboxs);
        listView.setAdapter(userAdapter);
    }

    private void clearList(){
        list.clear();
        checkboxs.clear();
    }

    private void getDefaultImgProject(){
        clearList();
        String homePath = getMyStorageHomePath(imgToolRepackActivity.this);
        AlertDialog show = showMyDialog(imgToolRepackActivity.this,"提示","请稍后，正在扫描本地镜像工程...");
        preventDismissDialog(show);
        String cmd = "find " + homePath + "/unpack -type d";
        CMD cmd1 = new CMD(cmd);
        if(cmd1.getResultCode() == 0){
            for (String s : cmd1.getResult().split("\n")) {
                String name = getPathByLastName(s);
                if(!name.equals("unpack")){
                    list.add(s);
                    checkboxs.add(false);
                }
            }
            Toast.makeText(this, "扫描成功", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "扫描失败，请确认当前应用拥有相关存储权限", Toast.LENGTH_SHORT).show();
        }
        multiFunc.dismissDialog(show);
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
                    selectFile(imgToolRepackActivity.this,storage,uri,list,checkboxs,"请选择正确的boot.txt文件","txt");
                }
            } else if(data.getData() != null) {//只有一个文件咯
                Uri uri = data.getData();
                selectFile(imgToolRepackActivity.this,storage,uri,list,checkboxs,"请选择正确的boot.txt文件","txt");
            }
            showImgs(itralv1);
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
