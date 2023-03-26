package org.fqaosp.myActivitys;

import static org.fqaosp.utils.fileTools.extactAssetsFile;
import static org.fqaosp.utils.fileTools.getMyHomeFilesPath;
import static org.fqaosp.utils.fileTools.getMyStorageHomePath;
import static org.fqaosp.utils.fileTools.writeDataToPath;
import static org.fqaosp.utils.multiFunc.dismissDialogHandler;
import static org.fqaosp.utils.multiFunc.preventDismissDialog;
import static org.fqaosp.utils.multiFunc.sendHandlerMSG;
import static org.fqaosp.utils.multiFunc.showImportToolsDialog;
import static org.fqaosp.utils.multiFunc.showInfoMsg;
import static org.fqaosp.utils.multiFunc.showMyDialog;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

/**
 *
 * ROM解包/打包工具
 *
 * */

public class romToolsActivity extends AppCompatActivity {
    private ArrayList<View> views = new ArrayList<>();
    private ArrayList<String> slist = new ArrayList<>();
    private ArrayList<String> list = new ArrayList<>();
    private ArrayList<Boolean> checkboxs = new ArrayList<>();
    private ViewPager rtavp;
    private View romUnpackView, romRepackView;
    private ListView rualv1,rralv1;
    private int rom_img_index=-1,rom_img_index2=-1,rom_img_index2_ver=-1,viewPageIndex=0;
    private String rom_imgs[] = {"system.new.dat","product.new.dat","vendor.new.dat","system.new.dat.br","product.new.dat.br","vendor.new.dat.br","payload.bin","super.img"};
    private String rom_imgs2[] = {"system.new.dat","system.new.dat.br"};
    private String rom_imgs2_ver[] = {"5.0","5.1","6.x","7.x+"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rom_tools_activity);
        fuckActivity.getIns().add(this);
        setTitle("ROM工具");
        String filesDir =getMyHomeFilesPath(this);
        String fqtoolsfile = filesDir+"/fqtools.sh";
        String fqtoolsusr = filesDir+"/usr";
        File fqtoolsusrdir = new File(fqtoolsusr);
        if(extractAssertFile(fqtoolsfile,filesDir)){
            Toast.makeText(this, "fqtools脚本已存在", Toast.LENGTH_SHORT).show();
        }else{
            showImportToolsDialog(this,"fqtools脚本无法获取，请退出重试或者重新安装app","fqtools没有找到,部分功能使用将受到限制或者异常,要继续使用吗？");
        }
        if(!fqtoolsusrdir.exists()){
            showImportToolsDialog(this,"fqtools核心无法获取，请退出重试或者重新安装app","fqtools工具包没有找到,功能使用将受到限制或者异常,要继续使用吗？");
        }
        initViews();
    }

    private void initViews() {
        rtavp = findViewById(R.id.rtavp);
        romUnpackView = getLayoutInflater().inflate(R.layout.rom_unpack_activity, null);
        romRepackView = getLayoutInflater().inflate(R.layout.rom_repack_activity, null);
        views.add(romUnpackView);
        views.add(romRepackView);
        slist.add("ROM文件解包");
        slist.add("ROM文件打包");
        FILESHARINGVIEWPAGERAdapter adapter = new FILESHARINGVIEWPAGERAdapter(views, slist);
        rtavp.setAdapter(adapter);
        initOnListen();
        initRomUnpackView();
        initRomRepackView();
    }

    private void initRomRepackView() {
        Context context = this;
        Button rrab1 = romRepackView.findViewById(R.id.rrab1);
        Button rrab2 = romRepackView.findViewById(R.id.rrab2);
        Spinner rrasp1 = romRepackView.findViewById(R.id.rrasp1);
        Spinner rrasp2 = romRepackView.findViewById(R.id.rrasp2);
        rralv1 = romRepackView.findViewById(R.id.rralv1);
        rrasp1.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, rom_imgs2));
        rrasp2.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, rom_imgs2_ver));

        rrasp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                rom_img_index2 = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        rrasp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                rom_img_index2_ver = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        rrab1.setOnClickListener((v)->{
            AlertDialog show = showMyDialog(context,"提示","正在扫描本地IMG镜像文件,请稍后(可能会出现无响应，请耐心等待)....");
            preventDismissDialog(show);
            Handler handler = new Handler(){
                @Override
                public void handleMessage(@NonNull Message msg) {
                    if(msg.what==0){
                        showRomImgs(rralv1);
                        multiFunc.dismissDialog(show);
                    }
                }
            };
            v.post(new Runnable() {
                @Override
                public void run() {
                    findROMImgs();
                    sendHandlerMSG(handler,0);
                }
            });

        });

        rrab2.setOnClickListener((v)->{
            for (int i = 0; i < checkboxs.size(); i++) {
                if(checkboxs.get(i)){
                    String path = list.get(i);
                    String storage = context.getExternalCacheDir().toString();
                    String outdir = storage+"/romrepack/"+System.currentTimeMillis();
                    String filesDir = getMyHomeFilesPath(context);
                    File file = new File(outdir);
                    if(!file.exists()){
                        file.mkdirs();
                    }
                    AlertDialog show = showMyDialog(context,"提示","正在打包本地IMG镜像文件,如果是br类型,可能会需要更久的时间,请稍后(可能会出现无响应，请耐心等待，该软件不支持后台)....");
                    preventDismissDialog(show);
                    Handler handler = dismissDialogHandler(0,show);
                    v.post(new Runnable() {
                        @Override
                        public void run() {
                            String cmdstr = "cd "+filesDir+" && sh fqtools.sh repackrom "+path+ " " + outdir + " "  +getRomType() + " " + (rom_img_index2_ver+1);
                            CMD cmd = new CMD(cmdstr,false);
//                            Log.d("unpackrom",cmd.getResult());
                            if(cmd.getResultCode()==0){
                                showInfoMsg(context,"提示",cmd.getResult()+"\r\n\r\n打包成功,文件存放在 "+outdir);
                            }else{
                                writeDataToPath(cmd.getResultCode()+" -- " +cmd.getResult(),outdir+".log",false);
                                showInfoMsg(context,"错误","打包失败,日志存放在 >>  "+outdir);
                            }
                            sendHandlerMSG(handler,0);
                        }
                    });
                }
            }
        });
    }

    private void initRomUnpackView() {
        Context context = this;
        Button ruab1 = romUnpackView.findViewById(R.id.ruab1);
        Button ruab2 = romUnpackView.findViewById(R.id.ruab2);
        Spinner ruasp1 = romUnpackView.findViewById(R.id.ruasp1);
        rualv1 = romUnpackView.findViewById(R.id.rualv1);
        ruasp1.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, rom_imgs));
        ruasp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                rom_img_index = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ruab1.setOnClickListener((v)->{
            AlertDialog show = showMyDialog(context,"提示","正在扫描本地ROM镜像文件,请稍后(可能会出现无响应，请耐心等待)....");
            preventDismissDialog(show);
            Handler handler = new Handler(){
                @Override
                public void handleMessage(@NonNull Message msg) {
                    if(msg.what==0){
                        showRomImgs(rualv1);
                        multiFunc.dismissDialog(show);
                    }
                }
            };
            v.post(new Runnable() {
                @Override
                public void run() {
                    findROMImgs();
                    sendHandlerMSG(handler,0);
                }
            });

        });

        ruab2.setOnClickListener((v)->{
            for (int i = 0; i < checkboxs.size(); i++) {
                if(checkboxs.get(i)){
                    String path = list.get(i);
                    String storage = context.getExternalCacheDir().toString();
                    String outdir = storage+"/romunpack/"+System.currentTimeMillis();
                    String filesDir = getMyHomeFilesPath(context);
                    File file = new File(outdir);
                    if(!file.exists()){
                        file.mkdirs();
                    }
                    AlertDialog show = showMyDialog(context,"提示","正在解包本地ROM镜像文件,请稍后(可能会出现无响应，请耐心等待)....");
                    preventDismissDialog(show);
                    Handler handler = dismissDialogHandler(0,show);
                    v.post(new Runnable() {
                        @Override
                        public void run() {
                            String cmdstr = "cd "+filesDir+" && sh fqtools.sh unpackrom "+getRomType() + " " +path + " " + outdir + " " + getRomPartType();
                            CMD cmd = new CMD(cmdstr,false);
//                            Log.d("unpackrom",cmd.getResult());
                            if(cmd.getResultCode()==0){
                                showInfoMsg(context,"提示",cmd.getResult()+"\r\n\r\n解包成功,文件存放在 "+outdir);
                            }else{
                                writeDataToPath(cmd.getResultCode()+" -- " +cmd.getResult(),outdir+".log",false);
                                showInfoMsg(context,"错误","解包失败,日志存放在 >>  "+outdir);
                            }
                            sendHandlerMSG(handler,0);
                        }
                    });
                }
            }
        });

    }

    private void initOnListen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            rtavp.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                    viewPageIndex = rtavp.getCurrentItem();
                }
            });
        }
    }

    private String getRomType(){
        if(viewPageIndex == 0){
            switch (rom_img_index){
                case 0:
                case 1:
                case 2:
                    return "sndat";
                case 3:
                case 4:
                case 5:
                    return "sndatbr";
                case 6:
                    return "paybin";
                case 7:
                    return "super";
            }
        }else if(viewPageIndex == 1){
            switch (rom_img_index2){
                case 0:
                    return "sdat";
                case 1:
                    return "sdatbr";
            }
        }

        return null;
    }

    private String getRomPartType(){
        switch (rom_img_index){
            case 0:
            case 3:
                return "system";
            case 1:
            case 4:
                return "product";
            case 2:
            case 5:
                return "vendor";
        }
        return null;
    }

    private void findROMImgs(){
        list.clear();
        checkboxs.clear();
        String storage = Environment.getExternalStorageDirectory().toString();
        String myStorageHomePath = getMyStorageHomePath(this);
        String cmdstr = "find " + storage + "/ -path \""+storage+"/Android\" -prune -o -name  '"+rom_imgs[rom_img_index]+"' -print";
        String cmdstr2 = "find " + storage + "/ -path \""+storage+"/Android\" -prune -o -name  '*.img' -print && find "+myStorageHomePath+"/ -name '*.img' -print";
        CMD cmd = new CMD(viewPageIndex==0? cmdstr:cmdstr2,false);
        for (String s1 : cmd.getResult().split("\n")) {
            list.add(s1);
            checkboxs.add(false);
        }
    }

    private void showRomImgs(ListView listView){
        USERAdapter userAdapter = new USERAdapter(list, romToolsActivity.this, checkboxs);
        listView.setAdapter(userAdapter);
    }

    private Boolean extractAssertFile(String sysupfile,String filesDir){
        File sysupF = new File(sysupfile);
        File fileD = new File(filesDir);
        if(!fileD.exists()){
            fileD.mkdirs();
        }
        if(!sysupF.exists()){
            extactAssetsFile(this,"fqtools.sh",sysupfile);
        }
        return sysupF.exists();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE,0,0,"帮助");
        menu.add(Menu.NONE,1,1,"退出");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId){
            case 0:
                if(viewPageIndex == 0){
                    showInfoMsg(this,"帮助信息","该功能主要是用于rom包解包，支持payload.bin/system.new.dat/system.new.dat.br/super.img等文件解包。\r\n" +
                            "1.选择左边下拉栏的文件类型,然后点击右边的扫描选项，即会开始扫描本地匹配的选项。\r\n" +
                            "2.搜索出结果后，会在下面显示，勾选左边选框再点击解包，就会开始解包当前镜像类型，支持批量解包.\r\n" +
                            "3.注意，一定要保持rom包本来的结构再使用这个工具，目前还处于测试阶段，后续会逐渐完善.\r\n"
                    );
                }else{
                    showInfoMsg(this,"帮助信息","该功能主要是用于本地IMG打包，暂时支持system.new.dat/system.new.dat.br文件格式打包。\r\n" +
                            "1.点击右边的扫描即会开始扫描本地所有匹配的img文件,并在下面列出所有匹配项。\r\n" +
                            "2.左边下拉框的内容是目前支持的打包格式,例如:system.new.dat，默认会将勾选中的目标img文件打包成system.new.dat类型，下面的br类型同理.\r\n" +
                            "3.右边下拉框的内容是设置打包的安卓版本,支持安卓5.x至安卓7以上.\r\n" +
                            "4.点击打包，则会将勾选中的全部按顺序开始进行打包操作.如果你是选择的br类型，那么可能会需要等待的久一些.br压缩率高,但是消耗的时间也很多\r\n"
                    );
                }

                break;
            case 1:
                fuckActivity.getIns().killall();
                ;
        }
        return super.onOptionsItemSelected(item);
    }

}
