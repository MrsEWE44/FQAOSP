package org.fqaosp.myActivitys;

import static org.fqaosp.utils.fileTools.copyFile;
import static org.fqaosp.utils.fileTools.execFileSelect;
import static org.fqaosp.utils.fileTools.extactAssetsFile;
import static org.fqaosp.utils.fileTools.getMyHomeFilesPath;
import static org.fqaosp.utils.fileTools.getMyStorageHomePath;
import static org.fqaosp.utils.fileTools.getSize;
import static org.fqaosp.utils.fileTools.selectFile;
import static org.fqaosp.utils.multiFunc.checkTools;
import static org.fqaosp.utils.multiFunc.dismissDialogHandler;
import static org.fqaosp.utils.multiFunc.sendHandlerMSG;
import static org.fqaosp.utils.multiFunc.showMyDialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.fqaosp.R;
import org.fqaosp.utils.CMD;
import org.fqaosp.utils.fuckActivity;
import org.fqaosp.utils.permissionRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class importToolsActivity extends Activity {

    private ArrayList<String> list = new ArrayList<>();
    private ArrayList<Boolean> checkboxs = new ArrayList<>();
    private TextView itatv1;
    private Button itab1 , itab2,itab5,itab6,itab7;
    private String fqfile="fqtools.tar.xz";
    boolean mIsCancel;
    private int mProgress;
    private long downloaded_sum;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.import_tools_activity);
        fuckActivity.getIns().add(this);
        initButton();
        permissionRequest.getExternalStorageManager(importToolsActivity.this);
    }

    private  void initButton(){
        itatv1 = findViewById(R.id.itatv1);
        itab1 = findViewById(R.id.itab1);
        itab2 = findViewById(R.id.itab2);
        itab5 = findViewById(R.id.itab5);
        itab6 = findViewById(R.id.itab6);
        itab7 = findViewById(R.id.itab7);
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
            showDownloadDialog(context,activity,0);
        });

        itab7.setOnClickListener((v)->{
            String filesPath = getMyHomeFilesPath(importToolsActivity.this);
            String s = Environment.getExternalStorageDirectory().toString();
            String makeFQTOOLSScriptFile = filesPath+"/makefqtools.sh";
            File makeFQTOOLSScriptF = new File(makeFQTOOLSScriptFile);
            if(!makeFQTOOLSScriptF.exists()){
                extactAssetsFile(this,"makefqtools.sh",makeFQTOOLSScriptFile);
            }
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

    }

    //显示在线下载提示框
    private void showDownloadDialog(Context context,Activity activity,int mode){
        String download_url = "";
        String save_path = getMyStorageHomePath(context)+"/cache/download/";
        String filename = "";
        download_url = "https://github.com/MrsEWE44/FQAOSP/releases/download/V1.2.4-ROMBUILD/fqtools.tar.xz";
        filename = fqfile;
        String fqfilepath = save_path+"/"+filename;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("下载中");
        View view = LayoutInflater.from(context).inflate(R.layout.download_process_bar, null);
        ProgressBar mProgressBar = (ProgressBar) view.findViewById(R.id.dpbpb);
        TextView dpbtv1 = view.findViewById(R.id.dpbtv1);
        TextView dpbtv2 = view.findViewById(R.id.dpbtv2);
        TextView dpbtv3 = view.findViewById(R.id.dpbtv3);
        dpbtv3.setText(download_url);
        builder.setView(view);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 隐藏当前对话框
                dialog.dismiss();
                // 设置下载状态为取消
                mIsCancel = true;
            }
        });
        AlertDialog alertDialog = builder.create();
        Handler mUpdateProgressHandler = new Handler() {

            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        // 设置进度条
                        mProgressBar.setProgress(mProgress);
                        break;
                    case 1:
                        // 隐藏当前下载对话框
                        alertDialog.dismiss();
                        itatv1.setText("文件下载完成，保存在:"+fqfilepath);
                        extractFile(fqfilepath,fqfile);
                }
            }
        };
        Handler handler2 = new Handler();
        Handler handler3 = new Handler(){
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(@NonNull Message msg) {
                downloaded_sum +=msg.what;
                dpbtv1.setText(getSize(downloaded_sum,0));
            }
        };

        alertDialog.show();
        downloadFile(download_url,save_path,filename,mUpdateProgressHandler,dpbtv2,handler2,handler3);

    }

    //开始下载并同步更新进度条
    private void downloadFile(String download_url , String dir_path , String filename,Handler handler,TextView dpbtv2,Handler handler2,Handler handler3) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String fileSavePath = dir_path+"/"+filename;
                try{
                    File dir = new File(dir_path);
                    if (!dir.exists()){
                        dir.mkdirs();
                    }
                    // 下载文件
                    HttpURLConnection conn = (HttpURLConnection) new URL(download_url).openConnection();
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    long length = conn.getContentLengthLong();
                    handler2.post(new Runnable() {
                        @Override
                        public void run() {
                            dpbtv2.setText(getSize(length,0));
                        }
                    });
                    File apkFile = new File(fileSavePath);
                    FileOutputStream fos = new FileOutputStream(apkFile);

                    int count = 0;
                    byte[] buffer = new byte[1024];
                    while (!mIsCancel) {
                        int numread = is.read(buffer);
                        count += numread;
                        // 计算进度条当前位置
                        mProgress = (int) (((float) count / length) * 100);
                        // 下载完成
                        if (numread < 0) {
                            handler.sendEmptyMessage(1);
                            break;
                        }
                        fos.write(buffer, 0, numread);
                        // 更新进度条
                        handler.sendEmptyMessage(0);
                        handler3.sendEmptyMessage(numread);
                    }
                    fos.close();
                    is.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
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
            checkTools(this);
            String cmd = "cd " + filesPath + " && sh extract.sh && cd ../ && chown -R "+myuid+":"+myuid+ " files/";
            AlertDialog show = showMyDialog(importToolsActivity.this,"提示","正在安装插件,请稍后(可能会出现无响应，请耐心等待)....");
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
