package org.fqaosp.myActivitys;

import static org.fqaosp.utils.multiFunc.preventDismissDialog;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
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

    private ListView ada_disablelv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_disable_activity);
        fuckActivity.getIns().add(this);
        buttoninit();
        hideButton();
        String datadir="/data/data/"+getPackageName();
        String filesDir = datadir+"/files";
        String sysupfile = filesDir+"startupsystem.sh";
        hideButtonClick(filesDir);
        if(extractAssertFile(sysupfile,filesDir)){
            Toast.makeText(this, "禁用脚本已存在", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "禁用脚本无法获取，请退出重试或者重新安装app", Toast.LENGTH_SHORT).show();
            fuckActivity.getIns().killall();
        }
        ada_disableappb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < checkboxs.size(); i++) {
                    if(checkboxs.get(i)){
                        PKGINFO pkginfo = pkginfos.get(i);
                        CMD cmd = new CMD("pm disable " + pkginfo.getPkgname());
                        if(cmd.getResultCode() ==0){
                            Toast.makeText(appDisableActivity.this, "禁用 "+pkginfo.getAppname() + " 成功", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

            }
        });
        ada_enableappb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < checkboxs.size(); i++) {
                    if(checkboxs.get(i)){
                        PKGINFO pkginfo = pkginfos.get(i);
                        CMD cmd = new CMD("pm enable " + pkginfo.getPkgname());
                        if(cmd.getResultCode() ==0){
                            Toast.makeText(appDisableActivity.this, "启用 "+pkginfo.getAppname() + " 成功", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
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
        menu.add(Menu.NONE,1,1,"显示用户安装的应用");
        menu.add(Menu.NONE,2,2,"显示已禁用的应用");
        menu.add(Menu.NONE,3,3,"显示其他禁用选项");
        menu.add(Menu.NONE,4,4,"退出");
        return super.onCreateOptionsMenu(menu);
    }

    private Boolean extractAssertFile(String sysupfile,String filesDir){
        File sysupF = new File(sysupfile);
        File fileD = new File(filesDir);
        if(!fileD.exists()){
           fileD.mkdirs();
        }
        if(!sysupF.exists()){
           multiFunc.extactAssetsFile(this,"startupsystem.sh",sysupfile);
        }
        return sysupF.exists();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId){
            case 0:
                getPKGS();
                showPKGS(ada_disablelv);
                break;
            case 1:
                getUserPKGS();
                showPKGS(ada_disablelv);
                break;
            case 2:
                getDisablePKGS();
                showPKGS(ada_disablelv);
                break;
            case 3:
                showButton();
                break;
            case 4:
                fuckActivity.getIns().killall();
                ;
        }
        return super.onOptionsItemSelected(item);
    }
    //获取对应的应用程序
    private void getPKGS(){
        checkboxs.clear();
        pkginfos.clear();
        multiFunc.queryPKGS(this,pkginfos,checkboxs,0);
    }

    //获取对应的应用程序
    private void getUserPKGS(){
        checkboxs.clear();
        pkginfos.clear();
        multiFunc.queryUserPKGS(this,pkginfos,checkboxs,0);
    }

    //获取对应的应用程序
    private void getDisablePKGS(){
        checkboxs.clear();
        pkginfos.clear();
        multiFunc.queryDisablePKGS(this,pkginfos,checkboxs,0);
    }

    private void showPKGS(ListView listView){
        PKGINFOAdapter pkginfoAdapter = new PKGINFOAdapter(pkginfos, appDisableActivity.this, checkboxs);
        listView.setAdapter(pkginfoAdapter);
    }

}
