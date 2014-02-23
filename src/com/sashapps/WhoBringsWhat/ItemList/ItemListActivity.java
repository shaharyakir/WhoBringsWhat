package com.sashapps.WhoBringsWhat.ItemList;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
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
    XListView listView;
    Bitmap facebookPhoto;
    Bitmap defaultPhoto;
    ListHandlers listHandlers;
    DataLoader dataLoader;
    AddItemListeners addItemListeners;
    ArrayList<Item> arrItems;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_list_activity);
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);

        dataLoader = new DataLoader();
        addItemListeners = new AddItemListeners();

        Log.d(getWBWApplication().LOG_TAG, "Started");
        /* Init App */
        Utils.setDefaultPhoto(BitmapFactory.decodeResource(getResources(), R.drawable.com_facebook_profile_default_icon));
        getWBWApplication().initParse();

        ParseAnalytics.trackAppOpened(getIntent());

        login();
    }

    private class ListHandlers {

        private XListView lv;
        private SwipeDismissListViewTouchListener swipeDismissListViewTouchListener;
        private SwipeDismissListViewTouchListener.DismissCallbacks dismissCallbacks;
        private Item deletedItem;
        private int deletedItemPosition;

        ListHandlers(XListView lv) {
            this.lv = lv;
            deletedItem = null;
            deletedItemPosition = 0;
            //lv.setOnItemClickListener(onItemClickListener);
            //lv.setOnItemLongClickListener(onItemLongClickListener);

            lv.setDismissCallback(new XListView.OnDismissCallback() {
                @Override
                public XListView.Undoable onDismiss(XListView XListView, int i) {
                    final int pos = i;
                    //final String st = (String)adapter.getItem(i);
                    final Item item = (Item) adapter.getListItem(i);
                    adapter.remove(i);

                    adapter.notifyDataSetChanged();

                    return new XListView.Undoable() {
                        @Override
                        public void undo() {
                            adapter.addItem(item, pos);
                            adapter.notifyDataSetChanged();
                        }
                    };
                }
            });

            lv.enableSwipeToDismiss();
            lv.setUndoStyle(XListView.UndoStyle.COLLAPSED_POPUP);
            lv.setSwipingLayout(R.id.list_item_front);

          /*  dismissCallbacks = new SwipeDismissListViewTouchListener.DismissCallbacks() {
                @Override
                public boolean canDismiss(int position) {
                    return adapter.getItemViewType(position) == RowType.ITEM.ordinal();
                }

                @Override
                public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                    for (int position : reverseSortedPositions) {
                        Log.d(LOG_TAG, String.valueOf(position));
                        if (deletedItem != null) {
                            deletedItem.deleteItem();
                        }
                        deletedItem = ((Item) adapter.getListItem(position));
                        deletedItemPosition = position;
                        adapter.remove(position);
                    }

                    new UndoBar.Builder(ItemListActivity.this)
                            .setMessage("Deleted '" + deletedItem.getTitle() + "'")
                            .setListener(undoListener)
                            .show();
                }
            };
            swipeDismissListViewTouchListener = new SwipeDismissListViewTouchListener(lv, dismissCallbacks);
            lv.setOnTouchListener(swipeDismissListViewTouchListener);
            lv.setOnScrollListener(swipeDismissListViewTouchListener.makeScrollListener()); // Setting this scroll listener is required to ensure that during ListView scrolling, we don't look for swipes.*/
        }


        private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getAdapter().getItemViewType(position) == RowType.ITEM.ordinal()) {
                    Item i = (Item) adapter.getListItem(position);

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

        private AdapterView.OnItemLongClickListener onItemLongClickListener = new AdapterView.OnItemLongClickListener() {
            @Override

            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getAdapter().getItemViewType(position) == RowType.ITEM.ordinal()) {
                    adapter.setEditMode(position, ! adapter.getEditMode(position));
                    adapter.notifyDataSetChanged();
                    return true;
                }
                else
                    return false;
            }
        };


      /*  final UndoBar.Listener undoListener = new UndoBar.Listener() {
            @Override
            public void onHide(Object item) {
                if (deletedItem != null) {
                    deletedItem.deleteItem();
                    deletedItem = null;
                    deletedItemPosition = 0;
                }
            }

            @Override
            public void onUndo(Object item) {
                if (deletedItem != null){
                    adapter.addItem(deletedItem, deletedItemPosition);
                }
                deletedItem = null;
                //adapter.notifyDataSetChanged();
            }
        };*/
    }

    private class DataLoader {
        ArrayList<Category> categoryList;
        int asyncNumberOfCallbacks;
        int asyncCount;

        private void initList() {

            XListView listView = (XListView) findViewById(R.id.list_item);
            listView.setAdapter(adapter);
            listHandlers = new ListHandlers(listView);
            Log.d(getWBWApplication().LOG_TAG, "InitList");
            getList();

        }

        private void getList() {
            // Get ItemList
            ParseQuery<ItemList> query = ParseQuery.getQuery(ItemList.class);
            query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
            Log.d(getWBWApplication().LOG_TAG, " query.hasCachedResult(): " + query.hasCachedResult());
            query.findInBackground(new FindCallback<ItemList>() {
                @Override
                public void done(List<ItemList> itemLists, ParseException e) {
                    Log.d(getWBWApplication().LOG_TAG, "Fetched list");
                    Utils.setItemList(itemLists.get(0));
                    getItems();
                }
            });
        }

        private void getItems() {

            arrItems = new ArrayList<Item>();

            ParseQuery<Item> itemQuery = ParseQuery.getQuery(Item.class);
            itemQuery.include("user");
            itemQuery.include("category");
            itemQuery.whereNotEqualTo("state", Utils.PARSE_DELETED);
            itemQuery.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
            itemQuery.findInBackground(getItemsCallback);
        }

        private FindCallback<Item> getItemsCallback = new FindCallback<Item>() {
            @Override
            public void done(List<Item> items, ParseException e) {
                arrItems.addAll(items);
                doneLoadingItems();
            }
        };

        private void doneLoadingItems() {
            final XListView listView = (XListView) findViewById(R.id.list_item);
            Log.d(getWBWApplication().LOG_TAG, "Fetched all items");
            findViewById(R.id.progressBar).setVisibility(View.GONE);
            adapter = new ItemListAdapter(getBaseContext(),R.layout.item_list, arrItems,listView);
            listView.setAdapter(adapter);
            for (Item i : arrItems) {
                if (i.isRegistered()) {
                    new loadItemUserPhoto().execute(i);
                }
            }
        }

    }

    private class AddItemListeners {
        private EditText addItemEditText;
        private Button addItemButton;

        public AddItemListeners() {
            addItemEditText = (EditText) findViewById(R.id.item_list_activity_add_item_edittext);
            addItemButton = (Button) findViewById(R.id.item_list_activity_add_item_button);

            addItemEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (addItemEditText.getText().length() > 0) {
                        addItemButton.setVisibility(View.VISIBLE);
                    } else {
                        addItemButton.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            addItemEditText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addItemEditText.setCursorVisible(true);
                }
            });

            addItemEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        addItemButton.callOnClick();
                    }
                    return false;
                }
            });

            addItemButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Item i = new Item(Utils.getItemList(), null, addItemEditText.getText().toString(), null, null);
                    addItemEditText.setText("");
                    addItemEditText.setCursorVisible(false);
                    adapter.addItem(i);
                    //adapter.notifyDataSetChanged();
                    ((XListView)findViewById(R.id.list_item)).smoothScrollToPosition(0);

                    /*ParseQuery<Category> catQuery = ParseQuery.getQuery(Category.class);
                    catQuery.whereEqualTo("isDefault", true);
                    catQuery.getFirstInBackground(new GetCallback<Category>() {
                        @Override
                        public void done(Category category, ParseException e) {


                        }
                    });*/
                }
            });
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


