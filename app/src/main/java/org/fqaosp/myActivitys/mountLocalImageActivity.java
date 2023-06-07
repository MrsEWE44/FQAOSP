package org.fqaosp.myActivitys;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.fqaosp.R;
import org.fqaosp.adapter.USERAdapter;
import org.fqaosp.utils.CMD;
import org.fqaosp.utils.dialogUtils;
import org.fqaosp.utils.fileTools;
import org.fqaosp.utils.fuckActivity;
import org.fqaosp.utils.makeImgToPC;
import org.fqaosp.utils.permissionRequest;

import java.io.File;
import java.util.ArrayList;

/**
 *
 *
 * 挂载手机本地的镜像文件到电脑上，实现driverdroid功能
 * 可以给你的电脑安装系统，也可以运行pe
 *
 *
 *
 * */
public class mountLocalImageActivity extends AppCompatActivity {

    private ArrayList<String> list = new ArrayList<>();
    private ArrayList<Boolean> checkboxs = new ArrayList<>();
    private Button mliab1,mliab2,mliab3;
    private ListView lv1;
    private String[] filetype = {"img","iso"};
    private String[] filetype2 = {"ext2","minix","minix2","vfat","reiser"};
    private String[] filetype2CMD = {"mkfs.ext2 -F","mkfs.minix","mkfs.minix -v","mkfs.vfat -v","mkfs.reiser -f"};
    private String[] sizetype = {"byte", "KB", "MB", "GB"};
    private String[] sizetypeCMD = {"", "K", "M", "G"};
    private Integer filetype2Index=0,sizetypeCMDIndex=0,filetypeIndex=0;
    private boolean isRoot = false,isADB=false;

    private dialogUtils du = new dialogUtils();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mount_local_img_activity);
        fuckActivity.getIns().add(this);
        setTitle("U盘模式");
        Intent intent = getIntent();
        isRoot = intent.getBooleanExtra("isRoot",false);
        isADB = intent.getBooleanExtra("isADB",false);
        if(isRoot){
            initBt();
            permissionRequest.getExternalStorageManager(mountLocalImageActivity.this);
        }else{
            du.showInfoMsg(this,"提示","本功能需要root才能正常使用");
        }

    }

    private void initBt(){
        mliab1 = findViewById(R.id.mliab1);
        mliab2 = findViewById(R.id.mliab2);
        mliab3 = findViewById(R.id.mliab3);
        lv1 = findViewById(R.id.mlialv1);
        clickBts();
    }

    private void clickBts(){

        Context context = this;

        mliab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeImgToPC toPC = new makeImgToPC();
                for (int i = 0; i < checkboxs.size(); i++) {
                    if(checkboxs.get(i)){
                        String imgPath = list.get(i);
                        if(toPC.mountLocalFile(imgPath)){
                            Toast.makeText(context, "挂载成功 "+imgPath, Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(context, "挂载失败 "+imgPath, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        mliab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProgressDialog show = du.showMyDialog(context,"正在扫描本地镜像文件,请稍后(可能会出现无响应，请耐心等待)....");
                Handler handler = new Handler(){
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        if(msg.what==0){
                            showImgs(lv1);
                            show.dismiss();
                        }
                    }
                };
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getImgs();
                        du.sendHandlerMSG(handler,0);
                    }
                }).start();
            }
        });

        mliab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImgFileCreateDialog(context);
            }
        });

    }

    private void checkBusybox(Context context){
        fileTools ft = new fileTools();
        String filesDir =ft.getMyHomeFilesPath(context);
        String busyboxFile = filesDir+"/busybox";
        File busyF = new File(busyboxFile);
        if(!busyF.exists()){
            ft.extactAssetsFile(context,"busybox",busyboxFile);
        }
    }

    //显示创建镜像文件提示框界面
    private void showImgFileCreateDialog(Context context){
        checkBusybox(context);
        String storagePath = Environment.getExternalStorageDirectory().toString();
        AlertDialog.Builder ab = new AlertDialog.Builder(context);
        View view2 = getLayoutInflater().inflate(R.layout.mount_local_img_create_img_file_activity, null);
        EditText mlicifaet1 = view2.findViewById(R.id.mlicifaet1);
        EditText mlicifaet2 = view2.findViewById(R.id.mlicifaet2);
        Spinner mlicifasp1 = view2.findViewById(R.id.mlicifasp1);
        Spinner mlicifasp2 = view2.findViewById(R.id.mlicifasp2);
        Spinner mlicifasp3 = view2.findViewById(R.id.mlicifasp3);
        mlicifasp1.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, filetype));
        mlicifasp2.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, sizetype));
        mlicifasp3.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, filetype2));
        mlicifasp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                filetypeIndex=i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        mlicifasp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                sizetypeCMDIndex=i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        mlicifasp3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                filetype2Index=i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        ab.setView(view2);
        ab.setTitle("创建文件");
        ab.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String filesDir =new fileTools().getMyHomeFilesPath(context);
                String busyboxFile = filesDir+"/busybox";
                String fileName = mlicifaet1.getText().toString().trim();
                String fileSize = mlicifaet2.getText().toString().trim();
                String outPath = storagePath+"/Download/"+fileName+"."+filetype[filetypeIndex];
                if(fileName.isEmpty() || (fileSize.isEmpty() || Integer.valueOf(fileSize) <=0)){
                    Toast.makeText(context, "请输入正确的文件名称或者文件大小", Toast.LENGTH_SHORT).show();
                }else{
                    String cmdstr = "dd if=/dev/zero of="+outPath+" bs=1"+sizetypeCMD[sizetypeCMDIndex] + " count="+fileSize + " && "+busyboxFile + " " +filetype2CMD[filetype2Index] + " " + outPath;
                    CMD cmd = new CMD(cmdstr);
                    if(cmd.getResultCode()==0){
                        Toast.makeText(context, "创建成功", Toast.LENGTH_SHORT).show();
                        dialogInterface.cancel();
                    }else{
                        Toast.makeText(context, "创建失败", Toast.LENGTH_SHORT).show();
                    }

                }
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

    private void getImgs(){
        list.clear();
        checkboxs.clear();
        String s = Environment.getExternalStorageDirectory().toString();
        if(Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT){
            s="/mnt/sdcard/0";
        }
        CMD cmd = new CMD("find "+s+"/ -name '*.img' -o -name '*.iso'");
        for (String s1 : cmd.getResult().split("\n")) {
            list.add(s1);
            checkboxs.add(false);
        }
    }

    private void showImgs(ListView listView){
        USERAdapter userAdapter = new USERAdapter(list, mountLocalImageActivity.this, checkboxs);
        listView.setAdapter(userAdapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }


    @Override
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
                du.showInfoMsg(this,"帮助信息","该页面是用于挂载手机上的镜像文件，让电脑识别的，可以当U盘使用，可以给电脑安装系统，需要root权限授权。\r\n" +
                        "1.挂载选中的镜像文件，勾选一个镜像文件，然后手机连接电脑，手机进入开发者模式，选择usb默认配置为存储，然后再点击挂载，然后电脑就会有反应，你就可以进行之后的操作了。\r\n" +
                        "2.扫描本地镜像文件，点击后，会申请root权限，通过find命令来查找当前设备里面的镜像文件(img/iso)。\r\n" +
                        "3.创建镜像文件,点击后，会弹出一个窗口，里面有几个必须要填的选项，填完后按“确定”即可生成对应镜像文件。生成后的镜像文件默认存放在内部存储根目录的/Download文件夹里面。\r\n" +
                        "3-1.里面有个img与iso的选项，其实没有区别，只是有些人更喜欢用iso或者img作为镜像文件的识别。\r\n" +
                        "3-2.在大小选择那里，不能输入小数点的，但是你可以换算成最小位的byte。\r\n" +
                        "3-3.在最后的文件格式那里，你如果想要让Windows直接识别，那可以选择vfat分区格式。这些分区格式都是通过busybox实现。\r\n" +
                        "3-4.在创建镜像文件的途中，会卡住一会，这部分我没有加入弹窗提示，可能会出现界面无响应的问题，等待就可以了。\r\n" +
                        "4.如何退出U盘模式，直接切换usb连接方式，把文件传输修改为充电就好了。如果还是不行，重启手机即可。\r\n"
                );
                break;
            case 1:
                fuckActivity.getIns().killall();
                ;
        }
        return super.onOptionsItemSelected(item);
    }

}
