package org.fqaosp;

import static org.fqaosp.utils.fileTools.getMyStorageHomePath;
import static org.fqaosp.utils.multiFunc.checkShizukuPermission;
import static org.fqaosp.utils.multiFunc.checkTools;
import static org.fqaosp.utils.multiFunc.dismissDialogHandler;
import static org.fqaosp.utils.multiFunc.isSuEnable;
import static org.fqaosp.utils.multiFunc.jump;
import static org.fqaosp.utils.multiFunc.sendHandlerMSG;
import static org.fqaosp.utils.multiFunc.showInfoMsg;
import static org.fqaosp.utils.multiFunc.showMyDialog;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.drawerlayout.widget.DrawerLayout;

import org.fqaosp.adapter.MENUSELECTAdapter;
import org.fqaosp.entity.menuEntity;
import org.fqaosp.myActivitys.apkDecompileMenuActivity;
import org.fqaosp.myActivitys.appopsActivity;
import org.fqaosp.myActivitys.backupRestoreActivity;
import org.fqaosp.myActivitys.fileSearchActivity;
import org.fqaosp.myActivitys.fileSharingActivity;
import org.fqaosp.myActivitys.imgMenuActivity;
import org.fqaosp.myActivitys.importToolsActivity;
import org.fqaosp.myActivitys.killAppActivity;
import org.fqaosp.myActivitys.mountLocalImageActivity;
import org.fqaosp.myActivitys.otherToolsActivity;
import org.fqaosp.myActivitys.romToolsActivity;
import org.fqaosp.myActivitys.sqliteManageActivity;
import org.fqaosp.myActivitys.workProfileMenuActivity;
import org.fqaosp.utils.fuckActivity;
import org.fqaosp.utils.multiFunc;
import org.fqaosp.utils.netUtils;
import org.fqaosp.utils.permissionRequest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 首页界面内容...
 *
 * */

public class MainActivity extends Activity {
    private String updatehost="https://gitee.com/SorryMyLife/FQAOSP/";
    private String updateurl=updatehost+"raw/master/app/build.gradle";
    private String updatelog=updatehost+"raw/master/README.md";
    private String updatefile=updatehost+"releases/download/";
    private int versioncode;
    private boolean haveupdate=false;
    private boolean isRoot , isShizuku;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fuckActivity.getIns().add(this);
        permissionRequest.requestExternalStoragePermission(this);
        try{
            getExternalCacheDir().mkdirs();
        }catch (Exception e){
            showInfoMsg(this,"警告","你当前的系统是存在问题，不能够读写Android/data/"+getPackageName()+"/cache路径.\r\n错误内容: " + e.toString());
        }
        isRoot=isSuEnable();
        isShizuku=checkShizukuPermission(1);
        initBut();
        checkTools(this);
    }

    private  void initBut(){
        Activity activity = this;
        Context context = this;
        DrawerLayout dl = findViewById(R.id.amdl);
        ListView lv = findViewById(R.id.amlv1);
        Button bt1 = findViewById(R.id.amb1);
        Button bt2 = findViewById(R.id.amb2);
        Button bt3 = findViewById(R.id.amb3);
        Button amupdate = findViewById(R.id.amupdate);
        TextView tv1 = findViewById(R.id.mtv1);
        TextView tv2 = findViewById(R.id.mtv2);
        ArrayList<menuEntity> menuEntities = new ArrayList<>();
        menuEntities.add(new menuEntity("文件搜索","该功能是用于文件搜索的，你可以按照任意条件搜索/Android/data或者obb或者/sdcard/里面的文件。\r\n长按该选项即可进入。\n" ,fileSearchActivity.class, ContextCompat.getDrawable(this,R.drawable.left_menu_icon_file_foreground),false,false));
        menuEntities.add(new menuEntity("文件共享","该功能是用于文件、应用网络共享的，当有人跟你同处在一个局域网的时候，就可以通过这个功能来分享文件给对方，该功能不需要root权限。\r\n长按该选项即可进入。\n",fileSharingActivity.class, ContextCompat.getDrawable(this,R.drawable.left_menu_icon_file_foreground),false,false));
        menuEntities.add(new menuEntity("后台管理","该功能是用于后台进程管理的，需要root授权。\r\n长按该选项即可进入。\n",killAppActivity.class, ContextCompat.getDrawable(this,R.drawable.left_menu_icon_foreground),true,true));
        menuEntities.add(new menuEntity("手机分身","该功能是用于手机分身管理的，但仅限于类原生，以及其它没有限制过多开用户的系统使用，国内定制系统使用会存在问题，包括moto的myui。需要root使用。\r\n长按该选项即可进入。\n",workProfileMenuActivity.class, ContextCompat.getDrawable(this,R.drawable.left_menu_icon_view_array_foreground),true,true));
        menuEntities.add(new menuEntity("U盘模式","该功能是用于挂载手机上的镜像文件，让电脑识别的，可以当U盘使用，可以给电脑安装系统，需要root权限授权。\r\n长按该选项即可进入。\n",mountLocalImageActivity.class, ContextCompat.getDrawable(this,R.drawable.left_menu_icon_usb_foreground),true,false));
        menuEntities.add(new menuEntity("apk反编译","该功能是用于apk回编译操作的，需要安装jdk与fqtools，采用传统apktool进行回编译操作,如果没有安装，则会自动跳转安装页面，按照页面提示安装即可。\r\n长按该选项即可进入。\n" ,apkDecompileMenuActivity.class, ContextCompat.getDrawable(this,R.drawable.left_menu_icon_foreground),false,false));
        menuEntities.add(new menuEntity("应用管理","该功能是用于应用管理的,支持应用提取、详情跳转、卸载应用、导出应用信息、安装apks/apk应用，需要安装fqtools,如果没有安装，则会自动跳转安装页面，按照页面提示安装即可。\r\n长按该选项即可进入。\n",appopsActivity.class, ContextCompat.getDrawable(this,R.drawable.left_menu_icon_android_foreground),true,true));
        menuEntities.add(new menuEntity("备份与恢复","该功能是用于应用备份与恢复的,支持应用备份与恢复，可选择只备份数据、安装包、安装包+数据，也支持仅恢复数据、安装包、安装包+数据，需要安装fqtools,如果没有安装，则会自动跳转安装页面，按照页面提示安装即可。\r\n长按该选项即可进入。\n",backupRestoreActivity.class, ContextCompat.getDrawable(this,R.drawable.left_menu_icon_backup_restore_foreground),true,false));
        menuEntities.add(new menuEntity("数据库编辑","该功能是用于编辑该软件产生的数据库文件。\r\n长按该选项即可进入。\n",sqliteManageActivity.class, ContextCompat.getDrawable(this,R.drawable.left_menu_icon_foreground),false,false));
        menuEntities.add(new menuEntity("分区管理","用于提取系统分区到本地或者刷入本地分区文件到分区位置。此功能需要root权限。\r\n 长按该选项即可进入。\r\n", imgMenuActivity.class, ContextCompat.getDrawable(this,R.drawable.left_menu_icon_data_usage_foreground),true,false));
        menuEntities.add(new menuEntity("ROM工具","用于rom固件相关的操作，例如解包payload.bin、system.new.dat文件，部分功能仍需要root权限才能解决。\r\n 长按该选项即可进入。\r\n", romToolsActivity.class, ContextCompat.getDrawable(this,R.drawable.left_menu_icon_android_foreground),false,false));
        menuEntities.add(new menuEntity("其它工具","杂七杂八的小工具合集.\r\n 长按该选项即可进入。\r\n", otherToolsActivity.class, ContextCompat.getDrawable(this,R.drawable.left_menu_icon_foreground),true,true));
        menuEntities.add(new menuEntity("组件检查","用于检查该设备是否已经安装部分功能所需的额外扩展组件。\r\n 长按该选项即可进入。\r\n",importToolsActivity.class, ContextCompat.getDrawable(this,R.drawable.left_menu_icon_find_foreground),false,false));

        //创建并设置适配器
        MENUSELECTAdapter adapter = new MENUSELECTAdapter(menuEntities,this,isRoot,isShizuku);
        lv.setAdapter(adapter);
        amupdate.setVisibility(View.INVISIBLE);
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dl.openDrawer(Gravity.LEFT);
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                menuEntity menu = menuEntities.get(i);
                tv1.setText(menu.getName());
                tv2.setText(menu.getInfo());
                amupdate.setVisibility(View.INVISIBLE);
                dl.closeDrawer(Gravity.LEFT);
            }
        });

        bt2.setOnClickListener((v)->{fuckActivity.getIns().killall();});

        bt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv1.setText("帮助信息");
                tv2.setText("红色是当前状态下不能使用的功能.\n黄色是当前状态下只可以使用部分功能,剩下部分则不能使用.\n默认绿色则是代表当前状态可以完美正常使用该功能.\n\n" +
                        "FQAOSP(中文全称为: 法可油安卓),它是一个适用于类原生的搞机工具，同样也适用于国内定制ui系统，它拥有很多常用的功能，例如：后台清理、一键卸载与安装应用、安装某个指定的文件夹里面所有apk文件、将手机本地的pe镜像文件挂载给电脑重装系统、反/回编译软件、提取或者刷入系统分区文件、软件的备份与恢复、应用分身、共享手机本地文件给局域网内所有用户、搜索自己设定范围内的文件等等，未来还会加入更多功能，现在部分功能已经可以不再需要root，已经对接了shizuku，但是仍有部分需要root才能使用，后续会逐渐完善与shizuku的对接。\r\n" +
                        "如果有新功能或建议，可以在GitHub提issue！\r\n" +
                        "\n" +
                        "1.加入小工具集合(同步设置国内标准时间/设置设备刷新率/开启墓碑模式).\n" +
                        "2.在应用管理界面加入应用待机与强制应用打盹配置选项.\n" +
                        "3.修改版本号为V1.3.1\n");
                amupdate.setVisibility(View.VISIBLE);
                dl.closeDrawer(Gravity.LEFT);
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                jump(activity, menuEntities.get(i).getClassz());
                return false;
            }
        });

        amupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                netUtils ddd = new netUtils();
                PackageManager pm = context.getPackageManager();
                try {
                    //获取当前应用版本号
                    PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
                    versioncode=pi.versionCode;
                } catch (PackageManager.NameNotFoundException e) {
                    setTV2Text(tv2,e);
                    Log.e("PKGMFQAOSP",e.toString());
                }

                String myStorageHomePath = getMyStorageHomePath(activity);
                String outDir = myStorageHomePath + "/cache/update";
                File file = new File(outDir);
                if(!file.exists()){
                    file.mkdirs();
                }
                //如果有更新，那就直接获取更新日志与下载连接
                if(haveupdate){
                    AlertDialog show = showMyDialog(MainActivity.this,"提示","正在获取更新内容,请稍后(可能会出现无响应，请耐心等待)....");
                    AlertDialog.Builder ab = new AlertDialog.Builder(context);
                    View view2 = activity.getLayoutInflater().inflate(R.layout.update_activity, null);
                    TextView updateatv1 = view2.findViewById(R.id.updateatv1);
                    TextView updateatv2 = view2.findViewById(R.id.updateatv2);
                    Button updateabt1 = view2.findViewById(R.id.updateabt1);
                    ab.setView(view2);
                    ab.setTitle("软件更新");
                    AlertDialog alertDialog = ab.create();
                    Handler handler = new Handler(){
                        @Override
                        public void handleMessage(@NonNull Message msg) {
                            if(msg.what==0){
                                multiFunc.dismissDialog(show);
                                alertDialog.show();
                            }
                        }
                    };
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Looper.prepare();
                            String html = null;
                            try {
                                //获取更新日志
                                html = ddd.getHTML(updatelog);
                                String byString = getByString(html, "# V(.+)", "#|\\s+");
                                updateatv1.setText(byString);
                                updateatv2.setText(html);
                                updateabt1.setText("有新版本可以用,点击马上更新!!!");
                            } catch (IOException e) {
                                setTV2Text(tv2,e);
                                Log.e("UPFQAOSP",e.toString());
                            }

                            sendHandlerMSG(handler,0);
                        }
                    }).start();
                    updateabt1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String filename = "app-release.apk";
                            String filename2 = "fqaosp.apk";
                            String path = outDir+"/"+filename2;
                            String s = updateatv1.getText().toString().replaceAll("\\s+","");
                            //拼接下载连接
                            updatefile=updatefile+s+"/"+filename;
                            Log.d("url",updatefile);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        try{
                                            ddd.downLoad(updatefile,outDir,filename2);
                                            Log.d(filename2,"download ok");
                                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                                boolean isGranted = getPackageManager().canRequestPackageInstalls();
                                                if(!isGranted){
                                                    Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
                                                    startActivityForResult(intent, 98);
                                                }
                                            }else{
                                                if(!(ActivityCompat.checkSelfPermission(context, Manifest.permission.REQUEST_INSTALL_PACKAGES) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(context, Manifest.permission.INSTALL_PACKAGES) == PackageManager.PERMISSION_GRANTED)){
                                                    Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
                                                    startActivityForResult(intent, 98);
                                                }
                                            }
                                            if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.N){
                                                /**
                                                 *android1.x-6.x
                                                 *@param path 文件的路径
                                                 */
                                                Intent install = new Intent(Intent.ACTION_VIEW);
                                                install.setDataAndType(Uri.parse("file://" + path), "application/vnd.android.package-archive");
                                                install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                context.startActivity(install);
                                            }else{
                                                //参数1 上下文, 参数2 在AndroidManifest中的android:authorities值, 参数3  共享的文件
                                                Uri apkUri = FileProvider.getUriForFile(context, "org.fqaosp.provider", new File(path));
                                                Intent install = new Intent(Intent.ACTION_VIEW);
                                                //由于没有在Activity环境下启动Activity,设置下面的标签
                                                install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                //添加这一句表示对目标应用临时授权该Uri所代表的文件
                                                install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                                install.setDataAndType(apkUri, "application/vnd.android.package-archive");
                                                startActivity(install);
                                            }
                                        }catch (Exception e){
                                            setTV2Text(tv2,e);
                                            Log.d("DLERROR",e.toString());
                                        }

                                    } catch (Exception e) {
                                        setTV2Text(tv2,e);
                                        Log.d("MERROR",e.toString());
                                    }
                                }
                            }).start();
                        }
                    });

                }else{
                    AlertDialog.Builder ab = new AlertDialog.Builder(context);
                    View view2 = activity.getLayoutInflater().inflate(R.layout.check_update_activity, null);
                    RadioGroup rg = view2.findViewById(R.id.auarg);
                    rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup radioGroup, int i) {
                            int childCount = radioGroup.getChildCount();
                            for (int i1 = 0; i1 < childCount; i1++) {
                                RadioButton radioButton  = (RadioButton) radioGroup.getChildAt(i1);
                                if(radioButton.isChecked()){
                                    if(i1 != 0){
                                        updatehost="https://github.com/MrsEWE44/FQAOSP/";
                                        updateurl="https://raw.githubusercontent.com/MrsEWE44/FQAOSP/master/app/build.gradle";
                                        updatelog="https://raw.githubusercontent.com/MrsEWE44/FQAOSP/master/README.md";
                                        updatefile=updatehost+"releases/download/";
                                    }
                                }
                            }

                        }
                    });
                    ab.setView(view2);
                    ab.setTitle("选择检测节点");
                    ab.setNegativeButton("检测", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            AlertDialog show = showMyDialog(MainActivity.this,"提示","正在检查更新,请稍后(可能会出现无响应，请耐心等待)....");
                            Handler handler = dismissDialogHandler(0,show);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Looper.prepare();
                                    String html = "";
                                    try{
                                        html = ddd.getHTML(updateurl);
                                        String byString = getByString(html, "versionCode(.+?\\d+)", "versionCode|\\s+");
                                        int fetchvercode=Integer.parseInt(byString.replaceAll("\\n",""));
                                        if(versioncode < fetchvercode){
                                            haveupdate=true;
                                            amupdate.setText("检测到更新,请再点击一次检测更新，获取详细更新日志");
                                        }else{
                                            haveupdate=false;
                                            amupdate.setText("没有检测到更新");
                                        }
                                    }catch (Exception e){
                                        setTV2Text(tv2,e);
                                        Log.e("MFQAOSP",e.toString());
                                    }
                                    dialogInterface.dismiss();
                                    sendHandlerMSG(handler,0);
                                }
                            }).start();
                        }
                    });
                    ab.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    ab.create().show();
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * <p>
     * 进行字符串正则提取
     */
    public String getByString(String src, String regex, String re_str) {
        StringBuilder tmp = new StringBuilder();
        Matcher m = Pattern.compile(regex).matcher(src);
        if (m.find()) {
            tmp.append(m.group().replaceAll(re_str, "") + "\n");
        }
        return tmp.toString();
    }

    public void setTV2Text(TextView tv2, Exception e){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv2.setText(e.toString());
            }
        });
    }
}