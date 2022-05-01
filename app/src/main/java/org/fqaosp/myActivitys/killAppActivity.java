package org.fqaosp.myActivitys;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
    private EditText kaaet1;
    private Button b1 ,kaasearchb;
    private Switch kaasb1,kaasb2,kaasb3;
    private Boolean kaasb1Bool,kaasb2Bool,kaasb3Bool;
    private killAppDB killAppdb = new killAppDB(killAppActivity.this, "killApp.db", null, 1);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kill_app_activity);
        fuckActivity.getIns().add(this);
        setTitle("后台管理");
        initBt();
        getRunning(1);
        showPKGS(lv1);
    }

    private void initBt(){
        b1 = findViewById(R.id.kaab1);
        kaasearchb = findViewById(R.id.kaasearchb);
        kaaet1 = findViewById(R.id.kaaet1);
        kaasb1 =findViewById(R.id.kaasb1);
        kaasb2 =findViewById(R.id.kaasb2);
        kaasb3 =findViewById(R.id.kaasb3);
        lv1 = findViewById(R.id.kaalv1);
        kaasb1Bool=false;
        kaasb2Bool=false;
        kaasb3Bool=false;
        kaasb1.setChecked(kaasb1Bool);
        kaasb2.setChecked(kaasb2Bool);
        kaasb3.setChecked(kaasb3Bool);
        btClick();
    }

    private void btClick(){

        kaasb1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                kaasb1Bool=b;
                kaasb2Bool=false;
                kaasb3Bool=false;
                kaasb1.setChecked(kaasb1Bool);
                kaasb2.setChecked(kaasb2Bool);
                kaasb3.setChecked(kaasb3Bool);
            }
        });

        kaasb2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                kaasb1Bool=false;
                kaasb2Bool=b;
                kaasb3Bool=false;
                kaasb1.setChecked(kaasb1Bool);
                kaasb2.setChecked(kaasb2Bool);
                kaasb3.setChecked(kaasb3Bool);
            }
        });

        kaasb3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                kaasb1Bool=false;
                kaasb2Bool=false;
                kaasb3Bool=b;
                kaasb1.setChecked(kaasb1Bool);
                kaasb2.setChecked(kaasb2Bool);
                kaasb3.setChecked(kaasb3Bool);
            }
        });


        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.post(new Runnable() {
                    @Override
                    public void run() {

                        if(kaasb3Bool){
                            for (int i = 0; i < checkboxs.size(); i++) {
                                if(!checkboxs.get(i)){
                                    PKGINFO pkginfo = pkginfos.get(i);
                                    if(!pkginfo.getPkgname().equals(getPackageName())){
                                        stopApp(pkginfo.getPkgname());
                                    }
                                }
                            }
                        }

                        if(kaasb2Bool){
                            for (int i = 0; i < checkboxs.size(); i++) {
                                if(checkboxs.get(i)){
                                    PKGINFO pkginfo = pkginfos.get(i);
                                    if(!pkginfo.getPkgname().equals(getPackageName())){
                                        stopApp(pkginfo.getPkgname());
                                    }
                                }
                            }
                        }

                        if(kaasb1Bool){
                            for (PKGINFO pkginfo : pkginfos) {
                                if(!pkginfo.getPkgname().equals(getPackageName())){
                                    //调用命令终止后台程序
                                    stopApp(pkginfo.getPkgname());
                                }
                            }
                        }

                        if(kaasb1Bool==false && kaasb2Bool ==false && kaasb3Bool ==false){
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
                        }
                        Toast.makeText(killAppActivity.this, "所有进程都已终止 ", Toast.LENGTH_SHORT).show();
                        getRunning(1);
                        showPKGS(lv1);
                    }
                });
            }
        });

        kaasearchb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchStr = kaaet1.getText().toString();
                pkginfos = multiFunc.indexOfPKGS(killAppActivity.this,searchStr,pkginfos,checkboxs,0);
                showPKGS(lv1);
            }
        });
    }


    private void stopApp(String pkgname){
        CMD cmd = new CMD("am force-stop "+pkgname);
        if(cmd.getResultCode() == 0){
            if(killAppdb.select(pkgname,0).size() == 0){
                killAppdb.insert(pkgname,0);
            }
            Log.d("killAppActivity","已终止 "+pkgname);
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
