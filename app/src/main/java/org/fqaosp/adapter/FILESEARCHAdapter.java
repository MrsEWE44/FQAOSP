package org.fqaosp.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import org.fqaosp.R;
import org.fqaosp.entity.SearchFileInfo;

import java.util.ArrayList;
import java.util.HashMap;

public class FILESEARCHAdapter extends BaseAdapter {

    public FILESEARCHAdapter(Context context, ArrayList<Boolean> checkboxs, ArrayList<SearchFileInfo> sfis) {
        this.context = context;
        this.checkboxs = checkboxs;
        this.sfis = sfis;
    }

    private Context context;
    private ArrayList<Boolean> checkboxs;
    private ArrayList<SearchFileInfo> sfis;


    @Override
    public int getCount() {
        return sfis.size();
    }

    @Override
    public Object getItem(int i) {
        return sfis.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(context).inflate(R.layout.wp_rm_item_activity,null);
        CheckBox checkBox=view.findViewById(R.id.wriacb1);
        TextView text = view.findViewById(R.id.wriatv1);
        text.setText(sfis.get(i).getFname());
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                checkboxs.set(i,b);
            }
        });
        checkBox.setChecked(checkboxs.get(i));
        return view;
    }
}
