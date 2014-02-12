package com.sashapps.WhoBringsWhat.ItemList;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import com.sashapps.WhoBringsWhat.ItemList.Row.CategoryRow;
import com.sashapps.WhoBringsWhat.ItemList.Row.ItemRow;
import com.sashapps.WhoBringsWhat.ItemList.Row.Row;
import com.sashapps.WhoBringsWhat.ItemList.Row.RowType;
import com.sashapps.WhoBringsWhat.WBWApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shahar on 2/12/14.
 */
public class ItemListAdapter extends BaseAdapter {
    final List<Row> rows;
    final Activity activity;
    final LayoutInflater inflater;
    final String LOG_TAG;

    ItemListAdapter(Activity a, ArrayList<Item> items, ArrayList<Category> categories){
        activity = a;
        LOG_TAG = ((WBWApplication)a.getApplication()).LOG_TAG;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rows = new ArrayList<Row>();

        for (Category category : categories) {
            rows.add(new CategoryRow(inflater, category));
            for (Item item : items) {
                if (item.getCategory().getTitle().equals(category.getTitle())){
                    rows.add(new ItemRow(inflater,item));
                }
            }
        }
    }

    @Override
    public int getCount() {
        return rows.size();
    }

    @Override
    public Object getItem(int position) {
        return rows.get(position).getItem();
    }

    public void remove(int position){
        rows.get(position).removeItem();
        rows.remove(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v;
        v = rows.get(position).getView(convertView);
        return v;
    }

    @Override
    public int getViewTypeCount() {
        return RowType.values().length;
    }

    @Override
    public int getItemViewType(int position) {
        return rows.get(position).getViewType();
    }

    public void addItem(Item i) {
        int pos=0;
        for (Row row : rows) {

            if (row.getViewType() == RowType.CATEGORY.ordinal()){
                if (((Category)row.getItem()).getTitle().equals(i.getCategory().getTitle())){
                    rows.add(pos+1,new ItemRow(inflater, i));
                    break;
                }
            }
            pos+=1;
        }

    }
}
