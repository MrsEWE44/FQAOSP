package org.fqaosp.myActivitys;

import static org.fqaosp.utils.multiFunc.preventDismissDialog;
import static org.fqaosp.utils.multiFunc.sendHandlerMSG;
import static org.fqaosp.utils.multiFunc.showInfoMsg;
import static org.fqaosp.utils.multiFunc.showMyDialog;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.fqaosp.R;
import org.fqaosp.adapter.USERAdapter;
import org.fqaosp.sql.workProfileDB;
import org.fqaosp.threads.alertDialogThread;
import org.fqaosp.utils.CMD;
import org.fqaosp.utils.fuckActivity;
import org.fqaosp.utils.makeWP;
import org.fqaosp.utils.multiFunc;

import java.util.ArrayList;

/**
 *
 * 分身移除部分功能
 *
 * */

public class workProfileRemoveActivity extends AppCompatActivity {

    private ArrayList<String> list = new ArrayList<>();
    private ArrayList<Boolean> checkboxs = new ArrayList<>();
    private workProfileDB workProfiledb = new workProfileDB(workProfileRemoveActivity.this, "workProfile", null, 1);
    private makeWP wp = new makeWP();
    private ListView listView1;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.work_profile_remove_activity);
        fuckActivity.getIns().add(this);
        setTitle("删除分身");
        Button b1 = findViewById(R.id.wprab1);
        Button b2 = findViewById(R.id.wprab2);
        listView1 = findViewById(R.id.wpralv1);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog show = showMyDialog(workProfileRemoveActivity.this,"提示","正在删除已经选中的分身用户,请稍后(可能会出现无响应，请耐心等待)....");
                preventDismissDialog(show);
                Handler handler = new Handler(){
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        if(msg.what==0){
                            getUsers();
                            showUsers(listView1);
                            multiFunc.dismissDialog(show);
                        }
                    }
                };
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < checkboxs.size(); i++) {
                            if (checkboxs.get(i)) {
                                //移除掉勾选的内容
                                delete(list.get(i));
                            }
                        }
                        sendHandlerMSG(handler,0);
                    }
                });
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog show = showMyDialog(workProfileRemoveActivity.this,"提示","正在删除分身用户,请稍后(可能会出现无响应，请耐心等待)....");
                preventDismissDialog(show);
                Handler handler = new Handler(){
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        if(msg.what==0){
                            getUsers();
                            showUsers(listView1);
                            multiFunc.dismissDialog(show);
                        }
                    }
                };
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        for (String s : list) {
                            delete(s);
                        }
                        sendHandlerMSG(handler,0);
                    }
                });
            }
        });
    }

    private void delete(String s){
        workProfiledb.delete(null,Integer.valueOf(s));
        CMD cmd = new CMD(wp.getRemoveWPCMD(s));
        cmd.getResultCode();
    }

    private  void getUsers(){
        checkboxs.clear();
        list.clear();
        //查询用户
        multiFunc.queryUSERS(this,list,checkboxs);
    }

    private void showUsers(ListView listView){
        USERAdapter userAdapter = new USERAdapter(list, workProfileRemoveActivity.this, checkboxs);
        listView.setAdapter(userAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE,0,0,"显示分身用户");
        menu.add(Menu.NONE,1,1,"帮助");
        menu.add(Menu.NONE,2,2,"退出");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId){
            case 0:
                getUsers();
                showUsers(listView1);
                break;
            case 1:
                showInfoMsg(this,"帮助信息","该页面是用于删除分身的，需要root授权。\r\n" +
                        "1.右上角三个点，显示分身用户，会列出除了当前主用户外的其它用户。\r\n" +
                        "2.全删了，点击后，会删除列表里所有用户。\r\n" +
                        "3.删除分身，需要勾选相应分身才能删除，不勾选就不删除。\r\n"
                );
                break;
            case 2:
                fuckActivity.getIns().killall();
                ;
        }
        return super.onOptionsItemSelected(item);
    }

}
