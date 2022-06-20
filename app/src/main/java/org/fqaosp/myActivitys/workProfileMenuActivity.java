package org.fqaosp.myActivitys;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.fqaosp.R;
import org.fqaosp.utils.fuckActivity;
import org.fqaosp.utils.multiFunc;

/**
 *
 * 分身菜单实现
 *
 * */

public class workProfileMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.work_profile_menu_activity);
        fuckActivity.getIns().add(this);
        setTitle("分身菜单");
        Button b1 = findViewById(R.id.wpmab1);
        Button b2 = findViewById(R.id.wpmab2);
        Button b3 = findViewById(R.id.wpmab3);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                multiFunc.jump(workProfileMenuActivity.this,workProfileActivity.class);
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                multiFunc.jump(workProfileMenuActivity.this,workProfileRemoveActivity.class);
            }
        });

        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                multiFunc.jump(workProfileMenuActivity.this,workProfileManageActivity.class);
            }
        });

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
