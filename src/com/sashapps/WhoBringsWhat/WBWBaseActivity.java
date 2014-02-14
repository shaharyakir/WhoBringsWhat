package com.sashapps.WhoBringsWhat;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by shahar on 2/12/14.
 */
public abstract class WBWBaseActivity extends Activity {

    WBWApplication mApplication;
    public String LOG_TAG;

    public WBWApplication getWBWApplication(){
        return mApplication;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mApplication = (WBWApplication)getApplication();
        this.LOG_TAG = mApplication.LOG_TAG;
    }
}
