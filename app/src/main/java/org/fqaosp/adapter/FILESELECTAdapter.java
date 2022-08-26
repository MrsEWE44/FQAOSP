package org.fqaosp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.fqaosp.R;

import java.util.ArrayList;

public class FILESELECTAdapter extends BaseAdapter {

    private ArrayList<String> list;
    private Context context;

    public FILESELECTAdapter(ArrayList<String> list, Context context) {
        this.list = list;
        this.context = context;
    }

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
        view = LayoutInflater.from(context).inflate(R.layout.file_select_item_activity,null);
        TextView text = view.findViewById(R.id.fsiatv);
        text.setText(list.get(i));
        return view;
    }
}
