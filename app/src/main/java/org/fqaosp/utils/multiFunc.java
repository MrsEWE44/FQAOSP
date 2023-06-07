package org.fqaosp.utils;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

/**
 * 通用功能函数集合
 * */

public class multiFunc {

    public multiFunc(){

    }

    //页面布局跳转
    public void jump(Context srcA , Class<?> cls,Boolean isRoot,Boolean isADB){
        Intent intent = new Intent(srcA, cls);
        intent.putExtra("isRoot", isRoot);
        intent.putExtra("isADB",isADB);
        srcA.startActivity(intent);
    }

    //页面布局跳转
    public void jump(Button b , Context srcA , Class<?> cls,Boolean isRoot,Boolean isADB){
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jump(srcA,cls,isRoot,isADB);
            }
        });
    }



}
