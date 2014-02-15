package com.sashapps.WhoBringsWhat.ItemList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.sashapps.WhoBringsWhat.ItemList.Row.*;
import com.sashapps.WhoBringsWhat.ItemList.Row.CategoryRow;
import com.sashapps.WhoBringsWhat.ItemList.Row.IRow;
import com.sashapps.WhoBringsWhat.WBWApplication;

import java.util.ArrayList;

/**
 * Created by shahar on 2/12/14.
 */
public class ItemListAdapter extends BaseAdapter {
    final ArrayList<IRow> rows;
    final Activity activity;
    final LayoutInflater inflater;
    final String LOG_TAG;

    ItemListAdapter(Activity a, ArrayList<Item> items, ArrayList<Category> categories) {
        activity = a;
        LOG_TAG = ((WBWApplication) a.getApplication()).LOG_TAG;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rows = new ArrayList<IRow>();


        for (Category category : categories) {
            rows.add(new CategoryRow(inflater, category));
            for (Item item : items) {
                if (item.getCategory().getTitle().equals(category.getTitle())) {
                    rows.add(new ItemRow(inflater, item));
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

    public void remove(int position) {
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

    public void addItem(Item i, int position) {
        rows.add(position, new ItemRow(inflater, i));
    }

    public void addItem(Item i) {
        boolean isFound = false;
        int pos = 0;
        for (IRow row : rows) {

            if (row.getViewType() == RowType.CATEGORY.ordinal()) {
                if (((Category) row.getItem()).getTitle().equals(i.getCategory().getTitle())) {
                    rows.add(pos + 1, new ItemRow(inflater, i));
                    isFound = true;
                    break;
                }
            }
            pos += 1;
        }

        if (!isFound) {
            rows.add(new CategoryRow(inflater, i.getCategory()));
            rows.add(new ItemRow(inflater, i));
        }

    }

}

