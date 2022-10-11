package com.pedro.rtplibrary.rtmp;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaCodec;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.pedro.rtmp.flv.video.ProfileIop;
import com.pedro.rtmp.rtmp.RtmpClient;
import com.pedro.rtmp.utils.ConnectCheckerRtmp;
import com.pedro.rtplibrary.base.Camera1Base;
import com.pedro.rtplibrary.base.USBBase;
import com.pedro.rtplibrary.view.LightOpenGlView;
import com.pedro.rtplibrary.view.OpenGlView;


import java.nio.ByteBuffer;

/**
 * More documentation see:
 * {@link Camera1Base}
 *
 * Created by pedro on 25/01/17.
 */

public class RtmpUSB extends USBBase {

//    private SrsFlvMuxer srsFlvMuxer;
    private  RtmpClient rtmpClient;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public RtmpUSB(OpenGlView openGlView, ConnectCheckerRtmp connectChecker) {
        super(openGlView);
//        srsFlvMuxer = new SrsFlvMuxer(connectChecker);
        rtmpClient = new RtmpClient(connectChecker);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public RtmpUSB(LightOpenGlView lightOpenGlView, ConnectCheckerRtmp connectChecker) {
        super(lightOpenGlView);
//        srsFlvMuxer = new SrsFlvMuxer(connectChecker);
        rtmpClient = new RtmpClient(connectChecker);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public RtmpUSB(Context context, ConnectCheckerRtmp connectChecker) {
        super(context);
//        srsFlvMuxer = new SrsFlvMuxer(connectChecker);
        rtmpClient = new RtmpClient(connectChecker);
    }

    /**
     * H264 profile.
     *
     * @param profileIop Could be ProfileIop.BASELINE or ProfileIop.CONSTRAINED
     */
    public void setProfileIop(ProfileIop profileIop) {
//        srsFlvMuxer.setProfileIop(profileIop);
        rtmpClient.setProfileIop(profileIop);
    }

    @Override
    public void setAuthorization(String user, String password) {
//        srsFlvMuxer.setAuthorization(user, password);
        rtmpClient.setAuthorization(user, password);
    }

    @Override
    protected void prepareAudioRtp(boolean isStereo, int sampleRate) {
//        srsFlvMuxer.setIsStereo(isStereo);
//        srsFlvMuxer.setSampleRate(sampleRate);
        rtmpClient.setAudioInfo(sampleRate, isStereo);
    }

    @Override
    protected void startStreamRtp(String url) {
//        if (videoEncoder.getRotation() == 90 || videoEncoder.getRotation() == 270) {
//            srsFlvMuxer.setVideoResolution(videoEncoder.getHeight(), videoEncoder.getWidth());
//        } else {
//            srsFlvMuxer.setVideoResolution(videoEncoder.getWidth(), videoEncoder.getHeight());
//        }
//        srsFlvMuxer.start(url);
        if (videoEncoder.getRotation() == 90 || videoEncoder.getRotation() == 270) {
            rtmpClient.setVideoResolution(videoEncoder.getHeight(), videoEncoder.getWidth());
        } else {
            rtmpClient.setVideoResolution(videoEncoder.getWidth(), videoEncoder.getHeight());
        }
        rtmpClient.setFps(videoEncoder.getFps());
        rtmpClient.setOnlyVideo(!audioInitialized);
        rtmpClient.connect(url);
    }

    @Override
    protected void stopStreamRtp() {
//        srsFlvMuxer.stop();
        rtmpClient.disconnect();
    }

    @Override
    protected void getAacDataRtp(ByteBuffer aacBuffer, MediaCodec.BufferInfo info) {
//        srsFlvMuxer.sendAudio(aacBuffer, info);
        rtmpClient.sendAudio(aacBuffer, info);
    }

    @Override
    protected void onSpsPpsVpsRtp(ByteBuffer sps, ByteBuffer pps, ByteBuffer vps) {
//        srsFlvMuxer.setSpsPPs(sps, pps);
        rtmpClient.setVideoInfo(sps, pps, vps);
    }

    @Override
    protected void getH264DataRtp(ByteBuffer h264Buffer, MediaCodec.BufferInfo info) {
//        srsFlvMuxer.sendVideo(h264Buffer, info);
        rtmpClient.sendVideo(h264Buffer, info);
    }
}

