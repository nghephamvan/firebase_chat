package com.example.tranquoctrungcntt.uchat.ActivityAdapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants;
import com.example.tranquoctrungcntt.uchat.Objects.Media;
import com.example.tranquoctrungcntt.uchat.R;

import java.util.ArrayList;

import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MediaType.MEDIA_TYPE_VIDEO;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NumberConstant.CLICK_DELAY;

public class SharedMediaAdapter extends RecyclerView.Adapter<SharedMediaAdapter.SharedMediaViewHolder> {


    private final Context mAdapterContext;
    private final ArrayList<Media> mList;
    private final AppConstants.AppInterfaces.OnAdapterItemClickListener mItemClickListener;

    private boolean isDelaying;


    public SharedMediaAdapter(Context mAdapterContext, ArrayList<Media> mList, AppConstants.AppInterfaces.OnAdapterItemClickListener mItemClickListener) {

        this.mAdapterContext = mAdapterContext;
        this.mList = mList;
        this.mItemClickListener = mItemClickListener;

        isDelaying = false;
    }

    @NonNull
    @Override
    public SharedMediaViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewtype) {
        View view = LayoutInflater.from(mAdapterContext).inflate(R.layout.row_shared_media, viewGroup, false);

        return new SharedMediaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final SharedMediaViewHolder mViewholder, int mIndex) {


        if (mList.get(mIndex).getType() == MEDIA_TYPE_VIDEO) {
            mViewholder.btn_play_video.setVisibility(View.VISIBLE);
        } else mViewholder.btn_play_video.setVisibility(View.GONE);

        Glide.with(mAdapterContext.getApplicationContext())
                .load(mList.get(mIndex).getThumbContentUrl())
                .thumbnail(0.5f)
                .error(R.drawable.ic_place_holder)
                .placeholder(R.drawable.ic_place_holder)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                        mViewholder.img_content.setImageResource(R.drawable.ic_place_holder);

                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                        mViewholder.img_content.setVisibility(View.VISIBLE);

                        return false;
                    }
                }).into(mViewholder.img_content);


    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    public class SharedMediaViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {

        final ImageView img_content, btn_play_video;


        public SharedMediaViewHolder(@NonNull View itemView) {

            super(itemView);


            img_content = (ImageView) itemView.findViewById(R.id.img_shared_media);
            btn_play_video = (ImageView) itemView.findViewById(R.id.img_play_shared_media);


            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

        }

        @Override
        public void onClick(View v) {

            if (!isDelaying) {

                isDelaying = true;

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isDelaying = false;
                    }
                }, CLICK_DELAY);

                mItemClickListener.OnItemClick(v, getAdapterPosition());

            }

        }

        @Override
        public boolean onLongClick(View v) {

            if (!isDelaying) {

                isDelaying = true;

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isDelaying = false;
                    }
                }, CLICK_DELAY);

                mItemClickListener.OnItemLongClick(v, getAdapterPosition());

            }

            return true;
        }
    }

}
