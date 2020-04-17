package com.example.tranquoctrungcntt.uchat.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.emoji.widget.EmojiAppCompatTextView;
import androidx.fragment.app.Fragment;

import com.example.tranquoctrungcntt.uchat.Activities.BlockedUsers;
import com.example.tranquoctrungcntt.uchat.Activities.MainActivity;
import com.example.tranquoctrungcntt.uchat.Activities.MyProfile;
import com.example.tranquoctrungcntt.uchat.Activities.MyQRCode;
import com.example.tranquoctrungcntt.uchat.Activities.UpdatePassword;
import com.example.tranquoctrungcntt.uchat.Activities.UpdateStatus;
import com.example.tranquoctrungcntt.uchat.Objects.User;
import com.example.tranquoctrungcntt.uchat.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_USERS;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.ROOT_REF;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.setMyGlobalProfile;
import static com.example.tranquoctrungcntt.uchat.AppUtils.CheckerUtils.isAccountValid;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getMyFirebaseUserId;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.LinkAction.openWebBrowser;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.setAvatarToView;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {

    private CircleImageView civ_avatar;

    private TextView tv_name;
    private TextView tv_email;

    private EmojiAppCompatTextView tv_status;

    private LinearLayout btn_profile_page;
    private LinearLayout btn_blocked_users;
    private LinearLayout btn_send_email;
    private LinearLayout btn_termofuse;
    private LinearLayout btn_faq;
    private LinearLayout btn_logout;
    private LinearLayout btn_change_password;
    private LinearLayout btn_qr;

    private ValueEventListener mProfileValueEvent;
    private DatabaseReference mProfileRef;

    private View rootView;

    public AccountFragment() { }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_account, container, false);

        initViews();

        clickButtons();

        if (mProfileValueEvent != null && mProfileRef != null) mProfileRef.removeEventListener(mProfileValueEvent);

        mProfileRef = ROOT_REF.child(CHILD_USERS).child(getMyFirebaseUserId());

        mProfileValueEvent = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (isAccountValid()) {

                    final User user = dataSnapshot.getValue(User.class);

                    if (user != null) {

                        tv_name.setText(user.getName());
                        tv_email.setText(user.getEmail());
                        tv_status.setText(user.getStatus());

                        setAvatarToView(getActivity(), user.getThumbAvatarUrl(), user.getName(), civ_avatar);

                    }

                    setMyGlobalProfile(user);

                } else setMyGlobalProfile(null);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mProfileRef.addValueEventListener(mProfileValueEvent);

        return rootView;
    }

    private void sendEmail() {

        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{"tranquoctrungcntt@gmail.com"});
        email.putExtra(Intent.EXTRA_SUBJECT, "");
        email.putExtra(Intent.EXTRA_TEXT, "");
        email.setType("message/rfc822");
        startActivity(Intent.createChooser(email, "Chọn phương thức gửi"));

    }

    private void clickButtons() {

        btn_send_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
            }
        });
        tv_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), UpdateStatus.class));
            }
        });
        btn_blocked_users.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), BlockedUsers.class)

                );
            }
        });


        View.OnClickListener viewProfileClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getActivity(), MyProfile.class));

            }
        };

        civ_avatar.setOnClickListener(viewProfileClick);
        tv_name.setOnClickListener(viewProfileClick);
        tv_email.setOnClickListener(viewProfileClick);
        btn_profile_page.setOnClickListener(viewProfileClick);

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).logoutFromMainActivity();
            }
        });

        btn_termofuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebBrowser(getActivity(), "https://zaloapp.com/zalo/dieukhoan/");
            }
        });

        btn_faq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebBrowser(getActivity(), "https://www.facebook.com/help/");
            }
        });

        btn_change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), UpdatePassword.class));
            }
        });

        btn_qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MyQRCode.class));
            }
        });
    }

    private void initViews() {

        civ_avatar = (CircleImageView) rootView.findViewById(R.id.civ_avatar);

        tv_name = (TextView) rootView.findViewById(R.id.tv_name);
        tv_status = (EmojiAppCompatTextView) rootView.findViewById(R.id.tv_message_status);
        tv_email = (TextView) rootView.findViewById(R.id.tv_email);

        btn_logout = (LinearLayout) rootView.findViewById(R.id.linear_logout);
        btn_profile_page = (LinearLayout) rootView.findViewById(R.id.linear_update_profile);
        btn_send_email = (LinearLayout) rootView.findViewById(R.id.linear_send_email);
        btn_blocked_users = (LinearLayout) rootView.findViewById(R.id.linear_blocked_users);
        btn_termofuse = (LinearLayout) rootView.findViewById(R.id.linear_term_of_use);
        btn_faq = (LinearLayout) rootView.findViewById(R.id.linear_faq);
        btn_change_password = (LinearLayout) rootView.findViewById(R.id.linear_change_password);
        btn_qr = (LinearLayout) rootView.findViewById(R.id.linear_my_qr);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (mProfileValueEvent != null && mProfileRef != null) mProfileRef.removeEventListener(mProfileValueEvent);

    }

}
