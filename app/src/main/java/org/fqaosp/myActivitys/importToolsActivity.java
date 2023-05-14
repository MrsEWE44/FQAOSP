package org.fqaosp.myActivitys;

import static org.fqaosp.utils.fileTools.copyFile;
import static org.fqaosp.utils.fileTools.execFileSelect;
import static org.fqaosp.utils.fileTools.extactAssetsFile;
import static org.fqaosp.utils.fileTools.getMyHomeFilesPath;
import static org.fqaosp.utils.fileTools.selectFile;
import static org.fqaosp.utils.fileTools.writeDataToPath;
import static org.fqaosp.utils.multiFunc.checkTools;
import static org.fqaosp.utils.multiFunc.dismissDialogHandler;
import static org.fqaosp.utils.multiFunc.sendHandlerMSG;
import static org.fqaosp.utils.multiFunc.showInfoMsg;
import static org.fqaosp.utils.multiFunc.showMyDialog;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Process;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import org.fqaosp.R;
import org.fqaosp.utils.CMD;
import org.fqaosp.utils.fuckActivity;
import org.fqaosp.utils.netUtils;
import org.fqaosp.utils.permissionRequest;

import java.io.File;
import java.util.ArrayList;

public class importToolsActivity extends Activity {

    private ArrayList<String> list = new ArrayList<>();
    private ArrayList<Boolean> checkboxs = new ArrayList<>();
    private TextView itatv1;
    private Button itab1 , itab2,itab5,itab6,itab7,itab8;
    private String fqfile="fqtools.tar.xz";
    private boolean isRoot=false , isADB=false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.import_tools_activity);
        fuckActivity.getIns().add(this);
        initButton();
        permissionRequest.getExternalStorageManager(importToolsActivity.this);
        Intent intent = getIntent();
        isRoot = intent.getBooleanExtra("isRoot",false);
        isADB = intent.getBooleanExtra("isADB",false);
    }

    private  void initButton(){
        itatv1 = findViewById(R.id.itatv1);
        itab1 = findViewById(R.id.itab1);
        itab2 = findViewById(R.id.itab2);
        itab5 = findViewById(R.id.itab5);
        itab6 = findViewById(R.id.itab6);
        itab7 = findViewById(R.id.itab7);
        itab8 = findViewById(R.id.itab8);
        clickButton();
    }

    private void clickButton(){

        Context context = this;
        Activity activity = this;

        itab1.setOnClickListener((v)->{
            String filesPath = getMyHomeFilesPath(importToolsActivity.this);
            String fqtoolsd = filesPath+"/fqtools";
            File file = new File(fqtoolsd);
            itatv1.setText(file.exists() ? "fqtools已经安装" : "fqtools未安装,请前往 https://github.com/MrsEWE44/FQAOSP/releases 下载最新工具包");
        });

        itab2.setOnClickListener((v)->{
            execFileSelect(importToolsActivity.this,importToolsActivity.this,"请选择 "+fqfile+" 文件");
        });


        itab5.setOnClickListener((v)->{fuckActivity.getIns().killall();});

        itab6.setOnClickListener((v)->{
            //fqtools下载点击事件
//            showDownloadDialog(context,activity,0);
            String url = "https://github.com/MrsEWE44/FQAOSP/releases/download/V1.3.0/fqtools.tar.xz";;
            String myStorageHomeCachePath = context.getExternalCacheDir().toString();
            String filepath = myStorageHomeCachePath+"/"+new File(url).getName();
            new netUtils().downloadFileOnUrlByAndorid(context,filepath,url);
            showInfoMsg(context,"提示","等待下载完成之后,就可以手动安装了!下载完成后的文件,会保存在 "+filepath);
        });

        itab7.setOnClickListener((v)->{
            String filesPath = getMyHomeFilesPath(context);
            String s = Environment.getExternalStorageDirectory().toString();
            String makeFQTOOLSScriptFile = filesPath+"/makefqtools.sh";
            File makeFQTOOLSScriptF = new File(makeFQTOOLSScriptFile);
            if(makeFQTOOLSScriptF.exists()){
                makeFQTOOLSScriptF.delete();
            }
            extactAssetsFile(this,"makefqtools.sh",makeFQTOOLSScriptFile);
            String outPath = s+"/Download/FQTOOLS";
            String fqtools = filesPath+"/makefqtools.sh";
            String outFile = outPath+"/makefqtools.sh";
            File file = new File(outPath);
            if(!file.exists()){
                file.mkdirs();
            }
            if(copyFile(fqtools,outFile)){
                File file1 = new File(outFile);
                if(file1.exists()){
                    itatv1.setText("你只需要复制下面这段命令在termux里边执行,随后等待出现\"make fqtools ok !\" 字样，然后选择本地安装即可.工具包在Downloads文件夹里面.\r\nsh storage/downloads/FQTOOLS/makefqtools.sh\r\n");
                }
            }else{
                itatv1.setText("请给予存储权限");
            }
        });

        itab8.setOnClickListener((v)->{
            String filesPath = getMyHomeFilesPath(context);
            String s = Environment.getExternalStorageDirectory().toString();
            String scriptName="startADBServiceByFQAOSP.sh";
            String makeFQTOOLSScriptFile = filesPath+"/"+scriptName;
            File makeFQTOOLSScriptF = new File(makeFQTOOLSScriptFile);
            makeFQTOOLSScriptF.delete();
            if(!makeFQTOOLSScriptF.exists()){
                String cmdstr ="killall FQAOSPADB\n" +
                        "exec app_process -Djava.class.path=\""+context.getApplicationInfo().sourceDir+"\" /system/bin --nice-name=FQAOSPADB org.fqaosp.service.startADBService >>/dev/null 2>&1 &\n" +
                        "echo \"run fqtools ok\"";
                writeDataToPath(cmdstr,makeFQTOOLSScriptFile,false);
            }
            String outPath = s+"/Download/FQTOOLS";
            String fqtools = filesPath+"/"+scriptName;
            String outFile = outPath+"/"+scriptName;
            File file = new File(outPath);
            if(!file.exists()){
                file.mkdirs();
            }
            if(copyFile(fqtools,outFile)){
                File file1 = new File(outFile);
                if(file1.exists()){
                    itatv1.setText("你只需要复制下面这段命令在adb shell里边执行,随后等待出现\"run fqtools ok !\" 字样即可.\r\nsh "+outFile+"\r\n");
                }
            }else{
                itatv1.setText("请给予存储权限");
            }
        });

    }

    private void clearList(){
        list.clear();
        checkboxs.clear();
    }

    private void extractFile(String s, String fff){
        String myuid = Process.myUid()+"";
        String filesPath = getMyHomeFilesPath(importToolsActivity.this);
        new File(filesPath).mkdirs();
        String outName = filesPath+"/"+fff;
        if(copyFile(s,outName)){
            checkTools(this,isADB);
            String cmd = "cd " + filesPath + " && sh extract.sh && cd ../ && chown -R "+myuid+":"+myuid+ " files/";
            ProgressDialog show = showMyDialog(importToolsActivity.this,"正在安装插件,请稍后(可能会出现无响应，请耐心等待)....");
            Handler handler = dismissDialogHandler(0,show);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    CMD cmd1 = new CMD(cmd,false);
                    Log.d("importTools",cmd1.getResultCode() +" -- " + cmd1.getResult());
                    sendHandlerMSG(handler,0);
                }
            }).start();
        }else{
            Log.e("importToolError",s+" -- "+outName);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            clearList();
            String storage = Environment.getExternalStorageDirectory().toString();
            if(data.getClipData() != null) {//有选择多个文件
                int count = data.getClipData().getItemCount();
                for(int i =0;i<count;i++){
                    Uri uri = data.getClipData().getItemAt(i).getUri();
                    selectFile(importToolsActivity.this,storage,uri,list,checkboxs,"请选择正确的 "+fqfile+" 文件","xz");
                }
            } else if(data.getData() != null) {//只有一个文件咯
                Uri uri = data.getData();
                selectFile(importToolsActivity.this,storage,uri,list,checkboxs,"请选择正确的 "+fqfile+" 文件","xz");
            }
            for (String s : list) {
                if(s.indexOf(fqfile) != -1){
                    extractFile(s,fqfile);
                }
            }

        }
    }

}
