package org.fqaosp.myActivitys;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
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
import org.fqaosp.entity.PKGINFO;
import org.fqaosp.utils.dialogUtils;
import org.fqaosp.utils.fileTools;
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

    private Context context;

    private fileTools ft = new fileTools();
    private dialogUtils du = new dialogUtils();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.apk_decompile_menu_activity);
        fuckActivity.getIns().add(this);
        setTitle("软件反编译");
        context=this;
        Intent intent = getIntent();
        isRoot = intent.getBooleanExtra("isRoot",false);
        isADB = intent.getBooleanExtra("isADB",false);
        if(Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT){
            du.showInfoMsg(this,"警告","安卓4.x设备暂不支持该功能");
        }else{
            extractAssetsFiles(context,isRoot,isADB);
            initViews();
        }
        du.showLowMemDialog(context);
    }

    private void extractAssetsFiles(Context context , boolean isRoot , boolean isADB) {
        try {
            multiFunc m = new multiFunc();
            ft.checkTools(context,isADB);
            String filesDir = ft.getMyHomeFilesPath(context);
            String jdkDir = filesDir + "/usr/opt/openjdk";
            String apktoolFile = filesDir + "/usr/apktool.jar";
            File file1 = new File(filesDir);
            File jdkD = new File(jdkDir);
            File apkToolF = new File(apktoolFile);
            if (!file1.exists()) {
                file1.mkdirs();
            }
            if (!jdkD.exists() ) {
                du.showInfoMsg(context,"错误","未找到jdk，请重新导入工具包后再执行此项");
                m.jump(context, importToolsActivity.class,isRoot,isADB);
            }
            if (!apkToolF.exists()) {
                du.showInfoMsg(context,"错误","未找到apktool.jar，请重新导入工具包后再执行此项");
                m.jump(context, importToolsActivity.class,isRoot,isADB);
            }
        } catch (Exception e) {
            du.showInfoMsg(context,"错误",e.toString());
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
        ArrayList<PKGINFO> pplist = new ArrayList<>();
        for (int i = 0; i < checkboxs.size(); i++) {
            if (checkboxs.get(i)) {
                String s = list.size() ==0?"":list.get(i);
                if(mode ==0){
                    pplist.add(new PKGINFO(s,s,s,null,null,null,null));
                }
                if(mode == 1){
                    pplist.add(pkginfos.size() > 0 ? pkginfos.get(i) : new PKGINFO(s,s,s,null,null,null,null));
                }
            }
        }

        du.showProcessBarDialogByCMD(context,pplist,"正在"+(mode == 0 ? "回" : "反")+"编译中","当前正在"+(mode == 0 ? "回" : "反")+"编译: ",8,
                null ,null,isRoot,"0",mode,null,
                null);
    }

    private void clearList() {
        checkboxs.clear();
        pkginfos.clear();
        list.clear();
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
        Activity activity = this;
        switch (viewPageIndex) {
            case 0:
                switch (itemId) {
                    case 0:
                        du.queryPKGProcessDialog(context,activity,delv,pkginfos,checkboxs,0,null,isRoot);
                        break;
                    case 1:
                        du.queryPKGProcessDialog(context,activity,delv,pkginfos,checkboxs,1,null,isRoot);
                        break;
                    case 2:
                        du.queryPKGProcessDialog(context,activity,delv,pkginfos,checkboxs,2,null,isRoot);
                        break;
                    case 3:
                        du.queryPKGProcessDialog(context,activity,delv,pkginfos,checkboxs,3,null,isRoot);
                        break;
                    case 4:
                        du.showInfoMsg(this, "帮助信息", "该页面是用于apk反编译操作的，需要安装jdk与fqtools，采用传统apktool进行反编译操作,如果没有安装，则会自动跳转安装页面，按照页面提示安装即可。\r\n" +
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
                        du.showInfoMsg(this, "帮助信息", "该页面是用于apk回编译操作的，需要安装jdk与fqtools，采用传统apktool进行回编译操作,如果没有安装，则会自动跳转安装页面，按照页面提示安装即可。\r\n" +
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
                    ft.selectFile(apkDecompileMenuActivity.this, storage, uri, list, checkboxs,msg,eq);
                }
            } else if (data.getData() != null) {//只有一个文件咯
                Uri uri = data.getData();
                ft.selectFile(apkDecompileMenuActivity.this, storage, uri, list, checkboxs, msg,eq);
            }

            if(viewPageIndex == 0) {
                du.showUsers(context,delv,list,checkboxs);
            }

            if(viewPageIndex == 1) {
                du.showUsers(context,relv,list,checkboxs);
            }

        }
    }


    @Override
    public void onClick(View view) {
        Activity activity = this;
        switch (view.getId()){
            case R.id.adab1:
                btClick(context,view,activity,1);
                break;
            case R.id.arab1:
                btClick(context,view,activity,0);
                break;
            case R.id.arab3:
                ft.execFileSelect(context, activity, "请选择 apktool.yml 文件");
                break;
            case R.id.adab3:
                ft.execFileSelect(context, activity, "请选择.apk文件");
                break;
            case R.id.adab2:
                du.queryPKGProcessDialog(context,activity,delv,pkginfos,checkboxs,2,null,isRoot);
                break;
            case R.id.adab4:
                du.showIndexOfPKGSDialog(context,activity,delv,adaet1,pkginfos,null,checkboxs);
                break;
            case R.id.arab2:
                ProgressDialog show = du.showMyDialog(context,"正在扫描默认路径内容,请稍后(可能会出现无响应，请耐心等待)....");
                Handler handler = new Handler(){
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        if(msg.what == 0){
                            show.dismiss();
                            du.showUsers(context,relv,list,checkboxs);
                        }
                        if(msg.what == 1){
                            show.dismiss();
                            Toast.makeText(context, "默认路径没有反编译后的内容", Toast.LENGTH_SHORT).show();
                        }
                    }
                };
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        clearList();
                        String defaultDecompileDir = context.getExternalCacheDir().toString() + "/decompile";
                        File file1 = new File(defaultDecompileDir);
                        if (file1.exists()) {
                            File[] files = file1.listFiles();
                            if (files.length > 0) {
                                for (File file : files) {
                                    list.add(file.toString());
                                    checkboxs.add(false);
                                }
                            } else {
                                Toast.makeText(context, "默认路径没有反编译后的内容", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            du.sendHandlerMSG(handler,1);
                        }
                        du.sendHandlerMSG(handler,0);
                    }
                }).start();
                break;
        }
    }
}
