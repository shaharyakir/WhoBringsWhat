package com.sashapps.WhoBringsWhat.ItemList;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import com.sashapps.WhoBringsWhat.ItemList.Row.*;
import com.sashapps.WhoBringsWhat.ItemList.Row.CategoryRow;
import com.sashapps.WhoBringsWhat.ItemList.Row.IRow;
import com.sashapps.WhoBringsWhat.WBWApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by shahar on 2/12/14.
 */


public class ItemListAdapter extends ArrayAdapter<IRow> {

    final LayoutInflater inflater;
    final String LOG_TAG;
    final XListView listView;

    public ItemListAdapter(Context context, int resource, ArrayList<Item> items,XListView listView) {
        super(context, resource);
        setNotifyOnChange(true);
        LOG_TAG = "WhoBringsWhat";
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.listView = listView;

        for (Item i : items) {
            this.addItem(i);
        }
    }

    public void remove(int position) {
        remove(getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getItem(position).getView(convertView,position);
    }

    @Override
    public int getViewTypeCount() {
        return RowType.values().length;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getViewType();
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    public void addItem(Item i, int position) {
        this.insert(new ItemRow(inflater, i,listView), position);
    }

    public void setEditMode(int pos, boolean isEdit) {
        getItem(pos).setEditMode(isEdit);
    }

    public boolean getEditMode(int pos) {
        return getItem(pos).getEditMode();
    }

    public Object getListItem(int pos) {
        return getItem(pos).getItem();
    }


    public void addItem(Item i) {

        int pos = 0;
        boolean isCategoryFound = false;

        if (i.getCategory() == null) {
            insert(new ItemRow(inflater, i,listView), pos);
        } else {
            for (pos = 0; pos < getCount(); pos++) {
                IRow row = getItem(pos);
                if (row.getViewType() == RowType.CATEGORY.ordinal()) {
                    if (((Category) row.getItem()).getTitle().equals(i.getCategory().getTitle())) {
                        insert(new ItemRow(inflater, i,listView), pos + 1);
                        isCategoryFound = true;
                        break;
                    }
                }
            }

            if (!isCategoryFound) {
                add(new CategoryRow(inflater, i.getCategory()));
                add(new ItemRow(inflater, i,listView));
            }
        }
    }

}

