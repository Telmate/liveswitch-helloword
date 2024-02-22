package com.example.helloworld;

import android.content.Context;
import android.media.AudioDeviceCallback;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.application.HelloWorldLogic;

import fm.liveswitch.SourceInput;

public class DeviceSwitchingFragment extends Fragment {

    private HelloWorldLogic appInstance;

    public DeviceSwitchingFragment() {
    }

    // TODO: Rename and change types and number of parameters
    public static DeviceSwitchingFragment newInstance() {
        DeviceSwitchingFragment fragment = new DeviceSwitchingFragment();
        fragment.setArguments(new Bundle());
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment.
        appInstance = HelloWorldLogic.getInstance(getActivity().getBaseContext());
        return inflater.inflate(R.layout.fragment_device_switching, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        setUpDevices();
    }

    private <Type> void setUpDropDown(Type[] sourceList, int id, AdapterView.OnItemSelectedListener actionClick) {
        Context context = getContext();
        getView().post(() -> {
            Spinner dropDown = (Spinner) getView().findViewById(id);
            ArrayAdapter<Type> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, sourceList);
            dropDown.setAdapter(adapter);
            dropDown.setOnItemSelectedListener(actionClick);
        });
    }

    public void setVideoInputsDropDown(SourceInput[] sourceList, AdapterView.OnItemSelectedListener actionClick) {
        setUpDropDown(sourceList, R.id.videoInputs, actionClick);
    }

    public void setAudioOutputsDropDown(String[] sourceList, AdapterView.OnItemSelectedListener actionClick) {
        setUpDropDown(sourceList, R.id.audioOutputs, actionClick);
    }

    @NonNull
    @Override
    public String toString() {
        return "Device Switching";
    }

    private void loadVideoInputsToUI() {
//        if (appInstance.getLocalMedia() != null) {
//            appInstance.getLocalMedia().getVideoSourceInputs().then(result -> {
//
//                setVideoInputsDropDown(result, new AdapterView.OnItemSelectedListener() {
//                    @Override
//                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                        appInstance.setVideoSourceInput((SourceInput) parent.getSelectedItem());
//                    }
//
//                    @Override
//                    public void onNothingSelected(AdapterView<?> parent) {
//                    }
//                });
//
//            });
//        }
    }

    private void loadAudioOutputsToUI() {
//        setAudioOutputsDropDown(appInstance.getAudioOutputs(), new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                appInstance.setAudioSourceOutput(parent.getSelectedItem().toString());
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });
    }

    private void setUpDevices() {
        loadVideoInputsToUI();
        loadAudioOutputsToUI();

        AudioManager audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);

        // Registering callback for audio changes.
        audioManager.registerAudioDeviceCallback(new AudioDeviceCallback() {
            @Override
            public void onAudioDevicesAdded(AudioDeviceInfo[] addedDevices) {
                super.onAudioDevicesAdded(addedDevices);
                loadAudioOutputsToUI();
            }

            @Override
            public void onAudioDevicesRemoved(AudioDeviceInfo[] removedDevices) {
                super.onAudioDevicesRemoved(removedDevices);
                loadAudioOutputsToUI();
            }
        }, new Handler());
    }
}