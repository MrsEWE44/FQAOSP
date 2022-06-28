package org.fqaosp.myActivitys;

import static org.fqaosp.utils.fileTools.extactAssetsFile;
import static org.fqaosp.utils.fileTools.getMyHomeFilesPath;
import static org.fqaosp.utils.multiFunc.preventDismissDialog;
import static org.fqaosp.utils.multiFunc.showMyDialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
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
import org.fqaosp.entity.PKGINFO;
import org.fqaosp.sql.killAppDB;
import org.fqaosp.utils.CMD;
import org.fqaosp.utils.fuckActivity;
import org.fqaosp.utils.multiFunc;

import java.io.File;
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

        lv1.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                PKGINFO pkginfo = pkginfos.get(i);
                ClipboardManager cpm = (ClipboardManager) killAppActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                cpm.setText(pkginfo.toString());
                Toast.makeText(killAppActivity.this, "已复制", Toast.LENGTH_SHORT).show();
                return false;
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

    //获取在后台运行的程序
    private void getRunning(int ss){
        AlertDialog show = showMyDialog(killAppActivity.this,"提示","正在获取后台应用,请稍后(可能会出现无响应，请耐心等待)....");
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
        Activity activity = this;
        new Thread(new Runnable() {
            @Override
            public void run() {
                checkboxs.clear();
                pkginfos.clear();
                //这里是设置了一个阈值参数，如果等于1，就默认列出用户安装的应用，否则就列出所有应用
                if(ss == 1){
                    multiFunc.queryRunningPKGS(activity,pkginfos,checkboxs,0);
                }else{
                    multiFunc.queryAllRunningPKGS(activity,pkginfos,checkboxs,0);
                }
                checkRunningPKG();
                Message msg = new Message();
                msg.what=0;
                handler.sendMessage(msg);
            }
        }).start();

    }

    private void checkRunningPKG(){
        String filesPath = getMyHomeFilesPath(killAppActivity.this);
        String busyboxFile = filesPath+"/busybox";
        File busyF = new File(busyboxFile);
        File filesP = new File(filesPath);
        if(!filesP.exists()){
            filesP.mkdirs();
        }
        if(!busyF.exists()){
            extactAssetsFile(this,"busybox",busyboxFile);
        }
        String cmdstr="chmod 755 "+busyboxFile +" && "+busyboxFile+" ps  && exit 0;";
        CMD cmd = new CMD(cmdstr);
//        Log.d("cmd",cmd.getResultCode() + " -- " +cmd.getResult());
        if(cmd.getResultCode() == 0){
            String[] split = cmd.getResult().split("\n");
            for (int i = 0; i < pkginfos.size(); i++) {
                PKGINFO pkginfo = pkginfos.get(i);
                for (String s : split) {
                    if(s.indexOf(pkginfo.getPkgname()) != -1){
                        String[] s1 = s.split(" ");
                        if(s1[s1.length-1].equals(pkginfo.getPkgname())){
                            String pid = s1[0];
                            if(pid == null || pid.isEmpty()){
                                pid=s1[1];
                            }
                            pid=pid.replaceAll("\\s+","");
                            if(!pid.isEmpty()){
                                String proc_pid_status_cmd="cat /proc/"+pid+"/status |grep 'VmRSS' | "+busyboxFile+" awk '{print $2}'  && exit 0;";
                                CMD cmd1 = new CMD(proc_pid_status_cmd);
                                if(cmd1.getResultCode() == 0){
//                                Log.d("ppp",pkginfo.getAppname()+ " -- " + cmd1.getResult());
                                    pkginfos.get(i).setFilesize((Long.parseLong(cmd1.getResult().trim())*1024));
                                }
                            }
                        }
                    }
                }
            }
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
