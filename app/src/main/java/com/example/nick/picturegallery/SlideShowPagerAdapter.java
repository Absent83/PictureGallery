package com.example.nick.picturegallery;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;


//	adapter
public class SlideShowPagerAdapter extends PagerAdapter {

    private String TAG = "TPG " + SlideShowPagerAdapter.class.getSimpleName();

    private List<ImageItem> imageItems;
    private LayoutInflater layoutInflater;

    SlideShowPagerAdapter(LayoutInflater layoutInflater, List<ImageItem> imageItems) {
        this.layoutInflater = layoutInflater;
        this.imageItems =  imageItems;

    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {


        View view = layoutInflater.inflate(R.layout.slideshowitem, container, false);

        ImageView imageViewPreview = (ImageView) view.findViewById(R.id.image_fullsize);


        YDWrap.get().DownloadImage(imageItems.get(position), imageViewPreview, true);


        container.addView(view);

        return view;
    }

    @Override
    public int getCount() {
        return imageItems.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object obj) {
        return view == ((View) obj);
    }


    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
