package org.fqaosp.myActivitys;

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
import org.fqaosp.sql.killAppDB;
import org.fqaosp.utils.CMD;
import org.fqaosp.utils.fuckActivity;
import org.fqaosp.utils.multiFunc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class killAppActivity extends AppCompatActivity {

    private ArrayList<PKGINFO> pkginfos = new ArrayList<>();
    private ArrayList<Boolean> checkboxs = new ArrayList<>();
    private ListView lv1;
    private killAppDB killAppdb = new killAppDB(killAppActivity.this, "killApp.db", null, 1);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kill_app_activity);
        fuckActivity.getIns().add(this);
        setTitle("后台管理");
        Button b1 = findViewById(R.id.kaab1);
        Button b2 = findViewById(R.id.kaab2);
        Button b3 = findViewById(R.id.kaab3);
        lv1 = findViewById(R.id.kaalv1);
        getRunning(1);
        showPKGS(lv1);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        if(killAppdb.count() == 0){
                            for (PKGINFO pkginfo : pkginfos) {
                                if(!pkginfo.getPkgname().equals(getPackageName())){
                                    //调用命令终止后台程序
                                    stopApp(pkginfo.getPkgname());
                                }
                            }
                        }else{
                            HashMap<String, Integer> select = killAppdb.select(null, 0);
                            for (Map.Entry<String, Integer> entry : select.entrySet()) {
                                stopApp(entry.getKey());
                            }
                        }

                        Toast.makeText(killAppActivity.this, "所有进程都已终止 ", Toast.LENGTH_SHORT).show();
                        getRunning(1);
                        showPKGS(lv1);
                    }
                });
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < checkboxs.size(); i++) {
                            if(checkboxs.get(i)){
                                PKGINFO pkginfo = pkginfos.get(i);
                                if(!pkginfo.getPkgname().equals(getPackageName())){
                                    stopApp(pkginfo.getPkgname());
                                    if(killAppdb.select(pkginfo.getPkgname(),0).size() == 0){
                                        killAppdb.insert(pkginfo.getPkgname(),0);
                                    }
                                }
                            }
                        }
                        Toast.makeText(killAppActivity.this, "所有进程都已终止 ", Toast.LENGTH_SHORT).show();
                        getRunning(1);
                        showPKGS(lv1);
                    }
                });
            }
        });

        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < checkboxs.size(); i++) {
                            if(!checkboxs.get(i)){
                                PKGINFO pkginfo = pkginfos.get(i);
                                if(!pkginfo.getPkgname().equals(getPackageName())){
                                    stopApp(pkginfo.getPkgname());
                                    if(killAppdb.select(pkginfo.getPkgname(),0).size() == 0){
                                        killAppdb.insert(pkginfo.getPkgname(),0);
                                    }
                                }
                            }
                        }
                        Toast.makeText(killAppActivity.this, "所有进程都已终止 ", Toast.LENGTH_SHORT).show();
                        getRunning(1);
                        showPKGS(lv1);
                    }
                });
            }
        });
    }

    private void stopApp(String pkgname){
        CMD cmd = new CMD("am force-stop "+pkgname);
        if(cmd.getResultCode() == 0){
            Toast.makeText(killAppActivity.this, "已终止 "+pkgname, Toast.LENGTH_SHORT).show();
        }
    }

    private void getRunning(int ss){
        checkboxs.clear();
        pkginfos.clear();
        //这里是设置了一个阈值参数，如果等于1，就默认列出用户安装的应用，否则就列出所有应用
        if(ss == 1){
            multiFunc.queryRunningPKGS(this,pkginfos,checkboxs,0);
        }else{
            multiFunc.queryAllRunningPKGS(this,pkginfos,checkboxs,0);
        }
    }

    private void showPKGS(ListView listView){
        PKGINFOAdapter pkginfoAdapter = new PKGINFOAdapter(pkginfos, killAppActivity.this, checkboxs);
        listView.setAdapter(pkginfoAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE,0,0,"显示所有进程");
        menu.add(Menu.NONE,1,1,"显示用户进程(用户安装的应用)");
        menu.add(Menu.NONE,2,2,"退出");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId){
            case 0 :
                getRunning(2);
                showPKGS(lv1);
                break;
            case 1:
                getRunning(1);
                showPKGS(lv1);
                break;
            case 2:
                fuckActivity.getIns().killall();
                ;
        }
        return super.onOptionsItemSelected(item);
    }
}
