/*
Copyright (C) 2011 by Indrajit Khare

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/

package com.sashapps.WhoBringsWhat.ItemList.Row;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.*;
import com.sashapps.WhoBringsWhat.ItemList.Item;
import com.sashapps.WhoBringsWhat.ItemList.ItemListActivity;
import com.sashapps.WhoBringsWhat.ItemList.ItemListAdapter;
import com.sashapps.WhoBringsWhat.ItemList.XListView;
import com.sashapps.WhoBringsWhat.R;

public class ItemRow implements IRow {
    private final Item item;
    private final LayoutInflater inflater;
    private final XListView listView;

    public ItemRow(LayoutInflater inflater, Item item,XListView listView) {
        this.item = item;
        this.inflater = inflater;
        this.listView = listView;
    }

    public View getView(View convertView,int position) {
        ViewHolder holder;
        View view;

        if (convertView == null) {

            ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.item_list, null);

            holder = new ViewHolder();

            holder.title = (TextView) viewGroup.findViewById(R.id.title);
            holder.quantity = (TextView) viewGroup.findViewById(R.id.quantity);
            holder.profilepic = (ImageView) viewGroup.findViewById(R.id.profilepic);
            holder.progressBar = (ProgressBar) viewGroup.findViewById(R.id.profilepic_progressbar);
            holder.editLayout = (LinearLayout) viewGroup.findViewById(R.id.item_list_activity_edit_item_layout);
            holder.quantityEditText = (EditText) viewGroup.findViewById(R.id.item_list_activity_quantity_edit_text);
            holder.editItemButton = (Button) viewGroup.findViewById(R.id.item_list_activity_edit_item_button);


            viewGroup.setTag(holder);

            view = viewGroup;
        } else {
            //get the holder back out
            holder = (ViewHolder) convertView.getTag();

            view = convertView;
        }

        final int finalPos = position;

        holder.editItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listView.resetState(finalPos, new XListView.OnAnimationEndCallback() {
                    @Override
                    public void onAnimationCallback() {
                        item.setIsEdit(!item.isEdit());
                        ((ItemListAdapter)listView.getAdapter()).notifyDataSetChanged();
                    }
                });

            }
        });

        holder.quantityEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((EditText)v).setText("900");
                item.setQuantity(900);
            }
        });


        //actually setup the view
        holder.title.setText(item.getTitle());

        if (item.getQuantity() == null) {
            holder.quantity.setVisibility(View.GONE);
        } else {
            holder.quantity.setVisibility(View.VISIBLE);
            holder.quantity.setText(item.getQuantity().toString());
        }

        holder.profilepic.setImageBitmap(item.getPhoto());
        if (!item.isRegistered() || item.getPhoto() != null) {
            holder.progressBar.setVisibility(View.GONE);
        }

        if (item.isEdit()) {
            holder.editLayout.setVisibility(View.VISIBLE);
        } else {
            holder.editLayout.setVisibility(View.GONE);
        }

        return view;
    }

    public int getViewType() {
        return RowType.ITEM.ordinal();
    }

    public Object getItem() {
        return item;
    }

    @Override
    public void removeItem() {
        item.deleteItem();
    }

    @Override
    public void setEditMode(boolean isEdit) {
        item.setIsEdit(isEdit);
    }

    public boolean getEditMode() {
        return item.isEdit();
    }

    private static class ViewHolder {
        TextView title;
        TextView quantity;
        ImageView profilepic;
        ProgressBar progressBar;
        LinearLayout editLayout;
        EditText quantityEditText;
        Button editItemButton;
    }
}
