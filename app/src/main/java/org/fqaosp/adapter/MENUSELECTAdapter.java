package org.fqaosp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.fqaosp.R;
import org.fqaosp.entity.menuEntity;

import java.util.ArrayList;

public class MENUSELECTAdapter extends BaseAdapter {

    public MENUSELECTAdapter(ArrayList<menuEntity> list, Context context, Boolean haveRoot, Boolean haveShizuku) {
        this.list = list;
        this.context = context;
        this.haveRoot = haveRoot;
        this.haveShizuku = haveShizuku;
    }

    private ArrayList<menuEntity> list;
    private Context context;
    private Boolean haveRoot,haveShizuku;


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
        menuEntity me = list.get(i);
        Boolean needRoot = me.getNeedRoot();
        Boolean needShizuku = me.getNeedShizuku();
        view = LayoutInflater.from(context).inflate(R.layout.menu_select_item_activity,null);
        ImageView imageView = view.findViewById(R.id.msiaiv1);
        TextView text = view.findViewById(R.id.msiatv1);
        text.setText(me.getName());
        imageView.setImageDrawable(me.getMenuIcon());
        if(haveRoot || (needRoot == false && needShizuku == false)){
            view.setBackgroundColor(Color.argb(0,0,0,0));
        }else if(haveShizuku && needShizuku){
            view.setBackgroundColor(Color.rgb(176,198,39));
        }else{
            view.setBackgroundColor(Color.rgb(223,90,90));
        }
        return view;
    }
}
