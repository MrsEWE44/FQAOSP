package org.fqaosp.myActivitys;

import static org.fqaosp.utils.multiFunc.checkBoxsHashMap;
import static org.fqaosp.utils.multiFunc.checkCMDResult;
import static org.fqaosp.utils.multiFunc.checkShizukuPermission;
import static org.fqaosp.utils.multiFunc.dismissDialogHandler;
import static org.fqaosp.utils.multiFunc.getCMD;
import static org.fqaosp.utils.multiFunc.getUID;
import static org.fqaosp.utils.multiFunc.isSuEnable;
import static org.fqaosp.utils.multiFunc.sendHandlerMSG;
import static org.fqaosp.utils.multiFunc.showInfoMsg;
import static org.fqaosp.utils.multiFunc.showMyDialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
import org.fqaosp.adapter.PKGINFOAdapter;
import org.fqaosp.adapter.USERAdapter;
import org.fqaosp.entity.PKGINFO;
import org.fqaosp.utils.CMD;
import org.fqaosp.utils.fuckActivity;
import org.fqaosp.utils.makeWP;
import org.fqaosp.utils.multiFunc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 分身菜单实现
 */

public class workProfileMenuActivity extends AppCompatActivity {

    private ViewPager wpmavp;
    private View wpf, wpfm, wpfr;
    private ArrayList<View> views = new ArrayList<>();
    private ArrayList<String> slist = new ArrayList<>();
    private Integer viewPageIndex = 0;
    private ArrayList<PKGINFO> pkginfos = new ArrayList<>();
    private ArrayList<Boolean> checkboxs = new ArrayList<>();
    private ListView wpfLv, wpfmLv,wpfmLv2, wpfrLv;
    private HashMap<String, PKGINFO> pkginfoHashMap = new HashMap<>();
    private ArrayList<Boolean> checkboxsByUser = new ArrayList<>();
    private ArrayList<String> wpfmUserList = new ArrayList<>();
    private makeWP wp = new makeWP();
    private boolean isRoot = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.work_profile_menu_activity);
        fuckActivity.getIns().add(this);
        setTitle("分身部分");
        isRoot=isSuEnable();
        if(isRoot || checkShizukuPermission(1)){
            initViews();
        }else{
            showInfoMsg(this,"提示","本功能需要root或者shizuku授权才能正常使用");
        }

    }

    private void initViews() {
        //分身创建
        wpf = getLayoutInflater().inflate(R.layout.work_profile_activity, null);
        //分身管理
        wpfm = getLayoutInflater().inflate(R.layout.work_profile_manage_activity, null);
        //分身删除
        wpfr = getLayoutInflater().inflate(R.layout.work_profile_remove_activity, null);

        wpmavp = findViewById(R.id.wpmavp);
        views.add(wpf);
        views.add(wpfm);
        views.add(wpfr);
        slist.add("分身创建");
        slist.add("分身管理");
        slist.add("分身删除");
        FILESHARINGVIEWPAGERAdapter adapter = new FILESHARINGVIEWPAGERAdapter(views, slist);
        wpmavp.setAdapter(adapter);
        initOnListen();
        initwpf();
        initwpfm();
        initwpfr();
    }

    private void initOnListen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            wpmavp.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                    viewPageIndex = wpmavp.getCurrentItem();
                }
            });
        }
    }

    //分身创建
    private void initwpf() {
        checkboxs.clear();
        pkginfos.clear();
        Context context = this;
        Activity activity = this;
        Button b1 = wpf.findViewById(R.id.wpb1);
        Button b2 = wpf.findViewById(R.id.wpb2);
        wpfLv = wpf.findViewById(R.id.wplv1);
        EditText editText1 = wpf.findViewById(R.id.wpet1);
        EditText editText2 = wpf.findViewById(R.id.wpet2);
        if(!wp.init()){
            wp.setInitsize(Integer.valueOf(getMaxUserNum().trim()));
        }
        editText1.setHint("请输入需要开启的数量，默认最高"+wp.getInitsize()+"个");
        showInfoMsg(context,"警告","分身部分不能一次性开启太多，不然的话会闪退或者无响应");
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = editText1.getText().toString();
                //获取需要多开的用户数量
                Integer num = (null==s||s.isEmpty())?1:Integer.valueOf(s);
                if(num < wp.getInitsize() && num > 0){
                    ProgressDialog show = showMyDialog(context, "正在创建分身空间,请稍后(可能会出现无响应，请耐心等待)....");
                    Handler handler = dismissDialogHandler(0, show);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            UserManager um = (UserManager) getSystemService(Context.USER_SERVICE);
                            ArrayList<String> userList = getExistsUsers(um);
                            StringBuilder sb = new StringBuilder();
                            sb.append(wp.getInitCMD()+"\n");
                            sb.append("i=0;while((i<"+num+"));do ((i++)); "+wp.getCreateWPCMD()+";done");
                            //初始化初始用户数量
                            CMD cmd = getCMD(context,sb.toString(),isRoot);
                            if(cmd.getResultCode()==0){
                                StringBuilder sb1 = new StringBuilder();
                                StringBuilder sb2 = new StringBuilder();
                                sb1.append("aaa=(");
                                for (int i = 0; i < checkboxs.size(); i++) {
                                    if(checkboxs.get(i)){
                                        sb1.append("\""+pkginfos.get(i).getPkgname()+"\" ");
                                    }
                                }
                                sb1.append(");for pp in ${aaa[@]};do ");
                                for (UserHandle userHandle : um.getUserProfiles()) {
                                    String uid = getUID(userHandle.toString());
                                    //如果用户不在之前获取的列表里面，那么就开始同步选中的应用程序到新建的用户空间下
                                    if(haveUser(userList,uid)){
                                        sb1.append(wp.getInstallPkgCMD(uid, "$pp")+";");
                                        sb2.append(wp.getStartWPCMD(uid)+";");
                                        hidePKGS(userHandle);
                                    }
                                }
                                sb1.append("done;");
                                sb1.insert(0,sb2.toString());
                                CMD cmd2 = getCMD(context ,sb1.toString(),isRoot);
                                if(cmd2.getResultCode() != 0){
                                    showInfoMsg(context,"cmd2错误",cmd2.getResult());
                                }
//                                Log.d("cmd222",cmd2.getResultCode()+" -- " + cmd2.getResult());
                            }
                            editText1.setText("");
                            sendHandlerMSG(handler, 0);
                            if(cmd.getResultCode()!=0){
                                showInfoMsg(context,"cmd错误",cmd.getResult());
                            }
                        }
                    }).start();
                }else{
                    showInfoMsg(context,"警告","请输入 " + wp.getInitsize() + " 以内并且大于0的数值");
                }
            }
        });

        b2.setOnClickListener((v)->{
            searchStr(editText2,activity,wpfLv);
        });

        wpfLv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                PKGINFO pkginfo = pkginfos.get(i);
                ClipboardManager cpm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                cpm.setText(pkginfo.toString());
                Toast.makeText(context, "已复制", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    private void searchStr(EditText editText2,Activity activity,ListView lv){
        String searchStr = editText2.getText().toString();
        pkginfos = multiFunc.indexOfPKGS(activity, searchStr, pkginfos, checkboxs, 0);
        showPKGS(lv);
    }

    //分身管理
    private void initwpfm() {
        Context context = this;
        Activity activity = this;
        Button b1 = wpfm.findViewById(R.id.wpmanageab1);
        Button b2 = wpfm.findViewById(R.id.wpmanageab2);
        Button b3 = wpfm.findViewById(R.id.wpmanageab3);
        wpfmLv = wpfm.findViewById(R.id.wpmanagealv1);
        wpfmLv2 = wpfm.findViewById(R.id.wpmanagealv2);
        EditText et1 = wpfm.findViewById(R.id.wpmanageaet1);

        b1.setOnClickListener((v)->{
            wpBtClicked(context,v,0);
        });

        b2.setOnClickListener((v)->{
            wpBtClicked(context,v,1);
        });

        b3.setOnClickListener((v)->{
            searchStr(et1,activity,wpfmLv2);
        });

        wpfmLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String uid = wpfmUserList.get(i);
                Intent intent = new Intent(context, appopsActivity.class);
                intent.putExtra("uid", uid);
                startActivity(intent);
            }
        });
    }

    private void wpBtClicked(Context context,View view,int mode){
        AlertDialog show = showMyDialog(context, "正在"+(mode==0?"安装":"删除")+"应用,请稍后(可能会出现无响应，请耐心等待)....");
        Handler handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == 0) {
                    getPKGByUID(1);
                    show.dismiss();
                }
            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                StringBuilder sb = new StringBuilder();
                sb.append("aaaa=(");
                for (int i = 0; i < checkboxs.size(); i++) {
                    if (checkboxs.get(i)) {
                        sb.append("\""+pkginfos.get(i).getPkgname()+"\" ");
                    }
                }
                sb.append(");for pp in ${aaaa[@]};do ");
                for (int i1 = 0; i1 < checkboxsByUser.size(); i1++) {
                    if (checkboxsByUser.get(i1)) {
                        if(mode ==0){
                            sb.append(wp.getInstallPkgCMD(wpfmUserList.get(i1), "$pp")+";");
                        }
                        if(mode ==1){
                            sb.append(wp.getUninstallPkgByUIDCMD(wpfmUserList.get(i1), "$pp")+";");
                        }
                    }
                }
                sb.append("done;");
                CMD cmd = getCMD(context, sb.toString(), isRoot);
                sendHandlerMSG(handler,0);
                if(cmd.getResultCode() != 0){
                    showInfoMsg(context,"错误",cmd.getResult());
                }
            }
        }).start();
    }

    //分身删除
    private void initwpfr() {
        Context context = this;
        Button b1 = wpfr.findViewById(R.id.wprab1);
        Button b2 = wpfr.findViewById(R.id.wprab2);
        wpfrLv = wpfr.findViewById(R.id.wpralv1);
        b1.setOnClickListener((v)->{
            wpfrBtClicked(context,v,false);
        });
        b2.setOnClickListener((v)->{
            wpfrBtClicked(context,v,true);
        });
    }

    private void wpfrBtClicked(Context context,View view,boolean delall){
        ProgressDialog show = showMyDialog(context, "正在删除已经选中的分身用户,请稍后(可能会出现无响应，请耐心等待)....");
        Handler handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == 0) {
                    getUsers();
                    showUsers(wpfrLv);
                    show.dismiss();
                }
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                StringBuilder sb = new StringBuilder();
                sb.append("aaaa=(");
                if(delall){
                    for (String s : wpfmUserList) {
                        sb.append("\""+s+"\" ");
                    }
                }else{
                    for (int i = 0; i < checkboxsByUser.size(); i++) {
                        if (checkboxsByUser.get(i)) {
                            sb.append("\""+wpfmUserList.get(i)+"\" ");
                        }
                    }
                }
                sb.append(");for pp in ${aaaa[@]};do pm remove-user $pp;done;");
                CMD cmd = getCMD(context, sb.toString(), isRoot);
                sendHandlerMSG(handler, 0);
                checkCMDResult(context,cmd,"删除成功","删除失败");
            }
        }).start();
    }

    private void hidePKGS(UserHandle userHandle){
        StringBuilder sb = new StringBuilder();
        LauncherApps launcherApps = (LauncherApps) getSystemService(Context.LAUNCHER_APPS_SERVICE);
        List<LauncherActivityInfo> activityList = launcherApps.getActivityList(null, userHandle);
        sb.append("aaa=(");
        for (LauncherActivityInfo launcherActivityInfo : activityList) {
            sb.append("\""+launcherActivityInfo.getApplicationInfo().packageName+"\" ");
        }
        sb.append(");for aa in ${aaa[@]};do pm hide --user "+getUID(userHandle.toString())+" $aa ;done;");
        CMD cmd = getCMD(this, sb.toString(), isRoot);
//        Log.d("hidecmd",cmd.getResultCode()+" -- " + cmd.getResult());
    }

    private ArrayList<String> getExistsUsers(UserManager um){
        ArrayList<String> userList = new ArrayList<>();
        for (UserHandle userHandle : um.getUserProfiles()) {
            userList.add(getUID(userHandle.toString()));
        }
        return userList;
    }

    private boolean haveUser(ArrayList<String> users, String uid){
        for (String id : users) {
            if(id.equals(uid)){
                return false;
            }
        }
        return true;
    }

    private String getMaxUserNum(){
        String cmdstr = "pm get-max-users|cut -d':' -f2";
        CMD cmd = getCMD(this, cmdstr, isRoot);
        return cmd.getResult();
    }

    private void getUserEnablePKGS() {
        multiFunc.queryUserEnablePKGS(this, pkginfos, checkboxs, 0);
    }

    //获取启用的应用程序
    private void getEnablePKGS() {
        multiFunc.queryEnablePKGS(this, pkginfos, checkboxs, 0);
    }

    private void getPKGS() {
        //提取所有已安装的应用列表
        multiFunc.queryPKGS(this, pkginfos, checkboxs, 0);
    }

    private void getUserPKGS() {
        //提取所有已安装的应用列表
        multiFunc.queryUserPKGS(this, pkginfos, checkboxs, 0);
    }

    private void showPKGS(ListView listView) {
        PKGINFOAdapter pkginfoAdapter = new PKGINFOAdapter(pkginfos, workProfileMenuActivity.this, checkboxs);
        listView.setAdapter(pkginfoAdapter);
    }

    private void showUsers(ListView listView) {
        USERAdapter userAdapter = new USERAdapter(wpfmUserList, workProfileMenuActivity.this, checkboxsByUser);
        listView.setAdapter(userAdapter);
    }

    private void getUsers() {
        clearUser();
        //查询用户
        multiFunc.queryUSERS(this, wpfmUserList, checkboxsByUser);
    }

    //启动已经创建好的分身用户
    private void startupUsers(){
        Context context = this;
        ProgressDialog show = showMyDialog(context, "正在启动应用分身,请稍后(可能会出现无响应，请耐心等待)....");
        Handler handler = dismissDialogHandler(0,show);
        Handler handler2 = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                getUsers();
                StringBuilder sb = new StringBuilder();
                sb.append("aaaa=(");
                for (String s : wpfmUserList) {
                    sb.append("\""+s+"\" ");
                }
                sb.append(");for pp in ${aaaa[@]};do am start-user $pp;done;");
                CMD cmd = getCMD(context, sb.toString(), isRoot);
                sendHandlerMSG(handler,0);
                handler2.post(new Runnable() {
                    @Override
                    public void run() {
                        checkCMDResult(context,cmd,"启动完成","启动失败");
                    }
                });

            }
        }).start();

    }

    //获取用户空间里安装的应用
    private void getPKGByUID(Integer state) {
        pkginfos.clear();
        checkboxs.clear();
        pkginfoHashMap.clear();
        makeWP wp = new makeWP();
        if (wpfmUserList.size() == 0) {
            getUsers();
        }
        ProgressDialog show = showMyDialog(workProfileMenuActivity.this, "正在检索用户下安装的应用,请稍后(可能会出现无响应，请耐心等待)....");
        Handler handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == 0) {
                    showPKGS(wpfmLv2);
                    show.dismiss();
                }
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (String uid : wpfmUserList) {
                    String cmdstr = wp.getPkgByUIDCMD(uid);
                    if (state == 1) {
                        cmdstr = wp.getUserPkgByUIDCMD(uid);
                    }
                    CMD cmd = new CMD(cmdstr);
                    String result = cmd.getResult();
                    String[] split = result.split("\n");
                    if (null != split) {
                        for (String s : cmd.getResult().split("\n")) {
                            PackageManager pm = getPackageManager();
                            PackageInfo packageInfo = null;
                            try {
                                packageInfo = pm.getPackageInfo(s, 0);
                                checkBoxsHashMap(pkginfoHashMap, checkboxs, packageInfo, pm);
                            } catch (PackageManager.NameNotFoundException e) {
                                Log.e(workProfileMenuActivity.class.getName(),e.toString());
                            }
                        }
                    }
                }
                pkginfos.clear();
                checkboxs.clear();
                pkginfos.addAll(pkginfoHashMap.values());
                for (PKGINFO pkginfo : pkginfos) {
                    checkboxs.add(false);
                }
                sendHandlerMSG(handler, 0);
            }
        }).start();
    }

    private void clearUser() {
        checkboxs.clear();
        wpfmUserList.clear();
        pkginfos.clear();
        checkboxsByUser.clear();
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        menu.clear();
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
                menu.add(Menu.NONE, 0, 0, "显示主用户所有应用");
                menu.add(Menu.NONE, 1, 1, "显示主用户所有应用(包括禁用)");
                menu.add(Menu.NONE, 2, 2, "显示主用户安装的应用");
                menu.add(Menu.NONE, 3, 3, "显示主用户安装的应用(包括禁用)");
                menu.add(Menu.NONE, 4, 4, "显示其他用户所有的应用(包括禁用)");
                menu.add(Menu.NONE, 5, 5, "显示其他用户安装的应用(包括禁用)");
                menu.add(Menu.NONE, 6, 6, "显示分身用户UID");
                menu.add(Menu.NONE, 7, 7, "启动其它分身用户");
                menu.add(Menu.NONE, 8, 8, "帮助");
                menu.add(Menu.NONE, 9, 9, "退出");
                break;
            case 2:
                menu.add(Menu.NONE, 0, 0, "显示分身用户");
                menu.add(Menu.NONE, 1, 1, "帮助");
                menu.add(Menu.NONE, 2, 2, "退出");
                break;
        }
        return super.onMenuOpened(featureId, menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("menu");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        switch (viewPageIndex) {
            case 0:
                switch (itemId) {
                    case 0:
                        getEnablePKGS();
                        showPKGS(wpfLv);
                        break;
                    case 1:
                        getPKGS();
                        showPKGS(wpfLv);
                        break;
                    case 2:
                        getUserPKGS();
                        showPKGS(wpfLv);
                        break;
                    case 3:
                        getUserEnablePKGS();
                        showPKGS(wpfLv);
                        break;
                    case 4:
                        showInfoMsg(this, "帮助信息", "该页面是用于应用分身的，需要root授权。\r\n" +
                                "1.右上角三个点，显示安装应用，会列出当前用户已经安装的应用。\r\n" +
                                "2.开启数量，由用户自己输入，但不能超过默认的最高1024个，不然会出错。\r\n" +
                                "3.搜索框支持中英文搜索，不区分大小写.\r\n" +
                                "4.如果创建失败或者只有一个分身，那是因为系统限制的缘故，更换为lineage或者pixel experience再次尝试。（炼妖壶issue连接：https://github.com/oasisfeng/island/issues/382）.\r\n"
                        );
                        break;
                    case 5:
                        fuckActivity.getIns().killall();
                        ;
                }
                break;
            case 1:
                switch (itemId) {
                    case 0:
                        getEnablePKGS();
                        showPKGS(wpfmLv2);
                        break;
                    case 1:
                        getPKGS();
                        showPKGS(wpfmLv2);
                        break;
                    case 2:
                        getUserPKGS();
                        showPKGS(wpfmLv2);
                        break;
                    case 3:
                        getUserEnablePKGS();
                        showPKGS(wpfmLv2);
                        break;
                    case 4:
                        getPKGByUID(0);
                        break;
                    case 5:
                        getPKGByUID(1);
                        break;
                    case 6:
                        getUsers();
                        showUsers(wpfmLv);
                        break;
                    case 7:
                        startupUsers();
                        break;
                    case 8:
                        showInfoMsg(this, "帮助信息", "该页面是用于管理分身应用的，需要root授权。\r\n" +
                                "1.右上角三个点，显示主用户安装应用，会列出当前用户已经安装的应用。\r\n" +
                                "2.右上角三个点，显示其它用户安装应用，会列出当其它分身用户已经安装的应用，但是不会显示多余的，遇到重复的会只列出一个。\r\n" +
                                "3.同步安装一个应用到分身，从当前主用户已安装的应用当中，批量勾选安装到其它分身用户下面。\r\n" +
                                "4.从分身删除应用，勾选分身里面的应用，批量进行卸载操作。\r\n" +
                                "5.显示分身id,列出当前设备上所有分身用户的id\r\n" +
                                "6.启动分身,从当前界面开始启动之前创建的分身用户,使其可以正常使用分身后的程序.\r\n" +
                                "7.搜索框支持中英文搜索，不区分大小写.\r\n" +
                                "8.点击分身应用，可以进入到该软件自带的应用管理当中，可以进行服务禁用、活动项禁用，权限管控之类的，等等。\r\n"
                        );
                        break;
                    case 9:
                        fuckActivity.getIns().killall();
                        ;
                }
                break;
            case 2:
                switch (itemId) {
                    case 0:
                        getUsers();
                        showUsers(wpfrLv);
                        break;
                    case 1:
                        showInfoMsg(this, "帮助信息", "该页面是用于删除分身的，需要root授权。\r\n" +
                                "1.右上角三个点，显示分身用户，会列出除了当前主用户外的其它用户。\r\n" +
                                "2.全删了，点击后，会删除列表里所有用户。\r\n" +
                                "3.删除分身，需要勾选相应分身才能删除，不勾选就不删除。\r\n"
                        );
                        break;
                    case 2:
                        fuckActivity.getIns().killall();
                        ;
                }
                break;

        }
        return super.onOptionsItemSelected(item);
    }


}
