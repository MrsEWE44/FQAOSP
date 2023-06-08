package org.fqaosp.myActivitys;

/**
 *
 * 用于更改组件状态
 * 可以禁用/启用应用的service、activity
 * 可以授权/撤销应用的permission
 *
 * */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import org.fqaosp.R;
import org.fqaosp.entity.PKGINFO;
import org.fqaosp.sql.iptablesDB;
import org.fqaosp.utils.CMD;
import org.fqaosp.utils.appopsCmdStr;
import org.fqaosp.utils.dialogUtils;
import org.fqaosp.utils.fileTools;
import org.fqaosp.utils.fuckActivity;
import org.fqaosp.utils.makeWP;
import org.fqaosp.utils.packageUtils;
import org.fqaosp.utils.permissionRequest;
import org.fqaosp.utils.stringUtils;
import org.fqaosp.utils.textUtils;
import org.fqaosp.utils.userUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class appopsActivity extends AppCompatActivity {

    private Button appopsab2,apopsab6;
    private ListView lv1;
    private EditText apopsaet1;
    private ArrayList<PKGINFO> pkginfos = new ArrayList<>();
    private ArrayList<Boolean> checkboxs = new ArrayList<>();
    private String uid;
    private Spinner apopsasp1,apopsasp2;
    private int nowItemIndex=-1;
    private boolean isRoot=false,isADB=false;
    private boolean isDisable=false;
    private String apops_permis[] = {"通话/短信相关", "存储","剪切板","电池优化","后台运行","摄像头","麦克风","定位","日历","传感器扫描","通知","待机模式","待机活动","应用联网"};
    private String apops_opt[] = {"默认", "拒绝","允许","仅在运行时允许"};
    private String apops_opt2[] = {"活跃", "工作集","常用","极少使用","受限"};
    private String apops_opt3[] = {"允许","拒绝"};
    private String script_name = "fqtools.sh";
    private int apops_permis_index,apops_opt_index,mode=0;

    private Context con;
    private Activity activity;

    private packageUtils pkgutils = new packageUtils();
    private dialogUtils du = new dialogUtils();
    private userUtils uu = new userUtils();
    private fileTools ft = new fileTools();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appops_activity);
        fuckActivity.getIns().add(this);
        setTitle("应用管理");
        con=this;
        activity=this;
        Intent intent = getIntent();
        isRoot = intent.getBooleanExtra("isRoot",false);
        isADB = intent.getBooleanExtra("isADB",false);
        if(isRoot == false && isADB == false){
            Toast.makeText(this, "没有被授权,将无法正常使用该功能", Toast.LENGTH_SHORT).show();
        }

        /**
         * 如果uid为null，就走默认操作
         * 如果uid不为null，则走分身部分
         * */
        uid = intent.getStringExtra("uid");
        if(uid == null){
            uid=uu.getMyUID();
        }
        initBt();
    }

    private void initBt(){
        appopsab2 = findViewById(R.id.apopsab2);
        apopsab6 = findViewById(R.id.apopsab6);
        apopsaet1 = findViewById(R.id.apopsaet1);
        lv1 = findViewById(R.id.apopsalv1);
        apopsasp1 = findViewById(R.id.apopsasp1);
        apopsasp2 = findViewById(R.id.apopsasp2);
        apopsasp1.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, apops_permis));
        apopsasp2.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, apops_opt));
        ft.checkTools(con,isADB);
        clickedBt();
    }

    private void clickedBt(){
        apopsasp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                apops_permis_index = i;
                if(apops_permis_index==11||apops_permis_index==13){
                    mode=1;
                    apopsasp2.setAdapter(new ArrayAdapter<String>(con, android.R.layout.simple_list_item_1, apops_opt3));
                    ProgressDialog pd = du.showMyDialog(con, "正在应用网络更改");
                    Handler handler = new Handler(){
                        @Override
                        public void handleMessage(@NonNull Message msg) {
                            if(msg.what == 0){
                                pd.dismiss();
                            }
                        }
                    };
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            iptablesDB ipDB = new iptablesDB(con,"iptable.db",null,1);
                            if(ipDB.count() > 0){
                                HashMap<String, Integer> select = ipDB.select(null, 0);
                                appopsCmdStr ac = new appopsCmdStr();
                                for (Map.Entry<String, Integer> entry : select.entrySet()) {
//                                Log.i("key-value",entry.getKey()+" -- "+entry.getValue());
                                    try {
                                        ApplicationInfo applicationInfo = con.getPackageManager().getApplicationInfo(entry.getKey(), 0);
                                        new CMD(ac.disableAppByAPPUIDCMD(applicationInfo.uid));
                                    } catch (PackageManager.NameNotFoundException e) {
                                        throw new RuntimeException(e);
                                    }

                                }
                            }

                            du.sendHandlerMSG(handler,0);
                        }
                    }).start();
                    //需要实现iptable数据库
                }else if(apops_permis_index==12){
                    mode=2;
                    apopsasp2.setAdapter(new ArrayAdapter<String>(con, android.R.layout.simple_list_item_1, apops_opt2));
                }else{
                    mode=0;
                    apopsasp2.setAdapter(new ArrayAdapter<String>(con, android.R.layout.simple_list_item_1, apops_opt));
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        apopsasp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                apops_opt_index = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        appopsab2.setOnClickListener((v)->{
            du.showIndexOfPKGSDialog(con,activity,lv1,apopsaet1,pkginfos,null,checkboxs);
        });


        lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                PKGINFO pkginfo = pkginfos.get(i);
                try {
                    PackageInfo packageInfo = getPackageManager().getPackageInfo(pkginfo.getPkgname(), 0);
                    Intent intent = new Intent(appopsActivity.this,appopsInfoActivity.class);
                    intent.putExtra("pkgname",pkginfo.getPkgname());
                    intent.putExtra("uid",uid);
                    startActivity(intent);
                } catch (PackageManager.NameNotFoundException e) {
                    Toast.makeText(con, "未安装该应用", Toast.LENGTH_SHORT).show();
                }

            }
        });

        lv1.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                nowItemIndex=i;
                createLVMenu();
                return false;
            }
        });

        //应用appops权限更改
        apopsab6.setOnClickListener((v)->{
            du.showProcessBarDialogByCMD(con,addPkginfos(),"正在应用更改中...","当前应用更改的应用: ",2,null ,isDisable,isRoot,uid,null,apops_opt_index,apops_permis_index);
        });

    }

    private ArrayList<PKGINFO> addPkginfos(){
        ArrayList<PKGINFO> list = new ArrayList<>();
        int hit=0;
        for (int i = 0; i < checkboxs.size(); i++) {
            if(checkboxs.get(i) && !pkginfos.get(i).getPkgname().equals(getPackageName())){
                list.add(pkginfos.get(i));
                hit++;
            }
        }
        if(hit == 0){
            for (PKGINFO pkginfo : pkginfos) {
                if(!pkginfo.getPkgname().equals(getPackageName())){
                    list.add(pkginfo);
                }
            }
        }
        return list;
    }

    //跳转到系统自带的应用详情界面
    private void intoSYSApp(int i){
        PKGINFO pkginfo = pkginfos.get(i);
        Intent intent2 = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent2.setData(Uri.parse("package:" + pkginfo.getPkgname()));
        startActivity(intent2);
    }

    //长按listview中的元素，显示一个菜单选项
    private void createLVMenu(){
        PackageManager pm = getPackageManager();
        PKGINFO pkginfo = pkginfos.get(nowItemIndex);
        lv1.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                contextMenu.add(0,0,0,"复制信息");
                try {
                    PackageInfo packageInfo= pm.getPackageInfo(pkginfo.getPkgname(),0);
                    contextMenu.add(0,1,0,"跳转至应用详情");
                    contextMenu.add(0,2,0,"导出所有勾选应用包名");
                    contextMenu.add(0,3,0,"导出并附加所有勾选应用包名");
                    if(isDisable){
                        contextMenu.add(0,10,0,"启用");
                    }else{
                        contextMenu.add(0,11,0,"禁用");
                    }
                    contextMenu.add(0,4,0,"尝试降级安装");
                    contextMenu.add(0,8,0,"尝试覆盖安装");
                    contextMenu.add(0,9,0,"尝试安装debug应用");
                    contextMenu.add(0,5,0,"提取应用");
                    contextMenu.add(0,6,0,"卸载应用");
                } catch (PackageManager.NameNotFoundException e) {
                    contextMenu.add(0,7,0,"尝试安装应用");
                    contextMenu.add(0,9,0,"尝试安装debug应用");
                }
            }
        });

    }

    //安装本地文件
    private void installLocalPKG(int install_mode){
        du.showProcessBarDialogByCMD(con,addPkginfos(),"正在安装本地应用中...","当前正在安装: ",3,install_mode ,isDisable,isRoot,uid,null,apops_opt_index,apops_permis_index);
    }

    //卸载应用
    private void uninstallPKG(){
        du.showProcessBarDialogByCMD(con,addPkginfos(),"正在卸载应用...","当前正在卸载: ",1,null ,isDisable,isRoot,uid,null,apops_opt_index,apops_permis_index);
    }

    //修改应用状态,禁用或者启用
    private void changePKGState(){
        du.showProcessBarDialogByCMD(con,addPkginfos(),"正在修改应用状态中...","当前正在更改的应用: ",0,null ,isDisable,isRoot,uid,null,apops_opt_index,apops_permis_index);
    }

    //提取apk文件
    private void extractPKGFileToLocal(){
        du.showProcessBarDialogByCMD(con,addPkginfos(),"正在提取应用中...","当前正在提取的应用: ",11,null ,isDisable,isRoot,uid,null,apops_opt_index,apops_permis_index);
    }

    //导出包名列表到本地
    private void extractPKGList(Boolean isApp){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < checkboxs.size(); i++) {
            if(checkboxs.get(i)){
                PKGINFO pkginfo = pkginfos.get(i);
                sb.append(pkginfo.getPkgname()+"\n");
            }
        }
        File cacheDir = this.getExternalCacheDir();
        String myStorageHomePath = cacheDir.toString()+"/pkgs";
        String outFile = myStorageHomePath+"/pkglist.txt";
        File file = new File(myStorageHomePath);
        File file2 = new File(outFile);
        if(!file.exists()){
            //创建/sdcard/Android/data/包名/cache文件夹,可以不需要申请存储权限实现
            cacheDir.mkdirs();
            boolean mkdirs = file.mkdirs();
        }
        if(file.exists()){
            if(file2.exists() && isApp == false){
                file2.delete();
            }
            try {
                file2.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(ft.writeDataToPath(sb.toString(),outFile,isApp)){
                du.showInfoMsg(appopsActivity.this,"提示","保存在: " + outFile);
            }else{
                du.showInfoMsg(appopsActivity.this,"错误","导出包名列表失败");
            }
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case 0:
                PKGINFO pkginfo = pkginfos.get(nowItemIndex);
                new textUtils().copyText(con,pkginfo.toString());
                break;
            case 1:
                intoSYSApp(nowItemIndex);
                break;
            case 2:
                extractPKGList(false);
                break;
            case 3:
                extractPKGList(true);
                break;
            case 4:
                installLocalPKG(1);
                break;
            case 5:
                extractPKGFileToLocal();
                break;
            case 6:
                uninstallPKG();
                break;
            case 7:
                installLocalPKG(0);
                break;
            case 8:
                installLocalPKG(3);
                break;
            case 9:
                installLocalPKG(2);
                break;
            case 10:
            case 11:
                changePKGState();
                break;

        }
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE,0,0,"显示所有应用");
        menu.add(Menu.NONE,1,1,"显示所有应用(包括禁用)");
        menu.add(Menu.NONE,2,2,"显示用户安装的应用");
        menu.add(Menu.NONE,3,3,"显示用户安装的应用(包括禁用)");
        menu.add(Menu.NONE,4,4,"显示被禁用的应用");
        menu.add(Menu.NONE,5,5,"选择本地应用");
        menu.add(Menu.NONE,6,6,"选择本地安装包文件夹");
        menu.add(Menu.NONE,7,7,"帮助");
        menu.add(Menu.NONE,8,8,"退出");
        return super.onCreateOptionsMenu(menu);
    }

    private void selectLocalFile(){
        permissionRequest.getExternalStorageManager(appopsActivity.this);
        ft.execFileSelect(appopsActivity.this,appopsActivity.this,"请选择要安装的文件");
    }

    private void selectLocalDir(){
        permissionRequest.getExternalStorageManager(appopsActivity.this);
        ft.execDirSelect(appopsActivity.this,appopsActivity.this,"请选择要安装的文件");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        isDisable=false;
        int itemId = item.getItemId();
        makeWP wp = new makeWP();
        switch (itemId){
            case 0:
                if(uid == null || uid.equals(uu.getMyUID())){
                    du.queryPKGProcessDialog(con,activity,lv1,pkginfos,checkboxs,0,null,isRoot);
                }else{
                    du.queryPKGProcessDialog(con,activity,lv1,pkginfos,checkboxs,null,wp.getPkgByUIDCMD(uid),isRoot);
                }
                break;
            case 1:
                if(uid == null || uid.equals(uu.getMyUID())){
                    du.queryPKGProcessDialog(con,activity,lv1,pkginfos,checkboxs,1,null,isRoot);
                }else{
                    du.queryPKGProcessDialog(con,activity,lv1,pkginfos,checkboxs,null,wp.getPkgByUIDCMD(uid),isRoot);
                }
                break;
            case 2:
                if(uid == null || uid.equals(uu.getMyUID())){
                    du.queryPKGProcessDialog(con,activity,lv1,pkginfos,checkboxs,2,null,isRoot);
                }else{
                    du.queryPKGProcessDialog(con,activity,lv1,pkginfos,checkboxs,null,wp.getUserPkgByUIDCMD(uid),isRoot);
                }
                break;
            case 3:
                if(uid == null || uid.equals(uu.getMyUID())){
                    du.queryPKGProcessDialog(con,activity,lv1,pkginfos,checkboxs,3,null,isRoot);
                }else{
                    du.queryPKGProcessDialog(con,activity,lv1,pkginfos,checkboxs,null,wp.getUserPkgByUIDCMD(uid),isRoot);
                }
                break;
            case 4:
                isDisable=true;
                if(uid == null || uid.equals(uu.getMyUID())){
                    du.queryPKGProcessDialog(con,activity,lv1,pkginfos,checkboxs,4,null,isRoot);
                }else{
                    du.queryPKGProcessDialog(con,activity,lv1,pkginfos,checkboxs,null,wp.getDisablePkgByUIDCMD(uid),isRoot);
                }
                break;
            case 5:
                selectLocalFile();
                break;
            case 6:
                selectLocalDir();
                break;
            case 7:
                du.showInfoMsg(this,"帮助信息","该页面是用于应用管理的,支持应用提取、详情跳转、卸载应用、导出应用信息、安装apks/apk应用，需要安装fqtools,如果没有安装，则会自动跳转安装页面，按照页面提示安装即可。\r\n" +
                        "1.搜索框，支持中英文搜索，无大小写限制.\r\n" +
                        "2.长按应用列表会出现相关操作菜单，根据自己需求点击即可。支持批量操作。\r\n" +
                        "3.右上角\"选择本地应用\",支持选择apks进行安装，传统apk文件可以加载出图标。\r\n" +
                        "4.点击应用列表，则会进入到应用配置页面.\r\n" +
                        "5.右上角\"选择本地安装包文件夹\"，选择一个文件夹，会自动安装里面所有apk/apks文件.如果你是通过mt直接从/data/app连同文件夹一起提取的，效果会更好。\r\n" +
                        "6.应用更改，该按钮的作用是用来配置应用权限(部分权限配置需要root权限)，可以批量或者勾选操作，不勾选则默认全部生效。左边是你想要操作的权限名称，右边是设置的模式。\r\n假如你想要将fqaosp的存储权限给拒绝掉：可以先点击左边的权限列表，选中\"存储\"，然后在右边列表中选择\"拒绝\"，最后勾选中fqaosp(如果不勾选而直接点击按钮，则会默认生效所有应用，请斟酌！)，点击\"应用更改\"即可生效。\r\n");
                break;
            case 8:
                fuckActivity.getIns().killall();
                ;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void addPKGINFO(PackageManager pm,Uri uri , String storage){
        String path = uri.getPath();
        String filePath = null;
        if(path.indexOf("document/msf") != -1){
            filePath = ft.uriToFilePath(uri,con);
        }else if(path.indexOf("document/primary") != -1){
            filePath = storage + "/" +uri.getPath().replaceAll("/document/primary:","");
        }else{
            filePath=uri.getPath();
        }
        stringUtils su = new stringUtils();
        String nameType = su.getPathByLastNameType(filePath);
        if(nameType.equals("apk")){
            PackageInfo packageInfo = pm.getPackageArchiveInfo(filePath, PackageManager.GET_PERMISSIONS);
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            pkginfos.add(new PKGINFO(applicationInfo.packageName, pm.getApplicationLabel(applicationInfo).toString(), filePath,applicationInfo.uid+"",packageInfo.versionName, pm.getApplicationIcon(applicationInfo),new File(filePath).length())) ;
            checkboxs.add(false);
        }else if(nameType.equals("apks")){
            Drawable d = ContextCompat.getDrawable(appopsActivity.this,R.drawable.ic_launcher_foreground);
            pkginfos.add(new PKGINFO(su.getPathByLastName(filePath),"未知",filePath,"未知","未知", d,new File(filePath).length()));
            checkboxs.add(false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String storage = ft.getSDPath(con);
        PackageManager pm = getPackageManager();
        if(requestCode == 0){
            pkgutils.clearList(pkginfos,checkboxs);
            if(data != null && data.getClipData() != null) {//有选择多个文件
                int count = data.getClipData().getItemCount();
                for(int i =0;i<count;i++){
                    Uri uri = data.getClipData().getItemAt(i).getUri();
                    addPKGINFO(pm,uri,storage);
                }
            } else if(data != null && data.getData() != null) {//只有一个文件咯
                Uri uri = data.getData();
                addPKGINFO(pm,uri,storage);
            }
            du.showPKGS(con,lv1,pkginfos,checkboxs);
        }

        //安装文件夹里面所有apk文件
        if(requestCode == 43){
            if(data.getData() != null) {//只有一个文件咯
                Uri uri = data.getData();
                String path = uri.getPath();
                String filePath=null;
                if(path.indexOf("tree/primary") != -1){
                    filePath = storage + "/" +path.replaceAll("/tree/primary:","");
                }else if(path.indexOf("document/primary") != -1){
                    filePath = storage + "/" +path.replaceAll("/document/primary:","");
                    filePath = new File(filePath).getParent();
                }else{
                    filePath = new File(path).getParent();
                }
                if(isRoot || isADB){
                    pkgutils.clearList(pkginfos,checkboxs);
                    try {
                        ArrayList<File> files = new ArrayList<>();
                        ft.getAllFileByEndName(filePath,".apk",files);
                        for (File listFile : files) {
                            addPKGINFO(pm,Uri.fromFile(listFile),storage);
                        }
                        du.showPKGS(con,lv1,pkginfos,checkboxs);
                        du.showProcessBarDialogByCMD(con,pkginfos,"正在安装 [ "+filePath+" ] 文件夹里面的内容...","当前正在安装: ",4,null ,isDisable,isRoot,uid,null,apops_opt_index,apops_permis_index);
                    }catch (Exception e){
                        String filesDir = getExternalFilesDir(null).toString();
                        if(isRoot){
                            filesDir = ft.getMyHomeFilesPath(con);
                        }
                        String barfile = filesDir+"/"+script_name;
                        String cmdstr = "sh "+barfile+" inapkonpath " + filePath;
                        du.showCMDInfoMSG(con,true,cmdstr,isRoot,"正在安装"+filePath+"路径下的应用,请稍后(可能会出现无响应，请耐心等待)....","安装"+filePath+"路径下的应用结束.");
                    }
                }else{
                    du.showInfoMsg(con,"错误","该功能需要adb或者root权限才能使用!!!!");
                }
            }
        }
    }

}
