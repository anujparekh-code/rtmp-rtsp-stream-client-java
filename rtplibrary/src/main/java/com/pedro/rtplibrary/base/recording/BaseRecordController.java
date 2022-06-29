package com.pedro.rtplibrary.base.recording;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.pedro.encoder.utils.CodecUtil;
import com.pedro.rtsp.utils.RtpConstants;

import java.io.IOException;
import java.nio.ByteBuffer;

public abstract class BaseRecordController implements RecordController {

    protected Status status = Status.STOPPED;
    protected Status status2 = Status.STOPPED;
    protected String videoMime = CodecUtil.H264_MIME;
    protected long pauseMoment = 0;
    protected long pauseTime = 0;
    protected Listener listener;
    protected int videoTrack = -1;
    protected int audioTrack = -1;
    protected final MediaCodec.BufferInfo videoInfo = new MediaCodec.BufferInfo();
    protected final MediaCodec.BufferInfo audioInfo = new MediaCodec.BufferInfo();
    protected boolean isOnlyAudio = false;
    protected boolean isOnlyVideo = false;
    private String videoFilePath;
    private String videoFilePath2;
    public void setVideoMime(String videoMime) {
        this.videoMime = videoMime;
    }

    public boolean isRunning() {
        return status == Status.STARTED || status == Status.RECORDING || status == Status.RESUMED || status == Status.PAUSED;
    }

    public boolean isRunning2() {
        return status2 == Status.STARTED || status2 == Status.RECORDING || status2 == Status.RESUMED || status2 == Status.PAUSED;
    }

    public boolean isRecording() {
        return status == Status.RECORDING;
    }

    public boolean isRecording2() {
        return status2 == Status.RECORDING;
    }

    public Status getStatus() {
        return status;
    }

    public Status getStatus2() {
        return status;
    }

    public void pauseRecord() {
        if (status == Status.RECORDING) {
            pauseMoment = System.nanoTime() / 1000;
            status = Status.PAUSED;
            if (listener != null) {
                listener.onStatusChange(status);
            }
        }
    }

    public void resumeRecord() {
        if (status == Status.PAUSED) {
            pauseTime += System.nanoTime() / 1000 - pauseMoment;
            status = Status.RESUMED;
            if (listener != null) {
                listener.onStatusChange(status);
            }
        }
    }

    protected boolean isKeyFrame(ByteBuffer videoBuffer) {
        byte[] header = new byte[5];
        videoBuffer.duplicate().get(header, 0, header.length);
        if (videoMime.equals(CodecUtil.H264_MIME) && (header[4] & 0x1F) == RtpConstants.IDR) {  //h264
            return true;
        } else { //h265
            return videoMime.equals(
                    CodecUtil.H265_MIME) && ((header[4] >> 1) & 0x3f) == RtpConstants.IDR_W_DLP || ((header[4] >> 1) & 0x3f) == RtpConstants.IDR_N_LP;
        }
    }

    //We can't reuse info because could produce stream issues
    protected void updateFormat(MediaCodec.BufferInfo newInfo, MediaCodec.BufferInfo oldInfo) {
        newInfo.flags = oldInfo.flags;
        newInfo.offset = oldInfo.offset;
        newInfo.size = oldInfo.size;
        newInfo.presentationTimeUs = oldInfo.presentationTimeUs - pauseTime;
    }

    public void setVideoFormat(MediaFormat videoFormat) {
        setVideoFormat(videoFormat, false);
    }

    public void setAudioFormat(MediaFormat audioFormat) {
        setAudioFormat(audioFormat, false);
    }

    public String getVideoFilePath() {
        return videoFilePath;
    }

    public void setVideoFilePath(String videoFilePath) {
        this.videoFilePath = videoFilePath;
    }

    public String getVideoFilePath2() {
        return videoFilePath2;
    }

    public void setVideoFilePath2(String videoFilePath2) {
        this.videoFilePath2 = videoFilePath2;
    }
}
