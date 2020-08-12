package com.bernatasel.onlinemuayene.fragment.videocall;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.bernatasel.onlinemuayene.FSOps;
import com.bernatasel.onlinemuayene.R;
import com.google.firebase.firestore.DocumentReference;

import org.json.JSONException;
import org.webrtc.MediaStream;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoRendererGui;

import java.util.HashMap;

public class VideoCallActivity extends Activity implements WebRtcClient.RtcListener {
    public static Intent newIntent(Context context, boolean isDoctor, String doctorUid, String patientUid, @Nullable String callerId) {
        Intent intent = new Intent(context, VideoCallActivity.class);
        intent.putExtra("isDoctor", isDoctor);
        intent.putExtra("doctorUid", doctorUid);
        intent.putExtra("patientUid", patientUid);
        intent.putExtra("callerId", callerId);
        return intent;
    }

    private static final String SOCKET_ADDRESS = "http://192.168.1.25:3000/";//"http://192.168.1.23:3000/"//"https://berry-shadowed-sweatpants.glitch.me/";
//  private static final int RC_VIDEO_CALL_SENT = 1212;

    // Bağlanmadan önceki ekran koordinatları
    private int LOCAL_X_CONNECTING = 0, LOCAL_Y_CONNECTING = 0;
    private int LOCAL_WIDTH_CONNECTING = 100, LOCAL_HEIGHT_CONNECTING = 100;

    // Karşı tarafın görüntüsü
    private static final int REMOTE_X = 0, REMOTE_Y = 0;
    private static final int REMOTE_WIDTH = 100, REMOTE_HEIGHT = 100;

    private VideoRendererGui.ScalingType scalingType = VideoRendererGui.ScalingType.SCALE_ASPECT_FILL;
    private GLSurfaceView vsv;
    private VideoRenderer.Callbacks localRender, remoteRender;
    private WebRtcClient client;
    private String callerId;

    private boolean isDoctor;
    private String doctorUid;
    private String patientUid;
    private DocumentReference drChatDoctorPatient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(LayoutParams.FLAG_FULLSCREEN
                | LayoutParams.FLAG_KEEP_SCREEN_ON
                | LayoutParams.FLAG_DISMISS_KEYGUARD
                | LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | LayoutParams.FLAG_TURN_SCREEN_ON);

        setContentView(R.layout.video_call_activity);

        vsv = findViewById(R.id.glview_call);
        vsv.setPreserveEGLContextOnPause(true);
        vsv.setKeepScreenOn(true);
        VideoRendererGui.setView(vsv, this::init);

        remoteRender = VideoRendererGui.create(
                REMOTE_X, REMOTE_Y,
                REMOTE_WIDTH, REMOTE_HEIGHT, scalingType, false);
        localRender = VideoRendererGui.create(
                LOCAL_X_CONNECTING, LOCAL_Y_CONNECTING,
                LOCAL_WIDTH_CONNECTING, LOCAL_HEIGHT_CONNECTING, scalingType, true);

        Intent intent = getIntent();
//        String action = intent.getAction();

        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            isDoctor = bundle.getBoolean("isDoctor");
            doctorUid = bundle.getString("doctorUid");
            patientUid = bundle.getString("patientUid");

            drChatDoctorPatient = FSOps.getInstance().getDRChatDoctorPatient(doctorUid, patientUid);

            String callerId = bundle.getString("callerId");
            if (callerId != null) this.callerId = callerId;
        }

//        if (Intent.ACTION_VIEW.equals(action)) {
//            List<String> segments = intent.getData().getPathSegments();
//            callerId = SOCKET_ADDRESS + segments.get(0);
//        }
    }

    private void init() {
        Point displaySize = new Point();
        getWindowManager().getDefaultDisplay().getSize(displaySize);
        PeerConnectionParameters params = new PeerConnectionParameters(
                true,
                false, displaySize.x, displaySize.y,
                30,
                1,
                "VP9",
                true,
                1,
                "opus",
                true);

        client = new WebRtcClient(this, SOCKET_ADDRESS, params, VideoRendererGui.getEGLContext());
    }

    @Override
    public void onPause() {
        super.onPause();
        vsv.onPause();
        if (client != null) client.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        vsv.onResume();
        if (client != null) client.onResume();
    }

    @Override
    public void onDestroy() {
        if (isDoctor) updateCallId(null);
        if (client != null) client.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onCallReady(String callId) {
        if (callerId != null) {
            try {
                answer(callerId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            call(callId);
        }
    }

    public void answer(String callerId) throws JSONException {
        client.sendMessage(callerId, "init", null);
        startCam();
    }

    private void updateCallId(String callId) {
        HashMap<String, Object> hm = new HashMap<>();
        hm.put("callId", callId);
        FSOps.getInstance().updateMerge(drChatDoctorPatient, hm);
    }

    public void call(String callId) {
        updateCallId(callId);

//        Intent msg = new Intent(Intent.ACTION_SEND);
//        msg.putExtra(Intent.EXTRA_TEXT, SOCKET_ADDRESS + callId);
//        msg.setType("text/plain");
//        startActivityForResult(Intent.createChooser(msg, "Call someone :"), RC_VIDEO_CALL_SENT);
        startCam();
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == RC_VIDEO_CALL_SENT) startCam();
//    }

    public void startCam() {
        // Camera settings
        client.start("android_test");
    }

    @Override
    public void onStatusChanged(final String newStatus) {
        runOnUiThread(() -> Toast.makeText(getApplicationContext(), newStatus, Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onLocalStream(MediaStream localStream) {
        localStream.videoTracks.get(0).addRenderer(new VideoRenderer(localRender));
        VideoRendererGui.update(localRender,
                LOCAL_X_CONNECTING, LOCAL_Y_CONNECTING,
                LOCAL_WIDTH_CONNECTING, LOCAL_HEIGHT_CONNECTING,
                scalingType, false);
    }

    @Override
    public void onAddRemoteStream(MediaStream remoteStream, int endPoint) {
        remoteStream.videoTracks.get(0).addRenderer(new VideoRenderer(remoteRender));
        VideoRendererGui.update(remoteRender,
                REMOTE_X, REMOTE_Y,
                REMOTE_WIDTH, REMOTE_HEIGHT, scalingType, false);
        int LOCAL_HEIGHT_CONNECTED = 25;
        int LOCAL_WIDTH_CONNECTED = 25;
        int LOCAL_Y_CONNECTED = 72;// Local preview screen position after call is connected.
        int LOCAL_X_CONNECTED = 72;
        VideoRendererGui.update(localRender,
                LOCAL_X_CONNECTED, LOCAL_Y_CONNECTED,
                LOCAL_WIDTH_CONNECTED, LOCAL_HEIGHT_CONNECTED,
                scalingType, false);
    }

    @Override
    public void onRemoveRemoteStream(int endPoint) {
        VideoRendererGui.update(localRender,
                LOCAL_X_CONNECTING, LOCAL_Y_CONNECTING,
                LOCAL_WIDTH_CONNECTING, LOCAL_HEIGHT_CONNECTING,
                scalingType, false);
    }

//    @Override
//    public boolean onBack() {
//        UtilsAndroid.showAlertDialog(getMA(),
//                null,
//                "Sohbeti sonlandırmak istediğinize emin misiniz?",
//                true,
//                R.string.evet, null, R.string.hayir,
//                (dialog, which) -> {
//                    getMA().myFM.clearAll();
//                }, null, null);
//        return false;
//    }
}