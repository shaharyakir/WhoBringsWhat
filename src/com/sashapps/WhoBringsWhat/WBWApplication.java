package com.sashapps.WhoBringsWhat;

import android.app.Application;
import android.graphics.Bitmap;
import android.util.Log;
import com.parse.*;
import com.sashapps.WhoBringsWhat.ItemList.Category;
import com.sashapps.WhoBringsWhat.ItemList.Item;
import com.sashapps.WhoBringsWhat.ItemList.ItemList;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by shahar on 2/12/14.
 */
public class WBWApplication extends Application {
    public String LOG_TAG="WhoBringsWhat";
    HashMap<String,Bitmap> userPhotos;

    @Override
    public void onCreate() {
        userPhotos = new HashMap<String, Bitmap>();
    }

    public HashMap<String,Bitmap> getUserPhotos() {
        return userPhotos;
    }

    public void initParse() {
        ParseObject.registerSubclass(ItemList.class);
        ParseObject.registerSubclass(Item.class);
        ParseObject.registerSubclass(Category.class);

        Parse.initialize(this, "36GvVowfQyFvW5XhZL7P05xB0pPciF9e3VSq4Qf4", "cu0pbNtOJoLczixm575YUdJBbzWH3eNMnMm7EThk");

    }


}
