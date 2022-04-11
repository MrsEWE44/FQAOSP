package org.fqaosp.myActivitys;

import static org.fqaosp.utils.multiFunc.getMyHomeFilesPath;
import static org.fqaosp.utils.multiFunc.jump;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.fqaosp.R;
import org.fqaosp.threads.alertDialogThread;
import org.fqaosp.utils.fuckActivity;

import java.io.File;

public class imgToolMenuActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.img_tool_menu_activity);
        fuckActivity.getIns().add(this);
        setTitle("镜像工具菜单");
        Button itmab1 = findViewById(R.id.itmab1);
        Button itmab2 = findViewById(R.id.itmab2);
        jump(itmab1,this,imgToolUnpackActivity.class);
        jump(itmab2,this,imgToolRepackActivity.class);
        extractAssetsFiles();
    }

    private  void extractAssetsFiles()  {
        try {
            String filesDir = getMyHomeFilesPath(imgToolMenuActivity.this);
            String mkbootimgFile = filesDir+"/mkbootimg";
            String unpackbootimgFile = filesDir+"/unpackbootimg";
            String repackScriptFile = filesDir+"/repack.sh";
            String unpackScriptFile = filesDir+"/unpack.sh";
            File file1 = new File(filesDir);
            File mkbootimgF = new File(mkbootimgFile);
            File unpackbootimgF = new File(unpackbootimgFile);
            File repackScriptF = new File(repackScriptFile);
            File unpackScriptF = new File(unpackScriptFile);
            if(!file1.exists()){
                file1.mkdirs();
            }
            if(!mkbootimgF.exists() ){
                Toast.makeText(this, "未找到mkbootimg，请重新导入工具包后再执行此项", Toast.LENGTH_LONG).show();
                jump(imgToolMenuActivity.this,importToolsActivity.class);
            }
            if(!unpackbootimgF.exists()){
                Toast.makeText(this, "未找到unpackbootimg，请重新导入工具包后再执行此项", Toast.LENGTH_LONG).show();
                jump(imgToolMenuActivity.this,importToolsActivity.class);
            }
            if(!repackScriptF.exists()){
                Toast.makeText(this, "未找到repack.sh，请重新导入工具包后再执行此项", Toast.LENGTH_LONG).show();
                jump(imgToolMenuActivity.this,importToolsActivity.class);
            }
            if(!unpackScriptF.exists()){
                Toast.makeText(this, "未找到unpack.sh，请重新导入工具包后再执行此项", Toast.LENGTH_LONG).show();
                jump(imgToolMenuActivity.this,importToolsActivity.class);
            }
            String cmd = "cd " + filesDir + " && [ -f mkbootimg ] && [ -f unpackbootimg ] ";
            alertDialogThread dialogThread = new alertDialogThread(imgToolMenuActivity.this, "请稍后，正在解压镜像工具相关资源文件", cmd, "提示", "解压成功", "解压失败");
            dialogThread.start();
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
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
