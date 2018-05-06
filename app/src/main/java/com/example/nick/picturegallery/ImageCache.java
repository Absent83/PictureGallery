package com.example.nick.picturegallery;

import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

public class ImageCache extends LruCache <String, Bitmap> {

    private String TAG = "TPG " + ImageCache.class.getSimpleName();

    /**
     * @param maxSize for caches that do not override {@link #sizeOf}, this is
     *                the maximum number of entries in the cache. For all other caches,
     *                this is the maximum sum of the sizes of the entries in this cache.
     */
    ImageCache(int maxSize) {
        super(maxSize);
    }

    public Bitmap getBitmapFromMemory(String key, boolean fullSize) {
        key = getNewKey(key, fullSize);
        return this.get(key);
    }

    public void setBitmapToMemory(String key, Bitmap bitMap, boolean fullSize) {
        key = getNewKey(key, fullSize);
        if (getBitmapFromMemory(key, fullSize) == null) {
            this.put(key, bitMap);
            Log.d(TAG, "setBitmapToMemory: " + key);
        }
    }


    private String getNewKey(String key, boolean fullSize){
        return fullSize ? key : "pr_" + key;
    }
}
