package com.example.tranquoctrungcntt.uchat.Activities;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.emoji.widget.EmojiAppCompatEditText;
import androidx.viewpager.widget.ViewPager;

import com.example.tranquoctrungcntt.uchat.PagerAdapters.EmojiPagerAdapter;
import com.example.tranquoctrungcntt.uchat.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_USERS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.ROOT_REF;
import static com.example.tranquoctrungcntt.uchat.AppUtils.ConnectionUtils.isConnectedToFirebaseService;
import static com.example.tranquoctrungcntt.uchat.AppUtils.ConnectionUtils.showNoConnectionDialog;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getMyFirebaseUserId;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.hideKeyboard;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.showLongToast;

public class UpdateStatus extends BaseActivity implements View.OnClickListener {


    private static final String keyStatusLine = "status";

    private Toolbar mToolbar;

    private EmojiAppCompatEditText edt_status;

    private ViewPager vp_emoji;

    private FrameLayout tab_faces;
    private FrameLayout tab_animals;
    private FrameLayout tab_foods;
    private FrameLayout tab_objects;
    private FrameLayout tab_places;
    private FrameLayout tab_symbols;
    private FrameLayout tab_flags;
    private FrameLayout tab_delete;

    private FrameLayout tab_faces_indicator;
    private FrameLayout tab_animals_indicator;
    private FrameLayout tab_foods_indicator;
    private FrameLayout tab_objects_indicator;
    private FrameLayout tab_places_indicator;
    private FrameLayout tab_symbols_indicator;
    private FrameLayout tab_flags_indicator;

    private DatabaseReference mCurrentStatusRef;
    private ValueEventListener mStatusValueEvent;

    private String mCurrentStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_status);

        initViews();

        initEmoji();

        if (mStatusValueEvent != null && mCurrentStatusRef != null) mCurrentStatusRef.removeEventListener(mStatusValueEvent);

        mCurrentStatusRef = ROOT_REF.child(CHILD_USERS).child(getMyFirebaseUserId()).child(keyStatusLine);

        mStatusValueEvent = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mCurrentStatus = dataSnapshot.getValue(String.class);

                edt_status.setText(mCurrentStatus);
                edt_status.requestFocus();
                edt_status.setSelection(edt_status.getText().toString().length());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mCurrentStatusRef.addValueEventListener(mStatusValueEvent);

    }

    private void initEmoji() {

        final EmojiPagerAdapter emojiPagerAdapter = new EmojiPagerAdapter(getSupportFragmentManager());

        vp_emoji = (ViewPager) findViewById(R.id.vp_emoji);
        vp_emoji.setOffscreenPageLimit(7);
        vp_emoji.setAdapter(emojiPagerAdapter);

        tab_faces = (FrameLayout) findViewById(R.id.emoji_tab_faces);
        tab_animals = (FrameLayout) findViewById(R.id.emoji_tab_animals);
        tab_foods = (FrameLayout) findViewById(R.id.emoji_tab_foods);
        tab_objects = (FrameLayout) findViewById(R.id.emoji_tab_objects);
        tab_places = (FrameLayout) findViewById(R.id.emoji_tab_places);
        tab_symbols = (FrameLayout) findViewById(R.id.emoji_tab_symbols);
        tab_flags = (FrameLayout) findViewById(R.id.emoji_tab_flags);
        tab_delete = (FrameLayout) findViewById(R.id.emoji_tab_delete);

        tab_faces_indicator = (FrameLayout) findViewById(R.id.tab_faces_indicator);
        tab_animals_indicator = (FrameLayout) findViewById(R.id.tab_animals_indicator);
        tab_foods_indicator = (FrameLayout) findViewById(R.id.tab_foods_indicator);
        tab_objects_indicator = (FrameLayout) findViewById(R.id.tab_objects_indicator);
        tab_places_indicator = (FrameLayout) findViewById(R.id.tab_places_indicator);
        tab_symbols_indicator = (FrameLayout) findViewById(R.id.tab_symbols_indicator);
        tab_flags_indicator = (FrameLayout) findViewById(R.id.tab_flags_indicator);

        tab_faces.setOnClickListener(this);
        tab_animals.setOnClickListener(this);
        tab_foods.setOnClickListener(this);
        tab_objects.setOnClickListener(this);
        tab_places.setOnClickListener(this);
        tab_symbols.setOnClickListener(this);
        tab_flags.setOnClickListener(this);
        tab_delete.setOnClickListener(this);

        vp_emoji.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) { }

            @Override
            public void onPageSelected(int i) {
                switch (i) {

                    case 0:

                        tab_faces_indicator.setVisibility(View.VISIBLE);
                        tab_animals_indicator.setVisibility(View.INVISIBLE);
                        tab_foods_indicator.setVisibility(View.INVISIBLE);
                        tab_objects_indicator.setVisibility(View.INVISIBLE);
                        tab_places_indicator.setVisibility(View.INVISIBLE);
                        tab_symbols_indicator.setVisibility(View.INVISIBLE);
                        tab_flags_indicator.setVisibility(View.INVISIBLE);

                        break;

                    case 1:

                        tab_faces_indicator.setVisibility(View.INVISIBLE);
                        tab_animals_indicator.setVisibility(View.VISIBLE);
                        tab_foods_indicator.setVisibility(View.INVISIBLE);
                        tab_objects_indicator.setVisibility(View.INVISIBLE);
                        tab_places_indicator.setVisibility(View.INVISIBLE);
                        tab_symbols_indicator.setVisibility(View.INVISIBLE);
                        tab_flags_indicator.setVisibility(View.INVISIBLE);

                        break;

                    case 2:

                        tab_faces_indicator.setVisibility(View.INVISIBLE);
                        tab_animals_indicator.setVisibility(View.INVISIBLE);
                        tab_foods_indicator.setVisibility(View.VISIBLE);
                        tab_objects_indicator.setVisibility(View.INVISIBLE);
                        tab_places_indicator.setVisibility(View.INVISIBLE);
                        tab_symbols_indicator.setVisibility(View.INVISIBLE);
                        tab_flags_indicator.setVisibility(View.INVISIBLE);

                        break;

                    case 3:

                        tab_faces_indicator.setVisibility(View.INVISIBLE);
                        tab_animals_indicator.setVisibility(View.INVISIBLE);
                        tab_foods_indicator.setVisibility(View.INVISIBLE);
                        tab_objects_indicator.setVisibility(View.VISIBLE);
                        tab_places_indicator.setVisibility(View.INVISIBLE);
                        tab_symbols_indicator.setVisibility(View.INVISIBLE);
                        tab_flags_indicator.setVisibility(View.INVISIBLE);

                        break;

                    case 4:

                        tab_faces_indicator.setVisibility(View.INVISIBLE);
                        tab_animals_indicator.setVisibility(View.INVISIBLE);
                        tab_foods_indicator.setVisibility(View.INVISIBLE);
                        tab_objects_indicator.setVisibility(View.INVISIBLE);
                        tab_places_indicator.setVisibility(View.VISIBLE);
                        tab_symbols_indicator.setVisibility(View.INVISIBLE);
                        tab_flags_indicator.setVisibility(View.INVISIBLE);

                        break;

                    case 5:

                        tab_faces_indicator.setVisibility(View.INVISIBLE);
                        tab_animals_indicator.setVisibility(View.INVISIBLE);
                        tab_foods_indicator.setVisibility(View.INVISIBLE);
                        tab_objects_indicator.setVisibility(View.INVISIBLE);
                        tab_places_indicator.setVisibility(View.INVISIBLE);
                        tab_symbols_indicator.setVisibility(View.VISIBLE);
                        tab_flags_indicator.setVisibility(View.INVISIBLE);

                        break;

                    case 6:

                        tab_faces_indicator.setVisibility(View.INVISIBLE);
                        tab_animals_indicator.setVisibility(View.INVISIBLE);
                        tab_foods_indicator.setVisibility(View.INVISIBLE);
                        tab_objects_indicator.setVisibility(View.INVISIBLE);
                        tab_places_indicator.setVisibility(View.INVISIBLE);
                        tab_symbols_indicator.setVisibility(View.INVISIBLE);
                        tab_flags_indicator.setVisibility(View.VISIBLE);

                        break;

                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        vp_emoji.setCurrentItem(0, false);
    }

    private void initViews() {

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Cập nhật cảm nghĩ");

        edt_status = (EmojiAppCompatEditText) findViewById(R.id.edt_status);
        edt_status.requestFocus();
        edt_status.setSelection(edt_status.getText().toString().length());

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mStatusValueEvent != null && mCurrentStatusRef != null) mCurrentStatusRef.removeEventListener(mStatusValueEvent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.one_button_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            case R.id.item_one_button:

                if (isConnectedToFirebaseService(UpdateStatus.this)) {

                    final String newStatus = edt_status.getText().toString().trim();

                    if (!newStatus.isEmpty()) {
                        if (!newStatus.equals(mCurrentStatus)) {
                            ROOT_REF.child(CHILD_USERS)
                                    .child(getMyFirebaseUserId()).child(keyStatusLine)
                                    .setValue(newStatus).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    showLongToast(UpdateStatus.this, "Cập nhật dòng trạng thái thành công !");

                                }
                            });

                        }


                    }

                    finish();

                } else showNoConnectionDialog(UpdateStatus.this);

                hideKeyboard(UpdateStatus.this);

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void appendEmoji(String emoji) {
        edt_status.append(emoji);
    }

    public void deleteOneEmoji() {
        edt_status.setFocusable(true);
        edt_status.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.emoji_tab_faces:
                vp_emoji.setCurrentItem(0, false);
                break;

            case R.id.emoji_tab_animals:
                vp_emoji.setCurrentItem(1, false);
                break;

            case R.id.emoji_tab_foods:
                vp_emoji.setCurrentItem(2, false);
                break;

            case R.id.emoji_tab_objects:
                vp_emoji.setCurrentItem(3, false);
                break;

            case R.id.emoji_tab_places:
                vp_emoji.setCurrentItem(4, false);
                break;

            case R.id.emoji_tab_symbols:
                vp_emoji.setCurrentItem(5, false);
                break;

            case R.id.emoji_tab_flags:
                vp_emoji.setCurrentItem(6, false);
                break;

            case R.id.emoji_tab_delete:
                edt_status.setFocusable(true);
                edt_status.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
                break;
        }

    }
}
