package org.fqaosp.myActivitys;

import static org.fqaosp.utils.fileTools.getMyHomeFilesPath;
import static org.fqaosp.utils.fileTools.getPathByLastName;
import static org.fqaosp.utils.multiFunc.checkTools;
import static org.fqaosp.utils.multiFunc.isSuEnable;
import static org.fqaosp.utils.multiFunc.sendHandlerMSG;
import static org.fqaosp.utils.multiFunc.showCMDInfoMSG;
import static org.fqaosp.utils.multiFunc.showImportToolsDialog;
import static org.fqaosp.utils.multiFunc.showInfoMsg;
import static org.fqaosp.utils.multiFunc.showMyDialog;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
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
import androidx.viewpager.widget.ViewPager;

import org.fqaosp.R;
import org.fqaosp.adapter.FILESHARINGVIEWPAGERAdapter;
import org.fqaosp.adapter.PKGINFOAdapter;
import org.fqaosp.adapter.USERAdapter;
import org.fqaosp.entity.PKGINFO;
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

    private String file_end="";
    private String [] mode={"数据+安装包","数据","安装包"};
    private String [] mode2={"full","data","apk"};
    private String [] fileEnd={"tgz","tbz","txz","tbr"};
    private String [] fileEnd2={".tar.gz",".tar.bz2",".tar.xz",".tar.br"};
    private int mode_index=0,fileEnd_index=0;

    private ViewPager brmavp;
    private View backupView, restoreView;
    private ArrayList<View> views = new ArrayList<>();
    private ArrayList<String> slist = new ArrayList<>();
    private ListView backupLv1,restoreLv1;
    private Integer viewPageIndex = 0;
    private Boolean switchBool1,switchBool2,switchBool3,isBackup;
    private String scriptName="fqtools.sh";

    private boolean isRoot = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.backup_restore_menu_activity);
        fuckActivity.getIns().add(this);
        setTitle("备份与恢复");
        isRoot=isSuEnable();
        if(isRoot){
            initViews();
            checkTools(this);
        }else{
            showInfoMsg(this,"提示","本功能需要root才能正常使用");
        }
    }

    private void initViews(){
        brmavp = findViewById(R.id.brmavp);
        backupView = getLayoutInflater().inflate(R.layout.backup_activity, null);
        restoreView = getLayoutInflater().inflate(R.layout.restore_activity, null);
        views.add(backupView);
        views.add(restoreView);
        slist.add("备份");
        slist.add("恢复");
        FILESHARINGVIEWPAGERAdapter adapter = new FILESHARINGVIEWPAGERAdapter(views, slist);
        brmavp.setAdapter(adapter);
        initOnListen();
        initBackupView();
        initRestoreView();

    }

    private void initRestoreView() {
        isBackup=false;
        Button b1 = restoreView.findViewById(R.id.rabt1);
        Button brasearchb = restoreView.findViewById(R.id.rasearchb);
        EditText braet1 = restoreView.findViewById(R.id.raet1);
        Switch brasb1 =restoreView.findViewById(R.id.rasb1);
        Switch brasb2 =restoreView.findViewById(R.id.rasb2);
        Switch brasb3 =restoreView.findViewById(R.id.rasb3);
        Spinner brasp = restoreView.findViewById(R.id.rasp);
        Spinner brasp2 = restoreView.findViewById(R.id.rasp2);
        restoreLv1 = restoreView.findViewById(R.id.ralv1);
        initBool();
        setSwitchChecked(brasb1,brasb2,brasb3);
        initSpinnerBt(brasp,brasp2);
        clickedSwitchBt(brasb1,brasb2,brasb3);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkRestoryTools()){
                    StringBuilder sb = new StringBuilder();
                    sb.append("aaa=(");
                    String filesDir =getMyHomeFilesPath(backupRestoreActivity.this);
                    String barfile = filesDir+"/"+scriptName;
                    String cmdstr = "sh "+barfile+" restory $pp " + mode2[mode_index] + " " + fileEnd[fileEnd_index];
                    if(isBackup == false){
                        for (int i = 0; i < checkboxs.size(); i++) {
                            String s = getPathByLastName(list.get(i));
                            if(switchBool3 && !checkboxs.get(i)){
                                sb.append("\""+getRestoryFileName(s)+"\" ");
                            }

                            if(switchBool2 && checkboxs.get(i)){
                                sb.append("\""+getRestoryFileName(s)+"\" ");
                            }

                            if(switchBool1){
                                sb.append("\""+getRestoryFileName(s)+"\" ");
                            }
                        }
                        sb.append(");for pp in ${aaa[@]};do "+cmdstr+" ;done;");
                        showCMDInfoMSG(backupRestoreActivity.this,false,sb.toString(),isRoot,"正在恢复应用,请稍后(可能会出现无响应，请耐心等待)....","恢复应用结束.");
                    }else{
                        Toast.makeText(backupRestoreActivity.this, "请切换回恢复模式", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    showImportToolsDialog(backupRestoreActivity.this,"当前功能选项缺失相关组件,需要补全组件才能正常使用","当前功能选项缺失相关组件,需要补全组件才能正常使用");
                }
            }
        });

        brasearchb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchStr = braet1.getText().toString();
                list=multiFunc.indexOfLIST(list,checkboxs,searchStr);
                showPKGS(restoreLv1);
            }
        });

        restoreLv1.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                String str = list.get(i);
                copyText(str);
                return false;
            }
        });

        clickedSpinnerBt(brasp,brasp2);
    }

    private void initBackupView() {
        isBackup=true;
        Button b1 = backupView.findViewById(R.id.babt1);
        Button brasearchb = backupView.findViewById(R.id.basearchb);
        EditText braet1 = backupView.findViewById(R.id.baet1);
        Switch brasb1 =backupView.findViewById(R.id.basb1);
        Switch brasb2 =backupView.findViewById(R.id.basb2);
        Switch brasb3 =backupView.findViewById(R.id.basb3);
        Spinner brasp = backupView.findViewById(R.id.basp);
        Spinner brasp2 = backupView.findViewById(R.id.basp2);
        backupLv1 = backupView.findViewById(R.id.balv1);
        initBool();
        setSwitchChecked(brasb1,brasb2,brasb3);
        initSpinnerBt(brasp,brasp2);
        clickedSwitchBt(brasb1,brasb2,brasb3);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkBackupTools()){
                    StringBuilder sb = new StringBuilder();
                    sb.append("aaa=(");
                    String filesDir =getMyHomeFilesPath(backupRestoreActivity.this);
                    String barfile = filesDir+"/"+scriptName;
                    String cmdstr = "sh "+barfile+" backup $pp " + mode2[mode_index] + " " + fileEnd[fileEnd_index];
                    if(isBackup){
                        //未勾选
                        for (int i = 0; i < checkboxs.size(); i++) {
                            PKGINFO pkginfo = pkginfos.get(i);
                            if(switchBool3 && !checkboxs.get(i)){
                                if(!pkginfo.getPkgname().equals(getPackageName())){
                                    sb.append("\""+pkginfo.getPkgname()+"\" ");
                                }
                            }
                            if(switchBool2 && checkboxs.get(i)){
                                if(!pkginfo.getPkgname().equals(getPackageName())){
                                    sb.append("\""+pkginfo.getPkgname()+"\" ");
                                }
                            }
                            if(switchBool1){
                                if(!pkginfo.getPkgname().equals(getPackageName())){
                                    sb.append("\""+pkginfo.getPkgname()+"\" ");
                                }
                            }
                        }
                        sb.append(");for pp in ${aaa[@]};do "+cmdstr+" ;done;");
                        showCMDInfoMSG(backupRestoreActivity.this,false,sb.toString(),isRoot,"正在备份应用,请稍后(可能会出现无响应，请耐心等待)....","备份应用结束.");

                    }else{
                        Toast.makeText(backupRestoreActivity.this, "请切换回备份模式", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    showImportToolsDialog(backupRestoreActivity.this,"当前功能选项缺失相关组件,需要补全组件才能正常使用","当前功能选项缺失相关组件,需要补全组件才能正常使用");
                }
            }
        });

        brasearchb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchStr = braet1.getText().toString();
                pkginfos = multiFunc.indexOfPKGS(backupRestoreActivity.this,searchStr,pkginfos,checkboxs,0);
                showPKGS(backupLv1);
            }
        });

        backupLv1.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                PKGINFO pkginfo = pkginfos.get(i);
                String str = pkginfo.toString();
                copyText(str);
                return false;
            }
        });

        clickedSpinnerBt(brasp,brasp2);

    }

    private void copyText(String str){
        ClipboardManager cpm = (ClipboardManager) backupRestoreActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
        cpm.setText(str);
        Toast.makeText(backupRestoreActivity.this, "已复制", Toast.LENGTH_SHORT).show();

    }

    private  void initSpinnerBt(Spinner brasp,Spinner brasp2){
        brasp.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,mode));
        brasp2.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,fileEnd));

    }

    private void clickedSpinnerBt(Spinner brasp ,int mode){
        brasp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(mode ==0){
                    mode_index=i;
                }else{
                    fileEnd_index=i;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void clickedSpinnerBt(Spinner brasp,Spinner brasp2){
        clickedSpinnerBt(brasp,0);
        clickedSpinnerBt(brasp2,1);
    }

    private void clickedSwitchBt(Switch brasb1,Switch brasb2,Switch brasb3){
        clickedSwitchBt(brasb1,brasb1,brasb2,brasb3,0);
        clickedSwitchBt(brasb2,brasb1,brasb2,brasb3,1);
        clickedSwitchBt(brasb3,brasb1,brasb2,brasb3,2);

    }

    private void clickedSwitchBt(Switch orgbt , Switch brasb1,Switch brasb2,Switch brasb3,int mode){
        orgbt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                switch (mode){
                    case 0:
                        switchBool1=b;
                        switchBool2=false;
                        switchBool3=false;
                        break;
                    case 1:
                        switchBool1=false;
                        switchBool2=b;
                        switchBool3=false;
                        break;
                    case 2:
                        switchBool1=false;
                        switchBool2=false;
                        switchBool3=b;
                        break;
                }

                setSwitchChecked(brasb1,brasb2,brasb3);
            }
        });

    }

    private void setSwitchChecked(Switch brasb1,Switch brasb2,Switch brasb3){
        brasb1.setChecked(switchBool1);
        brasb2.setChecked(switchBool2);
        brasb3.setChecked(switchBool3);
    }

    private void initBool(){
        switchBool1=false;
        switchBool2=true;
        switchBool3=false;
    }

    private void initOnListen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            brmavp.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                    viewPageIndex = brmavp.getCurrentItem();
                }
            });
        }

    }

    private void listLocalBackupFiles(){
        file_end=fileEnd2[fileEnd_index];
        permissionRequest.getExternalStorageManager(backupRestoreActivity.this);
        String s = Environment.getExternalStorageDirectory().toString();
        String localBackupDir= s+"/backup_app";
        File file = new File(localBackupDir);
        ProgressDialog show = showMyDialog(backupRestoreActivity.this,"正在扫描本地备份文件,请稍后(可能会出现无响应，请耐心等待)....");
        Handler handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                if(msg.what==0){
                    showPKGS(restoreLv1);
                    show.dismiss();
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

    private boolean checkUsrTool(){
        String filesDir =getMyHomeFilesPath(this);
        String usr=filesDir+"/usr";
        File file = new File(usr);
        return file.exists();
    }

    private boolean checkBackupTools(){
        if(checkUsrTool()){
            return true;
        }else if(checkUsrTool() == false && fileEnd_index < 2){
            return true;
        }
        return false;
    }

    private boolean checkRestoryTools(){
        if(checkUsrTool()){
            return true;
        }else if(checkUsrTool() == false && fileEnd_index < 3){
            return true;
        }
        return false;
    }

    private String getRestoryFileName(String filename){
        file_end=fileEnd2[fileEnd_index];
        String pkgname = filename.replaceAll(file_end,"");
        if(!pkgname.equals(getPackageName())){
            return pkgname;
        }
        return null;
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
    public boolean onMenuOpened(int featureId, Menu menu) {
        menu.clear();
        switch (viewPageIndex) {
            case 0:
                menu.add(Menu.NONE,0,0,"显示所有应用");
                menu.add(Menu.NONE,1,1,"显示所有应用(包括禁用)");
                menu.add(Menu.NONE,2,2,"显示用户安装的应用");
                menu.add(Menu.NONE,3,3,"显示用户安装的应用(包括禁用)");
                menu.add(Menu.NONE,4,4,"显示已禁用的应用");
                menu.add(Menu.NONE,5,5,"帮助");
                menu.add(Menu.NONE,6,6,"退出");
                break;
            case 1:
                menu.add(Menu.NONE,0,0,"显示本地备份文件");
                menu.add(Menu.NONE,1,1,"帮助");
                menu.add(Menu.NONE,2,2,"退出");
                break;
        }
        return super.onMenuOpened(featureId, menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("退出");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        clearlist();
        switch (viewPageIndex) {
            case 0:
                isBackup=true;
                switch (itemId){
                    case 0:
                        multiFunc.queryEnablePKGS(this,pkginfos,checkboxs,0);
                        showPKGS(backupLv1);
                        break;
                    case 1:
                        multiFunc.queryPKGS(this,pkginfos,checkboxs,0);
                        showPKGS(backupLv1);
                        break;
                    case 2:
                        multiFunc.queryUserEnablePKGS(this,pkginfos,checkboxs,0);
                        showPKGS(backupLv1);
                        break;
                    case 3:
                        multiFunc.queryUserPKGS(this,pkginfos,checkboxs,0);
                        showPKGS(backupLv1);
                        break;
                    case 4:
                        multiFunc.queryDisablePKGS(this,pkginfos,checkboxs,0);
                        showPKGS(backupLv1);
                        break;
                    case 5:
                        showInfoMsg(this,"帮助信息","该页面是用于应用备份与恢复的,支持应用备份与恢复，可选择只备份数据、安装包、安装包+数据，也支持仅恢复数据、安装包、安装包+数据，需要安装fqtools,如果没有安装，则会自动跳转安装页面，按照页面提示安装即可。\r\n" +
                                "1.右上角三个点，显示本地备份文件，会列出默认目录下所有通过该软件备份的应用压缩包。\r\n" +
                                "2.备份，备份应用.\r\n" +
                                "3.全选，不管有没有勾选，都会操作当前列表所有应用.\r\n" +
                                "4.勾选，仅操作勾选的应用.\r\n" +
                                "5.未勾选,仅操作勾选以外的应用.\r\n" +
                                "6.{数据+安装包，数据，安装包}，默认是全部，即备份该应用所有数据包括安装包。\r\n" +
                                "7.{tgz,tbz,txz,tbr},默认是采用tar.gz压缩格式，这个速度最快，tbr模式速度最慢但解压速度接近gzip，txz速度仅次于tbr但是压缩率最高.\r\n" +
                                "8.搜索框支持中英文搜索，不区分大小写.\r\n"
                        );
                        break;
                    case 6:
                        fuckActivity.getIns().killall();
                        ;
                }
                break;
            case 1:
                isBackup=false;
                switch (itemId){
                    case 0:
                        //列出本地已经备份的文件
                        listLocalBackupFiles();
                        break;
                    case 1:
                        showInfoMsg(this,"帮助信息","\r\n" +
                                "1.右上角三个点，显示本地备份文件，会列出默认目录下所有通过该软件备份的应用压缩包。\r\n" +
                                "2.恢复，恢复应用.\r\n" +
                                "3.全选，不管有没有勾选，都会操作当前列表所有应用.\r\n" +
                                "4.勾选，仅操作勾选的应用.\r\n" +
                                "5.未勾选,仅操作勾选以外的应用.\r\n" +
                                "6.{数据+安装包，数据，安装包}，默认是全部，即备份该应用所有数据包括安装包。\r\n" +
                                "7.{tgz,tbz,txz,tbr},默认是采用tar.gz压缩格式，这个速度最快，tbr模式速度最慢但解压速度接近gzip，txz速度仅次于tbr但是压缩率最高.\r\n" +
                                "8.搜索框支持中英文搜索，不区分大小写.\r\n"
                        );
                        break;
                    case 2:
                        fuckActivity.getIns().killall();
                        ;
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}
