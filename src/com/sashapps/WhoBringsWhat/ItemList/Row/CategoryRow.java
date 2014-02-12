package com.sashapps.WhoBringsWhat.ItemList.Row;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.sashapps.WhoBringsWhat.ItemList.Category;
import com.sashapps.WhoBringsWhat.R;

public class CategoryRow implements Row {
    private final Category category;
    private final LayoutInflater inflater;

    public CategoryRow(LayoutInflater inflater, Category category) {
        this.category = category;
        this.inflater = inflater;
    }

    public View getView(View convertView) {
        ViewHolder holder;
        View view;

        if (convertView == null) {
            ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.header, null);

            holder = new ViewHolder((TextView) viewGroup.findViewById(R.id.list_header_title));
            viewGroup.setTag(holder);

            view = viewGroup;
        } else {
            holder = (ViewHolder) convertView.getTag();
            view = convertView;
        }
        holder.title.setText(category.getTitle());
        // Here the category icon would be set

        return view;
    }

    public int getViewType() {
        return RowType.CATEGORY.ordinal();
    }

    @Override
    public Object getItem() {
        return category;
    }

    @Override
    public void removeItem() {
    }

    private static class ViewHolder {

        final TextView title;

        private ViewHolder(TextView title) {
            this.title = title;
        }
    }


}
