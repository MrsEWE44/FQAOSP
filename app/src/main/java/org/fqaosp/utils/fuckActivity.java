package org.fqaosp.utils;

import android.app.Activity;

import java.util.ArrayList;

/**
 * 保存activity，用于退出应用时，销毁所有activity
 *
 * */

public class fuckActivity {
     private ArrayList<Activity> list = new ArrayList<>();;
     private static fuckActivity ins;

     //创建应用实例，如果已经创建，则返回，否则新建实例
     public static fuckActivity getIns(){
         if(ins == null){
             ins = new fuckActivity();
         }
         return ins;
     }


     //添加一个activity实现
     public void add(Activity activity){
         list.add(activity);
     }

     //终止一个activity
     public void kill(Activity activity){
         for (Activity a : list) {
             if(a == activity){
                 a.finish();
                 list.remove(a);
             }
         }
     }

     //终止所保存的activity实例
     public void killall(){
         for (Activity activity : list) {
             activity.finish();
         }
         System.exit(0);
     }

}
