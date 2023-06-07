package org.fqaosp.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import org.fqaosp.entity.PKGINFO;
import org.fqaosp.entity.ProcessEntity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class processUtils {

    private packageUtils pkgutils = new packageUtils();
    private stringUtils su = new stringUtils();

    public processUtils(){}
    public void queryRunningPKGSCore(List<PackageInfo> installedPackages, ArrayList<PKGINFO> pkginfos , ArrayList<Boolean> checkboxs, PackageManager packageManager, boolean isAll){
        for (PackageInfo packageInfo : installedPackages) {
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            if (isAll) {
                if (((ApplicationInfo.FLAG_STOPPED & applicationInfo.flags) == 0)) {
                    pkgutils.checkBoxs(pkginfos,checkboxs,packageInfo,packageManager);
                }
            } else {
                if (((ApplicationInfo.FLAG_SYSTEM & applicationInfo.flags) == 0)
                        && ((ApplicationInfo.FLAG_UPDATED_SYSTEM_APP & applicationInfo.flags) == 0)
                        && ((ApplicationInfo.FLAG_STOPPED & applicationInfo.flags) == 0)) {
                    pkgutils.checkBoxs(pkginfos,checkboxs,packageInfo,packageManager);
                }

            }
        }
        pkgutils.sortPKGINFOS(pkginfos);
    }

    public String getProcessValue(String src,String key){
        return su.getByString(src,key+":(.+?\\n)",key+":");
    }

    //查询/proc路径下所有进程
    public Set<ProcessEntity> queryAllProcess(Context context , boolean isRoot){
        Set<ProcessEntity> set = new HashSet<>();
        String cmdstr = "for p in `ls /proc |grep -E \"[0-9]\"`;do cat \"/proc/$p/status\";echo \"FQAOSPLINE\";done";
        CMD cmd = new shellUtils().getCMD( cmdstr, isRoot);
        if(cmd.getResultCode()==0){
            for (String s : su.getByAllString(cmd.getResult(), "Name:([\\s\\S]*)FQAOSPLINE", "").split("FQAOSPLINE")) {
                if(!s.trim().isEmpty()){
                    set.add(new ProcessEntity(getProcessValue(s,"Name"),getProcessValue(s,"Umask"),getProcessValue(s,"State"),getProcessValue(s,"Tgid"),
                            getProcessValue(s,"Ngid"),getProcessValue(s,"Pid"),getProcessValue(s,"PPid"),getProcessValue(s,"TracerPid"),getProcessValue(s,"Uid"),
                            getProcessValue(s,"Gid"),getProcessValue(s,"FDSize"),getProcessValue(s,"Groups"),getProcessValue(s,"VmPeak"),getProcessValue(s,"VmSize"),getProcessValue(s,"VmLck"),
                            getProcessValue(s,"VmPin"),getProcessValue(s,"VmHWM"),getProcessValue(s,"VmRSS"),getProcessValue(s,"RssAnon"),getProcessValue(s,"RssFile"),
                            getProcessValue(s,"RssShmem"),getProcessValue(s,"VmData"),getProcessValue(s,"VmStk"),getProcessValue(s,"VmExe"),getProcessValue(s,"VmLib"),getProcessValue(s,"VmPTE"),
                            getProcessValue(s,"VmSwap"),getProcessValue(s,"CoreDumping"),getProcessValue(s,"Threads"),getProcessValue(s,"SigQ"),getProcessValue(s,"SigPnd"),getProcessValue(s,"ShdPnd"),
                            getProcessValue(s,"SigBlk"),getProcessValue(s,"SigIgn"),getProcessValue(s,"SigCgt"),getProcessValue(s,"CapInh"),getProcessValue(s,"CapPrm"),getProcessValue(s,"CapEff"),getProcessValue(s,"CapBnd"),
                            getProcessValue(s,"CapAmb"),getProcessValue(s,"NoNewPrivs"),getProcessValue(s,"Seccomp"),getProcessValue(s,"Speculation_Store_Bypass"),getProcessValue(s,"Cpus_allowed"),getProcessValue(s,"Cpus_allowed_list"),getProcessValue(s,"Mems_allowed"),getProcessValue(s,"Mems_allowed_list"),
                            getProcessValue(s,"voluntary_ctxt_switches"),getProcessValue(s,"nonvoluntary_ctxt_switches")
                    ));
                }
            }
        }
        return  set;
    }


    //查询当前运行在后台的应用，用户安装部分
    public void queryRunningPKGS(Activity activity, ArrayList<PKGINFO> pkginfos , ArrayList<Boolean> checkboxs, Integer types){
        PackageManager packageManager = activity.getPackageManager();
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(types);
        queryRunningPKGSCore(installedPackages,pkginfos,checkboxs,packageManager,false);
    }

    //查询当前运行在后台的应用，所有
    public void queryAllRunningPKGS(Activity activity, ArrayList<PKGINFO> pkginfos , ArrayList<Boolean> checkboxs,Integer types){
        PackageManager packageManager = activity.getPackageManager();
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(types);
        queryRunningPKGSCore(installedPackages,pkginfos,checkboxs,packageManager,true);
    }

}
