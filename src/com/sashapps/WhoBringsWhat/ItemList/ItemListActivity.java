package com.sashapps.WhoBringsWhat.ItemList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.parse.*;
import com.sashapps.WhoBringsWhat.R;
import com.sashapps.WhoBringsWhat.Utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shahar on 2/9/14.
 */
public class ItemListActivity extends Activity {

    SepListAdapter adapter;
    ListView listView;
    Bitmap facebookPhoto;
    Bitmap defaultPhoto;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_list_activity);

        /* Init App */
        Utils.setDefaultPhoto(BitmapFactory.decodeResource(getResources(), R.drawable.com_facebook_profile_default_icon));
        initParse();

        ParseQuery<ItemList> query = ParseQuery.getQuery(ItemList.class);
        try {
            Utils.setItemList(query.find().get(0));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        /* Init list */
        adapter = new SepListAdapter(this);

        ListView listView = (ListView) findViewById(R.id.list_item);

        ParseQuery<Item> itemQuery = ParseQuery.getQuery(Item.class);
        itemQuery.orderByDescending("category");
        List<Item> list = null;
        try {
            list = itemQuery.find();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String prevCategory = "";

        Boolean isFirst=true;

        ArrayList<Item> temp = new ArrayList<Item>(list);


        for (Item i:temp){
            Log.d(Utils.LOG_TAG,i.toString());
        }

        adapter.addSection("Food", new ItemListAdapter(this, temp));


        SwipeDismissListViewTouchListener swipeListener = new SwipeDismissListViewTouchListener(listView,
                new SwipeDismissListViewTouchListener.DismissCallbacks() {
                    @Override
                    public boolean canDismiss(int position) {
                        return adapter.isItem(position);
                    }

                    @Override
                    public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                /*Toast.makeText(getBaseContext(),"Item removed",Toast.LENGTH_SHORT).show();*/
                        for (int position : reverseSortedPositions) {
                            adapter.remove(position);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });

        listView.setAdapter(adapter);
        listView.setOnTouchListener(swipeListener);
        listView.setOnScrollListener(swipeListener.makeScrollListener()); // Setting this scroll listener is required to ensure that during ListView scrolling, we don't look for swipes.

        startFacebookLogin();

        AdapterView.OnItemClickListener x = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (adapter.isItem(position)) {
                    Item i = (Item) adapter.getItem(position);

                    if (!i.isRegistered()) {
                        i.register(facebookPhoto);
                    } else {
                        i.unregister();
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        };

        listView.setOnItemClickListener(x);

    }

    private void initParse() {
        ParseObject.registerSubclass(ItemList.class);
        ParseObject.registerSubclass(Item.class);

        Parse.initialize(this, "36GvVowfQyFvW5XhZL7P05xB0pPciF9e3VSq4Qf4", "cu0pbNtOJoLczixm575YUdJBbzWH3eNMnMm7EThk");
        ParseAnalytics.trackAppOpened(getIntent());
    }


    private void startFacebookLogin() {
        Session.openActiveSession(this, true, new Session.StatusCallback() {
            // callback when session changes state
            @Override
            public void call(Session session, SessionState state, Exception exception) {
                if (session.isOpened()) {
                    // make request to the /me API
                    Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {
                        // callback after Graph API response with user object
                        @Override
                        public void onCompleted(GraphUser user, Response response) {
                            if (user != null) {
                                Utils.setUserFacebookId(user.getId());
                                new loadUserPhoto(ItemListActivity.this).execute();
                            }
                        }
                    });
                }
            }
        });

    }

    private class loadUserPhoto extends AsyncTask<String, Void, Bitmap> {

        private Activity a;
        private String facebookId;

        loadUserPhoto(Activity a) {
            this.a = a;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {

            try {
                URL image_value = new URL("http://graph.facebook.com/" + Utils.getUserFacebookId() + "/picture?style=small");
                Bitmap profPict = null;
                try {
                    profPict = BitmapFactory.decodeStream(image_value.openConnection().getInputStream());
                    return profPict;
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

            return null;
        }

        @Override
        protected void onPostExecute(Bitmap b) {
            facebookPhoto = b;
            findViewById(R.id.list_item).setVisibility(View.VISIBLE);
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }
}

