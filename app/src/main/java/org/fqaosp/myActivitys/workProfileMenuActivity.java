package org.fqaosp.myActivitys;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.UserHandle;
import android.os.UserManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import org.fqaosp.R;
import org.fqaosp.adapter.FILESHARINGVIEWPAGERAdapter;
import org.fqaosp.entity.PKGINFO;
import org.fqaosp.utils.CMD;
import org.fqaosp.utils.dialogUtils;
import org.fqaosp.utils.fuckActivity;
import org.fqaosp.utils.makeWP;
import org.fqaosp.utils.packageUtils;
import org.fqaosp.utils.shellUtils;
import org.fqaosp.utils.textUtils;
import org.fqaosp.utils.userUtils;

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

    private Context context;
    private Activity activity;

    private dialogUtils du = new dialogUtils();
    private userUtils uu = new userUtils();
    private shellUtils su = new shellUtils();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.work_profile_menu_activity);
        fuckActivity.getIns().add(this);
        setTitle("分身部分");
        context=this;
        activity=this;
        isRoot=su.isSuEnable();
        if((isRoot || su.isADB()) && Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT){
            initViews();
        }else{
            du.showInfoMsg(this,"提示","本功能需要root或者shizuku授权才能正常使用,不支持安卓4.x设备.");
        }
        du.showLowMemDialog(context);
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
        initwpf();
        initwpfm();
        initwpfr();
    }

    //分身创建
    private void initwpf() {
        checkboxs.clear();
        pkginfos.clear();
        Button b1 = wpf.findViewById(R.id.wpb1);
        Button b2 = wpf.findViewById(R.id.wpb2);
        wpfLv = wpf.findViewById(R.id.wplv1);
        EditText editText1 = wpf.findViewById(R.id.wpet1);
        EditText editText2 = wpf.findViewById(R.id.wpet2);
        if(!wp.init()){
            wp.setInitsize(Integer.valueOf(getMaxUserNum().trim()));
        }
        editText1.setHint("请输入需要开启的数量，默认最高"+wp.getInitsize()+"个");
        du.showInfoMsg(context,"警告","分身部分不能一次性开启太多，不然的话会闪退或者无响应");
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = editText1.getText().toString();
                //获取需要多开的用户数量
                Integer num = (null==s||s.isEmpty())?1:Integer.valueOf(s);
                if(num < wp.getInitsize() && num > 0){
                    StringBuilder sb = new StringBuilder();
                    sb.append(wp.getInitCMD()+"\n");
                    sb.append("i=0;while((i<"+num+"));do ((i++)); "+wp.getCreateWPCMD()+";done");
                    showProcessDialogCMDByWP(context,addPKGS(0,false),checkboxsByUser,wpfmUserList,sb.toString(),"正在创建分身空间","当前正在安装: ",0,0,isRoot);
                }else{
                    du.showInfoMsg(context,"警告","请输入 " + wp.getInitsize() + " 以内并且大于0的数值");
                }
            }
        });

        b2.setOnClickListener((v)->{
            du.showIndexOfPKGSDialog(context,activity,wpfLv,editText2,pkginfos,null,checkboxs);
        });

        wpfLv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                new textUtils().copyText(context,pkginfos.get(i).toString());
                return false;
            }
        });
    }

    private ArrayList<PKGINFO> addPKGS(int wpmode,Boolean isDel){
        ArrayList<PKGINFO> pplist = new ArrayList<>();
        if(wpmode == 2 || wpmode == 3){
            if(isDel){
                getUsers();
                for (String s : wpfmUserList) {
                    pplist.add(new PKGINFO(s,s,s,null,null,null,null));
                }
            }else{
                for (int i = 0; i < checkboxsByUser.size(); i++) {
                    if (checkboxsByUser.get(i)) {
                        String s = wpfmUserList.get(i);
                        pplist.add(new PKGINFO(s,s,s,null,null,null,null));
                    }
                }
            }
        }else{
            for (int i = 0; i < checkboxs.size(); i++) {
                if(checkboxs.get(i)){
                    pplist.add(pkginfos.get(i));
                }
            }
        }
        return pplist;
    }

    //分身管理
    private void initwpfm() {
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
            du.showIndexOfPKGSDialog(context,activity,wpfmLv2,et1,pkginfos,null,checkboxs);
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
        showProcessDialogCMDByWP(context,addPKGS(1,false),checkboxsByUser,wpfmUserList,"","正在"+(mode==0?"安装":"删除")+"应用....","当前正在"+(mode==0?"安装":"删除")+": ",1,mode,isRoot);
    }

    //分身删除
    private void initwpfr() {
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
        showProcessDialogCMDByWP(context,addPKGS(2,delall),checkboxsByUser,wpfmUserList,"","正在删除已经选中的分身用户","当前正在删除用户: ",2,0,isRoot);
    }


    private String getMaxUserNum(){
        String cmdstr = "pm get-max-users|cut -d':' -f2";
        CMD cmd = su.getCMD( cmdstr, isRoot);
        return cmd.getResult();
    }

    private void getUsers() {
        clearUser();
        //查询用户
        uu.queryUSERS(activity, wpfmUserList, checkboxsByUser);
    }

    //启动已经创建好的分身用户
    private void startupUsers(){
        showProcessDialogCMDByWP(context,addPKGS(3,true),checkboxsByUser,wpfmUserList,"","正在启动分身用户","当前正在启动用户ID: ",3,0,isRoot);
    }

    //获取用户空间里安装的应用
    private void getPKGByUID(Integer state) {
        pkginfos.clear();
        checkboxs.clear();
        pkginfoHashMap.clear();
        if (wpfmUserList.size() == 0) {
            getUsers();
        }
        ProgressDialog show = du.showMyDialog(context, "正在检索用户下安装的应用,请稍后(可能会出现无响应，请耐心等待)....");
        Handler handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == 0) {
                    du.showPKGS(context,wpfmLv2,pkginfos,checkboxs);
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
                    new packageUtils().getPKGByUID(context,cmdstr,pkginfos,pkginfoHashMap,checkboxs,isRoot);
                }
                pkginfos.clear();
                checkboxs.clear();
                pkginfos.addAll(pkginfoHashMap.values());
                for (PKGINFO pkginfo : pkginfos) {
                    checkboxs.add(false);
                }
                du.sendHandlerMSG(handler, 0);
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
        viewPageIndex = wpmavp.getCurrentItem();
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
        viewPageIndex = wpmavp.getCurrentItem();
        switch (viewPageIndex) {
            case 0:
                switch (itemId) {
                    case 0:
                        du.queryPKGProcessDialog(context,activity,wpfLv,pkginfos,checkboxs,0,null,isRoot);
                        break;
                    case 1:
                        du.queryPKGProcessDialog(context,activity,wpfLv,pkginfos,checkboxs,1,null,isRoot);
                        break;
                    case 2:
                        du.queryPKGProcessDialog(context,activity,wpfLv,pkginfos,checkboxs,2,null,isRoot);
                        break;
                    case 3:
                        du.queryPKGProcessDialog(context,activity,wpfLv,pkginfos,checkboxs,3,null,isRoot);
                        break;
                    case 4:
                        du.showInfoMsg(this, "帮助信息", "该页面是用于应用分身的，需要root授权。\r\n" +
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
                        du.queryPKGProcessDialog(context,activity,wpfmLv2,pkginfos,checkboxs,0,null,isRoot);
                        break;
                    case 1:
                        du.queryPKGProcessDialog(context,activity,wpfmLv2,pkginfos,checkboxs,1,null,isRoot);
                        break;
                    case 2:
                        du.queryPKGProcessDialog(context,activity,wpfmLv2,pkginfos,checkboxs,2,null,isRoot);
                        break;
                    case 3:
                        du.queryPKGProcessDialog(context,activity,wpfmLv2,pkginfos,checkboxs,3,null,isRoot);
                        break;
                    case 4:
                        getPKGByUID(0);
                        break;
                    case 5:
                        getPKGByUID(1);
                        break;
                    case 6:
                        ProgressDialog show = du.showMyDialog(context,"正在获取其它用户(请稍后...)");
                        Handler handler = new Handler(){
                            @Override
                            public void handleMessage(@NonNull Message msg) {
                                if(msg.what==0){
                                    show.dismiss();
                                    du.showUsers(context,wpfmLv,wpfmUserList,checkboxsByUser);
                                }
                            }
                        };
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                getUsers();
                                du.sendHandlerMSG(handler,0);
                            }
                        }).start();
                        break;
                    case 7:
                        startupUsers();
                        break;
                    case 8:
                        du.showInfoMsg(this, "帮助信息", "该页面是用于管理分身应用的，需要root授权。\r\n" +
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
                        ProgressDialog show = du.showMyDialog(context,"正在获取其它用户(请稍后...)");
                        Handler handler = new Handler(){
                            @Override
                            public void handleMessage(@NonNull Message msg) {
                                if(msg.what==0){
                                    show.dismiss();
                                    du.showUsers(context,wpfrLv,wpfmUserList,checkboxsByUser);
                                }
                            }
                        };
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                getUsers();
                                du.sendHandlerMSG(handler,0);
                            }
                        }).start();
                        break;
                    case 1:
                        du.showInfoMsg(this, "帮助信息", "该页面是用于删除分身的，需要root授权。\r\n" +
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

    private void showProcessDialogCMDByWP(Context context , ArrayList<PKGINFO> pplist,ArrayList<Boolean> checkboxsByUser,ArrayList<String> wpfmUserList, String sb, String title, String text, Integer wpmode, Integer wpmode2, Boolean isRoot){
        makeWP wp = new makeWP();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        View vvv = LayoutInflater.from(context).inflate(R.layout.download_process_bar, null);
        ProgressBar mProgressBar = (ProgressBar) vvv.findViewById(R.id.dpbpb);
        TextView dpbtv1 = vvv.findViewById(R.id.dpbtv1);
        TextView dpbtv2 = vvv.findViewById(R.id.dpbtv2);
        TextView dpbtv3 = vvv.findViewById(R.id.dpbtv3);
        builder.setView(vvv);
        dpbtv2.setText("1");
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        du.preventDismissDialog(alertDialog);
        Handler mUpdateProgressHandler = du.getProcessBarDialogHandler(context,mProgressBar,alertDialog,dpbtv1,dpbtv2,dpbtv3,text);

        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {
                int size = pplist.size();
                if(wpmode == 0){
                    UserManager um = (UserManager) context.getSystemService(Context.USER_SERVICE);
                    ArrayList<String> userList = getExistsUsers(um);
                    //初始化初始用户数量
                    CMD cmd = su.getCMD(sb,isRoot);
                    if(cmd.getResultCode()==0){
                        du.sendHandlerMsg(mUpdateProgressHandler,4,"现在正在安装应用到分身空间里面");
                        du.sendHandlerMsg(mUpdateProgressHandler,5,size+"");
                        for (UserHandle userHandle : um.getUserProfiles()) {
                            String uid = uu.getUID(userHandle.toString());
                            //如果用户不在之前获取的列表里面，那么就开始同步选中的应用程序到新建的用户空间下
                            if(haveUser(userList,uid)){
                                hidePKGS(userHandle,isRoot);
                            }
                        }

                        for (int i = 0; i < pplist.size(); i++) {
                            PKGINFO pkginfo = pplist.get(i);
                            du.sendProcessBarHandlerSum(mUpdateProgressHandler,i,size,pkginfo);
                            for (UserHandle userHandle : um.getUserProfiles()) {
                                String uid = uu.getUID(userHandle.toString());
                                //如果用户不在之前获取的列表里面，那么就开始同步选中的应用程序到新建的用户空间下
                                if(haveUser(userList,uid)){
                                    String cmdstr = wp.getInstallPkgCMD(uid, pkginfo.getPkgname())+" && "+wp.getStartWPCMD(uid);
                                    CMD getcmd = su.getCMD(cmdstr, isRoot);
                                }
                            }
                        }
                    }
                }

                if(wpmode == 1){
                    du.sendHandlerMsg(mUpdateProgressHandler,5,size+"");
                    for (int i = 0; i < pplist.size(); i++) {
                        PKGINFO pkginfo = pplist.get(i);
                        du.sendProcessBarHandlerSum(mUpdateProgressHandler,i,size,pkginfo);
                        for (int i1 = 0; i1 < checkboxsByUser.size(); i1++) {
                            if (checkboxsByUser.get(i1)) {
                                String cmdstr = null;
                                if(wpmode2 ==0){
                                    cmdstr = wp.getInstallPkgCMD(wpfmUserList.get(i1), pkginfo.getPkgname());
                                }
                                if(wpmode2 ==1){
                                    cmdstr = wp.getUninstallPkgByUIDCMD(wpfmUserList.get(i1), pkginfo.getPkgname());
                                }
                                CMD getcmd = su.getCMD(cmdstr, isRoot);
                            }
                        }
                    }

                }

                if(wpmode == 2){
                    du.sendHandlerMsg(mUpdateProgressHandler,5,size+"");
                    for (int i = 0; i < pplist.size(); i++) {
                        PKGINFO pkginfo = pplist.get(i);
                        du.sendProcessBarHandlerSum(mUpdateProgressHandler,i,size,pkginfo);
                        String cmdstr = "pm remove-user "+pkginfo.getPkgname();
                        CMD getcmd = su.getCMD(cmdstr, isRoot);
                    }
                }

                if(wpmode == 3){
                    du.sendHandlerMsg(mUpdateProgressHandler,5,size+"");
                    for (int i = 0; i < pplist.size(); i++) {
                        PKGINFO pkginfo = pplist.get(i);
                        du.sendProcessBarHandlerSum(mUpdateProgressHandler,i,size,pkginfo);
                        String cmdstr = "am start-user "+pkginfo.getPkgname();
                        CMD getcmd = su.getCMD(cmdstr, isRoot);
                    }
                }


                mUpdateProgressHandler.sendEmptyMessage(1);
            }
        }).start();
    }


    private void hidePKGS(UserHandle userHandle,Boolean isRoot){
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            StringBuilder sb = new StringBuilder();
            LauncherApps launcherApps = (LauncherApps) getSystemService(Context.LAUNCHER_APPS_SERVICE);
            List<LauncherActivityInfo> activityList = launcherApps.getActivityList(null, userHandle);
            sb.append("aaa=(");
            for (LauncherActivityInfo launcherActivityInfo : activityList) {
                sb.append("\""+launcherActivityInfo.getApplicationInfo().packageName+"\" ");
            }
            sb.append(");for aa in ${aaa[@]};do pm hide --user "+uu.getUID(userHandle.toString())+" $aa ;done;");
            CMD cmd = su.getCMD( sb.toString(), isRoot);
        }
    }

    private ArrayList<String> getExistsUsers(UserManager um){
        ArrayList<String> userList = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            for (UserHandle userHandle : um.getUserProfiles()) {
                userList.add(uu.getUID(userHandle.toString()));
            }
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




}
