package org.fqaosp.myActivitys;

import static org.fqaosp.utils.fileTools.extactAssetsFile;
import static org.fqaosp.utils.fileTools.getMyHomeFilesPath;
import static org.fqaosp.utils.multiFunc.getCMD;
import static org.fqaosp.utils.multiFunc.sendHandlerMSG;
import static org.fqaosp.utils.multiFunc.showCMDInfoMSG;
import static org.fqaosp.utils.multiFunc.showInfoMsg;
import static org.fqaosp.utils.multiFunc.showMyDialog;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import org.fqaosp.entity.PKGINFO;
import org.fqaosp.sql.killAppDB;
import org.fqaosp.utils.CMD;
import org.fqaosp.utils.fuckActivity;
import org.fqaosp.utils.multiFunc;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class killAppActivity extends AppCompatActivity {

    private ArrayList<PKGINFO> pkginfos = new ArrayList<>();
    private ArrayList<Boolean> checkboxs = new ArrayList<>();
    private ListView lv1;
    private EditText kaaet1;
    private Button b1 ,kaasearchb;
    private Switch kaasb1,kaasb2,kaasb3;
    private Boolean kaasb1Bool,kaasb2Bool,kaasb3Bool;
    private killAppDB killAppdb = new killAppDB(killAppActivity.this, "killApp.db", null, 1);
    private boolean isRoot=false,isADB=false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kill_app_activity);
        fuckActivity.getIns().add(this);
        setTitle("后台管理");
        Intent intent = getIntent();
        isRoot = intent.getBooleanExtra("isRoot",false);
        isADB = intent.getBooleanExtra("isADB",false);
        if(isRoot == false && isADB == false){
            Toast.makeText(this, "没有被授权,将无法正常使用该功能", Toast.LENGTH_SHORT).show();
        }
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

                StringBuilder sb = new StringBuilder();
                sb.append("aaa=(");
                for (int i = 0; i < checkboxs.size(); i++) {
                    PKGINFO pkginfo = pkginfos.get(i);
                    if(kaasb3Bool && !checkboxs.get(i)){
                        if(!pkginfo.getPkgname().equals(getPackageName())){
                            sb.append("\""+pkginfo.getPkgname()+"\" ");
                        }
                    }

                    if(kaasb2Bool && checkboxs.get(i)){
                        if(!pkginfo.getPkgname().equals(getPackageName())){
                            sb.append("\""+pkginfo.getPkgname()+"\" ");
                        }
                    }

                    if(kaasb1Bool){
                        sb.append("\""+pkginfo.getPkgname()+"\" ");
                    }
                }

                if(kaasb1Bool==false && kaasb2Bool ==false && kaasb3Bool ==false){
                    if(killAppdb.count() == 0){
                        for (PKGINFO pkginfo : pkginfos) {
                            if(!pkginfo.getPkgname().equals(getPackageName())){
                                sb.append("\""+pkginfo.getPkgname()+"\" ");
                            }
                        }
                    }else{
                        HashMap<String, Integer> select = killAppdb.select(null, 0);
                        for (Map.Entry<String, Integer> entry : select.entrySet()) {
                            sb.append("\""+entry.getKey()+"\" ");
                        }
                    }
                }
                sb.append(");for pp in ${aaa[@]};do am force-stop $pp ;done;");
                showCMDInfoMSG(killAppActivity.this,false,sb.toString(),isRoot,"正在清理后台进程,请稍后(可能会出现无响应，请耐心等待)....","清理后台结束.");
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

    //获取在后台运行的程序
    private void getRunning(int ss){
        ProgressDialog show = showMyDialog(killAppActivity.this,"正在获取后台应用,请稍后(可能会出现无响应，请耐心等待)....");
        Handler handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                if(msg.what==0){
                    showPKGS(lv1);
                    show.dismiss();
                }
            }
        };
        Activity activity = this;
        Context context =this;
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
                checkRunningPKG(context);
                sendHandlerMSG(handler,0);
            }
        }).start();

    }

    /**
     * <p>
     * 进行字符串正则提取
     */
    public String getByString(String src, String regex, String re_str) {
        StringBuilder tmp = new StringBuilder();
        Matcher m = Pattern.compile(regex).matcher(src);
        while (m.find()) {
            tmp.append(m.group().replaceAll(re_str, "") + "\n");
        }
        return tmp.toString();
    }

    private void checkRunningPKG(Context context){
        String datab="/data/local/tmp/busybox";
        String filesPath = getMyHomeFilesPath(context);
        if(isADB){
            filesPath=context.getExternalCacheDir().toString();
        }
        String busyboxFile = filesPath+"/busybox";
        File busyF = new File(busyboxFile);
        File filesP = new File(filesPath);
        if(!filesP.exists()){
            filesP.mkdirs();
        }
        if(!busyF.exists()){
            extactAssetsFile(this,"busybox",busyboxFile);
        }
        String cmdstr="chmod 755 "+busyboxFile +" && "+busyboxFile+" ps";
        if(isADB){
            CMD cmd = getCMD(this, "cp " + busyboxFile + " "+datab+" && chmod 777 "+datab, false);
            cmdstr="chmod 777 "+datab+" && "+datab+" ps";
        }
        CMD cmd = getCMD(context,cmdstr,isRoot);
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
                                String proc_pid_status_cmd="cat /proc/"+pid+"/status |grep 'VmRSS' | "+(isADB?datab:busyboxFile)+" awk '{print $2}'";
                                CMD cmd1 = getCMD(context,proc_pid_status_cmd,isRoot);
                                if(cmd1.getResultCode() == 0){
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
        menu.add(Menu.NONE,2,2,"帮助");
        menu.add(Menu.NONE,3,3,"退出");
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
                showInfoMsg(this,"帮助信息","该页面是用于后台进程终止，需要root授权。\r\n" +
                        "1.右上角三个点，显示所有进程，会列出所有在后台运行的程序，包括系统应用进程。\r\n" +
                        "2.右上角三个点，显示用户进程，会列出所有在后台运行的程序，不包括系统应用进程，仅列出用户安装的。\r\n" +
                        "3.全选，不管有没有勾选，都会操作当前列表所有应用.\r\n" +
                        "4.勾选，仅操作勾选的应用.\r\n" +
                        "5.未勾选,仅操作勾选以外的应用.\r\n" +
                        "6.终止，点击该按钮后，就会终止后台进程,如果没有勾选任何进程，则会默认采用数据库里面的数据，数据库里面的数据，是上一次终止过的应用信息，采用am force-stop命令强行终止。\r\n" +
                        "7.搜索框支持中英文搜索，不区分大小写.\r\n"
                );
                break;
            case 3:
                fuckActivity.getIns().killall();
                ;
        }
        return super.onOptionsItemSelected(item);
    }
}
