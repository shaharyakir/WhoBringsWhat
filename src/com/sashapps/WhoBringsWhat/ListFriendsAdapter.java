package com.sashapps.WhoBringsWhat;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class ListFriendsAdapter extends BaseAdapter {

    private static LayoutInflater inflater=null;
    private Activity activity;
    private ArrayList<FacebookFriend> data;
    private boolean arePhotosReady;


    public ListFriendsAdapter(Activity a, ArrayList<FacebookFriend> map){

        activity = a;

        data=map;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        arePhotosReady=false;

    }

    @Override
    public int getCount() {
        return data.size();
        //return 0;
    }

    @Override
    public FacebookFriend getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void updatePhotoById(String id,Bitmap b){
        for (FacebookFriend f : data){
            if (f.getId()==id){
                f.setPhoto(b);
            }
        }
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.list_friends_item, null);

        FacebookFriend f = data.get(i);
        TextView name = (TextView)vi.findViewById(R.id.list_friends_name);
        name.setText(f.getName());

        ImageView iv = (ImageView)vi.findViewById(R.id.list_friends_profile_pic);
        if (f.getPhoto() != null){
            iv.setImageBitmap(f.getPhoto());
        }
        return vi;
    }
}
