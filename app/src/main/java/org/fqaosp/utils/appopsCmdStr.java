package org.fqaosp.utils;

public class appopsCmdStr {
    private String localTmpFile="/data/local/tmp/fqaosp.apk";

    public String getInstallLocalPkgCMD(String uid , String apkPath){
        return "rm -rf "+localTmpFile+" && cp \""+apkPath+"\" " + localTmpFile +" && pm install --user "+uid+" " + localTmpFile;
    }

    public String getInstallLocalPkgOnDowngradeCMD(String uid , String apkPath){
        return "rm -rf "+localTmpFile+" && cp \""+apkPath+"\" " + localTmpFile +" && pm install --user "+uid+" -r -d " + localTmpFile;
    }

    public String getInstallLocalPkgOnDebugCMD(String uid , String apkPath){
        return "rm -rf "+localTmpFile+" && cp \""+apkPath+"\" " + localTmpFile +" && pm install --user "+uid+" -r -t " + localTmpFile;
    }

    public String getInstallLocalPkgOnExistsCMD(String uid , String apkPath){
        return "rm -rf "+localTmpFile+" && cp \""+apkPath+"\" " + localTmpFile +" && pm install --user "+uid+" -r " + localTmpFile;
    }


}
