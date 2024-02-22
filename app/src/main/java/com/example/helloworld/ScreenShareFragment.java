package com.example.helloworld;

import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.application.HelloWorldLogic;

public class ScreenShareFragment extends Fragment {

    private HelloWorldLogic appInstance;
    private static boolean isProjectionReady = false;

    public ScreenShareFragment() {
    }

    public static ScreenShareFragment newInstance() {
        ScreenShareFragment fragment = new ScreenShareFragment();
        fragment.setArguments(new Bundle());
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment.
        appInstance = HelloWorldLogic.getInstance(getActivity().getBaseContext());
        Intent intent = new Intent(getActivity().getApplicationContext(), BackgroundService.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getActivity().startForegroundService(intent);
        } else {
            getActivity().startService(intent);
        }

        return inflater.inflate(R.layout.fragment_screen_share, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        setUpMediaProjection();
        setScreenShareClick();
    }

    public void setScreenShareClick() {
        Button screenShareButton = (Button) getView().findViewById(R.id.screenShareButton);
//        screenShareButton.setOnClickListener(view -> appInstance.toggleScreenShare());
    }

    @NonNull
    @Override
    public String toString() {
        return "Screen Sharing";
    }

    private MediaProjectionManager manager;

    private void setUpMediaProjection() {
//        if (!isProjectionReady) {
//            manager = (MediaProjectionManager) getActivity().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
//            Intent screenCaptureIntent = manager.createScreenCaptureIntent();
//            this.startActivityForResult(screenCaptureIntent, 42);
//        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 42 && data != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            manager = (MediaProjectionManager) getActivity().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
//            appInstance.setMediaProjection(manager.getMediaProjection(resultCode, data));
            isProjectionReady = true;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
