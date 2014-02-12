package com.sashapps.WhoBringsWhat;

import android.app.Application;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseObject;
import com.sashapps.WhoBringsWhat.ItemList.Category;
import com.sashapps.WhoBringsWhat.ItemList.Item;
import com.sashapps.WhoBringsWhat.ItemList.ItemList;

/**
 * Created by shahar on 2/12/14.
 */
public class WBWApplication extends Application {
    public String LOG_TAG="WhoBringsWhat";

    public void initParse() {
        ParseObject.registerSubclass(ItemList.class);
        ParseObject.registerSubclass(Item.class);
        ParseObject.registerSubclass(Category.class);

        Parse.initialize(this, "36GvVowfQyFvW5XhZL7P05xB0pPciF9e3VSq4Qf4", "cu0pbNtOJoLczixm575YUdJBbzWH3eNMnMm7EThk");
    }
}
