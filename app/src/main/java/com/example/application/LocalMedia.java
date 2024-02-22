// - Set to the appropriate package location in your project
package com.example.application;

import android.content.Context;

import fm.liveswitch.*;
import fm.liveswitch.android.AudioRecordSource;

public abstract class LocalMedia<TView> extends RtcLocalMedia<TView> {

    protected Context context;

    // Enable AEC
    public LocalMedia(Context context, boolean disableAudio, boolean disableVideo, AecContext aecContext) {
        super(disableAudio, disableVideo, aecContext);
        this.context = context;
    }

    // Local Audio
    @Override
    protected AudioSink createAudioRecorder(AudioFormat audioFormat) {
        return new fm.liveswitch.matroska.AudioSink(getId() + "-local-audio-" + audioFormat.getName().toLowerCase() + ".mkv");
    }

    @Override
    protected AudioSource createAudioSource(AudioConfig audioConfig) {
        return new AudioRecordSource(context, audioConfig);
    }

    @Override
    protected AudioEncoder createOpusEncoder(AudioConfig audioConfig) {
        return new fm.liveswitch.opus.Encoder(audioConfig);
    }

    // Local Video
    @Override
    protected VideoSink createVideoRecorder(VideoFormat videoFormat) {
        return new fm.liveswitch.matroska.VideoSink(getId() + "-local-video-" + videoFormat.getName().toLowerCase() + ".mkv");
    }

    @Override
    protected VideoEncoder createVp8Encoder() {
        return new fm.liveswitch.vp8.Encoder();
    }

    @Override
    protected VideoEncoder createVp9Encoder() {
        return new fm.liveswitch.vp9.Encoder();
    }

    @Override
    protected VideoEncoder createH264Encoder() {
        return null;
    }

    @Override
    protected VideoPipe createImageConverter(VideoFormat videoFormat) {
        return new fm.liveswitch.yuv.ImageConverter(videoFormat);
    }
}