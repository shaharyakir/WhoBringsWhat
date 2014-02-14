package com.sashapps.WhoBringsWhat;

import android.graphics.Bitmap;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseObject;
import com.sashapps.WhoBringsWhat.ItemList.Item;
import com.sashapps.WhoBringsWhat.ItemList.ItemList;

import java.util.ArrayList;

public class Utils {

    public static int PARSE_DELETED=3;

    private static String userFacebookId;
    private static Bitmap userPhoto;

    public static Bitmap getDefaultPhoto() {
        return defaultPhoto;
    }

    public static Bitmap getCurrentUserFacebookPhoto() {
        if (userPhoto != null){
        return userPhoto;
        }
        else{
            return getDefaultPhoto();
        }
    }

    public static void setCurrentUserFacebookPhoto(Bitmap b){
        userPhoto=b;
    }

    public static void setDefaultPhoto(Bitmap defaultPhoto) {
        Utils.defaultPhoto = defaultPhoto;
    }

    private static Bitmap defaultPhoto;

    private static ItemList itemList;

    public static ItemList getItemList() {
        return itemList;
    }
    public static void setItemList(ItemList il){
        itemList=il;
    }


}
