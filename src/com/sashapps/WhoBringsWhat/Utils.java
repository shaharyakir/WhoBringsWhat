package com.sashapps.WhoBringsWhat;

import android.graphics.Bitmap;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseObject;
import com.sashapps.WhoBringsWhat.ItemList.Item;
import com.sashapps.WhoBringsWhat.ItemList.ItemList;

import java.util.ArrayList;

public class Utils {

    private static int num=0;

    private static ItemList theItemList;

    private static String userFacebookId;

    public static String LOG_TAG="WhoBringsWhat";

    public static Bitmap getDefaultPhoto() {
        return defaultPhoto;
    }

    public static void setDefaultPhoto(Bitmap defaultPhoto) {
        Utils.defaultPhoto = defaultPhoto;
    }

    private static Bitmap defaultPhoto;


    public static String GOAL_SERIALIZABLE_OBJECT = "GOAL";

    public static int generateNumber(){
        return num++;
    }

    public static String getUserFacebookId() {
        return userFacebookId;
    }

    public static void setUserFacebookId(String userFacebookId) {
        Utils.userFacebookId = userFacebookId;
    }


    public static ItemList getItemList(){
        return theItemList;
    }

    public static void setItemList(ItemList il){
        theItemList=il;
    }
}
