package org.fqaosp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import org.fqaosp.R;

import java.util.ArrayList;

public class USERAdapter extends BaseAdapter {

    public USERAdapter(ArrayList<String> list, Context context, ArrayList<Boolean> checkboxs) {
        this.list = list;
        this.context = context;
        this.checkboxs = checkboxs;
    }

    private ArrayList<String> list;
    private Context context;
    private ArrayList<Boolean> checkboxs;

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
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
        text.setText(list.get(i));
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
