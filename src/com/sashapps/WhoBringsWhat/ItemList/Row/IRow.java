package com.sashapps.WhoBringsWhat.ItemList.Row;

import android.view.View;

public interface IRow {
    public View getView(View convertView,int position);
    public int getViewType();
    public Object getItem();
    public void removeItem();
    public void setEditMode(boolean isEdit);
    public boolean getEditMode();
}
