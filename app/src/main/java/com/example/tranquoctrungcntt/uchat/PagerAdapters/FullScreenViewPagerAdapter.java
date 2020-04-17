package com.example.tranquoctrungcntt.uchat.PagerAdapters;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.tranquoctrungcntt.uchat.Activities.ViewMedia;
import com.example.tranquoctrungcntt.uchat.Objects.Media;
import com.example.tranquoctrungcntt.uchat.R;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.ArrayList;

import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;

import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MediaType.MEDIA_TYPE_PICTURE;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MediaType.MEDIA_TYPE_VIDEO;


public class FullScreenViewPagerAdapter extends PagerAdapter {

    private final ArrayList<Media> mArraylist;
    private final ViewMedia mAdapterContext;

    public FullScreenViewPagerAdapter(ViewMedia context, ArrayList<Media> mArrayList) {
        this.mArraylist = mArrayList;
        this.mAdapterContext = context;

    }

    @Override
    public int getCount() {
        return mArraylist.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        LayoutInflater inflater = (LayoutInflater) mAdapterContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.media_fullsize_item, container, false);

        PhotoView picture = view.findViewById(R.id.photoview_fullscreen);

        JzvdStd videoView = view.findViewById(R.id.videoview_fullscreen);

        ProgressBar progressBar = view.findViewById(R.id.pb_loading);

        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAdapterContext.showOrHideToolbar();

            }
        });


        videoView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                Jzvd.FULLSCREEN_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_FULL_USER;
                Jzvd.NORMAL_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_FULL_USER;

                mAdapterContext.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
        });

        String mediaUrl = mArraylist.get(position).getContentUrl();


        switch (mArraylist.get(position).getType()) {

            case MEDIA_TYPE_VIDEO:

                videoView.setVisibility(View.VISIBLE);
                picture.setVisibility(View.GONE);

                videoView.setUp(mediaUrl, "", Jzvd.SCREEN_WINDOW_NORMAL);

                Glide.with(mAdapterContext.getApplicationContext())
                        .load(mediaUrl)
                        .error(R.drawable.ic_place_holder)
                        .thumbnail(0.5f)
                        .dontAnimate()
                        .fitCenter()
                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                        .addListener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                                progressBar.setVisibility(View.GONE);

                                videoView.thumbImageView.setImageResource(R.drawable.ic_place_holder);

                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                                progressBar.setVisibility(View.GONE);

                                return false;
                            }
                        }).into(videoView.thumbImageView);


                break;
            case MEDIA_TYPE_PICTURE:

                picture.setVisibility(View.VISIBLE);
                videoView.setVisibility(View.GONE);

                Glide.with(mAdapterContext.getApplicationContext())
                        .load(mediaUrl)
                        .thumbnail(0.5f)
                        .error(R.drawable.ic_place_holder)
                        .dontAnimate()
                        .fitCenter()
                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                        .addListener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                                progressBar.setVisibility(View.GONE);

                                picture.setImageResource(R.drawable.ic_place_holder);

                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                                progressBar.setVisibility(View.GONE);


                                return false;
                            }
                        }).into(picture);

                break;
        }


        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}

