package com.sashapps.WhoBringsWhat;

import android.graphics.Bitmap;

public class FacebookFriend {

    private boolean mIsComing;
    private String mName;
    private String mId;
    private Bitmap mPhoto;

    public Bitmap getPhoto() {
        return mPhoto;
    }

    public void setPhoto(Bitmap mPhoto) {
        this.mPhoto = mPhoto;
    }


    public boolean IsComing() {
        return mIsComing;
    }

    public void setIsComing(boolean mIsComing) {
        this.mIsComing = mIsComing;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        this.mId = id;
    }

    public FacebookFriend(String id, String name){

        mIsComing=false;
        mId = id;
        mName = name;
        mPhoto=null;

    }

}
