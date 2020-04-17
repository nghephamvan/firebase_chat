package com.example.tranquoctrungcntt.uchat.Activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.emoji.widget.EmojiAppCompatEditText;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tranquoctrungcntt.uchat.ActivityAdapters.SearchMessageAdapter;
import com.example.tranquoctrungcntt.uchat.Objects.Message;
import com.example.tranquoctrungcntt.uchat.Objects.User;
import com.example.tranquoctrungcntt.uchat.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_MESSAGES;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.IntentKey.INTENT_KEY_GROUP_ID;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.IntentKey.INTENT_KEY_USER_ID;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.MessageType.MESSAGE_TYPE_TEXT;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.ROOT_REF;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getDataFromIntent;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getMyFirebaseUserId;
import static com.example.tranquoctrungcntt.uchat.AppUtils.StringUtils.covertStringToURL;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.hasItemInList;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.hideKeyboard;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.searchMessage;


public class SearchMessage extends BaseActivity {

    private Toolbar mToolbar;

    private RecyclerView rv_result;
    private ArrayList<Message> mResultList;
    private ArrayList<Message> mCurrentMessageList;
    private SearchMessageAdapter mResultAdapter;

    private ProgressBar pb_loading;

    private String mUserOrGroupId;
    private String mIntentKey;

    private TextView tv_not_found;

    private EmojiAppCompatEditText edt_search;

    private AlertDialog searchKeyWordDialog;

    private Map<String, User> mUserProfileMap;

    private AppCompatButton btn_search_again;

    private ChildEventListener mMessageChildEvent;
    private Query mMessageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_message);

        if (mMessageChildEvent != null && mMessageRef != null) mMessageRef.removeEventListener(mMessageChildEvent);

        if (getDataFromIntent(this, INTENT_KEY_USER_ID) != null) {

            mIntentKey = INTENT_KEY_USER_ID;

        } else if (getDataFromIntent(this, INTENT_KEY_GROUP_ID) != null) {

            mIntentKey = INTENT_KEY_GROUP_ID;

        } else mIntentKey = null;


        if (mIntentKey != null) {

            initViews();

            mUserOrGroupId = (String) getDataFromIntent(this, mIntentKey);

            mMessageRef = ROOT_REF.child(CHILD_MESSAGES).child(getMyFirebaseUserId()).child(mUserOrGroupId).orderByChild("type").equalTo(MESSAGE_TYPE_TEXT);

            initMessagePool();

        } else finish();

    }

    private void initMessagePool() {

        mMessageChildEvent = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                final Message message = dataSnapshot.getValue(Message.class);

                mCurrentMessageList.add(message);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                final Message updatedMessage = dataSnapshot.getValue(Message.class);

                for (int index = 0; index < mCurrentMessageList.size(); index++) {

                    if (mCurrentMessageList.get(index).getMessageId().equals(updatedMessage.getMessageId())) {

                        mCurrentMessageList.set(index, updatedMessage);

                    }
                }


                final int j = searchMessage(mResultList, updatedMessage.getMessageId());

                if (hasItemInList(j)) {
                    mResultList.set(j, updatedMessage);
                    mResultAdapter.notifyItemChanged(j);
                }

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {


                mResultAdapter.stopActionWithThisMessage(dataSnapshot.getValue(Message.class));

                final Message removedMessage = dataSnapshot.getValue(Message.class);

                for (int index = 0; index < mCurrentMessageList.size(); index++) {

                    if (mCurrentMessageList.get(index).getMessageId().equals(removedMessage.getMessageId())) {

                        mCurrentMessageList.remove(index);

                        break;
                    }
                }


                final int j = searchMessage(mResultList, removedMessage.getMessageId());

                if (hasItemInList(j)) {
                    mResultList.remove(j);
                    mResultAdapter.notifyItemRemoved(j);
                    mResultAdapter.notifyItemRangeChanged(j, mResultAdapter.getItemCount());
                }

            }


            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mMessageRef.addChildEventListener(mMessageChildEvent);

    }


    private void initViews() {

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        btn_search_again = (AppCompatButton) findViewById(R.id.btn_search_again);
        btn_search_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                searchKeyWordDialog.show();
            }
        });

        mUserProfileMap = new HashMap<>();

        mCurrentMessageList = new ArrayList<>();

        mResultList = new ArrayList<>();

        mResultAdapter = new SearchMessageAdapter(
                SearchMessage.this,
                mResultList,
                mUserProfileMap,
                mUserOrGroupId,
                mIntentKey.equals(INTENT_KEY_GROUP_ID)
        );

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(SearchMessage.this, LinearLayoutManager.VERTICAL, false);

        rv_result = (RecyclerView) findViewById(R.id.rv_search_message);
        rv_result.setHasFixedSize(true);
        rv_result.setLayoutManager(mLinearLayoutManager);
        rv_result.setAdapter(mResultAdapter);
        rv_result.setItemAnimator(null);

        pb_loading = (ProgressBar) findViewById(R.id.pb_loading);

        tv_not_found = (TextView) findViewById(R.id.tv_not_found);

        final View view = LayoutInflater.from(this).inflate(R.layout.dialog_search_message, null);

        edt_search = (EmojiAppCompatEditText) view.findViewById(R.id.edt_search_message);

        edt_search.requestFocus();

        edt_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int ACTON, KeyEvent keyEvent) {

                if (ACTON == EditorInfo.IME_ACTION_SEARCH) hideKeyboard(SearchMessage.this);

                return true;
            }
        });


        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Tìm kiếm")
                .setCancelable(false)
                .setNegativeButton("Hủy", null)
                .setView(view).setPositiveButton("Tìm kiếm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        final String keyword = edt_search.getText().toString().trim();

                        if (!keyword.isEmpty()) {

                            pb_loading.setVisibility(View.VISIBLE);
                            tv_not_found.setVisibility(View.GONE);
                            btn_search_again.setVisibility(View.GONE);
                            rv_result.setVisibility(View.GONE);

                            mResultList.clear();

                            for (Message message : mCurrentMessageList) {

                                if (message.getContent() != null
                                        && covertStringToURL(message.getContent()).toLowerCase().contains(covertStringToURL(keyword).toLowerCase())) {
                                    mResultList.add(message);
                                }
                            }

                            Collections.sort(mResultList, Collections.reverseOrder(new Comparator<Message>() {
                                @Override
                                public int compare(Message o1, Message o2) {
                                    return Long.compare(o1.getSendTime(), o2.getSendTime());
                                }
                            }));

                            mResultAdapter.notifyDataSetChanged();

                            pb_loading.setVisibility(View.GONE);

                            if (mResultList.isEmpty()) {

                                tv_not_found.setVisibility(View.VISIBLE);
                                btn_search_again.setVisibility(View.VISIBLE);
                                rv_result.setVisibility(View.GONE);

                                getSupportActionBar().setDisplayShowTitleEnabled(false);

                            } else {

                                tv_not_found.setVisibility(View.GONE);
                                btn_search_again.setVisibility(View.GONE);
                                rv_result.setVisibility(View.VISIBLE);
                                getSupportActionBar().setDisplayShowTitleEnabled(true);
                                getSupportActionBar().setTitle(mResultList.size() + " kết quả");

                            }

                        }

                    }
                });


        searchKeyWordDialog = builder.create();
        searchKeyWordDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                edt_search.setText(null);
                edt_search.requestFocus();
            }
        });

        searchKeyWordDialog.show();


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mMessageChildEvent != null && mMessageRef != null) mMessageRef.removeEventListener(mMessageChildEvent);

    }
}
