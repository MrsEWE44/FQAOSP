package org.fqaosp.myActivitys;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.fqaosp.R;
import org.fqaosp.adapter.USERAdapter;
import org.fqaosp.utils.CMD;
import org.fqaosp.utils.fuckActivity;
import org.fqaosp.utils.makeImgToPC;

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
        Button b1 = findViewById(R.id.mliab1);
        ListView lv1 = findViewById(R.id.mlialv1);
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
        requestPermission(this);
        showImgs(lv1);


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

    private void requestPermission(Context context){
        // 通过api判断手机当前版本号
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // 安卓11，判断有没有“所有文件访问权限”权限
            if (Environment.isExternalStorageManager()) {
                getImgs();
            } else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + context.getPackageName()));
                startActivity(intent);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 安卓6 判断有没有读写权限权限
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                getImgs();
            }
        }
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
//        Toast.makeText(this, "item id :::: "+itemId, Toast.LENGTH_SHORT).show();
        switch (itemId){
            case 0:
                fuckActivity.getIns().killall();

                ;
        }
        return super.onOptionsItemSelected(item);
    }

}
