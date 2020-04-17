package com.example.tranquoctrungcntt.uchat.ActivityAdapters;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants;
import com.example.tranquoctrungcntt.uchat.Objects.Media;
import com.example.tranquoctrungcntt.uchat.R;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MediaType.MEDIA_TYPE_PICTURE;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MediaType.MEDIA_TYPE_VIDEO;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NumberConstant.CLICK_DELAY;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.setMediaUrlToView;


public class MultipleMediaAdapter extends RecyclerView.Adapter<MultipleMediaAdapter.MultipleMediaViewHolder> {


    private final Context mAdapterContext;
    private final List<Media> mList;
    private final AppConstants.AppInterfaces.OnAdapterItemClickListener mItemClickListener;
    private boolean isDelaying;

    public MultipleMediaAdapter(Context mAdapterContext, List<Media> mList, AppConstants.AppInterfaces.OnAdapterItemClickListener mItemClickListener) {

        this.mAdapterContext = mAdapterContext;
        this.mList = mList;
        this.mItemClickListener = mItemClickListener;

        isDelaying = false;
    }

    @NonNull
    @Override
    public MultipleMediaViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mAdapterContext).inflate(R.layout.row_multiple_media, viewGroup, false);
        return new MultipleMediaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MultipleMediaViewHolder mViewHolder, int mIndex) {

        final Media media = mList.get(mIndex);

        switch (media.getType()) {

            case MEDIA_TYPE_PICTURE:
                mViewHolder.btn_play_video.setVisibility(View.GONE);
                break;
            case MEDIA_TYPE_VIDEO:
                mViewHolder.btn_play_video.setVisibility(View.VISIBLE);
                break;

        }

        setMediaUrlToView(mAdapterContext, media.getThumbContentUrl(), mViewHolder.riv_content);


    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MultipleMediaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        final RoundedImageView riv_content;
        final ImageView btn_play_video;

        public MultipleMediaViewHolder(@NonNull View itemView) {
            super(itemView);

            riv_content = (RoundedImageView) itemView.findViewById(R.id.riv_multiple_media);
            btn_play_video = (ImageView) itemView.findViewById(R.id.img_play_multiple_media);

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
