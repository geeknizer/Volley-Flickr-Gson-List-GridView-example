package com.androidng.flickr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.androidng.flickr.model.FlickrListing;
import com.androidng.flickr.model.FlickrListing.PhotoItem;
import com.androidng.flickr.util.FlickrVolley;
import com.androidng.flickr.util.Preferences;
import com.androidng.flickr.view.FlickrAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Home activity that showcases easy use of Volley library with:
 * - Gson
 * - Touchable/zoomable NetworkImageView
 * - List / Grid layout switcher
 * 
 * http://geeknizer.com/ http://androidng.com http://geekphotography.in
 * @author Tarandeep Singh(taranfx atttt gmail dot com)
 */
public class MainActivity extends Activity {

    private final String FLICKR_JSON_API_URL = "http://api.flickr.com/services/feeds/photos_public.gne?format=json";
    private RequestQueue mRequestQueue;
    private FlickrAdapter mAdapter;
    /** initially flickr returns 20 items */
    private ArrayList<PhotoItem> mPhotoItems = new ArrayList<PhotoItem>(20);
    private ListView mListView;
    private GridView mGridView;
    private ApiListener mApiListener;
    private ApiErrorListener mApiErrorListener;
    private int mCurrentPage = 0;
    private boolean mIsListView = true;

    /**
     * Network callback success listener for Volley network operations.
     * 
     * @author tarandeep
     * 
     */
    private class ApiListener implements Response.Listener<FlickrListing> {
        @Override
        public void onResponse(FlickrListing response) {
            toggleViewsForSuccess();
            if (mCurrentPage == 0) {
                mPhotoItems.clear();
            }
            mPhotoItems.addAll(response.getItems());
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Network callback failure listener for Volley network operations.
     * 
     * @author tarandeep
     * 
     */
    private class ApiErrorListener implements Response.ErrorListener {

        @Override
        public void onErrorResponse(VolleyError error) {

            if (mCurrentPage == 0 && error.getCause() instanceof IOException) {
                List<PhotoItem> cachedResponse = Preferences
                        .getLastFlickrResponse(getApplicationContext());
                if (cachedResponse != null) {
                    mPhotoItems.addAll(cachedResponse);
                    mAdapter.notifyDataSetChanged();
                    Toast.makeText(getApplicationContext(), "Showing Images from cache. ",
                            Toast.LENGTH_LONG).show();
                    toggleViewsForSuccess();
                    return;
                }
            }
            Toast.makeText(getApplicationContext(), "Error: " + error.getMessage(),
                    Toast.LENGTH_LONG).show();

            toggleViewsForFailure();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mApiListener = new ApiListener();
        mApiErrorListener = new ApiErrorListener();
        FlickrVolley.init(this);
        mRequestQueue = Volley.newRequestQueue(this);
        mListView = (ListView) findViewById(R.id.list_view);
        mGridView = (GridView) findViewById(R.id.grid_view);
        initAdapter();
        ScrollListener scrollListener = new ScrollListener();
        mListView.setOnScrollListener(scrollListener);
        mGridView.setOnScrollListener(scrollListener);
        ListOrGridItemClickListener listener = new ListOrGridItemClickListener();
        mListView.setOnItemClickListener(listener);
        mGridView.setOnItemClickListener(listener);
        GsonRequest<FlickrListing> request = new GsonRequest<FlickrListing>(
                getApplicationContext(), FLICKR_JSON_API_URL, FlickrListing.class, null,
                mApiListener, mApiErrorListener);
        mRequestQueue.add(request);

    }

    private void initAdapter() {
        mAdapter = new FlickrAdapter(this, mIsListView ? R.id.list_view : R.id.grid_view,
                mPhotoItems, FlickrVolley.getImageLoader());
        if (mIsListView) {
            mListView.setAdapter(mAdapter);
        } else {
            mGridView.setAdapter(mAdapter);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mRequestQueue.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // no point taking any further network requests.
        mRequestQueue.stop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_refresh:
            mCurrentPage = 0;
            GsonRequest<FlickrListing> request = new GsonRequest<FlickrListing>(
                    getApplicationContext(), FLICKR_JSON_API_URL, FlickrListing.class, null,
                    mApiListener, mApiErrorListener);
            mRequestQueue.add(request);
            break;
        case R.id.menu_toggle:
            mIsListView = !mIsListView;
            initAdapter();
            toggleListGridView();
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggleListGridView() {
        mListView.setVisibility(mIsListView ? View.VISIBLE : View.GONE);
        mGridView.setVisibility(mIsListView ? View.GONE : View.VISIBLE);
    }

    public class ScrollListener implements OnScrollListener {
        private static final String PARAM_PAGE = "&page=";

        private int visibleThreshold = 4;

        private int previousTotal = 0;
        private boolean mIsLoading = true;

        public ScrollListener() {
        }

        public ScrollListener(int visibleThreshold) {
            this.visibleThreshold = visibleThreshold;
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                int totalItemCount) {
            if (mIsLoading) {
                if (totalItemCount > previousTotal) {
                    mIsLoading = false;
                    previousTotal = totalItemCount;
                    mCurrentPage++;
                }
            }
            if (!mIsLoading
                    && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                loadNextPage();
                mIsLoading = true;
            }
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            // do nothing
        }

        public int getCurrentPage() {
            return mCurrentPage;
        }

        private void loadNextPage() {
            GsonRequest<FlickrListing> request = new GsonRequest<FlickrListing>(
                    getApplicationContext(), FLICKR_JSON_API_URL + PARAM_PAGE + mCurrentPage,
                    FlickrListing.class, null, mApiListener, mApiErrorListener);
            mRequestQueue.add(request);
        }
    }

    private class ListOrGridItemClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(getApplicationContext(), FullscreenImageActivity.class);
            PhotoItem photo = mAdapter.getItem(position);
            intent.putExtra(FullscreenImageActivity.EXTRA_IMAGE_URL, photo.getFullImageUrl());
            intent.putExtra(FullscreenImageActivity.EXTRA_IMAGE_TITLE, photo.getTitle());
            intent.putExtra(FullscreenImageActivity.EXTRA_IMAGE_DESCRIPTION, photo.getDescription());
            intent.putExtra(FullscreenImageActivity.EXTRA_IMAGE_AUTHOR, photo.getAuthor());
            intent.putExtra(FullscreenImageActivity.EXTRA_IMAGE_DATE, photo.getDateTaken());
            intent.putExtra(FullscreenImageActivity.EXTRA_IMAGE_TAGS, photo.getTags());
            startActivity(intent);
        }

    }

    private void toggleViewsForSuccess() {
        findViewById(R.id.progress).setVisibility(View.GONE);
        int viewId = mIsListView ? R.id.list_view : R.id.grid_view;
        findViewById(viewId).setVisibility(View.VISIBLE);
        findViewById(R.id.text).setVisibility(View.GONE);
    }

    private void toggleViewsForFailure() {
        findViewById(R.id.progress).setVisibility(View.GONE);
        int viewId = mIsListView ? R.id.list_view : R.id.grid_view;
        findViewById(viewId).setVisibility(mCurrentPage == 0 ? View.GONE : View.VISIBLE);
        findViewById(R.id.text).setVisibility(mCurrentPage == 0 ? View.VISIBLE : View.GONE);
    }
}
