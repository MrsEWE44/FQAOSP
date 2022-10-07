package org.fqaosp.myActivitys;

import static org.fqaosp.utils.fileTools.extactAssetsFile;
import static org.fqaosp.utils.fileTools.getMyHomeFilesPath;
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
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.fqaosp.R;
import org.fqaosp.adapter.PKGINFOAdapter;
import org.fqaosp.entity.PKGINFO;
import org.fqaosp.threads.cmdThread;
import org.fqaosp.utils.CMD;
import org.fqaosp.utils.fuckActivity;
import org.fqaosp.utils.multiFunc;

import java.io.File;
import java.util.ArrayList;

public class appDisableActivity extends AppCompatActivity implements View.OnClickListener{
    private ArrayList<PKGINFO> pkginfos = new ArrayList<>();
    private ArrayList<Boolean> checkboxs = new ArrayList<>();
    private Button ada_disableappb,ada_enableappb, ada_disablemiuib,ada_disableflymeb,ada_disablemyuib, ada_disablecolorb,ada_disablevivob,ada_searchb;
    private EditText ada_et1;

    private ListView ada_disablelv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_disable_activity);
        fuckActivity.getIns().add(this);
        setTitle("应用程序禁用");
        buttoninit();
        hideButton();
        String filesDir =getMyHomeFilesPath(this);
        String sysupfile = filesDir+"/startupsystem.sh";
        hideButtonClick();
        if(extractAssertFile(sysupfile,filesDir)){
            Toast.makeText(this, "禁用脚本已存在", Toast.LENGTH_SHORT).show();
        }else{
            showImportToolsDialog(this,"禁用脚本无法获取，请退出重试或者重新安装app","禁用脚本没有找到,部分功能使用将受到限制或者异常,要继续使用吗？");
        }

        ada_disableappb.setOnClickListener(this);
        ada_enableappb.setOnClickListener(this);
        ada_searchb.setOnClickListener(this);

        ada_disablelv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                PKGINFO pkginfo = pkginfos.get(i);
                ClipboardManager cpm = (ClipboardManager) appDisableActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                cpm.setText(pkginfo.toString());
                Toast.makeText(appDisableActivity.this, "已复制", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

    }

    //按钮点击事件
    private void btClick(Context context , View view ,Activity activity , int mode , boolean isHideBt){
        if(isHideBt){
            String filesDir =getMyHomeFilesPath(context);
            switch (mode){
                case 0:
                    hideButtonCMD("cd "+filesDir +" && sh startupsystem.sh miui dis","miui禁用策略已运行结束",view);
                    break;
                case 1:
                    hideButtonCMD("cd "+filesDir +" && sh startupsystem.sh flyme dis","flyme禁用策略已运行结束",view);
                    break;
                case 2:
                    hideButtonCMD("cd "+filesDir +" && sh startupsystem.sh myui dis","myui禁用策略已运行结束",view);
                    break;
                case 3:
                    hideButtonCMD("cd "+filesDir +" && sh startupsystem.sh color dis","color禁用策略已运行结束",view);
                    break;
                case 4:
                    hideButtonCMD("cd "+filesDir +" && sh startupsystem.sh vivo dis","vivo禁用策略已运行结束",view);
                    break;
            }
        }else{
            if(mode == 6545){
                String searchStr = ada_et1.getText().toString();
                pkginfos = multiFunc.indexOfPKGS(activity,searchStr,pkginfos,checkboxs,0);
                showPKGS(ada_disablelv);
            }else{
                AlertDialog show = showMyDialog(context, "提示", "请稍后，正在"+(mode == 0 ? "禁" : "启")+"用中...(可能会出现无响应，请耐心等待)....");
                preventDismissDialog(show);
                Handler handler = new Handler(){
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        if (msg.what == 0) {
                            if(mode == 0){
                                getUserEnablePKGS();
                            }
                            if(mode == 1){
                                getDisablePKGS();
                            }
                            showPKGS(ada_disablelv);
                            multiFunc.dismissDialog(show);
                        }
                    }
                };
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < checkboxs.size(); i++) {
                            if(checkboxs.get(i)){
                                PKGINFO pkginfo = pkginfos.get(i);
                                String cmdstr = "";
                                String statestr="";
                                if(mode == 0){
                                    cmdstr = "pm disable " + pkginfo.getPkgname();
                                    statestr = "禁用 ";
                                }
                                if(mode == 1){
                                    cmdstr = "pm enable " + pkginfo.getPkgname();
                                    statestr = "启用 ";
                                }
                                CMD cmd = new CMD(cmdstr);
                                if(cmd.getResultCode() ==0){
                                    Toast.makeText(context, statestr+pkginfo.getAppname() + " 成功", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(context, statestr+pkginfo.getAppname() + " 失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        sendHandlerMSG(handler, 0);
                    }
                });

            }


        }
    }

    private void buttoninit(){
        ada_disableappb = findViewById(R.id.ada_disableappb);
        ada_enableappb = findViewById(R.id.ada_enableappb);
        ada_disablelv = findViewById(R.id.ada_disablelv);
        ada_disablemiuib = findViewById(R.id.ada_disablemiuib);
        ada_disableflymeb = findViewById(R.id.ada_disableflymeb);
        ada_disablemyuib = findViewById(R.id.ada_disablemyuib);
        ada_disablecolorb = findViewById(R.id.ada_disablecolorb);
        ada_disablevivob = findViewById(R.id.ada_disablevivob);
        ada_searchb = findViewById(R.id.ada_searchb1);
        ada_et1 = findViewById(R.id.ada_et1);
    }

    private void hideButton(){
        ada_disablemiuib.setVisibility(View.INVISIBLE);
        ada_disableflymeb.setVisibility(View.INVISIBLE);
        ada_disablemyuib.setVisibility(View.INVISIBLE);
        ada_disablecolorb.setVisibility(View.INVISIBLE);
        ada_disablevivob.setVisibility(View.INVISIBLE);
    }

    private void showButton(){
        ada_disablemiuib.setVisibility(View.VISIBLE);
        ada_disableflymeb.setVisibility(View.VISIBLE);
        ada_disablemyuib.setVisibility(View.VISIBLE);
        ada_disablecolorb.setVisibility(View.VISIBLE);
        ada_disablevivob.setVisibility(View.VISIBLE);
    }

    private  void hideButtonCMD(String cmd,String msgstr,View view){
        AlertDialog show = showMyDialog(appDisableActivity.this, "提示", "请稍后，正在执行禁用策略...");
        preventDismissDialog(show);
        Handler handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == 0) {
                    Toast.makeText(appDisableActivity.this,msgstr,Toast.LENGTH_SHORT).show();
                    multiFunc.dismissDialog(show);
                }
            }
        };
        view.post(new Runnable() {
            @Override
            public void run() {
                CMD cmd1 = new CMD(cmd);
                cmd1.getResultCode();
                sendHandlerMSG(handler,0);
            }
        });

    }

    private void hideButtonClick(){
        ada_disablemiuib.setOnClickListener(this);
        ada_disableflymeb.setOnClickListener(this);
        ada_disablemyuib.setOnClickListener(this);
        ada_disablecolorb.setOnClickListener(this);
        ada_disablevivob.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add(Menu.NONE,0,0,"显示所有应用");
        menu.add(Menu.NONE,1,1,"显示所有应用(包括禁用)");
        menu.add(Menu.NONE,2,2,"显示用户安装的应用");
        menu.add(Menu.NONE,3,3,"显示用户安装的应用(包括禁用)");
        menu.add(Menu.NONE,4,4,"显示已禁用的应用");
        menu.add(Menu.NONE,5,5,"显示其他禁用选项");
        menu.add(Menu.NONE,6,6,"帮助");
        menu.add(Menu.NONE,7,7,"退出");
        return super.onCreateOptionsMenu(menu);
    }

    private Boolean extractAssertFile(String sysupfile,String filesDir){
        File sysupF = new File(sysupfile);
        File fileD = new File(filesDir);
        if(!fileD.exists()){
           fileD.mkdirs();
        }
        if(!sysupF.exists()){
           extactAssetsFile(this,"startupsystem.sh",sysupfile);
        }
        return sysupF.exists();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId){
            case 0:
                getEnablePKGS();
                showPKGS(ada_disablelv);
                break;
            case 1:
                getPKGS();
                showPKGS(ada_disablelv);
                break;
            case 2:
                getUserEnablePKGS();
                showPKGS(ada_disablelv);
                break;
            case 3:
                getUserPKGS();
                showPKGS(ada_disablelv);
                break;
            case 4:
                getDisablePKGS();
                showPKGS(ada_disablelv);
                break;
            case 5:
                showButton();
                break;
            case 6:
                showInfoMsg(this,"帮助信息","该页面是用于apk禁用的，需要安装fqtools,如果没有安装，则会自动跳转安装页面，按照页面提示安装即可。\r\n" +
                        "1.禁用，勾选列出来的应用，即可批量禁用.\r\n" +
                        "2.启用，勾选列出来的应用，即可批量启用.\r\n" +
                        "3.点击右上角三个点，可以列出相关应用列表以及隐藏禁用策略.\r\n" +
                        "4.禁用策略，该功能并不稳定，但是有部分设备是可以支持，里面有提供miui、flyme、myui、color、vivo系统的禁用策略，软件里执行需要root执行，而电脑直接允许脚本则不需要root.\r\n");
                break;
            case 7:
                fuckActivity.getIns().killall();
                ;
        }
        return super.onOptionsItemSelected(item);
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
        PKGINFOAdapter pkginfoAdapter = new PKGINFOAdapter(pkginfos, appDisableActivity.this, checkboxs);
        listView.setAdapter(pkginfoAdapter);
    }

    @Override
    public void onClick(View view) {
        Context context = this;
        Activity activity = this;
        switch (view.getId()) {
            case R.id.ada_disableappb:
                btClick(context, view, activity, 0, false);
                break;
            case R.id.ada_enableappb:
                btClick(context, view, activity, 1, false);
                break;
            case R.id.ada_searchb1:
                btClick(context, view, activity, 6545, false);
                break;
            case R.id.ada_disablemiuib:
                btClick(context, view, activity, 0, true);
                break;
            case R.id.ada_disableflymeb:
                btClick(context, view, activity, 1, true);
                break;
            case R.id.ada_disablemyuib:
                btClick(context, view, activity, 2, true);
                break;
            case R.id.ada_disablecolorb:
                btClick(context, view, activity, 3, true);
                break;
            case R.id.ada_disablevivob:
                btClick(context, view, activity, 4, true);
                break;
        }
    }
}
