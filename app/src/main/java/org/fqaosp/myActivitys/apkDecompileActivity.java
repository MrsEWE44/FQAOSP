package org.fqaosp.myActivitys;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
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
import org.fqaosp.adapter.PKGINFOAdapter;
import org.fqaosp.adapter.USERAdapter;
import org.fqaosp.entity.PKGINFO;
import org.fqaosp.threads.cmdThread;
import org.fqaosp.utils.CMD;
import org.fqaosp.utils.fuckActivity;
import org.fqaosp.utils.multiFunc;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class apkDecompileActivity extends AppCompatActivity {

    private ArrayList<String> list = new ArrayList<>();
    private ArrayList<Boolean> checkboxs = new ArrayList<>();
    private ArrayList<PKGINFO> pkginfos = new ArrayList<>();
    private ListView lv1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.apk_decompile_activity);
        fuckActivity.getIns().add(this);
        Button b1 = findViewById(R.id.adab1);
        Button b2 = findViewById(R.id.adab2);
        Button b3 = findViewById(R.id.adab3);
        lv1 = findViewById(R.id.adalv1);
        requestPermission(this);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < checkboxs.size(); i++) {
                    if(checkboxs.get(i)){
                        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(apkDecompileActivity.this);
                        alertDialog.setTitle("提示");
                        alertDialog.setMessage("请稍后，正在反编译中...");
                        AlertDialog show = alertDialog.show();
                        preventDismissDialog(show);
                        String datadir="/data/data/"+getPackageName();
                        String filesDir = datadir+"/files";
                        String filePath = pkginfos.size() > 0 ?pkginfos.get(i).getApkpath() : list.get(i);
                        PackageManager packageManager = getPackageManager();
                        PackageInfo archiveInfo = packageManager.getPackageArchiveInfo(filePath, 0);
                        String pkgname =  archiveInfo.packageName;
                        String storage = Environment.getExternalStorageDirectory().toString();
                        String outDir = storage+"/Android/data/"+getPackageName()+"/files/decompile/"+pkgname;
                        cmdThread ee = new cmdThread("cd " + filesDir + " && sh de.sh " + outDir + " " + filePath, "反编译成功 " + outDir, "反编译失败", apkDecompileActivity.this, show);
                        ee.start();
                    }
                }
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPKGS();
                showPKGS(lv1);
            }
        });

        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);//打开多个文件
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivityForResult(intent, 0);
            }
        });


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
                    list.add(filePath);
                    checkboxs.add(false);
                }
            } else if(data.getData() != null) {//只有一个文件咯
                Uri uri = data.getData();
                String filePath = storage + "/" +uri.getPath().replaceAll("/document/primary:","");
                list.add(filePath);
                checkboxs.add(false);
            }

            showSelectApkPath(lv1);
        }
    }

    private void clearList(){
        checkboxs.clear();
        pkginfos.clear();
        list.clear();
    }

    //获取对应的应用程序
    private void getPKGS(){
       clearList();
        multiFunc.queryPKGS(this,pkginfos,checkboxs,0);
    }

    private void showPKGS(ListView listView){
        PKGINFOAdapter pkginfoAdapter = new PKGINFOAdapter(pkginfos, apkDecompileActivity.this, checkboxs);
        listView.setAdapter(pkginfoAdapter);
    }

    private void showSelectApkPath(ListView listView){
        if(list.size() > 0 && checkboxs.size() > 0){
            USERAdapter userAdapter = new USERAdapter(list, apkDecompileActivity.this, checkboxs);
            listView.setAdapter(userAdapter);
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
//        Toast.makeText(this, "item id :::: "+itemId, Toast.LENGTH_SHORT).show();
        switch (itemId){
            case 0:
                fuckActivity.getIns().killall();

                ;
        }
        return super.onOptionsItemSelected(item);
    }
}
