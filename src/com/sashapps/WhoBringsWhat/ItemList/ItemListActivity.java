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
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.parse.*;
import com.sashapps.WhoBringsWhat.ItemList.Row.RowType;
import com.sashapps.WhoBringsWhat.ToDelete.OLDItemListAdapter;
import com.sashapps.WhoBringsWhat.View.Utils.SwipeDismissListViewTouchListener;
import com.sashapps.WhoBringsWhat.WBWApplication;
import com.sashapps.WhoBringsWhat.WBWBaseActivity;
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
public class ItemListActivity extends WBWBaseActivity {

    ItemListAdapter adapter;
    ListView listView;
    Bitmap facebookPhoto;
    Bitmap defaultPhoto;
    ListHandlers listHandlers;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_list_activity);

        Log.d(getWBWApplication().LOG_TAG, "Started");
        /* Init App */
        Utils.setDefaultPhoto(BitmapFactory.decodeResource(getResources(), R.drawable.com_facebook_profile_default_icon));
        getWBWApplication().initParse();
        ParseAnalytics.trackAppOpened(getIntent());
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        startFacebookLogin();

        findViewById(R.id.add_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseQuery<Category> catQuery = ParseQuery.getQuery(Category.class);
                catQuery.whereEqualTo("title", "New Category");
                try {
                    Category c = catQuery.getFirst();
                    Item i = new Item(Utils.getItemList(), c, "New Item", null, null);
                    adapter.addItem(i);
                    adapter.notifyDataSetChanged();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private class ListHandlers {

        private ListView lv;

        ListHandlers(ListView lv) {
            this.lv = lv;
        }

        private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (adapter.getItemViewType(position) == RowType.ITEM.ordinal()) {
                    Item i = (Item) adapter.getItem(position);

                    if (!i.isRegistered()) {
                        i.register();
                    } else {
                        i.unregister();
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        };


    }

    private void initList() {

        ListView listView = (ListView) findViewById(R.id.list_item);

        listHandlers = new ListHandlers(listView);


        listView.setAdapter(adapter);

        Log.d(getWBWApplication().LOG_TAG, "InitList");

        listView.setOnItemClickListener(listHandlers.onItemClickListener);

        SwipeDismissListViewTouchListener swipeListener = new SwipeDismissListViewTouchListener(listView,
                new SwipeDismissListViewTouchListener.DismissCallbacks() {
                    @Override
                    public boolean canDismiss(int position) {
                        return adapter.getItemViewType(position) == RowType.ITEM.ordinal();
                    }

                    @Override
                    public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                        for (int position : reverseSortedPositions) {
                            ((Item) adapter.getItem(position)).deleteItem();
                            adapter.remove(position);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });

        listView.setOnTouchListener(swipeListener);
        listView.setOnScrollListener(swipeListener.makeScrollListener()); // Setting this scroll listener is required to ensure that during ListView scrolling, we don't look for swipes.


        getItemList();

    }

    private void getItemList() {
        // Get ItemList
        ParseQuery<ItemList> query = ParseQuery.getQuery(ItemList.class);
        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
        Log.d(getWBWApplication().LOG_TAG, " query.hasCachedResult(): " + query.hasCachedResult());
        query.findInBackground(new FindCallback<ItemList>() {
            @Override
            public void done(List<ItemList> itemLists, ParseException e) {
                Log.d(getWBWApplication().LOG_TAG, "Fetched list");
                Utils.setItemList(itemLists.get(0));
                getCategoryList();
            }
        });
    }

    ArrayList<Category> categoryList;

    private void getCategoryList() {
        // Get Categories
        ParseQuery<Category> catQuery = ParseQuery.getQuery(Category.class);
        catQuery.orderByAscending("position");
        catQuery.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
        Log.d(getWBWApplication().LOG_TAG, " catQuery.hasCachedResult(): " + catQuery.hasCachedResult());
        catQuery.findInBackground(new FindCallback<Category>() {
            @Override
            public void done(List<Category> categories, ParseException e) {
                /*for (Category category : categories) {
                    adapter.addSection(category.getTitle(),new OLDItemListAdapter(ItemListActivity.this,new ArrayList<Item>()));
                    adapter.notifyDataSetChanged();
                }*/
                Log.d(getWBWApplication().LOG_TAG, "Fetched categories");
                categoryList = (ArrayList<Category>)categories;
                getItems(categories);
            }
        });
    }

    int asyncNumberOfCallbacks;
    int asyncCount;
    ArrayList<Item> arrItems;

    private void getItems(final List<Category> catList) {


        asyncNumberOfCallbacks=catList.size();
        asyncCount=0;
        arrItems = new ArrayList<Item>();



        for (Category category : catList) {

            ParseQuery<Item> itemQuery = ParseQuery.getQuery(Item.class);
            itemQuery.include("category");
            itemQuery.whereNotEqualTo("state", Utils.PARSE_DELETED);
            itemQuery.whereEqualTo("category", category);
            itemQuery.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
            Log.d(getWBWApplication().LOG_TAG, " itemQuery.hasCachedResult(): " + itemQuery.hasCachedResult());
            itemQuery.findInBackground(getItemsCallback);
        }


    }

    private FindCallback<Item> getItemsCallback = new FindCallback<Item>() {
        @Override
        public void done(List<Item> items, ParseException e) {
            arrItems.addAll(items);
            asyncCount++;
            Log.d(getWBWApplication().LOG_TAG, "Fetched items:" + asyncCount);
            if (asyncCount==asyncNumberOfCallbacks){
                doneLoadingItems();
            }
        }
    };

    private void doneLoadingItems(){
        final ListView listView = (ListView) findViewById(R.id.list_item);
        Log.d(getWBWApplication().LOG_TAG, "Fetched all items");


        for (Item item : arrItems) {
            if (item.isRegistered()) {
                item.setPhoto(Utils.getFacebookPhoto());
            }
        }

        Log.d(getWBWApplication().LOG_TAG, "Processed items");
        findViewById(R.id.progressBar).setVisibility(View.GONE);
        adapter = new ItemListAdapter(ItemListActivity.this, arrItems, (ArrayList<Category>) categoryList);
        listView.setAdapter(adapter);
    }


    private void startFacebookLogin() {

        Log.d(getWBWApplication().LOG_TAG, "Started FB Login");

        Session fbSession = Session.openActiveSessionFromCache(this);

        if (fbSession != null && fbSession.isOpened()) {

            Request.newMeRequest(fbSession, new Request.GraphUserCallback() {

                // callback after Graph API response with user object
                @Override
                public void onCompleted(GraphUser user, Response response) {
                    Log.d(getWBWApplication().LOG_TAG, "Cached Done");
                    if (user != null) {
                        Utils.setUserFacebookId(user.getId());
                        new loadUserPhoto(ItemListActivity.this).execute();
                    }
                }
            }).executeAsync();

        } else {

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
                                Log.d(getWBWApplication().LOG_TAG, "Not cached Done");
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

    }

    private class loadUserPhoto extends AsyncTask<String, Void, Bitmap> {

        private Activity a;
        private String facebookId;

        loadUserPhoto(Activity a) {
            this.a = a;
            Log.d(getWBWApplication().LOG_TAG, "Start load photo");
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
            Utils.setFacebookPhoto(b);
            Log.d(getWBWApplication().LOG_TAG, "Done load photo");
            initList();
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }
}

