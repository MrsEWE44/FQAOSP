package org.fqaosp.myActivitys;

import android.content.Intent;
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
import org.fqaosp.adapter.USERAdapter;
import org.fqaosp.threads.alertDialogThread;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.work_profile_remove_activity);
        fuckActivity.getIns().add(this);

        Button b1 = findViewById(R.id.wprab1);
        Button b2 = findViewById(R.id.wprab2);
        ListView listView1 = findViewById(R.id.wpralv1);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < checkboxs.size(); i++) {
                    if (checkboxs.get(i)) {
                        //移除掉勾选的内容
                        makeWP wp = new makeWP();
                        alertDialogThread dialogThread = new alertDialogThread(workProfileRemoveActivity.this, "正在删除分身空间", wp.getRemoveWPCMD(list.get(i)), "提示", "删除成功", "删除失败");
                        dialogThread.start();
                    }
                }
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (String s : list) {
                    makeWP wp = new makeWP();
                    alertDialogThread dialogThread = new alertDialogThread(workProfileRemoveActivity.this, "正在删除分身空间", wp.getRemoveWPCMD(s), "提示", "删除成功", "删除失败");
                    dialogThread.start();
                }
            }
        });
        getUsers();
        showUsers(listView1);

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
        menu.add("退出");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId){
            case 0:
                fuckActivity.getIns().killall();
                ;
        }
        return super.onOptionsItemSelected(item);
    }

}
