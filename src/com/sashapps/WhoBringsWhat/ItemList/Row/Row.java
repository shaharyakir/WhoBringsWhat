package com.sashapps.WhoBringsWhat.ItemList.Row;

import android.view.View;

public interface Row {
    public View getView(View convertView);
    public int getViewType();
    public Object getItem();
    public void removeItem();
}
