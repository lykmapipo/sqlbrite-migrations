package com.github.lykmapipo.sqlbrite.migrations.sample;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import io.reactivex.functions.Consumer;

import java.util.Collections;
import java.util.List;

/**
 * Created by lally on 3/19/17.
 */
public class BriteAdapter extends BaseAdapter implements Consumer<List<Brite>> {
    private final LayoutInflater inflater;

    private List<Brite> items = Collections.emptyList();

    public BriteAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public void accept(List<Brite> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Brite getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        Brite item = getItem(position);
        TextView convertView1 = (TextView) convertView;
        convertView1.setText(item.getName() + " (" + position + ")");
        convertView1.setTextColor(R.color.colorBlack);

        return convertView;
    }
}
