package org.fqaosp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import org.fqaosp.R;
import org.fqaosp.utils.multiFunc;

import java.util.ArrayList;

public class APPOPSINFOAdapter extends BaseAdapter {


    public APPOPSINFOAdapter(ArrayList<String> list, Context context, ArrayList<Boolean> checkboxs, ArrayList<Boolean> switbs, String pkgname, int mode,String uid) {
        this.list = list;
        this.context = context;
        this.checkboxs = checkboxs;
        this.switbs = switbs;
        this.pkgname = pkgname;
        this.mode = mode;
    }

    private ArrayList<String> list;
    private Context context;
    private ArrayList<Boolean> checkboxs,switbs;
    private String pkgname,uid ;
    private int mode;

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(context).inflate(R.layout.appops_info_item_activity,null);
        CheckBox checkBox=view.findViewById(R.id.aiacb1);
        TextView text = view.findViewById(R.id.aiatv1);
        Switch switchbbb = view.findViewById(R.id.aiaswb1);
        text.setText(list.get(i));
        switchbbb.setChecked(switbs.get(i));
        switchbbb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                switbs.set(i,b);
                switchbbb.setChecked(switbs.get(i));
                String pkgcate = list.get(i);
                if(b){
                    switch (mode){
                        case 0:
                        case 1:
                            multiFunc.runAppopsCMD(context,pkgname,pkgcate,4,"开启组件成功","开启组件失败",uid);
                            break;
                        case 2:
                            multiFunc.runAppopsCMD(context,pkgname,pkgcate,3,"开启权限成功","开启权限失败",uid);
                            break;
                    }
                }else{
                    switch (mode){
                        case 0:
                        case 1:
                            multiFunc.runAppopsCMD(context,pkgname,pkgcate,1,"关闭组件成功","关闭组件失败",uid);
                            break;
                        case 2:
                            multiFunc.runAppopsCMD(context,pkgname,pkgcate,0,"关闭权限成功","关闭权限失败",uid);
                            break;
                    }
                }
            }
        });
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                checkboxs.set(i,b);
            }
        });
        return view;
    }
}
