package org.fqaosp.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Process;
import android.os.UserHandle;
import android.os.UserManager;

import java.util.ArrayList;
import java.util.Collections;

public class userUtils {

    public userUtils(){}

    //查询用户，默认
    public void queryUSERS(Activity activity , ArrayList<String> us){
        queryUSERS(activity,us,null);
    }

    //查询用户
    public void queryUSERS(Activity activity ,  ArrayList<String> us , ArrayList<Boolean> checkboxs){
        us.clear();
        UserManager um = (UserManager) activity.getSystemService(Context.USER_SERVICE);
        if(checkboxs != null){
            checkboxs.clear();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                for (UserHandle userHandle : um.getUserProfiles()) {
                    String uid = getUID(userHandle.toString());
                    if(!uid.equals(getMyUID())){
                        us.add(uid);
                        checkboxs.add(false);
                    }
                }
            }
        }else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                for (UserHandle userHandle : um.getUserProfiles()) {
                    String uid = getUID(userHandle.toString());
                    if(!uid.equals(getMyUID())){
                        us.add(uid);
                    }
                }
            }
        }

        Collections.sort(us,String::compareTo);

    }



    //移除uid多余字符串
    public String getUID(String str){
        return str.replaceAll("UserHandle|\\{|\\}","");
    }

    //获取自己当前应用的uid
    public String getMyUID(){
        return getUID(Process.myUserHandle().toString());
    }


}
