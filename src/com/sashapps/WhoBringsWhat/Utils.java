package com.sashapps.WhoBringsWhat;

import java.util.ArrayList;

public class Utils {

    private static int num=0;

    private static String userFacebookId;


    public static String GOAL_SERIALIZABLE_OBJECT = "GOAL";

    public static int generateNumber(){
        return num++;
    }

    public static String getUserFacebookId() {
        return userFacebookId;
    }

    public static void setUserFacebookId(String userFacebookId) {
        Utils.userFacebookId = userFacebookId;
    }
}
