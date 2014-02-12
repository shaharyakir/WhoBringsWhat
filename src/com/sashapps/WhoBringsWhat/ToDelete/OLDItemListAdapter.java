package com.sashapps.WhoBringsWhat.ToDelete;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.sashapps.WhoBringsWhat.ItemList.Item;
import com.sashapps.WhoBringsWhat.R;
import com.sashapps.WhoBringsWhat.Utils;

import java.util.ArrayList;

public class OLDItemListAdapter extends BaseAdapter {

    private static LayoutInflater inflater=null;
    private Activity activity;
    private ArrayList<Item> data;

    public OLDItemListAdapter(Activity a, ArrayList<Item> map){
        activity = a;
        data=map;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public Item getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View vi=convertView;
        Item item = data.get(i);
        String quantity = item.getQuantity() != null ? item.getQuantity().toString() : "";

        if(convertView==null)
            vi = inflater.inflate(R.layout.item_list, null);

        TextView tvTitle = (TextView)vi.findViewById(R.id.title);
        if (tvTitle!=null)
            tvTitle.setText(item.getTitle());
        TextView tvQuantity = (TextView)vi.findViewById(R.id.quantity);
        if (tvQuantity!=null)
            tvQuantity.setText(quantity);

        ImageView iv = (ImageView)vi.findViewById(R.id.profilepic);
        if (iv!=null)
            iv.setImageBitmap(item.getPhoto());


        return vi;
    }

    public void addItem(Item i){
        data.add(i);
    }

    public void remove(int position){
        this.data.remove(position);
    }

    public void setDataset(ArrayList<Item> newDataset){
        this.data = newDataset;
    }
}
