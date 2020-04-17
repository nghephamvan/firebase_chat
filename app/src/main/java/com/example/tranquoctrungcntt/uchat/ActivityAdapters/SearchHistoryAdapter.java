package com.example.tranquoctrungcntt.uchat.ActivityAdapters;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants;
import com.example.tranquoctrungcntt.uchat.Objects.SearchResult;
import com.example.tranquoctrungcntt.uchat.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_SEARCH_HISTORY;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.NumberConstant.CLICK_DELAY;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.ROOT_REF;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getMyFirebaseUserId;
import static com.example.tranquoctrungcntt.uchat.AppUtils.StringUtils.formatTimeForDetail;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.setAvatarToView;


public class SearchHistoryAdapter extends RecyclerView.Adapter<SearchHistoryAdapter.SearchHistoryViewHolder> {

    private final Context mAdapterContext;
    private final List<SearchResult> mList;
    private final AppConstants.AppInterfaces.OnAdapterItemClickListener mItemClickListener;

    private boolean isDelaying;

    public SearchHistoryAdapter(Context mAdapterContext, List<SearchResult> mList, AppConstants.AppInterfaces.OnAdapterItemClickListener mItemClickListener) {

        this.mAdapterContext = mAdapterContext;
        this.mList = mList;
        this.mItemClickListener = mItemClickListener;

        isDelaying = false;
    }

    @NonNull
    @Override
    public SearchHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewtype) {
        View view = LayoutInflater.from(mAdapterContext).inflate(R.layout.row_search_history, viewGroup, false);
        return new SearchHistoryViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull SearchHistoryViewHolder mViewHolder, int mIndex) {

        final SearchResult searchResult = mList.get(mIndex);

        setAvatarToView(mAdapterContext, searchResult.getSearchAvatar(), searchResult.getSearchName(), mViewHolder.search_avatar);

        mViewHolder.tv_name.setText(searchResult.getSearchName());

        mViewHolder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ROOT_REF.child(CHILD_SEARCH_HISTORY)
                        .child(getMyFirebaseUserId())
                        .child(mList.get(mIndex).getSearchId())
                        .removeValue();
            }
        });


        mViewHolder.tv_searchTime.setText(formatTimeForDetail(mList.get(mIndex).getSearchTime()));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    public class SearchHistoryViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {

        final CircleImageView search_avatar;
        final TextView tv_name;
        final TextView tv_searchTime;
        final FrameLayout btn_delete;

        public SearchHistoryViewHolder(@NonNull View itemView) {
            super(itemView);

            search_avatar = (CircleImageView) itemView.findViewById(R.id.civ_avatar);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            btn_delete = (FrameLayout) itemView.findViewById(R.id.frame_delete);
            tv_searchTime = (TextView) itemView.findViewById(R.id.tv_searchTime);
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
