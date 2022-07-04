package org.fqaosp.myActivitys;

import static org.fqaosp.utils.multiFunc.checkBoxsHashMap;
import static org.fqaosp.utils.multiFunc.preventDismissDialog;
import static org.fqaosp.utils.multiFunc.sendHandlerMSG;
import static org.fqaosp.utils.multiFunc.showMyDialog;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.fqaosp.R;
import org.fqaosp.adapter.PKGINFOAdapter;
import org.fqaosp.adapter.USERAdapter;
import org.fqaosp.entity.PKGINFO;
import org.fqaosp.entity.workProfileDBEntity;
import org.fqaosp.sql.workProfileDB;
import org.fqaosp.utils.CMD;
import org.fqaosp.utils.fuckActivity;
import org.fqaosp.utils.makeWP;
import org.fqaosp.utils.multiFunc;

import java.util.ArrayList;
import java.util.HashMap;

public class workProfileManageActivity extends AppCompatActivity {

    private ArrayList<PKGINFO> pkginfos = new ArrayList<>();
    private HashMap<String,PKGINFO> pkginfoHashMap =new HashMap<>();
    private ArrayList<String> list = new ArrayList<>();
    private ArrayList<Boolean> checkboxs = new ArrayList<>();
    private ArrayList<Boolean> checkboxsByUser = new ArrayList<>();
    private Button b1,b2,b3 ;
    private ListView lv1,lv2;
    private EditText et1;

    private workProfileDB workProfiledb = new workProfileDB(workProfileManageActivity.this, "workProfile", null, 1);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.work_profile_manage_activity);
        fuckActivity.getIns().add(this);
        setTitle("分身管理");
        b1 = findViewById(R.id.wpmanageab1);
        b2 = findViewById(R.id.wpmanageab2);
        b3 = findViewById(R.id.wpmanageab3);
        lv1 =findViewById(R.id.wpmanagealv1);
        lv2 =findViewById(R.id.wpmanagealv2);
        et1 = findViewById(R.id.wpmanageaet1);


        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog show = showMyDialog(workProfileManageActivity.this,"提示","正在安装应用,请稍后(可能会出现无响应，请耐心等待)....");
                preventDismissDialog(show);
                Handler handler = new Handler(){
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        if(msg.what==0){
                            getPKGByUID(1);
                            multiFunc.dismissDialog(show);
                        }
                    }
                };
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        makeWP wp = new makeWP();
                        for (int i = 0; i < checkboxs.size(); i++) {
                            if(checkboxs.get(i)){
                                PKGINFO pkginfo = pkginfos.get(i);
                                String pkgname = pkginfo.getPkgname();
                                for (int i1 = 0; i1 < checkboxsByUser.size(); i1++) {
                                    if(checkboxsByUser.get(i1)){
                                        String s = list.get(i1);
                                        CMD cmd = new CMD(wp.getInstallPkgCMD(s, pkgname));
                                        //判断该用户下有没有安装该应用,如果有就跳过
                                        ArrayList<workProfileDBEntity> select = workProfiledb.select(pkgname, Integer.valueOf(s));
                                        if(select.size() == 0 ){
                                            workProfiledb.insert(pkgname,Integer.valueOf(s));
                                        }else{
                                            Log.d("wpma insert error ::: ",pkgname + " --  uid ::: " + s + " -- " +select.size() + " -- cmd :::  " + cmd.getResultCode() + " -- " + cmd.getResult());
                                        }
                                    }
                                }
                            }

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
                AlertDialog show =showMyDialog(workProfileManageActivity.this,"提示","正在删除应用,请稍后(可能会出现无响应，请耐心等待)....");
                preventDismissDialog(show);
                Handler handler = new Handler(){
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        if(msg.what==0){
                            getPKGByUID(1);
                            multiFunc.dismissDialog(show);
                        }
                    }
                };
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        makeWP wp = new makeWP();
                        for (int i = 0; i < checkboxs.size(); i++) {
                            if(checkboxs.get(i)){
                                PKGINFO pkginfo = pkginfos.get(i);
                                String pkgname = pkginfo.getPkgname();
                                for (int i1 = 0; i1 < checkboxsByUser.size(); i1++) {
                                    if(checkboxsByUser.get(i1)){
                                        String s = list.get(i1);
                                        CMD cmd = new CMD(wp.getUninstallPkgByUIDCMD(s,pkgname));
                                        //判断该用户下有没有安装该应用,如果有就跳过
                                        ArrayList<workProfileDBEntity> select = workProfiledb.select(pkgname, Integer.valueOf(s));
                                        if(select.size() > 0 ){
                                            workProfiledb.delete(pkgname,Integer.valueOf(s));
                                        }else{
                                            Log.d("wpma delete error ::: ",pkgname + " --  uid ::: " + s + " -- " +select.size() + " -- cmd :::  " + cmd.getResultCode() + " -- " + cmd.getResult());
                                        }
                                    }
                                }
                            }
                        }
                        Message msg = new Message();
                        msg.what=0;
                        handler.sendMessage(msg);
                    }
                });
            }
        });

        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchStr = et1.getText().toString();
                pkginfos = multiFunc.indexOfPKGS(workProfileManageActivity.this,searchStr,pkginfos,checkboxs,0);
                showPKGS(lv2);
            }
        });

        lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String uid = list.get(i);
                Intent intent = new Intent(workProfileManageActivity.this, appopsActivity.class);
                intent.putExtra("uid",uid);
                startActivity(intent);
            }
        });

        getUsers();
        showUsers(lv1);

    }


    //获取用户空间里安装的应用
    private void getPKGByUID(Integer state){
        pkginfos.clear();
        checkboxs.clear();
        pkginfoHashMap.clear();
        makeWP wp = new makeWP();
        if(list.size() == 0){
            getUsers();
        }
        AlertDialog show =showMyDialog(workProfileManageActivity.this,"提示","正在检索用户下安装的应用,请稍后(可能会出现无响应，请耐心等待)....");
        preventDismissDialog(show);
        Handler handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                if(msg.what==0){
                    showPKGS(lv2);
                    multiFunc.dismissDialog(show);
                }
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (String uid : list) {
                    String cmdstr = wp.getPkgByUIDCMD(uid);
                    if(state == 1){
                        cmdstr = wp.getUserPkgByUIDCMD(uid);
                    }
                    CMD cmd = new CMD(cmdstr);
                    String result = cmd.getResult();
                    String[] split = result.split("\n");
                    if (null != split) {
                        for (String s : cmd.getResult().split("\n")) {
                            PackageManager pm = getPackageManager();
                            PackageInfo packageInfo = null;
                            try {
                                packageInfo = pm.getPackageInfo(s, 0);
                                checkBoxsHashMap(pkginfoHashMap, checkboxs, packageInfo, pm);
                            } catch (PackageManager.NameNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                pkginfos.clear();
                checkboxs.clear();
                pkginfos.addAll(pkginfoHashMap.values());
                for (PKGINFO pkginfo : pkginfos) {
                    checkboxs.add(false);
                }
               sendHandlerMSG(handler,0);

            }
        }).start();
    }


    private void clearUser(){
        checkboxs.clear();
        list.clear();
        pkginfos.clear();
        checkboxsByUser.clear();
    }

    private  void getUsers(){
        clearUser();
        //查询用户
        multiFunc.queryUSERS(this,list,checkboxsByUser);
    }

    private void showUsers(ListView listView){
        USERAdapter userAdapter = new USERAdapter(list, workProfileManageActivity.this, checkboxsByUser);
        listView.setAdapter(userAdapter);
    }

    private void getUserEnablePKGS(){
        multiFunc.queryUserEnablePKGS(this,pkginfos,checkboxs,0);
    }

    //获取启用的应用程序
    private void getEnablePKGS(){
        multiFunc.queryEnablePKGS(this,pkginfos,checkboxs,0);
    }

    private void getPKGS(){
        //提取所有已安装的应用列表
        multiFunc.queryPKGS(this,pkginfos,checkboxs,0);
    }

    private void getUserPKGS(){
        //提取所有已安装的应用列表
        multiFunc.queryUserPKGS(this,pkginfos,checkboxs,0);
    }

    private void showPKGS(ListView listView){
        PKGINFOAdapter pkginfoAdapter = new PKGINFOAdapter(pkginfos, workProfileManageActivity.this, checkboxs);
        listView.setAdapter(pkginfoAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE,0,0,"显示主用户所有应用");
        menu.add(Menu.NONE,1,1,"显示主用户所有应用(包括禁用)");
        menu.add(Menu.NONE,2,2,"显示主用户安装的应用");
        menu.add(Menu.NONE,3,3,"显示主用户安装的应用(包括禁用)");
        menu.add(Menu.NONE,4,4,"显示其他用户所有的应用(包括禁用)");
        menu.add(Menu.NONE,5,5,"显示其他用户安装的应用(包括禁用)");
        menu.add(Menu.NONE,6,6,"退出");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId){
            case 0:
                getEnablePKGS();
                showPKGS(lv2);
                break;
            case 1:
                getPKGS();
                showPKGS(lv2);
                break;
            case 2:
                getUserPKGS();
                showPKGS(lv2);
                break;
            case 3:
                getUserEnablePKGS();
                showPKGS(lv2);
                break;
            case 4:
                getPKGByUID(0);
                break;
            case 5:
                getPKGByUID(1);
                break;
            case 6:
                fuckActivity.getIns().killall();
                ;
        }
        return super.onOptionsItemSelected(item);
    }


}
