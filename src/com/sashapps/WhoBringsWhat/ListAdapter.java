package com.sashapps.WhoBringsWhat;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class ListAdapter extends BaseAdapter {

    private static LayoutInflater inflater=null;
    private Activity activity;
    private HashMap<Integer,Item> data;


    public ListAdapter(Activity a,HashMap<Integer,Item> map){

        activity = a;
        data=map;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return data.size();
        //return 0;
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.list_item, null);

        TextView desc = (TextView)vi.findViewById(R.id.description);
        desc.setText(data.get(i).getDescription());
        /*TextView title = (TextView)vi.findViewById(R.id.title);
        TextView date = (TextView)vi.findViewById(R.id.date);
        TextView number = (TextView)vi.findViewById(R.id.number);

        Goal g = Goals.getGoalById(i);

        title.setText(g.getTitle());
        date.setText(g.getDueDate());
        number.setText(String.valueOf(g.getNumber()));*/

        return vi;
    }
}
