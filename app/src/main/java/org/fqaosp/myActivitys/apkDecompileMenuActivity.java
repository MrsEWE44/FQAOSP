package org.fqaosp.myActivitys;

import static org.fqaosp.utils.fileTools.extactAssetsFile;
import static org.fqaosp.utils.fileTools.getMyHomeFilesPath;
import static org.fqaosp.utils.multiFunc.jump;

import android.os.Bundle;
import android.os.Environment;
import android.os.Process;
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

public class apkDecompileMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.apk_decompile_menu_activity);
        fuckActivity.getIns().add(this);
        setTitle("apktool菜单");
        Button b1 = findViewById(R.id.admab1);
        Button b2 = findViewById(R.id.admab2);
        jump(b1,this,apkDecompileActivity.class);
        jump(b2,this,apkRecompileActivity.class);
        extractAssetsFiles();
    }


//    private  Boolean checkjdkfile(File storageHomeJDKF){
//        if (!storageHomeJDKF.exists()){
//            AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(apkDecompileMenuActivity.this);
//            alertDialog2.setTitle("提示");
//            alertDialog2.setMessage("请下载该连接jdk文件 https://github.com/MrsEWE44/FQAOSP/releases/tag/V1.0-test-1 并把jdk.tar.xz放置在 "+storageHomeJDKF.toString());
//            alertDialog2.setNegativeButton("已下载", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    dialogInterface.dismiss();
//                    if(storageHomeJDKF.exists()){
//                        Toast.makeText(apkDecompileMenuActivity.this, "请重新进入app", Toast.LENGTH_LONG).show();
//                        fuckActivity.getIns().killall();
//                    }else{
//                        checkjdkfile(storageHomeJDKF);
//                    }
//                }
//            });
//            alertDialog2.show();
//        }else{
//            return true;
//        }
//        return false;
//    }

    private  void extractAssetsFiles()  {
        try {
            String filesDir = getMyHomeFilesPath(apkDecompileMenuActivity.this);
            String busyboxFile = filesDir+"/busybox";
            String jdkFile = filesDir+"/jdk.tar.xz";
            String jdkDir = filesDir+"/jdk";
            String storage = Environment.getExternalStorageDirectory().toString();
            String storageHome = storage+"/Download";
            String storageHomeJDKFile = storageHome+"/jdk.tar.xz";
            String makeScriptFile = filesDir+"/make.sh";
            String deScriptFile = filesDir+"/de.sh";
            String reScriptFile = filesDir+"/re.sh";
            String apktoolFile = filesDir+ "/apktool.jar";
            File file1 = new File(filesDir);
            File bboxF = new File(busyboxFile);
            File jdkF = new File(jdkFile);
            File jdkD = new File(jdkDir);
            File storageHomeJDKF = new File(storageHomeJDKFile);
            File storageHomeDir = new File(storageHome);
            File deScriptF = new File(deScriptFile);
            File reScriptF = new File(reScriptFile);
            File makeScriptF = new File(makeScriptFile);
            File apkToolF = new File(apktoolFile);
            if(!storageHomeDir.exists()){
               storageHomeDir.mkdirs();
            }
            if(!file1.exists()){
               file1.mkdirs();
            }

            if (!jdkD.exists() && !jdkF.exists() ){
//                if(checkjdkfile(storageHomeJDKF)){
//                    if(copyFile(storageHomeJDKFile,jdkFile)){
//                        Toast.makeText(this, "发现jdk!", Toast.LENGTH_SHORT).show();
//                        extractAssetsFiles();
//                    }
//                }
                Toast.makeText(this, "未找到 jdk.tar.xz ，请重新导入工具包后再执行此项", Toast.LENGTH_LONG).show();
                jump(apkDecompileMenuActivity.this,importToolsActivity.class);
            }else{
                if(!bboxF.exists() ){
                    extactAssetsFile(this,"busybox",busyboxFile);
                }
                if(jdkD.exists() && jdkF.exists()){
                    jdkF.delete();
                }
                if(!makeScriptF.exists()){
                    Toast.makeText(this, "未找到make.sh，请重新导入工具包后再执行此项", Toast.LENGTH_LONG).show();
                    jump(apkDecompileMenuActivity.this,importToolsActivity.class);
                }
                if(!apkToolF.exists()){
                    Toast.makeText(this, "未找到apktool.jar，请重新导入工具包后再执行此项", Toast.LENGTH_LONG).show();
                    jump(apkDecompileMenuActivity.this,importToolsActivity.class);
                }
                if(!deScriptF.exists()){
                    Toast.makeText(this, "未找到de.sh，请重新导入工具包后再执行此项", Toast.LENGTH_LONG).show();
                    jump(apkDecompileMenuActivity.this,importToolsActivity.class);
                }
                if(!reScriptF.exists()){
                    Toast.makeText(this, "未找到re.sh，请重新导入工具包后再执行此项", Toast.LENGTH_LONG).show();
                    jump(apkDecompileMenuActivity.this,importToolsActivity.class);
                }
                Integer myuid = Process.myUid();
                String cmd = "cd " + filesDir + " && sh make.sh && cd ../ && chown -R "+myuid+":"+myuid+" files/";

                alertDialogThread dialogThread = new alertDialogThread(apkDecompileMenuActivity.this, "请稍后，正在解压apktool相关资源文件", cmd, "提示", "解压成功", "解压失败");
                dialogThread.start();
            }


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
