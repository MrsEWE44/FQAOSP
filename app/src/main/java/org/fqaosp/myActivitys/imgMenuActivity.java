package org.fqaosp.myActivitys;

import static org.fqaosp.utils.fileTools.getMyStorageHomePath;
import static org.fqaosp.utils.multiFunc.dismissDialogHandler;
import static org.fqaosp.utils.multiFunc.getCMD;
import static org.fqaosp.utils.multiFunc.isSuEnable;
import static org.fqaosp.utils.multiFunc.preventDismissDialog;
import static org.fqaosp.utils.multiFunc.sendHandlerMSG;
import static org.fqaosp.utils.multiFunc.showInfoMsg;
import static org.fqaosp.utils.multiFunc.showMyDialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import org.fqaosp.R;
import org.fqaosp.adapter.FILESHARINGVIEWPAGERAdapter;
import org.fqaosp.adapter.USERAdapter;
import org.fqaosp.utils.CMD;
import org.fqaosp.utils.fuckActivity;
import org.fqaosp.utils.multiFunc;

import java.io.File;
import java.util.ArrayList;

public class imgMenuActivity extends AppCompatActivity {

    private ViewPager admavp;
    private ArrayList<View> views = new ArrayList<>();
    private ArrayList<String> slist = new ArrayList<>();
    private Integer viewPageIndex = 0;
    private View dumpImgView, flashImgView;
    private ListView dumpLv1, flashLv1,flashLv2;
    private ArrayList<String> dumpList = new ArrayList<>();
    private ArrayList<String> flashList1 = new ArrayList<>();
    private ArrayList<String> flashList2 = new ArrayList<>();
    private ArrayList<Boolean> dumpCheckboxs = new ArrayList<>();
    private ArrayList<Boolean> flashCheckboxs1 = new ArrayList<>();
    private ArrayList<Boolean> flashCheckboxs2 = new ArrayList<>();
    private Boolean switchBool1,switchBool2,switchBool3;
    private Context context;
    private boolean isRoot = false;
    private String bootdev="/dev/block/bootdevice";
    private String mapperdev="/dev/block/mapper";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.apk_decompile_menu_activity);
        fuckActivity.getIns().add(this);
        setTitle("分区管理");
        isRoot=isSuEnable();
        if(isRoot){
            initViews();
        }else{
            showMyDialog(this,"提示","本功能需要root才能正常使用");
        }
    }

    private void initViews() {
        context=this;
        admavp = findViewById(R.id.admavp);
        dumpImgView = getLayoutInflater().inflate(R.layout.dump_img_activity, null);
        flashImgView = getLayoutInflater().inflate(R.layout.flash_img_activity, null);
        views.add(dumpImgView);
        views.add(flashImgView);
        slist.add("分区提取");
        slist.add("分区刷入");
        FILESHARINGVIEWPAGERAdapter adapter = new FILESHARINGVIEWPAGERAdapter(views, slist);
        admavp.setAdapter(adapter);
        initOnListen();
        initDumpImgView();
        initFlashImgView();
    }

    //初始化分区刷入布局
    private void initFlashImgView() {
        EditText editText = flashImgView.findViewById(R.id.fiaet1);
        Button b1 = flashImgView.findViewById(R.id.fiabt1);
        Button b2 = flashImgView.findViewById(R.id.fiabt2);
        flashLv1 = flashImgView.findViewById(R.id.fialv1);
        flashLv2 = flashImgView.findViewById(R.id.fialv2);
        Switch fiasb1 = flashImgView.findViewById(R.id.fiasb1);
        initBool();
        setSwitchChecked(fiasb1,null,null);
        clickedSwitchBt(fiasb1,fiasb1,null,null,0);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchStr = editText.getText().toString();
                if(switchBool1){
                    flashList2= multiFunc.indexOfLIST(flashList2,flashCheckboxs2,searchStr);
                    showIMGS(flashLv2,flashList2,flashCheckboxs2);
                }else{
                    flashList1= multiFunc.indexOfLIST(flashList1,flashCheckboxs1,searchStr);
                    showIMGS(flashLv1,flashList1,flashCheckboxs1);
                }
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder ab = new AlertDialog.Builder(context);
                ab.setTitle("警告");
                ab.setMessage("刷入分区会存在不可逆转的问题，你是否要继续尝试刷入?");
                ab.setNeutralButton("继续", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AlertDialog show = showMyDialog(context,"提示","正在刷入,请稍后(可能会出现无响应，请耐心等待)....");
                        Handler handler = new Handler(){
                            @Override
                            public void handleMessage(@NonNull Message msg) {
                                if(msg.what==0){
                                    multiFunc.dismissDialog(show);
                                    showInfoMsg(context,"信息",msg.obj.toString());
                                }
                            }
                        };
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                String localImgPath = null,partname = null;
                                for (int i1 = 0; i1 < flashCheckboxs1.size(); i1++) {
                                    if(flashCheckboxs1.get(i1)){
                                        localImgPath=flashList1.get(i1);
                                        break;
                                    }
                                }
                                for (int j = 0; j < flashCheckboxs2.size(); j++) {
                                    if(flashCheckboxs2.get(j)){
                                        partname=flashList2.get(j);
                                        break;
                                    }
                                }
                                String mpper=mapperdev+"/"+partname;
                                String booter=bootdev+"/by-name/"+partname;
                                String cmdhead="dd if="+localImgPath;
                                String cmdstr = "if [ -b "+mpper+" ];then "+cmdhead+" of="+mpper+" ; elif [ -b "+booter +" ];then "+cmdhead+" of="+booter +"; else echo 'error !';fi";
                                CMD cmd = getCMD(context, cmdstr, true);
                                sendHandlerMSG(handler,0,"分区已刷入完毕: \r\n"+localImgPath+"  ------>>>>>   "+partname+"\r\n\r\n"+cmd.getResultCode()+" -- " + cmd.getResult());
                            }
                        }).start();
                    }
                });
                ab.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
               ab.create().show();
            }
        });

    }

    //初始化分区提取布局
    private void initDumpImgView() {
        EditText editText = dumpImgView.findViewById(R.id.diaet1);
        Button b1 = dumpImgView.findViewById(R.id.diabt1);
        Button b2 = dumpImgView.findViewById(R.id.diabt2);
        dumpLv1 = dumpImgView.findViewById(R.id.dialv1);
        Switch diasb1 = dumpImgView.findViewById(R.id.diasb1);
        Switch diasb2 = dumpImgView.findViewById(R.id.diasb2);
        Switch diasb3 = dumpImgView.findViewById(R.id.diasb3);
        initBool();
        setSwitchChecked(diasb1,diasb2,diasb3);
        clickedSwitchBt(diasb1,diasb2,diasb3);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchStr = editText.getText().toString();
                dumpList= multiFunc.indexOfLIST(dumpList,dumpCheckboxs,searchStr);
                showIMGS(dumpLv1,dumpList,dumpCheckboxs);
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog show = showMyDialog(context,"提示","正在提取分区,请稍后(可能会出现无响应，请耐心等待)....");
                Handler handler = dismissDialogHandler(0,show);
                String myStorageHomePath = getMyStorageHomePath(context);
                String outDir=myStorageHomePath+"/cache/dumpimg";
                File file = new File(outDir);
                if(!file.exists()){
                    file.mkdirs();
                }
                StringBuilder sb = new StringBuilder();
                sb.append("aaaa=(");

                view.post(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < dumpCheckboxs.size(); i++) {
                            String s  =dumpList.get(i);
                            if(switchBool3 && !dumpCheckboxs.get(i)){
                                sb.append("\""+s+"\" ");
                            }

                            if(switchBool2 && dumpCheckboxs.get(i)){
                                sb.append("\""+s+"\" ");
                            }

                            if(switchBool1){
                                sb.append("\""+s+"\" ");
                            }
                        }
                        String outCmd="of="+outDir+"/${pp}.img";
                        String mpper=mapperdev+"/${pp}";
                        String booter=bootdev+"/by-name/${pp}";
                        String cmdstr = "if [ -b "+mpper+" ];then dd if="+mpper+" "+outCmd+";else dd if="+booter+" "+outCmd+";fi";
                        sb.append(");for pp in ${aaaa[@]};do "+cmdstr+";done;");
                        CMD cmd = getCMD(context, sb.toString(), true);
                        sendHandlerMSG(handler,0);
                        showInfoMsg(context,"提示","已执行完毕: \r\n提取后的文件存放在 : "+outDir+"\r\n\r\n"+cmd.getResultCode()+" -- " + cmd.getResult());
                    }
                });


            }
        });

    }

    private void showIMGS(ListView lv,ArrayList<String> list,ArrayList<Boolean> checkboxs) {
        USERAdapter userAdapter = new USERAdapter(list, imgMenuActivity.this, checkboxs);
        lv.setAdapter(userAdapter);
    }

    private void clickedSwitchBt(Switch brasb1,Switch brasb2,Switch brasb3){
        clickedSwitchBt(brasb1,brasb1,brasb2,brasb3,0);
        clickedSwitchBt(brasb2,brasb1,brasb2,brasb3,1);
        clickedSwitchBt(brasb3,brasb1,brasb2,brasb3,2);
    }

    private void clickedSwitchBt(Switch orgbt , Switch brasb1,Switch brasb2,Switch brasb3,int mode){
        orgbt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                switch (mode){
                    case 0:
                        switchBool1=b;
                        switchBool2=false;
                        switchBool3=false;
                        break;
                    case 1:
                        switchBool1=false;
                        switchBool2=b;
                        switchBool3=false;
                        break;
                    case 2:
                        switchBool1=false;
                        switchBool2=false;
                        switchBool3=b;
                        break;
                }

                setSwitchChecked(brasb1,brasb2,brasb3);
            }
        });

    }

    private void setSwitchChecked(Switch brasb1,Switch brasb2,Switch brasb3){
        if(brasb1 != null){
            brasb1.setChecked(switchBool1);
        }if(brasb2 != null){
            brasb2.setChecked(switchBool2);
        }if(brasb3 != null){
            brasb3.setChecked(switchBool3);
        }
    }

    private void initBool(){
        switchBool1=false;
        switchBool2=true;
        switchBool3=false;
    }

    private void initOnListen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            admavp.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                    viewPageIndex = admavp.getCurrentItem();
                }
            });
        }
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        menu.clear();
        switch (viewPageIndex) {
            case 0:
                menu.add(Menu.NONE,0,0,"显示本机分区");
                menu.add(Menu.NONE,1,1,"帮助");
                menu.add(Menu.NONE,2,2,"退出");
                break;
            case 1:
                menu.add(Menu.NONE,0,0,"扫描本地镜像文件");
                menu.add(Menu.NONE,1,1,"显示本机分区");
                menu.add(Menu.NONE,2,2,"帮助");
                menu.add(Menu.NONE,3,3,"退出");
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
        switch (viewPageIndex) {
            case 0:
                switch (itemId){
                    case 0:
                        listLocalPartitionName();
                        break;
                    case 1:
                        showInfoMsg(context,"帮助信息","该页面是用于提取系统分区文件的，可以提取by-name下所有分区文件，需要root权限授权。\r\n" +
                                "1.点击显示本机分区，则会开始扫描/dev/block/by-name下的所有文件内容，过一会就会展示在界面。\r\n" +
                                "2.当分区列表出现数据时，你可以勾选分区名称进行提取操作。提取的数据保存在/storage/emulated/0/Android/data/org.fqaosp/cache/dumpimg路径下。\r\n" +
                                "3-1.全选，无论你是否有勾选列表的选项，都默认列表全部的内容，点击提取则会全部提取出来。\r\n"+
                                "3-1.勾选，只提取勾选中的列表选项，点击提取则只会提取勾选的内容。\r\n"+
                                "3-1.未勾选，只提取未勾选中的列表选项，点击提取则只会提取未勾选的内容。\r\n" +
                                "4.搜索,输入你想要搜索的内容，会在当前列表里面开始搜搜并将结果重载回列表里面。\r\n" +
                                "5.退出，结束程序运行，回到系统桌面。\r\n"
                        );
                        break;
                    case 2:
                        fuckActivity.getIns().killall();
                        break;
                }
                break;
            case 1:
                switch (itemId){
                    case 0:
                        listLocalImgName();
                        break;
                    case 1:
                        listLocalPartitionName();
                        break;
                    case 2:
                        showInfoMsg(context,"帮助信息","该页面是用于将本地系统分区文件刷入对应分区的，默认将本地镜像文件刷入by-name下对应的分区名称，需要root权限授权。\r\n" +
                                "1.点击扫描本地镜像文件，则会开始扫描本地的所有img或者iso文件。\r\n" +
                                "2.点击显示本机分区，则会开始扫描/dev/block/by-name下的所有文件内容，过一会就会展示在右边界面。\r\n" +
                                "3.刷入，当分区列表出现数据时，你可以勾选本地镜像文件跟分区名称进行刷入操作。只会刷入左边第一个勾选的本地文件，只会刷入右边第一个勾选的分区名称，要注意一下。\r\n" +
                                "4.非左即右，默认是搜索左边，打开则会搜索右边列表内容。\r\n"+
                                "5.搜索,输入你想要搜索的内容，会在当前列表里面开始搜搜并将结果重载回列表里面。\r\n" +
                                "6.退出，结束程序运行，回到系统桌面。\r\n"
                        );
                        break;
                    case 3:
                        fuckActivity.getIns().killall();
                        break;
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void listLocalImgName(){
        clearFlash1List();
        AlertDialog show = showMyDialog(context,"提示","正在扫描本地镜像文件,请稍后(可能会出现无响应，请耐心等待)....");
        preventDismissDialog(show);
        Handler handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                if(msg.what==0){
                    showIMGS(flashLv1,flashList1,flashCheckboxs1);
                    multiFunc.dismissDialog(show);
                }
            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                String s = Environment.getExternalStorageDirectory().toString();
                CMD cmd = new CMD("find "+s+"/ -name '*.img' -o -name '*.iso'");
                for (String s1 : cmd.getResult().split("\n")) {
                    flashList1.add(s1);
                    flashCheckboxs1.add(false);
                }
                sendHandlerMSG(handler,0);
            }
        }).start();
    }

    private void listLocalPartitionName() {
        if(viewPageIndex == 0){
            clearDumplist();
        }else{
            clearFlash2List();
        }
        AlertDialog show = showMyDialog(context,"提示","正在扫描本地分区系统,请稍后(可能会出现无响应，请耐心等待)....");
        preventDismissDialog(show);
        Handler handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                if(msg.what==0){
                    if(viewPageIndex == 0){
                        showIMGS(dumpLv1,dumpList,dumpCheckboxs);
                    }else{
                        showIMGS(flashLv2,flashList2,flashCheckboxs2);
                    }
                    multiFunc.dismissDialog(show);
                }
            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                CMD cmd = new CMD("if [ -d "+mapperdev+" ];then ls "+mapperdev+"/ && ls "+bootdev+"/by-name/ ;else ls "+bootdev+"/by-name/;fi");
                for (String s1 : cmd.getResult().split("\n")) {
                    if(viewPageIndex == 0){
                        dumpList.add(s1);
                        dumpCheckboxs.add(false);
                    }else{
                        flashList2.add(s1);
                        flashCheckboxs2.add(false);
                    }
                }
                sendHandlerMSG(handler,0);
            }
        }).start();
    }

    private void clearDumplist(){
        dumpList.clear();
        dumpCheckboxs.clear();
    }

    private void clearFlash1List(){
        flashList1.clear();
        flashCheckboxs1.clear();
    }
    private void clearFlash2List(){
        flashList2.clear();
        flashCheckboxs2.clear();
    }


}
