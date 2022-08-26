package org.fqaosp;

import static org.fqaosp.utils.multiFunc.jump;
import static org.fqaosp.utils.multiFunc.showInfoMsg;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.fqaosp.myActivitys.apkDecompileMenuActivity;
import org.fqaosp.myActivitys.appDisableActivity;
import org.fqaosp.myActivitys.appopsActivity;
import org.fqaosp.myActivitys.backupRestoreActivity;
import org.fqaosp.myActivitys.fileSearchActivity;
import org.fqaosp.myActivitys.imgToolMenuActivity;
import org.fqaosp.myActivitys.importToolsActivity;
import org.fqaosp.myActivitys.killAppActivity;
import org.fqaosp.myActivitys.mountLocalImageActivity;
import org.fqaosp.myActivitys.sqliteManageActivity;
import org.fqaosp.myActivitys.workProfileMenuActivity;
import org.fqaosp.utils.fuckActivity;
import org.fqaosp.utils.permissionRequest;

/**
 * 首页界面内容...
 *
 * */

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fuckActivity.getIns().add(this);
        permissionRequest.requestExternalStoragePermission(this);
        getExternalCacheDir().mkdirs();
        initBut();
    }

    private  void initBut(){
        Button b1 = findViewById(R.id.mb1);
        Button b3 = findViewById(R.id.mb3);
        Button b4 = findViewById(R.id.mb4);
        Button b5 = findViewById(R.id.mb5);
        Button mapkdisable = findViewById(R.id.mapkdisable);
        Button mappops = findViewById(R.id.mappops);
        Button imgtool = findViewById(R.id.imgtool);
        Button sqliteb = findViewById(R.id.sqliteb);
        Button brbutton = findViewById(R.id.brbutton);
        Button mfilesearch = findViewById(R.id.mfilesearch);

        jump(b1,this,workProfileMenuActivity.class);
        jump(b3,this, killAppActivity.class);
        jump(b4,this, mountLocalImageActivity.class);
        jump(b5,this, apkDecompileMenuActivity.class);
        jump(mapkdisable,this, appDisableActivity.class);
        jump(mappops,this, appopsActivity.class);
        jump(imgtool,this, imgToolMenuActivity.class);
        jump(sqliteb,this, sqliteManageActivity.class);
        jump(sqliteb,this, sqliteManageActivity.class);
        jump(brbutton,this, backupRestoreActivity.class);
        jump(mfilesearch,this, fileSearchActivity.class);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE,0,0,"进入工具包组件检查页面");
        menu.add(Menu.NONE,1,1,"帮助");
        menu.add(Menu.NONE,2,2,"退出");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId){
            case 0:
                jump(MainActivity.this, importToolsActivity.class);
                break;
            case 1:
                showInfoMsg(this,"帮助信息","FQAOSP是一个适用于类原生的搞机工具，同样也适用于国内定制ui系统，它拥有很多搞机必备的功能与工具，需要root支持。\r\n" +
                        "如果有新功能或建议，可以在GitHub提issue！\r\n");
                break;
            case 2:
                fuckActivity.getIns().killall();
                ;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}