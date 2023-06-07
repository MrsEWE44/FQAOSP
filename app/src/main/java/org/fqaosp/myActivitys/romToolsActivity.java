package org.fqaosp.myActivitys;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import org.fqaosp.R;
import org.fqaosp.adapter.FILESHARINGVIEWPAGERAdapter;
import org.fqaosp.entity.PKGINFO;
import org.fqaosp.utils.CMD;
import org.fqaosp.utils.dialogUtils;
import org.fqaosp.utils.fileTools;
import org.fqaosp.utils.fuckActivity;

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
    private String rom_imgs2[] = {"system.new.dat","system.new.dat.br","sparse img"};
    private String rom_imgs2_ver[] = {"5.0","5.1","6.x","7.x+"};
    private boolean isRoot=false,isADB=false;

    private Context context ;

    private fileTools ft = new fileTools();
    private dialogUtils du = new dialogUtils();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rom_tools_activity);
        fuckActivity.getIns().add(this);
        setTitle("ROM工具");
        context=this;
        Intent intent = getIntent();
        isRoot = intent.getBooleanExtra("isRoot",false);
        isADB = intent.getBooleanExtra("isADB",false);
        if(Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT){
            du.showInfoMsg(this,"警告","该功能暂时不支持安卓4.x设备.");
        }else{
            String filesDir =ft.getMyHomeFilesPath(this);
            ft.checkTools(this,isADB);
            String fqtoolsusr = filesDir+"/usr";
            File fqtoolsusrdir = new File(fqtoolsusr);
            if(!fqtoolsusrdir.exists()){
                du.showImportToolsDialog(this,"fqtools核心无法获取，请退出重试或者重新安装app","fqtools工具包没有找到,功能使用将受到限制或者异常,要继续使用吗？",isRoot,isADB);
            }
            du.showLowMemDialog(context);
            initViews();
        }
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
            ProgressDialog show = du.showMyDialog(context,"正在扫描本地IMG镜像文件,请稍后(可能会出现无响应，请耐心等待)....");
            Handler handler = new Handler(){
                @Override
                public void handleMessage(@NonNull Message msg) {
                    if(msg.what==0){
                        du.showUsers(context,rralv1,list,checkboxs);
                        show.dismiss();
                    }
                }
            };
            new Thread(new Runnable() {
                @Override
                public void run() {
                    findROMImgs();
                    du.sendHandlerMSG(handler,0);
                }
            }).start();

        });

        rrab2.setOnClickListener((v)->{
           btClicked(this,v,1);
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
            ProgressDialog show = du.showMyDialog(context,"正在扫描本地ROM镜像文件,请稍后(可能会出现无响应，请耐心等待)....");
            Handler handler = new Handler(){
                @Override
                public void handleMessage(@NonNull Message msg) {
                    if(msg.what==0){
                        du.showUsers(context,rualv1,list,checkboxs);
                        show.dismiss();
                    }
                }
            };
            new Thread(new Runnable() {
                @Override
                public void run() {
                    findROMImgs();
                    du.sendHandlerMSG(handler,0);
                }
            }).start();

        });

        ruab2.setOnClickListener((v)->{
            btClicked(context,v,0);
        });

    }

    private void btClicked(Context context,View v,int mode){
        ArrayList<PKGINFO> pplist = new ArrayList<>();
        for (int i = 0; i < checkboxs.size(); i++) {
            if(checkboxs.get(i)){
                String s = list.get(i);
                pplist.add(new PKGINFO(s,s,s,null,null,null,null));
            }
        }

        du.showProcessBarDialogByCMD(context,pplist,"正在"+(mode ==0?"解":"打")+"包ROM","当前正在"+(mode ==0?"解":"打")+"包ROM: ",9,
                null ,false,false,"0",mode,null,
                null, new String[]{getRomType() ,mode==0?getRomPartType():String.valueOf((rom_img_index2_ver+1))});
    }

    private String getRomType(){
        viewPageIndex = rtavp.getCurrentItem();
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
                case 2:
                    return "sparseimg";
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
        viewPageIndex = rtavp.getCurrentItem();
        list.clear();
        checkboxs.clear();
        String storage = Environment.getExternalStorageDirectory().toString();
        String myStorageHomePath = ft.getMyStorageHomePath(this);
        String cmdstr = "find " + storage + "/ -path \""+storage+"/Android\" -prune -o -name  '"+rom_imgs[rom_img_index]+"' -print";
        String cmdstr2 = "find " + storage + "/ -path \""+storage+"/Android\" -prune -o -name  '*.img' -print && find "+myStorageHomePath+"/ -name '*.img' -print";
        CMD cmd = new CMD(viewPageIndex==0? cmdstr:cmdstr2,false);
        for (String s1 : cmd.getResult().split("\n")) {
            list.add(s1);
            checkboxs.add(false);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE,0,0,"帮助");
        menu.add(Menu.NONE,1,1,"退出");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        viewPageIndex = rtavp.getCurrentItem();
        switch (itemId){
            case 0:
                if(viewPageIndex == 0){
                    du.showInfoMsg(this,"帮助信息","该功能主要是用于rom包解包，支持payload.bin/system.new.dat/system.new.dat.br/super.img等文件解包。\r\n" +
                            "1.选择左边下拉栏的文件类型,然后点击右边的扫描选项，即会开始扫描本地匹配的选项。\r\n" +
                            "2.搜索出结果后，会在下面显示，勾选左边选框再点击解包，就会开始解包当前镜像类型，支持批量解包.\r\n" +
                            "3.注意，一定要保持rom包本来的结构再使用这个工具，目前还处于测试阶段，后续会逐渐完善.\r\n"
                    );
                }else{
                    du.showInfoMsg(this,"帮助信息","该功能主要是用于本地IMG打包，暂时支持system.new.dat/system.new.dat.br文件格式打包。\r\n" +
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
