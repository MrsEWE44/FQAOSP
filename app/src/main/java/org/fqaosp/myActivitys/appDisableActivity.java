package org.fqaosp.myActivitys;

import static org.fqaosp.utils.fileTools.extactAssetsFile;
import static org.fqaosp.utils.multiFunc.jump;
import static org.fqaosp.utils.multiFunc.preventDismissDialog;

import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
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

public class appDisableActivity extends AppCompatActivity {
    private ArrayList<PKGINFO> pkginfos = new ArrayList<>();
    private ArrayList<Boolean> checkboxs = new ArrayList<>();
    private Button ada_disableappb;
    private  Button ada_enableappb;
    private Button ada_disablemiuib;
    private  Button ada_disableflymeb;
    private  Button ada_disablemyuib;
    private  Button ada_disablecolorb;
    private  Button ada_disablevivob;
    private Button ada_searchb;
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
        String datadir="/data/data/"+getPackageName();
        String filesDir = datadir+"/files";
        String sysupfile = filesDir+"/startupsystem.sh";
        hideButtonClick(filesDir);
        if(extractAssertFile(sysupfile,filesDir)){
            Toast.makeText(this, "禁用脚本已存在", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "禁用脚本无法获取，请退出重试或者重新安装app", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(appDisableActivity.this);
            alertDialog.setTitle("警告");
            alertDialog.setMessage("禁用脚本没有找到,部分功能使用将受到限制或者异常,要继续使用吗？");
            alertDialog.setPositiveButton("继续", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            alertDialog.setNegativeButton("补全组件脚本", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    jump(appDisableActivity.this,importToolsActivity.class);
                }
            });
            alertDialog.show();
        }

        ada_disableappb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < checkboxs.size(); i++) {
                            if(checkboxs.get(i)){
                                PKGINFO pkginfo = pkginfos.get(i);
                                CMD cmd = new CMD("pm disable " + pkginfo.getPkgname());
                                if(cmd.getResultCode() ==0){
                                    Toast.makeText(appDisableActivity.this, "禁用 "+pkginfo.getAppname() + " 成功", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(appDisableActivity.this, "禁用 "+pkginfo.getAppname() + " 失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        getUserEnablePKGS();
                        showPKGS(ada_disablelv);
                    }
                });
            }
        });
        ada_enableappb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < checkboxs.size(); i++) {
                            if(checkboxs.get(i)){
                                PKGINFO pkginfo = pkginfos.get(i);
                                CMD cmd = new CMD("pm enable " + pkginfo.getPkgname());
                                if(cmd.getResultCode() ==0){
                                    Toast.makeText(appDisableActivity.this, "启用 "+pkginfo.getAppname() + " 成功", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(appDisableActivity.this, "启用 "+pkginfo.getAppname() + " 失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        getDisablePKGS();
                        showPKGS(ada_disablelv);
                    }
                });
            }
        });

        ada_searchb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        String searchStr = ada_et1.getText().toString();
                        pkginfos = multiFunc.indexOfPKGS(appDisableActivity.this,searchStr,pkginfos,checkboxs,0);
                        showPKGS(ada_disablelv);
                    }
                });
            }
        });

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

    private  void hideButtonCMD(String cmd,String msg){
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(appDisableActivity.this);
        alertDialog.setTitle("提示");
        alertDialog.setMessage("请稍后，正在执行禁用策略...");
        AlertDialog show = alertDialog.show();
        preventDismissDialog(show);
        cmdThread ee = new cmdThread(cmd, msg, msg, appDisableActivity.this, show);
        ee.start();
    }

    private void hideButtonClick(String filesDir){
        ada_disablemiuib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideButtonCMD("cd "+filesDir +" && sh startupsystem.sh miui dis","miui禁用策略已运行结束");
            }
        });
        ada_disableflymeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideButtonCMD("cd "+filesDir +" && sh startupsystem.sh flyme dis","flyme禁用策略已运行结束");
            }
        });
        ada_disablemyuib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideButtonCMD("cd "+filesDir +" && sh startupsystem.sh myui dis","myui禁用策略已运行结束");
            }
        });
        ada_disablecolorb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideButtonCMD("cd "+filesDir +" && sh startupsystem.sh color dis","color禁用策略已运行结束");
            }
        });
        ada_disablevivob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideButtonCMD("cd "+filesDir +" && sh startupsystem.sh vivo dis","vivo禁用策略已运行结束");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add(Menu.NONE,0,0,"显示所有应用");
        menu.add(Menu.NONE,1,1,"显示所有应用(包括禁用)");
        menu.add(Menu.NONE,2,2,"显示用户安装的应用");
        menu.add(Menu.NONE,3,3,"显示用户安装的应用(包括禁用)");
        menu.add(Menu.NONE,4,4,"显示已禁用的应用");
        menu.add(Menu.NONE,5,5,"显示其他禁用选项");
        menu.add(Menu.NONE,6,6,"退出");
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

}
