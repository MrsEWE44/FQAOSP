package org.fqaosp.myActivitys;

import static org.fqaosp.utils.fileTools.execFileSelect;
import static org.fqaosp.utils.fileTools.getMyHomeFilesPath;
import static org.fqaosp.utils.fileTools.getMyStorageHomePath;
import static org.fqaosp.utils.fileTools.getPathByLastName;
import static org.fqaosp.utils.fileTools.selectFile;
import static org.fqaosp.utils.multiFunc.jump;
import static org.fqaosp.utils.multiFunc.preventDismissDialog;
import static org.fqaosp.utils.multiFunc.showInfoMsg;
import static org.fqaosp.utils.multiFunc.showMyDialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import org.fqaosp.R;
import org.fqaosp.adapter.FILESHARINGVIEWPAGERAdapter;
import org.fqaosp.adapter.USERAdapter;
import org.fqaosp.threads.alertDialogThread;
import org.fqaosp.utils.CMD;
import org.fqaosp.utils.fuckActivity;
import org.fqaosp.utils.multiFunc;
import org.fqaosp.utils.permissionRequest;

import java.io.File;
import java.util.ArrayList;

public class imgToolMenuActivity extends AppCompatActivity {

    private ViewPager itmavp;
    private ArrayList<View> views = new ArrayList<>();
    private ArrayList<String> slist = new ArrayList<>();
    private View unpackView, repackView;
    private ArrayList<String> list = new ArrayList<>();
    private ArrayList<Boolean> checkboxs = new ArrayList<>();
    private ListView itualv, itralv;
    private Integer viewPageIndex = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.img_tool_menu_activity);
        fuckActivity.getIns().add(this);
        setTitle("镜像工具");
        initViews();
        extractAssetsFiles();
    }

    private void initViews() {
        itmavp = findViewById(R.id.itmavp);
        unpackView = getLayoutInflater().inflate(R.layout.img_tool_unpack_activity, null);
        repackView = getLayoutInflater().inflate(R.layout.img_tool_repack_activity, null);
        views.add(unpackView);
        views.add(repackView);
        slist.add("解包");
        slist.add("打包");
        FILESHARINGVIEWPAGERAdapter adapter = new FILESHARINGVIEWPAGERAdapter(views, slist);
        itmavp.setAdapter(adapter);
        initOnListen();
        initUnpackView();
        initRepackView();
    }

    private void initOnListen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            itmavp.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                    viewPageIndex = itmavp.getCurrentItem();
                }
            });
        }
    }

    private void extractAssetsFiles() {
        try {
            String filesDir = getMyHomeFilesPath(imgToolMenuActivity.this);
            String mkbootimgFile = filesDir + "/mkbootimg";
            String unpackbootimgFile = filesDir + "/unpackbootimg";
            File file1 = new File(filesDir);
            File mkbootimgF = new File(mkbootimgFile);
            File unpackbootimgF = new File(unpackbootimgFile);
            if (!file1.exists()) {
                file1.mkdirs();
            }
            if (!mkbootimgF.exists()) {
                Toast.makeText(this, "未找到mkbootimg，请重新导入工具包后再执行此项", Toast.LENGTH_LONG).show();
                jump(imgToolMenuActivity.this, importToolsActivity.class);
            } else if (!unpackbootimgF.exists()) {
                Toast.makeText(this, "未找到unpackbootimg，请重新导入工具包后再执行此项", Toast.LENGTH_LONG).show();
                jump(imgToolMenuActivity.this, importToolsActivity.class);
            }
            String cmd = "cd " + filesDir + " && [ -f mkbootimg ] && [ -f unpackbootimg ] ";
            alertDialogThread dialogThread = new alertDialogThread(imgToolMenuActivity.this, "请稍后，正在解压镜像工具相关资源文件", cmd, "提示", "工具已存在", "解压失败");
            dialogThread.start();
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    //初始化解包界面与功能
    private void initUnpackView() {
        Button ituab1 = unpackView.findViewById(R.id.ituab1);
        Button ituab2 = unpackView.findViewById(R.id.ituab2);
        Button ituab3 = unpackView.findViewById(R.id.ituab3);
        itualv = unpackView.findViewById(R.id.itualv1);
        Context context = this;
        Activity activity = this;
        permissionRequest.getExternalStorageManager(context);
        ituab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mystoragehome = getMyStorageHomePath(context);
                String filesPath = getMyHomeFilesPath(context);
                for (int i = 0; i < checkboxs.size(); i++) {
                    if (checkboxs.get(i)) {
                        String s = list.get(i);
                        String name = getPathByLastName(s).replaceAll(".img", "");
                        String outPath = mystoragehome + "/files/unpack/" + name;
                        File file = new File(outPath);
                        if (!file.exists()) {
                            file.mkdirs();
                        }
                        String cmd = "cd " + filesPath + " && sh unpack.sh " + s + " " + outPath;
                        alertDialogThread dialogThread = new alertDialogThread(context, "正在解包 " + name + " ...", cmd, "提示", "解包成功 " + outPath, "解包失败");
                        dialogThread.start();
                    }
                }
            }
        });

        ituab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        getLocalImgs();
                        showImgs(itualv);
                    }
                });

            }
        });

        ituab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                execFileSelect(context, activity, "请选择 .img 文件");
            }
        });
    }

    //初始化打包界面与功能
    private void initRepackView() {
        Context context = this;
        Activity activity = this;
        Button itrab1 = repackView.findViewById(R.id.itrab1);
        Button itrab2 = repackView.findViewById(R.id.itrab2);
        Button itrab3 = repackView.findViewById(R.id.itrab3);
        itralv = repackView.findViewById(R.id.itralv1);
        itrab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < checkboxs.size(); i++) {
                    if (checkboxs.get(i)) {
                        String s = list.get(i);
                        String name = new File(s).getName();
                        String mystoragehome = getMyStorageHomePath(context);
                        String filesPath = getMyHomeFilesPath(context);
                        String outPath = mystoragehome + "/files/repack/" + name;
                        String outName = outPath + "/" + name;
                        File file = new File(outPath);
                        if (!file.exists()) {
                            file.mkdirs();
                        }
                        String cmd = "cd " + filesPath + " && sh repack.sh " + s + " " + outName + " " + name;

                        alertDialogThread dialogThread = new alertDialogThread(context, "正在打包 " + name + " ...", cmd, "提示", "打包成功 " + outName, "打包失败");
                        dialogThread.start();
                    }
                }
            }
        });

        itrab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        getDefaultImgProject();
                        showImgs(itralv);
                    }
                });
            }
        });

        itrab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                execFileSelect(context, activity, "请选择 boot.txt 文件");
            }
        });

    }

    //扫描应用默认待打包项目路径
    private void getDefaultImgProject() {
        clearList();
        String homePath = getMyStorageHomePath(imgToolMenuActivity.this);
        AlertDialog show = showMyDialog(imgToolMenuActivity.this, "提示", "请稍后，正在扫描本地镜像工程...");
        preventDismissDialog(show);
        String cmd = "find " + homePath + "/files/unpack -type d";
        CMD cmd1 = new CMD(cmd);
        if (cmd1.getResultCode() == 0) {
            for (String s : cmd1.getResult().split("\n")) {
                String name = getPathByLastName(s);
                if (!name.equals("unpack")) {
                    list.add(s);
                    checkboxs.add(false);
                }
            }
            Toast.makeText(this, "扫描成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "扫描失败，请确认当前应用拥有相关存储权限", Toast.LENGTH_SHORT).show();
        }
        multiFunc.dismissDialog(show);
    }

    //扫描本地所有img文件
    private void getLocalImgs() {
        clearList();
        String storage = Environment.getExternalStorageDirectory().toString();
        AlertDialog show = showMyDialog(imgToolMenuActivity.this, "提示", "请稍后，正在扫描本地镜像文件...");
        preventDismissDialog(show);
        String cmd = "find " + storage + "/ -name '*.img'";
        CMD cmd1 = new CMD(cmd);
        if (cmd1.getResultCode() == 0) {
            for (String s : cmd1.getResult().split("\n")) {
                list.add(s);
                checkboxs.add(false);
            }
            Toast.makeText(this, "扫描成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "扫描失败，请确认当前应用拥有相关存储权限", Toast.LENGTH_SHORT).show();
        }
        multiFunc.dismissDialog(show);
    }

    private void showImgs(ListView listView) {
        USERAdapter userAdapter = new USERAdapter(list, imgToolMenuActivity.this, checkboxs);
        listView.setAdapter(userAdapter);
    }

    private void clearList() {
        list.clear();
        checkboxs.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, 0, 0, "帮助");
        menu.add(Menu.NONE, 1, 1, "退出");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        switch (viewPageIndex) {
            case 0:
                switch (itemId) {
                    case 0:
                        showInfoMsg(this, "帮助信息", "该页面是用于recovery/boot镜像文件解包，需要安装fqtools,如果没有安装，则会自动跳转安装页面，按照页面提示安装即可。\r\n" +
                                "1.扫描本地镜像文件，会列出本地所有带.img文件后缀的文件。\r\n" +
                                "2.选择本地镜像文件，通过文件选择器，选中你想要进行解包的镜像文件.\r\n" +
                                "3.开始解包镜像，开始解包.\r\n"
                        );
                        break;
                    case 1:
                        fuckActivity.getIns().killall();
                        ;
                }
                break;
            case 1:
                switch (itemId) {
                    case 0:
                        showInfoMsg(this, "帮助信息", "该页面是用于recovery/boot镜像文件打包，需要安装fqtools,如果没有安装，则会自动跳转安装页面，按照页面提示安装即可。\r\n" +
                                "1.扫描默认解包路径，会列出通过该软件解包后的项目工程名称（推荐）。\r\n" +
                                "2.选择本地解包文件夹，必须是通过该软件解包后的工程才行，其它的会报错，但是这个路径可以自定义.\r\n" +
                                "3.开始打包镜像，开始打包.\r\n"
                        );
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
        Context context = this;
        if (resultCode == Activity.RESULT_OK) {
            clearList();
            String storage = Environment.getExternalStorageDirectory().toString();
            String msg = null, eq = null;
            if (viewPageIndex == 0) {
                msg = "请选择正确的img文件";
                eq = "img";
            }
            if (viewPageIndex == 1) {
                msg = "请选择正确的boot.txt文件";
                eq = "txt";
            }
            if (data.getClipData() != null) {//有选择多个文件
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    Uri uri = data.getClipData().getItemAt(i).getUri();
                    selectFile(context, storage, uri, list, checkboxs, msg, eq);
                }
            } else if (data.getData() != null) {//只有一个文件咯
                Uri uri = data.getData();
                selectFile(context, storage, uri, list, checkboxs, msg, eq);
            }
            if (viewPageIndex == 0) {
                showImgs(itualv);
            }
            if (viewPageIndex == 1) {
                showImgs(itralv);
            }
        }
    }

}
