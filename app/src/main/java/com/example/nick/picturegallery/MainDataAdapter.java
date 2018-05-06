package com.example.nick.picturegallery;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;


public class MainDataAdapter extends RecyclerView.Adapter<MainDataAdapter.MyViewHolder>   {

    private String TAG = "TPG " + MainDataAdapter.class.getSimpleName();


    public ArrayList<ImageItem> imageItems;



    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageHolder;

        MyViewHolder(View view) {
            super(view);
            imageHolder = view.findViewById(R.id.imgView);
        }
    }


    MainDataAdapter(ArrayList<ImageItem> imageItems) {
        this.imageItems = imageItems;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mainimageitem, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        //YDWrap.get(context).DownloadImage(imageItems.get(i), token, viewHolder.img_android);
        YDWrap.get().DownloadImage(imageItems.get(position), holder.imageHolder, false);
    }


    @Override
    public int getItemCount() {
        return imageItems.size();
    }



    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private MainDataAdapter.ClickListener clickListener;

        RecyclerTouchListener(Context context, final RecyclerView recyclerView, final MainDataAdapter.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }




}

