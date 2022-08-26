package org.fqaosp.myActivitys;

import static org.fqaosp.utils.multiFunc.dismissDialogHandler;
import static org.fqaosp.utils.multiFunc.preventDismissDialog;
import static org.fqaosp.utils.multiFunc.queryUSERS;
import static org.fqaosp.utils.multiFunc.sendHandlerMSG;
import static org.fqaosp.utils.multiFunc.showInfoMsg;
import static org.fqaosp.utils.multiFunc.showMyDialog;

import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.fqaosp.R;
import org.fqaosp.adapter.PKGINFOAdapter;
import org.fqaosp.entity.PKGINFO;
import org.fqaosp.entity.workProfileDBEntity;
import org.fqaosp.sql.workProfileDB;
import org.fqaosp.utils.CMD;
import org.fqaosp.utils.fuckActivity;
import org.fqaosp.utils.makeWP;
import org.fqaosp.utils.multiFunc;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * 工作资料创建部分
 * 类似炼妖壶，不过我这边没它那么麻烦，一键操作
 *
 * */

public class workProfileActivity extends AppCompatActivity {

    private ArrayList<PKGINFO> pkginfos = new ArrayList<>();
    private ArrayList<Boolean> checkboxs = new ArrayList<>();
    private ArrayList<String> users = new ArrayList<>();

    private ListView listView1;

    private workProfileDB workProfiledb = new workProfileDB(workProfileActivity.this, "workProfile", null, 1);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.work_profile_activity);
        fuckActivity.getIns().add(this);
        setTitle("应用分身");
        Button b1 = findViewById(R.id.wpb1);
        Button b2 = findViewById(R.id.wpb2);
        listView1 = findViewById(R.id.wplv1);
        EditText editText1 = findViewById(R.id.wpet1);
        EditText editText2 = findViewById(R.id.wpet2);
        showDialogWaring();
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog show = showMyDialog(workProfileActivity.this,"提示","正在创建分身空间,请稍后(可能会出现无响应，请耐心等待)....");
                Handler handler = dismissDialogHandler(0,show);
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        makeWP makewp = new makeWP();
                        Integer num = Integer.valueOf(editText1.getText().toString());
                        checkUser(makewp);
                        if(num<makewp.getInitsize() && num > 0){
                            if(makewp.init()){
                                for(int j=0;j<num;j++){
                                    //创建工作资料空间
                                    if(makewp.createWP()){
                                        Log.d("workP "," is create ok !!!");
                                    }
                                }
                            }
                            try {
                                queryUSERS(workProfileActivity.this,users);
                                for (int i = 0; i < checkboxs.size(); i++) {
                                    if (checkboxs.get(i)) {
                                        PKGINFO pkginfo = pkginfos.get(i);
                                        //同步所有空间都安装选中的应用
//                                                makewp.syncapk(workProfileActivity.this,pkginfo);
                                        for (String userid : users) {
                                            makewp.startWP(userid);
                                            String pkgname = pkginfo.getPkgname();
                                            //从数据库里查询，如果不存在该用户以及相关包名，则允许安装与插入数据库
                                            ArrayList<workProfileDBEntity> select = workProfiledb.select(pkgname, Integer.valueOf(userid));
                                            if(select.size() == 0){
                                                CMD cmd = new CMD(makewp.getInstallPkgCMD(userid,pkgname));
                                                if(cmd.getResultCode() == 0){
                                                    workProfiledb.insert(pkgname,Integer.valueOf(userid));
                                                }else{
                                                    Log.d("error wp cmd ::: ",cmd.getResultCode() + " -- " + cmd.getResult());
                                                }
                                            }
                                        }
                                    }
                                }
                                checkUser(makewp);
                                editText1.setText("");
                                Toast.makeText(workProfileActivity.this, "全部新增成功", Toast.LENGTH_LONG).show();
                                sendHandlerMSG(handler,0);
                            }catch (Exception e){
                                Toast.makeText(workProfileActivity.this, "wpa :: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }else{
                            Toast.makeText(workProfileActivity.this, "请输入 "+makewp.getInitsize()+" 以内并且大于0的数值", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchStr = editText2.getText().toString();
                pkginfos = multiFunc.indexOfPKGS(workProfileActivity.this,searchStr,pkginfos,checkboxs,0);
                showPKGS(listView1);
            }
        });

        listView1.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                PKGINFO pkginfo = pkginfos.get(i);
                ClipboardManager cpm = (ClipboardManager) workProfileActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                cpm.setText(pkginfo.toString());
                Toast.makeText(workProfileActivity.this, "已复制", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    private void checkUser(makeWP makewp){
        queryUSERS(workProfileActivity.this,users);
        for (String userid : users) {
            ArrayList<workProfileDBEntity> treeMap = workProfiledb.select(null, Integer.valueOf(userid));
            //如果没有找到该用户uid，则添加进数据库
            if(treeMap.size() == 0){
                //获取该用户下所有安装的包名
                makewp.addCMDResult(makewp.getUserPkgByUIDCMD(userid),workProfileActivity.this,pkginfos,checkboxs);
            }
            for (PKGINFO pkginfo : pkginfos) {
                ArrayList<workProfileDBEntity> select = workProfiledb.select(pkginfo.getPkgname(), Integer.valueOf(userid));
                if(select.size() == 0){
                    workProfiledb.insert(pkginfo.getPkgname(),Integer.valueOf(userid));
                }
            }
        }
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
        PKGINFOAdapter pkginfoAdapter = new PKGINFOAdapter(pkginfos, workProfileActivity.this, checkboxs);
        listView.setAdapter(pkginfoAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE,0,0,"显示所有应用");
        menu.add(Menu.NONE,1,1,"显示所有应用(包括禁用)");
        menu.add(Menu.NONE,2,2,"显示用户安装的应用");
        menu.add(Menu.NONE,3,3,"显示用户安装的应用(包括禁用)");
        menu.add(Menu.NONE,4,4,"帮助");
        menu.add(Menu.NONE,5,5,"退出");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId){
            case 0:
                getEnablePKGS();
                showPKGS(listView1);
                break;
            case 1:
                getPKGS();
                showPKGS(listView1);
                break;
            case 2:
                getUserPKGS();
                showPKGS(listView1);
                break;
            case 3:
                getUserEnablePKGS();
                showPKGS(listView1);
                break;
            case 4:
                showInfoMsg(this,"帮助信息","该页面是用于应用分身的，需要root授权。\r\n" +
                        "1.右上角三个点，显示安装应用，会列出当前用户已经安装的应用。\r\n" +
                        "2.开启数量，由用户自己输入，但不能超过默认的最高1024个，不然会出错。\r\n" +
                        "3.搜索框支持中英文搜索，不区分大小写.\r\n"
                );
                break;
            case 5:
                fuckActivity.getIns().killall();
                ;
        }
        return super.onOptionsItemSelected(item);
    }

}
