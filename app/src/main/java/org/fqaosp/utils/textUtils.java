package org.fqaosp.utils;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.widget.Toast;

import org.fqaosp.entity.PKGINFO;

import java.util.ArrayList;
import java.util.Locale;

public class textUtils {

    public textUtils(){}

    public void copyText(Context context, String str){
        ClipboardManager cpm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        cpm.setText(str);
        Toast.makeText(context, "已复制", Toast.LENGTH_SHORT).show();
    }


    //搜索列表匹配项
    public ArrayList<PKGINFO> indexOfPKGS(Activity activity, String findStr, ArrayList<PKGINFO> pkginfos , ArrayList<Boolean> checkboxs, Integer types){
        if(pkginfos.size() == 0){
            packageUtils pkgutils = new packageUtils();
            pkgutils.queryPKGS(activity,pkginfos,checkboxs,types);
        }
        return indexOfPKGS(pkginfos,checkboxs,findStr);
    }


    public Boolean isIndexOfStr(String str,String instr){
        return str.toLowerCase(Locale.ROOT).indexOf(instr.toLowerCase(Locale.ROOT)) != -1;
    }


    //搜索列表匹配项
    public ArrayList<PKGINFO> indexOfPKGS(ArrayList<PKGINFO> pkginfos , ArrayList<Boolean> checkboxs,String findStr){
        checkboxs.clear();
        ArrayList<PKGINFO> pkginfos2 = new ArrayList<>();
        for (PKGINFO pkginfo : pkginfos) {
            if(isIndexOfStr(pkginfo.getAppname(),findStr) || isIndexOfStr(pkginfo.getPkgname(),findStr)){
                pkginfos2.add(pkginfo);
                checkboxs.add(false);
            }
        }
        pkginfos.clear();
        return pkginfos2;
    }

    //搜索列表匹配项
    public ArrayList<String> indexOfLIST(ArrayList<String> list , ArrayList<Boolean> checkboxs,String findStr){
        checkboxs.clear();
        ArrayList<String> strings = new ArrayList<>();
        if(findStr==null || findStr.isEmpty()){
            return list;
        }
        for (String s : list) {
            if(isIndexOfStr(s,findStr)){
                strings.add(s);
                checkboxs.add(false);
            }
        }
        list.clear();
        return strings;
    }


}
