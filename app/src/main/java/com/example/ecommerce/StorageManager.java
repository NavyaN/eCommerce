package com.example.ecommerce;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.ar.core.Session;

import java.io.Serializable;

public class StorageManager implements Serializable {
    private static final String NEXT_SHORT_CODE ="next_short_code";
    private static final String KEY_PREFIX ="anchor;";
    private static final int INITIAL_SHORT_CODE=142;
    private Session session;

    /**Gets a new short code that can be used to store the anchorID.*/
    int nextShortCode(Activity activity){
        SharedPreferences sharedPreferences=activity.getPreferences(Context.MODE_PRIVATE);
        int shortcode=sharedPreferences.getInt(NEXT_SHORT_CODE,INITIAL_SHORT_CODE);
        //Increment and update the value in sharedprefs,so the next code retrived will be unused.
        sharedPreferences.edit().putInt(NEXT_SHORT_CODE,shortcode+1)
                .apply();
        return shortcode;
    }

    /**Stores the cloud anchor ID in the activity's SharedPreference.*/
    void storeUsingShortCode(Activity activity,int shortcode,String cloudAnchorId){
        SharedPreferences sharedPreferences=activity.getPreferences(Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(KEY_PREFIX+shortcode,cloudAnchorId).apply();
    }
    /***
     * Retrives the cloud anchor ID using a shortcode.Returns an empty string if a cloud anchor ID
     * was not stored for this short code
     */
    String getCloudAchorID(Activity activity,int shortCode){
        SharedPreferences sharedPreferences=activity.getPreferences(Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_PREFIX+shortCode,"");
    }

    void setSession(Session session){
         this.session = session;
    }
    Session getSession(){
        return session;
    }

}

