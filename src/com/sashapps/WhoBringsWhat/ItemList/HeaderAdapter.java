package com.sashapps.WhoBringsWhat.ItemList;

/**
 * Created by shahar on 2/9/14.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.sashapps.WhoBringsWhat.R;

import java.util.ArrayList;

public class HeaderAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;

    ArrayList<String> Datastring=new ArrayList<String>();

    public HeaderAdapter(Context context, ArrayList<String> HeaderTitles) {
        ctx = context;
        Datastring.addAll(HeaderTitles);
        lInflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public void add(String string){
        Datastring.add(string);
    }

    @Override
    public int getCount() {
        return Datastring.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = (View) lInflater.inflate(R.layout.header, parent, false);
        }
        TextView text=(TextView)convertView.findViewById(R.id.list_header_title);
        text.setText(Datastring.get(position));
        return convertView;
    }




}