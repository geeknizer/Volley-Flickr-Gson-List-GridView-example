package com.androidng.flickr.model;

import android.text.TextUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Gson model for Flickr response.
 * @author tarandeep
 *
 */
public class FlickrListing {
    private static final String ISO_8601_FORMAT_STRING = "yyyy-MM-dd'T'HH:mm:ssZ";
    private String title;
    private List<PhotoItem> items;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<PhotoItem> getItems() {
        return items;
    }

    public void setItems(List<PhotoItem> items) {
        this.items = items;
    }

    public static class Media {
        private String m;

        public String getM() {
            return m;
        }

        public void setM(String m) {
            this.m = m;
        }
    }

    public static class PhotoItem {
        private static final String AUTHOR_PREFIX = "nobody@flickr.com (";
        private String title;
        private String link;
        private Media media;
        private String description;
        private String date_taken;
        private String author;
        private String author_id;
        private String tags;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getThumbnailUrl() {
            String url = media.getM();
            if (!TextUtils.isEmpty(url)) {
                // TODO (tarandeep): support other image formats.
                url = url.substring(0, url.indexOf("_m.")) + "_s.jpg";
            }
            return url;
        }

        public String getFullImageUrl() {
            String url = media.getM();
            if (!TextUtils.isEmpty(url)) {
                // TODO (tarandeep): support other image formats.
                url = url.substring(0, url.indexOf("_m.")) + "_n.jpg";
            }
            return url;
        }

        public Media getMedia() {
            return media;
        }

        public void setMedia(Media media) {
            this.media = media;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getDateTaken() {
            // Flickr has 2013-12-29T08:23:06-08:00
            DateFormat df = new SimpleDateFormat(ISO_8601_FORMAT_STRING, Locale.ENGLISH);
            Date date = null;
            try {
                date = df.parse(date_taken);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            DateFormat format = DateFormat.getDateInstance();
            return format.format(date);
        }

        public void setDate_taken(String date_taken) {
            this.date_taken = date_taken;
        }

        public String getAuthor() {
            if (author != null && author.contains(AUTHOR_PREFIX)) {
                return author.substring(author.indexOf(AUTHOR_PREFIX) + AUTHOR_PREFIX.length(),
                        author.indexOf(")"));
            }
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getAuthor_id() {
            return author_id;
        }

        public void setAuthor_id(String author_id) {
            this.author_id = author_id;
        }

        public String getTags() {
            return tags;
        }

        public void setTags(String tags) {
            this.tags = tags;
        }
    }

}
