package com.sashapps.WhoBringsWhat.Persistence;

import android.util.Log;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.parse.ParseObject;
import com.sashapps.WhoBringsWhat.ItemList.Item;
import com.sashapps.WhoBringsWhat.Utils;

/**
 * Created by shahar on 2/9/14.
 */
public class Persister {

    private static String PARSE_ITEM_OBJECT = "Item";
    private static GsonBuilder gson = new GsonBuilder();

    public static void saveItem(Item i){
        ParseObject parseItemObject = new ParseObject(PARSE_ITEM_OBJECT);
        gson.setExclusionStrategies(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                return fieldAttributes.getDeclaredClass().getName().equals("android.graphics.Bitmap");
            }

            @Override
            public boolean shouldSkipClass(Class<?> aClass) {
                return false;
            }
        }).create();
        String json = gson.create().toJson(i);

    }
}
