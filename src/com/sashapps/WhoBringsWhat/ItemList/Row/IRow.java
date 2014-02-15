package com.sashapps.WhoBringsWhat.ItemList.Row;

import android.view.View;

public interface IRow {
    public View getView(View convertView);
    public int getViewType();
    public Object getItem();
    public void removeItem();
    public boolean hidden=false;
    public boolean isHidden();
    public void setHidden(boolean hidden);
}
