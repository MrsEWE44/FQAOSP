package org.fqaosp.utils;

import org.fqaosp.service.adbSocketClient;

import java.io.File;

public class shellUtils {

    public shellUtils(){}

    public CMD runADBCmd(String cmdstr){
        adbSocketClient adbSocketClient2 = new adbSocketClient(cmdstr,new adbSocketClient.SocketListener() {
            @Override
            public void getCMD(CMD cmd) {
//                Log.d("runADBCMD",cmd.toString());
            }
        });
        return adbSocketClient2.getCMD();
    }

    /**
     * 是否存在su命令，并且有执行权限
     *
     * @return 存在su命令，并且有执行权限返回true
     */
    public boolean isSuEnable() {
        File file = null;
        String[] paths = {"/system/bin/", "/system/xbin/", "/system/sbin/", "/sbin/", "/vendor/bin/", "/su/bin/"};
        try {
            for (String path : paths) {
                file = new File(path + "su");
                if (file.exists() && file.canExecute()) {
                    return testRoot();
                }
            }
        } catch (Exception x) {
            x.printStackTrace();
        }
        return false;
    }

    public boolean testRoot(){
        CMD cmd = new CMD("id");
        return cmd.getResultCode() == 0;
    }

    public boolean isADB(){
        CMD cmd = runADBCmd("id|grep shell");
        return !cmd.toString().isEmpty();
    }

    public CMD getCMD(String cmdstr,Boolean isRoot){
        if(isRoot){
            return new CMD(cmdstr);
        }else{
            return runADBCmd(cmdstr);
        }
    }

}
