package org.fqaosp.utils;

public class iptablesManage {

    public String disableAppByAPPUIDCMD(String uid){
        String cmd = "iptables -I OUTPUT -m owner --uid-owner "+uid +" -j DROP";
        return cmd;
    }

    public String enableAppByAPPUIDCMD(String uid){
        String cmd = "iptables -I OUTPUT -m owner --uid-owner "+uid +" -j ACCEPT";
        return cmd;
    }

}
