package com.example.tranquoctrungcntt.uchat.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.emoji.widget.EmojiAppCompatTextView;

import com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants;
import com.example.tranquoctrungcntt.uchat.Objects.User;
import com.example.tranquoctrungcntt.uchat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_USERS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.ROOT_REF;
import static com.example.tranquoctrungcntt.uchat.AppUtils.CheckerUtils.isAccountValid;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getLocalOrLoadMyProfile;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getMyFirebaseUserId;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.ViewAction.viewAvatar;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.setAvatarToView;


public class MyProfile extends BaseActivity {

    private Toolbar mToolbar;

    private CircleImageView civ_avatar;

    private TextView tv_name;
    private TextView tv_dateofbirth;
    private TextView tv_gender;
    private TextView tv_datejoined;
    private TextView tv_email;

    private EmojiAppCompatTextView tv_status;

    private ImageView img_gender;

    private ValueEventListener mProfileValueEvent;
    private DatabaseReference mProfileRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        initViews();

        initClickEvents();

    }


    private void initViews() {

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        civ_avatar = (CircleImageView) findViewById(R.id.civ_avatar);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_dateofbirth = (TextView) findViewById(R.id.tv_dateofbirth);
        tv_gender = (TextView) findViewById(R.id.tv_gender);
        tv_datejoined = (TextView) findViewById(R.id.tv_datejoined);
        tv_email = (TextView) findViewById(R.id.tv_email);
        tv_status = (EmojiAppCompatTextView) findViewById(R.id.tv_status);
        img_gender = (ImageView) findViewById(R.id.img_gender);

    }

    private void initClickEvents() {
        civ_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getLocalOrLoadMyProfile(new AppConstants.AppInterfaces.FirebaseUserProfileCallBack() {
                    @Override
                    public void OnCallBack(User callbackUserProfile) {
                        viewAvatar(MyProfile.this, callbackUserProfile.getAvatarUrl());
                    }
                });

            }
        });


        tv_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyProfile.this, UpdateStatus.class));
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.one_button_menu, menu);

        menu.findItem(R.id.item_one_button).setTitle("CẬP NHẬT");
        menu.findItem(R.id.item_one_button).setIcon(R.drawable.ic_update_profile);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                finish();

                break;
            case R.id.item_one_button:

                startActivity(new Intent(MyProfile.this, UpdateProfile.class));

                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onStart() {
        super.onStart();

        if (mProfileValueEvent != null && mProfileRef != null) mProfileRef.removeEventListener(mProfileValueEvent);

        if (isAccountValid()) {

            mProfileRef = ROOT_REF.child(CHILD_USERS).child(getMyFirebaseUserId());

            mProfileValueEvent = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (FirebaseAuth.getInstance().getCurrentUser() != null) {

                        final User user = dataSnapshot.getValue(User.class);

                        if (user != null) {

                            tv_name.setText(user.getName());
                            tv_datejoined.setText(user.getJoinedDate());
                            tv_dateofbirth.setText(user.getDateofbirth());
                            tv_gender.setText(user.getGender());
                            tv_email.setText(user.getEmail());
                            tv_status.setText(user.getStatus() != null ? "❝ " + user.getStatus() + " ❞" : null);
                            img_gender.setImageResource(user.getGender().equals("Nam") ? R.drawable.ic_boy_tritone : R.drawable.ic_girl_tritone);

                            setAvatarToView(MyProfile.this, user.getThumbAvatarUrl(), user.getName(), civ_avatar);

                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };

            mProfileRef.addValueEventListener(mProfileValueEvent);

        }


    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mProfileValueEvent != null && mProfileRef != null) mProfileRef.removeEventListener(mProfileValueEvent);
    }

}
