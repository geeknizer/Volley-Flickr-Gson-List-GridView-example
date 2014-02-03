package com.androidng.flickr.util;

import com.google.gson.Gson;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.androidng.flickr.model.FlickrListing;
import com.androidng.flickr.model.FlickrListing.PhotoItem;

import java.util.List;

public class Preferences {

    private static final String PREF = "flickr";
    private static final String PREF_JSON = "flickr_json";

    private static SharedPreferences getSharedPref(Context ctx) {
        return ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }

    public static List<PhotoItem> getLastFlickrResponse(Context context) {
        return new Gson().fromJson(getSharedPref(context).getString(PREF_JSON, ""),
                FlickrListing.class).getItems();
    }

    public static void setFlickrResponse(Context context, String response) {
        // TODO(tarandeep): Replace this with DB, if time is available.
        Editor edit = getSharedPref(context).edit();
        edit.putString(PREF_JSON, response);
        edit.commit();
    }
}
