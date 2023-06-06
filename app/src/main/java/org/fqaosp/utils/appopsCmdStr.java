package org.fqaosp.utils;

import android.os.Build;

public class appopsCmdStr {
    private String localTmpFile="/data/local/tmp/fqaosp.apk";
    private String rm_local_tmp_file="rm -rf "+localTmpFile;
    private String install_cmd_head=rm_local_tmp_file+" && cp \"";
    private String install_cmd_end="\" " + localTmpFile +" && chmod 777 "+localTmpFile;

    public String getInstallLocalPkgCMD(String uid , String apkPath){
        if(Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT){
            return install_cmd_head+apkPath+install_cmd_end+" && pm install " + localTmpFile+" && "+rm_local_tmp_file;
        }
        return install_cmd_head+apkPath+install_cmd_end+" && pm install --user "+uid+" " + localTmpFile+" && "+rm_local_tmp_file;
    }

    public String getInstallLocalPkgOnDowngradeCMD(String uid , String apkPath){
        if(Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT){
            return install_cmd_head+apkPath+install_cmd_end+" && pm install -r -d " + localTmpFile+" && "+rm_local_tmp_file;
        }
        return install_cmd_head+apkPath+install_cmd_end+" && pm install --user "+uid+" -r -d " + localTmpFile+" && "+rm_local_tmp_file;
    }

    public String getInstallLocalPkgOnDebugCMD(String uid , String apkPath){
        if(Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT){
            return install_cmd_head+apkPath+install_cmd_end+" && pm install -r -t " + localTmpFile+" && "+rm_local_tmp_file;
        }
        return install_cmd_head+apkPath+install_cmd_end+" && pm install --user "+uid+" -r -t " + localTmpFile+" && "+rm_local_tmp_file;
    }

    public String getInstallLocalPkgOnExistsCMD(String uid , String apkPath){
        if(Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT){
            return install_cmd_head+apkPath+install_cmd_end+" && pm install  -r " + localTmpFile+" && "+rm_local_tmp_file;
        }
        return install_cmd_head+apkPath+install_cmd_end+" && pm install --user "+uid+" -r " + localTmpFile+" && "+rm_local_tmp_file;
    }

    public String getPKGUIDByCMD(String pkgname){
        if(Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT){
            return "cat /data/system/packages.xml |grep "+pkgname+"|cut -d' ' -f14|cut -d'\"' -f2";
        }
        return "pm list packages -U  "+pkgname+"|cut -d ':' -f3";
    }

    public String disableAppByAPPUIDCMD(String pkgname){
        String cmd = "iptables -I OUTPUT -m owner --uid-owner $("+getPKGUIDByCMD(pkgname)+") -j DROP";
        return cmd;
    }

    public String disableAppByAPPUIDCMD(int pkguid){
        String cmd = "iptables -I OUTPUT -m owner --uid-owner "+pkguid+" -j DROP";
        return cmd;
    }

    public String enableAppByAPPUIDCMD(String pkgname){
        String cmd = "iptables -I OUTPUT -m owner --uid-owner $("+getPKGUIDByCMD(pkgname)+") -j ACCEPT";
        return cmd;
    }

    public String enableAppByAPPUIDCMD(int pkguid){
        String cmd = "iptables -I OUTPUT -m owner --uid-owner "+pkguid+" -j ACCEPT";
        return cmd;
    }

}
