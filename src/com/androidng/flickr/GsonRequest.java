package com.androidng.flickr;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.androidng.flickr.util.Preferences;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Custom Volley adapter for Gson.
 */
public class GsonRequest<T> extends Request<T> {
    private final Gson mGson = new Gson();
    private final Class<T> mClazz;
    private final Map<String, String> mCustomHeaders;
    private final Listener<T> mListener;
    private Context mContext;

    public GsonRequest(Context context, String url, Class<T> clazz, Map<String, String> headers,
            Listener<T> listener, ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        this.mContext = context;
        this.mClazz = clazz;
        // TODO(tarandeep): add gzip if volley doesnt already do it.
        this.mCustomHeaders = headers;
        this.mListener = listener;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return mCustomHeaders != null ? mCustomHeaders : super.getHeaders();
    }

    @Override
    protected void deliverResponse(T response) {
        mListener.onResponse(response);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            json = json.substring("jsonFlickrFeed(".length(), json.length() - 1);
            Preferences.setFlickrResponse(mContext, json);
            return Response.success(mGson.fromJson(json, mClazz),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }
}