package org.fqaosp.myActivitys;

import static org.fqaosp.utils.multiFunc.dismissDialog;
import static org.fqaosp.utils.multiFunc.preventDismissDialog;
import static org.fqaosp.utils.multiFunc.queryUSERS;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import org.fqaosp.threads.alertDialogThread;
import org.fqaosp.utils.CMD;
import org.fqaosp.utils.fuckActivity;
import org.fqaosp.utils.makeWP;
import org.fqaosp.utils.multiFunc;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 *
 * 工作资料创建部分
 * 类似炼妖壶，不过我这边没它那么麻烦，一键操作
 *
 * */

public class workProfileActivity extends AppCompatActivity {

    private ArrayList<PKGINFO> pkginfos = new ArrayList<>();
    private ArrayList<Boolean> checkboxs = new ArrayList<>();
    private ListView listView1;

    private ExecutorService executorService = Executors.newFixedThreadPool(4);
    private ExecutorService cacheThreadPool = Executors.newFixedThreadPool(4);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.work_profile_activity);
        fuckActivity.getIns().add(this);
        Button b1 = findViewById(R.id.wpb1);
        listView1 = findViewById(R.id.wplv1);
        EditText editText1 = findViewById(R.id.wpet1);
        showDialogWaring();
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(workProfileActivity.this);
                alertDialog.setTitle("提示");
                alertDialog.setMessage("正在创建分身空间,请稍后(可能会出现无响应，请耐心等待)....");
                AlertDialog show = alertDialog.show();
                preventDismissDialog(show);

                view.post(new Runnable() {
                    @Override
                    public void run() {
                        makeWP makewp = new makeWP();
                        Integer num = Integer.valueOf(editText1.getText().toString());
                        if(num<makewp.getInitsize() && num > 0){

                            if(makewp.init()){
                                for(int j=0;j<num;j++){
                                    //创建工作资料空间
                                    Runnable runnable = new Runnable() {
                                        @Override
                                        public void run() {
                                            if(makewp.createWP()){
                                                Log.d("workP "," is create ok !!!");
                                            }
                                        }
                                    };
                                    executorService.execute(runnable);
                                }

                            }
                            executorService.shutdown();
                            try {
                                while(true){
                                    if(executorService.isTerminated()){
                                        ArrayList<String> list = new ArrayList<>();
                                        queryUSERS(workProfileActivity.this,list);
                                        for (int i = 0; i < checkboxs.size(); i++) {
                                            if (checkboxs.get(i)) {
                                                PKGINFO pkginfo = pkginfos.get(i);
                                                //同步所有空间都安装选中的应用
//                                                makewp.syncapk(workProfileActivity.this,pkginfo);
                                                for (String userid : list) {
                                                    Runnable runnable = new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            makewp.startWP(userid);
                                                            String pkgname = pkginfo.getPkgname();
                                                            CMD cmd = new CMD(makewp.getInstallPkgCMD(userid,pkgname));
                                                            Log.d("install ",pkginfo.getAppname() +" --  code :: "+ cmd.getResultCode() );
                                                        }
                                                    };
                                                    cacheThreadPool.execute(runnable);
                                                }
                                            }
                                        }
                                        cacheThreadPool.shutdown();
                                        while(true){
                                            if(cacheThreadPool.isTerminated()){
                                                editText1.setText("");
                                                Toast.makeText(workProfileActivity.this, "全部新增成功", Toast.LENGTH_LONG).show();
                                                multiFunc.dismissDialog(show);
                                                break;
                                            }
                                            Thread.sleep(100);
                                        }
                                        break;
                                    }
                                    Thread.sleep(100);
                                }
                            }catch (Exception e){
                                Toast.makeText(workProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }else{
                            Toast.makeText(workProfileActivity.this, "请输入 "+makewp.getInitsize()+" 以内并且大于0的数值", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void showDialogWaring(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(workProfileActivity.this);
        alertDialog.setTitle("警告");
        alertDialog.setMessage("分身部分不能一次性开启太多，不然的话会闪退或者无响应");
        alertDialog.setNegativeButton("我已知晓", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }

    private void getPKGS(){
        checkboxs.clear();
        pkginfos.clear();
        //提取所有已安装的应用列表
        multiFunc.queryPKGS(this,pkginfos,checkboxs,0);
    }

    private void getUserPKGS(){
        checkboxs.clear();
        pkginfos.clear();
        //提取所有已安装的应用列表
        multiFunc.queryUserPKGS(this,pkginfos,checkboxs,0);
    }

    private void showPKGS(ListView listView){
        PKGINFOAdapter pkginfoAdapter = new PKGINFOAdapter(pkginfos, workProfileActivity.this, checkboxs);
        listView.setAdapter(pkginfoAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE,0,0,"显示所有应用");
        menu.add(Menu.NONE,1,1,"显示用户安装的应用");
        menu.add(Menu.NONE,2,2,"退出");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId){
            case 0:
                getPKGS();
                showPKGS(listView1);
                break;
            case 1:
                getUserPKGS();
                showPKGS(listView1);
                break;
            case 2:
                fuckActivity.getIns().killall();
                ;
        }
        return super.onOptionsItemSelected(item);
    }

}
