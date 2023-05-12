package org.fqaosp.utils;

import android.os.Build;

public class appopsCmdStr {
    private String localTmpFile="/data/local/tmp/fqaosp.apk";

    public String getInstallLocalPkgCMD(String uid , String apkPath){
        if(Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT){
            return "rm -rf "+localTmpFile+" && cp \""+apkPath+"\" " + localTmpFile +" && chmod 777 "+localTmpFile+" && pm install " + localTmpFile;
        }
        return "rm -rf "+localTmpFile+" && cp \""+apkPath+"\" " + localTmpFile +" && chmod 777 "+localTmpFile+" && pm install --user "+uid+" " + localTmpFile;
    }

    public String getInstallLocalPkgOnDowngradeCMD(String uid , String apkPath){
        if(Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT){
            return "rm -rf "+localTmpFile+" && cp \""+apkPath+"\" " + localTmpFile +" && chmod 777 "+localTmpFile+" && pm install -r -d " + localTmpFile;
        }
        return "rm -rf "+localTmpFile+" && cp \""+apkPath+"\" " + localTmpFile +" && chmod 777 "+localTmpFile+" && pm install --user "+uid+" -r -d " + localTmpFile;
    }

    public String getInstallLocalPkgOnDebugCMD(String uid , String apkPath){
        if(Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT){
            return "rm -rf "+localTmpFile+" && cp \""+apkPath+"\" " + localTmpFile +" && chmod 777 "+localTmpFile+" && pm install -r -t " + localTmpFile;
        }
        return "rm -rf "+localTmpFile+" && cp \""+apkPath+"\" " + localTmpFile +" && chmod 777 "+localTmpFile+" && pm install --user "+uid+" -r -t " + localTmpFile;
    }

    public String getInstallLocalPkgOnExistsCMD(String uid , String apkPath){
        if(Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT){
            return "rm -rf "+localTmpFile+" && cp \""+apkPath+"\" " + localTmpFile +" && chmod 777 "+localTmpFile+" && pm install  -r " + localTmpFile;
        }
        return "rm -rf "+localTmpFile+" && cp \""+apkPath+"\" " + localTmpFile +" && chmod 777 "+localTmpFile+" && pm install --user "+uid+" -r " + localTmpFile;
    }

    public String getPKGUIDByCMD(String pkgname){
        if(Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT){
            return "cat /data/system/packages.xml |grep "+pkgname+"|cut -d' ' -f14|cut -d'\"' -f2";
        }
        return "pm list packages -U|grep "+pkgname+"|cut -d ':' -f3";
    }

    public String disableAppByAPPUIDCMD(String pkgname){
        String cmd = "iptables -I OUTPUT -m owner --uid-owner $("+getPKGUIDByCMD(pkgname)+") -j DROP";
        return cmd;
    }

    public String enableAppByAPPUIDCMD(String pkgname){
        String cmd = "iptables -I OUTPUT -m owner --uid-owner $("+getPKGUIDByCMD(pkgname)+") -j ACCEPT";
        return cmd;
    }

}
