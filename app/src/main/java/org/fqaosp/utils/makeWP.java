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

    //初始化工作资料空间最大可创建数量为1024
    public Boolean init(){
        CMD cmd = new CMD("setprop persist.sys.max_profiles 1024 && setprop fw.max_users 1024");
        return cmd.getResultCode() == 0;
    }

    //创建一个工作空间
    public Boolean createWP(){
        CMD cmd = new CMD("pm create-user --profileOf "+getMyUID()+" --managed owp");
        return cmd.getResultCode() == 0;
    }

    //删除一个用户,需要指定uid
    public Boolean removeWP(String uid){
        CMD cmd = new CMD("am stop-user -f "+uid+" && pm remove-user "+uid);
        return cmd.getResultCode() == 0;
    }

    //运行一个用户空间，也可以理解为激活
    public Boolean startWP(String uid){
        CMD cmd = new CMD("am start-user "+uid);
        return cmd.getResultCode() == 0;
    }

    //同步传过来的pkginfo对象，将对应的apk都同步安装到其他用户空间里面
    public void syncapk(Activity activity, PKGINFO pkginfo){
        ArrayList<String> list = new ArrayList<>();
        queryUSERS(activity,list);
        for (String userid : list) {
            startWP(userid);
            String pkgname = pkginfo.getPkgname();
            CMD cmd = new CMD("pm install --user "+userid+" -r \"$(pm path "+pkgname+" | cut -d':' -f2 )\"");
            cmd.getResultCode();
        }
    }


}
