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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.sashapps.WhoBringsWhat.ItemList.Item;
import com.sashapps.WhoBringsWhat.R;

public class ItemRow implements Row {
    private final Item item;
    private final LayoutInflater inflater;

    public ItemRow(LayoutInflater inflater, Item item) {
        this.item = item;
        this.inflater = inflater;
    }

    public View getView(View convertView) {
        ViewHolder holder;
        View view;

        if (convertView == null) {
            ViewGroup viewGroup = (ViewGroup)inflater.inflate(R.layout.item_list, null);

            holder = new ViewHolder((TextView)viewGroup.findViewById(R.id.title),
                                    (TextView)viewGroup.findViewById(R.id.quantity),
                                    (ImageView)viewGroup.findViewById(R.id.profilepic),
                                    (ProgressBar)viewGroup.findViewById(R.id.profilepic_progressbar)
                                    );
            viewGroup.setTag(holder);

            view = viewGroup;
        } else {
            //get the holder back out
            holder = (ViewHolder)convertView.getTag();

            view = convertView;
        }

        //actually setup the view
        String quantity = item.getQuantity() != null ? item.getQuantity().toString() : "";
        holder.title.setText(item.getTitle());
        holder.quantity.setText(quantity);
        holder.profilepic.setImageBitmap(item.getPhoto());
        if (! item.isRegistered() || item.getPhoto() != null){
            holder.progressBar.setVisibility(View.GONE);
        }

        return view;
    }

    public int getViewType() {
        return RowType.ITEM.ordinal();
    }

    public Object getItem(){
        return item;
    }

    @Override
    public void removeItem() {
        item.deleteItem();
    }

    private static class ViewHolder {
        final TextView title;
        final TextView quantity;
        final ImageView profilepic;
        final ProgressBar progressBar;

        private ViewHolder(TextView title, TextView quantity, ImageView profilepic, ProgressBar progressBar) {
            this.title=title;
            this.quantity = quantity;
            this.profilepic = profilepic;
            this.progressBar = progressBar;
        }
    }
}
