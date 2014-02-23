package com.sashapps.WhoBringsWhat;

import android.graphics.Bitmap;
import com.sashapps.WhoBringsWhat.ItemList.Category;
import com.sashapps.WhoBringsWhat.ItemList.ItemList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class Utils {

    public static int PARSE_DELETED=3;

    private static String userFacebookId;
    private static Bitmap userPhoto;

    private static HashMap<String,Category> categoryHashMap;

    public static void addCategory(Category c) {
        HashMap<String,Category> array = getCategoryHashMap();
        if (c!=null && ! array.containsKey(c.getTitle())){
            array.put(c.getTitle(),c);
        }
    }

    public static void addCategory(String title) {
        HashMap<String, Category> array = getCategoryHashMap();
        array.put(title, null);
    }

    public static HashMap<String,Category> getCategoryHashMap(){

        if (categoryHashMap == null){
            categoryHashMap = new HashMap<String,Category>();
        }
        return categoryHashMap;
    }

    public static ArrayList<String> getCategoryArrayList() {

        ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.addAll(getCategoryHashMap().keySet());
        Collections.sort(arrayList,new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                return lhs.toLowerCase().compareTo(rhs.toLowerCase());
            }
        });
        Collections.swap(arrayList,arrayList.indexOf("(New Category)"),arrayList.size()-1);
        Collections.swap(arrayList,arrayList.indexOf("(None)"),0);
        return arrayList;
    }

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


    public static boolean isNumeric(String str)
    {
        try
        {
            double d = Double.parseDouble(str);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }
}
