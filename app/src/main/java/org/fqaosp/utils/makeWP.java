package org.fqaosp.utils;

import static org.fqaosp.utils.multiFunc.checkBoxs;
import static org.fqaosp.utils.multiFunc.getMyUID;
import static org.fqaosp.utils.multiFunc.queryUSERS;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import org.fqaosp.entity.PKGINFO;

import java.util.ArrayList;


/**
 *
 * 创建工作资料空间功能集合
 *
 * */

public class makeWP {

    public int getInitsize() {
        return initsize==0?1024:initsize;
    }

    public void setInitsize(int initsize) {
        this.initsize = initsize;
    }

    private int initsize = 0;

    public String getInitCMD(){
        return "setprop persist.sys.max_profiles "+getInitsize()+" && setprop fw.max_users "+getInitsize();
    }

    //初始化工作资料空间最大可创建数量为1024
    public Boolean init(){
        CMD cmd = new CMD(getInitCMD());
        return cmd.getResultCode() == 0;
    }

    public String getCreateWPCMD(){
        return "pm create-user --profileOf "+getMyUID()+" --managed fqaosop";
    }

    //创建一个工作空间
    public Boolean createWP(){
        CMD cmd = new CMD(getCreateWPCMD());
        return cmd.getResultCode() == 0;
    }

    public String getRemoveWPCMD(String uid){
        return "am stop-user -f "+uid+" && pm remove-user "+uid;
    }

    //删除一个用户,需要指定uid
    public Boolean removeWP(String uid){
        CMD cmd = new CMD(getRemoveWPCMD(uid));
        return cmd.getResultCode() == 0;
    }

    public String getStartWPCMD(String uid){
        return "am start-user "+uid;
    }

    //运行一个用户空间，也可以理解为激活
    public Boolean startWP(String uid){
        CMD cmd = new CMD(getStartWPCMD(uid));
        return cmd.getResultCode() == 0;
    }

    public String getInstallPkgCMD(String uid , String pkgname){
        return "pm install --user "+uid+" -r \"$(pm path "+pkgname+" | cut -d':' -f2 )\"";
    }

    public String getUserPkgByUIDCMD(String uid){
        return "pm list packages --user "+uid+" -3 | cut -d':' -f2";
    }
    public String getPkgByUIDCMD(String uid){
        return "pm list packages --user "+uid+"| cut -d':' -f2";
    }

    public String getChangePkgOnEnableByUIDCMD(String uid,String pkgname){
        return "pm enable --user "+uid + " " + pkgname;
    }

    public String getChangePkgOnDisableByUIDCMD(String uid,String pkgname){
        return "pm clear --user "+ uid + " "+pkgname+" && pm disable-user --user "+uid + " " + pkgname;
    }

    public String getDisablePkgByUIDCMD(String uid){
        return "pm list packages --user "+uid+" -d | cut -d':' -f2";
    }

    public String getUninstallPkgByUIDCMD(String uid,String pkgname){
        if(Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT){
            return "pm uninstall "+pkgname;
        }
        return "pm uninstall --user "+uid+" "+pkgname;
    }

    public void addCMDResult(String cmdstr , Activity activity,ArrayList<PKGINFO> pkginfos , ArrayList<Boolean> checkboxs){
        try {
            CMD cmd = new CMD(cmdstr);
            if(cmd.getResultCode() ==0){
                pkginfos.clear();
                for (String s : cmd.getResult().split("\n")) {
                    PackageManager pm = activity.getPackageManager();
                    PackageInfo packageInfo = pm.getPackageInfo(s, 0);
                    checkBoxs(pkginfos,checkboxs,packageInfo,pm);
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void getPkgByUID(Activity activity,String uid, ArrayList<PKGINFO> pkginfos , ArrayList<Boolean> checkboxs){
        addCMDResult(getPkgByUIDCMD(uid),activity,pkginfos,checkboxs);
    }

    public void getUserPkgByUID(Activity activity,String uid, ArrayList<PKGINFO> pkginfos , ArrayList<Boolean> checkboxs){
        addCMDResult(getUserPkgByUIDCMD(uid),activity,pkginfos,checkboxs);
    }

    public void getDisablePkgByUID(Activity activity,String uid, ArrayList<PKGINFO> pkginfos , ArrayList<Boolean> checkboxs){
        addCMDResult(getDisablePkgByUIDCMD(uid),activity,pkginfos,checkboxs);
    }

}
