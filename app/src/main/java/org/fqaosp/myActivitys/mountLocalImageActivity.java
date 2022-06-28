package org.fqaosp.myActivitys;

import static org.fqaosp.utils.multiFunc.preventDismissDialog;
import static org.fqaosp.utils.multiFunc.showMyDialog;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
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
import org.fqaosp.utils.CMD;
import org.fqaosp.utils.fuckActivity;
import org.fqaosp.utils.makeImgToPC;
import org.fqaosp.utils.multiFunc;
import org.fqaosp.utils.permissionRequest;

import java.util.ArrayList;

/**
 *
 *
 * 挂载手机本地的镜像文件到电脑上，实现driverdroid功能
 * 可以给你的电脑安装系统，也可以运行pe
 *
 *
 *
 * */
public class mountLocalImageActivity extends AppCompatActivity {

    private ArrayList<String> list = new ArrayList<>();
    private ArrayList<Boolean> checkboxs = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mount_local_img_activity);
        fuckActivity.getIns().add(this);
        setTitle("U盘模式");
        Button b1 = findViewById(R.id.mliab1);
        ListView lv1 = findViewById(R.id.mlialv1);
        AlertDialog show = showMyDialog(mountLocalImageActivity.this,"提示","正在扫描本地镜像文件,请稍后(可能会出现无响应，请耐心等待)....");
        preventDismissDialog(show);
        Handler handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                if(msg.what==0){
                    showImgs(lv1);
                    multiFunc.dismissDialog(show);
                }
            }
        };
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeImgToPC toPC = new makeImgToPC();
                for (int i = 0; i < checkboxs.size(); i++) {
                    if(checkboxs.get(i)){
                        String imgPath = list.get(i);
                        if(toPC.mountLocalFile(imgPath)){
                            Toast.makeText(mountLocalImageActivity.this, "挂载成功 "+imgPath, Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(mountLocalImageActivity.this, "挂载失败 "+imgPath, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
        permissionRequest.getExternalStorageManager(mountLocalImageActivity.this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                getImgs();
                Message msg = new Message();
                msg.what=0;
                handler.sendMessage(msg);
            }
        }).start();

    }

    private void getImgs(){
        list.clear();
        checkboxs.clear();
        String s = Environment.getExternalStorageDirectory().toString();
        CMD cmd = new CMD("find "+s+"/ -name '*.img' -o -name '*.iso'");
        for (String s1 : cmd.getResult().split("\n")) {
            list.add(s1);
            checkboxs.add(false);
        }
    }

    private void showImgs(ListView listView){
        USERAdapter userAdapter = new USERAdapter(list, mountLocalImageActivity.this, checkboxs);
        listView.setAdapter(userAdapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

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
