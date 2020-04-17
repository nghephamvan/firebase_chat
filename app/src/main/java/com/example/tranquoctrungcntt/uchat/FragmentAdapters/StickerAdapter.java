package com.example.tranquoctrungcntt.uchat.FragmentAdapters;

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
import com.example.tranquoctrungcntt.uchat.Models.StickerToShow;
import com.example.tranquoctrungcntt.uchat.R;

import java.util.List;

import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NumberConstant.CLICK_DELAY;


public class StickerAdapter extends RecyclerView.Adapter<StickerAdapter.StickerViewHolder> {

    private final Context mAdapterContext;
    private final List<StickerToShow> mList;
    private final AppConstants.AppInterfaces.OnAdapterItemClickListener mItemClickListener;
    private boolean isDelaying;

    public StickerAdapter(Context mAdapterContext, List<StickerToShow> mList, AppConstants.AppInterfaces.OnAdapterItemClickListener mItemClickListener) {
        this.mAdapterContext = mAdapterContext;
        this.mList = mList;
        this.mItemClickListener = mItemClickListener;

        isDelaying = false;
    }

    @NonNull
    @Override
    public StickerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewtype) {
        return new StickerViewHolder(LayoutInflater.from(mAdapterContext).inflate(R.layout.row_sticker, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull StickerViewHolder stickerViewHolder, int mIndex) {

        final String stickerUrl = mList.get(mIndex).getStickerUrl();

        Glide.with(mAdapterContext.getApplicationContext())
                .load(stickerUrl)
                .thumbnail(0.5f)
                .error(R.drawable.ic_place_holder)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                        stickerViewHolder.imageView.setImageResource(R.drawable.ic_place_holder);

                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                        return false;
                    }
                })
                .into(stickerViewHolder.imageView);


    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class StickerViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener, View.OnLongClickListener {

        final ImageView imageView;

        public StickerViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.img_sticker);

            itemView.setOnLongClickListener(this);
            itemView.setOnClickListener(this);
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
                }, CLICK_DELAY * 2);

                mItemClickListener.OnItemClick(v, getAdapterPosition());

            }

        }

        @Override
        public boolean onLongClick(View v) {
            mItemClickListener.OnItemLongClick(v, getAdapterPosition());
            return true;
        }
    }
}
