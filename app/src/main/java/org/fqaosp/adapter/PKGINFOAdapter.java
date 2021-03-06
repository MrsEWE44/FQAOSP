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
        return pkginfos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    private String getSize(double size,int count){
        String size_type[] = {"b","KB","MB","GB","TB","PB"};
        if(size > 1024){
            double d_size= size/1024;
            count = count + 1;
            return getSize(d_size,count);
        }
        String sizestr=String.format("%.2f",size)+size_type[count];
        return sizestr;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.list_view_item_activity,null);
        ImageView imageView = convertView.findViewById(R.id.lviaiv1);
        CheckBox checkBox=convertView.findViewById(R.id.lviacb1);
        TextView text = convertView.findViewById(R.id.lviatv1);
        TextView text2 = convertView.findViewById(R.id.lviatv2);
        TextView text3 = convertView.findViewById(R.id.lviatv3);
        TextView text4 = convertView.findViewById(R.id.lviatv4);
        PKGINFO pkginfo = pkginfos.get(position);
        text.setText(pkginfo.getAppname());
        text2.setText(pkginfo.getPkgname());
        text3.setText(pkginfo.getAppversionname());
        text4.setText(getSize(pkginfo.getFilesize(),0));

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                checkboxs.set(position,b);
            }
        });
        checkBox.setChecked(checkboxs.get(position));
        imageView.setImageDrawable(pkginfos.get(position).getAppicon());
        return convertView;
    }

}
