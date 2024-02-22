// - Set to the appropriate package location in your project
package com.example.application;

import android.content.Context;
import android.view.View;

import fm.liveswitch.LayoutScale;
import fm.liveswitch.VideoConfig;
import fm.liveswitch.VideoSource;
import fm.liveswitch.ViewSink;
import fm.liveswitch.android.Camera2Source;
import fm.liveswitch.android.CameraPreview;


public class CameraLocalMedia extends LocalMedia<View> {

    private final CameraPreview viewSink;
    private final VideoConfig videoConfig = new VideoConfig(640, 480, 30);

    public CameraLocalMedia(Context context, boolean disableAudio, boolean disableVideo, AecContext aecContext) {
        super(context, disableAudio, disableVideo, aecContext);

        this.context = context;
        viewSink = new CameraPreview(context, LayoutScale.Contain);

        super.initialize();
    }

    @Override
    protected VideoSource createVideoSource() {
        return new Camera2Source(viewSink, videoConfig);
    }

    @Override
    protected ViewSink<View> createViewSink() {
        return null;
    }

    // Return an Android View for local preview rather than using ViewSink.
    public View getView() {
        return viewSink.getView();
    }
}