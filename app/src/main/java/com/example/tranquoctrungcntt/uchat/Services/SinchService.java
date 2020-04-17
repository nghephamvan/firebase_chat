package com.example.tranquoctrungcntt.uchat.Services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.example.tranquoctrungcntt.uchat.Activities.VideoIncomingCallScreen;
import com.example.tranquoctrungcntt.uchat.Activities.VoiceIncomingCallScreen;
import com.sinch.android.rtc.AudioController;
import com.sinch.android.rtc.Beta;
import com.sinch.android.rtc.ClientRegistration;
import com.sinch.android.rtc.ManagedPush;
import com.sinch.android.rtc.PushTokenRegistrationCallback;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchClientListener;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.video.VideoController;

import java.util.Map;

import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.IntentKey.INTENT_KEY_CALLER_ID;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.IntentKey.INTENT_KEY_CALL_ID;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getMyFirebaseUserId;


public class SinchService extends Service {

    private static final String APP_KEY = "05ba8fc9-aa07-450c-8343-8dcee4bb47a0";
    private static final String APP_SECRET = "0u9EtxZ3jU6qiHC1LO3bog==";
    private static final String ENVIRONMENT = "clientapi.sinch.com";

    private final SinchServiceInterface mSinchServiceInterface = new SinchServiceInterface();

    private SinchClient mSinchClient;

    @Override
    public IBinder onBind(Intent intent) {
        return mSinchServiceInterface;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        createClientAndStart();

    }


    @Override
    public void onDestroy() {

        if (isSinchClientStarted()) {
            mSinchClient.terminateGracefully();
        }

        super.onDestroy();

    }

    private void createClientAndStart() {

        if (mSinchClient == null) {
            createSinch();
        }

        if (!isSinchClientStarted()) {
            mSinchClient.start();
        }

    }


    private void createSinch() {

        mSinchClient = Sinch.getSinchClientBuilder()
                .context(getApplicationContext())
                .userId(getMyFirebaseUserId())
                .applicationKey(APP_KEY)
                .applicationSecret(APP_SECRET)
                .environmentHost(ENVIRONMENT)
                .build();

        mSinchClient.setSupportCalling(true);
        mSinchClient.setSupportManagedPush(true);

        mSinchClient.addSinchClientListener(new MySinchClientListener());
        mSinchClient.getCallClient().addCallClientListener(new SinchCallClientListener());
        mSinchClient.getCallClient().setRespectNativeCalls(true);

    }

    private void stopSinch() {
        if (mSinchClient != null) {
            mSinchClient.terminateGracefully();
            mSinchClient = null;
        }
    }

    private boolean isSinchClientStarted() {
        return (mSinchClient != null && mSinchClient.isStarted());
    }

    public class SinchServiceInterface extends Binder {

        public Call callUserVideo(String userId, Map<String, String> header) {
            return mSinchClient.getCallClient().callUserVideo(userId, header);
        }

        public Call callUser(String userId, Map<String, String> header) {
            return mSinchClient.getCallClient().callUser(userId, header);
        }

        public boolean isSinchClientStarted() {
            return SinchService.this.isSinchClientStarted();
        }

        public void startSinchClient() {
            createClientAndStart();
        }

        public void stopSinchClient() {
            stopSinch();
        }

        public SinchClient getSinchClient() {
            return mSinchClient;
        }

        public Call getCurrentCall(String callId) {
            return mSinchClient.getCallClient().getCall(callId);
        }

        public VideoController getVideoController() {
            return isSinchClientStarted() ? mSinchClient.getVideoController() : null;
        }

        public AudioController getAudioController() {
            return isSinchClientStarted() ? mSinchClient.getAudioController() : null;
        }

        public void relayRemotePushNotificationPayload(final Map payload) {

            if (mSinchClient == null) createSinch();

            mSinchClient.relayRemotePushNotificationPayload(payload);

        }

        public void registerPushToken(PushTokenRegistrationCallback callback) {
            getManagedPush().registerPushToken(callback);
        }

        public void unregisterPushToken() {
            getManagedPush().unregisterPushToken();
        }

        private ManagedPush getManagedPush() {

            if (mSinchClient == null) createSinch();

            return Beta.createManagedPush(mSinchClient);

        }
    }

    private class MySinchClientListener implements SinchClientListener {

        @Override
        public void onClientFailed(SinchClient client, SinchError error) {

            mSinchClient.terminate();
            mSinchClient = null;

        }

        @Override
        public void onClientStarted(SinchClient client) {


        }

        @Override
        public void onClientStopped(SinchClient client) {

        }

        @Override
        public void onLogMessage(int level, String area, String message) {

        }

        @Override
        public void onRegistrationCredentialsRequired(SinchClient client, ClientRegistration clientRegistration) {
        }

    }

    private class SinchCallClientListener implements CallClientListener {

        @Override
        public void onIncomingCall(CallClient callClient, Call call) {

            if (call.getDetails().isVideoOffered()) {

                Intent intent = new Intent(SinchService.this, VideoIncomingCallScreen.class);
                intent.putExtra(INTENT_KEY_CALL_ID, call.getCallId());
                intent.putExtra(INTENT_KEY_CALLER_ID, call.getRemoteUserId());

                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                SinchService.this.startActivity(intent);

            } else {

                Intent intent = new Intent(SinchService.this, VoiceIncomingCallScreen.class);
                intent.putExtra(INTENT_KEY_CALL_ID, call.getCallId());
                intent.putExtra(INTENT_KEY_CALLER_ID, call.getRemoteUserId());

                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                SinchService.this.startActivity(intent);
            }

        }
    }

}
