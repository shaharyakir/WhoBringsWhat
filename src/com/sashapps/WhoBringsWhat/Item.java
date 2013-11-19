package com.sashapps.WhoBringsWhat;

public class Item {

    private final String NOBODY="NOBODY";
    private String mDescription;

    public String getParseId() {
        return mParseId;
    }

    public void setParseId(String mParseId) {
        this.mParseId = mParseId;
    }

    public String getFacebookUserId() {
        return mFacebookUserId;
    }

    public void setFacebookUserId(String mFacebookUserId) {
        this.mFacebookUserId = mFacebookUserId;
    }

    private String mParseId;
    private String mFacebookUserId;

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public Item(String description){

        mDescription=description;
        //mWhoBrings=NOBODY;

    }

}
