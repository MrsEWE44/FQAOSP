package org.fqaosp;

import static org.fqaosp.utils.multiFunc.jump;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.fqaosp.databinding.ApkDecompileMenuActivityBinding;
import org.fqaosp.databinding.AppDisableActivityBinding;
import org.fqaosp.databinding.AppopsActivityBinding;
import org.fqaosp.myActivitys.apkDecompileMenuActivity;
import org.fqaosp.myActivitys.apkExtractActivity;
import org.fqaosp.myActivitys.appDisableActivity;
import org.fqaosp.myActivitys.appopsActivity;
import org.fqaosp.myActivitys.killAppActivity;
import org.fqaosp.myActivitys.mountLocalImageActivity;
import org.fqaosp.myActivitys.workProfileMenuActivity;
import org.fqaosp.utils.fuckActivity;

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
        String p[] = {Manifest.permission.CHANGE_COMPONENT_ENABLED_STATE,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.MANAGE_EXTERNAL_STORAGE,Manifest.permission.REQUEST_COMPANION_PROFILE_WATCH};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(p,0);
        }
        Button b1 = findViewById(R.id.mb1);
        Button b2 = findViewById(R.id.mb2);
        Button b3 = findViewById(R.id.mb3);
        Button b4 = findViewById(R.id.mb4);
        Button b5 = findViewById(R.id.mb5);
        Button mapkdisable = findViewById(R.id.mapkdisable);
        Button mappops = findViewById(R.id.mappops);

        jump(b1,this,workProfileMenuActivity.class);
        jump(b2,this,apkExtractActivity.class);
        jump(b3,this, killAppActivity.class);
        jump(b4,this, mountLocalImageActivity.class);
        jump(b5,this, apkDecompileMenuActivity.class);
        jump(mapkdisable,this, appDisableActivity.class);
        jump(mappops,this, appopsActivity.class);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}