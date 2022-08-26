package org.fqaosp.myActivitys;

import static org.fqaosp.utils.fileTools.execFileSelect;
import static org.fqaosp.utils.fileTools.getMyHomeFilesPath;
import static org.fqaosp.utils.fileTools.getMyStorageHomePath;
import static org.fqaosp.utils.fileTools.selectFile;
import static org.fqaosp.utils.multiFunc.dismissDialogHandler;
import static org.fqaosp.utils.multiFunc.preventDismissDialog;
import static org.fqaosp.utils.multiFunc.queryUserPKGS;
import static org.fqaosp.utils.multiFunc.sendHandlerMSG;
import static org.fqaosp.utils.multiFunc.showInfoMsg;
import static org.fqaosp.utils.multiFunc.showMyDialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.fqaosp.R;
import org.fqaosp.adapter.PKGINFOAdapter;
import org.fqaosp.adapter.USERAdapter;
import org.fqaosp.entity.PKGINFO;
import org.fqaosp.utils.CMD;
import org.fqaosp.utils.fuckActivity;
import org.fqaosp.utils.multiFunc;
import org.fqaosp.utils.permissionRequest;

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
        setTitle("apk反编译");
        Button b1 = findViewById(R.id.adab1);
        Button b2 = findViewById(R.id.adab2);
        Button b3 = findViewById(R.id.adab3);
        Button b4 = findViewById(R.id.adab4);
        EditText adaet1 = findViewById(R.id.adaet1);
        lv1 = findViewById(R.id.adalv1);
        permissionRequest.getExternalStorageManager(apkDecompileActivity.this);
        Activity a = this;
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog show = showMyDialog(apkDecompileActivity.this,"提示","请稍后，正在反编译中...(可能会出现无响应，请耐心等待)....");
                Handler handler = dismissDialogHandler(0,show);
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        String filesDir = getMyHomeFilesPath(apkDecompileActivity.this);
                        PackageManager packageManager = getPackageManager();
                        String myStorageHomePath = getMyStorageHomePath(a);
                        for (int i = 0; i < checkboxs.size(); i++) {
                            if(checkboxs.get(i)){
                                String filePath = pkginfos.size() > 0 ?pkginfos.get(i).getApkpath() : list.get(i);
                                PackageInfo archiveInfo = packageManager.getPackageArchiveInfo(filePath, 0);
                                String pkgname =  archiveInfo.packageName;
                                String outDir = myStorageHomePath+"/files/decompile/"+pkgname;
                                String cmd = "cd " + filesDir + " && sh de.sh " + outDir + " " + filePath;
                                CMD cmd1 = new CMD(cmd);
                                Toast.makeText(apkDecompileActivity.this, cmd1.getResultCode()==0?"反编译成功":"反编译失败", Toast.LENGTH_SHORT).show();
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
                getUserEnablePKGS();
                showPKGS(lv1);
            }
        });

        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                execFileSelect(apkDecompileActivity.this,apkDecompileActivity.this,"请选择.apk文件");
            }
        });

        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchStr = adaet1.getText().toString();
                pkginfos = multiFunc.indexOfPKGS(apkDecompileActivity.this,searchStr,pkginfos,checkboxs,0);
                showPKGS(lv1);
            }
        });
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
                    selectFile(apkDecompileActivity.this,storage,uri,list,checkboxs,"请选择正确的apk文件","apk");
                }
            } else if(data.getData() != null) {//只有一个文件咯
                Uri uri = data.getData();
                selectFile(apkDecompileActivity.this,storage,uri,list,checkboxs,"请选择正确的apk文件","apk");
            }

            showSelectApkPath(lv1);
        }
    }

    private void clearList(){
        checkboxs.clear();
        pkginfos.clear();
        list.clear();
    }

    private void getUserEnablePKGS(){
        multiFunc.queryUserEnablePKGS(this,pkginfos,checkboxs,0);
    }

    private void getEnablePKGS(){
        multiFunc.queryEnablePKGS(this,pkginfos,checkboxs,0);
    }

    //获取对应的应用程序
    private void getPKGS(){
       multiFunc.queryPKGS(this,pkginfos,checkboxs,0);
    }

    private void getUserPKGS(){
        queryUserPKGS(this,pkginfos,checkboxs,0);
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
        menu.add(Menu.NONE,0,0,"显示所有应用");
        menu.add(Menu.NONE,1,1,"显示所有应用(包括禁用)");
        menu.add(Menu.NONE,2,2,"显示用户安装的应用");
        menu.add(Menu.NONE,3,3,"显示用户安装的应用(包括禁用)");
        menu.add(Menu.NONE,4,4,"帮助");
        menu.add(Menu.NONE,5,5,"退出");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId){
            case 0:
                getEnablePKGS();
                showPKGS(lv1);
                break;
            case 1:
                getPKGS();
                showPKGS(lv1);
                break;
            case 2:
                getUserPKGS();
                showPKGS(lv1);
                break;
            case 3:
                getUserEnablePKGS();
                showPKGS(lv1);
                break;
            case 4:
                showInfoMsg(this,"帮助信息","该页面是用于apk反编译操作的，需要安装jdk与fqtools，采用传统apktool进行反编译操作,如果没有安装，则会自动跳转安装页面，按照页面提示安装即可。\r\n" +
                        "1.点击右上角三个点，可以列出与之相匹配的应用列表，支持直接批量反编译它们.\r\n" +
                        "2.点击选择本地文件，可以批量反编译用户本地的apk文件。\r\n" +
                        "3.上面有个搜索框，支持中英文搜索，无大小写限制.\r\n");
                break;
            case 5:
                fuckActivity.getIns().killall();
                ;
        }
        return super.onOptionsItemSelected(item);
    }
}
