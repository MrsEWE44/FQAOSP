package org.fqaosp.utils;

import static org.fqaosp.utils.multiFunc.*;

import android.app.Activity;

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
        return "pm create-user --profileOf "+getMyUID()+" --managed owp";
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

    //同步传过来的pkginfo对象，将对应的apk都同步安装到其他用户空间里面
    public void syncapk(Activity activity, PKGINFO pkginfo){
        ArrayList<String> list = new ArrayList<>();
        queryUSERS(activity,list);
        for (String userid : list) {
            startWP(userid);
            String pkgname = pkginfo.getPkgname();
            CMD cmd = new CMD(getInstallPkgCMD(userid,pkgname));
            cmd.getResultCode();
        }
    }


}
