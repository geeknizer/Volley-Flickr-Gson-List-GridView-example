package com.androidng.flickr;

import android.app.Activity;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.widget.TextView;

import com.androidng.flickr.view.NetworkImageView;
import com.androidng.flickr.util.FlickrVolley;

/**
 * Full screen activity for image and meta data
 * @author: Tarandeep Singh(taranfx)
 */
public class FullscreenImageActivity extends Activity {

    public static final String EXTRA_IMAGE_URL = "image_url";
    public static final String EXTRA_IMAGE_AUTHOR = "image_author";
    public static final String EXTRA_IMAGE_DATE = "image_date";
    public static final String EXTRA_IMAGE_TAGS = "image_tags";
    public static final String EXTRA_IMAGE_TITLE = "image_title";
    public static final String EXTRA_IMAGE_DESCRIPTION = "image_desc";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_fullscreen);
        Bundle extras = getIntent().getExtras();
        if (extras == null || !extras.containsKey(EXTRA_IMAGE_URL)) {
            finish();
            // TODO(tarandeep): toast
        }
        ((NetworkImageView) findViewById(R.id.fullscreen_image)).setImageUrl(
                extras.getString(EXTRA_IMAGE_URL), FlickrVolley.getImageLoader());
        ((TextView) findViewById(R.id.fullscreen_author)).setText(extras
                .getString(EXTRA_IMAGE_AUTHOR));
        ((TextView) findViewById(R.id.fullscreen_date)).setText(extras.getString(EXTRA_IMAGE_DATE));
        ((TextView) findViewById(R.id.fullscreen_desc)).setText(extras
                .getString(EXTRA_IMAGE_DESCRIPTION));
        ((TextView) findViewById(R.id.fullscreen_title)).setText(Html.fromHtml(extras
                .getString(EXTRA_IMAGE_TITLE)));
        ((TextView) findViewById(R.id.fullscreen_tags)).setText(extras.getString(EXTRA_IMAGE_TAGS));

        if (Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB) {
            getActionBar().setDisplayShowHomeEnabled(true);
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            finish();
            break;
        }
        return super.onOptionsItemSelected(item);
    }
}
