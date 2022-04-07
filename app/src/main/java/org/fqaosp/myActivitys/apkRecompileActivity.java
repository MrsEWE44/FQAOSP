package org.fqaosp.myActivitys;

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
import org.fqaosp.threads.cmdThread;
import org.fqaosp.utils.fuckActivity;

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
                        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(apkRecompileActivity.this);
                        alertDialog.setTitle("提示");
                        alertDialog.setMessage("请稍后，正在回编译中...");
                        AlertDialog show = alertDialog.show();
                        preventDismissDialog(show);
                        String datadir="/data/data/"+getPackageName();
                        String filesDir = datadir+"/files";
                        String decompilepath = list.get(i);
                        String outname = decompilepath.substring(decompilepath.lastIndexOf("/")+1);
                        String storage = Environment.getExternalStorageDirectory().toString();
                        String outDir = storage+"/Android/data/"+getPackageName()+"/files/recompile";
                        File file = new File(outDir);
                        if(!file.exists()){
                           file.mkdirs();
                        }
                        String outFile = outDir+"/"+outname+".apk";
                        cmdThread ee = new cmdThread("cd " + filesDir + " && sh re.sh " + outFile + " " + decompilepath, "回编译成功 " + outFile, "回编译失败", apkRecompileActivity.this, show);
                        ee.start();
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
                Toast.makeText(apkRecompileActivity.this, "请选择 apktool.yml 文件", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);//打开多个文件
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivityForResult(intent, 0);
            }
        });

        requestPermission(this);
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
                    String filePath = storage + "/" +uri.getPath().replaceAll("/document/primary:","");
                    String fileName = filePath.substring(filePath.lastIndexOf("/")+1);
                    if(fileName.equals("apktool.yml")){
                        filePath=filePath.substring(0,filePath.lastIndexOf("/"));
                        list.add(filePath);
                        checkboxs.add(false);
                    }else{
                        Toast.makeText(this, "请选择正确的apktool.yml文件", Toast.LENGTH_SHORT).show();
                    }

                }
            } else if(data.getData() != null) {//只有一个文件咯
                Uri uri = data.getData();
                String filePath = storage + "/" +uri.getPath().replaceAll("/document/primary:","");
                String fileName = filePath.substring(filePath.lastIndexOf("/")+1);
                if(fileName.equals("apktool.yml")){
                    filePath=filePath.substring(0,filePath.lastIndexOf("/"));
                    list.add(filePath);
                    checkboxs.add(false);
                }else{
                    Toast.makeText(this, "请选择正确的apktool.yml文件", Toast.LENGTH_SHORT).show();
                }
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

    private void requestPermission(Context context){
        // 通过api判断手机当前版本号
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // 安卓11，判断有没有“所有文件访问权限”权限
            if (Environment.isExternalStorageManager()) {
                Toast.makeText(context, "已获取文件访问权限", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + context.getPackageName()));
                startActivity(intent);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 安卓6 判断有没有读写权限权限
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "已获取文件访问权限", Toast.LENGTH_SHORT).show();
            }
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
//        Toast.makeText(this, "item id :::: "+itemId, Toast.LENGTH_SHORT).show();
        switch (itemId){
            case 0:
                fuckActivity.getIns().killall();

                ;
        }
        return super.onOptionsItemSelected(item);
    }

}
