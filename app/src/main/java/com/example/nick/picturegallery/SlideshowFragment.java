package com.example.nick.picturegallery;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;


public class SlideshowFragment extends DialogFragment {
    private String TAG = "TPG " + SlideshowFragment.class.getSimpleName();

    private ArrayList<ImageItem> images;

    private ViewPager viewPager;
    private TextView lblCount, lblTitle, lblDate;
    private int selectedPosition = 0;

    static SlideshowFragment newInstance() {
        return new SlideshowFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView");

        View v = inflater.inflate(R.layout.slideshowlayout, container, false);
        viewPager = (ViewPager) v.findViewById(R.id.viewpager);
        lblCount = (TextView) v.findViewById(R.id.lbl_count);
        lblTitle = (TextView) v.findViewById(R.id.title);
        lblDate = (TextView) v.findViewById(R.id.date);

        images = (ArrayList<ImageItem>) getArguments().getSerializable("images");
        selectedPosition = getArguments().getInt("position");


        Log.d(TAG, "images size: " + images.size() + " position: " + selectedPosition);


        //LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        SlideShowPagerAdapter slideShowPagerAdapter = new SlideShowPagerAdapter(inflater, images);
        viewPager.setAdapter(slideShowPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        setCurrentItem(selectedPosition);

        return v;
    }

    private void setCurrentItem(int position) {
        viewPager.setCurrentItem(position, false);
        displayMetaInfo(selectedPosition);
    }

    //	page change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            displayMetaInfo(position);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    private void displayMetaInfo(int position) {
        lblCount.setText((position + 1) + " of " + images.size());

        ImageItem image = images.get(position);
        lblTitle.setText(image.getName());
        lblDate.setText(image.getSize()/1024 + "KB"); //TODO
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }


}
