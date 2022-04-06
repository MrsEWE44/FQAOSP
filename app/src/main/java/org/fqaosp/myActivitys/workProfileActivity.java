package org.fqaosp.myActivitys;

import android.os.Bundle;
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
import org.fqaosp.utils.fuckActivity;
import org.fqaosp.utils.makeWP;
import org.fqaosp.utils.multiFunc;

import java.util.ArrayList;

/**
 *
 * 工作资料创建部分
 * 类似炼妖壶，不过我这边没它那么麻烦，一键操作
 *
 * */

public class workProfileActivity extends AppCompatActivity {

    private ArrayList<PKGINFO> pkginfos = new ArrayList<>();
    private ArrayList<Boolean> checkboxs = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.work_profile_activity);
        fuckActivity.getIns().add(this);
        Button b1 = findViewById(R.id.wpb1);
        ListView listView1 = findViewById(R.id.wplv1);
        EditText editText1 = findViewById(R.id.wpet1);

        getPKGS();
        showPKGS(listView1);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(workProfileActivity.this, "6546", Toast.LENGTH_SHORT).show();
                for (int i = 0; i < checkboxs.size(); i++) {
                    if (checkboxs.get(i)) {
                        try {
                            Integer num = Integer.valueOf(editText1.getText().toString());
                            if(num<1024 && num > 0){
//                                Log.i("clickd  ", pkginfos.get(i).getAppname()+" -- edit text ::: "+editText1.getText().toString());
                                makeWP makewp = new makeWP();
                                if(makewp.init()){
                                    PKGINFO pkginfo = pkginfos.get(i);
                                    for(int j=0;j<num;j++){
                                        //创建工作资料空间
                                        if(makewp.createWP()){
                                            Toast.makeText(workProfileActivity.this, multiFunc.getMyUID() + " 已成功新增 : "+pkginfo.getAppname(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    //同步所有空间都安装选中的应用
                                    makewp.syncapk(workProfileActivity.this,pkginfo);
                                    editText1.setText("");
                                    Toast.makeText(workProfileActivity.this, "全部新增成功", Toast.LENGTH_LONG).show();
                                }
                            }else{
                                Toast.makeText(workProfileActivity.this, "请输入1024以内并且大于0的数值", Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){
                            Toast.makeText(workProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        
                    }
                }
            }
        });
    }


    private void getPKGS(){
        checkboxs.clear();
        pkginfos.clear();
        //提取所有已安装的应用列表
        multiFunc.queryPKGS(this,pkginfos,checkboxs,0);
    }

    private void showPKGS(ListView listView){
        PKGINFOAdapter pkginfoAdapter = new PKGINFOAdapter(pkginfos, workProfileActivity.this, checkboxs);
        listView.setAdapter(pkginfoAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add("退出");


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
//        Toast.makeText(this, "item id :::: "+itemId, Toast.LENGTH_SHORT).show();
        switch (itemId){
            case 0:
                fuckActivity.getIns().killall();

                ;
        }
        return super.onOptionsItemSelected(item);
    }

}
