package org.fqaosp.myActivitys;

import static org.fqaosp.utils.fileTools.execFileSelect;
import static org.fqaosp.utils.fileTools.getMyHomeFilesPath;
import static org.fqaosp.utils.fileTools.getMyStorageHomePath;
import static org.fqaosp.utils.fileTools.selectFile;
import static org.fqaosp.utils.multiFunc.checkTools;
import static org.fqaosp.utils.multiFunc.dismissDialogHandler;
import static org.fqaosp.utils.multiFunc.jump;
import static org.fqaosp.utils.multiFunc.queryUserPKGS;
import static org.fqaosp.utils.multiFunc.sendHandlerMSG;
import static org.fqaosp.utils.multiFunc.showInfoMsg;
import static org.fqaosp.utils.multiFunc.showMyDialog;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
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
import androidx.viewpager.widget.ViewPager;

import org.fqaosp.R;
import org.fqaosp.adapter.FILESHARINGVIEWPAGERAdapter;
import org.fqaosp.adapter.PKGINFOAdapter;
import org.fqaosp.adapter.USERAdapter;
import org.fqaosp.entity.PKGINFO;
import org.fqaosp.utils.CMD;
import org.fqaosp.utils.fuckActivity;
import org.fqaosp.utils.multiFunc;
import org.fqaosp.utils.permissionRequest;

import java.io.File;
import java.util.ArrayList;

public class apkDecompileMenuActivity extends AppCompatActivity implements View.OnClickListener{

    private ViewPager admavp;
    private ArrayList<View> views = new ArrayList<>();
    private ArrayList<String> slist = new ArrayList<>();
    private Integer viewPageIndex = 0;
    private View deView, reView;
    private ListView delv, relv;
    private EditText adaet1;
    private ArrayList<String> list = new ArrayList<>();
    private ArrayList<Boolean> checkboxs = new ArrayList<>();
    private ArrayList<PKGINFO> pkginfos = new ArrayList<>();
    private boolean isRoot,isADB;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.apk_decompile_menu_activity);
        fuckActivity.getIns().add(this);
        setTitle("软件反编译");
        Intent intent = getIntent();
        isRoot = intent.getBooleanExtra("isRoot",false);
        isADB = intent.getBooleanExtra("isADB",false);
        if(Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT){
            showInfoMsg(this,"警告","安卓4.x设备暂不支持该功能");
        }else{
            extractAssetsFiles();
            initViews();
        }

    }

    private void extractAssetsFiles() {
        try {
            checkTools(this,isADB);
            String filesDir = getMyHomeFilesPath(apkDecompileMenuActivity.this);
            String jdkDir = filesDir + "/usr/opt/openjdk";
            String apktoolFile = filesDir + "/usr/apktool.jar";
            File file1 = new File(filesDir);
            File jdkD = new File(jdkDir);
            File apkToolF = new File(apktoolFile);
            if (!file1.exists()) {
                file1.mkdirs();
            }
            if (!jdkD.exists() ) {
                showInfoMsg(this,"错误","未找到jdk，请重新导入工具包后再执行此项");
                jump(apkDecompileMenuActivity.this, importToolsActivity.class,isRoot,isADB);
            }
            if (!apkToolF.exists()) {
                showInfoMsg(this,"错误","未找到apktool.jar，请重新导入工具包后再执行此项");
                jump(apkDecompileMenuActivity.this, importToolsActivity.class,isRoot,isADB);
            }
        } catch (Exception e) {
            showInfoMsg(this,"错误",e.toString());
        }
    }

    private void initViews() {
        admavp = findViewById(R.id.admavp);
        deView = getLayoutInflater().inflate(R.layout.apk_decompile_activity, null);
        reView = getLayoutInflater().inflate(R.layout.apk_recompile_activity, null);
        views.add(deView);
        views.add(reView);
        slist.add("反编译");
        slist.add("回编译");
        FILESHARINGVIEWPAGERAdapter adapter = new FILESHARINGVIEWPAGERAdapter(views, slist);
        admavp.setAdapter(adapter);
        initDecompileView();
        initRecompileView();
    }

    //初始化反编译界面与功能
    private void initDecompileView() {
        Context context = this;
        Button b1 = deView.findViewById(R.id.adab1);
        Button b2 = deView.findViewById(R.id.adab2);
        Button b3 = deView.findViewById(R.id.adab3);
        Button b4 = deView.findViewById(R.id.adab4);
        adaet1 = deView.findViewById(R.id.adaet1);
        delv = deView.findViewById(R.id.adalv1);
        permissionRequest.getExternalStorageManager(context);
        b1.setOnClickListener(this);
        b2.setOnClickListener(this);
        b3.setOnClickListener(this);
        b4.setOnClickListener(this);
    }

    //初始化回编译界面与功能
    private void initRecompileView() {
        Context context = this;
        Button b1 = reView.findViewById(R.id.arab1);
        Button b2 = reView.findViewById(R.id.arab2);
        Button b3 = reView.findViewById(R.id.arab3);
        relv = reView.findViewById(R.id.aralv1);
        b1.setOnClickListener(this);
        b2.setOnClickListener(this);
        b3.setOnClickListener(this);
        permissionRequest.getExternalStorageManager(context);
    }

    //按钮点击事件
    private void btClick(Context context , View view ,Activity activity , int mode){
        ProgressDialog show = showMyDialog(context,  "请稍后，正在"+(mode == 0 ? "回" : "反")+"编译中...(可能会出现无响应，请耐心等待)....");
        Handler handler = dismissDialogHandler(0, show);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String filesDir = getMyHomeFilesPath(context);
                String myStorageHomePath = getMyStorageHomePath(activity);
                String cmd = null;
                String msg1=null,msg2=null;
                StringBuilder sb = new StringBuilder();
                StringBuilder sb2 = new StringBuilder();
                for (int i = 0; i < checkboxs.size(); i++) {
                    if (checkboxs.get(i)) {
                        if(mode ==0){
                            String filePath = list.get(i);
                            String outname = new File(filePath).getName();
                            String outDir = myStorageHomePath + "/cache/recompile";
                            File file = new File(outDir);
                            if (!file.exists()) {
                                file.mkdirs();
                            }
                            String outFile = outDir + "/" + outname + ".apk";
                            sb2.append(outFile+",");
                            cmd = "cd " + filesDir + " && sh fqtools.sh apktool reapk "+ filePath+ " "  + outFile ;
                            sb.append(cmd+"\n");
                        }
                        if(mode == 1){
                            String filePath = pkginfos.size() > 0 ? pkginfos.get(i).getApkpath() : list.get(i);
                            PackageManager packageManager = getPackageManager();
                            PackageInfo archiveInfo = packageManager.getPackageArchiveInfo(filePath, 0);
                            String pkgname = archiveInfo.packageName;
                            String outDir = myStorageHomePath + "/cache/decompile/" + pkgname;
                            sb2.append(outDir+",");
                            cmd = "cd " + filesDir + " && sh fqtools.sh apktool deapk " + filePath+ " "  + outDir ;
                            sb.append(cmd+"\n");
                        }
                    }
                }
                if(mode == 0){
                    msg1="回编译成功!\r\n文件输出在: "+sb2.toString();
                    msg2="回编译失败";
                }
                if(mode == 1){
                    msg1="反编译成功!\r\n反编译后的文件夹存放在: "+sb2.toString();
                    msg2="反编译失败";
                }
                CMD cmd1 = new CMD(sb.toString(),false);
                if(cmd1.getResultCode() == 0){
                    showInfoMsg(apkDecompileMenuActivity.this,"提示",msg1);
                }else {
                    showInfoMsg(apkDecompileMenuActivity.this, "错误", msg2 + " : " + cmd1.getResultCode() + " -- " + cmd1.getResult());
                }
                sendHandlerMSG(handler, 0);
            }
        }).start();
    }

    private void clearList() {
        checkboxs.clear();
        pkginfos.clear();
        list.clear();
    }

    private void getUserEnablePKGS() {
        multiFunc.queryUserEnablePKGS(this, pkginfos, checkboxs, 0);
    }

    private void getEnablePKGS() {
        multiFunc.queryEnablePKGS(this, pkginfos, checkboxs, 0);
    }

    //获取对应的应用程序
    private void getPKGS() {
        multiFunc.queryPKGS(this, pkginfos, checkboxs, 0);
    }

    private void getUserPKGS() {
        queryUserPKGS(this, pkginfos, checkboxs, 0);
    }

    private void showPKGS(ListView listView) {
        PKGINFOAdapter pkginfoAdapter = new PKGINFOAdapter(pkginfos, apkDecompileMenuActivity.this, checkboxs);
        listView.setAdapter(pkginfoAdapter);
    }

    private void showSelectApkToolPath(ListView listView) {
        if (list.size() > 0 && checkboxs.size() > 0) {
            USERAdapter userAdapter = new USERAdapter(list, apkDecompileMenuActivity.this, checkboxs);
            listView.setAdapter(userAdapter);
        }
    }

    private void showSelectApkPath(ListView listView) {
        if (list.size() > 0 && checkboxs.size() > 0) {
            USERAdapter userAdapter = new USERAdapter(list, apkDecompileMenuActivity.this, checkboxs);
            listView.setAdapter(userAdapter);
        }
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        menu.clear();
        viewPageIndex = admavp.getCurrentItem();
        switch (viewPageIndex) {
            case 0:
                menu.add(Menu.NONE, 0, 0, "显示所有应用");
                menu.add(Menu.NONE, 1, 1, "显示所有应用(包括禁用)");
                menu.add(Menu.NONE, 2, 2, "显示用户安装的应用");
                menu.add(Menu.NONE, 3, 3, "显示用户安装的应用(包括禁用)");
                menu.add(Menu.NONE, 4, 4, "帮助");
                menu.add(Menu.NONE, 5, 5, "退出");
                break;
            case 1:
                menu.add(Menu.NONE, 0, 0, "帮助");
                menu.add(Menu.NONE, 1, 1, "退出");
                break;
        }
        return super.onMenuOpened(featureId, menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("退出");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        viewPageIndex = admavp.getCurrentItem();
        switch (viewPageIndex) {
            case 0:
                switch (itemId) {
                    case 0:
                        getEnablePKGS();
                        showPKGS(delv);
                        break;
                    case 1:
                        getPKGS();
                        showPKGS(delv);
                        break;
                    case 2:
                        getUserPKGS();
                        showPKGS(delv);
                        break;
                    case 3:
                        getUserEnablePKGS();
                        showPKGS(delv);
                        break;
                    case 4:
                        showInfoMsg(this, "帮助信息", "该页面是用于apk反编译操作的，需要安装jdk与fqtools，采用传统apktool进行反编译操作,如果没有安装，则会自动跳转安装页面，按照页面提示安装即可。\r\n" +
                                "1.点击右上角三个点，可以列出与之相匹配的应用列表，支持直接批量反编译它们.\r\n" +
                                "2.点击选择本地文件，可以批量反编译用户本地的apk文件。\r\n" +
                                "3.上面有个搜索框，支持中英文搜索，无大小写限制.\r\n");
                        break;
                    case 5:
                        fuckActivity.getIns().killall();
                        ;
                }
                break;
            case 1:
                switch (itemId) {
                    case 0:
                        showInfoMsg(this, "帮助信息", "该页面是用于apk回编译操作的，需要安装jdk与fqtools，采用传统apktool进行回编译操作,如果没有安装，则会自动跳转安装页面，按照页面提示安装即可。\r\n" +
                                "1.点击选择本地文件夹，可以回编译用户本地反编译后的内容（需要选择带apktool.yml文件的项目工程）。\r\n" +
                                "2.点击加载默认，可以列出所有从该应用反编译后的项目名称（推荐这个）。\r\n" +
                                "3.点击上面开始回编译就会开始进行回编译操作.\r\n");
                        break;
                    case 1:
                        fuckActivity.getIns().killall();
                        ;
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        viewPageIndex = admavp.getCurrentItem();
        if (resultCode == Activity.RESULT_OK) {
            clearList();
            String msg = null, eq = null;
            if(viewPageIndex == 0){
                msg = "请选择正确的apk文件";
                eq = "apk";
            }

            if(viewPageIndex == 1){
                msg = "请选择正确的apktool.yml文件";
                eq = "yml";
            }

            String storage = Environment.getExternalStorageDirectory().toString();
            if (data.getClipData() != null) {//有选择多个文件
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    Uri uri = data.getClipData().getItemAt(i).getUri();
                    selectFile(apkDecompileMenuActivity.this, storage, uri, list, checkboxs,msg,eq);
                }
            } else if (data.getData() != null) {//只有一个文件咯
                Uri uri = data.getData();
                selectFile(apkDecompileMenuActivity.this, storage, uri, list, checkboxs, msg,eq);
            }

            if(viewPageIndex == 0) {
                showSelectApkPath(delv);
            }

            if(viewPageIndex == 1) {
                showSelectApkToolPath(relv);
            }

        }
    }


    @Override
    public void onClick(View view) {
        Context context =this;
        Activity activity = this;
        switch (view.getId()){
            case R.id.adab1:
                btClick(context,view,activity,1);
                break;
            case R.id.arab1:
                btClick(context,view,activity,0);
                break;
            case R.id.arab3:
                execFileSelect(context, activity, "请选择 apktool.yml 文件");
                break;
            case R.id.adab3:
                execFileSelect(context, activity, "请选择.apk文件");
                break;
            case R.id.adab2:
                getUserEnablePKGS();
                showPKGS(delv);
                break;
            case R.id.adab4:
                String searchStr = adaet1.getText().toString();
                pkginfos = multiFunc.indexOfPKGS(activity, searchStr, pkginfos, checkboxs, 0);
                showPKGS(delv);
                break;
            case R.id.arab2:
                ProgressDialog show = showMyDialog(context,"正在扫描默认路径内容,请稍后(可能会出现无响应，请耐心等待)....");
                Handler handler = dismissDialogHandler(0,show);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        clearList();
                        String myStorageHomePath = getMyStorageHomePath(activity);
                        String defaultDecompileDir = myStorageHomePath + "/cache/decompile";
                        File file1 = new File(defaultDecompileDir);
                        if (file1.exists()) {
                            File[] files = file1.listFiles();
                            if (files.length > 0) {
                                for (File file : files) {
                                    list.add(file.toString());
                                    checkboxs.add(false);
                                }
                                showSelectApkToolPath(relv);
                            } else {
                                Toast.makeText(context, "默认路径没有反编译后的内容", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(context, "默认路径没有反编译后的内容", Toast.LENGTH_SHORT).show();
                        }
                        sendHandlerMSG(handler,0);
                    }
                }).start();
                break;
        }
    }
}
