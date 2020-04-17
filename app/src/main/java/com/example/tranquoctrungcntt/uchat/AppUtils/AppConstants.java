package com.example.tranquoctrungcntt.uchat.AppUtils;

import android.Manifest;
import android.net.Uri;
import android.view.View;

import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.tranquoctrungcntt.uchat.Objects.GroupDetail;
import com.example.tranquoctrungcntt.uchat.Objects.Media;
import com.example.tranquoctrungcntt.uchat.Objects.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Map;


public class AppConstants {

    public static final String LIKE_ICON = "\uD83D\uDC4D";

    public static final DatabaseReference ROOT_REF = FirebaseDatabase.getInstance().getReference();

    public static final DatabaseReference CONNECT_REF = FirebaseDatabase.getInstance().getReference(".info/connected");

    public static final StorageReference ROOT_STORAGE = FirebaseStorage.getInstance().getReference();

    public static final ColorGenerator MY_AVATAR_COLOR_GENERATOR = ColorGenerator.MATERIAL;

    private static User MY_PROFILE = null;

    private static String CHATTING_USER_ID = null;

    private static boolean CONNECTED_TO_FIREBASE = false;

    public static boolean isConnectedToFirebaseDatabase() {
        return CONNECTED_TO_FIREBASE;
    }

    public static void setConnectedToFirebase(boolean isConnectedToFirebase) {
        CONNECTED_TO_FIREBASE = isConnectedToFirebase;
    }

    public static String getChattingUserId() {
        return CHATTING_USER_ID;
    }

    public static void setChattingUserId(String chattingUserId) {
        CHATTING_USER_ID = chattingUserId;
    }

    public static User getMyGlobalProfile() {
        return MY_PROFILE;
    }

    public static void setMyGlobalProfile(User user) {
        MY_PROFILE = user;
    }

    public static class PermissionConstant {

        public static final int CAPTURE_IMAGE_CODE = 1;
        public static final int PICK_IMAGES_CODE = 2;
        public static final int RECORD_VIDEO_CODE = 3;
        public static final int PICK_VIDEOS_CODE = 4;
        public static final int RECORD_AUDIO_CODE = 5;
        public static final int DOWNLOAD_MEDIA_CODE = 6;
        public static final int VOICE_CALL_CODE = 7;
        public static final int VIDEO_CALL_CODE = 8;
        public static final int SCAN_QR_CODE = 9;

        public static final String[] PERMISSIONS_SCAN_QR_CODE = new String[]{
                Manifest.permission.CAMERA
        };

        public static final String[] PERMISSIONS_RECORD_AUDIO = new String[]{
                Manifest.permission.RECORD_AUDIO
        };
        public static final String[] PERMISSIONS_VOICE_CALL = new String[]{
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.MODIFY_AUDIO_SETTINGS,
                Manifest.permission.READ_PHONE_STATE
        };
        public static final String[] PERMISSIONS_VIDEO_CALL = new String[]{
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.MODIFY_AUDIO_SETTINGS,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.CAMERA,

        };
        public static final String[] PERMISSIONS_CAPTURE_MEDIA = new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};

        public static final String[] PERMISSIONS_ACCESS_GALLERY = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
    }

    public static class AppInterfaces {

        public interface FirebaseUrlCallBack {
            void OnCallBack(Map<String, Uri> callbackUrl);
        }

        public interface FirebaseUserProfileCallBack {
            void OnCallBack(User callbackUserProfile);
        }

        public interface FirebaseGroupDetailCallBack {
            void OnCallBack(GroupDetail callbackGroupDetail);
        }

        public interface FirebaseBooleanCallBack {
            void OnCallBack(boolean renamedForCase);
        }

        public interface FirebaseUserListCallBack {
            void OnCallBack(Map<String, ArrayList<User>> callbackUserList);
        }

        public interface FirebaseMediaListCallBack {
            void OnCallBack(ArrayList<Media> callbackMediaList);
        }

        public interface FirebaseGroupMemberCallBack {
            void OnCallBack(DataSnapshot groupMemberSnapshot);
        }

        public interface OnAdapterItemClickListener {
            void OnItemClick(View view, int position);

            void OnItemLongClick(View view, int position);
        }

    }

    public class MediaType {

        public static final int MEDIA_TYPE_PICTURE = 301;
        public static final int MEDIA_TYPE_VIDEO = 302;
        public static final int MEDIA_TYPE_AUDIO = 303;

    }

    public class MessageStatus {

        public static final String MESSAGE_STATUS_SENDING = "sending";
        public static final String MESSAGE_STATUS_SENT = "sent";
        public static final String MESSAGE_STATUS_RECEIVED = "received";
        public static final String MESSAGE_STATUS_SEEN = "seen";

    }

    public class NotificationType {

        public static final int NOTIFICATION_TYPE_SINGLE_MESSAGE = 100;
        public static final int NOTIFICATION_TYPE_NEW_FRIEND_REQUEST = 101;
        public static final int NOTIFICATION_TYPE_ACCEPT_FRIEND_REQUEST = 102;
        public static final int NOTIFICATION_TYPE_REMOVE_MESSAGE = 103;
        public static final int NOTIFICATION_TYPE_VOICE_CALL_NOT_ANSWERED = 104;
        public static final int NOTIFICATION_TYPE_VIDEO_CALL_NOT_ANSWERED = 105;
        public static final int NOTIFICATION_TYPE_VOICE_CALL_SUCCESS = 106;
        public static final int NOTIFICATION_TYPE_VIDEO_CALL_SUCCESS = 107;
        public static final int NOTIFICATION_TYPE_GROUP_MESSAGE = 108;
        public static final int NOTIFICATION_TYPE_UPDATE_GROUP = 109;
        public static final int NOTIFICATION_TYPE_UPDATE_MEMBER = 110;

    }

    public class StringKey {

        public static final String KEY_FULL_SIZE_MEDIA = "FullSize";
        public static final String KEY_THUMB_SIZE_MEDIA = "ThumbSize";

        public static final String KEY_VALID_USER = "ValidUsers";
        public static final String KEY_INVALID_USER = "InvalidUsers";

        //for message
        public static final String kMessageId = "messageId";
        public static final String kMessageStatus = "status";
        public static final String kMessageContent = "content";
        public static final String kMessageSendTime = "sendTime";
        public static final String kMessageSeenTime = "seenTime";
        public static final String kMessageType = "type";

        //for user
        public static final String kLastSeen = "lastSeen";
        public static final String kPassword = "password";
        public static final String kVerified = "verified";
        public static final String kQRCodeUrl = "qrCodeUrl";
        public static final String kThumbQRCodeUrl = "thumbQRCodeUrl";

        //for group
        public static final String kGroupName = "groupName";
        public static final String kGroupDescription = "groupDescription";
        public static final String kGroupAvatar = "groupAvatar";
        public static final String kGroupThumbAvatar = "groupThumbAvatar";
        public static final String kCensorMode = "censorMode";
        public static final String kMessageViewer = "messageViewer";
        public static final String kEditHistory = "editHistory";
        public static final String kRole = "role";
        public static final String kAdminId = "adminId";
        public static final String kGroupMember = "member";
    }


    public class GroupRole {

        public static final String ROLE_ADMIN = "admin";
        public static final String ROLE_MEMBER = "member";

    }

    public class DatabaseNode {

        public static final String CHILD_RELATIONSHIPS = "Relationships";// 1

        public static final String CHILD_MESSAGES = "Messages";//2

        public static final String CHILD_TOKENS = "Tokens";//3

        public static final String CHILD_KEEP_CONNECTION_ALIVE = "Keep Connection Alive";

        public static final String CHILD_USERS = "Users";//4

        public static final String CHILD_REQUEST_SENDER = "Request Sender";

        public static final String CHILD_REQUEST_RECEIVER = "Request Receiver";

        public static final String CHILD_I_BLOCKED_USER = "I Blocked This User";

        public static final String CHILD_USER_BLOCKED_ME = "This User Blocked Me";

        public static final String CHILD_FRIEND = "Friend";

        public static final String CHILD_BLACKLIST = "Black List";

        public static final String CHILD_SETTINGS = "Settings";// 5

        public static final String CHILD_MUTE_NOTIFICATIONS = "Mute Notifications";

        public static final String CHILD_USER_PREVENTED_ME_FROM_MAKING_GROUP = "User Prevented Me From Making Group";

        public static final String CHILD_I_PREVENTED_THIS_USER_FROM_MAKING_GROUP = "I Prevented User From Making Group";

        public static final String CHILD_BLOCK_TIMER = "Block Timer";// 6

        public static final String CHILD_SEND_FRIEND_REQUEST_TIMER = "Send Friend Request Timer";// 7

        public static final String CHILD_UPDATE_PROFILE_TIMER = "Update Profile Timer";// 8

        public static final String CHILD_SEARCH_HISTORY = "Search History";// 9

        public static final String CHILD_GROUP_DETAIL = "Group Detail";// 10

        public static final String CHILD_JOIN_GROUP_REQUESTS = "Join Group Requests";// 11

        public static final String CHILD_MEDIA = "Media";// 12

        public static final String CHILD_STICKERS = "Stickers";// 13

        public static final String CHILD_EGG_STICKER = "Egg Stickers";

        public static final String CHILD_UPDATE_TIME = "updateTime";

        public static final String CHILD_UNBLOCK_TIME = "unblockTime";

        public static final String CHILD_CANCEL_TIME = "cancelTime";

        public static final String CHILD_TYPING = "isTyping";

        public static final String CHILD_TYPING_MESSAGE = "Typing Message";// 14

    }

    public class DatabaseStorageNode {

        public static final String STORAGE_GROUP_MEDIA = "GroupMedia";
        public static final String STORAGE_SINGLE_MEDIA = "SingleMedia";
        public static final String STORAGE_VIDEO = "Video";
        public static final String STORAGE_AUDIO = "Audio";
        public static final String STORAGE_FULL_SIZE_IMAGES = "FullSizeImages";
        public static final String STORAGE_THUMB_IMAGES = "ThumbImages";
        public static final String STORAGE_FULL_SIZE_AVATAR = "FullSizeAvatar";
        public static final String STORAGE_THUMB_AVATAR = "ThumbAvatar";
        public static final String STORAGE_STICKERS = "Stickers";
        public static final String STORAGE_USER_QR_CODE = "UserQRCode";
        public static final String STORAGE_FULL_SIZE_QR_CODE = "FullSizeQRCode";
        public static final String STORAGE_THUMB_QR_CODE = "ThumbQRCode";
        public static final String STORAGE_NEW_USER_AVATAR = "NewUserAvatar";
        public static final String STORAGE_EXIST_USER_AVATAR = "ExistUserAvatar";
    }

    public class NumberConstant {

        public static final int TIME_TO_WAIT_FOR_NEXT_BLOCK_HOUR = 48;

        public static final int TIME_TO_WAIT_FOR_NEXT_BLOCK_MILIES = TIME_TO_WAIT_FOR_NEXT_BLOCK_HOUR * 60 * 60 * 1000;

        public static final int TIME_TO_WAIT_FOR_SENDING_FRIEND_REQUEST_HOUR = 2;

        public static final int TIME_TO_WAIT_FOR_SENDING_FRIEND_REQUEST_MILIES = TIME_TO_WAIT_FOR_SENDING_FRIEND_REQUEST_HOUR * 60 * 60 * 1000;

        public static final int TAKING_BACK_MESSAGE_TIME_LIMIT = 5 * 60 * 1000;

        public static final int TIME_TO_WAIT_FOR_UPDATING_PROFILE_HOUR = 12;

        public static final int TIME_TO_WAIT_FOR_UPDATING_PROFILE_MILIES = TIME_TO_WAIT_FOR_UPDATING_PROFILE_HOUR * 60 * 60 * 1000;

        public static final int MAX_PROCESSING_TIME = 60 * 1000;

        public static final int VIDEO_MAX_SIZE_IN_MB = 50;

        public static final int NUMBER_MESSAGE_PER_PAGE = 20;

        public static final int SEARCH_DELAY_TIME = 500;

        public static final int SECOND_MILLIS = 1000;
        public static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
        public static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
        public static final int DAY_MILLIS = 24 * HOUR_MILLIS;

        public static final int HANDLER_LOADMORE_MESSAGE = 7777;

        public static final int CLICK_DELAY = 1000;

    }

    public class MessageType {

        public static final int MESSAGE_TYPE_TEXT = 200;
        public static final int MESSAGE_TYPE_LIKE = 201;
        public static final int MESSAGE_TYPE_VIDEO = 202;
        public static final int MESSAGE_TYPE_PICTURE = 203;
        public static final int MESSAGE_TYPE_AUDIO = 204;
        public static final int MESSAGE_TYPE_MULTIPLE_MEDIA = 205;
        public static final int MESSAGE_TYPE_REMOVED = 207;
        public static final int MESSAGE_TYPE_VOICE_CALL_SUCCESS = 208;
        public static final int MESSAGE_TYPE_VOICE_CALL_NOT_ANSWERED = 209;
        public static final int MESSAGE_TYPE_VIDEO_CALL_SUCCESS = 210;
        public static final int MESSAGE_TYPE_VIDEO_CALL_NOT_ANSWERED = 211;

        public static final int MESSAGE_TYPE_UPDATE_MEMBER = 213;
        public static final int MESSAGE_TYPE_UPDATE_GROUP = 217;
        public static final int MESSAGE_TYPE_LEAVE_GROUP = 215;
        public static final int MESSAGE_TYPE_CHANGE_ADMIN = 216;

        public static final int MESSAGE_TYPE_WAVE_HAND = 220;
        public static final int MESSAGE_TYPE_STICKER = 221;

    }

    public class IntentKey {

        public static final String INTENT_KEY_USER_ID = "USER_ID";
        public static final String INTENT_KEY_GROUP_ID = "GROUP_ID";
        public static final String INTENT_KEY_MEDIA_ID = "MEDIA_ID";
        public static final String INTENT_KEY_MESSAGE_ID = "MESSAGE_ID";

        public static final String INTENT_KEY_CALLEE_ID = "CALLEE_ID";
        public static final String INTENT_KEY_CALLER_ID = "CALLER_ID";
        public static final String INTENT_KEY_CALL_ID = "CALL_ID";

        public static final String INTENT_KEY_USER_PROFILE = "USER_PROFILE";
        public static final String INTENT_KEY_GROUP_DETAIL = "GROUP_DETAIL";

        public static final String INTENT_KEY_NOTIFICATION_DATA = "NOTIFICATION_DATA";
        public static final String INTENT_KEY_NOTIFICATION_DATA_LIKE = "NOTIFICATION_DATA_LIKE";
        public static final String INTENT_KEY_NOTIFICATION_DATA_ACCEPT_REQUEST = "NOTIFICATION_DATA_ACCEPT_REQUEST";
        public static final String INTENT_KEY_NOTIFICATION_DATA_DENY_REQUEST = "NOTIFICATION_DATA_DENY_REQUEST";
        public static final String INTENT_KEY_NOTIFICATION_ID = "NOTIFICATION_ID";
        public static final String INTENT_KEY_NOTIFICATION_DATA_MUTE = "NOTIFICATION_DATA_MUTE";


    }
}

