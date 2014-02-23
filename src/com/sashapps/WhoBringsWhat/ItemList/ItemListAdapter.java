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
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Created by shahar on 2/12/14.
 */


public class ItemListAdapter extends ArrayAdapter<IRow> {

    final LayoutInflater inflater;
    final String LOG_TAG;
    final XListView listView;

    public ItemListAdapter(Context context, int resource, ArrayList<Item> items, XListView listView) {
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
        if (position >= 0 && position < getCount()) {
            if (getItemViewType(position) == RowType.ITEM.ordinal() && // If it's an item
                    isLastItemInCategory(position) == true             // And last item in its category, remove the category
                    ) {
                remove(getItem(position));   // remove item
                remove(getItem(position-1)); // remove category
            } else {
                remove(getItem(position));
            }
        }
    }

    public boolean isLastItemInCategory(int itemPosition) {
        Item i = (Item) getListItem(itemPosition);

        boolean isRowAboveACategory = (itemPosition - 1 >= 0) && (getItemViewType(itemPosition - 1) == RowType.CATEGORY.ordinal());
        boolean isRowBelowACategoryOrEndOfList = (itemPosition + 1 >= getCount()) || (getItemViewType(itemPosition + 1) == RowType.CATEGORY.ordinal());

        return isRowAboveACategory && isRowBelowACategoryOrEndOfList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getItem(position).getView(convertView, position);
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
        this.insert(new ItemRow(inflater, i, listView), position);
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


    public int addItem(Item i) {

        int pos = 0;
        boolean isCategoryFound = false;

        if (i.getCategory() == null) {
            insert(new ItemRow(inflater, i, listView), pos);
            return 0;
        } else {
            for (pos = 0; pos < getCount(); pos++) {
                IRow row = getItem(pos);
                if (row.getViewType() == RowType.CATEGORY.ordinal()) {
                    if (((Category) row.getItem()).getTitle().equals(i.getCategory().getTitle())) {
                        insert(new ItemRow(inflater, i, listView), pos + 1);
                        isCategoryFound = true;
                        return pos + 1;
                    }
                }
            }

            if (!isCategoryFound) {
                add(new CategoryRow(inflater, i.getCategory()));
                add(new ItemRow(inflater, i, listView));
                return this.getCount();
            }
        }
        return 0;
    }

   /* private void sortCategory(){
        this.sort(new Comparator<IRow>() {
            @Override
            public int compare(IRow lhs, IRow rhs) {
                if (lhs.getViewType() == RowType.ITEM.ordinal()
                        && rhs.getViewType() == RowType.ITEM.ordinal()){
                    Item l = (Item)lhs.getItem();
                    Item r = (Item)lhs.getItem();
                    return l.getTitle().compareTo(r.getTitle());
                }
                return 0;
            }
        });
    }*/

    public void removeRegisteredItems(){

        for (int i=0; i<getCount();i++){
            if (getItemViewType(i) == RowType.ITEM.ordinal() && ((Item)getListItem(i)).isRegistered()){
                remove(i);
                i--;
            }
        }
    }
}

