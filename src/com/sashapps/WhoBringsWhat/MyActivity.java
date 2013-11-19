package com.sashapps.WhoBringsWhat;

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
import android.widget.TextView;
import com.facebook.*;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Util;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;
import com.parse.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.xml.transform.Result;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MyActivity extends Activity {

    private final String TAG = "MyActivity";
    private final String FACEBOOK_RESPONSE_ARRAY_NAME = "data";
    private final String FACEBOOK_NAME = "name";
    private final String FACEBOOK_UID = "uid";
    private String currentParseObjectId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        /*
        // Parse init
        Parse.initialize(this, "36GvVowfQyFvW5XhZL7P05xB0pPciF9e3VSq4Qf4", "cu0pbNtOJoLczixm575YUdJBbzWH3eNMnMm7EThk");
        ParseAnalytics.trackAppOpened(getIntent());

        // TEMP code for list
        HashMap<Integer,Item> map = new HashMap<Integer, Item>();
        map.put(Utils.generateNumber(),new Item("Burgers"));
        map.put(Utils.generateNumber(),new Item("Knives"));
        map.put(Utils.generateNumber(),new Item("Ketchup"));
        map.put(Utils.generateNumber(),new Item("Buns"));

        // List init
        ListView list =  (ListView)findViewById(R.id.list);
        ListAdapter adapter = new ListAdapter(this,map);
        list.setAdapter(adapter);
        list.setOnItemClickListener(listListener);*/

        // Load the facebook friends
        startFacebookLogin();
    }

    private class loadFaceBookFriends extends AsyncTask<Void,Void,ArrayList<FacebookFriend>>{

        private Activity a;

        loadFaceBookFriends(Activity a){
            this.a=a;
        }

        @Override
        protected ArrayList<FacebookFriend> doInBackground(Void... voids) {
            Session facebookSession = Session.getActiveSession();

            if (facebookSession.isOpened()) {
                // get friends
                String fqlQuery = "SELECT uid,name " +
                        "FROM user " +
                        "WHERE uid in (SELECT uid2 FROM friend WHERE uid1=me() LIMIT 25)";
                Bundle params = new Bundle();
                params.putString("q", fqlQuery);
                Request req = new Request(Session.getActiveSession(), "/fql", params, HttpMethod.GET);
                Response r = req.executeAndWait();
                Log.d(TAG, r.getGraphObject().getInnerJSONObject().toString());
                JSONArray facebookFriends = new JSONArray();
                try {
                    facebookFriends = r.getGraphObject().getInnerJSONObject().getJSONArray(FACEBOOK_RESPONSE_ARRAY_NAME);
                    ArrayList<FacebookFriend> facebookFriendsArrayList = new ArrayList<FacebookFriend>();
                    for (int i=0;i<facebookFriends.length();i++){
                        String facebookId = facebookFriends.getJSONObject(i).getString(FACEBOOK_UID);
                        String facebookName = facebookFriends.getJSONObject(i).getString(FACEBOOK_NAME);
                        facebookFriendsArrayList.add(new FacebookFriend(facebookId,facebookName));
                    }

                    Log.d(TAG, "Finished building array" + facebookFriendsArrayList.toString());

                    return facebookFriendsArrayList;

                } catch (JSONException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<FacebookFriend> facebookFriendArrayList) {
            Log.d(TAG, "onPostExecute");
            ListView friendsList =  (ListView)findViewById(R.id.list_friends);
            friendsList.setAdapter(new ListFriendsAdapter(a,facebookFriendArrayList));

            // load photos
            for (FacebookFriend friend : facebookFriendArrayList){
                 new loadFriendPhoto(a).execute(friend.getId());
            }
        }

    }

    private class loadFriendPhoto extends AsyncTask<String,Void,Bitmap>{

        private Activity a;
        private String facebookId;

        loadFriendPhoto(Activity a){
            this.a=a;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {

            this.facebookId=strings[0];

            try {
                URL image_value = new URL("http://graph.facebook.com/"+this.facebookId+"/picture?style=small" );
                Bitmap profPict= null;
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
            Log.d(TAG, "LoadFriendPhoto: onPostExecute");
            ListView friendsList =  (ListView)findViewById(R.id.list_friends);
            ListFriendsAdapter la = (ListFriendsAdapter)friendsList.getAdapter();
            la.updatePhotoById(this.facebookId,b);
            la.notifyDataSetChanged();
        }

    }

    // =============
    // List Listener
    // =============
    AdapterView.OnItemClickListener listListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            final ProfilePictureView p = (ProfilePictureView)view.findViewById(R.id.profilepic);
            //Item item = (Item)adapterView.getItemAtPosition(i);

            if (p.getProfileId() != null){
                p.setProfileId(null);
            }
            else{

                AsyncTask x = new AsyncTask() {
                    @Override
                    protected Object doInBackground(Object... objects) {
                        p.setProfileId(Utils.getUserFacebookId());
                        ParseObject listItem = new ParseObject("ListItem");
                        listItem.put("facebookId", Utils.getUserFacebookId());
                        listItem.saveInBackground();

                        return new Object();
                    }
                };
            }
        }
    };

    private void startFacebookLogin(){

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
                                Log.d(TAG,"Login to facebook completed");
                               // Utils.setUserFacebookId(user.getId());
                              //  findViewById(R.id.list).setVisibility(View.VISIBLE);
                                Log.d(TAG,"Calling asynctask to load friends");
                                //loadFaceBookFriends.execute();
                                new loadFaceBookFriends(MyActivity.this).execute();
                            }
                        }
                    });
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);

    }


}
