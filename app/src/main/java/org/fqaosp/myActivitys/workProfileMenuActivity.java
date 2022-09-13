package org.fqaosp.myActivitys;

import static org.fqaosp.utils.multiFunc.checkBoxsHashMap;
import static org.fqaosp.utils.multiFunc.dismissDialogHandler;
import static org.fqaosp.utils.multiFunc.preventDismissDialog;
import static org.fqaosp.utils.multiFunc.queryUSERS;
import static org.fqaosp.utils.multiFunc.sendHandlerMSG;
import static org.fqaosp.utils.multiFunc.showInfoMsg;
import static org.fqaosp.utils.multiFunc.showMyDialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import org.fqaosp.entity.workProfileDBEntity;
import org.fqaosp.sql.workProfileDB;
import org.fqaosp.utils.CMD;
import org.fqaosp.utils.fuckActivity;
import org.fqaosp.utils.makeWP;
import org.fqaosp.utils.multiFunc;

import java.util.ArrayList;
import java.util.HashMap;

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
    private ListView wpfLv, wpfmLv, wpfrLv;
    private HashMap<String, PKGINFO> pkginfoHashMap = new HashMap<>();
    private ArrayList<Boolean> checkboxsByUser = new ArrayList<>();
    private ArrayList<String> wpfmUserList = new ArrayList<>();
    private workProfileDB workProfiledb = new workProfileDB(workProfileMenuActivity.this, "workProfile", null, 1);
    private makeWP wp = new makeWP();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.work_profile_menu_activity);
        fuckActivity.getIns().add(this);
        setTitle("分身部分");
        initViews();
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
        ArrayList<String> users = new ArrayList<>();
        Button b1 = wpf.findViewById(R.id.wpb1);
        Button b2 = wpf.findViewById(R.id.wpb2);
        wpfLv = wpf.findViewById(R.id.wplv1);
        EditText editText1 = wpf.findViewById(R.id.wpet1);
        EditText editText2 = wpf.findViewById(R.id.wpet2);
        showInfoMsg(context,"警告","分身部分不能一次性开启太多，不然的话会闪退或者无响应");
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog show = showMyDialog(context, "提示", "正在创建分身空间,请稍后(可能会出现无响应，请耐心等待)....");
                Handler handler = dismissDialogHandler(0, show);
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        Integer num = Integer.valueOf(editText1.getText().toString());
                        checkUser(wp, activity, workProfiledb, users);
                        if (num < wp.getInitsize() && num > 0) {
                            if (wp.init()) {
                                for (int j = 0; j < num; j++) {
                                    //创建工作资料空间
                                    if (wp.createWP()) {
                                        Log.d("workP ", " is create ok !!!");
                                    }
                                }
                            }
                            try {
                                queryUSERS(activity, users);
                                for (int i = 0; i < checkboxs.size(); i++) {
                                    if (checkboxs.get(i)) {
                                        PKGINFO pkginfo = pkginfos.get(i);
                                        //同步所有空间都安装选中的应用
                                        for (String userid : users) {
                                            wp.startWP(userid);
                                            String pkgname = pkginfo.getPkgname();
                                            //从数据库里查询，如果不存在该用户以及相关包名，则允许安装与插入数据库
                                            ArrayList<workProfileDBEntity> select = workProfiledb.select(pkgname, Integer.valueOf(userid));
                                            if (select.size() == 0) {
                                                CMD cmd = new CMD(wp.getInstallPkgCMD(userid, pkgname));
                                                if (cmd.getResultCode() == 0) {
                                                    workProfiledb.insert(pkgname, Integer.valueOf(userid));
                                                } else {
                                                    Log.d("error wp cmd ::: ", cmd.getResultCode() + " -- " + cmd.getResult());
                                                }
                                            }
                                        }
                                    }
                                }
                                checkUser(wp, activity, workProfiledb, users);
                                editText1.setText("");
                                Toast.makeText(context, "全部新增成功", Toast.LENGTH_LONG).show();
                                sendHandlerMSG(handler, 0);
                            } catch (Exception e) {
                                Toast.makeText(context, "wpa :: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(context, "请输入 " + wp.getInitsize() + " 以内并且大于0的数值", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchStr = editText2.getText().toString();
                pkginfos = multiFunc.indexOfPKGS(activity, searchStr, pkginfos, checkboxs, 0);
                showPKGS(wpfLv);
            }
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

    //分身管理
    private void initwpfm() {
        Context context = this;
        Activity activity = this;
        Button b1 = wpfm.findViewById(R.id.wpmanageab1);
        Button b2 = wpfm.findViewById(R.id.wpmanageab2);
        Button b3 = wpfm.findViewById(R.id.wpmanageab3);
        wpfmLv = wpfm.findViewById(R.id.wpmanagealv1);
        ListView lv2 = wpfm.findViewById(R.id.wpmanagealv2);
        EditText et1 = wpfm.findViewById(R.id.wpmanageaet1);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog show = showMyDialog(context, "提示", "正在安装应用,请稍后(可能会出现无响应，请耐心等待)....");
                preventDismissDialog(show);
                Handler handler = new Handler() {
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        if (msg.what == 0) {
                            getPKGByUID(1);
                            multiFunc.dismissDialog(show);
                        }
                    }
                };
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < checkboxs.size(); i++) {
                            if (checkboxs.get(i)) {
                                PKGINFO pkginfo = pkginfos.get(i);
                                String pkgname = pkginfo.getPkgname();
                                for (int i1 = 0; i1 < checkboxsByUser.size(); i1++) {
                                    if (checkboxsByUser.get(i1)) {
                                        String s = wpfmUserList.get(i1);
                                        CMD cmd = new CMD(wp.getInstallPkgCMD(s, pkgname));
                                        //判断该用户下有没有安装该应用,如果有就跳过
                                        ArrayList<workProfileDBEntity> select = workProfiledb.select(pkgname, Integer.valueOf(s));
                                        if (select.size() == 0) {
                                            workProfiledb.insert(pkgname, Integer.valueOf(s));
                                        } else {
                                            Log.d("wpma insert error ::: ", pkgname + " --  uid ::: " + s + " -- " + select.size() + " -- cmd :::  " + cmd.getResultCode() + " -- " + cmd.getResult());
                                        }
                                    }
                                }
                            }

                        }
                        sendHandlerMSG(handler,0);
                    }
                });
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog show = showMyDialog(context, "提示", "正在删除应用,请稍后(可能会出现无响应，请耐心等待)....");
                preventDismissDialog(show);
                Handler handler = new Handler() {
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        if (msg.what == 0) {
                            getPKGByUID(1);
                            multiFunc.dismissDialog(show);
                        }
                    }
                };
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < checkboxs.size(); i++) {
                            if (checkboxs.get(i)) {
                                PKGINFO pkginfo = pkginfos.get(i);
                                String pkgname = pkginfo.getPkgname();
                                for (int i1 = 0; i1 < checkboxsByUser.size(); i1++) {
                                    if (checkboxsByUser.get(i1)) {
                                        String s = wpfmUserList.get(i1);
                                        CMD cmd = new CMD(wp.getUninstallPkgByUIDCMD(s, pkgname));
                                        //判断该用户下有没有安装该应用,如果有就跳过
                                        ArrayList<workProfileDBEntity> select = workProfiledb.select(pkgname, Integer.valueOf(s));
                                        if (select.size() > 0) {
                                            workProfiledb.delete(pkgname, Integer.valueOf(s));
                                        } else {
                                            Log.d("wpma delete error ::: ", pkgname + " --  uid ::: " + s + " -- " + select.size() + " -- cmd :::  " + cmd.getResultCode() + " -- " + cmd.getResult());
                                        }
                                    }
                                }
                            }
                        }
                        sendHandlerMSG(handler,0);
                    }
                });
            }
        });

        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchStr = et1.getText().toString();
                pkginfos = multiFunc.indexOfPKGS(activity, searchStr, pkginfos, checkboxs, 0);
                showPKGS(lv2);
            }
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

        getUsers();
        showUsers(wpfmLv);

    }

    //分身删除
    private void initwpfr() {
        Context context = this;
        Button b1 = wpfr.findViewById(R.id.wprab1);
        Button b2 = wpfr.findViewById(R.id.wprab2);
        wpfrLv = wpfr.findViewById(R.id.wpralv1);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog show = showMyDialog(context, "提示", "正在删除已经选中的分身用户,请稍后(可能会出现无响应，请耐心等待)....");
                preventDismissDialog(show);
                Handler handler = new Handler() {
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        if (msg.what == 0) {
                            getUsers();
                            showUsers(wpfrLv);
                            multiFunc.dismissDialog(show);
                        }
                    }
                };
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < checkboxs.size(); i++) {
                            if (checkboxs.get(i)) {
                                //移除掉勾选的内容
                                delete(wpfmUserList.get(i));
                            }
                        }
                        sendHandlerMSG(handler, 0);
                    }
                });
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog show = showMyDialog(context, "提示", "正在删除分身用户,请稍后(可能会出现无响应，请耐心等待)....");
                preventDismissDialog(show);
                Handler handler = new Handler() {
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        if (msg.what == 0) {
                            getUsers();
                            showUsers(wpfrLv);
                            multiFunc.dismissDialog(show);
                        }
                    }
                };
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        for (String s : wpfmUserList) {
                            delete(s);
                        }
                        sendHandlerMSG(handler, 0);
                    }
                });
            }
        });
    }

    private void delete(String s) {
        workProfiledb.delete(null, Integer.valueOf(s));
        CMD cmd = new CMD(wp.getRemoveWPCMD(s));
        cmd.getResultCode();
    }

    private void checkUser(makeWP makewp, Activity activity, workProfileDB workProfiledb, ArrayList<String> users) {
        queryUSERS(activity, users);
        for (String userid : users) {
            ArrayList<workProfileDBEntity> treeMap = workProfiledb.select(null, Integer.valueOf(userid));
            //如果没有找到该用户uid，则添加进数据库
            if (treeMap.size() == 0) {
                //获取该用户下所有安装的包名
                makewp.addCMDResult(makewp.getUserPkgByUIDCMD(userid), activity, pkginfos, checkboxs);
            }
            for (PKGINFO pkginfo : pkginfos) {
                ArrayList<workProfileDBEntity> select = workProfiledb.select(pkginfo.getPkgname(), Integer.valueOf(userid));
                if (select.size() == 0) {
                    workProfiledb.insert(pkginfo.getPkgname(), Integer.valueOf(userid));
                }
            }
        }
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

    //获取用户空间里安装的应用
    private void getPKGByUID(Integer state) {
        pkginfos.clear();
        checkboxs.clear();
        pkginfoHashMap.clear();
        makeWP wp = new makeWP();
        if (wpfmUserList.size() == 0) {
            getUsers();
        }
        AlertDialog show = showMyDialog(workProfileMenuActivity.this, "提示", "正在检索用户下安装的应用,请稍后(可能会出现无响应，请耐心等待)....");
        preventDismissDialog(show);
        Handler handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == 0) {
                    showPKGS(wpfmLv);
                    multiFunc.dismissDialog(show);
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
                                e.printStackTrace();
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
                menu.add(Menu.NONE, 6, 6, "帮助");
                menu.add(Menu.NONE, 7, 7, "退出");
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
                        showPKGS(wpfmLv);
                        break;
                    case 1:
                        getPKGS();
                        showPKGS(wpfmLv);
                        break;
                    case 2:
                        getUserPKGS();
                        showPKGS(wpfmLv);
                        break;
                    case 3:
                        getUserEnablePKGS();
                        showPKGS(wpfmLv);
                        break;
                    case 4:
                        getPKGByUID(0);
                        break;
                    case 5:
                        getPKGByUID(1);
                        break;
                    case 6:
                        showInfoMsg(this, "帮助信息", "该页面是用于管理分身应用的，需要root授权。\r\n" +
                                "1.右上角三个点，显示主用户安装应用，会列出当前用户已经安装的应用。\r\n" +
                                "2.右上角三个点，显示其它用户安装应用，会列出当其它分身用户已经安装的应用，但是不会显示多余的，遇到重复的会只列出一个。\r\n" +
                                "3.同步安装一个应用到分身，从当前主用户已安装的应用当中，批量勾选安装到其它分身用户下面。\r\n" +
                                "4.从分身删除应用，勾选分身里面的应用，批量进行卸载操作。\r\n" +
                                "5.搜索框支持中英文搜索，不区分大小写.\r\n" +
                                "6.点击分身应用，可以进入到该软件自带的应用管理当中，可以进行服务禁用、活动项禁用，权限管控之类的，等等。\r\n"
                        );
                        break;
                    case 7:
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
