package com.bernatasel.onlinemuayene.fragment.videocall;

class PeerConnectionParameters {
    final boolean videoCallEnabled;
    private final boolean loopback;
    final int videoWidth;
    final int videoHeight;
    final int videoFps;
    private final int videoStartBitrate;
    private final String videoCodec;
    final boolean videoCodecHwAcceleration;
    private final int audioStartBitrate;
    private final String audioCodec;
    private final boolean cpuOveruseDetection;

    PeerConnectionParameters(boolean videoCallEnabled, boolean loopback,
                             int videoWidth, int videoHeight, int videoFps, int videoStartBitrate,
                             String videoCodec, boolean videoCodecHwAcceleration,
                             int audioStartBitrate, String audioCodec,
                             boolean cpuOveruseDetection) {
        this.videoCallEnabled = videoCallEnabled;
        this.loopback = loopback;
        this.videoWidth = videoWidth;
        this.videoHeight = videoHeight;
        this.videoFps = videoFps;
        this.videoStartBitrate = videoStartBitrate;
        this.videoCodec = videoCodec;
        this.videoCodecHwAcceleration = videoCodecHwAcceleration;
        this.audioStartBitrate = audioStartBitrate;
        this.audioCodec = audioCodec;
        this.cpuOveruseDetection = cpuOveruseDetection;
    }
}