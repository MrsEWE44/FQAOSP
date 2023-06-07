package org.fqaosp.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import org.fqaosp.entity.PKGINFO;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class packageUtils {

    public  Integer QUERY_ALL_USER_ENABLE_PKG=2;
    public  Integer QUERY_ALL_ENABLE_PKG=0;
    public  Integer QUERY_ALL_USER_PKG=3;
    public  Integer QUERY_ALL_DISABLE_PKG=1;
    public  Integer QUERY_ALL_DEFAULT_PKG=4;

    public packageUtils(){}

    //查询当前机主安装的应用
    public void queryPKGS(Activity activity, ArrayList<PKGINFO> pkginfos , ArrayList<Boolean> checkboxs, Integer types){
        clearList(pkginfos,checkboxs);
        queryPKGSCore(activity,pkginfos,checkboxs,types,QUERY_ALL_DEFAULT_PKG);
    }

    //查询当前机主安装的应用,用户部分
    public void queryUserPKGS(Activity activity, ArrayList<PKGINFO> pkginfos , ArrayList<Boolean> checkboxs,Integer types){
        clearList(pkginfos,checkboxs);
        queryPKGSCore(activity,pkginfos,checkboxs,types,QUERY_ALL_USER_PKG);
    }

    //查询当前机主安装的应用,用户启用部分
    public void queryUserEnablePKGS(Activity activity, ArrayList<PKGINFO> pkginfos , ArrayList<Boolean> checkboxs,Integer types){
        clearList(pkginfos,checkboxs);
        queryPKGSCore(activity,pkginfos,checkboxs,types,QUERY_ALL_USER_ENABLE_PKG);
    }

    //查询当前机主安装的应用,禁用部分
    public void queryDisablePKGS(Activity activity, ArrayList<PKGINFO> pkginfos , ArrayList<Boolean> checkboxs,Integer types){
        clearList(pkginfos,checkboxs);
        queryPKGSCore(activity,pkginfos,checkboxs,types,QUERY_ALL_DISABLE_PKG);
    }


    public void getPKGByUID(Context context, String cmdstr, ArrayList<PKGINFO> pkginfos, HashMap<String, PKGINFO> pkginfoHashMap, ArrayList<Boolean> checkboxs, boolean isRoot){
        pkginfos.clear();
        checkboxs.clear();
        CMD cmd = new shellUtils().getCMD(cmdstr,isRoot);
        String result = cmd.getResult();
        String[] split = result.split("\n");
        if(split != null){
            PackageManager pm = context.getPackageManager();
            for (String s : cmd.getResult().split("\n")) {
                PackageInfo packageInfo = null;
                try {
                    packageInfo = pm.getPackageInfo(s, 0);
                    if(pkginfoHashMap!=null){
                        checkBoxsHashMap(pkginfoHashMap, checkboxs, packageInfo, pm);
                    }else{
                        checkBoxs(pkginfos, checkboxs, packageInfo, pm);
                    }

                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

    }



    public void checkBoxs(ArrayList<PKGINFO> pkginfos,ArrayList<Boolean> checkboxs,PackageInfo packageInfo,PackageManager packageManager){
        if(checkboxs != null){
            checkboxs.add(false);
        }
        ApplicationInfo applicationInfo = packageInfo.applicationInfo;
        pkginfos.add(new PKGINFO(applicationInfo.packageName, applicationInfo.loadLabel(packageManager).toString(), applicationInfo.sourceDir,applicationInfo.uid+"",packageInfo.versionName, applicationInfo.loadIcon(packageManager),new File(applicationInfo.sourceDir).length())) ;
    }

    public void checkBoxsHashMap(HashMap<String,PKGINFO> pkginfos, ArrayList<Boolean> checkboxs, PackageInfo packageInfo, PackageManager packageManager){
        if(checkboxs != null){
            checkboxs.add(false);
        }
        ApplicationInfo applicationInfo = packageInfo.applicationInfo;
        pkginfos.put(applicationInfo.packageName,
                new PKGINFO(applicationInfo.packageName, applicationInfo.loadLabel(packageManager).toString(), applicationInfo.sourceDir,applicationInfo.uid+"",packageInfo.versionName, applicationInfo.loadIcon(packageManager),new File(applicationInfo.sourceDir).length())) ;
    }

    public void appInfoAdd(PackageManager packageManager,PackageInfo packageInfo,ArrayList<PKGINFO> pkginfos , ArrayList<Boolean> checkboxs,Integer state){
        ApplicationInfo applicationInfo = packageInfo.applicationInfo;
        switch (state){
            case 0:
                if(applicationInfo.enabled){
                    checkBoxs(pkginfos,checkboxs,packageInfo,packageManager);
                }
                break;
            case 1:
                if(!applicationInfo.enabled){
                    checkBoxs(pkginfos,checkboxs,packageInfo,packageManager);
                }
                break;
            case 2:
                if((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0 && applicationInfo.enabled){
                    checkBoxs(pkginfos,checkboxs,packageInfo,packageManager);
                }
                break;
            case 3:
                if((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0){
                    checkBoxs(pkginfos,checkboxs,packageInfo,packageManager);
                }
                break;
            case 4:
                checkBoxs(pkginfos,checkboxs,packageInfo,packageManager);
                break;
        }

    }

    public void sortPKGINFOS(ArrayList<PKGINFO> pkginfos){
        if(pkginfos != null && pkginfos.size()>0){
            Collections.sort(pkginfos, new Comparator<PKGINFO>() {
                @Override
                public int compare(PKGINFO pkginfo, PKGINFO t1) {
                    return pkginfo.getAppname().compareTo(t1.getAppname());
                }
            });
        }
    }

    public void queryPKGSCore(Activity activity, ArrayList<PKGINFO> pkginfos , ArrayList<Boolean> checkboxs,Integer types,Integer state){
        PackageManager packageManager = activity.getPackageManager();
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(types);
        for (PackageInfo packageInfo : installedPackages) {
            appInfoAdd(packageManager,packageInfo,pkginfos,checkboxs,state);
        }
        sortPKGINFOS(pkginfos);
    }

    //查询当前机主安装的应用,启用部分
    public void queryEnablePKGS(Activity activity, ArrayList<PKGINFO> pkginfos , ArrayList<Boolean> checkboxs,Integer types){
        clearList(pkginfos,checkboxs);
        queryPKGSCore(activity,pkginfos,checkboxs,types,QUERY_ALL_ENABLE_PKG);
    }

    public void clearList(ArrayList<PKGINFO> pkginfos , ArrayList<Boolean> checkboxs){
        checkboxs.clear();
        pkginfos.clear();
    }



}
