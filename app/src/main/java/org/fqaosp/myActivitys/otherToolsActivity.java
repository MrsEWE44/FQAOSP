package org.fqaosp.myActivitys;

import static org.fqaosp.utils.multiFunc.checkShizukuPermission;
import static org.fqaosp.utils.multiFunc.dismissDialogHandler;
import static org.fqaosp.utils.multiFunc.getCMD;
import static org.fqaosp.utils.multiFunc.isSuEnable;
import static org.fqaosp.utils.multiFunc.preventDismissDialog;
import static org.fqaosp.utils.multiFunc.sendHandlerMSG;
import static org.fqaosp.utils.multiFunc.showCMDInfoMSG;
import static org.fqaosp.utils.multiFunc.showInfoMsg;
import static org.fqaosp.utils.multiFunc.showMyDialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.fqaosp.R;
import org.fqaosp.utils.CMD;
import org.fqaosp.utils.fuckActivity;
import org.fqaosp.utils.multiFunc;
import org.fqaosp.utils.netUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class otherToolsActivity extends AppCompatActivity {

    private Button otabt1,otabt2,otabt3,otabt4,otabt5;
    int otrrasp1_index=0,otrrasp2_index=0,otnsasp1_index=0;
    private boolean isRoot=false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.other_tools_activity);
        fuckActivity.getIns().add(this);
        setTitle("杂七杂八小工具");
        isRoot=isSuEnable();
        initBt();
    }

    private void initBt() {
        otabt1 = findViewById(R.id.otabt1);
        otabt2 = findViewById(R.id.otabt2);
        otabt3 = findViewById(R.id.otabt3);
        otabt4 = findViewById(R.id.otabt4);
        otabt5 = findViewById(R.id.otabt5);
        checkBts();
        clickBt();
    }

    private void clickBt(){
        Context context = this;

        otabt1.setOnClickListener((v)->{
            AlertDialog show = showMyDialog(context,"提示","正在获取网络时间,请稍后(可能会出现无响应，请耐心等待)....");
            Handler handler = new Handler(){
                @SuppressLint("HandlerLeak")
                @Override
                public void handleMessage(@NonNull Message msg) {
                    super.handleMessage(msg);
                    if(msg.what == 0){
                        multiFunc.dismissDialog(show);
                        String str = msg.obj.toString();
                        String year=getByString(str,"nyear=\\d+","nyear=||\\s+");
                        String month=getByString(str,"nmonth=\\d+","nmonth=||\\s+");
                        String day=getByString(str,"nday=\\d+","nday=||\\s+");
                        String hrs=getByString(str,"nhrs=\\d+","nhrs=||\\s+");
                        String min=getByString(str,"nmin=\\d+","nmin=||\\s+");
                        String sec=getByString(str,"nsec=\\d+","nsec=||\\s+");
                        try {
                            String fulltime=year+"-"+formatStr(month)+"-"+formatStr(day)+" "+formatStr(hrs)+":"+formatStr(min)+":"+formatStr(sec);
                            CMD cmd = new CMD("setprop persist.sys.timezone Asia/Shanghai; date \"" + fulltime + "\";");
                            showInfoMsg(context,"信息","执行完毕: \r\n"+cmd.getResultCode()+" -----> "+cmd.getResult()+" \r\n\r\n "+str);
                        }catch (Exception e){
                            showInfoMsg(context,"错误","执行出错: \r\n"+e.toString()+" \r\n\r\n "+str);

                        }
                    }
                }
            };
            netUtils net = new netUtils();
            Message msg = new Message();
            StringBuilder sb = new StringBuilder();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        sb.append(net.getHTML("http://www.beijing-time.org/t/time.asp"));
                    }catch (Exception e){
                        sb.append("\r\nError : "+e.toString());
                        e.printStackTrace();
                    }
                    msg.what=0;
                    msg.obj=sb;
                    handler.sendMessage(msg);
                }
            }).start();

        });

        otabt2.setOnClickListener((v)->{
            if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.R){
                AlertDialog show = showMyDialog(context,"提示","正在开启墓碑模式,请稍后(可能会出现无响应，请耐心等待)....");
                Handler handler = new Handler(){
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        super.handleMessage(msg);
                        multiFunc.dismissDialog(show);
                        if(msg.what==0){
                            AlertDialog.Builder ab = new AlertDialog.Builder(context);
                            ab.setTitle("信息");
                            ab.setMessage("命令已执行完毕,请重启手机生效.\r\n\r\n"+msg.obj.toString());
                            ab.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                    new CMD("svc power reboot");
                                }
                            });

                            ab.setNeutralButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            });

                            AlertDialog alertDialog = ab.create();
                            alertDialog.show();
                            TextView tv = alertDialog.getWindow().getDecorView().findViewById(android.R.id.message);
                            tv.setTextIsSelectable(true);
                        }
                    }
                };
                v.post(new Runnable() {
                    @Override
                    public void run() {
                        String cmdstr = "device_config put activity_manager_native_boot use_freezer true";
                        CMD cmd = getCMD(context,cmdstr,isRoot);
                        Message msg = new Message();
                        msg.obj=cmd.getResultCode()+" -----> "+cmd.getResult();
                        msg.what=0;
                        handler.sendMessage(msg);
                    }
                });
            }else{
                showInfoMsg(context,"错误","当前安卓版本不支持该功能,需要安卓11以上才行!");
            }
        });

        otabt3.setOnClickListener((v)->{
            String refresh_rates[] = {"48","60","90","120","144","165","自定义"};

            AlertDialog.Builder ab = new AlertDialog.Builder(context);
            View view2 = getLayoutInflater().inflate(R.layout.other_tools_refresh_rate_activity, null);
            Spinner otrrasp1 = view2.findViewById(R.id.otrrasp1);
            Spinner otrrasp2 = view2.findViewById(R.id.otrrasp2);
            EditText otrraet1 = view2.findViewById(R.id.otrraet1);
            EditText otrraet2 = view2.findViewById(R.id.otrraet2);
            otrrasp1.setAdapter(new ArrayAdapter<String>(view2.getContext(), android.R.layout.simple_list_item_1, refresh_rates));
            otrrasp2.setAdapter(new ArrayAdapter<String>(view2.getContext(), android.R.layout.simple_list_item_1, refresh_rates));
            otrrasp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    otrrasp1_index=i;
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            otrrasp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    otrrasp2_index = i;
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            ab.setView(view2);
            ab.setTitle("选择刷新率");
            ab.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String maxRefreshRate=(otrrasp1_index==(refresh_rates.length-1)?otrraet1.getText().toString().trim():refresh_rates[otrrasp1_index]);
                    String minRefreshRate=(otrrasp2_index==(refresh_rates.length-1)?otrraet2.getText().toString().trim():refresh_rates[otrrasp2_index]);
                    String cmdstr = "settings put system peak_refresh_rate "+Float.valueOf(maxRefreshRate)+" && settings put system min_refresh_rate "+Float.valueOf(minRefreshRate);
                    AlertDialog show = showMyDialog(context,"提示","正在更改刷新率,请稍后(可能会出现无响应，请耐心等待)....");
                    Handler handler = new Handler(){
                        @Override
                        public void handleMessage(@NonNull Message msg) {
                            super.handleMessage(msg);
                            if(msg.what == 0){
                                multiFunc.dismissDialog(show);
                                dialogInterface.cancel();
                                showInfoMsg(context,"信息","已执行结束!\r\n\r\n"+msg.obj.toString());
                            }
                        }
                    };
                    v.post(new Runnable() {
                        @Override
                        public void run() {
                            CMD cmd = getCMD(context,cmdstr,isRoot);
                            sendHandlerMSG(handler,0,cmd.getResultCode()+" -----> "+cmd.getResult());
                        }
                    });
                }
            });
            ab.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            ab.create().show();
        });

        otabt4.setOnClickListener((v)->{
            String cmdstr = "";
            int sdkInt = Build.VERSION.SDK_INT;
            if(sdkInt <= Build.VERSION_CODES.N){
                cmdstr = "settings delete global captive_portal_server;settings put global captive_portal_detection_enabled 0;";
            }
            if(sdkInt >= Build.VERSION_CODES.N_MR1 && sdkInt <= Build.VERSION_CODES.Q){
                cmdstr = "settings put global captive_portal_https_url https://www.google.cn/generate_204";
            }

            if(sdkInt >=Build.VERSION_CODES.R){
                cmdstr = "settings put global captive_portal_mode 0;settings put global captive_portal_https_url https://www.google.cn/generate_204;";
            }
            showCMDInfoMSG(context,v,cmdstr,isRoot,"提示","正在去掉信号栏X标记,请稍后(可能会出现无响应，请耐心等待)....");

        });

        otabt5.setOnClickListener((v)->{
            String ntp_services[] = {"dns1.synet.edu.cn","news.neu.edu.cn","dns.sjtu.edu.cn","dns2.synet.edu.cn","ntp.glnet.edu.cn","ntp-sz.chl.la","ntp.gwadar.cn","cn.pool.ntp.org","自定义"};

            AlertDialog.Builder ab = new AlertDialog.Builder(context);
            View view2 = getLayoutInflater().inflate(R.layout.other_tools_ntp_service_activity, null);
            Spinner otnsasp1 = view2.findViewById(R.id.otnsasp1);
            EditText otnsaet1 = view2.findViewById(R.id.otnsaet1);
            otnsasp1.setAdapter(new ArrayAdapter<String>(view2.getContext(), android.R.layout.simple_list_item_1, ntp_services));
            otnsasp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    otnsasp1_index=i;
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            ab.setView(view2);
            ab.setTitle("选择ntp服务器");
            ab.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String ntp_service_name=(otrrasp1_index==(ntp_services.length-1)?otnsaet1.getText().toString().trim():ntp_services[otnsasp1_index]);
                    String cmdstr = "setprop persist.sys.timezone Asia/Shanghai&& settings put global ntp_server "+ntp_service_name;
                    AlertDialog show = showMyDialog(context,"提示","正在修改ntp服务器,请稍后(可能会出现无响应，请耐心等待)....");
                    Handler handler = new Handler(){
                        @Override
                        public void handleMessage(@NonNull Message msg) {
                            super.handleMessage(msg);
                            if(msg.what == 0){
                                multiFunc.dismissDialog(show);
                                dialogInterface.cancel();
                                showInfoMsg(context,"信息","已执行结束!\r\n\r\n"+msg.obj.toString());
                            }
                        }
                    };
                    v.post(new Runnable() {
                        @Override
                        public void run() {
                            CMD cmd = getCMD(context,cmdstr,isRoot);
                            sendHandlerMSG(handler,0,cmd.getResultCode()+" -----> "+cmd.getResult());
                        }
                    });
                }
            });
            ab.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            ab.create().show();
        });
    }

    private void checkBts(){
        checkBt(otabt1,true,false);
        checkBt(otabt2,true,true);
        checkBt(otabt3,true,true);
        checkBt(otabt4,true,true);
        checkBt(otabt5,true,true);
    }

    private void checkBt(Button button , boolean needRoot , boolean needADB){
        if(isRoot|| (needRoot == false && needADB == false)){
            button.setBackgroundColor(Color.rgb(17,179,98));
        }else if(needADB && checkShizukuPermission(1)){
            button.setBackgroundColor(Color.rgb(176,198,39));
        }else{
            button.setBackgroundColor(Color.rgb(223,90,90));
        }
    }

    private String formatStr(String str){
        return (str.length()==2?str:"0"+str);
    }

    private String getByString(String src, String regex, String re_str) {
        StringBuilder tmp = new StringBuilder();
        Matcher m = Pattern.compile(regex).matcher(src);
        if (m.find()) {
            return m.group().replaceAll(re_str, "");
        }
        return null;
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
                showInfoMsg(this,"帮助信息","该页面是小工具的合集(部分功能需要重启设备才能生效)，有需要root或adb授权的功能,红色是必须root权限才能使用的,黄色则是可以通过adb权限使用。\r\n" +
                        "1.同步北京时间: 会联网获取当前北京时间,并且设置本地系统的时间为获取到的时间.\r\n" +
                        "2.开启墓碑模式: 会通过命令调用系统自带的墓碑模式(cached-apps-freezer),需要重启设备生效.\r\n" +
                        "3.调整刷新率: 会通过命令调用系统自带的刷新率参数进行设置,用户可以使用固定的几个选项来调整设备的刷新率.\r\n" +
                        "4.去掉信号X: 会通过调用命令来去掉状态栏上面得X标记.\r\n" +
                        "5.设置ntp服务器: 会通过调用命令配置ntp服务器地址,支持用户自定义ntp服务地址.\r\n"
                );
                break;
            case 1:
                fuckActivity.getIns().killall();
                ;
        }
        return super.onOptionsItemSelected(item);
    }

}
