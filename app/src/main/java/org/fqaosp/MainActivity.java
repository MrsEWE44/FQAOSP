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
import org.fqaosp.myActivitys.apkDecompileMenuActivity;
import org.fqaosp.myActivitys.apkExtractActivity;
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
        Button b1 = findViewById(R.id.mb1);
        Button b2 = findViewById(R.id.mb2);
        Button b3 = findViewById(R.id.mb3);
        Button b4 = findViewById(R.id.mb4);
        Button b5 = findViewById(R.id.mb5);

        jump(b1,this,workProfileMenuActivity.class);
        jump(b2,this,apkExtractActivity.class);
        jump(b3,this, killAppActivity.class);
        jump(b4,this, mountLocalImageActivity.class);
        jump(b5,this, apkDecompileMenuActivity.class);

    }


//    private void jump(Button b , Context srcA , Class<?> cls){
//        b.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(srcA, cls);
//                startActivity(intent);
//            }
//        });
//    }

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