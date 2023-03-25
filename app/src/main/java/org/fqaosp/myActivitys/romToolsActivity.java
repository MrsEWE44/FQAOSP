package org.fqaosp.myActivitys;

import static org.fqaosp.utils.fileTools.extactAssetsFile;
import static org.fqaosp.utils.fileTools.getMyHomeFilesPath;
import static org.fqaosp.utils.fileTools.writeDataToPath;
import static org.fqaosp.utils.multiFunc.dismissDialogHandler;
import static org.fqaosp.utils.multiFunc.preventDismissDialog;
import static org.fqaosp.utils.multiFunc.sendHandlerMSG;
import static org.fqaosp.utils.multiFunc.showImportToolsDialog;
import static org.fqaosp.utils.multiFunc.showInfoMsg;
import static org.fqaosp.utils.multiFunc.showMyDialog;

import android.app.AlertDialog;
import android.content.Context;
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

import org.fqaosp.R;
import org.fqaosp.adapter.USERAdapter;
import org.fqaosp.utils.CMD;
import org.fqaosp.utils.fuckActivity;
import org.fqaosp.utils.multiFunc;

import java.io.File;
import java.util.ArrayList;

/**
 *
 * ROM解包工具
 *
 * */

public class romToolsActivity extends AppCompatActivity {

    private ArrayList<String> list = new ArrayList<>();
    private ArrayList<Boolean> checkboxs = new ArrayList<>();
    private Button rtab1,rtab2;
    private ListView rtalv1;
    private Spinner rtasp1;
    private int rom_img_index=-1;
    private String rom_imgs[] = {"system.new.dat","product.new.dat","vendor.new.dat","system.new.dat.br","product.new.dat.br","vendor.new.dat.br","payload.bin","super.img"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rom_tools_activity);
        fuckActivity.getIns().add(this);
        setTitle("ROM解包");
        initBt();
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
    }

    private void initBt(){
        rtab1 = findViewById(R.id.rtab1);
        rtab2 = findViewById(R.id.rtab2);
        rtalv1 = findViewById(R.id.rtalv1);
        rtasp1 = findViewById(R.id.rtasp1);
        rtasp1.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, rom_imgs));

        clickBt();
    }

    private String getRomType(){
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

    private void clickBt(){
        Context context = this;

        rtasp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                rom_img_index = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        rtab1.setOnClickListener((v)->{
            if(rom_img_index>-1){
                AlertDialog show = showMyDialog(context,"提示","正在扫描本地ROM镜像文件,请稍后(可能会出现无响应，请耐心等待)....");
                preventDismissDialog(show);
                Handler handler = new Handler(){
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        if(msg.what==0){
                            showRomImgs(rtalv1);
                            multiFunc.dismissDialog(show);
                        }
                    }
                };
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        findROMImgs();
                        sendHandlerMSG(handler,0);
                    }
                }).start();
            }else{
                Toast.makeText(context, "请选择一个ROM文件类型", Toast.LENGTH_SHORT).show();
            }
            
        });

        rtab2.setOnClickListener((v)->{
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
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String cmdstr = "cd "+filesDir+" && sh fqtools.sh unpackrom "+getRomType() + " " +path + " " + outdir + " " + getRomPartType();
                            CMD cmd = new CMD(cmdstr,false);
                            if(cmd.getResultCode()==0){
                                Toast.makeText(context, "解包成功,保存在"+outdir, Toast.LENGTH_SHORT).show();
                            }else{
                                writeDataToPath(cmd.getResultCode()+" -- " +cmd.getResult(),outdir+".log",false);
                                Toast.makeText(context, "解包失败,日志保存在"+outdir, Toast.LENGTH_SHORT).show();
                            }
                            sendHandlerMSG(handler,0);
                        }
                    }).start();
                }
            }
        });
    }

    private void findROMImgs(){
        list.clear();
        checkboxs.clear();
        String storage = Environment.getExternalStorageDirectory().toString();
        String cmdstr = "find " + storage + "/ -path \""+storage+"/Android\" -prune -o -name  '"+rom_imgs[rom_img_index]+"' -print";
        CMD cmd = new CMD(cmdstr,false);
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
                showInfoMsg(this,"帮助信息","该功能主要是用于rom包解包，支持payload.bin/system.new.dat/system.new.dat.br/super.img等文件解包。\r\n" +
                        "1.选择左边下拉栏的文件类型,然后点击右边的扫描选项，即会开始扫描本地匹配的选项。\r\n" +
                        "2.搜索出结果后，会在下面显示，勾选左边选框再点击解包，就会开始解包当前镜像类型，支持批量解包.\r\n" +
                        "3.注意，一定要保持rom包本来的结构再使用这个工具，目前还处于测试阶段，后续会逐渐完善.\r\n"
                );
                break;
            case 1:
                fuckActivity.getIns().killall();
                ;
        }
        return super.onOptionsItemSelected(item);
    }

}
