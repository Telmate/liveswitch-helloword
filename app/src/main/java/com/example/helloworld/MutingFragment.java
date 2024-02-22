package com.example.helloworld;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.application.HelloWorldLogic;

public class MutingFragment extends Fragment {

    HelloWorldLogic appInstance;

    public MutingFragment() {
    }

    public static MutingFragment newInstance() {
        MutingFragment fragment = new MutingFragment();
        fragment.setArguments(new Bundle());
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        appInstance = HelloWorldLogic.getInstance(getActivity().getBaseContext());
        return inflater.inflate(R.layout.fragment_muting, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        setupButtons();
    }

    public void setupButtons() {
        Button localAudioToggle = getView().findViewById(R.id.localAudioToggle);
        Button localVideoToggle = getView().findViewById(R.id.localVideoToggle);
        Button remoteAudioToggle = getView().findViewById(R.id.remoteAudioToggle);
        Button remoteVideoToggle = getView().findViewById(R.id.remoteVideoToggle);

        localAudioToggle.setOnClickListener(view -> {
//            localAudioToggle.setClickable(false);
//            appInstance.toggleMuteLocalAudio().then(result -> {
//                getActivity().runOnUiThread(() -> localAudioToggle.setClickable(true));
//            });
        });

        localVideoToggle.setOnClickListener(view -> {
//            localVideoToggle.setClickable(false);
//            appInstance.toggleMuteLocalVideo().then(result -> {
//                getActivity().runOnUiThread(() -> localVideoToggle.setClickable(true));
//            });
        });

//        remoteAudioToggle.setOnClickListener(view -> appInstance.toggleDisableRemoteAudio());
//        remoteVideoToggle.setOnClickListener(view -> appInstance.toggleDisableRemoteVideo());
    }

    @NonNull
    @Override
    public String toString() {
        return "Muting stream options";
    }
}