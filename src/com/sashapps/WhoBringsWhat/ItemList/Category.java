package com.sashapps.WhoBringsWhat.ItemList;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;


@ParseClassName("Category")
public class Category extends ParseObject {
    public Category() {
    }

    public Category(String title){
        put("title",title);
        try {
            save();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getTitle(){
        return getString("title");
    }
}
