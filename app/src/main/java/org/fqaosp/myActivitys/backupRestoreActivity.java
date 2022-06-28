package org.fqaosp.myActivitys;

import static org.fqaosp.utils.fileTools.extactAssetsFile;
import static org.fqaosp.utils.fileTools.getMyHomeFilesPath;
import static org.fqaosp.utils.fileTools.getPathByLastName;
import static org.fqaosp.utils.multiFunc.jump;
import static org.fqaosp.utils.multiFunc.preventDismissDialog;
import static org.fqaosp.utils.multiFunc.showMyDialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
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

import java.io.File;
import java.util.ArrayList;


/**
 *
 * 应用程序备份与恢复
 * 2022年6月28日14点11分
 * */

public class backupRestoreActivity extends AppCompatActivity {
    private ArrayList<PKGINFO> pkginfos = new ArrayList<>();
    private ArrayList<String> list = new ArrayList<>();
    private ArrayList<Boolean> checkboxs = new ArrayList<>();
    private ListView lv1;
    private EditText braet1;
    private Button b1 ,b2,brasearchb;
    private Switch brasb1,brasb2,brasb3;
    private Boolean brasb1Bool,brasb2Bool,brasb3Bool,isBackup;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.backup_restore_activity);
        fuckActivity.getIns().add(this);
        setTitle("当前为: 备份");
        initBt();
        checkTools();
    }

    private void listLocalBackupFiles(){
        isBackup=false;
        setTitle("当前为: 恢复");
        permissionRequest.getExternalStorageManager(backupRestoreActivity.this);
        String s = Environment.getExternalStorageDirectory().toString();
        String localBackupDir= s+"/backup_app";
        File file = new File(localBackupDir);
        AlertDialog show = showMyDialog(backupRestoreActivity.this,"提示","正在扫描本地备份文件,请稍后(可能会出现无响应，请耐心等待)....");
        preventDismissDialog(show);
        Handler handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                if(msg.what==0){
                    showPKGS(lv1);
                    multiFunc.dismissDialog(show);
                }
            }
        };
        Activity a = this;
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(file.exists()){
                    clearlist();
                    for (File listFile : file.listFiles()) {
                        list.add(listFile.toString());
                        checkboxs.add(false);
                    }
                }else{
                    Toast.makeText(a, "看样子还没有备份过", Toast.LENGTH_SHORT).show();
                }
                Message msg = new Message();
                msg.what=0;
                handler.sendMessage(msg);
            }
        }).start();
    }

    private void clearlist(){
        list.clear();
        pkginfos.clear();
        checkboxs.clear();
    }

    private void initBt(){
        b1 = findViewById(R.id.brab1);
        b2 = findViewById(R.id.brab2);
        brasearchb = findViewById(R.id.brasearchb);
        braet1 = findViewById(R.id.braet1);
        brasb1 =findViewById(R.id.brasb1);
        brasb2 =findViewById(R.id.brasb2);
        brasb3 =findViewById(R.id.brasb3);

        lv1 = findViewById(R.id.bralv1);
        brasb1Bool=false;
        brasb2Bool=false;
        brasb3Bool=false;
        brasb1.setChecked(brasb1Bool);
        brasb2.setChecked(brasb2Bool);
        brasb3.setChecked(brasb3Bool);

        //默认是备份模式
        isBackup=true;
        
        btClick();
    }

    private void btClick(){

        brasb1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                brasb1Bool=b;
                brasb2Bool=false;
                brasb3Bool=false;
                brasb1.setChecked(brasb1Bool);
                brasb2.setChecked(brasb2Bool);
                brasb3.setChecked(brasb3Bool);
            }
        });

        brasb2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                brasb1Bool=false;
                brasb2Bool=b;
                brasb3Bool=false;
                brasb1.setChecked(brasb1Bool);
                brasb2.setChecked(brasb2Bool);
                brasb3.setChecked(brasb3Bool);
            }
        });

        brasb3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                brasb1Bool=false;
                brasb2Bool=false;
                brasb3Bool=b;
                brasb1.setChecked(brasb1Bool);
                brasb2.setChecked(brasb2Bool);
                brasb3.setChecked(brasb3Bool);
            }
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog show = showMyDialog(backupRestoreActivity.this,"提示","正在备份应用,请稍后(可能会出现无响应，请耐心等待)....");
                preventDismissDialog(show);
                Handler handler = new Handler(){
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        if(msg.what==0){
                            multiFunc.dismissDialog(show);
                        }
                    }
                };

                view.post(new Runnable() {
                    @Override
                    public void run() {
                        if(isBackup){

                            if(brasb3Bool){
                                //未选中
                                for (int i = 0; i < checkboxs.size(); i++) {
                                    if(!checkboxs.get(i)){
                                        PKGINFO pkginfo = pkginfos.get(i);
                                        if(!pkginfo.getPkgname().equals(getPackageName())){
                                            backupByPKGNAME(pkginfo.getPkgname());
                                        }
                                    }
                                }
                            }

                            if(brasb2Bool){
                                //选中
                                for (int i = 0; i < checkboxs.size(); i++) {
                                    if(checkboxs.get(i)){
                                        PKGINFO pkginfo = pkginfos.get(i);
                                        if(!pkginfo.getPkgname().equals(getPackageName())){
                                            backupByPKGNAME(pkginfo.getPkgname());
                                        }
                                    }
                                }
                            }

                            if(brasb1Bool){
                                //所有
                                for (PKGINFO pkginfo : pkginfos) {
                                    if(!pkginfo.getPkgname().equals(getPackageName())){
                                        backupByPKGNAME(pkginfo.getPkgname());
                                    }
                                }
                            }
                            //都没有选中
                            if(brasb1Bool==false && brasb2Bool ==false && brasb3Bool ==false){
                                //如果都没有选择，我们就默认走已经勾选的
                                for (int i = 0; i < checkboxs.size(); i++) {
                                    if(checkboxs.get(i)){
                                        PKGINFO pkginfo = pkginfos.get(i);
                                        if(!pkginfo.getPkgname().equals(getPackageName())){
                                            backupByPKGNAME(pkginfo.getPkgname());
                                        }
                                    }
                                }
                            }
                            Toast.makeText(backupRestoreActivity.this, "所选应用都已备份 ", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(backupRestoreActivity.this, "请切换回备份模式", Toast.LENGTH_SHORT).show();
                        }

                        Message msg = new Message();
                        msg.what=0;
                        handler.sendMessage(msg);

                    }
                });
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog show = showMyDialog(backupRestoreActivity.this,"提示","正在恢复应用,请稍后(可能会出现无响应，请耐心等待)....");
                preventDismissDialog(show);
                Handler handler = new Handler(){
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        if(msg.what==0){
                            multiFunc.dismissDialog(show);
                        }
                    }
                };

                view.post(new Runnable() {
                    @Override
                    public void run() {
                        if(isBackup == false){

                            if(brasb3Bool){
                                //未选中
                                for (int i = 0; i < checkboxs.size(); i++) {
                                    if(!checkboxs.get(i)){
                                        restoryByFileName(getPathByLastName(list.get(i)));
                                    }
                                }
                            }

                            if(brasb2Bool){
                                //选中
                                for (int i = 0; i < checkboxs.size(); i++) {
                                    if(checkboxs.get(i)){
                                        restoryByFileName(getPathByLastName(list.get(i)));
                                    }
                                }
                            }

                            if(brasb1Bool){
                                //所有
                                for (String s : list) {
                                    restoryByFileName(getPathByLastName(s));
                                }
                            }
                            //都没有选中
                            if(brasb1Bool==false && brasb2Bool ==false && brasb3Bool ==false){
                                //如果都没有选择，我们就默认走已经勾选的
                                for (int i = 0; i < checkboxs.size(); i++) {
                                    if(checkboxs.get(i)){
                                        restoryByFileName(getPathByLastName(list.get(i)));
                                    }
                                }
                            }
                            Toast.makeText(backupRestoreActivity.this, "所选应用都已恢复 ", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(backupRestoreActivity.this, "请切换回恢复模式", Toast.LENGTH_SHORT).show();
                        }
                        Message msg = new Message();
                        msg.what=0;
                        handler.sendMessage(msg);
                    }
                });
            }
        });

        brasearchb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchStr = braet1.getText().toString();
                pkginfos = multiFunc.indexOfPKGS(backupRestoreActivity.this,searchStr,pkginfos,checkboxs,0);
                showPKGS(lv1);
            }
        });

        lv1.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                String str = "";
                if(isBackup){
                    PKGINFO pkginfo = pkginfos.get(i);
                    str = pkginfo.toString();
                }else{
                    str = list.get(i);
                }
                ClipboardManager cpm = (ClipboardManager) backupRestoreActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                cpm.setText(str);
                Toast.makeText(backupRestoreActivity.this, "已复制", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

    }
    private Boolean extractAssertFile(String sysupfile,String filesDir){
        File sysupF = new File(sysupfile);
        File fileD = new File(filesDir);
        if(!fileD.exists()){
            fileD.mkdirs();
        }
        if(!sysupF.exists()){
            extactAssetsFile(this,"bar.sh",sysupfile);
        }
        return sysupF.exists();
    }

    private boolean checkTools(){
        String filesDir =getMyHomeFilesPath(this);
        String barfile = filesDir+"/bar.sh";
        if(extractAssertFile(barfile,filesDir)){
            Toast.makeText(this, "备份跟恢复脚本已存在", Toast.LENGTH_SHORT).show();
            return true;
        }else{
            Toast.makeText(this, "备份跟恢复脚本无法获取，请退出重试或者重新安装app", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(backupRestoreActivity.this);
            alertDialog.setTitle("警告");
            alertDialog.setMessage("备份跟恢复脚本没有找到,请补全脚本再使用");
            alertDialog.setPositiveButton("继续", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    jump(backupRestoreActivity.this,importToolsActivity.class);
                }
            });
            alertDialog.setNegativeButton("补全组件脚本", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    jump(backupRestoreActivity.this,importToolsActivity.class);
                }
            });
            alertDialog.show();
        }
        return false;
    }


    private boolean backupByPKGNAME(String pkgname){
        String filesDir =getMyHomeFilesPath(this);
        String barfile = filesDir+"/bar.sh";
        String cmdstr = "sh "+barfile+" backup " + pkgname;
        CMD cmd = new CMD(cmdstr);
        return cmd.getResultCode() ==0;
    }

    private boolean restoryByFileName(String filename){
        String pkgname = filename.replaceAll(".tar.gz","");
        if(!pkgname.equals(getPackageName())){
           return restoryByPKGNAME(pkgname);
        }
        return false;
    }

    private boolean restoryByPKGNAME(String pkgname){
        String filesDir =getMyHomeFilesPath(this);
        String barfile = filesDir+"/bar.sh";
        String cmdstr = "sh "+barfile+" restory " + pkgname;
        CMD cmd = new CMD(cmdstr);
        return cmd.getResultCode() ==0;
    }

    private void getUserEnablePKGS(){
        multiFunc.queryUserEnablePKGS(this,pkginfos,checkboxs,0);
    }

    //获取对应的应用程序
    private void getPKGS(){
        multiFunc.queryPKGS(this,pkginfos,checkboxs,0);
    }

    //获取启用的应用程序
    private void getEnablePKGS(){
        multiFunc.queryEnablePKGS(this,pkginfos,checkboxs,0);
    }

    //获取对应的应用程序
    private void getUserPKGS(){
        multiFunc.queryUserPKGS(this,pkginfos,checkboxs,0);
    }

    //获取对应的应用程序
    private void getDisablePKGS(){
        multiFunc.queryDisablePKGS(this,pkginfos,checkboxs,0);
    }

    private void showPKGS(ListView listView){
        if(isBackup){
            PKGINFOAdapter pkginfoAdapter = new PKGINFOAdapter(pkginfos, backupRestoreActivity.this, checkboxs);
            listView.setAdapter(pkginfoAdapter);
        }else{
            USERAdapter userAdapter = new USERAdapter(list, backupRestoreActivity.this, checkboxs);
            listView.setAdapter(userAdapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE,0,0,"显示所有应用");
        menu.add(Menu.NONE,1,1,"显示所有应用(包括禁用)");
        menu.add(Menu.NONE,2,2,"显示用户安装的应用");
        menu.add(Menu.NONE,3,3,"显示用户安装的应用(包括禁用)");
        menu.add(Menu.NONE,4,4,"显示已禁用的应用");
        menu.add(Menu.NONE,5,5,"显示本地备份文件");
        menu.add(Menu.NONE,6,6,"退出");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        isBackup=true;
        setTitle("当前为: 备份");
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
                getUserEnablePKGS();
                showPKGS(lv1);
                break;
            case 3:
                getUserPKGS();
                showPKGS(lv1);
                break;
            case 4:
                getDisablePKGS();
                showPKGS(lv1);
                break;
            case 5:
                //列出本地已经备份的文件
                listLocalBackupFiles();
                break;
            case 6:
                fuckActivity.getIns().killall();
                ;
        }
        return super.onOptionsItemSelected(item);
    }

}
