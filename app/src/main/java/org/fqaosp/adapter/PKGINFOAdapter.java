package org.fqaosp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.fqaosp.R;
import org.fqaosp.entity.PKGINFO;

import java.util.ArrayList;


public class PKGINFOAdapter extends BaseAdapter {

    public PKGINFOAdapter(ArrayList<PKGINFO> pkginfos, Context context, ArrayList<Boolean> checkboxs) {
        this.pkginfos = pkginfos;
        this.context = context;
        this.checkboxs = checkboxs;
    }

    private ArrayList<PKGINFO> pkginfos;
    private Context context;
    private ArrayList<Boolean> checkboxs;

    @Override
    public int getCount() {
        return pkginfos.size();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.list_view_item_activity,null);
        ImageView imageView = convertView.findViewById(R.id.lviaiv1);
        CheckBox checkBox=convertView.findViewById(R.id.lviacb1);
        TextView text = convertView.findViewById(R.id.lviatv1);
        text.setText(pkginfos.get(position).getAppname());
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                checkboxs.set(position,b);
            }
        });
        imageView.setImageDrawable(pkginfos.get(position).getAppicon());
        return convertView;
    }
}
