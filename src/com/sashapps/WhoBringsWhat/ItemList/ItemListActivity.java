package com.sashapps.WhoBringsWhat.ItemList;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.model.GraphUser;
import com.parse.*;
import com.sashapps.WhoBringsWhat.ItemList.Row.RowType;
import com.sashapps.WhoBringsWhat.View.Utils.SwipeDismissListViewTouchListener;
import com.sashapps.WhoBringsWhat.WBWBaseActivity;
import com.sashapps.WhoBringsWhat.R;
import com.sashapps.WhoBringsWhat.Utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.jensdriller.libs.undobar.UndoBar;

/**
 * Created by shahar on 2/9/14.
 */
public class ItemListActivity extends WBWBaseActivity {

    ItemListAdapter adapter;
    ListView listView;
    Bitmap facebookPhoto;
    Bitmap defaultPhoto;
    ListHandlers listHandlers;
    DataLoader dataLoader;
    ArrayList<Item> arrItems;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_list_activity);

        dataLoader = new DataLoader();

        Log.d(getWBWApplication().LOG_TAG, "Started");
        /* Init App */
        Utils.setDefaultPhoto(BitmapFactory.decodeResource(getResources(), R.drawable.ic_nobody));
        getWBWApplication().initParse();
        ParseAnalytics.trackAppOpened(getIntent());
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        /*startFacebookLogin();*/
        login();

        /*findViewById(R.id.add_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseQuery<Category> catQuery = ParseQuery.getQuery(Category.class);
                catQuery.whereEqualTo("title", "New Category");
                try {
                    Category c = catQuery.getFirst();
                    //Item i = new Item(Utils.getItemList(), c, "New Item", null, null);
                    *//*adapter.addItem(i);*//*
                    adapter.notifyDataSetChanged();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        });*/
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
                        if (i.isCurrentUserOwnerOfItem()) {
                            i.unregister();
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        };


    }

    private class DataLoader {
        ArrayList<Category> categoryList;
        int asyncNumberOfCallbacks;
        int asyncCount;
        Item deletedItem=null;

        private void initList() {

            ListView listView = (ListView) findViewById(R.id.list_item);

            listHandlers = new ListHandlers(listView);


            listView.setAdapter(adapter);

            Log.d(getWBWApplication().LOG_TAG, "InitList");

            final UndoBar.Listener undoListener = new UndoBar.Listener() {
                @Override
                public void onHide() {
                    deletedItem.deleteItem();
                }

                @Override
                public void onUndo(Parcelable token) {
                    adapter.restoreItem();
                    adapter.notifyDataSetChanged();
                    deletedItem=null;
                }
            };

            // Set listeners
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
                                deletedItem = ((Item) adapter.getItem(position));
                                adapter.hide(position);
                            }
                            adapter.notifyDataSetChanged();

                            new UndoBar.Builder(ItemListActivity.this)
                                         .setMessage("Deleted '" + deletedItem.getTitle() + "'")
                                         .setListener(undoListener).
                                         show();
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
                    categoryList = (ArrayList<Category>) categories;
                    getItems(categories);
                }
            });
        }

        private void getItems(final List<Category> catList) {


            asyncNumberOfCallbacks = catList.size();
            asyncCount = 0;
            arrItems = new ArrayList<Item>();


            for (Category category : catList) {

                ParseQuery<Item> itemQuery = ParseQuery.getQuery(Item.class);
                itemQuery.include("category");
                itemQuery.include("user");
                itemQuery.whereNotEqualTo("state", Utils.PARSE_DELETED);
                itemQuery.whereEqualTo("category", category);
                itemQuery.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
                itemQuery.findInBackground(getItemsCallback);
            }


        }

        private FindCallback<Item> getItemsCallback = new FindCallback<Item>() {
            @Override
            public void done(List<Item> items, ParseException e) {
                arrItems.addAll(items);
                asyncCount++;
                Log.d(getWBWApplication().LOG_TAG, "Fetched items category:" + asyncCount);
                if (asyncCount == asyncNumberOfCallbacks) {
                    doneLoadingItems();
                }
            }
        };

        private void doneLoadingItems() {
            final ListView listView = (ListView) findViewById(R.id.list_item);
            Log.d(getWBWApplication().LOG_TAG, "Fetched all items");
            findViewById(R.id.progressBar).setVisibility(View.GONE);
            adapter = new ItemListAdapter(ItemListActivity.this, arrItems, (ArrayList<Category>) categoryList);
            listView.setAdapter(adapter);
            for (Item i : arrItems) {
                if (i.isRegistered()) {
                    new loadItemUserPhoto().execute(i);
                }
            }
        }

    }

    public void login() {
        List<String> permissions = Arrays.asList("basic_info", "user_about_me",
                "user_relationships", "user_birthday", "user_location");

        ParseFacebookUtils.initialize(getResources().getString(R.string.app_id));

        ParseFacebookUtils.logIn(permissions, this, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {

                if (user == null) {
                    Log.d(LOG_TAG,
                            "Uh oh. The user cancelled the Facebook login.");
                } else if (user.isNew()) {
                    Log.d(LOG_TAG,
                            "User signed up and logged in through Facebook!");
                } else {
                    Log.d(LOG_TAG,
                            "User logged in through Facebook!");
                }

                dataLoader.initList();


                Request request = Request.newMeRequest(ParseFacebookUtils.getSession(),
                        new Request.GraphUserCallback() {
                            @Override
                            public void onCompleted(GraphUser user, Response response) {
                                //Utils.setUserFacebookId(user.getId());
                                ParseUser.getCurrentUser().put("facebookId", user.getId());
                                ParseUser.getCurrentUser().saveInBackground();
                                new loadCurrentUserPhoto().execute(user.getId());
                                // new loadUserPhoto(ItemListActivity.this).execute();
                            }
                        });
                request.executeAsync();
            }

        });
    }


    private Bitmap loadFacebookPhoto(String userFBID) {
        Bitmap b = null;
        if (getWBWApplication().getUserPhotos().get(userFBID) == null) {

            try {
                URL image_value = new URL("http://graph.facebook.com/" + userFBID + "/picture?style=small");
                try {
                    b = BitmapFactory.decodeStream(image_value.openConnection().getInputStream());
                    getWBWApplication().getUserPhotos().put(userFBID, b);
                    Log.d(getWBWApplication().LOG_TAG, "Done load photo for user: " + userFBID);
                    return b;
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        } else {
            b = getWBWApplication().getUserPhotos().get(userFBID);
            return b;
        }

        return null;
    }

    private class loadCurrentUserPhoto extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params) {
            String fbid = params[0];
            Utils.setCurrentUserFacebookPhoto(loadFacebookPhoto(fbid)); //TODO: update user added photos here
            return null;
        }
    }

    private class loadItemUserPhoto extends AsyncTask<Item, Void, Bitmap> {

        Item item;

        @Override
        protected Bitmap doInBackground(Item... params) {
            Item item = params[0];
            String userFBID = item.getUser().getString("facebookId");
            Bitmap b = loadFacebookPhoto(userFBID);
            item.setPhoto(b);
            return b;
        }

        @Override
        protected void onPostExecute(Bitmap b) {
            adapter.notifyDataSetChanged();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
    }
}


