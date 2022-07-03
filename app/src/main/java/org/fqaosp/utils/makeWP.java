package org.fqaosp.utils;

import static org.fqaosp.utils.multiFunc.checkBoxs;
import static org.fqaosp.utils.multiFunc.getMyUID;
import static org.fqaosp.utils.multiFunc.queryUSERS;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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
        CMD cmd = new CMD(getInitCMD(),true,false);
        return cmd.getResultCode() == 0;
    }

    public String getCreateWPCMD(){
        return "pm create-user --profileOf "+getMyUID()+" --managed owp";
    }

    //创建一个工作空间
    public Boolean createWP(){
        CMD cmd = new CMD(getCreateWPCMD(),true,false);
        return cmd.getResultCode() == 0;
    }

    public String getRemoveWPCMD(String uid){
        return "am stop-user -f "+uid+" && pm remove-user "+uid;
    }

    //删除一个用户,需要指定uid
    public Boolean removeWP(String uid){
        CMD cmd = new CMD(getRemoveWPCMD(uid),true,false);
        return cmd.getResultCode() == 0;
    }

    public String getStartWPCMD(String uid){
        return "am start-user "+uid;
    }

    //运行一个用户空间，也可以理解为激活
    public Boolean startWP(String uid){
        CMD cmd = new CMD(getStartWPCMD(uid),true,false);
        return cmd.getResultCode() == 0;
    }

    public String getInstallPkgCMD(String uid , String pkgname){
        return "pm install --user "+uid+" -r \"$(pm path "+pkgname+" | cut -d':' -f2 )\"";
    }

    public String getInstallLocalPkgCMD(String uid , String apkPath){
        String localTmpFile="/data/local/tmp/fqaosp.apk";
        return "cp \""+apkPath+"\" " + localTmpFile +" && pm install --user "+uid+" " + localTmpFile + " && exit 0;";
    }

    //同步传过来的pkginfo对象，将对应的apk都同步安装到其他用户空间里面
    public void syncapk(Activity activity, PKGINFO pkginfo){
        ArrayList<String> list = new ArrayList<>();
        queryUSERS(activity,list);
        for (String userid : list) {
            startWP(userid);
            String pkgname = pkginfo.getPkgname();
            CMD cmd = new CMD(getInstallPkgCMD(userid,pkgname),true,false);
            cmd.getResultCode();
        }
    }

    public String getUserPkgByUIDCMD(String uid){
        return "pm list packages --user "+uid+" -3 | cut -d':' -f2 ";
    }
    public String getPkgByUIDCMD(String uid){
        return "pm list packages --user "+uid+" | cut -d':' -f2 ";
    }

    public String getDisablePkgByUIDCMD(String uid){
        return "pm list packages --user "+uid+" -d | cut -d':' -f2 ";
    }

    public String getUninstallPkgByUIDCMD(String uid,String pkgname){
        return "pm uninstall --user "+uid+"  "+pkgname;
    }

    public void addCMDResult(String cmdstr , Activity activity,ArrayList<PKGINFO> pkginfos , ArrayList<Boolean> checkboxs){
        try {
            CMD cmd = new CMD(cmdstr,true,false);
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
