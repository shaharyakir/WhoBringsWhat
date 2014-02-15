package com.sashapps.WhoBringsWhat.ItemList;

import android.app.Application;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.sashapps.WhoBringsWhat.R;
import com.sashapps.WhoBringsWhat.Utils;

@ParseClassName("Item")
public class Item extends ParseObject {

    private Bitmap mPhoto;

    public ParseUser getUser(){
        return (ParseUser)getParseUser("user");
    }

    public void setUser(ParseUser user) {
        put("user", user);
    }

    public void removeUser(){
        this.remove("user");
    }

    public ItemList getItemList() {
        return (ItemList)getParseObject("itemList");
    }

    public void setItemList(ItemList itemList) {
        put("itemList",itemList);
    }

    public Category getCategory() {
        return (Category)getParseObject("category");
    }

    public void setCategory(Category category) {
        put("category",category);
    }

    public Boolean isRegistered() {
        return getUser() != null;
    }

    public Bitmap getPhoto() {
        return mPhoto;
    }

    public void register() {
        this.mPhoto = Utils.getCurrentUserFacebookPhoto(); // TODO: cache photo or wait till loaded
        this.setUser(ParseUser.getCurrentUser());
        this.saveInBackground();
    }

    public void unregister() {
        this.mPhoto = Utils.getDefaultPhoto();
        this.removeUser();
        this.saveInBackground();
    }

    private void setIsRegistered(boolean b) {
        put("isRegistered",b);
    }

    public Integer getQuantity() {
        Integer i = getInt("quantity");
        if (i==0) { return null; }
        else { return i; }
    }

    public void setQuantity(Integer quantity) {
        if (quantity != null) {
            put("quantity", quantity);
        }
    }

    public String getTitle() {
        return getString("title");
    }

    public void setTitle(String title) {
        put("title", title);
    }

    public String getDescription() {
        return getString("description");
    }

    public void setDescription(String description) {
        if (description != null)
            put("description", description);
    }

    public void setPhoto(Bitmap b){
        mPhoto = b;
    }

    public void deleteItem(){
        put("state",Utils.PARSE_DELETED);
        saveInBackground();
    }

    public Item(ItemList list, Category category,String title, String description, Integer quantity) {
        this.setItemList(list);
        this.setCategory(category);
        this.setTitle(title);
        this.setDescription(description);
        this.setQuantity(quantity);
        mPhoto = Utils.getDefaultPhoto();
        saveInBackground();
    }

    public Item() {
        mPhoto = Utils.getDefaultPhoto();
    }

    public boolean isCurrentUserOwnerOfItem() {
        return this.isRegistered() && ParseUser.getCurrentUser().getUsername().equals(this.getUser().getUsername());
    }
}
