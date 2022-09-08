package org.fqaosp.myActivitys;

import static org.fqaosp.utils.fileTools.extactAssetsFile;
import static org.fqaosp.utils.fileTools.getMyHomeFilesPath;
import static org.fqaosp.utils.fileTools.getPathByLastName;
import static org.fqaosp.utils.multiFunc.dismissDialogHandler;
import static org.fqaosp.utils.multiFunc.preventDismissDialog;
import static org.fqaosp.utils.multiFunc.sendHandlerMSG;
import static org.fqaosp.utils.multiFunc.showImportToolsDialog;
import static org.fqaosp.utils.multiFunc.showInfoMsg;
import static org.fqaosp.utils.multiFunc.showMyDialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
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
 *
 * 在備份的時候會出現無法訪問/data/data/路徑下完整文件夾的問題
 * 需要從面具的“挂在命名空間模式”那裏修改，修改為“全局命名空間”
 *
 * 參考neobackup: https://github.com/NeoApplications/Neo-Backup/blob/bee23171beaac044bcb34e2b8371d832a9c4709c/FAQ.md#at-restore-the-data-directory-of-the-app-does-not-exist
 *
 * */

public class backupRestoreActivity extends AppCompatActivity {
    private ArrayList<PKGINFO> pkginfos = new ArrayList<>();
    private ArrayList<String> list = new ArrayList<>();
    private ArrayList<Boolean> checkboxs = new ArrayList<>();
    private ListView lv1;
    private EditText braet1;
    private Button b1 ,b2,brasearchb;
    private Switch brasb1,brasb2,brasb3;
    private Spinner brasp,brasp2;
    private Boolean brasb1Bool,brasb2Bool,brasb3Bool,isBackup;
    private String file_end="";
    private String [] mode={"数据+安装包","数据","安装包"};
    private String [] mode2={"full","data","apk"};
    private String [] fileEnd={"tgz","txz","tbz"};
    private String [] fileEnd2={".tar.gz",".tar.xz",".tar.bz2"};
    private int mode_index=0,fileEnd_index=0;

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
        file_end=fileEnd2[fileEnd_index];
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
                        String s1 = listFile.toString();
                        if(s1.indexOf(file_end) != -1){
                            list.add(s1);
                            checkboxs.add(false);
                        }
                    }
                }else{
                    Toast.makeText(a, "看样子还没有备份过", Toast.LENGTH_SHORT).show();
                }
                sendHandlerMSG(handler,0);
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
        brasp = findViewById(R.id.brasp);
        brasp2 = findViewById(R.id.brasp2);
        lv1 = findViewById(R.id.bralv1);
        brasb1Bool=false;
        brasb2Bool=true;
        brasb3Bool=false;
        brasb1.setChecked(brasb1Bool);
        brasb2.setChecked(brasb2Bool);
        brasb3.setChecked(brasb3Bool);
        //默认是备份模式
        isBackup=true;
        brasp.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,mode));
        brasp2.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,fileEnd));

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
                Handler handler = dismissDialogHandler(0,show);
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        if(isBackup){
                            if(brasb3Bool){
                                //未勾选
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
                                //勾选
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
                            //都没有勾选
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
                        sendHandlerMSG(handler,0);

                    }
                });

            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog show = showMyDialog(backupRestoreActivity.this,"提示","正在恢复应用,请稍后(可能会出现无响应，请耐心等待)....");
                Handler handler = dismissDialogHandler(0,show);
                view.post(new Runnable() {
                    @Override
                    public void run() {

                        if(isBackup == false){

                            if(brasb3Bool){
                                //未勾选
                                for (int i = 0; i < checkboxs.size(); i++) {
                                    if(!checkboxs.get(i)){
                                        restoryByFileName(getPathByLastName(list.get(i)));
                                    }
                                }
                            }

                            if(brasb2Bool){
                                //勾选
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
                            //都没有勾选
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
                        sendHandlerMSG(handler,0);
                    }
                });
            }
        });

        brasearchb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchStr = braet1.getText().toString();
                if(isBackup){
                    pkginfos = multiFunc.indexOfPKGS(backupRestoreActivity.this,searchStr,pkginfos,checkboxs,0);
                }else{
                    list=multiFunc.indexOfLIST(list,checkboxs,searchStr);
                }
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

        brasp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mode_index=i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        brasp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                fileEnd_index=i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

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
            showImportToolsDialog(this,"备份跟恢复脚本无法获取，请退出重试或者重新安装app","备份跟恢复脚本没有找到,请补全脚本再使用");
        }
        return false;
    }

    private boolean backupByPKGNAME(String pkgname){
        String filesDir =getMyHomeFilesPath(this);
        String barfile = filesDir+"/bar.sh";
        String cmdstr = "sh "+barfile+" backup " + pkgname +" " + mode2[mode_index] + " " + fileEnd[fileEnd_index];
        CMD cmd = new CMD(cmdstr);
        return cmd.getResultCode()==0;
    }

    private boolean restoryByFileName(String filename){
        file_end=fileEnd2[fileEnd_index];
        String pkgname = filename.replaceAll(file_end,"");
        if(!pkgname.equals(getPackageName())){
           return restoryByPKGNAME(pkgname);
        }
        return false;
    }

    private boolean restoryByPKGNAME(String pkgname){
        String filesDir =getMyHomeFilesPath(this);
        String barfile = filesDir+"/bar.sh";
        String cmdstr = "sh "+barfile+" restory " + pkgname+" " + mode2[mode_index] + " " + fileEnd[fileEnd_index];
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
        menu.add(Menu.NONE,6,6,"帮助");
        menu.add(Menu.NONE,7,7,"退出");
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
                showInfoMsg(this,"帮助信息","该页面是用于应用备份与恢复的,支持应用备份与恢复，可选择只备份数据、安装包、安装包+数据，也支持仅恢复数据、安装包、安装包+数据，需要安装fqtools,如果没有安装，则会自动跳转安装页面，按照页面提示安装即可。\r\n" +
                        "1.右上角三个点，显示本地备份文件，会列出默认目录下所有通过该软件备份的应用压缩包。\r\n" +
                        "2.备份，备份应用.\r\n" +
                        "3.恢复，恢复应用.\r\n" +
                        "4.全选，不管有没有勾选，都会操作当前列表所有应用.\r\n" +
                        "5.勾选，仅操作勾选的应用.\r\n" +
                        "6.未勾选,仅操作勾选以外的应用.\r\n" +
                        "7.{数据+安装包，数据，安装包}，默认是全部，即备份该应用所有数据包括安装包。\r\n" +
                        "8.{tgz,txz,tbz},默认是采用tar.gz压缩格式，这个速度最快，txz模式速度最慢，tbz中规中矩.\r\n" +
                        "9.搜索框支持中英文搜索，不区分大小写.\r\n"
                );
                break;
            case 7:
                fuckActivity.getIns().killall();
                ;
        }
        return super.onOptionsItemSelected(item);
    }

}
