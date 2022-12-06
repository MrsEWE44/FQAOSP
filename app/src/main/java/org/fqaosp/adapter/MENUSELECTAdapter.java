package org.fqaosp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.fqaosp.MainActivity;
import org.fqaosp.R;
import org.fqaosp.entity.menuEntity;

import java.util.ArrayList;

public class MENUSELECTAdapter extends BaseAdapter {

    private ArrayList<menuEntity> list;
    private Context context;

    public MENUSELECTAdapter(ArrayList<menuEntity> menuEntities, Context mainActivity) {
        this.list = menuEntities;
        this.context=mainActivity;
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
        menuEntity menuEntity = list.get(i);
        view = LayoutInflater.from(context).inflate(R.layout.menu_select_item_activity,null);
        ImageView imageView = view.findViewById(R.id.msiaiv1);
        TextView text = view.findViewById(R.id.msiatv1);
        text.setText(menuEntity.getName());
        imageView.setImageDrawable(menuEntity.getMenuIcon());
        return view;
    }
}
