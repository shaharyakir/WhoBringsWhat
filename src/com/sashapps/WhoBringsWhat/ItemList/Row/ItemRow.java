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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.*;
import com.sashapps.WhoBringsWhat.ItemList.*;
import com.sashapps.WhoBringsWhat.R;
import com.sashapps.WhoBringsWhat.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ItemRow implements IRow {
    private final Item item;
    private final LayoutInflater inflater;
    private final XListView listView;
    private ItemListAdapter itemListAdapter;

    public ItemRow(LayoutInflater inflater, Item item, XListView listView) {
        this.item = item;
        this.inflater = inflater;
        this.listView = listView;
    }

    public View getView(View convertView, final int position) {
        final ViewHolder holder;
        View view;
        this.itemListAdapter = ((ItemListAdapter) listView.getAdapter());

        if (convertView == null) {

            ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.item_list, null);

            holder = new ViewHolder();

            holder.title = (TextView) viewGroup.findViewById(R.id.title);
            holder.quantity = (TextView) viewGroup.findViewById(R.id.quantity);
            holder.description = (TextView) viewGroup.findViewById(R.id.description);
            holder.profilepic = (ImageView) viewGroup.findViewById(R.id.profilepic);
            holder.progressBar = (ProgressBar) viewGroup.findViewById(R.id.profilepic_progressbar);
            holder.detailsLayout = (LinearLayout) viewGroup.findViewById(R.id.item_list_activity_item_details_layout);
            holder.editItemButton = (Button) viewGroup.findViewById(R.id.item_list_activity_edit_item_button);
            holder.swipeLayout = (LinearLayout) viewGroup.findViewById(R.id.list_item_front);

            // Edit
            holder.editLayout = (LinearLayout) viewGroup.findViewById(R.id.item_list_activity_edit_item_layout);
            holder.quantityEditText = (EditText) viewGroup.findViewById(R.id.item_list_activity_edit_item_quantity_edittext);
            holder.titleEditText = (EditText) viewGroup.findViewById(R.id.item_list_activity_edit_item_title_edittext);
            holder.descriptionEditText = (EditText) viewGroup.findViewById(R.id.item_list_activity_edit_item_description_edittext);
            holder.categorySpinner = (Spinner) viewGroup.findViewById(R.id.item_list_activity_edit_item_category_spinner);
            holder.profilepicEdit = (ImageView) viewGroup.findViewById(R.id.profilepic_edit);
            holder.doneEditingButton = (TextView) viewGroup.findViewById(R.id.item_list_activity_edit_item_done_editing_button);

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
                        ((ItemListAdapter) listView.getAdapter()).notifyDataSetChanged();
                    }
                });

            }
        });

        //actually setup the view
        if (!item.isRegistered() || item.getPhoto() != null) { // TODO: make it work
            holder.progressBar.setVisibility(View.GONE);
        }

        // Set the edit layout
        if (item.isEdit()) {
            holder.editLayout.setVisibility(View.VISIBLE);
            holder.detailsLayout.setVisibility(View.GONE);
            holder.titleEditText.setText(item.getTitle());

            holder.descriptionEditText.setText(item.getDescription());

            if (item.getQuantity() == null) {
                holder.quantityEditText.setText("");
            } else {
                holder.quantityEditText.setText(item.getQuantity().toString());
            }
            holder.profilepicEdit.setImageBitmap(item.getPhoto());

            final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(inflater.getContext(), android.R.layout.simple_spinner_item, Utils.getCategoryArrayList());
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            holder.categorySpinner.setAdapter(dataAdapter);

            // Set the spinner to point to the item's category
            if (item.getCategory() != null) {
                int pos = dataAdapter.getPosition(item.getCategory().getTitle());
                holder.categorySpinner.setSelection(pos);
            }

            // Handle new category addition
            holder.categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (dataAdapter.getItem(position).equals("(New Category)")) {

                        View layout = inflater.inflate(R.layout.add_category_layout, null);
                        final EditText categoryTitleEditText = (EditText) layout.findViewById(R.id.add_category_dialog_edittext);

                        AlertDialog.Builder builder = new AlertDialog.Builder(inflater.getContext());
                        builder.setTitle("Category Title")
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Category c = new Category(categoryTitleEditText.getText().toString());
                                        Utils.addCategory(c);
                                        onCategoryChange(c,finalPos);
                                        itemListAdapter.notifyDataSetChanged();
                                    }
                                })
                                .create();
                        builder.setView(layout);
                        builder.show();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            holder.doneEditingButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {



                    String newTitle = holder.titleEditText.getText().toString();
                    String newQuantityString = holder.quantityEditText.getText().toString();

                    Integer newQuantity = Utils.isNumeric(newQuantityString) ? Integer.valueOf(newQuantityString) : null;

                    String newDescription = holder.descriptionEditText.getText().toString();


                    // Handle the category change
                    String newCategoryTitle = (String) holder.categorySpinner.getSelectedItem();
                    String currentCategoryTitle = item.getCategory() == null ? null : item.getCategory().getTitle();
                    Category c = Utils.getCategoryHashMap().get(newCategoryTitle);

                    boolean isCategoryChanged =    !(c==null && item.getCategory()==null) &&    // Both items are null -> no change
                                                    ((c != null && item.getCategory() == null)  // One is null and the other isn't -> change
                                                || (c == null && item.getCategory() != null)
                                                || (! c.getTitle().equals(item.getCategory().getTitle()))); // Different titles -> change

                    if (isCategoryChanged) {
                        onCategoryChange(c,position);
                    }


                    if (newDescription.equals(""))
                        newDescription = null;

                    item.setTitle(newTitle);
                    item.setDescription(newDescription);
                    item.setQuantity(newQuantity);
                    item.setIsEdit(false);
                    itemListAdapter.notifyDataSetChanged();
                }
            });


        }

        // Set the details layout
        else {
            holder.editLayout.setVisibility(View.GONE);

            holder.detailsLayout.setVisibility(View.VISIBLE);
            holder.title.setText(item.getTitle());

            if (item.getQuantity() == null) {
                holder.quantity.setVisibility(View.GONE);
            } else {
                holder.quantity.setVisibility(View.VISIBLE);
                holder.quantity.setText(item.getQuantity().toString());

            }

            if (item.getDescription() == null) {
                holder.description.setVisibility(View.GONE);
            } else {
                holder.description.setVisibility(View.VISIBLE);
                holder.description.setText(item.getDescription());

            }

            holder.profilepic.setImageBitmap(item.getPhoto());
        }

        return view;
    }

    private void onCategoryChange(Category newCategory,int position){
        int addedPosition = 0;
        item.setCategory(newCategory);
        itemListAdapter.remove(position);
        addedPosition = itemListAdapter.addItem(item);
        listView.requestFocusFromTouch();
        listView.setItemChecked(Math.max(addedPosition - 1, 0),true);
        listView.setSelection(Math.max(addedPosition - 1, 0));
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
        TextView description;
        ImageView profilepic;
        ProgressBar progressBar;

        Button editItemButton;
        LinearLayout swipeLayout;
        LinearLayout detailsLayout;

        //Edit layout
        Spinner categorySpinner;
        LinearLayout editLayout;
        EditText titleEditText;
        EditText descriptionEditText;
        EditText quantityEditText;
        ImageView profilepicEdit;
        TextView doneEditingButton;
    }
}
