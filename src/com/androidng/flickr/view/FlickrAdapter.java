package com.androidng.flickr.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.androidng.flickr.R;
import com.androidng.flickr.model.FlickrListing.PhotoItem;

import java.util.List;

public class FlickrAdapter extends ArrayAdapter<PhotoItem> {
    private ImageLoader mImageLoader;
    private boolean mIsListView;

    public FlickrAdapter(Context context, int textViewResourceId, List<PhotoItem> objects,
            ImageLoader imageLoader) {
        super(context, textViewResourceId, objects);
        mImageLoader = imageLoader;
        mIsListView = textViewResourceId == R.id.list_view;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater inflator = (LayoutInflater) this.getContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            view = inflator.inflate(mIsListView ? R.layout.lv_item : R.layout.gv_item, null);
        }

        ViewHolder holder = (ViewHolder) view.getTag();

        if (holder == null) {
            holder = new ViewHolder(view);
            view.setTag(holder);
        }

        PhotoItem entry = getItem(position);
        String thumbUrl = entry.getThumbnailUrl();
        if (!TextUtils.isEmpty(thumbUrl)) {
            holder.image.setImageUrl(thumbUrl, mImageLoader);
        } else {
            holder.image.setImageResource(R.drawable.flickr); // TODO use a better placeholder.
        }

        holder.title.setText(entry.getTitle());
        if (mIsListView) {
            holder.author.setText(entry.getAuthor());
            holder.timestamp.setText(entry.getDateTaken());
        }

        return view;
    }

    private class ViewHolder {
        NetworkImageView image;
        TextView title;
        TextView author;
        TextView timestamp;

        public ViewHolder(View view) {
            image = (NetworkImageView) view.findViewById(R.id.lv_thumb);
            title = (TextView) view.findViewById(R.id.lv_title);
            if (mIsListView) {
                author = (TextView) view.findViewById(R.id.lv_author);
                timestamp = (TextView) view.findViewById(R.id.lv_date);
            }

            view.setTag(this);
        }
    }
}
